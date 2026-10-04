# FinSync - Requirements Engineering Context & AI Generation Guide

> **AI Assistant Persona & Usage Context:**
> When prompted to generate or update any document inside `docs/requirements/`, adopt the persona of a **Senior Business Analyst & Requirements Engineer** specializing in modern FinTech applications. Read this context guide completely to align with FinSync's domain rules, architectural constraints, and academic formatting standards for HCMUS (Course: CS300 - Introduction to Software Engineering).

---

## 1. Project Domain & Core Business Rules

FinSync is a dual-engine financial management platform engineered for young adults, university students, and shared households.

### 1.1. Core Business Boundaries (Critical Constraints)
- **Pure Bookkeeping Engine:** FinSync operates strictly as a manual and AI-assisted ledger. It **NEVER** connects directly to banking APIs for automated money transfers, never withdraws funds, and never stores real banking credentials or payment cards.
- **Strict Separation of Concerns:**
  - **Personal Finance:** Strictly private to the authenticated user. Encompasses manual personal wallets (Cash, Bank, Credit Card ledgers), categorized transactions, personal budgets, and long-term savings goals.
  - **Group Expense Management:** Public within an authorized group. Handles shared ledgers, bill splitting (equal, exact, percentage), debt balance calculation, and optimal settlement graphs. Payments occur peer-to-peer outside the app; FinSync records the settlement confirmation.
- **AI Financial Advisor:** An automated analytical service that scans monthly personal and group spending patterns, detects anomalies, evaluates budget health, and delivers actionable financial guidance.

### 1.2. Target Actors
1. **Regular User:** End-user tracking personal finances and participating in shared expense groups. Can hold the role of **Group Owner** (creator/manager) or **Group Member**.
2. **Administrator:** System operator accessing the Admin Web Portal to manage user account states (active/suspended), system default transaction categories, and global platform analytics.

### 1.3. The 10 Functional Requirement Groups
1. `GRP-01`: **Authentication & Security** (JWT auth, register, login, password recovery, session validation).
2. `GRP-02`: **Profile Management** (User bio, avatar, currency preference VND/USD, security settings).
3. `GRP-03`: **Wallet & Personal Accounts** (Multi-wallet bookkeeping, balance calculation, net worth overview).
4. `GRP-04`: **Personal Transaction Tracking** (Income, expense, transfer logging, receipt image attachment, category assignment).
5. `GRP-05`: **Group Management & Shared Wallets** (Group creation, invite codes/links, member role management, shared fund ledger).
6. `GRP-06`: **Smart Debt Split & Settlement** (Expense splitting algorithms, peer-to-peer debt ledger, debt simplification, settlement marking).
7. `GRP-07`: **Budget Planning & Alerting** (Category-based spending limits, weekly/monthly cycles, visual threshold alerts at 80% and 100%).
8. `GRP-08`: **Financial Reporting & Analytics** (Interactive cashflow charts, category breakdowns, PDF/Excel report export).
9. `GRP-09`: **Savings Goal Management** (Target amount tracking, target date, milestone progress, deposit allocation).
10. `GRP-10`: **System Administration** (Admin portal, user status management, system categories, high-level metrics).
- **Standalone AI Feature:** **AI Financial Advisor** (Monthly spending anomaly detection, budget optimization suggestions, savings pace analysis via Gemini API).

---

## 2. Directory Structure & Child File Catalog

All requirements artifacts must reside in this directory (`docs/requirements/`):

```text
docs/requirements/
├── README.md                      # This Context & Prompting Guide
├── vision.md                      # High-level product vision, goals, stakeholders & boundaries
├── functional-requirements.md     # Detailed specifications for all 10 Functional Groups
├── non-functional-requirements.md # Quality attributes: Security, Performance, Usability, etc.
├── glossary.md                    # Ubiquitous domain dictionary & terms
└── use-cases/                     # Detailed Use Case Specifications
    ├── README.md                  # Use case catalog & indexing
    ├── UC01_UserAuthentication.md
    ├── UC02_ManageWallets.md
    ├── UC03_LogPersonalTransaction.md
    ├── UC04_ManageExpenseGroup.md
    ├── UC05_SplitGroupExpense.md
    ├── UC06_SettleGroupDebt.md
    ├── UC07_ConfigureBudgetAlerts.md
    ├── UC08_ViewFinancialAnalytics.md
    ├── UC09_TrackSavingsGoal.md
    ├── UC10_AdminUserManagement.md
    └── UC11_GenerateAIFinancialAdvice.md
```

