package com.group3.touchscreen1p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.group3.touchscreen1p.navigation.NavGraph
import com.group3.touchscreen1p.ui.theme.NeonReactorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeonReactorTheme {
                NavGraph()
            }
        }
    }
}
