# PA3-2026 — Changes from PA2 — FinSync (Group 04)
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

**Project:** FinSync — Personal Finance & Group Expense Manager with AI Advisor  
**Submission:** PA3-2026 — Second submission of the Project Plan and Vision Document  
**PA2 baseline:** [PA2 report](../PA2/report.md), submitted revision: `2f4fcf6`  
**PA3 revision:** [PA3 report](report.md), revision: `Sprint 3 Working Draft`  
**Last updated:** 2026-11-02

> **Template instructions — remove before submission:** Use this single file for all PA3 revision records. Complete the two document sections below and duplicate change-entry blocks as needed. Record actual changes, including additions, removals, corrections, and updated planning information. Distinguish TA feedback from team-initiated changes; do not invent feedback. Use section names and IDs rather than page numbers alone. Replace all TODOs and record the actual performer, reviewer, and editor immediately after each section header. If a section is unchanged, state that explicitly in its coverage table. Export the completed file to PDF along with the other submitted Markdown documents.

## 1. Project Plan Changes
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

**Baseline document:** PA2 report, Part A — Project Plan.  
**Revised document:** PA3 report, Part A — Revised Project Plan.  
**TA feedback received:** No formal feedback has been received from the Teaching Assistant (TA) for PA2 as of 2026-11-02. All revisions documented below are team-initiated improvements.  
**Revision summary:** In the absence of TA feedback, the team conducted an internal review to proactively advance the project plan for Sprint 3. Key revisions include establishing the PA2 baseline (`2f4fcf6`), updating the risk management matrix for Spec Kit full-stack delivery, breaking down detailed Sprint 3 tasks (S3-01 to S3-24) with capacity allocations, and realigning Build 1 acceptance criteria to focus on the Personal Finance Management functional group.

### 1.1. Section Coverage
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| PA2 section | PA3 location | Change IDs or unchanged status |
|---|---|---|
| A.1 Introduction | A.1 Introduction and Revision Summary | PP-01 (Modified) |
| A.2 Project Overview | A.2 Project Overview | Unchanged |
| A.3 Project Organization | A.3 Project Organization and Risk Management | PP-02 (Modified) |
| A.4.1–A.4.3 Process, Sprint Overview, and Detailed Tasks | A.4 Process, Schedule, and Sprint 3 Tasks | PP-03 (Added / Modified) |
| A.4.4–A.4.5 Build Plan and Later Sprints | A.5 Build Plan and Later Sprints | PP-04 (Modified) |

### 1.2. PP-01 — Integration of Revision Baseline and Scope Updates
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Field | Details |
|---|---|
| Change type | Modified |
| Source and reason | Team decision following internal retrospective on 2026-10-26 to establish formal baseline tracking against PA2 and define explicit Sprint 3 planning horizons in the absence of TA feedback. |
| PA2 location | Section A.1 (Introduction) |
| Before — PA2 | Basic descriptive introduction of the FinSync system and general project goals without structured feedback reconciliation or revision tracking tables. |
| After — PA3 | Section expanded to "A.1 Introduction and Revision Summary" including formal baseline reference to PA2 revision `2f4fcf6` and clear Sprint 3 planning horizon definition (2026-10-26 to 2026-11-08). |
| PA3 location | Section A.1 (Introduction and Revision Summary) |
| Related records | Scrum Meeting #1 Notes (2026-10-26), Jira Task `FS-PLAN-01` |
| Review outcome | Accepted on 2026-10-28 by Ngo Thai Hoa |

