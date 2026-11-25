# FinSync Android — Frontend Publication Status

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Updated: 2026-11-25. Working branch: `codex/pa4-server-integration`. PA4 server
integration changes are local and await team review. Earlier frontend snapshot
was published to feature/mobile-ui on 2026-11-24.

Publication target selected by the user on 2026-11-25:
`feat/pa4-group-expense-debt` (combined backend and Android integration).

## PA4 authenticated Groups/debts integration — 2026-11-25

Fetched `origin/feat/pa4-group-expense-debt` (`6330674`, team commit 09:15 +07).
Fast-forwarded the project on local `codex/pa4-server-integration`, preserving all
frontend work. Backend source is unchanged from that commit. New native
`groups/ServerGroups`, `ServerGroupFlow`, `ServerSplit` connect authenticated group
creation/edit, guest membership, expense pagination/four splits, server balances,
debts, payment records and actual invitation-token create/accept. Existing HTTP
account transport/refresh is reused; group UUIDs map the caller to `me`. Native
screens use server-authoritative balances/debt plans, never local approximations.
Group fund-backed records are readable; creating fund entries is outside this
adapter. Server expense/payment corrections are read-only because endpoints are
absent. Friends/email delivery stays disconnected; guest rename/restore, token
revocation/claim UI, leave/archive/ownership UI are not connected here.

Signed-in Groups offers Server groups / On this device. Device ledgers, earlier
financial data/calculations, personal wallets and local Undo remain separate and
unchanged. No automatic upload, offline mutation queue or merge. VND only.
See PA4_BACKEND_INTEGRATION.md for endpoint coverage, local runtime and limits.

Backend package/tests21/21 PASS28.285s on separate PostgreSQL database
finsync_pa4_20261125, Redis logical DB1; migrationsV1–V3 verified successful.
Docker's engine pipe was unavailable after startup attempt, so this run uses the
existing local PostgreSQL/Redis plus unchanged team Java JAR at localhost8080.
No Docker verification is claimed. Prior user-demo DB is preserved.

Android debug/test APK/JVM/lint PASS1m59s (pa4-server-final-build.log), JVM98/98,
lint0errors38warnings3hints. Broad Pixel7 suite15:14passed; one existing live auth
profile test hit a stale lazy input node during IME focus. API-test input helper
now sets text through native semantics; real IME tests are unchanged. Also removed
the local entry cap from reading valid larger server amounts. Final APK/testAPK/
JVM rebuild PASS28s (pa4-server-verified-build.log); focused device follow-up in
output/pa4-server-verified-ui.log: **3/3 PASS59.488s**, including the formerly
failing auth/profile journey, both live server-group cases and larger valid
server amounts. All15 distinct scenarios have passing evidence across broad and
targeted runs; this is not a claim of one final clean15/15 run. Final JVM98/98.
Earlier lint was not repeated after the one-line server read-limit fix/test-only
input stabilization. Final APK installed on Pixel7 API36.

Native server screenshots captured/inspected at output/pa4-server-balances.png and
pa4-server-payment.png. SQL evidence verifies the QA dinner100001 and payment10000
persisted (pa4-server-sql-evidence.log). Data is isolated QA fixtures, not real bank
payments. No new integration commit/push performed.

## Compact bank transaction prompt — 2026-11-24

User requested a simple notification tap → wallet/purpose → Save flow. Added
`bank/BankQuickSheet.kt` and shared `BankNotificationPrompt.kt`; source notification
requests the new HIGH bank-review-popups channel, preserving a blocked legacy
channel. Tapping passes a validated opaque event ID through MainActivity and opens
the corresponding compact sheet over the current tab/Profile rather than routing
to Inbox. Request tokens are consumed; manual Inbox visits do not reopen old
prompts. Drafts under the overlay remain mounted, including profile editing.

Common path has prefilled/editable amount, mandatory wallet, purpose shortcuts and
one explicit Save confirmation; Later keeps pending without money movement.
Unknown direction requires a choice. More options retains quick field values and
exposes prior date/note/transfer/link/ignore flows. Atomic storage, deduplication,
calculations, privacy, permissions and navigation behavior are preserved.

