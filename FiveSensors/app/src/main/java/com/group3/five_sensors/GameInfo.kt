package com.group3.five_sensors

import androidx.compose.ui.graphics.Color

data class GameInfo(
    val name: String,
    val description: String,
    val howToPlay: String,
    val madeBy: String,
    val color: Color,
    val activityClass: Class<*>,
    val iconRes: Int? = null,
    val videoRes: Int? = null
)

val allGames = listOf(
    GameInfo(
        name = "Neon Reactor",
        description = "A solo tap challenge",
        howToPlay = "Match falling orbs to the correct color button at the bottom. Every hit scores 10 points and combos multiply your score. You have 3 lives — missing orbs costs a life. Watch for cyan, magenta, yellow, and purple orbs.",
        madeBy = "Sara Kiani Nejad",
        color = Color(0xFFE53935),
        activityClass = com.group3.touchscreen1p.MainActivity::class.java,
        iconRes = R.drawable.touchscreen1p,
        videoRes = R.raw.preview_touchscreen1p
    ),
    GameInfo(
        name = "Tap Battle",
        description = "A two-player reaction battle",
        howToPlay = "Place the phone between two players: P1 holds the bottom half, P2 holds the top (upside down). Tap targets in your half to score. Avoid yellow trick targets (−1 pt) and red bombs (−2 pts). First to the target score wins.",
        madeBy = "Ai Nguyen",
        color = Color(0xFFFFC107),
        activityClass = com.group3.touchscreen2p.MainActivity::class.java,
        iconRes = R.drawable.touchscreen2p,
        videoRes = R.raw.preview_touchscreen2p
    ),
    GameInfo(
        name = "Voice Jump",
        description = "Use your voice to win",
        howToPlay = "Make loud noise, shout, clap, or blow, to make your ninja jump. The louder the sound, the higher the jump! Platforms scroll in from the right; time your jumps to land on them. Fall off the bottom and it's game over.",
        madeBy = "Milana Doborjginidze",
        color = Color(0xFF1E88E5),
        activityClass = com.group3.microphone.MainActivity::class.java,
        iconRes = R.drawable.microphone,
        videoRes = R.raw.preview_microphone
    ),
    GameInfo(
        name = "Gyro Maze",
        description = "Tilt and navigate",
        howToPlay = "Tilt your phone left, right, up, or down to roll the marble across the board. Guide the marble into the hole to complete each level.",
        madeBy = "Rodrigo Neves Cardoso",
        color = Color(0xFF43A047),
        activityClass = com.group3.gyromaze.MainActivity::class.java,
        iconRes = R.drawable.gyroscope,
        videoRes = R.raw.preview_gyroscope
    ),
    GameInfo(
        name = "Face Snap",
        description = "Strike the expression!",
        howToPlay = "Position your face in front of the camera and match the expression shown on screen. Hold it steady, the camera flash captures your expression. Score is calculated at the end, so just focus on matching and have fun.",
        madeBy = "Arad Kashef Haghighi",
        color = Color(0xFF8E24AA),
        activityClass = com.group3.camera.MainActivity::class.java,
        iconRes = R.drawable.camera,
        videoRes = R.raw.preview_camera
    ),
)
