# FinSync - Agile Project Management & Scrum Context & AI Generation Guide

> **AI Assistant Persona & Usage Context:**
> When prompted to generate or update any document inside `docs/management/`, adopt the persona of a **Certified Scrum Master & Agile Project Manager** (Lead: Phạm Đình Tiểu Long - 24120087). Read this context guide completely to align with HCMUS CS300 course policies, TA guidelines, and professional software engineering project governance.

---

## 1. Project Management Framework & HCMUS Course Rules

FinSync follows the **Scrum / Agile Framework** adapted for academic engineering milestones.

### 1.1. Sprint Rhythm & Course Milestones
- **Sprint Cadence:** Each Project Assignment (PA) corresponds to **1 Sprint** (duration: 2-3 weeks).
- **Mandatory 4 Meetings per Sprint:**
  1. **1x Sprint Planning Meeting:** Held at the very beginning of the sprint. Deconstructs requirements, estimates story points, establishes Sprint Backlog, and assigns tasks on Jira.
  2. **2x Weekly Scrum (Standup) Meetings:** Held mid-sprint to monitor velocity and identify blockers. Every member must explicitly answer the **3 Standup Questions**:
     - *1. What have I done since last week?*
     - *2. What will I do until next week?*
     - *3. What issues / problems / obstacles do I have?*
  3. **1x Sprint Review & Retrospective:** Held at the sprint conclusion. Evaluates deliverables, confirms acceptance criteria, and conducts a deep retrospective answering the **5 Retrospective Questions**:
     - *1. What went well?*
     - *2. What went wrong?*
     - *3. What were the root causes?*
     - *4. What are the actionable improvements for next sprint?*
     - *5. What key lessons were learned?*

### 1.2. Jira Workflow & Integrity Invariants
- **Strict 1-Assignee Rule:** Every Jira issue must be assigned to **exactly one** member responsible for delivery.
- **No Retrospective Task Dumping:** Tasks must be created **before** implementation begins. Creating, assigning, and moving tasks to Done simultaneously after work is completed is strictly prohibited by TA guidelines.
- **Issue Lifecycle:** `To Do` $\to$ `In Progress` $\to$ `Review / Testing` $\to$ `Done`.
- **Traceability:** Git commits must reference Jira Issue Keys (e.g., `feat(split): implement debt simplification algorithm [FIN-42]`).

### 1.3. Team Roster & Assigned Leads
| Student ID | Full Name | Academic Role | Primary Lead Responsibility |
|---|---|---|---|
| **24120087** | **Phạm Đình Tiểu Long** | **Project Manager / Group Leader** | Sprint facilitation, Jira management, progress tracking |
| **24120038** | **Nguyễn Phú Đạt** | **UI/UX & Frontend Lead** | UI/UX design, Jetpack Compose client architecture |
| **24120403** | **Nguyễn Lê Đức Nhật** | **Backend Lead** | Spring Boot API architecture, DB schema, security |
| **24120054** | **Vương Đắc Gia Khiêm** | **QA & DevOps Lead** | Test planning, test case authoring, Docker CI/CD |
| **24120042** | **Đặng Gia Hòa** | **AI & Documentation Lead** | FastAPI microservice, Gemini integration, technical documentation |

---

## 2. Directory Structure & Child File Catalog

All project management and Scrum artifacts must reside in this directory (`docs/management/`):

```text
docs/management/
├── README.md                      # This Agile Management & Prompting Guide
├── meeting-notes/                 # Ad-hoc & Formal Meeting Minutes
│   ├── README.md                  # Meeting notes index & guidelines
│   ├── sprint1-planning-notes.md  # Detailed Sprint 1 planning minutes
│   └── sprint1-retrospective.md   # Sprint 1 retrospective analysis
└── weekly-reports/                # Formal Course Weekly Reports (PA Submission standard)
    ├── README.md                  # Weekly reports guidelines
    ├── WeeklyReport_Sprint1_Week1_2026-10-05.md
    └── WeeklyReport_Sprint1_Week2_2026-10-12.md
```

---

## 3. Mandatory Formatting & Attribution Standards

1. **Language:** Professional Technical English throughout.
2. **Attribution Line:** Placed immediately beneath every major heading:
   ```markdown
   > *Performed by: [Author Name] | Reviewed by: [Reviewer Name] | Edited by: [Editor Name]*
   ```
3. **Commit History Alignment:** Work items listed in weekly reports must correspond directly with actual Git commits and Jira issue statuses.

---

## 4. Templates for AI Document Generation

