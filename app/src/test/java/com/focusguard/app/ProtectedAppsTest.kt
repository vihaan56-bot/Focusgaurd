package com.focusguard.app

import com.focusguard.app.utils.ProtectedApps
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProtectedAppsTest {

    @Test
    fun testFocusGuardItselfIsProtected() {
        assertTrue(ProtectedApps.isProtectedPackage("com.focusguard.app"))
    }

    @Test
    fun testAndroidSettingsIsProtected() {
        assertTrue(ProtectedApps.isProtectedPackage("com.android.settings"))
        assertTrue(ProtectedApps.isProtectedPackage("com.google.android.settings"))
    }

    @Test
    fun testPhoneDialerAndEmergencyAreProtected() {
        assertTrue(ProtectedApps.isProtectedPackage("com.android.dialer"))
        assertTrue(ProtectedApps.isProtectedPackage("com.google.android.dialer"))
        assertTrue(ProtectedApps.isProtectedPackage("com.android.emergency"))
    }

    @Test
    fun testRegularDistractingAppsAreNotProtected() {
        assertFalse(ProtectedApps.isProtectedPackage("com.instagram.android"))
        assertFalse(ProtectedApps.isProtectedPackage("com.whatsapp"))
        assertFalse(ProtectedApps.isProtectedPackage("com.google.android.youtube"))
    }
}
