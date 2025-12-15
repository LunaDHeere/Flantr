package com.example.flantr.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.model.Route
import com.example.flantr.data.repository.RouteRepository
import com.example.flantr.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val savedRoutes: List<Route> = emptyList(),
    val popularRoutes: List<Route> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
class HomeViewModel(
    private val routeRepository: RouteRepository = RouteRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Fetch All Routes
                val allRoutes = routeRepository.getAllRoutes()

                // 2. Filter/Sort for "Popular"
                // Sort by popularityScore descending
                val popular = allRoutes.sortedByDescending { it.popularityScore }

                // 3. Fetch Saved Routes
                val user = userRepository.getCurrentUser()
                val savedRoutesList = mutableListOf<Route>()

                if (user != null && user.savedRouteIds.isNotEmpty()) {
                    // Efficiently match existing routes if we already fetched them in allRoutes
                    // Otherwise, fetch specific IDs (omitted for brevity)
                    savedRoutesList.addAll(
                        allRoutes.filter { user.savedRouteIds.contains(it.id) }
                    )
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        popularRoutes = popular,
                        savedRoutes = savedRoutesList,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // Handle error
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun bookmarkRoute(route: Route) {
        viewModelScope.launch {
            val isCurrentlySaved = _uiState.value.savedRoutes.any { it.id == route.id }

            // Optimistic Update
            if (isCurrentlySaved) {
                _uiState.update { state ->
                    state.copy(savedRoutes = state.savedRoutes.filter { it.id != route.id })
                }
                userRepository.removeBookmark(route.id)
            } else {
                _uiState.update { state ->
                    state.copy(savedRoutes = state.savedRoutes + route)
                }
                userRepository.bookmarkRoute(route.id)
            }
        }
    }
}