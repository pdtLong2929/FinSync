# FinSync — Regular User / Client Use Cases and Prototype Evidence
> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

**Evidence source: Figma Make Prototype — Simulated UI.** These are browser prototype captures using simulated, in-memory financial data, not Android emulator screenshots. No backend integration, PostgreSQL persistence, real JWT authentication, real device behavior or native Android pixel accuracy is verified. Imported validation claims belong to the supplied archive; no browser scenario tests were rerun during this documentation integration.

The twelve linked specifications preserve the supplied English source. This catalogue covers Client responsibilities only; existing Account/Admin, Backend and AI contributions are unchanged. The evidence demonstrates the declared simulated capture plan, not exhaustive production acceptance or every optional source-spec frame.

## Client catalogue and BF/AF/EF evidence coverage
> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

| Use case | Specification | BF | AF | EF | Total |
|---|---|---:|---:|---:|---:|
| UC-CLI-01 | [View Financial Overview](uc-cli-01-view-financial-overview.md) | 1 | 1 | 1 | 3 |
| UC-CLI-02 | [Manage Personal Wallets](uc-cli-02-manage-personal-wallets.md) | 1 | 3 | 1 | 5 |
| UC-CLI-03 | [Transfer Funds Between Wallets](uc-cli-03-transfer-funds-between-wallets.md) | 2 | 1 | 3 | 6 |
| UC-CLI-04 | [Record Income or Expense](uc-cli-04-record-income-or-expense.md) | 3 | 2 | 2 | 7 |
| UC-CLI-05 | [Search and Filter Transactions](uc-cli-05-search-and-filter-transactions.md) | 1 | 1 | 2 | 4 |
| UC-CLI-06 | [View and Edit Transaction](uc-cli-06-view-and-edit-transaction.md) | 1 | 2 | 1 | 4 |
| UC-CLI-07 | [Delete and Restore Transaction](uc-cli-07-delete-and-restore-transaction.md) | 2 | 2 | 1 | 5 |
| UC-CLI-08 | [View Budget Status](uc-cli-08-view-budget-status.md) | 1 | 2 | 1 | 4 |
| UC-CLI-09 | [View and Filter Financial Reports](uc-cli-09-view-and-filter-financial-reports.md) | 1 | 4 | 2 | 7 |
| UC-CLI-10 | [Create, Join, and View Groups](uc-cli-10-create-join-and-view-groups.md) | 1 | 2 | 2 | 5 |
| UC-CLI-11 | [Add and Split Group Expense](uc-cli-11-add-and-split-group-expense.md) | 2 | 3 | 4 | 9 |
| UC-CLI-12 | [View and Settle Group Debts](uc-cli-12-view-and-settle-group-debts.md) | 2 | 2 | 2 | 6 |

**Total:** 65 original PNG filenames across 12 use cases, all embedded in the detailed specifications. [Evidence matrix](../screenshots/prototypes/client-evidence/Screenshot-Matrix.csv) · [Audit and limitations](../screenshots/prototypes/client-evidence/client-evidence-audit.md) · [Prototype link](client-prototype-link.md).

## Evidence limitations and review status
> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

- Source CSV/manifest labels such as Implemented & Verified are imported supplier claims about the simulated browser prototype. This integration checked files, mappings, hashes, dimensions, links and visible states; it did not rerun 65 browser scenarios.
- The cancelled-edit, cancel-deletion and Undo final-details images are byte-identical. They remain distinct named scenario artifacts, but do not independently show their intermediate actions.
- Some captures show only a resulting screen; the audit documents privacy-hidden, budget drilldown, filtered report and other visible-state limitations.
- Team review, actual reviewer/editor confirmation, shared Figma access and reconciliation with the Part C use-case model remain pending.
