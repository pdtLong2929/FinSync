package com.finsync.entity;

import com.finsync.entity.enums.FundingSource;
import com.finsync.entity.enums.SplitMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Append-only shared group bill paid by one member (MEMBERS) or the shared fund (GROUP_FUND). */
@Entity
@Table(name = "group_expenses",
        uniqueConstraints = @UniqueConstraint(name = "uk_group_expenses_request_id", columnNames = "request_id"),
        indexes = {
                @Index(name = "idx_group_expenses_group_date", columnList = "group_id, expense_date"),
                @Index(name = "idx_group_expenses_category", columnList = "category_id"),
                @Index(name = "idx_group_expenses_created_by", columnList = "created_by_member_id"),
                @Index(name = "idx_group_expenses_paid_by", columnList = "paid_by_member_id")
        })
@Getter
@Setter
@NoArgsConstructor
public class GroupExpense extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private ExpenseGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_member_id", nullable = false)
    private GroupMember createdByMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_member_id")
    private GroupMember paidByMember;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String note;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "funding_source", nullable = false, length = 16)
    private FundingSource fundingSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "split_mode", nullable = false, length = 16)
    private SplitMode splitMode;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;
}
