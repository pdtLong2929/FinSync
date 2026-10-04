# FinSync - Scrum Meeting Notes & AI Generation Guide

> **AI Assistant Context & Prompting Guide:**
> When prompted to generate or update any meeting minutes inside `docs/management/meeting-notes/`, follow this guide. In accordance with HCMUS CS300 regulations, every meeting must be structured, accountable, and attributed to all active team members.

---

## 1. Meeting Types & Cadence per Sprint

| Meeting Type | When Held | Duration | Required Attendees | Key Deliverables |
|---|---|---|---|---|
| **Sprint Planning** | Day 1 of Sprint | 60 - 90 mins | Full Team | Sprint Goal, User Story breakdown, Jira task assignment |
| **Weekly Standup 1** | Middle of Week 1 | 15 - 30 mins | Full Team | Progress audit, 3 Standup Qs per member, blocker removal |
| **Weekly Standup 2** | Middle of Week 2 | 15 - 30 mins | Full Team | Pre-submission audit, testing progress, blocker removal |
| **Sprint Retrospective** | End of Sprint | 45 - 60 mins | Full Team | Deliverable evaluation, 5 Retrospective questions |

---

## 2. File Naming Standard

```text
MeetingNotes_Sprint[X]_[MeetingType]_YYYY-MM-DD.md
```
*Examples:*
- `MeetingNotes_Sprint1_Planning_2026-10-02.md`
- `MeetingNotes_Sprint1_Standup1_2026-10-07.md`
- `MeetingNotes_Sprint1_Retrospective_2026-10-14.md`

---

## 3. Standard Meeting Minutes Schema

When writing child meeting notes, use this standardized markdown schema:

```markdown
# Meeting Minutes: Sprint [X] - [Meeting Type]
> *Performed by: Phạm Định Tiểu Long | Reviewed by: Toàn thể nhóm | Edited by: Ngô Thái Hòa*

## 1. Meeting Metadata
- **Date & Time:** YYYY-MM-DD, HH:MM - HH:MM
- **Location:** Google Meet / HCMUS Library
- **Chair:** Phạm Định Tiểu Long (PM)
- **Secretary:** Ngô Thái Hòa
- **Attendees:**
  1. Phạm Định Tiểu Long (24120087) - PM
  2. Nguyễn Phú Đạt (24120038) - Frontend Lead
  3. Nguyễn Lê Đức Nhật (24120403) - Backend Lead
  4. Vương Đắc Gia Khiêm (24120342) - QA Lead
  5. Ngô Thái Hòa (24120051) - AI/Doc Lead

## 2. Agenda Items
1. Review sprint commitments and milestones.
2. Individual status reports.
3. Discussion of cross-module technical challenges.
4. Action items and deadlines.

## 3. Discussion Points & Decisions Made
- **Decision 1:** [Context & outcome]
- **Decision 2:** [Context & outcome]

## 4. Action Items & Jira Mapping
| Action Item | Assignee | Jira Issue Key | Deadline | Status |
|---|---|---|---|---|
| Implement JWT auth filter | Nguyễn Lê Đức Nhật | `FIN-12` | YYYY-MM-DD | In Progress |
```
