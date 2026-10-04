# FinSync - Functional Requirements Specification (FRS)

> *Performed by: Ngô Thái Hòa | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*

---

## 1. Document Overview & System Scope

### 1.1. Executive Summary
This document specifies the complete functional requirements for **FinSync**, an integrated cross-platform financial platform developed by **Group 04** for **CS300 (Introduction to Software Engineering)** at **HCMUS**. FinSync consolidates personal cashflow bookkeeping, multi-wallet net worth tracking, intelligent group expense splitting with debt minimization, and an automated AI-powered financial advisory engine into a single unified ecosystem.

### 1.2. The Pure Bookkeeping Invariant (Critical Architectural Boundary)
FinSync operates strictly as a **manual-entry bookkeeping, debt calculation, and financial intelligence ledger**. 
- The system **DOES NOT** connect directly to banking gateway APIs for automated withdrawals.
- The system **DOES NOT** initiate wire transfers or interact directly with banking institutions.
- All real-world settlements occur peer-to-peer (via bank transfer, cash, or e-wallets) outside the platform; FinSync records the confirmation, updates internal ledgers, and cancels out debt obligations mathematically.

### 1.3. System Actors
1. **Regular User (`ACT-USER`):** Individuals (university students, young professionals, roommates) who track personal cashflow, manage personal wallets, configure budgets, set savings goals, and participate in shared expense groups.
   - **Group Owner (`ACT-OWNER`):** A Regular User who creates an expense group, manages member access, and oversees group configurations.
   - **Group Member (`ACT-MEMBER`):** A Regular User participating in a shared expense group with permissions to log group expenses and settle debts.
2. **System Administrator (`ACT-ADMIN`):** An authorized platform maintainer who accesses the Web Admin Portal to moderate user accounts, manage system-wide default transaction categories, and inspect platform health metrics.
3. **AI Microservice (`ACT-AI`):** An automated background service powered by Google Gemini 1.5 that processes monthly transaction logs to synthesize spending anomaly alerts and budget recommendations.

