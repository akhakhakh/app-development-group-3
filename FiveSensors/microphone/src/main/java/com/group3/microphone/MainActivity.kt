package com.group3.microphone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.group3.microphone.SoundManager
import com.group3.microphone.ui.theme.FiveSensorsTheme

private enum class Screen { HOME, GAME, HOW_TO_PLAY, SETTINGS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FiveSensorsTheme {
                var screen by remember { mutableStateOf(Screen.HOME) }
                var sensitivity by remember { mutableFloatStateOf(0.5f) }
                var soundEffectsEnabled by remember { mutableStateOf(true) }
                BackHandler(enabled = screen != Screen.HOME) { screen = Screen.HOME }
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        onPlay = { screen = Screen.GAME },
                        onHowToPlay = { screen = Screen.HOW_TO_PLAY },
                        onSettings = { screen = Screen.SETTINGS }
                    )
                    Screen.GAME -> GameScreen(
                        sensitivity = sensitivity,
                        onHome = { screen = Screen.HOME }
                    )
                    Screen.HOW_TO_PLAY -> HowToPlayScreen(onBack = { screen = Screen.HOME })
                    Screen.SETTINGS -> SettingsScreen(
                        soundEffectsEnabled = soundEffectsEnabled,
                        onSoundEffectsChange = {
                            soundEffectsEnabled = it
                            SoundManager.setEnabled(it)
                        },
                        onBack = { screen = Screen.HOME }
                    )
                }
            }
        }
    }
}
