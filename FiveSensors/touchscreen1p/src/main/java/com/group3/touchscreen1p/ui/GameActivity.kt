package com.group3.touchscreen1p.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group3.touchscreen1p.ui.game.GameScreen
import com.group3.touchscreen1p.ui.theme.NeonReactorTheme
import com.group3.touchscreen1p.viewmodel.GameViewModel

class GameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NeonReactorTheme {

                val viewModel: GameViewModel = viewModel()

                GameScreen(
                    gameState = viewModel.gameState,
                    onColorPressed = { lane ->
                        viewModel.onColorPressed(lane)
                    },
                    onPause = {
                        viewModel.togglePause()
                    }
                )
            }
        }
    }
}