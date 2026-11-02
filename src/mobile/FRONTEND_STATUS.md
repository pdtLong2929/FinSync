# FinSync Android — Frontend Publication Status

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Date: 2026-11-02. Publication branch: `feature/mobile-ui`.

## Scope

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

This source delivery includes the native Kotlin/Compose/Material 3 client,
resources, Gradle Wrapper/version catalog, existing tests, README and technical
guide. The approved Liquid Metal UI and financial calculations are preserved.
Generated APKs, builds, screenshots, ZIPs, logs, local SDK paths, signing keys and
historical working notes are excluded. Local files are retained, not deleted.

The client currently uses fictional in-memory financial data. No API, PostgreSQL,
real authentication/JWT, online invitation, AI service or payment integration is
included. Process death loses local financial edits. This delivery is ready for
team source review subject to the checks below; it does not certify Part E
end-to-end completion or select the team's functional group.

## Current publication verification

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
