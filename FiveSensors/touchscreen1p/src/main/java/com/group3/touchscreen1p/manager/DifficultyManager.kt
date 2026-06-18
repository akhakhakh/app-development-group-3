package com.group3.touchscreen1p.manager

import android.content.Context
import com.group3.touchscreen1p.model.DifficultyLevel

class DifficultyManager(context: Context) {

    private val prefs = context.getSharedPreferences("neon_reactor", Context.MODE_PRIVATE)

    fun getDifficulty(): DifficultyLevel {
        return when (prefs.getString("difficulty", "MEDIUM")) {
            "EASY" -> DifficultyLevel.EASY
            "HARD" -> DifficultyLevel.HARD
            else -> DifficultyLevel.MEDIUM
        }
    }

    fun saveDifficulty(difficulty: DifficultyLevel) {
        prefs.edit().putString("difficulty", difficulty.name).apply()
    }
}
