# PA4-2026 — Changes from PA3 — FinSync (Group 04)
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

**Course:** CS300 - CSC13002 - Introduction to Software Engineering  
**Project:** FinSync — Personal Finance & Group Expense Manager with AI Advisor  
**Submission:** PA4-2026 — Revised Use-Case Specification, Software Architecture Document, and Build 2 Implementation  
**PA3 Baseline:** [PA3 report](../PA3/report.md) and its use-case catalogues in [PA3/use-cases/](../PA3/use-cases/client-use-cases.md)  
**PA4 Revision:** [PA4 report](report.md), Sprint 4 (`2026-11-17` to `2026-12-06`)  
**Target Code Baseline:** Branch `feat/pa4-group-expense-debt` (commit `66bb585`, Build 2 runtime)  
**Last Updated:** `2026-11-27`

This single `Changes.md` covers every revised section and supporting artifact in the PA4-2026 submission, organized by source of change (Section 1: TA feedback; Section 2: team-initiated changes).

---

## 1. Revisions Driven by Teaching Assistant (TA) Feedback
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

* **TA Feedback Received:** None. As of `2026-11-27`, the team has not received formal TA feedback on the PA3 submission (Project Plan, Vision Document, use cases, prototypes, or Spec Kit artifacts).
* **Consequence:** No revision in this submission is attributed to TA feedback. All updates documented in Section 2 are proactive, team-initiated improvements and implementation alignments.
* **Contingency:** If TA feedback is received before final packaging, each item will be logged below using `FB-xx` identifiers and reconciled in [PA4 report](report.md) Part A.

| Change ID | TA Feedback | Affected PA3 Section | PA4 Response | Status |
| :--- | :--- | :--- | :--- | :--- |
| — | None received | — | All PA4 revisions are documented in Section 2 (`TI-01`–`TI-13`). | Not applicable |

---

## 2. Team-Initiated Changes
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

**Revision Summary:** In the absence of TA feedback, the team audited the PA3 use-case specifications against the Build 2 implementation (**Group Expense Management** and **Smart Debt Simplification**) and explicitly recorded every rule refinement, scope boundary, and documented deviation. In addition, the team introduced the **Software Architecture Document** (Parts B, C, and D: Tech Stack, C4 Model Levels 1–3, and Deployment Diagram) and expanded end-to-end Spec Kit implementation from one functional group in PA3 to two functional groups in PA4.

### 2.1. Section Coverage Matrix

| PA3 Document / Section | PA4 Location | Change IDs / Status |
| :--- | :--- | :--- |
| Part C — Use-Case Model (Mermaid diagrams) | Part A | Structure retained; scope & behavior notes in `TI-01`–`TI-05`, `TI-13` |
| Part D — `UC-ACC-01`, `UC-ACC-02` (account & profile) | Part A.2 | `TI-13` (Modified) |
| Part D — `UC-CLI-10`, `UC-CLI-11` (groups, expense split) | Part A.2 | `TI-01`, `TI-03` (Modified) |
| Part D — `UC-CLI-12`, `UC-DL-07` (debts, settlement) | Part A.2 | `TI-02` (Modified — documented deviation) |
| Part D — `UC-DL-05`, `UC-DL-08` (shared fund, refunds) | Part A.2 | `TI-04` (Scope note) |
| Part D — `UC-CLI-06`, `UC-CLI-07`, `UC-DL-02` (edit/delete/Undo) | Part A.2 | `TI-05` (Scope note) |
| Part D — Remaining UCs (`UC-ADM`, `UC-AI`, personal `UC-CLI-01`–`05`, `08`–`09`, other `UC-DL`) | Part A.2 | Unchanged |
| Part E — Build 1 functional group | Part E | `TI-11`, `TI-12` (Extended to 2 Build 2 functional groups) |
| *(New in PA4)* Software Architecture Document | Parts B, C, D | `TI-06`–`TI-10` (Added) |

---

### 2.2. Use-Case Specification Changes (Part A)

#### TI-01 — Group expense and split rules aligned with the implementation
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

