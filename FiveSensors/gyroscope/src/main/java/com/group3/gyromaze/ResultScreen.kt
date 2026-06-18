package com.group3.gyromaze

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.material3.Icon

@Composable
fun ResultScreen(
    isWin: Boolean,
    score: Int,
    onPrimary: () -> Unit,   // "Next Level" (win) / "Retry Level" (fail)
    onSecondary: () -> Unit  // "Try Again" (win) / "Return" (fail)
) {
    val titleText    = if (isWin) "Congratulations!" else "Level Failed"
    val primaryLabel = if (isWin) "Next Level"       else "Retry Level"
    val secondaryLabel = if (isWin) "Try Again"      else "Return"

    val borderBrush = if (isWin)
        Brush.verticalGradient(listOf(NeonCyan, NeonPurple))
    else
        Brush.verticalGradient(listOf(NeonPink, NeonPurple))

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
                .border(width = 1.5.dp, brush = borderBrush, shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 28.dp, vertical = 32.dp)
        ) {
            // Title banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(borderBrush)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = titleText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Spacer(Modifier.height(20.dp))

            // Stars row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val filled = isWin && index < starCount(score)
                    Text(
                        text = if (filled) "★" else "☆",
                        fontSize = 36.sp,
                        color = if (filled) StarActive else StarEmpty
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Icon(
                imageVector = if (isWin) Icons.Rounded.EmojiEvents else Icons.Rounded.SentimentDissatisfied,
                contentDescription = null,
                tint = if (isWin) NeonCyan else NeonPink,
                modifier = Modifier.size(if (isWin) 56.dp else 48.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Score: $score",
                fontSize = 18.sp,
                color = TextWhite
            )

            Spacer(Modifier.height(24.dp))

            WireframeButton(label = primaryLabel, onClick = onPrimary)
            Spacer(Modifier.height(12.dp))
            WireframeButton(label = secondaryLabel, onClick = onSecondary)
        }
    }
}

// Returns how many stars to award based on score
private fun starCount(score: Int): Int = when {
    score >= 1000 -> 3
    score >= 500 -> 2
    score > 0 -> 1
    else -> 0
}