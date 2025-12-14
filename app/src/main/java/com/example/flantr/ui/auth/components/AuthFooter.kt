package com.example.flantr.ui.auth.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AuthFooter(isLoginMode: Boolean, onToggle: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Log In",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable { onToggle() }
                .align(Alignment.Center)
        )
    }
}

