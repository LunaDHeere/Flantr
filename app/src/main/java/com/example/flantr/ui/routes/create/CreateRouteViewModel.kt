package com.example.flantr.ui.routes.create

import android.app.Application
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.model.GeoPoint
import com.example.flantr.data.model.Route
import com.example.flantr.data.model.Stop
import com.example.flantr.data.repository.RouteRepository
import com.example.flantr.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.*
import com.google.firebase.auth.FirebaseAuth

data class CreateRouteUiState(
    val routeName: String = "",
    val routeTheme: String = "",
    val routeDescription: String = "",
    val stops: List<Stop> = emptyList(),
    val editingStopId: String? = null,
    val isSaving: Boolean = false,
    val isCalculating: Boolean = false,
    val selectedIconId: String = "place"
)

class CreateRouteViewModel(application: Application) : AndroidViewModel(application) {


    private val routeRepo: RouteRepository = RouteRepository()
    private val userRepo: UserRepository = UserRepository()
    private val _uiState = MutableStateFlow(CreateRouteUiState())
    private val auth = FirebaseAuth.getInstance()
    val uiState: StateFlow<CreateRouteUiState> = _uiState

    // --- Basic Updates ---
    fun updateName(name: String) {
        _uiState.update { it.copy(routeName = name) }
    }

    fun updateTheme(theme: String) {
        _uiState.update { it.copy(routeTheme = theme) }
    }

    fun updateDescription(desc: String) {
        _uiState.update { it.copy(routeDescription = desc) }
    }

    fun updateIcon(iconId: String) {
        _uiState.update { it.copy(selectedIconId = iconId) }
    }

    fun addStop() {
        val newStop = Stop(
            id = UUID.randomUUID().toString(),
            name = "",
            estimatedTimeMinutes = 0 // Will be auto-calculated
        )
        _uiState.update { it.copy(stops = it.stops + newStop, editingStopId = newStop.id) }
    }

    fun removeStop(stopId: String) {
        _uiState.update { it.copy(stops = it.stops.filter { s -> s.id != stopId }) }
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

    // --- SMART LOGIC STARTS HERE ---

    fun updateStopText(stopId: String, name: String, address: String, description: String) {
        _uiState.update { state ->
            val updatedStops = state.stops.map { stop ->
                if (stop.id == stopId) stop.copy(name = name, address = address, description = description)
                else stop
            }
            state.copy(stops = updatedStops)
        }
    }

    // 2. Call this manually when a user finishes editing or clicks "Done Editing"
    fun finalizeStopEditing() {
        val context = getApplication<Application>().applicationContext
        recalculateRouteTimes(context)
    }

    fun updateStop(stopId: String, context: Context, update: (Stop) -> Stop) {
        _uiState.update { state ->
            val updatedStops = state.stops.map { stop ->
                if (stop.id == stopId) update(stop) else stop
            }
            state.copy(stops = updatedStops)
        }

        val changedStop = _uiState.value.stops.find { it.id == stopId }
        if (changedStop?.address?.isNotBlank() == true) {
            recalculateRouteTimes(context)
        }
    }

    private fun recalculateRouteTimes(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isCalculating = true) }

            // 1. Get User Profile Settings for speed
            val user = userRepo.getCurrentUser()
            val usePublicTransport = user?.includePublicTransport ?: false
            val pace = user?.walkingPace ?: "moderate"

            val walkingSpeedKmMin = when (pace) {
                "slow" -> 0.05
                "fast" -> 0.11
                else -> 0.08
            }

            val effectiveSpeed =
                if (usePublicTransport) walkingSpeedKmMin * 3 else walkingSpeedKmMin

            val geocoder = Geocoder(context)
            val calculatedStops = _uiState.value.stops.toMutableList()
            var previousGeo: GeoPoint? = null

            // 2. Loop through all stops
            for (i in calculatedStops.indices) {
                val stop = calculatedStops[i]
                var currentGeo = stop.geoPoint

                // Geocode: Address -> Lat/Lng
                if (stop.address.isNotBlank()) {
                    try {
                        // Max 1 result
                        @Suppress("DEPRECATION") // Suppress warning for older API levels
                        val addresses = geocoder.getFromLocationName(stop.address, 1)
                        if (!addresses.isNullOrEmpty()) {
                            currentGeo = GeoPoint(addresses[0].latitude, addresses[0].longitude)
                        }
                    } catch (e: Exception) {
                    }
                }

                var timeToGetHere = 0
                if (i > 0 && previousGeo != null && currentGeo != null) {
                    val distKm = calculateDistanceKm(previousGeo, currentGeo)
                    timeToGetHere = (distKm / effectiveSpeed).roundToInt()

                    timeToGetHere += 15
                }

                calculatedStops[i] = stop.copy(
                    geoPoint = currentGeo,
                    estimatedTimeMinutes = if (timeToGetHere > 0) timeToGetHere else 0
                )

                previousGeo = currentGeo
            }

            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(stops = calculatedStops, isCalculating = false) }
            }
        }
    }

    private fun calculateDistanceKm(p1: GeoPoint, p2: GeoPoint): Double {
        val R = 6371.0 // Earth Radius in km
        val dLat = Math.toRadians(p2.lat - p1.lat)
        val dLon = Math.toRadians(p2.lng - p1.lng)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(p1.lat)) * cos(Math.toRadians(p2.lat)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    // --- Save Logic ---
    fun saveRoute(onSuccess: (String) -> Unit) { // Change to pass the new routeId
        val validStops = _uiState.value.stops.filter { it.name.isNotBlank() }
        if (_uiState.value.routeName.isBlank() || validStops.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try{
                val totalTime = validStops.sumOf { it.estimatedTimeMinutes }

                var totalKm = 0.0
                for (i in 0 until validStops.size - 1) {
                    val p1 = validStops[i].geoPoint
                    val p2 = validStops[i + 1].geoPoint
                    if (p1 != null && p2 != null) {
                        totalKm += calculateDistanceKm(p1, p2)
                    }
                }
                val distString = "%.1f km".format(totalKm)

                val newRoute = Route(
                    name = _uiState.value.routeName,
                    theme = _uiState.value.routeTheme.ifBlank { "Custom" },
                    description = _uiState.value.routeDescription,
                    totalTimeMinutes = totalTime,
                    distance = distString,
                    stops = validStops,
                    authorId = auth.currentUser?.uid ?: "",
                    iconId = _uiState.value.selectedIconId
                )
                val newRouteId = routeRepo.createRoute(newRoute)
                withContext(Dispatchers.Main) {
                    onSuccess(newRouteId)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}