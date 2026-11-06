# Feature Specification: FinSync Core System

**Feature Branch**: `001-finsync-core-system`

**Created**: 2026-10-20

**Updated**: 2026-11-04

**Status**: Draft

**Input**: User description: "Hãy tạo tài liệu đặc tả hệ thống (spec.md) cho tính năng specs/001-finsync-core-system/spec.md dựa trên khuôn mẫu của spec-template.md. Hãy đảm bảo các User Story được chia theo mức độ ưu tiên (P1, P2) và có tiêu chí nghiệm thu độc lập. Yêu cầu này phải tuân thủ nghiêm ngặt các nguyên tắc cốt lõi đã ghi trong file constitution.md của dự án FinSync."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Personal Bookkeeping & Multi-Wallet Tracking (Priority: P1)

As an individual user, I want to manage multiple financial wallets (Cash, Bank Account, Credit Card) and record daily income and expenses with categories and receipts, and set budget thresholds so that I can monitor and control my personal financial status.

**Why this priority**: Core utility of FinSync. Without reliable individual bookkeeping and balance calculation, secondary features such as group ledgers and intelligent financial advice cannot function.

**Independent Test**: Can be verified by creating a wallet, logging an expense with an attached receipt, and confirming that wallet balance and category budget progress update immediately, triggering an alert when reaching budget limits.

**Acceptance Scenarios**:

1. **Given** an authenticated user with a "Cash" wallet having a balance of $200, **When** they log an expense of $50 categorized under "Groceries" with a receipt image, **Then** the "Cash" wallet balance reflects $150, the transaction appears in history, and the receipt image is viewable.
2. **Given** a user with an active monthly "Dining Out" budget of $500, **When** an expense brings cumulative monthly dining spending to $410 (82%), **Then** the system sends a notification that the 80% threshold has been reached.
3. **Given** a user creating a wallet, **When** they attempt to create a wallet with a name that already exists in their account, **Then** the system rejects the creation and prompts for a unique name.
4. **Given** an existing transaction, **When** the user edits the transaction amount or deletes it, **Then** the corresponding wallet balance and affected budget totals are recalculated immediately.
5. **Given** a user with a wallet having historical transactions, **When** the user requests to delete the wallet, **Then** the system archives the wallet (soft-delete), removing it from active transaction entry while preserving all past transactions and reporting accuracy.
6. **Given** a user who copies a bank SMS or balance notification text, **When** they paste the text into the transaction entry screen, **Then** the system parses the amount, date, and description into a pre-filled transaction form for the user to review and save, without running silent background notification listeners.
7. **Given** a user with a Credit Card wallet having a $0 balance and a Bank wallet having $1,000, **When** they log a $200 expense using the Credit Card, **Then** the Credit Card reflects $200 outstanding debt, the Bank balance remains $1,000, and user's calculated Net Worth displays $800.
8. **Given** a user with a Credit Card outstanding debt of $200, **When** they record a transfer of $200 from their Bank wallet to the Credit Card wallet, **Then** the Credit Card outstanding debt returns to $0 and the Bank wallet balance decreases to $800.

---

### User Story 2 - Group Expense Sharing & Smart Debt Settlement (Priority: P1)

As a member of a shared group (e.g., housemates, project teams, trip organizers), I want to log shared expenses, distribute costs using flexible split methods, and receive an optimized debt settlement plan so that all members can settle debts transparently with the minimum number of manual transfers.

**Why this priority**: Major value differentiator for collaborative finances. Enables multi-user financial coordination under FinSync's strict non-custodial manual settlement principle.

**Independent Test**: Can be tested independently by creating a shared ledger with 3 or more members, recording multiple shared expenses with different split rules, verifying the simplified debt graph, and executing a manual settlement confirmed by the creditor.

**Acceptance Scenarios**:

1. **Given** a shared ledger with 3 members (A, B, C), **When** member A logs a $90 expense split equally among all 3, **Then** the ledger records that B owes A $30 and C owes A $30.
2. **Given** complex mutual debts in a ledger (e.g., A owes B $20, B owes C $20), **When** members view the settlement summary, **Then** the system simplifies the debt graph to show that A owes C $20, eliminating unnecessary intermediary transactions.
3. **Given** member B makes an external payment to member A to settle a $30 debt and records it in the app, **When** member A reviews the record, **Then** the balance is marked as cleared only after member A manually confirms receipt.
4. **Given** a recorded debt settlement where the payee member is unresponsive or disputes the payment, **When** the Ledger Owner reviews external payment proof and resolves the dispute, **Then** the Ledger Owner can override-confirm or cancel the settlement record, logging the justification in the audit history.
5. **Given** a member with an outstanding positive or negative balance in a ledger, **When** an attempt is made to remove that member from the ledger, **Then** the system blocks removal until the member's net balance reaches zero.
6. **Given** a shared ledger, **When** an owner attempts to invite more than 50 members, **Then** the system enforces the 50-member capacity limit and informs the user.
7. **Given** a shared group expense, **When** a member records the expense, **Then** the system requires selecting exactly one payer member for the full amount, distributing the split shares among selected participants according to the chosen split strategy.

