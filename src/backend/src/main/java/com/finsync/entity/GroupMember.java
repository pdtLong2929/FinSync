package com.finsync.entity;

import com.finsync.entity.enums.GroupMemberRole;
import com.finsync.entity.enums.GroupMemberStatus;
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

import java.time.Instant;

/** Group membership association, supporting both registered users and unlinked guest participants. */
@Entity
@Table(name = "group_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_group_members_group_user", columnNames = {"group_id", "user_id"}),
                @UniqueConstraint(name = "uk_group_members_group_order", columnNames = {"group_id", "member_order"})
        },
        indexes = @Index(name = "idx_group_members_user", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class GroupMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private ExpenseGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GroupMemberRole role = GroupMemberRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GroupMemberStatus status = GroupMemberStatus.ACTIVE;

    @Column(name = "member_order", nullable = false)
    private Integer memberOrder;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();

    @Column(name = "left_at")
    private Instant leftAt;

    public boolean isActive() {
        return status == GroupMemberStatus.ACTIVE;
    }

    public boolean isOwner() {
        return role == GroupMemberRole.OWNER && status == GroupMemberStatus.ACTIVE;
    }

    public boolean isGuest() {
        return user == null;
    }
}
