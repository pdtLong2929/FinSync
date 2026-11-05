# Account persistence

Flyway owns the schema; Hibernate only validates entity mappings. V1 creates
`users` and `auth_sessions`, their constraints and indexes. It does not seed accounts.

Use an empty development PostgreSQL database and set `DB_URL`, `DB_USERNAME`,
and `DB_PASSWORD` before starting the application. Environment files are not
automatically loaded by Spring Boot. Check database-name casing: the existing
Compose creates `FinSync`, while the application fallback URL uses `finsync`.

Do not enable automatic baselining to bypass errors on an existing schema:
baseline version 1 could skip this migration. Inspect and reconcile any database
previously created by Hibernate before adopting Flyway. No existing data is deleted
by this change. Java timestamps are managed by entity callbacks; direct SQL updates
must explicitly maintain `updated_at` and `version`.

Repository lookups are not authentication decisions. The future service must:

- Normalize email before lookups and handle unique-constraint races on registration.
- Check user status, session expiry/revocation and JWT blacklist as applicable.
- Use transactions for locking methods; consistently acquire user before session.
- Recheck refresh hash after locking, then rotate it once in the same transaction.
- Revoke sessions through managed entities so optimistic versions/timestamps update.

The integration test `AccountPersistenceTests` requires a **dedicated empty test
database** supplied through `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and a test-only
`JWT_SECRET` of at least 32 UTF-8 bytes for the application context. Its test data
rolls back, but Flyway schema creation persists. Run it with
`mvn -Dtest=AccountPersistenceTests test`. Never point it at production data.
