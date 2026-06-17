package com.group3.gyromaze.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary   = NeonCyan,
    secondary = NeonPurple,
    background = NavyDeep,
    surface    = NavyCard,
    onPrimary  = NavyDeep,
    onBackground = TextWhite,
    onSurface    = TextWhite,
)

@Composable
fun FiveSensorsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content     = content
    )
}
