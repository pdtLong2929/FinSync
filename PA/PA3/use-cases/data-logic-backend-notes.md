
# Data & Logic — Backend Design Notes (Supporting Appendix)
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

Implementation detail moved out of [D_usecase.md](D_usecase.md) to keep Part D focused on actor goals, business flows and outcomes. These notes are not additional Use Cases or completed backend implementation.

**Baseline caveat:** PA2 recipient/custodian confirmation, own-group-share recognition, historical rates, refunds and offline behavior remain requirements unless the team explicitly revises Part B. The current database v1 does not implement all of them.

## A.2. Baseline reconciliation
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Topic | PA2/PA3 reporting requirement retained here | Current DB v1 / FE gap | Affected cases |
|---|---|---|---|
| Settlement | Pending claim; recipient confirmation/rejection; Owner cannot bypass recipient. | DB v1 is CONFIRMED-only and permits Owner recording; requires lifecycle and authorization changes. | 07, 08 |
| Shared fund | Contribution confirmation and per-member fund/reimbursement positions. | DB v1 posts directly and treats fund distribution as Owner discretion. | 05, 06, 08 |
| Refunds | Linked partial/full refunds and post-settlement adjustments. | DB v1 has no expense-refund journal and makes confirmed group records immutable without corrections. | 08 |
| Currency | Stored VND/USD quotes and original/historical values. | DB v1 deliberately has no conversion; two currencies are reported separately. | 03, 05, 06, 08–11, 13 |
| Budget/report scope | Include own group consumption once. | DB v1 budget is personal-only; report recognition and linked records must be reconciled. | 09, 11, 13, 14 |
| Offline | Durable encrypted personal queue, tombstones, per-operation outcomes and conflict resolution. | FE is in-memory; DB request IDs for creates alone do not cover edit/delete replay. | 02, 12 |
| Automated reporting/batches | Durable run/items, leases, frozen input snapshots, private report revisions, outbox/delivery state. | 22-table v1 has no job/report/outbox metadata; further schema design is needed. | 13, 14 |
| Sessions | Apply account security requirements and reauthentication before synchronization. | PA2 access TTL is 60 minutes; current security design uses 15-minute mobile tokens. Account owner must reconcile this separately. | All protected cases |

**Resolution status:** These are explicit implementation gaps, not approved deletions from the vision. This document does not silently modify database_design.md, the team vision or account/AI specifications. The lead should reconcile Part B/Changes.md and database design before treating the complete Data & Logic scope as ready to implement. Prototype-only simplifications do not override this specification.

## A.5. Common backend contract
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

Endpoint names below are proposed contracts, not implemented APIs. Mutations carry an authenticated principal, operation/request identity and expected version where applicable; JSON amounts use exact decimal strings and explicit currency. Use stable server errors such as VALIDATION_FAILED (422), UNAUTHENTICATED (401), FORBIDDEN (403), VERSION_CONFLICT / IDEMPOTENCY_CONFLICT (409) and SERVICE_UNAVAILABLE (503). Resource lookup must not reveal another user's private data.

Check idempotent outcomes before applying effects; the same identity with another payload is a conflict. Store durable outcomes for all offline mutation kinds, not just creation. Use consistent lock ordering and transaction boundaries per aggregate. Do not hold database locks across an LLM call, file upload, export rendering or notification network request. Pending/confirmed/rejected states must be visibly distinct. Every visible success follows acknowledgement of a committed transaction or an explicitly labeled durable local save.

