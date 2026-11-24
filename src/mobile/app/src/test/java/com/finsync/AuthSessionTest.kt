package com.finsync

import com.finsync.auth.*
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class AuthSessionTest {
    private val user = AuthUser("c68a1a0a-9607-400a-b94d-a1a6fcfe5760", "test@example.test", "Test User", "ROLE_USER")
    private fun session(expired: Boolean = false) = AuthSession("test-only-secret-token", user,
        System.currentTimeMillis() + if (expired) -1_000 else 3_600_000)

    @Test fun expiredCredentialsAreDroppedAndDiagnosticsAreRedacted() {
        val store = AuthSessionStore()
        store.install(session(true)); assertNull(store.get())
        val valid = session(); store.install(valid); assertSame(valid, store.get())
        assertFalse(valid.toString().contains("test-only-secret-token"))
        assertFalse(AuthResult.SignedIn(valid).toString().contains("test-only-secret-token"))
        store.clear(); assertNull(store.get())
    }

    @Test fun acceptedBackendSessionHasIdentityAndLocalSignOutClearsCredentials() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val store = AuthSessionStore()
        val repo = object : AuthRepository {
            override suspend fun signIn(email: String, password: String) = AuthResult.SignedIn(session())
            override suspend fun register(email: String, password: String) = signIn(email, password)
            override suspend fun requestPasswordReset(email: String) = AuthResult.Failed(AuthFailure.Unsupported)
            override fun acceptSession(session: AuthSession) = store.install(session)
            override fun clearSession() = store.clear()
        }
        try {
            val controller = AuthController(repo, scope)
            controller.open(AuthPage.Login); controller.submit(user.email, "test-password")
            assertTrue(controller.state.value.authenticated)
            assertEquals(AuthPage.Workspace, controller.state.value.page)
            assertEquals(user, controller.state.value.user)
            assertNotNull(store.get())
            controller.expireSession()
            assertFalse(controller.state.value.authenticated); assertNull(store.get())
            assertEquals(AuthPage.Expired, controller.state.value.page)
            controller.open(AuthPage.Login); controller.submit(user.email, "test-password")
            controller.leaveAccount()
            assertNull(store.get()); assertNull(controller.state.value.user)
            assertFalse(controller.state.value.canReturnToWorkspace)
        } finally { scope.cancel() }
    }

    @Test fun connectedRegistrationRequiresDisplayNameBeforeNetworkCall() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        var calls = 0
        val repo = object : AuthRepository {
            override val requiresDisplayName = true
            override suspend fun signIn(email: String, password: String): AuthResult { calls++; return AuthResult.SignedIn(session()) }
            override suspend fun register(email: String, password: String) = signIn(email, password)
            override suspend fun requestPasswordReset(email: String) = AuthResult.Failed(AuthFailure.Unsupported)
        }
        try {
            val controller = AuthController(repo, scope)
            controller.open(AuthPage.Register)
            controller.submit(user.email, "password123", "password123")
            assertEquals(0, calls); assertNotNull(controller.state.value.errors.displayName)
            controller.submit(user.email, "password123", "password123", user.displayName)
            assertEquals(1, calls); assertTrue(controller.state.value.authenticated)
        } finally { scope.cancel() }
    }

    @Test fun lateResponseAfterNavigationCannotInstallCredentials() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val response = CompletableDeferred<AuthResult>()
        val store = AuthSessionStore()
        val repo = object : AuthRepository {
            override suspend fun signIn(email: String, password: String) = withContext(NonCancellable) { response.await() }
            override suspend fun register(email: String, password: String) = signIn(email, password)
            override suspend fun requestPasswordReset(email: String) = AuthResult.Failed(AuthFailure.Unsupported)
            override fun acceptSession(session: AuthSession) = store.install(session)
        }
        try {
            val controller = AuthController(repo, scope)
            controller.open(AuthPage.Login); controller.submit(user.email, "password123")
            controller.open(AuthPage.Welcome)
            response.complete(AuthResult.SignedIn(session())); yield()
            assertEquals(AuthPage.Welcome, controller.state.value.page)
            assertFalse(controller.state.value.authenticated); assertNull(store.get())
        } finally { scope.cancel() }
    }
}
