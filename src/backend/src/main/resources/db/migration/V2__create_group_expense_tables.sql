-- Build 2: Group Expense Management & Smart Debt Simplification
-- Aligned with database_design.md (Final v1.0) and PostgreSQL 16.

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(16) NOT NULL,
    icon_key VARCHAR(50) NOT NULL DEFAULT 'category',
    color_key VARCHAR(30) NOT NULL DEFAULT 'violet',
    archived_at TIMESTAMPTZ,
    CONSTRAINT ck_categories_name CHECK (btrim(name) <> ''),
    CONSTRAINT ck_categories_type CHECK (type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT ck_categories_icon_key CHECK (btrim(icon_key) <> ''),
    CONSTRAINT ck_categories_color_key CHECK (btrim(color_key) <> '')
);

CREATE UNIQUE INDEX uk_categories_type_name ON categories (type, lower(name));

CREATE TABLE groups (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    currency_code VARCHAR(3) NOT NULL,
    archived_at TIMESTAMPTZ,
    CONSTRAINT ck_groups_name CHECK (btrim(name) <> ''),
    CONSTRAINT ck_groups_currency CHECK (currency_code IN ('VND', 'USD'))
);

CREATE TABLE group_members (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    group_id UUID NOT NULL,
    user_id UUID,
    display_name VARCHAR(100) NOT NULL,
    role VARCHAR(16) NOT NULL DEFAULT 'MEMBER',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    member_order INTEGER NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMPTZ,
    CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_group_members_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT uk_group_members_group_user UNIQUE (group_id, user_id),
    CONSTRAINT uk_group_members_group_order UNIQUE (group_id, member_order),
    CONSTRAINT ck_group_members_display_name CHECK (btrim(display_name) <> ''),
    CONSTRAINT ck_group_members_role CHECK (role IN ('OWNER', 'MEMBER')),
    CONSTRAINT ck_group_members_status CHECK (status IN ('ACTIVE', 'LEFT')),
    CONSTRAINT ck_group_members_order CHECK (member_order >= 0),
    CONSTRAINT ck_group_members_owner_rules CHECK (role <> 'OWNER' OR (user_id IS NOT NULL AND status = 'ACTIVE')),
    CONSTRAINT ck_group_members_left_state CHECK (
        (status = 'ACTIVE' AND left_at IS NULL)
        OR (status = 'LEFT' AND left_at IS NOT NULL)
    )
);

CREATE INDEX idx_group_members_user ON group_members (user_id);
CREATE UNIQUE INDEX uk_group_members_active_owner ON group_members (group_id)
    WHERE role = 'OWNER' AND status = 'ACTIVE';

