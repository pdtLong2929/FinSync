# Friends, group invitations and account frontend

> 2026-11-25: Server groups now has a separate real invitation-token create/accept
> flow; see PA4_BACKEND_INTEGRATION.md. The disconnected `social/` draft behavior
> documented below still applies to Friends/email invitations and local codes.
> It is not silently promoted to remote delivery.

2026-11-23. Android Kotlin / Jetpack Compose / Material 3. Frontend only.
This describes the UI and adapter requirements; it does not define or change the
team's deployed API contracts. No Groups/Friends backend has been connected.

## Entry points

- Overview → top-right avatar → My profile (requires the existing account login).
- My profile → Edit profile: existing name, phone, birthday, bio and photo editor.
  Existing supported account fields retain their current integration. Local-only
  fields remain labelled. No second profile database or identity is introduced.
- My profile → Friends & invitations. The same hub is available from Groups.
- Groups → Join with code. Codes are saved for later validation, not activated.
- Groups → group → Members → Invite members → Email / Friends / Code.
- Members → Edit / Restore: changes a local ledger display name/activity only;
  it does not edit another person's account. Owner cannot be removed; outstanding
  balances still prevent removal. Existing group expense/payment history remains.
- My profile → Log out → confirmation. Existing server sign-out is retained.
  If it fails, explicit confirmed local sign-out clears credentials and closes
  authenticated screens without claiming server revocation or deleting ledgers.

## Current device behavior and boundaries

`DevicePeopleRepository` persists unsent drafts atomically in private no-backup
storage under a SHA-256 workspace key. Device exploration and signed-in accounts
have separate files. A new account/workspace creates a fresh controller and
disposes the previous UI. Read errors protect the file and disable mutations
until Retry succeeds. Write errors keep the prior displayed/saved state. No
background delivery or automatic replay takes place when connectivity changes.

Users can prepare a friend request by email, prepare a group invitation by email
or by choosing an accepted friend, remove unsent drafts, save/remove a code,
copy the local group's code, search lists and inspect request status. Local
requests always remain `Draft`; they never become accepted friends or members.
The accepted list starts empty. This delivery does not seed fictional people.
The friend picker and incoming states are tested using isolated injected test
repositories, not by creating fabricated production friendships.

Codes displayed for local groups are explicitly inactive on other devices. A
code is 6–12 ASCII letters/digits; spaces around it are trimmed and case is
normalized. No arbitrary invitation URL is fetched. Saving a valid-format code
does not prove the group exists. A code for an already joined local group is
rejected. Group membership and financial totals never change from saving a draft.

## Adapter seam for backend implementation

`social/PeopleRepository.kt` exposes `load()`, `execute(PeopleCommand)` and
`resolveCode(code)` returning verified group/owner/member-count metadata.
The default `connected=false` implementation saves drafts. A future authenticated
adapter must return authoritative `PeopleData` after each command; the app must
not simply toggle `connected` on the device repository. This is a frontend seam,
not a claim that an HTTP adapter or social endpoints already exist.

Models: stable server `Person` ID, display name/email; `Friendship` with direction
and status; `GroupInvitation` with group identity/name, recipient/direction/status;
`JoinRequest` with code/status; `GroupPreview` for the code review step. States:
Draft, Pending, Accepted, Declined,
Cancelled, Expired. UI includes incoming accept/decline, outgoing cancel/revoke,
accepted friend removal and disabled network-only actions while disconnected.

Command intent: AddFriend, ReplyFriend, RemoveFriend, Invite, CancelInvite,
ReplyInvite, Join and CancelJoin. The current one-person friend picker submits
one invitation per confirmation. It never adds a ledger member on invitation.

Backend responsibilities before enabling delivery:

- Authenticate each caller; canonicalize identities, enforce self/duplicate rules,
  owner permissions, membership and invitation authorization on the server.
- Do not expose a global user directory or infer that an email is registered from
  the device. Define privacy-safe request/lookup responses with the team.