### 1.4. Requirement Prioritization (MoSCoW Method)
- **[Must Have]:** Core capabilities critical for the viable operation of FinSync (Sprint 1 / PA1-PA2).
- **[Should Have]:** Important features adding significant user value (PA2-PA3).
- **[Could Have]:** Valuable enhancements implemented if time and resources permit (PA3-PA4).
- **[Won't Have]:** Out-of-scope for the academic semester (e.g., real banking API auto-debits, crypto wallet integration).

---

## 2. Requirements Traceability Matrix

| Requirement ID | Module / Feature Group | Requirement Title | Target Actor | Priority |
|---|---|---|---|---|
| `FR-AUTH-01` | `GRP-01` Authentication & Security | User Account Registration | `ACT-USER` | **Must Have** |
| `FR-AUTH-02` | `GRP-01` Authentication & Security | User Login & JWT Session Management | `ACT-USER`, `ACT-ADMIN` | **Must Have** |
| `FR-AUTH-03` | `GRP-01` Authentication & Security | Password Recovery via Email Token | `ACT-USER` | **Should Have** |
| `FR-AUTH-04` | `GRP-01` Authentication & Security | User Logout & Token Revocation | `ACT-USER`, `ACT-ADMIN` | **Must Have** |
| `FR-PROF-01` | `GRP-02` Profile Management | View and Update User Profile | `ACT-USER` | **Must Have** |
| `FR-PROF-02` | `GRP-02` Profile Management | Password Change & Security Verification | `ACT-USER` | **Must Have** |
| `FR-PROF-03` | `GRP-02` Profile Management | Default Currency & Locale Configuration | `ACT-USER` | **Must Have** |
| `FR-WALL-01` | `GRP-03` Wallet & Personal Accounts | Create and Configure Personal Wallets | `ACT-USER` | **Must Have** |
| `FR-WALL-02` | `GRP-03` Wallet & Personal Accounts | Edit, Archive, and Delete Wallets | `ACT-USER` | **Should Have** |
| `FR-WALL-03` | `GRP-03` Wallet & Personal Accounts | Net Worth Dashboard Aggregation | `ACT-USER` | **Must Have** |
| `FR-WALL-04` | `GRP-03` Wallet & Personal Accounts | Inter-Wallet Internal Transfer | `ACT-USER` | **Must Have** |
| `FR-TXN-01` | `GRP-04` Personal Transactions | Log Personal Income and Expense | `ACT-USER` | **Must Have** |
| `FR-TXN-02` | `GRP-04` Personal Transactions | Custom Category & Subcategory Tagging | `ACT-USER` | **Must Have** |
| `FR-TXN-03` | `GRP-04` Personal Transactions | Receipt Photo Attachment | `ACT-USER` | **Should Have** |
| `FR-TXN-04` | `GRP-04` Personal Transactions | Transaction Filtering, Search & Sorting | `ACT-USER` | **Must Have** |
| `FR-GRP-01` | `GRP-05` Group Management | Create and Configure Expense Group | `ACT-OWNER` | **Must Have** |
| `FR-GRP-02` | `GRP-02` Group Management | Member Invitation via Code and Deep Link | `ACT-OWNER`, `ACT-USER` | **Must Have** |
| `FR-GRP-03` | `GRP-05` Group Management | Group Shared Ledger & Member Directory | `ACT-MEMBER` | **Must Have** |
| `FR-GRP-04` | `GRP-05` Group Management | Member Departure & Group Archival | `ACT-OWNER`, `ACT-MEMBER` | **Should Have** |
| `FR-SPLT-01` | `GRP-06` Smart Debt Split | Multi-Mode Bill Splitting (Equal, Exact, %) | `ACT-MEMBER` | **Must Have** |
| `FR-SPLT-02` | `GRP-06` Smart Debt Split | Consolidated Who-Owes-Whom Balance Matrix | `ACT-MEMBER` | **Must Have** |
| `FR-SPLT-03` | `GRP-06` Smart Debt Split | Cyclic Debt Graph Simplification Algorithm | `ACT-MEMBER` | **Must Have** |
| `FR-SPLT-04` | `GRP-06` Smart Debt Split | Debt Settlement Recording & Confirmation | `ACT-MEMBER` | **Must Have** |
| `FR-SPLT-05` | `GRP-06` Smart Debt Split | Payment Reminder Notifications | `ACT-MEMBER` | **Should Have** |
| `FR-BUDG-01` | `GRP-07` Budget Planning | Category Spending Budget Setup | `ACT-USER` | **Must Have** |
| `FR-BUDG-02` | `GRP-07` Budget Planning | Real-Time Consumption Tracking Progress | `ACT-USER` | **Must Have** |
| `FR-BUDG-03` | `GRP-07` Budget Planning | Threshold Alerting (80% and 100%) | `ACT-USER` | **Must Have** |
| `FR-REP-01` | `GRP-08` Financial Reporting | Interactive Cashflow Charts (Bar / Line) | `ACT-USER` | **Must Have** |
| `FR-REP-02` | `GRP-08` Financial Reporting | Category Expense Breakdown (Pie Chart) | `ACT-USER` | **Must Have** |
| `FR-REP-03` | `GRP-08` Financial Reporting | Group Event Financial Summary Report | `ACT-MEMBER` | **Should Have** |
| `FR-REP-04` | `GRP-08` Financial Reporting | Export Financial Reports (PDF / Excel) | `ACT-USER`, `ACT-MEMBER` | **Should Have** |
| `FR-SAV-01` | `GRP-09` Savings Goal Management | Long-Term Savings Goal Setup | `ACT-USER` | **Should Have** |
| `FR-SAV-02` | `GRP-09` Savings Goal Management | Fund Allocation from Personal Wallets | `ACT-USER` | **Should Have** |
| `FR-SAV-03` | `GRP-09` Savings Goal Management | Milestone Tracking & Completion Alert | `ACT-USER` | **Could Have** |
| `FR-ADM-01` | `GRP-10` System Administration | Admin Portal Authentication & Session | `ACT-ADMIN` | **Must Have** |
| `FR-ADM-02` | `GRP-10` System Administration | User Account Management (Lock / Unlock) | `ACT-ADMIN` | **Must Have** |
| `FR-ADM-03` | `GRP-10` System Administration | System Default Category Configuration | `ACT-ADMIN` | **Must Have** |
| `FR-ADM-04` | `GRP-10` System Administration | Aggregate Platform Statistics Dashboard | `ACT-ADMIN` | **Should Have** |
| `FR-AI-01` | Standalone AI Feature | Monthly Spending Automated History Scan | `ACT-USER`, `ACT-AI` | **Must Have** |
| `FR-AI-02` | Standalone AI Feature | Spending Anomaly & Budget Spike Detection | `ACT-USER`, `ACT-AI` | **Must Have** |
| `FR-AI-03` | Standalone AI Feature | Optimal Budget Allocation Suggestions | `ACT-USER`, `ACT-AI` | **Must Have** |
| `FR-AI-04` | Standalone AI Feature | Savings Goal Trajectory Assessment | `ACT-USER`, `ACT-AI` | **Should Have** |

---

## 3. Detailed Specifications by Functional Group

### 3.1. GRP-01: Authentication & Security

#### FR-AUTH-01: User Account Registration
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a new user,  
  > I want to register an account using my email, password, and full name,  
  > So that I can start tracking my personal and group finances securely.
- **Functional Scope & Business Rules:**
  1. System requires: Full Name, Email, Password, and Password Confirmation.
  2. Email must be validated against RFC 5322 standard and must be unique in the system.
  3. Passwords must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.
  4. Passwords must be cryptographically hashed using BCrypt before persisting.
- **Acceptance Criteria (Gherkin):**
  - **Scenario:** Successful User Registration
    - **Given** a user provides a valid, unregistered email `alice@example.com` and password `SecureP@ss123`
    - **When** the user submits the registration form
    - **Then** the system creates a new user record with status `ACTIVE`
    - **And** returns HTTP `201 Created` with an initial JWT access and refresh token pair.
  - **Scenario:** Duplicate Email Registration
    - **Given** an existing user registered with `alice@example.com`
    - **When** another user attempts to register with `alice@example.com`
    - **Then** the system rejects registration with HTTP `409 Conflict` and message `Email already registered`.

#### FR-AUTH-02: User Login & JWT Session Management
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`), Admin (`ACT-ADMIN`)
- **User Story:**
  > As an existing user,  
  > I want to log in securely with my credentials,  
  > So that I can access my private financial data across sessions.
- **Functional Scope & Business Rules:**
  1. Authenticates against email and BCrypt-hashed password.
  2. Issues a short-lived JWT Access Token (lifespan: 15 minutes) and a long-lived Refresh Token (lifespan: 7 days).
  3. Role claim (`ROLE_USER` or `ROLE_ADMIN`) is embedded within the JWT token.
  4. Accounts marked as `LOCKED` by an administrator cannot authenticate.
- **Acceptance Criteria (Gherkin):**
  - **Scenario:** Successful Login
    - **Given** an active account with email `bob@example.com` and password `Password123!`
    - **When** the user submits valid credentials
    - **Then** the system returns HTTP `200 OK` with JWT Access Token and Refresh Token.
  - **Scenario:** Locked Account Login Attempt
    - **Given** an account with status `LOCKED`
    - **When** the user attempts to log in with valid credentials
    - **Then** the system returns HTTP `403 Forbidden` with message `Account has been suspended by an administrator`.

#### FR-AUTH-03: Password Recovery via Email Token
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user who forgot their password,  
  > I want to request a reset link sent to my email,  
  > So that I can regain access to my account securely.
- **Functional Scope & Business Rules:**
  1. User submits registered email address.
  2. System generates a secure one-time token (TTL: 15 minutes) and sends a reset link.
  3. Reset link allows user to set a new password complying with security complexity rules.
  4. Token is invalidated immediately upon successful password reset.

#### FR-AUTH-04: User Logout & Token Revocation
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`), Admin (`ACT-ADMIN`)
- **User Story:**
  > As a logged-in user,  
  > I want to log out of my current session,  
  > So that unauthorized individuals cannot access my financial records on this device.