---

### User Story 3 - Financial Analytics & Intelligent Advisory (Priority: P2)

As an active user, I want the system to analyze my historical spending patterns, alert me to spending anomalies, and suggest actionable budget adjustments aligned with my savings targets so that I can make informed financial decisions.

**Why this priority**: Enhances user engagement and long-term financial health, building upon the core transaction data collected in P1.

**Independent Test**: Can be tested independently by supplying transaction records spanning at least 3 months with a >40% spending surge in a specific category and verifying that the system flags the anomaly and recommends a realistic budget reallocation toward savings goals.

**Acceptance Scenarios**:

1. **Given** a user whose average monthly spending on "Shopping" is $200 over the past 3 months, **When** the current month's spending in "Shopping" reaches $300 (50% increase), **Then** the system generates an anomaly notice highlighting the deviation.
2. **Given** a user with a savings goal to save $1,200 in 6 months, **When** they view advisory suggestions, **Then** the system displays recommended category budget adjustments based on previous non-essential spending trends.
3. **Given** a user receiving a financial advisory summary, **When** reading the recommendations, **Then** no private banking credentials, external account numbers, or unauthorized personal identity data are requested or displayed.

---

### User Story 4 - Platform Administration & Governance (Priority: P2)

As a platform administrator, I want to monitor aggregated system telemetry, govern user account access, and maintain the system-wide default category taxonomy so that the platform remains secure, compliant, and operational.

**Why this priority**: Necessary for governance, auditability, operational health, and user security, ensuring that rogue accounts can be locked and system taxonomies remain clean.

**Independent Test**: Can be tested independently by logging into the admin portal, locking a test account, and confirming the locked user's sessions are terminated and subsequent login attempts are rejected.

**Acceptance Scenarios**:

1. **Given** an administrator identifying a compromised or abusive account, **When** the administrator locks the account, **Then** all active sessions for that account are invalidated immediately and new sign-in attempts are blocked.
2. **Given** an administrator updating the standard taxonomy, **When** a new global category is published, **Then** all newly created user accounts receive the updated default category list.
3. **Given** an administrator viewing platform telemetry, **When** accessing the dashboard, **Then** the system displays high-level aggregated metrics (active users, total transaction counts) without exposing private financial notes or individual transaction details.

---

### Edge Cases

