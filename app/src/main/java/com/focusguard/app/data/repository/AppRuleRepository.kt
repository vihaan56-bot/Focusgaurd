package com.focusguard.app.data.repository

import com.focusguard.app.data.database.dao.AppRuleDao
import com.focusguard.app.data.database.entity.AppRuleEntity
import com.focusguard.app.domain.model.AppRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRuleRepository(private val appRuleDao: AppRuleDao) {

    fun getAllRules(): Flow<List<AppRule>> {
        return appRuleDao.getAllRules().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getRuleForPackage(packageName: String): AppRule? {
        return appRuleDao.getRuleForPackage(packageName)?.toDomainModel()
    }

    suspend fun saveRule(rule: AppRule) {
        appRuleDao.insertOrUpdateRule(rule.toEntity())
    }

    suspend fun deleteRule(packageName: String) {
        appRuleDao.deleteRule(packageName)
    }

    private fun AppRuleEntity.toDomainModel() = AppRule(
        packageName = packageName,
        appName = appName,
        dailyLimitMinutes = dailyLimitMinutes,
        sessionLimitMinutes = sessionLimitMinutes,
        isBlocked = isBlocked,
        scheduleStartMinuteOfDay = scheduleStartMinuteOfDay,
        scheduleEndMinuteOfDay = scheduleEndMinuteOfDay,
        isWhatsAppStatusBlocked = isWhatsAppStatusBlocked,
        isInstagramReelsBlocked = isInstagramReelsBlocked,
        isYouTubeShortsBlocked = isYouTubeShortsBlocked,
        isDistractingApp = isDistractingApp
    )

    private fun AppRule.toEntity() = AppRuleEntity(
        packageName = packageName,
        appName = appName,
        dailyLimitMinutes = dailyLimitMinutes,
        sessionLimitMinutes = sessionLimitMinutes,
        isBlocked = isBlocked,
        scheduleStartMinuteOfDay = scheduleStartMinuteOfDay,
        scheduleEndMinuteOfDay = scheduleEndMinuteOfDay,
        isWhatsAppStatusBlocked = isWhatsAppStatusBlocked,
        isInstagramReelsBlocked = isInstagramReelsBlocked,
        isYouTubeShortsBlocked = isYouTubeShortsBlocked,
        isDistractingApp = isDistractingApp
    )
}
