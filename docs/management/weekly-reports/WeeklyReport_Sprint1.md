# Weekly Reports - Sprint 1 (PA1 Milestone)

> *Performed by: Phạm Định Tiểu Long | Reviewed by: Toàn thể nhóm | Edited by: Ngô Thái Hòa*

**Process Methodology:** Scrum / Agile  
**Course:** CS300 - CSC13002: Introduction to Software Engineering (HCMUS)  
**Academic Year:** Fall 2026  
**Team:** Group 04 - **FinSync**  
**Sprint Duration:** 2 Weeks (2026-09-27 to 2026-10-04)  
**Sprint Goal:** Complete PA1 submission deliverables, establish project governance, conduct app survey, finalize team contract, set up development tooling, and author initial architecture and requirements documentation.

---

## Sprint Meeting Overview

Each Sprint is fixed for 2-3 weeks, corresponding to each Project Assignment (PA). In accordance with the course specification, the team conducts **4 mandatory meetings** per Sprint:

| # | Meeting Type | Date & Time | Location | Objectives & Status |
|---|---|---|---|---|
| 1 | **Sprint 1 Planning** | 2026-09-27 (19:00 - 20:30) | Google Meet | Deconstruct PA1 requirements, define backlog, create & assign Jira tasks | **Completed** |
| 2 | **Weekly Scrum 1** | 2026-10-01 (20:00 - 20:45) | Google Meet | Mid-sprint status check, individual 3 questions, blocker identification | **Completed** |
| 3 | **Weekly Scrum 2** | 2026-10-04 (19:00 - 19:40) | Google Meet | Pre-submission review, deliverable verification, documentation alignment | **Completed** |
| 4 | **Sprint 1 Review & Retrospective** | 2026-10-04 (19:45 - 20:50) | Google Meet | Deliverables evaluation, demo, answer 5 Retrospective questions | **Completed** |

---

## Meeting 1: Sprint 1 Planning Meeting

- **Date & Time:** 2026-09-27, 19:00 - 20:30
- **Location:** Google Meet
- **Facilitator:** Phạm Định Tiểu Long (Project Manager)
- **Secretary:** Ngô Thái Hòa
- **Team members present:**
  1. Phạm Định Tiểu Long (24120087)
  2. Nguyễn Phú Đạt (24120038)
  3. Nguyễn Lê Đức Nhật (24120403)
  4. Vương Đắc Gia Khiêm (24120342)
  5. Ngô Thái Hòa (24120051)
- **Team members absent:** None (Full team present)

### Agenda & Planning Activities
1. **Deconstruct PA1 Specification:** Review `pa1_2026_project_assignment_specification.md` requirements across Section A (Registration), Section B (Proposal), Section C (App Survey), Section D (Team Contract), and Section E (Tool Setup).
2. **Define Product Vision:** Finalize the dual-engine FinSync concept (Personal Money Lover + Group Splitwise with AI Advisor), enforcing the **Pure Bookkeeping Invariant** (no real bank withdrawal, no automated funds transfers).
3. **Jira Backlog Creation & Task Assignment:** Break down all deliverables into discrete Jira tasks with **exactly 1 assignee per task** prior to starting any work:
   - `FIN-1`: Author Section A - Group Registration and Member Roster (Assignee: Long)
   - `FIN-2`: Draft Section B - Project Proposal and 10 Functional Groups (Assignee: Nhật)
   - `FIN-3`: Specify AI Financial Advisor feature using Google Gemini API (Assignee: Hòa)
   - `FIN-4`: Conduct App Survey & Screen Analysis for Money Lover (Assignee: Khiêm)
   - `FIN-5`: Conduct App Survey & Screen Analysis for Splitwise (Assignee: Đạt)
   - `FIN-6`: Draft Section D - Team Contract, Rules, and Policies (Assignee: Long)
   - `FIN-7`: Configure Git Repository, Branch Protections, and Folder Structure (Assignee: Long)
   - `FIN-8`: Setup Section E - Development Process and Tooling Evidence (Assignee: Đạt)
   - `FIN-9`: Setup Docker Compose and initial Spring Boot backend skeleton (Assignee: Khiêm)
   - `FIN-10`: Establish Documentation Hub and context files in `/docs` (Assignee: Hòa)

