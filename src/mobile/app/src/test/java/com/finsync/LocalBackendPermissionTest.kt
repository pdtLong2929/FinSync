package com.finsync

import com.finsync.auth.requiresLocalBackendPermission
import org.junit.Assert.*
import org.junit.Test

class LocalBackendPermissionTest {
    @Test fun android17LocalDemoNeedsPermissionButOlderAndroidAndPublicApiDoNot() {
        assertTrue(requiresLocalBackendPermission(37, true, "http://10.0.2.2:8080"))
        assertTrue(requiresLocalBackendPermission(38, true, "http://10.0.2.2:18080"))
        assertFalse(requiresLocalBackendPermission(36, true, "http://10.0.2.2:8080"))
        assertFalse(requiresLocalBackendPermission(37, false, "https://api.example.test"))
        assertFalse(requiresLocalBackendPermission(37, true, "https://api.example.test"))
        assertFalse(requiresLocalBackendPermission(37, true, "http://10.0.2.2.attacker.test"))
        assertFalse(requiresLocalBackendPermission(37, true, ""))
    }
}
