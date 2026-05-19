package com.group3.five_sensors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.group3.five_sensors.ui.theme.FiveSensorsTheme
import com.group3.microphone.GameScreen
import com.group3.microphone.HomeScreen

private enum class Screen { HOME, GAME }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FiveSensorsTheme {
                var screen by remember { mutableStateOf(Screen.HOME) }
                BackHandler(enabled = screen == Screen.GAME) { screen = Screen.HOME }
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        onPlay = { screen = Screen.GAME },
                        onHowToPlay = { },
                        onSettings = { }
                    )
                    Screen.GAME -> GameScreen()
                }
            }
        }
    }
}
