package com.group3.touchscreen2p.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.group3.touchscreen2p.ui.theme.BlueDivider
import com.group3.touchscreen2p.ui.theme.BluePlayer2
import com.group3.touchscreen2p.ui.theme.GreyText
import com.group3.touchscreen2p.ui.theme.NavyBackground
import com.group3.touchscreen2p.ui.theme.NavyCard
import com.group3.touchscreen2p.ui.theme.NavySurface
import com.group3.touchscreen2p.ui.theme.OrangePlayer1
import com.group3.touchscreen2p.ui.theme.RedDanger
import com.group3.touchscreen2p.ui.theme.WhitePrimary
import com.group3.touchscreen2p.ui.theme.YellowAccent

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(vertical = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // --- App icon ---
            AppIcon()

            Spacer(modifier = Modifier.height(3.dp))

            // Title
            Text(
                text = "TAP",
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                color = WhitePrimary,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "BATTLE",
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                color = YellowAccent,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // --- Subtitle ---
            Text(
                text = "2   PLAYERS",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = YellowAccent,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // --- Tagline ---
            Text(
                text = "TAP FAST. THINK FASTER.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = GreyText,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // --- Divider ---
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(1.0f),
                thickness = 2.dp,
                color = BlueDivider
            )

            Spacer(modifier = Modifier.height(20.dp))


            // --- PLAY button ---
            Button(
                onClick = onPlayClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YellowAccent,
                    contentColor = NavyBackground
                )
            ) {
                Text(
                    text = "PLAY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
            }

            // --- HOW TO PLAY button ---
            OutlinedButton(
                onClick  = { /* Sprint 2 */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = WhitePrimary)
            ) {
                Text(
                    text = "HOW TO PLAY",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold)
            }

            // --- SETTINGS button ---
            OutlinedButton(
                onClick  = { /* Sprint 2 */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = WhitePrimary)
            ) {
                Text(
                    text  = "SETTINGS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // --- Footer hint ---
            Text(
                text = "FIRST 10 POINTS TO WINS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = GreyText,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- Target Legend ---
            TargetLegend()
        }
    }
}

@Composable
private fun AppIcon() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(NavyCard)
            .border(2.dp, YellowAccent, RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy((-8).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left player silhouette
            PlayerSilhouette()

            // Red dot between players
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(RedDanger)
                    .zIndex(1f)
            )

            // Right player silhouette
            PlayerSilhouette()
        }
    }
}

@Composable
private fun PlayerSilhouette() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // Head
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(YellowAccent)
        )
        // Body
        Box(
            modifier = Modifier
                .width(22.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(YellowAccent)
        )
    }
}

@Composable
private fun TargetLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = OrangePlayer1,    label = "P1 TARGET", value = "+1")
        LegendItem(color = BluePlayer2,    label = "P2 TARGET", value = "+1")
        LegendItem(color = YellowAccent,   label = "TRICK", value = "TARGET-1")
        LegendItem(color = RedDanger,  label = "BOMB", value = "TARGET -2")
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Coloured dot
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Label
        Text(
            text = label,
            fontSize = 9.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GreyText,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(1.dp))

        // Point value
        Text(
            text = value,
            fontSize = 9.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GreyText,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center
        )
    }
}