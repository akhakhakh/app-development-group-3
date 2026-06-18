package com.group3.touchscreen2p.model

import com.group3.touchscreen2p.Constants

data class GameState(
    val phase: Phase = Phase.COUNTDOWN,
    val score1: Int = 0,
    val score2: Int = 0,
    val combo1: Int = 0,
    val combo2: Int = 0,
    val comboWindowEndMs1: Long = 0L,
    val comboWindowEndMs2: Long = 0L,
    val targets: List<Target> = emptyList(),
    val countdownValue: Int = Constants.COUNTDOWN_SECONDS,
    val winner: Int = 0,
    val bestCombo: Int = 0,
    val floatingEffects: List<FloatingEffect> = emptyList()
)
