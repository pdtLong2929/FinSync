CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(30),
    avatar_key VARCHAR(512),
    default_currency VARCHAR(3) NOT NULL DEFAULT 'VND',
    time_zone VARCHAR(64) NOT NULL DEFAULT 'Asia/Ho_Chi_Minh',
    role VARCHAR(16) NOT NULL DEFAULT 'USER',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    locked_at TIMESTAMPTZ,
    lock_reason VARCHAR(500),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_email_normalized CHECK (email = lower(btrim(email)) AND email <> ''),
    CONSTRAINT ck_users_display_name CHECK (btrim(display_name) <> ''),
    CONSTRAINT ck_users_currency CHECK (default_currency IN ('VND', 'USD')),
    CONSTRAINT ck_users_role CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'LOCKED')),
    CONSTRAINT ck_users_lock_state CHECK (
        (status = 'ACTIVE' AND locked_at IS NULL AND lock_reason IS NULL)
        OR (status = 'LOCKED' AND locked_at IS NOT NULL
            AND lock_reason IS NOT NULL AND btrim(lock_reason) <> '')
    )
);

CREATE INDEX idx_users_status_created_at ON users (status, created_at);

CREATE TABLE auth_sessions (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    user_id UUID NOT NULL,
    refresh_token_hash VARCHAR(64) NOT NULL,
    client_type VARCHAR(16) NOT NULL,
    device_label VARCHAR(200),
    expires_at TIMESTAMPTZ NOT NULL,
    last_used_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    CONSTRAINT fk_auth_sessions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT uk_auth_sessions_refresh_token_hash UNIQUE (refresh_token_hash),
    CONSTRAINT ck_auth_sessions_client_type CHECK (client_type IN ('MOBILE', 'ADMIN_WEB')),
    CONSTRAINT ck_auth_sessions_expiry CHECK (expires_at > created_at)
);

CREATE INDEX idx_auth_sessions_user_revoked ON auth_sessions (user_id, revoked_at);
CREATE INDEX idx_auth_sessions_expires_at ON auth_sessions (expires_at);
