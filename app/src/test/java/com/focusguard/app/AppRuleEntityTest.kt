package com.focusguard.app

import com.focusguard.app.data.database.entity.AppRuleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AppRuleEntityTest {

    @Test
    fun testAppRuleEntityDefaults() {
        val entity = AppRuleEntity(
            packageName = "com.instagram.android",
            appName = "Instagram",
            dailyLimitMinutes = 30
        )

        assertEquals("com.instagram.android", entity.packageName)
        assertEquals("Instagram", entity.appName)
        assertEquals(30, entity.dailyLimitMinutes)
        assertFalse(entity.isBlocked)
        assertFalse(entity.isInstagramReelsBlocked)
    }
}
