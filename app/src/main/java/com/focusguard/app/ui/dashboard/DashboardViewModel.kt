package com.focusguard.app.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.app.FocusGuardApplication
import com.focusguard.app.domain.model.AppUsageInfo
import com.focusguard.app.utils.PermissionUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val hasUsageAccess: Boolean = false,
    val hasAccessibilityAccess: Boolean = false,
    val totalScreenTimeMs: Long = 0L,
    val blockedAttemptsToday: Int = 0,
    val isFocusModeActive: Boolean = false,
    val focusModeRemainingMs: Long = 0L,
    val topApps: List<AppUsageInfo> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FocusGuardApplication
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun refreshDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val context = getApplication<Application>()
            val hasUsagePermission = PermissionUtils.hasUsageAccessPermission(context)
            val hasAccessibilityPermission = PermissionUtils.isAccessibilityServiceEnabled(context)

            if (!hasUsagePermission) {
                _uiState.value = DashboardUiState(
                    hasUsageAccess = false,
                    hasAccessibilityAccess = hasAccessibilityPermission,
                    isLoading = false
                )
                return@launch
            }

            val todayUsage = app.getTodayUsageUseCase()
            val totalMs = todayUsage.sumOf { it.totalTimeInForegroundMs }
            val topUsedApps = todayUsage.take(5)

            _uiState.value = DashboardUiState(
                hasUsageAccess = true,
                hasAccessibilityAccess = hasAccessibilityPermission,
                totalScreenTimeMs = totalMs,
                blockedAttemptsToday = 0,
                isFocusModeActive = false,
                topApps = topUsedApps,
                isLoading = false
            )
        }
    }
}
