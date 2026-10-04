# FinSync - Documentation Hub & AI Context Architecture

> **Master Architecture Guide:**
> This repository documentation is structured into **4 specialized disciplines**. Each subfolder contains an authoritative `README.md` that serves as a **Domain Context & AI Prompt Guide**. Whenever asking an AI agent (or onboarding a new engineer) to write or update child documents, instruct them to read the corresponding subfolder's `README.md` first.

---

## 1. Documentation Map & Navigation

```text
docs/
├── README.md                      # [You are here] Master Documentation Directory
├── requirements/                  # Requirements Engineering & Specifications
│   └── README.md                  # Context guide for BA, Use Cases & Specifications
├── analysis-and-design/           # System Architecture & Technical Design
│   └── README.md                  # Context guide for Architecture, C4, API & Database
├── test/                          # Quality Assurance & Testing
│   └── README.md                  # Context guide for QA Lead, Test Plan & Test Cases
└── management/                    # Agile Scrum Governance & Project Reporting
    └── README.md                  # Context guide for PM, Standups & Retrospectives
```

---

## 2. Quick Directory Overview

| Discipline | Subfolder | Primary Lead | Key Deliverables Governed |
|---|---|---|---|
| **Requirements** | [`docs/requirements/`](file:///D:/FinSync/FinSync/docs/requirements/README.md) | Đặng Gia Hòa (Doc Lead) | `vision.md`, `functional-requirements.md`, `non-functional-requirements.md`, `glossary.md`, `use-cases/` |
| **Analysis & Design** | [`docs/analysis-and-design/`](file:///D:/FinSync/FinSync/docs/analysis-and-design/README.md) | Nguyễn Lê Đức Nhật (Backend) & Nguyễn Phú Đạt (UI/UX) | `architecture.md` (C4), `api-design.md`, `database-design.md` (ERD), `class-diagrams/`, `sequence-diagrams/`, `ui-design/` |
| **Testing & QA** | [`docs/test/`](file:///D:/FinSync/FinSync/docs/test/README.md) | Vương Đắc Gia Khiêm (QA Lead) | `test-plan.md`, `test-cases/` (all 10 groups + AI), `test-results/`, defect tracking |
| **Project Management** | [`docs/management/`](file:///D:/FinSync/FinSync/docs/management/README.md) | Phạm Đình Tiểu Long (PM) | `weekly-reports/`, `meeting-notes/`, Sprint Planning, Standups (3 Qs), Retrospectives (5 Qs) |

---

## 3. How to Prompt AI Assistants Using These Context Files

When requesting an AI to generate child documentation, use the following prompt pattern for optimal results:

```text
Prompt Template:
"Read the context guide in docs/[discipline]/README.md thoroughly.
Using the personas, domain invariants, templates, and attribution standards defined in that file,
please generate docs/[discipline]/[target-file.md] for FinSync.
Ensure full adherence to the 10 functional groups and technical stack."
```

### Examples:
- **To write a Use Case:**
  > *"Please read `docs/requirements/README.md`. Using the Use Case template and domain rules, write `docs/requirements/use-cases/UC05_SplitGroupExpense.md` covering equal, percentage, and exact split options."*
- **To write API Endpoints:**
  > *"Please read `docs/analysis-and-design/README.md`. Using the API specification template, write `docs/analysis-and-design/api-design.md` detailing all endpoints for Personal Wallets (GRP-03) and Transactions (GRP-04)."*
- **To write Test Cases:**
  > *"Please read `docs/test/README.md`. Using the test case schema, write `docs/test/test-cases/TC_GRP06_SMART_SPLIT.md` covering zero-sum validation and debt simplification."*
- **To write Weekly Scrum Notes:**
  > *"Please read `docs/management/README.md`. Write the meeting minutes for Sprint 1 Weekly Scrum 1 adhering to the 3 standup questions per member."*

---

## 4. Universal Documentation Rules (All Folders)
1. **Language:** English is mandatory for all documentation.
2. **Attribution:** Every primary section must have an author attribution line:
   `> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*`
3. **Diagrams:** Use **Mermaid** syntax exclusively for all diagrams.
4. **Data Integrity:** Uphold the **Pure Bookkeeping** principle (no real banking connections or automated fund transfers).
