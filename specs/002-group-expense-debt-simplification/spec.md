# Feature Specification: Group Expense Management & Smart Debt Simplification (Build 2)

> *Performed by: Nguyen Le Duc Nhat (24120403) | Reviewed by: Ngo Thai Hoa (24120051) | Edited by: Nguyen Le Duc Nhat (24120403)*

**Feature Branch**: `002-group-expense-debt-simplification`  
**Created**: `2026-11-25`  
**Status**: `Ready for Implementation`  
**Sprint**: `Sprint 4 / PA4-2026 (Build 2)`  
**Selected Functional Groups**:
1. **Group Expense Management** (Use Cases: `UC-CLI-10`, `UC-CLI-11`)
2. **Smart Debt Simplification** (Use Cases: `UC-CLI-12`)

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Group Creation, Membership & Invitations (Priority: P1)

As an authenticated FinSync user, I want to create shared expense groups, add both registered users (via invite link/token) and non-registered guest members (by display name), manage ownership and member departure, and archive finished groups so that our group can coordinate shared expenses with clear accountability.

**Why this priority**: Shared groups and verified membership boundaries are the structural foundation for shared funds, group expenses, and debt settlements. Without atomic group/wallet initialization and strict membership rules, no collaborative financial flow can operate safely.

**Independent Test**: Can be verified by creating a group (confirming an `OWNER` member and `GroupWallet` are created atomically), adding a guest member (`user_id IS NULL`), generating a targeted single-use invitation for that guest, having a second authenticated user redeem the invitation to claim that guest identity, enforcing the 50-member limit, transferring ownership, and verifying that a member with a non-zero net balance cannot leave or be removed.

**Acceptance Scenarios**:

1. **Given** an authenticated user with an `ACTIVE` account, **When** they create a group with a non-empty name, optional description, and currency (`VND` or `USD`), **Then** the system atomically creates the `ExpenseGroup`, adds the caller as the sole `ACTIVE` `OWNER` in `GroupMember` (`member_order = 0`), and initializes exactly one `GroupWallet` (`name = "Quỹ chung"` by default) in the same database transaction.
2. **Given** an `ACTIVE` group `OWNER`, **When** they add a participant who does not yet have a FinSync account by providing a `displayName`, **Then** the system creates a `GroupMember` row with `user_id = NULL`, `role = MEMBER`, `status = ACTIVE`, and the next sequential `member_order`, without granting that guest API access until linked.
3. **Given** an `ACTIVE` group `OWNER` and an unlinked guest `GroupMember`, **When** the Owner generates a guest-targeted invitation (`targetMemberId`), **Then** the system returns a high-entropy single-use token (storing only its SHA-256 `token_hash`, `max_uses = 1`, and `expires_at`) that, when redeemed by another authenticated user not yet in the group, links `group_members.user_id` to that user without losing or merging existing guest expense history.
4. **Given** an authenticated user attempting to redeem an invitation token, **When** the token is expired, revoked, already used (`use_count >= max_uses`), or the user already has an `ACTIVE` `GroupMember` record in that group, **Then** the system rejects the request with an appropriate conflict/validation error and leaves membership unchanged.
5. **Given** a group that already has 50 `ACTIVE` members, **When** the `OWNER` attempts to add a guest or another user attempts to accept a general invitation, **Then** the system enforces the 50-member capacity guardrail and rejects the addition (`409 Conflict`).
6. **Given** an `ACTIVE` group member with a non-zero net balance (`netBalance != 0`), **When** the member attempts to leave or the `OWNER` attempts to remove them, **Then** the system rejects the action (`409 Conflict`), requiring outstanding debts/receivables to reach zero first; if `netBalance == 0`, the member transitions to `status = LEFT` (`left_at` populated) while preserving all historical records.
7. **Given** an `ACTIVE` group `OWNER`, **When** they transfer ownership to another `ACTIVE` registered `MEMBER` (`user_id IS NOT NULL`), **Then** the system acquires a pessimistic lock on the `groups` row and updates the previous owner to `MEMBER` and the target member to `OWNER` within one transaction, preserving the invariant of exactly one active `OWNER`.
8. **Given** an `OWNER` archiving an `ACTIVE` group, **When** `archived_at` is set, **Then** the group becomes strictly read-only for new invitations, member edits, fund entries, expenses, and settlements until the `OWNER` unarchives it.

