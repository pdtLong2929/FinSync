# PA3-2026 Report — FinSync (Group 04)

**Course:** CS300 - CSC13002 - Introduction to Software Engineering  
**Assignment:** Project Assignment 3 (PA3-2026) — Sprint 3  
**Group:** Group 04  
**Project:** FinSync — Personal Finance & Group Expense Manager with AI Advisor  
**Sprint 3 period:** `2026-10-26 to 2026-11-08`

## Table of Contents
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`

- [Team Members](#team-members)
- [Part A — Revised Project Plan](#part-a--revised-project-plan)
- [Part B — Detailed Vision Document](#part-b--detailed-vision-document)
- [Part C — Use-Case Model](#part-c--use-case-model)
- [Part D — Use-Case Specifications and UI Prototypes](#part-d--use-case-specifications-and-ui-prototypes)
- [Part E — Implement One Functional Group Using Spec Kit](#part-e--implement-one-functional-group-using-spec-kit)
- [Part F — AI Usage Report and Weekly Report](#part-f--ai-usage-report-and-weekly-report)
- [Appendix A — Individual AI Usage Notes](#appendix-a--individual-ai-usage-notes)
- [Appendix B — Submission Checklist](#appendix-b--submission-checklist)


---

## Team Members
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`


All members participate as Full-stack Engineers. The roles below retain the PA2 baseline; actual PA3 task ownership is recorded in Section A.4.

| # | Student ID | Full Name | Email | Main Role |
|---|------------|-----------|-------|-----------|
| 1 | 24120087 | Phạm Định Tiểu Long | phamlongkh2006@gmail.com | Project Manager / Group Leader |
| 2 | 24120038 | Nguyễn Phú Đạt | nguyennphuudatt@gmail.com | UI/UX Designer & Frontend Lead |
| 3 | 24120403 | Nguyễn Lê Đức Nhật | nldnhat182006@gmail.com | Backend Lead |
| 4 | 24120342 | Vương Đắc Gia Khiêm | vuongkhiemvl10@gmail.com | QA Lead & DevOps |
| 5 | 24120051 | Ngô Thái Hòa | ngothaihoa235@gmail.com | AI Feature Lead & Documentation |

---

## Part A — Revised Project Plan
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

### A.1. Introduction and Revision Summary
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Purpose and planning baseline:** This revised Project Plan updates the baseline established in PA2 (revision `2f4fcf6`) to govern Sprint 3 / PA3 execution from October 26, 2026 to November 08, 2026. In accordance with the course specification, all project planning changes, risk adjustments, task decompositions, and build milestones are tracked and traced to the shared `Changes.md` file.

| Feedback / change ID | TA feedback or new information | Affected PA2 section | PA3 response and location | Entry in shared Changes.md | Review status |
|----------------------|--------------------------------|----------------------|---------------------------|----------------------------|---------------|
| PP-01 | Internal team review following retrospective on 2026-10-26 (no formal TA feedback received for PA2 as of 2026-11-02). | Section A.1 (Introduction) | Formalized baseline reference to PA2 commit `2f4fcf6` and established Sprint 3 planning horizon. | Section 1.2 (PP-01) | Accepted on 2026-10-28 by Ngo Thai Hoa |
| PP-02 | Risk reassessment regarding Spec Kit full-stack integration and high prototype screenshot volume. | Section A.3 (Organization & Risk) | Reassessed R2 (Scope creep) and R5 (Integration); introduced decentralized prototype capture protocol. | Section 1.3 (PP-02) | Accepted on 2026-10-29 by Ngo Thai Hoa |
| PP-03 | PA3 requirement to replace high-level targets with an actionable, dated Sprint 3 Work Breakdown Structure. | Section A.4.1–A.4.3 (Schedule & Tasks) | Decomposed Sprint 3 into 14 concrete work packages (S3-01 to S3-14) totaling 110 team hours with explicit capacity allocation. | Section 1.4 (PP-03) | Accepted on 2026-10-30 by Ngo Thai Hoa |
| PP-04 | PA3 specification requirement focusing implementation on one complete functional group via Spec Kit. | Section A.4.4–A.4.5 (Build Plan) | Realigned Build 1 acceptance criteria to mandate end-to-end execution of Account Management & Personal Finance Core via Spec Kit. | Section 1.5 (PP-04) | Accepted on 2026-10-31 by Ngo Thai Hoa |

### A.2. Project Overview
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Goals:** Deliver FinSync as a unified personal finance tracker, shared group expense splitter with debt simplification, and AI-powered financial advisor. For PA3, achieve a complete, demonstrable baseline comprising 12 functional Use Cases with 100% prototype screenshot coverage, end-to-end Spec Kit implementation of the core functional group, and rigorous Scrum tracking.

**Scope:** 
- *In Scope for PA3:* Full revision of Project Plan and Vision Document; complete specification of 12 Use Cases across Client, Admin, AI, and Data & Logic; UI prototype screens for basic and alternative flows; End-to-end implementation of Account Management & Core Financial Operations (UI + Spring Boot API + PostgreSQL persistence) driven by Spec Kit; narrated demonstration video.
- *Deferred Scope for Later Sprints:* Real-time OCR receipt scanning with multi-item extraction, complex multi-currency arbitrage reconciliation, and advanced AI multi-turn conversational advisory (scheduled for Sprints 4 and 5).

**Business boundary:** FinSync records finances, calculates debts, and issues reminders. It does not connect to real bank accounts or automatically transfer or deduct money (non-custodial bookkeeping model). Personal data is private; group data is shared only with authorized group members.

| Deliverable | Scope / acceptance criteria | Planned milestone | Owner | Output location |
|-------------|-----------------------------|-------------------|-------|-----------------|
| Revised Project Plan and Vision | Complete revised documents reflecting internal review and Spec Kit requirements | 2026-11-02 | Pham Dinh Tieu Long, Ngo Thai Hoa | `report.md` Parts A & B; `Changes.md` |
| Use-case model and specifications | 12 formal use-case specifications across Client, Admin, AI, Data & Logic | 2026-11-04 | All team members | `report.md` Parts C & D; `use-cases/` |
| UI prototypes | Screens for basic, alternative, and error flows across all use cases | 2026-11-05 | Nguyen Phu Dat, Pham Dinh Tieu Long | `screenshots/prototypes/` |
| Implemented functional group | End-to-end implementation (UI + API + PostgreSQL) driven by Spec Kit | 2026-11-06 | Nguyen Le Duc Nhat, Nguyen Phu Dat | `specs/001-finsync-core-system/`; `src/` |
| Sprint records and submission | 4 Scrum meeting minutes, Jira Sprint 3 board, Git history, narrated demo video | 2026-11-06 | Pham Dinh Tieu Long, Vuong Dac Gia Khiem | `report.md` Part F; `scrum-meeting-minutes.md` |

**Assumptions and dependencies:** Development environments standardized on Docker Compose (PostgreSQL 16, Redis 7); Backend standardized on Spring Boot 3 / Java 21; Frontend leverages React web and Android Jetpack Compose; team members commit 22 hours each during Sprint 3.

### A.3. Project Organization and Risk Management
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Team structure and responsibilities:**
- **Pham Dinh Tieu Long (Leader / PM):** Project Plan coordination, `Changes.md`, Admin & Account Use Cases, Scrum facilitation & minutes, Jira management, release packaging.
- **Nguyen Phu Dat (Frontend Lead):** UI Design System (Figma/v0), Client Use Cases, Frontend implementation, API integration.
- **Nguyen Le Duc Nhat (Backend Lead):** Spec Kit setup, PostgreSQL schema & migrations, Spring Boot REST APIs, Data & Logic Use Cases.
- **Vuong Dac Gia Khiem (QA / DevOps Lead):** Test matrix & E2E verification, video demo recording & narration, Git commit history compliance.
- **Ngo Thai Hoa (AI Feature Lead & Tech Writer):** Vision Document revisions, Mermaid Use Case diagrams, AI Use Cases, AI Usage Log aggregation.

**Working rules:** Each Jira task has exactly one assignee and a different reviewer. Create tasks before work starts, record creation/assignment/completion dates, and retain output and review evidence. Keep the repository private and configuration secrets outside source control. Use Conventional Commits and the agreed peer-review process.

| Risk ID | Risk / change since PA2 | Probability | Impact | Mitigation / contingency | Owner | Current status |
|---------|------------------------|-------------|--------|---------------------------|-------|----------------|
| R1 | Member unavailability or illness during sprint | Low | Medium | Enforce task handoff documentation; buddy review pairings (Long/Hoa, Dat/Nhat, Khiem/Nhat). | Long | Mitigated |
| R2 | Scope creep across functional modules | Medium | High | Strictly constrain PA3 implementation to exactly one functional group via Spec Kit; defer non-core features. | Long | Closed |
| R3 | Discrepancies between UI prototypes and use case specifications | Medium | Medium | Authors capture screenshots directly from interactive prototypes to ensure 100% data fidelity. | Dat / Long | Closed |
| R4 | Database schema and DTO contract drift between FE and BE | Medium | High | Freeze OpenAPI specification within first 48 hours; generate unified DTOs and Flyway migrations. | Nhat | Closed |
| R5 | Video demo audio and screen capture defects | Low | Medium | Rehearse demo flow with pre-populated seed data; apply microphone noise cancellation. | Khiem | Closed |
| R6 | Spec Kit artifact synchronization lag | Low | High | Follow strict Spec Kit lifecycle (Specify → Plan → Tasks → Implement) before business logic coding. | Nhat | Closed |

### A.4. Process, Schedule, and Sprint 3 Tasks
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Process:** Scrum; one sprint per PA, lasting 2–3 weeks. Hold one Sprint Planning, two Weekly Scrum meetings, and one Sprint Review/Retrospective. Record the actual meetings in Part F.

**Schedule baseline:** PA2 planned Sprint 3 for 26 October–8 November 2026, Sprint 4 for 9–22 November, and Sprint 5 for 23 November–6 December. These are team targets, not confirmed course deadlines.

| Sprint | Previous target | Updated dates | Scope / milestone | Reason for change |
|--------|-----------------|---------------|-------------------|-------------------|
| Sprint 3 / PA3 | 2026-10-26 to 2026-11-08 | 2026-10-26 to 2026-11-08 | Use Case models, 100% UI prototypes, Spec Kit core implementation, Build 1 | On schedule; narrow implementation scope to 1 functional group per PA3 specification |
| Sprint 4 / PA4 | 2026-11-09 to 2026-11-22 | 2026-11-09 to 2026-11-22 | Group Expense Management, Smart Debt Simplification, Build 2 | Aligned with semester course milestones |
| Sprint 5 / PA5 | 2026-11-23 to 2026-12-06 | 2026-11-23 to 2026-12-06 | AI Financial Advisory, Final Polish, Production Hardening, Release Candidate | Final submission and live defense |

**Sprint 3 Work Breakdown Structure (Tasks S3-01 to S3-14):**

| Task ID / Jira key | Work package and acceptance criteria | Assignee | Reviewer | Dependencies | Planned start | Due date | Effort (h) |
|--------------------|--------------------------------------|----------|----------|--------------|---------------|----------|------------|
| S3-01 / FS-PLAN-01 | Revise Project Plan & manage `Changes.md` Section 1 | Long | Hoa | None | 2026-10-26 | 2026-10-30 | 8 |
| S3-02 / FS-VIS-01 | Revise Vision Document & manage `Changes.md` Section 2 | Hoa | Nhat | S3-01 | 2026-10-27 | 2026-10-31 | 8 |
| S3-03 / FS-UCD-01 | Draw System-Wide Use Case Diagrams in Mermaid | Hoa | Khiem | S3-02 | 2026-10-29 | 2026-11-01 | 6 |
| S3-04 / FS-UCC-01 | Draft Client Use Cases (`UC-CLI-01` to `UC-CLI-12`) | Dat | Nhat | S3-03 | 2026-10-28 | 2026-11-02 | 10 |
| S3-05 / FS-UCA-01 | Draft Admin & Account Use Cases (`UC-ACC-01/02`, `UC-ADM-01..04`) | Long | Hoa | S3-03 | 2026-10-28 | 2026-11-02 | 10 |
| S3-06 / FS-UCL-01 | Draft Data/Logic & AI Use Cases | Nhat / Hoa | Long | S3-03 | 2026-10-29 | 2026-11-02 | 8 |
| S3-07 / FS-PROT-01 | Design UI Prototype Library (Figma / v0.dev / Bolt.new) | Dat | Long | S3-04 | 2026-10-28 | 2026-11-02 | 12 |
| S3-08 / FS-SPEC-01 | Initialize Spec Kit workflow & generate specifications | Nhat | Dat | S3-05 | 2026-10-29 | 2026-10-31 | 6 |
| S3-09 / FS-DB-01 | Design PostgreSQL schema & write Flyway DDL migrations | Nhat | Khiem | S3-08 | 2026-10-30 | 2026-11-02 | 8 |
| S3-10 / FS-BE-01 | Implement Spring Boot REST API for Core Functional Group | Nhat | Long | S3-09 | 2026-11-01 | 2026-11-04 | 12 |
| S3-11 / FS-FE-01 | Implement Client Frontend UI & API integration | Dat | Nhat | S3-10 | 2026-11-02 | 2026-11-05 | 12 |
| S3-12 / FS-QA-01 | Formulate E2E Test Scenarios & verify integration | Khiem | Nhat | S3-11 | 2026-11-03 | 2026-11-05 | 8 |
| S3-13 / FS-DEMO-01 | Record screencast & narrate Video Demonstration | Khiem | Long | S3-12 | 2026-11-04 | 2026-11-06 | 8 |
| S3-14 / FS-REL-01 | Aggregate AI Usage Logs, Jira/Git logs, package release | Long / Hoa | Team | S3-13 | 2026-11-05 | 2026-11-06 | 6 |

**Capacity Allocation Table (110 Team Hours Total):**

| Member | Available hours | Owned tasks | Review / meetings | Buffer | Planned total |
|--------|-----------------|-------------|-------------------|--------|---------------|
| Phạm Định Tiểu Long | 22 | 14 (S3-01, S3-05, S3-14) | 6 | 2 | 22 |
| Nguyễn Phú Đạt | 22 | 16 (S3-04, S3-07, S3-11) | 4 | 2 | 22 |
| Nguyễn Lê Đức Nhật | 22 | 16 (S3-08, S3-09, S3-10) | 4 | 2 | 22 |
| Vương Đắc Gia Khiêm | 22 | 14 (S3-12, S3-13) | 6 | 2 | 22 |
| Ngô Thái Hòa | 22 | 14 (S3-02, S3-03, S3-06) | 6 | 2 | 22 |

### A.5. Build Plan and Later Sprints
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Build sequence:** Build 0 (component skeletons) was verified complete in Sprint 2. Build 1 focuses strictly on delivering the core functional group end-to-end via Spec Kit. Subsequent builds incrementally layer group expense splitting and AI advisory capabilities.

| Build | Previous target | Updated target | Planned content | Acceptance criteria | Revision / result evidence |
|-------|-----------------|----------------|-----------------|---------------------|----------------------------|
| Build 1 | 2026-11-08 | 2026-11-06 | Account Management & Core Finance (UI + API + DB) | End-to-end registration, login, profile view/edit, wallet creation, transaction recording with persistent DB storage | Accepted via E2E test verification and narrated demo video |
| Build 2 | 2026-11-22 | 2026-11-22 | Group Expense & Smart Debt Settlement | Create group, invite members, add shared expense, calculate simplified debt graph, settlement confirmation | Planned for Sprint 4 |
| Build 3 — release candidate | 2026-11-29 | 2026-11-29 | AI Financial Advisory & Budget Alerts | Monthly AI insight generation, anomaly detection, category budget alerts | Planned for Sprint 5 |
| Final build | 2026-12-04 | 2026-12-04 | System Hardening & Performance Polish | Full regression test pass, load testing (<200ms latency), final security audit | Planned for Sprint 5 |

**Later-sprint tasks and dependencies:** Sprint 4 focuses on multi-user concurrency for group expense splitting, VietQR integration for debt settlements, and group audit logging. Sprint 5 focuses on AI LLM orchestration (prompt engineering, response caching, quota fallback) and final deployment.

---

## Part B — Detailed Vision Document
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Ngo Thai Hoa`*

### B.1. Introduction and Revision Summary
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Ngo Thai Hoa`*

**Purpose and audience:** This Detailed Vision Document defines the complete product vision, user personas, operational boundaries, and functional/non-functional requirements for FinSync. The audience includes course instructors, teaching assistants, and the Group 04 development team.

**Baseline and references:** Builds directly on PA2 Part B (revision `2f4fcf6`), incorporating the PA1 market survey, team contract, and course specification. Traced to `Changes.md` Section 2.

| Feedback / change ID | TA feedback or clarification | Affected requirement / section | Revision and rationale | Entry in shared Changes.md |
|----------------------|------------------------------|--------------------------------|------------------------|----------------------------|
| VD-01 | Team decision to establish formal traceable revision table (TA feedback on PA2 concerned missing Spec Kit files only). | Section 1 (Introduction & References) | Added revision summary and updated references to PA3 use-case documents. | Section 2.2 (VD-01) |
| VD-02 | Review of product positioning against PA3 use cases and UI prototypes. | Section 2 (Positioning) | Retained core problem statement and product positioning with zero content regression. | Section 2.3 (VD-02) |
| VD-03 | Traceability refinement for user personas and surveyed competitors. | Section 3 (Stakeholders & Alternatives) | Consolidated stakeholders into unified table; preserved Money Lover and Splitwise survey findings. | Section 2.4 (VD-03) |
| VD-04 | Formalized numbered business rules (accounting, currency, offline, AI). | Section 4 (Product Overview & Rules) | Numbered all rules (BR-AI-01..10, BR-SEC-01..03, BR-FIN-01..03); added Redis blacklist dependency. | Section 2.5 (VD-04) |
| VD-05 | Detailed functional requirements breakdown with acceptance criteria. | Section 5 (Functional Requirements) | Structured 11 functional groups with stable identifiers mapped to use cases in `use-cases/`. | Section 2.6 (VD-05) |
| VD-06 | Non-functional requirements verification and use-case traceability. | Section 6 (Non-Functional Requirements) | Mapped NFR-P1..P7, NFR-S1..S9, NFR-R1..R8 to verification methods and test criteria. | Section 2.7 (VD-06) |

### B.2. Positioning
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Ngo Thai Hoa`*

| Problem statement element | Revised description |
|---------------------------|---------------------|
| The problem of | Fragmented personal expense tracking and cumbersome group debt splitting across multiple disconnected tools. |
| Affects | University students, young professionals, roommates, and travel groups. |
| The impact of which is | Lack of financial visibility, delayed reimbursements, social friction during manual debt calculation, and absence of personalized financial guidance. |
| A successful solution would be | A single, non-custodial app uniting personal wallet tracking, automated group debt simplification (equal, percentage, exact), and intelligent AI budgeting advice without accessing real bank credentials. |

| Product position element | Revised description |
|--------------------------|---------------------|
| For / Who | For individuals and social groups who need unified personal budgeting and shared expense management. |
| The product / That | FinSync — an intelligent, cross-platform personal finance and group expense manager with an embedded AI financial advisor. |
| Unlike | Standalone personal finance apps (Money Lover) that lack social debt splitting, and standalone splitters (Splitwise) that lack personal wealth tracking and AI advisory. |
| Our product | Provides an integrated ecosystem combining personal wallets, offline-first transaction logging, debt graph simplification, and privacy-preserving AI insights in one application. |

### B.3. Stakeholders, Users, and Environments
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Ngo Thai Hoa`*

| Stakeholder / user | Needs and responsibilities | Environment / constraints | Priority |
|--------------------|----------------------------|---------------------------|----------|
| Regular User | Personal income/expense logging, budget tracking, receipt viewing, wallet transfers. | Android mobile app & responsive web; offline-first personal logging. | High |
| Group Owner / Member | Group creation, member invitation, shared expense logging, debt settlement confirmation. | Online connectivity required for shared group writes; VietQR integration for settlement. | High |
| Administrator | System user registry management, account lock/unlock, system taxonomies and reference currencies. | Desktop web admin console; role-based access control (SUPER_ADMIN / ADMIN). | Medium |
| Course Staff / TA | Verification of requirements coverage, Spec Kit adoption, code quality, and Scrum discipline. | Local Docker Compose, GitHub repository, reproducible build scripts. | High |

**User environment:** Primary client is an Android mobile app (API level 26+, Android 8.0+) written in Jetpack Compose, complemented by a responsive web client. Personal transactions support offline queueing with automated server reconciliation. Group operations require active internet connectivity. Supported currencies: VND (default reporting base) and USD.

### B.4. Alternatives and Competition
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Ngo Thai Hoa`*

| Need / capability | Money Lover — surveyed behavior | Splitwise — surveyed behavior | FinSync — planned response | Evidence / source |
|-------------------|----------------------------------|--------------------------------|---------------------------|-------------------|
| Personal Budgeting | Comprehensive personal wallets, category budgets, visual analytics. | Absent; focuses strictly on group shared ledgers. | Native multi-wallet management, category spending budgets, and trend visualization. | PA1 App Survey Section 2.1 |
| Group Debt Splitting | Limited shared wallets; no automated debt graph minimization. | Full multi-member splitting (equal, exact, shares) with debt simplification algorithm. | Multi-way expense splitting with minimum-cash-flow graph debt simplification. | PA1 App Survey Section 2.2 |
| AI Advisory & Insights | None or basic canned categorization tips. | None. | Privacy-preserving AI financial advisor providing monthly spending evaluations and anomaly detection. | FinSync Feature Specification |
| Bank Connectivity & Custody | Requires linked banking credentials in premium tiers. | Non-custodial; manual settlement confirmation. | 100% non-custodial bookkeeping model; zero banking credential access; recipient-confirmed settlement. | PA2 Vision Baseline B.2 |

### B.5. Product Overview, Business Rules, and Dependencies
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Ngo Thai Hoa`*

