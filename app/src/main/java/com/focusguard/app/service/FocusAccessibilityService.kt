package com.focusguard.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.focusguard.app.FocusGuardApplication
import com.focusguard.app.detector.DistractionDetector
import com.focusguard.app.detector.InstagramDistractionDetector
import com.focusguard.app.detector.WhatsAppDistractionDetector
import com.focusguard.app.detector.YouTubeDistractionDetector
import com.focusguard.app.ui.blocking.BlockingActivity
import com.focusguard.app.utils.ProtectedApps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FocusAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val whatsAppDetector: DistractionDetector = WhatsAppDistractionDetector()
    private val instagramDetector: DistractionDetector = InstagramDistractionDetector()
    private val youTubeDetector: DistractionDetector = YouTubeDistractionDetector()

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        // Strict fail-safe: Never inspect protected applications
        if (ProtectedApps.isProtectedPackage(packageName)) return

        val rootNode = rootInActiveWindow

        serviceScope.launch {
            val app = FocusGuardApplication.instance
            val settingsRepo = app.settingsRepository
            val isFocusModeActive = settingsRepo.isFocusModeActive.first()

            val appRule = app.appRuleRepository.getRuleForPackage(packageName)

            // 1. Focus Mode Block
            val isDistracting = appRule?.isDistractingApp ?: true
            if (isFocusModeActive && isDistracting) {
                launchBlockingActivity(appRule?.appName ?: packageName, "Focus Mode is Active")
                return@launch
            }

            // 2. Daily app limit block
            if (appRule != null && appRule.dailyLimitMinutes > 0) {
                val todayTimeForeground = app.usageTrackingManager.getTodayUsageForPackage(packageName)
                val limitMs = appRule.dailyLimitMinutes * 60 * 1000L
                if (todayTimeForeground >= limitMs) {
                    launchBlockingActivity(appRule.appName, "Daily screen time limit reached")
                    return@launch
                }
            }

            // 3. Tab-level sub-section blocks
            if (rootNode != null) {
                when (packageName) {
                    whatsAppDetector.targetPackageName -> {
                        val isEnabled = settingsRepo.isWhatsAppStatusBlocking.first()
                        if (isEnabled && whatsAppDetector.isDistractionDetected(event, rootNode)) {
                            launchBlockingActivity("WhatsApp", "WhatsApp Status / Updates Blocked")
                        }
                    }
                    instagramDetector.targetPackageName -> {
                        val isEnabled = settingsRepo.isInstagramReelsBlocking.first()
                        if (isEnabled && instagramDetector.isDistractionDetected(event, rootNode)) {
                            launchBlockingActivity("Instagram", "Instagram Reels Blocked")
                        }
                    }
                    youTubeDetector.targetPackageName -> {
                        val isEnabled = settingsRepo.isYouTubeShortsBlocking.first()
                        if (isEnabled && youTubeDetector.isDistractionDetected(event, rootNode)) {
                            launchBlockingActivity("YouTube", "YouTube Shorts Blocked")
                        }
                    }
                }
            }
        }
    }

    private fun launchBlockingActivity(appName: String, reason: String) {
        val intent = Intent(this, BlockingActivity::class.java).apply {
            putExtra("EXTRA_APP_NAME", appName)
            putExtra("EXTRA_BLOCK_REASON", reason)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
    }

    override fun onInterrupt() {
        // Service interrupted
    }
}
