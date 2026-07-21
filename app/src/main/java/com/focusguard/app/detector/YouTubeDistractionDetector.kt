package com.focusguard.app.detector

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class YouTubeDistractionDetector : DistractionDetector {

    override val targetPackageName: String = "com.google.android.youtube"

    override fun isDistractionDetected(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo?): Boolean {
        if (event.packageName != targetPackageName || rootNode == null) return false

        try {
            val shortsNodes = rootNode.findAccessibilityNodeInfosByText("Shorts")
            for (node in shortsNodes) {
                if (node.isSelected || node.isFocused) {
                    return true
                }
                if (node.viewIdResourceName?.contains("shorts_player", ignoreCase = true) == true) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }
}
