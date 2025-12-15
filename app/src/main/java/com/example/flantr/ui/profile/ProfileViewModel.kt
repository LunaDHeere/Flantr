package com.example.flantr.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ProfileUiState(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val memberSince: String = "",
    val tripCount: Int = 0,
    val placesVisited: Int = 0,

    // Transportation
    val includePublicTransport: Boolean = true,
    val walkingPace: String = "moderate",
    val maxWalkingDistance: Float = 5f,

    // Accessibility
    val accessibilityMode: Boolean = false,
    val avoidStairs: Boolean = false,

    // Route
    val preferScenic: Boolean = true,

    // Notifications
    val notifyRouteReminders: Boolean = true,
    val notifyNewRoutes: Boolean = true,
    val notifyNearbyPlaces: Boolean = false,

    // Appearance
    val appTheme: String = "auto"
)

class ProfileViewModel(
    private val repo: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = repo.getCurrentUser()
            user?.let {
                _uiState.value = ProfileUiState(
                    id = it.id,
                    name = it.name.ifEmpty { "Unknown" },
                    email = it.email,
                    memberSince = formatDate(it.memberSince),
                    tripCount = it.tripCount,
                    placesVisited = it.placesVisited,

                    // Map new fields
                    includePublicTransport = it.includePublicTransport,
                    walkingPace = it.walkingPace,
                    maxWalkingDistance = it.maxWalkingDistance,
                    accessibilityMode = it.accessibilityMode,
                    avoidStairs = it.avoidStairs,
                    preferScenic = it.preferScenic,
                    notifyRouteReminders = it.notifyRouteReminders,
                    notifyNewRoutes = it.notifyNewRoutes,
                    notifyNearbyPlaces = it.notifyNearbyPlaces,
                    appTheme = it.appTheme
                )
            }
        }
    }

    // Generic function to update any boolean field
    fun updateBoolean(field: String, value: Boolean) {
        val current = _uiState.value
        // Update local state immediately for UI responsiveness
        _uiState.value = when(field) {
            "includePublicTransport" -> current.copy(includePublicTransport = value)
            "accessibilityMode" -> current.copy(accessibilityMode = value)
            "avoidStairs" -> current.copy(avoidStairs = value)
            "preferScenic" -> current.copy(preferScenic = value)
            "notifyRouteReminders" -> current.copy(notifyRouteReminders = value)
            "notifyNewRoutes" -> current.copy(notifyNewRoutes = value)
            "notifyNearbyPlaces" -> current.copy(notifyNearbyPlaces = value)
            else -> current
        }
        // Save to Firebase
        viewModelScope.launch { repo.updateUser(current.id, mapOf(field to value)) }
    }

    fun setWalkingPace(pace: String) {
        val current = _uiState.value
        _uiState.value = current.copy(walkingPace = pace)
        viewModelScope.launch { repo.updateUser(current.id, mapOf("walkingPace" to pace)) }
    }

    fun setMaxDistance(dist: Float) {
        val current = _uiState.value
        _uiState.value = current.copy(maxWalkingDistance = dist)
        // Note: For sliders, usually you debounce the DB update, but direct update is fine for now
        viewModelScope.launch { repo.updateUser(current.id, mapOf("maxWalkingDistance" to dist)) }
    }

    fun setTheme(theme: String) {
        val current = _uiState.value
        _uiState.value = current.copy(appTheme = theme)
        viewModelScope.launch { repo.updateUser(current.id, mapOf("appTheme" to theme)) }
    }

    private fun formatDate(timestamp: Long): String {
        return try {
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(timestamp))
        } catch (e: Exception) { "Unknown" }
    }
}