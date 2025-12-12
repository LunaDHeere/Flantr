package com.example.flantr.ui.profile
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// so this was an idea from figma ai but i have NO CLUE HOW TF I'M GOING TO DO THAT WITH
// THE GOOGLE MAPS THING
enum class WalkingPace { SLOW, MODERATE, FAST }
enum class AppTheme { LIGHT, DARK, AUTO }

data class ProfileUiState(
    // TODO: there should be a place to change the password

    //TODO: add logic to change password SECURELY (adding some security checks like
    // inputting the previous password before continuing, etc.
    val name: String = "Alex Morgan",
    val email: String = "alex.morgan@email.com",
    val memberSince: String = "January 2024",
    val tripCount: Int = 12,
    val placesVisited: Int = 47,

    // Once again, a VERY COOL idea but absolutely NO CLUE how i'm going to execute it hihihi
    val includePublicTransport: Boolean = true,
    val walkingPace: WalkingPace = WalkingPace.MODERATE,
    val maxWalkingDistance: Float = 5f, // this is one of the few things i do think will be possible
    // maybe i'm not seeing how complicated this is da is ook een ding natuurlijk
    val accessibilityMode: Boolean = false,
    val avoidStairs: Boolean = false,

    //TODO: add some notifications logic and ui
    //TODO: when you finally make Colors a seperate theme we CAN FINALLY MAKE A DARK THEME WOOPWOOP
    // so make that colors theme + logic to change themes thanks <3
)
class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun togglePublicTransport() {
        _uiState.update { it.copy(includePublicTransport = !it.includePublicTransport) }
    }

    fun setWalkingPace(pace: WalkingPace) {
        _uiState.update { it.copy(walkingPace = pace) }
    }

    fun setMaxWalkingDistance(distance: Float) {
        _uiState.update { it.copy(maxWalkingDistance = distance) }
    }

    fun toggleAccessibilityMode() {
        _uiState.update { it.copy(accessibilityMode = !it.accessibilityMode) }
    }

    fun toggleAvoidStairs() {
        _uiState.update { it.copy(avoidStairs = !it.avoidStairs) }
    }

}