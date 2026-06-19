package com.group3.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HtpBlue   = Color(0xFF3885F0)
private val HtpYellow = Color(0xFFFFD426)
private val HtpDark   = Color(0xFF1A2035)
private val HtpBg     = Color(0xFFFAFAFA)

@Composable
fun HowToPlayScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HtpBg)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(HtpBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "HOW TO PLAY",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 28.dp)
        ) {
            Instruction(
                number = "1",
                title  = "FACE THE CAMERA",
                body   = "Hold your phone at eye level so your face is clearly visible in the camera frame."
            )
            Spacer(Modifier.height(28.dp))
            Instruction(
                number = "2",
                title  = "CHECK YOUR FACE",
                body   = "The app will detect your face before starting. Make sure you're in good lighting."
            )
            Spacer(Modifier.height(28.dp))
            Instruction(
                number = "3",
                title  = "MATCH THE EXPRESSION",
                body   = "An expression will appear on screen. Look out for the countdown or the vibrations!"
            )
            Spacer(Modifier.height(28.dp))
            Instruction(
                number = "4",
                title  = "HOLD IT FOR THE SNAP",
                body   = "Hold the expression steady while it counts down. Remember, the flash of the camera will be the one capturing your facial expression!"
            )
            Spacer(Modifier.height(28.dp))
            Instruction(
                number = "5",
                title  = "COMPLETE ALL ROUNDS",
                body   = "Survive all expression rounds to finish the game and see your final score. The score on each face is only calculated at the end, so just focus on matching the expressions and have fun!"
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HtpBg)
                .padding(horizontal = 28.dp, vertical = 20.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HtpBlue,
                    contentColor = Color.White
                )
            ) {
                Text("BACK", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun Instruction(number: String, title: String, body: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(36.dp)
                .background(HtpYellow, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = HtpDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(Modifier.width(16.dp))

        Column {
            Text(text = title, color = HtpDark, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(4.dp))
            Text(text = body, color = Color(0xFF555555), fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}
