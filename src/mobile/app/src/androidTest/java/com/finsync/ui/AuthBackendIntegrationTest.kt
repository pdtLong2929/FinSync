package com.finsync.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.finsync.BuildConfig
import com.finsync.MainActivity
import com.finsync.auth.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.Assume.assumeTrue
import androidx.test.platform.app.InstrumentationRegistry
import java.util.UUID

/** Opt-in live local backend checks. Creates disposable users only, never financial records. */
class AuthBackendIntegrationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private lateinit var model: AuthViewModel
    private val auth get() = model.controller
    private val repo get() = model.repository as HttpAuthRepository
    @Before fun bindActualActivityModel() {
        assumeTrue("Opt in with instrumentation argument liveAuth=true and start the local backend first.",
            InstrumentationRegistry.getArguments().getString("liveAuth") == "true")
        compose.runOnIdle { model = ViewModelProvider(compose.activity)[AuthViewModel::class.java] }
    }
    private fun click(label: String) {
        compose.activityRule.scenario.onActivity { activity ->
            WindowInsetsControllerCompat(activity.window, activity.window.decorView).hide(WindowInsetsCompat.Type.ime())
        }
        compose.waitUntil(5_000) { compose.runOnIdle {
            ViewCompat.getRootWindowInsets(compose.activity.window.decorView)?.isVisible(WindowInsetsCompat.Type.ime()) != true
        } }
        if (runCatching { compose.onNodeWithText(label).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(label))
        compose.onNodeWithText(label).performClick()
    }
    private fun input(label: String, value: String) {
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(label))
        compose.onNodeWithText(label).performTextReplacement(value)
    }
    private fun await(predicate: () -> Boolean) = compose.waitUntil(20_000) { predicate() }

    @Test fun realRegistrationLoginIdentityAndLocalSignOut() {
        assertTrue("Requires configured local backend", BuildConfig.AUTH_BASE_URL.isNotBlank())
        val email = "android-${UUID.randomUUID()}@example.test"
        val password = "Local-${UUID.randomUUID()}"
        click("Sign up")
        input("Display name", "Android Local QA")
        input("Email address", email); input("Password", password); input("Confirm password", password)
        click("Create account")
        await { auth.state.value.page == AuthPage.RegistrationFeedback }
        assertFalse(auth.state.value.authenticated); assertNull(repo.sessions.get())
        compose.onNodeWithText("Account created").assertIsDisplayed()
        captureScreen("account-registration-created", compose, nativeWindow = true)
        click("Back to sign in")
        input("Password", password); click("Sign in")
        await { auth.state.value.authenticated }
        assertEquals(email, auth.state.value.user?.email)
        assertNotNull(repo.sessions.get())
        compose.onNodeWithText("Overview").assertIsSelected()
        captureScreen("profile-overview-entry", compose, nativeWindow = true)
        compose.onNodeWithContentDescription("Account").performClick()
        compose.onAllNodesWithText(email).onFirst().assertIsDisplayed()
        captureScreen("auth-live-register-success", compose, nativeWindow = true)
        await { compose.onAllNodesWithText("Edit profile").fetchSemanticsNodes().isNotEmpty() &&
            runCatching { compose.onNodeWithText("Edit profile").assertIsEnabled() }.isSuccess }
        click("Edit profile")
        input("Display name", "Android Synced QA")
        input("Phone number (optional)", "+84 912 345 678")
        click("Save profile")
        await { compose.onAllNodesWithText("Account profile saved.").fetchSemanticsNodes().isNotEmpty() }
        val stored = runBlocking { repo.loadProfile() }
        assertEquals("Android Synced QA", stored.displayName); assertEquals("+84 912 345 678", stored.phoneNumber)
        captureScreen("account-profile-server-saved", compose, nativeWindow = true)
        val secondDevice = HttpAuthRepository(BuildConfig.AUTH_BASE_URL, allowLocalHttp = true)
        val login = runBlocking { secondDevice.signIn(email, password) } as AuthResult.SignedIn
        secondDevice.acceptSession(login.session)
        assertEquals("Android Synced QA", login.session.user.displayName)
        runBlocking { secondDevice.updateProfile(LocalProfile(stored.id, "Other Device QA", stored.phoneNumber.orEmpty(), serverVersion = stored.version)) }
        click("Edit profile"); input("Display name", "Unsaved Android Draft"); click("Save profile")
        await { compose.onAllNodesWithText("Reload latest profile").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Display name").assertTextContains("Unsaved Android Draft")
        assertEquals("Other Device QA", runBlocking { repo.loadProfile() }.displayName)
        captureScreen("account-profile-version-conflict", compose, nativeWindow = true)
        click("Reload latest profile"); compose.onNodeWithText("Load latest").performClick()
        await { compose.onAllNodesWithText("Other Device QA").fetchSemanticsNodes().isNotEmpty() }
        runBlocking { secondDevice.signOut() }; secondDevice.clearSession()
        val revokedSession = requireNotNull(repo.sessions.get())
        click("Sign out of account")
        compose.onNodeWithText("Sign out").performClick()
        await { !auth.state.value.authenticated }
        assertNull(repo.sessions.get()); assertFalse(auth.state.value.authenticated)
        val revokedProbe = HttpAuthRepository(BuildConfig.AUTH_BASE_URL, allowLocalHttp = true)
        revokedProbe.acceptSession(revokedSession)
        val revoked = runCatching { runBlocking { revokedProbe.loadProfile() } }.exceptionOrNull() as AccountApiException
        assertEquals(AuthFailure.SessionExpired, revoked.reason)
        click("Get started")
        input("Email address", email); input("Password", "incorrect-test-password")
        click("Sign in")
        await { auth.state.value.failure == AuthFailure.InvalidCredentials }
        assertFalse(auth.state.value.authenticated)
        compose.onNodeWithText("Email or password is incorrect.").assertIsDisplayed()
        captureScreen("auth-live-invalid-credentials", compose, nativeWindow = true)
        input("Password", password); click("Sign in")
        await { auth.state.value.authenticated }
        assertEquals(email, auth.state.value.user?.email)
        assertNotNull(repo.sessions.get())
        assertEquals("Other Device QA", auth.state.value.user?.displayName)
        compose.onNodeWithText("Overview").assertIsSelected()
        compose.onNodeWithContentDescription("Account").performClick()
        captureScreen("auth-live-login-success", compose, nativeWindow = true)
        click("Open sample workspace")
        compose.onNodeWithText("Overview").assertIsSelected()
        compose.onNodeWithContentDescription("Account").performClick()
        click("Sign out of account")
        compose.onNodeWithText("Sign out").performClick()
        await { !auth.state.value.authenticated }
        assertNull(repo.sessions.get())
    }

    @Test fun duplicateRegistrationAndUnsupportedRecoveryNeverClaimSuccess() {
        val email = "android-duplicate-${UUID.randomUUID()}@example.test"
        val password = "Local-${UUID.randomUUID()}"
        val prepared = runBlocking { repo.register(email, password, "Duplicate QA") }
        assertTrue(prepared is AuthResult.Registered)
        click("Sign up")
        input("Display name", "Duplicate QA")
        input("Email address", email.uppercase()); input("Password", password); input("Confirm password", password)
        click("Create account")
        await { auth.state.value.failure == AuthFailure.EmailExists }
        assertFalse(auth.state.value.authenticated); assertNull(repo.sessions.get())
        compose.onNodeWithText("This email is already registered. Sign in instead.").assertIsDisplayed()
        captureScreen("auth-live-duplicate-email", compose, nativeWindow = true)
        click("Back to sign in"); compose.onNodeWithText("Leave form").performClick()
        click("Forgot password?"); input("Email address", email); click("Continue")
        await { auth.state.value.failure == AuthFailure.Unsupported }
        assertEquals(AuthPage.ForgotPassword, auth.state.value.page)
        compose.onNodeWithText("Request received").assertDoesNotExist()
        captureScreen("auth-live-recovery-unsupported", compose, nativeWindow = true)
    }

    @Test fun unreachableBackendIsOfflineAndNeverStoresSession() = runBlocking {
        val isolated = HttpAuthRepository("http://127.0.0.1:1", allowLocalHttp = true)
        assertEquals(AuthResult.Failed(AuthFailure.Offline), isolated.signIn("test@example.test", "test-password"))
        assertNull(isolated.sessions.get())
    }

    @Test fun expiredAccessAndConcurrentProfileRequestsRotateRefreshSafely() = runBlocking {
        val client = HttpAuthRepository(BuildConfig.AUTH_BASE_URL, allowLocalHttp = true)
        val email = "refresh-${UUID.randomUUID()}@example.test"
        val password = "Local-${UUID.randomUUID()}"
        assertTrue(client.register(email, password, "Refresh QA") is AuthResult.Registered)
        val signedIn = client.signIn(email, password) as AuthResult.SignedIn
        val original = signedIn.session
        client.acceptSession(AuthSession(original.token, original.user, System.currentTimeMillis() - 1,
            original.refreshToken, original.refreshExpiresAtMillis))
        val observed = coroutineScope { (1..5).map { async {
            assertEquals(email, client.loadProfile().email)
            requireNotNull(client.sessions.get()).refreshToken
        } }.awaitAll() }
        assertEquals(1, observed.toSet().size)
        assertNotEquals(original.refreshToken, observed.first())
        val http = AccountHttp(BuildConfig.AUTH_BASE_URL, true)
        assertEquals(401, http.call("POST", "auth/refresh", org.json.JSONObject().put("refreshToken", original.refreshToken)).status)
        val current = requireNotNull(client.sessions.get())
        client.acceptSession(AuthSession("invalid-test-signature", current.user, System.currentTimeMillis() + 60_000,
            current.refreshToken, current.refreshExpiresAtMillis))
        assertEquals(email, client.loadProfile().email) //401 -> one refresh -> retry.
        assertNotEquals(current.refreshToken, client.sessions.get()?.refreshToken)
        assertNull(client.signOut()); client.clearSession()
    }
}
