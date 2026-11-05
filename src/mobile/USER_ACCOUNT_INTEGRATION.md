# User account integration — demo branch

Updated 2026-11-05. Android: Nguyễn Phú Đạt; team review pending.

Branch: `task/demo_user_management`. Backend contract/migration imported from
`6bd7c2d3229172d65ce7a45b961bb52810a92501`. Backend source, schema definitions and
financial calculations were not rewritten for the Android integration.

## Implemented end to end

| UI action | Real API | Persistent result |
|---|---|---|
| Register | POST `/api/v1/auth/register` | PostgreSQL user + BCrypt password hash; then sign in |
| Sign in | POST `/api/v1/auth/login`, GET `/api/v1/users/me` | Database-backed session, authenticated identity |
| Open profile | GET `/api/v1/users/me` | Server name/phone/version override device cache |
| Save name/phone | PATCH `/api/v1/users/me` | Versioned PostgreSQL update |
| Access expiry / one protected401 retry | POST `/api/v1/auth/refresh` | Refresh rotation, same absolute server session expiry |
| Confirm sign-out | POST `/api/v1/auth/logout` | Database revocation + Redis blacklist; then clear device session |

Register returns account details, not tokens. Android shows **Account created** and
prefills only the public email on the sign-in screen. Passwords are never saved.
Successful sign-in opens Overview; its top-right avatar opens **My profile**.

Access/refresh tokens remain process-memory only. No remembered-login toggle is
offered; app restart requires sign-in. HTTP is restricted to exact loopback/emulator
hosts in debug, while configured release URLs require HTTPS. No TLS bypass or
JWT/refresh logging is used. `AccountHttp`, `HttpAuthRepository`, `AuthSessionStore`
and `AuthController` implement the existing repository boundary without new libraries.

Protected requests and rotation share a mutex. Access expiry refreshes when possible;
an unauthorized protected response can refresh/retry once, never recursively. A
failed temporary refresh leaves the credential available for retry; invalid refresh
or a locked account ends the authenticated UI. A canceled old refresh cannot replace
a newer/cleared memory session. Lost responses after server rotation may require a
new login, as documented by the backend team.

Registration validates at least 8 UTF-16 characters and at most 72 UTF-8 password
bytes without trimming/truncating passwords. Name/phone rules match the backend.
The latest profile version is submitted. A 409 conflict keeps the draft and offers
**Reload latest profile**, with discard confirmation; it never silently overwrites
another device's edits. Server logout errors remain visible and retryable. A401 on
logout retry means the session is already invalid; a locked account returns to sign-in.
In-flight replies are scoped to a session family and rejected after another sign-in
or sign-out, preventing stale profile/refresh/logout responses from affecting it.

## Boundaries that remain explicit

- Avatar upload, password recovery and admin/user-lock endpoints are absent from
  the supplied demo API. No server upload/recovery success is simulated.
- Photo, birthday and bio remain in private per-user device storage. **Save profile**
  explains that name/phone sync while those extra fields stay on the device. Email
  is read-only. A device-storage failure after an account save is reported separately.
- Wallets, transactions, groups and reports still use the approved in-memory sample
  workspace. This task does not claim remote financial persistence or real payments.
- No production deployment, full accessibility certification or minimum-API runtime
  certification is implied by the demo verification.

## Run on another computer

Follow the backend team's [API](../../docs/management/demo-handoff/ACCOUNT_API.md)
and [database](../../docs/management/demo-handoff/ACCOUNT_PERSISTENCE.md) guides.
Use Java 21+, PostgreSQL 16 and Redis 7; set real DB/Redis credentials and a random
JWT secret through untracked environment configuration. Never baseline an existing
schema just to bypass a migration error. Use an empty development database for V1.

From the repository root, with Docker Compose available and a private `.env`:

```powershell
docker compose up --build -d postgres redis backend
Set-Location src/mobile
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug '-Pfinsync.authBaseUrl=http://10.0.2.2:8080' --console=plain
```

The URL is the server origin without `/api/v1`; the adapter adds the API path.
Use the actual published backend port if different. Install/reuse Pixel_7_2 as in
the README. On a physical device use an HTTPS development endpoint; the debug
cleartext allowlist deliberately does not permit arbitrary LAN hosts.

## Prepared workstation runtime

This workstation has a dedicated runtime at `tmp/user-demo-runtime`, excluded from
Git. Secrets, Redis binary/config/AOF, database files, test accounts and logs are
not included in source delivery. It reuses the separately downloaded PostgreSQL
binary/cluster at `tmp/auth-runtime-20261104`, preserving the old `finsync_authdev`
database. The new migration is applied to **finsync_userdemo**; **finsync_userdemo_test**
is separate. Old users are not automatically copied to this new demo database.

- API: `127.0.0.1:18080`; emulator default: `10.0.2.2:18080`.
- PostgreSQL: `127.0.0.1:55432`; Redis: `127.0.0.1:56379`, password protected.
- Runtime-only Redis 7.4.11 Windows/Cygwin port: [source release](https://github.com/redis-windows/redis-windows/releases/tag/7.4.11),
  SHA256 `835999942bbef27e2b01f4a36955522cf19fff6511e67bbfe3fee322f6586705`.
  This is a community Windows development build, not a production Redis deployment.
  The team's Docker runtime uses its configured Redis image instead.
- Redis AOF is enabled with no-eviction policy. Runtime files stay private. The
  Redis test client uses logical database 1, while the demo uses database 0.

From `src/mobile`, after building the backend JAR:

```powershell
.\scripts\Start-LocalAuthBackend.ps1
# Stops only the owned demo server/Redis; preserves DB/AOF. PostgreSQL stays up.
.\scripts\Stop-LocalAuthBackend.ps1
```

These scripts reuse this prepared workstation; they do not silently install a new
service or bundle secrets on a fresh clone. A busy unrelated port/PID is rejected.
No AVD/app/database wipe is performed. Keep enough RAM free for Pixel7; stopping
the Gradle daemon before UI tests avoids competing with the emulator/backend.

## Verification

Backend package and 13 tests passed on the separate PostgreSQL test database and
real Redis: 8 API, 4 persistence, 1 context test. A further 18 black-box API/DB checks
passed against the running demo, including actual SQL profile/hash verification.
Android JVM/UI/build results and remaining limitations are recorded in
[FRONTEND_STATUS.md](FRONTEND_STATUS.md). Generated evidence is private local output,
not source or proof of any unimplemented endpoint.

Live Android tests are opt-in after installing app/test APKs and starting the API:

```powershell
adb -s emulator-5554 shell am instrument -w -e liveAuth true -e class com.finsync.ui.AuthBackendIntegrationTest com.finsync.test/androidx.test.runner.AndroidJUnitRunner
```

The live journey registers/signs in, saves a real profile, checks another session,
handles a version conflict without overwriting, reloads, logs out and verifies
server revocation, then signs in again to prove persistence. Another case tests
concurrent refresh and a one-time 401 retry. Local-only profile fixture tests are
separate and do not claim backend coverage.
