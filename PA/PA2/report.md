# PA2-2026 Report — FinSync (Group 04)
> *Performed by: Nguyen Le Duc Nhat | Reviewed by: Pham Dinh Tieu Long | Edited by: Nguyen Le Duc Nhat*

**Course:** CS300 - CSC13002 - Introduction to Software Engineering   
**Assignment:** Project Assignment 2 (PA2-2026) — Sprint 2  
**Group:** Group 04  
**Project:** FinSync — Personal Finance & Group Expense Manager with AI Advisor  
**Sprint 2 period:** `2026-10-10 to 2026-10-25`  
**Reporting date:** 2026-10-18  
**Time zone:** UTC+07:00 (Asia/Ho_Chi_Minh)   


Attribution lines identify the members responsible for writing, review, and editing. Actual review completion is supported by the task evidence. Future meetings and builds remain scheduled work; observed outcomes are recorded only when available.

## Table of Contents
> *Performed by: Nguyen Le Duc Nhat | Reviewed by: Pham Dinh Tieu Long | Edited by: Nguyen Le Duc Nhat*

- [Team Members](#team-members)
- [Part A — Project Plan](#part-a--project-plan)
- [Part B — Vision Document](#part-b--vision-document)
- [Part C — Spec Kit Initialization](#part-c--spec-kit-initialization)
- [Part D — AI Usage Report and Weekly Report](#part-d--ai-usage-report-and-weekly-report)
- [Appendix — Submission Checklist](#appendix--submission-checklist)
- [Appendix — AI Usage Notes](#4-ai-usage-notes)
- [Appendix — Evidence Register](#1-evidence-register)

---

## Team Members
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

| # | Student ID | Full Name | Email | Main Role |
|---|------------|-----------|-------|-----------|
| 1 | 24120087 | Phạm Định Tiểu Long | phamlongkh2006@gmail.com | Project Manager / Group Leader |
| 2 | 24120038 | Nguyễn Phú Đạt | nguyennphuudatt@gmail.com | UI/UX Designer & Frontend Lead |
| 3 | 24120403 | Nguyễn Lê Đức Nhật | nldnhat182006@gmail.com | Backend Lead |
| 4 | 24120342 | Vương Đắc Gia Khiêm | vuongkhiemvl10@gmail.com | QA Lead & DevOps |
| 5 | 24120051 | Ngô Thái Hòa | ngothaihoa235@gmail.com | AI Feature Lead & Documentation |

---

# Part A — Project Plan
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

## 1. Introduction
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

FinSync is a mobile application that combines personal finance management (similar to *Money Lover*) with smart group expense sharing (similar to *Splitwise*) in a single product. It is designed for students, young professionals, and small groups who share costs such as rent, trips, and events.

The system works as a **bookkeeping, debt calculation, and reminder tool only**. It does not connect to real bank accounts and never moves money automatically. An AI Financial Advisor analyzes spending history and suggests a reasonable spending plan for the next month.

This document is the initial version of the Project Plan. It will be updated in PA3-2026 based on the TA feedback.

## 2. Project Overview
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

### 2.1 Goals
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

- Build a working mobile app that unifies personal and group finance tracking.
- Automate expense splitting and debt optimization to reduce the number of transfers between group members.
- Provide an AI Financial Advisor that turns raw spending data into clear monthly guidance.
- Provide a simple web admin panel for basic system administration.
- Apply Scrum and good software engineering practice throughout the semester (Jira, Git, code review, testing).

### 2.2 Scope
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

**In scope**

| # | Functional Group | Short Description |
|---|------------------|-------------------|
| 1 | Authentication & Security | Register, login, logout, password recovery with JWT |
| 2 | Profile Management | Edit profile/password/avatar, Vietnamese/English interface, reporting currency (VND/USD) |
| 3 | Wallet & Personal Account Management | Manual cash/bank/card wallets, original balances, converted net-worth overview |
| 4 | Personal Transaction Tracking | Online/offline income and expense entry, receipt capture, queued sync and conflict resolution |
| 5 | Group Management & Shared Wallets | Create groups, invite by link/code, Owner/Member roles, shared wallet |
| 6 | Smart Debt Split & Settlement | Exact splits, debt simplification, refunds, and external payments confirmed by the recipient |
| 7 | Budget Planning & Alerting | Weekly/monthly category budgets, alerts at 80% and 100% |
| 8 | Financial Reporting & Analytics | Pie/bar charts, group trip summary, PDF/Excel export |
| 9 | Savings Goal Management | Long-term goals and progress tracking |
| 10 | System Administration (Admin Panel) | User lock/unlock, default categories, reference exchange rates, and aggregate statistics |
| AI | AI Financial Advisor | Monthly analysis, anomaly detection, budget suggestions |

**Out of scope**

- Direct connection to real bank accounts or payment gateways.
- Automatic money deduction or real money transfer.
- iOS client.
- Executing currency exchange, automatic bank synchronization, and automatic financial transfers.
- Offline changes to group ledgers, settlement confirmations, or administrator data; offline group access is read-only.

### 2.3 Deliverables
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

| Deliverable | Sprint / PA | Format |
|-------------|-------------|--------|
| Project Proposal, app survey | Sprint 1 / PA1 | Markdown + PDF |
| Project Plan (initial), Vision Document (initial), Spec Kit init | Sprint 2 / PA2 | Markdown + PDF, repo artifacts |
| Revised Project Plan and Vision Document after TA feedback | Sprint 3 / PA3 | Markdown + PDF |
| Use cases, architecture and design documents | Sprint 3–4 | Markdown + PDF |
| Test plan and test cases | Sprint 4 | Markdown + PDF |
| Source code: mobile app, backend API, AI microservice, admin web | Sprint 2–5 | GitHub repository (private) |
| Test builds | See [Build Plan](#44-build-plan) | APK / deployed environment |
| Final demo of all features | Sprint 5 / PA5 | Live demo |

### 2.4 Assumptions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

- Every member works as a full-stack engineer; roles below only define the main person in charge (Lead).
- Each of the five members allocates at least **10 hours per week**, equivalent to approximately **114 team hours** over the 16-calendar-day PA2 window. Work is distributed between individual outputs, review, and meetings.
- Local Docker environments are the baseline for development and rehearsal. Remote hosting is optional for the demo; external AI calls are limited, cached where appropriate, and controlled by configuration so development can continue with synthetic responses during provider outages.
- Users enter all transactions manually; actual payments happen outside the app.
- Dates for PA3–PA5 are internal planning targets in this initial plan; official course deadlines take precedence. Their detailed scope will be refined at the start of each sprint.

## 3. Project Organization
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*

### 3.1 Team Structure and Roles
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*

| Member | Role | Main Responsibilities |
|--------|------|-----------------------|
| Phạm Định Tiểu Long | Project Manager / Group Leader | Track progress, coordinate work, lead Sprint meetings, manage Jira board |
| Nguyễn Phú Đạt | UI/UX Designer & Frontend Lead | Wireframes and mockups, UI consistency, lead mobile client development |
| Nguyễn Lê Đức Nhật | Backend Lead | Backend architecture, Spring Boot REST API, database design |
| Vương Đắc Gia Khiêm | QA Lead & DevOps | Testing strategy, test cases, CI/CD pipeline, deployment |
| Ngô Thái Hòa | AI Feature Lead & Documentation | AI Advisor (Gemini/OpenAI integration), technical documentation |

**Working rules**

- Each Jira task is assigned to exactly **one** member and reviewed by **another** member.
- Tasks are created **before** work starts (never created, assigned, and closed at the same time).
- Commit messages follow Conventional Commits: `<type>(<scope>): <subject>`.
- Secrets (API keys, passwords, JWT secret, `.env`) are never committed. The repository is private.

### 3.2 Risk Management
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*

| ID | Risk | Probability | Impact | Mitigation Strategy | Owner |
|----|------|-------------|--------|---------------------|-------|
| R1 | Member unavailability or uneven workload | Medium | High | Limit most work packages to three days, reserve review time, use a different reviewer as backup, and redistribute incomplete work during Scrum. | Long |
| R2 | Scope creep across eleven feature groups | High | High | Deliver a basic usable flow for every planned feature by Build 3; prioritize correctness and integration, and defer cosmetic depth through a documented scope decision. | Long |
| R3 | Learning Kotlin/Compose, Spec Kit, and deployment tools | Medium | Medium | Schedule individual training and small runnable skeletons in Sprint 2; retain the PA1 stack and share setup instructions. | Đạt |
| R4 | AI quota, latency, cost, or invalid suggestions | Medium | Medium | Use bounded requests, aggregate input, schema validation, a 30-second timeout, cached advice, and synthetic development responses. Keep finance functions independent of the AI provider. | Hòa |
| R5 | Mobile/backend/AI/admin integration failures | Medium | High | Define API contracts and shared currency/accounting rules early, smoke-test each build, and integrate before the final review. | Nhật |
| R6 | Exposed credentials or unauthorized financial access | Low | High | Externalize secrets, review tracked files and authorization tests, limit AI payloads, and rotate any exposed credential. | Khiêm |
| R7 | Incorrect shares, refunds, or settlements | Medium | High | Use exact money arithmetic, deterministic remainder allocation, immutable adjustments, atomic updates, and recipient-confirmation/retry tests. | Nhật |
| R8 | Lost or duplicated offline changes | Medium | High | Persist a local queue, identify operations uniquely, retain conflicting versions, test restart/retry behavior, and keep shared writes online. | Đạt |
| R9 | Stale or inconsistent exchange rates | Medium | Medium | Store rate/source/time with converted entries, label cached rates, reject missing-rate conversions, and test that rate updates never rewrite historical debt. | Nhật |

## 4. Project Plan
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Vuong Dac Gia Khiem` | Edited by: `Pham Dinh Tieu Long`*

### 4.1 Process
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Vuong Dac Gia Khiem` | Edited by: `Pham Dinh Tieu Long`*

The project follows the **Scrum** process. The semester is divided into **5 sprints**, each matching one Project Assignment (Sprint 1 = PA1, Sprint 2 = PA2, and so on). Each sprint lasts 2–3 weeks and has four meetings:

| Meeting | When | Purpose |
|---------|------|---------|
| Sprint Planning (1) | At the transition before sprint implementation; Sprint 2 kickoff slot: 10 October, 09:00–10:00 | Prioritize work, allocate capacity, and assign tasks |
| Weekly Scrum (2) | During sprint | Each member answers: done since last week / plan until next week / obstacles |
| Sprint Review & Retrospective (1) | End of sprint | Review results; what went well, what went wrong, root causes, improvements, lessons learned |

Tools follow the PA1 team contract: Jira (task tracking), GitHub (code and review), Discord / Zalo (daily communication), and Google Meet / Zoom (meetings).

The Jira workflow is **To Do → In Progress → Done**, as recorded in PA1. Work remains In Progress until another member reviews it. A task is Done when its output is linked, its acceptance criteria are checked, its review is recorded, and its actual completion date is entered. Documentation and self-training tasks follow the same rules as coding tasks.

### 4.2 Sprint Overview
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa & Nguyen Phu Dat` | Edited by: `Pham Dinh Tieu Long`*

| Sprint | PA | Planning period | Main focus |
|--------|----|-----------------|------------|
| Sprint 1 | PA1 | Before 2026-10-10; PA1 artifacts available in repository by 2026-10-05 | Proposal, app survey, team contract, and initial repository/backend setup |
| Sprint 2 | PA2 | **2026-10-10 to 2026-10-25** | Initial plan/vision, Spec Kit/training, skeletons, and Build 0 |
| Sprint 3 | PA3 | 2026-10-26 to 2026-11-08 | Revised documents; Auth, Profile, Wallet, offline Personal Transactions; AI/admin prototypes; Build 1 |
| Sprint 4 | PA4 | 2026-11-09 to 2026-11-22 | Groups, shared fund, split/settlement/refunds, currency conversion, budgets, savings, reports; Build 2 |
| Sprint 5 | PA5 | 2026-11-23 to 2026-12-06 | Complete AI/admin/export, full regression and offline rehearsal, release candidate, final build, and demo |

Sprint 3–5 dates are team planning targets aligned to two-week cycles, rather than confirmed course submission deadlines. Sprint Planning refines each later sprint's task-level estimates and assignments.

### 4.3 Sprint 2 — Detailed Task List
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa & Nguyen Phu Dat` | Edited by: `Pham Dinh Tieu Long`*

The dated task list is the Sprint 2 baseline. Every task has one assignee and a different reviewer; due dates include the owner/reviewer handoff. Local IDs identify report work packages, while actual Jira keys, dates, and status history are evidenced by the board and task-history screenshots in Part D. No actual completion is inferred from a planned due date.

Long, Đạt, Nhật, Khiêm, and Hòa refer to the members in the team table. Meetings involve all five members; the assigned owner facilitates and records the minutes. Estimates below cover the owner's task execution; peer review and other members' meeting attendance are budgeted separately.

| Local ID | Task | Assignee | Reviewer | Planned start | Due date | Owner effort (h) |
|----------|------|----------|----------|---------------|----------|------------------|
| S2-01 | Sprint 2 Planning and meeting notes | Long | Hòa | 2026-10-10 | 2026-10-10 | 1.5 |
| S2-02 | Project Plan — Introduction and Overview | Nhật | Long | 2026-10-13 | 2026-10-14 | 2 |
| S2-03 | Project Plan — Organization and Risk Management | Long | Khiêm | 2026-10-13 | 2026-10-14 | 2 |
| S2-04 | Project Plan — Schedule and Build Plan | Long | Khiêm | 2026-10-15 | 2026-10-16 | 2 |
| S2-05 | Vision — Introduction and Positioning | Hòa | Long | 2026-10-13 | 2026-10-14 | 2 |
| S2-06 | Vision — Users, Stakeholders, Alternatives; review PA1 survey | Đạt | Hòa | 2026-10-13 | 2026-10-15 | 3 |
| S2-07 | Vision — Product Overview and accounting boundaries | Nhật | Khiêm | 2026-10-15 | 2026-10-16 | 3 |
| S2-08 | Vision — Product Features, 3–5 sentences per feature | Hòa | Nhật | 2026-10-15 | 2026-10-17 | 4 |
| S2-09 | Two Mermaid user workflows | Đạt | Khiêm | 2026-10-18 | 2026-10-19 | 3 |
| S2-10 | Vision — NFR targets and verification methods | Khiêm | Nhật | 2026-10-18 | 2026-10-19 | 3 |
| S2-11 | Review existing GitHub setup, folders, ignore rules, and branch rules | Khiêm | Long | 2026-10-10 | 2026-10-11 | 1.5 |
| S2-12 | Initialize Spec Kit in `/src`; complete constitution and artifact inventory | Khiêm | Nhật | 2026-10-13 | 2026-10-15 | 3 |
| S2-13 | Spec Kit training and individual evidence — Long | Long | Hòa | 2026-10-10 | 2026-10-12 | 3 |
| S2-14 | Spec Kit training and individual evidence — Đạt | Đạt | Long | 2026-10-10 | 2026-10-12 | 3 |
| S2-15 | Spec Kit training and individual evidence — Nhật | Nhật | Đạt | 2026-10-10 | 2026-10-12 | 3 |
| S2-16 | Spec Kit training and individual evidence — Khiêm | Khiêm | Nhật | 2026-10-10 | 2026-10-12 | 3 |
| S2-17 | Spec Kit training and individual evidence — Hòa | Hòa | Khiêm | 2026-10-10 | 2026-10-12 | 3 |
| S2-18 | Kotlin / Compose self-training and runnable mobile skeleton | Đạt | Nhật | 2026-10-16 | 2026-10-18 | 4 |
| S2-19 | Review/complete backend skeleton, database, migrations, and API documentation | Nhật | Khiêm | 2026-10-17 | 2026-10-19 | 6 |
| S2-20 | AI service self-training, skeleton, connectivity and failure smoke test | Hòa | Nhật | 2026-10-18 | 2026-10-20 | 4 |
| S2-21 | Weekly Scrum #1 and notes | Long | Hòa | 2026-10-16 | 2026-10-16 | 1 |
| S2-22 | Weekly Scrum #2 and notes | Long | Hòa | 2026-10-22 | 2026-10-22 | 1 |
| S2-23 | Sprint Review & Retrospective and notes | Long | Khiêm | 2026-10-24 | 2026-10-24 | 1.5 |
| S2-24 | Consolidate AI usage records and supporting screenshots | Hòa | Long | 2026-10-21 | 2026-10-23 | 2 |
| S2-25 | Jira/Git evidence, Markdown/PDF visual check, final submission package | Khiêm | Long | 2026-10-24 | 2026-10-25 | 3 |
| S2-26 | Produce Build 0 and record environment smoke-test results | Khiêm | Đạt | 2026-10-21 | 2026-10-22 | 2 |
| S2-27 | Cross-check report against PA2, resolve reviews, and reconcile Jira | Đạt | Hòa | 2026-10-23 | 2026-10-24 | 2 |

**Capacity allocation**

| Member | Owned tasks (h) | Peer review (h) | Additional meeting attendance (h) | Planned total (h) |
|--------|-----------------|-----------------|----------------------------------|-------------------|
| Long | 12 | 3 | 0 | 15 |
| Đạt | 15 | 2.5 | 3.5 | 21 |
| Nhật | 14 | 3 | 3.5 | 20.5 |
| Khiêm | 15.5 | 2.5 | 3.5 | 21.5 |
| Hòa | 15 | 2.5 | 3.5 | 21 |

The plan allocates **99 team hours** from approximately **114 available hours**, leaving about **15 hours** for corrections, environment problems, and coordination. Long's meeting tasks already include his attendance and minutes preparation; the other four members each reserve 3.5 hours for attendance.

**Dependencies and checkpoints:** repository checks finish by 11 October and individual training by 12 October, before Spec Kit setup on 13–15 October. Feature/accounting descriptions precede the 18–19 October workflows and NFRs. Mobile, backend, and AI skeletons are due by 20 October, before Build 0 on 21–22 October. The 23–24 October review window absorbs corrections, and packaging finishes on 25 October.

**Definition of Done:** the output exists, meets its acceptance criteria, has a different member's recorded review, and has accurate actual dates/status in Jira. A training task includes individual learning evidence; a meeting task includes attendance, updates, decisions, and actions; a build task includes reproducible startup steps and smoke-test results. Relevant Jira screenshots show task keys, assignees, review history, and created/assigned/due/completed dates. Until that evidence is supplied, task completion remains unverified.

### 4.4 Build Plan
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Vuong Dac Gia Khiem | Edited by: Pham Dinh Tieu Long*

The final sprint (PA5) includes a demo of all features, so builds are planned to deliver features gradually.

| Build | Target date | Planned content | Acceptance focus |
|-------|-------------|-----------------|------------------|
| Build 0 — skeleton | 2026-10-22 | Runnable mobile/backend/database/AI skeletons and startup instructions | Environment reproduction, service connectivity, failure handling, and recorded smoke tests |
| Build 1 | 2026-11-08 | Auth, Profile, wallets, offline personal transactions and synchronization; AI/admin prototypes | Access control, restart/retry safety, conflict handling, and core transaction flows |
| Build 2 | 2026-11-22 | Groups/shared fund, splits/refunds, confirmed settlements, VND/USD conversion, budgets, savings, reports | Exact financial totals, historical rates, group permissions, and connected/offline boundaries |
| Build 3 — release candidate | 2026-11-29 | All ten functional groups and AI Advisor, complete admin/rate tools, PDF/Excel export | End-to-end feature coverage, security/performance tests, and 24-hour offline rehearsal |
| Final build | 2026-12-04 | Reviewed fixes to the release candidate | Regression closure, complete runbook, final data/evidence, and demo rehearsal |

**Five testable builds** are planned. Each build is associated with a source revision/tag, installation/startup steps, test outcomes, and known issues. Build 3 delivers all planned feature groups before final regression; the final sprint's internal demo target is **6 December 2026**, with 5–6 December reserved for rehearsal and presentation. Dates are adjusted if official PA3–PA5 deadlines require it.

### 4.5 Plan for Later Sprints (planned tasks only)
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat & Vuong Dac Gia Khiem` | Edited by: `Pham Dinh Tieu Long`*

**Sprint 3 (PA3)**
- Revise Project Plan and Vision Document based on TA feedback.
- Write use cases and requirements specification (`/speckit.specify`).
- Design database schema, API contracts, and UI mockups.
- Implement Authentication, Profile, Wallet, and Personal Transactions, including encrypted offline storage, queued changes, synchronization, and conflict handling.
- Prototype the AI analysis contract with synthetic data and the admin login/user-list flow.
- Write test plan; produce Build 1.

**Sprint 4 (PA4)**
- Implement online Group Management, confirmed shared-wallet contributions, exact splitting/refunds, recipient-confirmed settlement, and auditable adjustments.
- Implement VND/USD conversion with stored rate snapshots, Budget Planning & Alerting, Savings Goals, Reports, and charts.
- Integrate the first AI analysis and admin management flows; exercise timeout and permission handling.
- Write test cases; set up CI pipeline; produce Build 2.

**Sprint 5 (PA5)**
- Implement AI Financial Advisor and Admin Panel; export PDF/Excel.
- Run integration, financial-invariant, performance/security, and 24-hour offline tests; fix defects and verify backup/restore.
- Prepare final report and demo; produce Build 3 and the final build.

---

# Part B — Vision Document
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Nguyen Le Duc Nhat`*

## 1. Introduction
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Nguyen Le Duc Nhat`*

### 1.1 Purpose
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Nguyen Le Duc Nhat`*

This document describes the vision for **FinSync**: the problem it solves, the people who will use it, its main features, and the quality requirements it must meet. It provides a common reference for the team, lecturer, and TAs when prioritizing work and evaluating the final product. The PA2 vision includes offline personal transaction management, VND/USD conversion, and explicit rules for shared expenses and settlement. This initial version will be revised in PA3-2026 based on TA feedback.

### 1.2 References
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Nguyen Le Duc Nhat`*

| Reference | Document and use |
|-----------|------------------|
| R1 | [PA1 Project Proposal — Part B](../PA1/report.md#b---project-proposal): original product scope, target users, and feature baseline |
| R2 | [PA1 Existing App Survey — Part C](../PA1/report.md#c---existing-app-survey): unchanged Money Lover and Splitwise findings and screenshots |
| R3 | [PA1 Team Contract — Part D](../PA1/report.md#d---team-contract): team roles and working agreements |
| R4 | [PA2 Assignment Specification](pa2_project_assignment_specification.md): required Project Plan, Vision Document, and submission format |
| R5 | [FinSync Project Context](../../GEMINI.md): system boundaries, technologies, and business domains |
| R6 | Part A of this report: sprint schedule, responsibilities, dependencies, and build plan |


## 2. Positioning
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

### 2.1 Problem Statement
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

| | |
|---|---|
| **The problem of** | In the team's PA1 survey, personal tracking and group splitting were handled by separate tools. Users must combine the information by hand, and often forget small debts. |
| **Affects** | Students, young professionals, and small groups (roommates, travel friends, project teams) who spend money together and also want to control their own spending. |
| **The impact of which is** | Time wasted on manual calculation, unclear "who owes whom", conflicts between friends, an incomplete view of personal spending, and overspending without early warning. |
| **A successful solution would be** | One app that combines personal wallets and budgets with group expense splitting and debt optimization, and gives clear AI-based advice on how to spend in the next month. |

### 2.2 Product Position Statement
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

| | |
|---|---|
| **For** | Students and young professionals who manage personal money and share costs with others |
| **Who** | Need to track personal spending, split shared bills fairly, and plan their monthly budget |
| **The product name** | FinSync |
| **That** | Combines personal finance tracking, smart group expense splitting with debt optimization, and an AI Financial Advisor in one mobile app |
| **Unlike** | Using separate personal-finance and group-splitting tools, represented by Money Lover and Splitwise in the PA1 survey |
| **Our product** | Unifies personal and group finance in one place, reduces the number of transfers needed to settle debts, and turns spending history into clear monthly advice — without touching the user's real bank accounts |

## 3. Stakeholder and User Descriptions
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Ngo Thai Hoa`*


### 3.1 Stakeholder Summary
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Ngo Thai Hoa`*


| Stakeholder | Description | Main Interest |
|-------------|-------------|---------------|
| Regular Users | Students and young professionals using the app | Easy tracking, fair splitting, useful advice |
| System Administrator | Person managing users and default data | Simple tools to manage accounts and categories |
| Development Team (Group 04) | Five students building the system | Deliver a working product on time with good quality |
| Lecturer and TAs | Course staff who grade and give feedback | Correct process, documents, and working software |
| Third-party services (Gemini/OpenAI, cloud hosting) | Providers of AI and hosting | Fair use within limits and terms |

### 3.2 User Summary
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Ngo Thai Hoa`*


| User | Description | Responsibilities in the System |
|------|-------------|--------------------------------|
| **Regular User** | Individual who tracks money and joins expense groups | Manage wallets, transactions, budgets, goals; create or join groups; confirm payments |
| ↳ Group Owner | Regular User who creates and manages a group | Invite members, manage group settings and group wallet |
| ↳ Group Member | Regular User who joins a group | Add shared expenses, view debts, mark payments |
| **Administrator** | Manages the system through a simple web page | View users, lock/unlock accounts, manage default categories, view statistics |

### 3.3 User Environment
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Ngo Thai Hoa`*


- **Regular Users** use an Android smartphone, as selected in PA1, on mobile data or Wi-Fi, often in short sessions for recording an expense. iOS is outside the current baseline unless a scope change is approved.
- **Offline support is included.** After an initial online sign-in and synchronization, users can view cached wallets, transactions, budgets, goals, and reports, and create, edit, or delete personal transactions without a connection. The app stores these changes locally, shows their pending status, and synchronizes them when connectivity and a valid session are available.
- **Shared operations require a connection.** Group creation/invitations, shared expense changes, settlement confirmation, new AI analysis, and administration use the latest server state. Cached group balances and previous AI advice remain readable offline with their last-sync time; the app explains when an action requires a connection.
- Receipt images captured offline remain on the device and upload after reconnection. Updates to the same personal record on two devices are shown for resolution rather than silently overwriting either version.
- **Administrators** use a desktop web browser (Chrome, Edge, Firefox).
- **Currencies and language:** VND and USD are supported with bidirectional conversion; Vietnamese and English are available in the user interface. A user's selected reporting currency determines converted summaries, while every record retains its original currency and amount.

### 3.4 Summary of Key Stakeholder or User Needs
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Ngo Thai Hoa`*


| Need | Priority | Current Solution | Proposed Solution |
|------|----------|------------------|-------------------|
| Record personal income/expense quickly, including offline | High | Notes app, spreadsheets, Money Lover | Local transaction entry with categories, receipt capture, visible sync status, and synchronization after reconnection |
| See total money across cash, bank, and card in one reporting currency | High | Manual calculation | Wallet dashboard with VND/USD conversion and visible exchange-rate details |
| Split shared bills fairly | High | Mental calculation, chat messages, Splitwise | Equal, percentage, and exact splits |
| Know who owes whom and settle with few transfers | High | Manual reminders | Debt summary, simplified repayment suggestions, reminders, and settlement confirmed by the recipient |
| Avoid overspending | Medium | Self-control | Category budgets with alerts at 80% and 100% |
| Save for long-term goals | Medium | Notes, memory | Savings goals with progress tracking |
| Understand spending and plan next month | Medium | Manual analysis | AI Financial Advisor with monthly suggestions |
| Keep financial data private and safe | High | Depends on the app | Authentication, JWT, private personal data |
| Manage users and default categories | Medium | None | Web admin panel |

### 3.5 Alternatives and Competition
> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Ngo Thai Hoa`*


The team retains the Money Lover and Splitwise survey from [PA1 Part C](../PA1/report.md#c---existing-app-survey) without changes to its observations, comparison results, or supporting screenshots. The table below reproduces the feature comparison in PA1 Part C.3. Competitor entries describe the PA1 survey baseline; FinSync entries describe the planned product.

| Feature | Money Lover | Splitwise | FinSync (planned) |
|---------|:-----------:|:---------:|:-----------------:|
| Personal wallet management | ✅ | ❌ | ✅ |
| Personal transaction tracking | ✅ | ❌ | ✅ |
| Budget planning & alerts | ✅ | ❌ | ✅ |
| Financial reports & charts | ✅ | Limited | ✅ |
| Savings goals | ✅ | ❌ | ✅ |
| Group expense management | ❌ | ✅ | ✅ |
| Smart debt splitting | ❌ | ✅ | ✅ |
| Debt simplification algorithm | ❌ | ✅ | ✅ |
| Payment reminders | ❌ | ✅ | ✅ |
| AI financial advisor | ❌ | ❌ | ✅ |
| Admin panel | ❌ | ❌ | ✅ |
| Unified personal + group finance | ❌ | ❌ | ✅ |

Money Lover provides the personal dashboard, category selection, wallet aggregation, and reporting patterns that inform FinSync's personal-finance experience. Splitwise provides the group balance, flexible split, and external-settlement patterns that inform shared-expense workflows. FinSync's planned differentiation remains the combination of personal and group finance with monthly AI guidance and a Vietnamese-friendly interface. Offline operation and currency conversion are specified as FinSync requirements here; no additional competitor findings are asserted.

## 4. Product Overview
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*


### 4.1 Product Perspective
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*

FinSync is a new, self-contained product made of four parts that communicate through REST APIs:

```mermaid
flowchart LR
    U[Regular User] --> M[Mobile App<br/>Android]
    A[Administrator] --> W[Web Admin Panel<br/>React.js]
    M <-->|Offline access and pending changes| L[(Encrypted local data)]
    M -->|HTTPS + JWT and synchronization| B[Backend API<br/>Spring Boot, Java 21]
    W -->|HTTPS + JWT| B
    B --> D[(PostgreSQL)]
    B -->|REST| AI[AI Microservice<br/>Python FastAPI]
    AI -->|API| G[Gemini / OpenAI]
```

The product is a **bookkeeping, calculation, and reminder** system. Payments happen outside the app (bank transfer, cash), and users confirm them in the app. The product never accesses real bank accounts.

The system separates two business areas:

- **Personal Expense Management:** private data, visible only to the account owner.
- **Group Expense Management:** shared data, visible to members of the same group.

**Accounting and reconciliation rules**

| Rule | Required behavior |
|------|-------------------|
| Group permissions | The Owner manages membership and settings; members may add expenses and payment claims. An expense recorder or the Owner may correct an expense, with the original values retained in history. Changes affecting previously settled amounts use linked adjustments and recalculated balances. |
| Personal and group ownership | Only the owner can access personal records. Group records are shared only with current authorized members; administrative statistics do not expose individual private transaction histories. |
| Expense recognition | Personal spending reports, category budgets, and AI analysis include only the user's own share of a group expense. The full amount advanced by a member is a cash movement; the amount recoverable from others is tracked as a receivable rather than additional personal consumption. |
| Linked records | A personal wallet movement, group expense, and allocated share may reference the same event. Reports identify that event and count consumption once, including when a user links an existing personal entry to a group expense. |
| Shared-wallet contributions | A member records a contribution claim and the Owner, acting as fund custodian, confirms receipt before it increases the shared fund and contributor's fund balance; it is a transfer, not an expense. An expense paid from the fund reduces available cash and allocates consumption to participants. Differences between each member's contributions and allocated costs determine amounts owed to or recoverable from the fund. A direct payment by a member is recorded against that member instead, so both funding methods cannot fund the same expense. |
| Shared-wallet visibility | The group fund balance is shown separately from personal wallets. Its entire balance is never included as one member's personal asset; member contribution and reimbursement positions are shown separately. All balances represent bookkeeping entries. |
| Splitting and rounding | Equal, percentage, and exact-amount splits operate in the group's currency. Percentages must total 100%; exact shares must equal the expense. Any rounding remainder is assigned in a stable member order so shares always sum to the recorded total. |
| Settlement authorization | The debtor records a positive payment claim after paying outside the app; the intended recipient confirms receipt before the debt is reduced. A rejected or pending claim leaves the confirmed debt unchanged. Partial settlement is allowed up to the outstanding amount; confirming the same claim twice has no additional effect. An Owner cannot confirm on another recipient's behalf. |
| Settlement accounting | Confirmed settlement changes debt and any linked cash movement, not consumption or income. Linking a previously recorded payment prevents a second cash entry. A return of unused shared funds follows the same sender/recipient confirmation rule and is a transfer, not an expense. |
| Refunds and corrections | Refunds reference the original expense, cannot exceed its unrefunded amount, and reverse the original participants' shares proportionally with exact rounding. Previously confirmed payments remain in history; resulting overpayments create reimbursement balances. Changes affecting balances are recorded as linked adjustments rather than silently deleting settled history. |
| Server reconciliation | Financial updates are applied atomically, preserve an audit trail, and reject stale conflicting versions. Retrying an operation with the same identifier cannot duplicate a transaction, share, refund, or settlement. Online group debt and fund calculations use the same ledger and currency rules. |

**Currency conversion rules**

FinSync supports conversion in both directions between VND and USD. Users select VND or USD as their reporting currency; each wallet and group has its own currency, which cannot be changed after financial entries exist.

| Item | Conversion rule |
|------|-----------------|
| Exchange-rate source | Administrators maintain a shared reference-rate table for the course deployment, recording a positive VND-per-USD rate, the quoted source, effective date/time, and update author. Rate entry is manual; a live exchange-rate API is not required for this version. |
| Conversion direction | USD amounts are multiplied by the recorded VND-per-USD rate to obtain VND; VND amounts are divided by that rate to obtain USD. The original amount is retained. |
| Precision | Monetary values use exact decimal arithmetic. VND is rounded to whole dong and USD to two decimal places using half-up rounding; conversion is performed before group shares are allocated. |
| Transaction history | Every converted financial entry retains the applied rate, source, and effective timestamp. Later rate updates do not change previously recorded expense shares or confirmed settlements. |
| Cross-currency group entries | An expense entered in another currency is converted once into the group's currency using the recorded rate snapshot. Debts and settlement limits are calculated in the group's currency; the original amount remains visible for checking. |
| Summaries and reports | Personal net-worth summaries show original wallet balances and converted equivalents using an identified valuation rate. Historical spending reports use transaction-rate snapshots and state their reporting currency. Changing the reporting preference recalculates the presentation without rewriting the ledger. |
| Offline and unavailable rates | Offline screens may use the last synchronized rate and show its effective date and offline status. If a rate is unavailable, original-currency records can still be saved, while conversion-dependent totals or shared operations wait for a valid rate. Reconnection does not silently replace a saved transaction's applied rate. |

**Offline synchronization**

The Android client keeps an encrypted local copy of the signed-in user's permitted data. Personal transaction changes receive stable identifiers and remain visible with pending, synchronized, or conflict status. Synchronization preserves transaction dates and prevents duplicate retries; version conflicts retain both versions for explicit user resolution. Group data remains a read-only cached snapshot while offline, and a valid online session is required before pending personal changes are submitted.

### 4.2 Assumptions and Dependencies
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*  
**Assumptions**

- Users enter transactions manually and honestly.
- Users have an Android smartphone and can connect for initial sign-in, synchronization, and shared operations; personal transaction entry remains available during temporary disconnection.
- Group members agree on expense participants, contributions, and refunds, and confirm actual external payments in the app.
- Users understand that converted totals are bookkeeping estimates using displayed reference rates; the application does not execute currency exchange or bank transfers.

**Dependencies**

| Dependency | Used For | Risk |
|------------|----------|------|
| Google Gemini / OpenAI API | AI Financial Advisor | Quota, cost, availability |
| PostgreSQL | Data storage | Hosting and backup |
| Docker / Docker Compose | Local and deployment environment | Setup problems |
| Docker-capable Linux host | Backend, PostgreSQL, AI service, and admin deployment | Resource limits and host availability; a local Docker environment supports rehearsal |
| In-app notification store | Debt reminders, settlement requests, and budget alerts | Offline users see server-side notifications after the next successful sync; local budget warnings remain available |
| Administrator-maintained exchange-rate table | VND/USD conversion with recorded rate/source/time | Stale or missing rates are shown explicitly and never silently invented |
| Android encrypted local storage and sync queue | Offline personal transactions and cached authorized data | Device storage, session renewal, and explicit resolution of conflicting edits |
| Transactional email service | Password recovery using expiring, single-use reset links | Delivery failure or delay; recovery requires a connection |
| GitHub, Jira, Spec Kit | Development process | Learning curve |

## 5. Product Features
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Nguyen Le Duc Nhat`*

### 5.1 Authentication & Security
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

This feature lets users register, log in, log out, and recover a forgotten password through an expiring reset link. Protected online requests use validated JWT access tokens, while offline access is limited to locally stored data of the previously signed-in user. Access control separates personal records, group membership, and administrator functions because the app stores sensitive financial information. Regular Users and Administrators benefit from a consistent account lifecycle and protection against unauthorized access.

### 5.2 Profile Management
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*  

Users can edit their name and avatar, change their password, select Vietnamese or English, and choose VND or USD as their reporting currency. Reports show converted totals with exchange-rate details while retaining the original currency on each financial entry. These preferences make the app understandable to local users and let group members recognize one another. Regular Users benefit from consistent identity and financial presentation across personal and group screens.

### 5.3 Wallet & Personal Account Management
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

Users create manual cash, bank-account, and credit-card wallets, each with a recorded currency and opening balance. The dashboard shows individual balances and a combined estimate in the selected reporting currency using an identified reference rate. Cached balances remain readable offline, and pending personal transactions update the local view until synchronization. This helps Regular Users understand their recorded assets without connecting to a real bank account.

### 5.4 Personal Transaction Tracking
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

Users record income and expenses with amount, category, wallet, note, date, and an optional receipt image. They can create, edit, or delete personal transactions offline, with changes and receipt uploads queued until reconnection. Stable record identifiers prevent duplicates, while conflicting edits remain visible for resolution rather than being silently overwritten. Regular Users benefit from reliable capture at the time of payment and a transaction history that supports budgets, reports, and AI analysis.

### 5.5 Group Management & Shared Wallets
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

Users create groups for shared activities, invite members by link or code, and use Owner and Member roles to control group access. A group records its currency, shared-wallet contributions, expenses, and member balances separately from private wallets. Group changes require a connection so all members work from the same confirmed ledger, while previously synchronized information remains readable offline. Friends, roommates, and teammates benefit from a transparent record of who contributed, who spent, and who should be reimbursed.

### 5.6 Smart Debt Split & Settlement
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

Shared expenses are divided equally, by percentage, or by exact amount after any required conversion into the group's currency. The app shows confirmed debts and suggests fewer repayment transfers while preserving each member's net balance. A debtor records an external payment claim, and the recipient confirms it before the debt is reduced; partial payments and rejected claims remain traceable. Refunds and corrections reference the original expense and adjust member balances without deleting payment history. Group members benefit from consistent calculations, reminders, and an auditable settlement process.

### 5.7 Budget Planning & Alerting
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

Users set weekly or monthly limits for personal spending categories in their selected budget currency. Spending includes their own group-expense shares once, using the relevant recorded conversion rates. Visual warnings appear when a category reaches 80% and 100% of its limit, including for locally recorded offline expenses. Regular Users benefit from timely feedback instead of discovering overspending only at the end of the month.

### 5.8 Financial Reporting & Analytics
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

The app presents personal income and spending in pie and bar charts and provides group summaries for trips or recurring shared costs. Reports separate consumption from transfers, contributions, and debt settlements, and display the reporting currency and exchange-rate basis. Authorized users can export PDF or Excel reports online, while cached summaries remain readable offline with their last-sync time. Individuals benefit from understanding spending patterns, and groups benefit from clear records they can review together.

### 5.9 Savings Goal Management
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

Users create savings goals with a target amount, currency, deadline, and recorded contributions or withdrawals. The app shows progress and the remaining amount, using recorded conversion rates when a contribution has another currency. A savings allocation moves recorded funds toward a goal without creating new income or counting the same expense twice. Students and young professionals benefit from a measurable saving plan and can view previously synchronized progress offline.

### 5.10 System Administration (Admin Panel)
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

Administrators use an online web panel to list users, lock or unlock accounts, maintain default categories and reference exchange rates, and view aggregate system statistics. Changes to rates retain their source, effective time, and author so converted amounts can be explained. Administrative access does not grant routine access to private transaction histories or permission to confirm another member's settlement. Administrators benefit from consistent maintenance tools, and Regular Users benefit from traceable reference data and clear privacy boundaries.

### 5.11 AI Financial Advisor
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

At the start of a month or on request, the AI analyzes the user's personal spending and attributable group shares, excluding duplicate entries and using recorded currency conversions. It compares available history with budgets, identifies unusual category increases, and suggests a practical allocation for the next month and savings goals. New analysis requires a connection, explains limited history when applicable, and leaves existing finance functions available if the provider fails. Regular Users can review previously saved advice offline and choose whether to apply a suggestion, with no automatic change to budgets or financial records.

### 5.12 Key Workflow Diagrams
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`* 

**Workflow 1 — Record a shared expense and settle debts**

```mermaid
flowchart TD
    A([Member opens group]) --> N{Online?}
    N -->|No| O[Show cached balances and last-sync time]
    O --> P([Reconnect to change shared records])
    N -->|Yes| B[Add expense, payer, currency and participants]
    B --> C[Convert to group currency if needed<br/>and save the rate snapshot]
    C --> D[Calculate equal, percentage or exact shares]
    D --> V{Valid shares and exact total?}
    V -->|No| B
    V -->|Yes| E[Save expense and update confirmed balances]
    E --> F[Show simplified debts and notify members]
    F --> G[Debtor pays outside the app]
    G --> H[Debtor records a pending settlement claim]
    H --> I{Recipient confirms receipt?}
    I -->|No| R[Keep debt unchanged and resolve payment details]
    R --> H
    I -->|Yes| J[Validate amount and post settlement once]
    J --> K{All selected debts settled?}
    K -->|No| F
    K -->|Yes| L([Settlement complete; history retained])
```

**Workflow 2 — Monthly AI Financial Advisor**

```mermaid
sequenceDiagram
    actor U as Regular User
    participant M as Mobile App
    participant B as Backend API
    participant AI as AI Microservice
    participant G as Gemini/OpenAI

    U->>M: Open AI Advisor (or monthly trigger)
    M->>B: Request monthly analysis
    B->>B: Authorize user and collect own data<br/>plus own group shares without duplicates<br/>using recorded conversion rates
    B->>AI: Send necessary category totals,<br/>budgets and savings goal progress
    AI->>G: Prompt with spending data
    alt Provider returns a valid response
        G-->>AI: Analysis and suggestions
        AI->>AI: Validate structured response
        AI-->>B: Analysis result
        B-->>M: Anomalies, budget suggestions, goal advice
        M-->>U: Show advice for review
    else Timeout, unavailable, or invalid output
        AI-->>B: Controlled error
        B-->>M: Analysis unavailable; retry option
        M-->>U: Keep normal finance functions available
    end
```

Workflow 1 requires an online ledger and confirmation by the payment recipient before a debt changes. Workflow 2 operates on the signed-in user's synchronized data and recorded conversion rates; offline users can read cached advice and personal records. Both workflows preserve the separation between external payments and in-app bookkeeping.

## 6. Non-Functional Requirements
> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

The following requirements define acceptance criteria for implementation and final testing. Performance tests use a Linux deployment with at least **2 vCPUs and 4 GiB RAM**, a seeded database of **100 users with 100 personal transactions each**, and group test cases of up to **20 members and 500 expenses per group**. Mobile timing tests use an Android device with **4 GiB RAM**, Android 10 or later, and a stable connection of at least **10 Mbps** with round-trip latency no greater than **100 ms**. Minimum-platform compatibility is checked separately on Android 8.0; every test report records the actual hardware, versions, dataset, and build revision.

### 6.1 Performance
> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

| ID | Requirement |
|----|-------------|
| NFR-P1 | At least 95% of non-AI API requests complete in **less than 500 ms** during the NFR-P3 load test. |
| NFR-P2 | The dashboard and first page of up to 50 transactions appear in **less than 2 seconds** in at least **19 of 20** warm-load trials on the specified mobile test environment. Cached offline lists meet the same target. |
| NFR-P3 | Support **100 concurrent authenticated users for 10 minutes**, each making one non-AI request every two seconds, with an unexpected-error rate **below 1%**. The request mix is 70% reads and 30% personal transaction writes; expected validation and authorization rejections are reported separately. |
| NFR-P4 | An online AI request produces advice or a controlled failure response within **30 seconds**. The interface shows progress and a retry option; successful and failed analysis counts are reported separately. |
| NFR-P5 | Recalculate group balances and simplified repayment suggestions for **20 members and 500 expenses in less than 2 seconds**, preserving each member's net position. |
| NFR-P6 | With the app open, valid authorization, and the specified connection, synchronize **100 queued personal transaction changes within 30 seconds**, excluding receipt uploads and edits awaiting conflict resolution. |

### 6.2 Security
> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

| ID | Requirement |
|----|-------------|
| NFR-S1 | Store passwords only as salted BCrypt hashes; no plaintext or recoverable password is stored or logged. |
| NFR-S2 | Use **HTTPS with TLS 1.2 or later** for deployed client/API and external-service communication. Local development uses isolated test data. |
| NFR-S3 | Access tokens expire after **60 minutes**; refresh sessions expire after **7 days**. Protected requests reject invalid or expired tokens. Logout removes local tokens and private cached data, revokes the refresh session, and invalidates its active access token on the server. Synchronization after session expiry requires reauthentication. |
| NFR-S4 | All private-record, group-membership, administrator-role, and settlement-recipient authorization checks pass the agreed negative-test suite. A Group Owner cannot confirm a payment intended for another member. |
| NFR-S5 | Keep API keys, passwords, signing secrets, and real environment files outside tracked source and submission files. Application logs and AI requests must omit credentials and access tokens. |
| NFR-S6 | Validate amounts, currencies, split totals, uploaded file types, and user input on the server. Use parameterized database access and safe UI rendering; all invalid-input and script-injection cases in the security suite must pass. |
| NFR-S7 | Send the AI provider only the signed-in user's necessary category totals, budget limits, own group shares, and goal progress. Exclude other members' private records, names, emails, phone numbers, raw receipts, and authentication data. |
| NFR-S8 | Encrypt private offline records and receipt files at rest with keys protected by the Android system keystore. Cached data belongs to one signed-in account, requires device/app unlock to access, and is cleared on explicit logout or account removal from the device. |
| NFR-S9 | Password-reset links expire after **15 minutes**, can be used only once, and do not expose whether an unknown email address has an account. |

### 6.3 Reliability, Offline Operation, and Financial Correctness
> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

| ID | Requirement |
|----|-------------|
| NFR-R1 | The demo environment passes **at least 95%** of one-minute health probes during a **two-hour rehearsal** before the demo. |
| NFR-R2 | Back up hosted project data **daily**, retain the latest **7 daily backups**, and demonstrate restoration to a separate test database within **30 minutes** before the final demo. The QA/DevOps Lead owns backup and restore verification. |
| NFR-R3 | Use exact decimal arithmetic with whole-dong VND and cent-precision USD. In every split/refund test, allocated shares equal the recorded amount exactly, and every group debt/fund journal balances to zero. Stable remainder allocation produces the same result on repeated calculation. |
| NFR-R4 | During an AI-provider outage or invalid response, personal tracking, group bookkeeping, and cached advice remain available within their normal connectivity limits; no financial record or budget is changed by a failed AI request. |
| NFR-R5 | After initial synchronization, users can create, edit, and delete personal transactions for cached wallets with **no network connection**. A **24-hour offline test with 100 local changes**, including app restart, loses no acknowledged local change and retains pending status. |
| NFR-R6 | Replaying each queued change **three times** creates no duplicate financial effect. Conflicting edits from two devices are detected, both versions are retained, and the user resolves the conflict before that record is marked synchronized. |
| NFR-R7 | A payment claim remains pending until the intended recipient confirms. Rejection, repeated confirmation, server failure, and attempts to exceed the outstanding debt produce **no incorrect balance change** in the settlement test suite; dependent ledger updates commit together or roll back together. |
| NFR-R8 | Every converted entry stores its original amount/currency and applied rate/source/time. Changing the reference rate or reporting preference does not alter existing group shares, original amounts, or confirmed payments. Missing rates never produce an invented converted total. |

### 6.4 Usability and Accessibility
> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

| ID | Requirement |
|----|-------------|
| NFR-U1 | At least **4 of 5** first-time test participants record their first personal transaction in **3 minutes or less** without assistance, starting from an authenticated account with a wallet. |
| NFR-U2 | At least **4 of 5** participants save a basic expense in **30 seconds or less** from the add-transaction screen, excluding receipt capture/upload. Use the same task script online and offline and record results separately. |
| NFR-U3 | All primary screens use consistent labels and components; text supports Android font scaling to **150%** without hiding required actions, and primary touch targets are at least **48 dp**. |
| NFR-U4 | Provide Vietnamese and English labels for all primary flows, VND/USD formatting, original and converted amounts, and the applied rate's effective date. Amounts are always labelled with their currency. |
| NFR-U5 | Offline, pending synchronization, conflict, and pending/confirmed settlement states are distinguishable using text or icons as well as color. Shared-data views show their last successful synchronization time. |
| NFR-U6 | Budget warnings are updated when a relevant local or synchronized expense causes spending to cross **80% or 100%** of its limit; offline warnings identify that group data may be stale. |

### 6.5 Platform and Compatibility
> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

| ID | Requirement |
|----|-------------|
| NFR-C1 | The Kotlin/Jetpack Compose mobile app installs and supports the core personal and group flows on **Android 8.0, API 26, or later**. Compatibility is checked on API 26 and an Android 10-or-later device/emulator. |
| NFR-C2 | The online admin panel supports the **two most recent stable desktop versions** of Chrome, Edge, and Firefox available at final acceptance; record the tested version numbers. |
| NFR-C3 | The server targets **Java 21 and PostgreSQL 16**. Backend, database, AI service, and admin deployment are reproducible on a Docker-capable Linux host using documented Docker Compose commands and external configuration. |
| NFR-C4 | Receipt capture works without a connection and uploads later. Online recovery, new AI requests, group writes, and settlement confirmation clearly indicate their network requirement; viewing cached data remains available. |

### 6.6 Maintainability and Quality
> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

| ID | Requirement |
|----|-------------|
| NFR-M1 | Automated tests achieve **at least 70% line coverage** of backend services implementing splitting, settlement, refunds, currency conversion, and budget calculations. Coverage is measured against the reviewed build. |
| NFR-M2 | Follow Java naming conventions, the Kotlin Style Guide, PEP 8, and the agreed React conventions. Another member reviews each change before merge; review findings are resolved or explicitly accepted. |
| NFR-M3 | Document all public REST endpoints, authorization rules, request/response schemas, and error responses in OpenAPI. Document synchronization identifiers and conflict responses. |
| NFR-M4 | Apply server database changes through versioned Flyway migrations and verify both a clean setup and upgrade from the previous test build. |
| NFR-M5 | Use Conventional Commits and retain traceability from a Jira task to its implementation/document output and review record. |
| NFR-M6 | Automated financial tests include equal/percentage/exact splits, rounding remainders, partial/rejected settlements, refunds after settlement, duplicate retries, offline conflicts, and VND/USD conversion using fixed test rates. |

### 6.7 Standards and Verification
> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

The API uses REST with JSON and OpenAPI documentation. Security review uses the OWASP Top 10 as a checklist, alongside the explicit authorization and data-protection requirements above. Money calculations follow the stated VND/USD precision, half-up conversion, immutable rate-snapshot, and exact-sum rules. Development follows Scrum, peer review, Conventional Commits, and versioned database migrations.

| Requirement group | Verification method | Acceptance evidence produced during testing |
|-------------------|---------------------|---------------------------------------------|
| Performance, P1–P6 | Defined mixed API load, mobile timing trials, AI timeout simulation, debt benchmark, and queued-change sync timing | Load configuration, timing/error results, actual environment, and tested revision |
| Security, S1–S9 | Password/token/reset tests, role and ownership tests, invalid-input cases, secret checks, AI-payload inspection, and offline-cache/logout checks | Security checklist, test results, reviewed payload samples, and protected-data checks |
| Reliability, R1–R8 | Rehearsal health probes, backup/restore, 24-hour offline/restart exercise, repeated retries, concurrent edits, ledger rollback, and historical-rate tests | Health log, restore record, synchronization/conflict results, and financial invariant test results |
| Usability, U1–U6 | Timed online/offline tasks with five first-time participants, language review, font/touch-target checks, and budget-threshold scenarios | Anonymized task timings, screen captures, accessibility checks, and alert results |
| Compatibility, C1–C4 | Android/API-level installation and workflows, browser matrix, reproducible Compose startup, and disconnected receipt capture | Device/OS/browser versions, startup record, and compatibility results |
| Maintainability, M1–M6 | Business-service coverage, peer review, API/migration checks, and automated financial cases | Coverage report, review record, OpenAPI/migration verification, and test summary |

---

# Part C — Spec Kit Initialization
> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

## 1. Repository and Spec Kit Setup
> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

| Item | Value |
|------|-------|
| GitHub repository | [pdtLong2929/FinSync](https://github.com/pdtLong2929/FinSync), recorded in PA1; `[TODO: verify private visibility and TA access]` |
| Spec Kit location | `/src` |
| Initialization status | `[TODO: not yet evidenced in this report / completed with evidence link]` |
| AI agent used with Spec Kit | `[TODO: actual agent and version]` |
| Spec Kit version | `[TODO]` |
| Setup date | `[TODO]` |
| Environment | `[TODO: OS, required runtime versions, and CLI installation method]` |
| Setup performer / reviewer | `[TODO: member]` / `[TODO: different member]` |
| Evidence / commit | `[TODO: transcript or screenshot and commit hash]` |

**Commands used**

```text
[TODO: paste the exact installation/setup commands actually executed]
[TODO: show that initialization ran in the repository's src directory]
[TODO: paste the constitution-generation/customization command if an AI agent was used]
[TODO: include relevant output, versions, and success/failure result]
```

This is a record template, not proof of initialization. Populate it from the team's actual run and the course-provided tutorial. Preserve existing source files during setup and ensure all initialization artifacts are located under `/src`.

## 2. Submitted Artifacts
> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

| Artifact | Actual path under `/src` | Commit / evidence | Included in zip |
|----------|--------------------------|-------------------|:---------------:|
| Project-specific `constitution.md` | `[TODO: exact generated path]` | `[TODO]` | ☐ |
| `[TODO: generated Markdown filename; repeat one row per file]` | `[TODO: exact path, including hidden folders]` | `[TODO]` | ☐ |

> **Reminder:** Spec Kit creates hidden folders (for example `.specify/` and the agent's command folder). Make sure they are included in the zip file.

List **every Markdown file produced by initialization**, including templates and agent instructions where generated. Record tool-dependent paths from the actual output rather than assuming a fixed folder layout. Link the reviewed inventory or directory listing: `[TODO]`. The original files must be attached; this table and the summary below do not replace them. Git history evidence is recorded in Part D, Section 2.6.

## 3. Constitution Summary
> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

`[TODO]` Summarize the actual customized constitution after initialization; do not describe proposed principles as completed artifacts.

| Principle to cover | FinSync-specific content to verify in the constitution | Actual section / reviewer result |
|--------------------|-------------------------------------------------------|----------------------------------|
| Business boundaries | Manual bookkeeping only; personal/group privacy; real payments remain external | `[TODO]` |
| Financial correctness | Exact money representation, split-total invariants, rounding, and no duplicate expense counting | `[TODO]` |
| Architecture and security | Agreed client/backend/AI/admin boundaries; access control and externalized secrets | `[TODO]` |
| Quality and delivery | Peer review, appropriate tests, documented APIs/migrations, reproducible builds | `[TODO]` |
| Governance | Version, adoption/amendment dates, and how the team approves changes | `[TODO]` |

## 4. Spec Kit Training Evidence
> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

Each member must complete the official YouTube training and submit evidence.

**Official tutorial list:** `[TODO: course-provided YouTube URLs/titles]`. Each evidence item should identify the member, tutorial(s), completion date, and a screenshot, practice log, or individual summary. Suggested summary prompts: what the constitution controls, what files initialization generated, what the member practiced, and one problem/lesson. Do not mark completion from account registration alone.

| Member | Completed / Date | Tutorial(s) / Evidence | Individual Learning Summary | Reviewer / Date |
|--------|------------------|------------------------|-----------------------------|-----------------|
| Phạm Định Tiểu Long | ☐ / `[TODO]` | `[TODO]` | `[TODO]` | Hòa / `[TODO]` |
| Nguyễn Phú Đạt | ☐ / `[TODO]` | `[TODO]` | `[TODO]` | Long / `[TODO]` |
| Nguyễn Lê Đức Nhật | ☐ / `[TODO]` | `[TODO]` | `[TODO]` | Đạt / `[TODO]` |
| Vương Đắc Gia Khiêm | ☐ / `[TODO]` | `[TODO]` | `[TODO]` | Nhật / `[TODO]` |
| Ngô Thái Hòa | ☐ / `[TODO]` | `[TODO]` | `[TODO]` | Khiêm / `[TODO]` |

---

# Part D — AI Usage Report and Weekly Report
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Nguyen Le Duc Nhat`* 

## 1. AI Usage Report
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Nguyen Le Duc Nhat`*

**Declaration:** All five members of Group 04 used **Codex** during preparation of PA2. Assistance included report structure and wording, requirements checks, schedule proposals, documentation audit, and revisions based on the team's decisions. The group confirms that all five members used Codex; individual account histories provide evidence of each member's own interactions.

**Tool and platform:** Codex by OpenAI, used through a workspace session for FinSync. The exact model/runtime version and access hours are not exposed in the supplied conversation record; they must be recoverable from the application screenshots/history in Section D.2.8. Dates below follow the recorded report-preparation sessions in UTC+07:00.

| Usage ID | Recorded date | Purpose | Affected output |
|----------|---------------|---------|-----------------|
| AI-01 | 2026-10-17 | PA2 requirement coverage and report template | Parts A–D and the Appendix |
| AI-02 | 2026-10-17 | Role-based scheduling within 10–25 October | Sections A.4.2–A.4.4 |
| AI-03 | 2026-10-17 | AI disclosure and weekly reporting from supplied guidelines | Part D and Appendix AI Usage Notes |
| AI-04 | 2026-10-18 | Replace link/export evidence with screenshot slots | Jira/Git evidence and submission checks |
| AI-05 | 2026-10-18 | Consolidate duplicate AI evidence | Original prompts retained in the report; redundant file removed |
| AI-06 | 2026-10-18 | Identify editorial notes and unfinished fields | Section-by-section audit supplied in the conversation |
| AI-07 | 2026-10-18 | Finalize Part B using the user's business decisions | Offline support, PA1 survey, accounting, conversion, features, and NFRs |
| AI-08 | 2026-10-18 | Complete the report outside Part C | Plan, meeting attendance, disclosure, and screenshot register |

Detailed original prompts, generated contributions, student-provided decisions, and validation limits are recorded in [Appendix AI Usage Notes](#4-ai-usage-notes). Hòa consolidates the five members' records under S2-24 and Long reviews the disclosure. The register covers the shared conversation available for this report; each member's additional Codex interactions or other AI tools are included when their histories are supplied.

The supplied AI Usage Guidelines require transparency, understanding, independent validation, and supporting screenshots/history. The team distinguishes assistant checks from completed human review and generated drafts from observed results. No training completion, successful build, benchmark, task completion, or future meeting outcome is inferred from AI-generated text.

## 2. Weekly Report
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Nguyen Le Duc Nhat`*  

**Project:** FinSync — Group 04. **Sprint:** Sprint 2 / PA2. **Period:** 2026-10-10 to 2026-10-25. **Reporting date:** 2026-10-18. All proposed meeting times use UTC+07:00.

**Sprint goal:** Prepare an initial Project Plan and Vision Document, initialize Spec Kit under /src, collect individual training evidence, and produce a reproducible skeleton build that supports later implementation.

The structure follows [WeeklyReport.md](../../WeeklyReport.md): one Sprint Planning meeting, two Weekly Scrum meetings, and one Sprint Review/Retrospective. Both Scrum records are kept in this document. Long coordinates the meetings and minutes; all five members provide their own progress updates.

**Attendance and evidence basis:** The group confirms that all five members attended the 10 October Planning and 16 October Scrum meetings. All five are scheduled for the 22 October Scrum and 24 October Review, which are future events at this reporting date. The records consolidate the task baseline, available repository changes, and team confirmations; actual task completion and review are evidenced by the Jira screenshots, rather than inferred from due dates.

| Meeting | Planned date / time | Task | Minutes owner / reviewer |
|---------|---------------------|------|-------------------------|
| Sprint Planning | 2026-10-10, 09:00–10:00, before sprint work begins | S2-01 | Long / Hòa |
| Weekly Scrum #1 | 2026-10-16, 19:30–20:15 | S2-21 | Long / Hòa |
| Weekly Scrum #2 | 2026-10-22, 19:30–20:15 | S2-22 | Long / Hòa |
| Sprint Review & Retrospective | 2026-10-24, 19:30–20:30 | S2-23 | Long / Khiêm |

The table gives the scheduled meeting slots. Planning is the PA1-to-PA2 transition checkpoint on 10 October before implementation begins. Meetings use Google Meet, with follow-up coordination through Discord/Zalo.

### 2.1 Sprint Planning Meeting
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Pham Dinh Tieu Long`* 

**Date / scheduled slot:** 2026-10-10, 09:00–10:00. **Facilitator / note taker:** Long. **Members present:** Phạm Định Tiểu Long, Nguyễn Phú Đạt, Nguyễn Lê Đức Nhật, Vương Đắc Gia Khiêm, and Ngô Thái Hòa. **Members absent:** None; full attendance confirmed by the group.

**Prioritized scope and allocation**

| Priority | Work package | Assigned owner(s), each for their own tasks | Expected output |
|----------|--------------|---------------------------------------------|-----------------|
| 1 | Project Plan, schedule, risk management, and coordination | Long; Nhật for Introduction/Overview | Initial plan covering five sprints, task/reviewer assignments, risks, and builds |
| 1 | Vision, positioning, user needs, and product boundaries | Hòa for positioning/features; Đạt for user research; Nhật for product perspective | Refined descriptions of all ten functional groups and AI Advisor; two Mermaid workflows |
| 1 | Spec Kit setup and training evidence | Khiêm for setup; every member for individual training | Customized constitution, generated Markdown inventory, and five individual evidence records |
| 1 | Quality targets and PA2 evidence | Khiêm for NFRs/evidence; Hòa for the AI log; Long for minutes | Measurable NFRs, AI Usage Notes, four meeting records, and Jira/Git evidence |
| 2 | Mobile/backend/AI skeletons and Build 0 | Đạt for mobile; Nhật for backend; Hòa for AI; Khiêm for integration | Reproducible startup instructions and documented smoke-test results |

Each task has one assignee and a different reviewer, as listed in [Section A.4.3](#43-sprint-2--detailed-task-list). The baseline allocates 99 team hours against approximately 114 available hours, including separate review/attendance budgets and a correction buffer.

**Planning baseline**

- Retain the Android Kotlin/Jetpack Compose baseline and the Spring Boot/PostgreSQL backend described in PA1.
- Keep personal records private and group records visible only to authorized members. Record real payments outside the app.
- Complete training by 12 October, Spec Kit setup by 15 October, feature drafts by 17 October, and skeleton work by 20 October.
- Produce Build 0 by 22 October; reserve 23–24 October for review and fixes, and 25 October for the final submission package.
- Track all writing, learning, coding, review, and evidence tasks in Jira; update actual status and dates from real work.

**Follow-up actions:** ACT-01 and ACT-02 in Section 2.7.

**Summary:** The sprint baseline prioritizes mandatory PA2 documents and training while reserving time for early technical integration. Product descriptions guide workflows and quality targets; component skeletons precede Build 0. The current scope also incorporates the user's 18 October decisions on offline support, recipient-confirmed settlement, and VND/USD conversion, as consolidated in Part B.

### 2.2 Weekly Scrum Meeting #1 — 16 October 2026
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Pham Dinh Tieu Long`* 

**Date / scheduled slot:** 2026-10-16, 19:30–20:15. **Facilitator / note taker:** Long. **Members present:** Phạm Định Tiểu Long, Nguyễn Phú Đạt, Nguyễn Lê Đức Nhật, Vương Đắc Gia Khiêm, and Ngô Thái Hòa. **Members absent:** None; full attendance confirmed by the group.

The checkpoint table identifies each member's assigned outputs, next actions, and issues requiring attention. Completion of individual tasks is recorded in the Jira board/history evidence; the scope listed here does not certify completion without that evidence.

| Member | What have I done since last week? — assigned checkpoint outputs | What will I do until next week? — planned | Issues / problems / obstacles — topics to check |
|--------|---------------------------------------------------------------|------------------------------------------|------------------------------------------------|
| Long | Due by this checkpoint: training evidence (S2-13), organization/risks (S2-03), and schedule/build plan (S2-04) | Coordinate review, resolve Jira/date discrepancies, prepare Scrum #2 and Sprint Review | Missing actual task dates, unconfirmed capacity, and scope changes |
| Đạt | Due: training evidence (S2-14), users/stakeholders/PA1 survey review (S2-06); mobile skeleton scheduled for 16–18 October (S2-18) | Finish the mobile skeleton and two workflows (S2-09); review Build 0 | Mobile/backend contract alignment and how group shares appear in personal views |
| Nhật | Due: training evidence (S2-15), plan overview (S2-02), product perspective/accounting boundaries (S2-07) | Complete backend skeleton/API startup (S2-19); review features, NFRs, and AI integration | Currency handling, duplicate-counting rules, and available integration time |
| Khiêm | Due: repository checks (S2-11), training evidence (S2-16), and Spec Kit artifacts (S2-12) | Finalize NFR verification methods (S2-10); prepare Build 0 and its smoke-test checklist | Missing setup/training evidence and reproducibility across environments |
| Hòa | Due: training evidence (S2-17) and positioning (S2-05); feature paragraphs due 17 October (S2-08) | Finish features, implement AI skeleton (S2-20), and consolidate AI usage records (S2-24) | API quota/timeout handling and missing per-member AI prompts/timestamps |

**Available repository progress:** commit 8af0078 on 14 October reorganized PA1 artifacts and added the PA2 specification; commit 42b3819 on 16 October added the initial PA2 report. These establish document preparation, while reviewed task completion and build results are recorded separately.

**Follow-up actions:** ACT-03 and ACT-04. Any missed due date is recorded in Jira with its reason and revised target.

**Summary:** This checkpoint links documentation and training work to the upcoming skeleton integration. The follow-up focus is a consistent product/accounting baseline, reviewed workflow/NFR drafts, and component readiness for the 22 October build checkpoint. The screenshot register records the outstanding task-history evidence.

### 2.3 Weekly Scrum Meeting #2 — 22 October 2026
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`* 

**Status:** Scheduled for 2026-10-22, 19:30–20:15. **Facilitator / note taker:** Long. **Expected participants:** all five members; none scheduled absent. Actual proceedings and attendance are recorded after the meeting.

| Member | What have I done since last week? — expected progress to verify at the meeting | What will I do until next week? — planned through submission | Issues / problems / obstacles — topics to check |
|--------|----------------------------------------------------------------------------|-------------------------------------------------------------|------------------------------------------------|
| Long | Plan and vision reviews coordinated; task changes and blockers recorded | Facilitate Review on 24 October, approve corrections, and review the final package | Unreviewed items or carry-over work that threaten submission |
| Đạt | Mobile skeleton and Mermaid workflows available; Build 0 review ready | Perform the PA2 cross-check (S2-27), verify diagram readability, and correct UI/workflow inconsistencies | Diagram rendering, ambiguous split/settlement behavior, and client/API mismatches |
| Nhật | Backend skeleton and startup documentation available; AI contract reviewed | Fix integration blockers and verify that architecture/accounting descriptions match the implementation | Startup configuration, database migration, authorization, or contract failures |
| Khiêm | NFR methods completed; Build 0 and smoke-test results ready for review (S2-26) | Resolve integration issues; gather Jira/Git evidence and export/check PDFs under S2-25 | Reproduction failures, missing artifacts, or unreadable exported tables/diagrams |
| Hòa | Feature descriptions and AI skeleton ready; AI usage log in consolidation | Finish the AI log by 23 October, collect remaining member disclosures, and support final report review | Missing prompt history, invalid AI responses, or provider limits |

**Review agenda:** Run the documented Build 0 steps; record pass/fail outcomes, versions, and known defects. Check all Spec Kit generated Markdown and individual training records. Compare Jira assignments/statuses with Section A.4.3 and identify the remaining submission work.

**Planned follow-up actions:** ACT-05 and ACT-06. No new feature work is planned after this checkpoint; remaining work focuses on fixes, review, and evidence.

**Expected outcome:** Determine whether the skeleton and mandatory documents are ready for the Sprint Review, and assign an owner/recovery date to every remaining blocker. Build results and actual completion status enter the final minutes after this checkpoint.

### 2.4 Sprint Review & Retrospective — 24 October 2026
> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Vuong Dac Gia Khiem` | Edited by: `Pham Dinh Tieu Long`* 

**Status:** Scheduled for 2026-10-24, 19:30–20:30. **Facilitator / note taker:** Long. **Expected participants:** all five members; none scheduled absent. This review has not yet occurred at the reporting cutoff.

**Deliverables to demonstrate and accept**

| Deliverable | Presenter / owner | Acceptance check |
|-------------|-------------------|------------------|
| Project Plan | Long; Nhật for the overview | Required sections, at least three risks, consistent schedule, different reviewers, and build plan |
| Vision Document | Hòa, with Đạt and Nhật presenting their assigned sections | Required positioning tables, user needs, PA1 survey reference, 3–5 sentences per feature, and two rendered workflows |
| NFRs and evidence plan | Khiêm | Quantified targets and feasible verification methods; targets distinguished from measured results |
| Spec Kit and training | Khiêm; each member for their own training | Actual constitution/generated Markdown under /src and individual evidence for all five members |
| Build 0 | Khiêm, with component owners | Reproducible startup and recorded smoke-test outcomes; known defects disclosed |
| AI/weekly reporting and submission package | Hòa and Long for reports; Khiêm for packaging | Complete AI Usage Notes, confirmed meeting minutes, Jira/Git evidence, Markdown/PDF files, and correct archive layout |

**Review result record:** Acceptance findings, the reviewed build/revision, completed and incomplete Jira keys, feedback, and carry-over decisions are recorded at sprint close. As of 18 October, this section defines the review scope rather than claiming future acceptance.

**Retrospective basis and improvement plan**

| Required question | Current basis / planned evaluation |
|-------------------|------------------------------------|
| What went well? | The report now has named task owners, different reviewers, a dated build sequence, a consistent accounting/currency baseline, and all required vision feature descriptions. The final review evaluates how these helped delivery. |
| What went wrong? | Jira/Git/AI screenshots and final task-review evidence are not yet attached at the reporting cutoff. Integration success and training completion cannot be inferred from the written plan. |
| What problems occurred and what caused them? | Planning dates, written outputs, and actual acceptance are recorded separately. The current evidence gap requires coordinated screenshot/history collection rather than retrospective invention of task dates or results. |
| What can be done differently next sprint? | Attach evidence when each task is reviewed, schedule contract/accounting checks before implementation, and run an integration checkpoint before the final Review. Long coordinates ownership; Khiêm checks build/evidence readiness. |
| What lessons were learned? | The current preparation demonstrates the need to distinguish cash movement from personal consumption, preserve exchange-rate history, and reserve time for offline synchronization, review, and evidence collection. Further delivery lessons are added from actual sprint-close outcomes. |

**Expected outcome:** Establish readiness for PA2 submission and the Sprint 3 action list. The final goal outcome is assessed from accepted artifacts and actual task evidence at this meeting; packaging is scheduled to finish on 25 October.

### 2.5 Jira Evidence
> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Nguyen Phu Dat`* 

**Evidence format:** Embed screenshots of the Sprint 2 Jira board and task details below. The image paths are placeholders; the team will add the actual images later. Keep task keys, assignees, statuses, and relevant dates readable in both Markdown and the exported PDF.

**Figure D.1 — Sprint Planning board**

![TODO: Add the Sprint 2 Jira Planning board screenshot](screenshots/jira-sprint2-planning.png)

*Caption: Sprint 2 scope and task assignments at Planning. Planned checkpoint: 10 October 2026. Actual capture date/time: [TODO]. Show the sprint name, task keys, assignees, and planned dates.*

**Figure D.2 — Weekly Scrum #1 board**

![TODO: Add the Sprint 2 Jira board screenshot for Weekly Scrum 1](screenshots/jira-sprint2-scrum1.png)

*Caption: Task assignments and actual progress for the first Scrum checkpoint, planned for 16 October 2026. Actual capture date/time: [TODO].*

**Figure D.3 — Weekly Scrum #2 board**

![TODO: Add the Sprint 2 Jira board screenshot for Weekly Scrum 2](screenshots/jira-sprint2-scrum2.png)

*Caption: Integration progress, blockers, and tasks awaiting review at the second Scrum checkpoint, planned for 22 October 2026. Actual capture date/time: [TODO].*

**Figure D.4 — Sprint Review board**

![TODO: Add the Sprint 2 Jira board screenshot for the Sprint Review](screenshots/jira-sprint2-review.png)

*Caption: Final task statuses, reviewed work, and any carry-over tasks at the Sprint Review, planned for 24 October 2026. Actual capture date/time: [TODO].*

**Figure D.5 — Task details and history**

![TODO: Add Jira task details and history showing assignments and dates](screenshots/jira-sprint2-task-history.png)

*Caption: Task [TODO: actual Jira key] showing the assignee, created/assigned dates, start/due dates, completion date if applicable, and review evidence. Actual capture date/time: [TODO]. Add further screenshots as needed to support the task-history register in Section A.4.3.*

**Attachment status:** Five Jira screenshots remain to be supplied. Add images at the indicated paths relative to this report, or update the paths to match the supplied files. If an earlier checkpoint screenshot was not taken, use a screenshot of the actual task history and state its real capture date. Map local S2 IDs to the Jira keys visible in the images.

### 2.6 Git History Evidence
> *Performed by: Vuong Dac Gia Khiem | Reviewed by: Pham Dinh Tieu Long | Edited by: Vuong Dac Gia Khiem*

**Evidence format:** Embed screenshots of the repository's commit history from GitHub or a readable terminal Git log. The team will add the actual images later. Show commit hashes, authors, timestamps, and messages for Sprint 2; include the final submitted revision and identify the branch in the image or caption.

**Figure D.6 — Git commit history**

![TODO: Add the Git commit history screenshot for Sprint 2](screenshots/git-log-sprint2-01.png)

*Caption: FinSync repository commit history for Sprint 2, 10–25 October 2026. Branch: [TODO]. Actual capture date/time: [TODO]. Final submitted commit hash: [TODO].*

**Figure D.7 — Git history continuation / final revision**

![TODO: Add a continuation or final submitted revision screenshot](screenshots/git-log-sprint2-02.png)

*Caption: Additional commit history or details of the final submitted revision. Actual capture date/time: [TODO]. Use overlapping commit entries when continuing Figure D.6 so the sequence is clear. Remove this slot if one screenshot provides all required evidence.*

Khiêm captures the final screenshots during S2-25, and Long checks that the visible revision matches the submitted files. **Attachment status:** Pending image insertion. Khiêm prepares the evidence and Long reviews it under S2-25.

### 2.7 Meeting Action Register
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

The following actions implement the sprint baseline and evidence requirements. Actual completion is established through the Jira task-history screenshots.

| Action / source | Action and expected result | Owner | Reviewer | Due | Status |
|-----------------|----------------------------|-------|----------|-----|--------|
| ACT-01 / Planning | Map every S2 task to a Jira key and confirm capacity, dates, and reviewers | Long | Khiêm | 2026-10-10 | Awaiting task-history evidence |
| ACT-02 / Planning | Collect five individual training records and the exact Spec Kit artifact inventory | Khiêm | Nhật | 2026-10-15 | Awaiting task-history evidence |
| ACT-03 / Scrum #1 | Reconcile actual task progress with Section A.4.3 and record recovery dates for missed tasks | Long | Hòa | 2026-10-17 | Awaiting task-history evidence |
| ACT-04 / Scrum #1 | Confirm currency, own-group-share, and duplicate-counting rules before finalizing workflows/NFRs | Nhật | Khiêm | 2026-10-17 | Awaiting task-history evidence |
| ACT-05 / Scrum #2 | Record Build 0 smoke-test outcomes and triage remaining integration blockers | Khiêm | Đạt | 2026-10-22 | Planned |
| ACT-06 / Scrum #2 | Complete per-member AI disclosures with access times, prompts, edits, and evidence | Hòa | Long | 2026-10-23 | Planned |
| ACT-07 / Review | Reconcile report, Jira, actual meeting outcomes, and outstanding submission items | Đạt | Hòa | 2026-10-24 | Planned |
| ACT-08 / Review | Capture and embed Git/Jira screenshots, check rendered PDFs, and verify PA2-Group04 archive contents | Khiêm | Long | 2026-10-25 | Planned |

### 2.8 Codex Usage Evidence
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

All five members' Codex use is confirmed by the group. Each image below records that member's actual prompts, relevant responses, visible tool/model information, and access timestamp. Additional images may be inserted when one screenshot cannot show the complete interaction; the Usage ID in the caption identifies the corresponding Appendix entry.

**Figure D.8 — Long's Codex usage**

![ADD IMAGE: Long's Codex prompt and response history](screenshots/ai-usage-long.png)

*Caption: Phạm Định Tiểu Long — Codex usage, relevant Usage ID(s), and actual access date/time visible in the screenshot.*

**Figure D.9 — Đạt's Codex usage**

![ADD IMAGE: Dat's Codex prompt and response history](screenshots/ai-usage-dat.png)

*Caption: Nguyễn Phú Đạt — Codex usage, relevant Usage ID(s), and actual access date/time visible in the screenshot.*

**Figure D.10 — Nhật's Codex usage**

![ADD IMAGE: Nhat's Codex prompt and response history](screenshots/ai-usage-nhat.png)

*Caption: Nguyễn Lê Đức Nhật — Codex usage, relevant Usage ID(s), and actual access date/time visible in the screenshot.*

**Figure D.11 — Khiêm's Codex usage**

![ADD IMAGE: Khiem's Codex prompt and response history](screenshots/ai-usage-khiem.png)

*Caption: Vương Đắc Gia Khiêm — Codex usage, relevant Usage ID(s), and actual access date/time visible in the screenshot.*

**Figure D.12 — Hòa's Codex usage**

![ADD IMAGE: Hoa's Codex prompt and response history](screenshots/ai-usage-hoa.png)

*Caption: Ngô Thái Hòa — Codex usage, relevant Usage ID(s), and actual access date/time visible in the screenshot.*

---

# Appendix — Submission Checklist
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

## 1. Evidence Register
> *Performed by: Vuong Dac Gia Khiem | Reviewed by: Pham Dinh Tieu Long | Edited by: Vuong Dac Gia Khiem*

The following images are the remaining visual attachments outside Part C. The original image files belong in **PA/PA2/screenshots/** and are embedded in Part D. Actual capture dates, relevant task keys/Usage IDs, and the visible revision identify the evidence. An old checkpoint may be supported by a current screenshot of genuine task history, with its actual capture date.

| Figure | Section | Image filename | Required visible information | Owner |
|--------|---------|----------------|------------------------------|-------|
| D.1 | D.2.5 | jira-sprint2-planning.png | Sprint name, planned task scope, assignees, start/due dates, and actual capture time | Khiêm |
| D.2 | D.2.5 | jira-sprint2-scrum1.png | First-checkpoint task status and assignments, or history supporting that checkpoint | Khiêm |
| D.3 | D.2.5 | jira-sprint2-scrum2.png | Second-checkpoint progress, blockers, and review status | Khiêm |
| D.4 | D.2.5 | jira-sprint2-review.png | Final completed/reviewed work and carry-over tasks | Khiêm |
| D.5 | D.2.5 | jira-sprint2-task-history.png | Real Jira keys and created/assigned/start/due/completed dates, plus reviewer evidence; add pages for further tasks | Khiêm |
| D.6 | D.2.6 | git-log-sprint2-01.png | Commit hashes, authors, dates, messages, branch, and final submitted revision | Khiêm |
| D.7 | D.2.6 | git-log-sprint2-02.png | Continuation/final-commit details; optional when D.6 contains the required history | Khiêm |
| D.8 | D.2.8 | ai-usage-long.png | Long's original prompt(s), relevant response(s), access date/hour, and visible tool/model information | Long |
| D.9 | D.2.8 | ai-usage-dat.png | Đạt's original prompt(s), relevant response(s), access date/hour, and visible tool/model information | Đạt |
| D.10 | D.2.8 | ai-usage-nhat.png | Nhật's original prompt(s), relevant response(s), access date/hour, and visible tool/model information | Nhật |
| D.11 | D.2.8 | ai-usage-khiem.png | Khiêm's original prompt(s), relevant response(s), access date/hour, and visible tool/model information | Khiêm |
| D.12 | D.2.8 | ai-usage-hoa.png | Hòa's original prompt(s), relevant response(s), access date/hour, and visible tool/model information | Hòa |

Long reviews Jira/Git attachment consistency; Hòa consolidates the individual AI histories. Additional image pages are included when needed for readable task or prompt histories. Part C and its initialization/training evidence are maintained separately from this revision.

## 2. Requirement Coverage
> *Performed by: Pham Dinh Tieu Long | Reviewed by: Ngo Thai Hoa | Edited by: Pham Dinh Tieu Long*

| Area | Points | Coverage in this report | Evidence or closing activity |
|------|-------:|-------------------------|------------------------------|
| A — Project Plan | 15 | Overview/scope, roles, nine risks with owners, five-sprint schedule, 27 dated tasks, 99-hour capacity allocation, and five builds | Jira task/history screenshots and actual review/completion records |
| B — Vision Document | 20 | Required positioning tables, stakeholders/needs, unchanged PA1 survey, offline/currency/accounting rules, eleven feature paragraphs, two user workflows, quantified NFRs | Initial requirements baseline; implementation/testing evidence is produced in later sprints |
| C — Spec Kit | 5 | Existing Part C retained without modification in this revision | Original initialization files, constitution, and five-member training evidence remain the team's separate work |
| D — AI/Weekly Reports | 5 | All-member Codex declaration, eight visible request records, two past meetings with confirmed full attendance, two future meetings, action/evidence registers | AI/Jira/Git screenshots, human review, actual task outcomes, and the remaining scheduled meetings |
| Submission | — | English narrative, Mermaid source, section responsibility lines, and package specification | Rendered diagrams/PDFs and final archive verification |

## 3. Submission Package and Final Checks
> *Performed by: Vuong Dac Gia Khiem | Reviewed by: Pham Dinh Tieu Long | Edited by: Vuong Dac Gia Khiem*

The submission is named **PA2-Group04.zip** and opens as a single **PA2-Group04/** directory. It contains the report's original Markdown and converted PDF, actual evidence images, and the original Spec Kit artifacts when Part C is completed. Relative links and hidden generated folders are preserved.

| Package content | Required form |
|-----------------|---------------|
| PA/PA2/report.md and report.pdf | Final reviewed Markdown and PDF with readable tables, captions, and rendered Mermaid diagrams |
| PA/PA2/screenshots/ | Original Jira, Git, and all five members' Codex evidence images |
| src/ | Actual Spec Kit initialization artifacts, preserving original paths and hidden folders |
| Referenced PA1 material | Proposal, unchanged survey, and relevant screenshots used as the vision baseline |
| Referenced guideline/context files | PA2 specification, WeeklyReport.md, AI-Usage-Guidelines.md, and GEMINI.md where linked in the submitted package |
| Separate meeting/build records, if used | Actual minutes, final build revision, smoke-test results, and carry-over decisions consistent with the report |

- [x] The planning/vision narrative is written in English; original Vietnamese prompts are retained as source evidence with English translations.
- [x] Part A includes dated assignments, different reviewers, named risk owners, capacity, and staged builds.
- [x] Part B contains eleven feature paragraphs and two representative user workflows in Mermaid.
- [x] All five members' Codex use and full attendance at the past meetings are declared from the team's confirmation.
- [x] The eight visible report-preparation requests are disclosed.
- [ ] Jira/Git/Codex screenshots are supplied, readable, and labelled with actual dates, keys, and revisions.
- [ ] Actual task acceptance and review records match the submitted artifacts; remaining meeting outcomes are recorded at sprint close.
- [ ] Part C is completed with actual initialization artifacts and individual training evidence.
- [ ] Markdown is converted to PDF after evidence insertion; all diagrams, tables, and image captions remain readable.
- [ ] The final archive contains all referenced files and excludes credentials, dependency caches, unrelated build output, and temporary data.

## 4. AI Usage Notes
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

**Declaration:** All five members used Codex in PA2. This disclosure records the assistance that occurred, including substantive draft wording and planning proposals, and the user-supplied decisions that guided revisions. It does not describe AI-generated outputs as independently authored student work or as successful implementation/testing results.

The supplied AI Usage Guidelines require tool/version/platform, access date/hour, prompts, purpose, generated content, independent contribution, human validation, and screenshots/history. They restrict report assistance and prohibit generation of an entire report, invented results, and false declarations. The team's final review must assess accepted content against those requirements; disclosure itself is not certification of compliance.

### 4.1 Common Tool Metadata and Validation Responsibility
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

| Field | Recorded information |
|-------|----------------------|
| Tool / provider | Codex / OpenAI |
| Platform | Workspace session for the FinSync project |
| Users | Phạm Định Tiểu Long, Nguyễn Phú Đạt, Nguyễn Lê Đức Nhật, Vương Đắc Gia Khiêm, and Ngô Thái Hòa; use confirmed by the group |
| Model/runtime version | Not exposed in the supplied text record; actual application information is included in each member's screenshot/history where available |
| Dates / access hours | Shared request dates are recorded below; precise hours are supplied by the original application histories, not inferred from Git or meeting timestamps |
| Supporting evidence | Figures D.8–D.12; additional pages retain the full prompts and relevant outputs |
| Individual request attribution | The eight entries describe the available shared conversation. Each member's own screenshots identify their additional interactions; the same shared request is not arbitrarily attributed to all five accounts. |
| Human review responsibility | Hòa consolidates disclosures and Long reviews them; the responsible section author explains accepted material and records substantive revisions against the relevant task |
| Validation status | Assistant document checks are recorded per interaction. Completion of human review is supported by actual task/history evidence and is not inferred from an attribution line. |

### 4.2 AI-01 — PA2 requirement coverage and report template
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

**Recorded date:** 2026-10-17. **Tool/platform:** Codex / FinSync workspace session. Exact access hour and model metadata are retained in the relevant member's application screenshot/history.

**Prompt — English translation:** Read the PA2 specification and update the existing report as a template, using GEMINI.md for context.

**Original request** (formatting and file references normalized):

> đọc [pa2_project_assignment_specification.md](pa2_project_assignment_specification.md) sau đó cập nhật [report.md](report.md) cho đạt yêu cầu giùm tôi, bạn cứ tạo cái template, sau đó tôi sẽ bổ sung những gì còn thiếu sau, có thể đọc [GEMINI.md](../../GEMINI.md) để tham khảo Context nhé

**AI-generated contribution:** Revised the existing report's organization, vision wording, planning/evidence tables, workflows, proposed NFRs, and submission guidance. This included substantive draft text and proposals, beyond grammar correction.

**Student input and independent contribution:** The user supplied the existing proposal/context, assignment, and draft, and explicitly requested a template with missing information left for later completion. Earlier files are source inputs; this session does not establish that every sentence in them was independently written.

**Validation and human revision:** Assistant checks compared the local assignment/context, headings, links, task/reviewer separation, and feature paragraph lengths. Human acceptance/revisions are evidenced through the assigned task's review record.

### 4.3 AI-02 — Role-based scheduling within 10–25 October
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

**Recorded date:** 2026-10-17. **Tool/platform:** Codex / FinSync workspace session. Exact access hour and model metadata are retained in the relevant member's application screenshot/history.

**Prompt — English translation:** Allocate suitable start/due dates during 10–25 October and distribute tasks according to the members' roles.

**Original request** (formatting and file references normalized):

> mục 4.3, bạn tự gán start date với due date sao cho phù hợp luôn đi, PA2 này làm trong 2 tuần từ 10/10 - 25/10 nên bạn chia task sao cho hợp lý nhé, dựa vào vai trò các thành viên mà chia task nhé

**AI-generated contribution:** Proposed role-based ownership and dates for 27 tasks, sequenced dependencies and review time, and aligned the Sprint 2 overview and Build 0 target.

**Student input and independent contribution:** The user supplied the sprint window and required allocation by team role. Team names and roles came from the existing project records.

**Validation and human revision:** Assistant checks validated dates within the sprint, different assignee/reviewer pairs, and dependency order. Final task progress and real Jira keys are supplied in screenshots.

### 4.4 AI-03 — AI disclosure and weekly reporting from supplied guidelines
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

**Recorded date:** 2026-10-17. **Tool/platform:** Codex / FinSync workspace session. Exact access hour and model metadata are retained in the relevant member's application screenshot/history.

**Prompt — English translation:** Prepare Part D using WeeklyReport.md and AI-Usage-Guidelines.md.

**Original request** (formatting and file references normalized):

> phần D AI Usage Report and Weekly Report, bạn dựa vào 2 file là [WeeklyReport.md](../../WeeklyReport.md) và [AI-Usage-Guidelines.md](../../AI-Usage-Guidelines.md) để viết cho tôi nhé, bạn viết sao cho hợp lý là được

**AI-generated contribution:** Prepared the declaration, AI records, Appendix notes, and role-specific meeting/checkpoint drafts. Inspected repository history; no attendance, completed training, or build success was generated as fact.

**Student input and independent contribution:** The user selected the two local guideline files and the required report section. Actual meeting and task evidence was not supplied at that point.

**Validation and human revision:** Assistant checks compared the required meeting types, three Scrum questions, five retrospective topics, and AI disclosure fields. Team confirmation supplies attendance; actual task completion and future outcomes remain separate.

### 4.5 AI-04 — Replace link/export evidence with screenshot slots
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

**Recorded date:** 2026-10-18. **Tool/platform:** Codex / FinSync workspace session. Exact access hour and model metadata are retained in the relevant member's application screenshot/history.

**Prompt — English translation:** Replace Jira/Git link-based evidence with screenshot slots; images will be supplied later.

**Original request** (formatting and file references normalized):

> mấy phần như git log với jira á, là chụp ảnh minh chứng chứ không phải gắn link như bạn đâu, bạn sửa lại giùm tôi nhé, tôi sẽ bổ sung ảnh sau

**AI-generated contribution:** Replaced Jira/Git evidence links with five Jira and two Git image slots, captions, and matching screenshot instructions.

**Student input and independent contribution:** The user selected screenshots as the required evidence format and stated that images would be added later.

**Validation and human revision:** Assistant checks counted image slots, removed link/export instructions, and retained attribution. Actual screenshots and capture dates are supplied by the team.

### 4.6 AI-05 — Consolidate duplicate AI evidence
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

**Recorded date:** 2026-10-18. **Tool/platform:** Codex / FinSync workspace session. Exact access hour and model metadata are retained in the relevant member's application screenshot/history.

**Prompt — English translation:** Remove the separate AI evidence file if its content duplicates the report.

**Original request** (formatting and file references normalized):

> file ai-usage-codex-2026-10-17.md là y chang phần AI trong report.md đúng không, nếu giống thì xóa đi cũng được, cần thiết tôi nhờ bạn viết lại sau

**AI-generated contribution:** Moved the three original prompts into the report, removed the redundant evidence file, and removed stale references.

**Student input and independent contribution:** The user authorized removal of redundant documentation. Original prompts were preserved in the canonical report.

**Validation and human revision:** Assistant checks confirmed file removal, preservation of all three original prompts, and removal of broken references.

### 4.7 AI-06 — Identify editorial notes and unfinished fields
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

**Recorded date:** 2026-10-18. **Tool/platform:** Codex / FinSync workspace session. Exact access hour and model metadata are retained in the relevant member's application screenshot/history.

**Prompt — English translation:** Identify all assistant editorial notes and unfinished fields by report section and location.

**Original request** (formatting and file references normalized):

> bạn xem file report.md những chỗ nào là ghi chú của bạn thì ghi chi tiết ra là ở mục nào phần nào cho tôi đi, tại report dài quá nhiều khi tôi bỏ qua mà không biết

**AI-generated contribution:** Read the current report and supplied a section/line-level inventory of editorial instructions, placeholders, and planned rather than observed content.

**Student input and independent contribution:** The user requested an audit to locate assistant notes, rather than additional project facts.

**Validation and human revision:** The audit described the file as it existed at that time; line numbers subsequently changed as revisions were applied.

### 4.8 AI-07 — Finalize Part B using the user's business decisions
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

**Recorded date:** 2026-10-18. **Tool/platform:** Codex / FinSync workspace session. Exact access hour and model metadata are retained in the relevant member's application screenshot/history.

**Prompt — English translation:** Complete Part B with offline support, unchanged PA1 survey findings, settled accounting rules, and currency conversion.

**Original request** (formatting and file references normalized):

> phần B nhé
> B3.3: có hổ trợ offline
> B3.5: bạn hổ trợ giúp tôi nhé, không thay đổi gì so với PA1
> B4.1: chốt tất cả nhé
> B4.1: quy tắc tiền tệ: có đổi nhé
> các phần còn lại bạn tự trình bày sao cho hợp lý đi, giúp tôi phần B trước đã nhé

**AI-generated contribution:** Retained the twelve-row PA1 comparison, specified offline/online boundaries, settled accounting/refund/confirmation and VND/USD rules, and aligned feature paragraphs, workflows, and quantified NFRs.

**Student input and independent contribution:** The user confirmed offline support, no survey changes from PA1, adoption of the accounting rules, and VND/USD conversion. The assistant supplied the detailed wording and acceptance criteria.

**Validation and human revision:** Assistant checks confirmed the exact PA1 table values, eleven 3–5-sentence feature paragraphs, attribution, local reference paths, and absence of unfinished Part B markers. These checks do not establish implemented/tested product features.

### 4.9 AI-08 — Complete the report outside Part C
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

**Recorded date:** 2026-10-18. **Tool/platform:** Codex / FinSync workspace session. Exact access hour and model metadata are retained in the relevant member's application screenshot/history.

**Prompt — English translation:** Complete the report except Part C; retain image evidence slots and list what remains to be supplied.

**Original request** (formatting and file references normalized):

> ngoại trừ phần C ra, bạn hoàn thành hết giúp tôi đi, các phần nào cần ảnh minh chứng thì để đó và ghi ra đây là cần bổ sung những gì, nhớ làm sao cho hợp lý nhé

**AI-generated contribution:** Completed Part A planning/capacity/risk assignments, consolidated confirmed attendance and all-member Codex use, extended disclosure to eight requests, and prepared the evidence register and submission checks. Part C was preserved.

**Student input and independent contribution:** The user excluded Part C, requested completion of the remaining document, and confirmed that all five members used Codex and attended the past meetings. Future meeting participation remains scheduled. The clarification was: “cả 5 thành viên đều dùng Codex và tất cả buổi họp có mặt đầy đủ nhé”.

**Validation and human revision:** Document validation checks capacity totals, task/build/meeting dates, screenshot references, feature structure, and preservation of Part C. Completed peer review, final build results, exact AI access hours/model metadata, and future proceedings require the corresponding actual records.

### 4.10 Final Disclosure Control
> *Performed by: Ngo Thai Hoa | Reviewed by: Pham Dinh Tieu Long | Edited by: Ngo Thai Hoa*

Each member retains their own Codex history, including additional self-training, coding, debugging, translation, or Spec Kit interactions. Hòa consolidates these histories without replacing them with invented examples, and Long checks that accepted AI-assisted sections, human revisions, and source references can be explained. The final disclosure covers any additional tool use supplied before submission; exact access metadata and completed human review remain tied to the actual records.
