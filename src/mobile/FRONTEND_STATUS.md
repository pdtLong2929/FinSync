# FinSync Android — Frontend Publication Status

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Updated: 2026-11-07. Working branch: `task/demo_user_management`.

## Scope

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

This source delivery includes the native Kotlin/Compose/Material 3 client,
resources, Gradle Wrapper/version catalog, existing tests, README and technical
guide. The approved Liquid Metal UI and financial calculations are preserved.
Generated APKs, builds, screenshots, ZIPs, logs, local SDK paths, signing keys and
historical working notes are excluded. Local files are retained, not deleted.

The client still uses fictional in-memory financial data. Registration/login now
connect to the real local Spring Boot/PostgreSQL service with a memory-only JWT.
Financial API persistence, invitations, AI and payments remain unconnected.
Process death loses local financial edits. This delivery is ready for
team source review subject to the checks below; it does not certify Part E
end-to-end completion or select the team's functional group.

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