| Field | Details |
| :--- | :--- |
| **Change type** | Modified |
| **Source and reason** | Team decision to align `UC-CLI-11` and `UC-DL-06` with the Spring Boot `ExpenseSplitCalculator` and the connected Android client. |
| **PA3 location** | [uc-cli-11-add-and-split-group-expense.md](../PA3/use-cases/uc-cli-11-add-and-split-group-expense.md); `UC-DL-06` in [data-logic-use-cases.md](../PA3/use-cases/data-logic-use-cases.md) |
| **Before — PA3** | Specified four split modes (`Equal`, `Exact`, `Percentage`, `Shares`) with remainder rounding described only as "handled deterministically" without a concrete tie-breaker; the PA3 Spec Kit `001` draft listed only `Equal`, `Exact`, and `Percentage`. |
| **After — PA4** | All four modes (`EQUAL`, `EXACT`, `PERCENTAGE`, `SHARES`) are implemented. Online server allocation computes shares in integer minor units using the **largest-remainder rule** with ascending participant `splitOrder` tie-breaking so shares always sum exactly to the expense amount (e.g., `100,001 VND` split across 2 members = `50,001 + 50,000`). Online groups operate in whole `VND` amounts and require a single funding source (`MEMBERS` single payer or `GROUP_FUND` with sufficient computed balance). The separate on-device ledger preserves its earlier payer-remainder rule. |
| **PA4 location** | [report.md](report.md) Part A.2, Part C.4, Part E.6 |
| **Impact** | Preserves all user flows while specifying exact server vs. device rounding and funding invariants. |
| **Review outcome** | Pending reviewer sign-off (`Pham Dinh Tieu Long`) |

#### TI-02 — Debt view and settlement recording
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

| Field | Details |
| :--- | :--- |
| **Change type** | Modified (documented deviation) |
| **Source and reason** | Team decision following the Build 2 data model (`database_design.md` v1.0 and Spec Kit `002`). |
| **PA3 location** | [uc-cli-12-view-and-settle-group-debts.md](../PA3/use-cases/uc-cli-12-view-and-settle-group-debts.md); `UC-DL-07` in [data-logic-use-cases.md](../PA3/use-cases/data-logic-use-cases.md) |
| **Before — PA3** | `UC-DL-07` specified a two-step lifecycle where the debtor submits a `PENDING` claim and only the recipient confirms it before debt balances decrease (PA3 backend notes already flagged a difference in `database_design.md` v1). |
| **After — PA4** | The debt screen displays server-derived net member balances and a minimized transfer plan computed by `DebtSimplificationEngine` (greedy two-heap algorithm, $O(V \log V)$). A member records an external payment (full or partial) after an explicit confirmation prompt; the server persists it directly as `status = CONFIRMED` under pessimistic group row locking and `requestId` idempotency. FinSync never moves real money. The `PENDING` $\rightarrow$ recipient-confirmation/rejection/withdrawal workflow of `UC-DL-07` is **not implemented in Build 2** and remains scheduled as target behavior for a future release. |
| **PA4 location** | [report.md](report.md) Part A.2, Part C.4, Part E.6 |
| **Impact** | Overpayment protection, partial payments, and idempotency are enforced; two-party pending claim confirmation is deferred. |
| **Review outcome** | Pending team/reviewer sign-off (`Pham Dinh Tieu Long`) |

#### TI-03 — Group membership and invitations
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

| Field | Details |
| :--- | :--- |
| **Change type** | Modified |
| **Source and reason** | Team decision replacing PA3's local prototype-only invitation code with real authenticated backend APIs. |
| **PA3 location** | [uc-cli-10-create-join-and-view-groups.md](../PA3/use-cases/uc-cli-10-create-join-and-view-groups.md) |
| **Before — PA3** | Group join-by-invitation existed only as a simulated local prototype flow without persistent server verification or access isolation. |
| **After — PA4** | Online groups support an owner (`OWNER`), unlinked guest members (`user_id IS NULL`), and single-use invitation tokens created by the owner and accepted by a second authenticated account, with strict access denial for non-members. Features not yet wired in the Android UI (guest rename/restore, targeted guest claim, token revocation, group archive/ownership transfer/leave) and friend/email invite drafts remain scoped outside the Build 2 UI adapter. |
| **PA4 location** | [report.md](report.md) Part A.2, Part E.6.1 |
| **Impact** | Establishes real multi-account security boundaries while documenting deferred UI controls. |
| **Review outcome** | Pending reviewer sign-off (`Pham Dinh Tieu Long`) |

#### TI-04 — Shared fund and refunds
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

