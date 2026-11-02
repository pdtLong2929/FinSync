package com.finsync

import com.finsync.data.DemoFixtures
import com.finsync.domain.*
import com.finsync.ui.FinanceViewModel
import com.finsync.ui.dashboard.toDashboardData
import org.junit.Assert.*
import org.junit.Test

class FrontendConsistencyTest {
    @Test fun addEditDeleteUndoRecalculateAllPersonalViewsWithoutDuplicateRestore() {
        val model = FinanceViewModel()
        val today = DemoFixtures.today
        val original = model.uiState.value
        val originalExpense = original.toDashboardData().personal.expenses
        fun verify(extra: Long) {
            val state = model.uiState.value
            assertEquals(originalExpense + extra, state.toDashboardData().personal.expenses)
            assertEquals(originalExpense + extra, monthlyReport(state.entries, java.time.YearMonth.from(today)).expenses)
            assertEquals(originalExpense + extra, analyticsSummary(state.entries, state.categories,
                today.withDayOfMonth(1), today, null, null, null).expenses)
        }
        assertNull(model.saveTransaction(null, "101", "cash", EntryType.EXPENSE, "Food", today.toString(), "Consistency check"))
        val added = model.uiState.value.entries.last()
        verify(101)
        assertNull(model.saveTransaction(added.id, "203", "bank", EntryType.EXPENSE, "Food", today.toString(), "Updated check"))
        val edited = model.uiState.value.entries.first { it.id == added.id }
        verify(203)
        model.deleteTransaction(edited.id)
        verify(0)
        model.restoreTransaction(edited)
        model.restoreTransaction(edited)
        verify(203)
        assertEquals(listOf(edited), model.uiState.value.entries.filter { it.id == edited.id })
        assertEquals(original.groupExpenses, model.uiState.value.groupExpenses)
        assertEquals(original.settlements, model.uiState.value.settlements)
    }
}
