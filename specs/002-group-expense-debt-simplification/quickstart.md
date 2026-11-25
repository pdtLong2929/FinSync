# Quickstart & Verification Guide: Group Expense Management & Smart Debt Simplification (Build 2)

> *Performed by: Nguyen Le Duc Nhat (24120403) | Reviewed by: Ngo Thai Hoa (24120051) | Edited by: Nguyen Le Duc Nhat (24120403)*

This guide validates all user stories (`US1`–`US4`) and database invariants end-to-end against PostgreSQL 16, Redis 7.2, and the Spring Boot REST API (`http://localhost:8080/api/v1`).

---

## 1. Prerequisites & Environment Startup

1. Start PostgreSQL 16 and Redis 7.2 via Docker Compose:
   ```powershell
   docker compose up -d postgres redis
   ```
2. Run backend tests (verifies Flyway `V1`–`V3`, JPA schema validation, `ExpenseSplitCalculatorTests`, `DebtSimplificationEngineTests`, `GroupExpenseApiTests`, and `DebtSettlementApiTests`):
   ```powershell
   cd src/backend
   ./mvnw test
   ```
3. Start the Spring Boot API server:
   ```powershell
   ./mvnw spring-boot:run
   ```
4. Authenticate two users (`Alice` as Group Owner, `Bob` as registered member) using PA3 endpoints (`POST /api/v1/auth/register` and `POST /api/v1/auth/login`) to obtain JWT Bearer tokens `TOKEN_ALICE` and `TOKEN_BOB`.

---

## 2. End-to-End Verification Scenarios

### Scenario 1: Atomic Group Creation, Guest Member & Targeted Invitation (`US1`)
1. `Alice` creates a group: `POST /api/v1/groups` with `{"name": "Da Lat Trip", "currencyCode": "VND", "guestMemberNames": ["Guest Bob"]}`.
   - **Expected**: `201 Created`; returns `groupId`, `walletId` (`name = "Quỹ chung"`), `Alice` as `OWNER` (`memberOrder = 0`), and `Guest Bob` (`userId = null`, `guest = true`, `memberOrder = 1`).
2. `Alice` creates a guest-targeted invitation for `Guest Bob`: `POST /api/v1/groups/{groupId}/invitations` with `{"targetMemberId": "<guestBobMemberId>", "maxUses": 1, "expiresInHours": 24}`.
   - **Expected**: `201 Created` returning raw `token` (only `token_hash` stored in PostgreSQL `group_invitations`).
3. `Bob` accepts the invitation: `POST /api/v1/groups/invitations/accept` with `{"token": "<token>"}`.
   - **Expected**: `200 OK`; `Guest Bob`'s `group_members.user_id` is now linked to `Bob`'s user ID (`guest = false`), and `use_count = 1`.
4. **Guardrail check**: Re-submitting the same `token` returns `409 Conflict`.

### Scenario 2: Shared Group Fund Contribution, Refund & Guardrail (`US2`)
1. `Alice` records a fund contribution for `Bob`: `POST /api/v1/groups/{groupId}/fund/entries` with `requestId`, `type: "CONTRIBUTION"`, `memberId: "<bobMemberId>"`, `amount: 500000`.
   - **Expected**: `201 Created`; `GET /api/v1/groups/{groupId}/fund` shows `balance = "500000.00"`.
2. `Alice` attempts a `REFUND` of `600000`.
   - **Expected**: `422 Unprocessable Entity` (`INSUFFICIENT_GROUP_FUND`); balance remains `500000.00`.
3. `Bob` (non-owner `MEMBER`) attempts a `CONTRIBUTION`.
   - **Expected**: `403 Forbidden`.

### Scenario 3: Group Expense Splitting Across 4 Modes & Zero Lost Cents (`US2`)
1. Add guest `Charlie` (`POST /api/v1/groups/{groupId}/members` -> `displayName: "Charlie"`).
2. `Alice` logs an indivisible `MEMBERS` expense (`amount: 100000`, `fundingSource: "MEMBERS"`, `paidByMemberId: "<aliceMemberId>"`, `splitMode: "EQUAL"`, participants `[Alice, Bob, Charlie]`):
   - **Expected**: `201 Created`; splits are `33334.00`, `33333.00`, `33333.00` (sum = `100000.00`).
