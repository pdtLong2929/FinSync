package com.finsync.auth


/** UI integration boundary. A real adapter must own secure session handling separately.
 * No tokens or credentials are persisted by this demo implementation. */
interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    suspend fun requestPasswordReset(email: String): AuthResult
}

sealed interface AuthResult {
    data object Completed : AuthResult
    data class Failed(val reason: AuthFailure) : AuthResult
}
enum class AuthFailure { NotConnected, Unavailable, Offline }

/** Shipped until an approved backend adapter is supplied. Never accepts credentials,
 * creates accounts, sends recovery email or generates tokens. */
class UnavailableAuthRepository : AuthRepository {
    override suspend fun signIn(email: String, password: String) = AuthResult.Failed(AuthFailure.NotConnected)
    override suspend fun register(email: String, password: String) = AuthResult.Failed(AuthFailure.NotConnected)
    override suspend fun requestPasswordReset(email: String) = AuthResult.Failed(AuthFailure.NotConnected)
}

enum class AuthPage { Welcome, Login, Register, ForgotPassword, ResetFeedback, RegistrationFeedback, Account, Expired, Explore, Workspace }
data class AuthFieldErrors(val email: String? = null, val password: String? = null, val confirmation: String? = null) {
    val any get() = email != null || password != null || confirmation != null
}

fun validateAuth(page: AuthPage, email: String, password: String, confirmation: String): AuthFieldErrors {
    val address = email.trim()
    val emailError = when {
        address.isEmpty() -> "Enter your email address."
        address.length > 254 || !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(address) -> "Enter a valid email address."
        else -> null
    }
    val passwordError = when {
        page == AuthPage.ForgotPassword -> null
        password.isEmpty() -> "Enter your password."
        password.length > 128 -> "Use no more than 128 characters."
        page == AuthPage.Register && password.length < 8 -> "Use at least 8 characters."
        else -> null
    }
    return AuthFieldErrors(emailError, passwordError,
        if (page == AuthPage.Register && confirmation != password) "Passwords do not match." else null)
}