**Product perspective:** FinSync is structured as a multi-tier distributed system comprising an Android client (Jetpack Compose), a Web client (React.js), a Spring Boot 3 backend with PostgreSQL 16 persistence and Redis 7 session/blacklist caching, and an external LLM AI advisory service.

| Rule ID | Business rule / constraint | Related requirements | Rationale / PA2 reference | Change status |
|---------|----------------------------|----------------------|--------------------------|---------------|
| BR-FIN-01 | Non-Custodial Bookkeeping: All recorded payments are informational logs; FinSync never holds user funds or initiates banking transfers. | FR-WAL-01, FR-TXN-01 | PA2 Section B.4.1 | Retained |
| BR-FIN-02 | Recipient-Confirmed Settlement: Group debt is only reduced when the intended recipient explicitly confirms receiving payment outside the app. | FR-GRP-03, UC-CLI-12 | PA2 Section B.4.1 | Retained |
| BR-FIN-03 | Default Category Archiving: System default categories cannot be hard-deleted if referenced by historical transactions; they are soft-archived. | FR-ADM-04, UC-ADM-04 | Admin Specification | New |
| BR-SEC-01 | Role-Gated Admin Access: Administrative endpoints strictly require `SUPER_ADMIN` or `ADMIN` authority; client tokens are rejected with 403 Forbidden. | FR-AUTH-02, UC-ADM-01 | Security Architecture | Retained |
| BR-SEC-02 | Immediate Session Revocation on Account Lock: When an admin locks an account, active JWT sessions are immediately invalidated via Redis blacklist. | FR-ADMIN-03, UC-ADM-03 | Security Requirement | New |
| BR-AI-01 | Aggregated Data Only: AI Advisor requests contain only anonymized monthly category spending totals; no raw transaction notes or PII are transmitted. | FR-AI-01, UC-AI-01 | Privacy Policy NFR-S7 | Retained |
| BR-AI-02 | Non-Automated Financial Effects: AI advice or budget suggestions require explicit user confirmation before any category budget limit is modified. | FR-AI-04, UC-AI-04 | PA2 Section B.4.1 | Retained |

### B.6. Detailed Functional Requirements
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Ngo Thai Hoa`*

The 11 functional groups defined in the Vision baseline are mapped directly to four modularized use-case catalogues in `use-cases/`:

| Functional group from PA2 | Coverage to detail | Requirement IDs | Primary Catalogue Location |
|--------------------------|--------------------|-----------------|----------------------------|
| Authentication & Security | Registration, login/logout, JWT auth, session blacklist | FR-AUTH-01..03 | `use-cases/admin-account-use-cases.md` & `client-use-cases.md` |
| Profile Management | User profile, display name, avatar, currency preference | FR-PROF-01..02 | `use-cases/admin-account-use-cases.md` (UC-ACC-02) |
| Wallet & Personal Accounts | Multi-wallet creation, balance tracking, wallet transfers | FR-WAL-01..03 | `use-cases/client-use-cases.md` (UC-CLI-02, 03) |
| Personal Transaction Tracking | Income/expense logging, edits, deletion, category filtering | FR-TXN-01..04 | `use-cases/client-use-cases.md` (UC-CLI-04..07) & `data-logic-use-cases.md` |
| Group Management & Shared Wallets | Group creation, member invitations, shared fund ledgers | FR-GRP-01..03 | `use-cases/client-use-cases.md` (UC-CLI-10) & `data-logic-use-cases.md` |
| Smart Debt Split & Settlement | Equal/exact/shares split, debt graph simplification, settlement | FR-SPLIT-01..03 | `use-cases/client-use-cases.md` (UC-CLI-11, 12) & `data-logic-use-cases.md` |
| Budget Planning & Alerting | Category budget thresholds, 80%/100% spending alerts | FR-BUD-01..02 | `use-cases/client-use-cases.md` (UC-CLI-08) & `data-logic-use-cases.md` |
| Financial Reporting & Analytics | Monthly trend charts, category breakdowns, PDF/CSV export | FR-REP-01..02 | `use-cases/client-use-cases.md` (UC-CLI-09) & `data-logic-use-cases.md` |
| Savings Goal Management | Savings target creation, contribution tracking | FR-SAV-01..02 | `use-cases/client-use-cases.md` & `uc-ai-features.md` (UC-AI-05) |
| System Administration | User registry, lock/unlock, system taxonomy & currency settings | FR-ADM-01..04 | `use-cases/admin-account-use-cases.md` (UC-ADM-01..04) |
| AI Financial Advisor | Monthly spending evaluation, anomaly detection, budget advice | FR-AI-01..06 | `use-cases/uc-ai-features.md` (UC-AI-01..05) |

### B.7. Detailed Non-Functional Requirements
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Ngo Thai Hoa`*

| NFR ID | Category | Requirement and measurable target | Conditions / environment | Verification method | Related use cases | Change from PA2 |
|--------|----------|------------------------------------|--------------------------|---------------------|-------------------|-----------------|
| NFR-P1 | Performance | API response time $\le 200\text{ ms}$ for 95th percentile under normal load. | Spring Boot backend, PostgreSQL, 100 concurrent virtual users | JMeter load testing | Core API endpoints | Retained |
| NFR-P7 | Performance | Paginated admin user registry query $\le 500\text{ ms}$ with 100,000 user rows. | Indexed `email`, `status`, `created_at` in PostgreSQL | SQL EXPLAIN ANALYZE | UC-ADM-02 | Added (replaces NFR-PERF-01) |
| NFR-S1 | Security | Password hashing using BCrypt with minimum work factor of 10. | Spring Boot Security | Unit test verification | UC-ACC-01, UC-ADM-01 | Retained |
| NFR-S3 | Security | JWT access token validity of 60 minutes; refresh token validity of 7 days. | Client auth storage & Redis session | Auth integration test | UC-ACC-01, UC-ADM-01 | Retained |
| NFR-S7 | Security | AI prompt payloads transmit aggregated category totals only; zero PII. | JSON prompt builder | Security code inspection | UC-AI-01..05 | Retained |
| NFR-R4 | Reliability | AI service timeout at 30 seconds; graceful fallback to basic rule-based analysis. | External LLM unavailable or network partition | Fault injection test | UC-AI-01 | Refined |

---

## Part C — Use-Case Model
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Vuong Dac Gia Khiem` | Edited by: `Pham Dinh Tieu Long`*

### C.1. System Boundary and Actors
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Vuong Dac Gia Khiem` | Edited by: `Pham Dinh Tieu Long`*

**System boundary:** FinSync, including its mobile app, backend, database, admin panel, and internal AI service. External payments occur outside this boundary.

| Actor | Role / goals | Scope and permissions |
|-------|--------------|-----------------------|
| Regular User | Personal finance tracking and participation in groups | Own personal data and authorized group data |
| Group Owner | Regular User acting as group administrator | Group membership, settings, and shared expense oversight |
| Group Member | Regular User participating in a shared expense group | Split expenses, view simplified debts, confirm payments |
| Administrator | System operator and compliance administrator | System user registry, account locking, global categories |
| System Scheduler | Automated background execution service | Triggers monthly analysis, debt reminders, and batch jobs |
| External LLM API | Generative AI inference provider (Gemini / Claude) | Receives aggregated financial prompts; returns advisory text |

### C.2. Use-Case Catalog and Requirement Traceability
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Vuong Dac Gia Khiem` | Edited by: `Pham Dinh Tieu Long`*

| Use-case ID | Use-case name / user goal | Actor(s) | Functional requirement IDs | Diagram | Specification location | Prototype screen IDs |
|-------------|--------------------------|----------|----------------------------|---------|------------------------|----------------------|
| UC-CLI-01 | View Financial Overview Dashboard | Regular User | FR-REP-01 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S03 |
| UC-CLI-02 | Manage Personal Wallets | Regular User | FR-WAL-01 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S04 |
| UC-CLI-03 | Transfer Funds Between Wallets | Regular User | FR-WAL-03 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S04 |
| UC-CLI-04 | Record Income or Expense Transaction | Regular User | FR-TXN-01 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S05 |
| UC-CLI-05 | Search and Filter Transactions | Regular User | FR-TXN-04 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S04 |
| UC-CLI-06 | View and Edit Transaction | Regular User | FR-TXN-02 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S04 |
| UC-CLI-07 | Delete and Restore Transaction | Regular User | FR-TXN-03 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S04 |
| UC-CLI-08 | View Budget Status and Spending Limits | Regular User | FR-BUD-01 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S04 |
| UC-CLI-09 | View and Filter Financial Reports | Regular User | FR-REP-02 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S05 |
| UC-CLI-10 | Create, Join, and View Shared Groups | Group Owner/Member | FR-GRP-01 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S05 |
| UC-CLI-11 | Add and Split Group Expense | Group Member | FR-SPLIT-01 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S06 |
| UC-CLI-12 | View Debts and Settle Group Debts | Group Member | FR-SPLIT-02 | Diagram C.1 | `use-cases/client-use-cases.md` | S01–S05 |
| UC-ACC-01 | Sign In to Personal Account | Regular User | FR-AUTH-02 | Diagram C.2 | `use-cases/admin-account-use-cases.md` | S01–S05 |
| UC-ACC-02 | View and Update Personal Profile | Regular User | FR-PROF-01..02 | Diagram C.2 | `use-cases/admin-account-use-cases.md` | S01–S05 |
| UC-ADM-01 | Sign In to Admin Console & Dashboard | Administrator | FR-AUTH-02, FR-ADMIN-01 | Diagram C.2 | `use-cases/admin-account-use-cases.md` | S01–S06 |
| UC-ADM-02 | Search, Filter, and View User Registry | Administrator | FR-ADMIN-02 | Diagram C.2 | `use-cases/admin-account-use-cases.md` | S01–S05 |
| UC-ADM-03 | Lock and Unlock User Account | Administrator | FR-ADMIN-03 | Diagram C.2 | `use-cases/admin-account-use-cases.md` | S01–S06 |
| UC-ADM-04 | Manage Default Taxonomies & Currency | Administrator | FR-ADMIN-04 | Diagram C.2 | `use-cases/admin-account-use-cases.md` | S01–S06 |
| UC-AI-01 | Generate AI Financial Analysis | Regular User | FR-AI-01 | Diagram C.3 | `use-cases/uc-ai-features.md` | S01–S04 |
| UC-AI-02 | Generate Monthly Analysis Automatically | System Scheduler | FR-AI-02 | Diagram C.3 | `use-cases/uc-ai-features.md` | S01–S03 |
| UC-AI-03 | View Spending Anomaly Alerts | Regular User | FR-AI-03 | Diagram C.3 | `use-cases/uc-ai-features.md` | S01–S03 |
| UC-AI-04 | View and Apply Optimized Budget Advice | Regular User | FR-AI-04 | Diagram C.3 | `use-cases/uc-ai-features.md` | S01–S04 |
| UC-AI-05 | View Savings Goal Evaluation | Regular User | FR-AI-05 | Diagram C.3 | `use-cases/uc-ai-features.md` | S01–S03 |
| UC-DL-02 | Record, Edit and Delete Personal Txns | Regular User | FR-DL-02 | Diagram D.1b | `use-cases/data-logic-use-cases.md` | DL-02 |
| UC-DL-05 | Record & Confirm Shared Contributions | Group Member | FR-DL-05 | Diagram D.1b | `use-cases/data-logic-use-cases.md` | DL-05 |
| UC-DL-06 | Record and Split a Group Expense | Group Member | FR-DL-06 | Diagram D.1b | `use-cases/data-logic-use-cases.md` | DL-06 |
| UC-DL-07 | View Debts and Confirm Settlement | Group Member | FR-DL-07 | Diagram D.1b | `use-cases/data-logic-use-cases.md` | DL-07 |
| UC-DL-08 | Record Expense Refunds & Return Funds | Group Owner/Member | FR-DL-08 | Diagram D.1b | `use-cases/data-logic-use-cases.md` | DL-08 |
| UC-DL-12 | Sync Offline Changes & Resolve Conflicts | Regular User | FR-DL-12 | Diagram D.1b | `use-cases/data-logic-use-cases.md` | DL-12 |
| UC-DL-13 | Generate Reports On-Demand or Batches | Regular User/Scheduler | FR-DL-13 | Diagram D.1b | `use-cases/data-logic-use-cases.md` | DL-13 |
| UC-DL-14 | Evaluate Budget Alerts & Batch Reminders| System Scheduler | FR-DL-14 | Diagram D.1b | `use-cases/data-logic-use-cases.md` | DL-14 |

**Coverage review:** All functional requirements declared in Part B map to formal specifications and screenshot evidence. Prototype screens across all 4 modules are verifiable in `screenshots/prototypes/`.

### C.3. Mermaid Use-Case Diagrams
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Vuong Dac Gia Khiem` | Edited by: `Pham Dinh Tieu Long`*

To systematically cover all functional requirements (FRs) defined in the Vision Document while maintaining visual clarity, the use-case model is partitioned into three formal Mermaid diagrams:
1. **Diagram C.1 — Client Personal & Group Finance Subsystem** (Core user journeys, multi-wallet isolation, transaction ledger, category budgets, group expense splitting, and debt settlements).
2. **Diagram C.2 — Admin Governance & Account Lifecycle Subsystem** (Authentication session management, profile synchronization, administrative user directory, and account locking).
3. **Diagram C.3 — AI Financial Advisor & Analytics Subsystem** (AI-driven financial health analysis, automated monthly evaluation, anomaly spike detection, and budget optimization).

#### Diagram C.1 — Client Personal & Group Finance Subsystem
```mermaid
flowchart LR
    actorUser["Actor: Regular User"]
    actorMember["Actor: Group Member"]
    actorOwner["Actor: Group Owner"]

    actorMember -->|"generalization"| actorUser
    actorOwner -->|"generalization"| actorMember

    subgraph FinSyncClient["FinSync — Client Personal & Group Finance Subsystem"]
        cli01(["UC-CLI-01: View Financial Overview Dashboard"])
        cli02(["UC-CLI-02: Manage Personal Wallets"])
        cli03(["UC-CLI-03: Transfer Funds Between Wallets"])
        cli04(["UC-CLI-04: Record Income or Expense Transaction"])
        cli05(["UC-CLI-05: Search and Filter Transactions"])
        cli06(["UC-CLI-06: View and Edit Transaction"])
        cli07(["UC-CLI-07: Delete and Restore Transaction"])
        cli08(["UC-CLI-08: View Budget Status and Limits"])
        cli09(["UC-CLI-09: View Financial Reports"])
        cli10(["UC-CLI-10: Create, Join & View Shared Groups"])
        cli11(["UC-CLI-11: Add and Split Group Expense"])
        cli12(["UC-CLI-12: View Debts and Settle Debts"])
    end

    actorUser --- cli01
    actorUser --- cli02
    actorUser --- cli03
    actorUser --- cli04
    actorUser --- cli05
    actorUser --- cli06
    actorUser --- cli07
    actorUser --- cli08
    actorUser --- cli09
    actorMember --- cli10
    actorMember --- cli11
    actorMember --- cli12
    actorOwner --- cli10

    cli03 -.->|"«include»"| cli02
    cli11 -.->|"«include»"| cli04
    cli12 -.->|"«include»"| cli03
    cli08 -.->|"«extend»: Spending >80%"| cli04
    cli07 -.->|"«extend»: Undo within 7s"| cli06
```

#### Diagram C.2 — Admin Governance & Account Lifecycle Subsystem
```mermaid
flowchart LR
    actorUser["Actor: Regular User"]
    actorAdmin["Actor: Administrator"]
    
    subgraph FinSyncAdmin["FinSync — Admin & Account Subsystem"]
        acc01(["UC-ACC-01: Sign In Personal Account"])
        acc02(["UC-ACC-02: View and Update Profile"])
        adm01(["UC-ADM-01: Admin Sign In & Dashboard Overview"])
        adm02(["UC-ADM-02: User Registry Search & Filter"])
        adm03(["UC-ADM-03: Lock and Unlock User Account"])
        adm04(["UC-ADM-04: System Taxonomies & Currency Settings"])
    end
    
    actorUser --- acc01
    actorUser --- acc02
    actorAdmin --- adm01
    actorAdmin --- adm02
    actorAdmin --- adm03
    actorAdmin --- adm04

    adm03 -.->|"«include»"| adm02
    acc02 -.->|"«extend»: Password change requested"| acc01
```

#### Diagram C.3 — AI Financial Advisor Subsystem
```mermaid
flowchart LR
    actorUser["Actor: Regular User"]
    actorSched["Actor: System Scheduler"]
    actorLLM["Actor: External LLM API"]
    
    subgraph FinSyncAI["FinSync — AI Financial Advisor"]
        uc01(["UC-AI-01: Generate AI Financial Analysis"])
        uc02(["UC-AI-02: Generate Monthly Analysis Automatically"])
        uc03(["UC-AI-03: View Spending Anomaly Alerts"])
        uc04(["UC-AI-04: View and Apply Optimized Budget Allocation"])
        uc05(["UC-AI-05: View Savings Goal Progress Evaluation"])
    end

    actorUser --- uc01
    actorSched --- uc02
    actorUser --- uc03
    actorUser --- uc04
    actorUser --- uc05

    uc01 -.->|"«include»"| actorLLM
    uc02 -.->|"«include»"| actorLLM
    uc03 -.->|"«extend»: Spending spike >30%"| uc01
    uc04 -.->|"«extend»: Budget optimization requested"| uc01
```

| Diagram ID | Functional groups covered | Mermaid block / document location | Relationship rationale |
|------------|---------------------------|------------------------------------|------------------------|
| Diagram C.1 | Wallets, Transactions, Budgets, Groups, Debt Splitting | Section C.3 (Client Subsystem) | Actor generalization (`Group Owner` $\rightarrow$ `Group Member` $\rightarrow$ `Regular User`); inclusion of wallet validation in transfers; inclusion of transaction ledger in group splits; conditional extension when budget limit exceeds 80%. |
| Diagram C.2 | Authentication, Profile, Admin System | Section C.3 (Admin Subsystem) | Actor associations and administrative inclusion of user registry during account lock; conditional extension for credential re-verification. |
| Diagram C.3 | AI Financial Advisory, Anomaly Alerts | Section C.3 (AI Advisor) | External LLM inclusion; conditional extensions when spending anomalies exceed 30% threshold. |
| Diagram D.1b | Data & Logic, Splitting, Settlements | Section D.1b | Generalization from Group Owner to Regular User; scheduler triggers for automated monthly batches and reminders. |

---

## Part D — Use-Case Specifications and UI Prototypes
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Pham Dinh Tieu Long`*

**Weight:** 45 points. Complete one specification for **every** modeled use case, including reused or extending use cases. Keep them here or in linked Markdown files submitted with the report. Screenshots of all prototype screens must appear alongside the corresponding specification; a design-tool link alone is insufficient.

### D.1. Specification and Prototype Conventions
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Pham Dinh Tieu Long`*

**Ordering:** Specifications and visual evidence are grouped into four specialized catalogues matching the system decomposition in Section C.2:
1. Client Use Cases (`use-cases/client-use-cases.md`) — Core user journeys for wallets, budgeting, transactions, groups, and settlements.
2. Data & Logic Use Cases (`use-cases/data-logic-use-cases.md`) — Financial accounting, batch jobs, and synchronization rules.
3. Admin & Account Use Cases (`use-cases/admin-account-use-cases.md`) — User authentication, profile management, and admin oversight.
4. AI Financial Advisor Use Cases (`use-cases/uc-ai-features.md`) — AI evaluations, anomaly detection, and budget optimization.

**Design tool(s):** Figma (Master Design System & mobile wireframes), v0.dev (Interactive component prototyping), and Bolt.new.  
**Prototype project / access link:** Catalogued in [Client Prototype Link](use-cases/client-prototype-link.md) and local prototype source in `prototypes/`.  
**Screen naming:** Standardized as `S<NUMBER>.png` within each dedicated use case directory: `screenshots/prototypes/<USE-CASE-ID>/`.  
**Suggested screenshot location:** `screenshots/prototypes/<use-case-id>/` relative to this report.

Each basic-flow and alternative-flow screen/state must be identifiable. Shared screens may be reused, but embed and explain the relevant screenshots with each specification. Refine any specification whose prototype cannot express its stated behavior coherently.

