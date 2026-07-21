package com.focusguard.app.domain.model

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val totalTimeInForegroundMs: Long,
    val dailyLimitMinutes: Int = 0,
    val isDistractingApp: Boolean = true,
    val isLimitExceeded: Boolean = false
) {
    val formattedUsageTime: String
        get() = com.focusguard.app.utils.TimeUtils.formatDurationMs(totalTimeInForegroundMs)
}
