package com.finsync.auth

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.time.Instant
import java.util.Locale
import java.util.UUID

/** Real demo account contract; no credentials in logs, UI state or device storage. */
class HttpAuthRepository(baseUrl: String, allowLocalHttp: Boolean = false,
    val sessions: AuthSessionStore = AuthSessionStore()) : AuthRepository {
    private val http = AccountHttp(baseUrl, allowLocalHttp)
    private val accountLock = Mutex()
    override val requiresDisplayName = true
    override val supportsAccountApi = true
    override fun acceptSession(session: AuthSession) = sessions.install(session)
    override fun clearSession() = sessions.clear()

    override suspend fun signIn(email: String, password: String): AuthResult = authResult {
        val tokens = http.call("POST", "auth/login", JSONObject().put("email", email.trim().lowercase(Locale.ROOT))
            .put("password", password).put("rememberMe", false).put("deviceLabel", "FinSync Android")).checked()
        val user = parseUser(http.call("GET", "users/me", token = tokens.getString("accessToken")).checked())
        AuthResult.SignedIn(parseSession(tokens, user))
    }
    override suspend fun register(email: String, password: String): AuthResult = AuthResult.Failed(AuthFailure.InvalidInput)
    override suspend fun register(email: String, password: String, displayName: String): AuthResult = authResult {
        val body = http.call("POST", "auth/register", JSONObject().put("email", email.trim().lowercase(Locale.ROOT))
            .put("password", password).put("displayName", displayName.trim())).checked()
        AuthResult.Registered(parseUser(body))
    }
    override suspend fun requestPasswordReset(email: String): AuthResult = AuthResult.Failed(AuthFailure.Unsupported)
    override suspend fun loadProfile(): AuthUser = accountLock.withLock {
        val owner = sessions.get() ?: throw AccountApiException(AuthFailure.SessionExpired)
        val user = parseUser(protectedCall("GET", "users/me", expected = owner).checked())
        updateSessionIdentity(owner, user)
        user
    }
    override suspend fun updateProfile(profile: LocalProfile): AuthUser = accountLock.withLock {
        val current = sessions.get() ?: throw AccountApiException(AuthFailure.SessionExpired)
        if (current.user.id != profile.userId) throw AccountApiException(AuthFailure.SessionExpired)
        val version = profile.serverVersion ?: throw AccountApiException(AuthFailure.ProfileConflict)
        val user = parseUser(protectedCall("PATCH", "users/me", JSONObject().put("version", version)
            .put("displayName", profile.displayName.trim()).put("phoneNumber", profile.phone.trim().ifBlank { null } ?: JSONObject.NULL), expected = current).checked())
        updateSessionIdentity(current, user)
        user
    }
    private fun updateSessionIdentity(owner: AuthSession, user: AuthUser) {
        val current = sessions.get()
        if (current == null || current.familyId != owner.familyId) throw CancellationException("Account session changed")
        if (user.id != owner.user.id) throw AccountApiException(AuthFailure.SessionExpired)
        if (!sessions.replace(current, current.withUser(user))) throw CancellationException("Account session changed")
    }
    override suspend fun refreshSession(): AuthResult = accountLock.withLock { authResult {
        val current = sessions.get() ?: throw AccountApiException(AuthFailure.SessionExpired)
        AuthResult.SignedIn(if (current.isExpired()) rotate(current) else current)
    } }
    override suspend fun signOut(): AuthFailure? = accountLock.withLock {
        val owner = sessions.get() ?: return@withLock null
        try {
            // A previous 503 can follow committed revocation; 401 on retry means already revoked.
            val reply = protectedCall("POST", "auth/logout", retryUnauthorized = false, expected = owner)
            if (reply.status != 401) reply.checked()
            null
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (failure: AccountApiException) { if (failure.reason == AuthFailure.SessionExpired) null else failure.reason }
    }
    private suspend fun protectedCall(method: String, path: String, body: JSONObject? = null,
        retryUnauthorized: Boolean = true, expected: AuthSession? = null): AccountReply {
        var session = sessions.get() ?: throw AccountApiException(AuthFailure.SessionExpired)
        if (expected != null && session.familyId != expected.familyId) throw CancellationException("Account session changed")
        if (session.isExpired()) session = rotate(session)
        var reply = http.call(method, path, body, session.token)
        if (sessions.get()?.familyId != session.familyId) throw CancellationException("Account session changed")
        if (retryUnauthorized && reply.status == 401 && reply.body.optString("code") == "UNAUTHORIZED") {
            session = rotate(session)
            reply = http.call(method, path, body, session.token)
            if (sessions.get()?.familyId != session.familyId) throw CancellationException("Account session changed")
        }
        return reply
    }
    private suspend fun rotate(old: AuthSession): AuthSession {
        if (sessions.get()?.familyId != old.familyId) throw CancellationException("Account session changed")
        if (!old.canRefresh()) throw AccountApiException(AuthFailure.SessionExpired)
        try {
            val fresh = parseSession(http.call("POST", "auth/refresh", JSONObject().put("refreshToken", old.refreshToken)).checked(), old.user, old.familyId)
            if (!sessions.replace(old, fresh)) throw CancellationException("Account session changed")
            return fresh
        } catch (failure: AccountApiException) {
            if (sessions.get()?.familyId != old.familyId) throw CancellationException("Account session changed")
            if (failure.reason == AuthFailure.SessionExpired || failure.reason == AuthFailure.AccountLocked) {
                if (sessions.get() === old) sessions.clear()
            }
            throw failure
        }
    }
    private fun parseUser(body: JSONObject): AuthUser {
        val id = UUID.fromString(body.getString("id")).toString()
        val email = body.getString("email"); val name = body.getString("displayName")
        require(email.isNotBlank() && name.isNotBlank())
        val version = body.optLong("version", 0); require(version >= 0)
        return AuthUser(id, email, name, "USER", if (body.isNull("phoneNumber")) null else body.optString("phoneNumber").ifBlank { null }, version)
    }
    private fun parseSession(body: JSONObject, user: AuthUser, familyId: String = UUID.randomUUID().toString()): AuthSession {
        val now = System.currentTimeMillis(); val expires = body.getLong("expiresIn")
        require(body.getString("tokenType").equals("Bearer", true) && expires in 1..900)
        val access = body.getString("accessToken"); val refresh = body.getString("refreshToken")
        val refreshExpiry = Instant.parse(body.getString("refreshExpiresAt")).toEpochMilli()
        require(access.isNotBlank() && refresh.matches(Regex("[A-Za-z0-9_-]{43}")) && refreshExpiry > now)
        return AuthSession(access, user, now + expires * 1_000, refresh, refreshExpiry, familyId)
    }
    private suspend fun authResult(block: suspend () -> AuthResult): AuthResult = try { block() }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (failure: AccountApiException) { AuthResult.Failed(failure.reason, failure.fields) }
        catch (_: Exception) { AuthResult.Failed(AuthFailure.Unavailable) }
}
