package com.example.flantr.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.model.Collection
import com.example.flantr.data.model.Route
import com.example.flantr.data.repository.RouteRepository
import com.example.flantr.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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
    val appTheme: String = "auto",

    // Collections & Routes
    val collections: List<Collection> = emptyList(),
    val createdRoutes: List<Route> = emptyList(),
    val showCreateCollectionDialog: Boolean = false,
    val showEditProfileDialog: Boolean = false
)

class ProfileViewModel(
    private val userRepo: UserRepository = UserRepository(),
    private val routeRepo: RouteRepository = RouteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        observeUser()
        loadCollections()
        loadCreatedRoutes()
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepo.getUserFlow().collect{ user ->
                user?.let{
                    _uiState.update { currentState ->
                        currentState.copy(
                            id = it.id,
                            name = it.name.ifEmpty { "Unknown" },
                            email = it.email,
                            memberSince = formatDate(it.memberSince),
                            tripCount = it.tripCount,
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
        }
    }

    // --- Profile Info Updates ---

    fun toggleEditProfileDialog(show: Boolean) {
        _uiState.update { it.copy(showEditProfileDialog = show) }
    }

    fun updateName(newName: String) {
        val current = _uiState.value
        if (newName.isNotBlank() && newName != current.name) {
            _uiState.update { it.copy(name = newName) }
            // Save to Firestore
            viewModelScope.launch {
                userRepo.updateUser(current.id, mapOf("name" to newName))
            }
        }
        toggleEditProfileDialog(false)
    }

    // --- Preference Updates ---

    // Generic function to update any boolean field
    fun updateBoolean(field: String, value: Boolean) {
        val current = _uiState.value
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
        viewModelScope.launch { userRepo.updateUser(current.id, mapOf(field to value)) }
    }

    fun setWalkingPace(pace: String) {
        val current = _uiState.value
        _uiState.update { it.copy(walkingPace = pace) }
        viewModelScope.launch { userRepo.updateUser(current.id, mapOf("walkingPace" to pace)) }
    }

    fun setMaxDistance(dist: Float) {
        val current = _uiState.value
        _uiState.update { it.copy(maxWalkingDistance = dist) }
        viewModelScope.launch { userRepo.updateUser(current.id, mapOf("maxWalkingDistance" to dist)) }
    }

    fun setTheme(theme: String) {
        val current = _uiState.value
        _uiState.update { it.copy(appTheme = theme) }
        viewModelScope.launch { userRepo.updateUser(current.id, mapOf("appTheme" to theme)) }
    }

    // --- Collections & Routes ---

    private fun loadCollections(){
        viewModelScope.launch {
            val cols = userRepo.getUserCollections()
            _uiState.update { it.copy(collections = cols) }
        }
    }

    private fun loadCreatedRoutes(){
        viewModelScope.launch {
            val userId = userRepo.getCurrentUser()?.id ?: return@launch
            val myRoutes = routeRepo.getRoutesByAuthor(userId)
            _uiState.update { it.copy(createdRoutes = myRoutes) }
        }
    }

    fun createCollection(name: String, desc: String, color: String) {
        viewModelScope.launch {
            val newCol = Collection(title = name, description = desc, color = color)
            userRepo.createCollection(newCol)
            loadCollections()
            _uiState.update { it.copy(showCreateCollectionDialog = false) }
        }
    }

    fun deleteCollection(id: String) {
        viewModelScope.launch {
            userRepo.deleteCollection(id)
            loadCollections()
        }
    }

    fun toggleCreateDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateCollectionDialog = show) }
    }

    private fun formatDate(timestamp: Long): String {
        return try {
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(timestamp))
        } catch (e: Exception) { "Unknown" }
    }

    // In ProfileViewModel.kt
    fun refreshData() {
        observeUser()
        loadCollections()
        loadCreatedRoutes()
    }
}