package com.focusguard.app.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.inputmethod.InputMethodManager

object ProtectedApps {

    private val PROTECTED_PACKAGES = setOf(
        "com.focusguard.app",                      // DistractOff itself
        "com.android.settings",                    // Android Settings
        "com.google.android.settings",             // Pixel Settings
        "com.android.systemui",                    // System UI
        "com.android.dialer",                      // Phone / Dialer
        "com.google.android.dialer",               // Google Phone
        "com.samsung.android.dialer",              // Samsung Dialer
        "com.samsung.android.incallui",            // Samsung In-call UI
        "com.android.server.telecom",              // Telecom System
        "com.android.phone",                       // Phone Service
        "com.android.emergency",                   // Emergency Info/Calling
        "com.google.android.permissioncontroller", // Permission Manager
        "com.android.permissioncontroller"          // Android Permission Manager
    )

    /**
     * Returns true if the package is critical for phone functionality or system settings and MUST NEVER be blocked.
     */
    fun isProtectedPackage(context: Context, packageName: String): Boolean {
        if (packageName.isBlank()) return true
        val lowerPkg = packageName.lowercase()

        // 1. Static whitelist packages
        if (PROTECTED_PACKAGES.contains(lowerPkg)) return true

        // 2. Active default System Launcher check
        if (isLauncherPackage(context, packageName)) return true

        // 3. Active system Keyboards / Input Methods check
        if (isInputMethodPackage(context, packageName)) return true

        // 4. Safety fallback checks
        if (lowerPkg.contains("settings") ||
            lowerPkg.contains("dialer") ||
            lowerPkg.contains("systemui") ||
            lowerPkg.contains("incallui") ||
            lowerPkg.contains("emergency") ||
            lowerPkg.contains("telecom") ||
            lowerPkg.contains("launcher") ||
            lowerPkg.contains("keyboard") ||
            lowerPkg.contains("inputmethod") ||
            lowerPkg.contains("wallpaper") ||
            lowerPkg.contains("activeise")
        ) {
            return true
        }

        return false
    }

    private fun isLauncherPackage(context: Context, packageName: String): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = context.packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveInfo?.activityInfo?.packageName == packageName
    }

    private fun isInputMethodPackage(context: Context, packageName: String): Boolean {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val imes = imm?.inputMethodList ?: return false
        return imes.any { it.packageName == packageName }
    }
}
