package com.focusguard.app.domain.model

import android.graphics.drawable.Drawable

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val dailyLimitMinutes: Int = 0,
    val isDistracting: Boolean = true,
    val isSystemApp: Boolean = false
)
