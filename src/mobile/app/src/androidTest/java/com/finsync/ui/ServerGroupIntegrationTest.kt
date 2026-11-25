package com.finsync.ui

import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.finsync.BuildConfig
import com.finsync.MainActivity
import com.finsync.auth.*
import com.finsync.bank.personalLedger
import com.finsync.domain.*
import com.finsync.groups.*
import com.finsync.ui.dashboard.*
import com.finsync.ui.theme.FinSyncTheme
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.util.UUID

/** Real authenticated Android → PA4 backend → PostgreSQL. Dedicated QA accounts. */
class ServerGroupIntegrationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val keyboard = TestKeyboard()
    private val password = "QA-Group-Password-2026"
    private fun account(name: String): HttpAuthRepository = runBlocking {
        val auth = HttpAuthRepository(BuildConfig.AUTH_BASE_URL, true)
        val email = "pa4-${UUID.randomUUID()}@example.test"
        assertTrue(auth.register(email, password, name) is AuthResult.Registered)
        val login = auth.signIn(email, password) as AuthResult.SignedIn
        auth.acceptSession(login.session)
        auth
    }
    private fun click(text: String) {
        keyboard.close(compose)
        if (runCatching { compose.onNodeWithText(text).assertIsDisplayed() }.isFailure)
            compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
        compose.onNodeWithText(text).performClick()
    }
    private fun input(label: String, value: String) {
        keyboard.close(compose)
        compose.waitUntil(15_000) { compose.onAllNodesWithText(label).fetchSemanticsNodes().isNotEmpty() }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(label))
        compose.onNodeWithText(label).performTextReplacement(value)
        keyboard.close(compose)
    }
    @Test fun nativeCreateExpensePartialPaymentAndFreshServerReload() {
        val auth = account("PA4 UI Owner")
        val server = ServerGroups(auth)
        val finance = FinanceViewModel()
        val personalBefore = finance.uiState.value.personalLedger()
        compose.activityRule.scenario.onActivity { it.setContent {
            keyboard.Observe()
            CompositionLocalProvider(LocalServerGroups provides server) { FinSyncTheme {
                FinSyncApp(DashboardUiState.Content(DemoDashboard.data), finance = finance)
            } }
        } }
        click("Groups")
        compose.waitUntil(15_000) { !server.state.value.loading }
        assertNull(server.state.value.error)
        assertTrue(server.state.value.ledger.groups.isEmpty())
        click("Create group")
        input("Group name", "PA4 live ${UUID.randomUUID().toString().take(5)}")
        click("Save group")
        compose.waitUntil(15_000) { server.state.value.ledger.groups.size == 1 && !server.state.value.busy }
        val group = server.state.value.ledger.groups.single()
        click(group.name); click("Members")
        input("Member name", "Guest Linh")
        click("Add member")
        compose.waitUntil(15_000) { server.state.value.ledger.groups.single().members.size == 2 && !server.state.value.busy }
        click("Expenses"); click("Add group expense")
        input("Expense title", "Real API dinner")
        input("Amount (VND)", "100001")
        click("Review split"); click("Save shared expense")
        compose.waitUntil(15_000) { server.state.value.ledger.expenses.size == 1 && !server.state.value.busy }
        val expense = server.state.value.ledger.expenses.single()
        assertEquals(100001L, expense.shares.values.sum())
        click("Balances")
        val debt = server.state.value.debts.getValue(group.id).single()
        assertEquals(50000L, debt.amount)
        captureScreen("pa4-server-balances", compose, nativeWindow = true)
        click("Record payment"); click("Partial payment")
        input("Amount (VND)", "10000")
        click("Review settlement"); click("Confirm partial payment")
        compose.waitUntil(15_000) { server.state.value.ledger.settlements.size == 1 && !server.state.value.busy }
        assertEquals(40000L, server.state.value.debts.getValue(group.id).single().amount)
        val fresh = ServerGroups(auth)
        runBlocking { fresh.refresh() }
        assertNull(fresh.state.value.error)
        assertEquals(server.state.value.ledger, fresh.state.value.ledger)
        assertEquals(server.state.value.balances, fresh.state.value.balances)
        assertEquals(personalBefore, finance.uiState.value.personalLedger())
        captureScreen("pa4-server-payment", compose, nativeWindow = true)
        runBlocking { auth.signOut() }; auth.clearSession()
    }
    @Test fun realInvitationFourSplitMethodsIdempotencyAndAccessIsolation() = runBlocking {
        val owner = account("PA4 API Owner"); val other = account("PA4 API Member")
        val server = ServerGroups(owner); server.refresh()
        assertNull(server.create("Server contract ${UUID.randomUUID().toString().take(5)}", "QA-only group"))
        val group = server.state.value.ledger.groups.single()
        val token = server.invitation(group.id)
        val outsider = ServerGroups(other); outsider.refresh()
        assertTrue(outsider.state.value.ledger.groups.isEmpty())
        assertNull(outsider.accept(token))
        server.refresh()
        val joined = server.state.value.ledger.groups.single()
        assertEquals(2, joined.members.size)
        val people = joined.members.map { it.id }
        val category = server.state.value.categories.keys.first()
        for (mode in SplitMode.entries) {
            val request = UUID.randomUUID().toString()
            val values = when(mode) {
                SplitMode.Equal -> emptyMap()
                SplitMode.Exact -> people.zip(listOf("50000", "50001")).toMap()
                SplitMode.Percentage -> people.associateWith { "50" }
                SplitMode.Shares -> people.associateWith { "1" }
            }
            assertNull(server.expense(group.id, request, "QA ${mode.name}", "100001", "me", LocalDate.now().toString(), category, people, mode, values))
            val entry = server.state.value.ledger.expenses.first { it.title == "QA ${mode.name}" }
            assertEquals(serverSplit(100001, "me", people, mode, values), entry.shares)
            assertNull(server.expense(group.id, request, "QA ${mode.name}", "100001", "me", LocalDate.now().toString(), category, people, mode, values))
            assertEquals(1, server.state.value.ledger.expenses.count { it.title == "QA ${mode.name}" })
        }
        assertNull(server.expense(group.id, UUID.randomUUID().toString(), "QA larger server record", "1000000000000",
            "me", LocalDate.now().toString(), category, people, SplitMode.Equal, emptyMap()))
        assertNull(server.state.value.error)
        assertEquals(1000000000000L, server.state.value.ledger.expenses.first { it.title == "QA larger server record" }.amount)
        val stranger = account("PA4 Stranger")
        val denied = stranger.groupRequest("GET", "groups/${group.id}")
        assertTrue(denied.status in listOf(403, 404))
        owner.signOut(); other.signOut(); stranger.signOut()
        owner.clearSession(); other.clearSession(); stranger.clearSession()
    }
}