| Field | Details |
| :--- | :--- |
| **Change type** | Scope note |
| **Source and reason** | Team decision to clarify implemented Build 2 backend capabilities versus deferred Android UI screens. |
| **PA3 location** | `UC-DL-05`, `UC-DL-08` in [data-logic-use-cases.md](../PA3/use-cases/data-logic-use-cases.md) |
| **Before — PA3** | Full shared-wallet contribution claims, custodian approvals, expense refunds, and unused-fund returns were specified conceptually. |
| **After — PA4** | The Build 2 backend implements group fund persistence (`group_wallets`, `group_fund_entries`) and supports `GROUP_FUND` as an expense funding source. Dedicated Android fund-entry screens, contribution claim/custodian confirmation, expense refunds, and unused-fund return flows are excluded from the demonstrated Build 2 UI scope; PA3 specifications remain the target design. |
| **PA4 location** | [report.md](report.md) Part A.2, Part E.6.1 |
| **Impact** | Prevents grading mismatch between documented UI flows and the demonstrated Android build. |
| **Review outcome** | Pending reviewer sign-off (`Pham Dinh Tieu Long`) |

#### TI-05 — Correction, deletion, and Undo of group records
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

| Field | Details |
| :--- | :--- |
| **Change type** | Scope note |
| **Source and reason** | The Build 2 backend enforces append-only ledger integrity with no edit or delete endpoints for online group expenses or settlements. |
| **PA3 location** | [uc-cli-06-view-and-edit-transaction.md](../PA3/use-cases/uc-cli-06-view-and-edit-transaction.md), [uc-cli-07-delete-and-restore-transaction.md](../PA3/use-cases/uc-cli-07-delete-and-restore-transaction.md); `UC-DL-02` in [data-logic-use-cases.md](../PA3/use-cases/data-logic-use-cases.md) |
| **Before — PA3** | Edit, delete, and Undo behaviors were documented across transaction specifications without distinguishing immutable online group ledgers from personal/device records. |
| **After — PA4** | Edit, delete, and Undo remain available for personal and on-device ledger entries; online group expenses and confirmed settlements are strictly append-only (read-only after submission) in Build 2. |
| **PA4 location** | [report.md](report.md) Part A.2, Part E.6.1 |
| **Impact** | Aligns UX expectations and use-case preconditions with immutable server accounting. |
| **Review outcome** | Pending reviewer sign-off (`Pham Dinh Tieu Long`) |

#### TI-13 — Account registration, authentication, and profile use cases (`UC-ACC-01`, `UC-ACC-02`)
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

| Field | Details |
| :--- | :--- |
| **Change type** | Modified |
| **Source and reason** | Team refinement to align account specifications with Spring Security 6, BCrypt hashing, and attachment/avatar storage rules. |
| **PA3 location** | `UC-ACC-01`, `UC-ACC-02` in [admin-account-use-cases.md](../PA3/use-cases/admin-account-use-cases.md) |
| **Before — PA3** | Basic registration, login, and profile update flows without explicit OAuth2 exception handling, presigned avatar upload flow, or immutable email constraint notes. |
| **After — PA4** | `UC-ACC-01` adds strengthened password validation rules (minimum 12-character policy aligned with client/backend) and OAuth2 Google exception flows (target social auth). `UC-ACC-02` documents the presigned URL avatar upload flow (target Cloud Storage) and explicit immutable email constraint verified against the PostgreSQL `users` table. |
| **PA4 location** | [report.md](report.md) Part A.2 |
| **Impact** | Resolves earlier inconsistency where `UC-ACC-01`/`02` were marked revised in `report.md` A.2 while unlisted in `Changes.md`. |
| **Review outcome** | Pending reviewer sign-off (`Pham Dinh Tieu Long`) |

---

### 2.3. Software Architecture Document (New in PA4: Parts B, C, D)

#### TI-06 — Comprehensive technology stack specification (Part B.2)
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Nguyen Le Duc Nhat (24120403) | Edited by: Ngo Thai Hoa (24120051)*

