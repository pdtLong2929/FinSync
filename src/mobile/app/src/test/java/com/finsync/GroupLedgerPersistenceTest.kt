package com.finsync

import com.finsync.data.*
import com.finsync.domain.*
import com.finsync.ui.*
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class GroupLedgerPersistenceTest {
    private class MemoryStore : GroupLedgerStore {
        var ledger = GroupLedger()
        var failSave = false
        var failLoad = false
        var saved: CompletableDeferred<Unit>? = null
        var release: CompletableDeferred<Unit>? = null
        override suspend fun load(): GroupLedger {
            if (failLoad) error("Disk read failure")
            return ledger
        }
        override suspend fun save(ledger: GroupLedger) {
            saved?.complete(Unit)
            release?.await()
            if (failSave) error("Disk full")
            this.ledger = ledger
        }
    }

    private val group = ExpenseGroup("trip", "Trip", "TRIP", "", listOf(GroupMember("me", "You")))

    @Test fun failedDiskWriteRollsBackGroupsWhileKeepingOtherFinanceEdits() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        var state = FinanceState()
        val controller = GroupLedgerPersistence(scope, { state }) { ledger ->
            state = state.copy(groups = ledger.groups, groupExpenses = ledger.expenses, settlements = ledger.settlements)
        }
        try {
            val store = MemoryStore()
            controller.bind("account-a", store)
            assertTrue(state.groups.isEmpty())
            store.failSave = true
            val error = controller.update {
                state = state.copy(groups = listOf(group), recentWalletId = "edited-wallet")
                null
            }
            assertNotNull(error)
            assertTrue(state.groups.isEmpty())
            assertEquals("edited-wallet", state.recentWalletId)
            assertTrue(store.ledger.groups.isEmpty())
            assertFalse(controller.state.value.saving)
            store.failSave = false
            assertNull(controller.update { state = state.copy(groups = listOf(group)); null })
            assertEquals(listOf(group), store.ledger.groups)
        } finally { scope.cancel() }
    }

    @Test fun loadFailureBlocksWritesAndRetryRecoversSavedGroups() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        var state = FinanceState()
        val controller = GroupLedgerPersistence(scope, { state }) { state = state.copy(groups = it.groups) }
        try {
            val store = MemoryStore().apply { failLoad = true; ledger = GroupLedger(listOf(group)) }
            controller.bind("device", store)
            assertTrue(controller.state.value.loadFailed)
            var called = false
            assertNotNull(controller.update { called = true; null })
            assertFalse(called)
            store.failLoad = false
            controller.retry()
            assertFalse(controller.state.value.loadFailed)
            assertEquals(listOf(group), state.groups)
        } finally { scope.cancel() }
    }

    @Test fun changingAccountsDuringSaveKeepsLedgersIsolated() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        var state = FinanceState()
        val controller = GroupLedgerPersistence(scope, { state }) { state = state.copy(groups = it.groups) }
        try {
            val old = MemoryStore().apply { saved = CompletableDeferred(); release = CompletableDeferred() }
            val next = MemoryStore()
            controller.bind("account-a", old)
            val saving = scope.async { controller.update { state = state.copy(groups = listOf(group)); null } }
            old.saved!!.await()
            assertTrue(controller.state.value.saving)
            controller.bind("account-b", next)
            assertTrue(state.groups.isEmpty())
            old.release!!.complete(Unit)
            assertNotNull(saving.await())
            yield()
            assertEquals("account-b", controller.state.value.workspace)
            assertTrue(state.groups.isEmpty())
            assertEquals(listOf(group), old.ledger.groups)
            assertTrue(next.ledger.groups.isEmpty())
        } finally { scope.cancel() }
    }

    @Test fun queuedEditsPersistInOrderWithoutLosingFirstChange() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        var state = FinanceState()
        val controller = GroupLedgerPersistence(scope, { state }) { state = state.copy(groups = it.groups) }
        try {
            val store = MemoryStore().apply { saved = CompletableDeferred(); release = CompletableDeferred() }
            controller.bind("device", store)
            val first = scope.async { controller.update { state = state.copy(groups = listOf(group)); null } }
            store.saved!!.await()
            val second = scope.async { controller.update {
                state = state.copy(groups = state.groups + group.copy(id = "home", name = "Home")); null
            } }
            store.release!!.complete(Unit)
            assertNull(first.await()); assertNull(second.await())
            assertEquals(listOf("trip", "home"), store.ledger.groups.map { it.id })
        } finally { scope.cancel() }
    }
}
