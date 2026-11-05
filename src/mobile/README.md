# FinSync Android Frontend

> Performed by: Nguyễn Phú Đạt (24120038). Team review pending.

Updated **2026-11-05**. Kotlin, Jetpack Compose, Material3 and the approved bright
Liquid Metal design. Application ID: `com.finsync`.

**User account management now uses the demo backend and PostgreSQL/Redis:** real
registration, login, profile read/update, refresh and server logout. Financial
screens still use fictional in-memory local data; process death discards financial
edits. No finance API, AI, real bank connection or money movement is claimed.

## Start here

- [Account integration and backend/database setup](USER_ACCOUNT_INTEGRATION.md).
- [Frontend technical guide](FRONTEND_GUIDE.md): build/run, emulator, code structure,
  libraries, financial rules and remaining integration gaps.
- [Verification/status](FRONTEND_STATUS.md): exact results, scope and limitations.

## Build and run

Use PowerShell in `src/mobile`, or open this directory in Android Studio. Use the
bundled JDK, SDK matching compile SDK37, and the checked-in Gradle Wrapper.

```powershell
Set-Location 'C:\Users\namda\FinSync\src\mobile'
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:assembleDebug --console=plain
```

Default debug API is `http://10.0.2.2:18080` for the prepared workstation runtime.
For the team's Docker backend on port8080, rebuild with:

```powershell
.\gradlew.bat :app:assembleDebug '-Pfinsync.authBaseUrl=http://10.0.2.2:8080' --console=plain
```

Release has no default API URL and requires HTTPS when configured. Debug HTTP is
limited to `10.0.2.2`, `127.0.0.1`, `localhost`; no TLS validation is bypassed.
See the integration guide for the team's Docker/DB setup. On this already prepared
workstation, start/stop the private API/Redis runtime with:

```powershell
.\scripts\Start-LocalAuthBackend.ps1
# When finished; preserves database files and Redis AOF:
.\scripts\Stop-LocalAuthBackend.ps1
```

The scripts require the prepared private runtime and built backend JAR, not included
in Git. No credentials or binaries are bundled. The legacy database is retained;
new demo users belong to `finsync_userdemo`, not the old `finsync_authdev` database.

Start/reuse Pixel_7_2 following the guide; Android Studio need not stay open. After
ADB reports `device` and `sys.boot_completed=1`:

```powershell
$adb = Join-Path $env:ANDROID_HOME 'platform-tools\adb.exe'
& $adb devices -l
$deviceSerial = 'emulator-5554' # Use the actual device serial.
& $adb -s $deviceSerial install -r 'app\build\outputs\apk\debug\app-debug.apk'
& $adb -s $deviceSerial shell am start -n com.finsync/.MainActivity
```

## Account and profile

1. **Sign up**: display name, email, password/confirmation. Backend creates the user;
   Android shows **Account created**, then asks you to sign in.
2. **Get started → Sign in**: authenticated identity is loaded from `/users/me`;
   successful login opens Overview.
3. **Overview → top-right avatar → My profile → Edit profile**: save name/phone to
   the server. A version conflict preserves the draft and offers a confirmed reload.
4. Photo, birthday and bio are labeled **device-only** until their APIs exist.
   Photo Picker needs no broad media permission. Registered email is read-only.
5. **Sign out of account → Sign out**: server revokes the current session, then
   Android clears it. Temporary server errors are shown without false success.

Access/refresh credentials stay in memory. The client refreshes expired access,
serializes rotation, retries an unauthorized protected request at most once and
requires login for an invalid/locked session. App restart requires sign-in; there
is no remembered-login promise. Password recovery UI never claims email delivery
because this API is still absent. Financial data remains a separate sample workspace.

## Preserved local financial features

Wallet CRUD/archive/restore/transfers; income/expense forms, categories, validation,
history/search/filters, edit/delete/Undo; groups/members, Equal/Exact/Percentage/Shares
and external-settlement records; interactive reports, budgets and manually tracked
savings goals. The spending guide contains authored general advice, not AI output.

## Checks

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug --console=plain
# Booted emulator required. Live API tests are opt-in; see the integration guide.
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

See the status report for current results and separate historical runs. Native app
captures, APKs, private runtime state and logs are retained locally, excluded from
source delivery. Figma/prototype images are not evidence of backend integration.
