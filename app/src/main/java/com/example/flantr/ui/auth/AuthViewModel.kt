package com.example.flantr.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val errorMessage: String? = null,
    val isLoginMode: Boolean = true,
    val isLoading : Boolean = false //for UX, no actual logic.
)
//TODO: add logic for a "confirm password" option

class AuthViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onToggleMode(){
        _uiState.update { it.copy(isLoginMode = !it.isLoginMode, errorMessage = null) }
    }
    fun onEmailChange(newEmail: String){
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChange(newPassword: String){
        _uiState.update { it.copy(password = newPassword) }
    }

    fun onUsernameChange(newUsername: String){
        _uiState.update { it.copy(username = newUsername) }
    }

    fun onSubmit(onSuccess: () -> Unit){
        _uiState.update { it.copy(isLoading = true) }

        //TODO: add password checking logic here (login mode)
        //TODO: add logic for a create new user (signUp mode)

        onSuccess()

        _uiState.update { it.copy(isLoading = false) }
    }


}