Files updated: MainActivity, FinSyncApp, AuthScreens, BankCaptureScreen,
BankReviewScreen, BankNotificationListener and BankCaptureUiTest; two new native
sheet/prompt files above. Full debug/testAPK/JVM95/lint passed1m41s
(`output/bank-quick-build.log`); lint0errors/37warnings/3hints. UI8/8 passed43.896s
(`bank-quick-ui.log`), including real system listener/private HIGH reminder.
Final UI overlay/adaptive changes: APK/testAPK passed26s (`bank-prompt-final-build.log`),
affected UI3/3 passed27.64s (`bank-prompt-final-ui.log`); actual Android font150%
UI1/1 passed7.205s (`bank-prompt-system-font-ui.log`). No unit/lint repeat for these
last UI-only changes. Final screenshots inspected, APK installed/reopened Pixel7.
Font restored1.0 and temporary notification/listener access restored false.
No backend/financial-data change, commit or push. Native image evidence uses
isolated fixtures: `output/bank-quick-overview-popup.png`, `bank-quick-popup-large.png`.

## Android bank notification review — 2026-11-24

Implemented opt-in local Android notification capture with exact selected-package
allowlist, prominent consent, Android notification-access and separate reminder
permission UI. The system-bound listener ignores OTP/security, balance-only,
unapproved/own apps, stale/summary/ongoing notifications. It does not scan previous
active notifications, launch background forms, read SMS, use Accessibility, call a
bank API or upload data. Original notification text/account/reference/OTP strings
are not stored or logged. Reminders omit money values and use immutable explicit
Activity PendingIntents. Default capture and permissions remain ungranted/off.

The Inbox supports amount/direction correction, explicit wallet/category/date,
Expense/Income, own-wallet Transfer, linking an existing entry and Ignore/restore.
Separate confirmation is required. Metadata and the personal ledger are saved in
one private account-scoped AtomicFile before publishing a recorded amount. Native
wallet/transaction/category/transfer edits then persist, including delete/Undo;
write failure restores the saved ledger and offers retry. Existing sample values
are preserved and disclosed, not treated as live bank balances. Group ledgers are
unchanged; budgets/goals retain their earlier in-memory behavior.

Source files: new `bank/BankModels.kt`, `BankNotificationParser.kt`, `BankJson.kt`,
`BankStore.kt`, `BankCaptureSettings.kt`, `BankController.kt`,
`BankNotificationListener.kt`, `BankCaptureScreen.kt`, `BankReviewScreen.kt`.
Updated Manifest/MainActivity, FinanceViewModel, FinSyncApp, AuthScreens and
ProfileScreens. Tests: `BankCaptureTest.kt`, `BankCaptureUiTest.kt`, and a test-APK-
only Java Activity/manifest that posts actual Android synthetic notifications.
Guide: [BANK_NOTIFICATION_CAPTURE.md](BANK_NOTIFICATION_CAPTURE.md).

Verification: debug/test APK builds passed. JVM95/95 passed in
`output/bank-consent-final-build.log` (29s); the full build/JVM/lint run passed2m2s
in `bank-device-build.log`, lint0errors/36warnings/2hints (five additional findings
concern direct SharedPreferences/Uri APIs, not security or correctness errors).
Broad UI11 run passed10 and exposed the QA notification-source test failure.
Runtime permission revocation originally killed the runner during cleanup; it is
now performed by the host AFTER instrumentation. Main startup was synchronized,
and fixture text is transported as base64 because UiAutomation tokenizes commands
and otherwise truncates whitespace. No production parser was loosened to pass.
Real Android listener/reminder test passed1/1 in9.285s:
`output/bank-qa-source-ui.log`. All11 distinct UI scenarios passed across broad and
targeted runs, not one final clean11/11 suite. Includes actual listener filtering,
private reminder, duplicate/OTP handling, durable records/native edit-delete-Undo,
account/codec corruption,320dp150% review/setup and group/history/profile regressions.

