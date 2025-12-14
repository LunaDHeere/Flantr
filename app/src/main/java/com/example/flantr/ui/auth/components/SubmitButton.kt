package com.example.flantr.ui.auth.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flantr.ui.auth.AuthUiState
import com.example.flantr.ui.auth.AuthViewModel
import com.example.flantr.ui.navigation.Screen

@Composable
fun SubmitButton(
    uiState: AuthUiState,
    viewModel: AuthViewModel,
    navController: NavController
) {
    Button(
        onClick = { viewModel.onSubmit { navController.navigate(Screen.Home.route) } },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA))
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text(if (uiState.isLoginMode) "Log In" else "Create Account")
        }
    }
}
