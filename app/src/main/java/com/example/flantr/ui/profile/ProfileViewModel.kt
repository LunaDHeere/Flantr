package com.example.flantr.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.model.User
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
    val memberSince: String = "", // UI wants a String
    val accessibilityMode: Boolean = false,
    val avoidStairs: Boolean = false,
    val tripCount: Int = 0,
    val placesVisited: Int = 0
)

class ProfileViewModel(private val repo: UserRepository = UserRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = repo.getCurrentUser()
            if (user != null) {
                _uiState.value = user.toUiState()
            }
        }
    }

    fun toggleAccessibilityMode() {
        // Toggle based on CURRENT state, not the last emitted value
        val current = _uiState.value
        val newValue = !current.accessibilityMode

        // Optimistic update (update UI immediately)
        _uiState.value = current.copy(accessibilityMode = newValue)

        viewModelScope.launch {
            repo.updateUser(current.id, mapOf("accessibilityMode" to newValue))
        }
    }

    fun toggleAvoidStairs() {
        val current = _uiState.value
        val newValue = !current.avoidStairs

        _uiState.value = current.copy(avoidStairs = newValue)

        viewModelScope.launch {
            repo.updateUser(current.id, mapOf("avoidStairs" to newValue))
        }
    }
}

// FIX: Convert the Long timestamp to a formatted String
private fun User.toUiState(): ProfileUiState {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormatter.format(Date(memberSince))

    return ProfileUiState(
        id = id,
        name = name,
        email = email,
        memberSince = dateString, // Pass the converted string here
        accessibilityMode = accessibilityMode,
        avoidStairs = avoidStairs,
        tripCount = tripCount,
        placesVisited = placesVisited
    )
}