Final visual review found stale validation after wallet/category selection; it was
cleared on field correction. Handled history labels the original amount Detected
to distinguish it from an edited ledger value. Final APK build passed32s
(`bank-review-final-build.log`); the affected durable record/edit-delete-Undo UI
case passed1/1 in21.26s (`bank-review-final-ui.log`). Earlier JVM/lint/broad UI were
not rerun for these text/feedback edits. Final native PNGs were inspected and the
APK was installed/reopened on Pixel_7_2 API36. Whitespace diff check passed.
Restoration confirmed: FinSync listener access false, POST_NOTIFICATIONS false,
capture disabled, workspace device, empty selected-package set. The user must
explicitly grant/select/enable the feature. No real financial records were used.

The instrumented notification source is QA only; no real bank/account/transfer
was exercised. Bank formats/redaction/battery/force-stop can prevent detection.
Source bank is the selected Android app; the wallet is always chosen manually,
especially when one banking app has multiple accounts. Backend is unnecessary for
local capture; cross-device finance sync remains a separate integration. Source
notifications are never dismissed by FinSync. No backend/db/contract change,
commit or push. Real native screenshots are `output/bank-*.png`, with fixture data.

## Friends, invitations and account frontend — 2026-11-23

Added native Friends/Requests/Invitations lists, email friend-request preparation,
group invitation by email or accepted friend, local code copying, saved join codes
and a verified group/owner preview before the connected adapter can submit Join.
Default delivery is disconnected: requests are private account/device-scoped,
atomic unsent drafts. No fictional accepted friendships are seeded. Incoming
accept/decline and connected picker/preview states use injected test repositories.
They are frontend evidence, not successful online delivery or group membership.

Members now supports owner-controlled local name editing and restoration with
stable IDs/history. Existing debt-safe removal rules remain. Profile reuses the
existing photo/personal-information editor, adds the Friends hub and prominent
confirmed Log out. If server logout fails, local logout requires a separate
explicit confirmation and never claims server revocation. Existing account API
integration is retained; no backend or financial logic was changed.

Files: new `social/PeopleModels.kt`, `PeopleRepository.kt`, `PeopleController.kt`,
`PeopleScreens.kt`, `GroupInvitationScreens.kt`; updated `MainActivity.kt`,
`auth/AuthScreens.kt`, `auth/ProfileScreens.kt`, `ui/FinanceViewModel.kt`,
`ui/groups/GroupScreens.kt`, `ui/groups/GroupManagementScreens.kt`. Tests:
`PeopleTest.kt`, `PeopleUiTest.kt`. Handoff and boundaries:
[PEOPLE_FRONTEND_HANDOFF.md](PEOPLE_FRONTEND_HANDOFF.md).

Build/JVM/lint passed in 1m46s (`output/people-final-build.log`). JVM: 83/83 passed.
Lint: 0 errors, 31 warnings, 2 hints. Initial UI10 run passed8 and exposed a test
startup race and keyboard handling in the new member-name dialog. Test setup was
fixed and native IME Done handling added. Final PeopleUi6/6 passed in47.571s
(`output/people-final-ui.log`), including320dp/150% friend picker, incoming replies,
expired-code/review/confirm, local draft durability/account isolation, member
rename/remove/restore, authenticated profile entry/logout and local fallback.
The preceding broader run passed GroupCompletion3 and ProfileUi2 regressions
(`output/people-ui.log`); these five cases were not repeated after the focused
preview/IME edits. After moving the Friends button directly under the profile
hero, debug/test APK build passed26s and the targeted Profile hub/logout UI case
passed1/1 in9.337s (`people-profile-entry-build.log`, `people-profile-entry-ui.log`).
The installed final APK includes this placement change. Whitespace diff check
passed. Python skill validators were unavailable on PATH; no pass is claimed.

No notification/SMS listener, bank account integration or money movement was
implemented. Bank-event capture is a documented follow-up proposal only. No
commit or push. Screenshots in `output/people-*.png` use isolated test identities;
files labelled `fixture` show simulated service data, not a real connected backend.

## Scope

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

This source delivery includes the native Kotlin/Compose/Material 3 client,
resources, Gradle Wrapper/version catalog, existing tests, README and technical
guide. The approved Liquid Metal UI and financial calculations are preserved.
Generated APKs, builds, screenshots, ZIPs, logs, local SDK paths, signing keys and
historical working notes are excluded. Local files are retained, not deleted.