When prompting an AI to generate child management documents, instruct it to use these standardized templates:

### 4.1. Template for Weekly Scrum Standup Notes
```markdown
# Weekly Scrum Meeting Notes - Sprint [X] Week [Y]
> *Performed by: Phạm Đình Tiểu Long | Reviewed by: Toàn thể nhóm | Edited by: Đặng Gia Hòa*

- **Date & Time:** YYYY-MM-DD, HH:MM - HH:MM
- **Location:** Online (Google Meet / Discord) / Offline (HCMUS Library)
- **Chair / Facilitator:** Phạm Đình Tiểu Long (PM)
- **Attendees:** Full team present (Long, Đạt, Nhật, Khiêm, Hòa)

---

## Member Standup Reports

### 1. Phạm Đình Tiểu Long (Project Manager)
- **What did I do since last week:**
  - Facilitated Sprint 1 backlog refinement and created 15 Jira tasks.
  - Setup repository governance, branch protection, and project guidelines.
- **What will I do until next week:**
  - Coordinate integration testing between Mobile and Backend.
  - Track sprint velocity and resolve schedule bottlenecks.
- **Impediments / Blockers:**
  - None at this time.

### 2. Nguyễn Phú Đạt (UI/UX & Frontend Lead)
- **What did I do since last week:**
  - Designed high-fidelity Figma mockups for Auth, Dashboard, and Wallet screens.
  - Scaffolding Android project with Jetpack Compose and Navigation component.
- **What will I do until next week:**
  - Implement login and register UI with form validation.
  - Setup Retrofit networking layer for Auth API.
- **Impediments / Blockers:**
  - Need final API response schema for JWT authentication token payload.

### 3. Nguyễn Lê Đức Nhật (Backend Lead)
- **What did I do since last week:**
  - Configured Spring Boot 3.2 skeleton with PostgreSQL and Flyway.
  - Implemented JWT token generation and Spring Security filter chain.
- **What will I do until next week:**
  - Complete user registration and login endpoints.
  - Implement CRUD services and repository for Personal Wallets.
- **Impediments / Blockers:**
  - None.

### 4. Vương Đắc Gia Khiêm (QA & DevOps Lead)
- **What did I do since last week:**
  - Authored Master Test Plan and established Jira test tracking workflow.
  - Created initial Dockerfile for Spring Boot and Docker Compose environment.
- **What will I do until next week:**
  - Write test cases for GRP-01 (Auth) and GRP-03 (Wallets).
  - Setup GitHub Actions CI pipeline for automated backend build and unit tests.
- **Impediments / Blockers:**
  - None.

### 5. Đặng Gia Hòa (AI & Documentation Lead)
- **What did I do since last week:**
  - Authored Requirements Vision and Functional Specification documents.
  - Researched Google Gemini 1.5 Pro API pricing, rate limits, and JSON mode.
- **What will I do until next week:**
  - Setup FastAPI project structure and Pydantic schemas.
  - Draft prompt engineering templates for monthly spending advice.
- **Impediments / Blockers:**
  - Waiting for sample transaction dataset for prompt benchmarking.
```

### 4.2. Template for Sprint Retrospective (`sprint[x]-retrospective.md`)
```markdown
# Sprint [X] Retrospective Report
> *Performed by: Phạm Đình Tiểu Long | Reviewed by: Toàn thể nhóm | Edited by: Đặng Gia Hòa*

## 1. What Went Well?
- High collaboration on Discord; architectural decisions resolved quickly.
- Jira tasks were kept updated in real-time.
- Docker environment simplified database setup for all members.

## 2. What Went Wrong?
- Underestimated time required to configure Jetpack Compose theme and typography.
- Initial debt split formula did not handle odd division remainders cleanly.

## 3. Root Causes
- Lack of upfront agreement on decimal handling conventions between Android and Java.

## 4. Actionable Improvements for Next Sprint
- Standardize all currency handling to `BigDecimal` in backend and `Long` (VND cents) in mobile.
- Hold a brief 10-minute technical sync before starting complex cross-cutting tasks.

## 5. Lessons Learned
- Writing comprehensive README context files early drastically accelerates AI-assisted documentation.
```

---

## 5. Management Quality Checklist (AI Self-Review)

Before finalizing any management document, verify:
- [ ] Are all 5 team members represented with their exact full names and student IDs?
- [ ] Does every standup entry strictly answer the **3 questions**?
- [ ] Does every retrospective strictly answer the **5 questions**?
- [ ] Is there exact alignment with Jira task keys and milestones?
- [ ] Is the attribution line present under every section?
