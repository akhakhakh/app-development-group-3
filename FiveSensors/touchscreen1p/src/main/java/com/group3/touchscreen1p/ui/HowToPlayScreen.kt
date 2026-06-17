package com.group3.touchscreen1p.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HowToPlayScreen() {

    Column(
        modifier = Modifier.padding(24.dp)
    ) {

        Text("HOW TO PLAY")

        Spacer(Modifier.height(16.dp))

        Text(
            "Press the matching color when the orb reaches the target zone."
        )

        Text(
            "Missing an orb costs one life."
        )

        Text(
            "Three misses = Game Over."
        )
    }
}