Personal finance still uses fictional in-memory data. Groups, members, shared
expenses and settlement history now persist in an account-scoped private device
ledger. Group workspaces start empty, support create/edit/search and member
management, four split methods, debt suggestions and confirmed full/partial
settlements. Save failures preserve the prior ledger and form draft. Read failures
offer Retry while protecting the existing file. Online invitations show an
unavailable state until group sync is integrated. Group data is independent of the
personal wallet/report ledger. Registration/login retains its existing integration.

PA4 scope currently covers local Android frontend behavior. Group API integration
will begin when the team supplies its backend and requests the integration.

## PA4 three-section workflow completion — 2026-11-23

User selected the Tricount/Splitwise-style Expenses / Balances / Members layout.
Expense details now support prefilled editing, confirmed removal and durable Undo.
Original split inputs use optional version-1 metadata. Older records retain all
recorded VND shares and reopen as Exact. Existing split/balance algorithms remain.

Balances offers direct full/partial payment recording, payment history and
confirmed amount/date correction or removal with Undo. These are manual records
of payments made outside the app; FinSync does not transfer or refund money.
Local owner/payer/payment-participant restrictions and former-member history
protection apply. Server authorization remains backend work.

New implementation files: `ui/GroupRecordEdits.kt`,
`ui/groups/GroupWorkspaceComponents.kt`, `ui/groups/GroupExpenseForm.kt`,
`ui/groups/GroupRecordScreens.kt`. Updated `domain/GroupFinance.kt`,
`data/GroupLedgerStore.kt`, `ui/FinanceViewModel.kt`, `ui/FinSyncApp.kt`,
`ui/groups/GroupScreens.kt`, `ui/groups/GroupManagementScreens.kt` and
`ui/groups/DebtScreens.kt`. Added JVM `GroupRecordEditsTest` and Android
`GroupCompletionTest`; updated `DeviceGroupLedgerTest` navigation.

Verification: JVM 77/77 passed. Debug and test APK builds passed; lint recorded
0 errors, 31 warnings and 2 hints. The broad 13-case UI run passed 12 cases and
exposed the large-font issue below. After the final label-layout fix, all three
GroupCompletionTest cases passed in 38.993s, including 320dp/150% readability,
durable expense/payment correction-removal-Undo and legacy JSON compatibility.
Thus all 13 distinct UI scenarios passed across the broad and targeted runs;
there was no single clean 13/13 run after the final small layout fix.
Logs: `output/pa4-completion-responsive-build.log`,
`output/pa4-completion-final-ui.log`, `output/pa4-label-layout-build.log`,
`output/pa4-label-layout-ui.log`.
The first UI run passed 12/13 cases; the remaining case exposed a missing keyboard
observer in its fixture. Once corrected, it exposed section-label wrapping and
horizontal overflow at 320dp/150% font. Large-font section insets/gaps were reduced
and the intrinsic height constraint removed. Labels now occupy their complete
equal-width slot for stable centering and measurement. Font size and 48dp minimum
touch targets are preserved. No unrelated screens were redesigned.

Real native emulator evidence: `output/pa4-expenses-complete.png`,
`pa4-balances-complete.png`, `pa4-expense-detail.png`, `pa4-payment-restored.png`,
`pa4-large-expenses.png`, `pa4-large-balances.png`, `pa4-large-members.png`.
Screenshots use isolated test ledgers, not backend or real user records. Shared
groups remain local-only and separate from personal wallets/reports. No backend
changes, commit or push.

## PA4 device Groups and debts — 2026-11-23

Build debug APK/test APK passed. JVM tests: 72/72 passed. Final focused Pixel7
API36 Compose suite: 9/9 passed in 98.729s. Lint: 0 errors, 31 warnings, 2 hints.
The initial UI rerun exposed duplicate Add group expense actions in empty history;
the redundant action was removed and the full focused suite passed afterward.

