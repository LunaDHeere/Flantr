package com.example.flantr.ui.routes.active

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.model.Route
import com.example.flantr.data.model.Stop
import com.example.flantr.data.repository.UserRepository
import com.example.flantr.utils.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.util.GeoPoint as OsmGeoPoint
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.AndroidViewModel
import com.example.flantr.data.repository.RouteRepository

data class ActiveRouteUiState(
    val calculatedDurations: Map<Int, Int> = emptyMap(),
    val activeRoute: Route? = null,
    val isMapNavigationActive: Boolean = false,
    val currentStopIndex: Int = 0,
    val completedStops: Set<String> = emptySet(),
    val userNotes: Map<String, String> = emptyMap(),
    val editingNoteId: String? = null,
    val timeToFirstStop: Int? = null,
    val routePoints: List<OsmGeoPoint> = emptyList(),
    val currentUserId: String = ""
)

class ActiveRouteViewModel(application: Application ) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val userRepo = UserRepository()
    private val routeRepo = RouteRepository()
    private val _uiState = MutableStateFlow(ActiveRouteUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val firebaseUser = auth.currentUser
        _uiState.update { it.copy(currentUserId = firebaseUser?.uid ?: "") }

        observeUserSettings()
    }
    /* -------------------- User Settings logic -------------------- */

    private fun observeUserSettings() {
        viewModelScope.launch {
            userRepo.getUserFlow().collect { user ->
                val currentRoute = _uiState.value.activeRoute
                if (user != null && currentRoute != null) {
                    loadRealRoute(currentRoute.stops, getApplication())
                }
            }
        }

        viewModelScope.launch {
            _uiState.collect { state ->
                val route = state.activeRoute
                if (route != null && state.calculatedDurations.isEmpty()) {
                    loadRealRoute(route.stops, getApplication())
                }
            }
        }
    }

    /* -------------------- Navigation -------------------- */

    fun startNavigation(route: Route) {
        _uiState.update {
            it.copy(
                activeRoute = route,
                isMapNavigationActive = true
            )
        }
    }

    fun stopNavigationOnMap() {
        _uiState.update { it.copy(isMapNavigationActive = false) }
    }

    /* -------------------- Stops -------------------- */

    fun completeStop() {
        _uiState.update { state ->
            val stops = state.activeRoute?.stops ?: return@update state
            val index = state.currentStopIndex

            if (index !in stops.indices) return@update state

            val stopId = stops[index].id
            val newCompleted = state.completedStops + stopId

            // Advance index atomically if not at the end
            val nextIndex = if (index < stops.lastIndex) index + 1 else index

            state.copy(
                completedStops = newCompleted,
                currentStopIndex = nextIndex
            )
        }
    }

    fun nextStop() {
        _uiState.update { state ->
            val stops = state.activeRoute?.stops ?: return@update state
            if (state.currentStopIndex < stops.lastIndex) {
                state.copy(currentStopIndex = state.currentStopIndex + 1)
            } else {
                state
            }
        }
    }

    fun previousStop() {
        _uiState.update { state ->
            if (state.currentStopIndex > 0) {
                state.copy(currentStopIndex = state.currentStopIndex - 1)
            } else {
                state
            }
        }
    }

    fun jumpToStop(index: Int) {
        _uiState.update { state ->
            val stops = state.activeRoute?.stops ?: return@update state
            if (index in stops.indices) {
                state.copy(currentStopIndex = index)
            } else {
                state
            }
        }
    }

    /* -------------------- Notes -------------------- */

    fun setEditingNote(stopId: String?) {
        _uiState.update { it.copy(editingNoteId = stopId) }
    }

    fun updateUserNote(stopId: String, note: String) {
        _uiState.update {
            it.copy(userNotes = it.userNotes + (stopId to note))
        }
    }

    /* -------------------- ETA -------------------- */

    fun calculateStartEta(
        userLat: Double,
        userLng: Double,
        firstStop: Stop,
        userPace: String,
        useTransport: Boolean
    ) {
        val geoPoint = firstStop.geoPoint ?: return
        val distance = LocationUtils.getDistanceMeters(userLat, userLng, geoPoint)
        val minutes = LocationUtils.calculateEtaMinutes(distance, userPace, useTransport)

        _uiState.update { it.copy(timeToFirstStop = minutes) }
    }

    /* -------------------- OSRM Route -------------------- */

    private fun loadRealRoute(stops: List<Stop>, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepo.getCurrentUser() ?: return@launch
            val useTransport = user.includePublicTransport

            val roadManager = OSRMRoadManager(context, "FlantrApp/1.0").apply {
                setMean(OSRMRoadManager.MEAN_BY_FOOT)
            }

            val waypoints = stops.mapNotNull {
                it.geoPoint?.let { gp -> OsmGeoPoint(gp.lat, gp.lng) }
            }

            if (waypoints.isEmpty()) return@launch

            val road = roadManager.getRoad(ArrayList(waypoints))

            val routePoints = road.mRouteHigh.map {
                OsmGeoPoint(it.latitude, it.longitude)
            }

            val durations = road.mLegs.mapIndexed { index, leg ->
                val rawMinutes = (leg.mDuration / 60).toInt()
                val adjustedMinutes = if (useTransport) (rawMinutes / 2.5).toInt() else rawMinutes

                (index + 1) to maxOf(1, adjustedMinutes)
            }.toMap()

            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        routePoints = routePoints,
                        calculatedDurations = durations
                    )
                }
            }
        }
    }

    /* -------------------- Delete -------------------- */

    fun deleteRoute(routeId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                routeRepo.deleteRoute(routeId)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun loadRoute(routeId: String) {
        viewModelScope.launch {
            val route = routeRepo.getRouteById(routeId)
            if (route != null) {
                _uiState.update { it.copy(activeRoute = route) }
            }
        }
    }
}
