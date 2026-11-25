package com.finsync.ui

import android.content.ContextWrapper
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
import com.finsync.social.*
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.UUID

/** Isolated local and injected service-fixture tests. Never proves backend delivery. */
class PeopleUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    private val root by lazy { File(compose.activity.cacheDir, "people-ui-${UUID.randomUUID()}") }
    private val context by lazy { object : ContextWrapper(compose.activity.applicationContext) {
        override fun getNoBackupFilesDir() = root
    } }
    private val own = "qa@example.test"
    private fun repo(workspace: String = "qa") = DevicePeopleRepository(context, workspace, own)
    @After fun cleanup() { if (root.exists()) root.deleteRecursively() }
    private fun click(text: String) {
        keyboard.close(compose)
        if (runCatching { compose.onNodeWithText(text).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
        compose.onNodeWithText(text).performClick()
    }
    private fun input(label: String, text: String) {
        keyboard.close(compose)
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(label))
        compose.onNodeWithText(label).performTextReplacement(text)
        keyboard.close(compose)
    }
    private fun host(controller: PeopleController, finance: FinanceViewModel, large: Boolean = false) {
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalPeopleController provides controller,
                LocalDensity provides Density(density.density, if (large) 1.5f else 1f)) {
                FinSyncTheme { Box(Modifier.width(if (large) 320.dp else 411.dp).fillMaxHeight().safeDrawingPadding()) {
                    FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance)
                } }
            }
        } }
    }
    @Test fun unsentRequestsInvitationsAndCodesPersistWithoutChangingMembership() {
        val storage = repo(); val controller = PeopleController(storage)
        runBlocking { controller.reload() }
        val finance = FinanceViewModel(); val before = finance.uiState.value
        host(controller, finance)
        click("Groups"); click("Friends & invitations"); click("Add a friend")
        input("Friend's email", "bad"); click("Save unsent request")
        compose.onNodeWithText("Enter a valid email address.").assertIsDisplayed()
        input("Friend's email", own); click("Save unsent request")
        compose.onNodeWithText("Use another person's email address.").assertIsDisplayed()
        input("Friend's email", "linh@example.test"); click("Save unsent request")
        compose.waitUntil { controller.state.value.data.friends.size == 1 }
        assertEquals(ConnectionStatus.Draft, controller.state.value.data.friends.single().status)
        captureScreen("people-unsent-request", compose, nativeWindow = true)
        click("Back"); click("Join with code")
        input("Invitation code", "xx"); click("Save code for later")
        compose.onNodeWithText("Enter a code of 6–12 letters or numbers.").assertIsDisplayed()
        input("Invitation code", "a12b34"); click("Save code for later")
        compose.waitUntil { controller.state.value.data.joins.size == 1 }
        captureScreen("people-join-code", compose, nativeWindow = true)
        click("Back"); click("Roommates"); click("Members"); click("Invite members")
        input("Invitee email", "linh@example.test"); click("Save invitation draft")
        compose.waitUntil { controller.state.value.data.invitations.size == 1 }
        captureScreen("people-group-invitation", compose, nativeWindow = true)
        click("Code"); compose.onNodeWithText("This code is not active on other devices yet. Group sync must activate it first.").assertIsDisplayed()
        click("Copy code"); compose.onNodeWithText("Code copied").assertIsDisplayed()
        assertEquals(before.groups, finance.uiState.value.groups)
        assertEquals(before.groupExpenses, finance.uiState.value.groupExpenses)
        assertEquals(before.entries, finance.uiState.value.entries)
        assertEquals(controller.state.value.data, runBlocking { repo().load() })
        assertEquals(PeopleData(), runBlocking { repo("other-account").load() })
    }

    private class ServiceFixture : PeopleRepository {
        override val connected = true
        var data = PeopleData(friends = listOf(
            Friendship("friend", Person("linh", "Linh QA", "linh@example.test"), false, ConnectionStatus.Accepted),
            Friendship("incoming", Person("minh", "Minh QA", "minh@example.test"), true, ConnectionStatus.Pending)),
            invitations = listOf(GroupInvitation("incoming-trip", "fixture", "Test trip", "qa@example.test", true, ConnectionStatus.Pending)))
        override suspend fun load() = data
        override suspend fun resolveCode(code: String): GroupPreview {
            require(code != "ZZZZZZ") { "This invitation has expired. Ask the owner for a new code." }
            return GroupPreview("fixture", "Test trip", "Owner QA", 4, code)
        }
        override suspend fun execute(command: PeopleCommand): PeopleData {
            data = when (command) {
                is PeopleCommand.ReplyFriend -> data.copy(friends = data.friends.map { if (it.id == command.id) it.copy(status = if (command.accept) ConnectionStatus.Accepted else ConnectionStatus.Declined) else it })
                is PeopleCommand.ReplyInvite -> data.copy(invitations = data.invitations.map { if (it.id == command.id) it.copy(status = if (command.accept) ConnectionStatus.Accepted else ConnectionStatus.Declined) else it })
                is PeopleCommand.Invite -> data.copy(invitations = data.invitations + GroupInvitation("fixture-outgoing", command.groupId, command.groupName, command.email, false, ConnectionStatus.Pending))
                is PeopleCommand.Join -> data.copy(joins = data.joins + JoinRequest("fixture-join", command.code, ConnectionStatus.Pending))
                else -> error("Unexpected fixture command")
            }
            return data
        }
    }
    @Test fun acceptedFriendPickerAndIncomingRepliesAtLargeText() {
        val service = ServiceFixture(); val controller = PeopleController(service)
        runBlocking { controller.reload() }
        val finance = FinanceViewModel(); val before = finance.uiState.value
        host(controller, finance, large = true)
        click("Groups"); click("Roommates"); click("Members"); click("Invite members"); click("Friends")
        click("Choose Linh QA"); click("Invite to group")
        compose.waitUntil { service.data.invitations.size == 2 }
        assertEquals(ConnectionStatus.Pending, service.data.invitations.last().status)
        captureScreen("people-friend-picker-large-fixture", compose, nativeWindow = true)
        click("Back"); click("Back"); click("Back"); click("Friends & invitations"); click("Requests")
        click("Accept friend"); click("Cancel")
        assertEquals(ConnectionStatus.Pending, service.data.friends.last().status)
        click("Accept friend"); click("Confirm")
        compose.waitUntil { service.data.friends.last().status == ConnectionStatus.Accepted }
        click("Friends")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Minh QA"))
        compose.onNodeWithText("Minh QA").assertIsDisplayed()
        click("Invitations"); click("Accept group invite"); click("Confirm")
        compose.waitUntil { service.data.invitations.first().status == ConnectionStatus.Accepted }
        captureScreen("people-incoming-invitations-large-fixture", compose, nativeWindow = true)
        // A social response alone must not fabricate a group-ledger membership.
        assertEquals(before.groups, finance.uiState.value.groups)
        assertEquals(before.entries, finance.uiState.value.entries)
    }
    @Test fun ownerCanRenameRemoveAndRestoreLocalMemberWithoutLosingHistory() {
        val controller = PeopleController(repo()); runBlocking { controller.reload() }
        val finance = FinanceViewModel()
        finance.createGroup("Member QA", "TRIP", "")
        val group = finance.uiState.value.groups.last()
        finance.changeMember(group.id, "Linh")
        val member = finance.uiState.value.groups.last().members.last()
        host(controller, finance)
        click("Groups"); click("Member QA"); click("Members"); click("Edit Linh")
        compose.onNodeWithText("Member display name").performTextReplacement("Linh Updated")
        compose.onNodeWithText("Member display name").performImeAction()
        click("Save member"); click("Remove Linh Updated"); click("Remove")
        compose.waitUntil { finance.uiState.value.groups.last().members.last().active == false }
        click("Restore Linh Updated"); click("Save member")
        compose.waitUntil { finance.uiState.value.groups.last().members.last().active }
        assertEquals(member.id, finance.uiState.value.groups.last().members.last().id)
        captureScreen("people-member-management", compose, nativeWindow = true)
    }
    @Test fun profileFriendsEntryAndConfirmedLogoutCloseAuthenticatedUi() {
        val controller = PeopleController(repo()); runBlocking { controller.reload() }
        lateinit var auth: AuthController
        val user = AuthUser(UUID.randomUUID().toString(), own, "Account QA", "ROLE_USER")
        val repository = object : AuthRepository {
            override suspend fun signIn(email: String, password: String) = AuthResult.SignedIn(AuthSession("fixture-token", user, System.currentTimeMillis() + 3_600_000))
            override suspend fun register(email: String, password: String) = AuthResult.Failed(AuthFailure.Unsupported)
            override suspend fun requestPasswordReset(email: String) = AuthResult.Failed(AuthFailure.Unsupported)
        }
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe()
            val scope = rememberCoroutineScope()
            auth = remember { AuthController(repository, scope) }
            LaunchedEffect(Unit) { auth.open(AuthPage.Login); auth.submit(own, "test-password") }
            CompositionLocalProvider(LocalPeopleController provides controller) { FinSyncTheme {
                AuthHost(auth) { account -> FinSyncApp(DashboardUiState.Content(DemoDashboard.data), onAccount = account) }
            } }
        } }
        compose.waitForIdle()
        compose.waitUntil(5_000) { auth.state.value.authenticated }
        compose.onNodeWithContentDescription("Account").performClick()
        compose.onNodeWithText("My profile").assertIsDisplayed()
        click("Friends & invitations"); click("Back")
        captureScreen("people-profile-logout", compose, nativeWindow = true)
        click("Log out"); click("Stay signed in"); assertTrue(auth.state.value.authenticated)
        click("Log out"); click("Sign out")
        compose.waitUntil { !auth.state.value.authenticated }
        assertEquals(AuthPage.Welcome, auth.state.value.page)
        assertNull(auth.state.value.user)
        compose.onNodeWithText("My profile").assertDoesNotExist()
    }
    @Test fun failedServerLogoutOffersExplicitConfirmedLocalFallback() {
        var localSignOut = false
        var serverSignOut = false
        val user = AuthUser(UUID.randomUUID().toString(), own, "Logout QA", "ROLE_USER")
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe()
            FinSyncTheme { Box(Modifier.fillMaxSize().safeDrawingPadding()) {
                ProfileScreen(user, LocalProfile(user.id, user.displayName), false, false,
                    LocalProfileStore(context), {}, {}, {}, {}, { serverSignOut = true },
                    remoteFailure = AuthFailure.Unavailable, onLocalSignOut = { localSignOut = true })
            } }
        } }
        click("Sign out locally instead"); click("Cancel")
        assertFalse(localSignOut)
        click("Sign out locally instead"); click("Confirm local sign out")
        assertTrue(localSignOut); assertFalse(serverSignOut)
    }
    @Test fun codePreviewRequiresReviewBeforeAnyJoinRequest() {
        val service = ServiceFixture(); val controller = PeopleController(service)
        runBlocking { controller.reload() }
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            keyboard.Observe(); FinSyncTheme { Box(Modifier.fillMaxSize().safeDrawingPadding()) {
                JoinGroupCodeScreen(controller, emptySet(), {})
            } }
        } }
        input("Invitation code", "ZZZZZZ"); click("Review group")
        compose.onNodeWithText("This invitation has expired. Ask the owner for a new code.").assertIsDisplayed()
        assertTrue(service.data.joins.isEmpty())
        input("Invitation code", "ABC123"); click("Review group")
        compose.onNodeWithText("Owner: Owner QA").assertIsDisplayed()
        assertTrue(service.data.joins.isEmpty())
        captureScreen("people-code-preview-fixture", compose, nativeWindow = true)
        click("Confirm join request")
        compose.waitUntil { service.data.joins.size == 1 }
        assertEquals(ConnectionStatus.Pending, service.data.joins.single().status)
    }
}
