# FinSync Constitution

## Core Principles

### I. Non-Custodial & Manual Settlement (Core Business Principle)
FinSync MUST operate strictly as a manual bookkeeping, debt calculation, and financial advisory engine. The system MUST NEVER directly store or access real banking credentials, hold user funds, or trigger automated bank account deductions or transfers. All financial settlements MUST be executed externally by users and manually recorded within FinSync for transparent reconciliation.

### II. Mobile Client Architecture
Mobile client applications MUST maintain clean, reactive architectures and standardized networking libraries. Android clients MUST use Kotlin with Jetpack Compose for UI, Kotlin Coroutines for asynchronous execution, and Retrofit for HTTP communications. Cross-platform mobile clients MUST use Flutter (Dart) with BLoC or Riverpod state management and Dio HTTP client.

### III. Backend Service Architecture
The core backend API service MUST be built on Spring Boot 4.1.1 with Java 21, adhering to a RESTful layered architecture (Controller → Service → Repository). Security MUST be implemented using Spring Security with stateless JWT authentication. Database migrations MUST be managed via Flyway, and API documentation MUST be generated and exposed via SpringDoc OpenAPI (Swagger).

### IV. AI & Data Parsing Microservice Isolation
AI features and notification data extraction MUST be isolated in a dedicated Python (FastAPI) microservice. This service is exclusively responsible for Notification Regex parsing algorithms (extracting structured transaction data from bank SMS and device notifications) and constructing formatted prompts for Google Gemini API and OpenAI API integrations.

### V. Web Admin Architecture
The administrative portal MUST be implemented as a modern React.js web application. It MUST provide system management capabilities exclusively for platform health monitoring, usage telemetry, and user account governance.

### VI. Managed Persistence & Relational Schema
All primary persistent storage MUST utilize PostgreSQL 16 (cloud instance or Docker container) accessed through Spring Data JPA / Hibernate ORM. Direct manual schema modifications in production or staging environments are PROHIBITED; all schema changes MUST be managed via versioned Flyway migration scripts.

## Technology Stack & Technical Constraints

### Approved Stack Baseline
- **Mobile**: Kotlin + Jetpack Compose + Coroutines + Retrofit (Android) / Dart + Flutter + BLoC/Riverpod + Dio (Cross-Platform).
- **Backend API**: Java 21 + Spring Boot 4.1.1 + Spring Security (JWT) + Flyway + SpringDoc OpenAPI.
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

**Version**: 1.0.1 | **Ratified**: 2026-10-20 | **Last Amended**: 2026-10-22