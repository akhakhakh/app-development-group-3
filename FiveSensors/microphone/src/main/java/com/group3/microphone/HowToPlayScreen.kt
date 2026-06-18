package com.group3.microphone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.microphone.ui.theme.FiveSensorsTheme

private val BgBlue   = Color(0xFF4890D1)
private val Yellow   = Color(0xFFFFD426)
private val CharDark = Color(0xFF1A2035)

@Composable
fun HowToPlayScreen(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBlue)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(52.dp))

        // ── Top row: BACK pill (left) ────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Yellow,
                    contentColor = CharDark
                ),
                modifier = Modifier.height(40.dp)
            ) {
                Text("BACK", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Title ─────────────────────────────────────────────────────────────
        Text(
            text = "HOW TO PLAY",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(40.dp))

        // ── Instructions ──────────────────────────────────────────────────────
        Instruction(
            number = "1",
            title  = "MAKE NOISE TO JUMP",
            body   = "Shout, clap, or make any loud sound — your ninja will leap into the air."
        )
        Spacer(Modifier.height(28.dp))
        Instruction(
            number = "2",
            title  = "LOUDER = HIGHER JUMP",
            body   = "The louder your sound, the higher the jump. Scream for maximum height!"
        )
        Spacer(Modifier.height(28.dp))
        Instruction(
            number = "3",
            title  = "LAND ON PLATFORMS",
            body   = "Platforms scroll toward you from the right. Time your jumps to land on top of them."
        )
        Spacer(Modifier.height(28.dp))
        Instruction(
            number = "4",
            title  = "DON'T FALL",
            body   = "Miss every platform and fall off the bottom — game over!"
        )
    }
}

@Composable
private fun Instruction(number: String, title: String, body: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Circle number badge
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(36.dp)
                .background(Yellow, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = CharDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(Modifier.width(16.dp))

        Column {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(4.dp))
            Text(text = body, color = Color.White.copy(alpha = 0.80f), fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HowToPlayScreenPreview() {
    FiveSensorsTheme {
        HowToPlayScreen()
    }
}
