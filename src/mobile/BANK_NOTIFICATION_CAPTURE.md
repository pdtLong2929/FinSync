# Android bank notification review

## Optional default wallet — 2026-11-24

In the quick sheet, choose a wallet and tap **Use this wallet next time** to
remember it for that source app and the current account/workspace. The next alert
prefills that active wallet; purpose and Save still require user action. **Forget
default wallet** removes the suggestion. Missing or archived wallets are never
prefilled. Preferences are local and separate from notification-access consent;
remembering a wallet neither enables capture nor records a transaction.

This is notification-assisted bookkeeping, not a linked bank account. One bank
app may contain several accounts, so the suggested wallet can always be changed.
No credentials, live balance, bank API or backend synchronization are added.

Verification: debug APK + test APK build passed in 48s; 95 JVM tests passed
(`output/bank-default-wallet-build.log`). Four targeted Compose tests passed on
Pixel 7 in 21.37s (`output/bank-default-wallet-ui.log`): persistence and removal,
workspace/source isolation, missing-wallet validation, quick save once, and
advanced draft preservation at large text size. Test data was isolated; no real
bank transfer was performed. Updated APK installed on the existing emulator.

## Quick prompt update — 2026-11-24

The normal daily flow is now a heads-up notification → tap → compact native bottom
sheet OVER the current Overview/tab/Profile, without navigating to the verbose
Inbox page. Profile/transaction drafts underneath remain mounted. The sheet shows
the detected amount, Wallet, purpose shortcuts and Save
transaction; Later leaves the alert pending. Unknown amounts/direction require
input rather than silently selecting an expense. Save is the explicit confirmation
and uses the same atomic/idempotent record operation; no second review dialog is
required in this quick path. A double tap cannot create two entries.

More options keeps the amount/wallet/purpose draft and opens the existing advanced
date/note/own-wallet-transfer/link/ignore workflow. Handled history still shows the
original detected amount separately from editable ledger records. Notification
intents contain only the opaque event ID and are resolved in the current owner's
store; a tap opens that alert, and the request is consumed so manual Inbox visits
do not repeatedly reopen an old prompt. Authentication/account isolation remain.

The new `bank-review-popups` channel requests IMPORTANCE_HIGH. Android/user
notification settings, lock state, DND and cooldown still determine heads-up
presentation; no SYSTEM_ALERT_WINDOW permission, full-screen intent or background
form is forced. An
explicitly blocked legacy bank-review channel is not silently re-enabled during
migration. Notification amounts remain private and are not shown in the reminder.
Enable review reminders in setup; if Android has disabled pop-ups, use Android
notification settings → FinSync → Bank transaction prompts.

Quick-flow verification: full build/JVM95/lint passed1m41s (bank-quick-build.log,
lint37warnings/3hints, no errors); initial UI8/8 passed43.896s (bank-quick-ui.log).
After switching the prompt background to the current page and adapting amount
layout for large text, final APK/testAPK build passed26s (bank-prompt-final-build.log),
affected UI3/3 passed27.64s (bank-prompt-final-ui.log), and an actual Android
font_scale=1.5 run passed1/1 in7.205s (bank-prompt-system-font-ui.log). Font scale was
restored to1.0; temporary listener/notification grants restored false. No unit/lint
repeat for the last UI-only changes. Native screenshots: bank-quick-overview-popup.png
and bank-quick-popup-large.png under output, isolated test ledger. No real bank
transaction is claimed; heads-up presentation remains controlled by Android.

2026-11-23. Native Kotlin/Compose/Material 3, local-only. Explicitly requested by
the user as a follow-up to the research in PEOPLE_FRONTEND_HANDOFF.md. This does
not connect to a bank API, transfer money or require the team's backend.

## Use

Overview → Bank notifications icon beside the account avatar, or My profile →
Bank notifications → Set up bank alerts.

1. Read and acknowledge the prominent privacy notice. Android notification access
   exposes notification content; FinSync processes only explicitly selected apps.
2. Grant **Notification access** to FinSync bank alert review in Android settings.
   Return to FinSync; grant/access state is rechecked on resume. Some sideloaded
   installations may require Android's restricted-settings approval first.
3. Choose installed banking apps using their names/package IDs. The launcher-app
   inventory stays local. No QUERY_ALL_PACKAGES, SMS or Accessibility permission.
4. Enable selected bank apps. Enable review reminders separately (POST_NOTIFICATIONS
   on Android 13+). Denying reminders does not discard saved inbox items.
5. A supported new transaction alert creates a Pending candidate and a generic
   private reminder. Tap it, or open the Inbox, review the amount/direction/date,
   choose the wallet and category, then confirm the bookkeeping entry.

The source bank is identified by the user-selected Android app, not an API lookup.
The FinSync wallet is always chosen explicitly during review. A bank app may
contain multiple accounts; the app does not guess an account-to-wallet match or
store account numbers to do so. Backend work is unnecessary for this local read/
review flow; multi-device finance sync would be a separate backend integration.

