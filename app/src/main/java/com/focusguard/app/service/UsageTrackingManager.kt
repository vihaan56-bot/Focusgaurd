package com.focusguard.app.service

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import com.focusguard.app.domain.model.AppUsageInfo
import com.focusguard.app.utils.PermissionUtils
import com.focusguard.app.utils.ProtectedApps
import com.focusguard.app.utils.TimeUtils

class UsageTrackingManager(private val context: Context) {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

    /**
     * Queries actual foreground app usage statistics for today from UsageStatsManager.
     * Returns empty list if Usage Access permission is not granted.
     */
    fun getTodayAppUsage(): List<AppUsageInfo> {
        if (!PermissionUtils.hasUsageAccessPermission(context) || usageStatsManager == null) {
            return emptyList()
        }

        val startTime = TimeUtils.getStartOfTodayMs()
        val endTime = System.currentTimeMillis()

        val statsList: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: emptyList()

        if (statsList.isEmpty()) return emptyList()

        val packageManager = context.packageManager

        // Aggregate usage stats by packageName
        val aggregatedUsage = mutableMapOf<String, Long>()
        for (stats in statsList) {
            val totalTime = stats.totalTimeInForeground
            if (totalTime > 0) {
                val current = aggregatedUsage[stats.packageName] ?: 0L
                aggregatedUsage[stats.packageName] = maxOf(current, totalTime)
            }
        }

        val result = mutableListOf<AppUsageInfo>()
        for ((pkgName, foregroundMs) in aggregatedUsage) {
            if (ProtectedApps.isProtectedPackage(pkgName)) continue

            val appLabel = try {
                val appInfo = packageManager.getApplicationInfo(pkgName, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                pkgName
            }

            result.add(
                AppUsageInfo(
                    packageName = pkgName,
                    appName = appLabel,
                    totalTimeInForegroundMs = foregroundMs,
                    isDistractingApp = true
                )
            )
        }

        return result.sortedByDescending { it.totalTimeInForegroundMs }
    }

    /**
     * Fast, low-overhead lookup of a single application's daily screen time in milliseconds.
     */
    fun getTodayUsageForPackage(packageName: String): Long {
        if (!PermissionUtils.hasUsageAccessPermission(context) || usageStatsManager == null) {
            return 0L
        }

        val startTime = TimeUtils.getStartOfTodayMs()
        val endTime = System.currentTimeMillis()

        val statsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: return 0L

        var maxTime = 0L
        for (stats in statsList) {
            if (stats.packageName == packageName) {
                maxTime = maxOf(maxTime, stats.totalTimeInForeground)
            }
        }
        return maxTime
    }
}