- **Zero Lost Cents in Expense Splitting**: When splitting an indivisible amount (e.g., $10.00 among 3 users), the rounding remainder must be deterministically allocated to the payer or first debtor so the sum of individual split shares precisely equals the total amount.
- **Offline Entry & Synchronization**: When transactions are recorded without internet connectivity, the system must queue the entries locally and sync them in sequence upon reconnection without creating duplicates.
- **Concurrent Ledger Updates**: When two members record or edit shared expenses concurrently, the system must serialize ledger recalculations to ensure consistent debt matrix states.
- **Wallet Archival vs Deletion**: Wallets with zero transaction history may be permanently deleted. Wallets with existing transaction history must transition to an archived status upon user deletion, preserving historical financial records and past budget calculations while hiding the wallet from active entry interfaces.
- **Settlement Dispute & Inactivity Resolution**: When a payee is inactive or disputes a recorded external settlement, the Ledger Owner can intervene with an override-confirmation or void action accompanied by mandatory justification notes, unblocking ledger closure while preserving complete audit logs.
- **Pasted Notification Parsing Resilience**: When a user pastes unrecognized or non-financial text, the parsing engine must notify the user that structured details could not be extracted and cleanly fallback to a blank manual entry form without errors or data loss.
- **Non-Custodial Boundary Enforcement**: Under no circumstances should the system accept bank account passwords, PINs, or automated fund transfer authorizations. Any external transfer reference must be purely informational and manually entered.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001 (Non-Custodial Bookkeeping)**: The system MUST operate strictly as a manual bookkeeping and reconciliation tool; it MUST NEVER store banking credentials, hold user funds, or execute automated fund transfers.
- **FR-002 (Wallet Lifecycle & Management)**: The system MUST allow users to create, update, and manage multiple wallet types, specifically Cash, Bank Account, and Credit Card. Wallet names MUST be unique per user. Credit Card wallets MUST track balance as outstanding debt (spending increases debt, transfers/payments reduce debt), which is subtracted when computing aggregate user Net Worth. Deleting a wallet with existing transaction history MUST archive the wallet (soft-delete), preserving historical data while removing it from active selection.
- **FR-003 (Transaction Logging & Text Parsing)**: The system MUST record transactions with Amount, Direction (Income/Expense/Transfer), Source Wallet, Destination Wallet (for transfers), Category, Timestamp, Note, and optional Receipt Image Attachment. The system MUST support user-initiated parsing of pasted bank notification or SMS text into pre-filled transaction forms, and MUST NOT perform silent background notification scraping.
- **FR-004 (Budget Thresholds)**: The system MUST allow users to define category-level periodic budgets (monthly/weekly) and trigger notifications at 80% and 100% threshold utilization.
- **FR-005 (Savings Goals)**: The system MUST allow users to define savings goals with target amount, current progress, and target deadline, tracking progress against linked wallet balances.
- **FR-006 (Shared Ledgers)**: The system MUST support collaborative group ledgers with Owner and Member roles. Each shared ledger MUST support up to 50 members.
- **FR-007 (Expense Splitting Engine & Payer Model)**: Each group expense MUST have exactly one designated payer member who fronted the full expense amount. The system MUST support Equal Split, Exact Amount Split, and Percentage Split methods across participating members to calculate individual debt shares.
- **FR-008 (Smart Debt Minimization)**: The system MUST calculate an optimized debt settlement graph that reduces redundant reciprocal debts to the minimum viable number of direct transfers.
- **FR-009 (Manual Settlement Confirmation & Dispute Resolution)**: The system MUST require explicit confirmation from the receiving member before a recorded debt settlement balance is cleared. If the recipient is unresponsive or disputes the payment, the Ledger Owner MUST have permission to override-confirm or void the settlement record with an audit justification note.
- **FR-010 (Member Removal Guardrail)**: The system MUST prohibit deleting or removing any ledger member who has a non-zero outstanding net balance.
- **FR-011 (Spending Anomaly Detection)**: The system MUST detect spending anomalies when a category's current period expenditure deviates by more than 40% from its 3-month rolling average.
- **FR-012 (Proactive Financial Insights)**: The system MUST provide automated budget allocation recommendations aligned with user-defined savings goals based on historical spending trends.
- **FR-013 (User Governance & Session Control)**: The administration interface MUST allow administrators to lock and unlock user accounts, immediately terminating all active user sessions upon lock.
- **FR-014 (System-Wide Category Taxonomy)**: The administration interface MUST allow administrators to maintain standard default income and expense categories applied to all new user profiles.
- **FR-015 (Platform Health & Aggregated Telemetry)**: The administration interface MUST display operational metrics (active user volume, total transaction count) without exposing confidential user transactions or balances.

### Key Entities *(include if feature involves data)*

- **User**: Represents an account holder. Key attributes: User ID, Display Name, Email, Status (Active/Locked), Created At.
- **Wallet**: Represents a personal financial repository. Key attributes: Wallet ID, User ID, Name (unique per user), Type (Cash/Bank/Credit), Currency, Current Balance (represents outstanding debt for Credit type), Status (Active/Archived).
- **Transaction**: A single financial record. Key attributes: Transaction ID, User ID, Wallet ID, Category ID, Amount, Type (Income/Expense/Transfer), Timestamp, Note, Receipt Image Reference.
- **Category**: Classification for financial transactions. Key attributes: Category ID, Name, Scope (System Default/User Custom), Icon/Color.
- **Budget**: Spending limit rule. Key attributes: Budget ID, User ID, Category ID, Target Limit Amount, Period (Weekly/Monthly), Current Spend Amount.
- **Savings Goal**: Target financial accumulation. Key attributes: Goal ID, User ID, Target Amount, Current Saved Amount, Target Date.
- **Shared Ledger**: Group expense container. Key attributes: Ledger ID, Name, Owner User ID, Member Count (max 50), Created At.
- **Ledger Member**: Association between a user and a ledger. Key attributes: Ledger ID, User ID, Role (Owner/Member), Net Balance.
- **Group Expense**: Shared expenditure in a ledger. Key attributes: Expense ID, Ledger ID, Payer User ID (single designated payer), Total Amount, Description, Split Strategy (Equal/Exact/Percentage).
- **Split Item**: Individual allocation of a group expense. Key attributes: Split Item ID, Expense ID, Debtor User ID, Allocated Amount.
- **Settlement Record**: Manual reimbursement between group members. Key attributes: Settlement ID, Ledger ID, Payer User ID, Payee User ID, Amount, Status (Pending Confirmation/Confirmed/Rejected/Owner Overridden), Resolved By User ID (optional), Resolution Reason (optional), Timestamp.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001 (Fast Transaction Logging)**: Users can complete manual transaction entry with category and optional photo attachment in under 15 seconds.
- **SC-002 (Settlement Optimization Efficiency)**: In group ledgers with 5 or more members and circular debts, the debt minimization calculation reduces the total number of required settlement transfers by at least 30%.
- **SC-003 (Timely Budget Alerting)**: 100% of budget warning notifications (80% and 100% marks) are delivered within 1 minute of recording the threshold-breaching transaction.
- **SC-004 (High Insight Relevance)**: Spending anomaly detections achieve at least 85% positive user feedback regarding relevance and actionability.
- **SC-005 (Administrative Audit Response)**: Administrative account lock action invalidates active user access across all client interfaces in under 2 seconds.
- **SC-006 (Zero Financial Custody Violation)**: 100% adherence to non-custodial manual settlement; zero storage of bank credentials or automated account debit authorizations in the system.

