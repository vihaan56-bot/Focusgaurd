package com.focusguard.app.domain.usecase

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.focusguard.app.domain.model.InstalledApp
import com.focusguard.app.utils.ProtectedApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetInstalledAppsUseCase(private val context: Context) {

    suspend operator fun invoke(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL)
        val appList = mutableListOf<InstalledApp>()

        for (resolveInfo in resolveInfos) {
            val packageName = resolveInfo.activityInfo.packageName
            
            // Exclude system UI/settings/dialer and FocusGuard itself from target limit list
            if (ProtectedApps.isProtectedPackage(packageName)) continue

            val appName = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)

            appList.add(
                InstalledApp(
                    packageName = packageName,
                    appName = appName,
                    icon = icon,
                    isDistracting = true
                )
            )
        }

        return@withContext appList.sortedBy { it.appName.lowercase() }
    }
}
