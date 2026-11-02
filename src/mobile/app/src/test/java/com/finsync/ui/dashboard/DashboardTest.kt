package com.finsync.ui.dashboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardTest {
    @Test fun remainingBudgetDistinguishesAvailableAndOverspentFunds() {
        assertEquals(300_000L, budgetRemaining(1_200_000, 1_500_000))
        assertEquals(-200_000L, budgetRemaining(1_700_000, 1_500_000))
        assertEquals(0L, budgetRemaining(1_500_000, 1_500_000))
        assertEquals(0L, budgetRemaining(0, 0))
        assertEquals(-Long.MAX_VALUE, budgetRemaining(Long.MAX_VALUE, 0))
    }

    @Test fun vndFormattingUsesVietnameseSeparatorsWithoutCents() {
        assertEquals("1.250.000 ₫", formatVnd(1_250_000))
        assertEquals("-65.000 ₫", formatVnd(-65_000))
        assertEquals("0 ₫", formatVnd(0))
    }

    @Test fun budgetProgressHandlesZeroLimitAndOverspending() {
        assertEquals(0f, budgetProgress(100, 0), 0f)
        assertEquals(1f, budgetProgress(200, 100), 0f)
        assertEquals(0f, budgetProgress(-100, 100), 0f)
        assertEquals(0.8f, budgetProgress(1_200_000, 1_500_000), 0.001f)
    }

    @Test fun fixtureKeepsPersonalAndGroupLedgersSeparate() {
        val data = DemoDashboard.data
        assertEquals(12_450_000L, data.personal.balance)
        assertEquals(350_000L, data.group.receivable)
        assertEquals(120_000L, data.group.payable)
        assertTrue(data.transactions.map { it.id }.distinct().size == data.transactions.size)
        assertEquals(data.personal.income, data.transactions.filter { it.signedAmount > 0 }.sumOf { it.signedAmount })
    }
}