---

### User Story 2 - Shared Group Fund & Group Expense Recording with 4 Split Modes (Priority: P1)

As an active registered group member, I want the group Owner to manage a shared group fund (contributions and refunds) and I want to log shared expenses funded either by a single member or completely by the shared fund, split across selected participants using `EQUAL`, `EXACT`, `PERCENTAGE`, or `SHARES` with zero lost cents, so that every shared bill is recorded accurately and transparently.

**Why this priority**: Expense capture, shared fund bookkeeping, and deterministic split allocation are the primary operational capabilities of Functional Group 1 (`UC-CLI-11`). Rounding inaccuracies or personal/group double-counting directly compromise financial integrity.

**Independent Test**: Can be verified by having the Owner record a `CONTRIBUTION` to the `GroupWallet`, attempting an excessive `REFUND` (expecting rejection), logging `MEMBERS`-funded expenses across all 4 split modes (`EQUAL`, `EXACT`, `PERCENTAGE`, `SHARES`) including indivisible amounts (e.g., `100,000 VND` / 3 or `$10.00 USD` / 3), logging a `GROUP_FUND`-funded expense (confirming atomic creation of the `EXPENSE` fund entry), and verifying that `sum(splits.amount) == expense.amount` and `personal_transactions` remain untouched.

**Acceptance Scenarios**:

1. **Given** an `ACTIVE` group `OWNER`, **When** they record a `CONTRIBUTION` (`amount > 0`) for an `ACTIVE` member (including guests), **Then** the system appends a `GroupFundEntry` (`type = CONTRIBUTION`) and increases the computed `GroupWallet` balance without creating inter-member debt or personal expense rows.
2. **Given** a `GroupWallet` with computed balance `500,000 VND`, **When** the `OWNER` attempts to record a `REFUND` of `600,000 VND`, **Then** the system rejects the request (`422 Unprocessable Entity` / insufficient fund balance) so the shared fund balance never becomes negative.
3. **Given** an `ACTIVE` registered group member recording a `MEMBERS`-funded expense of `100,000 VND` paid by Member A (`paidByMemberId`) and split `EQUAL` among `[A, B, C]` in that `splitOrder`, **When** the expense is saved, **Then** the system allocates `33,334`, `33,333`, `33,333` VND (`splitOrder` 0 receives the deterministic `+1` VND remainder), persists `GroupExpense` and `GroupExpenseSplit` rows atomically, and locks the group's currency.
4. **Given** an `ACTIVE` registered member recording a group expense in `EXACT`, `PERCENTAGE`, or `SHARES` mode, **When** `EXACT` amounts sum to the total, `PERCENTAGE` values have at most 2 decimal places (`0..100`) and sum to `100.00%`, or `SHARES` weights are integers in `1..1,000,000` with positive sum, **Then** the system calculates exact integer minor-unit shares using largest-remainder distribution (tie-broken by ascending `splitOrder`) such that `sum(splits.amount) == expense.amount`.
5. **Given** a `GROUP_FUND`-funded expense of `200,000 VND`, **When** an `ACTIVE` registered member submits it and the `GroupWallet` balance is `>= 200,000 VND`, **Then** the system rejects `paidByMemberId` (must be `NULL`), persists the `GroupExpense`, `GroupExpenseSplit` rows (for consumption reporting), and an atomic `GroupFundEntry` (`type = EXPENSE`, `expense_id` linked, `member_id = NULL`, deterministic `request_id`) in a single transaction, without altering inter-member debt balances.
6. **Given** a client retrying a `POST` request with the same `requestId` and identical normalized payload (`requestHash`), **When** the request arrives again, **Then** the system returns the previously persisted record instead of creating a duplicate; if the same `requestId` is reused with a different payload, the system returns `409 Conflict`.
7. **Given** any saved `GroupExpense`, **When** inspected against personal bookkeeping, **Then** no `personal_transactions` row is automatically created, preserving strict non-duplication between personal and group ledgers.