### 1.3. PP-02 — Refinement of Risk Management Matrix for Sprint 3
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Field | Details |
|---|---|
| Change type | Modified |
| Source and reason | Team Sprint 3 retrospective and risk reassessment regarding end-to-end Spec Kit integration bottlenecks and prototype screenshot workload. |
| PA2 location | Section A.3 (Project Organization and Risk Management) |
| Before — PA2 | Generic project risks (R1 to R9) defined during PA2 kickoff without specific mitigations for Spec Kit task breakdown dependencies or UI prototype volume. |
| After — PA3 | Reassessed probability and impact for R2 (Scope creep) and R5 (Fullstack integration); introduced mitigation protocols for Spec Kit task tracking and decentralized prototype screenshot capture across feature leads (Long, Nhật, Hòa, Đạt). |
| PA3 location | Section A.3 (Project Organization and Risk Management) |
| Related records | Jira Task `FS-RISK-02`, Sprint Planning 3 Meeting Notes |
| Review outcome | Accepted on 2026-10-29 by Ngo Thai Hoa |

### 1.4. PP-03 — Detailed Sprint 3 WBS, Task List, and Capacity Allocation
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Field | Details |
|---|---|
| Change type | Added / Modified |
| Source and reason | PA3 specification requirement to replace high-level targets with an actionable, dated Sprint 3 plan covering use cases, prototypes, and Spec Kit implementation. |
| PA2 location | Section A.4.1–A.4.3 (Process, Sprint Overview, and Detailed Tasks) |
| Before — PA2 | Section A.4.3 detailed only completed Sprint 2 tasks (S2-01 to S2-27). Sprint 3 was represented solely as a high-level placeholder row in the Sprint Overview table. |
| After — PA3 | Replaced Sprint 2 list with a granular Sprint 3 Work Breakdown Structure (S3-01 to S3-24) totaling ~110 team hours. Added owner effort, reviewer pairing, explicit dependencies, and an individual capacity allocation table for all 5 members. |
| PA3 location | Section A.4 (Process, Schedule, and Sprint 3 Tasks) |
| Related records | Jira Sprint 3 Board, Conventional Commits policy, PhanCong.md |
| Review outcome | Accepted on 2026-10-30 by Ngo Thai Hoa |

### 1.5. PP-04 — Realignment of Build Plan and Spec Kit Functional Group Milestone
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Field | Details |
|---|---|
| Change type | Modified |
| Source and reason | PA3 specification requirement narrowing implementation scope from multiple broad features to exactly one fully functional end-to-end group (Personal Finance Management). |
| PA2 location | Section A.4.4–A.4.5 (Build Plan and Later Sprints) |
| Before — PA2 | Build 1 was broadly planned to include Auth, Profile, Wallet, and Personal Transactions simultaneously across mobile and backend without verified end-to-end Spec Kit integration. |
| After — PA3 | Section promoted to major heading A.5. Formally confirmed Build 0 completion from Sprint 2. Refined Build 1 acceptance criteria to mandate end-to-end execution of the Personal Finance Management functional group (UI + Spring Boot API + PostgreSQL persistence) driven strictly by Spec Kit artifacts (`spec.md`, `plan.md`, `tasks.md`). |
| PA3 location | Section A.5 (Build Plan and Later Sprints) |
| Related records | Spec Kit feature branch `001-finsync-core-system`, Docker Compose configuration |
| Review outcome | Accepted on 2026-10-31 by Ngo Thai Hoa |

### 1.6. Outstanding Project Plan Feedback
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

None. All internal review items regarding the Project Plan have been fully resolved through revision items PP-01, PP-02, PP-03, and PP-04.

---
## 2. Vision Document Changes
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

