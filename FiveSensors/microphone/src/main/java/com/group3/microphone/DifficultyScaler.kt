package com.group3.microphone

// ─────────────────────────────────────────────────────────────────────────────
// Progressive difficulty — all tunable knobs in one place.
//
// Safety analysis (all values must remain beatable at max voice):
//   Max jump velocity = JUMP_VELOCITY_BASE + JUMP_VELOCITY_SCALE = 0.28 + 0.55 = 0.83 canvas-h/s
//   Max air time      = 2 × 0.83 / GRAVITY (0.70) ≈ 2.37 s
//   Max gap clearance = 2.37 × SCROLL_SPEED_EXPERT (0.25) ≈ 0.59 canvas-widths
//   Max gap defined   = PLATFORM_MAX_GAP_EXPERT (0.32)  →  0.32 < 0.59  ✓ always beatable
// ─────────────────────────────────────────────────────────────────────────────

// ── Score thresholds that unlock each difficulty tier ─────────────────────────
// Raise to delay the ramp; lower to accelerate it.
internal const val DIFF_TIER1_SCORE = 10    // Normal
internal const val DIFF_TIER2_SCORE = 25    // Medium
internal const val DIFF_TIER3_SCORE = 50    // Hard  (moving platforms appear)
internal const val DIFF_TIER4_SCORE = 100   // Expert

// ── World scroll speed (canvas-widths / second, while airborne) ───────────────
internal const val SCROLL_SPEED_EASY   = 0.10f
internal const val SCROLL_SPEED_NORMAL = 0.12f
internal const val SCROLL_SPEED_MEDIUM = 0.16f
internal const val SCROLL_SPEED_HARD   = 0.20f
internal const val SCROLL_SPEED_EXPERT = 0.25f

// ── Platform width range (fraction of canvas width) ───────────────────────────
internal const val PLATFORM_MIN_WIDTH_EASY   = 0.30f
internal const val PLATFORM_MAX_WIDTH_EASY   = 0.45f
internal const val PLATFORM_MIN_WIDTH_NORMAL = 0.20f
internal const val PLATFORM_MAX_WIDTH_NORMAL = 0.40f
internal const val PLATFORM_MIN_WIDTH_MEDIUM = 0.15f
internal const val PLATFORM_MAX_WIDTH_MEDIUM = 0.30f
internal const val PLATFORM_MIN_WIDTH_HARD   = 0.11f
internal const val PLATFORM_MAX_WIDTH_HARD   = 0.22f
internal const val PLATFORM_MIN_WIDTH_EXPERT = 0.08f
internal const val PLATFORM_MAX_WIDTH_EXPERT = 0.16f

// ── Horizontal gap between platforms (fraction of canvas width) ───────────────
internal const val PLATFORM_MIN_GAP_EASY   = 0.06f
internal const val PLATFORM_MAX_GAP_EASY   = 0.16f
internal const val PLATFORM_MIN_GAP_NORMAL = 0.08f
internal const val PLATFORM_MAX_GAP_NORMAL = 0.22f
internal const val PLATFORM_MIN_GAP_MEDIUM = 0.12f
internal const val PLATFORM_MAX_GAP_MEDIUM = 0.26f
internal const val PLATFORM_MIN_GAP_HARD   = 0.15f
internal const val PLATFORM_MAX_GAP_HARD   = 0.30f
internal const val PLATFORM_MIN_GAP_EXPERT = 0.17f
internal const val PLATFORM_MAX_GAP_EXPERT = 0.32f

// ── Platform Y spawn range (fraction of canvas height; smaller value = higher up) ──
internal const val PLATFORM_MIN_Y_EASY   = 0.45f
internal const val PLATFORM_MAX_Y_EASY   = 0.65f
internal const val PLATFORM_MIN_Y_NORMAL = 0.35f
internal const val PLATFORM_MAX_Y_NORMAL = 0.70f
internal const val PLATFORM_MIN_Y_MEDIUM = 0.30f
internal const val PLATFORM_MAX_Y_MEDIUM = 0.73f
internal const val PLATFORM_MIN_Y_HARD   = 0.25f
internal const val PLATFORM_MAX_Y_HARD   = 0.76f
internal const val PLATFORM_MIN_Y_EXPERT = 0.22f
internal const val PLATFORM_MAX_Y_EXPERT = 0.78f