---

## 3. Mandatory Formatting & Academic Standards

Every child document generated in this folder **MUST** comply with these mandatory conventions:
1. **Language:** Professional Technical English throughout.
2. **Attribution Line:** Placed immediately beneath every top-level or primary section header:
   ```markdown
   > *Performed by: [Author Name] | Reviewed by: [Reviewer Name] | Edited by: [Editor Name]*
   ```
3. **MoSCoW Prioritization:** Every functional requirement must carry a priority tag: `[Must Have]`, `[Should Have]`, `[Could Have]`, or `[Won't Have]`.
4. **Traceability:** Requirements must use standard identifiers (`FR-01-01`, `NFR-SEC-01`, `UC-05`).

---

## 4. Templates for AI Document Generation

When prompting an AI to generate child documents, instruct it to use the following standardized schemas:

### 4.1. Template for `functional-requirements.md`
```markdown
### FR-[Group]-[ID]: [Requirement Title]
> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

- **Priority:** Must Have / Should Have / Could Have
- **Target Actor:** Regular User / Group Owner / Admin
- **User Story:**
  > As a [Role],
  > I want to [Capability/Action],
  > So that [Business Value/Benefit].
- **Functional Scope & Rules:**
  - Detailed rule 1...
  - Detailed rule 2...
- **Acceptance Criteria (Gherkin Format):**
  - **Scenario:** [Title]
    - **Given** [Initial context]
    - **When** [User triggers action]
    - **Then** [Expected system outcome]
```

### 4.2. Template for `non-functional-requirements.md`
```markdown
### NFR-[CATEGORY]-[ID]: [Quality Attribute Title]
> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

- **Category:** Security / Performance / Usability / Reliability / Maintainability
- **Metric / Benchmark:** Measurable target (e.g., API P95 latency < 300ms, JWT expiry = 15m)
- **Specification:** Exhaustive technical requirement.
- **Verification Method:** Load testing / Static analysis / Security audit.
```

### 4.3. Template for `use-cases/UCxx_[Name].md`
```markdown
# Use Case: UC-[ID] - [Use Case Name]
> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

| Attribute | Specification |
|---|---|
| **ID** | `UC-[ID]` |
| **Primary Actor** | Regular User / Group Owner / Admin |
| **Trigger** | What initiates this interaction |
| **Preconditions** | Required system state before execution |
| **Postconditions** | Guaranteed state after successful execution |

### 1. Main Success Scenario (Basic Flow)
1. User navigates to...
2. System presents...
3. User enters/selects...
4. System validates and commits...

### 2. Alternative Flows
- **2a. [Condition]:**
  1. System executes fallback...
  2. Resumes at step X of Basic Flow.

### 3. Exception Flows
- **3a. [Error Condition]:**
  1. System displays validation message...
  2. Transaction is rolled back.

### 4. Business Rules & UI Notes
- Specific calculation formula or UI component behavior.
```

---

## 5. Requirements Quality Checklist (AI Self-Review)

Before submitting or generating any requirements file, verify:
- [ ] Does it respect the **"Pure Bookkeeping"** invariant (no automated bank withdrawals)?
- [ ] Are Personal Wallets strictly separated from Group Ledgers?
- [ ] Are all requirements uniquely tagged (`FR-xx`, `NFR-xx`, `UC-xx`)?
- [ ] Is every section attributed with the required HCMUS team attribution line?
- [ ] Are edge cases (network failure, zero balance, uneven debt split) accounted for?