- **Functional Scope & Business Rules:**
  1. Client sends revocation request with current refresh token.
  2. Server adds the refresh token to a revocation blacklist (Redis or DB blacklist table).
  3. Client wipes all stored access/refresh tokens and local session caches.

---

### 3.2. GRP-02: Profile Management

#### FR-PROF-01: View and Update User Profile
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to view and edit my personal details (display name, phone number, avatar),  
  > So that my identity is accurate and recognizable in expense groups.
- **Functional Scope & Business Rules:**
  1. Displays user's email (read-only), display name, phone number, and avatar image.
  2. Allows uploading avatar images (JPEG, PNG, WebP up to 5MB).
  3. Updates propagate immediately to group member lists.

#### FR-PROF-02: Password Change & Security Verification
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As an authenticated user,  
  > I want to update my account password by verifying my existing password,  
  > So that I can maintain optimal security over my credentials.
- **Functional Scope & Business Rules:**
  1. User must provide the current password and the new password twice.
  2. System verifies current password against hash before updating.
  3. Upon change, all other active refresh tokens for the user are invalidated.

#### FR-PROF-03: Default Currency & Locale Configuration
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to configure my preferred display currency (`VND` or `USD`),  
  > So that all personal summaries and reports use my native monetary format.
- **Functional Scope & Business Rules:**
  1. Default system currency is `VND` (Vietnamese Đồng). Supports `USD` (US Dollar).
  2. Formats numbers according to locale: VND uses dot thousand separators (`100.000 ₫`); USD uses commas and cents (`$1,000.00`).