Verified create/edit/discard, add members, integer-VND rounding, four split modes,
full and partial settlement confirmations, duplicate-save guards, large text and
real keyboard visibility. New cases exercise failed-write rollback/retry, corrupt
file recovery, owner isolation, serialized writes and stale-owner completion.
The UI journey recreates both Activity and FinanceViewModel and confirms the
remaining35000 VND debt is restored from disk. Tests use isolated device cache
directories and dispose their own ledgers. Backend integration is outside this run.

Changed source files:
- `MainActivity.kt`: bind a device/account group workspace.
- `ui/FinSyncApp.kt`: discard group-edit drafts on exit.
- `ui/FinanceViewModel.kt`: persistent group coordinator, current group date,
  metadata editing and stable expense/settlement request IDs.
- `data/GroupLedgerStore.kt`: versioned, validated, atomic private JSON storage.
- `ui/GroupLedgerPersistence.kt`: asynchronous load/write, isolation and rollback.
- `ui/groups/GroupScreens.kt`: device-ledger routing, states and shared expenses.
- `ui/groups/GroupManagementScreens.kt`: create/edit and member management.
- `ui/groups/DebtScreens.kt`: current debt suggestions and settlement history.
- `test/.../GroupFinanceTest.kt`, `test/.../GroupLedgerPersistenceTest.kt` and
  `androidTest/.../DeviceGroupLedgerTest.kt`: financial, durability and UI checks.
- `README.md`, `FRONTEND_GUIDE.md`, `FRONTEND_STATUS.md`: usage and current scope.

Evidence stays local under `output/pa4-*`. The final APK was installed on Pixel7.
The supported durable store is device-local; online invitations and multi-device
group sync await backend work. Personal finance and account behavior have their
separate scopes and previous verification records below.

## Default Docker endpoint correction — 2026-11-07

Debug builds now default to `http://10.0.2.2:8080`, matching the Docker demo after
Android Studio Sync/Run with no command-line flags. Explicit `finsync.authBaseUrl`
overrides remain supported; existing user/project properties take precedence.
Release still has an empty default URL and requires HTTPS when configured.

The prepared workstation startup script also uses port8080. It migrates only its
verified owned old18080 process and rejects a busy target port. SQL/schema/UI and
financial logic were not changed. README/integration/build instructions agree.

- Debug APK build and 64/64 JVM tests: PASS. Lint: 0 errors / 30 warnings / 2 hints.
- Generated debug BuildConfig verified at8080 after a no-override build; explicit
  alternate18080 override separately verified. Release BuildConfig remained empty.
- PowerShell syntax and actual legacy18080→8080 startup migration: PASS. Only8080
  remained listening; QA login and authenticated profile both200, same DB user.
- Logs: `output/docker-url-build.txt`, `output/docker-url-override-check.txt`;
  private runtime `url8080-check.json`. Existing UI/API regression results below
  are historical and were not rerun for this configuration-only correction.

## User account integration — 2026-11-05

User authorized fetch/integration/database verification and push to the confirmed
demo branch `task/demo_user_management`. Starting backend commit `6bd7c2d` was fetched
with no backend/schema source changes. Existing uncommitted Android UI was retained.

- Backend JAR package PASS; backend 13/13 tests PASS (8 API, 4 persistence, 1 context)
  on dedicated `finsync_userdemo_test` and real Redis. No failures/errors/skips.
- Separate black-box 18/18 API/SQL checks PASS against `finsync_userdemo`: actual
  profile persistence, BCrypt, versions, denied role changes, refresh replay,
  logout revocation and re-login. The old `finsync_authdev` database is untouched.
- Final Android debug app/test APK build PASS; 64/64 JVM tests PASS; lint 0 errors,
  30 warnings and 2 hints (`output/user-account-reviewed-build.txt`).
- First live Pixel 7 account integration run 4/4 PASS (36.069s): real register→login,
  edit→another session,409 draft/reload, remote logout/revocation/re-login;
  concurrent refresh/401 retry; duplicate email/recovery boundary; offline failure.
- Added `AccountHttp`/new account contract to the existing repository; kept memory
  credentials, HTTPS release boundary and financial logic. Profile name/phone now
  use DB; photo/birthday/bio remain explicitly local. No simulated recovery/upload.
