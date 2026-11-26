# PostgreSQL verification

Captured 26 November 2026 from the isolated PA4 QA database after the Android
journey shown in FE-01–FE-09. This is database query output, not a screenshot.

```sql
SELECT version, success FROM flyway_schema_history ORDER BY installed_rank;
WITH latest AS (
  SELECT id, group_id, title, amount FROM group_expenses
  WHERE title = 'Real API dinner' ORDER BY created_at DESC LIMIT 1
)
SELECT title, amount AS expense_vnd,
  (SELECT sum(amount) FROM group_expense_splits WHERE expense_id = latest.id) AS allocated_vnd,
  (SELECT sum(amount) FROM settlements WHERE group_id = latest.group_id) AS recorded_payment_vnd
FROM latest;
```

```text
 version | success
---------+---------
 1       | t
 2       | t
 3       | t
(3 rows)

      title      | expense_vnd | allocated_vnd | recorded_payment_vnd
-----------------+-------------+---------------+----------------------
 Real API dinner |   100001.00 |     100001.00 |             10000.00
(1 row)
```
