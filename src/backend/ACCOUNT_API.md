# Account API — PA3 demo
> *Implemented by: Nguyen Le Duc Nhat (AI-assisted) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Implemented: registration, login, refresh, logout, profile read/update. Android has
not been modified. Hòa owns Spec Kit, Nhật backend, Đạt Android, Khiêm testing.

## Run
> *Performed by: Nguyen Le Duc Nhat (AI-assisted) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Requirements: Java 21, Maven, PostgreSQL 16 and Redis. Read the [migration notes](ACCOUNT_PERSISTENCE.md)
before using an existing database. Do not reset or automatically baseline existing data.

For Docker Compose, create an untracked `.env` at the repository root with your
`DB_PASSWORD` and a random `JWT_SECRET` of at least 32 UTF-8 bytes. Generate a key:

```powershell
[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

Then run `docker compose up --build -d postgres redis backend` from the root.
Redis uses AOF persistence and a no-eviction policy; its port is not published.
Use HTTPS and restrict network access for remote deployments.

For Java outside Docker, export [.env.example](.env.example) variables into the
process environment, including `DB_PASSWORD` and `JWT_SECRET`, and point to your
PostgreSQL/Redis instances. Spring does not load `.env` automatically. Run
`mvn spring-boot:run` from `src/backend`. Installed Maven was used for verification
because the Windows Maven Wrapper failed during bootstrap in this environment.

JWT defaults to 900 seconds; `JWT_EXPIRATION_MS` accepts 1,000–900,000 milliseconds.
Do not commit real credentials or token values.

## Android contract
> *Performed by: Nguyen Le Duc Nhat (AI-assisted) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Standard Android emulator base URL: `http://10.0.2.2:8080/api/v1/`. HTTP is for
debug only; use HTTPS for release. JSON uses camelCase. Protected routes require
`Authorization: Bearer <accessToken>`. No browser cookies are used.

| Method / path | Request | Success |
|---|---|---|
| POST `auth/register` | `displayName`, `email`, `password` | 201: `id`, `email`, `displayName`; return to login. |
| POST `auth/login` | `email`, `password`, optional `rememberMe`, optional `deviceLabel` | 200: token pair. |
| POST `auth/refresh` | `refreshToken` | 200: replacement token pair; access token not required. |
| POST `auth/logout` | No body; Bearer token | 204: current session revoked. |
| GET `users/me` | Bearer token | 200: profile. |
| PATCH `users/me` | Bearer token; `version` plus `displayName` and/or `phoneNumber` | 200: updated profile. |

Token pair:

```json
{"tokenType":"Bearer","accessToken":"<jwt>","expiresIn":900,"refreshToken":"<opaque-token>","refreshExpiresAt":"2026-11-12T10:00:00Z"}
```

Profile:

```json
{"id":"1733f7f5-5f28-4532-9e83-683c62e033ec","email":"demo@example.com","displayName":"Demo User","phoneNumber":null,"version":0}
```

- Email is trimmed/lowercased and unique. Name is trimmed, 1–100 characters.
- Registration passwords require at least 8 Java string characters and at most
  72 UTF-8 bytes for BCrypt, without trimming/truncation. Android must replace its
  old 128-character limit and add display name. Confirmation stays client-side.
- Phone: optional, up to 30 characters, digits/spaces/`+`/`-`/parentheses, at least
  one digit. PATCH null/blank clears it; omission preserves it. Name cannot be null.
- Unknown fields are rejected. Never send role, status, email or userId in PATCH.
- PATCH needs the latest version. A no-op may return the unchanged version.
- Profile ownership comes from the JWT; responses never expose password/refresh hashes.

Example patch: `{"version":0,"displayName":"New Name","phoneNumber":null}`.

## Errors and session handling
> *Performed by: Nguyen Le Duc Nhat (AI-assisted) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Error shape: `{"code":"VALIDATION_ERROR","message":"Invalid request","fieldErrors":{"displayName":"Use 1 to 100 characters"},"traceId":"..."}`.
`fieldErrors` is `{}` for non-field errors. Branch on `code`, not message text.

| Status / code | Client action |
|---|---|
| 400 VALIDATION_ERROR | Retain draft and show validation errors. |
| 409 EMAIL_ALREADY_EXISTS | Choose another email or return to login. |
| 401 INVALID_CREDENTIALS | Show generic email/password error. |
| 401 UNAUTHORIZED | Refresh once when appropriate; otherwise login. |
| 401 INVALID_REFRESH_TOKEN | Clear invalid local session; login again. |
| 403 ACCOUNT_LOCKED | Stop authenticated operations. |
| 409 PROFILE_VERSION_CONFLICT | Reload and ask user to review before saving. |
| 503 AUTH_SERVICE_UNAVAILABLE | Report temporary failure; do not treat as invalid credentials. |

HS256 access tokens validate signature, issuer, audience, timestamps and session/user
state. Redis blacklist is checked for every protected request; outages fail closed.
Refresh tokens expire absolutely after 7 days (30 with rememberMe), rotate on every
use, and do not extend that deadline. Serialize refresh in Android and atomically
replace local secure storage. Retry an original request at most once; never refresh
recursively. A lost refresh response after commit may require login again.

Refresh does not invalidate older access JWTs. Logout commits database session
revocation before writing the JWT blacklist. If that write fails, response is 503
but the session stays revoked; a retry can return 401. Other sessions remain active.
If Redis loses blacklist data, revoke existing sessions before reopening authentication.
Password recovery, account-lock endpoints and admin APIs remain outside this demo.

## Verification and handoff
> *Performed by: Nguyen Le Duc Nhat (AI-assisted) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Run `mvn test` against dedicated PostgreSQL/Redis test instances with DB/Redis
variables and a test-only JWT secret set. HTTP tests commit test accounts/sessions;
never point them at production. Redis outage tests simulate failures at the client
boundary; ordinary blacklist tests use real Redis.

Verified 05/11/2026: **13 tests passed** — 8 HTTP API, 4 persistence, 1 context test
on disposable PostgreSQL 16 and Redis 7. Coverage includes concurrent registration
and refresh, hashing, profile ownership/version/null behavior, account locking,
session expiry, JWT signature/issuer/audience/expiry, blacklist, and revocation
surviving Redis write failure. No Android end-to-end verification is claimed.

Đạt can connect the six endpoints. Khiêm should repeat the API matrix and then test
the Android flow. Hòa can reconcile implementation with the Spec Kit artifacts.

References: [Spring Security stateless sessions](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/session-management.html)
and [Nimbus HMAC JWT](https://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-hmac).
