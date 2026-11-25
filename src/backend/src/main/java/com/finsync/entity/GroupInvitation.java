package com.finsync.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Expiring, revocable group invitation (storing only SHA-256 token hash). */
@Entity
@Table(name = "group_invitations",
        uniqueConstraints = @UniqueConstraint(name = "uk_group_invitations_token_hash", columnNames = "token_hash"),
        indexes = {
                @Index(name = "idx_group_invitations_group", columnList = "group_id"),
                @Index(name = "idx_group_invitations_created_by", columnList = "created_by_member_id"),
                @Index(name = "idx_group_invitations_target_member", columnList = "target_member_id")
        })
@Getter
@Setter
@NoArgsConstructor
public class GroupInvitation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private ExpenseGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_member_id", nullable = false)
    private GroupMember createdByMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id")
    private GroupMember targetMember;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "max_uses", nullable = false)
    private Integer maxUses = 1;

    @Column(name = "use_count", nullable = false)
    private Integer useCount = 0;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    public boolean isUsableAt(Instant now) {
        return revokedAt == null && expiresAt.isAfter(now) && useCount < maxUses;
    }
}
