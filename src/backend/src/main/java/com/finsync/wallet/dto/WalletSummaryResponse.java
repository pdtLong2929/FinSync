package com.finsync.wallet.dto;

import java.math.BigDecimal;
import java.util.List;

public record WalletSummaryResponse(
        BigDecimal netWorth,
        BigDecimal totalAssets,
        BigDecimal totalDebts,
        List<WalletResponse> wallets
) {}