| Field | Details |
| :--- | :--- |
| **Change type** | Added |
| **Source and reason** | New mandatory PA4 Section B requirement to formally specify all frontend, backend, database, caching, storage, and AI technologies. |
| **PA3 location** | Not in PA3 as a formal architecture section (only high-level stack notes in Vision and Spec Kit `plan.md`). |
| **Before — PA3** | Stack details were scattered across `README.md`, Vision assumptions, and Sprint 3 planning notes without version/rationale verification. |
| **After — PA4** | Added a comprehensive Tech Stack specification covering Android Jetpack Compose (`compileSdk 35`, Compose BOM `2024.09.00`, Kotlin `1.9.24`), React 18 / TypeScript 5 Web Admin Portal, Spring Boot 3 / Java 21 LTS, PostgreSQL 16 (Flyway), Redis 7 (`redis:7-alpine`), Cloud Storage (local private baseline $\rightarrow$ AWS S3/Cloudinary target), and Gemini LLM (via isolated FastAPI microservice), with explicit **Implemented Baseline vs. Target Architecture** separation and build-manifest audit. |
| **PA4 location** | [report.md](report.md) Part B.2; standalone specification in [tech-stack-specification.md](tech-stack-specification.md) |
| **Impact** | Eliminates ambiguity between active Build 2 dependencies and target PA5 extensions. |
| **Review outcome** | Pending reviewer sign-off (`Nguyen Le Duc Nhat`) |

#### TI-07 — C4 Model Level 1 System Context diagram and narrative (Part B.3–B.4)
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

| Field | Details |
| :--- | :--- |
| **Change type** | Added |
| **Source and reason** | New mandatory PA4 Section B requirement for a Mermaid C4 Level 1 diagram and written architectural rationale. |
| **PA3 location** | Not in PA3 (new PA4 artifact). |
| **Before — PA3** | Only workflow activity diagrams existed in PA2/PA3 Vision Section B.5; no formal C4 System Context boundary diagram existed. |
| **After — PA4** | Added an A4-formatted Mermaid `flowchart TB` C4 Level 1 diagram placing **FinSync Platform** at the center, surrounded by three human actors (`Regular User`, `Group Owner / Member`, `System Administrator`) and four external systems (`Google Auth`, `Banking SMS Sources`, `Cloud Storage / AWS S3`, `Google Gemini AI`), accompanied by an actor/dependency catalog and trust-boundary narrative (non-custodial financial boundary, AI PII-stripping boundary, storage decoupling). |
| **PA4 location** | [report.md](report.md) Part B.3–B.4; standalone specification in [c4-level1-system-context.md](c4-level1-system-context.md) |
| **Impact** | Formally establishes system scope, external dependencies, and security boundaries. |
| **Review outcome** | Pending reviewer sign-off (`Pham Dinh Tieu Long`) |

#### TI-08 — C4 Model Level 2 Container diagram (Part C.1–C.2)
> *Performed by: Nguyen Le Duc Nhat (24120403) | Reviewed by: Nguyen Phu Dat (24120038) | Edited by: Nguyen Le Duc Nhat (24120403)*

| Field | Details |
| :--- | :--- |
| **Change type** | Added; PA3 planned containers narrowed for runtime conformance |
| **Source and reason** | Mandatory PA4 Section C requirement and strict grading rule requiring diagrams to match implemented runtime containers. |
| **PA3 location** | Not in PA3 as a C4 diagram; planned containers appeared in Spec Kit `001` plan and `README.md`. |
| **Before — PA3** | Early architecture notes included a Python AI service and React Admin Portal as active system containers. |
| **After — PA4** | The C4 Level 2 Container Diagram models strictly the 5 active runtime containers in the Build 2 `docker-compose.yml` (`feat/pa4-group-expense-debt`) and `src/`: **Android Mobile App**, **Spring Boot REST API Server (`backend`)**, **PostgreSQL 16 (`postgres`)**, **Redis 7 Token Blacklist (`redis`)**, and **Secret Bootstrap (`init-secrets`)**. Planned PA5 containers (React Admin Portal, AI Advisory Service) are intentionally excluded from runtime C4 L2/L3 diagrams until implemented. |
| **PA4 location** | [report.md](report.md) Part C.1–C.2 |
| **Impact** | Guarantees zero-penalty runtime conformance for Build 2 container documentation. |
| **Review outcome** | Pending reviewer sign-off (`Nguyen Phu Dat`) |

#### TI-09 — C4 Model Level 3 Backend and Frontend component diagrams (Part C.3–C.7)
> *Performed by: Nguyen Le Duc Nhat (24120403) and Nguyen Phu Dat (24120038) | Reviewed by: Ngo Thai Hoa (24120051) and Nguyen Le Duc Nhat (24120403) | Edited by: Nguyen Le Duc Nhat (24120403) and Nguyen Phu Dat (24120038)*