**Baseline document:** PA2 report, Part B — Vision Document.  
**Revised document:** PA3 report, Part B — Detailed Vision Document.  
**TA feedback received:** [DATE], via [SOURCE]. The TA feedback on the PA2 submission concerned only missing files: `constitution.md` and the other Spec Kit files were not included, and only a GitHub link was provided. The TA gave no feedback on the content of the Vision Document, so every Vision revision below is a team-initiated change. The Spec Kit feedback is addressed in PA3 Part E (Spec Kit artifacts in the `spec-kit/` folder of the submission); it does not change the Vision.  
**Revision summary:** The team turned the PA2 narrative Vision into a detailed, traceable one for PA3. PA2 requirements are retained, not dropped. The revisions are: a revision table and updated references (VD-01); positioning retained without content change (VD-02); one stakeholder, user, and environment section tied to requirement IDs, and a competitor comparison with evidence (VD-03); numbered business rules with a retained, revised, or new status, plus updated dependencies (VD-04); individual functional requirements with acceptance criteria, using the identifiers of the supporting use-case documents (VD-05); and non-functional requirements that keep every PA2 identifier and add verification methods and use-case links (VD-06).

### 2.1. Section Coverage
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

| PA2 section | PA3 location | Change IDs or unchanged status |
|---|---|---|
| 1 Introduction (1.1 Purpose, 1.2 References) | B.1 Introduction and Revision Summary | VD-01 (Modified) |
| 2 Positioning (2.1 Problem Statement, 2.2 Product Position Statement) | B.2 Positioning | VD-02 (Retained; content unchanged, heading renumbered) |
| 3.1 Stakeholder Summary, 3.2 User Summary, 3.3 User Environment, 3.4 Key Needs | B.3 Stakeholders, Users, and Environments; B.6 where needs become requirements | VD-03 (Modified) |
| 3.5 Alternatives and Competition | B.4 Alternatives and Competition | VD-03 (Modified) |
| 4 Product Overview (4.1 Product Perspective, 4.2 Assumptions and Dependencies) | B.5 Product Overview, Business Rules, and Dependencies | VD-04 (Modified; new rules and dependencies added) |
| 5 Product Features (5.1–5.11), 5.12 Key Workflow Diagrams | B.6 Detailed Functional Requirements; the two PA2 workflow diagrams are retained unchanged in B.5 | VD-05 (Modified); AI Financial Advisor detail in VD-05 and VD-04 |
| 6 Non-Functional Requirements (6.1–6.7) | B.7 Detailed Non-Functional Requirements | VD-06 (Modified) |

### 2.2. VD-01 — Revision Summary and Updated References
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

| Field | Details |
|---|---|
| Change type | Modified |
| Source and reason | Team decision. PA2 stated that the Vision would be revised in PA3; the team added a revision table so that every change can be traced to this file. |
| PA2 location | Section 1 (Introduction): 1.1 Purpose and 1.2 References (R1–R6) |
| Before — PA2 | A purpose statement that ends by saying the initial version will be revised in PA3-2026 based on TA feedback, and six references (PA1 proposal, PA1 app survey, PA1 team contract, PA2 specification, project context, Part A) without a revision log. |
| After — PA3 | B.1 states the purpose and audience of the detailed Vision and lists its references: PA2 Part B, the PA1 proposal and survey, the project context file, the PA3 specification, and the PA3 use-case documents. It contains the revision table linking VD-01 to VD-06 to this file. It records that the TA feedback on PA2 concerned missing Spec Kit files only. |
| PA3 location | B.1 Introduction and Revision Summary |
| Affected requirements and use cases | Not applicable |
| Impact on scope or implementation | No impact; documentation only. |
| Supporting evidence | Jira SCRUM-104; PA3 `report.md` B.1; this file |
| Review outcome | Accepted on 2026-11-05 by Pham Dinh Tieu Long |

### 2.3. VD-02 — Positioning Retained
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

| Field | Details |
|---|---|
| Change type | Retained (reviewed; no content change) |
| Source and reason | Team decision. The PA3 use cases and prototypes were checked against the PA2 positioning, and no change to the problem or product position was found. |
| PA2 location | B.2.1 Problem Statement; B.2.2 Product Position Statement |
| Before — PA2 | FinSync addresses the split between personal-finance and group-splitting tools for students, young professionals, and small groups. It offers one app for personal and group finance with debt simplification and AI advice, without access to real bank accounts, and positions Money Lover and Splitwise as the PA1 alternatives. |
| After — PA3 | The same problem statement and product position, restated in the PA3 table format with the same meaning. |
| PA3 location | B.2 Positioning |
| Affected requirements and use cases | Not applicable |
| Impact on scope or implementation | None. |
| Supporting evidence | PA2 `report.md` B.2; PA3 `report.md` B.2 |
| Review outcome | Accepted on 2026-11-05 by Pham Dinh Tieu Long |

