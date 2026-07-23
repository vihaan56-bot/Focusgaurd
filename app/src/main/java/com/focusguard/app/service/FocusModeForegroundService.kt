package com.focusguard.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.focusguard.app.FocusGuardApplication
import com.focusguard.app.MainActivity
import com.focusguard.app.R
import kotlinx.coroutines.*
import java.util.Locale

class FocusModeForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var countdownJob: Job? = null

    companion object {
        private const val CHANNEL_ID = "focus_mode_channel"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.focusguard.app.action.START_FOCUS"
        const val ACTION_STOP = "com.focusguard.app.action.STOP_FOCUS"
        const val EXTRA_END_TIME_MS = "extra_end_time_ms"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val endTimeMs = intent.getLongExtra(EXTRA_END_TIME_MS, 0L)
                if (endTimeMs > System.currentTimeMillis()) {
                    startFocusCountdown(endTimeMs)
                } else {
                    stopSelf()
                }
            }
            ACTION_STOP -> {
                stopFocusSession()
            }
            else -> stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun startFocusCountdown(endTimeMs: Long) {
        countdownJob?.cancel()
        
        // Build initial notification
        val notification = buildNotification("Focus Mode Active", "Initializing...")
        startForeground(NOTIFICATION_ID, notification)

        countdownJob = serviceScope.launch {
            while (isActive) {
                val remainingMs = endTimeMs - System.currentTimeMillis()
                if (remainingMs <= 0) {
                    // Session expired!
                    onSessionExpired()
                    break
                }

                // Update notification text
                val timeString = formatTime(remainingMs)
                val updatedNotification = buildNotification("Focus Mode Active", "$timeString remaining")
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, updatedNotification)

                delay(1000L) // tick every second
            }
        }
    }

    private fun stopFocusSession() {
        countdownJob?.cancel()
        stopSelf()
    }

    private suspend fun onSessionExpired() {
        val app = application as FocusGuardApplication
        app.settingsRepository.setFocusModeState(active = false, endTimeMs = 0L)
        stopSelf()
    }

    private fun buildNotification(title: String, content: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Mode Session",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress of active focus sessions."
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSecs = ms / 1000L
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
