package com.focusguard.app.ui.apps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focusguard.app.FocusGuardApplication
import com.focusguard.app.domain.model.AppRule
import com.focusguard.app.domain.model.InstalledApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AppsUiState(
    val apps: List<InstalledApp> = emptyList(),
    val filteredApps: List<InstalledApp> = emptyList(),
    val rulesMap: Map<String, AppRule> = emptyMap(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class AppsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FocusGuardApplication
    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    init {
        loadInstalledAppsAndRules()
    }

    fun loadInstalledAppsAndRules() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val installedApps = app.getInstalledAppsUseCase()

            app.appRuleRepository.getAllRules().collect { rulesList ->
                val rulesMap = rulesList.associateBy { it.packageName }
                val currentQuery = _uiState.value.searchQuery
                val filtered = if (currentQuery.isBlank()) {
                    installedApps
                } else {
                    installedApps.filter { it.appName.contains(currentQuery, ignoreCase = true) || it.packageName.contains(currentQuery, ignoreCase = true) }
                }

                _uiState.value = AppsUiState(
                    apps = installedApps,
                    filteredApps = filtered,
                    rulesMap = rulesMap,
                    searchQuery = currentQuery,
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val currentApps = _uiState.value.apps
        val filtered = if (query.isBlank()) {
            currentApps
        } else {
            currentApps.filter { it.appName.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true) }
        }
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredApps = filtered
        )
    }

    fun saveAppRule(packageName: String, appName: String, dailyLimitMinutes: Int) {
        viewModelScope.launch {
            val existingRule = app.appRuleRepository.getRuleForPackage(packageName)
            val newRule = (existingRule ?: AppRule(packageName = packageName, appName = appName)).copy(
                dailyLimitMinutes = dailyLimitMinutes,
                appName = appName
            )
            app.appRuleRepository.saveRule(newRule)
        }
    }
}
