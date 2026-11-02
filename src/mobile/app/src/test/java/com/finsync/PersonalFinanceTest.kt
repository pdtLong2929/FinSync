package com.finsync

import com.finsync.domain.*
import com.finsync.ui.FinanceViewModel
import org.junit.Assert.*
import org.junit.Test

class PersonalFinanceTest {
    @Test fun walletCreationAddsOpeningEntryAndRejectsDuplicateNames() {
        val model = FinanceViewModel()
        assertNull(model.saveWallet(null, "Travel", WalletType.CASH, "1500000"))
        val wallet = model.uiState.value.wallets.last()
        assertEquals(1_500_000, walletBalance(wallet, model.uiState.value.entries))
        assertNotNull(model.saveWallet(null, " travel ", WalletType.CASH, "1"))
        assertNotNull(model.saveWallet(null, "", WalletType.CASH, "1"))
    }
    @Test fun editAndArchivePreserveHistoryAndNetWorth() {
        val model = FinanceViewModel()
        val before = model.uiState.value
        assertNull(model.saveWallet("cash", "Daily cash", WalletType.CASH, "0"))
        model.archiveWallet("cash")
        assertEquals(before.entries, model.uiState.value.entries)
        assertEquals(personalNetWorth(before.wallets, before.entries), personalNetWorth(model.uiState.value.wallets, model.uiState.value.entries))
    }
    @Test fun amountParserRejectsFractionAndOverflow() {
        assertNull(parseVnd("1.5")); assertNull(parseVnd("-2")); assertNull(parseVnd("9999999999999"))
        assertEquals(100_000L, parseVnd("100000"))
    }
}
