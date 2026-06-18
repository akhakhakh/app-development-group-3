package com.group3.touchscreen1p.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.group3.touchscreen1p.model.OrbColor
import androidx.compose.ui.unit.dp

@Composable
fun OrbView(
    color: OrbColor,
    modifier: Modifier = Modifier
) {

    val orbColor = when (color) {
        OrbColor.CYAN -> Color(0xFF00FFFF)
        OrbColor.PINK -> Color(0xFFFF0099)
        OrbColor.YELLOW -> Color(0xFFFFFF00)
        OrbColor.PURPLE -> Color(0xFF9D00FF)
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                shape = CircleShape
            )
            .background(
                color = orbColor,
                shape = CircleShape
            )
    )
}