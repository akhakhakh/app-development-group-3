package com.group3.touchscreen2p.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.touchscreen2p.ui.theme.NavyBackground
import com.group3.touchscreen2p.ui.theme.WhitePrimary
import com.group3.touchscreen2p.ui.theme.YellowAccent

@Composable
fun HomeScreen(onPlayClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "TAP ",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WhitePrimary
                )
                Text(
                    text = "BATTLE",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = YellowAccent
                )
            }
        }
    }

    // PLAY button
    Button(
        onClick  = onPlayClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor = YellowAccent,
            contentColor   = NavyBackground
        )
    ) {
        Text(text = "PLAY", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }

    // HOW TO PLAY button
    OutlinedButton(
        onClick  = { /* Sprint 2 */ },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors   = ButtonDefaults.outlinedButtonColors(contentColor = WhitePrimary)
    ) {
        Text(text = "HOW TO PLAY", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }

    // SETTINGS button
    TextButton(
        onClick  = { /* Sprint 2 */ },
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        Text(
            text  = "SETTINGS",
            fontSize = 14.sp,
            color = WhitePrimary.copy(alpha = 0.45f)
        )
    }
}