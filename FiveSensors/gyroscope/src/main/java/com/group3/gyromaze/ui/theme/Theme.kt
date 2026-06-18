package com.group3.gyromaze.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GyroMazeDarkColors = darkColorScheme(
    primary = NeonCyan,
    secondary = NeonPurple,
    tertiary = NeonPink,
    background = NavyDeep,
    surface = NavyCard,
    onPrimary = NavyDeep,
    onSecondary = NavyDeep,
    onBackground = TextWhite,
    onSurface = TextWhite,
)

@Composable
fun FiveSensorsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GyroMazeDarkColors,
        typography = Typography,
        content = content
    )
}