package com.example.flantr.ui.auth.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flantr.ui.auth.AuthUiState
import com.example.flantr.ui.auth.AuthViewModel
import com.example.flantr.ui.components.StandardTextField

@Composable
fun AuthForm(
    uiState: AuthUiState,
    viewModel: AuthViewModel,
    navController: NavController
) {
    Column(modifier = Modifier.padding(24.dp)) {

        AuthToggleRow(isLoginMode = uiState.isLoginMode, onToggle = { viewModel.onToggleMode() })

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.errorMessage != null) {
            Text(uiState.errorMessage, color = Color.Red, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        AnimatedVisibility(visible = !uiState.isLoginMode) {
            StandardTextField(
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = "Full Name",
                icon = Icons.Default.Person
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        StandardTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = "Email",
            icon = Icons.Default.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        StandardTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = "Password",
            icon = Icons.Default.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        SubmitButton(uiState = uiState, viewModel = viewModel, navController = navController)

        Spacer(modifier = Modifier.height(16.dp))

        AuthFooter(isLoginMode = uiState.isLoginMode, onToggle = { viewModel.onToggleMode() })
    }
}