---

### User Story 3 - Net Balance Calculation & Smart Debt Simplification Engine (Priority: P1)

As an active registered group member, I want to view each member's dynamically computed net balance and a minimized settlement plan that eliminates circular and intermediary debts so that members can settle all group obligations with the fewest possible external transfers.

**Why this priority**: Smart debt simplification is the core value proposition of Functional Group 2 (`UC-CLI-12`) and directly fulfills course success criterion `SC-002` ($\ge 30\%$ transfer reduction on $\ge 5$-member circular graphs in $< 5\text{ms}$).

**Independent Test**: Can be verified by creating a 5-member group with circular and multi-party `MEMBERS` expenses (plus at least one `GROUP_FUND` expense and one `CONTRIBUTION` to verify exclusion from debt), querying `GET /api/v1/groups/{groupId}/debts`, and confirming that `sum(netBalances) == 0`, `GROUP_FUND`/fund entries do not skew net balances, and the simplified transfer list reduces the raw pairwise edge count by $\ge 30\%$ in $< 5\text{ms}$.

**Acceptance Scenarios**:

1. **Given** a group with `MEMBERS`-funded expenses and `CONFIRMED` settlements, **When** any `ACTIVE` registered member requests `GET /api/v1/groups/{groupId}/debts`, **Then** the system computes each member's net balance as `sum(MEMBERS expenses paid) - sum(MEMBERS splits owed) + sum(settlements sent) - sum(settlements received)`, excluding `GROUP_FUND` expenses and `CONTRIBUTION`/`REFUND` fund entries.
2. **Given** a group with circular debts (e.g., A owes B `50,000`, B owes C `50,000`, C owes A `20,000`), **When** `DebtSimplificationEngine` runs its greedy two-heap algorithm (matching the largest creditor with the largest debtor), **Then** the returned `simplifiedTransfers` resolve all non-zero net balances with minimal direct transfers and conserve total value (`sum(netBalances) == 0`).
3. **Given** a group where all members have `netBalance == 0`, **When** `GET /api/v1/groups/{groupId}/debts` is called, **Then** the system returns an empty `simplifiedTransfers` array and all member balances equal to `0`.
4. **Given** a group with `LEFT` members who settled to zero before leaving, **When** `GET /api/v1/groups/{groupId}/debts` is called, **Then** historical `LEFT` members remain visible in historical ledger views with `netBalance = 0` and do not appear in active transfer recommendations.

---

### User Story 4 - Manual External Debt Settlement Recording (Priority: P1)

As an active registered group member (or group Owner), I want to record full or partial manual debt settlements performed externally (e.g., cash or external bank transfer) as immutable confirmed records so that group net balances and simplified transfer plans update immediately without FinSync touching real bank funds.

**Why this priority**: Closes the group financial loop in Functional Group 2 (`UC-CLI-12`) while strictly adhering to FinSync's non-custodial manual settlement model (`database_design.md` §1 & §3.16).

**Independent Test**: Can be verified by recording a partial settlement (e.g., `100,000` of a `300,000` debt) and confirming net balances decrease by `100,000`, attempting an overpayment above remaining pairwise/simplified debt (expecting `422`), verifying authorization (payer, receiver, or Owner allowed; unrelated third-party `MEMBER` rejected with `403`), and recording the final settlement to bring both members to zero.

**Acceptance Scenarios**:

