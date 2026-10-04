# FinSync - Use Case Specifications Catalog & AI Generation Guide

> **AI Assistant Context & Prompting Guide:**
> When prompted to generate or update any Use Case document (`UCxx_[Name].md`) in this directory, follow this guide strictly. Every Use Case must reflect FinSync's domain rules: pure bookkeeping, manual ledger, strict isolation between personal wallets and shared group debts, and automated AI financial advisory.

---

## 1. Master Use Case Catalog

| Use Case ID | Use Case Name | Primary Actor | Functional Group | Priority | Target File |
|---|---|---|---|---|---|
| `UC-01` | User Registration & Authentication | Regular User | `GRP-01` Auth & Security | Must Have | `UC01_UserAuthentication.md` |
| `UC-02` | Manage User Profile & Preferences | Regular User | `GRP-02` Profile Management | Should Have | `UC02_ManageProfile.md` |
| `UC-03` | Create & Manage Personal Wallets | Regular User | `GRP-03` Personal Wallets | Must Have | `UC03_ManageWallets.md` |
| `UC-04` | Log Personal Income & Expense | Regular User | `GRP-04` Personal Transactions | Must Have | `UC04_LogPersonalTransaction.md` |
| `UC-05` | Create & Manage Expense Group | Group Owner | `GRP-05` Group Management | Must Have | `UC05_ManageExpenseGroup.md` |
| `UC-06` | Split Group Expense | Group Member | `GRP-06` Smart Debt Split | Must Have | `UC06_SplitGroupExpense.md` |
| `UC-07` | Settle Group Debt | Group Member | `GRP-06` Smart Debt Split | Must Have | `UC07_SettleGroupDebt.md` |
| `UC-08` | Configure Category Budget & Alerts | Regular User | `GRP-07` Budget Planning | Should Have | `UC08_ConfigureBudgetAlerts.md` |
| `UC-09` | View Cashflow & Analytics Reports | Regular User | `GRP-08` Financial Reporting | Should Have | `UC09_ViewFinancialAnalytics.md` |
| `UC-10` | Track Long-Term Savings Goal | Regular User | `GRP-09` Savings Goals | Could Have | `UC10_TrackSavingsGoal.md` |
| `UC-11` | Moderate Accounts & System Categories | Administrator | `GRP-10` System Admin | Should Have | `UC11_AdminUserManagement.md` |
| `UC-12` | Generate Monthly AI Financial Advice | Regular User | AI Feature | Must Have | `UC12_GenerateAIFinancialAdvice.md` |

---

## 2. Standard Use Case Markdown Schema

When writing any `UCxx_[Name].md`, use this exact schema:

```markdown
# Use Case Specification: UC-[ID] - [Use Case Name]

> *Performed by: [Author Name] | Reviewed by: [Reviewer Name] | Edited by: [Editor Name]*

## 1. Description & Meta
- **Use Case ID:** `UC-[ID]`
- **Use Case Name:** [Title]
- **Primary Actor:** Regular User / Group Owner / Group Member / System Administrator
- **Secondary Actors:** AI Microservice / Notification Engine
- **Priority:** Must Have / Should Have / Could Have
- **Complexity:** Low / Medium / High

## 2. Preconditions & Postconditions
- **Preconditions:**
  - System prerequisites, authenticated state, required existing records.
- **Postconditions (Success):**
  - Guaranteed database state, balance adjustments, notifications dispatched.
- **Postconditions (Failure):**
  - State unchanged, audit log recorded, error message shown.

## 3. Main Success Scenario (Basic Flow)
1. **Actor:** Navigates to [Screen] and selects [Action].
2. **System:** Fetches and presents [Form/Data].
3. **Actor:** Fills in [Fields] and submits.
4. **System:** Validates input parameters (format, boundary, authorization).
5. **System:** Persists record to database and recalculates ledger.
6. **System:** Returns confirmation message and updates UI state.

## 4. Alternative Flows
- **4a. [Condition e.g., Uneven Split Option]:**
  1. Actor selects custom percentage or exact amount split.
  2. System verifies sum matches total expense exactly.
  3. Resumes at step 5 of Main Flow.

## 5. Exception Flows
- **5a. [Condition e.g., Network Timeout / Invalid Data]:**
  1. System detects validation failure (e.g., negative amount).
  2. System halts transaction and displays inline error.
  3. Actor corrects input or cancels.

## 6. Business Rules & Special Constraints
- **BR-[ID]:** Specific algorithmic formula (e.g., debt minimization rounding rules).
- **Security:** Strict authorization check (e.g., non-members cannot view group expenses).
```
