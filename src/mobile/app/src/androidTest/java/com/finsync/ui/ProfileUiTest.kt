package com.finsync.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.finsync.MainActivity
import com.finsync.auth.*
import com.finsync.ui.theme.FinSyncTheme
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.UUID

/** Local UI/storage tests. Fixture identities are not proof of backend login. */
class ProfileUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    private lateinit var store: LocalProfileStore
    private val user = AuthUser(UUID.randomUUID().toString(), "profile-ui@example.test", "Profile UI QA", "ROLE_USER")
    private var signOut = false
    private fun host(large: Boolean = false) {
        store = LocalProfileStore(compose.activity)
        val initial = runBlocking { store.load(user) }
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                FinSyncTheme { Box(Modifier.width(if (large) 320.dp else 393.dp).fillMaxHeight().safeDrawingPadding()) {
                    var profile by remember { mutableStateOf(initial) }
                    ProfileScreen(user, profile, false, false, store, {}, { profile = it }, {}, {}, { signOut = true })
                } }
            }
        } }
    }
    private fun click(text: String) {
        keyboard.close(compose)
        if (runCatching { compose.onNodeWithText(text).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
        compose.onNodeWithText(text).performClick()
    }
    private fun input(label: String, value: String) {
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(label))
        compose.onNodeWithText(label).performTextReplacement(value)
    }
    @Test fun actualLauncherAcceptsHardwareTypingOnAllAccountForms() {
        // Keep the actual Activity composition; do not replace its window/host.
        val model = compose.runOnIdle { androidx.lifecycle.ViewModelProvider(compose.activity)[AuthViewModel::class.java] }
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        for (page in listOf(AuthPage.Login, AuthPage.Register, AuthPage.ForgotPassword)) {
            compose.runOnIdle { model.controller.open(page) }
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Email address"))
            compose.onNodeWithText("Email address").performTouchInput { click() }
            compose.onNodeWithText("Email address").assertIsFocused()
            // Registration can prefill the public email. Start this typing check empty.
            compose.onNodeWithText("Email address").performTextClearance()
            compose.activityRule.scenario.onActivity { activity ->
                androidx.core.view.WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                    .show(androidx.core.view.WindowInsetsCompat.Type.ime())
            }
            compose.waitUntil(15_000) { compose.runOnIdle {
                val insets = androidx.core.view.ViewCompat.getRootWindowInsets(compose.activity.window.decorView)
                val input = compose.activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                insets?.isVisible(androidx.core.view.WindowInsetsCompat.Type.ime()) == true &&
                    insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.ime()).bottom > 0 && input.isAcceptingText
            } }
            // A visible keyboard can precede the app's editable input connection.
            compose.waitForIdle()
            instrumentation.waitForIdleSync()
            instrumentation.sendStringSync("123@")
            compose.waitUntil(5_000) { compose.onAllNodes(hasSetTextAction() and hasText("123@")).fetchSemanticsNodes().isNotEmpty() }
            instrumentation.sendStringSync("gmail.com")
            compose.waitUntil(5_000) { compose.onAllNodes(hasSetTextAction() and hasText("123@gmail.com")).fetchSemanticsNodes().isNotEmpty() }
            captureScreen("profile-account-keyboard-${page.name.lowercase()}", compose, true)
            compose.activityRule.scenario.onActivity { activity ->
                androidx.core.view.WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                    .hide(androidx.core.view.WindowInsetsCompat.Type.ime())
            }
            compose.waitUntil(5_000) { compose.runOnIdle {
                androidx.core.view.ViewCompat.getRootWindowInsets(compose.activity.window.decorView)
                    ?.isVisible(androidx.core.view.WindowInsetsCompat.Type.ime()) != true
            } }
        }
    }
    @Test fun editValidatePersistAndConfirmSignOut() {
        host()
        captureScreen("profile-home", compose, true)
        click("Edit profile")
        input("Display name", " "); click("Save on this device")
        compose.onNodeWithText("Enter a name (1–100 characters).").assertExists()
        input("Display name", "Nguyễn Phú Đạt")
        input("Phone number (optional)", "+84 912 345 678")
        input("Date of birth (optional)", "2004-01-15")
        input("About you (optional)", "My everyday finance space")
        captureScreen("profile-editor", compose, true)
        click("Save on this device")
        compose.waitUntil(5_000) { compose.onAllNodesWithText("Profile saved on this device.").fetchSemanticsNodes().isNotEmpty() }
        val saved = runBlocking { LocalProfileStore(compose.activity).load(user) }
        assertEquals("Nguyễn Phú Đạt", saved.displayName)
        assertEquals("2004-01-15", saved.birthday)
        val other = runBlocking { store.load(user.copy(id = UUID.randomUUID().toString(), displayName = "Other user")) }
        assertEquals("Other user", other.displayName); assertEquals("", other.phone)
        captureScreen("profile-saved", compose, true)
        click("Sign out on this device"); compose.onNodeWithText("Stay signed in").performClick()
        assertFalse(signOut)
        click("Sign out on this device"); compose.onNodeWithText("Sign out").performClick()
        assertTrue(signOut)
    }
    @Test fun compactLargeTextKeyboardAndDiscardKeepSavedProfile() {
        host(true); click("Edit profile")
        input("Display name", "Unsaved change")
        compose.onNodeWithText("Display name").performClick(); keyboard.awaitOpen(compose)
        compose.onNodeWithText("Save on this device").assertIsDisplayed()
        captureScreen("profile-large-keyboard", compose, true)
        click("Back"); compose.onNodeWithText("Keep editing").performClick()
        compose.onNodeWithText("Display name").assertTextContains("Unsaved change")
        click("Back"); compose.onNodeWithText("Discard").performClick()
        assertEquals("Profile UI QA", runBlocking { store.load(user) }.displayName)
    }
    @Test fun selectedPhotoIsCopiedAndInvalidImageLeavesProfileUnchanged() {
        host()
        click("Edit profile"); click("Choose photo")
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        compose.waitUntil(10_000) {
            instrumentation.uiAutomation.rootInActiveWindow?.packageName?.toString()?.let { it != "com.finsync" } == true
        }
        captureScreen("profile-photo-picker", compose, true)
        instrumentation.sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        compose.onNodeWithText("Choose photo").assertExists()
        val photo = File(compose.activity.cacheDir, "profile-test-${UUID.randomUUID()}.png")
        val bitmap = Bitmap.createBitmap(32, 48, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.CYAN)
        photo.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }; bitmap.recycle()
        try {
            val avatar = runBlocking { store.importAvatar(Uri.fromFile(photo)) }
            val saved = runBlocking { store.save(LocalProfile(user.id, user.displayName, avatarPath = avatar)) }
            assertTrue(File(avatar).isFile)
            assertEquals(saved, runBlocking { store.load(user) })
            val dimensions = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
            android.graphics.BitmapFactory.decodeFile(avatar, dimensions)
            assertEquals(dimensions.outWidth, dimensions.outHeight)
            photo.writeText("not an image")
            assertTrue(runCatching { runBlocking { store.importAvatar(Uri.fromFile(photo)) } }.isFailure)
            assertEquals(saved.avatarPath, runBlocking { store.load(user) }.avatarPath)
            runBlocking { store.save(saved.copy(avatarPath = null)); store.discardAvatar(avatar) }
            assertNull(runBlocking { store.load(user) }.avatarPath); assertFalse(File(avatar).exists())
        } finally { photo.delete() }
    }
}
