# FinSync - Non-Functional Requirements Specification (NFRS)

> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*

---

## 1. Document Overview & Quality Philosophy

### 1.1. Purpose & Standards Alignment
This document establishes the non-functional requirements (NFRs) and quality attribute benchmarks for **FinSync**, following the international **ISO/IEC 25010** software product quality standard. Developed by **Group 04** for **CS300 (HCMUS)**, FinSync prioritizes financial data integrity, impenetrable boundary security, sub-second transaction responsiveness, and high accessibility.

### 1.2. The Zero-Trust & Pure Bookkeeping Paradigm
Because FinSync manages sensitive financial tracking for individuals and shared groups:
- **No Real Banking Credentials:** The system never requests, stores, or transmits credit card CVVs, online banking PINs, or direct bank login credentials.
- **Strict Multi-Tenant Isolation:** Users must never be able to inspect, query, or infer personal financial records of another user via Insecure Direct Object References (IDOR).
- **Mathematical Zero-Sum Integrity:** All debt-splitting and ledger balance calculations must be strictly transactional and immune to floating-point rounding drifts.

---

## 2. NFR Traceability & Quality Benchmark Matrix

| NFR ID | Category | Quality Attribute | Measurable Metric / Benchmark Target | Verification Method |
|---|---|---|---|---|
| `NFR-SEC-01` | Security | Session Authentication | Stateless JWT (15-min Access Token, 7-day Refresh Token) | Security code audit & token test |
| `NFR-SEC-02` | Security | Credential Protection | Passwords hashed with BCrypt (cost factor $\ge 12$) | Penetration testing & static analysis |
| `NFR-SEC-03` | Security | Data Isolation & IDOR | 100% authorization checks on all wallet/group endpoints | Automated security test cases |
| `NFR-SEC-04` | Security | In-Transit Encryption | TLS 1.3 encryption on all external network communication | SSL Labs / OWASP ZAP scan |
| `NFR-SEC-05` | Security | Secret Governance | 0 secrets/keys in source control; 100% environment variables | Git secret scanning (TruffleHog) |
| `NFR-PERF-01`| Performance | API Latency | P95 latency $< 300\text{ ms}$, P99 latency $< 500\text{ ms}$ | JMeter / k6 load testing |
| `NFR-PERF-02`| Performance | Debt Algorithm Speed | Simplification of 50-member graph in $< 100\text{ ms}$ | JUnit benchmark testing |
| `NFR-PERF-03`| Performance | AI Advisory Latency | Complete monthly synthesis payload delivered $< 3.0\text{ s}$ | Async HTTP benchmark |
| `NFR-PERF-04`| Performance | Mobile Client Cold Start | App launch to interactive Dashboard $< 2.0\text{ s}$ | Android Vitals / Profiler |
| `NFR-PERF-05`| Performance | Database Concurrency | Zero deadlocks under 50 concurrent transactions | PostgreSQL stress testing |
| `NFR-AVAIL-01`| Availability | Backend Service Uptime | $\ge 99.5\%$ availability during academic evaluation | Uptime monitoring |
| `NFR-AVAIL-02`| Reliability | Offline Resilience | 100% offline personal transaction logging on Android (Room) | Network cutoff test |
| `NFR-AVAIL-03`| Reliability | Non-Destructive Sync | 0 lost or duplicated transactions upon reconnection | Sync stress verification |
| `NFR-AVAIL-04`| Reliability | ACID Transaction State | 100% rollback on failed multi-wallet or debt updates | Database failure injection |
| `NFR-USAB-01`| Usability | Quick Transaction Logging| Log an expense within $\le 3$ taps from Home Screen | User flow stopwatch audit |
| `NFR-USAB-02`| Usability | Currency & Number Format | Native VND dot formatting (`100.000 ₫`) and USD format | UI locale test |
| `NFR-USAB-03`| Usability | Accessibility Contrast | WCAG 2.1 AA compliance (contrast ratio $\ge 4.5:1$, touch $\ge 48\text{ dp}$) | Accessibility Scanner |
| `NFR-MAINT-01`| Maintainability| Code Modularity | Clean Architecture (Android) & Layered Service (Spring Boot) | SonarQube architecture audit |
| `NFR-MAINT-02`| Maintainability| Automated Test Coverage | $\ge 75\%$ line coverage on core business and split logic | Jacoco & PyTest coverage reports |
| `NFR-MAINT-03`| Portability | Containerization | 1-command startup via `docker-compose up -d` | Clean VM deployment verification |

