package com.focusguard.app.detector

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class InstagramDistractionDetector : DistractionDetector {

    override val targetPackageName: String = "com.instagram.android"

    override fun isDistractionDetected(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo?): Boolean {
        if (event.packageName != targetPackageName || rootNode == null) return false

        try {
            val reelsNodes = rootNode.findAccessibilityNodeInfosByText("Reels")
            for (node in reelsNodes) {
                if (node.isSelected || node.isFocused) {
                    return true
                }
                // Check if view ID contains reels tab indicator
                if (node.viewIdResourceName?.contains("reels_tab", ignoreCase = true) == true) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }
}