3. Repeat with `EXACT`, `PERCENTAGE` (`33.33`, `33.33`, `33.34`), and `SHARES` (`1`, `2`, `3`).
   - **Expected**: `201 Created`; sum of persisted `group_expense_splits.amount` equals `group_expenses.amount`.
4. Log a `GROUP_FUND` expense (`amount: 200000`, `fundingSource: "GROUP_FUND"`, `paidByMemberId: null`, `splitMode: "EQUAL"`):
   - **Expected**: `201 Created`; shared fund balance drops from `500000.00` to `300000.00`, and an `EXPENSE` row appears in `group_fund_entries`.
5. **Idempotency check**: Replay the exact same `POST /expenses` payload with the same `requestId` -> returns existing record without duplication; replay with modified `amount` and same `requestId` -> `409 Conflict`.

### Scenario 4: Circular Debt Simplification (`US3`)
1. In a 5-member group (`M1..M5`), record circular `MEMBERS` expenses:
   - `M1` pays `100,000` split `EXACT` to `M2` (`100,000`)
   - `M2` pays `100,000` split `EXACT` to `M3` (`100,000`)
   - `M3` pays `100,000` split `EXACT` to `M4` (`100,000`)
   - `M4` pays `100,000` split `EXACT` to `M5` (`100,000`)
   - `M5` pays `60,000` split `EXACT` to `M1` (`60,000`)
2. Query `GET /api/v1/groups/{groupId}/debts`.
   - **Expected**: `200 OK`; `M1` net = `+40,000`, `M5` net = `-40,000`, `M2 = M3 = M4 = 0`. `simplifiedTransfers` contains **1 transfer** (`M5 -> M1: 40000.00`) vs. 5 raw edges (`80%` reduction >= `30%` target).

### Scenario 5: Manual Partial & Full Settlement + Zero-Balance Departure (`US4`)
1. Attempt an overpayment settlement `M5 -> M1` of `50,000`:
   - **Expected**: `422 Unprocessable Entity`.
2. `M5` (or `M1` or `OWNER`) records partial settlement `25,000` (`POST /api/v1/groups/{groupId}/settlements`).
   - **Expected**: `201 Created`, `status = CONFIRMED`; `GET /debts` shows `M5 -> M1: 15000.00`.
3. `M5` attempts to leave (`POST /api/v1/groups/{groupId}/members/leave`) while owing `15,000`:
   - **Expected**: `409 Conflict`.
4. Record remaining `15,000` settlement; verify `GET /debts` has `simplifiedTransfers = []` and all net balances `0.00`.
5. `M5` calls `POST /api/v1/groups/{groupId}/members/leave`:
   - **Expected**: `200 OK`; `M5` transitions to `status = LEFT`.

---

## 3. PostgreSQL Verification Queries

```sql
-- 1. Verify Flyway migrations
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;

-- 2. Verify split conservation across all expenses (must return 0 mismatched rows)
SELECT ge.id, ge.amount, COALESCE(SUM(ges.amount), 0) AS split_sum
FROM group_expenses ge
LEFT JOIN group_expense_splits ges ON ges.expense_id = ge.id
GROUP BY ge.id, ge.amount
HAVING ge.amount <> COALESCE(SUM(ges.amount), 0);

-- 3. Verify non-negative group fund balances
SELECT gw.group_id,
       COALESCE(SUM(CASE WHEN gfe.type = 'CONTRIBUTION' THEN gfe.amount
                         WHEN gfe.type IN ('REFUND', 'EXPENSE') THEN -gfe.amount
                         ELSE 0 END), 0) AS fund_balance
FROM group_wallets gw
LEFT JOIN group_fund_entries gfe ON gfe.group_wallet_id = gw.id
GROUP BY gw.group_id;
```