---

## 3. Detailed Non-Functional Requirements by Category

### 3.1. Category 1: Security & Privacy (`NFR-SEC`)

#### NFR-SEC-01: Stateless JWT Authentication & Token Lifecycle
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** Access Token TTL: **15 minutes**; Refresh Token TTL: **7 days**; Signature algorithm: **HMAC-SHA256 (HS256)** or **RSA-SHA256 (RS256)**.
- **Specification:**
  1. All protected REST API endpoints require a valid HTTP header: `Authorization: Bearer <token>`.
  2. Access tokens are stateless and contain claims: `sub` (User UUID), `email`, `role`, and `exp`.
  3. When an access token expires, client requests a new token pair using the refresh token endpoint (`/api/v1/auth/refresh`).
  4. Revoked refresh tokens are stored in an in-memory blacklist (or Redis) and rejected immediately.
- **Verification Method:** Automated unit tests verifying 401 Unauthorized upon expired token and successful refresh cycle.

#### NFR-SEC-02: Cryptographic Password Storage (BCrypt)
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** BCrypt work factor (cost) $\ge 12$.
- **Specification:**
  1. Plaintext passwords must NEVER be logged, cached, transmitted unencrypted, or stored in any database column.
  2. User passwords must be salted with a cryptographically secure random salt and hashed using BCrypt.
  3. Authentication compares hashes using constant-time string comparison to prevent timing attacks.
- **Verification Method:** Database inspection ensuring all entries in the `password_hash` column begin with `$2a$12$` or `$2b$12$`.

#### NFR-SEC-03: Multi-Tenant Data Isolation & IDOR Prevention
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** 100% elimination of Insecure Direct Object References (IDOR).
- **Specification:**
  1. Every database query accessing a personal wallet, personal transaction, budget, or savings goal MUST enforce `WHERE user_id = :authenticatedUserId`.
  2. Every query accessing group ledgers MUST verify that the requesting user has an active membership record in `group_members` for the targeted `group_id`.
  3. Accessing another user's private entity ID must return HTTP `403 Forbidden` or `404 Not Found` without disclosing record existence.
- **Verification Method:** Negative integration security tests asserting HTTP 403 when User A requests User B's wallet UUID.

#### NFR-SEC-04: In-Transit Encryption & API Security
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** TLS 1.3 mandatory on all public endpoints; zero HTTP plaintext traffic allowed.
- **Specification:**
  1. All client-to-server and inter-service communication outside Docker internal networks must utilize HTTPS with TLS 1.3.
  2. Implement standard HTTP security headers: `Strict-Transport-Security`, `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Content-Security-Policy`.
  3. CORS policy on Spring Boot backend must strictly whitelist the Admin Web origin (`http://localhost:3000` / production domain).

#### NFR-SEC-05: Secret Governance & Environment Variable Isolation
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** 0 credentials, secrets, or private keys committed to Git.
- **Specification:**
  1. Database credentials, JWT secret keys, and Google Gemini API keys must be loaded exclusively via environment variables (`.env`).
  2. Repository contains `.env.example` with dummy values. `.env` is explicitly listed in `.gitignore`.
  3. Pre-commit hooks and automated CI pipelines run automated secret scanners (e.g., TruffleHog / GitGuardian).

---

### 3.2. Category 2: Performance & Efficiency (`NFR-PERF`)

