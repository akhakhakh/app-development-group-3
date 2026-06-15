package com.group3.touchscreen2p.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.touchscreen2p.ui.theme.BlueBorder
import com.group3.touchscreen2p.ui.theme.BlueDivider
import com.group3.touchscreen2p.ui.theme.BluePlayer2
import com.group3.touchscreen2p.ui.theme.GreyText
import com.group3.touchscreen2p.ui.theme.NavyBackground
import com.group3.touchscreen2p.ui.theme.OrangePlayer1
import com.group3.touchscreen2p.ui.theme.White
import com.group3.touchscreen2p.ui.theme.Yellow

@Composable
fun GameOverScreen(
    winner: Int,
    score1: Int,
    score2: Int,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    val winnerColor = if (winner == 1) OrangePlayer1 else BluePlayer2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(horizontal = 32.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "GAME OVER",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = GreyText,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PLAYER $winner",
            fontSize = 52.sp,
            fontWeight = FontWeight.ExtraBold,
            color = winnerColor,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "WINS!",
            fontSize = 52.sp,
            fontWeight = FontWeight.ExtraBold,
            color = winnerColor,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        HorizontalDivider(color = BlueDivider, thickness = 2.dp)

        Spacer(modifier = Modifier.height(24.dp))

        // Each player score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "P1", fontSize = 14.sp, color = OrangePlayer1, fontWeight = FontWeight.Bold)
                Text(text = "$score1", fontSize = 48.sp, color = OrangePlayer1, fontWeight = FontWeight.ExtraBold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "P2", fontSize = 14.sp, color = BluePlayer2, fontWeight = FontWeight.Bold)
                Text(text = "$score2", fontSize = 48.sp, color = BluePlayer2, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Button
        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth(0.7f).height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Yellow,
                contentColor = NavyBackground
            )
        ) {
            Text(text = "PLAY AGAIN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth(0.7f).height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
            border   = BorderStroke(3.dp, BlueBorder),
        ) {
            Text(text = "HOME", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}