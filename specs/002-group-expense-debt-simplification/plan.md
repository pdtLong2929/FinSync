# Implementation Plan: Group Expense Management & Smart Debt Simplification (Build 2)

> *Performed by: Nguyen Le Duc Nhat (24120403) | Reviewed by: Ngo Thai Hoa (24120051) | Edited by: Nguyen Le Duc Nhat (24120403)*

**Branch**: `002-group-expense-debt-simplification` | **Date**: `2026-11-25` | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `specs/002-group-expense-debt-simplification/spec.md`

---

## Summary

Sprint 4 / PA4 (Build 2) delivers two full-stack functional groups on top of the PA3 Authentication & Account baseline (`users`, `auth_sessions`, RS256 JWT + Redis `jti` blacklist):

1. **Group Expense Management**:
   - Atomic creation of `ExpenseGroup`, initial `OWNER` `GroupMember`, and `1:1` `GroupWallet`.
   - Membership management supporting registered users, unlinked guest members (`user_id IS NULL`), general and guest-targeted single-use invitations (`SHA-256` token hashing), ownership transfer, 50-member capacity enforcement, zero-balance departure guardrails, and group archiving.
   - Shared group fund ledger (`CONTRIBUTION`, `REFUND`, `EXPENSE`) enforcing non-negative balance invariants.
   - Append-only group expense recording funded by either `MEMBERS` (single `paid_by_member_id`) or `GROUP_FUND` (atomic `EXPENSE` fund entry), with deterministic integer minor-unit splitting across `EQUAL`, `EXACT`, `PERCENTAGE`, and `SHARES` modes.
2. **Smart Debt Simplification**:
   - Dynamic net balance derivation from `MEMBERS` expenses and `CONFIRMED` settlements.
   - Greedy two-heap cash-flow minimization (`DebtSimplificationEngine`, $O(V \log V)$) producing minimal direct transfer recommendations without persisting stale debt edges.
   - Immutable manual external settlement recording (`status = CONFIRMED`) supporting partial and full repayments under pessimistic group row locking and `requestId` idempotency.

---

## Technical Context

**Language/Version**:
- Backend API: Java 21 LTS
- Mobile Client: Kotlin (Android SDK, Jetpack Compose, Coroutines & Flow)

**Primary Dependencies**:
- Backend: Spring Boot `4.1.1`, Spring Security (Stateless RS256 JWT + `spring-security-oauth2-jose`), Spring Data JPA / Hibernate ORM (`ddl-auto=validate`), Spring Data Redis, Flyway, SpringDoc OpenAPI, Lombok
- Mobile Client (Android): Jetpack Compose, Material 3, Kotlin Coroutines & StateFlow, Retrofit 2 / OkHttp 4

**Storage**:
- PostgreSQL 16 (Relational schema managed strictly via Flyway migrations: `V1__create_account_tables.sql` from PA3, `V2__create_group_expense_tables.sql`, and `V3__seed_default_categories.sql`)
- Redis 7.2 Alpine (JWT access token `jti` revocation blacklist from PA3)

**Testing**:
- Backend: JUnit 5, Spring Boot Test (`MockMvc`), AssertJ, PostgreSQL integration tests
- Mobile: JUnit 4/5, Compose UI tests, domain unit tests (`GroupFinanceTest.kt`)
- E2E Verification: Postman / Newman API test suite

**Target Platform**:
- Mobile: Android 8.0+ (API Level 26+)
- Backend Server & DB: Docker Compose (`finsync-network`: Spring Boot API `8080`, PostgreSQL `5432`, Redis `6379`)

**Project Type**: Multi-tier mobile + backend system (`src/backend/` Spring Boot REST API + `src/mobile/` Android Jetpack Compose client).

**Performance Goals**:
- `DebtSimplificationEngine` execution: `< 5ms` for groups up to 50 members (`SC-G02`).
- Transfer edge reduction: `>= 30%` reduction on `>= 5`-member circular debt graphs (`SC-G02`).
- Financial write latency: `< 200ms` p95 for expense creation and settlement confirmation under group lock.

**Constraints**:
- **Non-Custodial Operation (Constitution I)**: Zero banking credentials, zero automated transfers; manual settlement recording only.
- **Monetary Precision**: `NUMERIC(19,2)` in PostgreSQL mapped to Java `BigDecimal`; `VND` requires scale `0`, `USD` requires scale `<= 2`, validated before database persistence.
- **Group Capacity**: Maximum `50` `ACTIVE` members per group.
- **Append-Only Group Ledger**: No update/delete endpoints for `group_fund_entries`, `group_expenses`, `group_expense_splits`, or `settlements`.
- **Personal/Group Isolation**: Group writes never automatically create `personal_transactions` records.