## A.6. Transactions, synchronization and batch processing
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Processing stage | Required behavior / contract |
|---|---|
| Command envelope | actor identity comes from the validated session/service principal; mutation carries operationId, entityId or aggregateId, expectedVersion, exact decimal amount/currency, business date and payload. The server computes a canonical payload hash; it never accepts actor roles or balance totals as trusted client facts. |
| Financial command sequence | 1. Validate session and request shape. 2. Resolve an existing idempotent outcome. 3. Lock affected aggregates in canonical order and recheck authorization. 4. Recalculate available funds/debt/rate/split constraints. 5. Write all journal changes and durable logical events. 6. Commit. 7. Return the committed identity/version. Failure before commit rolls back the whole aggregate. |
| Sync operation envelope | operationId, entityId, CREATE/UPDATE/DELETE, baseVersion, clientRecordedAt and payload; account identity is session-bound. The local queue stores Pending/Sending/Acknowledged/Conflict/ActionRequired. Only server acknowledgements clear pending status; client timestamps do not override server versions. |
| Conflict resolution | Display local and server values with versions and tombstones. Keep server discards only that local draft; reapply local changes creates a new operationId against the refreshed baseVersion. An explicit recreated deleted entry receives a new entityId. Independent records may continue while one record remains conflicted. |
| Run metadata contract | runId, jobType, logicalRunKey, jobVersion, periodStart/End, businessTimezone, inputCutoff, status, startedAt/finishedAt, checkpoint, counts and sanitized error code. These are required metadata contracts, not claims that current DB tables already contain these fields. |
| Item/lease contract | itemId, runId, partitionKey, attemptCount, leaseOwner, leaseExpiresAt, fencingToken, state, nextAttemptAt and committedResult/event ID. A write/acknowledgement must carry the current fencingToken; fetching a page is not a checkpoint. |
| Proposed batch defaults | Page size 100; lease 120 seconds with heartbeat every 30 seconds; at most 5 total attempts for transient errors, backoff starting at 30 seconds and capped at 15 minutes with jitter. These are configurable initial design values, not measured performance or deployment settings. |
| Retry classification | Retry timeouts and temporary dependency failures. Do not auto-retry forbidden access, invalid business input or expired/conflicting quotes as if they were valid. Exhausted/permanent items remain Failed for operator action; independent items continue and the run becomes Partial rather than Succeeded. |
| Report snapshot boundary | Determine the closed month in the recipient's business timezone. Materialize the authorized input values/IDs/versions and rate snapshots consistently before asynchronous rendering, or use equivalent immutable journal-as-of reads. A created_at filter over mutable rows alone is insufficient for reproducibility. |
| Report revision and outbox | Use unique user/currency/period/revision identity. Partition transaction commits the snapshot and outbox publication request together. A renderer writes a private artifact idempotently, then records artifact metadata; publication references the committed revision. Failed uploads leave no public success; orphan objects are cleaned safely. |
| Delivery semantics | A unique logical event key prevents duplicate inbox items. External delivery is at-least-once unless the provider accepts idempotency keys; do not claim exactly-once network delivery. Recheck recipient permissions and current debt before releasing protected or stale reminder content. |
| Monitoring and audit | Expose run/item status, checkpoints, attempts, counts and error codes to authorized operators. Record retry/cancel actor and time. Users see only their report/notification and its data timestamp; operators do not receive unrestricted transaction or receipt details. |

**State transitions:** Financial claims: Pending → Confirmed / Rejected / Cancelled (terminal). Job runs: Queued → Running → Succeeded / Partial / Failed / Cancelled. Item: Pending → Leased → Committed, or RetryDue / Failed / Cancelled. A recovered worker advances a new fencing token; it does not reset already committed items. Delivery state is separate: Pending → Delivered or RetryDue / Failed / Suppressed.

**Proposed acceptance beyond the PA2 targets:** Cancel a run between items; completed outputs remain and unclaimed work stops. Simulate commit-before-acknowledgement and overlapping workers; the resulting journal/inbox identities remain unique. Remove a user's group access during export or settle a debt during delivery delay; the protected export/reminder is denied or suppressed.

## A.7. Supporting API and Persistence Notes
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*


### UC-DL-01 — Manage Personal Wallets
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | POST /api/v1/wallets; PATCH /wallets/{id} |
| Transaction and algorithm | Validate caller ownership, currency and signed opening-balance policy; lock/version-check an existing wallet; preserve all referenced ledger identities. |
| Idempotency / output / error contract | A create request returns one stable wallet ID; replay is idempotent. A stale edit returns 409; invalid input returns 422. |
| Persistence mapping / gap | personal_wallets; balance queries over personal_transactions and wallet_transfers. |

### UC-DL-02 — Record, Edit and Delete Personal Transactions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | POST/PATCH/DELETE /api/v1/personal-transactions; POST /{id}/undo |
| Transaction and algorithm | Validate wallet/category and source-event linkage; lock old/new wallets in stable order; update the entry and relevant journal effects in one transaction. Receipt upload acknowledgement is independent of financial posting. |
| Idempotency / output / error contract | Create operations and every queued edit/delete carry separate stable operation IDs; edits carry baseVersion. Reject mismatched idempotency payloads and unauthorized references. |
| Persistence mapping / gap | personal_transactions, transaction_attachments; offline queue/conflict metadata is not covered by current DB v1. |

