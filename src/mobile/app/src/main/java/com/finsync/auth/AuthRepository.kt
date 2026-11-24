package com.finsync.auth


/** Account UI boundary. Adapters own sessions; the UI never persists credentials. */
interface AuthRepository {
    val requiresDisplayName: Boolean get() = false
    val supportsAccountApi: Boolean get() = false
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String, displayName: String): AuthResult = register(email, password)
    suspend fun requestPasswordReset(email: String): AuthResult
    fun acceptSession(session: AuthSession) {}
    fun clearSession() {}
    suspend fun refreshSession(): AuthResult = AuthResult.Failed(AuthFailure.SessionExpired)
    suspend fun signOut(): AuthFailure? { clearSession(); return null }
    suspend fun loadProfile(): AuthUser = throw AccountApiException(AuthFailure.Unsupported)
    suspend fun updateProfile(profile: LocalProfile): AuthUser = throw AccountApiException(AuthFailure.Unsupported)
}

sealed interface AuthResult {
    data object Completed : AuthResult
    data class SignedIn(val session: AuthSession) : AuthResult
    data class Registered(val user: AuthUser) : AuthResult
    data class Failed(val reason: AuthFailure, val fields: AuthFieldErrors? = null) : AuthResult
}
enum class AuthFailure { NotConnected, Unavailable, Offline, InvalidCredentials, AccountLocked, EmailExists, InvalidInput, Unsupported, RateLimited, SessionExpired, ProfileConflict }

class AccountApiException(val reason: AuthFailure, val fields: AuthFieldErrors? = null) : Exception("Account request failed: $reason")

/** Fallback when no account endpoint is configured. Never accepts credentials,
 * creates accounts, sends recovery email or generates tokens. */
class UnavailableAuthRepository : AuthRepository {
    override suspend fun signIn(email: String, password: String) = AuthResult.Failed(AuthFailure.NotConnected)
    override suspend fun register(email: String, password: String) = AuthResult.Failed(AuthFailure.NotConnected)
    override suspend fun requestPasswordReset(email: String) = AuthResult.Failed(AuthFailure.NotConnected)
}

enum class AuthPage { Welcome, Login, Register, ForgotPassword, ResetFeedback, RegistrationFeedback, Account, Expired, Explore, Workspace }
data class AuthFieldErrors(val email: String? = null, val password: String? = null, val confirmation: String? = null,
                           val displayName: String? = null) {
    val any get() = email != null || password != null || confirmation != null || displayName != null
}

fun validateAuth(page: AuthPage, email: String, password: String, confirmation: String, displayName: String? = null): AuthFieldErrors {
    val address = email.trim()
    val emailError = when {
        address.isEmpty() -> "Enter your email address."
        address.length > 254 || !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(address) -> "Enter a valid email address."
        else -> null
    }
    val passwordError = when {
        page == AuthPage.ForgotPassword -> null
        password.isEmpty() -> "Enter your password."
        password.toByteArray(Charsets.UTF_8).size > 72 -> "Use no more than 72 UTF-8 bytes."
        page == AuthPage.Register && password.length < 8 -> "Use at least 8 characters."
        else -> null
    }
    return AuthFieldErrors(emailError, passwordError,
        if (page == AuthPage.Register && confirmation != password) "Passwords do not match." else null,
        if (page == AuthPage.Register && displayName != null && displayName.trim().length !in 1..100)
            "Enter a display name (1–100 characters)." else null)
}
