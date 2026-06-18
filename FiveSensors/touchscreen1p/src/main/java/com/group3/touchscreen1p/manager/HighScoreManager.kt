package com.group3.touchscreen1p.manager

import android.content.Context

class HighScoreManager(
    context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "neon_reactor",
            Context.MODE_PRIVATE
        )

    fun getHighScore(): Int {
        return prefs.getInt("high_score", 0)
    }

    fun saveScore(score: Int) {

        val current = getHighScore()

        if (score > current) {

            prefs.edit()
                .putInt("high_score", score)
                .apply()
        }
    }
}