### 2.4. VD-03 — Stakeholders, User Environment, and Alternatives
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

| Field | Details |
|---|---|
| Change type | Modified |
| Source and reason | Team decision, so that users, environments, and competitor findings can be traced to requirements and use cases and are consistent with the PA3 specifications. |
| PA2 location | B.3.1–B.3.5 |
| Before — PA2 | Separate tables for five stakeholders and the user roles (Regular User, Group Owner, Group Member, Administrator); a user-environment list covering Android use, offline personal transactions, online-only shared operations, desktop administration, VND/USD, and Vietnamese/English; nine key needs; and the Money Lover and Splitwise comparison taken from PA1. |
| After — PA3 | B.3 presents stakeholders and users in one table with needs, environment or constraints, and priority. The environment text keeps every PA2 constraint (offline personal changes, online-only shared operations, receipts, currencies, languages). Each key need is traced to requirement IDs in B.6. B.4 keeps the PA1 survey result and adds, per capability, the surveyed behavior of each competitor, the planned FinSync response, and the evidence source; observed competitor behavior is kept separate from planned FinSync behavior. No new competitor finding is asserted. |
| PA3 location | B.3 Stakeholders, Users, and Environments; B.4 Alternatives and Competition |
| Affected requirements and use cases | UC-CLI-01 to UC-CLI-12; UC-ACC-01 to UC-ACC-02; UC-ADM-01 to UC-ADM-04; UC-AI-01 to UC-AI-06; the Data & Logic use cases (UC-DL) |
| Impact on scope or implementation | No change in scope; no PA2 requirement was dropped. Prototype images show simulated browser screens and do not establish Android runtime, backend, or persistence behavior. |
| Supporting evidence | PA2 `report.md` B.3; PA3 `report.md` B.3–B.4; `use-cases/` catalogues; `screenshots/prototypes/*-evidence` |
| Review outcome | Accepted on 2026-11-05 by Pham Dinh Tieu Long |

### 2.5. VD-04 — Numbered Business Rules, Added Rules, and Dependencies
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

| Field | Details |
|---|---|
| Change type | Modified (rule identifiers added; new rules and dependencies added) |
| Source and reason | Team decision, so that each rule can be referenced from requirements, use cases, and tests, and so that rules introduced by the PA3 use cases are recorded in the Vision. |
| PA2 location | B.4.1 (accounting and reconciliation rules, currency conversion rules, offline synchronization) and B.4.2 (Assumptions and Dependencies) |
| Before — PA2 | A table of eleven accounting rules (group permissions, personal and group ownership, expense recognition, linked records, shared-wallet contributions, shared-wallet visibility, splitting and rounding, settlement authorization, settlement accounting, refunds and corrections, server reconciliation) without identifiers; a currency conversion table; an offline synchronization paragraph; and assumptions and dependency tables that list PostgreSQL, Docker, the AI provider, an exchange-rate table, local encrypted storage, an email service, and GitHub, Jira, and Spec Kit. |
| After — PA3 | B.5 gives each rule an identifier, related requirements, rationale, and a status of Retained, Revised, or New. All eleven PA2 rules and the currency and offline rules are Retained. New rules: BR-AI-01 to BR-AI-09 for the AI Financial Advisor (3-month baseline and limited-history label, minimum data, aggregated data only, response validation, 30-second total limit including one retry, one stored result per month and at most five requests per day, 30% anomaly threshold, advice applied only after confirmation, result source label) and BR-AI-10 (previously synchronized advice stays readable offline). Rules introduced by the Account and Admin use cases are recorded as New and listed under Outstanding: immediate session revocation on account lock (BR-SEC-03), role gating for administrator endpoints (BR-SEC-02), default categories archived rather than deleted when in use (BR-FIN-01), and a warning when a base-currency setting changes (BR-FIN-03). Dependencies are updated with the Redis token blacklist (UC-ADM-03) and external image storage for avatars (UC-ACC-02). |
| PA3 location | B.5 Product Overview, Business Rules, and Dependencies; `use-cases/uc-ai-features.md` Section 1.2; `use-cases/admin-account-use-cases.md` |
| Affected requirements and use cases | UC-AI-01 to UC-AI-06; UC-ACC-01 to UC-ACC-02; UC-ADM-01 to UC-ADM-04; UC-DL cases that apply the settlement, split, refund, and currency rules |
| Impact on scope or implementation | The new dependencies must be provisioned in deployment (Docker Compose) if the use cases are implemented. No PA2 behavior is removed. |
| Supporting evidence | PA2 `report.md` B.4; `use-cases/uc-ai-features.md`; `use-cases/admin-account-use-cases.md`; Jira SCRUM-104 |
| Review outcome | Accepted on 2026-11-05 by Pham Dinh Tieu Long |

