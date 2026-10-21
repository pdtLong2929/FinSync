<<<<<<< HEAD
# [PROJECT_NAME] Constitution
<!-- Example: Spec Constitution, TaskFlow Constitution, etc. -->

## Core Principles

### [PRINCIPLE_1_NAME]
<!-- Example: I. Library-First -->
[PRINCIPLE_1_DESCRIPTION]
<!-- Example: Every feature starts as a standalone library; Libraries must be self-contained, independently testable, documented; Clear purpose required - no organizational-only libraries -->

### [PRINCIPLE_2_NAME]
<!-- Example: II. CLI Interface -->
[PRINCIPLE_2_DESCRIPTION]
<!-- Example: Every library exposes functionality via CLI; Text in/out protocol: stdin/args → stdout, errors → stderr; Support JSON + human-readable formats -->

### [PRINCIPLE_3_NAME]
<!-- Example: III. Test-First (NON-NEGOTIABLE) -->
[PRINCIPLE_3_DESCRIPTION]
<!-- Example: TDD mandatory: Tests written → User approved → Tests fail → Then implement; Red-Green-Refactor cycle strictly enforced -->

### [PRINCIPLE_4_NAME]
<!-- Example: IV. Integration Testing -->
[PRINCIPLE_4_DESCRIPTION]
<!-- Example: Focus areas requiring integration tests: New library contract tests, Contract changes, Inter-service communication, Shared schemas -->

### [PRINCIPLE_5_NAME]
<!-- Example: V. Observability, VI. Versioning & Breaking Changes, VII. Simplicity -->
[PRINCIPLE_5_DESCRIPTION]
<!-- Example: Text I/O ensures debuggability; Structured logging required; Or: MAJOR.MINOR.BUILD format; Or: Start simple, YAGNI principles -->

## [SECTION_2_NAME]
<!-- Example: Additional Constraints, Security Requirements, Performance Standards, etc. -->

[SECTION_2_CONTENT]
<!-- Example: Technology stack requirements, compliance standards, deployment policies, etc. -->

## [SECTION_3_NAME]
<!-- Example: Development Workflow, Review Process, Quality Gates, etc. -->

[SECTION_3_CONTENT]
<!-- Example: Code review requirements, testing gates, deployment approval process, etc. -->

## Governance
<!-- Example: Constitution supersedes all other practices; Amendments require documentation, approval, migration plan -->

[GOVERNANCE_RULES]
<!-- Example: All PRs/reviews must verify compliance; Complexity must be justified; Use [GUIDANCE_FILE] for runtime development guidance -->

**Version**: [CONSTITUTION_VERSION] | **Ratified**: [RATIFICATION_DATE] | **Last Amended**: [LAST_AMENDED_DATE]
<!-- Example: Version: 2.1.1 | Ratified: 2025-06-13 | Last Amended: 2025-07-16 -->
=======
<!--
SYNC IMPACT REPORT
- Version Change: Initial -> 1.0.0
- Added Principles:
  1. Non-Custodial & Manual Settlement (Core Business Principle)
  2. Mobile Client Architecture (Kotlin/Compose & Flutter/BLoC/Riverpod)
  3. Backend Service Architecture (Spring Boot 3 / Java 21 REST Layered Architecture)
  4. AI & Data Parsing Microservice Isolation (FastAPI Python Service)
  5. Web Admin Portal Architecture (React.js Administrative Dashboard)
  6. Managed Persistence & Relational Schema (PostgreSQL 16 + Flyway)
- Added Sections: Technology Stack & Technical Constraints, Development Workflow & Quality Gates
- Removed Sections: None (replaced unfilled template)
- Follow-up TODOs: None
-->

# FinSync Constitution

## Core Principles

### I. Non-Custodial & Manual Settlement (Core Business Principle)
FinSync MUST operate strictly as a manual bookkeeping, debt calculation, and financial advisory engine. The system MUST NEVER directly store or access real banking credentials, hold user funds, or trigger automated bank account deductions or transfers. All financial settlements MUST be executed externally by users and manually recorded within FinSync for transparent reconciliation.

### II. Mobile Client Architecture
Mobile client applications MUST maintain clean, reactive architectures and standardized networking libraries. Android clients MUST use Kotlin with Jetpack Compose for UI, Kotlin Coroutines for asynchronous execution, and Retrofit for HTTP communications. Cross-platform mobile clients MUST use Flutter (Dart) with BLoC or Riverpod state management and Dio HTTP client.

### III. Backend Service Architecture
The core backend API service MUST be built on Spring Boot 3 with Java 21, adhering to a RESTful layered architecture (Controller → Service → Repository). Security MUST be implemented using Spring Security with stateless JWT authentication. Database migrations MUST be managed via Flyway, and API documentation MUST be generated and exposed via SpringDoc OpenAPI (Swagger).

### IV. AI & Data Parsing Microservice Isolation
AI features and notification data extraction MUST be isolated in a dedicated Python (FastAPI) microservice. This service is exclusively responsible for Notification Regex parsing algorithms (extracting structured transaction data from bank SMS and device notifications) and constructing formatted prompts for Google Gemini API and OpenAI API integrations.

### V. Web Admin Architecture
The administrative portal MUST be implemented as a modern React.js web application. It MUST provide system management capabilities exclusively for platform health monitoring, usage telemetry, and user account governance.

### VI. Managed Persistence & Relational Schema
All primary persistent storage MUST utilize PostgreSQL 16 (cloud instance or Docker container) accessed through Spring Data JPA / Hibernate ORM. Direct manual schema modifications in production or staging environments are PROHIBITED; all schema changes MUST be managed via versioned Flyway migration scripts.

## Technology Stack & Technical Constraints

### Approved Stack Baseline
- **Mobile**: Kotlin + Jetpack Compose + Coroutines + Retrofit (Android) / Dart + Flutter + BLoC/Riverpod + Dio (Cross-Platform).
- **Backend API**: Java 21 + Spring Boot 3 + Spring Security (JWT) + Flyway + SpringDoc OpenAPI.
- **AI Microservice**: Python + FastAPI + Google Gemini API / OpenAI API.
- **Web Admin Portal**: React.js.
- **Database**: PostgreSQL 16 + Spring Data JPA / Hibernate ORM.
- **DevOps & Infrastructure**: Docker & Docker Compose, Git, GitHub Actions CI/CD, Jira Software.

### Security & Compliance Requirements
- Zero banking credential collection or storage allowed anywhere in application code, logs, or databases.
- Stateless authentication enforced via short-lived JWTs.
- Environment-based configuration management with strict `.env.example` templates and zero committed credentials.

## Development Workflow & Quality Gates

### Quality & Governance Standards
- All database migrations MUST be checked into version control as Flyway migration files prior to merge.
- OpenAPI definitions MUST be updated concurrently with any REST API contract changes.
- CI/CD via GitHub Actions MUST enforce automated build validation and container build verification.
- Feature requests, bug fixes, and development workflows MUST map directly to Jira Software issues.

## Governance

This Constitution supersedes all informal architectural practices and conventions across all FinSync projects and repositories. Amendments to this Constitution require explicit rationale documentation, semantic versioning increments, and an updated Sync Impact Report.

- **MAJOR**: Incompatible governance policy changes or fundamental stack/architectural re-definitions.
- **MINOR**: Addition of new core principles, technology layers, or structural constraints.
- **PATCH**: Non-semantic clarifications, typographical corrections, and minor wording refinements.

**Version**: 1.0.0 | **Ratified**: 2026-10-20 | **Last Amended**: 2026-10-20
>>>>>>> 49d9e32 (add: add .specify/ and specs/)