**Scale/Scope**:
- Up to 50 active members per group, 9 new relational tables (`categories`, `groups`, `group_members`, `group_invitations`, `group_wallets`, `group_fund_entries`, `group_expenses`, `group_expense_splits`, `settlements`), 18+ REST endpoints.

---

## Constitution Check

*GATE: Checked against `.specify/memory/constitution.md` (v1.0.0).*

| Principle | Requirement | Plan Compliance Status | Notes |
|---|---|---|---|
| **I. Non-Custodial & Manual Settlement** | System MUST NEVER store banking credentials, hold user funds, or trigger automated deductions; settlements MUST be external and manually recorded. | **PASS** | `Settlement` records manual external transfers as `CONFIRMED`; zero banking secrets or payment gateways exist. |
| **II. Mobile Client Architecture** | Android MUST use Kotlin with Jetpack Compose, Coroutines, and Retrofit. | **PASS** | `src/mobile/` implements Jetpack Compose screens (`ui/groups/*`), domain models (`GroupFinance.kt`), and Retrofit API integration. |
| **III. Backend Service Architecture** | Spring Boot + Java 21, RESTful layered architecture (`Controller -> Service -> Repository`), stateless JWT, Flyway, SpringDoc OpenAPI. | **PASS** | Extends PA3 layered package layout (`controller`, `service`, `service/impl`, `repository`, `entity`, `dto`) with Flyway `V2`/`V3` and OpenAPI YAML. |
| **IV. AI & Data Parsing Microservice Isolation** | AI and notification parsing isolated in Python FastAPI microservice. | **PASS** | No AI/LLM calls are introduced into Spring Boot group expense or debt settlement modules (AI Advisory belongs to PA5). |
| **V. Web Admin Architecture** | React.js web application for platform health and user governance. | **PASS** | No admin web scope in PA4 Build 2; system `categories` seeded via Flyway `V3` and exposed via read-only `GET /api/v1/categories`. |
| **VI. Managed Persistence & Schema** | PostgreSQL 16 via Spring Data JPA / Hibernate; all schema changes via versioned Flyway scripts. | **PASS** | Flyway `V2__create_group_expense_tables.sql` and `V3__seed_default_categories.sql` define all DDL and seeds; `ddl-auto=validate` enforced. |

*Gate Result*: **ALL 6 CONSTITUTIONAL GATES PASSED**.

---

## Project Structure

### Documentation (this feature)

```text
specs/002-group-expense-debt-simplification/
├── spec.md                         # Prioritized user stories, functional requirements & acceptance criteria
├── plan.md                         # Implementation plan and architectural decisions (this file)
├── data-model.md                   # Relational schema (9 tables), ERD, constraints & JPA mappings
├── quickstart.md                   # End-to-end verification scenarios & SQL assertions
├── tasks.md                        # Phased implementation tasks & dependency DAG
└── contracts/
    └── backend-api.yaml            # OpenAPI 3.0.3 REST contract for Build 2 endpoints
```

### Source Code (Repository Layout)