// ── Minimum Y distance between consecutive platforms ─────────────────────────
internal const val MIN_Y_SEP_EASY   = 0.08f
internal const val MIN_Y_SEP_NORMAL = 0.12f
internal const val MIN_Y_SEP_MEDIUM = 0.14f
internal const val MIN_Y_SEP_HARD   = 0.16f
internal const val MIN_Y_SEP_EXPERT = 0.18f

// ── Moving platforms (appear from Tier 3 / Hard onward) ──────────────────────
// Probability a newly spawned platform will oscillate (0 = never, 1 = always).
internal const val MOVING_CHANCE_HARD   = 0.25f
internal const val MOVING_CHANCE_EXPERT = 0.45f
// Oscillation angular speed (radians / second).
internal const val MOVING_SPEED_HARD   = 1.5f
internal const val MOVING_SPEED_EXPERT = 2.2f
// Oscillation half-amplitude (canvas-width fraction); platform swings ± this from its scroll position.
internal const val MOVING_AMP_HARD   = 0.06f
internal const val MOVING_AMP_EXPERT = 0.09f

// ─────────────────────────────────────────────────────────────────────────────

data class DifficultyConfig(
    val scrollSpeed: Float,
    val platformMinWidth: Float,
    val platformMaxWidth: Float,
    val platformMinHGap: Float,
    val platformMaxHGap: Float,
    val platformMinY: Float,
    val platformMaxY: Float,
    val minYSeparation: Float,
    val movingPlatformChance: Float,
    val movingPlatformSpeed: Float,
    val movingPlatformAmplitude: Float
)

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
private fun smoothstep(t: Float): Float {
    val c = t.coerceIn(0f, 1f)
    return c * c * (3f - 2f * c)
}

internal object DifficultyScaler {

