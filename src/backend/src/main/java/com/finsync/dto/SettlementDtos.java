package com.finsync.dto;

import com.finsync.entity.enums.CurrencyCode;
import com.finsync.entity.enums.GroupMemberStatus;
import com.finsync.entity.enums.SettlementStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class SettlementDtos {
    private SettlementDtos() {}

    public record MemberBalanceItem(
            UUID memberId,
            String displayName,
            GroupMemberStatus status,
            boolean guest,
            String netBalance
    ) {}

    public record SimplifiedTransferItem(
            UUID fromMemberId,
            String fromDisplayName,
            UUID toMemberId,
            String toDisplayName,
            String amount
    ) {}

    public record GroupDebtSummaryResponse(
            UUID groupId,
            CurrencyCode currencyCode,
            List<MemberBalanceItem> memberBalances,
            List<SimplifiedTransferItem> simplifiedTransfers,
            int rawPairwiseTransferCount,
            int simplifiedTransferCount,
            double reductionPercentage
    ) {}

    public record CreateSettlementRequest(
            @NotNull UUID requestId,
            @NotNull UUID payerMemberId,
            @NotNull UUID receiverMemberId,
            @NotNull @Positive BigDecimal amount,
            @NotNull LocalDate settlementDate,
            @Size(max = 2000) String note
    ) {}

    public record SettlementResponse(
            UUID id,
            UUID groupId,
            UUID payerMemberId,
            String payerDisplayName,
            UUID receiverMemberId,
            String receiverDisplayName,
            UUID recordedByMemberId,
            String amount,
            CurrencyCode currencyCode,
            LocalDate settlementDate,
            String note,
            SettlementStatus status,
            UUID requestId,
            Instant createdAt
    ) {}
}
