package com.finsync.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record WalletCreateRequest(
        @NotBlank(message = "Wallet name is required")
        @Size(max = 100, message = "Wallet name cannot exceed 100 characters")
        String name,

        @NotBlank(message = "Wallet type is required (CASH, BANK, CREDIT)")
        String type,

        String currency,

        @PositiveOrZero(message = "Initial balance cannot be negative")
        BigDecimal initialBalance
) {}