    private val tiers: List<Pair<Int, DifficultyConfig>> = listOf(
        0 to DifficultyConfig(
            scrollSpeed             = SCROLL_SPEED_EASY,
            platformMinWidth        = PLATFORM_MIN_WIDTH_EASY,
            platformMaxWidth        = PLATFORM_MAX_WIDTH_EASY,
            platformMinHGap         = PLATFORM_MIN_GAP_EASY,
            platformMaxHGap         = PLATFORM_MAX_GAP_EASY,
            platformMinY            = PLATFORM_MIN_Y_EASY,
            platformMaxY            = PLATFORM_MAX_Y_EASY,
            minYSeparation          = MIN_Y_SEP_EASY,
            movingPlatformChance    = 0f,
            movingPlatformSpeed     = 0f,
            movingPlatformAmplitude = 0f
        ),
        DIFF_TIER1_SCORE to DifficultyConfig(
            scrollSpeed             = SCROLL_SPEED_NORMAL,
            platformMinWidth        = PLATFORM_MIN_WIDTH_NORMAL,
            platformMaxWidth        = PLATFORM_MAX_WIDTH_NORMAL,
            platformMinHGap         = PLATFORM_MIN_GAP_NORMAL,
            platformMaxHGap         = PLATFORM_MAX_GAP_NORMAL,
            platformMinY            = PLATFORM_MIN_Y_NORMAL,
            platformMaxY            = PLATFORM_MAX_Y_NORMAL,
            minYSeparation          = MIN_Y_SEP_NORMAL,
            movingPlatformChance    = 0f,
            movingPlatformSpeed     = 0f,
            movingPlatformAmplitude = 0f
        ),
        DIFF_TIER2_SCORE to DifficultyConfig(
            scrollSpeed             = SCROLL_SPEED_MEDIUM,
            platformMinWidth        = PLATFORM_MIN_WIDTH_MEDIUM,
            platformMaxWidth        = PLATFORM_MAX_WIDTH_MEDIUM,
            platformMinHGap         = PLATFORM_MIN_GAP_MEDIUM,
            platformMaxHGap         = PLATFORM_MAX_GAP_MEDIUM,
            platformMinY            = PLATFORM_MIN_Y_MEDIUM,
            platformMaxY            = PLATFORM_MAX_Y_MEDIUM,
            minYSeparation          = MIN_Y_SEP_MEDIUM,
            movingPlatformChance    = 0f,
            movingPlatformSpeed     = 0f,
            movingPlatformAmplitude = 0f
        ),
        DIFF_TIER3_SCORE to DifficultyConfig(
            scrollSpeed             = SCROLL_SPEED_HARD,
            platformMinWidth        = PLATFORM_MIN_WIDTH_HARD,
            platformMaxWidth        = PLATFORM_MAX_WIDTH_HARD,
            platformMinHGap         = PLATFORM_MIN_GAP_HARD,
            platformMaxHGap         = PLATFORM_MAX_GAP_HARD,
            platformMinY            = PLATFORM_MIN_Y_HARD,
            platformMaxY            = PLATFORM_MAX_Y_HARD,
            minYSeparation          = MIN_Y_SEP_HARD,
            movingPlatformChance    = MOVING_CHANCE_HARD,
            movingPlatformSpeed     = MOVING_SPEED_HARD,
            movingPlatformAmplitude = MOVING_AMP_HARD
        ),
        DIFF_TIER4_SCORE to DifficultyConfig(
            scrollSpeed             = SCROLL_SPEED_EXPERT,
            platformMinWidth        = PLATFORM_MIN_WIDTH_EXPERT,
            platformMaxWidth        = PLATFORM_MAX_WIDTH_EXPERT,
            platformMinHGap         = PLATFORM_MIN_GAP_EXPERT,
            platformMaxHGap         = PLATFORM_MAX_GAP_EXPERT,
            platformMinY            = PLATFORM_MIN_Y_EXPERT,
            platformMaxY            = PLATFORM_MAX_Y_EXPERT,
            minYSeparation          = MIN_Y_SEP_EXPERT,
            movingPlatformChance    = MOVING_CHANCE_EXPERT,
            movingPlatformSpeed     = MOVING_SPEED_EXPERT,
            movingPlatformAmplitude = MOVING_AMP_EXPERT
        )
    )

    fun forScore(score: Int): DifficultyConfig {
        var lo = 0
        for (i in tiers.indices) {
            if (tiers[i].first <= score) lo = i else break
        }
        val hi = (lo + 1).coerceAtMost(tiers.lastIndex)
        if (lo == hi) return tiers[lo].second
        val (loScore, loConfig) = tiers[lo]
        val (hiScore, hiConfig) = tiers[hi]
        val t = smoothstep((score - loScore).toFloat() / (hiScore - loScore).toFloat())
        return blend(loConfig, hiConfig, t)
    }

    private fun blend(a: DifficultyConfig, b: DifficultyConfig, t: Float) = DifficultyConfig(
        scrollSpeed             = lerp(a.scrollSpeed,             b.scrollSpeed,             t),
        platformMinWidth        = lerp(a.platformMinWidth,        b.platformMinWidth,        t),
        platformMaxWidth        = lerp(a.platformMaxWidth,        b.platformMaxWidth,        t),
        platformMinHGap         = lerp(a.platformMinHGap,         b.platformMinHGap,         t),
        platformMaxHGap         = lerp(a.platformMaxHGap,         b.platformMaxHGap,         t),
        platformMinY            = lerp(a.platformMinY,            b.platformMinY,            t),
        platformMaxY            = lerp(a.platformMaxY,            b.platformMaxY,            t),
        minYSeparation          = lerp(a.minYSeparation,          b.minYSeparation,          t),
        movingPlatformChance    = lerp(a.movingPlatformChance,    b.movingPlatformChance,    t),
        movingPlatformSpeed     = lerp(a.movingPlatformSpeed,     b.movingPlatformSpeed,     t),
        movingPlatformAmplitude = lerp(a.movingPlatformAmplitude, b.movingPlatformAmplitude, t)
    )
}
