package com.finsync.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Shared group fund container (1:1 per group; balance computed from group_fund_entries). */
@Entity
@Table(name = "group_wallets",
        uniqueConstraints = @UniqueConstraint(name = "uk_group_wallets_group_id", columnNames = "group_id"))
@Getter
@Setter
@NoArgsConstructor
public class GroupWallet extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private ExpenseGroup group;

    @Column(nullable = false, length = 100)
    private String name = "Quỹ chung";
}