```text
FinSync/
├── docker-compose.yml                                  # PostgreSQL 16 + Redis 7.2 + Backend orchestration
├── database_design.md                                  # Authoritative v1.0 22-table database specification
├── specs/002-group-expense-debt-simplification/        # Build 2 Spec Kit artifacts
└── src/
    ├── backend/
    │   ├── pom.xml
    │   └── src/
    │       ├── main/
    │       │   ├── java/com/finsync/
    │       │   │   ├── FinSyncApplication.java
    │       │   │   ├── config/                         # SecurityConfig (PA3)
    │       │   │   ├── security/                       # JwtAuthenticationFilter, JwtTokens, TokenBlacklist (PA3)
    │       │   │   ├── exception/                      # ApiException, ApiExceptionHandler, ApiErrorWriter (PA3)
    │       │   │   ├── controller/
    │       │   │   │   ├── AuthController.java         # (PA3)
    │       │   │   │   ├── UserController.java         # (PA3)
    │       │   │   │   ├── CategoryController.java     # [NEW] Read default categories
    │       │   │   │   ├── GroupController.java        # [NEW] Group CRUD, members, invitations, archive
    │       │   │   │   ├── GroupFundController.java    # [NEW] Shared wallet balance & fund entries
    │       │   │   │   ├── GroupExpenseController.java # [NEW] Expense creation & listing
    │       │   │   │   └── DebtSettlementController.java # [NEW] Simplified debts & settlement recording
    │       │   │   ├── dto/
    │       │   │   │   ├── AccountDtos.java            # (PA3)
    │       │   │   │   ├── CategoryDtos.java           # [NEW]
    │       │   │   │   ├── GroupDtos.java              # [NEW]
    │       │   │   │   ├── GroupExpenseDtos.java       # [NEW]
    │       │   │   │   └── SettlementDtos.java         # [NEW]
    │       │   │   ├── entity/
    │       │   │   │   ├── BaseEntity.java             # (PA3)
    │       │   │   │   ├── User.java, AuthSession.java # (PA3)
    │       │   │   │   ├── Category.java               # [NEW]
    │       │   │   │   ├── ExpenseGroup.java           # [NEW]
    │       │   │   │   ├── GroupMember.java            # [NEW]
    │       │   │   │   ├── GroupInvitation.java        # [NEW]
    │       │   │   │   ├── GroupWallet.java            # [NEW]
    │       │   │   │   ├── GroupFundEntry.java         # [NEW]
    │       │   │   │   ├── GroupExpense.java           # [NEW]
    │       │   │   │   ├── GroupExpenseSplit.java      # [NEW]
    │       │   │   │   ├── Settlement.java             # [NEW]
    │       │   │   │   └── enums/
    │       │   │   │       ├── ClientType.java, CurrencyCode.java, UserRole.java, UserStatus.java # (PA3)
    │       │   │   │       ├── CategoryType.java       # [NEW]
    │       │   │   │       ├── GroupMemberRole.java    # [NEW]
    │       │   │   │       ├── GroupMemberStatus.java  # [NEW]
    │       │   │   │       ├── GroupFundEntryType.java # [NEW]
    │       │   │   │       ├── FundingSource.java      # [NEW]
    │       │   │   │       ├── SplitMode.java          # [NEW]
    │       │   │   │       └── SettlementStatus.java   # [NEW]
    │       │   │   ├── repository/
    │       │   │   │   ├── UserRepository.java, AuthSessionRepository.java # (PA3)
    │       │   │   │   ├── CategoryRepository.java     # [NEW]
    │       │   │   │   ├── ExpenseGroupRepository.java # [NEW] Includes PESSIMISTIC_WRITE lock query
    │       │   │   │   ├── GroupMemberRepository.java  # [NEW]
    │       │   │   │   ├── GroupInvitationRepository.java # [NEW]
    │       │   │   │   ├── GroupWalletRepository.java  # [NEW]
    │       │   │   │   ├── GroupFundEntryRepository.java # [NEW]
    │       │   │   │   ├── GroupExpenseRepository.java # [NEW]
    │       │   │   │   ├── GroupExpenseSplitRepository.java # [NEW]
    │       │   │   │   └── SettlementRepository.java   # [NEW]
    │       │   │   └── service/
    │       │   │       ├── AccountService.java         # (PA3)
    │       │   │       ├── GroupService.java           # [NEW]
    │       │   │       ├── GroupFundService.java       # [NEW]
    │       │   │       ├── GroupExpenseService.java    # [NEW]
    │       │   │       ├── DebtSettlementService.java  # [NEW]
    │       │   │       └── impl/
    │       │   │           ├── AccountServiceImpl.java # (PA3)
    │       │   │           ├── GroupServiceImpl.java   # [NEW]
    │       │   │           ├── GroupFundServiceImpl.java # [NEW]
    │       │   │           ├── ExpenseSplitCalculator.java # [NEW] Pure minor-unit split engine
    │       │   │           ├── GroupExpenseServiceImpl.java # [NEW]
    │       │   │           ├── DebtSimplificationEngine.java # [NEW] Two-heap O(V log V) optimizer
    │       │   │           ├── DebtSettlementServiceImpl.java # [NEW]
    │       │   │           └── IdempotencySupport.java # [NEW] Canonical SHA-256 requestHash helper
    │       │   └── resources/
    │       │       ├── application.properties
    │       │       └── db/migration/
    │       │           ├── V1__create_account_tables.sql        # (PA3)
    │       │           ├── V2__create_group_expense_tables.sql  # [NEW]
    │       │           └── V3__seed_default_categories.sql      # [NEW]
    │       └── test/java/com/finsync/
    │           ├── AccountApiTests.java, AccountPersistenceTests.java # (PA3)
    │           ├── ExpenseSplitCalculatorTests.java    # [NEW] Unit tests for 4 split modes & rounding
    │           ├── DebtSimplificationEngineTests.java  # [NEW] Unit tests + 50-member <5ms benchmark
    │           ├── GroupExpenseApiTests.java           # [NEW] Integration tests for Group Expense Management
    │           └── DebtSettlementApiTests.java         # [NEW] Integration tests for Debts & Settlements
    └── mobile/
        └── app/src/main/java/com/finsync/
            ├── domain/GroupFinance.kt                  # Existing UI domain logic
            ├── data/                                   # Retrofit service interfaces & remote repository
            └── ui/groups/                              # Group, Split, Fund, and Debt screens
```

**Structure Decision**: Extends the existing PA3 Spring Boot package layout (`controller`, `dto`, `entity`, `repository`, `service`, `service/impl`, `exception`, `security`) rather than introducing ad-hoc package splits, keeping code-to-C4-component mapping exact and transparent.

---

