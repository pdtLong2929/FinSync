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

import java.math.BigDecimal;

/** Individual participant allocation row belonging to a GroupExpense. */
@Entity
@Table(name = "group_expense_splits",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_group_expense_splits_expense_member", columnNames = {"expense_id", "member_id"}),
                @UniqueConstraint(name = "uk_group_expense_splits_expense_order", columnNames = {"expense_id", "split_order"})
        },
        indexes = @Index(name = "idx_group_expense_splits_member", columnList = "member_id"))
@Getter
@Setter
@NoArgsConstructor
public class GroupExpenseSplit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private GroupExpense expense;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private GroupMember member;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "input_value", precision = 19, scale = 2)
    private BigDecimal inputValue;

    @Column(name = "split_order", nullable = false)
    private Integer splitOrder;
}
