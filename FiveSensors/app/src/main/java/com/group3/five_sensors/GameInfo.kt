package com.group3.five_sensors

import androidx.compose.ui.graphics.Color

data class GameInfo(
    val name: String,
    val description: String,
    val howToPlay: String,
    val color: Color,
    val activityClass: Class<*>
)

val allGames = listOf(
    GameInfo(
        name = "Touchscreen 1P",
        description = "A solo touch challenge",
        howToPlay = "Tap the targets as fast as you can before the timer runs out. Precision and speed are key!",
        color = Color(0xFFE53935),
        activityClass = com.group3.touchscreen1p.MainActivity::class.java
    ),
    GameInfo(
        name = "Touchscreen 2P",
        description = "A two-player touch battle",
        howToPlay = "Two players face off on the same screen. Tap your side faster than your opponent to win!",
        color = Color(0xFFFFC107),
        activityClass = com.group3.touchscreen2p.MainActivity::class.java
    ),
    GameInfo(
        name = "Microphone",
        description = "Use your voice to win",
        howToPlay = "Speak, sing, or blow into the microphone to power up and control your character.",
        color = Color(0xFF1E88E5),
        activityClass = com.group3.microphone.MainActivity::class.java
    ),
    GameInfo(
        name = "Gyroscope",
        description = "Tilt and navigate",
        howToPlay = "Tilt your phone in any direction to steer and control the game. Balance is everything!",
        color = Color(0xFF43A047),
        activityClass = com.group3.gyromaze.MainActivity::class.java
    ),
    GameInfo(
        name = "Camera",
        description = "Strike the expression!",
        howToPlay = "Follow the beat and match the target facial expressions on screen before the snap!",
        color = Color(0xFF8E24AA),
        activityClass = com.group3.camera.MainActivity::class.java
    ),
)
