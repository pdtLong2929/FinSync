package com.finsync

import com.finsync.bank.*
import com.finsync.domain.*
import com.finsync.ui.FinanceViewModel
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class BankCaptureTest {
    private val today = LocalDate.now()
    private val time = System.currentTimeMillis()
    private fun parse(text: String, key: String = "key", eventTime: Long = time) = BankNotificationParser.parse("bank.fixture", key, time, eventTime, text)
    private val candidate get() = parse("GD -125.000 VND. Số dư 9.999.999 VND")!!
    private val ledger get() = PersonalLedger(listOf(Wallet("bank", "My bank", WalletType.BANK_ACCOUNT), Wallet("cash", "Cash", WalletType.CASH)),
        listOf(LedgerEntry("opening", "bank", EntryType.OPENING, 1_000_000, "Opening", today)), defaultCategories, emptyList(), "bank", "Food")
    private fun review(kind: BankRecordKind = BankRecordKind.Expense) = BankReview(kind, "125000", "bank", "cash", if (kind == BankRecordKind.Income) "Salary" else "Food", today.toString(), "Lunch")

    @Test fun parsesSignedVietnameseAlertsWithoutImportingBalance() {
        assertEquals(125000L, candidate.amount); assertEquals(BankDirection.Outgoing, candidate.direction)
        assertEquals(2_300_000L, parse("TK 123456 GD: +2,300,000 VND. SD: 12,000,000 VND")!!.amount)
        assertEquals(125000L, parse("Tài khoản 123456 -125.000đ. Số dư 9.999.999đ")!!.amount)
        assertEquals(BankDirection.Incoming, parse("Ghi có 150.000 VND")!!.direction)
        assertEquals(BankDirection.Outgoing, parse("Thanh toán 150,000 VND")!!.direction)
        assertNull(parse("Số dư tài khoản: 9.999.999 VND"))
    }
    @Test fun rejectsOtpForeignCurrencyAndUnrelatedPromotions() {
        listOf("OTP giao dịch -125.000 VND: 123456", "Mã xác thực thanh toán 125.000 VND là 123456",
            "Verification code for paid 125000 VND", "Giao dịch -100 USD", "Ưu đãi 125.000 VND").forEach { assertNull(it, parse(it)) }
        assertNull(parse("GD -125000 VND " + "x".repeat(8001)))
    }
    @Test fun multipleAmountsAndMixedDirectionsRequireManualReview() {
        val ambiguous = parse("GD -100.000 VND, phí -2.000 VND")!!
        assertNull(ambiguous.amount)
        assertEquals(BankDirection.Unknown, parse("GD -100.000 VND và +100.000 VND")!!.direction)
        assertEquals(BankDirection.Unknown, parse("Giao dịch 100.000 VND")!!.direction)
    }
    @Test fun wholeVndGroupingDoesNotSilentlyTurnDecimalsIntoLargerAmounts() {
        mapOf("125000" to 125000L, "1.234.567" to 1234567L, "1,234,567" to 1234567L,
            "125,000.00" to 125000L, "125.000,00" to 125000L).forEach { (text, expected) -> assertEquals(expected, BankNotificationParser.wholeVnd(text)) }
        listOf("125.50", "1,2,3", "0", "1000000000000", "12.345,67").forEach { assertNull(BankNotificationParser.wholeVnd(it)) }
    }
    @Test fun stableEventIdentityDoesNotRetainRawAccountOtpOrReference() {
        val a = parse("TK 987654321 GD -125.000 VND ma GD REF123456", "one")!!
        val b = parse("TK 987654321 GD -125.000 VND ma GD REF123456", "updated")!!
        assertEquals(a.id, b.id)
        assertFalse(a.toString().contains("987654321")); assertFalse(a.toString().contains("ref123456"))
        assertEquals(candidate.id, candidate.id)
        assertNotEquals(parse("GD -125000 VND", eventTime = time)!!.id, parse("GD -125000 VND", eventTime = time + 1000)!!.id)
    }
    @Test fun expenseAndIncomeChangeTotalsOnlyAfterExplicitRecording() {
        val expense = recordCandidate(candidate, ledger, review(), today)
        assertEquals(875000L, walletBalance(expense.personal.wallets.first(), expense.personal.entries))
        assertEquals(1_000_000L, walletBalance(ledger.wallets.first(), ledger.entries))
        val income = recordCandidate(candidate.copy(direction = BankDirection.Incoming), ledger, review(BankRecordKind.Income), today)
        assertEquals(1_125_000L, personalNetWorth(income.personal.wallets, income.personal.entries))
        assertTrue(runCatching { recordCandidate(candidate, expense.personal, review(), today) }.isFailure)
        assertTrue(runCatching { recordCandidate(candidate.copy(status = CaptureStatus.Recorded), ledger, review(), today) }.isFailure)
    }
    @Test fun ownWalletTransferConservesNetWorthAndCreatesNoExpense() {
        val transfer = recordCandidate(candidate, ledger, review(BankRecordKind.Transfer), today)
        assertEquals(1_000_000L, personalNetWorth(transfer.personal.wallets, transfer.personal.entries))
        assertEquals(0, transfer.personal.entries.count { it.type.isTransaction })
        assertEquals(2, transfer.entryIds.size)
        assertEquals(125000L, walletBalance(transfer.personal.wallets.last(), transfer.personal.entries))
        assertTrue(runCatching { recordCandidate(candidate, ledger, review(BankRecordKind.Transfer).copy(amount = "2000000"), today) }.isFailure)
    }
    @Test fun linkingRequiresMatchingAmountWalletDateAndDirection() {
        val recorded = recordCandidate(candidate, ledger, review(), today)
        val otherAlert = candidate.copy(id = "a".repeat(64))
        val link = review(BankRecordKind.Link).copy(linkedId = recorded.entryIds.single())
        assertEquals(recorded.personal, recordCandidate(otherAlert, recorded.personal, link, today).personal)
        assertTrue(runCatching { recordCandidate(otherAlert, recorded.personal, link.copy(amount = "999"), today) }.isFailure)
        assertTrue(runCatching { recordCandidate(otherAlert.copy(direction = BankDirection.Incoming), recorded.personal, link, today) }.isFailure)
        assertTrue(runCatching { recordCandidate(otherAlert, recorded.personal, review().copy(date = today.plusDays(1).toString()), today) }.isFailure)
    }
    private class Memory(var data: BankData) : BankStore {
        var fail = false
        var entered: CompletableDeferred<Unit>? = null
        var release: CompletableDeferred<Unit>? = null
        override suspend fun load() = data
        override suspend fun edit(change: (BankData) -> BankData): BankData {
            entered?.complete(Unit); release?.await()
            if (fail) error("disk")
            return change(data).also { data = it }
        }
    }
    @Test fun failedRecordDoesNotMutateWalletsAndRetryIsIdempotent() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val finance = FinanceViewModel(); finance.restorePersonal(ledger)
        val store = Memory(BankData(listOf(candidate)))
        val controller = BankController("qa", store, finance, scope)
        try {
            controller.open(); val before = finance.uiState.value
            store.fail = true; assertNotNull(controller.confirm(candidate.id, review()))
            assertEquals(before, finance.uiState.value)
            store.fail = false; assertNull(controller.confirm(candidate.id, review()))
            assertNotNull(controller.confirm(candidate.id, review()))
            assertEquals(1, finance.uiState.value.entries.count { it.type == EntryType.EXPENSE })
            assertEquals(before.groups, finance.uiState.value.groups)
            assertEquals(before.groupExpenses, finance.uiState.value.groupExpenses)
        } finally { controller.close(); scope.cancel() }
    }
    @Test fun nativeEditDeleteAndUndoPersistWithoutReimportingTheAlert() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val finance = FinanceViewModel(); finance.restorePersonal(ledger)
        val store = Memory(BankData(listOf(candidate)))
        val controller = BankController("qa", store, finance, scope)
        try {
            controller.open(); assertNull(controller.confirm(candidate.id, review()))
            val entry = finance.uiState.value.entries.last()
            assertNull(finance.saveTransaction(entry.id, "100000", "bank", EntryType.EXPENSE, "Food", today.toString(), "Edited"))
            assertEquals(100000L, store.data.personal!!.entries.last().amount)
            finance.deleteTransaction(entry.id)
            assertFalse(store.data.personal!!.entries.any { it.id == entry.id })
            finance.restoreTransaction(entry.copy(amount = 100000))
            assertEquals(1, store.data.personal!!.entries.count { it.id == entry.id })
            assertEquals(CaptureStatus.Recorded, store.data.candidates.single().status)
        } finally { controller.close(); scope.cancel() }
    }
    @Test fun walletSaveFailureRollsBackAndKeepsARetryableDraft() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val finance = FinanceViewModel(); finance.restorePersonal(ledger)
        val store = Memory(BankData(listOf(candidate), ledger))
        val controller = BankController("qa", store, finance, scope)
        try {
            controller.open(); store.fail = true
            finance.saveTransaction(null, "100000", "bank", EntryType.EXPENSE, "Food", today.toString(), "Draft")
            assertEquals(ledger, finance.uiState.value.personalLedger())
            assertNotNull(controller.state.value.unsaved)
            store.fail = false; assertNull(controller.retryPersonal())
            assertEquals(1, finance.uiState.value.entries.count { it.type == EntryType.EXPENSE })
        } finally { controller.close(); scope.cancel() }
    }
    @Test fun ignoredAlertsCanReturnToInboxWithoutChangingFinancialData() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val finance = FinanceViewModel(); val store = Memory(BankData(listOf(candidate)))
        val controller = BankController("qa", store, finance, scope)
        try {
            controller.open(); val before = finance.uiState.value
            assertNull(controller.ignore(candidate.id)); assertEquals(CaptureStatus.Ignored, store.data.candidates.single().status)
            assertNull(controller.reopenIgnored(candidate.id)); assertEquals(CaptureStatus.Pending, store.data.candidates.single().status)
            assertEquals(before, finance.uiState.value)
        } finally { controller.close(); scope.cancel() }
    }
}