## Assumptions

- **Non-Custodial Model**: Users execute all real-world settlements via their preferred external payment methods (cash, external bank apps) and utilize FinSync solely to record, reconcile, and calculate balances.
- **Zero Bank Credentials**: The system does not request or store bank account passwords, PINs, or direct payment tokens.
- **Single Base Currency per Ledger**: In this core system phase, all transactions within a specific shared ledger operate under a uniform base currency.
- **Secure Image Storage**: Receipt images are securely retained in object storage with privacy-controlled access links.
- **Network Dependency for Advisory**: Intelligent advisory and cross-user group ledger synchronization require network connectivity; offline transactions are buffered and synced upon reconnection.
- **Strictly User-Initiated Text Parsing**: In adherence to privacy and non-custodial boundaries, the application does not run background listeners or scrapers for device notifications; transaction parsing is strictly user-initiated via paste.

## Clarifications

### Session 2026-11-04
- **Q**: Hệ thống nên xử lý như thế nào khi người nhận không phản hồi hoặc tranh chấp một khoản thanh toán nợ nhóm đã được người trả ghi nhận?
  - **A**: Chủ sổ quỹ (Ledger Owner) có quyền xác nhận thay thế hoặc hủy bỏ giao dịch tranh chấp sau khi kiểm tra bằng chứng (kèm ghi chú lý do và lưu vết lịch sử kiểm toán).
- **Q**: Hệ thống nên xử lý như thế nào khi người dùng muốn xóa một ví tài chính đã có lịch sử giao dịch?
  - **A**: Chuyển sang trạng thái Lưu trữ (Archive/Soft-Delete): ẩn khỏi màn hình ghi chép mới, nhưng bảo lưu toàn bộ lịch sử và báo cáo quá khứ.
- **Q**: Hệ thống nên xử lý luồng ghi nhận giao dịch từ thông báo biến động số dư (SMS / Device Notification) như thế nào để vừa tiện lợi vừa bảo đảm nghiêm ngặt nguyên tắc Non-Custodial và kiểm soát của người dùng?
  - **A**: Không tự động đọc ngầm thông báo; chỉ kích hoạt phân tích trích xuất dữ liệu khi người dùng chủ động dán (Paste) nội dung tin nhắn/thông báo vào màn hình ghi chép.
- **Q**: Đối với loại ví Thẻ tín dụng (Credit Card), số dư ví nên được thể hiện và tính toán theo cơ chế nào?
  - **A**: Dư nợ hiện tại (Outstanding Debt): Chi tiêu qua thẻ làm tăng nợ, thanh toán trả nợ từ ví khác làm giảm nợ, và tổng tài sản ròng (Net Worth) bằng tổng tài sản trừ đi dư nợ thẻ.
- **Q**: Trong một khoản chi tiêu chung của sổ quỹ nhóm (Group Expense), hệ thống nên hỗ trợ mô hình người chi trả như thế nào?
  - **A**: Mỗi khoản chi có một người chi trả duy nhất (Single Payer). Trường hợp hóa đơn do nhiều người cùng thanh toán, người dùng tách thành các khoản chi riêng lẻ tương ứng.

### Session 2026-10-20
- **Q**: Should the system allow a user to create multiple wallets with identical names?
  - **A**: Wallet names must be unique for each user.
- **Q**: How should the system handle a situation where a group member is deleted from a ledger while they still have outstanding debts or credits?
  - **A**: Prevent deletion if the member's net balance is not zero.
- **Q**: What is the maximum number of members allowed in a single Shared Group Ledger?
  - **A**: Medium groups with a strict upper limit of up to 50 members.
- **Q**: When a member settles a debt, must the recipient confirm receipt before the debt is officially marked as cleared?
  - **A**: The recipient must manually confirm receipt before the balance is cleared.
