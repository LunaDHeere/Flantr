package com.example.flantr.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flantr.data.model.User
import com.example.flantr.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Important import for .await()

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val errorMessage: String? = null,
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false
)

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onToggleMode() {
        _uiState.update { it.copy(isLoginMode = !it.isLoginMode, errorMessage = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun onSubmit(onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                if (uiState.value.isLoginMode) {
                    login()
                } else {
                    signUp()
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Something went wrong")
                }
            } finally {
                // ✅ ALWAYS runs, even if coroutine is cancelled
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun login() {
        val state = uiState.value
        auth.signInWithEmailAndPassword(state.email, state.password).await()
    }

    private suspend fun signUp() {
        val state = uiState.value

        val result =
            auth.createUserWithEmailAndPassword(state.email, state.password).await()

        val uid = result.user!!.uid

        val newUser = User(
            id = uid,
            name = state.username,
            email = state.email
        )

        userRepository.saveUser(newUser)
    }
}