### UC-DL-03 — Transfer Funds Between Personal Wallets
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | POST /api/v1/wallet-transfers |
| Transaction and algorithm | Lock both owned wallets in a stable order; recheck availability; validate or refresh a cross-currency quote; post source and destination legs with original amounts/rate snapshot atomically. |
| Idempotency / output / error contract | No partial debit or credit. requestId identifies the transfer; stale quote requires new review, not automatic repricing. |
| Persistence mapping / gap | wallet_transfers; original/destination amounts and rate snapshots require an extension to DB v1. |

### UC-DL-04 — Manage Groups, Membership and Invitations
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | POST /api/v1/groups; POST /groups/{id}/invitations; POST /invitations/accept |
| Transaction and algorithm | Create group/Owner/fund together. Lock group and invitation for redeem/role changes; enforce unique account membership and exactly one active Owner; guest linkage requires targeted token proof. |
| Idempotency / output / error contract | Invalid invite yields no membership. Role changes require expectedVersion. Do not use a display name as proof of identity. |
| Persistence mapping / gap | groups, group_members, group_invitations, group_wallets. |

### UC-DL-05 — Record and Confirm Shared-Fund Contributions
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | POST /api/v1/groups/{id}/fund-claims; POST /fund-claims/{id}/confirm |
| Transaction and algorithm | Claim creation does not post funds. On confirmation, lock the claim and group/fund, check Pending and custodian identity, then post receipt/member position and notification atomically. |
| Idempotency / output / error contract | Lifecycle: Pending → Confirmed/Rejected/Cancelled. Confirmed is terminal; repeat confirm returns existing outcome. Opposing confirm/cancel must not both succeed. |
| Persistence mapping / gap | group_wallets and group_fund_entries are a starting point; pending claims, confirmation and member fund positions need schema additions. |

### UC-DL-06 — Record and Split a Group Expense
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | POST /api/v1/groups/{id}/expenses |
| Transaction and algorithm | Convert once using the reviewed snapshot; allocate integer minor units with stable remainder ordering; validate participant membership and sum; lock group/fund and atomically post expense, shares and one funding source. |
| Idempotency / output / error contract | Store original amount/currency, quote identity/time, mode, inputs, final shares and order. Reject mixed funding and foreign-member IDs. Expense requestId covers the whole aggregate. |
| Persistence mapping / gap | group_expenses, group_expense_splits, group_fund_entries; rate snapshots/member fund accounting extend v1. |

### UC-DL-07 — View Debts and Confirm a Settlement
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | GET /api/v1/groups/{id}/debts; POST /settlement-claims; POST /settlement-claims/{id}/confirm |
| Transaction and algorithm | Build net positions from confirmed ledger only. Pending claim has no balance effect. Recipient confirmation locks claim/group, rechecks live debt and posts settlement plus linked effects atomically. |
| Idempotency / output / error contract | Only intended recipient confirms; Owner privilege does not bypass this rule. Pending → Confirmed/Rejected/Cancelled. Competing claims are capped by the outstanding amount at confirmation. |
| Persistence mapping / gap | settlements/group balances; current v1 CONFIRMED-only/Owner-authorized design must be revised for PA2 recipient confirmation. |

### UC-DL-08 — Record Expense Refunds and Return Unused Funds
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | POST /api/v1/group-expenses/{id}/refunds; POST /groups/{id}/fund-return-claims |
| Transaction and algorithm | Lock original expense and prior adjustments; cap total and per-member refund at unrefunded allocations; append immutable reversal links using historical rate basis. Confirm fund returns only through the intended recipient. |
| Idempotency / output / error contract | Never delete settled history. A refund request is idempotent; post-settlement overpayments become reimbursement positions. Pending fund return has no cash effect. |
| Persistence mapping / gap | Requires linked refund/adjustment and fund-return claim records not represented in current 22-table v1. |

### UC-DL-09 — Manage Category Budgets and Threshold Alerts
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | POST/PATCH /api/v1/budgets; GET /budgets/{id}/status |
| Transaction and algorithm | Normalize period boundaries, validate unique scope and expectedVersion, aggregate personal consumption plus own group shares once with historical conversion; calculate thresholds from the same contributing set. |
| Idempotency / output / error contract | Persist the budget and any logical alert event atomically; UNIQUE event identity includes budget period and threshold. Do not silently substitute the v1 personal-only calculation. |
| Persistence mapping / gap | budgets, notifications and consumption queries; PA2 own-group-share recognition differs from DB v1 personal-only budget rule. |

