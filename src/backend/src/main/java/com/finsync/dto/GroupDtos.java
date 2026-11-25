package com.finsync.dto;

import com.finsync.entity.enums.CurrencyCode;
import com.finsync.entity.enums.GroupMemberRole;
import com.finsync.entity.enums.GroupMemberStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class GroupDtos {
    private GroupDtos() {}

    public record CreateGroupRequest(
            @NotBlank @Size(max = 100) String name,
            @Size(max = 1000) String description,
            @NotNull CurrencyCode currencyCode,
            @Size(max = 100) String fundName,
            @Size(max = 49) List<@NotBlank @Size(max = 100) String> guestMemberNames
    ) {}

    public record UpdateGroupRequest(
            @Size(max = 100) String name,
            @Size(max = 1000) String description,
            CurrencyCode currencyCode
    ) {}

    public record GroupSummaryResponse(
            UUID id,
            String name,
            String description,
            CurrencyCode currencyCode,
            boolean archived,
            Instant archivedAt,
            int activeMemberCount,
            UUID callerMemberId,
            GroupMemberRole callerRole,
            String callerNetBalance,
            String fundBalance,
            Instant createdAt
    ) {}

    public record GroupMemberResponse(
            UUID id,
            UUID userId,
            String displayName,
            GroupMemberRole role,
            GroupMemberStatus status,
            boolean guest,
            int memberOrder,
            String netBalance,
            Instant joinedAt,
            Instant leftAt
    ) {}

    public record GroupDetailResponse(
            UUID id,
            String name,
            String description,
            CurrencyCode currencyCode,
            boolean archived,
            Instant archivedAt,
            long version,
            UUID walletId,
            String walletName,
            String fundBalance,
            List<GroupMemberResponse> members,
            Instant createdAt
    ) {}

    public record AddGuestMemberRequest(
            @NotBlank @Size(max = 100) String displayName
    ) {}

    public record TransferOwnershipRequest(
            @NotNull UUID newOwnerMemberId
    ) {}

    public record CreateInvitationRequest(
            UUID targetMemberId,
            @Min(1) @Max(50) Integer maxUses,
            @Min(1) @Max(720) Integer expiresInHours
    ) {}

    public record GroupInvitationResponse(
            UUID id,
            UUID groupId,
            UUID createdByMemberId,
            UUID targetMemberId,
            int maxUses,
            int useCount,
            Instant expiresAt,
            Instant revokedAt,
            boolean active
    ) {}

    public record CreateInvitationResponse(
            GroupInvitationResponse invitation,
            String token
    ) {}

    public record AcceptInvitationRequest(
            @NotBlank @Size(min = 8, max = 128) String token
    ) {}
}
