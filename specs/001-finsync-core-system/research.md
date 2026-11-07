# Research & Technical Decisions: FinSync Core System

**Feature Branch**: `001-finsync-core-system` | **Date**: 2026-11-04

---

## 1. Debt Minimization Algorithm (Cash Flow Simplification)

### Context & Problem
In shared ledgers (up to 50 members), multiple members record expenses with different split strategies. Without optimization, settling pairwise debts requires \(O(N^2)\) bilateral payments. The success criterion (SC-002) requires reducing the total number of transfers by at least 30%.

### Decision
Implement a greedy net-balance settlement algorithm (Bipartite Debt Simplification) in Spring Boot:
1. Compute the net balance for each member: \(Net_i = \sum \text{Paid}_i - \sum \text{Owed}_i\).
2. Filter members into two priority heaps:
   - `Creditors` (Net balance > 0), sorted descending by balance.
   - `Debtors` (Net balance < 0), sorted ascending (maximum negative balance).
3. Greedily match the largest debtor with the largest creditor:
   - Settle \(\min(|Net_{\text{debtor}}|, Net_{\text{creditor}})\).
   - Decrement both balances; if a balance reaches 0, remove the member from the heap; otherwise re-insert the remaining balance.
4. Repeat until all balances reach 0.

### Rationale
- **Time Complexity**: \(O(V \log V)\) where \(V \le 50\), executing in < 5ms.
- **Guarantee**: Produces at most \(V - 1\) transfers for \(V\) members, mathematically guaranteeing a \(\ge 30\%\) reduction for complex groups compared to raw pairwise debts.
- **Precision**: Operates strictly on `BigDecimal` with 2 decimal places to guarantee zero lost cents.

### Alternatives Considered
- *Pairwise raw debt matrix*: Rejected because it generates redundant reciprocal transfers (e.g., A pays B, B pays C).
- *Min-Cost Max-Flow / Integer Linear Programming*: Rejected because it is NP-hard for minimum transaction counts and adds unnecessary external solver dependencies for groups \(\le 50\).

---

## 2. AI Microservice & Notification Parsing Architecture

### Context & Problem
Constitution IV mandates isolating AI and notification data extraction in a dedicated Python (FastAPI) microservice. Users paste SMS / bank notifications (Vietcombank, Techcombank, MBBank, etc.) to extract structured transaction drafts without storing banking credentials.

### Decision
Implement `finsync-ai-service` using Python 3.11 + FastAPI:
1. **Tier 1 (Fast Regex Matcher)**: Deterministic regex patterns matching popular Vietnamese bank SMS formats (amount, balance, time, counterparty). Response latency < 10ms.
2. **Tier 2 (Gemini LLM Fallback)**: For unstructured or unfamiliar notifications, format prompts using Google Gemini API (`gemini-1.5-flash` or `gemini-pro`) to extract JSON with fields `{amount, date, description, suggested_category, counterparty}`.
3. **Privacy Boundary**: Strip phone numbers, OTP codes, and account numbers prior to sending prompts to LLM. Never retain or log private banking credentials.

### Rationale
- Complies strictly with Constitution IV and Principle I (Non-Custodial).
- Separates high-frequency Java backend business transactions from Python ML/AI pipelines.

### Alternatives Considered
- *In-process Java regex parser*: Rejected because it violates Constitution IV.
- *Background SMS Listener / Auto-Sync*: Rejected because Constitution I and clarification decisions prohibit background scraping; user-initiated paste ensures privacy and control.

---

## 3. Mobile Client Architecture (Android Kotlin + Jetpack Compose)

### Context & Problem
Constitution II mandates Kotlin with Jetpack Compose for Android client applications, using Coroutines and Retrofit for HTTP communications.

### Decision
Structure Android application under `src/mobile/android/` following Clean Architecture:
- **Presentation**: Jetpack Compose UI, ViewModels with Kotlin `StateFlow`, Navigation Compose, Material 3 design tokens.
- **Domain**: UseCases / Interactors encapsulating business logic (e.g., `LogTransactionUseCase`, `CalculateDebtPlanUseCase`).
- **Data**: Repositories, Retrofit 2 for Spring Boot REST API calls, Room Database for local offline cache and buffering pending transactions.
- **Async Execution**: Kotlin Coroutines + Structured Concurrency (`viewModelScope`).

### Rationale
- Complies with Constitution II.
- Reactive UI state handles offline synchronization smoothly without UI freezing.

---

## 4. Backend Service Architecture (Spring Boot 4.1.1 + Java 21)

### Context & Problem
Constitution III mandates Spring Boot 4.1.1 with Java 21, RESTful layered architecture (Controller → Service → Repository), Spring Security with stateless JWT, Flyway for database migrations, and SpringDoc OpenAPI for documentation.

### Decision
Structure backend under `src/backend/` as Maven multi-package layered architecture:
- `com.finsync.api.controller`: REST controllers with OpenAPI annotations (`@Tag`, `@Operation`).
- `com.finsync.service`: Transactional business logic annotated with `@Transactional`.
- `com.finsync.repository`: Spring Data JPA repositories.
- `com.finsync.model.entity`: JPA entities mapping PostgreSQL tables.
- `com.finsync.model.dto`: Request/Response records (Java 21 records).
- `com.finsync.security`: JWT filter, authentication provider, user details service.
- `src/main/resources/db/migration`: Flyway SQL migration scripts (`V1__init_schema.sql`, etc.).

### Rationale
- Complies with Constitution III and VI.
- Java 21 records provide immutable, concise DTO contracts.

---

## 5. Monetary Arithmetic & Relational Persistence

### Context & Problem
Financial systems require absolute precision. Floating point arithmetic (`float`/`double`) leads to rounding errors ("lost cents") in split calculations.

### Decision
- **Database**: PostgreSQL `NUMERIC(15, 2)` for all monetary balances, transaction amounts, and split portions.
- **Backend**: `java.math.BigDecimal` with `RoundingMode.HALF_UP` for rounding.
- **Split Remainder Rule**: When dividing amounts that do not split evenly (e.g. $10.00 / 3 = 3.3333...), assign $3.34 to the first debtor and $3.33 to the remaining members, guaranteeing \(\sum \text{splits} = \text{total}\).
- **Concurrency Control**: `@Version` column on `Ledger` entity to prevent race conditions during concurrent expense additions.

### Rationale
- Prevents balance discrepancies and satisfies Edge Case requirements in `spec.md`.
