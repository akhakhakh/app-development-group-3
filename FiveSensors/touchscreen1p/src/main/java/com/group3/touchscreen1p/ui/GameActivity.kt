package com.group3.touchscreen1p.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.group3.touchscreen1p.ui.theme.NeonReactorTheme

class GameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NeonReactorTheme {
                GameScreen()
            }
        }
    }
}