---

### 3.3. GRP-03: Wallet & Personal Account Management

#### FR-WALL-01: Create and Configure Personal Wallets
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to create distinct bookkeeping wallets (Cash, Bank Account, Credit Card),  
  > So that I can keep track of where my funds are held.
- **Functional Scope & Business Rules:**
  1. Wallet attributes: Name, Wallet Type (`CASH`, `BANK_ACCOUNT`, `CREDIT_CARD`, `E_WALLET`), Initial Balance, Currency (`VND`/`USD`), and Icon/Color.
  2. **Pure Bookkeeping Invariant:** System does NOT link to real bank APIs. Balances reflect user-entered numbers.
  3. Initial balance automatically records a baseline transaction.
- **Acceptance Criteria (Gherkin):**
  - **Scenario:** Create Cash Wallet
    - **Given** a user creates a wallet named "Tiền mặt" with type `CASH` and initial balance `1.500.000 VND`
    - **When** the wallet is saved
    - **Then** the wallet balance displays `1.500.000 VND` and appears in the user's wallet list.

#### FR-WALL-02: Edit, Archive, and Delete Wallets
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to edit wallet details or archive inactive wallets,  
  > So that my wallet overview remains tidy without deleting historical transaction data.
- **Functional Scope & Business Rules:**
  1. Name and icon can be edited at any time.
  2. Wallets with existing transactions cannot be hard-deleted; they can only be `ARCHIVED` (soft-delete).
  3. Archived wallets are hidden from the active transaction entry dropdown.

#### FR-WALL-03: Net Worth Dashboard Aggregation
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to see a consolidated Net Worth calculation on my dashboard,  
  > So that I immediately understand my overall financial standing.
- **Functional Scope & Business Rules:**
  1. $	ext{Net Worth} = \sum (	ext{Assets: Cash, Bank, E-Wallet}) - \sum (	ext{Liabilities: Credit Card Balances})$.
  2. Dashboard displays total Net Worth, Total Assets, and Total Liabilities in the user's default currency.

#### FR-WALL-04: Inter-Wallet Internal Transfer
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to record money transfers between my own wallets (e.g., withdrawing cash from an ATM),  
  > So that individual balances adjust accurately without affecting my overall income/expense totals.
- **Functional Scope & Business Rules:**
  1. Requires: Source Wallet, Destination Wallet, Amount, Date, Transfer Fee (optional).
  2. Deducts amount (+ fee) from Source Wallet and credits amount to Destination Wallet within an atomic database transaction.
  3. Internal transfers are excluded from general Income/Expense cashflow reports.

---

### 3.4. GRP-04: Personal Transaction Tracking

#### FR-TXN-01: Log Personal Income and Expense
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to quickly record an income or expense transaction with an amount, wallet, category, and date,  
  > So that my financial ledger stays up to date.
- **Functional Scope & Business Rules:**
  1. Transaction fields: Type (`INCOME` / `EXPENSE`), Amount (> 0), Category, Wallet, Timestamp, Note (optional), Image Receipt (optional).
  2. Automatically adjusts the associated wallet balance:
     - `INCOME`: $	ext{Balance}_{	ext{new}} = 	ext{Balance}_{	ext{old}} + 	ext{Amount}$
     - `EXPENSE`: $	ext{Balance}_{	ext{new}} = 	ext{Balance}_{	ext{old}} - 	ext{Amount}$
  3. Evaluates active budget limits for the selected category immediately upon saving.

#### FR-TXN-02: Custom Category & Subcategory Tagging
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to assign pre-defined or custom categories (e.g., Food & Beverage, Rent, Transportation, Salary),  
  > So that I can analyze granular spending habits.
- **Functional Scope & Business Rules:**
  1. Default system categories provided (Food, Utilities, Education, Healthcare, Entertainment, Shopping).
  2. Users can create custom categories with custom icons and colors.
  3. Categories are classified strictly as `INCOME_CATEGORY` or `EXPENSE_CATEGORY`.

#### FR-TXN-03: Receipt Photo Attachment
> *Performed by: Ngô Thái Hòa | Reviewed by: Vương Đắc Gia Khiêm | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to attach a photo of my physical receipt to a transaction,  
  > So that I have verifiable proof and visual context for past purchases.
- **Functional Scope & Business Rules:**
  1. Supports capturing via mobile camera or selecting from gallery.
  2. Client compresses images before uploading to cloud/backend storage (max size 2MB, formats: JPEG, PNG).

#### FR-TXN-04: Transaction Filtering, Search & Sorting
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to search and filter my transaction history by date range, category, wallet, and keyword,  
  > So that I can easily locate specific historical transactions.