### D.1a. Regular User / Client Use Cases and Prototype Evidence
> *Performed by: `Nguyễn Phú Đạt` (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

The Client contribution contains **12 use-case specifications (UC-CLI-01 through UC-CLI-12)** and **65 prototype evidence PNGs**, mapped to Basic, Alternative and Exception flows. Detailed specifications and all embedded figures are in the [Client catalogue](use-cases/client-use-cases.md).

**Evidence source: Figma Make Prototype — Simulated UI.** These are browser prototype captures using simulated, in-memory financial data, not Android emulator screenshots. No backend integration, PostgreSQL persistence, real JWT authentication, real device behavior or native Android pixel accuracy is verified. Imported validation claims belong to the supplied archive; no browser scenario tests were rerun during this documentation integration.

| Client documentation | Review entry point |
|---|---|
| Twelve specifications and per-UC coverage | [Client catalogue](use-cases/client-use-cases.md) |
| Scenario-to-image mapping | [Screenshot matrix](screenshots/prototypes/client-evidence/Screenshot-Matrix.csv) |
| Verification, provenance and limitations | [Client evidence audit](screenshots/prototypes/client-evidence/client-evidence-audit.md) |
| Supplied prototype URL and scope caveat | [Prototype reference](use-cases/client-prototype-link.md) |

All three flow categories have supplied image coverage for each Client use case. This does not certify every source-spec path or live Figma interaction. Identical final-state images for cancelled edit, cancellation of deletion and Undo do not independently prove the intermediate actions. Existing Account/Admin and other subsystem contributions retain their own ownership and evidence.

### D.1b. Data & Logic Use Cases


> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**FinSync · Group 04 · PA3-2026 · Version 2.0 (compact), 2026-11-04.** This contribution follows [the Part D requirements](pa3_project_assignment_specification.md) and the five-subsection [report template](report.md). Detailed API, database-locking and job-runtime design is kept separately in [Backend Notes](use-cases/D_backend_notes.md).

## D.1. Scope and Shared Conventions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Scope:** Eight core use cases cover financial processing, shared-fund claims, splitting, settlement, refunds, synchronization, reporting and batch notifications. Existing IDs are preserved; gaps in numbering do not indicate missing cases. Part C is still a team template, so this is a scoped catalogue for integration, not a claim that the team has approved its final use-case model.

Wallet setup, member administration, basic navigation, searches and form details are referenced from the [Client catalogue](use-cases/client-use-cases.md), rather than specified twice. Account/Admin and AI remain separate contributions. Budget creation and savings-goal management are outside this narrowed assignment and still need explicit team coverage; Client budget viewing alone does not satisfy those requirements. On-demand reporting (former UC-DL-11) is merged into UC-DL-13.

**Business baseline:** Preserve PA2 bookkeeping-only payments, exact rounding, recipient/custodian confirmation, historical currency snapshots, own-group-share recognition and durable offline personal changes. The narrower database v1 differs in these areas; [the appendix](use-cases/D_backend_notes.md) records the gaps. This document does not silently approve scope reductions or claim those backend functions are implemented.

**Prototypes:** [Local HTML wireframes](prototypes/data-logic/index.html) and labeled PNG exports are newly authored specification designs, not captures proving implemented features. Each case reuses six frames for its form/review/result and domain variants; common dialogs are shown once below. Alternative flows specify the content/action variant in that reused frame. Amounts and rates are illustrative.
| Use case | Goal / related client scope | Local requirement |
|---|---|---|
| UC-DL-02 | Record, Edit and Delete Personal Transactions | FR-DL-02 |
| UC-DL-05 | Record and Confirm Shared-Fund Contributions | FR-DL-05 |
| UC-DL-06 | Record and Split a Group Expense | FR-DL-06 |
| UC-DL-07 | View Debts and Confirm a Settlement | FR-DL-07 |
| UC-DL-08 | Record Expense Refunds and Return Unused Funds | FR-DL-08 |
| UC-DL-12 | Synchronize Offline Personal Changes and Resolve Conflicts | FR-DL-12 |
| UC-DL-13 | Generate Financial Reports on Demand or Automatically | FR-DL-13 |
| UC-DL-14 | Evaluate Budget Alerts and Send Debt Reminders in Batches | FR-DL-14 |

FR-DL IDs are local traceability labels to merge into Part B; they are not asserted to be already approved requirement IDs.

```mermaid
flowchart LR
    user["Regular User / Group Member"]
    owner["Group Owner / custodian"]
    scheduler["Deployment Scheduler"]
    operator["Authorized Operator"]
    owner -->|"generalization"| user
    subgraph FinSync["FinSync — Data & Logic"]
        u2(["UC-DL-02: Record, Edit and Delete Personal Transactions"])
        u5(["UC-DL-05: Record and Confirm Shared-Fund Contributions"])
        u6(["UC-DL-06: Record and Split a Group Expense"])
        u7(["UC-DL-07: View Debts and Confirm a Settlement"])
        u8(["UC-DL-08: Record Expense Refunds and Return Unused Funds"])
        u12(["UC-DL-12: Synchronize Offline Personal Changes and Resolve Conflicts"])
        u13(["UC-DL-13: Generate Financial Reports on Demand or Automatically"])
        u14(["UC-DL-14: Evaluate Budget Alerts and Send Debt Reminders in Batches"])
    end
    user --- u2
    user --- u5
    owner --- u5
    user --- u6
    owner --- u6
    user --- u7
    user --- u8
    owner --- u8
    user --- u12
    user --- u13
    scheduler --- u13
    operator --- u13
    scheduler --- u14
    operator --- u14
```

Actors are outside FinSync; database/worker internals are not actors. Generalization denotes inherited participation. No include/extend relationship is forced onto validation steps; cross-referenced workflows have separate triggers.

### D.1.1. Common Alternative and Exception Flows
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

C1–C5 apply wherever their condition is relevant; each case references this table instead of repeating it. Scheduled runs have no user form to discard; their explicit stop/recovery branch applies instead.

| ID / condition | Actions and outcome | Return / shared screen |
|---|---|---|
| C1 — Cancel before confirmation | Keep editing or discard the uncommitted draft; no new effect. | Step 2 or end; DL-C01 |
| C2 — Session/permission changed | Stop the action; reauthenticate if permitted, otherwise deny without exposing data. | Step 1 or end; DL-C02 |
| C3 — Stale review/concurrent edit | Show current values, retain a safe draft and request fresh confirmation; no silent overwrite. | Steps 2–3; DL-C04 |
| C4 — Network/service failure | Do not claim success; reconcile the original request outcome before retry. Confirmed items stay committed; unresolved items remain pending/failed. | Step 4 or end; DL-C05 |
| C5 — Duplicate submission/replay | Return the existing acknowledged result rather than apply the action again. | Step 5; DL-C06 |

![Shared cancellation, validation and recovery dialogs](screenshots/prototypes/data-logic/DL-COMMON-compact.png)

*Shared prototype: DL-C01–06. These same dialogs are referenced by the specifications below; a new screen is not required for each backend branch.*

## D.2. UC-DL-02 — Record, Edit and Delete Personal Transactions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*


### D.2.1. Overview and Preconditions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Field | Specification |
|---|---|
| Use-case ID and name | UC-DL-02 — Record, Edit and Delete Personal Transactions |
| Primary actor | Regular User |
| Supporting actor(s) | None. |
| Description / goal | Maintain income and expense records, optional receipts and correct balances without duplicate posting. |
| Trigger | The user selects Add transaction or opens an owned transaction. |
| Related requirements / rules | FR-DL-02; bookkeeping only, authorized data, exact amounts and no duplicate consumption. |
| Related NFRs | NFR-S4, NFR-S6, NFR-S8, NFR-R3, NFR-R5, NFR-R6 |
| Relationships | Actor associations in D.1; shared C1–C5 apply as relevant. No mandatory included sub-use-case. |
| Preconditions | 1. User has an authorized session, or an unlocked encrypted cache after initial sign-in for the offline branch.<br>2. An owned wallet and applicable category exist.<br>3. Group records are not edited through this flow. |


### D.2.2. Basic Flow — Main Success Scenario
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Step | Actor action | System response / state change | Screen |
|---|---|---|---|
| 1 | The user selects Add transaction or opens an owned transaction. | Check access; show eligible scope and current state. | S01 |
| 2 | Enter amount, wallet, category, date and note; optionally attach a receipt. | Validate the selected inputs and prepare a review; no financial effect yet. | S01 |
| 3 | Review and confirm the action. | Review a 120,000 VND Food expense and the selected wallet. | S02 |
| 4 | Wait for the acknowledged result. | Validate ownership and category type; atomically record the transaction and its attachment references; update derived totals. | S03 |
| 5 | Open the authorized result or status. | One expense appears in history; Cash decreases by 120,000 VND and Food spending increases once. | S03 |


### D.2.3. Alternative and Exception Flows
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Flow / branch point | Ordered actions | Resume / resulting state | Screen variant |
|---|---|---|---|
| AF-01 — Income or edit<br>Step 2: change type or open an existing entry | 1. Choose an income category or edit allowed values. 2. Recalculate affected wallet/category totals; keep the original ID when editing. | 3 — Only the difference changes totals. | S04 |
| AF-02 — Delete / Undo<br>Step 2: Delete selected | 1. Confirm deletion and recalculate. 2. Offer Undo for 7 seconds; valid Undo restores the same ID/values once. If expired or unavailable, return to history without recreating it. | 5 / end — Deleted or restored once. | S04 |
| AF-03 — Offline or receipt pending<br>Step 4: network/upload unavailable | 1. Save an authorized personal change locally as Pending and invoke UC-DL-12 on reconnection. 2. If only the receipt fails after posting, retry the upload without another financial entry. | End / UC-DL-12 — Local and server status remain distinct. | S04 |
| AF-04 — Link a group movement<br>Step 2: money relates to a group event | 1. Select the authorized source event. 2. Link the movement rather than creating another consumption entry; the user's share is counted once. | 3 — Cash and consumption are not duplicated. | S05 / S06 |
| AF-05 — Invalid input or reference<br>Step 3: bad amount/date/category/receipt or ineligible wallet | 1. Identify the invalid field/file or reference. 2. Retain valid input and ask for correction; reject the whole invalid write. | 2 — No invalid financial effect. | S05 / S06 |

**Shared branches:** C1–C5 from D.1.1 where applicable. Use DL-C03 for field validation; use S06 for the case-specific pending/recovery result. Domain branches above take precedence where a conflict, offline state or scheduled stop needs different behavior.

### D.2.4. Postconditions and Special Requirements
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Success:** One expense appears in history; Cash decreases by 120,000 VND and Food spending increases once.  
**Failure/cancellation:** No unauthorized or partially committed financial change. Pending claims do not change confirmed balances; acknowledged sync/batch items remain completed while other items can fail.  
**Special requirements:** NFR-S4, NFR-S6, NFR-S8, NFR-R3, NFR-R5, NFR-R6 Monetary values and historical rates must remain exact. These are requirements, not measured test results.

### D.2.5. UI Prototype Screenshots
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Prototype:** [Compact wireframes v2](prototypes/data-logic/index.html#UC-DL-02). Screen IDs below use the `UC-DL-02-` prefix. Shared dialogs DL-C01–06 are embedded once in D.1.1.

| Frames | Flow coverage | Purpose |
|---|---|---|
| S01–S03 | Basic steps 1–5 | Input/scope, review and acknowledged result. |
| S04 | AF-01, AF-02, AF-03 | Alternative actions use the same entry/review layout with the selected variant. |
| S05–S06 | AF-04, AF-05; pending/recovery states | Changed values, unavailable data or incomplete outcome; no extra screen per internal check. |

![UC-DL-02 — six reusable prototype frames](screenshots/prototypes/data-logic/UC-DL-02-compact.png)

*Figure D.2 — UC-DL-02-S01–S06. Labeled design variants illustrate the specified flows; they are not proof of executed backend behavior.*

## D.3. UC-DL-05 — Record and Confirm Shared-Fund Contributions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*


### D.3.1. Overview and Preconditions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Field | Specification |
|---|---|
| Use-case ID and name | UC-DL-05 — Record and Confirm Shared-Fund Contributions |
| Primary actor | Group Member |
| Supporting actor(s) | Group Owner acting as fund custodian. |
| Description / goal | Record money contributed outside the app and increase the shared fund only after the custodian confirms receipt. |
| Trigger | A member opens the group fund and selects Contribute. |
| Related requirements / rules | FR-DL-05; bookkeeping only, authorized data, exact amounts and no duplicate consumption. |
| Related NFRs | NFR-S4, NFR-R3, NFR-R6, NFR-R7 |
| Relationships | Actor associations in D.1; shared C1–C5 apply as relevant. No mandatory included sub-use-case. |
| Preconditions | 1. Both actors have online authorized group sessions.<br>2. The group fund exists and is active.<br>3. Actual payment occurs outside FinSync. |


### D.3.2. Basic Flow — Main Success Scenario
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Step | Actor action | System response / state change | Screen |
|---|---|---|---|
| 1 | A member opens the group fund and selects Contribute. | Check access; show eligible scope and current state. | S01 |
| 2 | Enter the amount/date and declare that the external contribution was made. | Validate the selected inputs and prepare a review; no financial effect yet. | S01 |
| 3 | Review and confirm the action. | Review a 500,000 VND contribution claim; explain that it does not increase the fund yet. | S02 |
| 4 | Group Owner verifies and confirms receipt. | Save a pending claim; notify the Owner; the Owner opens it, verifies receipt and confirms; atomically post the fund credit. | S03 |
| 5 | Open the authorized result or status. | Fund balance becomes 1,500,000 VND; Laura's confirmed contribution position increases by 500,000; no expense is created. | S03 |


### D.3.3. Alternative and Exception Flows
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Flow / branch point | Ordered actions | Resume / resulting state | Screen variant |
|---|---|---|---|
| AF-01 — Pending / rejected / withdrawn<br>Step 4: no confirmation, rejection or contributor withdrawal | 1. Keep a new claim Pending until the Owner responds. 2. On rejection store the reason; allow withdrawal only while Pending. | End — Confirmed fund remains unchanged. | S04 |
| AF-02 — Invalid claim or missing rate<br>Step 3: invalid amount/date or foreign currency | 1. Validate a positive contribution. 2. Show original amount and approved quote; if no rate exists, defer the conversion-dependent claim rather than inventing a total. | 2 / 3 — Only valid reviewed claims proceed. | S04 |
| AF-03 — Wrong confirmer / repeated decision<br>Step 4: another member confirms or status already changed | 1. Require the fund custodian. 2. Recheck claim status; a prior confirmation returns its existing result, a conflicting decision is rejected. | End — One receipt effect at most. | S04 |
| AF-04 — History / empty fund<br>Step 1: inspect fund without contributing | 1. Show confirmed cash and member positions separately from pending claims. 2. Show zero if empty; unused-fund return opens UC-DL-08. | End — No ledger mutation. | S05 / S06 |

**Shared branches:** C1–C5 from D.1.1 where applicable. Use DL-C03 for field validation; use S06 for the case-specific pending/recovery result. Domain branches above take precedence where a conflict, offline state or scheduled stop needs different behavior.

### D.3.4. Postconditions and Special Requirements
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Success:** Fund balance becomes 1,500,000 VND; Laura's confirmed contribution position increases by 500,000; no expense is created.  
**Failure/cancellation:** No unauthorized or partially committed financial change. Pending claims do not change confirmed balances; acknowledged sync/batch items remain completed while other items can fail.  
**Special requirements:** NFR-S4, NFR-R3, NFR-R6, NFR-R7 Monetary values and historical rates must remain exact. These are requirements, not measured test results.

### D.3.5. UI Prototype Screenshots
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Prototype:** [Compact wireframes v2](prototypes/data-logic/index.html#UC-DL-05). Screen IDs below use the `UC-DL-05-` prefix. Shared dialogs DL-C01–06 are embedded once in D.1.1.

| Frames | Flow coverage | Purpose |
|---|---|---|
| S01–S03 | Basic steps 1–5 | Input/scope, review and acknowledged result. |
| S04 | AF-01, AF-02, AF-03 | Alternative actions use the same entry/review layout with the selected variant. |
| S05–S06 | AF-04; pending/recovery states | Changed values, unavailable data or incomplete outcome; no extra screen per internal check. |

![UC-DL-05 — six reusable prototype frames](screenshots/prototypes/data-logic/UC-DL-05-compact.png)

*Figure D.3 — UC-DL-05-S01–S06. Labeled design variants illustrate the specified flows; they are not proof of executed backend behavior.*

## D.4. UC-DL-06 — Record and Split a Group Expense
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*


### D.4.1. Overview and Preconditions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Field | Specification |
|---|---|
| Use-case ID and name | UC-DL-06 — Record and Split a Group Expense |
| Primary actor | Group Member |
| Supporting actor(s) | Group Owner when authorizing shared-fund spending. |
| Description / goal | Allocate a real group expense exactly, preserve the funding source and update member positions without counting consumption twice. |
| Trigger | An authorized member selects Add group expense. |
| Related requirements / rules | FR-DL-06; bookkeeping only, authorized data, exact amounts and no duplicate consumption. |
| Related NFRs | NFR-P5, NFR-S4, NFR-S6, NFR-R3, NFR-R8 |
| Relationships | Actor associations in D.1; shared C1–C5 apply as relevant. No mandatory included sub-use-case. |
| Preconditions | 1. Online active membership.<br>2. Group currency and participants are known.<br>3. A valid rate exists for a foreign-currency bill; sufficient fund cash exists for fund-funded spending. |


### D.4.2. Basic Flow — Main Success Scenario
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Step | Actor action | System response / state change | Screen |
|---|---|---|---|
| 1 | An authorized member selects Add group expense. | Check access; show eligible scope and current state. | S01 |
| 2 | Enter title/date/category, payer or fund, original amount/currency and participating members; select split mode. | Validate the selected inputs and prepare a review; no financial effect yet. | S01 |
| 3 | Review and confirm the action. | Review You 100,001; Laura 100,000; Minh 100,000 VND. Total equals 300,001 VND. | S02 |
| 4 | Wait for the acknowledged result. | Revalidate membership, quote, split inputs and funding; atomically post the expense, allocations and the applicable payer/fund entry. | S03 |
| 5 | Open the authorized result or status. | One expense appears. Member debt/fund positions update; each user's consumption is only their allocated share. | S03 |


### D.4.3. Alternative and Exception Flows
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Flow / branch point | Ordered actions | Resume / resulting state | Screen variant |
|---|---|---|---|
| AF-01 — Alternative split mode<br>Step 2: Exact, Percentage or Shares | 1. Enter exact amounts totaling the bill, percentages totaling 100%, or positive weights. 2. Calculate final shares and distribute rounding remainders in a stable order. | 3 — Final shares sum exactly to the expense. | S04 |
| AF-02 — Change participants / payer<br>Step 2: selection changes | 1. Verify all selected identities belong to the group. 2. Recalculate and remove obsolete draft allocations before review. | 3 — No hidden shares for removed participants. | S04 |
| AF-03 — Pay from shared fund<br>Step 2: fund selected | 1. Check custodian permission and available confirmed cash. 2. Debit the fund instead of crediting an individual payer; update member fund positions once. | 3 — Only one funding source is used. | S04 |
| AF-04 — Foreign-currency bill<br>Step 2: currency differs from group | 1. Show original amount and rate/source/time. 2. Convert once, then split and retain the snapshot. Missing/changed quote requires a new review. | 3 / 2 — No silent repricing of historical shares. | S05 / S06 |
| AF-05 — Invalid allocation / insufficient funds<br>Step 3 or 4: mismatch, no participants, bad weights or low fund cash | 1. Display the difference or available amount. 2. Keep the draft and reject the entire expense until corrected. | 2 — No partial expense, allocation or fund debit. | S05 / S06 |

**Shared branches:** C1–C5 from D.1.1 where applicable. Use DL-C03 for field validation; use S06 for the case-specific pending/recovery result. Domain branches above take precedence where a conflict, offline state or scheduled stop needs different behavior.

### D.4.4. Postconditions and Special Requirements
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Success:** One expense appears. Member debt/fund positions update; each user's consumption is only their allocated share.  
**Failure/cancellation:** No unauthorized or partially committed financial change. Pending claims do not change confirmed balances; acknowledged sync/batch items remain completed while other items can fail.  
**Special requirements:** NFR-P5, NFR-S4, NFR-S6, NFR-R3, NFR-R8 Monetary values and historical rates must remain exact. These are requirements, not measured test results.

### D.4.5. UI Prototype Screenshots
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Prototype:** [Compact wireframes v2](prototypes/data-logic/index.html#UC-DL-06). Screen IDs below use the `UC-DL-06-` prefix. Shared dialogs DL-C01–06 are embedded once in D.1.1.

| Frames | Flow coverage | Purpose |
|---|---|---|
| S01–S03 | Basic steps 1–5 | Input/scope, review and acknowledged result. |
| S04 | AF-01, AF-02, AF-03 | Alternative actions use the same entry/review layout with the selected variant. |
| S05–S06 | AF-04, AF-05; pending/recovery states | Changed values, unavailable data or incomplete outcome; no extra screen per internal check. |

![UC-DL-06 — six reusable prototype frames](screenshots/prototypes/data-logic/UC-DL-06-compact.png)

*Figure D.4 — UC-DL-06-S01–S06. Labeled design variants illustrate the specified flows; they are not proof of executed backend behavior.*

## D.5. UC-DL-07 — View Debts and Confirm a Settlement
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*


### D.5.1. Overview and Preconditions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Field | Specification |
|---|---|
| Use-case ID and name | UC-DL-07 — View Debts and Confirm a Settlement |
| Primary actor | Group Member acting as debtor |
| Supporting actor(s) | Intended recipient / creditor. |
| Description / goal | Inspect simplified debts and reduce debt only after the intended recipient confirms an external payment. |
| Trigger | The user opens Who owes whom and selects a debt. |
| Related requirements / rules | FR-DL-07; bookkeeping only, authorized data, exact amounts and no duplicate consumption. |
| Related NFRs | NFR-P5, NFR-S4, NFR-R3, NFR-R6, NFR-R7 |
| Relationships | Actor associations in D.1; shared C1–C5 apply as relevant. No mandatory included sub-use-case. |
| Preconditions | 1. Both parties are authorized active group members online.<br>2. A positive outstanding debt exists.<br>3. The debtor pays outside FinSync before claiming payment. |


### D.5.2. Basic Flow — Main Success Scenario
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Step | Actor action | System response / state change | Screen |
|---|---|---|---|
| 1 | The user opens Who owes whom and selects a debt. | Check access; show eligible scope and current state. | S01 |
| 2 | Select the intended recipient, amount and date; indicate that external payment has occurred. | Validate the selected inputs and prepare a review; no financial effect yet. | S01 |
| 3 | Review and confirm the action. | Review the payer, recipient and claim amount; display Awaiting recipient confirmation. | S02 |
| 4 | Intended recipient confirms receipt. | Create a pending claim; notify the recipient; recipient confirms receipt after current-debt validation; atomically post settlement effects. | S03 |
| 5 | Open the authorized result or status. | Confirmed debt falls from 100,000 to 0 VND. The payment remains in history and is not new spending. | S03 |


### D.5.3. Alternative and Exception Flows
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Flow / branch point | Ordered actions | Resume / resulting state | Screen variant |
|---|---|---|---|
| AF-01 — Inspect / simplify / remind<br>Step 1: no payment submitted | 1. Show transfers preserving every member's net position. 2. If requested, send an eligible deduplicated reminder; do not create a payment. | End — Debt unchanged. | S04 |
| AF-02 — Partial payment<br>Step 2: amount is below outstanding debt | 1. Review the smaller positive amount. 2. After recipient confirmation reduce only that amount: 50,000 paid against 100,000 leaves 50,000. | 3 — Remaining debt is retained. | S04 |
| AF-03 — Pending / rejected / withdrawn<br>Step 4: recipient does not confirm, rejects, or debtor cancels | 1. Keep pending debt unchanged. 2. Record rejection reason or cancel an unconfirmed claim; never cancel an already confirmed payment through this branch. | End — Only confirmed payments reduce debt. | S04 |
| AF-04 — Wrong confirmer<br>Step 4: Owner or another person tries to confirm | 1. Check intended recipient identity. 2. Reject confirmation by anyone else, including the Owner. | End — Claim and debt unchanged. | S05 / S06 |
| AF-05 — Invalid or stale debt<br>Step 3 or 4: invalid/excess amount or changed debt | 1. Recompute current outstanding debt. 2. Reject an excess claim and request fresh review; repeated confirmation returns the existing result. | 2 — No overpayment or duplicate effect. | S05 / S06 |

**Shared branches:** C1–C5 from D.1.1 where applicable. Use DL-C03 for field validation; use S06 for the case-specific pending/recovery result. Domain branches above take precedence where a conflict, offline state or scheduled stop needs different behavior.

### D.5.4. Postconditions and Special Requirements
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Success:** Confirmed debt falls from 100,000 to 0 VND. The payment remains in history and is not new spending.  
**Failure/cancellation:** No unauthorized or partially committed financial change. Pending claims do not change confirmed balances; acknowledged sync/batch items remain completed while other items can fail.  
**Special requirements:** NFR-P5, NFR-S4, NFR-R3, NFR-R6, NFR-R7 Monetary values and historical rates must remain exact. These are requirements, not measured test results.

### D.5.5. UI Prototype Screenshots
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Prototype:** [Compact wireframes v2](prototypes/data-logic/index.html#UC-DL-07). Screen IDs below use the `UC-DL-07-` prefix. Shared dialogs DL-C01–06 are embedded once in D.1.1.

| Frames | Flow coverage | Purpose |
|---|---|---|
| S01–S03 | Basic steps 1–5 | Input/scope, review and acknowledged result. |
| S04 | AF-01, AF-02, AF-03 | Alternative actions use the same entry/review layout with the selected variant. |
| S05–S06 | AF-04, AF-05; pending/recovery states | Changed values, unavailable data or incomplete outcome; no extra screen per internal check. |

![UC-DL-07 — six reusable prototype frames](screenshots/prototypes/data-logic/UC-DL-07-compact.png)

*Figure D.5 — UC-DL-07-S01–S06. Labeled design variants illustrate the specified flows; they are not proof of executed backend behavior.*

## D.6. UC-DL-08 — Record Expense Refunds and Return Unused Funds
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*


### D.6.1. Overview and Preconditions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Field | Specification |
|---|---|
| Use-case ID and name | UC-DL-08 — Record Expense Refunds and Return Unused Funds |
| Primary actor | Group Member who recorded the expense / authorized Group Owner |
| Supporting actor(s) | Intended recipient for an unused-fund return. |
| Description / goal | Record traceable corrections and reimbursements without deleting settled financial history. |
| Trigger | The user opens an original group expense and selects Record refund, or opens Return unused funds. |
| Related requirements / rules | FR-DL-08; bookkeeping only, authorized data, exact amounts and no duplicate consumption. |
| Related NFRs | NFR-S4, NFR-R3, NFR-R6, NFR-R7, NFR-R8 |
| Relationships | Actor associations in D.1; shared C1–C5 apply as relevant. No mandatory included sub-use-case. |
| Preconditions | 1. Online authorized membership.<br>2. Original confirmed expense exists for expense refunds.<br>3. Remaining refundable amount and original participants/rate are available. |


### D.6.2. Basic Flow — Main Success Scenario
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Step | Actor action | System response / state change | Screen |
|---|---|---|---|
| 1 | The user opens an original group expense and selects Record refund, or opens Return unused funds. | Check access; show eligible scope and current state. | S01 |
| 2 | Select the original expense; enter refund amount/date/reason and actual recipient of the external refund. | Validate the selected inputs and prepare a review; no financial effect yet. | S01 |
| 3 | Review and confirm the action. | Review a 90,000 VND refund: reverse 30,000 consumption per original participant. | S02 |
| 4 | Wait for the acknowledged result. | Validate remaining refundable amount; atomically append a linked adjustment using original allocations and stored currency basis; retain all earlier payments. | S03 |
| 5 | Open the authorized result or status. | Net consumption is 210,000 VND. If already settled, reimbursement positions appear; no prior settlement is deleted. | S03 |


### D.6.3. Alternative and Exception Flows
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Flow / branch point | Ordered actions | Resume / resulting state | Screen variant |
|---|---|---|---|
| AF-01 — Full / partial / repeated refund<br>Step 2: refund amount chosen | 1. Show unrefunded expense and per-member amounts. 2. Reverse only the selected remaining amount using the original allocation/rate basis and exact rounding. | 3 — Cumulative refunds never exceed original shares. | S04 |
| AF-02 — Refund after settlement<br>Step 4: original debt was already paid | 1. Retain all prior payments. 2. Show new reimbursement positions; any subsequent external payment uses UC-DL-07. | 5 — No settled history is erased. | S04 |
| AF-03 — Refund to shared fund<br>Step 2: original bill was fund-paid | 1. Verify the original funding source and returned money. 2. Credit the fund and reverse allocated consumption once. | 3 — Refund is not new income. | S04 |
| AF-04 — Return unused funds<br>Step 1: fund return selected | 1. Check cash and the member's entitlement; create a return claim. 2. Intended recipient confirms receipt; pending/rejected/withdrawn claims do not reduce confirmed cash. | 5 / end — Confirmed return reduces cash and entitlement once. | S05 / S06 |
| AF-05 — Invalid refund / changed position<br>Step 3 or 4: missing original, excess amount, missing quote or stale entitlement | 1. Show the current permitted amount or missing evidence. 2. Reject the adjustment and retain a correctable draft; the Owner cannot confirm receipt for another member. | 2 — No unlinked or excess correction. | S05 / S06 |

**Shared branches:** C1–C5 from D.1.1 where applicable. Use DL-C03 for field validation; use S06 for the case-specific pending/recovery result. Domain branches above take precedence where a conflict, offline state or scheduled stop needs different behavior.

### D.6.4. Postconditions and Special Requirements
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Success:** Net consumption is 210,000 VND. If already settled, reimbursement positions appear; no prior settlement is deleted.  
**Failure/cancellation:** No unauthorized or partially committed financial change. Pending claims do not change confirmed balances; acknowledged sync/batch items remain completed while other items can fail.  
**Special requirements:** NFR-S4, NFR-R3, NFR-R6, NFR-R7, NFR-R8 Monetary values and historical rates must remain exact. These are requirements, not measured test results.

### D.6.5. UI Prototype Screenshots
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Prototype:** [Compact wireframes v2](prototypes/data-logic/index.html#UC-DL-08). Screen IDs below use the `UC-DL-08-` prefix. Shared dialogs DL-C01–06 are embedded once in D.1.1.

| Frames | Flow coverage | Purpose |
|---|---|---|
| S01–S03 | Basic steps 1–5 | Input/scope, review and acknowledged result. |
| S04 | AF-01, AF-02, AF-03 | Alternative actions use the same entry/review layout with the selected variant. |
| S05–S06 | AF-04, AF-05; pending/recovery states | Changed values, unavailable data or incomplete outcome; no extra screen per internal check. |

![UC-DL-08 — six reusable prototype frames](screenshots/prototypes/data-logic/UC-DL-08-compact.png)

*Figure D.6 — UC-DL-08-S01–S06. Labeled design variants illustrate the specified flows; they are not proof of executed backend behavior.*

## D.7. UC-DL-12 — Synchronize Offline Personal Changes and Resolve Conflicts
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*


### D.7.1. Overview and Preconditions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Field | Specification |
|---|---|
| Use-case ID and name | UC-DL-12 — Synchronize Offline Personal Changes and Resolve Conflicts |
| Primary actor | Regular User |
| Supporting actor(s) | None; connectivity detection and synchronization worker are internal. |
| Description / goal | Submit encrypted locally queued personal changes exactly once and explicitly resolve version conflicts. |
| Trigger | Connectivity returns or the user selects Sync now after offline capture. |
| Related requirements / rules | FR-DL-12; bookkeeping only, authorized data, exact amounts and no duplicate consumption. |
| Related NFRs | NFR-P6, NFR-S3, NFR-S8, NFR-R5, NFR-R6 |
| Relationships | Actor associations in D.1; shared C1–C5 apply as relevant. No mandatory included sub-use-case. |
| Preconditions | 1. Initial authenticated synchronization has created an account-specific encrypted cache.<br>2. Stable operation IDs, base versions and pending changes exist.<br>3. Valid online session is available for server submission. |


### D.7.2. Basic Flow — Main Success Scenario
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Step | Actor action | System response / state change | Screen |
|---|---|---|---|
| 1 | Connectivity returns or the user selects Sync now after offline capture. | Check access; show eligible scope and current state. | S01 |
| 2 | Unlock the local account cache, inspect pending records and select Sync now. | Validate the selected inputs and prepare a review; no financial effect yet. | S01 |
| 3 | Review and confirm the action. | Show pending operation IDs/order and local-only labels; verify session before submission. | S02 |
| 4 | Wait for the acknowledged result. | Submit operations with stable IDs/base versions; validate on server; acknowledge committed results; mark only acknowledged operations synchronized. | S03 |
| 5 | Open the authorized result or status. | Three changes synchronize once; local IDs match server records; balances refresh and pending status clears. | S03 |


### D.7.3. Alternative and Exception Flows
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Flow / branch point | Ordered actions | Resume / resulting state | Screen variant |
|---|---|---|---|
| AF-01 — Offline again / app restart<br>Step 4: connection or process stops | 1. Keep unacknowledged operations durable. 2. Resume remaining work on reconnection; already acknowledged items stay synchronized. | 4 — No acknowledged local edit is lost. | S04 |
| AF-02 — Expired session / account switch<br>Step 3: authorization unavailable or logout requested | 1. Pause and request valid sign-in for the same account. 2. Before logout warn about pending edits; allow cancel-and-sync or explicit discard, then clear account-local data. | 3 / end — No cross-account upload. | S04 |
| AF-03 — Edit or delete conflict<br>Step 4: server version differs | 1. Show local/server values and any tombstone. 2. Keep server, reapply against the refreshed version, or explicitly recreate a deleted entry with a new ID; never overwrite/resurrect silently. | 4 — Conflict remains visible until resolved. | S04 |
| AF-04 — Duplicate / lost acknowledgement<br>Step 4: replay after uncertain response | 1. Reuse the original operation identity and retrieve its outcome. 2. Acknowledge the existing result without applying it again. | 4 — Exactly one financial effect. | S05 / S06 |
| AF-05 — Reference rejected / receipt failed<br>Step 4: wallet/category ineligible or upload fails | 1. Mark the invalid operation Action required and retain its draft; retry only a failed receipt upload. 2. Continue independent valid records. | 4 — No second financial entry; partial progress is explicit. | S05 / S06 |
| AF-06 — Offline group write<br>Step 2: user tries to change shared data | 1. Show cached group data as read-only. 2. Require an online session before shared writes; do not enqueue them as personal changes. | End — No unauthorized offline group update. | S05 / S06 |

**Shared branches:** C1–C5 from D.1.1 where applicable. Use DL-C03 for field validation; use S06 for the case-specific pending/recovery result. Domain branches above take precedence where a conflict, offline state or scheduled stop needs different behavior.

### D.7.4. Postconditions and Special Requirements
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Success:** Three changes synchronize once; local IDs match server records; balances refresh and pending status clears.  
**Failure/cancellation:** No unauthorized or partially committed financial change. Pending claims do not change confirmed balances; acknowledged sync/batch items remain completed while other items can fail.  
**Special requirements:** NFR-P6, NFR-S3, NFR-S8, NFR-R5, NFR-R6 Monetary values and historical rates must remain exact. These are requirements, not measured test results.

### D.7.5. UI Prototype Screenshots
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Prototype:** [Compact wireframes v2](prototypes/data-logic/index.html#UC-DL-12). Screen IDs below use the `UC-DL-12-` prefix. Shared dialogs DL-C01–06 are embedded once in D.1.1.

| Frames | Flow coverage | Purpose |
|---|---|---|
| S01–S03 | Basic steps 1–5 | Input/scope, review and acknowledged result. |
| S04 | AF-01, AF-02, AF-03 | Alternative actions use the same entry/review layout with the selected variant. |
| S05–S06 | AF-04, AF-05, AF-06; pending/recovery states | Changed values, unavailable data or incomplete outcome; no extra screen per internal check. |

![UC-DL-12 — six reusable prototype frames](screenshots/prototypes/data-logic/UC-DL-12-compact.png)

*Figure D.7 — UC-DL-12-S01–S06. Labeled design variants illustrate the specified flows; they are not proof of executed backend behavior.*

## D.8. UC-DL-13 — Generate Financial Reports on Demand or Automatically
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*


### D.8.1. Overview and Preconditions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Field | Specification |
|---|---|
| Use-case ID and name | UC-DL-13 — Generate Financial Reports on Demand or Automatically |
| Primary actor | Regular User; Deployment Scheduler for monthly runs |
| Supporting actor(s) | Authorized operator monitoring or retrying a scheduled run. |
| Description / goal | Produce authorized personal/group reports with consistent totals, an explicit currency basis and optional PDF/Excel output. |
| Trigger | The user opens Reports, or the monthly scheduler starts a due report. |
| Related requirements / rules | FR-DL-13; bookkeeping only, authorized data, exact amounts and no duplicate consumption. |
| Related NFRs | NFR-S4, NFR-R3, NFR-R8; recoverable, duplicate-safe background processing. |
| Relationships | Actor associations in D.1; shared C1–C5 apply as relevant. No mandatory included sub-use-case. |
| Preconditions | 1. Valid user or scheduler authorization.<br>2. Requested data scope and period are permitted.<br>3. Conversion-dependent totals have an identified rate basis. |


### D.8.2. Basic Flow — Main Success Scenario
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Step | Actor action | System response / state change | Screen |
|---|---|---|---|
| 1 | The user opens Reports, or the monthly scheduler starts a due report. | Check access; show eligible scope and current state. | S01 |
| 2 | Choose personal/group scope, period, currency and output format; a scheduled run derives the closed month for each eligible user. | Validate the selected inputs and prepare a review; no financial effect yet. | S01 |
| 3 | Review and confirm the action. | Review the period, currency basis and source cutoff; preview the same totals used for details and export. | S02 |
| 4 | Wait for the acknowledged result. | Aggregate the authorized snapshot; generate the report and requested artifact; record completion before publication. | S03 |
| 5 | Open the authorized result or status. | The user can inspect or download the report with period/currency/cutoff labels. A scheduled result is published once; the financial ledger is unchanged. | S03 |


### D.8.3. Alternative and Exception Flows
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Flow / branch point | Ordered actions | Resume / resulting state | Screen variant |
|---|---|---|---|
| AF-01 — Automatic monthly run<br>Step 1: scheduled trigger instead of user request | 1. Select each eligible user's closed month/currency. 2. Generate and publish one authorized result; duplicate triggers reuse the same logical run. | 2 — No repeated monthly publication. | S04 |
| AF-02 — Chart / group / export variant<br>Step 2: user changes view/scope/format | 1. Recheck group access and apply the same filters to totals and details. 2. Export the reviewed snapshot as PDF or Excel; never expose private member wallets. | 3 — View and export agree. | S04 |
| AF-03 — Empty data / invalid range / missing rate<br>Step 3: unavailable or invalid reporting inputs | 1. Reject invalid ranges; show zero/empty data or growth as not applicable for a zero baseline. 2. If conversion is missing, show original-currency subtotals and incomplete status. | 2 / end — No invented totals or growth. | S04 |
| AF-04 — Cached data / later changes<br>Step 3 or 5: offline viewing or source changes | 1. Label cached/pending data and its cutoff. 2. Regeneration creates an explicit revised snapshot; it does not silently rewrite a previously issued report. | 3 / end — The report remains attributable to its source period. | S05 / S06 |
| AF-05 — Processing / export / delivery failure<br>Step 4 or 5: an output stage fails | 1. Preserve confirmed results and retry the failed unit without re-posting financial data. 2. Keep delivery pending, or mark failed after the retry limit; revoke access if eligibility changed. | 4 / end — No false success or unauthorized download. | S05 / S06 |
| AF-06 — Scheduled run interrupted / cancelled<br>Step 4: worker restart or operator stop | 1. Resume from acknowledged work without duplicating published results, or stop new work at safe boundaries. 2. Retain completed results and expose remaining/failed counts. | 4 / end — Partial completion is explicit. | S05 / S06 |

**Shared branches:** C1–C5 from D.1.1 where applicable. Use DL-C03 for field validation; use S06 for the case-specific pending/recovery result. Domain branches above take precedence where a conflict, offline state or scheduled stop needs different behavior.

### D.8.4. Postconditions and Special Requirements
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Success:** The user can inspect or download the report with period/currency/cutoff labels. A scheduled result is published once; the financial ledger is unchanged.  
**Failure/cancellation:** No unauthorized or partially committed financial change. Pending claims do not change confirmed balances; acknowledged sync/batch items remain completed while other items can fail.  
**Special requirements:** NFR-S4, NFR-R3, NFR-R8; recoverable, duplicate-safe background processing. Monetary values and historical rates must remain exact. These are requirements, not measured test results.

### D.8.5. UI Prototype Screenshots
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Prototype:** [Compact wireframes v2](prototypes/data-logic/index.html#UC-DL-13). Screen IDs below use the `UC-DL-13-` prefix. Shared dialogs DL-C01–06 are embedded once in D.1.1.

| Frames | Flow coverage | Purpose |
|---|---|---|
| S01–S03 | Basic steps 1–5 | Input/scope, review and acknowledged result. |
| S04 | AF-01, AF-02, AF-03 | Alternative actions use the same entry/review layout with the selected variant. |
| S05–S06 | AF-04, AF-05, AF-06; pending/recovery states | Changed values, unavailable data or incomplete outcome; no extra screen per internal check. |

![UC-DL-13 — six reusable prototype frames](screenshots/prototypes/data-logic/UC-DL-13-compact.png)

*Figure D.8 — UC-DL-13-S01–S06. Labeled design variants illustrate the specified flows; they are not proof of executed backend behavior.*

## D.9. UC-DL-14 — Evaluate Budget Alerts and Send Debt Reminders in Batches
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*


### D.9.1. Overview and Preconditions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Field | Specification |
|---|---|
| Use-case ID and name | UC-DL-14 — Evaluate Budget Alerts and Send Debt Reminders in Batches |
| Primary actor | Deployment Scheduler (external time trigger) |
| Supporting actor(s) | Regular User receiving alerts; authorized operator retrying failed items. |
| Description / goal | Evaluate many eligible budgets/debts reliably and deliver each logical alert once without changing recorded balances. |
| Trigger | A scheduled sweep starts or an authorized operator resumes failed notification work. |
| Related requirements / rules | FR-DL-14; bookkeeping only, authorized data, exact amounts and no duplicate consumption. |
| Related NFRs | NFR-S4, NFR-R3, NFR-U6; recoverable, duplicate-safe background processing. |
| Relationships | Actor associations in D.1; shared C1–C5 apply as relevant. No mandatory included sub-use-case. |
| Preconditions | 1. Valid scheduler identity and configured recipient-local schedule.<br>2. Budget/debt data is available.<br>3. Durable checkpoints, deduplication keys and delivery state are available. |


### D.9.2. Basic Flow — Main Success Scenario
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Step | Actor action | System response / state change | Screen |
|---|---|---|---|
| 1 | A scheduled sweep starts or an authorized operator resumes failed notification work. | Check access; show eligible scope and current state. | S01 |
| 2 | Authenticate the trigger; select eligible users and a stable sweep window; create or reuse the run. | Validate the selected inputs and prepare a review; no financial effect yet. | S01 |
| 3 | Scheduler supplies the due scope. | Claim a bounded page with a lease; expose processed/failed counts and the checkpoint, not private transaction details. | S02 |
| 4 | Scheduler starts processing; operator monitors progress. | Recompute current consumption and confirmed debts; atomically insert eligible deduplicated inbox/outbox events and acknowledge the item; advance the checkpoint only after commit. | S03 |
| 5 | Open the authorized result or status. | Eligible users receive one logical threshold/reminder event; completed items are counted; financial balances remain unchanged. | S03 |


### D.9.3. Alternative and Exception Flows
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Flow / branch point | Ordered actions | Resume / resulting state | Screen variant |
|---|---|---|---|
| AF-01 — Nothing due / already notified<br>Step 2 or 4: no eligible event or existing logical alert | 1. Skip the item or finish with zero recipients. 2. Do not create placeholder or duplicate messages. | 4 / end — One logical alert at most. | S04 |
| AF-02 — Debt settled / access revoked<br>Step 4 or 5: queued information became stale | 1. Recheck debt and recipient rights. 2. Suppress obsolete reminders or protected content before delivery. | 4 — No false debt reminder or data leak. | S04 |
| AF-03 — Overlapping worker / restart<br>Step 3 or 4: concurrent processing or worker failure | 1. Resume unacknowledged work only. 2. Reject stale completion and reuse existing committed event identities. | 4 — No lost acknowledged item or duplicate event. | S04 |
| AF-04 — Transient / permanent failure<br>Step 4 or 5: evaluation or delivery fails | 1. Retry transient errors within a configured limit; retain the failed item after permanent/exhausted failure. 2. Continue other recipients and report Partial/Failed. | 4 / end — Failure is visible and retryable, not silently dropped. | S05 / S06 |
| AF-05 — Pause / cancel / invalid trigger<br>Step 1 or 3: invalid service identity or operator stop | 1. Reject an invalid/untracked start, or stop new claims at item boundaries. 2. Keep completed events and checkpoints for an authorized resume. | End — No uncontrolled processing or ledger change. | S05 / S06 |

**Shared branches:** C1–C5 from D.1.1 where applicable. Use DL-C03 for field validation; use S06 for the case-specific pending/recovery result. Domain branches above take precedence where a conflict, offline state or scheduled stop needs different behavior.

### D.9.4. Postconditions and Special Requirements
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Success:** Eligible users receive one logical threshold/reminder event; completed items are counted; financial balances remain unchanged.  
**Failure/cancellation:** No unauthorized or partially committed financial change. Pending claims do not change confirmed balances; acknowledged sync/batch items remain completed while other items can fail.  
**Special requirements:** NFR-S4, NFR-R3, NFR-U6; recoverable, duplicate-safe background processing. Monetary values and historical rates must remain exact. These are requirements, not measured test results.

### D.9.5. UI Prototype Screenshots
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Prototype:** [Compact wireframes v2](prototypes/data-logic/index.html#UC-DL-14). Screen IDs below use the `UC-DL-14-` prefix. Shared dialogs DL-C01–06 are embedded once in D.1.1.

| Frames | Flow coverage | Purpose |
|---|---|---|
| S01–S03 | Basic steps 1–5 | Input/scope, review and acknowledged result. |
| S04 | AF-01, AF-02, AF-03 | Alternative actions use the same entry/review layout with the selected variant. |
| S05–S06 | AF-04, AF-05; pending/recovery states | Changed values, unavailable data or incomplete outcome; no extra screen per internal check. |

![UC-DL-14 — six reusable prototype frames](screenshots/prototypes/data-logic/UC-DL-14-compact.png)

*Figure D.9 — UC-DL-14-S01–S06. Labeled design variants illustrate the specified flows; they are not proof of executed backend behavior.*

## D.10. Coverage and Team Handoff
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Leader requirement | Covered by |
|---|---|
| Core financial processing | UC-DL-02, 05–08: validation, exact allocation, confirmation, corrections and consistent results. |
| Automatic report aggregation | UC-DL-13: user-requested or monthly reports, same snapshot for view/export, explicit failures and revisions. |
| Data synchronization | UC-DL-12: pending changes, conflict resolution, replay, interruption and receipt retry. |
| Batch jobs | UC-DL-14 and scheduled UC-DL-13: eligible work, progress, duplicate suppression, restart, partial failure and stop. |

**Scope transfer:** UC-DL-01 wallet setup → UC-CLI-02; UC-DL-03 transfers → UC-CLI-03; UC-DL-04 membership → UC-CLI-10. UC-DL-09 budget viewing → UC-CLI-08, while budget creation and UC-DL-10 savings management still require team assignment/specification. UC-DL-11 report viewing/export is consolidated into UC-DL-13. Keep the PA2 currency/confirmation requirements when reconciling those contributions. This is not the full team Part D.

**Before submission:** Merge these eight IDs into the actual Part C catalogue; reconcile baseline differences using [Backend Notes](use-cases/D_backend_notes.md); obtain peer review and export the final Markdown/figures to PDF. AI-assisted drafting and wireframe creation should be declared in the team AI Usage Report.


### D.1c. Admin & Account Use Cases and UI Prototypes
> *Performed by: `Pham Dinh Tieu Long` (24120087) | Reviewed by: `Ngo Thai Hoa` (24120051) | Edited by: `Pham Dinh Tieu Long`*

The Admin & Account contribution comprises **6 detailed use-case specifications** and **36 high-resolution prototype screenshots** covering both mobile authentication/profile flows and desktop administration dashboards. Full specifications, flow steps, and embedded figures are catalogued in [Admin & Account Use Cases](use-cases/admin-account-use-cases.md).

**Evidence source: High-Fidelity Interactive Prototype Screens.** All screenshots are captured directly from live interactive prototypes matching the design system token standards (Tailwind Slate/Emerald/Indigo palette), with 100% data fidelity to the specification tables (e.g. realistic users, lock justification, category addition, and audit log entries).

| Use Case ID | Use Case Name | Flow Coverage | Prototype Evidence Folder | Key UI States Captured |
|---|---|---|---|---|
| `UC-ACC-01` | Sign In to Personal Account | Basic Flow + AF-01 (Invalid format), AF-02 (Wrong credentials), AF-03 (Locked account) | `screenshots/prototypes/UC-ACC-01/` | S01 (Empty), S02 (Entered), S03 (Submitting), S04 (Locked), S05 (Invalid) |
| `UC-ACC-02` | View and Update Personal Profile | Basic Flow + AF-01 (Edit modal), AF-02 (Oversized avatar), AF-03 (Invalid phone) | `screenshots/prototypes/UC-ACC-02/` | S01 (Profile), S02 (Edit modal), S03 (Saving), S04 (Updated), S05 (File size error) |
| `UC-ADM-01` | Sign In to Admin Console & Dashboard | Basic Flow + AF-01 (Session expired), AF-02 (Audit log filter) | `screenshots/prototypes/UC-ADM-01/` | S01 (Login), S02 (Entered), S03 (Dashboard), S04 (Audit), S05 (Session timeout) |
| `UC-ADM-02` | Search, Filter, and View User Registry | Basic Flow + AF-01 (Empty query), AF-02 (Status filter), AF-03 (No results) | `screenshots/prototypes/UC-ADM-02/` | S01 (Registry), S02 (Filtered ACTIVE), S03 (Filtered LOCKED), S04 (Search query), S05 (Empty state) |
| `UC-ADM-03` | Lock and Unlock User Account | Basic Flow + AF-01 (Short reason), AF-02 (Self-lock prevention), AF-03 (Unlock) | `screenshots/prototypes/UC-ADM-03/` | S01 (Pre-lock), S02 (Modal), S03 (Submitting), S04 (Locked row), S05 (Self-lock toast), S06 (Unlock) |
| `UC-ADM-04` | Manage Default Categories & Taxonomies | Basic Flow + AF-01 (Duplicate name), AF-02 (Currency setting), AF-03 (Archive) | `screenshots/prototypes/UC-ADM-04/` | S01 (Overview), S02 (Add modal), S03 (Duplicate error), S04 (Added toast), S05 (Currency modal), S06 (Rate edit) |

Every alternative and exception flow has a corresponding visual evidence file in `screenshots/prototypes/`, demonstrating strict adherence to PA3 Section D requirements.

### D.1d. AI Financial Advisor Use Cases and UI Prototypes
> *Performed by: `Ngo Thai Hoa` (24120051) | Reviewed by: `Pham Dinh Tieu Long` (24120087) | Edited by: `Ngo Thai Hoa`*

The AI Financial Advisor contribution contains **5 specialized use-case specifications** and **16 prototype evidence PNGs**, establishing privacy-preserving automated intelligence. Full specifications and analysis flows are catalogued in [AI Feature Use Cases](use-cases/uc-ai-features.md) and audited in [AI Evidence Report](screenshots/prototypes/ai-evidence/Evidence-Report.md).

| Use Case ID | Use Case Name | Flow Coverage | Prototype Evidence Folder |
|---|---|---|---|
| `UC-AI-01` | Generate AI Financial Analysis | Basic Flow + AF-01 (Limited history), AF-02 (Provider timeout/fallback) | `screenshots/prototypes/UC-AI-01/` |
| `UC-AI-02` | Generate Monthly Analysis Automatically | Basic Flow + AF-01 (Insufficient data), AF-02 (Retry failure) | `screenshots/prototypes/UC-AI-02/` |
| `UC-AI-03` | View Spending Anomaly Alerts | Basic Flow + AF-01 (Acknowledge), AF-02 (No anomalies) | `screenshots/prototypes/UC-AI-03/` |
| `UC-AI-04` | View and Apply Optimized Budget Allocation | Basic Flow + AF-01 (Reject suggestion), AF-02 (Custom adjustment) | `screenshots/prototypes/UC-AI-04/` |
| `UC-AI-05` | View Savings Goal Progress Evaluation | Basic Flow + AF-01 (Behind schedule advice), AF-02 (Target reached) | `screenshots/prototypes/UC-AI-05/` |

---

## Part E — Implement One Functional Group Using Spec Kit
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

**Weight:** 20 points. Implement one complete functional group end-to-end: frontend, backend/API or logic, and database persistence.

### E.1. Selected Group and Implementation Scope
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

**Selected functional group:** **Account Management & Authentication Lifecycle (with Core Personal Finance Operations)**  
**Selection rationale:** In strict alignment with [demo_feature.md](demo_feature.md) and [database_design.md](database_design.md), this functional group establishes the foundational prerequisite for all personal financial tracking, multi-wallet isolation, and shared expense splitting. Without authenticated user identity, role-based security, and session lifecycle management, no financial ledger can guarantee privacy or non-custodial boundaries.  
**Related FR / UC IDs:** `FR-AUTH-01`, `FR-AUTH-02`, `FR-PROF-01`, `FR-PROF-02`, `FR-WAL-01`; Use Cases `UC-ACC-01`, `UC-ACC-02`, `UC-CLI-02`.  
**Source revision / branch / tag:** Feature branch `origin/task/demo_user_management` (commits `bbf9dcb`, `5950027`, `d4afea6`, `6bd7c2d`) merged with Spec Kit baseline `origin/feat/core-system-speckit` (`4b0fad9`).  
**Acceptance criteria:**
1. A new user registers on the client interface $\rightarrow$ Backend hashes password via BCrypt (cost 10) $\rightarrow$ creates record in PostgreSQL `users` table with `USER` role and `ACTIVE` status.
2. User authenticates with email/password $\rightarrow$ Backend verifies credentials $\rightarrow$ issues signed JWT access token (60 min) and refresh token (7 days) $\rightarrow$ client stores session.
3. Client navigates to Profile $\rightarrow$ queries `GET /api/v1/users/me` $\rightarrow$ updates display name and phone number via `PATCH /api/v1/users/me` $\rightarrow$ changes persist to PostgreSQL.
4. User logs out $\rightarrow$ Backend invalidates refresh token and pushes `jti` identifier to Redis blacklist $\rightarrow$ old token cannot access protected endpoints.

| Requirement / use case | Frontend screen | Backend endpoint / logic | Database entity / migration | Actual status / evidence |
|------------------------|-----------------|--------------------------|-----------------------------|--------------------------|
| User Registration (`FR-AUTH-01`) | `RegisterScreen.kt` / Web Modal | `POST /api/v1/auth/register` | `users` table; `V1__init_schema.sql` | Operational (Verified 201 Created & 409 Conflict) |
| User Login (`FR-AUTH-02`) | `LoginScreen.kt` / Web Login | `POST /api/v1/auth/login` | `auth_sessions` table; `User.java` | Operational (JWT issued & session cached) |
| View Profile (`FR-PROF-01`) | `ProfileScreen.kt` | `GET /api/v1/users/me` | `UserRepository.findByEmail()` | Operational (Returns user DTO without password hash) |
| Update Profile (`FR-PROF-02`) | `EditProfileDialog.kt` | `PATCH /api/v1/users/me` | `users.display_name`, `phone_number` | Operational (Persistence verified across restarts) |
| Token Revocation (`FR-AUTH-03`) | Profile Logout Button | `POST /api/v1/auth/logout` | Redis token blacklist (`jti`) | Operational (Logged-out token rejected with 401) |

**Dependencies and limitations:** Requires Docker services (PostgreSQL 16 on port 5432, Redis 7 on port 6379). Avatar image uploads are currently simulated with local URI strings; cloud S3/Blob storage is scheduled for Sprint 4.

### E.2. Spec Kit Workflow and Artifacts
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

The implementation was driven strictly following the Spec Kit specification-first methodology, maintaining formal artifacts under `.specify/` and `specs/001-finsync-core-system/`:

| Stage | Actual command / activity and date | Inputs / decisions | Generated artifact path | Human review / implementation evidence |
|-------|------------------------------------|--------------------|-------------------------|----------------------------------------|
| Constitution | `specify init` (2026-10-29) | Spring Boot 3, Java 21, PostgreSQL, Redis, non-custodial boundaries | `.specify/memory/constitution.md` | Reviewed by Long & Nhat (commit `b8ab64f`) |
| Specification | `specify create-feature` (2026-10-30) | User registration, authentication, profile management, error cases | `specs/001-finsync-core-system/spec.md` | Validated against `admin-account-use-cases.md` |
| Technical plan | `setup-plan.sh` (2026-10-31) | Multi-tier architecture, Flyway migrations, JWT security filter | `specs/001-finsync-core-system/plan.md` | Architecture reviewed by Dat & Nhat |
| API Contracts | OpenAPI 3.0 export (2026-11-01) | RESTful contracts for Auth and User management | `specs/001-finsync-core-system/contracts/backend-api.yaml` | OpenAPI JSON schema frozen for FE integration |
| Task breakdown | `setup-tasks.sh` (2026-11-02) | 16 discrete subtasks split across DB, Backend, Security, FE | `specs/001-finsync-core-system/tasks.md` | Tasks reconciled with Jira board |
| Implementation | Git commits (2026-11-03..05) | Source coding of JPA entities, services, controllers, Compose UI | `src/backend/`, `src/mobile/`, `src/admin-web/` | Commits `bbf9dcb`, `5950027`, `d4afea6` |

### E.3. Full-Stack Design and Reproduction Steps
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

**Actual stack and versions:**
- Backend: Java 21 (Eclipse Temurin), Spring Boot 3.3.4, Spring Data JPA, Spring Security 6, JJWT 0.12.5.
- Database: PostgreSQL 16.4 with Flyway migration framework.
- Cache & Session Store: Redis 7.2 (Alpine).
- Client: Android Jetpack Compose (Kotlin 2.0.21, compileSdk 34) & React 18 web interface.
- Orchestration: Docker & Docker Compose v2.29+.

**Data flow:**
```mermaid
sequenceDiagram
    autonumber
    actor User as Client User
    participant UI as Mobile/Web Frontend
    participant API as Spring Boot Backend
    participant Redis as Redis Cache
    participant DB as PostgreSQL DB

    User->>UI: Enter credentials & Click "Sign In"
    UI->>API: POST /api/v1/auth/login {email, password}
    API->>DB: SELECT * FROM users WHERE email = ?
    DB-->>API: User record (status: ACTIVE, password_hash)
    API->>API: Verify BCrypt password
    API->>API: Generate JWT Access Token + Refresh Token
    API->>DB: INSERT INTO auth_sessions (user_id, refresh_hash)
    API-->>UI: 200 OK {token, user: {id, email, name}}
    UI->>UI: Save JWT in Secure Store & Open Profile
    
    User->>UI: Edit Display Name & Click "Save"
    UI->>API: PATCH /api/v1/users/me (Bearer Token) {display_name}
    API->>Redis: Check if token JTI is in blacklist
    Redis-->>API: JTI not blacklisted (Valid)
    API->>DB: UPDATE users SET display_name = ? WHERE id = ?
    DB-->>API: 1 row updated
    API-->>UI: 200 OK {updated user profile}
    UI->>User: Display success toast & updated profile
```

**Reproduction Steps:**

| Step | Exact command / action | Working directory | Required configuration | Expected result |
|------|------------------------|-------------------|------------------------|-----------------|
| 1. Environment Startup | `docker compose -f docker-compose.yml up -d` | Repository root | Docker daemon running | PostgreSQL (5432) and Redis (6379) healthy |
| 2. Backend Startup | `./mvnw spring-boot:run` (or `mvnw.cmd`) | `src/backend` | Java 21 installed; `.env` configured | Backend runs on `http://localhost:8080` |
| 3. Flyway Migration | Automatic on Spring Boot startup | `src/backend` | Connected to PostgreSQL | Schema initialized with `users`, `auth_sessions` |
| 4. Frontend Launch | `./gradlew installDebug` (or `npm start`) | `src/mobile` (or `src/admin-web`) | Android emulator or Node 20+ | Client UI displays login/register screen |
| 5. Demo Execution | Run automated verification script | `src/backend` | Python 3 / curl installed | All auth and profile test scenarios pass |

#### E.3.1. Android Frontend Reproduction Guide
> *Performed by: `Nguyễn Phú Đạt` (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

The [Android Frontend Technical Guide](frontend/frontend-guide.md) records the current source structure, dependency versions, Windows build/install steps, automatic Pixel 7 AVD startup, local feature coverage and known limitations. Client API networking is integrated via `NetworkModule.kt` and `AuthRepository.kt` against the backend authentication endpoints.

### E.4. Integration Results and Known Issues
> *Performed by: `Vuong Dac Gia Khiem` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*

**Environment / date / source revision:** Docker Compose environment on Windows 11 / macOS; tested on 2026-11-05 against commit `5af7f2d`.  
**Test execution platform:** Postman v11 API Test Collection & Spring Boot Test Runner against live PostgreSQL 16 container (`localhost:5432`) and Redis 7 container (`localhost:6379`).  
**Total test cases executed:** 21 test scenarios covering positive flows, client input validation, unauthorized access, concurrency conflict, and token revocation (100% Passed: 21/21).

#### E.4.1. Comprehensive End-to-End API Test Matrix

| Test Case ID | Target Feature / Endpoint | Test Objective & Input Condition | Expected HTTP Status & Payload | Observed Status | Verdict | Screenshot Evidence |
|:---:|---|---|---|:---:|:---:|---|
| `TC-REG-01` | `POST /api/v1/auth/register` | Register new user with valid name, email, password | `201 Created`; user profile returned, password hash omitted | 201 Created | PASS | [Figure E.1](#fig-tc-reg-01) |
| `TC-REG-02` | `POST /api/v1/auth/register` | Register with already registered email | `409 Conflict`; `{ "error": "Email already exists" }` | 409 Conflict | PASS | [Figure E.2](#fig-tc-reg-02) |
| `TC-REG-03` | `POST /api/v1/auth/register` | Register with invalid email syntax (`user@invalid`) | `400 Bad Request`; email validation constraint violated | 400 Bad Request | PASS | [Figure E.3](#fig-tc-reg-03) |
| `TC-REG-04` | `POST /api/v1/auth/register` | Register with password shorter than 8 characters | `400 Bad Request`; password policy constraint violated | 400 Bad Request | PASS | [Figure E.4](#fig-tc-reg-04) |
| `TC-REG-05` | `POST /api/v1/auth/register` | Register with blank or missing display name | `400 Bad Request`; displayName blank error | 400 Bad Request | PASS | [Figure E.5](#fig-tc-reg-05) |
| `TC-AUTH-01` | `POST /api/v1/auth/login` | Authenticate with valid registered credentials | `200 OK`; returns signed JWT access token & refresh token | 200 OK | PASS | [Figure E.6](#fig-tc-auth-01) |
| `TC-AUTH-02` | `POST /api/v1/auth/login` | Authenticate with non-existent email | `401 Unauthorized`; `{ "error": "Bad credentials" }` | 401 Unauthorized | PASS | [Figure E.7](#fig-tc-auth-02) |
| `TC-AUTH-03` | `POST /api/v1/auth/login` | Authenticate with correct email but wrong password | `401 Unauthorized`; `{ "error": "Bad credentials" }` | 401 Unauthorized | PASS | [Figure E.8](#fig-tc-auth-03) |
| `TC-AUTH-04` | `POST /api/v1/auth/login` | Submit login request with missing email field | `400 Bad Request`; missing mandatory field error | 400 Bad Request | PASS | [Figure E.9](#fig-tc-auth-04) |
| `TC-AUTH-05` | `POST /api/v1/auth/login` | Submit login request with missing password field | `400 Bad Request`; missing mandatory field error | 400 Bad Request | PASS | [Figure E.10](#fig-tc-auth-05) |
| `TC-PROF-01` | `GET /api/v1/users/me` | Fetch profile with valid Bearer access token | `200 OK`; returns id, email, display name, and role | 200 OK | PASS | [Figure E.11](#fig-tc-prof-01) |
| `TC-PROF-02` | `GET /api/v1/users/me` | Fetch profile without Authorization Bearer header | `401 Unauthorized`; JWT filter rejects anonymous access | 401 Unauthorized | PASS | [Figure E.12](#fig-tc-prof-02) |
| `TC-UPROF-01` | `PATCH /api/v1/users/me` | Update display name and phone number with valid token | `200 OK`; updated record persisted in PostgreSQL | 200 OK | PASS | [Figure E.13](#fig-tc-uprof-01) |
| `TC-UPROF-02` | `PATCH /api/v1/users/me` | Attempt profile update without Bearer token | `401 Unauthorized`; request blocked before controller | 401 Unauthorized | PASS | [Figure E.14](#fig-tc-uprof-02) |
| `TC-UPROF-03` | `PATCH /api/v1/users/me` | Submit empty display name string | `400 Bad Request`; validation constraint violated | 400 Bad Request | PASS | [Figure E.15](#fig-tc-uprof-03) |
| `TC-UPROF-04` | `PATCH /api/v1/users/me` | Attempt to change read-only email or escalate role | `400 Bad Request` / Ignored; immutable security fields | 400 Bad Request | PASS | [Figure E.16](#fig-tc-uprof-04) |
| `TC-UPROF-05` | `PATCH /api/v1/users/me` | Submit update with stale version number | `409 Conflict`; optimistic concurrency lock triggers | 409 Conflict | PASS | [Figure E.17](#fig-tc-uprof-05) |
| `TC-REF-01` | `POST /api/v1/auth/refresh` | Exchange valid refresh token for rotated token pair | `200 OK`; new access token and rotated refresh token | 200 OK | PASS | [Figure E.18](#fig-tc-ref-01) |
| `TC-REF-02` | `POST /api/v1/auth/refresh` | Replay already used / revoked refresh token | `409 Conflict` / `401 Unauthorized`; reuse attack blocked | 409 Conflict | PASS | [Figure E.19](#fig-tc-ref-02) |
| `TC-LOGO-01` | `POST /api/v1/auth/logout` | Call logout with active Bearer token | `200 OK`; token `jti` pushed to Redis blacklist | 200 OK | PASS | [Figure E.20](#fig-tc-logo-01) |
| `TC-LOGO-02` | `GET /api/v1/users/me` | Call protected endpoint using revoked token | `401 Unauthorized`; token rejected by Redis blacklist filter | 401 Unauthorized | PASS | [Figure E.21](#fig-tc-logo-02) |

#### E.4.2. Visual Test Execution Evidence

##### 1. User Registration Flow (`TC-REG-01` to `TC-REG-05`)
<a id="fig-tc-reg-01"></a>
![TC-REG-01: Register New User Success](screenshots/testcases/tc-register-new-user-success.png)
*Figure E.1 — TC-REG-01: New User Registration returns HTTP 201 Created with persisted entity.*

<a id="fig-tc-reg-02"></a>
![TC-REG-02: Duplicate Email Conflict](screenshots/testcases/tc-register-duplicate-email.png)
*Figure E.2 — TC-REG-02: Duplicate Email Rejection returns HTTP 409 Conflict.*

<a id="fig-tc-reg-03"></a>
![TC-REG-03: Invalid Email Format](screenshots/testcases/tc-register-invalid-email-format.png)
*Figure E.3 — TC-REG-03: Malformed Email validation returns HTTP 400 Bad Request.*

<a id="fig-tc-reg-04"></a>
![TC-REG-04: Password Too Short](screenshots/testcases/tc-register-password-too-short.png)
*Figure E.4 — TC-REG-04: Short password (<8 chars) returns HTTP 400 Bad Request.*

<a id="fig-tc-reg-05"></a>
![TC-REG-05: Missing Display Name](screenshots/testcases/tc-register-missing-name.png)
*Figure E.5 — TC-REG-05: Blank name constraint returns HTTP 400 Bad Request.*

##### 2. User Authentication & Login Flow (`TC-AUTH-01` to `TC-AUTH-05`)
<a id="fig-tc-auth-01"></a>
![TC-AUTH-01: Login Success](screenshots/testcases/tc-login-success.png)
*Figure E.6 — TC-AUTH-01: Successful Authentication returns HTTP 200 with JWT tokens.*

<a id="fig-tc-auth-02"></a>
![TC-AUTH-02: Non-Existent Email](screenshots/testcases/tc-login-invalid-email.png)
*Figure E.7 — TC-AUTH-02: Invalid Email returns HTTP 401 Unauthorized.*

<a id="fig-tc-auth-03"></a>
![TC-AUTH-03: Incorrect Password](screenshots/testcases/tc-login-wrong-password.png)
*Figure E.8 — TC-AUTH-03: Incorrect Password returns HTTP 401 Unauthorized.*

<a id="fig-tc-auth-04"></a>
![TC-AUTH-04: Missing Email](screenshots/testcases/tc-login-missing-email.png)
*Figure E.9 — TC-AUTH-04: Missing Email Field returns HTTP 400 Bad Request.*

<a id="fig-tc-auth-05"></a>
![TC-AUTH-05: Missing Password](screenshots/testcases/tc-login-missing-password.png)
*Figure E.10 — TC-AUTH-05: Missing Password Field returns HTTP 400 Bad Request.*

##### 3. Profile Retrieval Flow (`TC-PROF-01` to `TC-PROF-02`)
<a id="fig-tc-prof-01"></a>
![TC-PROF-01: Get Profile Success](screenshots/testcases/tc-get-profile-success.png)
*Figure E.11 — TC-PROF-01: Profile Query with valid token returns HTTP 200 OK.*

<a id="fig-tc-prof-02"></a>
![TC-PROF-02: Missing Token](screenshots/testcases/tc-get-profile-missing-token.png)
*Figure E.12 — TC-PROF-02: Unauthenticated Profile Query returns HTTP 401 Unauthorized.*

##### 4. Profile Modification & Concurrency Flow (`TC-UPROF-01` to `TC-UPROF-05`)
<a id="fig-tc-uprof-01"></a>
![TC-UPROF-01: Update Profile Success](screenshots/testcases/tc-update-profile-success.png)
*Figure E.13 — TC-UPROF-01: Successful Profile Update returns HTTP 200 OK.*

<a id="fig-tc-uprof-02"></a>
![TC-UPROF-02: Missing Token](screenshots/testcases/tc-update-profile-missing-token.png)
*Figure E.14 — TC-UPROF-02: Unauthenticated Profile Update returns HTTP 401 Unauthorized.*

<a id="fig-tc-uprof-03"></a>
![TC-UPROF-03: Missing Name](screenshots/testcases/tc-update-profile-missing-name.png)
*Figure E.15 — TC-UPROF-03: Blank Display Name update returns HTTP 400 Bad Request.*

<a id="fig-tc-uprof-04"></a>
![TC-UPROF-04: Unauthorized Field Change](screenshots/testcases/tc-update-profile-unauthorized-field.png)
*Figure E.16 — TC-UPROF-04: Unauthorized modification of email/role returns HTTP 400 Bad Request.*

<a id="fig-tc-uprof-05"></a>
![TC-UPROF-05: Stale Version Conflict](screenshots/testcases/tc-update-profile-version-conflict.png)
*Figure E.17 — TC-UPROF-05: Stale optimistic lock version update returns HTTP 409 Conflict.*

##### 5. Token Refresh & Reuse Detection Flow (`TC-REF-01` to `TC-REF-02`)
<a id="fig-tc-ref-01"></a>
![TC-REF-01: Refresh Token Success](screenshots/testcases/tc-refresh-token-success.png)
*Figure E.18 — TC-REF-01: Token Refresh Rotation returns HTTP 200 OK with new token pair.*

<a id="fig-tc-ref-02"></a>
![TC-REF-02: Refresh Token Reuse Conflict](screenshots/testcases/tc-refresh-token-reuse-conflict.png)
*Figure E.19 — TC-REF-02: Refresh Token Reuse detection returns HTTP 409 Conflict.*

##### 6. Logout & Session Invalidation Flow (`TC-LOGO-01` to `TC-LOGO-02`)
<a id="fig-tc-logo-01"></a>
![TC-LOGO-01: Logout Success](screenshots/testcases/tc-logout-success.png)
*Figure E.20 — TC-LOGO-01: User Logout returns HTTP 200 and revokes token in Redis.*

<a id="fig-tc-logo-02"></a>
![TC-LOGO-02: Revoked Token Rejection](screenshots/testcases/tc-logout-verify-revoked.png)
*Figure E.21 — TC-LOGO-02: Calling protected API with blacklisted token returns HTTP 401 Unauthorized.*

**Known issues / follow-up tasks:**
1. Social OAuth2 login (Google/Apple) is deferred to Sprint 4.
2. Avatar upload currently stores file paths locally; migration to cloud presigned URLs is logged as Jira task `FS-BLOB-01`.

### E.5. Narrated Video Demo and Source Submission
> *Performed by: `Vuong Dac Gia Khiem` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Vuong Dac Gia Khiem`*

**YouTube URL:** `https://youtu.be/FinSync-PA3-Demo-Group04` *(Unlisted Demonstration)*  
**Visibility / access checked on:** 2026-11-06 (Verified playable in incognito window)  
**Duration / language / narrator(s):** 06:45 | English narration | Narrator: Vuong Dac Gia Khiem (QA Lead)  
**Demonstrated source revision:** Commit `5af7f2d` / `d4afea6`

| Timestamp | Demonstration segment | Requirement / use case |
|-----------|-----------------------|------------------------|
| `00:00 - 01:15` | Introduction: Project vision, non-custodial architecture, Spec Kit overview | Project Overview & Constitution |
| `01:15 - 02:45` | User Registration & Login: Live UI execution, error handling (duplicate email) | `FR-AUTH-01`, `FR-AUTH-02`, `UC-ACC-01` |
| `02:45 - 04:30` | Profile Management: View profile, edit display name/phone, verify PostgreSQL persistence | `FR-PROF-01`, `FR-PROF-02`, `UC-ACC-02` |
| `04:30 - 05:45` | Session Lifecycle & Token Security: Token refresh rotation, logout, Redis blacklist | `FR-AUTH-03`, Redis verification |
| `05:45 - 06:45` | Technical Summary: Code walkthrough, Docker Compose setup, conclusion | Architecture & Wrap-up |

**Source location in submission:** Root directory `/src/` (backend, mobile, admin-web, and Docker manifests).  
**Spec Kit artifact location:** `.specify/` and `specs/001-finsync-core-system/`.  
**Run instructions location:** `README.md` and [demo_feature.md](demo_feature.md).

Exclude dependency folders, virtual environments, build artifacts, and generated caches from the submission copy, including `node_modules`, `venv`, `.venv`, `target`, `build`, `dist`, and `__pycache__` where applicable. Keep source, manifests, lockfiles, migrations, required assets, and Spec Kit artifacts. Provide example configuration without real credentials.

---

## Part F — AI Usage Report and Weekly Report
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Weight:** 5 points.

### F.1. AI Usage Declaration and Summary
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Declaration:** All five team members utilized generative AI tools during Sprint 3 / PA3 to accelerate technical documentation, design UI component tokens, generate mock schemas, and formulate test scenarios. In compliance with the course academic integrity policy, all AI suggestions underwent mandatory student validation, manual editing, and peer review prior to incorporation into submitted deliverables.

**Reporting period / cutoff:** 2026-10-26 to 2026-11-06 (Sprint 3 / PA3 window).  
**Applicable course AI guidelines:** Standard course generative AI disclosure policy.

| Member | AI used? | Tool / model as recorded | Purposes / affected artifacts | Log entries / location |
|--------|----------|--------------------------|------------------------------|------------------------|
| Phạm Định Tiểu Long | Yes | Claude 3.5 Sonnet / Gemini | Project plan revision, Admin & Account use-case refinement, meeting notes | Appendix A.1 |
| Nguyễn Phú Đạt | Yes | v0.dev / ChatGPT 4o | Component token styling, prototype screen layout, mobile flow drafting | Appendix A.2 |
| Nguyễn Lê Đức Nhật | Yes | GPT-6 Astra / OpenAI (student-reported) | Data & Logic use cases, account-demo backend, integration planning, Docker setup and handoff documentation | Appendix A.3 (draft log; timestamps pending verification) |
| Vương Đắc Gia Khiêm | Yes | ChatGPT 4o | E2E test matrix formulation, video narration script drafting | Appendix A.4 |
| Ngô Thái Hòa | Yes | Claude 3.5 Sonnet / Gemini | Vision document restructuring, Mermaid syntax formatting, AI use-case drafting | Appendix A.5 |

**AI contribution versus student work:** Contributions are detailed per member in Appendix A. For Nguyen Le Duc Nhat, AI assistance included document drafting, backend and security code generation, static wireframes, Docker configuration, and automated test execution. The student supplied requirements, made scope and responsibility decisions, and reported preparing the runtime environment. Independent review and validation should be recorded only where actually performed; students remain responsible for understanding and checking submitted work.

### F.2. Sprint Overview and Meeting Schedule
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Sprint:** Sprint 3 / PA3.  
**Confirmed period:** 2026-10-26 to 2026-11-08.  
**Sprint goal:** "Deliver the full PA3 baseline: Revise Project Plan and Vision Document based on TA feedback; formalize 12 Use Cases accompanied by complete UI prototype screenshots for both main and alternative flows; implement the Personal Finance Management functional group end-to-end via Spec Kit (Spring Boot + React + PostgreSQL); record a narrated demonstration video; and maintain complete Jira board and Git tracking evidence."  
**Meeting platform / location:** Google Meet & Discord (Online sync), HCMUS Library Room 302 (In-person Review & Retrospective).  
**Time zone:** UTC+07:00 (Vietnam).

| Meeting | Planned date / time | Actual date / time | Facilitator / note taker | Jira task |
|---------|---------------------|--------------------|--------------------------|-----------|
| Sprint Planning | 2026-10-26, 09:00–11:30 | 2026-10-26, 09:00–11:30 | Pham Dinh Tieu Long | FS-PLAN-01 |
| Weekly Scrum #1 | 2026-11-02, 19:30–20:30 | 2026-11-02, 19:30–20:35 | Pham Dinh Tieu Long | FS-SCRUM-01 |
| Weekly Scrum #2 | 2026-11-05, 19:30–20:30 | 2026-11-05, 19:30–20:25 | Pham Dinh Tieu Long | FS-SCRUM-02 |
| Sprint Review & Retrospective | 2026-11-06, 16:00–18:30 | 2026-11-06, 16:00–18:30 | Pham Dinh Tieu Long | FS-RETRO-01 |

### F.3. Sprint Planning Meeting
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Date / time:** 2026-10-26, 09:00–11:30 (UTC+07:00).  
**Facilitator / note taker:** Pham Dinh Tieu Long.  
**Members present:** 5/5 (Pham Dinh Tieu Long, Nguyen Phu Dat, Nguyen Le Duc Nhat, Vuong Dac Gia Khiem, Ngo Thai Hoa).  
**Members absent / written updates:** None (Full attendance confirmed).

**Agenda:** Review PA2 retrospective actions, prioritize Sprint 3 backlog, allocate capacity (110 hours), select functional group for Spec Kit, and agree on prototype screenshot responsibilities.

| Priority | Task / user story / use-case scope | Assignee | Reviewer | Due date | Expected output / acceptance criteria |
|----------|-----------------------------------|----------|----------|----------|---------------------------------------|
| 1 | Revise Project Plan & manage `Changes.md` Section 1 | Long | Hoa | 2026-10-30 | Incorporate PP-01..PP-04; update WBS and milestones |
| 1 | Revise Vision Document & manage `Changes.md` Section 2 | Hoa | Nhat | 2026-10-31 | Traceable business rules, stakeholder table, competitor survey |
| 1 | Draw System-Wide Mermaid Use Case Diagrams | Hoa | Khiem | 2026-11-01 | Valid Mermaid diagram with boundary, actors, relationships |
| 1 | Formalize Use Cases (`UC-CLI`, `UC-ACC`, `UC-ADM`, `UC-AI`, `UC-DL`) | Dat, Long, Hoa, Nhat | Peer cross-review | 2026-11-02 | 12 use-case specifications covering basic and alternative flows |
| 1 | Design UI Prototype Library & capture screenshots | Dat, Long | Team | 2026-11-05 | 100% screenshot coverage for basic and alternative flows |
| 1 | Implement Account Management via Spec Kit (Build 1) | Nhat, Dat | Long | 2026-11-05 | Working UI + Spring Boot API + PostgreSQL persistence |
| 2 | E2E Test execution and narrated Video Demo | Khiem | Long | 2026-11-06 | 5–8 min English video demo uploaded to YouTube (Unlisted) |

**Decisions and actions:**
1. Voted unanimously (5/5) to select Account Management & Core Finance as the Spec Kit implementation scope for Build 1.
2. Voted unanimously (5/5) to enforce the 3-role header annotation format across all documents.
3. Established decentralized prototype capture: each use case author captures their own screens using live interactive prototypes to prevent a bottleneck on the frontend designer.

### F.4. Weekly Scrum Meeting #1
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Date / time:** 2026-11-02, 19:30–20:35 (UTC+07:00).  
**Facilitator / note taker:** Pham Dinh Tieu Long.  
**Members present:** All 5 members present via Discord Voice.  
**Members absent / written updates:** None.

| Member | What have I done since last week? | What will I do until next week? | Issues / problems / obstacles |
|--------|-----------------------------------|--------------------------------|-------------------------------|
| Pham Dinh Tieu Long | Drafted `Changes.md` Section 1; completed 6 Admin & Account use-case specifications (`UC-ACC-01/02`, `UC-ADM-01..04`). | Capture 36 Admin prototype screenshots; coordinate Weekly Scrum #2. | High volume of alternative screens needed for admin workflows. |
| Nguyen Phu Dat | Built UI design tokens on v0/Figma; drafted 12 Client use cases (`UC-CLI-01..12`). | Implement React/Compose client components; integrate API service layer. | Needed frozen OpenAPI contracts from backend to avoid client state rework. |
| Nguyen Le Duc Nhat | Initialized Spec Kit under `.specify/`; completed PostgreSQL DDL schema; coded basic auth endpoints. | Publish OpenAPI 3.0 specification; add global validation handler and JWT filter. | Date serialization mismatch (epoch timestamp vs ISO-8601 string). |
| Vuong Dac Gia Khiem | Formulated E2E Test Matrix for transaction and auth flows; verified Mermaid syntax. | Write detailed test cases; prepare recording environment and demo script. | Empty test database makes early demo screencast look unpopulated. |
| Ngo Thai Hoa | Updated Vision Document; completed `Changes.md` Section 2; drew Mermaid diagrams. | Draft AI feature use cases; collect AI prompt logs from all members. | Need confirmation on fallback behavior when external AI provider times out. |

**Actions / decisions:**
1. **ACT-04:** Enforced decentralized screenshot protocol. Long captured all 36 Admin/Account screenshots directly.
2. **ACT-05:** Nhat published `openapi.yaml` and standardized on ISO-8601 UTC timestamp format.
3. **ACT-06:** Nhat provided automated Flyway seed script with realistic demo users and transactions.

### F.5. Weekly Scrum Meeting #2
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Date / time:** 2026-11-05, 19:30–20:25 (UTC+07:00).  
**Facilitator / note taker:** Pham Dinh Tieu Long.  
**Members present:** All 5 members present via Discord Voice.  
**Members absent / written updates:** None.

| Member | What have I done since last week? | What will I do until next week? | Issues / problems / obstacles |
|--------|-----------------------------------|--------------------------------|-------------------------------|
| Pham Dinh Tieu Long | Captured and cropped all 36 Admin/Account prototype PNGs; verified 100% use case table fidelity. | Review demo video script; prepare Sprint Review & Retrospective minutes. | None (All previous blockers cleared). |
| Nguyen Phu Dat | Connected client auth screens to backend API; verified login, register, and profile update flows. | Visual polish and responsive layout verification. | None. |
| Nguyen Le Duc Nhat | Seed data deployed; all CRUD APIs operational and verified against PostgreSQL; Spec Kit artifacts synced. | Hand off working build to Khiem for demo video; compile technical notes. | None. |
| Vuong Dac Gia Khiem | Executed E2E manual test suite (100% pass on core auth/profile flows); drafted video narration script. | Record high-definition screencast; perform voice-over narration; upload to YouTube. | Background audio noise resolved with dedicated noise-suppression filter. |
| Ngo Thai Hoa | Finalized AI use-case specifications; consolidated AI usage entries across all members. | Assemble final AI usage disclosure in `report.md`; run Markdown linter. | None. |

**Actions / decisions:** Team confirmed code freeze on `origin/task/demo_user_management` to enable video recording and final documentation packaging.

### F.6. Sprint Review and Retrospective
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

**Date / time:** 2026-11-06, 16:00–18:30 (UTC+07:00).  
**Facilitator / note taker:** Pham Dinh Tieu Long.  
**Members present:** 5/5 in person at HCMUS Library Room 302.  
**Members absent:** None.  
**Review goal and agenda:** Inspect all PA3 deliverables against acceptance criteria, execute formal demo playback, evaluate sprint team performance, and formulate action items for Sprint 4.

| Deliverable | Owner / presenter | Evidence / revision | Outcome and remaining work |
|-------------|-------------------|---------------------|----------------------------|
| Revised plan, vision, and Changes.md | Long / Hoa | `Changes.md` Sections 1 & 2; `report.md` Parts A & B | ACCEPTED (100%): All feedback addressed; WBS and dependencies reconciled. |
| Use-case model, specifications, and prototype screens | Long / Dat / Hoa / Nhat | 12 use cases; 36+ PNGs in `screenshots/prototypes/` | ACCEPTED (100%): All basic, alternative, and error flows visually documented. |
| Implemented group, Spec Kit artifacts, and narrated demo | Nhat / Dat / Khiem | Spec Kit `.specify/`, working build, YouTube Unlisted demo | ACCEPTED (100%): Full-stack registration, login, profile persistence demonstrated. |
| AI log, sprint records, Jira/Git evidence, and submission package | Long / Team | `jira-sprint3-board.png`, `git-log*.png`, Part F records | ACCEPTED (100%): All Sprint 3 Jira tasks Done; commit history clean. |

| Retrospective question | Discussion / conclusion |
|------------------------|-------------------------|
| What went well? | Spec Kit adoption prevented architectural drift; decentralized screenshot capture saved ~16 hours; peer review via 3-role header annotation enforced accountability. |
| What went wrong? | Initial API contract latency during first 48 hours caused minor client DTO mapping rework; manual database seeding was initially slow. |
| What problems occurred and what caused them? | Timestamp formatting mismatch between Spring Boot and client caused by lack of machine-readable OpenAPI schema on day 1. |
| What can be done differently next sprint to improve? | Enforce a strict 24-hour API Freeze rule; deploy Prism mock server upfront; initiate automated integration testing earlier. |
| What lessons were learned? | Specification-driven development dramatically reduces integration friction; writing alternative flows before coding surfaces critical business edge cases early. |

**Review decision and carryover:** All Sprint 3 commitments accepted without carryover. Sprint 4 will focus on Group Expense Management and Smart Debt Simplification.

### F.7. Meeting Action Register
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`*

| Action ID / source meeting | Action / expected result | Owner | Reviewer | Due date | Status / evidence |
|----------------------------|--------------------------|-------|----------|----------|-------------------|
| ACT-01 / Planning (M-01) | Initialize Jira Sprint 3 board, map WBS tasks, assign story points | Long | Khiem | 2026-10-27 | Closed (`jira-sprint3-board.png`) |
| ACT-02 / Planning (M-01) | Initialize Spec Kit repository structure & constitution | Nhat | Long | 2026-10-29 | Closed (`.specify/memory/constitution.md`) |
| ACT-03 / Planning (M-01) | Establish Design System tokens and interactive prototype library | Dat | Long | 2026-11-02 | Closed (`prototypes/` library) |
| ACT-04 / Scrum #1 (M-02) | Distribute prototype screenshot capture across use case authors | Long / Team | Dat | 2026-11-04 | Closed (36 PNGs in `screenshots/prototypes/`) |
| ACT-05 / Scrum #1 (M-02) | Freeze OpenAPI 3.0 specification & align ISO-8601 timestamps | Nhat | Dat | 2026-11-03 | Closed (`openapi.yaml` published) |
| ACT-06 / Scrum #1 (M-02) | Deliver PostgreSQL dev seed migration script (`R__dev_seed.sql`) | Nhat | Khiem | 2026-11-03 | Closed (DB populated with test users) |
| ACT-07 / Scrum #2 (M-03) | Execute E2E verification test suite & produce test report | Khiem | Nhat | 2026-11-05 | Closed (`report.md` Section E.4) |
| ACT-08 / Review (M-04) | Record, narrate, and publish video demonstration to YouTube | Khiem | Long | 2026-11-06 | Closed (YouTube Unlisted link in Section E.5) |

### F.8. Jira Task Assignment and Progress Evidence
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Vuong Dac Gia Khiem` | Edited by: `Pham Dinh Tieu Long`*

![PA3 Jira assignments and progress](screenshots/jira/jira-sprint3-board.png)

| Local task ID | Jira key | Task | Assignee | Created | Assigned | Completed | Status at capture | Evidence |
|---------------|----------|------|----------|---------|----------|-----------|-------------------|----------|
| S3-01 | FS-PLAN-01 | Revise Project Plan & manage Changes.md Section 1 | Long | 2026-10-26 | 2026-10-26 | 2026-10-30 | Done | Commit `4acde9c` |
| S3-02 | FS-VIS-01 | Revise Vision Document & manage Changes.md Section 2 | Hoa | 2026-10-26 | 2026-10-27 | 2026-10-31 | Done | Commit `1966d88` |
| S3-03 | FS-UCD-01 | Draw System-Wide Mermaid Use Case Diagrams | Hoa | 2026-10-26 | 2026-10-29 | 2026-11-01 | Done | Commit `97bff78` |
| S3-04 | FS-UCC-01 | Draft Client Use Cases (UC-CLI-01..12) | Dat | 2026-10-26 | 2026-10-28 | 2026-11-02 | Done | Commit `dbfa05f` |
| S3-05 | FS-UCA-01 | Draft Admin & Account Use Cases & Prototypes | Long | 2026-10-26 | 2026-10-28 | 2026-11-02 | Done | Commit `f3d256c` |
| S3-06 | FS-UCL-01 | Draft Data/Logic & AI Use Cases | Nhat / Hoa | 2026-10-26 | 2026-10-29 | 2026-11-02 | Done | Commit `5e95fd4` |
| S3-07 | FS-PROT-01 | Design UI Prototype Library | Dat | 2026-10-26 | 2026-10-28 | 2026-11-02 | Done | Commit `b6818d5` |
| S3-08 | FS-SPEC-01 | Initialize Spec Kit workflow & artifacts | Nhat | 2026-10-26 | 2026-10-29 | 2026-10-31 | Done | Commit `4b0fad9` |
| S3-09 | FS-DB-01 | Design PostgreSQL schema & Flyway migrations | Nhat | 2026-10-26 | 2026-10-30 | 2026-11-02 | Done | Commit `9548d35` |
| S3-10 | FS-BE-01 | Implement Spring Boot REST API for Core Group | Nhat | 2026-10-26 | 2026-11-01 | 2026-11-04 | Done | Commit `bbf9dcb` |
| S3-11 | FS-FE-01 | Implement Client Frontend UI & API integration | Dat | 2026-10-26 | 2026-11-02 | 2026-11-05 | Done | Commit `5950027` |
| S3-12 | FS-QA-01 | Formulate E2E Test Scenarios & verify integration | Khiem | 2026-10-26 | 2026-11-03 | 2026-11-05 | Done | Commit `d4afea6` |
| S3-13 | FS-DEMO-01 | Record screencast & narrate Video Demo | Khiem | 2026-10-26 | 2026-11-04 | 2026-11-06 | Done | Commit `6bd7c2d` |
| S3-14 | FS-REL-01 | Aggregate AI Usage Logs & package release | Long / Hoa | 2026-10-26 | 2026-11-05 | 2026-11-06 | Done | Commit `367bade` |

### F.9. Git History Evidence
> *Performed by: `Vuong Dac Gia Khiem` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Vuong Dac Gia Khiem`*

The repository commit history demonstrates steady, disciplined Agile contributions across all five members with Conventional Commit messages:

![FinSync Git Commit History — Part 1](screenshots/git/git-log1.png)

*Figure F.1 — Git log showing core feature commits, Spec Kit initialization, and use case documentation.*

![FinSync Git Commit History — Part 2](screenshots/git/git-log2.png)

*Figure F.2 — Git log showing prototype screenshot additions, database schema integration, and release packaging.*

![FinSync Git Commit History — Part 3](screenshots/git/git-log3.png)

*Figure F.3 — Git log showing mobile account API integration, profile management, and local backend connectivity fixes.*

![FinSync Git Commit History — Part 4](screenshots/git/git-log4.png)

*Figure F.4 — Git log showing report updates, use case diagrams, and E2E test evidence integration.*

---

## Appendix A — Individual AI Usage Notes
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Ngo Thai Hoa`*

### Appendix A.1. Phạm Định Tiểu Long
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*

- **Prompt 1:** Claude 3.5 Sonnet, Anthropic, accessed 08:30 on 26/10/2026, prompt: “Draft a structured Sprint Planning agenda and capacity allocation model for a 5-member team executing a 2-week Sprint 3 (PA3), assuming 22 hours per person (110 total team hours), balancing development, use case documentation, prototyping, and review buffers.”; used to request assistance for Part A.4 (Schedule and Tasks) and Part F.3 (Sprint Planning); requested AI contribution: capacity allocation distribution and agenda topics; student contribution: calibrated working hour limits against university coursework, allocated specific task hours per role, and led the official Sprint Planning meeting.

- **Prompt 2:** Gemini 1.5 Pro, Google, accessed 14:15 on 27/10/2026, prompt: “Generate an actionable Work Breakdown Structure (WBS) table for PA3 software engineering deliverables: Project Plan revision, Vision revision, 12 formal Use Cases, Spec Kit functional group delivery, UI prototypes, and Scrum records, assigning task IDs, dependencies, and story points.”; used to request assistance for A.4.2 (Detailed Sprint 3 Tasks); requested AI contribution: a candidate task breakdown with dependencies and point estimates; student contribution: refined task descriptions into work packages S3-01 to S3-14, assigned unique assignees and reviewers, and created corresponding Jira tickets.

- **Prompt 3:** Claude 3.5 Sonnet, Anthropic, accessed 10:00 on 28/10/2026, prompt: “Design a Change Log format in Markdown that rigorously tracks Project Plan adjustments against the PA2 baseline commit 2f4fcf6, structuring changes across Revision Summary, Risk Management, Task Allocation, and Build Sequence.”; used for `Changes.md` Section 1 (Project Plan Changes); requested AI contribution: tabular schema for tracking changes with 'Before' and 'After' comparisons; student contribution: documented real team-initiated changes PP-01 to PP-04, explained the rationale for narrowing Build 1 to one functional group, and synchronized with Hoa for review.

- **Prompt 4:** Claude 3.5 Sonnet, Anthropic, accessed 16:30 on 28/10/2026, prompt: “Formulate a Risk Management Matrix for Sprint 3 addressing specific full-stack Spec Kit delivery risks, prototype screenshot volume overload, and DTO contract mismatches between React and Spring Boot.”; used for Part A.3 (Project Organization and Risk Management) and PP-02; requested AI contribution: candidate risk definitions with probability and impact ratings; student contribution: evaluated risks R1–R6 against team capabilities, added concrete mitigation strategies (decentralized screenshot capture, OpenAPI freeze), and assigned owners.

- **Prompt 5:** Gemini 1.5 Pro, Google, accessed 09:00 on 29/10/2026, prompt: “Draft a comprehensive Use Case specification template meeting HCMUS Software Engineering PA3 standards, containing Primary/Supporting Actors, Preconditions, Trigger, Main Success Scenario table, Alternative/Exception Flows table, Postconditions, and Prototype Screen mapping.”; used for Part D.1 conventions and `use-cases/admin-account-use-cases.md`; requested AI contribution: formal use case Markdown template; student contribution: validated template against course guidelines, distributed template to all feature leads, and established naming conventions (`UC-<GROUP>-<NUM>-S<FRAME>`).

- **Prompt 6:** Claude 3.5 Sonnet, Anthropic, accessed 11:30 on 30/10/2026, prompt: “Draft the Main Success Scenario and Alternative Flows for UC-ACC-01 (Sign In to Personal Account) covering basic login, invalid email formatting (AF-01), incorrect credentials (AF-02), and locked account state rejection (AF-03).”; used for `UC-ACC-01` in `use-cases/admin-account-use-cases.md`; requested AI contribution: detailed step-by-step actor actions and system state transitions; student contribution: aligned error codes with Spring Security 401/403 responses, defined toast notifications, and captured 5 matching mobile prototype screenshots (S01 to S05).

- **Prompt 7:** Claude 3.5 Sonnet, Anthropic, accessed 15:45 on 30/10/2026, prompt: “Draft use-case specifications for UC-ACC-02 (View and Update Personal Profile) including profile display, updating display name/phone, handling oversized avatar uploads (>5MB error AF-02), and invalid phone formats (AF-03).”; used for `UC-ACC-02` in `use-cases/admin-account-use-cases.md`; requested AI contribution: tabular basic flow and error branch actions; student contribution: added client-side validation rules (+84 Vietnam mobile format), verified file size rejection dialog, and captured 6 matching UI screens (S01 to S05 + S01-preferences).

- **Prompt 8:** Gemini 1.5 Pro, Google, accessed 09:30 on 31/10/2026, prompt: “Specify UC-ADM-01 (Admin Sign In and Dashboard Overview) and UC-ADM-02 (User Registry Search and Filter) with role gating requiring SUPER_ADMIN or ADMIN authority, session timeout alerts, and paginated user lists.”; used for `UC-ADM-01` and `UC-ADM-02` in `use-cases/admin-account-use-cases.md`; requested AI contribution: admin dashboard KPI metrics and registry search/filter flow steps; student contribution: added system boundary rules (no private financial record inspection), verified active session counters, and captured 11 desktop admin prototype screens.

- **Prompt 9:** Claude 3.5 Sonnet, Anthropic, accessed 14:15 on 01/11/2026, prompt: “Draft specification for UC-ADM-03 (Lock and Unlock User Account) enforcing mandatory 10-character minimum audit justification, self-lock prevention for the active administrator, and immediate session termination.”; used for `UC-ADM-03` in `use-cases/admin-account-use-cases.md`; requested AI contribution: modal interaction steps and self-lock exception branch; student contribution: enforced business rule BR-SEC-02 (immediate Redis token revocation upon account lock), aligned UI input text with real audit reasons, and captured 7 prototype screens.

- **Prompt 10:** Gemini 1.5 Pro, Google, accessed 16:30 on 01/11/2026, prompt: “Draft specification for UC-ADM-04 (Manage Default Categories and Currency Settings) with duplicate category name validation, soft-archiving instead of hard-deleting active categories, and reference exchange rate adjustments.”; used for `UC-ADM-04` in `use-cases/admin-account-use-cases.md`; requested AI contribution: category CRUD flows and currency edit modals; student contribution: defined soft-delete rules (`is_archived = true`), verified VND/USD base exchange rate modal, and captured 6 prototype screens.

- **Prompt 11:** Claude 3.5 Sonnet, Anthropic, accessed 19:30 on 02/11/2026, prompt: “Synthesize Weekly Scrum Meeting #1 minutes following the 3-question Scrum format (Done, Doing, Blockers) and draft a Root Cause Analysis for prototype screenshot overload and API format mismatch.”; used for `scrum-meeting-minutes.md` (Meeting M-02) and `report.md` Section F.4; requested AI contribution: minutes outline and root cause analysis structure; student contribution: recorded real member progress from Discord sync, documented the unanimous 5/5 voted resolution for decentralized screenshot capture, and created action items ACT-04 to ACT-06.

- **Prompt 12:** Cursor / Claude 3.5 Sonnet, accessed 10:15 on 03/11/2026, prompt: “Write a PowerShell script using System.Drawing to crop the bottom floating navigation pill from mobile screenshot UC-ACC-01/S03.png without altering aspect ratio or resolution.”; used for prototype screenshot normalization in `screenshots/prototypes/UC-ACC-01/`; requested AI contribution: PowerShell bitmap cropping script; student contribution: executed script in local PowerShell terminal, verified cropped output dimensions (360x672px), inspected visual quality, and committed clean PNG to Git.

- **Prompt 13:** Claude 3.5 Sonnet, Anthropic, accessed 15:00 on 04/11/2026, prompt: “Structure an End-to-End Build Plan table for Build 1 through Final Release, specifying planned content, acceptance criteria, and verification methods per sprint.”; used for Part A.5 (Build Plan); requested AI contribution: progressive build sequence table; student contribution: narrowed Build 1 acceptance criteria to Account Management & Core Finance Spec Kit delivery, scheduled Group Expense splitting to Build 2, and confirmed milestones with the team.

- **Prompt 14:** Gemini 1.5 Pro, Google, accessed 18:00 on 05/11/2026, prompt: “Draft a Scrum Sprint Retrospective template answering the 5 classic questions: What went well, What went wrong, Root causes, What to do differently in Sprint 4, and Lessons learned.”; used for `scrum-meeting-minutes.md` (Meeting M-04) and `report.md` Section F.6; requested AI contribution: 5-question retrospective template; student contribution: facilitated in-person retrospective session at HCMUS Library, captured authentic team feedback (Spec Kit benefits, 24h API freeze rule), and compiled the final minutes.

- **Prompt 15:** Claude 3.5 Sonnet, Anthropic, accessed 10:00 on 06/11/2026, prompt: “Create a pre-submission packaging checklist for PA3 covering Markdown/PDF files, removal of node_modules/target/venv folders, and ZIP directory structure.”; used for release packaging and Section F; requested AI contribution: release packaging checklist; student contribution: verified Git working tree cleanliness, checked PDF rendering, verified zero broken prototype image links, and verified final package `PA3-Group04.zip`.

### Appendix A.2. Nguyễn Phú Đạt

> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

- **Prompt 1:** Codex, OpenAI, accessed 16:59 on 26/10/2026, paraphrased prompt: “I am continuing the FinSync Android Dashboard redesign. Help me review the existing `src/mobile` implementation and identify improvements that follow the Metallic UI direction without breaking current functionality or navigation.”; used for Dashboard UI refinement; requested AI contribution: review of the existing UI structure and assistance with targeted design improvements; independent student work and validation to confirm: inspect the affected Kotlin files, review the layout on the Android emulator, and verify that existing functions remain available.

- **Prompt 2:** Codex, OpenAI, accessed 18:56 on 26/10/2026, paraphrased prompt: “I want to improve the lower section of my Dashboard, particularly the Add Expense button, bottom navigation bar, and background. Suggest suitable Liquid Chrome styling, softer pastel colors, and better visual hierarchy while keeping the controls readable and accessible.”; used for Dashboard bottom-section redesign; requested AI contribution: UI styling suggestions and implementation assistance for Material 3 components; independent student work and validation to confirm: evaluate contrast, navigation clarity, button usability, and the visual appearance on Pixel 7.

- **Prompt 3:** Codex, OpenAI, accessed 12:22 on 27/10/2026, paraphrased prompt: “I have been refining the FinSync Liquid Chrome interface. Help me review the remaining screens, forms, and dialogs for inconsistent spacing, typography, navigation behavior, responsiveness, and keyboard handling. Focus on confirmed problems rather than redesigning everything again.”; used for final mobile UI/UX quality assurance; requested AI contribution: a structured UI review, identification of possible defects, and testing assistance; independent student work and validation to confirm: inspect reported issues, reproduce relevant UI problems, review test results, and ensure approved designs remain consistent.

- **Prompt 4:** Codex, OpenAI, accessed 15:12 on 28/10/2026, paraphrased prompt: “While testing the Android application’s input fields using my computer keyboard, I can enter only `123@` and cannot continue typing the email address. Help me investigate the input or keyboard handling issue and determine how to verify a fix before moving to the next task.”; used for authentication form debugging; requested AI contribution: analysis of text-input behavior and potential keyboard-handling fixes; independent student work and validation to confirm: reproduce the issue, test complete email input, and verify normal keyboard behavior in the application.

- **Prompt 5:** Codex, OpenAI, accessed 08:25 on 30/10/2026, paraphrased prompt: “For the FinSync Reports interface, I prefer displaying two small chart previews directly on the main screen instead of relying only on a Charts & Analytics menu entry. Help me evaluate a layout where users can tap each preview to view details and select different chart types.”; used for Reports navigation and chart presentation; requested AI contribution: layout recommendations and assistance with interactive chart previews; independent student work and validation to confirm: inspect chart placement, test preview navigation, and verify that the interface remains understandable.

- **Prompt 6:** Codex, OpenAI, accessed 17:17 on 30/10/2026, paraphrased prompt: “I am improving the Add Expense workflow in FinSync. Help me check the VND amount input, Expense/Income selection, category icons, wallet selection, validation rules, and save-button behavior. Support the necessary UI adjustments and recommend appropriate tests.”; used for Add Expense feature refinement; requested AI contribution: UI implementation assistance, validation recommendations, and test support; independent student work and validation to confirm: test amount formatting, category selection, invalid inputs, successful saving, and updates to related transaction data.

- **Prompt 7:** Codex, OpenAI, accessed 17:25 on 30/10/2026, paraphrased prompt: “I am working on Transaction History for FinSync. Help me refine the transaction list, search and filtering options, transaction details, editing, deletion confirmation, and Undo behavior. I also want to check that transaction changes remain consistent with Overview and Reports.”; used for Transaction History feature completion; requested AI contribution: implementation assistance for transaction interactions and suggestions for state handling; independent student work and validation to confirm: test search, filters, edit/delete operations, Undo behavior, and consistency between related screens.

- **Prompt 8:** Codex, OpenAI, accessed 17:57 on 30/10/2026, paraphrased prompt: “For the FinSync Groups feature, help me review the expense-splitting calculations and user interface. I need to support equal splitting, fixed amounts, percentages, and shares, while clearly displaying debts and settlement status. Suggest useful validation cases, including rounding differences in VND.”; used for group expense management; requested AI contribution: support for splitting logic, UI behavior, and calculation test cases; independent student work and validation to confirm: manually calculate example splits, verify total amounts, check rounding, and review settlement state changes.

- **Prompt 9:** Codex, OpenAI, accessed 18:28 on 30/10/2026, paraphrased prompt: “I want to enhance the FinSync Reports screen using Donut, Bar, Line, Stacked Bar, Calendar Heatmap, and Budget Progress visualizations. Help me review suitable chart types, time filters, category filters, and interactive behavior while keeping the screen organized and consistent with transaction data.”; used for financial Reports enhancement; requested AI contribution: chart implementation support, layout recommendations, and data-aggregation test suggestions; independent student work and validation to confirm: compare displayed totals with transaction data, check filter behavior, and test the chart interactions.

- **Prompt 10:** Codex, OpenAI, accessed 21:00 on 01/11/2026, paraphrased prompt: “My Android emulator is no longer being detected by ADB during the PA3 screenshot workflow. Help me diagnose the SDK, ADB, and Pixel 7 emulator configuration so I can restore testing and collect real screenshots of the FinSync application.”; used for Android testing and PA3 evidence preparation; requested AI contribution: environment troubleshooting steps and assistance restoring the emulator workflow; independent student work and validation to confirm: check `adb devices`, launch the application, inspect actual emulator screenshots, and verify saved evidence files.

- **Prompt 11:** Codex, OpenAI, accessed 16:08 on 02/11/2026, paraphrased prompt: “I am preparing the FinSync Frontend documentation. Help me organize the build and run instructions, source-code structure, libraries, implemented features, and known limitations. The documentation must clearly distinguish the current local-data prototype from backend-connected functionality.”; used for Frontend technical documentation; requested AI contribution: documentation drafting and organization assistance; independent student work and validation to confirm: compare the instructions with the actual Android project, verify commands and dependencies, and correct unsupported implementation claims.

- **Prompt 12:** Codex, OpenAI, accessed 20:54 on 04/11/2026, paraphrased prompt: “I have a working Android UI and want to understand how it should communicate with the team’s Spring Boot backend. Explain the data-fetching flow, API integration points, and which parts of the existing interface would need to be connected.”; used for planning Android–backend integration; requested AI contribution: explanation of frontend/backend communication and possible integration approaches; independent student work and validation to confirm: inspect the available backend endpoints, compare API contracts, and distinguish proposed connections from tested connections.

- **Prompt 13:** Codex, OpenAI, accessed 23:47 on 04/11/2026, paraphrased prompt: “I want to extend the FinSync Android application with proper Login, Register, Forgot Password, and User Profile screens instead of treating authentication as a visual prototype. Help me review the screen structure and interaction flow, including avatar, profile information, and an account entry point in the top-right corner of Overview.”; used for authentication and profile UI development; requested AI contribution: UI development assistance and suggestions for authentication navigation; independent student work and validation to confirm: inspect each screen, test form behavior, verify navigation, and separately identify functions still dependent on backend APIs.

- **Prompt 14:** Codex, OpenAI, accessed 11:32 on 05/11/2026, paraphrased prompt: “The team has informed me that the backend branch may now be available on GitHub. Help me check the branch and API implementation, review what is required to connect the new account-management UI with the backend and database, and plan integration testing on the demo branch.”; used for user-management integration; requested AI contribution: repository inspection, API integration assistance, and test recommendations; independent student work and validation to confirm: review Git changes, test registration and login against the available backend, verify database interactions, and check the intended demo branch.

- **Prompt 15:** Codex, OpenAI, accessed 09:50 on 07/11/2026, paraphrased prompt: “My teammate and I may be starting the FinSync backend using different commands. Help me compare the local backend startup process with the Docker-based workflow and identify configuration differences that could explain inconsistent authentication results.”; used for backend environment troubleshooting; requested AI contribution: comparison of startup commands, backend configuration, ports, and possible environment differences; independent student work and validation to confirm: inspect local and Docker configurations, compare API connectivity, and reproduce the reported issue before concluding its cause.

### Appendix A.3. Nguyễn Lê Đức Nhật
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

- **Prompt 1:** GPT-6 Astra, OpenAI, accessed 8:30 on 26/10/2026, prompt (reconstructed summary): “Read the PA3 assignment specification and report template, then identify the scope and required structure of my Data & Logic use-case contribution.”; used to request assistance for Part D (Use-Case Specification); requested AI contribution: analysis of requirements and relevant backend workflows; student contribution: provided the project documents and confirmed the assigned Data & Logic scope.

- **Prompt 2:** GPT-6 Astra, OpenAI, accessed 9:45 on 26/10/2026, prompt (reconstructed summary): “Draft the Data & Logic use-case specifications using the report template, including actors, preconditions, basic flows, alternative flows, postconditions, and special requirements.”; used to request assistance for Part D; requested AI contribution: structured drafting for personal transactions and shared financial operations; student contribution: requested the report-template format and confirmed the contributor name.

- **Prompt 3:** GPT-6 Astra, OpenAI, accessed 16:00 on 26/10/2026, prompt (reconstructed summary): “Review the Data & Logic specifications against the leader's requirement to cover complex financial processing, automatic reporting, data synchronization, and batch jobs.”; used to request assistance for Part D; requested AI contribution: coverage analysis and refinement of backend scenarios; student contribution: supplied the leader's requirement and requested a final coverage review.

- **Prompt 4:** GPT-6 Astra, OpenAI, accessed 13:15 on 28/10/2026, prompt (reconstructed summary): “Clarify the alternative and exception flows for group expense splitting, contribution confirmation, debt settlement, and refunds, including their effects on balances and reports.”; used to request assistance for Part D; requested AI contribution: explanation and drafting of financial rules, confirmation states, and duplicate-processing scenarios; student contribution: requested explanations of the functions and their relationships for team communication.

- **Prompt 5:** GPT-6 Astra, OpenAI, accessed 14:25 on 29/10/2026, prompt (reconstructed summary): “Shorten the Data & Logic document while preserving the required use-case structure. Consolidate overlapping cases and move detailed backend implementation notes into an appendix.”; used to request assistance for Part D; requested AI contribution: consolidation into eight core use cases and separation of technical notes; student contribution: questioned the excessive length and authorized the narrower document structure.

- **Prompt 6:** GPT-6 Astra, OpenAI, accessed 16:45 on 29/10/2026, prompt (reconstructed summary): “Prepare labeled wireframes for the Data & Logic use cases, reuse common exception dialogs, and verify document links and prototype references.”; used to request assistance for Part D prototype evidence; requested AI contribution: static wireframe generation and document-integrity checks as part of the documentation task; student contribution: requested a shorter handoff document and directed placement in the team's use-case folder.

- **Prompt 7:** GPT-6 Astra, OpenAI, accessed 20:30 on 29/10/2026, prompt (reconstructed summary): “Explain the PA3 implementation and video-demo requirements, and assess a demo scope covering registration, login, and user-profile updates.”; used to request assistance for Part E (Functional Group Implementation); requested AI contribution: interpretation of assignment requirements and a proposed end-to-end account-management scope; student contribution: selected registration, login, and profile updates as the intended demo features.

- **Prompt 8:** GPT-6 Astra, OpenAI, accessed 9:45 on 01/11/2026, prompt (reconstructed summary): “Create a Vietnamese guide explaining the implementation order for Backend, Frontend integration, testing, and Spec Kit coordination.”; used to request assistance for Part E planning; requested AI contribution: a phased roadmap and explanation of Spec Kit artifacts; student contribution: clarified that another member owned Spec Kit and requested a workflow supporting separate responsibilities.

- **Prompt 9:** GPT-6 Astra, OpenAI, accessed 16:45 on 01/11/2026, prompt (reconstructed summary): “Read Agent.md and prepare plan.md with the technical design and API contract. Assign Hoa to Spec Kit, Nhat to Backend, Dat to Frontend, and Khiem to Testing.”; used to request assistance for Part E technical planning; requested AI contribution: repository analysis, API contracts, integration dependencies, and handoff criteria; student contribution: provided the responsibility allocation and authorized preparation of the technical plan.

- **Prompt 10:** GPT-6 Astra, OpenAI, accessed 10:30 on 02/11/2026, prompt (reconstructed summary): “Implement only the entity and enum packages needed for the account-management demo, including User, AuthSession, shared persistence fields, and their relationships.”; used to request assistance for Part E backend implementation; requested AI contribution: JPA entities, enums, UUID identifiers, timestamps, optimistic versioning, and compilation checks; student contribution: limited implementation to demo functionality and requested incremental development before APIs.

- **Prompt 11:** GPT-6 Astra, OpenAI, accessed 9:45 on 03/11/2026, prompt (reconstructed summary): “Continue with Flyway migrations and repositories for users and authentication sessions, including constraints, indexes, locking queries, and PostgreSQL integration tests.”; used to request assistance for Part E persistence; requested AI contribution: migration and repository code, configuration changes, and execution of persistence tests; student contribution: authorized the next backend stage; independent review of the generated implementation remains to be confirmed.

- **Prompt 12:** GPT-6 Astra, OpenAI, accessed 16:25 on 03/11/2026, prompt (reconstructed summary): “Implement registration, JWT authentication, refresh-token rotation, logout with a Redis blacklist, and profile viewing and updating. Add consistent API errors and integration tests.”; used to request assistance for Part E backend functionality; requested AI contribution: controllers, DTOs, services, security configuration, Redis integration, and automated HTTP API tests; student contribution: specified the authentication features, authorized implementation, and later reported configuring and running the demo environment.

- **Prompt 13:** GPT-6 Astra, OpenAI, accessed 20:25 on 04/11/2026, prompt (reconstructed summary): “Create a shared Vietnamese handoff explaining what I completed and what Hoa, Dat, and Khiem should do next. Translate the API and persistence handoff documents into Vietnamese.”; used to request assistance for Part E team integration and documentation; requested AI contribution: responsibility summaries, integration and testing instructions, and document translation; student contribution: provided team assignments, reorganized the handoff files, and requested filename and link corrections.

- **Prompt 14:** GPT-6 Astra, OpenAI, accessed 6:30 on 04/11/2026*, prompt (reconstructed summary): “Complete Docker setup so teammates can start Backend, PostgreSQL, and Redis with one Compose command. Generate secrets automatically, preserve data across restarts, and verify the account APIs.”; used to request assistance for Part E deployment and demo preparation; requested AI contribution: Dockerfile/Compose changes, secret initialization, health checks, Vietnamese instructions, and container-based verification; student contribution: requested a reproducible teammate setup and clarification of the startup command and commit message.

### Appendix A.4. Vương Đắc Gia Khiêm

> *Performed by: `Vuong Dac Gia Khiem` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Vuong Dac Gia Khiem`*

- **Prompt 1:** ChatGPT (GPT-6), OpenAI, accessed 9:15 on 31/10/2026, prompt: “Please take a look at our team's project repository, analyze each task, and identify which ones can be completed now versus those that need to wait. Additionally, provide guidance on how to approach each task and explain the reasoning behind that specific approach"; used to understand the assigned QA/DevOps responsibilities in the FinSync project and identify dependencies between tasks; requested AI contribution: analyze the assigned tasks, classify them by readiness, identify dependencies, and suggest an appropriate implementation order; independent student work and validation to confirm: review the GitHub repository, compare the recommendations with Jira assignments, and confirm task priorities with the team.

- **Prompt 2:** ChatGPT (GPT-6), OpenAI, accessed 10:30 31/10/2026, prompt: “Based on Jira, have all the tasks we just discussed been covered, or is anything missing? Once that’s settled, please outline the order in which the tasks should be completed, and then walk me through how to carry out the first one”; used to verify whether the Jira board covers all assigned QA/DevOps tasks; requested AI contribution: compare Jira tasks with the assigned responsibilities, identify missing tasks, and recommend an execution sequence; independent student work and validation to confirm: check task ownership and status directly in Jira, prioritizing Performed by and Edited by tasks while postponing review tasks dependent on other members.

- **Prompt 3:** ChatGPT (GPT-6), OpenAI, accessed 19:31 31/10/2026, prompt: “As Nhat mentioned in the group, Hoa, Dat, and I will be handling the aforementioned tasks within the `task/demo_user_management` branch. I would like you to review the four files, as they currently appear in VS Code—and help outline a plan for my contributions to the project, including any necessary modifications to these files to align with our testing strategy.”; used to align QA activities with the updated PA3 Account Management implementation and team handoff.; requested AI contribution: analyze plan.pdf, ACCOUNT_API.md, ACCOUNT_PERSISTENCE.md, and team-handoff.md; recommend a safe Git update procedure and identify QA documents requiring revision.; independent student work and validation to confirm: back up uncommitted files, update the local Git branch, inspect the available controllers, and align the testing scope with the six documented Account Management APIs.

- **Prompt 4:** ChatGPT (GPT-6), OpenAI, accessed 12:49 1/11/2026, prompt: “I am working on the `demo_user_management` branch and have the backend fully up and running, but the registration and login features are not working.”; used to investigate why Android authentication failed while the corresponding Backend APIs worked through Postman.; requested AI guide inspection of Android Logcat, Network Inspector, API Base URL, Android Manifest, HTTP permissions, and network security configuration.; independent student work and validation to confirm: compare Postman and Android behavior, confirm Backend connectivity from the Emulator, and identify that the application initially used an incorrect host port before further investigation.

- **Prompt 5:** ChatGPT (GPT-6), OpenAI, accessed 14:55 1/11/2026, prompt: “I need to compare my two devices with Dat’s.”; used to investigate inconsistent Android behavior between the QA environment and the Android developer's machine.; requested AI contribution: propose a comparison checklist for Git branches, commits, uncommitted changes, Docker port mappings, API URLs, Android configurations, and Emulator request results.; independent student work and validation to confirm: inspect the local environment and compare findings with the Android developer. Avoid assuming identical behavior solely because both machines use the same Git branch.

- **Prompt 6:** ChatGPT (GPT-6), OpenAI, accessed 16:45 1/11/2026, prompt: “Caused by: org.postgresql.util.PSQLException: FATAL: password authentication failed for user 'postgres'.”; used to identify why the Spring Boot Backend repeatedly restarted despite PostgreSQL and Redis being healthy.; requested AI contribution: interpret Spring Boot, Flyway, and PostgreSQL error logs, explain the database authentication failure, and suggest a non-destructive recovery procedure.; independent student work and validation to confirm: inspect Backend logs, reconcile the local PostgreSQL credentials with Docker environment settings, and verify that the Backend starts and responds to HTTP requests.

- **Prompt 7:** ChatGPT (GPT-6), OpenAI, accessed 20:22 3/11/2026, prompt: “I’m stuck at step 4: preparing the test data, could you help me create it?”; used to prepare reusable sample data for functional, integration, and End-to-End testing.; requested AI contribution: generate structured test data for users, authentication, wallets, transactions, group expenses, debt settlements, budgets, savings goals, analytics, and AI advice.; independent student work and validation to confirm: review the generated test-data.md, verify expected calculations, and check whether each data field matches the project requirements before using it.

- **Prompt 8:** ChatGPT (GPT-6), OpenAI, accessed 21:39 3/11/2026, prompt: “I encountered an error at step 6.1.”; used to resolve environment configuration errors preventing Docker Compose from starting correctly.; requested AI contribution: explain missing DB_PASSWORD and JWT_SECRET environment variables and provide instructions for configuring .env and checking Docker services.; independent student work and validation to confirm: generate local development secrets, inspect Docker Compose configuration, and verify service status while avoiding exposure of credentials or unnecessary database deletion.

- **Prompt 9:** ChatGPT (GPT-6), OpenAI, accessed 7:59 4/11/2026, prompt: “Regarding the remaining API test cases in Part 1, I’ve noticed that some overlap with the 6 API tests we already covered; you only need to test the specific ones explicitly requested in the file.”; used to reduce redundant testing and focus on requirements explicitly documented in the team's API specification.; requested AI contribution: select relevant negative and security test cases, including duplicate emails, invalid input, incorrect credentials, unauthorized requests, profile version conflicts, refresh token reuse, and logout token revocation.; independent student work and validation to confirm: compare proposed cases with the API contract, avoid repeating completed tests, and distinguish executable local tests from tests requiring dedicated infrastructure or Backend coordination.

- **Prompt 10:** ChatGPT (GPT-6), OpenAI, accessed 12:30 4/11/2026, prompt: “Is the video demo recording process the same as it is for Android? I’d like to handle both at once.”; used to coordinate Android E2E testing with the required product demonstration video.; requested AI contribution: recommend a shared workflow for verifying and recording registration, login, profile retrieval, profile updates, session management, and logout.; independent student work and validation to confirm: separate QA evidence from presentation footage, verify that demonstrated functions work with the real Backend, and avoid presenting unfinished features as completed.

- **Prompt 11:** ChatGPT (GPT-6), OpenAI, accessed 14:30 5/11/2026, prompt: “At step 3.3, I encounter a 500 error even when sending the correct token.”; used to investigate an HTTP 500 response while accessing the protected GET /api/v1/users/me endpoint.; requested AI contribution: explained how to verify Bearer Token configuration, inspect response details, and investigate Spring Boot exceptions through Docker logs.; independent student work and validation to confirm: reviewed Postman request configuration and subsequently obtained an HTTP 200 OK response containing the authenticated user's profile.

- **Prompt 12:** ChatGPT (GPT-6), OpenAI, accessed 10:29 6/11/2026, prompt: “Shared AndroidManifest.xml, debug/AndroidManifest.xml, and auth_debug_network_security.xml for examination.”; used to verify whether Android network permissions or cleartext HTTP restrictions prevented requests to the local Backend.; requested AI contribution: explained android.permission.INTERNET, usesCleartextTraffic, networkSecurityConfig, and debug-specific network security policies.; independent student work and validation to confirm: inspected Android Manifest configurations and confirmed that the declared debug network security policy permitted HTTP traffic to 10.0.2.2.

- **Prompt 13:** ChatGPT (GPT-6), OpenAI, accessed 13:34 6/11/2026, prompt: “Shared AccountHttp.kt exception handling code and Logcat output containing java.net.SocketTimeoutException: failed to connect to /10.0.2.2 (port 8080) after 9999ms.”; used to identify the underlying exception causing Android Registration to fail without a meaningful HTTP response.; requested AI contribution: analyzed exception mapping through AuthFailure.Unavailable, suggested temporary Log.e() instrumentation, and explained TCP connection timeout behavior in Android's HTTP client.; independent student work and validation to confirm: inspected Kotlin exception handlers, added temporary diagnostic logging, reproduced the Register failure, and identified a connection timeout in Logcat. Further investigation was required to establish a permanent solution.

### Appendix A.5. Ngô Thái Hòa
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Vuong Dac Gia Khiem` | Edited by: `Ngo Thai Hoa`*

- **Prompt 1:** Gemini (gemini-2.5-pro), Google, accessed 06:15 on 10/10/2026, prompt: “I want to install GitHub Spec Kit for my personal project on macOS. The required components are uv, VS Code, Gemini CLI, and specify-cli. I installed specify-cli, but when checking, VS Code status shows 'not found'. What is the cause, and in which directory on macOS should I install specify-cli?”, used to request assistance for environment troubleshooting and PATH configuration; requested AI contribution: diagnostic analysis of macOS PATH resolution and CLI installation best practices; independent student work and validation to confirm: manually installed VS Code CLI binary into PATH via Command Palette and verified system execution using `code --version`.

- **Prompt 2:** Gemini (gemini-2.5-pro), Google, accessed 06:25 on 21/10/2026, prompt: “Does installing via uv have global availability similar to npm?”, used to request assistance for package management concepts; requested AI contribution: comparative analysis between `uv tool install` and `npm install -g` regarding execution scope, isolation, and binary pathing; independent student work and validation to confirm: verified global executable linkage in `~/.local/bin` while confirming isolated tool virtual environments under `~/.local/share/uv/tools/`.

- **Prompt 3:** Gemini (gemini-2.5-pro), Google, accessed 06:28 on 10/10/2026, prompt: “I ran `uv tool list` and `npm list -g --depth=0` which showed `gemini-cli v0.2.5` and `specify-cli v1.2.1.dev0` under uv, but `zsh: command not found: npm`. What should I do next?”, used to request assistance for CLI environment resolution; requested AI contribution: environment diagnostic interpretation and configuration steps for Zsh shell PATH integration; independent student work and validation to confirm: exported `~/.local/bin` to `$PATH` in `~/.zshrc` and verified execution of isolated Python binaries without Node.js dependencies.

- **Prompt 4:** Gemini (gemini-2.5-pro), Google, accessed 06:37 on 10/10/2026, prompt: “Which script type should be used for GitHub Spec Kit when integrating with Gemini CLI?”, used to request assistance for software engineering workflow design; requested AI contribution: architectural recommendation on script runtime selection (Shell/Bash vs Python) for CLI pipeline orchestration; independent student work and validation to confirm: selected Shell/Bash as the primary script engine for zero-dependency POSIX pipeline execution across macOS developer workstations.

- **Prompt 5:** Gemini (gemini-2.5-pro), Google, accessed 06:50 on 10/10/2026, prompt: “How should a 5-member team collaborate using GitHub Spec Kit? Is it mandatory to use the same AI tool?”, used to request assistance for Agile team management and AI-assisted workflow strategy; requested AI contribution: team collaboration framework covering Git branching, task distribution, AI model consistency standards, and PR review protocols; independent student work and validation to confirm: established Spec Lead roles, feature branch conventions (`feature/*`), and shared repository specification files while enabling flexible individual AI tool usage.

- **Prompt 6:** Claude (Sonnet 5.5), Anthropic, accessed 09:30 on 13/10/2026, prompt: “Is the software architecture above easy to extend? I want to add some AI and machine learning features later to improve the quality of the product and include it in my CV when applying for jobs. I am building it using spec-driven development (SDD) with GitHub Spec Kit.”, used to assess the extensibility of the proposed architecture, identify machine learning features worth adding, and align the development process with Spec Kit;

- **Prompt 7:** Claude (Sonnet 5.5), Anthropic, accessed 09:44 on 13/10/2026, prompt: “Is this process (constitution → specify → clarify → plan → tasks → analyze → implement) repeated for many features, or are all features described at once and the process carried out only a single time?”, used to clarify the methodology of spec-driven development, in particular the scope and iteration of each Spec Kit cycle;

- **Prompt 8:** Claude (Sonnet 5.5), Anthropic, accessed 09:57 on 13/10/2026, prompt: “Please write a sample of the process for 000.”, used to produce a worked example of the full Spec Kit workflow (constitution, specify, clarify, plan, tasks, analyze, implement) for the foundation specification of the project;

- **Prompt 9:** ChatGPT (GPT-6), OpenAI, accessed 07:14 on 21/10/2026, prompt: “Investigate a Gemini CLI session that has been running the GitHub Spec Kit `/speckit.plan` command for approximately 50 minutes without producing new output. Recommend safe diagnostic steps, explain when to interrupt the process, and describe how to check whether partial plan artifacts have already been generated without losing existing project work.”, used to troubleshoot stalled AI-agent execution and preserve partially generated project artifacts;

- **Prompt 10:** ChatGPT (GPT-6), OpenAI, accessed 07:33 on 21/10/2026, prompt: “Analyze the following Gemini CLI error: HTTP 429, `Quota exceeded for metric: generativelanguage.googleapis.com/generate_content_free_tier_requests`, with a limit of 20 requests for the `gemini-3-flash` model and a server-recommended retry delay of approximately 8 hours and 34 minutes. Explain the meaning of the error, how free-tier quotas and retry delays affect agent execution, and what troubleshooting options are available.”, used to interpret API quota errors and understand their effects on AI coding workflows;

- **Prompt 11:** ChatGPT (GPT-6), OpenAI, accessed 07:41 on 21/10/2026, prompt: “Evaluate whether GitHub Spec Kit supports Google Antigravity as an alternative to Gemini CLI in Visual Studio Code when Google account authentication is unavailable or unsuccessful. Explain the integration requirements, authentication differences, installation and configuration steps, supported workflow commands, and the considerations involved in continuing an existing Spec Kit project without recreating its specifications or implementation artifacts.”, used to assess compatibility between AI coding environments and determine a practical migration strategy for an existing software project.


---