#### NFR-PERF-01: API Endpoint Latency Benchmarks
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** 
  - Read queries (Dashboard, Wallets, Transactions list): **P95 $< 200	ext{ ms}$**, **P99 $< 400	ext{ ms}$**.
  - Write queries (Add Transaction, Split Bill): **P95 $< 300	ext{ ms}$**, **P99 $< 500	ext{ ms}$**.
  - Under concurrency load of **50 requests/second**.
- **Specification:**
  1. Heavy read queries must leverage database indexing on foreign keys (`user_id`, `group_id`, `created_at`).
  2. Endpoints return paginated responses for transaction lists (default page size: 20 records).
- **Verification Method:** Load testing script executed using k6 / Apache JMeter against the Spring Boot REST endpoints.

#### NFR-PERF-02: Algorithmic Complexity of Debt Simplification
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** Execution time $< 100	ext{ ms}$ for groups with up to 50 active members and 500 historical shared transactions.
- **Specification:**
  1. The debt simplification algorithm must run in $\mathcal{O}(V \log V + E)$ time, where $V$ is number of members and $E$ is number of pairwise debt edges.
  2. Algorithm executes in-memory on the backend service before persisting simplified settlement suggestions.
- **Verification Method:** JUnit 5 benchmark test generating synthetic graphs of 50 users and asserting execution time $< 100	ext{ ms}$.

#### NFR-PERF-03: Asynchronous AI Advisory Processing Latency
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Ngô Thái Hòa | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** AI Advisory payload generated and returned in $< 3.0	ext{ s}$ for a standard monthly log (up to 300 transactions).
- **Specification:**
  1. The FastAPI microservice communicates with Google Gemini API asynchronously using `asyncio` and HTTPX.
  2. System implements exponential backoff retry (up to 3 attempts) for external LLM API rate limits.
  3. If external LLM times out after 5.0 seconds, system returns a graceful fallback deterministic analytical summary without crashing.

#### NFR-PERF-04: Mobile Application Launch & Frame Rate
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** Cold start time $< 2.0	ext{ s}$; Warm start $< 1.0	ext{ s}$; UI rendering at steady **60 FPS** (frame drop $< 5\%$).
- **Specification:**
  1. Jetpack Compose recomposition must be optimized: avoid unnecessary recompositions using `@Immutable` and `remember` keys.
  2. Local Room cache renders cached dashboard data immediately while fetching remote updates in background.

---

### 3.3. Category 3: Reliability & Availability (`NFR-AVAIL`)

#### NFR-AVAIL-01: System Uptime & High Availability
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** Uptime $\ge 99.5\%$ during course evaluation periods.
- **Specification:**
  1. The backend services must run as Docker containers configured with automatic restart policies (`restart: unless-stopped`).
  2. Database health checks (`pg_isready`) monitor PostgreSQL status.

#### NFR-AVAIL-02: Offline-First Capability on Android
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** 100% of personal transactions can be recorded while disconnected from the Internet.
- **Specification:**
  1. Android client stores personal wallets, transactions, and categories locally in a SQLite/Room database.
  2. When offline, new transactions receive a temporary UUID and a sync flag `SYNC_PENDING`.
  3. UI updates local balances optimistically without blocking the user.

#### NFR-AVAIL-03: Non-Destructive Bidirectional Synchronization
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** 0 duplicate transactions and 0 data conflicts upon network reconnection.
- **Specification:**
  1. Android WorkManager dispatches pending transactions to the backend when network connectivity is restored.
  2. Backend implements idempotent transaction processing using client-generated idempotency keys/UUIDs.
  3. Timestamp preservation ensures historical chronological ordering remains accurate.

#### NFR-AVAIL-04: Database ACID Transaction Consistency
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** 100% rollback guarantee on multi-step financial modifications.
- **Specification:**
  1. Inter-wallet transfers, group expense splitting, and settlement operations must be wrapped in `@Transactional` blocks with isolation level `READ_COMMITTED`.
  2. If any step fails (e.g., balance deduction succeeds but credit fails), the entire transaction rolls back completely.
  3. Zero-sum debt invariant is verified before committing to the database.

