package com.example.flantr.ui.routes.active

import androidx.lifecycle.ViewModel
import com.example.flantr.data.model.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ActiveRouteUiState(
    val currentStopIndex: Int = 0,
    val completedStops: Set<String> = emptySet(), // Set of Stop IDs
    val userNotes: Map<String, String> = emptyMap(), // Map of StopID -> Note
    val editingNoteId: String? = null // ID of the stop currently being edited
)

class ActiveRouteViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ActiveRouteUiState())
    val uiState = _uiState.asStateFlow()

    // Logic: Mark stop as complete and auto-advance
    fun completeStop(route: Route) {
        val currentIndex = _uiState.value.currentStopIndex
        val currentStop = route.stops[currentIndex]

        _uiState.update { state ->
            state.copy(completedStops = state.completedStops + currentStop.id)
        }

        // If not last stop, move next
        if (currentIndex < route.stops.lastIndex) {
            nextStop(route)
        }
    }

    fun nextStop(route: Route) {
        if (_uiState.value.currentStopIndex < route.stops.lastIndex) {
            _uiState.update { it.copy(currentStopIndex = it.currentStopIndex + 1) }
        }
    }

    fun previousStop() {
        if (_uiState.value.currentStopIndex > 0) {
            _uiState.update { it.copy(currentStopIndex = it.currentStopIndex - 1) }
        }
    }

    fun jumpToStop(index: Int) {
        _uiState.update { it.copy(currentStopIndex = index) }
    }

    // Notes Logic
    fun setEditingNote(stopId: String?) {
        _uiState.update { it.copy(editingNoteId = stopId) }
    }

    fun updateUserNote(stopId: String, note: String) {
        _uiState.update { state ->
            val newNotes = state.userNotes.toMutableMap()
            newNotes[stopId] = note
            state.copy(userNotes = newNotes)
        }
    }
}