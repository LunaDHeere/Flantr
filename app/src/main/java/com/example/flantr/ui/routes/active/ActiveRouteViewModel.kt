package com.example.flantr.ui.routes.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.model.Route
import com.example.flantr.data.model.Stop
import com.example.flantr.utils.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.util.GeoPoint as OsmGeoPoint

data class ActiveRouteUiState(
    val activeRoute: Route? = null, // The currently active route object
    val isMapNavigationActive: Boolean = false, // Is the route currently visible on the MapScreen?
    val currentStopIndex: Int = 0,
    val completedStops: Set<String> = emptySet(),
    val userNotes: Map<String, String> = emptyMap(),
    val editingNoteId: String? = null,
    val timeToFirstStop: Int? = null,
    val routePoints: List<OsmGeoPoint> = emptyList(),
)

class ActiveRouteViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ActiveRouteUiState())
    val uiState = _uiState.asStateFlow()

    // --- Navigation & State Management ---

    // Called when user clicks "Get Directions"
    fun startNavigation(route: Route, context: android.content.Context) {
        _uiState.update {
            it.copy(
                activeRoute = route,
                isMapNavigationActive = true // Show on map
            )
        }
        // Load the trail points immediately
        loadRealRoute(route.stops, context)
    }

    // Called when user clicks "Stop Route" on MapScreen
    fun stopNavigationOnMap() {
        _uiState.update {
            it.copy(isMapNavigationActive = false) // Hide from map, but keep progress
        }
    }

    // Resume simply ensures the flag is true (if the route exists)
    fun resumeNavigation() {
        if (_uiState.value.activeRoute != null) {
            _uiState.update { it.copy(isMapNavigationActive = true) }
        }
    }

    // --- Existing Logic (Updated to use activeRoute from state if needed) ---

    fun completeStop() {
        val route = _uiState.value.activeRoute ?: return
        val currentIndex = _uiState.value.currentStopIndex
        val currentStop = route.stops[currentIndex]

        _uiState.update { state ->
            state.copy(completedStops = state.completedStops + currentStop.id)
        }

        if (currentIndex < route.stops.lastIndex) {
            nextStop()
        }
    }

    fun nextStop() {
        val route = _uiState.value.activeRoute ?: return
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

    fun calculateStartEta(userLat: Double, userLng: Double, firstStop: Stop, userPace: String, useTransport: Boolean) {
        if (firstStop.geoPoint != null) {
            val dist = LocationUtils.getDistanceMeters(userLat, userLng, firstStop.geoPoint)
            val minutes = LocationUtils.calculateEtaMinutes(dist, userPace, useTransport)
            _uiState.update { it.copy(timeToFirstStop = minutes) }
        }
    }

    // Private helper to load route, exposed via startNavigation
    private fun loadRealRoute(stops: List<Stop>, context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (stops.size < 2) return@launch

            val roadManager = OSRMRoadManager(context, "FlantrApp/1.0")
            roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT)

            val waypoints = ArrayList<OsmGeoPoint>()
            stops.forEach { stop ->
                stop.geoPoint?.let { waypoints.add(OsmGeoPoint(it.lat, it.lng)) }
            }

            if (waypoints.isNotEmpty()) {
                val road = roadManager.getRoad(waypoints)
                val detailedPoints = road.mRouteHigh.map { OsmGeoPoint(it.latitude, it.longitude) }

                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(routePoints = detailedPoints) }
                }
            }
        }
    }
    fun completeStop(route: Route) {
        // We use the passed route, so this works even if navigation hasn't started
        val currentIndex = _uiState.value.currentStopIndex

        // Safety check
        if (currentIndex >= route.stops.size) return

        val currentStop = route.stops[currentIndex]

        _uiState.update { state ->
            state.copy(completedStops = state.completedStops + currentStop.id)
        }

        // Move to next stop if available
        if (currentIndex < route.stops.lastIndex) {
            nextStop(route)
        }
    }

    // 2. Restore the 'route' parameter here
    fun nextStop(route: Route) {
        if (_uiState.value.currentStopIndex < route.stops.lastIndex) {
            _uiState.update { it.copy(currentStopIndex = it.currentStopIndex + 1) }
        }
    }
}