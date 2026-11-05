package com.finsync.auth

import java.time.LocalDate

/** Device-only preferences, not a server profile or an authenticated identity. */
data class LocalProfile(
    val userId: String,
    val displayName: String,
    val phone: String = "",
    val birthday: String = "",
    val bio: String = "",
    val avatarPath: String? = null,
    val serverVersion: Long? = null,
)

data class ProfileErrors(val name: String? = null, val phone: String? = null,
    val birthday: String? = null, val bio: String? = null) {
    val any get() = name != null || phone != null || birthday != null || bio != null
}

fun validateProfile(profile: LocalProfile, today: LocalDate = LocalDate.now()): ProfileErrors = ProfileErrors(
    name = if (profile.displayName.trim().length !in 1..100) "Enter a name (1–100 characters)." else null,
    phone = if (profile.phone.isNotBlank() && (profile.phone.trim().length > 30 ||
        !profile.phone.trim().matches(Regex("[0-9+() -]+")) || profile.phone.none { it in '0'..'9' }))
        "Enter a valid phone number or leave it blank." else null,
    birthday = if (profile.birthday.isBlank()) null else {
        val date = runCatching { LocalDate.parse(profile.birthday) }.getOrNull()
        if (date == null || date.isAfter(today) || date.isBefore(today.minusYears(130)))
            "Use YYYY-MM-DD and a valid past date." else null
    },
    bio = if (profile.bio.length > 200) "Use 200 characters or fewer." else null,
)

fun profileInitials(name: String): String = name.trim().split(Regex("\\s+"))
    .filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() }