| Field | Details |
| :--- | :--- |
| **Change type** | Added |
| **Source and reason** | Mandatory PA4 Section C requirement to zoom into internal components of both Backend and Frontend containers. |
| **PA3 location** | Not in PA3 (new PA4 artifact). |
| **Before — PA3** | No C4 Level 3 component diagrams or class-level conformance catalog existed. |
| **After — PA4** | Added Backend Component Diagram (Security/Error layer, REST Controllers, Services, `ExpenseSplitCalculator`, `DebtSimplificationEngine`, JPA Repositories mapped 1-to-1 to `com.finsync` classes) and two Frontend Component Diagrams (main app/server-group flow and device-only supporting components at commit `66bb585`), plus a Code-to-Architecture Conformance Audit in Part C.7. |
| **PA4 location** | [report.md](report.md) Part C.3–C.7; sources in `screenshots/c4/` |
| **Impact** | Provides full 1-to-1 traceability between source files and documented components. |
| **Review outcome** | Pending reviewer sign-off (`Ngo Thai Hoa` for Backend L3; `Nguyen Le Duc Nhat` for Frontend L3) |

#### TI-10 — Infrastructure Deployment diagram (Part D)
> *Performed by: Vuong Dac Gia Khiem (24120342) | Reviewed by: Nguyen Le Duc Nhat (24120403) | Edited by: Vuong Dac Gia Khiem (24120342)*

| Field | Details |
| :--- | :--- |
| **Change type** | Added |
| **Source and reason** | Mandatory PA4 Section D requirement mapping C4 Level 2 containers onto execution nodes, ports, and protocols. |
| **PA3 location** | Not in PA3 (new PA4 artifact). |
| **Before — PA3** | Only basic Docker Compose startup commands existed without a formal deployment node topology. |
| **After — PA4** | Added Mermaid Deployment Diagram and node specification mapping containers across the Android client runtime and Docker Compose host (`finsync-network`), documenting exposed/internal ports (`8080`, `5432`, `6379`) and transport protocols. |
| **PA4 location** | [report.md](report.md) Part D |
| **Impact** | Documents physical/logical runtime topology and network boundaries. |
| **Review outcome** | Pending reviewer sign-off (`Nguyen Le Duc Nhat`) |

---

### 2.4. Implementation Scope (Part E)

#### TI-11 — Two functional groups delivered via Spec Kit (Build 2)
> *Performed by: Nguyen Le Duc Nhat (24120403) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Nguyen Le Duc Nhat (24120403)*

| Field | Details |
| :--- | :--- |
| **Change type** | Scope expansion |
| **Source and reason** | PA4 Section E mandate to implement two additional full-stack functional groups using Spec Kit SDD. |
| **PA3 location** | [PA3 report](../PA3/report.md) Part A.2, Part E |
| **Before — PA3** | PA3 planned "Account Management & Core Financial Operations" under `specs/001-finsync-core-system/`, with Build 1 delivering the authenticated Account & Session persistence baseline (`users`, `auth_sessions`). |
| **After — PA4** | Build 2 delivers two complete functional groups on top of the Build 1 baseline: **Group Expense Management** and **Smart Debt Simplification** (`specs/002-group-expense-debt-simplification/`: `spec.md`, `plan.md`, `data-model.md`, `contracts/backend-api.yaml`, `tasks.md` `T001`–`T030`, `quickstart.md`). On the `feat/pa4-group-expense-debt` baseline, Flyway `V2` adds 9 relational tables (`categories`, `groups`, `group_members`, `group_invitations`, `group_wallets`, `group_fund_entries`, `group_expenses`, `group_expense_splits`, `settlements`) and `V3` seeds default categories, bringing the Build 2 schema to 11 tables. |
| **PA4 location** | [report.md](report.md) Part E.1–E.3 |
| **Impact** | Expands end-to-end backend and database implementation from 2 tables to 11 tables across 2 full functional groups. |
| **Review outcome** | Pending reviewer sign-off (`Pham Dinh Tieu Long`) |

#### TI-12 — Android Jetpack Compose client connected to Build 2 Backend APIs
> *Performed by: Nguyen Phu Dat (24120038) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Nguyen Phu Dat (24120038)*

