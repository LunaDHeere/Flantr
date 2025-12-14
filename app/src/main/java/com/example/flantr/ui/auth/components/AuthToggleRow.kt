package com.example.flantr.ui.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AuthToggleRow(isLoginMode: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        AuthToggleButton("Log In", isSelected = isLoginMode, onClick = { if (!isLoginMode) onToggle() })
        AuthToggleButton("Sign Up", isSelected = !isLoginMode, onClick = { if (isLoginMode) onToggle() })
    }
}

@Composable
fun RowScope.AuthToggleButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.White else Color.Transparent
    val textColor = if (isSelected) Color(0xFF9333EA) else Color.Gray
    val shadow = if (isSelected) 2.dp else 0.dp

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = shadow,
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
            Text(text, color = textColor, fontWeight = FontWeight.SemiBold)
        }
    }
}