### Summary of Meeting 1
The team unanimously agreed on the FinSync product scope, technology stack (Kotlin Android, Spring Boot 3, FastAPI Python, PostgreSQL), communication channels (Zalo + Google Meet), and task distribution.

---

## Meeting 2: Weekly Scrum Meeting 1

- **Date & Time:** 2026-10-01, 20:00 - 20:45
- **Location:** Google Meet
- **Facilitator:** Phạm Định Tiểu Long (Project Manager)
- **Team members present:** Full team present (Long, Đạt, Nhật, Khiêm, Hòa)
- **Team members absent:** None

### Member Status Reports (3 Standup Questions)

#### 1. Phạm Định Tiểu Long (Student ID: 24120087) - Project Manager
- **Completed tasks:**
  - Configured Jira Scrum project `CNPM-2026 - Group 4` with Sprint 1 backlog.
  - Setup GitHub repository `pdtLong2929/FinSync` with initial folder hierarchy (`/src`, `/docs`, `/screenshots`).
  - Authored Section A (Group Registration) and initial draft of Section D (Team Contract).
- **To-do tasks:**
  - Finalize Team Contract ground rules (Communication, Milestone schedules, Conflict resolution).
  - Review Section B and C draft submissions.
- **Issues / Obstacles:**
  - Need to ensure all members register their educational AI coding platform accounts (GitHub Copilot / ChatGPT).

#### 2. Nguyễn Phú Đạt (Student ID: 24120038) - UI/UX & Frontend Lead
- **Completed tasks:**
  - Completed Splitwise app survey (5 key screens: Group Dashboard, Add Expense, Balance Sheet, Settle Up, Activity Log).
  - Drafted Section E.2 (Communication Tools setup) and verified Zalo group evidence.
- **To-do tasks:**
  - Capture and verify Splitwise UI screenshots in `screenshots/app/`.
  - Prepare UI design tokens and wireframe guidelines for Android Jetpack Compose in `docs/analysis-and-design/ui-design/`.
- **Issues / Obstacles:**
  - Need higher-resolution screenshots for Splitwise debt settlement flow.

#### 3. Nguyễn Lê Đức Nhật (Student ID: 24120403) - Backend Lead
- **Completed tasks:**
  - Authored draft of Section B (Project Proposal: Introduction, Target Users, 10 Functional Groups).
  - Designed high-level architecture diagram and preliminary relational schema for multi-wallet and group ledgers.
- **To-do tasks:**
  - Refine Section B descriptions to strictly emphasize the Pure Bookkeeping constraint.
  - Initialize Spring Boot 3.2 backend skeleton with Maven and JPA entities.
- **Issues / Obstacles:**
  - Determining whether to use SQLite or PostgreSQL for local developer testing (resolved: use PostgreSQL via Docker).

#### 4. Vương Đắc Gia Khiêm (Student ID: 24120342) - QA & DevOps Lead
- **Completed tasks:**
  - Completed Money Lover app survey (5 key screens: Home, Transaction Entry, Budgeting, Reports, Wallets).
  - Created `docker-compose.yml` to orchestrate PostgreSQL 16 database.
- **To-do tasks:**
  - Author comparison matrix between Money Lover and Splitwise in Section C.3.
  - Draft Master Test Plan in `docs/test/test-plan.md`.
- **Issues / Obstacles:**
  - Docker Compose service port mapping conflict with existing local PostgreSQL service (resolved: map to port 5433 or stop local service).

#### 5. Ngô Thái Hòa (Student ID: 24120051) - AI & Documentation Lead
- **Completed tasks:**
  - Authored Section B.4 (AI Financial Advisor specification and Google Gemini 1.5 API workflow).
  - Researched Gemini API rate limits and structured JSON prompting patterns.
- **To-do tasks:**
  - Establish documentation context guides in `docs/requirements/` and `docs/analysis-and-design/`.
  - Author complete Functional Requirements Specification (FRS).