| Field | Details |
| :--- | :--- |
| **Change type** | Modified / extended |
| **Source and reason** | PA4 Section E requirement that both functional groups be integrated end-to-end (UI + REST API + PostgreSQL). |
| **PA3 location** | [PA3 report](../PA3/report.md) Part E |
| **Before — PA3** | Only Android account/auth screens called the backend; group, wallet, and reporting flows operated solely on simulated/on-device data. |
| **After — PA4** | Delivered authenticated **"Server groups"** mode connected to live group-expense and simplified-debt APIs (explicitly separated from the existing **"On this device"** ledger), verified by 9 native Android screenshots (`FE-01`–`FE-09`), 98/98 Android JVM tests, 15 UI/live integration scenarios, 21/21 backend tests, and PostgreSQL inspection logs. |
| **PA4 location** | [report.md](report.md) Part E.6; [frontend evidence index](evidence/frontend/README.md) |
| **Impact** | Completes real full-stack integration for both Sprint 4 functional groups. |
| **Review outcome** | Pending reviewer sign-off (`Pham Dinh Tieu Long`) |

---

## 3. Pre-Submission Synchronization Checklist (Internal Release Gate)
> *Performed by: Ngo Thai Hoa (24120051) | Reviewed by: Pham Dinh Tieu Long (24120087) | Edited by: Ngo Thai Hoa (24120051)*

*Note: This release checklist tracks remaining synchronization steps across `report.md` and Git branches prior to generating the final `PA4-Group04.zip` archive. Resolve and remove this section once all gates pass.*

| Gate ID | Synchronization Issue Detected | Required Action Before Packaging | Owner | Status |
| :--- | :--- | :--- | :--- | :--- |
| **SYNC-01** | **Branch divergence (`documentation` vs. `feat/pa4-group-expense-debt`):** Current `documentation` branch still holds an earlier `002-client-finance` snapshot (`pom.xml` Spring Boot `3.3.6`, `ai-service`, Flyway `V1`–`V7`), whereas `PA4/report.md` Parts C & E and `Changes.md` document `feat/pa4-group-expense-debt` (`66bb585`, Spring Boot `4.1.1`, `redis`, `init-secrets`, Flyway `V1`–`V3`). | Merge `feat/pa4-group-expense-debt` (and finalized `PA/PA4` docs) into the final submission branch so `src/` matches Parts C, D, E 100%, or update Parts B–E if a different code baseline is submitted. | Nhat; Long | Open |
| **SYNC-02** | **Part A placeholders in [report.md](report.md):** Lines 91, 100–102, and 108–109 still contain `<!-- TODO: [Hoa] ... -->` and outdated references to `FB-01`–`FB-04` and `TI-01`–`TI-05`. | Replace `TODO`s in `report.md` Part A.1–A.3 with "No TA feedback received", complete `UC-CLI-10`/`11`/`12` summary rows, and reference `TI-01`–`TI-13`. | Hoa | Open |
| **SYNC-03** | **Part B integration in [report.md](report.md):** Section B.2/B.3 still contains earlier draft text and `TODO` in B.4 (line 179), plus Spring Boot version discrepancy (`3.3.x` in B.2 vs `4.1.1` in C.2). | Embed finalized content from [tech-stack-specification.md](tech-stack-specification.md) and [c4-level1-system-context.md](c4-level1-system-context.md) into `report.md` Part B, aligning Spring Boot version with the merged `pom.xml`. | Hoa; Nhat | Open |
| **SYNC-04** | **Part D Deployment alignment in [report.md](report.md):** Part D.1 draws `Google Gemini AI Platform` and `HTTPS`, whereas `feat/pa4-group-expense-debt` uses local HTTP on port `8080` and excludes `ai-service`, while Part D.3 still has a `TODO` (line 568). | Update Part D.1 to show `HTTP / REST (Port 8080)` for local Docker, align nodes with Part C (`init-secrets`, `backend`, `postgres`, `redis`), and complete Part D.3 narrative. | Khiem | Open |
| **SYNC-05** | **Team sign-off on `TI-02`/`TI-04` deviations & reviewer dates:** All `Review outcome` fields in Section 2 currently show `Pending reviewer sign-off`. | Record actual acceptance dates after peer review and close/remove Section 3 before compiling `Changes.pdf`. | Long; Team | Open |
