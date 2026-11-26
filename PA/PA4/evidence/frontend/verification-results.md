# Verification result excerpts

Original result lines are reproduced below. JVM/Surefire CSVs omit runtime properties and credentials.

## 25 November — Backend package/tests

Source: `pa4-backend-verified-tests.log`.

```text
[INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## 25 November — Final Android build

Source: `pa4-server-verified-build.log`.

```text
BUILD SUCCESSFUL in 28s
```

## 25 November — Broad UI run

Source: `pa4-server-final-ui.log`.

```text
Time: 161.167
FAILURES!!!
Tests run: 15,  Failures: 1
```

## 25 November — Targeted follow-up

Source: `pa4-server-verified-ui.log`.

```text
Time: 59.488
OK (3 tests)
```

## 26 November — Screenshot test-APK build

Source: `pa4-evidence-capture-build.log`.

```text
BUILD SUCCESSFUL in 26s
```

## 26 November — Native online screenshot journey

Source: `pa4-evidence-capture-ui.log`.

```text
Time: 37.096
OK (1 test)
```

The broad run had one auth/profile automation failure. The targeted follow-up passed that case and both live-group cases. These are distinct runs, not a single 15/15 suite. The 26 November run replayed one existing native journey for additional screenshots.
