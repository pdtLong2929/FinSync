# FinSync - Course Weekly Reports & AI Generation Guide

> **AI Assistant Context & Prompting Guide:**
> When prompted to generate or update any weekly report inside `docs/management/weekly-reports/`, follow this guide strictly. All weekly reports must comply with the HCMUS CS300 `WeeklyReport.md` specification and match real commit history and Jira task progress.

---

## 1. Weekly Report Objectives & TA Guidelines

- **Purpose:** Serve as formal academic evidence for TA evaluation of team velocity, individual accountability, and project governance.
- **Reporting Period:** Weekly submission aligned with sprint progress.
- **Mandatory Requirements:**
  - Explicit answer to the **3 Scrum Questions** for every single team member.
  - Actual task IDs and deliverables mapped to Jira.
  - Attribution line under every heading.

---

## 2. File Naming Standard

```text
WeeklyReport_Sprint[X]_Week[Y]_YYYY-MM-DD.md
```
*Example:* `WeeklyReport_Sprint1_Week1_2026-10-05.md`

---

## 3. Standard Weekly Report Schema

```markdown
# Weekly Progress Report - Sprint [X] Week [Y]
> *Performed by: Phạm Định Tiểu Long | Reviewed by: Toàn thể nhóm | Edited by: Ngô Thái Hòa*

## 1. Sprint Overview & Team Goals
- **Sprint:** Sprint [X]
- **Week:** Week [Y] (From YYYY-MM-DD to YYYY-MM-DD)
- **Sprint Goal:** [Brief statement of sprint milestone]
- **Overall Sprint Completion Rate:** [XX]%

---

## 2. Individual Progress (3 Scrum Questions per Member)

### 2.1. Phạm Định Tiểu Long (Student ID: 24120087) - Project Manager
- **1. What have I done since last week?**
  - Item 1...
- **2. What will I do until next week?**
  - Item 1...
- **3. What issues / problems / obstacles do I have?**
  - Item 1...

### 2.2. Nguyễn Phú Đạt (Student ID: 24120038) - UI/UX & Frontend Lead
- [Same 3 questions]

### 2.3. Nguyễn Lê Đức Nhật (Student ID: 24120403) - Backend Lead
- [Same 3 questions]

### 2.4. Vương Đắc Gia Khiêm (Student ID: 24120342) - QA & DevOps Lead
- [Same 3 questions]

### 2.5. Ngô Thái Hòa (Student ID: 24120051) - AI & Documentation Lead
- [Same 3 questions]

---

## 3. Team Collaboration & Quality Verification
- **Code Review Status:** [Summary of PRs merged and reviewed]
- **Test Execution Summary:** [Pass rate, automated tests run]
- **Jira Board Snapshot:** Reference to screenshot evidence in `screenshots/jira/`
```