- **Issues / Obstacles:**
  - None at this time.

### Actions & Summary of Meeting 2
Sprint 1 progress is on schedule (approx. 50% completed). All members have active Jira tasks moved to `In Progress`. Action item: Khiêm and Đạt to verify all app screenshots and upload them to the repository before Saturday.

---

## Meeting 3: Weekly Scrum Meeting 2

- **Date & Time:** 2026-10-04, 19:00 - 19:40
- **Location:** Google Meet
- **Facilitator:** Phạm Định Tiểu Long (Project Manager)
- **Team members present:** Full team present (Long, Đạt, Nhật, Khiêm, Hòa)
- **Team members absent:** None

### Member Status Reports (3 Standup Questions)

#### 1. Phạm Định Tiểu Long (Student ID: 24120087) - Project Manager
- **Completed tasks:**
  - Finalized Section D (Team Contract: Roles, Protocols, Milestones, Accountability, Decision-Making).
  - Reviewed and consolidated all sections of `report.md`.
  - Monitored Jira task completion and captured board screenshot for Section E evidence.
- **To-do tasks:**
  - Facilitate Sprint Review & Retrospective meeting.
  - Export report to PDF and package PA1 submission zip archive.
- **Issues / Obstacles:**
  - None.

#### 2. Nguyễn Phú Đạt (Student ID: 24120038) - UI/UX & Frontend Lead
- **Completed tasks:**
  - Populated all 5 Splitwise screenshots and analytical commentary in Section C.2.
  - Authored Section E (Development Process, Jira, GitHub, AI tools evidence).
  - Created UI/UX context documentation in `docs/analysis-and-design/ui-design/README.md`.
- **To-do tasks:**
  - Assist in final report proofreading and layout styling.
- **Issues / Obstacles:**
  - None.

#### 3. Nguyễn Lê Đức Nhật (Student ID: 24120403) - Backend Lead
- **Completed tasks:**
  - Finalized Section B (Proposal) with detailed scope for all 10 modules.
  - Implemented initial Spring Boot Dockerfile and validated containerized build.
  - Reviewed team contract and Git log evidence.
- **To-do tasks:**
  - Prepare for PA2 requirements and API design phase.
- **Issues / Obstacles:**
  - None.

#### 4. Vương Đắc Gia Khiêm (Student ID: 24120342) - QA & DevOps Lead
- **Completed tasks:**
  - Populated all 5 Money Lover screenshots and key takeaways in Section C.1.
  - Completed comparative feature matrix (Money Lover vs Splitwise vs FinSync) in Section C.3.
  - Exported and verified Git commit log graph evidence.
- **To-do tasks:**
  - Prepare test case scaffolding for PA2.
- **Issues / Obstacles:**
  - None.

#### 5. Ngô Thái Hòa (Student ID: 24120051) - AI & Documentation Lead
- **Completed tasks:**
  - Authored exhaustive Functional Requirements (`docs/requirements/functional-requirements.md` - 42KB).
  - Authored Non-Functional Requirements (`docs/requirements/non-functional-requirements.md` - 18KB).
  - Generated all context `README.md` files across `/docs` subdirectories.
- **To-do tasks:**
  - Consolidate meeting minutes into the final Weekly Report document.
- **Issues / Obstacles:**
  - None.

### Actions & Summary of Meeting 3
All deliverables for PA1 have been completed and verified against the grading rubric. Proceed immediately to the Sprint Review and Retrospective.

---

## Meeting 4: Sprint 1 Review & Retrospective

- **Date & Time:** 2026-10-04, 19:45 - 20:50
- **Location:** Google Meet
- **Facilitator:** Phạm Định Tiểu Long (Project Manager)
- **Team members present:** Full team present (Long, Đạt, Nhật, Khiêm, Hòa)
- **Team members absent:** None

