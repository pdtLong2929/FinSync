# Functional Requirements Specification

> *Performed by: [Name] | Reviewed by: [Name] | Edited by: [Name]*

## 1. Authentication & Security

### FR-1.1: User Registration
- **Description:** The system shall allow new users to register with email, password, and display name.
- **Priority:** High
- **Actor:** Regular User

### FR-1.2: User Login
- **Description:** The system shall authenticate users using email and password, returning a JWT token.
- **Priority:** High
- **Actor:** Regular User, Administrator

### FR-1.3: Forgot Password
- **Description:** The system shall allow users to reset their password via email verification.
- **Priority:** Medium
- **Actor:** Regular User

### FR-1.4: Change Password
- **Description:** The system shall allow authenticated users to change their password.
- **Priority:** Medium
- **Actor:** Regular User

---

## 2. Profile Management

### FR-2.1: Update Profile Information
- **Description:** The system shall allow users to update their display name, avatar, and default currency (VND, USD).
- **Priority:** Medium
- **Actor:** Regular User

---

## 3. Wallet & Personal Account Management

### FR-3.1: Create Wallet
- **Description:** The system shall allow users to create bookkeeping wallets (cash, card, bank) with a name, type, initial balance, and currency.
- **Priority:** High
- **Actor:** Regular User

### FR-3.2: View Total Assets
- **Description:** The system shall display the total assets across all wallets.
- **Priority:** Medium
- **Actor:** Regular User

---

## 4. Personal Transaction Tracking

### FR-4.1: Record Transaction
- **Description:** The system shall allow users to record income/expense transactions with amount, category, date, note, and optional receipt photo.
- **Priority:** High
- **Actor:** Regular User

### FR-4.2: Categorize Transaction
- **Description:** The system shall allow users to assign a category (from default or custom categories) to each transaction.
- **Priority:** High
- **Actor:** Regular User

---

## 5. Group Management & Shared Wallets

### FR-5.1: Create Group
- **Description:** The system shall allow users to create a group with a name and description. The creator becomes the Owner.
- **Priority:** High
- **Actor:** Regular User (Owner)

### FR-5.2: Invite Members
- **Description:** The system shall allow group owners to invite members via shareable link or invitation code.
- **Priority:** High
- **Actor:** Regular User (Owner)

### FR-5.3: Group Fund
- **Description:** The system shall maintain a group fund (shared wallet) that is independent of members' personal wallets.
- **Priority:** High
- **Actor:** Regular User (Owner, Member)

---

## 6. Smart Debt Split & Settlement

### FR-6.1: Split Expense
- **Description:** The system shall support splitting group expenses using: equal split, percentage-based split, or custom amount split.
- **Priority:** High
- **Actor:** Regular User (Owner, Member)

### FR-6.2: Debt Board
- **Description:** The system shall display a "who owes whom" debt summary for each group.
- **Priority:** High
- **Actor:** Regular User

### FR-6.3: Payment Reminder
- **Description:** The system shall allow users to send debt reminder notifications to other members.
- **Priority:** Medium
- **Actor:** Regular User

### FR-6.4: Mark as Paid
- **Description:** The system shall allow users to mark debts as settled (paid outside the app).
- **Priority:** High
- **Actor:** Regular User

---

## 7. Budget Planning & Alerting

### FR-7.1: Set Budget Limit
- **Description:** The system shall allow users to set spending limits per category on a weekly or monthly basis.
- **Priority:** Medium
- **Actor:** Regular User

### FR-7.2: Budget Alerts
- **Description:** The system shall display visual alerts when spending reaches 80% and 100% of the budget limit.
- **Priority:** Medium
- **Actor:** Regular User

---

## 8. Financial Reporting & Analytics

### FR-8.1: Interactive Charts
- **Description:** The system shall provide interactive charts analyzing cash flow (income vs. expense over time, by category).
- **Priority:** Medium
- **Actor:** Regular User

### FR-8.2: Group Trip Summary
- **Description:** The system shall generate a spending summary report after a group trip/event.
- **Priority:** Low
- **Actor:** Regular User

### FR-8.3: Export Report
- **Description:** The system shall allow users to export reports in PDF and Excel formats.
- **Priority:** Low
- **Actor:** Regular User

---

## 9. Savings Goal Management

### FR-9.1: Create Savings Goal
- **Description:** The system shall allow users to set long-term financial savings goals with a target amount and deadline.
- **Priority:** Medium
- **Actor:** Regular User

### FR-9.2: Track Savings Progress
- **Description:** The system shall display progress toward each savings goal.
- **Priority:** Medium
- **Actor:** Regular User

---

## 10. System Administration (Admin Panel)

### FR-10.1: Manage Users
- **Description:** The system shall allow administrators to view, search, lock, and unlock user accounts.
- **Priority:** High
- **Actor:** Administrator

### FR-10.2: Manage Default Categories
- **Description:** The system shall allow administrators to manage default income/expense categories available to all users.
- **Priority:** Medium
- **Actor:** Administrator

### FR-10.3: System Dashboard
- **Description:** The system shall provide a dashboard with system-wide statistics (total users, transactions, active groups, etc.).
- **Priority:** Medium
- **Actor:** Administrator

---

## AI Feature: AI Financial Advisor

### FR-AI.1: Spending Analysis
- **Description:** The system shall analyze the user's previous month's spending history, detect anomalies, and identify categories with unusual increases.
- **Priority:** Medium
- **Actor:** Regular User

### FR-AI.2: Budget Optimization
- **Description:** The system shall suggest an optimal budget allocation for the next month based on income and savings goals.
- **Priority:** Medium
- **Actor:** Regular User
