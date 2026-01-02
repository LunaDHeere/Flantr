package com.example.flantr.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.model.Collection
import com.example.flantr.data.model.Route
import com.example.flantr.data.repository.RouteRepository
import com.example.flantr.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

data class RoutesOverviewUiState(
    val collections: List<Collection> = emptyList(),
    val allRoutes: List<Route> = emptyList(), // Source of truth
    val filteredRoutes: List<Route> = emptyList(), // What is shown
    val searchQuery: String = "",
    val showFilters: Boolean = false,

    // Filter Selections
    val selectedTheme: String = "All",
    val selectedDuration: String = "All",
    val selectedDistance: String = "All",

    val isLoading: Boolean = false
)

class RoutesOverviewViewModel(
    private val routeRepo: RouteRepository = RouteRepository(),
    private val userRepo: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutesOverviewUiState())
    val uiState: StateFlow<RoutesOverviewUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Load Collections
            val cols = userRepo.getUserCollections()

            // 2. Load User's Saved/Created Routes
            // For now fetching ALL routes, in real app fetch user.savedRouteIds
            val routes = routeRepo.getAllRoutes()

            _uiState.update {
                it.copy(
                    collections = cols,
                    allRoutes = routes,
                    filteredRoutes = routes, // Initially show all
                    isLoading = false
                )
            }
            applyFilters()
        }
    }

    // --- Actions ---

    fun toggleFilters() {
        _uiState.update { it.copy(showFilters = !it.showFilters) }
    }

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun setFilterTheme(theme: String) {
        _uiState.update { it.copy(selectedTheme = theme) }
        applyFilters()
    }

    fun setFilterDuration(duration: String) {
        _uiState.update { it.copy(selectedDuration = duration) }
        applyFilters()
    }

    fun setFilterDistance(distance: String) {
        _uiState.update { it.copy(selectedDistance = distance) }
        applyFilters()
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedTheme = "All",
                selectedDuration = "All",
                selectedDistance = "All"
            )
        }
        applyFilters()
    }


    // --- Filtering Logic ---

    private fun applyFilters() {
        val state = _uiState.value
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val query = state.searchQuery.lowercase()

        val filtered = state.allRoutes.filter { route ->
            val isNotMine = route.authorId != currentUid

            // 1. Search
            val matchesSearch = query.isEmpty() ||
                    route.name.lowercase().contains(query) ||
                    route.theme.lowercase().contains(query)

            // 2. Theme
            val matchesTheme = state.selectedTheme == "All" || route.theme == state.selectedTheme

            // 3. Duration (minutes)
            val matchesDuration = when (state.selectedDuration) {
                "Under 2h" -> route.totalTimeMinutes < 120
                "2-4h" -> route.totalTimeMinutes in 120..240
                "Over 4h" -> route.totalTimeMinutes > 240
                else -> true
            }

            // 4. Distance (Need to parse string "2.1 miles" or "3.5 km")
            // This is a rough parser based on the string format
            val matchesDistance = if (state.selectedDistance == "All") true else {
                val distVal = parseDistance(route.distance)
                when (state.selectedDistance) {
                    "Under 2 miles" -> distVal < 2.0
                    "2-3 miles" -> distVal in 2.0..3.0
                    "Over 3 miles" -> distVal > 3.0
                    else -> true
                }
            }

            isNotMine && matchesTheme && matchesDuration && matchesDistance
        }

        _uiState.update { it.copy(filteredRoutes = filtered) }
    }

    private fun parseDistance(distString: String): Double {
        // Removes non-numeric chars except dot
        val num = distString.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
        return num // Assuming miles for logic simplicity matching Figma
    }
}