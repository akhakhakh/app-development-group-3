package com.group3.touchscreen1p.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun NeonReactorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = AppTypography,
        content = content
    )
}