- **Functional Scope & Business Rules:**
  1. Filtering parameters: Date range (This Week, This Month, Custom), Category ID, Wallet ID, Min/Max Amount.
  2. Free-text search matches against transaction notes.
  3. Sorted chronologically descending by default.

---

### 3.5. GRP-05: Group Management & Shared Wallets

#### FR-GRP-01: Create and Configure Expense Group
> *Performed by: Ngô Thái Hòa | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Group Owner (`ACT-OWNER`)
- **User Story:**
  > As a user,  
  > I want to create a shared expense group (e.g., Roommates, Da Lat Trip, Project Team),  
  > So that I can manage collective bills with other members in a dedicated ledger.
- **Functional Scope & Business Rules:**
  1. Group parameters: Group Name, Description, Group Type (`HOME`, `TRIP`, `COUPLE`, `PROJECT`, `OTHER`), Base Currency (`VND`).
  2. Creator is automatically designated as `GROUP_OWNER`.
  3. The group maintains an independent shared ledger completely isolated from members' personal wallets.

#### FR-GRP-02: Member Invitation via Code and Deep Link
> *Performed by: Ngô Thái Hòa | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Group Owner (`ACT-OWNER`), Regular User (`ACT-USER`)
- **User Story:**
  > As a group owner,  
  > I want to invite members by sharing a 6-character alphanumeric code or a direct deep link,  
  > So that my friends can join the group without friction.
- **Functional Scope & Business Rules:**
  1. System generates a unique 6-character invite code (e.g., `SYNC88`) and corresponding universal link.
  2. Users entering the code or opening the link are validated and added as `GROUP_MEMBER`.
  3. Owner can regenerate or invalidate the invite code at any time.

#### FR-GRP-03: Group Shared Ledger & Member Directory
> *Performed by: Ngô Thái Hòa | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Group Member (`ACT-MEMBER`)
- **User Story:**
  > As a group member,  
  > I want to view the group's chronological activity feed and member list,  
  > So that I stay informed of all collective expenses and settlements.
- **Functional Scope & Business Rules:**
  1. Shows real-time chronological list of all group expenses and debt settlements.
  2. Shows member list with current individual net balance (positive = owed money, negative = owes money).

#### FR-GRP-04: Member Departure & Group Archival
> *Performed by: Ngô Thái Hòa | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** Group Owner (`ACT-OWNER`), Group Member (`ACT-MEMBER`)
- **User Story:**
  > As a member or owner,  
  > I want to leave or archive a group once all debts are settled,  
  > So that completed trips or past room leases are closed cleanly.
- **Functional Scope & Business Rules:**
  1. A member **CANNOT** leave a group if their net balance is non-zero ($	ext{Net Balance} 
eq 0$).
  2. An owner can only archive a group when all member balances are exactly zero ($0 	ext{ VND}$).

---

### 3.6. GRP-06: Smart Debt Split & Settlement

#### FR-SPLT-01: Multi-Mode Bill Splitting
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Group Member (`ACT-MEMBER`)
- **User Story:**
  > As a group member logging a shared expense,  
  > I want to split the bill using flexible methods (Equal, Exact Amounts, or Percentages),  
  > So that expenses are distributed accurately according to how we actually spent.
- **Functional Scope & Business Rules:**
  1. User specifies: Title, Total Amount, Payer (one or multiple members), Date, Category, and Split Mode:
     - **Mode 1 (Equal Split):** Distributes amount equally among selected participants. 
       - *Rounding remainder rule:* If $100.000 	ext{ VND}$ is split among 3 members ($33.333 	ext{ VND}$ each), the $1 	ext{ VND}$ remainder is allocated to the payer. Total split must equal total expense exactly.
     - **Mode 2 (Exact Amount Split):** User manually enters each member's exact share. System verifies $\sum 	ext{Shares} = 	ext{Total Amount}$.
     - **Mode 3 (Percentage Split):** User enters percentages for each member. System verifies $\sum 	ext{Percentages} = 100\%$.
  2. Enforces the **Zero-Sum Ledger Invariant:** $\sum (	ext{Paid}) = \sum (	ext{Owed})$.

#### FR-SPLT-02: Consolidated Who-Owes-Whom Balance Matrix
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Group Member (`ACT-MEMBER`)
- **User Story:**
  > As a group member,  
  > I want to see a clear summary showing exactly who owes whom and how much,  
  > So that there is complete transparency and no confusion among friends.
