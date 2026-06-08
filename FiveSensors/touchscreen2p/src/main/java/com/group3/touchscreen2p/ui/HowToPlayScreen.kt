package com.group3.touchscreen2p.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import com.group3.touchscreen2p.ui.theme.BluePlayer2
import com.group3.touchscreen2p.ui.theme.GreyText
import com.group3.touchscreen2p.ui.theme.NavyBackground
import com.group3.touchscreen2p.ui.theme.NavyCard
import com.group3.touchscreen2p.ui.theme.OrangePlayer1
import com.group3.touchscreen2p.ui.theme.BombRed
import com.group3.touchscreen2p.ui.theme.White
import com.group3.touchscreen2p.ui.theme.Yellow

@Composable
fun HowToPlayScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "HOW TO PLAY",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = White,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        StepCard(
            number = "1",
            title = "SETUP",
            description = "Place the phone between two players. P1 holds the bottom half, P2 holds the top half (upside down)."
        )

        Spacer(modifier = Modifier.height(16.dp))

        StepCard(
            number = "2",
            title = "TAP YOUR TARGETS",
            description = "Targets appear in your half of the screen. Tap them as fast as you can to score points."
        )

        Spacer(modifier = Modifier.height(16.dp))

        StepCard(
            number = "3",
            title = "DON'T LET THEM EXPIRE",
            description = "Each target has a timer arc. If it runs out before you tap it, the target disappears — no point."
        )

        Spacer(modifier = Modifier.height(16.dp))

        StepCard(
            number = "4",
            title = "TRICK TARGET",
            description = "A trick target will spawns randomly on each side. You will lose 1 point if you tap them."
        )

        Spacer(modifier = Modifier.height(16.dp))

        StepCard(
            number = "5",
            title = "BOMB TARGET",
            description = "A bomb target will spawns randomly on each side. You will lose 2 point if you tap them. "
        )


        Spacer(modifier = Modifier.height(16.dp))

        StepCard(
            number = "6",
            title = "FIRST TO 10 WINS",
            description = "Race to 10 points before your opponent."
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "TARGET COLORS",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = GreyText,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        ColorRow(color = OrangePlayer1, label = "Player 1 target -> +1 points")
        Spacer(modifier = Modifier.height(8.dp))
        ColorRow(color = BluePlayer2,   label = "Player 2 target -> +1 points")
        Spacer(modifier = Modifier.height(8.dp))
        ColorRow(color = Yellow,  label = "Trick target  →  -1 point if tapped")
        Spacer(modifier = Modifier.height(8.dp))
        ColorRow(color = BombRed,     label = "Bomb  →  -2 points if tapped")
        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Yellow,
                contentColor = NavyBackground
            )
        ) {
            Text(text = "GOT IT!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StepCard(number: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavyCard)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Yellow),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = NavyBackground)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Yellow, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, fontSize = 13.sp, color = White, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun ColorRow(color: Color, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 13.sp, color = White)
    }
}