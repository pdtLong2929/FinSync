package com.finsync.auth

/** Public account data; never includes a password or token. */
data class AuthUser(val id: String, val email: String, val displayName: String, val role: String,
    val phoneNumber: String? = null, val version: Long = 0)

/** Process-memory credential. Deliberately redacted from diagnostic string output. */
class AuthSession(internal val token: String, val user: AuthUser, val expiresAtMillis: Long,
    internal val refreshToken: String? = null, val refreshExpiresAtMillis: Long = 0,
    internal val familyId: String = java.util.UUID.randomUUID().toString()) {
    fun isExpired(now: Long = System.currentTimeMillis()) = now >= expiresAtMillis
    fun canRefresh(now: Long = System.currentTimeMillis()) = !refreshToken.isNullOrBlank() && now < refreshExpiresAtMillis
    fun withUser(user: AuthUser) = AuthSession(token, user, expiresAtMillis, refreshToken, refreshExpiresAtMillis, familyId)
    override fun toString() = "AuthSession(token=<redacted>, expiresAtMillis=$expiresAtMillis)"
}

class AuthSessionStore {
    @Volatile private var value: AuthSession? = null
    @Synchronized fun get(): AuthSession? = value?.takeUnless { it.isExpired() && !it.canRefresh() }.also { if (it == null) value = null }
    @Synchronized fun install(session: AuthSession) { value = session.takeUnless { it.isExpired() && !it.canRefresh() } }
    @Synchronized fun replace(expected: AuthSession, next: AuthSession): Boolean {
        if (value !== expected) return false
        install(next); return true
    }
    @Synchronized fun clear() { value = null }
}
