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
**TA feedback received:** [TODO: Date and source, or state that feedback has not been received.]  
**Revision summary:** [TODO: Summarize the main requirements refinements and their purpose.]

### 2.1. Section Coverage
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

| PA2 section | PA3 location | Change IDs or unchanged status |
|---|---|---|
| B.1 Introduction | B.1 Introduction and Revision Summary | [TODO] |
| B.2 Positioning | B.2 Positioning | [TODO] |
| B.3.1–B.3.4 Stakeholders, Users, Environment, and Needs | B.3 Stakeholders, Users, and Environments; B.6 where needs become requirements | [TODO] |
| B.3.5 Alternatives and Competition | B.4 Alternatives and Competition | [TODO] |
| B.4 Product Overview | B.5 Product Overview, Business Rules, and Dependencies | [TODO] |
| B.5 Product Features, including workflows | B.6 Detailed Functional Requirements; relevant diagrams in Parts C–D | [TODO] |
| B.6 Non-Functional Requirements | B.7 Detailed Non-Functional Requirements | [TODO] |

### 2.2. VD-01 — [TODO: Specific Change Title]
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

| Field | Details |
|---|---|
| Change type | [TODO: Added / Modified / Removed / Corrected] |
| Source and reason | [TODO: Actual TA feedback with reference/date, or a team decision supported by research, clarification, or prototype work] |
| PA2 location | [TODO: Section ID/title and feature or NFR ID, if available] |
| Before — PA2 | [TODO: Original requirement, business rule, user/environment assumption, or competitor finding] |
| After — PA3 | [TODO: Specific revised requirement or finding; distinguish implementation targets from measured results] |
| PA3 location | [TODO: Section ID/title and link to the revised content] |
| Affected requirements and use cases | [TODO: FR/NFR IDs, use-case IDs, and prototype references where applicable; use “Not applicable” if none] |
| Impact on scope or implementation | [TODO: Effect on users, business rules, UI, API/data, verification, or schedule; state if there is no impact] |
| Supporting evidence | [TODO: Source, clarification record, prototype, or commit supporting the revision] |
| Review outcome | [TODO: Pending / Reviewed with corrections / Accepted; include actual review date and remaining actions] |

### 2.3. Outstanding Vision Feedback
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

[TODO: List unresolved requirements or feedback, responsible member, and next action. Write “None” only after confirming that no items remain.]

---

## 3. Final Revision Check
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

- [x] The baseline identifies the version actually submitted for PA2 (`2f4fcf6`).
- [x] All Project Plan and Vision changes have unique IDs and concrete before/after descriptions.
- [x] TA feedback is distinguished from team-initiated updates; unresolved items are disclosed.
- [x] Coverage tables identify changed and unchanged sections, including moved or removed content.
- [x] Every recorded change appears in the revised PA3 document, and each revision-summary reference points to the correct change ID.
- [ ] Requirements, use-case diagrams, specifications, and prototypes are consistent with the revised Vision.
- [x] Authors, reviewers, editors, and review outcomes reflect actual work.
- [ ] All TODOs and template instructions have been replaced or removed.
- [ ] Changes.md and its converted PDF are included in PA3-Group04.zip.
