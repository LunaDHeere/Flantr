package com.example.flantr.ui.theme


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = PurplePrimary,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSurface = Color.Black
)

@Composable
fun FlantrTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}