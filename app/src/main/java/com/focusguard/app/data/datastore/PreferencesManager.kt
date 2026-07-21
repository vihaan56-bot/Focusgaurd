package com.focusguard.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "focusguard_settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val WHATSAPP_STATUS_BLOCKING = booleanPreferencesKey("whatsapp_status_blocking")
        val INSTAGRAM_REELS_BLOCKING = booleanPreferencesKey("instagram_reels_blocking")
        val YOUTUBE_SHORTS_BLOCKING = booleanPreferencesKey("youtube_shorts_blocking")
        val FOCUS_MODE_ACTIVE = booleanPreferencesKey("focus_mode_active")
        val FOCUS_MODE_END_TIME = longPreferencesKey("focus_mode_end_time")
        val DAILY_RESET_HOUR = intPreferencesKey("daily_reset_hour")
    }

    val isWhatsAppStatusBlockingEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[WHATSAPP_STATUS_BLOCKING] ?: false }

    val isInstagramReelsBlockingEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[INSTAGRAM_REELS_BLOCKING] ?: false }

    val isYouTubeShortsBlockingEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[YOUTUBE_SHORTS_BLOCKING] ?: false }

    val isFocusModeActive: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[FOCUS_MODE_ACTIVE] ?: false }

    val focusModeEndTime: Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[FOCUS_MODE_END_TIME] ?: 0L }

    val dailyResetHour: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[DAILY_RESET_HOUR] ?: 0 } // 0 = Midnight

    suspend fun setWhatsAppStatusBlocking(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[WHATSAPP_STATUS_BLOCKING] = enabled
        }
    }

    suspend fun setInstagramReelsBlocking(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[INSTAGRAM_REELS_BLOCKING] = enabled
        }
    }

    suspend fun setYouTubeShortsBlocking(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[YOUTUBE_SHORTS_BLOCKING] = enabled
        }
    }

    suspend fun setFocusModeState(active: Boolean, endTimeMs: Long) {
        context.dataStore.edit { preferences ->
            preferences[FOCUS_MODE_ACTIVE] = active
            preferences[FOCUS_MODE_END_TIME] = endTimeMs
        }
    }

    suspend fun setDailyResetHour(hour: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_RESET_HOUR] = hour
        }
    }
}
