package com.example.flantr.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val PurplePrimary = Color(0xFF9333EA)
val BluePrimary = Color(0xFF3B82F6)

val GreenSuccess = Color(0xFF22C55E)

val GrayBorder = Color(0xFFE5E7EB)
val GrayText = Color(0xFF6B7280)

val BackgroundGradient = Brush.verticalGradient(
    listOf(
        Color(0xFFEFF6FF),
        Color.White,
        Color(0xFFFAF5FF)
    )
)
val PrimaryGradient = Brush.linearGradient(
    listOf(
        Color(0xFF3B82F6),
        Color(0xFF9333EA)
    )
)
val AuthGradient = Brush.linearGradient(
    listOf(
        Color(0xFF2563EB),
        Color(0xFF9333EA),
        Color(0xFFEC4899)
    )
)