package com.focusguard.app.domain.model

data class AppRule(
    val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Int = 0,
    val sessionLimitMinutes: Int = 0,
    val isBlocked: Boolean = false,
    val scheduleStartMinuteOfDay: Int = -1,
    val scheduleEndMinuteOfDay: Int = -1,
    val isWhatsAppStatusBlocked: Boolean = false,
    val isInstagramReelsBlocked: Boolean = false,
    val isYouTubeShortsBlocked: Boolean = false,
    val isDistractingApp: Boolean = true
)
