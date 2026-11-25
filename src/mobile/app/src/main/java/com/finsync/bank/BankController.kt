package com.finsync.bank

import androidx.compose.runtime.staticCompositionLocalOf
import com.finsync.ui.FinanceViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

data class BankUiState(val data: BankData = BankData(), val loading: Boolean = true, val loadFailed: Boolean = false,
    val busy: Boolean = false, val error: String? = null, val unsaved: PersonalLedger? = null)
class BankController(val workspace: String, val store: BankStore, private val finance: FinanceViewModel,
    private val scope: CoroutineScope) {
    private val mutable = MutableStateFlow(BankUiState())
    val state = mutable.asStateFlow()
    val financeState get() = finance.uiState
    private val serial = Mutex()
    private var active = true
    private var applying = false
    private var revision = 0
    suspend fun open() = serial.withLock {
        mutable.value = state.value.copy(loading = true, loadFailed = false, error = null)
        try {
            val saved = store.load()
            if (!active) return@withLock
            finance.selectPersonalWorkspace(workspace)
            saved.personal?.let(::applyPersonal)
            mutable.value = BankUiState(saved, loading = false)
            finance.observePersonal(this, ::personalChanged)
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { if (active) mutable.value = state.value.copy(loading = false, loadFailed = true, error = "Couldn't open saved bank alerts and wallet data. Retry without resetting your data.") }
    }
    fun observeStore(data: BankData?) { if (active && data != null && !state.value.loading && !state.value.loadFailed) mutable.value = state.value.copy(data = data) }
    private fun applyPersonal(ledger: PersonalLedger) {
        if (!active) return
        applying = true
        try { finance.restorePersonal(ledger); finance.durablePersonal = true }
        finally { applying = false }
    }
    private fun personalChanged(snapshot: PersonalLedger) {
        if (!active || applying || state.value.loading || state.value.loadFailed || state.value.data.personal == null) return
        val request = ++revision
        scope.launch { serial.withLock {
            if (!active || request != revision) return@withLock
            try {
                val saved = withContext(NonCancellable) { store.edit { it.copy(personal = snapshot) } }
                if (active) mutable.value = state.value.copy(data = saved)
            } catch (_: Exception) {
                if (active && request == revision) {
                    state.value.data.personal?.let(::applyPersonal)
                    mutable.value = state.value.copy(error = "Wallet changes weren't saved. Your last saved ledger has been restored.", unsaved = snapshot)
                }
            }
        } }
    }
    suspend fun retryPersonal(): String? = serial.withLock {
        val draft = state.value.unsaved ?: return@withLock null
        mutable.value = state.value.copy(busy = true)
        try {
            val saved = withContext(NonCancellable) { store.edit { it.copy(personal = draft) } }
            if (active) { revision++; applyPersonal(draft); mutable.value = state.value.copy(data = saved, unsaved = null, error = null) }
            null
        } catch (_: Exception) { "Couldn't save wallet changes. Try again." }
        finally { if (active) mutable.value = state.value.copy(busy = false) }
    }
    fun discardUnsaved() { if (!state.value.busy) mutable.value = state.value.copy(unsaved = null, error = null) }
    suspend fun confirm(id: String, review: BankReview): String? = serial.withLock {
        if (!active || state.value.loading || state.value.loadFailed || state.value.busy) return@withLock "Reload bank alerts before saving."
        mutable.value = state.value.copy(busy = true)
        try {
            val current = finance.uiState.value.personalLedger()
            val saved = withContext(NonCancellable) { store.edit { data ->
                val candidate = data.candidates.find { it.id == id } ?: throw IllegalArgumentException("This alert is no longer available.")
                val recording = recordCandidate(candidate, current, review, LocalDate.now())
                data.copy(personal = recording.personal, candidates = data.candidates.map {
                    if (it.id == id) it.copy(status = if (review.kind == BankRecordKind.Link) CaptureStatus.Linked else CaptureStatus.Recorded, entryIds = recording.entryIds) else it
                })
            } }
            if (active) { revision++; saved.personal?.let(::applyPersonal); mutable.value = state.value.copy(data = saved, busy = false) }
            null
        } catch (invalid: IllegalArgumentException) { invalid.message ?: "Check the transaction details." }
          catch (_: Exception) { "Couldn't save the transaction. No changes were recorded. Try again." }
        finally { if (active) mutable.value = state.value.copy(busy = false) }
    }
    suspend fun ignore(id: String): String? = serial.withLock {
        if (!active || state.value.loading || state.value.loadFailed) return@withLock "Reload bank alerts first."
        try {
            val next = withContext(NonCancellable) { store.edit { data ->
                require(data.candidates.any { it.id == id && it.status == CaptureStatus.Pending }) { "This alert has already been handled." }
                data.copy(candidates = data.candidates.map {
                if (it.id == id && it.status == CaptureStatus.Pending) it.copy(status = CaptureStatus.Ignored) else it
            }) } }
            if (active) mutable.value = state.value.copy(data = next)
            null
        } catch (invalid: IllegalArgumentException) { invalid.message }
          catch (_: Exception) { "Couldn't ignore this alert. Try again." }
    }
    suspend fun reopenIgnored(id: String): String? = serial.withLock {
        try {
            val next = store.edit { data ->
                require(data.candidates.any { it.id == id && it.status == CaptureStatus.Ignored }) { "Only ignored alerts can return to review." }
                data.copy(candidates = data.candidates.map { if (it.id == id) it.copy(status = CaptureStatus.Pending) else it })
            }
            if (active) mutable.value = state.value.copy(data = next)
            null
        } catch (_: Exception) { "Couldn't restore this alert. Try again." }
    }
    fun close() { active = false; finance.stopObservingPersonal(this) }
}
val LocalBankController = staticCompositionLocalOf<BankController?> { null }
