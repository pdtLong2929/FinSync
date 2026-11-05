package com.finsync

import com.finsync.auth.*
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class AccountContractTest {
    private val user = AuthUser("c68a1a0a-9607-400a-b94d-a1a6fcfe5760", "qa@example.test", "QA", "USER")
    @Test fun bcryptLimitCountsUtf8BytesWithoutTruncatingPasswords() {
        assertNull(validateAuth(AuthPage.Register, user.email, "é".repeat(36), "é".repeat(36), "QA").password)
        assertNotNull(validateAuth(AuthPage.Register, user.email, "é".repeat(37), "é".repeat(37), "QA").password)
        assertNotNull(validateAuth(AuthPage.Login, user.email, "a".repeat(73), "").password)
    }
    @Test fun expiredAccessCanRetainLiveRefreshAndAtomicSwapRejectsOldSessions() {
        val store = AuthSessionStore(); val now = System.currentTimeMillis()
        val old = AuthSession("old-access", user, now - 1, "refresh-secret", now + 60_000)
        store.install(old); assertSame(old, store.get())
        assertEquals(old.familyId, old.withUser(user.copy(displayName = "Updated QA")).familyId)
        val next = AuthSession("next-access", user, now + 30_000, "new-refresh", now + 60_000)
        assertTrue(store.replace(old, next)); assertFalse(store.replace(old, old))
        store.clear(); assertFalse(store.replace(next, old)); assertNull(store.get())
        assertFalse(old.toString().contains("refresh-secret"))
    }
    @Test fun registrationRequiresSignInAndPrefillsOnlyPublicEmail() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val repo = object : AuthRepository {
            override val requiresDisplayName = true
            override suspend fun signIn(email: String, password: String) = AuthResult.Failed(AuthFailure.Unavailable)
            override suspend fun register(email: String, password: String) = AuthResult.Registered(user)
            override suspend fun requestPasswordReset(email: String) = AuthResult.Failed(AuthFailure.Unsupported)
        }
        try {
            val controller = AuthController(repo, scope); controller.open(AuthPage.Register)
            controller.submit(user.email, "password123", "password123", user.displayName)
            assertEquals(AuthPage.RegistrationFeedback, controller.state.value.page)
            assertFalse(controller.state.value.authenticated); assertNull(controller.state.value.user)
            assertEquals(user.email, controller.state.value.prefillEmail)
        } finally { scope.cancel() }
    }
    @Test fun failedRemoteLogoutDoesNotPretendToClearSession() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val session = AuthSession("test", user, System.currentTimeMillis() + 60_000)
        var failure: AuthFailure? = AuthFailure.Offline
        var cleared = false
        val repo = object : AuthRepository {
            override val supportsAccountApi = true
            override suspend fun signIn(email: String, password: String) = AuthResult.SignedIn(session)
            override suspend fun register(email: String, password: String) = signIn(email, password)
            override suspend fun requestPasswordReset(email: String) = AuthResult.Failed(AuthFailure.Unsupported)
            override suspend fun signOut() = failure
            override fun clearSession() { cleared = true }
        }
        try {
            val controller = AuthController(repo, scope); controller.open(AuthPage.Login); controller.submit(user.email, "password123")
            controller.signOut(); assertTrue(controller.state.value.authenticated); assertFalse(cleared)
            assertEquals(AuthFailure.Offline, controller.state.value.failure)
            failure = AuthFailure.AccountLocked; controller.signOut()
            assertFalse(controller.state.value.authenticated); assertTrue(cleared)
            assertEquals(AuthPage.Expired, controller.state.value.page)
            cleared = false; controller.open(AuthPage.Login); controller.submit(user.email, "password123")
            failure = null; controller.signOut(); assertFalse(controller.state.value.authenticated); assertTrue(cleared)
        } finally { scope.cancel() }
    }
    @Test fun lateLogoutResponseCannotClearANewerSignIn() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val response = CompletableDeferred<AuthFailure?>()
        var nextUser = user
        val repo = object : AuthRepository {
            override val supportsAccountApi = true
            override suspend fun signIn(email: String, password: String) = AuthResult.SignedIn(AuthSession("test", nextUser, System.currentTimeMillis() + 60_000))
            override suspend fun register(email: String, password: String) = signIn(email, password)
            override suspend fun requestPasswordReset(email: String) = AuthResult.Failed(AuthFailure.Unsupported)
            override suspend fun signOut() = withContext(NonCancellable) { response.await() }
        }
        try {
            val controller = AuthController(repo, scope)
            controller.open(AuthPage.Login); controller.submit(user.email, "password123"); controller.signOut()
            nextUser = user.copy(id = "21007acf-e5d2-4c07-a72b-cf98fe972ea0", email = "new@example.test")
            controller.open(AuthPage.Login); controller.submit(nextUser.email, "password123")
            response.complete(null); yield()
            assertTrue(controller.state.value.authenticated)
            assertEquals(nextUser.id, controller.state.value.user?.id)
        } finally { scope.cancel() }
    }
}
