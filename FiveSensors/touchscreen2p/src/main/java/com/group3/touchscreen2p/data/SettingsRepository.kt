package com.group3.touchscreen2p.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    companion object {
        private val SFX_ENABLED = booleanPreferencesKey("sfx_enabled")
        private val SFX_VOLUME = floatPreferencesKey("sfx_volume")
        private val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    val sfxEnabled: Flow<Boolean> = context.dataStore.data.map { it[SFX_ENABLED] ?: true }
    val sfxVolume: Flow<Float> = context.dataStore.data.map { it[SFX_VOLUME] ?: 0.8f }
    val vibrationEnabled: Flow<Boolean> = context.dataStore.data.map { it[VIBRATION_ENABLED] ?: true }

    suspend fun setSfxEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SFX_ENABLED] = enabled }
    }

    suspend fun setSfxVolume(volume: Float) {
        context.dataStore.edit { it[SFX_VOLUME] = volume}
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[VIBRATION_ENABLED] = enabled }
    }
}