1. **Given** a group where member B owes member A `300,000 VND`, **When** member B (payer), member A (receiver), or the group `OWNER` records a manual settlement from B to A for `150,000 VND` (partial) or `300,000 VND` (full) with a unique `requestId`, **Then** the system acquires a pessimistic lock on the `groups` row, recomputes current net balances, verifies `0 < amount <= maximum settleable debt between payer and receiver`, persists an immutable `Settlement` (`status = CONFIRMED`), and updates both members' net balances immediately.
2. **Given** a member attempting to record a settlement exceeding the current settleable debt between payer and receiver, or where the payer is not a debtor or the receiver is not a creditor, **When** `POST /api/v1/groups/{groupId}/settlements` is called, **Then** the system rejects the request (`422 Unprocessable Entity`) without altering balances.
3. **Given** an unrelated `ACTIVE` `MEMBER` C (neither payer, receiver, nor `OWNER`), **When** C attempts to record a settlement between B and A, **Then** the system rejects the request with `403 Forbidden`.
4. **Given** a retried settlement request with the same `requestId` and identical `requestHash`, **When** submitted again, **Then** the system returns the existing `Settlement` record idempotently without double-settling the debt.
5. **Given** a confirmed settlement, **When** stored in PostgreSQL, **Then** no banking credentials are requested, stored, or accessed, and no `personal_transactions` record is created automatically.

---

### Edge Cases

