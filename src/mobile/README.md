# FinSync Android Frontend

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Updated **2026-11-02**. Kotlin, Jetpack Compose and Material 3 with the approved
bright Liquid Metal design. Application ID: `com.finsync`.

**The application uses fictional, in-memory local data. It is not connected to
backend APIs, PostgreSQL, real authentication or an AI service.** Changes update
the current UI session; process death discards them. Prototype screenshots are
not evidence of backend integration.

## Start here

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

- [Frontend technical guide](FRONTEND_GUIDE.md): setup, automatic emulator launch,
  build/install, architecture, dependencies, coverage and integration gaps.
- [Frontend status](FRONTEND_STATUS.md): verification scope and integration limits.
- [Local finance state](app/src/main/java/com/finsync/ui/FinanceViewModel.kt) and
  [authentication boundary](app/src/main/java/com/finsync/auth/AuthRepository.kt).

## Build and run

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Run PowerShell in `src/mobile`, or open this directory in Android Studio. Use the
bundled Android Studio JDK, SDK matching compile SDK 37, and the Gradle Wrapper.
No API URL, backend account or secret is needed for the current local workspace.

```powershell
Set-Location 'C:\Users\namda\FinSync\src\mobile'
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:assembleDebug --console=plain
```

Start/reuse `Pixel_7_2` using the automatic startup procedure in the guide. Android
Studio does not need to stay open. Once ADB shows the emulator as `device` and
`sys.boot_completed` is 1:

```powershell
$adb = Join-Path $env:ANDROID_HOME 'platform-tools\adb.exe'
& $adb devices -l
$deviceSerial = 'emulator-5554' # Replace with the actual serial from this list.
& $adb -s $deviceSerial install -r 'app\build\outputs\apk\debug\app-debug.apk'
& $adb -s $deviceSerial shell am start -n com.finsync/.MainActivity
```

Choose **Explore FinSync → Open sample workspace**. Use Overview, Wallets, Groups
and Reports. Sign-in/register/recovery deliberately report an unavailable backend;
there are no credentials that create a real session.

## Local feature coverage

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Wallet CRUD/archive/restore and transfers; income/expense forms, validation,
history/search/filters, edit/delete/Undo; groups, members, Equal/Exact/Percentage/
Shares splits and external-settlement records; interactive reports, budgets and
manually recorded savings goals. The spending guide contains authored general
advice. All operations are local bookkeeping; FinSync does not move real money.

## Checks and recorded evidence

> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Developer commands, not results from this documentation update:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug --console=plain
# Requires a booted emulator/device:
.\gradlew.bat :app:connectedDebugAndroidTest --console=plain
```

The 2026-11-01 rollout recorded build/install success, 51 JVM tests, 25 selected
integration UI tests, 9 secondary/auth UI tests, and a later category test 1/1 passing.
Lint recorded 0 errors / 30 warnings / 2 hints before the final category correction.
These were separate runs, not a claim that all repository UI tests ran together.
See the status report for verification scope and known limitations.

No build/runtime tests were repeated for this docs-only update. Older design
reports remain historical; their test counts and approval checkpoints do not
supersede the current guide.
