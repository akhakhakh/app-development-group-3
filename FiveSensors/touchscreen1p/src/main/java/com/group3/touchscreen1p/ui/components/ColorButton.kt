package com.group3.touchscreen1p.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ColorButton(
    drawable: Int,
    onClick: () -> Unit
) {

    Image(
        painter = painterResource(drawable),
        contentDescription = null,
        modifier = Modifier
            .size(80.dp)
            .clickable {
                onClick()
            },
        contentScale = ContentScale.Fit
    )
}