- Implement idempotent command submission, pagination, server-side statuses,
  expiry, revocation, capacity limits and rate limits; map failures to safe UI.
- Connect `resolveCode` to authenticated group/owner lookup. Native UI already
  implements loading, invalid/expired/failure, verified preview and a separate
  Confirm join request action. Editing the code clears the preview; resolution
  alone never creates a join request. The disconnected UI saves a code only.
  Validate expired/revoked/private groups again when accepting the request.
- On accepted invitation/join, refresh the authoritative group/member ledger.
  Reconcile stable account/member IDs and the existing current-user `me` alias;
  never replace names as a substitute for identities or duplicate historical IDs.
- Define permissions for owner transfer, leaving/deleting groups and debt
  constraints before adding those operations. They are not invented here.
- Never replay device drafts automatically. Ask the user to review and send after
  a real adapter is connected. Keep auth failures distinct from offline/service
  and validation failures; Retry must not duplicate a successful remote mutation.
- Group/friend state needs account-scoped server persistence and multi-device sync.
  Existing avatar/birthday/bio limitations are unchanged by this task.

## Real bank-transfer capture: research, not implemented

**Superseded by the user's explicit Android-listener follow-up on 2026-11-23:**
see [BANK_NOTIFICATION_CAPTURE.md](BANK_NOTIFICATION_CAPTURE.md) for the implemented
local opt-in review feature. The original research/proposal below is historical;
bank/provider API integration is still not implemented.

No notification/SMS reader, bank connection, new system permission, actual money
movement or automatic financial import is enabled in this delivery.
Before real import, personal wallets/transactions also need durable storage or an
authoritative finance backend instead of the current in-memory sample workspace,
and an explicit mapping from a linked bank account to the correct FinSync wallet.

Two possible follow-up designs:

1. Android notification access: the user explicitly grants notification-listener
   access. Restrict parsing to user-selected bank packages, discard unrelated
   notifications, keep raw messages private/minimal, handle redaction/duplicates
   and classify an uncertain amount as a candidate rather than a bank truth.
   App alerts require notification permission on Android 13+. Android restricts
   automatic background activities; prompt with a notification, not a forced form.
2. An authorized bank/provider integration: backend receives transaction events
   for eligible linked accounts, verifies/authenticates them and sends a push.
   Provider coverage and outgoing-transaction eligibility must be verified for
   the team's bank/accounts. Credentials and provider secrets stay off Android.

Recommended flow: event → idempotent pending candidate → "What was this for?"
notification → review amount/direction/date/wallet and choose expense/income/
internal transfer/group settlement → confirm → one ledger entry → consistent
Overview/Reports. Do not count moving money between your own wallets as spending,
deduct a bank event twice, treat every outbound transfer as an expense, or deduct
an external group payment a second time. Allow Ignore/link to existing entry,
permission denial/revocation, offline retry and a history of imported candidates.

Primary sources checked 2026-11-23:
- https://developer.android.com/reference/android/service/notification/NotificationListenerService
- https://developer.android.com/develop/ui/compose/notifications/notification-permission
- https://developer.android.com/guide/components/activities/secure-bal
- https://support.google.com/googleplay/android-developer/answer/10208820
- https://developer.sepay.vn/vi/sepay-webhooks/tich-hop-webhook

SePay documents `in`/`out` event payloads, but this does not establish eligibility
for every personal account or bank. SMS access has Google Play restrictions and
should not be assumed available. Research is not a provider recommendation or
successful integration claim.

## Verification

Debug/test APK builds passed; JVM83/83 and lint0errors/31warnings/2hints. PeopleUi
6/6 passed47.571s, then final Profile entry placement check1/1 passed9.337s. Earlier
broader run also passed GroupCompletion3 and ProfileUi2; exact run boundaries and
initial failures are recorded in FRONTEND_STATUS.md. UI fixtures and screenshots
are test evidence, not server delivery or proof of friendships or bank activity.
