package com.group3.gyromaze

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.gyromaze.ui.theme.*

@Composable
fun GameCompleteScreen(onMainMenu: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(NavyCard)
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(listOf(NeonCyan, NeonPurple)),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 28.dp, vertical = 36.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.EmojiEvents,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(72.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "You Did It!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeonPurple,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Congratulations — you have completed\nall 10 levels of Gyro Maze.\nYou are a true master of the marble!",
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(32.dp))

            WireframeButton(label = "Back to Main Menu", onClick = onMainMenu)
        }
    }
}