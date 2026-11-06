# Implementation Plan: FinSync Core System

**Branch**: `001-finsync-core-system` | **Date**: 2026-11-04 | **Spec**: [specs/001-finsync-core-system/spec.md](spec.md)

**Input**: Feature specification from `specs/001-finsync-core-system/spec.md`

---

## Summary

The FinSync Core System delivers a non-custodial personal finance management and collaborative group expense platform. It enables:
1. Multi-wallet tracking (Cash, Bank, Credit Card) with strict credit debt accounting and category budget utilization alerts.
2. Group ledgers (up to 50 members) with single-payer expense distribution (Equal, Exact, Percentage), graph-simplifying debt minimization (reducing transfer counts by \(\ge 30\%\)), and manual settlement reconciliation with owner dispute overrides.
3. User-initiated bank notification/SMS text parsing and spending anomaly detection isolated in an AI microservice.
4. Administrative user account governance (immediate session locking) and system-wide category taxonomy management.

---

## Technical Context

**Language/Version**:
- Backend API: Java 21 LTS
- Mobile Client: Kotlin 2.0+ (Android)
- AI Microservice: Python 3.11+
- Web Admin Portal: JavaScript / TypeScript (Node.js 20 LTS)

**Primary Dependencies**:
- Backend: Spring Boot 4.1.1, Spring Security (Stateless JWT), Spring Data JPA / Hibernate ORM, Flyway 10+, SpringDoc OpenAPI 2.6+
- Mobile (Android): Jetpack Compose, Kotlin Coroutines & Flow, Retrofit 2, OkHttp 4, Room Database
- AI Microservice: FastAPI, Uvicorn, Google Gemini API SDK (`google-generativeai`), Pydantic v2
- Web Admin: React.js 18+, Axios, React Router v6, Tailwind CSS

**Storage**:
- PostgreSQL 16 (Relational schema managed strictly via Flyway migrations)
- Cloud Object Storage (S3 / GCS / MinIO for receipt image attachments)

**Testing**:
- Backend: JUnit 5, AssertJ, Mockito, Testcontainers (PostgreSQL), `MockMvc`
- AI Microservice: `pytest`, `httpx`
- Mobile: JUnit 4/5, Compose UI Test, MockWebServer

**Target Platform**:
- Mobile: Android 8.0+ (API Level 26+)
- Server/API: Linux / Docker Containers (amd64, arm64)
- Web Admin: Modern evergreen browsers (Chrome, Firefox, Safari, Edge)

**Project Type**: Distributed multi-tier system (Backend REST API + Isolated AI Microservice + Android Mobile Client + React Admin Web Portal).

**Performance Goals**:
- Debt minimization algorithm: Executed in < 5ms for ledgers up to 50 members.
- Manual transaction creation: User-flow completable in < 15 seconds (SC-001).
- Budget threshold notifications: Dispatched within 1 minute of transaction write (SC-003).
- Admin account lock propagation: Active sessions invalidated across all endpoints in < 2 seconds (SC-005).

**Constraints**:
- **Strict Non-Custodial Operation (Constitution I)**: No banking credentials, PINs, or automated fund transfer mechanisms stored or executed.
- **Precision**: Absolute financial precision using `NUMERIC(15, 2)` / `BigDecimal` (zero lost cents).
- **Group Capacity**: Upper limit of 50 members per shared ledger.
- **Notification Privacy**: User-initiated paste only (zero background notification listeners).

**Scale/Scope**:
- 10,000 active users, 1 million transaction volume analytics, rolling 3-month windows for anomaly detection.

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-checked after Phase 1 design.*

| Principle | Requirement | Plan Compliance Status | Notes |
|---|---|---|---|
| **I. Non-Custodial & Manual Settlement** | System MUST NEVER store banking credentials, hold user funds, or trigger automated deductions; settlements MUST be external and manually recorded. | **PASS** | FR-001, FR-009, and contracts guarantee non-custodial boundaries. Zero banking credentials stored. |
| **II. Mobile Client Architecture** | Android MUST use Kotlin with Jetpack Compose, Coroutines, and Retrofit. Cross-platform MUST use Flutter/Dio if adopted. | **PASS** | Android architecture designed with Jetpack Compose + Coroutines + Retrofit + Room. |
| **III. Backend Service Architecture** | Spring Boot 4.1.1 + Java 21, RESTful layered architecture (Controller → Service → Repository), stateless JWT, Flyway, SpringDoc OpenAPI. | **PASS** | Spring Boot 4.1.1 configuration verified in `pom.xml`, layered package hierarchy, Flyway migrations, OpenAPI 3 contracts generated. |
| **IV. AI Microservice Isolation** | AI and notification parsing MUST be isolated in dedicated Python (FastAPI) microservice (regex parsing + Gemini/OpenAI prompts). | **PASS** | `src/ai-service` defined using Python FastAPI, exposing dedicated `/parse-notification` and `/budget-advice` endpoints isolated from Spring Boot. |
| **V. Web Admin Architecture** | Modern React.js web application for platform health, telemetry, and user governance. | **PASS** | `src/admin-web` designated for administrative React app interacting with backend admin APIs. |
| **VI. Managed Persistence & Schema** | PostgreSQL 16 accessed through Spring Data JPA / Hibernate; all schema modifications via versioned Flyway scripts. | **PASS** | PostgreSQL 16 containerized; Flyway migrations (`db/migration/V1__*.sql`) define all schema structures; no direct manual DDL. |