### 2.6. VD-05 — Functional Requirements with Acceptance Criteria
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

| Field | Details |
|---|---|
| Change type | Modified (requirement identifiers and acceptance criteria added; AI Financial Advisor detailed) |
| Source and reason | The PA3 assignment asks for detailed functional requirements, and Parts C and D must cover every functional requirement. The PA2 feature descriptions had no identifiers or acceptance criteria. |
| PA2 location | B.5 Product Features, 5.1–5.11, and 5.12 Key Workflow Diagrams |
| Before — PA2 | Eleven feature descriptions of one paragraph each (Authentication & Security; Profile Management; Wallet & Personal Account Management; Personal Transaction Tracking; Group Management & Shared Wallets; Smart Debt Split & Settlement; Budget Planning & Alerting; Financial Reporting & Analytics; Savings Goal Management; System Administration; AI Financial Advisor) and two workflow diagrams. |
| After — PA3 | B.6 expands each of the eleven groups into individual requirements with identifier, description, actor and trigger, inputs and validation, outputs and financial effects, priority and sprint, acceptance criteria, and related use cases. It reuses the identifiers already used by the supporting documents: FR-AUTH-01 to FR-AUTH-03, FR-PROF-01 to FR-PROF-02, FR-ADMIN-01 to FR-ADMIN-04, and FR-AI-01 to FR-AI-07 (on-demand analysis, monthly automatic analysis, anomaly alerts, budget suggestion applied after confirmation, savings-goal evaluation, history, basic analysis when the AI is unavailable). The remaining groups (wallets, personal transactions, groups and shared wallets, debt split and settlement, budgets, reports, savings goals) receive new stable identifiers of the form FR-<GROUP>-nn, cross-referenced to UC-CLI-01 to UC-CLI-12 and the UC-DL cases. The two PA2 workflow diagrams are retained in B.5. |
| PA3 location | B.6 Detailed Functional Requirements; Parts C and D; `use-cases/*.md` |
| Affected requirements and use cases | All eleven PA2 feature groups; UC-CLI, UC-ACC, UC-ADM, UC-AI, and UC-DL specifications |
| Impact on scope or implementation | No scope change; all PA2 groups remain in the Vision. Budget creation and savings-goal management need use-case coverage in Parts C and D (see Outstanding). PA3 implements one functional group only (Personal Finance Management, with Spec Kit); the other groups are specified and prototyped, not implemented. |
| Supporting evidence | PA2 `report.md` B.5; Client, Admin/Account, AI, and Data & Logic catalogues; prototype evidence folders |
| Review outcome | Accepted on 2026-11-05 by Pham Dinh Tieu Long |

