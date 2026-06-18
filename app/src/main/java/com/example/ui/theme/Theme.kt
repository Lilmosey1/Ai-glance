package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = CyberPrimary,
    onPrimary = Color.Black,
    secondary = CyberSecondary,
    onSecondary = Color.Black,
    tertiary = CyberTertiary,
    onTertiary = Color.White,
    background = CyberBg,
    onBackground = CyberTextPrimary,
    surface = CyberCard,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberCardLight,
    onSurfaceVariant = CyberTextSecondary,
    error = CyberError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark mode for premium developer look
    dynamicColor: Boolean = false, // Disable dynamic colors to enforce the Cyberpunk palette
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
