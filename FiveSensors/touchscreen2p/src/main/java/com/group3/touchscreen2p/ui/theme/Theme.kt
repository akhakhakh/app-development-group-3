package com.group3.touchscreen2p.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TapBattleColors = darkColorScheme(
    primary      = Yellow,
    onPrimary    = NavyBackground,
    secondary    = OrangePlayer1,
    tertiary     = BluePlayer2,
    background   = NavyBackground,
    surface      = NavySurface,
    onBackground = White,
    onSurface    = White,
    error        = BombRed
)

/** Dark Material 3 theme for Tap Battle. */
@Composable
fun TapBattleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TapBattleColors,
        typography  = Typography,
        content     = content
    )
}