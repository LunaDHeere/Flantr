package com.example.flantr.ui.routes.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.model.Route
import com.example.flantr.data.model.Stop
import com.example.flantr.data.repository.RouteRepository
import com.example.flantr.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class CreateRouteUiState(
    val routeName: String = "",
    val routeTheme: String = "",
    val routeDescription: String = "",
    val stops: List<Stop> = emptyList(),
    val editingStopId: String? = null, // Which stop is currently open for editing?
    val isSaving: Boolean = false
)

class CreateRouteViewModel(
    private val routeRepo: RouteRepository = RouteRepository(),
    private val userRepo: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRouteUiState())
    val uiState: StateFlow<CreateRouteUiState> = _uiState

    // --- Field Updates ---
    fun updateName(name: String) { _uiState.update { it.copy(routeName = name) } }
    fun updateTheme(theme: String) { _uiState.update { it.copy(routeTheme = theme) } }
    fun updateDescription(desc: String) { _uiState.update { it.copy(routeDescription = desc) } }

    // --- Stop Logic ---

    fun addStop() {
        val newStop = Stop(
            id = UUID.randomUUID().toString(), // Generate unique ID
            name = "",
            address = "",
            description = "",
            estimatedTimeMinutes = 30
        )
        _uiState.update {
            it.copy(
                stops = it.stops + newStop,
                editingStopId = newStop.id // Auto-open the new stop
            )
        }
    }

    fun removeStop(stopId: String) {
        _uiState.update {
            it.copy(
                stops = it.stops.filter { stop -> stop.id != stopId },
                editingStopId = if (it.editingStopId == stopId) null else it.editingStopId
            )
        }
    }

    fun updateStop(stopId: String, update: (Stop) -> Stop) {
        _uiState.update { state ->
            val updatedStops = state.stops.map { stop ->
                if (stop.id == stopId) update(stop) else stop
            }
            state.copy(stops = updatedStops)
        }
    }

    fun toggleEditStop(stopId: String?) {
        _uiState.update { it.copy(editingStopId = stopId) }
    }

    fun moveStop(index: Int, direction: Int) {
        val currentStops = _uiState.value.stops.toMutableList()
        val newIndex = index + direction

        if (newIndex in 0..currentStops.lastIndex) {
            val temp = currentStops[index]
            currentStops[index] = currentStops[newIndex]
            currentStops[newIndex] = temp
            _uiState.update { it.copy(stops = currentStops) }
        }
    }

    // --- Save Logic ---

    fun saveRoute(onSuccess: () -> Unit) {
        if (_uiState.value.routeName.isBlank()) return // Validation

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val totalTime = _uiState.value.stops.sumOf { it.estimatedTimeMinutes }

            val newRoute = Route(
                name = _uiState.value.routeName,
                theme = _uiState.value.routeTheme.ifBlank { "Custom" },
                description = _uiState.value.routeDescription,
                totalTimeMinutes = totalTime,
                distance = "${_uiState.value.stops.size * 0.5} km", // Mock distance calculation
                stops = _uiState.value.stops.filter { it.name.isNotBlank() }, // Filter empty ones
                authorId = userRepo.getCurrentUser()?.id ?: ""
            )

            try {
                // 1. Save to global Route DB
                val routeId = routeRepo.createRoute(newRoute)

                // 2. Auto-bookmark it for the creator
                userRepo.bookmarkRoute(routeId)

                onSuccess()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}