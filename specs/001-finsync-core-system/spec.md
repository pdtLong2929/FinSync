# Feature Specification: FinSync Core System

**Feature Branch**: `001-finsync-core-system`

**Created**: 2026-10-20

**Status**: Draft

**Input**: User description: "Hãy viết tài liệu đặc tả hệ thống (Software Specification) chi tiết cho dự án FinSync dựa trên 4 nhóm tính năng chính sau đây..."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Personal Bookkeeping & Budgeting (Priority: P1)

As a personal user, I want to track my income and expenses across multiple wallets (Cash, Bank, Credit Card) and set budgets so I can stay on top of my financial health.

**Why this priority**: This is the core utility of the application. Without basic bookkeeping, other features like AI advice cannot function.

**Independent Test**: Can be fully tested by creating a wallet, logging a transaction with a category and attachment, and setting a budget that triggers an alert when the limit is approached.

**User Flow**:
1. Open App -> Navigate to "Wallets" -> Create "Bank" wallet.
2. Navigate to "Add Transaction" -> Enter amount, select "Bank" wallet, choose "Food" category, add note, and attach receipt photo.
3. Save Transaction -> Balance updates immediately.
4. Navigate to "Budgets" -> Set monthly "Food" budget of $500.
5. System checks total spending in "Food" -> If >= $400 (80%), send notification.

**Data Flow**:
- UI -> `POST /transactions` (Spring Boot API) -> Persistence (PostgreSQL).
- Backend -> `GET /budgets/status` -> Compare Transaction Aggregates vs. Budget Thresholds -> Push Notification Service.

**Acceptance Scenarios**:

1. **Given** a user with a "Cash" wallet, **When** they log a $50 expense for "Groceries", **Then** the "Cash" wallet balance decreases by $50 and the transaction appears in the history with the category "Groceries".
2. **Given** a budget of $100 for "Entertainment", **When** a transaction is logged that brings total spending to $85, **Then** the user receives an "80% Threshold Reached" alert.

---

### User Story 2 - Group Expense & Smart Debt Settlement (Priority: P1)

As a group member (e.g., housemate or traveler), I want to log shared expenses and have the system calculate the fairest way to settle debts with the fewest transactions possible.

**Why this priority**: This differentiates FinSync from simple personal trackers and solves a major pain point for shared living/activities.

**Independent Test**: Can be tested by creating a group ledger, adding 3 users, logging an expense paid by one for all, and verifying the debt matrix calculation.

