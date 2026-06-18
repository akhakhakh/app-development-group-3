package com.group3.touchscreen1p.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NeonButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Cyan,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(
            2.dp,
            color
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1A0A2E),
            contentColor = color
        )
    ) {

        Text(
            text = text
        )
    }
}