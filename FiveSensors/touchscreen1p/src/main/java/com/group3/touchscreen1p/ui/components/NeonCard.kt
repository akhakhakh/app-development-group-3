package com.group3.touchscreen1p.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Cyan,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = modifier,
        border = BorderStroke(
            width = 2.dp,
            color = borderColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A0A2E)
        ),
        shape = MaterialTheme.shapes.large
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}