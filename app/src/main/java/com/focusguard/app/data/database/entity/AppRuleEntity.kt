package com.focusguard.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_rules")
data class AppRuleEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Int = 0, // 0 = no limit
    val sessionLimitMinutes: Int = 0, // 0 = no continuous limit
    val isBlocked: Boolean = false,
    val scheduleStartMinuteOfDay: Int = -1, // -1 = no schedule
    val scheduleEndMinuteOfDay: Int = -1,
    val isWhatsAppStatusBlocked: Boolean = false,
    val isInstagramReelsBlocked: Boolean = false,
    val isYouTubeShortsBlocked: Boolean = false,
    val isDistractingApp: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
