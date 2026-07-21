package com.focusguard.app.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object TimeUtils {

    /**
     * Formats milliseconds into human readable string, e.g. "42 min", "1 hr 10 min", "0 min".
     */
    fun formatDurationMs(durationMs: Long): String {
        if (durationMs <= 0) return "0 min"
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours} hr ${minutes} min"
            hours > 0 -> "${hours} hr"
            else -> "${minutes} min"
        }
    }

    /**
     * Formats minutes into human readable string.
     */
    fun formatMinutes(minutes: Int): String {
        if (minutes <= 0) return "No limit"
        val hours = minutes / 60
        val mins = minutes % 60
        return when {
            hours > 0 && mins > 0 -> "${hours} hr ${mins} min"
            hours > 0 -> "${hours} hr"
            else -> "${mins} min"
        }
    }

    /**
     * Returns the epoch day for today based on current system clock and local timezone.
     */
    fun getTodayEpochDay(): Long {
        return LocalDate.now(ZoneId.systemDefault()).toEpochDay()
    }

    /**
     * Returns start of today in epoch milliseconds.
     */
    fun getStartOfTodayMs(resetHourOfDay: Int = 0): Long {
        val now = LocalDateTime.now(ZoneId.systemDefault())
        var startOfDay = now.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (resetHourOfDay > 0) {
            startOfDay += TimeUnit.HOURS.toMillis(resetHourOfDay.toLong())
        }
        return startOfDay
    }

    /**
     * Checks if current minute of day falls within a start/end schedule window.
     */
    fun isTimeInSchedule(startMinute: Int, endMinute: Int, currentMinute: Int): Boolean {
        if (startMinute < 0 || endMinute < 0) return false
        return if (startMinute <= endMinute) {
            currentMinute in startMinute..endMinute
        } else {
            // Overnight schedule, e.g. 22:00 (1320m) to 07:00 (420m)
            currentMinute >= startMinute || currentMinute <= endMinute
        }
    }
}
