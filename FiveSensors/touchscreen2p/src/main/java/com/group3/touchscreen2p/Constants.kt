package com.group3.touchscreen2p

object Constants {
    /* --- Score --- */
    const val WIN_SCORE =20
    const val POINTS_BULLSEYE = 1
    const val POINTS_TRICK = -1
    const val POINTS_BOMB = -2
    const val MIN_SCORE = 0

    /* --- Combo --- */
    const val COMBO_THRESHOLD = 3 // amount of taps needed to activate combo
    const val COMBO_MULTIPLIER = 2 // score x2 when combo is active
    const val COMBO_WINDOW_MS = 2000L // time allowed between correct taps before combo resets

    /* --- Timing (milliseconds) --- */
    const val COUNTDOWN_SECONDS = 3
    const val GAME_TICK_MS = 16L // ~60 updates per second
    const val TARGET_LIFETIME_MS = 3000L // target disappears after 3s
    const val TARGET_LIFETIME_MIN_MS = 100L // fastest lifetime at max score
    const val SPAWN_INTERVAL_MS = 900L  // new target every 1.5s
    const val FLOATING_EFFECT_DURATION_MS = 800L // score label fades in 0.8s

    /* --- Targets --- */
    const val MAX_TARGETS_PER_PLAYER = 3
    const val MAX_TARGETS_PER_PLAYER_LATE = 8 // cap once leadingScore reaches WIN_SCORE
    const val TARGET_MIN_SPACING = 0.22f // minimum distance between targets
    const val LIFETIME_REDUCTION_START_SCORE = 4 // score at which targets start shrinking faster
    const val LIFETIME_SHRINK_EXPONENT = 2f // >1 = shrink ramps up faster as score nears WIN_SCORE
    const val SPECIALS_UNLOCK_SCORE = 2

    /* --- Spawn positions (0.0 to 1.0 of the full screen) --- */
    const val SPAWN_X_MIN = 0.12f // padding to avoid edge
    const val SPAWN_X_MAX = 0.88f // padding to avoid edge
    const val SPAWN_Y_P1_MIN = 0.55f // gap for separator
    const val SPAWN_Y_P1_MAX = 0.90f // avoid edge
    const val SPAWN_Y_P2_MIN = 0.10f // avoid edge
    const val SPAWN_Y_P2_MAX = 0.45f // gap for separator

    /* --- Visual --- */
    const val TARGET_RADIUS_DP = 36f
    const val HIT_RADIUS_DP = 46f // generous hit box
    const val TARGET_SHRINK_FACTOR = 0.30f // how much target shrink before disappearing
}