package com.group3.touchscreen1p.manager

import android.content.Context

object HighScoreManager {

    private const val PREFS = "neon_reactor"
    private const val HIGH_SCORE = "high_score"

    fun saveScore(
        context: Context,
        score: Int
    ) {

        val prefs =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        if (score > getHighScore(context)) {

            prefs.edit()
                .putInt(HIGH_SCORE, score)
                .apply()
        }
    }

    fun getHighScore(
        context: Context
    ): Int {

        return context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(HIGH_SCORE, 0)
    }
}