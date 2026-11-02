package com.finsync

import com.finsync.auth.*
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class AuthTest {
    private class ControlledRepository : AuthRepository {
        var calls = 0
        var emailSeen = ""
        val result = CompletableDeferred<AuthResult>()
        override suspend fun signIn(email: String, password: String): AuthResult { calls++; emailSeen = email; return result.await() }
        override suspend fun register(email: String, password: String) = signIn(email, password)
        override suspend fun requestPasswordReset(email: String) = signIn(email, "")
    }
    @Test fun shippedRepositoryNeverAuthenticatesOrClaimsDelivery() = runBlocking {
        val repo = UnavailableAuthRepository()
        assertEquals(AuthResult.Failed(AuthFailure.NotConnected), repo.signIn("qa@example.com", "anything"))
        assertEquals(AuthResult.Failed(AuthFailure.NotConnected), repo.register("qa@example.com", "anything"))
        assertEquals(AuthResult.Failed(AuthFailure.NotConnected), repo.requestPasswordReset("qa@example.com"))
    }
    @Test fun validatesEmailAndRegistrationWithoutChangingPasswords() {
        assertTrue(validateAuth(AuthPage.Login, "", "", "").any)
        listOf("a", "a@@demo.test", "a b@demo.test", "a@demo").forEach {
            assertNotNull(validateAuth(AuthPage.Login, it, "valid", "").email)
        }
        assertNull(validateAuth(AuthPage.Login, "  qa+test@example.com  ", "x", "").email)
        assertNotNull(validateAuth(AuthPage.Register, "qa@example.com", "short", "short").password)
        assertNotNull(validateAuth(AuthPage.Register, "qa@example.com", "abcdefgh", "different").confirmation)
        assertFalse(validateAuth(AuthPage.Register, "qa@example.com", "abcdefgh", "abcdefgh").any)
        assertNotNull(validateAuth(AuthPage.Login, "qa@example.com", "x".repeat(129), "").password)
        assertFalse(validateAuth(AuthPage.ForgotPassword, "qa@example.com", "", "").any)
    }
    @Test fun invalidInputNeverCallsRepository() = runBlocking {
        val repo = ControlledRepository(); val controller = AuthController(repo, this)
        controller.open(AuthPage.Login); controller.submit("bad", "")
        yield(); assertEquals(0, repo.calls); assertFalse(controller.state.value.busy)
        assertTrue(controller.state.value.errors.any)
    }
    @Test fun duplicateSubmissionIsIgnoredAndSuccessIsExplicit() = runBlocking {
        val repo = ControlledRepository(); val controller = AuthController(repo, this)
        controller.open(AuthPage.Login)
        controller.submit(" qa@example.com ", "example1"); controller.submit("qa@example.com", "example1")
        assertTrue(controller.state.value.busy); yield(); assertEquals(1, repo.calls)
        assertEquals("qa@example.com", repo.emailSeen)
        repo.result.complete(AuthResult.Completed); yield()
        assertEquals(AuthPage.Account, controller.state.value.page)
        assertTrue(controller.state.value.authenticated); assertFalse(controller.state.value.busy)
    }
    @Test fun cancelledRequestCannotNavigateAfterLeaving() = runBlocking {
        val repo = ControlledRepository(); val controller = AuthController(repo, this)
        controller.open(AuthPage.Login); controller.submit("qa@example.com", "example1"); yield()
        controller.open(AuthPage.Welcome); repo.result.complete(AuthResult.Completed); yield()
        assertEquals(AuthPage.Welcome, controller.state.value.page); assertFalse(controller.state.value.authenticated)
    }
    @Test fun offlineAndExceptionsAreSafeAndRetryable() = runBlocking {
        val repo = ControlledRepository(); val controller = AuthController(repo, this)
        controller.open(AuthPage.Login); controller.submit("qa@example.com", "example1"); yield()
        repo.result.complete(AuthResult.Failed(AuthFailure.Offline)); yield()
        assertEquals(AuthPage.Login, controller.state.value.page); assertEquals(AuthFailure.Offline, controller.state.value.failure)
        assertFalse(controller.state.value.busy)
        val failing = object : AuthRepository {
            override suspend fun signIn(email: String, password: String): AuthResult = error("sensitive provider detail")
            override suspend fun register(email: String, password: String) = signIn(email,password)
            override suspend fun requestPasswordReset(email: String) = signIn(email, "")
        }
        val safe = AuthController(failing, this); safe.open(AuthPage.Login); safe.submit("qa@example.com", "example1"); yield()
        assertEquals(AuthFailure.Unavailable, safe.state.value.failure)
        assertFalse(safe.state.value.toString().contains("sensitive provider detail"))
    }
    @Test fun recoveryDoesNotMarkPreviewSignedInAndExpiryClearsPreview() = runBlocking {
        val repo = ControlledRepository(); repo.result.complete(AuthResult.Completed)
        val controller = AuthController(repo, this)
        controller.open(AuthPage.ForgotPassword); controller.submit("qa@example.com"); yield()
        assertEquals(AuthPage.ResetFeedback, controller.state.value.page); assertFalse(controller.state.value.authenticated)
        controller.open(AuthPage.Register); controller.submit("qa@example.com", "example1", "example1"); yield()
        assertTrue(controller.state.value.registered)
        controller.expireSession()
        assertEquals(AuthPage.Expired, controller.state.value.page); assertFalse(controller.state.value.authenticated)
    }
}