## Core Architectural & Algorithmic Design

### 1. Concurrency & Locking Strategy
- All mutating group operations acquire a `PESSIMISTIC_WRITE` (`SELECT ... FOR UPDATE`) lock on the parent `groups` row first via `ExpenseGroupRepository.findByIdForUpdate(groupId)`.
- This serializes concurrent modifications to:
  - `member_order` assignment and the 50-member capacity limit.
  - `group_invitations.use_count` and guest-claiming (`user_id` binding).
  - `group_wallets` balance checks prior to `REFUND` or `GROUP_FUND` expense creation.
  - Net balance recomputation prior to `Settlement` validation or member departure/removal.
- Optimistic `@Version` (`BaseEntity.version`) protects individual entities from stale-screen edits.

### 2. Deterministic Split Allocation (`ExpenseSplitCalculator`)
- Converts `expense.amount` into integer minor units (`multiplier = 1` for `VND`, `100` for `USD`).
- Validates scale before conversion: `VND` rejects fractional decimals (`scale > 0`); `USD` rejects more than 2 decimal places (`scale > 2`).
- Computes allocations:
  - `EQUAL`: `base = totalMinor / n`, `remainder = totalMinor % n`; assigns `+1` minor unit to the first `remainder` participants ordered by ascending `splitOrder`. `inputValue` stored as `NULL`.
  - `EXACT`: Validates each `inputValue >= 0` in valid currency scale and checks `sum(inputs) == expense.amount`.
  - `PERCENTAGE`: Validates `0.00 <= pct <= 100.00` (scale `<= 2`) and `sum(pct) == 100.00`. Computes exact integer floor via `totalMinor * basisPoints / 10_000` and distributes remaining minor units by largest remainder `(totalMinor * basisPoints) % 10_000`, breaking ties by ascending `splitOrder`.
  - `SHARES`: Validates each share integer in `1..1_000_000`. Computes floor `totalMinor * share / totalShares` and distributes remaining minor units by largest remainder `(totalMinor * share) % totalShares`, breaking ties by ascending `splitOrder`.

### 3. Smart Debt Simplification (`DebtSimplificationEngine`)
- Inputs: Map of `memberId -> netBalance` computed as:
  $$\text{netBalance}(m) = \sum \text{Paid}_{\text{MEMBERS}}(m) - \sum \text{Split}_{\text{MEMBERS}}(m) + \sum \text{SettledPaid}(m) - \sum \text{SettledReceived}(m)$$
- Partition members into two `PriorityQueue`s (using minor-unit `long` amounts):
  - **Max Creditors Heap**: `netBalance > 0`, ordered by descending `netBalance` (tie-broken by `memberOrder` / UUID for determinism).
  - **Max Debtors Heap**: `netBalance < 0`, storing `abs(netBalance)` ordered descending.
- Iteratively pop top creditor `c` and top debtor `d`, emit transfer `d -> c` for `min(c.credit, d.debt)`, and push back any remaining non-zero residue.
- Complexity: $O(V \log V)$ for $V$ members (`V <= 50`), typically executing in `< 0.5ms`.

### 4. Idempotency Protocol (`IdempotencySupport`)
- Client sends a UUID `requestId` on `POST /fund/entries`, `POST /expenses`, and `POST /settlements`.
- Backend computes `requestHash = SHA-256(canonicalPayloadJson)` anchored to `(actorUserId, groupId, endpoint, normalizedFields)`.
- Inside the locked transaction, service looks up `requestId` first:
  - Not found → execute business logic and insert with `request_id` and `request_hash`.
  - Found with matching `request_hash` (and caller belongs to the same group) → return the previously stored DTO without re-applying mutations.
  - Found with different `request_hash` (or from another group) → throw `409 Conflict` (`IDEMPOTENCY_CONFLICT`).

---

## Complexity Tracking

> **Zero constitutional violations. All technical choices align with `.specify/memory/constitution.md` and `database_design.md` Final v1.0.**

| Aspect | Architectural Choice | Justification |
|---|---|---|
| Debt Simplification | Greedy two-heap matching over dynamic net balances ($O(V \log V)$) | Avoids persisting stale pairwise debt tables, guarantees value conservation, and achieves $\ge 30\%$ transfer reduction in $< 5\text{ms}$ for up to 50 members. |
| Concurrency Control | Pessimistic `SELECT ... FOR UPDATE` on `groups` row | Guarantees linearizability of 50-member cap, non-negative group fund balance, invitation `use_count`, and over-settlement prevention across concurrent requests. |
| Monetary Arithmetic | Integer minor-unit `long` arithmetic inside `ExpenseSplitCalculator` and `DebtSimplificationEngine`, persisted as `BigDecimal` / `NUMERIC(19,2)` | Eliminates IEEE-754 floating-point drift and guarantees exact zero-lost-cent conservation. |
