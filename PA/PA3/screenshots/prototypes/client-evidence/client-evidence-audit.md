# Client Evidence Integration Audit
> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

Date: 2026-11-02. Branch: `documentation`.

**Evidence source: Figma Make Prototype — Simulated UI.** These are browser prototype captures using simulated, in-memory financial data, not Android emulator screenshots. No backend integration, PostgreSQL persistence, real JWT authentication, real device behavior or native Android pixel accuracy is verified. Imported validation claims belong to the supplied archive; no browser scenario tests were rerun during this documentation integration.

## Input provenance
> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

The expected `_incoming` paths were absent:

- `PA/PA3/_incoming/UseCases-Client.md`
- `PA/PA3/_incoming/FinSync_PA3_All_Client_UseCases_Evidence (1).zip`
- `PA/PA3/_incoming/Prototype-Link.md`

Used the explicit user-provided alternatives: `C:\Users\namda\FinSync-PA3\UseCases-Client.md`, `C:\Users\namda\Downloads\FinSync_PA3_All_Client_UseCases_Evidence.zip`, and `C:\Users\namda\FinSync-PA3\figma\Prototype-Link.md`. No `_incoming` folder was created/deleted and no source ZIP was added to Git. Original archive metadata files prefixed `source-` are retained verbatim; their statuses are provenance claims, not independent test results. The normalized [matrix](Screenshot-Matrix.csv) provides working screenshot paths and visible evidence limitations. Some source metadata contains replacement characters in punctuation; original bytes were preserved.

## Checks
> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

- 65/65 PNGs decoded, all412 ×917; all CSV/manifest SHA-256 values match.
- Exactly12 UC IDs, UC-CLI-01 through UC-CLI-12; each has BF/AF/EF captures.
- All65 images visually reviewed using contact sheets generated outside the repository.
- All65 filenames preserved and embedded in detailed use-case documents; 63 unique image hashes. Three named scenario artifacts share one hash.
- No browser tests were rerun. Image inspection does not establish native Android, backend, database, authentication or persistent financial correctness.
- Pre-existing repository files were hash-compared; only the additive Section D insertion changed report.md. No teammate-owned file was changed.

## Visible-state limitations
> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

- `UC-CLI-01_AF_01_PrivacyAndPeriod.png`: The October point/period is visible, but financial totals remain visible; this image does not demonstrate the privacy-hidden state.
- `UC-CLI-02_AF_02_ArchiveAndRestore.png`: The list and Wallet restored feedback are visible; the preceding archive/cancel/restore sequence is not independently shown.
- `UC-CLI-03_BF_02_TransferSuccess.png`: Transfer history shows a simulated entry; unchanged net worth and income/spending across the operation require separate behavioral evidence.
- `UC-CLI-03_AF_01_ChangedTransferCancelled.png`: Empty transfer history is visible; swap/edit/cancel intermediate controls are not shown.
- `UC-CLI-04_AF_01_AddIncome.png`: History and Income saved simulated feedback are visible; this is the final income result, not an image of the Income form.
- `UC-CLI-04_AF_02_KeepDraft.png`: A populated draft is visible; the preceding keep/discard dialog action is not shown.
- `UC-CLI-04_BF_03_SaveSuccess.png`: History contains the PA3 lunch entry and an Expense saved simulated message; no server persistence is implied.
- `UC-CLI-05_AF_01_CombinedFilters.png`: The resulting filtered list is visible; the selected filter values and reset sequence are not independently shown.
- `UC-CLI-06_BF_01_EditedTransaction.png`: The details screen shows the edited amount/note; recalculation across summaries is not independently shown.
- `UC-CLI-06_AF_01_ChangedTransactionFields.png`: Updated fields appear on the details screen; the field-editing sequence is not shown.
- `UC-CLI-08_AF_02_BudgetDrilldown.png`: The budget status and View food transactions link are visible; the downstream transaction list and Back transition are not shown.
- `UC-CLI-09_AF_03_FilteredReport.png`: The capture shows transaction history with four active filters, not a filtered Reports chart.
- `UC-CLI-10_AF_01_ExistingGroup.png`: The capture shows the existing group member list.
- `UC-CLI-12_AF_01_CancelledSettlement.png`: The settlement form is visible with its amount; the earlier confirmation/cancel action is not shown.
- `UC-CLI-06_AF_02_CancelledEdit.png`: Identical final transaction-details pixels are shared with the other cancelled-edit/cancel-deletion/Undo captures. The image alone does not independently demonstrate the intermediate action, preserved ID or recalculated balances.
- `UC-CLI-07_AF_01_CancelDeletion.png`: Identical final transaction-details pixels are shared with the other cancelled-edit/cancel-deletion/Undo captures. The image alone does not independently demonstrate the intermediate action, preserved ID or recalculated balances.
- `UC-CLI-07_AF_02_UndoRestoresOriginal.png`: Identical final transaction-details pixels are shared with the other cancelled-edit/cancel-deletion/Undo captures. The image alone does not independently demonstrate the intermediate action, preserved ID or recalculated balances.

## Review warnings
> *Performed by: Nguyễn Phú Đạt (24120038) | Reviewed by: Pending team review | Edited by: Pending confirmation*

The source UseCases document remains a proposed specification, with conditional/optional behaviors. Declared scenario coverage does not establish every source-spec branch. The prototype reference was corrected on 2026-11-02 to the Figma Make URL supplied by Nguyễn Phú Đạt; see [current prototype reference](../../../use-cases/client-prototype-link.md). The previous Figma Design URL and obsolete four-screen remaining-work list have been replaced. This link correction did not independently verify live access or preview interactions. Pending team review/editor confirmation and pre-existing report TODO placeholders remain; they were not attributed to this Client contribution or silently removed. [Machine-readable verification](integration-verification.json) records coverage, sources and file changes.