### UC-DL-10 — Manage Savings Goals and Recorded Allocations
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | POST/PATCH /api/v1/savings-goals; POST /goals/{id}/allocations |
| Transaction and algorithm | Lock goal and owned wallet positions in a stable order; check available unallocated funds; record original wallet units and goal equivalent/rate for cross-currency allocation. Release cannot exceed its original position. |
| Idempotency / output / error contract | Allocation/withdrawal operations have stable identities. They change reserved availability and progress, not income/expense; currency change cannot rewrite prior allocations. |
| Persistence mapping / gap | savings_goals, savings_contributions; cross-currency allocation needs original amounts/rate snapshots beyond v1. |

### UC-DL-11 — Generate, Inspect and Export Financial Reports
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | GET /api/v1/reports; POST /report-exports |
| Transaction and algorithm | Build one authorized snapshot using normalized filters, original and historical converted amounts, and unique consumption-event identity; derive chart and drilldown from the same source set. Export never mutates ledger. |
| Idempotency / output / error contract | Filter/period/currency and generation/cutoff metadata accompany outputs. Private artifacts require authorization on download. Export failure leaves source data unchanged. |
| Persistence mapping / gap | Queries over wallets, transactions, group shares, confirmed settlements and fund ledger; report rows need not be persisted. |

### UC-DL-12 — Synchronize Offline Personal Changes and Resolve Conflicts
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | POST /api/v1/sync/personal-operations; GET /sync/status |
| Transaction and algorithm | Process operations in per-record order. Each item carries operationId, entityId, action, baseVersion, local timestamp and payload hash. Keep a durable outcome for create/edit/delete, including tombstones. Acknowledge only committed items. |
| Idempotency / output / error contract | Atomicity is per operation/aggregate, not the entire queue. Independent items may succeed while a conflicted item pauses. Preserve both versions; user resolution submits a new operation against the current version. |
| Persistence mapping / gap | personal_transactions/attachments version + request identity are a base; durable client queue, operation receipts and conflict records require further implementation. |

### UC-DL-13 — Automatically Aggregate and Publish Monthly Financial Reports
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | Authenticated scheduler command GenerateMonthlyReports(period, jobVersion); operator retry endpoint |
| Transaction and algorithm | Unique logical run and partition keys; lease plus fencing token; freeze input values/IDs/versions and quote basis before aggregation. Commit snapshot and outbox event per partition, then checkpoint. Do not hold a DB transaction across artifact rendering or delivery. |
| Idempotency / output / error contract | Run: Queued → Running → Succeeded/Partial/Failed/Cancelled. Item acknowledgements are durable. Regeneration creates an explicit report revision; original artifact remains attributable to its snapshot. |
| Persistence mapping / gap | Ledger source tables; persistent job runs/items, report snapshots/artifacts and publication outbox are required extensions, not present in DB v1. |

### UC-DL-14 — Evaluate Budget Alerts and Send Debt Reminders in Batches
> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Concern | Required behavior |
|---|---|
| Command / API | Authenticated scheduler command EvaluateFinancialAlerts(window); operator retry failed item |
| Transaction and algorithm | Claim bounded pages (default 100 items) using durable leases/fencing. Recheck current debt and eligibility; insert deduplicated notification/outbox atomically per recipient/event, then acknowledge. Delivery retries do not rerun financial writes. |
| Idempotency / output / error contract | Event key: budgetId/period/threshold or groupId/debtor/creditor/reminder-window. Configure backoff, retry cap, lease duration and a failed-item queue; no claim of exactly-once external delivery without provider idempotency. |
| Persistence mapping / gap | budgets, notifications, group_expenses, group_expense_splits, settlements; durable job/outbox/lease metadata extends DB v1. |

**Scope migration:** The compact Part D keeps UC-DL-02, 05–08 and 12–14. UC-DL-11 reporting is merged into UC-DL-13. Other IDs above are retained only as historical implementation references for the relevant Client/team work; they are not extra cases in the compact contribution. Performance/retry defaults remain proposals, not measured results.
