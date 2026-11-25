package com.finsync.entity;

import com.finsync.entity.enums.GroupFundEntryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Append-only shared group fund ledger entry (CONTRIBUTION, REFUND, or EXPENSE). */
@Entity
@Table(name = "group_fund_entries",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_group_fund_entries_expense_id", columnNames = "expense_id"),
                @UniqueConstraint(name = "uk_group_fund_entries_request_id", columnNames = "request_id")
        },
        indexes = {
                @Index(name = "idx_group_fund_entries_wallet_date", columnList = "group_wallet_id, entry_date"),
                @Index(name = "idx_group_fund_entries_member", columnList = "member_id"),
                @Index(name = "idx_group_fund_entries_recorded_by", columnList = "recorded_by_member_id")
        })
@Getter
@Setter
@NoArgsConstructor
public class GroupFundEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_wallet_id", nullable = false)
    private GroupWallet groupWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private GroupMember member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    private GroupExpense expense;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recorded_by_member_id", nullable = false)
    private GroupMember recordedByMember;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GroupFundEntryType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(length = 2000)
    private String note;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;
}
