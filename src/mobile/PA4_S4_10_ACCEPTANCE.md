# PA4 S4-10 — Android frontend acceptance

> **2026-11-25 update:** The local-only acceptance below remains historical for
> On this device mode. Actual authenticated Groups/debt API integration has since
> been added. See [PA4_BACKEND_INTEGRATION.md](PA4_BACKEND_INTEGRATION.md) and
> FRONTEND_STATUS.md for supported server operations, verification and the
> expense/payment edit/delete endpoints still absent from the backend.

> Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Nguyễn Phú Đạt

Audit date: 2026-11-24. Scope: group expenses and debt/settlement bookkeeping.
The functional Android frontend is implemented with Kotlin, Jetpack Compose and
Material 3, retaining the approved Liquid Metal appearance. It saves real user
entries locally; it is not merely a clickable prototype. Full-stack S4-10 remains
pending the team's Groups/debt API integration.

## Implemented frontend

| Requirement | Native implementation / entry point |
| --- | --- |
| Create/edit groups | Groups → Create group; open a group → Edit group. Name/type/description validation, duplicate-name checks and discard confirmation. |
| Members | Group → Members. Add, rename, remove and restore local ledger members. Owner restrictions and unpaid-balance checks preserve history. |
| Expenses | Group → Expenses → Add group expense. Title, formatted numeric VND amount, payer, date and category; Review split before saving. |
| Four split methods | Equal, Exact, Percentage and Shares; participant selection, live difference and integer-VND rounding. Invalid or incomplete allocations cannot save. |
| Expense corrections | Open an expense → Edit expense / Remove expense. Prefilled form, recalculated balances, confirmed removal and Undo. Former participants retain historical allocations. |
| Debt overview/details | Group → Balances → View debt details. Who owes whom, current outstanding amount, simplified transfer suggestions and recorded payment history. |
| Payment recording | Balances → Record payment. Full or partial amount, date, validation and confirmation; remaining debt updates after successful save. |
| Payment corrections | Open a confirmed payment → Review correction / Remove payment record. Confirmation, balance recalculation and Undo; no actual transfer/refund. |
| UI states | Empty groups/expenses/payments, loading, local read failure with Retry, write failure with rollback and retry, validation errors and successful-save feedback. |
| Persistence | Account/device-scoped atomic private group ledger. Activity/ViewModel recreation reloads saved groups, expenses and settlements. Personal wallet/report ledgers stay separate. |
| Input usability | Numeric financial keyboard, keyboard-safe primary action, scrollable forms, discard confirmation and guarded repeated submission. Compact 320dp / 150% text section labels are covered by device tests. |

## Source map

- `app/src/main/java/com/finsync/ui/groups/`: GroupScreens, GroupManagementScreens,
  GroupExpenseForm, MemberAllocationRow, GroupRecordScreens, DebtScreens and
  GroupWorkspaceComponents provide the native screens and routes.
- `app/src/main/java/com/finsync/ui/FinanceViewModel.kt`: shared state and actions.
- `app/src/main/java/com/finsync/domain/GroupFinance.kt`: validation, splits,
  integer rounding, balances and simplified debts.
- `app/src/main/java/com/finsync/ui/GroupRecordEdits.kt`: correction/removal rules.
- `app/src/main/java/com/finsync/ui/GroupLedgerPersistence.kt` and
  `app/src/main/java/com/finsync/data/GroupLedgerStore.kt`: serialized atomic
  storage, owner isolation, rollback and data validation.

## Boundaries before backend integration

- Local ledger members are not newly registered user accounts. Friends and
  invitations currently persist unsent drafts; codes do not join another device's
  group. See `PEOPLE_FRONTEND_HANDOFF.md` for the existing UI and adapter seam.
- No Groups/debt HTTP adapter or PostgreSQL synchronization is claimed. Loading
  and errors here include real local I/O, not proof of a working server API.
- Recorded settlements acknowledge payments made outside FinSync. FinSync does
  not send, verify or refund money.
- API contract, stable server identities, server authorization, conflict handling
  and cross-device acceptance must be agreed and tested with the backend team.
- Physical-device performance, full TalkBack, API 26 and 200% text acceptance are
  not certified by the focused Pixel 7 suite.

## Verification evidence

The current debug and instrumentation APKs were built successfully in 48s and
installed on Pixel 7; all 95 JVM tests passed, including split/rounding, invalid
input, duplicate request, balance conservation, persistence and record edits.
Evidence: `output/bank-default-wallet-build.log` and
`app/build/test-results/testDebugUnitTest/`. This is the same source build used for
the focused S4-10 device acceptance; no app source changed during this audit.

Focused device run: `DeviceGroupLedgerTest`, `GroupCompletionTest` and
`GroupFlowTest`: **9/9 passed in 63.348s** on Pixel 7 API 36. Result recorded in
`output/pa4-s4-10-current-ui.log`. Tests exercise isolated fixtures and do not
claim real bank payments or backend integration.

No application source, financial data, backend or database changes were required
by the code audit. No commit/push was performed.
