package com.focusguard.app.utils

object ProtectedApps {

    private val PROTECTED_PACKAGES = setOf(
        "com.focusguard.app",                      // FocusGuard itself
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
    fun isProtectedPackage(packageName: String): Boolean {
        if (packageName.isBlank()) return true
        if (PROTECTED_PACKAGES.contains(packageName.lowercase())) return true
        
        // Safety fallback checks
        if (packageName.contains("settings") ||
            packageName.contains("dialer") ||
            packageName.contains("systemui") ||
            packageName.contains("incallui") ||
            packageName.contains("emergency") ||
            packageName.contains("telecom")
        ) {
            return true
        }

        return false
    }
}
