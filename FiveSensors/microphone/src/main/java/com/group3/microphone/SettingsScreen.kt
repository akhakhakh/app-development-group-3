package com.group3.microphone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
fun SettingsScreen(
    soundEffectsEnabled: Boolean = true,
    onSoundEffectsChange: (Boolean) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBlue)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(52.dp))

        // ── BACK button (top-left) ────────────────────────────────────────────
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
            text = "SETTINGS",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(40.dp))

        // ── Sound Effects row ─────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SOUND EFFECTS",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Switch(
                checked = soundEffectsEnabled,
                onCheckedChange = onSoundEffectsChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CharDark,
                    checkedTrackColor = Yellow,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.30f),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    FiveSensorsTheme {
        SettingsScreen(soundEffectsEnabled = true)
    }
}
