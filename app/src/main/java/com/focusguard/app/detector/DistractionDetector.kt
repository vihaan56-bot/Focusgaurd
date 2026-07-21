package com.focusguard.app.detector

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

interface DistractionDetector {
    /**
     * Target application package name handled by this detector (e.g. "com.whatsapp").
     */
    val targetPackageName: String

    /**
     * Evaluates accessibility event and node hierarchy to determine whether a distracting section is active.
     * Returns true only if confidence is high. Fails safely by returning false if signals are insufficient.
     */
    fun isDistractionDetected(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo?): Boolean
}