- **Zero Lost Cents on Indivisible Splits**: Splitting `100 VND` across 3 members (`EQUAL`) allocates `34`, `33`, `33` VND (`split_order = 0, 1, 2`). Splitting `$10.00 USD` across 3 members allocates `$3.34`, `$3.33`, `$3.33`. For `PERCENTAGE` and `SHARES`, fractional remainders are distributed in descending order of fractional remainder (and ascending `split_order` for ties) so `sum(splits.amount) == expense.amount` always holds.
- **Currency Scale Enforcement**: `VND` amounts MUST have `0` fractional digits (e.g., `10000.50 VND` is rejected with `400 Bad Request` before database rounding). `USD` amounts MUST have at most `2` decimal places. Group `currency_code` becomes immutable once any fund entry, expense, or settlement exists.
- **Concurrent Group Ledger Writes**: Simultaneous expense submissions, settlement recordings, fund refunds, or invitation redemptions on the same group serialize on a `PESSIMISTIC_WRITE` (`SELECT ... FOR UPDATE`) lock on the parent `groups` row, ensuring consistent balance checks, `member_order` assignment, and `use_count` enforcement.
- **Idempotency Conflict & Race Safety**: Every financial write (`POST` fund entry, expense, settlement) requires a client-supplied UUID `requestId` and computes a canonical SHA-256 `requestHash`. Same `requestId` + same `requestHash` returns the existing DTO; same `requestId` + different `requestHash` returns `409 Conflict`. Database `UNIQUE(request_id)` serves as the final concurrency backstop.
- **Archived Group & Departed (`LEFT`) Member Guardrails**: Archived groups reject all mutating operations (`409 Conflict`) except unarchiving by the `OWNER`. `LEFT` members cannot be chosen as payer, split participant, fund member, or settlement party for new entries, though their past records remain intact for balance auditing.
- **Non-Custodial Boundary Enforcement**: No endpoint accepts bank account passwords, PINs, card CVVs, or automated debit authorizations; all group settlements are manual external confirmations.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-G01 (Non-Custodial Bookkeeping)**: The system MUST strictly function as a manual bookkeeping and debt reconciliation ledger; it MUST NEVER store banking credentials, hold real user funds, or execute automated bank transfers.
- **FR-G02 (Atomic Group & Shared Wallet Lifecycle)**: Creating a group MUST atomically persist `groups`, the creator's `group_members` row (`role = OWNER`, `status = ACTIVE`, `member_order = 0`), and a `group_wallets` row (`1:1`). Archiving a group MUST make all group financial and membership state read-only until unarchived by the `OWNER`.
- **FR-G03 (Membership, Capacity & Guest Identities)**: Each group MUST support up to 50 `ACTIVE` members and enforce a partial unique constraint guaranteeing at most one `ACTIVE` `OWNER`. The `OWNER` MAY add guest members (`user_id IS NULL`), transfer ownership to another `ACTIVE` registered member, or remove a member whose net balance is `0` (marking `status = LEFT` without deleting historical financial rows).
- **FR-G04 (Group Invitations & Guest Claiming)**: Only the `OWNER` MAY create or revoke invitations. General invitations (`target_member_id IS NULL`) support configurable `max_uses >= 1`; guest-targeted invitations (`target_member_id IS NOT NULL`) MUST enforce `max_uses = 1` and link the redeeming user's `user_id` to that exact `GroupMember` ID upon login redemption. Only the SHA-256 hash of the invitation token is stored in PostgreSQL.
- **FR-G05 (Shared Group Fund Ledger)**: Only the `OWNER` MAY record `CONTRIBUTION` and `REFUND` entries in `group_fund_entries`. The computed `GroupWallet` balance (`sum(CONTRIBUTION) - sum(REFUND) - sum(EXPENSE)`) MUST NEVER drop below `0`.
- **FR-G06 (Group Expense Funding Sources)**: Every `GroupExpense` MUST specify an `EXPENSE`-type `Category` and a `fundingSource`: either `MEMBERS` (requiring exactly one `ACTIVE` `paidByMemberId`) or `GROUP_FUND` (`paidByMemberId` MUST be `NULL`, atomically creating a matching `group_fund_entries` row of type `EXPENSE` within the same transaction if sufficient fund balance exists).
- **FR-G07 (Four Split Modes & Deterministic Rounding)**: Every `GroupExpense` MUST persist at least one `GroupExpenseSplit` row and support `EQUAL` (`input_value` NULL), `EXACT` (`input_value` equals allocated amount and sums to `expense.amount`), `PERCENTAGE` (`input_value` in `0.00..100.00` summing to `100.00`), and `SHARES` (integer weights `1..1,000,000` with total `>= 1`). Allocation arithmetic MUST operate in integer minor units (`1` for VND, `1 cent` for USD) with largest-remainder distribution and ascending `split_order` tie-breaking so `sum(splits.amount) == expense.amount`.
- **FR-G08 (Append-Only Financial Ledger)**: In v1.0, `group_fund_entries`, `group_expenses`, `group_expense_splits`, and `settlements` MUST be strictly append-only; no update or delete endpoints are exposed for confirmed group financial records.
- **FR-G09 (Net Balance Calculation)**: Each member's net balance MUST be derived dynamically from persistent ledger tables: `(total MEMBERS expenses paid) - (total MEMBERS splits owed) + (total CONFIRMED settlements paid) - (total CONFIRMED settlements received)`. Positive values indicate net receivables; negative values indicate net payables. `GROUP_FUND` expenses and `CONTRIBUTION`/`REFUND` entries MUST NOT alter inter-member debt balances.
- **FR-G10 (Smart Debt Simplification)**: `DebtSimplificationEngine` MUST compute minimal direct transfers from member net balances using a greedy two-heap algorithm in $O(V \log V)$ time without persisting stale transfer graphs in a separate database table.
- **FR-G11 (Manual External Settlement)**: Settlements MUST be recorded with `status = CONFIRMED` by the payer (registered), receiver (registered), or group `OWNER`. Under a `groups` row lock, the service MUST verify `payerMemberId != receiverMemberId`, payer has negative net balance, receiver has positive net balance, and `0 < amount <= min(|payerNetBalance|, receiverNetBalance)`.
- **FR-G12 (Concurrency & Idempotency)**: All mutating group operations MUST lock the target `groups` row via `PESSIMISTIC_WRITE` (`SELECT ... FOR UPDATE`). All financial write requests (`POST` fund entry, expense, settlement) MUST accept a client UUID `requestId` and store a canonical SHA-256 `requestHash`, returning the existing result for identical retries or `409 Conflict` for mismatched payload reuse.
- **FR-G13 (Personal Ledger Isolation)**: Creating a group expense, fund entry, or settlement MUST NOT automatically insert rows into `personal_transactions`.
- **FR-G14 (Authorization & Role Enforcement)**: Every group endpoint MUST authenticate the caller via PA3 stateless JWT (`User` must be `ACTIVE`, session non-revoked, token `jti` not in Redis blacklist) and verify that the caller is an `ACTIVE` `GroupMember` of the target group (and `OWNER` for owner-only actions).

### Key Entities

