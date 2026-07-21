package com.focusguard.app.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.focusguard.app.FocusGuardApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HistoryUiState(
    val selectedFilter: String = "Today", // "Today", "Yesterday", "7 Days"
    val totalDistractingMs: Long = 0L,
    val totalBlockedAttempts: Int = 0
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as FocusGuardApplication
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun selectFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }
}
