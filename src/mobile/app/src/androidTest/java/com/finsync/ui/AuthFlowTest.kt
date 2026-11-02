package com.finsync.ui

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.finsync.MainActivity
import com.finsync.auth.*
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class AuthFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var auth = AuthController(UnavailableAuthRepository(), scope)
    private val keyboard = TestKeyboard()
    @After fun cleanup() { scope.cancel() }
    private fun host(large: Boolean = false, repository: AuthRepository = UnavailableAuthRepository()) {
        auth = AuthController(repository, scope)
        val finance = FinanceViewModel()
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                FinSyncTheme(darkTheme = large) { Box(Modifier.width(if (large) 320.dp else 393.dp).fillMaxHeight()) {
                    AuthHost(auth) { onAccount -> FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance, onAccount = onAccount) }
                } }
            }
        } }
    }
    private fun click(text: String) {
        if (runCatching { compose.onNodeWithText(text).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
        compose.onNodeWithText(text).performClick()
    }
    private fun input(label: String, value: String) {
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(label))
        compose.onNodeWithText(label).performTextReplacement(value)
    }
    @Test fun emailAcceptsHardwareKeyEventsAfterAtSign() {
        host()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        for (page in listOf(AuthPage.Login, AuthPage.Register, AuthPage.ForgotPassword)) {
            compose.runOnIdle { auth.open(page) }
            compose.onNodeWithText("Email address").performTouchInput { click() }
            compose.onNodeWithText("Email address").assertIsFocused()
            // Compose focus can precede Android attaching the editable input connection.
            keyboard.awaitOpen(compose)
            // Real Android key events, not Compose's synthetic text replacement.
            instrumentation.sendStringSync("123@")
            compose.waitUntil(5_000) {
                compose.onAllNodes(hasSetTextAction() and hasText("123@")).fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("Email address").assertTextContains("123@")
            instrumentation.sendStringSync("gmail.com")
            compose.waitUntil(5_000) {
                compose.onAllNodes(hasSetTextAction() and hasText("123@gmail.com")).fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("Email address").assertTextContains("123@gmail.com")
            compose.onNodeWithText("Email address").assertIsFocused()
            keyboard.close(compose)
        }
    }
    @Test fun actualLauncherSeparatesExplorationFromAuthentication() {
        click("Explore FinSync")
        compose.onNodeWithText("Sample workspace").assertIsDisplayed()
        compose.onNodeWithText("Balances and people are examples.", substring = true).assertIsDisplayed()
        click("Open sample workspace")
        compose.onNodeWithText("Overview").assertIsSelected()
        compose.onNodeWithText("Wallets").performClick()
        compose.onNode(hasText("Wallets") and !hasClickAction()).assertIsDisplayed()
        compose.onNodeWithText("Overview").performClick(); compose.onNodeWithText("Account").performClick()
        compose.onNodeWithText("Get started").assertExists()
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Overview").assertIsSelected()
    }
    @Test fun compactFormSupportsAutofillFocusMaskingKeyboardAndHonestFailure() {
        host(true); click("Get started")
        input("Email address", "  qa@example.com  ")
        compose.onNodeWithText("Email address").performImeAction()
        compose.onNodeWithText("Password").assertIsFocused()
        compose.onNodeWithText("Password").assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.Password))
        compose.onNodeWithText("Password").performTextInput("fictional-pass")
        compose.onNodeWithContentDescription("Show password").performClick()
        compose.onNodeWithText("Password").assertTextContains("fictional-pass")
        compose.onNodeWithContentDescription("Hide password").performClick()
        compose.onNodeWithText("Password").performClick(); keyboard.awaitOpen(compose)
        compose.onNodeWithText("Sign in").assertIsDisplayed()
        captureScreen("polish-login-keyboard-large", compose, nativeWindow = true)
        compose.onNodeWithText("Password").performImeAction()
        compose.waitUntil(5_000) { auth.state.value.failure == AuthFailure.NotConnected }
        assertFalse(auth.state.value.authenticated); assertEquals(AuthPage.Login, auth.state.value.page)
        keyboard.close(compose)
        compose.onNodeWithText("Email address").assertTextContains("qa@example.com")
        click("Back"); compose.onNodeWithText("Keep editing").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Password"))
        compose.onNodeWithText("Password").assertTextContains("fictional-pass")
        click("Explore FinSync"); compose.onNodeWithText("Leave form").performClick()
        assertEquals(AuthPage.Explore, auth.state.value.page)
    }
    @Test fun registrationAndRecoveryNeverClaimSuccessWithoutBackend() {
        host(); click("Sign up")
        input("Email address", "qa@example.com"); input("Password", "fictional-pass"); input("Confirm password", "mismatch")
        keyboard.close(compose); click("Create account")
        compose.onNodeWithText("Passwords do not match.").assertIsDisplayed()
        input("Confirm password", "fictional-pass"); keyboard.close(compose); click("Create account")
        compose.waitUntil(5_000) { auth.state.value.failure == AuthFailure.NotConnected }
        assertEquals(AuthPage.Register, auth.state.value.page); assertFalse(auth.state.value.authenticated)
        click("Back to sign in"); compose.onNodeWithText("Leave form").performClick()
        click("Forgot password?"); input("Email address", "qa@example.com"); keyboard.close(compose); click("Continue")
        compose.waitUntil(5_000) { auth.state.value.failure == AuthFailure.NotConnected }
        assertEquals(AuthPage.ForgotPassword, auth.state.value.page)
        compose.onNodeWithText("Request received").assertDoesNotExist()
    }
    @Test fun loadingDisablesSubmissionAndFailurePreservesInput() {
        val response = CompletableDeferred<AuthResult>()
        var calls = 0
        val repository = object : AuthRepository {
            override suspend fun signIn(email: String, password: String): AuthResult { calls++; return response.await() }
            override suspend fun register(email: String, password: String) = signIn(email,password)
            override suspend fun requestPasswordReset(email: String) = signIn(email, "")
        }
        host(repository = repository); click("Get started")
        input("Email address", "qa@example.com"); input("Password", "fictional-pass"); keyboard.close(compose); click("Sign in")
        compose.onNodeWithText("Please wait...").assertIsNotEnabled()
        compose.runOnIdle { auth.submit("qa@example.com", "fictional-pass"); assertEquals(1, calls) }
        captureScreen("polish-login-loading", compose, nativeWindow = true)
        compose.runOnIdle { response.complete(AuthResult.Failed(AuthFailure.Offline)) }
        compose.waitUntil(5_000) { auth.state.value.failure == AuthFailure.Offline }
        compose.onNodeWithText("You're offline.", substring = true).assertIsDisplayed()
        captureScreen("polish-login-offline", compose, nativeWindow = true)
        compose.onNodeWithText("Password").assertTextContains("fictional-pass")
    }
    @Test fun compactAndNormalAuthVisualsStayConsistent() {
        var large by mutableStateOf(false)
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                FinSyncTheme(darkTheme = large) { Box(Modifier.width(if (large) 320.dp else 393.dp).fillMaxHeight()) {
                    AuthHost(auth) { }
                } }
            }
        } }
        for (size in listOf(false, true)) {
            compose.runOnIdle { large = size }
            for (page in listOf(AuthPage.Welcome, AuthPage.Login, AuthPage.Register, AuthPage.ForgotPassword, AuthPage.ResetFeedback, AuthPage.RegistrationFeedback, AuthPage.Expired, AuthPage.Explore)) {
                compose.runOnIdle { auth.open(page) }
                captureScreen("polish-${page.name.lowercase()}-${if (size) "large" else "normal"}", compose, nativeWindow = true)
                if (page == AuthPage.Register) {
                    compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Create account"))
                    compose.onNodeWithText("Create account").assertIsDisplayed()
                    captureScreen("polish-register-action-${if (size) "large" else "normal"}", compose, nativeWindow = true)
                }
            }
        }
    }
}