### 2.7. VD-06 — Non-Functional Requirements with Verification and Traceability
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

| Field | Details |
|---|---|
| Change type | Modified |
| Source and reason | Team decision, to link each requirement to a verification method and to the use cases it affects, and to align the PA2 requirements with the PA3 specifications. |
| PA2 location | B.6.1–B.6.7 |
| Before — PA2 | NFR-P1 to P6, NFR-S1 to S9, NFR-R1 to R8, NFR-U1 to U6, NFR-C1 to C4, and NFR-M1 to M6, with a test-environment definition and a verification table per group. |
| After — PA3 | B.7 keeps every PA2 identifier and threshold and adds, per requirement, category, conditions, verification method, related use cases, and a "Change from PA2" column. Changes: NFR-S3 is retained with the PA2 values (60-minute access token, 7-day refresh session), and a shorter lifetime in the database v1 design is recorded as a known deviation, not as a new requirement. NFR-S7 adds the aggregated income total to the data allowed in AI requests (needed for budget suggestions). NFR-R4 is extended so that an AI-provider failure produces a labeled basic analysis instead of only an error (FR-AI-07). NFR-P4 is retained: the 30-second limit covers the whole request including one retry. NFR-S1 is refined with the BCrypt work factor used in UC-ACC-01. NFR-P7 is added for the administrator user directory (paginated queries under 500 ms for up to 100,000 records), replacing the identifier NFR-PERF-01 used in UC-ADM-02. Targets remain acceptance criteria, not measured results. |
| PA3 location | B.7 Detailed Non-Functional Requirements |
| Affected requirements and use cases | All NFR categories; UC-AI-01 to UC-AI-06; UC-ACC-01; UC-ADM-01 to UC-ADM-03 |
| Impact on scope or implementation | The AI fallback and the income total require matching backend behavior when the AI Advisor is built. Session lifetime in the implementation must be brought back to NFR-S3 or the difference must be approved. |
| Supporting evidence | PA2 `report.md` B.6; `use-cases/uc-ai-features.md`; `use-cases/admin-account-use-cases.md`; Data & Logic notes |
| Review outcome | Accepted on 2026-11-05 by Pham Dinh Tieu Long   |

### 2.8. Outstanding Vision Feedback
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

No outstanding TA feedback concerns the Vision Document. The Spec Kit feedback is tracked in PA3 Part E. The items below remain open until the owners confirm them; due dates are proposed targets within Sprint 3.

| Item | Decision or next action | Owner | Target | Status |
|---|---|---|---|---|
| TA feedback record | Enter the real date and source in the header of this section, and align the statement in Section 1, which currently says that no TA feedback had been received. | Ngo Thai Hoa; Pham Dinh Tieu Long | 2026-11-06 | Open |
| Budget creation and savings-goal management coverage | Confirm whether use cases for creating budgets and managing goals are added to Parts C and D (the current set covers viewing budget status and AI suggestions only), or whether these requirements are limited to what the use cases cover. | Ngo Thai Hoa; Pham Dinh Tieu Long; Nguyen Le Duc Nhat | 2026-11-07 | Open |
| New rules and dependencies from Account and Admin use cases | Confirm Redis token blacklist, avatar image storage, administrator role gating, default-category archiving, and the system base-currency setting as Vision content. | Pham Dinh Tieu Long | 2026-11-07 | Open |
| AI decisions recorded in VD-04 and VD-06 | Confirm the basic-analysis fallback, the 30-second total limit, offline reading of saved advice, and the income total in NFR-S7. | Nguyen Le Duc Nhat; Ngo Thai Hoa | 2026-11-07 | Open |
| Review of VD-01 to VD-06 | Record the actual review date and outcome for each entry. | Pham Dinh Tieu Long | 2026-11-08 | Open |