- **Functional Scope & Business Rules:**
  1. Aggregates all recorded expenses into pairwise debt relationships.
  2. Calculates individual Net Balance:
     $$	ext{Net}_i = \sum 	ext{Paid By } i - \sum 	ext{Share Owed By } i$$
  3. Displays user-specific view: "You owe X [amount]" or "Y owes you [amount]".

#### FR-SPLT-03: Cyclic Debt Graph Simplification Algorithm
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Group Member (`ACT-MEMBER`)
- **User Story:**
  > As a group member,  
  > I want the system to automatically simplify circular debts,  
  > So that we minimize the total number of bank transfers needed to settle up.
- **Functional Scope & Business Rules:**
  1. Implements a greedy debt simplification algorithm:
     - Calculates Net Balance for all members.
     - Separates members into Creditors ($	ext{Net} > 0$) and Debtors ($	ext{Net} < 0$).
     - Greedily matches the largest debtor with the largest creditor until all net balances are zero.
  2. Preserves every member's net financial balance while reducing transaction count to at most $N - 1$ transactions for $N$ members.
- **Acceptance Criteria (Gherkin):**
  - **Scenario:** 3-way circular debt simplification
    - **Given** Member A owes Member B 50.000 VND, Member B owes Member C 50.000 VND, and Member C owes Member A 50.000 VND
    - **When** the debt simplification algorithm executes
    - **Then** all pairwise debts are cancelled out to 0 VND
    - **And** the required settlement transfers list is empty (0 transfers required).

#### FR-SPLT-04: Debt Settlement Recording & Confirmation
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Group Member (`ACT-MEMBER`)
- **User Story:**
  > As a debtor who transferred money to a creditor outside the app,  
  > I want to record a "Settle Up" transaction in FinSync,  
  > So that our recorded debt ledger is updated and cleared.
- **Functional Scope & Business Rules:**
  1. Debtor or Creditor logs a settlement: Payer, Receiver, Amount, Date.
  2. Ledger updates immediately, decrementing outstanding debt between the two parties.
  3. Settlement entry is visible in the group activity feed with status `CONFIRMED`.

#### FR-SPLT-05: Payment Reminder Notifications
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** Group Member (`ACT-MEMBER`)
- **User Story:**
  > As a creditor,  
  > I want to send a polite automated payment reminder to members who owe me money,  
  > So that I do not have to awkwardly ask them in person.
- **Functional Scope & Business Rules:**
  1. Creditor taps "Send Reminder" next to an outstanding debt.
  2. System dispatches a push notification to the debtor (e.g., *"Friendly reminder: You have an outstanding balance of 150.000 ₫ with Long for 'Room Rent'"*).
  3. Cooldown limit: Max 1 reminder per debt every 24 hours to prevent spamming.

---

### 3.7. GRP-07: Budget Planning & Alerting

#### FR-BUDG-01: Category Spending Budget Setup
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to define spending limits for specific expense categories over weekly or monthly periods,  
  > So that I can prevent overspending on discretionary categories like dining or entertainment.
- **Functional Scope & Business Rules:**
  1. Parameters: Category, Spending Limit Amount, Time Cycle (`WEEKLY` or `MONTHLY`), Start Date.
  2. Users can create multiple concurrent category budgets.
  3. Budget cycles auto-renew at the beginning of each week/month.

#### FR-BUDG-02: Real-Time Consumption Tracking Progress
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to see a visual progress bar indicating how much of my budget I have spent,  
  > So that I can instantly judge whether I need to slow down spending.
- **Functional Scope & Business Rules:**
  1. Calculates spent percentage: $	ext{Percentage} = rac{	ext{Actual Expenses}}{	ext{Budget Limit}} 	imes 100\%$.
  2. Dynamic color coding:
     - $0\% - 79\%$: Green (Healthy)
     - $80\% - 99\%$: Amber / Orange (Warning threshold)
     - $\ge 100\%$: Red (Budget Exceeded)

#### FR-BUDG-03: Threshold Alerting (80% and 100%)
> *Performed by: Ngô Thái Hòa | Reviewed by: Vương Đắc Gia Khiêm | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to receive immediate in-app and push alerts when my spending hits 80% and 100% of a budget,  
  > So that I am proactively warned before exceeding my planned financial limits.
- **Functional Scope & Business Rules:**
  1. Evaluated synchronously whenever a new expense transaction is logged.
  2. Dispatches an alert at exactly 80% consumption: *"Warning: You have used 80% of your Food & Beverage budget."*
  3. Dispatches a critical alert at 100% consumption: *"Alert: You have reached 100% of your Food & Beverage budget."*
  4. Alerts are only fired once per threshold breach per cycle.

---

### 3.8. GRP-08: Financial Reporting & Analytics