**User Flow**:
1. Owner creates "Vietnam Trip" Ledger -> Invites Members.
2. Member A logs "Dinner" ($90) -> Selects "Split Equally" between A, B, and C.
3. System calculates: B owes A $30, C owes A $30.
4. Member B logs "Taxi" ($30) -> Selects "Split for A and B" (50/50).
5. System runs Smart Debt Minimization.
6. User checks "Settlement" -> System shows optimized results (e.g., "C pays A $30, B owes nothing" if previous taxi offset B's debt).

**Data Flow**:
- UI -> `POST /ledgers/{id}/expenses` -> Backend calculates Split -> Update Ledger State in DB.
- UI -> `GET /ledgers/{id}/settlement` -> Backend runs Minimization Algorithm (Graph Simplification) -> Return optimized edges.

**Acceptance Scenarios**:

1. **Given** a group of 3, **When** an expense is split equally, **Then** each member is assigned a debt of exactly 1/3 the total.
2. **Given** a complex web of cross-debts, **When** the user views the settlement plan, **Then** the system presents the minimum number of transfers required to reach zero balance.

---

### User Story 3 - AI Financial Insights (Priority: P2)

As a user, I want the system to analyze my spending habits and proactively suggest better budget allocations or alert me to spending anomalies.

**Why this priority**: Leverages AI to provide value-added services that help users improve their financial behavior.

**Independent Test**: Can be tested by providing 3 months of mock data with a sudden spike in one category and verifying the AI detects it as an anomaly.

**User Flow**:
1. User navigates to "AI Advisor" tab.
2. System triggers background analysis of last month's data.
3. System identifies "Dining Out" increased by 40% vs. average.
4. System displays: "Warning: Your food spending is significantly higher than usual. Consider reducing it next month."
5. System suggests: "Based on your goal to save $1000, we recommend lowering your Entertainment budget by 10%."

**Data Flow**:
- Backend (Spring Boot) -> Export anonymized transaction history -> AI Microservice (FastAPI).
- AI Microservice -> Prompt Engineering -> Google Gemini API -> Analyze Patterns -> Return structured insights.
- AI Microservice -> Return JSON to Backend -> UI displays insights.

**Acceptance Scenarios**:

1. **Given** a 40% increase in a category, **When** the pattern scan runs, **Then** an anomaly alert is generated.
2. **Given** a savings goal, **When** the budget suggestion is requested, **Then** the system provides a breakdown of recommended allocations across categories.

---

### User Story 4 - Web Administration Portal (Priority: P3)

As a system administrator, I want to manage the platform's health, user accounts, and global category taxonomies.

**Why this priority**: Essential for platform maintenance and governance, but not a primary feature for end-users.

**Independent Test**: Admin logs into the portal, locks a test user account, and verifies the user can no longer log in.

**Acceptance Scenarios**:

1. **Given** a malicious user, **When** the admin locks their account, **Then** all active sessions are invalidated and login is blocked.
2. **Given** a request for a new default category, **When** the admin adds "Health Insurance" to the global taxonomy, **Then** all new users see this category by default.

---

### Edge Cases

- **Offline Sync**: User logs a transaction while offline. System must queue the request and sync when connectivity returns.
- **Floating Point Precision**: Handling currency splits (e.g., $10 / 3) must ensure no "lost cents" (e.g., 3.33, 3.33, 3.34).
- **Concurrent Ledger Edits**: Two users edit the same group expense simultaneously. System must handle conflicts or use Last-Write-Wins with notification.
- **AI Token Limits**: If a user has thousands of transactions, the system must summarize data before sending it to the AI to avoid context window overflows.
- **Member Deletion with Balance**: System must prevent deleting a member from a ledger if their net balance is not zero.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST support multiple wallet types: Cash, Bank Account, and Credit Card.
- **FR-002**: System MUST allow logging transactions with: Amount, Wallet, Category, Timestamp, Note, and Image Attachment (Receipt).
- **FR-003**: System MUST provide automated budget alerts at 80% and 100% of the defined threshold for any category.
- **FR-004**: System MUST allow users to set Savings Goals with a target amount and deadline, tracking progress automatically.
- **FR-005**: System MUST support Shared Group Ledgers with Owner (full control) and Member (read/log) roles. Each ledger MUST NOT exceed 50 members.
- **FR-006**: Splitting Engine MUST support: Equal Split, Exact Amount Split, and Percentage Split.
- **FR-007**: System MUST implement a Debt Minimization Algorithm to simplify net balances within a group.
- **FR-008**: System MUST allow manual confirmation of debt settlements by the receiver before balances are cleared.
- **FR-009**: AI Advisor MUST scan transaction patterns monthly to identify spending trends.
- **FR-010**: AI Advisor MUST detect anomalies defined as a >40% deviation from a 3-month rolling average.
- **FR-011**: AI Advisor MUST generate proactive budget suggestions based on User Savings Goals.
- **FR-012**: Admin Portal MUST allow locking/unlocking user accounts.
- **FR-013**: Admin Portal MUST provide a management interface for the system-wide default category taxonomy.
- **FR-014**: Admin Portal MUST display Platform Analytics including user growth, active users, and transaction volume.

### Key Entities *(include if feature involves data)*

- **Wallet**: Represents a financial source. Attributes: Name (must be unique per user), Type, Balance, Currency.
- **Transaction**: A single record of flow. Attributes: Amount, Type (In/Out), Category, WalletID, LedgerID (optional), AttachmentURL, Timestamp.
- **Ledger**: A container for group expenses. Attributes: Name, OwnerID, MembersList, TotalBalance.
- **Budget**: A spending limit. Attributes: UserID, CategoryID, ThresholdAmount, Period (Monthly/Weekly).
- **Goal**: A savings target. Attributes: UserID, TargetAmount, CurrentAmount, Deadline.
- **DebtMatrix**: A virtual representation of "Who owes Whom" within a Ledger.

## Clarifications

### Session 2026-10-20
- Q: Should the system allow a user to create multiple wallets with identical names? → A: Wallet names must be unique for each user.
- Q: How should the system handle a situation where a group member is deleted from a ledger while they still have outstanding debts or credits? → A: Prevent deletion if the member's net balance is not zero.
- Q: What is the maximum number of members allowed in a single Shared Group Ledger? → A: Medium groups (up to 50 members).
- Q: When a member settles a debt, must the recipient confirm receipt before the debt is officially marked as cleared? → A: Recipient must manually confirm before the balance is cleared.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can log a transaction (including photo attachment) in under 15 seconds.
- **SC-002**: The Debt Minimization Algorithm must reduce the number of required transfers by at least 30% in groups larger than 5 members with complex debts.
- **SC-003**: AI Anomaly Detection must have a false-positive rate of less than 10% based on user feedback (e.g., "Was this helpful?").
- **SC-004**: The Admin Portal dashboard must load platform-wide analytics in under 3 seconds for datasets up to 1 million transactions.
- **SC-005**: 100% of budget alerts must be delivered within 5 minutes of the triggering transaction being saved.

## Assumptions

- **Manual Entry**: Users are responsible for manual data entry as the system is non-custodial (no direct bank sync).
- **Connectivity**: Real-time AI analysis requires an active internet connection to reach the AI Microservice and Gemini API.
- **Currency**: All transactions in a single ledger must use the same base currency for simplicity in v1.
- **Storage**: Receipt images will be stored in a cloud bucket (e.g., S3/GCS) with only URLs in the DB.
- **Authentication**: The system uses JWT-based authentication as per the Project Constitution.