- Run/reproducibility, source-file responsibilities and residual integration limits:
  [USER_ACCOUNT_INTEGRATION.md](USER_ACCOUNT_INTEGRATION.md).

- Final selected UI run: 13/14 PASS in 124.566s, including all 4 live account cases,
  local profile/photo/large-font cases and all 6 existing auth cases. The new direct
  typing check assumed a blank email despite registration now prefilling it. Its
  setup now clears the field; exact native IME/key-event assertions remain. Targeted
  rerun: 1/1 PASS in 13.323s. All 14 cases have passing evidence across runs, not a
  single 14/14 suite pass. No unimplemented API was represented as tested.
- Backend/Redis stop/start scripts tested. After restart, SQL-backed name/phone,
  revoked session rejection and the actual Redis AOF blacklist key remained intact.
- Session-family/CAS guards discard responses from a replaced account session;
  JVM tests cover late logout not clearing a newer sign-in and UTF-8 BCrypt bounds.
- Native screenshots were captured/reviewed in `output/user-account-evidence/`.
  Profile UI skill audit: no errors, one heuristic empty-state warning (no profile
  collection to empty). Full accessibility/production certification remains open.
- Source review excludes generated files, credentials, Redis/PG binaries and data.
  Only the approved Android source/docs and the team handoff note are published;
  the supplied backend/SQL migration source remains unchanged.

## Historical profile UI-first extension

User authorized the Android UI first; profile/avatar/recovery APIs remain pending.
No backend source/database schema changes. Overview now has a labeled avatar entry;
successful real authentication opens Overview. Profile details/editor, native
Photo Picker, optional personal fields, device-only persistence per account, draft
discard and confirmed local sign-out are implemented. Backend account name/email
are not overwritten. Existing financial data and calculations remain unchanged.

Source additions: `auth/LocalProfile.kt`, `LocalProfileStore.kt`, `ProfileScreens.kt`,
`UserAvatar.kt`, two small vector icons, JVM profile tests and `ProfileUiTest`.
Existing auth host/controller, Overview header, field wrapper and affected auth
tests were updated. The field wrapper adds defaulted read-only/max-lines support;
existing callers retain their behavior. No new dependencies.

- Final debug app/test APK build and 59 JVM tests: PASS.
- Lint: 0 errors / 30 warnings / 2 hints. UI skill audit: no errors; heuristic
  empty/offline findings do not describe a network-backed collection here.
- Profile UI/storage: 3/3 PASS (33.254s), including 320dp/150% font with real IME,
  validation, save/reload, per-user isolation, photo import/remove and discard.
  First run had 2 failures while `dumpsys power` showed `mWakefulness=Asleep`;
  waking the restored emulator allowed the unchanged checks to pass.
- Selected auth regression initially passed 7/9. One live check matched two copies
  of the same email after the profile UI added header/detail rows; its selector was
  corrected to target the first visible match. The hardware-keyboard case timed out
  and requires the targeted rerun recorded below. No assertion was removed.
- Final recovery: native window diagnostics showed **System UI isn't responding**;
  host RAM was down to 425 MB. Stopped Gradle and cold-booted the same AVD with a
  temporary 2048 MB RAM override (no AVD config change/data wipe), then dismissed
  the verified System UI ANR dialog. Backend source and database schema unchanged.
- On the final app APK, selected live auth/profile/auth regression passed 12/13
  in 153.165s. The existing hardware-key event case passed on all three forms.
  The new actual-Activity case sent keys before its native editable connection
  was ready. Added `InputMethodManager.isAcceptingText`/idle synchronization, keeping
  real IME visibility/insets and exact `123@` then `123@gmail.com` assertions.
  That affected case then passed 1/1 in 18.307s. All 13 selected cases have passing
  evidence across these runs; this is not a single 13/13 full-suite pass.
- The final app/test APKs are installed on Pixel_7_2. The last app build/JVM/lint
  is `output/profile-release-check.txt` (despite that log name, debug tasks only).
  Final test-only build: `output/profile-input-connection-test-build.txt`.
  UI logs: `profile-systemui-recovered-ui.txt` and `profile-native-hardware-final.txt`.