### Part 1: Sprint 1 Review (Deliverables Audit)
The team reviewed all committed artifacts against `pa1_2026_project_assignment_specification.md`:
1. **Section A (Group Registration):** Verified complete (5 members, group name FinSync, Group 04).
2. **Section B (Project Proposal):** Verified complete (Dual-engine concept, 10 functional groups, AI Financial Advisor, Pure Bookkeeping rule).
3. **Section C (Existing App Survey):** Verified complete (Money Lover & Splitwise with 10 screenshots and comparative analysis matrix).
4. **Section D (Team Contract):** Verified complete (All 8 subsections signed off with full attribution).
5. **Section E (Tool Setup):** Verified complete (Zalo, Google Meet, Jira board, GitHub repo, Docker, AI coding accounts).
6. **Documentation Hub (`/docs`):** Verified complete with structured AI context guides for Requirements, Architecture, Testing, and Management.
7. **Jira Tasks:** 100% of PA1 Sprint tasks moved to `Done` with single assignees and proper timestamps.

### Part 2: Sprint 1 Retrospective (5 Mandatory Questions)

#### 1. What Went Well?
- **Seamless Communication:** Daily interactions on Zalo and weekly syncs on Google Meet kept everyone aligned with zero misunderstandings.
- **Strict Jira Governance:** All tasks were created during Sprint Planning before work began, adhering strictly to course instructions (no post-hoc task dumping).
- **Proactive Documentation:** Building the `/docs` context files early created a solid knowledge base for AI-assisted workflows in subsequent sprints.
- **Fast Problem Resolution:** Port conflicts and screenshot alignments were diagnosed and resolved within 24 hours.

#### 2. What Went Wrong?
- **Initial Scope Ambiguity:** Some early discussions blurred the line between real banking APIs and manual bookkeeping; required clarification to prevent feature bloat.
- **Screenshot Formatting Overhead:** Sizing and captioning 10 app survey screenshots in Markdown took more time than originally estimated.

#### 3. Root Causes
- Underestimating the formatting effort required to ensure Mermaid diagrams and screenshots render crisply in both GitHub Markdown and exported PDF.

#### 4. Actionable Improvements for Next Sprint (PA2)
- **Spec Kit Adoption:** Setup and initialize Spec Kit immediately at the start of PA2 as required by TA instructions.
- **Standardized Templates First:** Define Markdown schemas and templates before assigning writing tasks to save formatting time.
- **Continuous Integration (CI):** Configure GitHub Actions to automatically run unit tests on every pull request.

#### 5. Lessons Learned
- Creating clear, modular context files (`README.md` in each subfolder) drastically accelerates AI assistant efficiency and ensures consistent technical depth.
- Treating academic milestones as real-world Agile sprints instills professional engineering discipline.

---

## Summary of Completed Tasks on Jira (PA1 Sprint)

| Jira Key | Task Summary | Assignee | Story Points | Status |
|---|---|---|:---:|:---:|
| `FIN-1` | Author Section A - Group Registration & Member Roster | Phạm Định Tiểu Long | 2 | **Done** |
| `FIN-2` | Draft Section B - Project Proposal & 10 Functional Groups | Nguyễn Lê Đức Nhật | 5 | **Done** |
| `FIN-3` | Specify AI Financial Advisor feature using Google Gemini API | Ngô Thái Hòa | 3 | **Done** |
| `FIN-4` | Conduct Money Lover App Survey & Screen Analysis | Vương Đắc Gia Khiêm | 5 | **Done** |
| `FIN-5` | Conduct Splitwise App Survey & Screen Analysis | Nguyễn Phú Đạt | 5 | **Done** |
| `FIN-6` | Draft Section D - Team Contract, Rules, and Policies | Phạm Định Tiểu Long | 3 | **Done** |
| `FIN-7` | Configure Git Repository, Branch Protections, and Folder Structure | Phạm Định Tiểu Long | 2 | **Done** |
| `FIN-8` | Setup Section E - Development Process & Tooling Evidence | Nguyễn Phú Đạt | 3 | **Done** |
| `FIN-9` | Setup Docker Compose & Spring Boot backend container | Vương Đắc Gia Khiêm | 3 | **Done** |
| `FIN-10` | Author Requirements & System Design Context in `/docs` | Ngô Thái Hòa | 5 | **Done** |