#### FR-REP-01: Interactive Cashflow Charts (Bar / Line)
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to view my monthly income vs. expense cashflow displayed in an interactive chart,  
  > So that I can identify trends in my net savings rate over time.
- **Functional Scope & Business Rules:**
  1. Displays monthly comparisons of Total Income vs Total Expense.
  2. Interactive tooltips on hover/tap reveal exact figures per interval.
  3. Supports switching intervals: This Month, Last 3 Months, Last 6 Months, Year-to-Date.

#### FR-REP-02: Category Expense Breakdown (Pie Chart)
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to see a pie/donut chart breakdown of my spending by category,  
  > So that I can identify which categories absorb the largest portion of my income.
- **Functional Scope & Business Rules:**
  1. Renders pie chart with percentage and absolute figures per category.
  2. Selecting a slice filters down to show the specific list of transactions under that category.

#### FR-REP-03: Group Event Financial Summary Report
> *Performed by: Ngô Thái Hòa | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** Group Member (`ACT-MEMBER`)
- **User Story:**
  > As a group member,  
  > I want a comprehensive financial summary after a trip or event completes,  
  > So that everyone has a clear record of total group spending, largest expenses, and settled debts.
- **Functional Scope & Business Rules:**
  1. Aggregates: Total Group Cost, Average Cost Per Person, Highest Spender, Category Breakdown.
  2. Shows full settlement audit trail.

#### FR-REP-04: Export Financial Reports (PDF / Excel)
> *Performed by: Ngô Thái Hòa | Reviewed by: Vương Đắc Gia Khiêm | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** Regular User (`ACT-USER`), Group Member (`ACT-MEMBER`)
- **User Story:**
  > As a user,  
  > I want to export my financial reports or group ledgers to PDF or Excel files,  
  > So that I can archive them offline or share them with roommates.
- **Functional Scope & Business Rules:**
  1. PDF export formats data into a clean, printable report with tables and summary metrics.
  2. Excel (.xlsx) / CSV export provides raw tabular data for custom spreadsheets.

---

### 3.9. GRP-09: Savings Goal Management

#### FR-SAV-01: Long-Term Savings Goal Setup
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to create a dedicated savings goal with a target amount and deadline,  
  > So that I can save systematically for major future purchases (e.g., buying a laptop, tuition).
- **Functional Scope & Business Rules:**
  1. Parameters: Goal Title, Target Amount (> 0), Target Completion Date, Color/Icon.
  2. Users can manage multiple independent goals.

#### FR-SAV-02: Fund Allocation from Personal Wallets
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to deposit funds into a savings goal from one of my personal wallets,  
  > So that my available spending balance reduces while my savings accumulation grows.
- **Functional Scope & Business Rules:**
  1. User specifies Source Wallet and Deposit Amount.
  2. System records an internal transfer, decrementing available wallet balance and incrementing goal saved amount.
  3. Allows withdrawing funds back to a wallet if needed.

#### FR-SAV-03: Milestone Tracking & Completion Alert
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Phú Đạt | Edited by: Ngô Thái Hòa*
- **Priority:** Could Have
- **Target Actor:** Regular User (`ACT-USER`)
- **User Story:**
  > As a user,  
  > I want to see visual milestone badges (25%, 50%, 75%, 100%) and celebration alerts,  
  > So that I stay motivated to reach my financial target.
- **Functional Scope & Business Rules:**
  1. Progress bar reflects: $rac{	ext{Current Saved}}{	ext{Target Amount}} 	imes 100\%$.
  2. In-app celebration banner and notification triggers upon reaching 100%.

---

### 3.10. GRP-10: System Administration (Admin Web Portal)

#### FR-ADM-01: Admin Portal Authentication & Session
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** System Administrator (`ACT-ADMIN`)
- **User Story:**
  > As an administrator,  
  > I want to log in to the Web Admin Portal using dedicated administrative credentials,  
  > So that I can securely manage system governance and user accounts.
- **Functional Scope & Business Rules:**
  1. Accessible via web browser. Requires `ROLE_ADMIN` authority in the JWT token.
  2. Normal user accounts (`ROLE_USER`) are strictly rejected with HTTP `403 Forbidden`.

#### FR-ADM-02: User Account Monitoring & Lock/Unlock Control
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** System Administrator (`ACT-ADMIN`)
- **User Story:**
  > As an administrator,  
  > I want to view all registered users and lock or unlock suspicious or abusive accounts,  
  > So that platform integrity and policy compliance are maintained.
