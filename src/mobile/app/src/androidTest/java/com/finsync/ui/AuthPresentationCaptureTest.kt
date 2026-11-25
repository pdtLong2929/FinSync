package com.finsync.ui

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.finsync.MainActivity
import com.finsync.auth.*
import com.finsync.ui.theme.FinSyncTheme
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Rule
import org.junit.Test

/** Full available Pixel 7 viewport; no forced 393dp test column or backend mutation. */
class AuthPresentationCaptureTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    @After fun cleanup() { scope.cancel() }

    @Test fun nativeAuthPagesKeepTheirPrimaryActionReachable() {
        val auth = AuthController(UnavailableAuthRepository(), scope)
        compose.activityRule.scenario.onActivity { activity ->
            activity.setContent { FinSyncTheme { AuthHost(auth) {} } }
        }
        for ((page, action) in listOf(AuthPage.Welcome to "Get started", AuthPage.Login to "Sign in",
            AuthPage.Register to "Create account", AuthPage.ForgotPassword to "Continue")) {
            compose.runOnIdle { auth.open(page) }
            captureScreen("sculpted-${page.name.lowercase()}", compose, nativeWindow = true)
            if (page == AuthPage.Welcome || page == AuthPage.Login) {
                for (tab in listOf("Wallets", "Groups", "Reports")) compose.onNodeWithText(tab).assertDoesNotExist()
            }
            val target = hasText(action) and hasClickAction()
            if (page == AuthPage.Login) {
                compose.onNodeWithText("Email address").assertIsDisplayed()
                compose.onNodeWithText("Password").assertIsDisplayed()
                compose.onNode(target).assertIsDisplayed()
            }
            if(runCatching { compose.onNode(target).assertIsDisplayed() }.isFailure)
                compose.onNode(hasScrollToIndexAction()).performScrollToNode(target)
            compose.onNode(target).assertIsDisplayed().assertIsEnabled()
        }
    }
}
