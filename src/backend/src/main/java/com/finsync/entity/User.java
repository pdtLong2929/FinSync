package com.finsync.entity;

import com.finsync.entity.enums.CurrencyCode;
import com.finsync.entity.enums.UserRole;
import com.finsync.entity.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Locale;

/** Account persistence model. API responses must use DTOs rather than this entity. */
@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
        indexes = @Index(name = "idx_users_status_created_at", columnList = "status, created_at"))
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "avatar_key", length = 512)
    private String avatarKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_currency", nullable = false, length = 3)
    private CurrencyCode defaultCurrency = CurrencyCode.VND;

    @Column(name = "time_zone", nullable = false, length = 64)
    private String timeZone = "Asia/Ho_Chi_Minh";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "locked_at")
    private Instant lockedAt;

    @Column(name = "lock_reason", length = 500)
    private String lockReason;

    @PrePersist
    @PreUpdate
    private void normalizeEmail() {
        if (email != null) {
            email = email.trim().toLowerCase(Locale.ROOT);
        }
    }
}
