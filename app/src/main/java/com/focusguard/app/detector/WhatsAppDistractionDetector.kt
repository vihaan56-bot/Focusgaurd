package com.focusguard.app.detector

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class WhatsAppDistractionDetector : DistractionDetector {

    override val targetPackageName: String = "com.whatsapp"

    override fun isDistractionDetected(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo?): Boolean {
        if (event.packageName != targetPackageName || rootNode == null) return false

        try {
            // Check for Status/Updates selected tab or view ID
            val statusNodes = rootNode.findAccessibilityNodeInfosByText("Updates")
                .ifEmpty { rootNode.findAccessibilityNodeInfosByText("Status") }

            for (node in statusNodes) {
                if (node.isSelected || node.isFocused) {
                    return true
                }
            }

            // Inspect selected tabs
            return checkChildNodesForSelectedStatus(rootNode)
        } catch (e: Exception) {
            return false
        }
    }

    private fun checkChildNodesForSelectedStatus(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        val text = node.text?.toString() ?: node.contentDescription?.toString() ?: ""

        if ((text.equals("Updates", ignoreCase = true) || text.equals("Status", ignoreCase = true)) && node.isSelected) {
            return true
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (checkChildNodesForSelectedStatus(child)) return true
        }
        return false
    }
}
