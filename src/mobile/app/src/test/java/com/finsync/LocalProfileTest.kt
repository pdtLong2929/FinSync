package com.finsync

import com.finsync.auth.*
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class LocalProfileTest {
    private val today = LocalDate.of(2026, 11, 4)
    private val valid = LocalProfile("test", "Nguyễn Phú Đạt")
    @Test fun optionalInformationCanBeEmpty() { assertFalse(validateProfile(valid, today).any) }
    @Test fun nameAndBioLengthsAreValidated() {
        assertNotNull(validateProfile(valid.copy(displayName = "   "), today).name)
        assertNotNull(validateProfile(valid.copy(displayName = "a".repeat(101)), today).name)
        assertNotNull(validateProfile(valid.copy(bio = "a".repeat(201)), today).bio)
    }
    @Test fun birthdayRejectsFutureAndInvalidDates() {
        listOf("2027-01-01", "2024-02-30", "11/04/2000", "1800-01-01").forEach {
            assertNotNull(it, validateProfile(valid.copy(birthday = it), today).birthday)
        }
        assertNull(validateProfile(valid.copy(birthday = "2004-02-29"), today).birthday)
    }
    @Test fun phoneAndInitialsSupportNormalInput() {
        assertNull(validateProfile(valid.copy(phone = "+84 912 345 678"), today).phone)
        assertNotNull(validateProfile(valid.copy(phone = "wrong"), today).phone)
        assertEquals("NĐ", profileInitials(" Nguyễn  Đạt "))
        assertEquals("", profileInitials(" "))
    }
}