- **Functional Scope & Business Rules:**
  1. Displays searchable table: User ID, Full Name, Email, Registration Date, Account Status (`ACTIVE` / `LOCKED`).
  2. Admin can toggle user status between `ACTIVE` and `LOCKED`.
  3. Locking an account revokes all active JWT tokens immediately and blocks further API access.

#### FR-ADM-03: System Default Category Configuration
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** System Administrator (`ACT-ADMIN`)
- **User Story:**
  > As an administrator,  
  > I want to add, edit, and reorganize default system categories and icons,  
  > So that new users have a well-structured taxonomy upon joining.
- **Functional Scope & Business Rules:**
  1. Allows modifying system-wide default categories.
  2. Existing user custom categories are isolated and unaffected by system default changes.

#### FR-ADM-04: Aggregate Platform Statistics Dashboard
> *Performed by: Ngô Thái Hòa | Reviewed by: Phạm Định Tiểu Long | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** System Administrator (`ACT-ADMIN`)
- **User Story:**
  > As an administrator,  
  > I want to view global platform metrics (total users, total transactions logged, active groups),  
  > So that I can monitor overall platform growth and activity without violating user privacy.
- **Functional Scope & Business Rules:**
  1. Displays high-level aggregated metrics: Total Users, Monthly Active Users (MAU), Total Transactions Logged, Total Active Expense Groups.
  2. **Privacy Enforcement:** Global statistics are strictly aggregate; admin cannot view personal transaction details or private receipts of users.

---

### 3.11. Standalone AI Feature: AI Financial Advisor

#### FR-AI-01: Monthly Spending Automated History Scan
> *Performed by: Ngô Thái Hòa | Reviewed by: Nguyễn Lê Đức Nhật | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`), AI Microservice (`ACT-AI`)
- **User Story:**
  > As a user,  
  > I want the AI engine to automatically analyze my entire previous month's transactions,  
  > So that I receive deep, data-driven financial insights without manual spreadsheet work.
- **Functional Scope & Business Rules:**
  1. Triggered on-demand by user or automatically at the beginning of each calendar month.
  2. Aggregates: Personal Income, Personal Expenses by Category, and Group Expense contributions.
  3. Serializes anonymized transaction summaries into a standardized JSON payload and passes it to the FastAPI AI microservice.

#### FR-AI-02: Spending Anomaly & Budget Spike Detection
> *Performed by: Ngô Thái Hòa | Reviewed by: Ngô Thái Hòa | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`), AI Microservice (`ACT-AI`)
- **User Story:**
  > As a user,  
  > I want the AI to detect categories where spending spiked abnormally compared to my 3-month rolling average,  
  > So that I can immediately identify wasteful habits.
- **Functional Scope & Business Rules:**
  1. Compares current month category expenditure with the user's historical 3-month moving average.
  2. Flags categories experiencing a spike $\ge 25\%$:
     - *Example Advice:* *"Your dining out expenses increased by 40% compared to your 3-month average. We recommend capping dining at 2.500.000 ₫ this month."*
  3. Highlights top 3 largest single expenditure events.

#### FR-AI-03: Intelligent Budget Allocation Recommendations
> *Performed by: Ngô Thái Hòa | Reviewed by: Ngô Thái Hòa | Edited by: Ngô Thái Hòa*
- **Priority:** Must Have
- **Target Actor:** Regular User (`ACT-USER`), AI Microservice (`ACT-AI`)
- **User Story:**
  > As a user,  
  > I want the AI to propose an optimized category budget allocation for the upcoming month,  
  > So that I can follow a balanced 50/30/20 budget tailored to my actual income.
- **Functional Scope & Business Rules:**
  1. Evaluates total monthly income and fixed recurring expenses (Rent, Utilities, Tuition).
  2. Generates proposed budget limits per category structured according to the 50/30/20 financial rule (50% Needs, 30% Wants, 20% Savings).
  3. User can apply the AI-recommended budget directly with a 1-tap "Apply to Budgets" button.

#### FR-AI-04: Savings Goal Trajectory Assessment
> *Performed by: Ngô Thái Hòa | Reviewed by: Ngô Thái Hòa | Edited by: Ngô Thái Hòa*
- **Priority:** Should Have
- **Target Actor:** Regular User (`ACT-USER`), AI Microservice (`ACT-AI`)
- **User Story:**
  > As a user with active savings goals,  
  > I want the AI to evaluate whether my current monthly net savings pace will achieve my goal by the target date,  
  > So that I can adjust my spending behavior in time.
- **Functional Scope & Business Rules:**
  1. Calculates projected completion date based on average monthly surplus ($	ext{Income} - 	ext{Expenses}$).
  2. If projected completion is after target deadline, AI suggests specific discretionary categories to trim down.
