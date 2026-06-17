package com.group3.five_sensors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameListScreen(onGameSelected: (GameInfo) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F3E8))
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SELECT A GAME",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF1A1A1A),
                letterSpacing = 1.sp
            )
        }

        HorizontalDivider(color = Color(0xFFDDDDB8), thickness = 1.dp)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(allGames) { game ->
                GameRow(game = game, onClick = { onGameSelected(game) })
                HorizontalDivider(color = Color(0xFFDDDDB8), thickness = 1.dp)
            }
        }
    }
}

@Composable
private fun GameRow(game: GameInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(game.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Icon",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = game.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = game.description,
                fontSize = 13.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}
