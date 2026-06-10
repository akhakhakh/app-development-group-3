package com.group3.touchscreen1p.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.touchscreen1p.R
import com.group3.touchscreen1p.ui.theme.*

@Composable
fun HomeScreen() {

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        BackgroundSurface,
                        BackgroundDark
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            // Logo + Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(220.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "NEON REACTOR",
                    color = NeonBlue,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Color Matching Reflex Game",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }

            // Play Button
            Image(
                painter = painterResource(R.drawable.play_button),
                contentDescription = "Play",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable {
                        context.startActivity(
                            Intent(context, GameActivity::class.java)
                        )
                    }
            )

            // Bottom Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                MenuCard(
                    icon = R.drawable.info_icon,
                    title = "How To Play",
                    modifier = Modifier.weight(1f)
                )

                MenuCard(
                    icon = R.drawable.settings_icon,
                    title = "Settings",
                    modifier = Modifier.weight(1f)
                )

                MenuCard(
                    icon = R.drawable.cup_icon,
                    title = "High Score",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MenuCard(
    icon: Int,
    title: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundSurface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NeonReactorTheme {
        HomeScreen()
    }
}