CREATE TABLE group_invitations (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    group_id UUID NOT NULL,
    created_by_member_id UUID NOT NULL,
    target_member_id UUID,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    max_uses INTEGER NOT NULL DEFAULT 1,
    use_count INTEGER NOT NULL DEFAULT 0,
    revoked_at TIMESTAMPTZ,
    CONSTRAINT fk_group_invitations_group FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_group_invitations_created_by FOREIGN KEY (created_by_member_id) REFERENCES group_members (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_group_invitations_target_member FOREIGN KEY (target_member_id) REFERENCES group_members (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT uk_group_invitations_token_hash UNIQUE (token_hash),
    CONSTRAINT ck_group_invitations_uses CHECK (max_uses > 0 AND use_count >= 0 AND use_count <= max_uses),
    CONSTRAINT ck_group_invitations_expiry CHECK (expires_at > created_at),
    CONSTRAINT ck_group_invitations_target_single_use CHECK (target_member_id IS NULL OR max_uses = 1)
);

CREATE INDEX idx_group_invitations_group ON group_invitations (group_id);
CREATE INDEX idx_group_invitations_created_by ON group_invitations (created_by_member_id);
CREATE INDEX idx_group_invitations_target_member ON group_invitations (target_member_id);

CREATE TABLE group_wallets (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    group_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL DEFAULT 'Quỹ chung',
    CONSTRAINT fk_group_wallets_group FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT uk_group_wallets_group_id UNIQUE (group_id),
    CONSTRAINT ck_group_wallets_name CHECK (btrim(name) <> '')
);

CREATE TABLE group_expenses (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    group_id UUID NOT NULL,
    category_id UUID NOT NULL,
    created_by_member_id UUID NOT NULL,
    paid_by_member_id UUID,
    title VARCHAR(200) NOT NULL,
    note VARCHAR(2000),
    amount NUMERIC(19, 2) NOT NULL,
    expense_date DATE NOT NULL,
    funding_source VARCHAR(16) NOT NULL,
    split_mode VARCHAR(16) NOT NULL,
    request_id UUID NOT NULL,
    request_hash VARCHAR(64) NOT NULL,
    CONSTRAINT fk_group_expenses_group FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_group_expenses_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_group_expenses_created_by FOREIGN KEY (created_by_member_id) REFERENCES group_members (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_group_expenses_paid_by FOREIGN KEY (paid_by_member_id) REFERENCES group_members (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT uk_group_expenses_request_id UNIQUE (request_id),
    CONSTRAINT ck_group_expenses_title CHECK (btrim(title) <> ''),
    CONSTRAINT ck_group_expenses_amount CHECK (amount > 0),
    CONSTRAINT ck_group_expenses_funding_source CHECK (funding_source IN ('MEMBERS', 'GROUP_FUND')),
    CONSTRAINT ck_group_expenses_split_mode CHECK (split_mode IN ('EQUAL', 'EXACT', 'PERCENTAGE', 'SHARES')),
    CONSTRAINT ck_group_expenses_payer_match CHECK (
        (funding_source = 'MEMBERS' AND paid_by_member_id IS NOT NULL)
        OR (funding_source = 'GROUP_FUND' AND paid_by_member_id IS NULL)
    )
);

CREATE INDEX idx_group_expenses_group_date ON group_expenses (group_id, expense_date DESC);
CREATE INDEX idx_group_expenses_category ON group_expenses (category_id);
CREATE INDEX idx_group_expenses_created_by ON group_expenses (created_by_member_id);
CREATE INDEX idx_group_expenses_paid_by ON group_expenses (paid_by_member_id);

CREATE TABLE group_fund_entries (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    group_wallet_id UUID NOT NULL,
    member_id UUID,
    expense_id UUID,
    recorded_by_member_id UUID NOT NULL,
    type VARCHAR(16) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    entry_date DATE NOT NULL,
    note VARCHAR(2000),
    request_id UUID NOT NULL,
    request_hash VARCHAR(64) NOT NULL,
    CONSTRAINT fk_group_fund_entries_wallet FOREIGN KEY (group_wallet_id) REFERENCES group_wallets (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_group_fund_entries_member FOREIGN KEY (member_id) REFERENCES group_members (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_group_fund_entries_expense FOREIGN KEY (expense_id) REFERENCES group_expenses (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_group_fund_entries_recorded_by FOREIGN KEY (recorded_by_member_id) REFERENCES group_members (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT uk_group_fund_entries_expense_id UNIQUE (expense_id),
    CONSTRAINT uk_group_fund_entries_request_id UNIQUE (request_id),
    CONSTRAINT ck_group_fund_entries_type CHECK (type IN ('CONTRIBUTION', 'REFUND', 'EXPENSE')),
    CONSTRAINT ck_group_fund_entries_amount CHECK (amount > 0),
    CONSTRAINT ck_group_fund_entries_links CHECK (
        (type IN ('CONTRIBUTION', 'REFUND') AND member_id IS NOT NULL AND expense_id IS NULL)
        OR (type = 'EXPENSE' AND member_id IS NULL AND expense_id IS NOT NULL)
    )
);

CREATE INDEX idx_group_fund_entries_wallet_date ON group_fund_entries (group_wallet_id, entry_date DESC);
CREATE INDEX idx_group_fund_entries_member ON group_fund_entries (member_id);
CREATE INDEX idx_group_fund_entries_recorded_by ON group_fund_entries (recorded_by_member_id);

CREATE TABLE group_expense_splits (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    expense_id UUID NOT NULL,
    member_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    input_value NUMERIC(19, 2),
    split_order INTEGER NOT NULL,
    CONSTRAINT fk_group_expense_splits_expense FOREIGN KEY (expense_id) REFERENCES group_expenses (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_group_expense_splits_member FOREIGN KEY (member_id) REFERENCES group_members (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT uk_group_expense_splits_expense_member UNIQUE (expense_id, member_id),
    CONSTRAINT uk_group_expense_splits_expense_order UNIQUE (expense_id, split_order),
    CONSTRAINT ck_group_expense_splits_amount CHECK (amount >= 0),
    CONSTRAINT ck_group_expense_splits_input CHECK (input_value IS NULL OR input_value >= 0),
    CONSTRAINT ck_group_expense_splits_order CHECK (split_order >= 0)
);

CREATE INDEX idx_group_expense_splits_member ON group_expense_splits (member_id);

CREATE TABLE settlements (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    group_id UUID NOT NULL,
    payer_member_id UUID NOT NULL,
    receiver_member_id UUID NOT NULL,
    recorded_by_member_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    settlement_date DATE NOT NULL,
    note VARCHAR(2000),
    status VARCHAR(16) NOT NULL DEFAULT 'CONFIRMED',
    request_id UUID NOT NULL,
    request_hash VARCHAR(64) NOT NULL,
    CONSTRAINT fk_settlements_group FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_settlements_payer FOREIGN KEY (payer_member_id) REFERENCES group_members (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_settlements_receiver FOREIGN KEY (receiver_member_id) REFERENCES group_members (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_settlements_recorded_by FOREIGN KEY (recorded_by_member_id) REFERENCES group_members (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT uk_settlements_request_id UNIQUE (request_id),
    CONSTRAINT ck_settlements_distinct_parties CHECK (payer_member_id <> receiver_member_id),
    CONSTRAINT ck_settlements_amount CHECK (amount > 0),
    CONSTRAINT ck_settlements_status CHECK (status = 'CONFIRMED')
);

CREATE INDEX idx_settlements_group_date ON settlements (group_id, settlement_date DESC);
CREATE INDEX idx_settlements_payer ON settlements (payer_member_id);
CREATE INDEX idx_settlements_receiver ON settlements (receiver_member_id);
CREATE INDEX idx_settlements_recorded_by ON settlements (recorded_by_member_id);
