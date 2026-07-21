package com.focusguard.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.app.FocusGuardApplication
import com.focusguard.app.utils.PermissionUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val hasUsageAccess: Boolean = false,
    val hasAccessibilityAccess: Boolean = false,
    val isWhatsAppStatusBlocking: Boolean = false,
    val isInstagramReelsBlocking: Boolean = false,
    val isYouTubeShortsBlocking: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FocusGuardApplication
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        refreshPermissionStates()
        observeSettings()
    }

    fun refreshPermissionStates() {
        val context = getApplication<Application>()
        val usageGranted = PermissionUtils.hasUsageAccessPermission(context)
        val accessibilityGranted = PermissionUtils.isAccessibilityServiceEnabled(context)

        _uiState.value = _uiState.value.copy(
            hasUsageAccess = usageGranted,
            hasAccessibilityAccess = accessibilityGranted
        )
    }

    private fun observeSettings() {
        viewModelScope.launch {
            app.settingsRepository.isWhatsAppStatusBlocking.collect { enabled ->
                _uiState.value = _uiState.value.copy(isWhatsAppStatusBlocking = enabled)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.isInstagramReelsBlocking.collect { enabled ->
                _uiState.value = _uiState.value.copy(isInstagramReelsBlocking = enabled)
            }
        }
        viewModelScope.launch {
            app.settingsRepository.isYouTubeShortsBlocking.collect { enabled ->
                _uiState.value = _uiState.value.copy(isYouTubeShortsBlocking = enabled)
            }
        }
    }

    fun toggleWhatsAppStatusBlocking(enabled: Boolean) {
        viewModelScope.launch {
            app.settingsRepository.setWhatsAppStatusBlocking(enabled)
        }
    }

    fun toggleInstagramReelsBlocking(enabled: Boolean) {
        viewModelScope.launch {
            app.settingsRepository.setInstagramReelsBlocking(enabled)
        }
    }

    fun toggleYouTubeShortsBlocking(enabled: Boolean) {
        viewModelScope.launch {
            app.settingsRepository.setYouTubeShortsBlocking(enabled)
        }
    }
}
