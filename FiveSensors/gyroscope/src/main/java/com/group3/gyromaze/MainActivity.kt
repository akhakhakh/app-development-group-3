package com.group3.gyromaze

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {

    // viewModels() creates the ViewModel and keeps it alive across config changes
    private val viewModel: GameViewModel by viewModels()

    // SensorHandler is created once and lives for the Activity's lifetime
    private lateinit var sensorHandler: SensorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorHandler = SensorHandler(this)

        setContent {
            MaterialTheme {
                GameScreen(viewModel = viewModel, sensorHandler = sensorHandler)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Start reading sensor when the game is visible
        sensorHandler.start()
    }

    override fun onPause() {
        super.onPause()
        // Stop reading sensor when the game goes to background — saves battery
        sensorHandler.stop()
    }
}