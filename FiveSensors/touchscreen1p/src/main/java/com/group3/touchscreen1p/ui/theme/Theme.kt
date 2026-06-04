package com.group3.touchscreen1p.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NeonReactorColors = darkColorScheme(
    primary = NeonPurple,
    onPrimary = TextPrimary,

    secondary = NeonBlue,
    tertiary = ReactorPink,

    background = BackgroundDark,
    surface = BackgroundSurface,

    onBackground = TextPrimary,
    onSurface = TextPrimary,

    error = ReactorPink
)

@Composable
fun NeonReactorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NeonReactorColors,
        typography = Typography,
        content = content
    )
}