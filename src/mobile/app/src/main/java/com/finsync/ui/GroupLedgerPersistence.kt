package com.finsync.ui

import com.finsync.data.GroupLedger
import com.finsync.data.GroupLedgerStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class GroupStorageState(
    val workspace: String? = null,
    val loading: Boolean = false,
    val loadFailed: Boolean = false,
    val saving: Boolean = false,
)

/** Serializes writes and rolls back only group data if an atomic disk write fails. */
class GroupLedgerPersistence(
    private val scope: CoroutineScope,
    private val read: () -> FinanceState,
    private val replace: (GroupLedger) -> Unit,
) {
    private val mutableState = MutableStateFlow(GroupStorageState())
    val state = mutableState.asStateFlow()
    private var store: GroupLedgerStore? = null
    private var generation = 0
    private var loading: Job? = null
    private val mutex = Mutex()

    fun bind(workspace: String, storage: GroupLedgerStore) {
        if (workspace == state.value.workspace) return
        store = storage
        load(workspace, storage)
    }

    fun retry() { store?.let { load(state.value.workspace ?: return, it) } }

    private fun load(workspace: String, storage: GroupLedgerStore) {
        val epoch = ++generation
        loading?.cancel()
        replace(GroupLedger())
        mutableState.value = GroupStorageState(workspace, loading = true)
        loading = scope.launch {
            try {
                val ledger = mutex.withLock { storage.load() }
                if (epoch == generation) {
                    replace(ledger)
                    mutableState.value = GroupStorageState(workspace)
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) {
                if (epoch == generation) mutableState.value = GroupStorageState(workspace, loadFailed = true)
            }
        }
    }

    suspend fun update(action: () -> String?): String? = mutex.withLock {
        val storage = store ?: return@withLock action()
        if (state.value.loading || state.value.loadFailed) return@withLock "Open Groups and reload your saved data first."
        val epoch = generation
        val before = read().groupLedger()
        action()?.let { return@withLock it }
        val after = read().groupLedger()
        mutableState.value = state.value.copy(saving = true)
        // Complete the atomic write even when a form is dismissed or its owner signs out.
        withContext(NonCancellable) {
            try {
                storage.save(after)
                if (epoch == generation) null else "Your workspace changed. Open Groups again."
            } catch (_: Exception) {
                if (epoch == generation) replace(before)
                "Couldn't save on this device. Your changes weren't recorded. Try again."
            } finally {
                if (epoch == generation) mutableState.value = state.value.copy(saving = false)
            }
        }
    }
}

internal fun FinanceState.groupLedger() = GroupLedger(groups, groupExpenses, settlements)
