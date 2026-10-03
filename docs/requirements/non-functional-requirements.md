# Non-Functional Requirements Specification

> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

## 1. Performance

| ID | Requirement | Target |
|---|---|---|
| NFR-1.1 | API response time for standard CRUD operations | ≤ 500ms |
| NFR-1.2 | API response time for report generation (PDF/Excel) | ≤ 3s |
| NFR-1.3 | AI advisor response time | ≤ 10s |
| NFR-1.4 | Concurrent users supported | ≥ 100 |

## 2. Security

| ID | Requirement |
|---|---|
| NFR-2.1 | All API endpoints (except auth) must require JWT authentication |
| NFR-2.2 | Passwords must be hashed using BCrypt |
| NFR-2.3 | JWT tokens must expire after a configurable duration (default: 24h) |
| NFR-2.4 | API keys and secrets must never be committed to version control |
| NFR-2.5 | The system must not connect to real bank accounts or process real payments |

## 3. Usability

| ID | Requirement |
|---|---|
| NFR-3.1 | Mobile app must support Android 8.0+ (API level 26+) |
| NFR-3.2 | Admin web panel must support modern browsers (Chrome, Firefox, Edge) |
| NFR-3.3 | The app must support Vietnamese (VND) and US Dollar (USD) currencies |
| NFR-3.4 | All user-facing error messages must be clear and actionable |

## 4. Reliability & Availability

| ID | Requirement |
|---|---|
| NFR-4.1 | System uptime target: 99% during the semester |
| NFR-4.2 | Database must be backed up regularly |
| NFR-4.3 | The system must handle invalid input gracefully without crashing |

## 5. Scalability

| ID | Requirement |
|---|---|
| NFR-5.1 | Database schema must support future addition of new transaction categories |
| NFR-5.2 | API architecture must allow adding new endpoints without modifying existing ones |

## 6. Maintainability

| ID | Requirement |
|---|---|
| NFR-6.1 | Code must follow consistent naming conventions and project structure |
| NFR-6.2 | All API endpoints must be documented via Swagger/OpenAPI |
| NFR-6.3 | Database migrations must be versioned using Flyway |
| NFR-6.4 | Unit test coverage target: ≥ 60% for business logic layer |
