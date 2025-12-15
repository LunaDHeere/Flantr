package com.example.flantr.ui.routes.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.model.Route
import com.example.flantr.data.repository.RouteRepository
import com.example.flantr.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FavouritesUiState(
    val isLoading: Boolean = true,
    val favouriteRoutes: List<Route> = emptyList(),
    val selectedCategory: String = "All"
)

class FavouritesViewModel(
    private val userRepo: UserRepository = UserRepository(),
    private val routeRepo: RouteRepository = RouteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouritesUiState())
    val uiState: StateFlow<FavouritesUiState> = _uiState

    init {
        loadFavourites()
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 1. Get the current user to find the list of IDs
            val user = userRepo.getCurrentUser()
            val savedIds = user?.savedRouteIds ?: emptyList()

            // 2. Fetch the actual Route objects for those IDs
            val routes = savedIds.mapNotNull { id ->
                routeRepo.getRouteById(id)
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                favouriteRoutes = routes
            )
        }
    }

    fun removeFavourite(routeId: String) {
        viewModelScope.launch {
            // 1. Remove from database
            userRepo.removeBookmark(routeId)

            // 2. Update UI immediately (optimistic update)
            val updatedList = _uiState.value.favouriteRoutes.filter { it.id != routeId }
            _uiState.value = _uiState.value.copy(favouriteRoutes = updatedList)
        }
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }
}