# PA4 Groups and debts — live Android integration

2026-11-25. Backend source: `origin/feat/pa4-group-expense-debt`, commit `6330674`.
The existing project was fast-forwarded on local branch
`codex/pa4-server-integration`; prior frontend changes were preserved. No push.

## Use

Sign in → Groups → **Server groups**. Create a VND group, add guest members, add a
shared expense and choose Equal / Exact / Percentage / Shares. Balances shows the
server's member balances and simplified transfers. Record a full or partial
payment after paying outside FinSync. Confirmed payment records are viewable.
Refresh server groups to retrieve changes from other devices.

Group → Members → Invite members creates a real one-use, 24-hour token. Copy it
privately. Another signed-in account uses Groups → Join with code → paste the
full token → Review invitation → Confirm join. The server validates it; there is
no invented pre-join group preview because the API does not expose one.

**On this device** preserves the existing account-scoped local groups and all
their edit/delete/Undo features. Nothing is uploaded, merged, migrated or deleted
automatically. Local member names and local codes are not server identities.
Personal wallet balances and Reports are not changed by recording group debt.

## Adapter and contract

- `groups/ServerGroups.kt`: authenticated API adapter/controller, server snapshot,
  complete expense pagination, per-group caller UUID ↔ `me` alias, categories,
  balances, debts and payment history. Only complete reads publish a snapshot.
- `groups/ServerGroupFlow.kt`: native forms/routes using existing group components,
  validation, loading, empty/error/Retry and saved-state feedback.
- `groups/ServerSplit.kt`: preview matches backend largest-remainder rounding;
  local ledgers retain their original payer-remainder rules.
- `HttpAuthRepository.groupRequest`: existing serialized refresh and session-family
  checks. Credentials stay in process memory. No separate login/token database.
- MainActivity supplies the adapter only for a real authenticated HTTP account.
  FinSyncApp separates Server groups / On this device.

Endpoints used: `/api/v1/groups` GET/POST; group detail GET/PATCH; members POST/DELETE;
expenses GET (all pages)/POST; debts GET; settlements GET/POST; invitations POST;
`/groups/invitations/accept` POST; `/categories?type=EXPENSE` GET.

Amounts are validated whole VND. Server record IDs and allocations remain
authoritative; no locally calculated balance is presented as a server response.
Expense/payment POSTs keep the form's stable UUID requestId on retry. A network
failure for non-idempotent create/member/join requests requires refreshing before
retry. Successful writes followed by a failed read show Saved on server / Refresh
instead of claiming the write failed and automatically resubmitting.

## Backend/runtime verification

Backend builds unchanged from the team's branch. PostgreSQL migrations V1–V3 run
on a separate local test database `finsync_pa4_20261125`; the earlier user-demo
database is preserved. Redis uses logical database 1. Test accounts and financial
records are explicitly QA fixtures, not real bank transfers.

Docker CLI is installed but its Linux engine pipe was unavailable, including after
starting the installed Docker Desktop. The verified run therefore uses the
existing local PostgreSQL/Redis and the new Java JAR at localhost:8080. Android's
debug default `http://10.0.2.2:8080` matches. This does not certify Docker startup.
Private runtime settings/PIDs/logs remain in ignored `tmp/user-demo-runtime/`;
no credentials are committed. Since this is a separate database, existing accounts
from the earlier demo database are not silently migrated here.

For a machine with Docker engine available, run the team's unchanged compose in
the combined checkout (`feat/pa4-group-expense-debt`, retaining backend source
`6330674` plus the Android integration): `docker compose up --build -d --wait`.
This is the publication branch selected by the user for the combined FE/BE demo.
The earlier `feature/mobile-ui` snapshot remains separate. Use an available
host port and match `-Pfinsync.authBaseUrl=http://10.0.2.2:<port>` when overriding
8080. Do not delete volumes or point the app at another person's local IP.

Backend package + tests: **21/21 passed**, 28.285s;
`output/pa4-backend-verified-tests.log`. An earlier attempt failed because Redis
had not started; its Windows path was corrected before rerunning successfully.
Android APK/test APK + JVM + lint: build passed 1m59s; **98/98 JVM tests passed**,
lint **0 errors, 38 warnings, 3 hints**; `output/pa4-server-final-build.log`.
Device verification and any follow-up results are recorded in FRONTEND_STATUS.md.

Pixel 7 API36: broad UI15 run passed14; auth/profile automation hit an IME-related
stale input node. The API test now sets native text semantics; independent real
IME tests are unchanged. Final APK/testAPK/JVM rebuild passed28s after this and a
server-record read-limit correction. Focused live group2 + auth/profile1 passed
**3/3 in59.488s** (`pa4-server-verified-ui.log`). All15 unique scenarios have passing
evidence across runs, including the9 local group regressions; no final single
15/15-run claim. Lint predates these final small changes. SQL evidence confirms
the QA dinner100001 and payment10000 persisted. Actual emulator PNGs:
`output/pa4-server-balances.png`, `output/pa4-server-payment.png`.

## Remaining API boundaries

- Server expense/payment correction, deletion and Undo have no endpoints in this
  backend revision; their online records are explicitly read-only. Local records
  retain the existing operations.
- Server guest renaming/restoring is unsupported. Owner can add/remove guests,
  subject to backend permissions and unpaid balances.
- Friends/email delivery remain unsent local drafts. Token invitations are the
  actual supported remote join path. Targeted guest-claim, invitation revocation,
  ownership transfer, archive/leave and group-fund entry UI are not connected in
  this integration, although some endpoints exist.
- Online mode requires a connection; no automatic offline queue/cache/migration.
  Only VND groups are supported. Unsupported server currency fails visibly.
- Recording a payment does not initiate or verify a bank transfer.
