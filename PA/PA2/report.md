# PA2-2026 Report — FinSync (Group 04)

**Course:** CS300 - CSC13002 - Introduction to Software Engineering   
**Assignment:** Project Assignment 2 (PA2-2026) — Sprint 2  
**Group:** Group 04  
**Project:** FinSync — Personal Finance & Group Expense Manager with AI Advisor  
**Sprint 2 period:** `2026-10-10 to 2026-10-25`   

## Table of Contents

> *Performed by: Nguyen Le Duc Nhat | Reviewed by: Pham Dinh Tieu Long | Edited by: Nguyen Le Duc Nhat*

- [Team Members](#team-members)
- [Part A — Project Plan](#part-a--project-plan)
- [Part B — Vision Document](#part-b--vision-document)
- [Part C — Spec Kit Initialization](#part-c--spec-kit-initialization)
- [Part D — AI Usage Report and Weekly Report](#part-d--ai-usage-report-and-weekly-report)
- [Appendix — AI Usage Notes](#appendix--ai-usage-notes)

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

## Part A — Project Plan

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

### 1. Introduction

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

FinSync is a mobile application that combines personal finance management (similar to *Money Lover*) with smart group expense sharing (similar to *Splitwise*) in a single product. It is designed for students, young professionals, and small groups who share costs such as rent, trips, and events.

The system works as a **bookkeeping, debt calculation, and reminder tool only**. It does not connect to real bank accounts and never moves money automatically. An AI Financial Advisor analyzes spending history and suggests a reasonable spending plan for the next month.

This document is the initial version of the Project Plan. It will be updated in PA3-2026 based on the TA feedback.

### 2. Project Overview

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

#### 2.1 Goals

- Build a working mobile app that unifies personal and group finance tracking.
- Automate expense splitting and debt optimization to reduce the number of transfers between group members.
- Provide an AI Financial Advisor that turns raw spending data into clear monthly guidance.
- Provide a simple web admin panel for basic system administration.
- Apply Scrum and good software engineering practice throughout the semester (Jira, Git, code review, testing).

#### 2.2 Scope

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

#### 2.3 Deliverables

| Deliverable | Sprint / PA | Format |
|-------------|-------------|--------|
| Project Proposal, app survey | Sprint 1 / PA1 | Markdown + PDF |
| Project Plan (initial), Vision Document (initial), Spec Kit init | Sprint 2 / PA2 | Markdown + PDF, repo artifacts |
| Revised Project Plan and Vision Document after TA feedback | Sprint 3 / PA3 | Markdown + PDF |
| Use cases, architecture and design documents | Sprint 3–4 | Markdown + PDF |
| Test plan and test cases | Sprint 4 | Markdown + PDF |
| Source code: mobile app, backend API, AI microservice, admin web | Sprint 2–5 | GitHub repository (private) |
| Test builds | See [Build Plan](#44-build-plan) | APK / deployed environment |
| Final demo of all features | Sprint 5 / PA5 | Live demo |

#### 2.4 Assumptions

- Every member works as a full-stack engineer; roles below only define the main person in charge (Lead).
- Each of the five members allocates at least **10 hours per week**, equivalent to approximately **114 team hours** over the 16-calendar-day PA2 window. Work is distributed between individual outputs, review, and meetings.
- Local Docker environments are the baseline for development and rehearsal. Remote hosting is optional for the demo; external AI calls are limited, cached where appropriate, and controlled by configuration so development can continue with synthetic responses during provider outages.
- Users enter all transactions manually; actual payments happen outside the app.
- Dates for PA3–PA5 are internal planning targets in this initial plan; official course deadlines take precedence. Their detailed scope will be refined at the start of each sprint.

### 3. Project Organization

> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*

#### 3.1 Team Structure and Roles

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

#### 3.2 Risk Management

| ID | Risk | Probability | Impact | Mitigation Strategy | Owner |
|----|------|-------------|--------|---------------------|-------|
| R1 | Member unavailability or uneven workload | Medium | High | Limit most work packages to three days, reserve review time, use a different reviewer as backup, and redistribute incomplete work during Scrum. | Long |
| R2 | Scope creep across eleven feature groups | High | High | Deliver a basic usable flow for every planned feature by Build 3; prioritize correctness and integration, and defer cosmetic depth through a documented scope decision. | Long |
| R3 | Learning Kotlin/Compose, Spec Kit, and deployment tools | Medium | Medium | Schedule individual training and small runnable skeletons in Sprint 2; retain the PA1 stack and share setup instructions. | Đạt |
| R4 | AI quota, latency, cost, or invalid suggestions | Medium | Medium | Use bounded requests, aggregate input, schema validation, a 30-second timeout, cached advice, and synthetic development responses. Keep finance functions independent of the AI provider. | Hòa |
| R5 | Mobile/backend/AI/admin integration failures | Medium | High | Define API contracts and shared currency/accounting rules early, smoke-test each build, and integrate before the final review. | Nhật |
| R6 | Exposed credentials or unauthorized financial access | Low | High | Externalize secrets, review tracked files and authorization tests, limit AI payloads, and rotate any exposed credential. | Khiêm |
| R7 | Incorrect shares, refunds, or settlements | Medium | High | Use exact money arithmetic, deterministic remainder allocation, immutable adjustments, atomic updates, and recipient-confirmation/retry tests. | Nhật |
| R8 | Lost or duplicated offline changes | Medium | High | Persist a local queue, identify operations uniquely, retain conflicting versions, test restart/retry behavior, and keep shared writes online. | Đạt |
| R9 | Stale or inconsistent exchange rates | Medium | Medium | Store rate/source/time with converted entries, label cached rates, reject missing-rate conversions, and test that rate updates never rewrite historical debt. | Nhật |

### 4. Project Plan

> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Vuong Dac Gia Khiem` | Edited by: `Pham Dinh Tieu Long`*

#### 4.1 Process

The project follows the **Scrum** process. The semester is divided into **5 sprints**, each matching one Project Assignment (Sprint 1 = PA1, Sprint 2 = PA2, and so on). Each sprint lasts 2–3 weeks and has four meetings:

| Meeting | When | Purpose |
|---------|------|---------|
| Sprint Planning (1) | At the transition before sprint implementation; Sprint 2 kickoff slot: 10 October, 09:00–10:00 | Prioritize work, allocate capacity, and assign tasks |
| Weekly Scrum (2) | During sprint | Each member answers: done since last week / plan until next week / obstacles |
| Sprint Review & Retrospective (1) | End of sprint | Review results; what went well, what went wrong, root causes, improvements, lessons learned |

Tools follow the PA1 team contract: Jira (task tracking), GitHub (code and review), Discord / Zalo (daily communication), and Google Meet / Zoom (meetings).

The Jira workflow is **To Do → In Progress → Done**, as recorded in PA1. Work remains In Progress until another member reviews it. A task is Done when its output is linked, its acceptance criteria are checked, its review is recorded, and its actual completion date is entered. Documentation and self-training tasks follow the same rules as coding tasks.

#### 4.2 Sprint Overview

| Sprint | PA | Planning period | Main focus |
|--------|----|-----------------|------------|
| Sprint 1 | PA1 | Before 2026-10-10; PA1 artifacts available in repository by 2026-10-05 | Proposal, app survey, team contract, and initial repository/backend setup |
| Sprint 2 | PA2 | **2026-10-10 to 2026-10-25** | Initial plan/vision, Spec Kit/training, skeletons, and Build 0 |
| Sprint 3 | PA3 | 2026-10-26 to 2026-11-08 | Revised documents; Auth, Profile, Wallet, offline Personal Transactions; AI/admin prototypes; Build 1 |
| Sprint 4 | PA4 | 2026-11-09 to 2026-11-22 | Groups, shared fund, split/settlement/refunds, currency conversion, budgets, savings, reports; Build 2 |
| Sprint 5 | PA5 | 2026-11-23 to 2026-12-06 | Complete AI/admin/export, full regression and offline rehearsal, release candidate, final build, and demo |

Sprint 3–5 dates are team planning targets aligned to two-week cycles, rather than confirmed course submission deadlines. Sprint Planning refines each later sprint's task-level estimates and assignments.

#### 4.3 Sprint 2 — Detailed Task List

The dated task list is the Sprint 2 baseline. Every task has one assignee and a different reviewer; due dates include the owner/reviewer handoff. The S2 identifiers name work packages and are retained in Jira task titles. Section D.2.5 contains the single board screenshot and the four Jira-key mappings readable in that image. The baseline dates below remain unchanged for comparison with the recovery targets recorded in D.2.2: individual training by 20 October and completion of the Spec Kit constitution/inventory by 21 October. Long owns S2-12 and Nhật reviews it. Part C records initial setup work on 19 October; this date does not imply completion of the entire constitution and inventory task. The agreed completion sequence is Build 0 on 22 October, technical/document preparation by 23 October, Review and reconciliation on 24 October, and package preparation on 24–25 October. The reporting basis for this sequence is stated in D.2.

Long, Đạt, Nhật, Khiêm, and Hòa refer to the members in the team table. Meetings involve all five members; the assigned owner facilitates and records the minutes. Estimates below cover the owner's task execution; peer review and other members' meeting attendance are budgeted separately.

| Local ID | Task | Assignee | Reviewer | Planned start | Due date | Owner effort (h) |
|----------|------|----------|----------|---------------|----------|------------------|
| S2-01 | Sprint 2 Planning and meeting notes | Long | Hòa | 2026-10-10 | 2026-10-10 | 1.5 |
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
| S2-12 | Initialize Spec Kit in `/src`; complete constitution and artifact inventory | Long | Nhật | 2026-10-13 | 2026-10-15 | 3 |
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
| S2-24 | Consolidate AI usage records and prompt histories | Hòa | Long | 2026-10-21 | 2026-10-23 | 2 |
| S2-25 | Jira/Git evidence, Markdown/PDF visual check, final submission package | Khiêm | Long | 2026-10-24 | 2026-10-25 | 3 |
| S2-26 | Produce Build 0 and record environment smoke-test results | Khiêm | Đạt | 2026-10-21 | 2026-10-22 | 2 |
| S2-27 | Cross-check report against PA2, resolve reviews, and reconcile Jira | Đạt | Hòa | 2026-10-23 | 2026-10-24 | 2 |

**Capacity allocation**

| Member | Owned tasks (h) | Peer review (h) | Additional meeting attendance (h) | Planned total (h) |
|--------|-----------------|-----------------|----------------------------------|-------------------|
| Long | 15 | 3 | 0 | 18 |
| Đạt | 15 | 2.5 | 3.5 | 21 |
| Nhật | 14 | 3 | 3.5 | 20.5 |
| Khiêm | 12.5 | 2.5 | 3.5 | 18.5 |
| Hòa | 15 | 2.5 | 3.5 | 21 |

The plan allocates **99 team hours** from approximately **114 available hours**, leaving about **15 hours** for corrections, environment problems, and coordination. Long's meeting tasks already include his attendance and minutes preparation; the other four members each reserve 3.5 hours for attendance.

**Dependencies and checkpoints:** repository checks finish by 11 October and individual training by 12 October, before Spec Kit setup on 13–15 October. Feature/accounting descriptions precede the 18–19 October workflows and NFRs. Mobile, backend, and AI skeletons are due by 20 October, before Build 0 on 21–22 October. The 23–24 October review window absorbs corrections, and packaging finishes on 25 October.

**Definition of Done:** the output exists, meets its acceptance criteria, has a different member's recorded review, and has accurate actual dates/status in Jira. A training task includes individual learning evidence; a meeting task includes attendance, updates, decisions, and actions; a build task includes reproducible startup steps and smoke-test results. The single Jira board screenshot in Section D.2.5 summarizes task assignments and progress. Completion is confirmed through the task output and recorded review.

#### 4.4 Build Plan

The final sprint (PA5) includes a demo of all features, so builds are planned to deliver features gradually.

| Build | Target date | Planned content | Acceptance focus |
|-------|-------------|-----------------|------------------|
| Build 0 — skeleton | 2026-10-22 | Runnable mobile/backend/database/AI skeletons and startup instructions | Environment reproduction, service connectivity, failure handling, and recorded smoke tests |
| Build 1 | 2026-11-08 | Auth, Profile, wallets, offline personal transactions and synchronization; AI/admin prototypes | Access control, restart/retry safety, conflict handling, and core transaction flows |
| Build 2 | 2026-11-22 | Groups/shared fund, splits/refunds, confirmed settlements, VND/USD conversion, budgets, savings, reports | Exact financial totals, historical rates, group permissions, and connected/offline boundaries |
| Build 3 — release candidate | 2026-11-29 | All ten functional groups and AI Advisor, complete admin/rate tools, PDF/Excel export | End-to-end feature coverage, security/performance tests, and 24-hour offline rehearsal |
| Final build | 2026-12-04 | Reviewed fixes to the release candidate | Regression closure, complete runbook, final data/evidence, and demo rehearsal |

**Five testable builds** are planned. Each build is associated with a source revision/tag, installation/startup steps, test outcomes, and known issues. Build 3 delivers all planned feature groups before final regression; the final sprint's internal demo target is **6 December 2026**, with 5–6 December reserved for rehearsal and presentation. Dates are adjusted if official PA3–PA5 deadlines require it.

#### 4.5 Plan for Later Sprints (planned tasks only)

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

## Part B — Vision Document

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Nguyen Le Duc Nhat`*

### 1. Introduction

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Nguyen Le Duc Nhat`*

#### 1.1 Purpose

This document describes the vision for **FinSync**: the problem it solves, the people who will use it, its main features, and the quality requirements it must meet. It provides a common reference for the team, lecturer, and TAs when prioritizing work and evaluating the final product. The PA2 vision includes offline personal transaction management, VND/USD conversion, and explicit rules for shared expenses and settlement. This initial version will be revised in PA3-2026 based on TA feedback.

#### 1.2 References

| Reference | Document and use |
|-----------|------------------|
| R1 | [PA1 Project Proposal — Part B](../PA1/report.md#b---project-proposal): original product scope, target users, and feature baseline |
| R2 | [PA1 Existing App Survey — Part C](../PA1/report.md#c---existing-app-survey): unchanged Money Lover and Splitwise findings and screenshots |
| R3 | [PA1 Team Contract — Part D](../PA1/report.md#d---team-contract): team roles and working agreements |
| R4 | [PA2 Assignment Specification](pa2_project_assignment_specification.md): required Project Plan, Vision Document, and submission format |
| R5 | [FinSync Project Context](../../GEMINI.md): system boundaries, technologies, and business domains |
| R6 | Part A of this report: sprint schedule, responsibilities, dependencies, and build plan |

### 2. Positioning

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

#### 2.1 Problem Statement

| | |
|---|---|
| **The problem of** | In the team's PA1 survey, personal tracking and group splitting were handled by separate tools. Users must combine the information by hand, and often forget small debts. |
| **Affects** | Students, young professionals, and small groups (roommates, travel friends, project teams) who spend money together and also want to control their own spending. |
| **The impact of which is** | Time wasted on manual calculation, unclear "who owes whom", conflicts between friends, an incomplete view of personal spending, and overspending without early warning. |
| **A successful solution would be** | One app that combines personal wallets and budgets with group expense splitting and debt optimization, and gives clear AI-based advice on how to spend in the next month. |

#### 2.2 Product Position Statement

| | |
|---|---|
| **For** | Students and young professionals who manage personal money and share costs with others |
| **Who** | Need to track personal spending, split shared bills fairly, and plan their monthly budget |
| **The product name** | FinSync |
| **That** | Combines personal finance tracking, smart group expense splitting with debt optimization, and an AI Financial Advisor in one mobile app |
| **Unlike** | Using separate personal-finance and group-splitting tools, represented by Money Lover and Splitwise in the PA1 survey |
| **Our product** | Unifies personal and group finance in one place, reduces the number of transfers needed to settle debts, and turns spending history into clear monthly advice — without touching the user's real bank accounts |

### 3. Stakeholder and User Descriptions

> *Performed by: `Ngo Thai Hoa` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Ngo Thai Hoa`*

#### 3.1 Stakeholder Summary

| Stakeholder | Description | Main Interest |
|-------------|-------------|---------------|
| Regular Users | Students and young professionals using the app | Easy tracking, fair splitting, useful advice |
| System Administrator | Person managing users and default data | Simple tools to manage accounts and categories |
| Development Team (Group 04) | Five students building the system | Deliver a working product on time with good quality |
| Lecturer and TAs | Course staff who grade and give feedback | Correct process, documents, and working software |
| Third-party services (Gemini/OpenAI, cloud hosting) | Providers of AI and hosting | Fair use within limits and terms |

#### 3.2 User Summary

| User | Description | Responsibilities in the System |
|------|-------------|--------------------------------|
| **Regular User** | Individual who tracks money and joins expense groups | Manage wallets, transactions, budgets, goals; create or join groups; confirm payments |
| ↳ Group Owner | Regular User who creates and manages a group | Invite members, manage group settings and group wallet |
| ↳ Group Member | Regular User who joins a group | Add shared expenses, view debts, mark payments |
| **Administrator** | Manages the system through a simple web page | View users, lock/unlock accounts, manage default categories, view statistics |

#### 3.3 User Environment

- **Regular Users** use an Android smartphone, as selected in PA1, on mobile data or Wi-Fi, often in short sessions for recording an expense. iOS is outside the current baseline unless a scope change is approved.
- **Offline support is included.** After an initial online sign-in and synchronization, users can view cached wallets, transactions, budgets, goals, and reports, and create, edit, or delete personal transactions without a connection. The app stores these changes locally, shows their pending status, and synchronizes them when connectivity and a valid session are available.
- **Shared operations require a connection.** Group creation/invitations, shared expense changes, settlement confirmation, new AI analysis, and administration use the latest server state. Cached group balances and previous AI advice remain readable offline with their last-sync time; the app explains when an action requires a connection.
- Receipt images captured offline remain on the device and upload after reconnection. Updates to the same personal record on two devices are shown for resolution rather than silently overwriting either version.
- **Administrators** use a desktop web browser (Chrome, Edge, Firefox).
- **Currencies and language:** VND and USD are supported with bidirectional conversion; Vietnamese and English are available in the user interface. A user's selected reporting currency determines converted summaries, while every record retains its original currency and amount.

#### 3.4 Summary of Key Stakeholder or User Needs

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

#### 3.5 Alternatives and Competition

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

### 4. Product Overview

> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*

#### 4.1 Product Perspective

FinSync is a new, self-contained product made of four parts that communicate through REST APIs:

```mermaid
%%{init: {"look": "classic", "themeVariables": {"fontSize": "13px"}, "flowchart": {"rankSpacing": 18, "nodeSpacing": 20, "padding": 6, "wrappingWidth": 240}}}%%
flowchart TB
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

#### 4.2 Assumptions and Dependencies
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

### 5. Product Features

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Nguyen Le Duc Nhat`*

#### 5.1 Authentication & Security

This feature lets users register, log in, log out, and recover a forgotten password through an expiring reset link. Protected online requests use validated JWT access tokens, while offline access is limited to locally stored data of the previously signed-in user. Access control separates personal records, group membership, and administrator functions because the app stores sensitive financial information. Regular Users and Administrators benefit from a consistent account lifecycle and protection against unauthorized access.

#### 5.2 Profile Management

Users can edit their name and avatar, change their password, select Vietnamese or English, and choose VND or USD as their reporting currency. Reports show converted totals with exchange-rate details while retaining the original currency on each financial entry. These preferences make the app understandable to local users and let group members recognize one another. Regular Users benefit from consistent identity and financial presentation across personal and group screens.

#### 5.3 Wallet & Personal Account Management

Users create manual cash, bank-account, and credit-card wallets, each with a recorded currency and opening balance. The dashboard shows individual balances and a combined estimate in the selected reporting currency using an identified reference rate. Cached balances remain readable offline, and pending personal transactions update the local view until synchronization. This helps Regular Users understand their recorded assets without connecting to a real bank account.

#### 5.4 Personal Transaction Tracking

Users record income and expenses with amount, category, wallet, note, date, and an optional receipt image. They can create, edit, or delete personal transactions offline, with changes and receipt uploads queued until reconnection. Stable record identifiers prevent duplicates, while conflicting edits remain visible for resolution rather than being silently overwritten. Regular Users benefit from reliable capture at the time of payment and a transaction history that supports budgets, reports, and AI analysis.

#### 5.5 Group Management & Shared Wallets

Users create groups for shared activities, invite members by link or code, and use Owner and Member roles to control group access. A group records its currency, shared-wallet contributions, expenses, and member balances separately from private wallets. Group changes require a connection so all members work from the same confirmed ledger, while previously synchronized information remains readable offline. Friends, roommates, and teammates benefit from a transparent record of who contributed, who spent, and who should be reimbursed.

#### 5.6 Smart Debt Split & Settlement

Shared expenses are divided equally, by percentage, or by exact amount after any required conversion into the group's currency. The app shows confirmed debts and suggests fewer repayment transfers while preserving each member's net balance. A debtor records an external payment claim, and the recipient confirms it before the debt is reduced; partial payments and rejected claims remain traceable. Refunds and corrections reference the original expense and adjust member balances without deleting payment history. Group members benefit from consistent calculations, reminders, and an auditable settlement process.

#### 5.7 Budget Planning & Alerting

Users set weekly or monthly limits for personal spending categories in their selected budget currency. Spending includes their own group-expense shares once, using the relevant recorded conversion rates. Visual warnings appear when a category reaches 80% and 100% of its limit, including for locally recorded offline expenses. Regular Users benefit from timely feedback instead of discovering overspending only at the end of the month.

#### 5.8 Financial Reporting & Analytics

The app presents personal income and spending in pie and bar charts and provides group summaries for trips or recurring shared costs. Reports separate consumption from transfers, contributions, and debt settlements, and display the reporting currency and exchange-rate basis. Authorized users can export PDF or Excel reports online, while cached summaries remain readable offline with their last-sync time. Individuals benefit from understanding spending patterns, and groups benefit from clear records they can review together.

#### 5.9 Savings Goal Management

Users create savings goals with a target amount, currency, deadline, and recorded contributions or withdrawals. The app shows progress and the remaining amount, using recorded conversion rates when a contribution has another currency. A savings allocation moves recorded funds toward a goal without creating new income or counting the same expense twice. Students and young professionals benefit from a measurable saving plan and can view previously synchronized progress offline.

#### 5.10 System Administration (Admin Panel)

Administrators use an online web panel to list users, lock or unlock accounts, maintain default categories and reference exchange rates, and view aggregate system statistics. Changes to rates retain their source, effective time, and author so converted amounts can be explained. Administrative access does not grant routine access to private transaction histories or permission to confirm another member's settlement. Administrators benefit from consistent maintenance tools, and Regular Users benefit from traceable reference data and clear privacy boundaries.

#### 5.11 AI Financial Advisor

At the start of a month or on request, the AI analyzes the user's personal spending and attributable group shares, excluding duplicate entries and using recorded currency conversions. It compares available history with budgets, identifies unusual category increases, and suggests a practical allocation for the next month and savings goals. New analysis requires a connection, explains limited history when applicable, and leaves existing finance functions available if the provider fails. Regular Users can review previously saved advice offline and choose whether to apply a suggestion, with no automatic change to budgets or financial records.

#### 5.12 Key Workflow Diagrams

**Workflow 1 — Record a shared expense and settle debts**

```mermaid
%%{init: {"look": "classic", "themeVariables": {"fontSize": "13px"}, "flowchart": {"rankSpacing": 18, "nodeSpacing": 18, "padding": 6, "wrappingWidth": 280}}}%%
flowchart TD
    N{"Online?"}
    N -->|No| O(["View cached balances and sync time<br/>Reconnect to change group records"])
    N -->|Yes| B["Add expense, payer and participants<br/>Convert if needed; save rate snapshot<br/>Calculate equal, percentage or exact shares"]
    B --> V{"Valid shares<br/>and exact total?"}
    V -->|No| B
    V -->|Yes| E["Save expense and confirmed balances<br/>Show simplified debts and notify members"]
    E --> F["Debtor pays outside the app"]
    F --> P["Record a pending settlement claim"]
    P --> I{"Recipient<br/>confirms receipt?"}
    I -->|No| R["Keep debt unchanged<br/>Resolve payment details"]
    R --> P
    I -->|Yes| J["Validate amount<br/>Post settlement once"]
    J --> K{"Any selected<br/>debts remaining?"}
    K -->|Yes| F
    K -->|No| L(["Settlement complete<br/>Retain payment history"])
```

**Workflow 2 — Monthly AI Financial Advisor**

```mermaid
%%{init: {"look": "classic", "themeVariables": {"fontSize": "13px", "actorFontSize": "13px", "signalFontSize": "13px", "noteFontSize": "13px"}, "sequence": {"wrap": false, "mirrorActors": false, "width": 90, "height": 40, "actorMargin": 20, "diagramMarginX": 10, "messageFontSize": 13, "actorFontSize": 13, "noteFontSize": 13, "messageMargin": 22, "boxMargin": 4}}}%%
sequenceDiagram
    actor U as Regular User
    participant M as Mobile App
    participant B as Backend API
    participant AI as AI Microservice
    participant G as Gemini/OpenAI

    U->>M: Open AI Advisor<br/>(or monthly trigger)
    M->>B: Request monthly<br/>analysis
    B->>B: Authorize user and<br/>collect own data<br/>plus own group shares<br/>without duplicates#59;<br/>use recorded rates
    B->>AI: Send necessary totals,<br/>budgets and savings<br/>goal progress
    AI->>G: Prompt with<br/>spending data
    alt Valid provider response
        G-->>AI: Analysis and<br/>suggestions
        AI->>AI: Validate structured<br/>response
        AI-->>B: Analysis result
        B-->>M: Anomalies, budget<br/>suggestions, goal advice
        M-->>U: Show advice for review
    else Timeout, unavailable,<br/>or invalid output
        AI-->>B: Controlled error
        B-->>M: Analysis unavailable#59;<br/>retry option
        M-->>U: Keep normal finance<br/>functions available
    end
```

Workflow 1 requires an online ledger and confirmation by the payment recipient before a debt changes. Workflow 2 operates on the signed-in user's synchronized data and recorded conversion rates; offline users can read cached advice and personal records. Both workflows preserve the separation between external payments and in-app bookkeeping.

### 6. Non-Functional Requirements

> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

The following requirements define acceptance criteria for implementation and final testing. Performance tests use a Linux deployment with at least **2 vCPUs and 4 GiB RAM**, a seeded database of **100 users with 100 personal transactions each**, and group test cases of up to **20 members and 500 expenses per group**. Mobile timing tests use an Android device with **4 GiB RAM**, Android 10 or later, and a stable connection of at least **10 Mbps** with round-trip latency no greater than **100 ms**. Minimum-platform compatibility is checked separately on Android 8.0; every test report records the actual hardware, versions, dataset, and build revision.

#### 6.1 Performance

| ID | Requirement |
|----|-------------|
| <span style="white-space: nowrap;">NFR-P1</span> | At least 95% of non-AI API requests complete in **less than 500 ms** during the NFR-P3 load test. |
| <span style="white-space: nowrap;">NFR-P2</span> | The dashboard and first page of up to 50 transactions appear in **less than 2 seconds** in at least **19 of 20** warm-load trials on the specified mobile test environment. Cached offline lists meet the same target. |
| <span style="white-space: nowrap;">NFR-P3</span> | Support **100 concurrent authenticated users for 10 minutes**, each making one non-AI request every two seconds, with an unexpected-error rate **below 1%**. The request mix is 70% reads and 30% personal transaction writes; expected validation and authorization rejections are reported separately. |
| <span style="white-space: nowrap;">NFR-P4</span> | An online AI request produces advice or a controlled failure response within **30 seconds**. The interface shows progress and a retry option; successful and failed analysis counts are reported separately. |
| <span style="white-space: nowrap;">NFR-P5</span> | Recalculate group balances and simplified repayment suggestions for **20 members and 500 expenses in less than 2 seconds**, preserving each member's net position. |
| <span style="white-space: nowrap;">NFR-P6</span> | With the app open, valid authorization, and the specified connection, synchronize **100 queued personal transaction changes within 30 seconds**, excluding receipt uploads and edits awaiting conflict resolution. |

#### 6.2 Security

| ID | Requirement |
|----|-------------|
| <span style="white-space: nowrap;">NFR-S1</span> | Store passwords only as salted BCrypt hashes; no plaintext or recoverable password is stored or logged. |
| <span style="white-space: nowrap;">NFR-S2</span> | Use **HTTPS with TLS 1.2 or later** for deployed client/API and external-service communication. Local development uses isolated test data. |
| <span style="white-space: nowrap;">NFR-S3</span> | Access tokens expire after **60 minutes**; refresh sessions expire after **7 days**. Protected requests reject invalid or expired tokens. Logout removes local tokens and private cached data, revokes the refresh session, and invalidates its active access token on the server. Synchronization after session expiry requires reauthentication. |
| <span style="white-space: nowrap;">NFR-S4</span> | All private-record, group-membership, administrator-role, and settlement-recipient authorization checks pass the agreed negative-test suite. A Group Owner cannot confirm a payment intended for another member. |
| <span style="white-space: nowrap;">NFR-S5</span> | Keep API keys, passwords, signing secrets, and real environment files outside tracked source and submission files. Application logs and AI requests must omit credentials and access tokens. |
| <span style="white-space: nowrap;">NFR-S6</span> | Validate amounts, currencies, split totals, uploaded file types, and user input on the server. Use parameterized database access and safe UI rendering; all invalid-input and script-injection cases in the security suite must pass. |
| <span style="white-space: nowrap;">NFR-S7</span> | Send the AI provider only the signed-in user's necessary category totals, budget limits, own group shares, and goal progress. Exclude other members' private records, names, emails, phone numbers, raw receipts, and authentication data. |
| <span style="white-space: nowrap;">NFR-S8</span> | Encrypt private offline records and receipt files at rest with keys protected by the Android system keystore. Cached data belongs to one signed-in account, requires device/app unlock to access, and is cleared on explicit logout or account removal from the device. |
| <span style="white-space: nowrap;">NFR-S9</span> | Password-reset links expire after **15 minutes**, can be used only once, and do not expose whether an unknown email address has an account. |

#### 6.3 Reliability, Offline Operation, and Financial Correctness

| ID | Requirement |
|----|-------------|
| <span style="white-space: nowrap;">NFR-R1</span> | The demo environment passes **at least 95%** of one-minute health probes during a **two-hour rehearsal** before the demo. |
| <span style="white-space: nowrap;">NFR-R2</span> | Back up hosted project data **daily**, retain the latest **7 daily backups**, and demonstrate restoration to a separate test database within **30 minutes** before the final demo. The QA/DevOps Lead owns backup and restore verification. |
| <span style="white-space: nowrap;">NFR-R3</span> | Use exact decimal arithmetic with whole-dong VND and cent-precision USD. In every split/refund test, allocated shares equal the recorded amount exactly, and every group debt/fund journal balances to zero. Stable remainder allocation produces the same result on repeated calculation. |
| <span style="white-space: nowrap;">NFR-R4</span> | During an AI-provider outage or invalid response, personal tracking, group bookkeeping, and cached advice remain available within their normal connectivity limits; no financial record or budget is changed by a failed AI request. |
| <span style="white-space: nowrap;">NFR-R5</span> | After initial synchronization, users can create, edit, and delete personal transactions for cached wallets with **no network connection**. A **24-hour offline test with 100 local changes**, including app restart, loses no acknowledged local change and retains pending status. |
| <span style="white-space: nowrap;">NFR-R6</span> | Replaying each queued change **three times** creates no duplicate financial effect. Conflicting edits from two devices are detected, both versions are retained, and the user resolves the conflict before that record is marked synchronized. |
| <span style="white-space: nowrap;">NFR-R7</span> | A payment claim remains pending until the intended recipient confirms. Rejection, repeated confirmation, server failure, and attempts to exceed the outstanding debt produce **no incorrect balance change** in the settlement test suite; dependent ledger updates commit together or roll back together. |
| <span style="white-space: nowrap;">NFR-R8</span> | Every converted entry stores its original amount/currency and applied rate/source/time. Changing the reference rate or reporting preference does not alter existing group shares, original amounts, or confirmed payments. Missing rates never produce an invented converted total. |

#### 6.4 Usability and Accessibility

| ID | Requirement |
|----|-------------|
| <span style="white-space: nowrap;">NFR-U1</span> | At least **4 of 5** first-time test participants record their first personal transaction in **3 minutes or less** without assistance, starting from an authenticated account with a wallet. |
| <span style="white-space: nowrap;">NFR-U2</span> | At least **4 of 5** participants save a basic expense in **30 seconds or less** from the add-transaction screen, excluding receipt capture/upload. Use the same task script online and offline and record results separately. |
| <span style="white-space: nowrap;">NFR-U3</span> | All primary screens use consistent labels and components; text supports Android font scaling to **150%** without hiding required actions, and primary touch targets are at least **48 dp**. |
| <span style="white-space: nowrap;">NFR-U4</span> | Provide Vietnamese and English labels for all primary flows, VND/USD formatting, original and converted amounts, and the applied rate's effective date. Amounts are always labelled with their currency. |
| <span style="white-space: nowrap;">NFR-U5</span> | Offline, pending synchronization, conflict, and pending/confirmed settlement states are distinguishable using text or icons as well as color. Shared-data views show their last successful synchronization time. |
| <span style="white-space: nowrap;">NFR-U6</span> | Budget warnings are updated when a relevant local or synchronized expense causes spending to cross **80% or 100%** of its limit; offline warnings identify that group data may be stale. |

#### 6.5 Platform and Compatibility

| ID | Requirement |
|----|-------------|
| <span style="white-space: nowrap;">NFR-C1</span> | The Kotlin/Jetpack Compose mobile app installs and supports the core personal and group flows on **Android 8.0, API 26, or later**. Compatibility is checked on API 26 and an Android 10-or-later device/emulator. |
| <span style="white-space: nowrap;">NFR-C2</span> | The online admin panel supports the **two most recent stable desktop versions** of Chrome, Edge, and Firefox available at final acceptance; record the tested version numbers. |
| <span style="white-space: nowrap;">NFR-C3</span> | The server targets **Java 21 and PostgreSQL 16**. Backend, database, AI service, and admin deployment are reproducible on a Docker-capable Linux host using documented Docker Compose commands and external configuration. |
| <span style="white-space: nowrap;">NFR-C4</span> | Receipt capture works without a connection and uploads later. Online recovery, new AI requests, group writes, and settlement confirmation clearly indicate their network requirement; viewing cached data remains available. |

#### 6.6 Maintainability and Quality

| ID | Requirement |
|----|-------------|
| <span style="white-space: nowrap;">NFR-M1</span> | Automated tests achieve **at least 70% line coverage** of backend services implementing splitting, settlement, refunds, currency conversion, and budget calculations. Coverage is measured against the reviewed build. |
| <span style="white-space: nowrap;">NFR-M2</span> | Follow Java naming conventions, the Kotlin Style Guide, PEP 8, and the agreed React conventions. Another member reviews each change before merge; review findings are resolved or explicitly accepted. |
| <span style="white-space: nowrap;">NFR-M3</span> | Document all public REST endpoints, authorization rules, request/response schemas, and error responses in OpenAPI. Document synchronization identifiers and conflict responses. |
| <span style="white-space: nowrap;">NFR-M4</span> | Apply server database changes through versioned Flyway migrations and verify both a clean setup and upgrade from the previous test build. |
| <span style="white-space: nowrap;">NFR-M5</span> | Use Conventional Commits and retain traceability from a Jira task to its implementation/document output and review record. |
| <span style="white-space: nowrap;">NFR-M6</span> | Automated financial tests include equal/percentage/exact splits, rounding remainders, partial/rejected settlements, refunds after settlement, duplicate retries, offline conflicts, and VND/USD conversion using fixed test rates. |

#### 6.7 Standards and Verification

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

## Part C — Spec Kit Initialization

> *Performed by: `Phạm Định Tiểu Long` | Reviewed by: `Nguyễn Lê Đức Nhật` | Edited by: `Phạm Định Tiểu Long`*

### 1. Repository and Spec Kit Setup

> *Performed by: `Phạm Định Tiểu Long` | Reviewed by: `Nguyễn Lê Đức Nhật` | Edited by: `Phạm Định Tiểu Long`*

| Item | Value |
|------|-------|
| GitHub repository | [pdtLong2929/FinSync](https://github.com/pdtLong2929/FinSync), recorded in PA1; `verified repository access for TA` |
| Spec Kit location | `/src` |
| Initialization status | Spec Kit files are present under `/src` in `b98938f`; the generated constitution still contains unfilled project/principle placeholders |
| AI agent used with Spec Kit | `Gemini CLI` |
| Spec Kit version | `1.2.3.dev0` |
| Initial setup date reported by the team | `19/10/2026`; full S2-12 acceptance also requires the completed constitution and artifact inventory |
| Environment | Arch Linux, Node.js/npm, Python, uv/uvx |
| Setup performer / reviewer | `Phạm Định Tiểu Long` / `Nguyễn Lê Đức Nhật` |
| Earlier setup commit | `6c5cd20` ? add: new speckit |
| Initialization artifact evidence | `b98938f` ? chore: initialize Spec Kit in src |

**Team-reported setup commands**

```text
sudo pacman -S uv
uvx --from git+https://github.com/github/spec-kit.git specify init --here
mkdir -p src
mv constitution.md src/
git checkout -b speckit
git add src/constitution.md .specify/
git commit -m "add: new speckit"
git push -u origin speckit
```

### 2. Submitted Artifacts

> *Performed by: `Phạm Định Tiểu Long` | Reviewed by: `Nguyễn Lê Đức Nhật` | Edited by: `Phạm Định Tiểu Long`*

| Artifact | Repository path | Evidence |
|----------|-----------------|----------|
| speckit-analyze instructions | `src/.github/skills/speckit-analyze/SKILL.md` | `b98938f` |
| speckit-checklist instructions | `src/.github/skills/speckit-checklist/SKILL.md` | `b98938f` |
| speckit-clarify instructions | `src/.github/skills/speckit-clarify/SKILL.md` | `b98938f` |
| speckit-constitution instructions | `src/.github/skills/speckit-constitution/SKILL.md` | `b98938f` |
| speckit-converge instructions | `src/.github/skills/speckit-converge/SKILL.md` | `b98938f` |
| speckit-implement instructions | `src/.github/skills/speckit-implement/SKILL.md` | `b98938f` |
| speckit-plan instructions | `src/.github/skills/speckit-plan/SKILL.md` | `b98938f` |
| speckit-specify instructions | `src/.github/skills/speckit-specify/SKILL.md` | `b98938f` |
| speckit-tasks instructions | `src/.github/skills/speckit-tasks/SKILL.md` | `b98938f` |
| speckit-taskstoissues instructions | `src/.github/skills/speckit-taskstoissues/SKILL.md` | `b98938f` |
| constitution.md | `src/.specify/memory/constitution.md` | `b98938f`; uncustomized template |
| checklist-template.md | `src/.specify/templates/checklist-template.md` | `b98938f` |
| constitution-template.md | `src/.specify/templates/constitution-template.md` | `b98938f` |
| plan-template.md | `src/.specify/templates/plan-template.md` | `b98938f` |
| spec-template.md | `src/.specify/templates/spec-template.md` | `b98938f` |
| tasks-template.md | `src/.specify/templates/tasks-template.md` | `b98938f` |

The repository contains 16 initialization Markdown files under `/src` at [commit b98938f](https://github.com/pdtLong2929/FinSync/tree/b98938f/src): a generated constitution, five templates, and ten workflow instruction files. This is the artifact inventory for the report. The earlier `6c5cd20` commit contains only an empty `src/constitution.md`; it does not represent this initialization output.

### 3. Constitution Summary

> *Performed by: `Phạm Định Tiểu Long` | Reviewed by: `Nguyễn Lê Đức Nhật` | Edited by: `Phạm Định Tiểu Long`*

The following principles define the intended FinSync constitution. The generated file at `src/.specify/memory/constitution.md` in `b98938f` still contains project-name, principle, and governance placeholders; these principles have not yet been incorporated into that artifact.

1. **Offline-First & Data Privacy:** Supports offline personal transaction entry and cached read-only group access. Group ledger changes and settlement confirmation require an online server connection, consistent with Part B. All real payments take place outside the app; FinSync records bookkeeping entries while protecting user privacy.
2. **Financial Correctness & Precision:** Enforces exact currency representation (avoiding floating-point inaccuracies), strict split-total calculation invariants, rounding precision, and prevention of duplicate expense entries across all personal and shared group wallets.
3. **Modular Monolith Architecture & Boundaries:** Formally outlines the system boundaries separating the Android client (Kotlin/Jetpack Compose), backend modular services (Spring Boot 3 / Java 21), AI advisory microservices (FastAPI/Gemini), and administrative portals, backed by stateless JWT authentication.
4. **Test-First & Engineering Quality Gates:** Establishes rigorous development protocols including mandatory peer reviews via Pull Requests, automated containerized builds via Docker, and appropriate validation checks for financial logic.
5. **Governance:** Designates the constitution as the supreme technical authority for the project (Version 1.0.0), specifying that future amendments require formal documentation, team consensus, and structured migration planning.

| Principle | Intended FinSync content | Verification against cited artifact |
|--------------------|-------------------------------------------------------|----------------------------------|
| Business boundaries | Manual bookkeeping only; personal/group privacy; real payments remain external | Not incorporated into the generated constitution |
| Financial correctness | Exact money representation, split-total invariants, rounding, and no duplicate expense counting | Not incorporated into the generated constitution |
| Architecture and security | Agreed client/backend/AI/admin boundaries; access control and externalized secrets | Not incorporated into the generated constitution |
| Quality and delivery | Peer review, appropriate tests, documented APIs/migrations, reproducible builds | Not incorporated into the generated constitution |
| Governance | Version, adoption/amendment dates, and how the team approves changes | Not incorporated into the generated constitution |

### 4. Spec Kit Training Evidence

> *Performed by: `Phạm Định Tiểu Long` | Reviewed by: `Nguyễn Lê Đức Nhật` | Edited by: `Phạm Định Tiểu Long`*

**Shared training evidence:** [Spec Kit training records for all five members](https://drive.google.com/drive/folders/1kren79mDeP6oG1WoA7sGW7CYb0sZIgS1?usp=drive_link).

**Tutorial reference:** [GitHub Spec Kit tutorial](https://youtu.be/61K-2VRaC6s?si=hACZPoR6ZPlSJ23R). The table records the team's reported completion dates, individual learning summaries and reviewers. The evidence for all five members is stored in the shared folder above.

| Member | Completed / Date | Individual Learning Summary | Reviewer / Date |
|--------|------------------|-----------------------------|-----------------|
| Phạm Định Tiểu Long | `19/10/2026` | Completed tutorials #1-#7. Practiced end-to-end Spec Kit workflow and repo init. Key lesson: strict rules in constitution.md effectively prevent AI architectural drift. | Hòa / `20/10/2026`|
| Nguyễn Phú Đạt | `19/10/2026` | Learned how the `/clarify` command identifies edge cases before planning and reduces rework. | Long / `20/10/2026`|
| Nguyễn Lê Đức Nhật | `20/10/2026` | Gained hands-on experience turning user ideas into concrete specs using /specify; learned that thorough acceptance criteria make the final task checklist much easier to execute. | Đạt / `20/10/2026`|
| Vương Đắc Gia Khiêm | `20/10/2026` | Learned to use the interactive `/clarify` command to refine requirements before code generation and align them with the project constitution. | Nhật / `20/10/2026`|
| Ngô Thái Hòa | `20/10/2026` | Realized that spec-driven development eliminates guesswork and trial-and-error by enforcing clear structure right from workspace initialization. | Khiêm / `20/10/2026`|

All members have completed the entire tutorial series (Parts 1 to 7) to gain an end-to-end understanding of GitHub Spec Kit. To thoroughly assess individual comprehension and ensure mastery of each stage, specific modules were assigned to each member for in-depth verification:

- **Long:** Part 2 – Constitution File (`constitution.md` principles & governance)
- **Nhật:** Part 3 – The `/specify` Command (Feature specification & PRD generation)
- **Hòa:** Part 4 – The `/clarify` Command (Resolving ambiguities & handling edge cases)
- **Khiêm:** Part 5 – The `/plan` Command (Technical architecture & implementation planning)
- **Đạt:** Part 6 – The `/tasks` Command (Task breakdown & execution tracking)

---


## Part D — AI Usage Report and Weekly Report

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Nguyen Le Duc Nhat`*

### 1. AI Usage Report

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Nguyen Phu Dat` | Edited by: `Nguyen Le Duc Nhat`*

**Declaration:** Team members report using AI tools including Codex, ChatGPT, Claude, Gemini, and Cursor during PA2. Nguyễn Lê Đức Nhật declares Codex-only use. Vương Đắc Gia Khiêm's record identifies ChatGPT (GPT-6), OpenAI, for Test Strategy preparation and Spec Kit learning. Individual tool declarations and prompt entries are organized by member in the Appendix.

**Reporting scope:** Sprint 2, 10–25 October 2026. The Appendix currently contains 27 English-language prompt entries: 14 for Nhật, dated 10–23 October and labelled Codex (`gpt-6.1-sol`), and 13 for Khiêm, dated 14–20 October and labelled ChatGPT (GPT-6). They describe the requested assistance; actual outputs, adopted content, and completed independent validation are not established by the entries alone. Usage records for Long, Đạt, and Hòa remain absent.

| Purpose | AI-generated assistance | Affected sections | Human input and validation responsibility |
|---------|-------------------------|-------------------|------------------------------------------|
| Report organization and English wording | Proposed section structure, refined descriptions, and prepared document tables | Parts A–D and the Appendix | Nhật supplied the assignment, existing report, and project context; section authors check the resulting meaning and assignment coverage |
| Requirements and business rules | Drafted offline boundaries, group accounting, refunds, settlement confirmation, and VND/USD conversion rules | Part B, Sections 3–5 | Nhật confirmed offline support, the unchanged PA1 survey, adoption of the accounting rules, and currency conversion; assigned reviewers check technical accuracy |
| Sprint planning and workload | Proposed assignments, dates, dependencies, estimates, capacity allocation, and builds | Part A, Sections 4.2–4.5 | The team supplied roles and the sprint window; Long coordinates the plan and reviewers check the assigned outputs |
| Workflows and quality requirements | Drafted Mermaid workflows, measurable NFR targets, and verification methods | Part B, Sections 5.12 and 6 | Authors check workflow logic, accounting examples, and whether the proposed criteria can be tested |
| Weekly reporting and evidence | Drafted role-based meeting records, captions, and the AI disclosure | Part D and the Appendix | Nhật confirmed past attendance and the 16 October draft/training status, and selected the completion scenario described in D.2 |
| Document checking and revision | Located unfinished fields, corrected caption CSS and Mermaid syntax, reduced Jira evidence to one board image, and aligned reviewers and packaging dates | Report and PDF stylesheet | Nhật requested these corrections; document checks compare the final wording, local image paths, task baseline, and supplied evidence |
| Test Strategy and Spec Kit learning | Feedback on Test Strategy structure, a requested draft of TEST_STRATEGY.md, and explanations of testing methods, CI/CD, quality gates, and Spec Kit workflows | Test Strategy document; Part C; Appendix, Section 4 | Khiêm supplied the proposed structure and learning questions. His 13 entries identify intended independent checks; completed revisions and validation are not established by the entries alone. |

**Student contribution and validation**

Nhật selected the source documents, supplied the project decisions, identified inconsistencies, and directed revisions through the recorded prompts. AI supplied substantial draft wording and planning proposals. Document checks cover section structure, assignee/reviewer separation, date dependencies, PA1 survey consistency, image references, and the distinction between requirements and measured results. Assigned team reviewers remain responsible for recording their own review decisions against the corresponding tasks.

**Supporting records**

The Appendix groups the supplied prompt entries by member and identifies the report sections and requested AI contributions. Nhật's English prompt text is translated from the supplied Vietnamese entries. Khiêm's 13 entries cover Test Strategy drafting, testing concepts, CI/CD, quality gates, and Spec Kit workflows. Dates are retained as supplied; entries dated after the report review date are planned records until supported by the actual conversation history.

Hòa is responsible for consolidating the group disclosure under S2-24, with Long as reviewer.

### 2. Weekly Report

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Nguyen Le Duc Nhat`*  

**Project:** FinSync — Group 04.   
**Sprint:** Sprint 2 / PA2.   
**Period:** 2026-10-10 to 2026-10-25.   

**Sprint goal:** Prepare an initial Project Plan and Vision Document, initialize Spec Kit under /src, collect individual training evidence, and produce a reproducible skeleton build that supports later implementation.

The structure follows `WeeklyReport`: one Sprint Planning meeting, two Weekly Scrum meetings, and one Sprint Review/Retrospective. Both Scrum records are kept in this document. Long coordinates the meetings and minutes; all five members provide their own progress updates.

**Reporting basis:** The 10/16 October records combine team confirmations and repository evidence. The 22/24 October meeting records and associated build, acceptance, and packaging outcomes are a hypothetical completion scenario, not observed results. Part C records the team's setup/training information and the limits of the cited artifact evidence. The Jira figure retains its 19 October snapshot; the Git figures show commits through 21 October.

| Meeting | Planned date / time | Task | Minutes owner / reviewer |
|---------|---------------------|------|-------------------------|
| Sprint Planning | 2026-10-10, 09:00–10:00, before sprint work begins | S2-01 | Long / Hòa |
| Weekly Scrum #1 | 2026-10-16, 19:30–20:15 | S2-21 | Long / Hòa |
| Weekly Scrum #2 | 2026-10-22, 19:30–20:15 | S2-22 | Long / Hòa |
| Sprint Review & Retrospective | 2026-10-24, 19:30–20:30 | S2-23 | Long / Khiêm |

The table gives the scheduled meeting slots. Planning is the PA1-to-PA2 transition checkpoint on 10 October before implementation begins. Meetings use Google Meet, with follow-up coordination through Discord/Zalo.

#### 2.1 Sprint Planning Meeting

**Date / scheduled slot:** 2026-10-10, 09:00–10:00.   
**Facilitator / note taker:** Long.   
**Members present:** Phạm Định Tiểu Long, Nguyễn Phú Đạt, Nguyễn Lê Đức Nhật, Vương Đắc Gia Khiêm, and Ngô Thái Hòa.   
**Members absent:** None; full attendance confirmed by the group.  

**Prioritized scope and allocation**

| Priority | Work package | Assigned owner(s), each for their own tasks | Expected output |
|----------|--------------|---------------------------------------------|-----------------|
| 1 | Project Plan, schedule, risk management, and coordination | Long; Nhật for Introduction/Overview | Initial plan covering five sprints, task/reviewer assignments, risks, and builds |
| 1 | Vision, positioning, user needs, and product boundaries | Hòa for positioning/features; Đạt for user research; Nhật for product perspective | Refined descriptions of all ten functional groups and AI Advisor; two Mermaid workflows |
| 1 | Spec Kit setup and training evidence | Long for setup; Khiêm for evidence collection; every member for individual training | Customized constitution, generated Markdown inventory, and five individual evidence records |
| 1 | Quality targets and PA2 evidence | Khiêm for NFRs/evidence; Hòa for the AI log; Long for minutes | Measurable NFRs, AI Usage Notes, four meeting records, and Jira/Git evidence |
| 2 | Mobile/backend/AI skeletons and Build 0 | Đạt for mobile; Nhật for backend; Hòa for AI; Khiêm for integration | Reproducible startup instructions and documented smoke-test results |

Each task has one assignee and a different reviewer, as listed in [Section A.4.3](#43-sprint-2--detailed-task-list). The baseline allocates 99 team hours against approximately 114 available hours, including separate review/attendance budgets and a correction buffer.

**Planning baseline**

- Retain the Android Kotlin/Jetpack Compose baseline and the Spring Boot/PostgreSQL backend described in PA1.
- Keep personal records private and group records visible only to authorized members. Record real payments outside the app.
- Complete training by 12 October, Spec Kit setup by 15 October, feature drafts by 17 October, and skeleton work by 20 October.
- Produce Build 0 by 22 October; reserve 23–24 October for review and fixes, and 25 October for the final submission package.
- Track all writing, learning, coding, review, and evidence tasks in Jira; update actual status and dates from real work.

**Follow-up actions:** ACT-01 and ACT-02 in Section 2.7.

**Summary:** The sprint baseline prioritizes mandatory PA2 documents and training while reserving time for early technical integration. Product descriptions guide workflows and quality targets; component skeletons precede Build 0. The current scope also incorporates the agreed product decisions on offline support, recipient-confirmed settlement, and VND/USD conversion, as consolidated in Part B.

#### 2.2 Weekly Scrum Meeting #1 — 16 October 2026

**Date / scheduled slot:** 2026-10-16, 19:30–20:15.  
**Facilitator / note taker:** Phạm Định Tiểu Long.  
**Members present:** Phạm Định Tiểu Long, Nguyễn Phú Đạt, Nguyễn Lê Đức Nhật, Vương Đắc Gia Khiêm, and Ngô Thái Hòa.  
**Members absent:** None; full attendance confirmed by the group.

**Meeting focus:** Review the first documentation drafts, identify work behind the initial schedule, and prepare the mobile/backend/AI integration checkpoint.

**Confirmed progress basis:** The group confirms that initial document drafts were available, while Spec Kit initialization and the five-member training workstream were not yet complete. Draft availability is distinguished from completed peer review. Individual training completion, technical test outcomes, and actual Jira dates are supported by their respective records when supplied.

| Member | What have I done since last week? | What will I do until next week? | Issues / problems / obstacles |
|--------|-----------------------------------|--------------------------------|-------------------------------|
| Long | Initial organization, risk, schedule, and build-plan drafts are available in the shared report under S2-03/S2-04. | Complete Spec Kit setup, the constitution, and artifact inventory (S2-12); coordinate document reviews and training recovery before the next Scrum. | The training/setup workstream is behind its original early-sprint milestones; draft documents alone do not establish readiness for integration. |
| Đạt | User/stakeholder descriptions and the PA1 survey baseline are available for the vision draft under S2-06. | Complete the mobile skeleton (S2-18), finalize two workflows (S2-09), and review the integrated Build 0. | The mobile client needs agreed request/response fields, connectivity states, and currency/accounting behavior before integration. |
| Nhật | Introduction and product-perspective drafts are available; the initial PA2 report was committed on 16 October as 42b3819. | Complete the backend/database/API skeleton (S2-19), review the Spec Kit setup, and align the backend/AI contract. | Exact money representation, duplicate prevention, and group/personal boundaries require consistent treatment across components; successful integration is not yet evidenced. |
| Khiêm | Repository preparation and the Spec Kit/training workstream have been identified for technical follow-up; completed initialization/training evidence is not yet available. | Support Long on environment checks for S2-12, collect each member's training evidence, define the Build 0 checklist, and prepare NFR verification methods (S2-10). | Spec Kit/training remain incomplete against the 12/15 October baseline targets and need a recovery checkpoint. |
| Hòa | Vision introduction/positioning and product-feature descriptions are available as initial draft material under S2-05/S2-08. | Refine the features with peer feedback, complete the AI skeleton (S2-20), and consolidate AI-use evidence (S2-24). | AI-provider timeout, quota, and invalid-output handling remain integration risks to test; per-member usage histories must be collected. |

**Repository record:** Commit 8af0078 on 14 October reorganized PA1 materials and added the PA2 specification; commit 42b3819 on 16 October added the initial PA2 report. These support document preparation without establishing successful training, reviewed task closure, or a passing build.

**Follow-up reconciliation — 19 October 2026**

The following recovery targets respond to the confirmed training/setup delay. They are planning adjustments recorded during report preparation, rather than backdated decisions claimed for 16 October. Original baseline dates remain traceable in Section A.4.3 and Jira history.

| Follow-up | Task / expected output | Owner | Reviewer | Recovery target |
|-----------|------------------------|-------|----------|-----------------|
| SC1-01 | Complete Long's training and retain an individual learning/evidence record (S2-13) | Long | Hòa | 2026-10-20 |
| SC1-02 | Complete Đạt's training and retain an individual learning/evidence record (S2-14) | Đạt | Long | 2026-10-20 |
| SC1-03 | Complete Nhật's training and retain an individual learning/evidence record (S2-15) | Nhật | Đạt | 2026-10-20 |
| SC1-04 | Complete Khiêm's training and retain an individual learning/evidence record (S2-16) | Khiêm | Nhật | 2026-10-20 |
| SC1-05 | Complete Hòa's training and retain an individual learning/evidence record (S2-17) | Hòa | Khiêm | 2026-10-20 |
| SC1-06 | Complete the project constitution and verify the generated-file inventory under /src after initial setup (S2-12) | Long | Nhật | 2026-10-21 |
| SC1-07 | Coordinate outstanding document reviews and record actual progress/recovery dates in Jira (ACT-03/ACT-04) | Long | Hòa | 2026-10-21 |

**Summary:** Initial plan and vision drafts provide a basis for review, but Spec Kit/training are the confirmed schedule gap. The recovery plan prioritizes individual evidence and reproducible setup before the 22 October integration checkpoint. Scope is kept stable so the remaining days can be used for technical checks, review, and submission evidence.

#### 2.3 Weekly Scrum Meeting #2 — 22 October 2026

**Date / time:** 2026-10-22, 19:30–20:15.  
**Leader / facilitator / note taker:** Phạm Định Tiểu Long.  
**Members present:** Phạm Định Tiểu Long, Nguyễn Phú Đạt, Nguyễn Lê Đức Nhật, Vương Đắc Gia Khiêm, and Ngô Thái Hòa.  
**Members absent:** None.

**Meeting focus:** Review the recovered training/setup work, assess Build 0, and complete technical work, document corrections, and the pre-Review draft by 23 October. Final Review records, evidence updates, and submission packaging follow on 24–25 October under S2-25.

**Member updates**

| Member | What have I done since last week? | What will I do until next week? | Issues / problems / obstacles |
|--------|-----------------------------------|--------------------------------|-------------------------------|
| Long | Completed individual training, performed the initial setup reported in Part C on 19 October, and coordinated constitution/inventory follow-up and Project Plan reviews. Reconciled task owners, reviewers, and recovery dates for the final checkpoint. | Review the pre-Review draft on 23 October, facilitate and record the 24 October Review, and review the final package during 24–25 October before submission. | No blocking coordination issue remains; final captions, task history, and report dates need one consistency check. |
| Đạt | Completed the mobile skeleton and the two user workflows. Reviewed startup/connectivity with Nhật and checked that the workflows reflect the agreed offline, settlement, and currency requirements. | Complete the pre-Review cross-check and draft PDF readability check on 23 October, then reconcile Review outcomes on 24 October to close S2-27. | No blocking client issue remains within the Build 0 scope; document and PDF consistency checks are the remaining work. |
| Nhật | Completed the backend/database/API skeleton and reviewed the Spec Kit setup. Aligned request/response fields with the mobile and AI owners, including money representation and error handling. | Support the final technical cross-check, confirm that startup instructions match the reviewed revision, and prepare the Sprint 3 API backlog. | No blocking integration issue remains within the skeleton scope; later feature implementation follows the agreed contracts. |
| Khiêm | Supported environment and evidence checks for the Spec Kit workstream led by Long. Collected all five training records and coordinated the Build 0 startup/connectivity checks with the component owners. | Organize the available technical evidence for the 23 October draft, then finalize Jira/Git evidence and verify the Markdown/PDF/archive on 24–25 October under S2-25. | The earlier training/setup delay has been recovered; final evidence captions and package verification follow the Review. |
| Hòa | Completed the feature refinements and AI-service skeleton. Aligned the AI contract with Nhật and consolidated the available AI records for Nhật and Khiêm. | Consolidate the shared disclosure and the records for Nhật and Khiêm on 23 October, assign the remaining individual records to their owners, and review the report with Đạt. | No blocking AI-service issue remains within the skeleton scope. Long, Đạt, and Hòa retain responsibility for supplying their individual AI histories. |

**Checkpoint decisions**

- Part C reports training completion on 19 October for Long and Đạt and on 20 October for Nhật, Khiêm, and Hòa. Long is the S2-12 owner in A.4.3 and C.1, with Nhật as reviewer. Initial setup is reported on 19 October; 21 October is the recovery target for the completed constitution and inventory. The generated artifacts are present in `b98938f`; the project constitution is still an uncustomized template.
- Build 0 was accepted within the agreed skeleton scenario: the mobile client starts, the backend connects to PostgreSQL, the AI service responds through the agreed interface, and provider failure returns a controlled message. These scenario outcomes are not measured test results.
- The team agreed that the Project Plan, Vision, workflows, and NFRs use the same offline, group-accounting, settlement, and VND/USD rules.
- The sprint scope remained stable. Technical work, document corrections, AI disclosure consolidation, and the pre-Review draft were targeted for completion by 23 October. Review minutes and final reconciliation follow on 24 October; S2-25 covers final evidence, PDF/archive checks, and packaging on 24–25 October.
- No blocking technical issue remained within the skeleton scope. Long coordinated the 23 October readiness checkpoint with the component owners; the AI histories for Long, Đạt, and Hòa remain outstanding.

**Final preparation and submission actions**

| Action | Owner | Reviewer | Completion target | Expected output |
|--------|-------|----------|-------------------|-----------------|
| Record Build 0 acceptance within the agreed scenario (ACT-05) | Khiêm | Đạt | 2026-10-22 | Mobile startup, backend/database connectivity, AI response, and controlled failure behavior |
| Consolidate the shared AI declaration and the histories of Nhật and Khiêm (ACT-06 / S2-24) | Hòa | Long | 2026-10-23 | Include 14 entries from Nhật and 13 from Khiêm; Long, Đạt, and Hòa supply their own histories |
| Finish the report/Jira cross-check and PDF readability review (S2-27) | Đạt | Hòa | 2026-10-24 | Pre-Review checks on 23 October, followed by reconciliation of Review outcomes on 24 October |
| Finalize evidence and the Markdown/PDF/archive package (S2-25) | Khiêm | Long | 2026-10-25 | Work on 24–25 October: add Review records and final evidence, regenerate the PDF, and verify the archive |

**Summary:** Training and setup have recovered, and Build 0 meets its initial integration scope. Technical work, document corrections, and the pre-Review draft are ready by 23 October. The team reviews these outputs on 24 October and completes final records, evidence updates, and packaging by 25 October.

#### 2.4 Sprint Review & Retrospective — 24 October 2026

**Date / time:** 2026-10-24, 19:30–20:30.  
**Leader / facilitator / note taker:** Phạm Định Tiểu Long.  
**Members present:** Phạm Định Tiểu Long, Nguyễn Phú Đạt, Nguyễn Lê Đức Nhật, Vương Đắc Gia Khiêm, and Ngô Thái Hòa.  
**Members absent:** None.

**Review goal:** Evaluate the completed PA2 outputs against the sprint goal, confirm submission readiness, and agree on practical improvements for Sprint 3.

**Review agenda**

| Time | Activity | Presenter / lead |
|------|----------|------------------|
| 19:30–19:40 | Present the Project Plan, Vision, workflows, and task consistency checks | Long, Hòa, Đạt, and Nhật |
| 19:40–19:55 | Demonstrate Build 0 and review the recorded startup/connectivity checks | Khiêm with Đạt, Nhật, and Hòa |
| 19:55–20:05 | Review Spec Kit artifacts and the five individual training records | Long, Khiêm, and all members |
| 20:05–20:15 | Review AI disclosures, available meeting records/evidence, the draft PDF, and the final packaging plan | Hòa, Khiêm, and Long |
| 20:15–20:30 | Discuss retrospective findings and agree on Sprint 3 improvements | All members |

**Deliverable review**

| Deliverable | Presenter / owner | Review outcome |
|-------------|-------------------|----------------|
| Project Plan | Long; Nhật for the overview | Accepted. Scope, organization, risks, task ownership, separate reviewers, capacity, and staged build milestones were checked for consistency. |
| Vision Document and workflows | Hòa, Đạt, and Nhật | Accepted. The PA1 survey was retained; product features and workflows consistently describe offline support, group accounting, settlement confirmation, and VND/USD conversion. |
| Non-functional requirements | Khiêm | Accepted. Quality targets have measurable criteria and proposed verification methods; target values are distinguished from observed build results. |
| Spec Kit and individual training | Long for setup; Khiêm for evidence collection; each member for their own training | Training completion is reported for 19–20 October; setup evidence is assessed in Part C. Part C contains the current artifact inventory and five training summaries; the initialization artifacts are present in `b98938f`, but its constitution still requires project-specific content. |
| Build 0 | Khiêm with the component owners | Accepted for the skeleton milestone in the agreed scenario. The acceptance scope is startup, mobile/API/database connectivity, AI-service response, and controlled provider-failure handling; later feature and NFR tests belong to subsequent builds. |
| AI Usage Report and Weekly Report | Hòa and Long | The shared disclosure, 14 prompt entries from Nhật, 13 from Khiêm, and four meeting narratives are included in this revision. Long, Đạt, and Hòa have not yet supplied their individual AI histories. Long records the Review and Khiêm reviews its minutes. |
| Evidence and submission readiness | Khiêm, with Đạt and Long reviewing | The pre-Review draft and available evidence were reviewed. Final evidence, Review records, PDF regeneration, and PA2-Group04 archive verification are completed on 24–25 October under S2-25, with Long reviewing the final package. |

**Review decision:** Within the reporting basis stated in D.2, the team accepted the Project Plan, Vision, workflows, NFR specification, meeting narratives, and skeleton scope prepared by 23 October. Part C is included in this report; the AI histories for Long, Đạt, and Hòa remain absent. Long completes the Review minutes and Đạt reconciles the Review outcomes on 24 October. Khiêm completes final evidence, PDF/archive verification, and packaging under S2-25 on 24–25 October, with Long reviewing the package before submission. These closeout activities remain within Sprint 2; no substantive PA2 development or document correction is carried into Sprint 3. The staged implementation milestones in Sprints 3–5 remain the basis for subsequent product development.

**Retrospective**

| Required question | Discussion and conclusion |
|-------------------|---------------------------|
| What went well? | All five members participated in each meeting, and the role-based allocation gave each workstream a clear owner and a different reviewer. Reusing the PA1 baseline helped the team refine the plan and vision, while the shared Build 0 checkpoint brought the component owners together before the final Review. |
| What went wrong? | Spec Kit and individual training missed their initial early-sprint targets. Although the team recovered the work before the second Scrum, this reduced the time available for review and concentrated evidence/package preparation near the end of the sprint. |
| What problems occurred and what caused them? | The team underestimated the combined effort for learning, environment setup, and retaining individual evidence. Component work also needed agreement on API fields and financial rules. Recovery used named owners, revised checkpoints, and a shared startup review to resolve these dependencies. |
| What can be done differently next sprint? | Define each task's acceptance criteria and dependencies during Planning, validate the environment early, and retain evidence when work is completed. Review API examples before dependent feature work and schedule an integration rehearsal before the release build. |
| What lessons were learned? | Training, setup, implementation, review, and evidence collection each require explicit time. Recovery dates should remain traceable alongside the original plan. A shared build checkpoint and concrete accounting examples help the team find cross-component inconsistencies before the final Review. |

**Agreed Sprint 3 improvement actions**

| Improvement | Owner | Reviewer | Target | Success criterion |
|-------------|-------|----------|--------|-------------------|
| Prepare Sprint 3 tasks with an assignee, different reviewer, estimate, dependencies, and acceptance criteria | Long | Khiêm | 2026-10-26 | Every task selected at Planning contains the required fields before work starts |
| Use a per-task evidence and review checklist from the start of the sprint | Khiêm | Long | 2026-10-26 | A completed sample shows where the output, review decision, and supporting evidence are retained |
| Publish reviewed mobile/backend/AI contract examples, including money and error behavior | Nhật | Đạt | 2026-10-27 | Component owners use the same request/response and accounting examples |
| Run a shared integration rehearsal before Build 1 | Khiêm | Nhật | 2026-11-05 | The rehearsal records results and assigns any defects before the 8 November build target |

**Submission handoff:** By 23 October, Đạt completed the pre-Review cross-check, Hòa consolidated the shared AI declaration and the records for Nhật and Khiêm, and Khiêm organized the supplied Jira/Git evidence. The AI records for Long, Đạt, and Hòa remain absent; Part C contains the submitted setup and training information. Long records the Review minutes on 24 October, Khiêm reviews those minutes, and Đạt reconciles Review outcomes to close S2-27. On 24–25 October, Khiêm completes S2-25 by adding the Review records and final evidence, regenerating the PDF, and checking the archive. Long reviews the final package and confirms that the submitted files match the accepted revision before the 25 October handoff.

**Summary:** The Review accepts the prepared technical and document content, with full team attendance and Long responsible for the minutes. Final records and submission packaging are completed within 24–25 October. The retrospective retains the lesson from the training/setup delay and assigns concrete improvements for Sprint 3.

#### 2.5 Jira Evidence

Figure D.1 provides the single Jira board image used for PA2 assignment/progress evidence. The visible project is **CNPM-2026 - Group 4**. Each displayed task card includes a Jira key, title, and assignee avatar, and its column identifies its current status.

**Figure D.1 — Jira board overview for PA2**

![FinSync Jira board showing task assignments and progress](screenshots/jira/jira-sprint2-board.png)

*Caption: CNPM-2026 - Group 4 board, showing 7 To Do, 14 In Progress, and 24 Done tasks at the recorded snapshot. The board includes PA2 tasks identified by their S2 prefixes. Evidence image saved locally on 19 October 2026 at 11:33:00 (UTC+07:00).*

The counters cover the displayed project board, including other project tasks; they are not a count of only the 27 Sprint 2 tasks. The following local IDs and Jira keys can be read directly from the visible cards:

| Local ID in A.4.3 | Jira key | Visible task | Status shown |
|------------------|----------|--------------|--------------|
| S2-07 | SCRUM-39 | Vision — Product Overview and accounting boundaries | Done |
| S2-09 | SCRUM-41 | Two Mermaid user workflows | Done |
| S2-20 | SCRUM-52 | AI service self-training, skeleton, connectivity and failure checks | Done |
| S2-21 | SCRUM-53 | Weekly Scrum #1 and notes | Done |

**Evidence timing:** The PNG images contain no embedded capture timestamp. The image date/time in Section 2.5 is the file's local last-saved timestamp; dates in the Git figures are commit dates, not capture times; these image timestamps do not verify the access times in the AI appendix. Figure D.1 documents the board state shown and does not establish when an earlier status transition occurred.

#### 2.6 Git History Evidence

The five screenshots show repository history across branches using `git log --all --graph`, including commit hashes, authors, dates, and messages. The latest visible commit is `40f5263`, dated 21 October 2026. Some screenshots overlap.

![Git history overview with PA2 updates](screenshots/git/git-log-sprint2-01.png)

*Figure D.2 — PA2 report updates, evidence additions, and Spec Kit commits.*

![Recent PA2 documentation commits](screenshots/git/git-log-sprint2-02.png)

*Figure D.3 — PA2 documentation and screenshot updates from 14–21 October 2026.*

![Spec Kit and test strategy commits across branches](screenshots/git/git-log-sprint2-03.png)

*Figure D.4 — Branch history showing Spec Kit initialization and test strategy commits.*

![Project documentation survey and backend history](screenshots/git/git-log-sprint2-04.png)

*Figure D.5 — Earlier project documentation, app-survey screenshots, and backend setup.*

![Consolidated repository commit graph](screenshots/git/git-log-sprint2-05.png)

*Figure D.6 — Repository commit graph covering project setup and PA2 work, 3–21 October 2026.*

#### 2.7 Meeting Action Register

This register consolidates the documented outputs and the closeout sequence under the reporting basis in D.2. Baseline due dates remain visible alongside recovery or completion milestones. The Jira evidence retains its 19 October snapshot and the Git evidence shows commits through 21 October; Part C supplies the current setup/training information.

| Action / source | Action and expected result | Owner | Reviewer | Baseline due | Disposition and closeout milestone |
|-----------------|----------------------------|-------|----------|--------------|------------------------------------|
| ACT-01 / Planning | Establish task ownership, dates, capacity, and Jira traceability | Long | Khiêm | 2026-10-10 | A.4.3 contains all 27 work packages, separate reviewers, dates, and the capacity calculation. D.2.5 records the four Jira keys visible in the supplied board image; the remaining keys are not inferred. |
| ACT-02 / Planning | Complete Spec Kit setup and coordinate five training records | Long | Nhật | 2026-10-15 | Long owns S2-12 and Nhật reviews it; Khiêm collects evidence and each member completes their own training. The baseline due date is 15 October; initial setup is reported on 19 October, training on 19–20 October, and the recovery target for the constitution/inventory is 21 October. Commit `b98938f` contains the initialization artifacts under `/src`; final constitution customization is not evidenced by that commit. |
| ACT-03 / Scrum #1 | Reconcile the baseline and recovery milestones | Long | Hòa | 2026-10-17 | D.2.2 records the confirmed 16 October delay and the 19 October recovery targets. A.4.3 retains the original dates, so the delay and recovery remain traceable. |
| ACT-04 / Scrum #1 | Settle currency, own-group-share, and duplicate-counting rules | Nhật | Khiêm | 2026-10-17 | The selected rules are complete in B.4.1 and consistently reflected in the features, two workflows, and measurable NFRs. |
| ACT-05 / Scrum #2 | Assess Build 0 against its skeleton scope | Khiêm | Đạt | 2026-10-22 | Accepted at the 22 October checkpoint within the agreed scenario: startup, component/database connectivity, AI response, and controlled failure behavior. No measured test log is asserted by this narrative. |
| ACT-06 / Scrum #2 | Consolidate AI declarations and supporting histories | Hòa | Long | 2026-10-23 | The Appendix contains 14 supplied prompt entries for Nhật and 13 for Khiêm. Adopted outputs and completed independent validation remain to be documented; the records for Long, Đạt, and Hòa are absent. |
| ACT-07 / Review | Reconcile report content and Review decisions | Đạt | Hòa | 2026-10-24 | The agreed sequence completes the document cross-check on 23 October and incorporates the 24 October Review narrative. Task roles, baseline/recovery dates, business rules, and evidence captions are aligned in this report. |
| ACT-08 / Review | Prepare and check the Markdown/PDF submission package | Khiêm | Long | 2026-10-25 | Completed on 25 October within the agreed end-of-sprint scenario in D.2. Khiêm assembled the Markdown, PDF, stylesheet, referenced files, one Jira image, and five Git images into PA2-Group04.zip; checked image paths, diagram rendering, page readability, and archive contents; and handed the package to Long for final review. This is a hypothetical closeout result; no generated archive is evidenced here. |

---

## Appendix — AI Usage Notes

Team members used various AI tools, including Codex, ChatGPT, Claude, Gemini, and Cursor. Entries below are grouped by member. They describe the requested AI assistance; adopted outputs and completed independent revision or validation are not documented in these entries.

### 1. Nguyễn Lê Đức Nhật

> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Le Duc Nhat`*

- **Prompt 1:** Codex(gpt-6.1-sol), Open AI, accessed 7:00 on 10/10/2026, prompt: “Read `pa2_project_assignment_specification.md` and create a table listing a checklist of all deliverables for Parts A, B, C, and D, including section names, formats, required evidence, and submission instructions.”; used to request assistance for Parts A–D; requested AI contribution: a deliverable and submission checklist.

- **Prompt 2:** Codex(gpt-6.1-sol), Open AI, accessed 15:30 on 11/10/2026, prompt: “Based on `GEMINI.md`, write a Project Overview for FinSync in English using one or two paragraphs, a simple academic tone, and no more than 150 words.”; used to request assistance for A.1–A.2 (Introduction and Project Overview); requested AI contribution: an English overview based on `GEMINI.md`.

- **Prompt 3:** Codex(gpt-6.1-sol), Open AI, accessed 16:00 on 11/10/2026, prompt: “List at least 10 risks for the FinSync project covering technical issues, staffing, scheduling, AI, and security. Rate Probability and Impact on a scale of 1–5 and calculate the Risk Score as Probability × Impact in a Markdown table.”; used to request assistance for A.3.2 (Risk Management); requested AI contribution: candidate risks and probability × impact scores.

- **Prompt 4:** Codex(gpt-6.1-sol), Open AI, accessed 19:00 on 13/10/2026, prompt: “Create a Performed/Reviewed/Edited assignment table for all sections, ensuring that the performer and reviewer are different people and rotating the assignments evenly among the five members.”; used to request assistance for the attribution lines across Parts A–D and the role/task tables in A.3.1 and A.4.3; requested AI contribution: a proposed Performed/Reviewed/Edited allocation.

- **Prompt 5:** Codex(gpt-6.1-sol), Open AI, accessed 17:00 on 13/10/2026, prompt: “Write a Problem Statement and a Product Position Statement for FinSync using standard templates, based on Section 1 of `GEMINI.md`.”; used to request assistance for B.2.1–B.2.2 (Problem and Product Position Statements); requested AI contribution: draft statements based on GEMINI.md.

- **Prompt 6:** Codex(gpt-6.1-sol), Open AI, accessed 8:30 on 15/10/2026, prompt: “Compare FinSync with Money Lover and Splitwise in a table using the following criteria: personal wallets, group expense splitting, AI, budgeting, and offline support.”; used to request assistance for B.3.5 (Alternatives and Competition); requested AI contribution: a comparison table for FinSync, Money Lover, and Splitwise.

- **Prompt 7:** Codex(gpt-6.1-sol), Open AI, accessed 9:10 on 16/10/2026, prompt: “Write an 80–100-word description in English of Feature 6: Smart Debt Split & Settlement, based on `GEMINI.md`.”; used to request assistance for B.5.6 (Smart Debt Split & Settlement); requested AI contribution: an English feature-description draft.

- **Prompt 8:** Codex(gpt-6.1-sol), Open AI, accessed 7:45 on 17/10/2026, prompt: “Draw a Mermaid flowchart showing the following workflow: record a shared expense → split the amount → send notifications → mark as paid.”; used to request assistance for B.5.12 (Key Workflow Diagrams); requested AI contribution: Mermaid syntax for recording, splitting, notifying, and settling shared expenses.

- **Prompt 9:** Codex(gpt-6.1-sol), Open AI, accessed 7:45 on 20/10/2026, prompt: “Propose Non-Functional Requirements covering Performance, Security, Privacy, Usability, and Reliability in a table with the columns ID, Category, Requirement, Measurement, and Priority.”; used to request assistance for B.6 (Non-Functional Requirements); requested AI contribution: candidate requirements, measurement criteria, and priorities.

- **Prompt 10:** Codex(gpt-6.1-sol), Open AI, accessed 13:30 on 20/10/2026, prompt: “Briefly explain the Spec Kit workflow (constitution → specify → plan → tasks → implement) and provide instructions for initializing it in the existing FinSync repository.”; used to request assistance for C.1 (Repository and Spec Kit Setup) and C.4 (Training Evidence); requested AI contribution: a workflow explanation and suggested initialization steps.

- **Prompt 11:** Codex(gpt-6.1-sol), Open AI, accessed 14:23 on 22/10/2026, prompt: “Draft a constitution prompt for Spec Kit based on the coding conventions in Section 6.6 of `GEMINI.md`.”; used to request assistance for C.3 (Constitution Summary); requested AI contribution: a constitution prompt based on GEMINI.md Section 6.6.

- **Prompt 12:** Codex(gpt-6.1-sol), Open AI, accessed 18:35 on 22/10/2026, prompt: “I am the Backend Lead working with Spring Boot. Explain how to use Spec Kit for my role and provide a small exercise for independent practice.”; used to request assistance for C.4 (Spec Kit Training Evidence); requested AI contribution: a backend-specific explanation and a Spring Boot practice exercise.

- **Prompt 13:** Codex(gpt-6.1-sol), Open AI, accessed 20:00 on 22/10/2026, prompt: “Create a checklist to verify that the Spec Kit files have been committed, `.gitignore` is configured correctly, and no API keys or `.env` files are exposed.”; used to request assistance for C.1–C.2 (Repository Setup and Submitted Artifacts); requested AI contribution: a checklist for tracked Spec Kit files, ignore rules, and possible secret exposure.

- **Prompt 14:** Codex(gpt-6.1-sol), Open AI, accessed 16:00 on 23/10/2026, prompt: “Suggest a structure for the AI Usage Report covering policy, logs, a summary by report part, validation, risks, and reflection.”; used to request assistance for D.1 (AI Usage Report) and the AI Usage Notes appendix; requested AI contribution: a proposed structure for policy, logs, affected sections, verification, risks, and reflection.

### 2. Phạm Định Tiểu Long

> *Performed by: `Pham Dinh Tieu Long` | Reviewed by: `Nguyen Le Duc Nhat` | Edited by: `Pham Dinh Tieu Long`*

No individual usage record is included in this revision.

### 3. Nguyễn Phú Đạt

> *Performed by: `Nguyen Phu Dat` | Reviewed by: `Pham Dinh Tieu Long` | Edited by: `Nguyen Phu Dat`*

No individual usage record is included in this revision.

### 4. Vương Đắc Gia Khiêm

> *Performed by: `Vuong Dac Gia Khiem` | Reviewed by: `Ngo Thai Hoa` | Edited by: `Vuong Dac Gia Khiem`*

- **Prompt 1:** ChatGPT (GPT-6), OpenAI, accessed 8:38 on 14/10/2026, prompt: “I am preparing a Test Strategy for the FinSync project. Is the following structure complete and suitable for a beginner: (1) Test Objectives, (2) Scope, (3) Test Environment, (4) Testing Methods & Types (manual/automation, functional/performance/security), (5) Tools, and (6) Risk Analysis & Contingency Plan?”; used to request assistance with planning the Test Strategy document; requested AI contribution: review the proposed structure and recommend missing sections; independent validation plan: verify that the final structure matches the project requirements, the team's expectations, and the actual repository setup.

- **Prompt 2:** ChatGPT (GPT-6), OpenAI, accessed 9:40 on 14/10/2026, prompt: “Rewrite a Markdown file based on your recommendations for the FinSync Test Strategy.”; used to request assistance with preparing the Test Strategy document; requested AI contribution: draft TEST_STRATEGY.md with sections covering objectives, scope, environment, testing methods and types, tools and frameworks, test prioritization, entry and exit criteria, CI/CD integration and reporting, and risk analysis and contingency planning; independent validation plan: check the document against the actual project architecture, implemented features, installed testing tools, build commands, and CI/CD configuration, and revise any assumptions that have not been verified.

- **Prompt 3:** ChatGPT (GPT-6), OpenAI, accessed 13:20 15/10/2026, prompt: “Explain each section of the Test Strategy so that I can understand it and avoid becoming dependent on AI.”; used to request assistance with understanding the Test Strategy and learning the underlying testing concepts; requested AI contribution: explain the purpose and practical application of each section, including test objectives, scope, environment, testing methods and levels, tools, prioritization, entry and exit criteria, CI/CD integration, and risk analysis; independent validation plan: explain the concepts in my own words, relate them to actual FinSync requirements and code, and independently justify the selected testing methods, priorities, and completion criteria.

- **Prompt 4:** ChatGPT (GPT-6), OpenAI, accessed 17:54 15/10/2026, prompt: “Explain the difference between a Test Strategy, Test Plan, Test Case, and Test Scenario using simple examples from a software project.”; used to clarify fundamental testing concepts before preparing the FinSync Test Strategy; requested AI contribution: definitions, comparisons, and practical examples; independent validation plan: explain each concept in my own words and verify that the distinctions match the course materials.

- **Prompt 5:** ChatGPT (GPT-6), OpenAI, accessed 10:22 16/10/2026, prompt: “Explain how automated tests can be integrated into a CI/CD pipeline and how test results, failures, and reports should be handled. Keep the recommendations independent of any specific CI platform.”; used to develop the CI/CD Integration and Reporting section; requested AI contribution: a general pipeline sequence and reporting recommendations; independent validation plan: inspect the repository's workflow configuration and verify which stages, commands, and reports are supported by the actual setup.

- **Prompt 6:** ChatGPT (GPT-6), OpenAI, accessed 19:36 18/10/2026, prompt: “Explain entry criteria, exit criteria, and quality gates in a software testing strategy, and provide examples suitable for a CI/CD pipeline.”; used to define when testing should begin and when a build may be considered ready to proceed; requested AI contribution: sample criteria and explanations of quality gates; independent validation plan: adjust the criteria to the team's agreed quality standards and the checks that can actually run in its pipeline.

- **Prompt 7:** ChatGPT (GPT-6), OpenAI, accessed 21:39 18/10/2026, prompt: “Explain the difference between manual testing and automated testing, including their advantages, limitations, and appropriate use cases in a CI/CD workflow.”; used to develop the Testing Methods section; requested AI contribution: a comparison and examples of when to use each method; independent validation plan: choose an approach that matches the team's time, skills, and actual CI/CD setup.

- **Prompt 8:** ChatGPT (GPT-6), OpenAI, accessed 15:30 19/10/2026, prompt: “Explain the purpose of the Test Objectives section in a Test Strategy and suggest measurable objectives for a student software project without assuming unverified project requirements.”; used to develop the Test Objectives section; requested AI contribution: example objectives and guidance on making them measurable; independent validation plan: select only objectives relevant to FinSync and avoid claiming that unverified targets have been achieved.

- **Prompt 9:** ChatGPT (GPT-6), OpenAI, accessed 16:55 19/10/2026, prompt: “Explain the difference between the SpecKit Specify, Clarify, Plan, Tasks, and Implement stages. For each stage, identify its purpose, expected inputs, outputs, and when it should be used.”; used to understand the overall SpecKit workflow; requested AI contribution: a comparison of the stages and their dependencies; independent validation plan: compare the explanation with the available workflow instructions and trace the stages using the project's actual files.

- **Prompt 10:** ChatGPT (GPT-6), OpenAI, accessed 18:11 19/10/2026, prompt: “Review constitution.md and explain its purpose in the SpecKit workflow. Identify the principles, constraints, and development standards that should guide later specification, planning, and implementation tasks.”; used to understand the role of the project constitution in SpecKit; requested AI contribution: an explanation of the constitution and its relationship to later workflow stages; independent validation plan: compare the explanation with the actual constitution file and identify which principles apply to the FinSync project.

- **Prompt 11:** ChatGPT (GPT-6), OpenAI, accessed 12:30 20/10/2026, prompt: “Read the relevant SKILL.md file for the SpecKit Specify workflow and explain its required inputs, steps, outputs, and constraints in beginner-friendly language.”; used to understand how the Specify workflow operates; requested AI contribution: a step-by-step explanation of the skill instructions; independent validation plan: follow the actual instructions and verify that the explanation matches the files and commands in the repository.

- **Prompt 12:** ChatGPT (GPT-6), OpenAI, accessed 13:10 20/10/2026, prompt: “Read the SpecKit Clarify instructions and explain how to identify ambiguities, missing requirements, and important decisions before planning implementation.”; used to understand the Clarify stage; requested AI contribution: an ambiguity checklist and examples of useful clarification questions; independent validation plan: identify genuine unanswered questions in the FinSync specification and distinguish them from assumptions that already have evidence.

- **Prompt 13:** ChatGPT (GPT-6), OpenAI, accessed 14:30 20/10/2026, prompt: “Explain the SpecKit Plan workflow and how its technical plan should be derived from an approved feature specification and the project's existing constitution and technology constraints.”; used to understand technical planning in SpecKit; requested AI contribution: an explanation of planning inputs, technical decisions, and expected outputs; independent validation plan: compare proposed technologies and architecture with the actual repository, existing dependencies, and team decisions.

### 5. Ngô Thái Hòa

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