- **`Category`** (`categories`): Shared system expense/income classification; group expenses require an unarchived `EXPENSE` category.
- **`ExpenseGroup`** (`groups`): Shared expense ledger container (`name`, `description`, `currency_code`, `archived_at`).
- **`GroupMember`** (`group_members`): Group membership linking `ExpenseGroup` to optional `User` (`user_id` nullable for guests), with `display_name`, `role` (`OWNER`/`MEMBER`), `status` (`ACTIVE`/`LEFT`), and stable `member_order`.
- **`GroupInvitation`** (`group_invitations`): Expiring, revocable invitation record (`token_hash`, optional `target_member_id`, `max_uses`, `use_count`).
- **`GroupWallet`** (`group_wallets`): `1:1` shared group fund account per group.
- **`GroupFundEntry`** (`group_fund_entries`): Append-only fund ledger (`CONTRIBUTION`, `REFUND`, `EXPENSE`), linked optionally to `member_id` or `expense_id`, with `request_id`/`request_hash`.
- **`GroupExpense`** (`group_expenses`): Append-only shared bill (`funding_source`, `split_mode`, `paid_by_member_id`, `created_by_member_id`, `category_id`, `amount`, `expense_date`, `request_id`/`request_hash`).
- **`GroupExpenseSplit`** (`group_expense_splits`): Individual participant share allocation (`amount`, `input_value`, `split_order`).
- **`Settlement`** (`settlements`): Immutable confirmed manual repayment between `payer_member_id` and `receiver_member_id` (`amount`, `settlement_date`, `status = CONFIRMED`, `recorded_by_member_id`, `request_id`/`request_hash`).

---

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-G01 (Zero Lost Cents)**: 100% of group expense split calculations across `EQUAL`, `EXACT`, `PERCENTAGE`, and `SHARES` modes conserve the exact total expense amount (`sum(splits.amount) == expense.amount`) for both `VND` (`scale = 0`) and `USD` (`scale = 2`).
- **SC-G02 (Debt Simplification Performance & Reduction)**: On groups with 5 to 50 members and circular/multi-party debts, `DebtSimplificationEngine` executes in `< 5ms` and reduces the number of required transfers by at least `30%` compared to raw unsimplified pairwise expense edges.
- **SC-G03 (Idempotency & Concurrency Safety)**: 100% of duplicate `POST` retries with identical `requestId` produce zero duplicate ledger rows, and concurrent writes on the same group never cause negative fund balances, >50 active members, or over-settled debts.
- **SC-G04 (Membership Guardrail Enforcement)**: 100% of attempts to remove/leave with non-zero net balance or create an expense/settlement on an archived group are rejected deterministically.
- **SC-G05 (Personal/Group Isolation)**: Zero `personal_transactions` rows are auto-generated when recording group expenses, fund entries, or settlements.
- **SC-G06 (Non-Custodial Compliance)**: 100% adherence to manual settlement recording; zero banking credentials, PINs, or automated fund transfers exist anywhere in schemas, DTOs, or logs.

---

## Assumptions & Scope Boundaries

- **Authentication Reuse (PA3 Baseline)**: User registration, RS256 JWT issuance, `auth_sessions`, Redis `jti` blacklist, and `JwtAuthenticationFilter` from PA3 (`V1__create_account_tables.sql`) are reused directly; no duplicate auth tables are created.
- **Single Currency per Group & No FX**: Each group operates in a single currency (`VND` or `USD`), immutable after first financial entry. Cross-currency conversion is out of scope in v1.0.
- **Immediate Settlement Confirmation in v1.0**: Per `database_design.md` §1 and §3.16, v1.0 records manual settlements directly as `CONFIRMED` when submitted by the payer, receiver, or group `OWNER`; multi-step `PENDING` / dispute override workflows are deferred outside v1.0.
- **Optional Personal Wallet Linking (`GROUP_MOVEMENT`)**: Recording a group item into a user's personal wallet belongs to the Personal Wallet functional group (`personal_transactions` is not part of PA4 Build 2 scope and is excluded from PA4 migrations).
- **Notifications Deferral**: In-app notification inbox delivery (`notifications` table) is outside the PA4 Build 2 scope and will be added when Budget/Notification functional groups are implemented.
