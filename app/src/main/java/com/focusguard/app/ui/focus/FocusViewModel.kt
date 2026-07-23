package com.focusguard.app.ui.focus

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.app.FocusGuardApplication
import com.focusguard.app.service.FocusModeForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FocusUiState(
    val isFocusActive: Boolean = false,
    val selectedDurationMinutes: Int = 30,
    val remainingTimeMs: Long = 0L
)

class FocusViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FocusGuardApplication
    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            app.settingsRepository.isFocusModeActive.collect { isActive ->
                _uiState.value = _uiState.value.copy(isFocusActive = isActive)
            }
        }
    }

    fun selectDuration(minutes: Int) {
        _uiState.value = _uiState.value.copy(selectedDurationMinutes = minutes)
    }

    fun startFocusMode() {
        viewModelScope.launch {
            val durationMs = _uiState.value.selectedDurationMinutes * 60 * 1000L
            val endTime = System.currentTimeMillis() + durationMs
            app.settingsRepository.setFocusModeState(active = true, endTimeMs = endTime)

            val intent = Intent(app, FocusModeForegroundService::class.java).apply {
                action = FocusModeForegroundService.ACTION_START
                putExtra(FocusModeForegroundService.EXTRA_END_TIME_MS, endTime)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                app.startForegroundService(intent)
            } else {
                app.startService(intent)
            }
        }
    }

    fun stopFocusMode() {
        viewModelScope.launch {
            app.settingsRepository.setFocusModeState(active = false, endTimeMs = 0L)

            val intent = Intent(app, FocusModeForegroundService::class.java).apply {
                action = FocusModeForegroundService.ACTION_STOP
            }
            app.stopService(intent)
        }
    }
}
