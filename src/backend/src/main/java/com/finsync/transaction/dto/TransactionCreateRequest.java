package com.finsync.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionCreateRequest(
        @NotNull(message = "Source wallet ID is required")
        UUID walletId,

        UUID destinationWalletId, // Required only if direction is TRANSFER

        @NotNull(message = "Category ID is required")
        UUID categoryId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Direction is required (INCOME, EXPENSE, TRANSFER)")
        String direction,

        Instant transactionDate,

        @Size(max = 1000, message = "Note cannot exceed 1000 characters")
        String note,

        @Size(max = 500, message = "Receipt URL cannot exceed 500 characters")
        String receiptImageUrl
) {}