Pause capture or uncheck consent to stop processing new alerts. Android settings
can revoke access at any time. Account/workspace changes pause capture instead of
assigning one person's notifications to another account. Enable again explicitly
in the intended workspace. Existing candidates/records are not deleted on logout.

## Detection and privacy

`BankNotificationListener` is system-bound using BIND_NOTIFICATION_LISTENER_SERVICE.
It does not poll apps, scrape screens, start banking apps or read SMS. Its exported
service is protected by the signature/system binding permission. There is no
automatic background Activity launch, overlay or full-screen prompt.

Default capture is off with an empty package allowlist. Callback processing checks
consent, owner, exact package, activation timestamp and freshness (five minutes).
Ongoing/group-summary notifications and FinSync's own notifications are ignored.
No historical active-notification replay is requested on connection.

Generic conservative VND parsing supports explicit +/− and transaction direction
phrases, Vietnamese accents and validated whole-VND grouping. Balance labels are
cut off before amount extraction. OTP/security/authentication notifications,
unrelated promotions and unsupported currencies are ignored. Multiple amounts or
ambiguous direction require manual classification/amount review. No ambiguous
candidate is silently converted to an expense. These rules are not a claim of
universal support for every bank's changing formats or redacted notifications.

The original notification text/title, account numbers, OTPs, recipient details and
reference strings are not stored or logged. Stored fields are the source package,
observed time, optional parsed amount, direction, state, hashed event identity and
linked ledger IDs. References are used only transiently to derive a hash. Manual
purpose notes are entered by the user. Files are private/no-backup; nothing from
this feature is uploaded. Reminders omit amounts and use a private/public version
with an immutable Activity PendingIntent. No financial action executes from a
notification action or an external intent.

Exact event/reference hashes prevent reimport of the same event. A new alert with
the same amount/direction within two minutes is flagged as a possible duplicate,
not automatically discarded (two legitimate payments can have equal amounts).
Source/storage failures never book money; a capture-failure notice asks for manual
review. Capacity is bounded; previous data is protected when a write fails.

## Bookkeeping and durability

Expense/Income use native financial semantics and category validation. Own-wallet
Transfer uses a balanced pair and the existing transfer validation; it is excluded
from income/expense totals. Already recorded links a matching wallet, amount,
direction and date without inserting another entry. Ignore changes metadata only;
History permits returning an ignored alert to Inbox. Group debt/settlement records
are independent and never marked paid from a bank alert.

Recording requires explicit user confirmation (Save in the quick sheet; a separate
review confirmation in More options). It atomically commits the
candidate state and a durable personal ledger before publishing wallet changes.
The first confirmed record preserves the user's CURRENT existing wallets,
transactions, categories and transfer history, including any existing sample
values. It does not reset them or pretend they are live bank balances. The
confirmation explicitly discloses this. Users must choose their intended wallet
and verify its bookkeeping/opening value separately.

Thereafter native wallet/transaction/category/transfer edits are saved locally,
including transaction edit/delete/Undo. Private AtomicFile JSON is scoped by a
SHA-256 workspace key. A shared file mutex serializes background capture and UI
writes; late completions cannot replace a different account's visible ledger.
Read errors block financial workspace initialization and offer Retry, not reset.
Native optimistic-save failure restores the last saved ledger and offers Retry
or Keep saved version. Budgets/goals retain their previous in-memory behavior.
No backend/database/API-contract change was made.

## Verification and limits

Exact results/evidence are recorded in FRONTEND_STATUS.md and TASK_STATE.md.
Build/JVM95/95 passed; the full lint run had0errors/36warnings/2hints. Ten cases
passed in the broad UI11 run; after fixing QA startup/command transport and moving
permission revocation outside the runner, the actual Android listener/reminder
case passed1/1 (9.285s). These11 unique scenarios passed across runs, not one clean
final11-case invocation. Original source payloads were not added to production
logging to debug the fixture transport. Permissions/config were restored: listener
access false, app notification permission false, device capture disabled, empty
allowlist. After clearing stale validation and labelling original detected amounts
in alert history, final debug/testAPK build passed32s and the affected durable
record/edit-delete-Undo UI case passed1/1 in21.26s. APK installed/reopened on Pixel7.
Tests include parsing/OTP/balance/duplicate cases, financial conservation and
idempotency, rollback, durable native edits, account isolation, codec corruption,
large-font review and Android permissions. The instrumented test APK contains a
framework-only QA notification source Activity; it is NOT shipped in the app.
It posts synthetic bank-like notifications through the REAL Android listener.
Tests restore the prior listener/notification permissions and capture settings.
No real bank account, real transfer or bank-specific certification is claimed.

Android/individual bank redaction, disabled notifications, battery restrictions,
force-stop and format changes may prevent or delay detection. This is an assisted
entry feature, not a bank statement or a guaranteed real-time transaction feed.
Test a real alert from the user's selected banking app before relying on a parser.

Primary Android references checked 2026-11-23:
- https://developer.android.com/reference/android/service/notification/NotificationListenerService
- https://developer.android.com/develop/ui/compose/notifications/notification-permission
- https://developer.android.com/guide/components/activities/secure-bal