- Real PNG evidence: `output/profile-evidence/`. Fixture-driven profile UI images
  do not certify backend profile integration. Live auth images use the actual API.
- Remaining: profile/avatar sync, recovery email, verified personal information,
  remembered login, complete accessibility/localization and minimum-API photo review.

## Local authentication verification — 2026-11-04

- Fetched backend commit `4b0fad92125a8b85c2a5506c8ad32c52612651b5` from
  `origin/feat/core-system-speckit` into a detached worktree. Backend source unchanged;
  no branch merge, commit or push.
- Backend package and context test: PASS (1 test), using a new isolated local
  PostgreSQL 16.15 cluster. No app/AVD data reset.
- Eleven API/DB checks: PASS, including register/login, duplicate email, invalid
  input, wrong/unknown credentials, BCrypt storage, locked account and unlock.
- API/database restart: PASS; the same stored account logged in afterward.
- Android app/test APK build: PASS. JVM: 55/55. Lint: 0 errors / 30 warnings / 2 hints.
- Pixel_7_2 live API UI tests: 3/3 PASS (38.063s). Selected existing auth/full-app
  regression: 7/7 PASS (80.715s), separate runs.
- After final Account copy/Back handling: rebuilt app/test APKs, JVM/lint PASS,
  reinstalled both APKs and reran the live register/login/sign-out journey: 1/1
  PASS (35.726s). Native success/error screenshots refreshed from this run.
- Native screenshots are local artifacts in `output/auth-backend-evidence/`.
  Live tests require `liveAuth=true` and create disposable users only.
- Session expiry, local sign-out and rejection of canceled requests are tested.
  JWTs/passwords are not persisted on the device. Remembered login is not implemented.
- Recovery, refresh, `/me`, server logout/revocation and protected financial
  endpoints are not available in this backend and were not claimed tested.
- Scripts in `scripts/` successfully stop/restart only the prepared local service
  and its dedicated DB. Secrets/runtime files remain excluded from Git.
- UI skill audit: no errors, one heuristic warning about an empty state in the
  auth form; forms have no collection to empty. No full-app accessibility or
  production-security certification is claimed.

## Previous publication verification — 2026-11-02

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

- Debug app and instrumentation APK build: PASS.
- JVM tests: 51 tests, with no failures, errors or skipped cases.
- Lint: 0 errors, 30 warnings and 2 hints. Warnings are not a release certification.
- Exact staged-source export: clean `assembleDebug` PASS (36 tasks executed),
  using SDK discovery through `ANDROID_HOME`, without local.properties, historical
  outputs or other untracked files. Gradle Wrapper and local drawable assets are
  included in the source delivery.
- Selected Pixel_7_2 UI regression: first run 24/26 passed. Two real-IME cases
  failed to open the keyboard while a native diagnostic screenshot showed
  **System UI isn't responding**. After closing that system dialog and restarting
  Gboard, the two affected methods passed 2/2 in 18.299s. This gives passing
  evidence for all 26 selected cases across runs, not a single 26/26 suite pass.
  No app code or IME assertion was changed for recovery.
- Coverage includes navigation/dock, compact/large text, wallets, transactions,
  search/delete/Undo, groups, analytics, reports/budgets/goals and the focused
  category word-layout regression. Not every repository UI test was run.
- Staged file review: mobile-only paths; no generated output directories, APK/ZIP,
  signing keys, local SDK properties or detected high-confidence credential markers.
  Documentation links resolve; checksums/source export were reviewed locally.

Raw build/UI logs and native diagnostic images are retained in ignored `output/`
for local review, not published as application source. Normal Windows line endings
and existing blank EOF lines were accounted for during whitespace review.

## Historical evidence

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

The 2026-11-01 rollout recorded build/install success, 51 JVM tests, 25 selected
integration UI tests, 9 secondary/auth UI tests and a later focused category test
passing. Lint recorded 0 errors, 30 warnings and 2 hints before the isolated final
category layout correction. These were separate runs; not every repository UI
test ran in a single full-suite pass. Full runtime coverage, backend acceptance,
TalkBack, minimum-API/foldable acceptance and frame-time benchmarking remain open.