*Gate Result*: **ALL 6 CONSTITUTIONAL GATES PASSED (100%)**. Zero violations.

---

## Project Structure

### Documentation (this feature)

```text
specs/001-finsync-core-system/
├── spec.md              # Requirements and validated feature specification
├── checklists/
│   └── requirements.md  # Specification quality checklist
├── plan.md              # Implementation plan (this file)
├── research.md          # Technical research & algorithmic decisions (Phase 0)
├── data-model.md        # Relational schema & entity definitions (Phase 1)
├── quickstart.md        # Runnable verification & validation scenarios (Phase 1)
└── contracts/           # API interface specifications (Phase 1)
    ├── backend-api.yaml     # Spring Boot REST API OpenAPI 3.0 specification
    └── ai-service-api.yaml  # FastAPI AI microservice OpenAPI 3.0 specification
```

### Source Code (Repository Layout)

```text
FinSync/
├── docker-compose.yml                     # Multi-service orchestration (Postgres, Backend, AI Service)
├── README.md
├── .specify/                              # Spec Kit configuration and constitution memory
├── specs/                                 # Feature specifications and design artifacts
└── src/
    ├── backend/                           # Core Backend API (Spring Boot 4.1.1 + Java 21)
    │   ├── pom.xml
    │   ├── Dockerfile
    │   └── src/
    │       ├── main/
    │       │   ├── java/com/finsync/
    │       │   │   ├── FinSyncApplication.java
    │       │   │   ├── common/            # Error handling, constants, global advice
    │       │   │   ├── security/          # JWT authentication filters & security config
    │       │   │   ├── user/              # User management & account governance
    │       │   │   ├── wallet/            # Multi-wallet & Net Worth service
    │       │   │   ├── transaction/       # Transaction logging & category management
    │       │   │   ├── budget/            # Budget thresholds & alert dispatchers
    │       │   │   ├── ledger/            # Shared ledgers, members & permissions
    │       │   │   ├── expense/           # Group expense splitting engine (Single Payer)
    │       │   │   ├── settlement/        # Debt minimization algorithm & confirmations
    │       │   │   └── admin/             # Platform telemetry & user lock controllers
    │       │   └── resources/
    │       │       ├── application.properties
    │       │       └── db/migration/      # Versioned Flyway SQL migration files
    │       │           ├── V1__init_schema.sql
    │       │           └── V2__seed_default_categories.sql
    │       └── test/java/com/finsync/
    │           ├── unit/                  # Unit tests (algorithms, services)
    │           └── integration/           # SpringBootTest with Testcontainers
    │
    ├── ai-service/                        # Isolated AI & Parsing Microservice (Python FastAPI)
    │   ├── Dockerfile
    │   ├── requirements.txt
    │   ├── main.py
    │   ├── parsers/                       # Vietnamese bank SMS regex matchers
    │   ├── services/                      # Google Gemini API prompt integration
    │   └── tests/                         # pytest test suite
    │
    ├── mobile/                            # Client Applications
    │   └── android/                       # Kotlin + Jetpack Compose Mobile App
    │       ├── build.gradle.kts
    │       └── app/src/main/
    │           └── java/com/finsync/mobile/
    │               ├── ui/                # Jetpack Compose screens, components, theme
    │               ├── domain/            # Use cases & domain models
    │               └── data/              # Retrofit API clients & Room local cache
    │
    └── admin-web/                         # Administrative Portal (React.js)
        ├── package.json
        ├── Dockerfile
        └── src/
            ├── components/                # Reusable UI components
            ├── pages/                     # Dashboard, Users, Categories, Telemetry
            └── services/                  # Backend REST API client (Axios)
```

**Structure Decision**: Multi-service microservice/client architecture aligned with the 6 approved stacks in `constitution.md`. Core business data and transactional workflows reside in `src/backend/`, isolated regex/AI models reside in `src/ai-service/`, mobile interactions in `src/mobile/android/`, and management portal in `src/admin-web/`.

---

## Complexity Tracking

> **Zero violations recorded. All technical choices strictly conform to the approved Baseline in `constitution.md`.**

| Aspect | Architectural Choice | Justification |
|---|---|---|
| Split Calculation | Single Payer model with greedy heap debt minimization | Guarantees \(O(V \log V)\) execution, preserves exact net balance conservation, and simplifies data structure without heavy linear programming solvers. |
| Notification Extraction | User-pasted text into FastAPI regex engine with Gemini fallback | Enforces non-custodial privacy, zero background OS battery drain, and isolates LLM prompts from core banking records. |
| Monetary Types | `NUMERIC(15,2)` / `BigDecimal` | Eliminates IEEE 754 floating-point inaccuracies, guaranteeing zero lost cents. |
