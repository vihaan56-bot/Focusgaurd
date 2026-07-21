package com.focusguard.app

import com.focusguard.app.utils.TimeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun testFormatDurationMs() {
        assertEquals("0 min", TimeUtils.formatDurationMs(0))
        assertEquals("35 min", TimeUtils.formatDurationMs(35 * 60 * 1000L))
        assertEquals("1 hr 10 min", TimeUtils.formatDurationMs((70 * 60 * 1000L)))
        assertEquals("2 hr", TimeUtils.formatDurationMs((120 * 60 * 1000L)))
    }

    @Test
    fun testFormatMinutes() {
        assertEquals("No limit", TimeUtils.formatMinutes(0))
        assertEquals("45 min", TimeUtils.formatMinutes(45))
        assertEquals("1 hr 30 min", TimeUtils.formatMinutes(90))
    }

    @Test
    fun testIsTimeInScheduleNormalRange() {
        // Schedule: 09:00 (540m) to 17:00 (1020m)
        val start = 540
        val end = 1020

        assertTrue(TimeUtils.isTimeInSchedule(start, end, 600)) // 10:00 AM
        assertFalse(TimeUtils.isTimeInSchedule(start, end, 480)) // 08:00 AM
    }

    @Test
    fun testIsTimeInScheduleOvernightRange() {
        // Schedule: 22:00 (1320m) to 07:00 (420m)
        val start = 1320
        val end = 420

        assertTrue(TimeUtils.isTimeInSchedule(start, end, 1380)) // 23:00 PM
        assertTrue(TimeUtils.isTimeInSchedule(start, end, 120))  // 02:00 AM
        assertFalse(TimeUtils.isTimeInSchedule(start, end, 720))  // 12:00 PM (noon)
    }
}
