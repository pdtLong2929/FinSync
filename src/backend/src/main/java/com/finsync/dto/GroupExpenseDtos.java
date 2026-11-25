package com.finsync.dto;

import com.finsync.entity.enums.CurrencyCode;
import com.finsync.entity.enums.FundingSource;
import com.finsync.entity.enums.GroupFundEntryType;
import com.finsync.entity.enums.SplitMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class GroupExpenseDtos {
    private GroupExpenseDtos() {}

    public record CreateFundEntryRequest(
            @NotNull UUID requestId,
            @NotNull GroupFundEntryType type,
            @NotNull UUID memberId,
            @NotNull @Positive BigDecimal amount,
            @NotNull LocalDate entryDate,
            @Size(max = 2000) String note
    ) {}

    public record GroupFundEntryResponse(
            UUID id,
            UUID groupWalletId,
            UUID memberId,
            String memberDisplayName,
            UUID expenseId,
            UUID recordedByMemberId,
            GroupFundEntryType type,
            String amount,
            LocalDate entryDate,
            String note,
            UUID requestId,
            Instant createdAt
    ) {}

    public record GroupFundOverviewResponse(
            UUID walletId,
            UUID groupId,
            String walletName,
            CurrencyCode currencyCode,
            String balance,
            List<GroupFundEntryResponse> entries
    ) {}

    public record GroupExpenseSplitInput(
            @NotNull UUID memberId,
            BigDecimal inputValue
    ) {}

    public record CreateGroupExpenseRequest(
            @NotNull UUID requestId,
            @NotNull UUID categoryId,
            UUID paidByMemberId,
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String note,
            @NotNull @Positive BigDecimal amount,
            @NotNull LocalDate expenseDate,
            @NotNull FundingSource fundingSource,
            @NotNull SplitMode splitMode,
            @NotEmpty @Size(max = 50) List<@Valid GroupExpenseSplitInput> splits
    ) {}

    public record GroupExpenseSplitResponse(
            UUID id,
            UUID memberId,
            String memberDisplayName,
            String amount,
            String inputValue,
            int splitOrder
    ) {}

    public record GroupExpenseResponse(
            UUID id,
            UUID groupId,
            UUID categoryId,
            String categoryName,
            UUID createdByMemberId,
            UUID paidByMemberId,
            String paidByDisplayName,
            String title,
            String note,
            String amount,
            CurrencyCode currencyCode,
            LocalDate expenseDate,
            FundingSource fundingSource,
            SplitMode splitMode,
            List<GroupExpenseSplitResponse> splits,
            UUID requestId,
            Instant createdAt
    ) {}

    public record GroupExpensePageResponse(
            List<GroupExpenseResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}
}
