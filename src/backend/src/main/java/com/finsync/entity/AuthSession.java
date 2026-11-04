package com.finsync.entity;

import com.finsync.entity.enums.ClientType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Stores only the refresh token hash; access-token denylisting belongs in Redis. */
@Entity
@Table(name = "auth_sessions",
        uniqueConstraints = @UniqueConstraint(name = "uk_auth_sessions_refresh_token_hash",
                columnNames = "refresh_token_hash"),
        indexes = {
                @Index(name = "idx_auth_sessions_user_revoked", columnList = "user_id, revoked_at"),
                @Index(name = "idx_auth_sessions_expires_at", columnList = "expires_at")
        })
@Getter
@Setter
@NoArgsConstructor
public class AuthSession extends BaseEntity {

    // No cascade: session operations must not create or delete the account.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_auth_sessions_user"))
    private User user;

    @Column(name = "refresh_token_hash", nullable = false, length = 64)
    private String refreshTokenHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false, length = 16)
    private ClientType clientType = ClientType.MOBILE;

    @Column(name = "device_label", length = 200)
    private String deviceLabel;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;
}
