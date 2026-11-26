# PA4 Android frontend evidence

> Performed by: Nguyen Phu Dat (24120038) | Reviewed by: Pham Dinh Tieu Long (assigned; review pending) | Edited by: Nguyen Phu Dat (24120038)

Application baseline: [`66bb585`](https://github.com/pdtLong2929/FinSync/commit/66bb585),
`feat/pa4-group-expense-debt`. Native Pixel 7 emulator, Android API 36,
1080 × 2400 screenshots, captured **26 November 2026 (UTC+07)**.

The nine PNGs are unchanged captures of the Android app using isolated QA
accounts and the real PA4 Backend/PostgreSQL. Screenshot checkpoints were added
temporarily to the existing native test journey; its source was restored byte for
byte afterward. No application UI, financial rules or production data changed.
These are not Figma frames, mockups or real bank transfers.

## Screenshots

| ID | Open original PNG | Evidence |
| --- | --- | --- |
| FE-01 | [Members](../../screenshots/features/frontend/FE-01-members.png) | Owner and guest saved on the server |
| FE-02 | [Expense form](../../screenshots/features/frontend/FE-02-expense-form.png) | Title, whole-VND amount, payer, date and server category |
| FE-03 | [Split review](../../screenshots/features/frontend/FE-03-split.png) | Equal allocation: 50,001 + 50,000 = 100,001 VND |
| FE-04 | [Expense list](../../screenshots/features/frontend/FE-04-expenses.png) | Persisted shared expense in the group history |
| FE-05 | [Expense detail](../../screenshots/features/frontend/FE-05-expense-detail.png) | Server record and original participant allocation |
| FE-06 | [Debt before payment](../../screenshots/features/frontend/FE-06-balances.png) | Guest owes 50,000 VND before payment |
| FE-07 | [Payment confirmation](../../screenshots/features/frontend/FE-07-confirm-payment.png) | Explicit confirmation of a 10,000 VND partial payment |
| FE-08 | [Debt after payment](../../screenshots/features/frontend/FE-08-payment.png) | Server balance reduced to 40,000 VND |
| FE-09 | [Payment history](../../screenshots/features/frontend/FE-09-payment-history.png) | Confirmed 10,000 VND payment record and remaining debt |

SHA-256 hashes, original capture filenames and timestamps: [screenshots.csv](screenshots.csv).

## Results and supporting files

- [Android JVM suites](android-jvm-tests.csv): **98 tests, 0 failures/errors/skips**, from 25 November.
- [Backend suites](backend-tests.csv): **21 tests, 0 failures/errors/skips**, from 25 November.
- [Android UI case matrix](android-ui-tests.csv): 15 unique cases with passing results across the broad and targeted runs. The broad run was 14/15; targeted follow-up was 3/3.
- [Build and UI result excerpts](verification-results.md): includes the fresh 26 November **1/1 native capture run (37.096s)**.
- [PostgreSQL query and output](postgresql-verification.md): migrations V1–V3 successful, expense 100,001 VND, split total 100,001 VND and payment 10,000 VND.

The demonstrated owner paid 100,001 VND for two participants. The server allocation
is 50,001 VND for the owner and 50,000 VND for the guest. Recording an outside-app
payment of 10,000 VND reduces the guest's debt to 40,000 VND. A fresh server read
matches the saved expenses/payments, and the personal ledger remains unchanged.

Runtime for this evidence: the team's unchanged Java Backend JAR, local PostgreSQL
and Redis in an isolated QA workspace. Docker engine startup is not certified by
these captures. Android/API tests are independent of the team's Postman deliverable.

## Functional scope

Connected: authenticated VND groups, guest members, server categories, four split
methods, expenses, server member balances/debt suggestions, full/partial payment
records and real invitation-token creation/acceptance. The displayed screenshots
show the Equal native flow; all four split methods and invitation/access/idempotency
checks are covered by the linked Android-to-API test.

Online expense/payment corrections, deletion and Undo have no endpoints in this
Backend revision. Guest rename/restore, funds entry, targeted guest claim, invitation
revocation and archive/ownership/leave UI are not connected. Friends/email delivery
remains device drafts. Local ledgers keep their existing operations separately.

Source: [ServerGroupIntegrationTest.kt](https://github.com/pdtLong2929/FinSync/blob/66bb585/src/mobile/app/src/androidTest/java/com/finsync/ui/ServerGroupIntegrationTest.kt),
[ServerSplitTest.kt](https://github.com/pdtLong2929/FinSync/blob/66bb585/src/mobile/app/src/test/java/com/finsync/ServerSplitTest.kt),
[integration specification](https://github.com/pdtLong2929/FinSync/blob/66bb585/src/mobile/PA4_BACKEND_INTEGRATION.md).
