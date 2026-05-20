package com.group3.touchscreen2p.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val TapBattleColors = darkColorScheme(
    primary      = YellowAccent,
    onPrimary    = NavyBackground,
    secondary    = OrangePlayer1,
    tertiary     = BluePlayer2,
    background   = NavyBackground,
    surface      = NavySurface,
    onBackground = WhitePrimary,
    onSurface    = WhitePrimary,
    error        = RedDanger
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