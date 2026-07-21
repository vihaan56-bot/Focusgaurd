package com.focusguard.app.domain.usecase

import com.focusguard.app.data.repository.AppRuleRepository
import com.focusguard.app.domain.model.AppUsageInfo
import com.focusguard.app.service.UsageTrackingManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class GetTodayUsageUseCase(
    private val usageTrackingManager: UsageTrackingManager,
    private val appRuleRepository: AppRuleRepository
) {

    suspend operator fun invoke(): List<AppUsageInfo> = withContext(Dispatchers.IO) {
        val usageList = usageTrackingManager.getTodayAppUsage()
        val activeRules = appRuleRepository.getAllRules().first()

        val ruleMap = activeRules.associateBy { it.packageName }

        return@withContext usageList.map { usage ->
            val rule = ruleMap[usage.packageName]
            val limitMins = rule?.dailyLimitMinutes ?: 0
            val limitMs = limitMins * 60 * 1000L
            val isExceeded = limitMs in 1..usage.totalTimeInForegroundMs

            usage.copy(
                dailyLimitMinutes = limitMins,
                isLimitExceeded = isExceeded
            )
        }
    }
}