---

### 3.4. Category 4: Usability & Accessibility (`NFR-USAB`)

#### NFR-USAB-01: Frictionless Expense Logging Flow
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** Logging a routine expense must take $\le 3$ user taps from the home screen and $< 10	ext{ seconds}$ total.
- **Specification:**
  1. Floating Action Button (FAB) `+` is accessible from all 4 primary bottom navigation tabs.
  2. Keyboard with decimal numpad opens automatically upon entering the Add Expense screen.
  3. Pre-selects default wallet and current timestamp automatically.

#### NFR-USAB-02: Currency & Number Formatting Localization
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** 100% correct monetary display adhering to Vietnamese and US accounting standards.
- **Specification:**
  1. **VND Standard:** Separated by periods, no decimal cents, followed by currency symbol: e.g., `1.250.000 ₫`.
  2. **USD Standard:** Separated by commas, two decimal places: e.g., `$1,250.00`.
  3. Input fields must automatically format numbers as the user types to prevent accidental zero omissions.

#### NFR-USAB-03: Accessibility & Ergonomics (WCAG 2.1 AA)
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** Color contrast ratio $\ge 4.5:1$ for normal text; Minimum touch target size $\ge 48 	imes 48	ext{ dp}$.
- **Specification:**
  1. All clickable icons and buttons must conform to Material 3 accessibility guidelines.
  2. Critical state changes (e.g., Budget exceeded) cannot rely solely on color; must include iconography and clear text copy.
  3. Support dynamic system font scaling up to 130% without text truncation.

---

### 3.5. Category 5: Maintainability, Portability & Compliance (`NFR-MAINT`)

#### NFR-MAINT-01: Architectural Layering & Clean Architecture
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** 0 circular dependencies; strict separation between Presentation, Domain, and Data layers.
- **Specification:**
  1. **Android Client:** Organized into Clean Architecture modules: `presentation` (Composables, ViewModels), `domain` (UseCases, Domain Models), `data` (Repositories, Room, Retrofit).
  2. **Spring Boot Backend:** Organized into strict layers: `controller` $	o$ `service` (interface + implementation) $	o$ `repository` $	o$ `entity`. Entities never leak directly into API responses; DTO records are mandatory.
  3. **AI Microservice:** Follows FastAPI recommended structure with Pydantic v2 schemas and modular prompt templates.

#### NFR-MAINT-02: Automated Test Coverage Benchmarks
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Vương Đắc Gia Khiêm | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** Line coverage $\ge 75\%$ on core business logic (debt split, budget alerts, ledger calculations).
- **Specification:**
  1. Core algorithmic services (debt simplification, split calculations) must achieve $\ge 90\%$ branch coverage.
  2. CI pipeline fails automatically if test coverage drops below the defined 75% threshold.
- **Verification Method:** Jacoco coverage reports for Java backend; MockK/JUnit reports for Kotlin Android.

#### NFR-MAINT-03: 1-Click Containerized Deployment Portability
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** 100% reproducible developer and evaluation environment launched via a single command:
  ```bash
  docker compose up -d
  ```
- **Specification:**
  1. `docker-compose.yml` orchestrates PostgreSQL 16 database, Spring Boot backend API, and FastAPI AI service.
  2. Persistent volume mapping ensures database state survives container restarts.
  3. Database schema migrations run automatically on startup via Flyway.

#### NFR-MAINT-04: Coding Style & Linting Compliance
> *Performed by: Vương Đắc Gia Khiêm | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*
- **Metric / Benchmark:** 0 critical lint errors in production builds.
- **Specification:**
  1. Android Kotlin follows official [Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).
  2. Spring Boot Java adheres to Google Java Style conventions.
  3. Python FastAPI adheres to [PEP 8](https://peps.python.org/pep-0008/) style guide (verified via `flake8` / `black`).
  4. All commits must follow Conventional Commits format (`feat`, `fix`, `docs`, `refactor`, `test`, `chore`) referencing Jira issue keys.
