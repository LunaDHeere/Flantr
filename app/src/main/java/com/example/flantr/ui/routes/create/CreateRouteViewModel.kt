package com.example.flantr.ui.routes.create

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
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

data class CreateRouteUiState(
    val routeName: String = "",
    val routeTheme: String = "",
    val routeDescription: String = "",
    val stops: List<Stop> = emptyList(),
    val editingStopId: String? = null,
    val isSaving: Boolean = false,
    val isCalculating: Boolean = false, // Shows a spinner while calculating
    val selectedIconId: String = "place"
)

class CreateRouteViewModel(
    private val routeRepo: RouteRepository = RouteRepository(),
    private val userRepo: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRouteUiState())
    val uiState: StateFlow<CreateRouteUiState> = _uiState

    // --- Basic Updates ---
    fun updateName(name: String) { _uiState.update { it.copy(routeName = name) } }
    fun updateTheme(theme: String) { _uiState.update { it.copy(routeTheme = theme) } }
    fun updateDescription(desc: String) { _uiState.update { it.copy(routeDescription = desc) } }
    fun updateIcon(iconId: String) { _uiState.update { it.copy(selectedIconId = iconId) } }

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

    // Called whenever the user types in the address field
    fun updateStop(stopId: String, context: Context, update: (Stop) -> Stop) {
        _uiState.update { state ->
            val updatedStops = state.stops.map { stop ->
                if (stop.id == stopId) update(stop) else stop
            }
            state.copy(stops = updatedStops)
        }

        // Debounce logic could go here, but for now we calculate on every update
        // Only calculate if the address actually changed
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

            // Define speed in km/minute
            val walkingSpeedKmMin = when (pace) {
                "slow" -> 0.05 // ~3 km/h
                "fast" -> 0.11 // ~6.5 km/h
                else -> 0.08   // ~5 km/h (moderate)
            }

            // Public transport assumption: 3x faster than walking
            val effectiveSpeed = if (usePublicTransport) walkingSpeedKmMin * 3 else walkingSpeedKmMin

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
                        // Geocoding failed (bad address or no internet), keep old geo or null
                    }
                }

                // Calculate Time
                var timeToGetHere = 0
                if (i > 0 && previousGeo != null && currentGeo != null) {
                    val distKm = calculateDistanceKm(previousGeo, currentGeo)
                    // Time = Distance / Speed
                    timeToGetHere = (distKm / effectiveSpeed).roundToInt()

                    // Add buffer: Assume 15 mins spent AT the previous stop
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

    // Haversine Formula: Standard way to calculate distance between two points on a sphere (Earth)
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
    fun saveRoute(onSuccess: () -> Unit) {
        if (_uiState.value.routeName.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            // Sum up the calculated times
            val totalTime = _uiState.value.stops.sumOf { it.estimatedTimeMinutes }
            val totalStops = _uiState.value.stops.size

            // Rough distance estimate based on stop count if exact math fails
            val distString = "${totalStops * 0.8} km"

            val newRoute = Route(
                name = _uiState.value.routeName,
                theme = _uiState.value.routeTheme.ifBlank { "Custom" },
                description = _uiState.value.routeDescription,
                totalTimeMinutes = totalTime,
                distance = distString,
                stops = _uiState.value.stops.filter { it.name.isNotBlank() },
                authorId = userRepo.getCurrentUser()?.id ?: "",
                iconId = _uiState.value.selectedIconId // Use the selected icon
            )

            try {
                // 1. Create the route in Firestore (returns the new ID)
                val routeId = routeRepo.createRoute(newRoute)

                // 2. Immediately bookmark it for the user
                userRepo.bookmarkRoute(routeId)

                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}