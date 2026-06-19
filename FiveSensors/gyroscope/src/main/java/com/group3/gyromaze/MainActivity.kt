package com.group3.gyromaze

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.group3.gyromaze.ui.theme.FiveSensorsTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()
    private lateinit var sensorHandler: SensorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorHandler = SensorHandler(this)
        hideSystemBars()
        setContent {
            FiveSensorsTheme {
                var currentScreen by remember { mutableStateOf(Screen.MAIN_MENU) }
                var resultScore by remember { mutableIntStateOf(0) }
                var isWin by remember { mutableStateOf(false) }

                when (currentScreen) {
                    Screen.MAIN_MENU -> MainMenuScreen(
                        onPlay = {
                            sensorHandler.recalibrate()
                            viewModel.restartLvl()
                            currentScreen = Screen.GAME
                        },
                        onHowToPlay = {
                            currentScreen = Screen.INSTRUCTIONS
                        },
                        onReturn = {
                            finish()
                        }
                    )

                    Screen.INSTRUCTIONS -> HowToPlayScreen(
                        onPlay = {
                            sensorHandler.recalibrate()
                            viewModel.restartLvl()
                            currentScreen = Screen.GAME
                        },
                        onReturn = {
                            currentScreen = Screen.MAIN_MENU
                        }
                    )

                    Screen.GAME -> GameScreen(
                        viewModel = viewModel,
                        sensorHandler = sensorHandler,
                        onLvlComplete = { score ->
                            resultScore = score
                            isWin = true
                            currentScreen = Screen.LVL_COMPLETE
                        },
                        onLvlFailed = { score ->
                            resultScore = score
                            isWin = false
                            currentScreen = Screen.LVL_FAILED
                        },
                        onReturn = {
                            currentScreen = Screen.MAIN_MENU
                        }
                    )

                    Screen.LVL_COMPLETE -> ResultScreen(
                        isWin = true,
                        score = resultScore,
                        onPrimary = {
                            // If this was the last level, go to game complete screen instead
                            if (viewModel.isLastLevel) {
                                currentScreen = Screen.GAME_COMPLETE
                            } else {
                                sensorHandler.recalibrate()
                                viewModel.nextLvl()
                                currentScreen = Screen.GAME
                            }
                        },
                        onSecondary = {
                            sensorHandler.recalibrate()
                            viewModel.restartLvl()
                            currentScreen = Screen.GAME
                        }
                    )

                    Screen.GAME_COMPLETE -> GameCompleteScreen(
                        onMainMenu = {
                            viewModel.resetGame()
                            currentScreen = Screen.MAIN_MENU
                        }
                    )

                    Screen.LVL_FAILED -> ResultScreen(
                        isWin = false,
                        score = resultScore,
                        onPrimary = {
                            sensorHandler.recalibrate()
                            viewModel.restartLvl()
                            currentScreen = Screen.GAME
                        },
                        onSecondary = {
                            currentScreen = Screen.MAIN_MENU
                        }
                    )
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    override fun onResume() {
        super.onResume()
        sensorHandler.start()
    }

    override fun onPause() {
        super.onPause()
        sensorHandler.stop()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}