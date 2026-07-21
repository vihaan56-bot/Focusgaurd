package com.focusguard.app.data.repository

import com.focusguard.app.data.datastore.PreferencesManager
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val preferencesManager: PreferencesManager) {

    val isWhatsAppStatusBlocking: Flow<Boolean> = preferencesManager.isWhatsAppStatusBlockingEnabled
    val isInstagramReelsBlocking: Flow<Boolean> = preferencesManager.isInstagramReelsBlockingEnabled
    val isYouTubeShortsBlocking: Flow<Boolean> = preferencesManager.isYouTubeShortsBlockingEnabled
    val isFocusModeActive: Flow<Boolean> = preferencesManager.isFocusModeActive
    val focusModeEndTime: Flow<Long> = preferencesManager.focusModeEndTime

    suspend fun setWhatsAppStatusBlocking(enabled: Boolean) {
        preferencesManager.setWhatsAppStatusBlocking(enabled)
    }

    suspend fun setInstagramReelsBlocking(enabled: Boolean) {
        preferencesManager.setInstagramReelsBlocking(enabled)
    }

    suspend fun setYouTubeShortsBlocking(enabled: Boolean) {
        preferencesManager.setYouTubeShortsBlocking(enabled)
    }

    suspend fun setFocusModeState(active: Boolean, endTimeMs: Long = 0L) {
        preferencesManager.setFocusModeState(active, endTimeMs)
    }
}
