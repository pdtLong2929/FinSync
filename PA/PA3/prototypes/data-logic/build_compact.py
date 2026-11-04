"""Generate the concise Part D contribution; implementation notes live separately."""
import html
import json
import re
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
ATTR = '> *Performed by: `Nguyen Le Duc Nhat` | Reviewed by: Pending team review | Edited by: Pending confirmation*'


def heading(level, text):
    return '\n' + '#' * level + ' ' + text + '\n' + ATTR + '\n\n'


def table(labels, rows):
    def clean(x):
        return str(x).replace('|', '&#124;').replace('\n', '<br>')
    return '| ' + ' | '.join(labels) + ' |\n|' + '|'.join(['---'] * len(labels)) + '|\n' + ''.join('| ' + ' | '.join(map(clean, row)) + ' |\n' for row in rows) + '\n'


# Each row specifies a business branch, rather than an implementation-level screen.
# title, branch point, ordered response, return, resulting state
BRANCHES = {
2: [
    ('Income or edit', 'Step 2: change type or open an existing entry', '1. Choose an income category or edit allowed values. 2. Recalculate affected wallet/category totals; keep the original ID when editing.', '3', 'Only the difference changes totals.'),
    ('Delete / Undo', 'Step 2: Delete selected', '1. Confirm deletion and recalculate. 2. Offer Undo for 7 seconds; valid Undo restores the same ID/values once. If expired or unavailable, return to history without recreating it.', '5 / end', 'Deleted or restored once.'),
    ('Offline or receipt pending', 'Step 4: network/upload unavailable', '1. Save an authorized personal change locally as Pending and invoke UC-DL-12 on reconnection. 2. If only the receipt fails after posting, retry the upload without another financial entry.', 'End / UC-DL-12', 'Local and server status remain distinct.'),
    ('Link a group movement', 'Step 2: money relates to a group event', '1. Select the authorized source event. 2. Link the movement rather than creating another consumption entry; the user\'s share is counted once.', '3', 'Cash and consumption are not duplicated.'),
    ('Invalid input or reference', 'Step 3: bad amount/date/category/receipt or ineligible wallet', '1. Identify the invalid field/file or reference. 2. Retain valid input and ask for correction; reject the whole invalid write.', '2', 'No invalid financial effect.')],
5: [
    ('Pending / rejected / withdrawn', 'Step 4: no confirmation, rejection or contributor withdrawal', '1. Keep a new claim Pending until the Owner responds. 2. On rejection store the reason; allow withdrawal only while Pending.', 'End', 'Confirmed fund remains unchanged.'),
    ('Invalid claim or missing rate', 'Step 3: invalid amount/date or foreign currency', '1. Validate a positive contribution. 2. Show original amount and approved quote; if no rate exists, defer the conversion-dependent claim rather than inventing a total.', '2 / 3', 'Only valid reviewed claims proceed.'),
    ('Wrong confirmer / repeated decision', 'Step 4: another member confirms or status already changed', '1. Require the fund custodian. 2. Recheck claim status; a prior confirmation returns its existing result, a conflicting decision is rejected.', 'End', 'One receipt effect at most.'),
    ('History / empty fund', 'Step 1: inspect fund without contributing', '1. Show confirmed cash and member positions separately from pending claims. 2. Show zero if empty; unused-fund return opens UC-DL-08.', 'End', 'No ledger mutation.')],
6: [
    ('Alternative split mode', 'Step 2: Exact, Percentage or Shares', '1. Enter exact amounts totaling the bill, percentages totaling 100%, or positive weights. 2. Calculate final shares and distribute rounding remainders in a stable order.', '3', 'Final shares sum exactly to the expense.'),
    ('Change participants / payer', 'Step 2: selection changes', '1. Verify all selected identities belong to the group. 2. Recalculate and remove obsolete draft allocations before review.', '3', 'No hidden shares for removed participants.'),
    ('Pay from shared fund', 'Step 2: fund selected', '1. Check custodian permission and available confirmed cash. 2. Debit the fund instead of crediting an individual payer; update member fund positions once.', '3', 'Only one funding source is used.'),
    ('Foreign-currency bill', 'Step 2: currency differs from group', '1. Show original amount and rate/source/time. 2. Convert once, then split and retain the snapshot. Missing/changed quote requires a new review.', '3 / 2', 'No silent repricing of historical shares.'),
    ('Invalid allocation / insufficient funds', 'Step 3 or 4: mismatch, no participants, bad weights or low fund cash', '1. Display the difference or available amount. 2. Keep the draft and reject the entire expense until corrected.', '2', 'No partial expense, allocation or fund debit.')],
7: [
    ('Inspect / simplify / remind', 'Step 1: no payment submitted', '1. Show transfers preserving every member\'s net position. 2. If requested, send an eligible deduplicated reminder; do not create a payment.', 'End', 'Debt unchanged.'),
    ('Partial payment', 'Step 2: amount is below outstanding debt', '1. Review the smaller positive amount. 2. After recipient confirmation reduce only that amount: 50,000 paid against 100,000 leaves 50,000.', '3', 'Remaining debt is retained.'),
    ('Pending / rejected / withdrawn', 'Step 4: recipient does not confirm, rejects, or debtor cancels', '1. Keep pending debt unchanged. 2. Record rejection reason or cancel an unconfirmed claim; never cancel an already confirmed payment through this branch.', 'End', 'Only confirmed payments reduce debt.'),
    ('Wrong confirmer', 'Step 4: Owner or another person tries to confirm', '1. Check intended recipient identity. 2. Reject confirmation by anyone else, including the Owner.', 'End', 'Claim and debt unchanged.'),
    ('Invalid or stale debt', 'Step 3 or 4: invalid/excess amount or changed debt', '1. Recompute current outstanding debt. 2. Reject an excess claim and request fresh review; repeated confirmation returns the existing result.', '2', 'No overpayment or duplicate effect.')],
8: [
    ('Full / partial / repeated refund', 'Step 2: refund amount chosen', '1. Show unrefunded expense and per-member amounts. 2. Reverse only the selected remaining amount using the original allocation/rate basis and exact rounding.', '3', 'Cumulative refunds never exceed original shares.'),
    ('Refund after settlement', 'Step 4: original debt was already paid', '1. Retain all prior payments. 2. Show new reimbursement positions; any subsequent external payment uses UC-DL-07.', '5', 'No settled history is erased.'),
    ('Refund to shared fund', 'Step 2: original bill was fund-paid', '1. Verify the original funding source and returned money. 2. Credit the fund and reverse allocated consumption once.', '3', 'Refund is not new income.'),
    ('Return unused funds', 'Step 1: fund return selected', '1. Check cash and the member\'s entitlement; create a return claim. 2. Intended recipient confirms receipt; pending/rejected/withdrawn claims do not reduce confirmed cash.', '5 / end', 'Confirmed return reduces cash and entitlement once.'),
    ('Invalid refund / changed position', 'Step 3 or 4: missing original, excess amount, missing quote or stale entitlement', '1. Show the current permitted amount or missing evidence. 2. Reject the adjustment and retain a correctable draft; the Owner cannot confirm receipt for another member.', '2', 'No unlinked or excess correction.')],
12: [
    ('Offline again / app restart', 'Step 4: connection or process stops', '1. Keep unacknowledged operations durable. 2. Resume remaining work on reconnection; already acknowledged items stay synchronized.', '4', 'No acknowledged local edit is lost.'),
    ('Expired session / account switch', 'Step 3: authorization unavailable or logout requested', '1. Pause and request valid sign-in for the same account. 2. Before logout warn about pending edits; allow cancel-and-sync or explicit discard, then clear account-local data.', '3 / end', 'No cross-account upload.'),
    ('Edit or delete conflict', 'Step 4: server version differs', '1. Show local/server values and any tombstone. 2. Keep server, reapply against the refreshed version, or explicitly recreate a deleted entry with a new ID; never overwrite/resurrect silently.', '4', 'Conflict remains visible until resolved.'),
    ('Duplicate / lost acknowledgement', 'Step 4: replay after uncertain response', '1. Reuse the original operation identity and retrieve its outcome. 2. Acknowledge the existing result without applying it again.', '4', 'Exactly one financial effect.'),
    ('Reference rejected / receipt failed', 'Step 4: wallet/category ineligible or upload fails', '1. Mark the invalid operation Action required and retain its draft; retry only a failed receipt upload. 2. Continue independent valid records.', '4', 'No second financial entry; partial progress is explicit.'),
    ('Offline group write', 'Step 2: user tries to change shared data', '1. Show cached group data as read-only. 2. Require an online session before shared writes; do not enqueue them as personal changes.', 'End', 'No unauthorized offline group update.')],
13: [
    ('Automatic monthly run', 'Step 1: scheduled trigger instead of user request', '1. Select each eligible user\'s closed month/currency. 2. Generate and publish one authorized result; duplicate triggers reuse the same logical run.', '2', 'No repeated monthly publication.'),
    ('Chart / group / export variant', 'Step 2: user changes view/scope/format', '1. Recheck group access and apply the same filters to totals and details. 2. Export the reviewed snapshot as PDF or Excel; never expose private member wallets.', '3', 'View and export agree.'),
    ('Empty data / invalid range / missing rate', 'Step 3: unavailable or invalid reporting inputs', '1. Reject invalid ranges; show zero/empty data or growth as not applicable for a zero baseline. 2. If conversion is missing, show original-currency subtotals and incomplete status.', '2 / end', 'No invented totals or growth.'),
    ('Cached data / later changes', 'Step 3 or 5: offline viewing or source changes', '1. Label cached/pending data and its cutoff. 2. Regeneration creates an explicit revised snapshot; it does not silently rewrite a previously issued report.', '3 / end', 'The report remains attributable to its source period.'),
    ('Processing / export / delivery failure', 'Step 4 or 5: an output stage fails', '1. Preserve confirmed results and retry the failed unit without re-posting financial data. 2. Keep delivery pending, or mark failed after the retry limit; revoke access if eligibility changed.', '4 / end', 'No false success or unauthorized download.'),
    ('Scheduled run interrupted / cancelled', 'Step 4: worker restart or operator stop', '1. Resume from acknowledged work without duplicating published results, or stop new work at safe boundaries. 2. Retain completed results and expose remaining/failed counts.', '4 / end', 'Partial completion is explicit.')],
14: [
    ('Nothing due / already notified', 'Step 2 or 4: no eligible event or existing logical alert', '1. Skip the item or finish with zero recipients. 2. Do not create placeholder or duplicate messages.', '4 / end', 'One logical alert at most.'),
    ('Debt settled / access revoked', 'Step 4 or 5: queued information became stale', '1. Recheck debt and recipient rights. 2. Suppress obsolete reminders or protected content before delivery.', '4', 'No false debt reminder or data leak.'),
    ('Overlapping worker / restart', 'Step 3 or 4: concurrent processing or worker failure', '1. Resume unacknowledged work only. 2. Reject stale completion and reuse existing committed event identities.', '4', 'No lost acknowledged item or duplicate event.'),
    ('Transient / permanent failure', 'Step 4 or 5: evaluation or delivery fails', '1. Retry transient errors within a configured limit; retain the failed item after permanent/exhausted failure. 2. Continue other recipients and report Partial/Failed.', '4 / end', 'Failure is visible and retryable, not silently dropped.'),
    ('Pause / cancel / invalid trigger', 'Step 1 or 3: invalid service identity or operator stop', '1. Reject an invalid/untracked start, or stop new claims at item boundaries. 2. Keep completed events and checkpoints for an authorized resume.', 'End', 'No uncontrolled processing or ledger change.')]
}


def extract_appendix():
    target = ROOT / 'use-cases' / 'D_backend_notes.md'
    if target.exists():
        return
    old = (ROOT / 'use-cases' / 'D_usecase.md').read_text(encoding='utf-8')
    out = heading(1, 'Data & Logic — Backend Design Notes (Supporting Appendix)')
    out += 'Implementation detail moved out of [D_usecase.md](D_usecase.md) to keep Part D focused on actor goals, business flows and outcomes. These notes are not additional Use Cases or completed backend implementation.\n\n'
    out += '**Baseline caveat:** PA2 recipient/custodian confirmation, own-group-share recognition, historical rates, refunds and offline behavior remain requirements unless the team explicitly revises Part B. The current database v1 does not implement all of them.\n'
    for oldnum, title in [('2','Baseline reconciliation'),('5','Common backend contract'),('6','Transactions, synchronization and batch processing')]:
        m = re.search(r'(?ms)^### D\.1\.'+oldnum+r'\..*?\n>[^\n]*\n(.*?)(?=^### |^## |\Z)', old)
        if m:
            out += heading(2, 'A.'+oldnum+'. '+title) + m.group(1).strip()+'\n'
    out += heading(2, 'A.7. Supporting API and Persistence Notes')
    for m in re.finditer(r'(?ms)^## D\.\d+\. (UC-DL-\d+) — ([^\n]+)(.*?)(?=^## |\Z)', old):
        contract = re.search(r'(?s)\*\*Backend processing contract:\*\*(.*?)(?=\n### )',m.group(3))
        if contract:
            out += heading(3,m.group(1)+' — '+m.group(2)) + contract.group(1).strip()+'\n'
    out += '\n**Scope migration:** The compact Part D keeps UC-DL-02, 05–08 and 12–14. UC-DL-11 reporting is merged into UC-DL-13. Other IDs above are retained only as historical implementation references for the relevant Client/team work; they are not extra cases in the compact contribution. Performance/retry defaults remain proposals, not measured results.\n'
    out = out.replace('Section D.1.2','this appendix').replace('gaps in D.1.2','gaps in this appendix')
    target.write_text(out,encoding='utf-8')


def state(sid,title,tag,fields,message,action='Continue',secondary='Back'):
    return dict(id=sid,title=title,tag=tag,fields=fields,message=message,action=action,secondary=secondary)


def main():
    extract_appendix()
    cases=json.loads((HERE/'compact_source.json').read_text(encoding='utf-8'))['cases']
    for c in cases:
        if c['n']==13:
            c.update(title='Generate Financial Reports on Demand or Automatically',actor='Regular User; Deployment Scheduler for monthly runs',support='Authorized operator monitoring or retrying a scheduled run.',goal='Produce authorized personal/group reports with consistent totals, an explicit currency basis and optional PDF/Excel output.',trigger='The user opens Reports, or the monthly scheduler starts a due report.',pre='1. Valid user or scheduler authorization.<br>2. Requested data scope and period are permitted.<br>3. Conversion-dependent totals have an identified rate basis.',fr='On-demand and monthly report aggregation, filters, private publication and PDF/Excel export.',entry='Reports / Period and scope',input='Choose personal/group scope, period, currency and output format; a scheduled run derives the closed month for each eligible user.',review='Review the period, currency basis and source cutoff; preview the same totals used for details and export.',commit='Aggregate the authorized snapshot; generate the report and requested artifact; record completion before publication.',result='The user can inspect or download the report with period/currency/cutoff labels. A scheduled result is published once; the financial ledger is unchanged.',sample=['Scope: Personal consumption','Period: October 2026','Currency: VND','Output: Screen / PDF / Excel'])
        c['allAlt']=[dict(id=f'AF-{i:02d}',a=list(a)) for i,a in enumerate(BRANCHES[c['n']],1)]
        c['screens']=[
            state(c['id']+'-S01',c['entry'],'Basic steps 1–2',c['sample'][:4],c['input'],'Review'),
            state(c['id']+'-S02','Review / confirmation','Basic step 3',c['sample'][:3],c['review'],'Confirm','Edit / cancel'),
            state(c['id']+'-S03','Result and details','Basic steps 4–5',['Status is explicit: pending / confirmed / complete'],c['result'],'View details'),
            state(c['id']+'-S04','Alternative actions','Alternative flow variants',[a[0] for a in BRANCHES[c['n']][:3]],'Choose the applicable action, then review its effect before confirmation. Each alternative keeps its own permission and financial rules.','Review selected action'),
            state(c['id']+'-S05','Validation / changed data','Alternative flow variants',[a[0] for a in BRANCHES[c['n']][3:]],'Show the affected value, permitted amount or unavailable source. Keep valid input; correct or reload before retrying. No invalid partial effect is accepted.','Correct / refresh'),
            state(c['id']+'-S06','Pending / recovery state','Shared state variants',['Pending: awaiting confirmation or connection','Failed: show reason and a safe retry','Complete: show the acknowledged result'],'Display the applicable state, not all three at once. Retrying must not duplicate an already acknowledged financial effect.','Inspect status / retry')]
        if c['n']==14:
            c['screens'][0].update(action='View run status')
            c['screens'][1].update(title='Due scope / progress', action='Refresh progress', secondary='Back', message='Scheduled processing starts without human confirmation. The operator sees eligible, processed and failed counts.')
            c['screens'][3].update(title='Skipped / resumed items', message='The run records why items were skipped or resumed. These outcomes follow processing conditions; they are not user-selected financial actions.', action='View item outcomes')
        c['boards']=[dict(filename=c['id']+'-compact.png',screen_ids=[s['id'] for s in c['screens']])]
    common=dict(id='DL-COMMON',title='Reusable dialogs and recovery states',n=0,screens=[
        state('DL-C01','Cancel or discard','Common flow C1',['Draft has not been committed'],'Keep editing, or discard this draft. Already confirmed records are not undone by Back.','Keep editing','Discard'),
        state('DL-C02','Session / access changed','Common flow C2',['Session expired or permission revoked'],'Stop the protected action. Sign in again when permitted; do not expose data through stale permissions.','Sign in','Return'),
        state('DL-C03','Input validation','Domain validation branches',['Highlight invalid values and permitted range'],'Retain valid input and identify what must be corrected. Missing rates or sources are not replaced by invented values.','Correct input'),
        state('DL-C04','Record changed','Common flow C3',['Current values differ from the reviewed values'],'Refresh, compare changes and confirm again. Do not silently overwrite newer values.','Refresh and review'),
        state('DL-C05','Result not yet confirmed','Common flow C4',['Connection or service failure'],'Check the existing request outcome before retry. Show Pending or Failed until a confirmed result is known.','Check status / retry'),
        state('DL-C06','Already processed','Common flow C5',['The same request has already completed'],'Open the existing result. Do not post another transaction, claim, report or alert for the same logical action.','Open result')])
    common['boards']=[dict(filename='DL-COMMON-compact.png',screen_ids=[s['id'] for s in common['screens']])]
    manifest=dict(version='Compact v2.0',provenance='Static specification wireframe variants, not live Figma/backend evidence.',cases=cases+[common])
    (HERE/'screens.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2),encoding='utf-8')
    doc=heading(1,'Part D — Data & Logic Use-Case Specifications')
    doc+='**FinSync · Group 04 · PA3-2026 · Version 2.0 (compact), 2026-11-04.** This contribution follows [the Part D requirements](pa3_project_assignment_specification.md) and the five-subsection [report template](report.md). Detailed API, database-locking and job-runtime design is kept separately in [Backend Notes](D_backend_notes.md).\n'
    doc+=heading(2,'D.1. Scope and Shared Conventions')
    doc+='**Scope:** Eight core use cases cover financial processing, shared-fund claims, splitting, settlement, refunds, synchronization, reporting and batch notifications. Existing IDs are preserved; gaps in numbering do not indicate missing cases. Part C is still a team template, so this is a scoped catalogue for integration, not a claim that the team has approved its final use-case model.\n\n'
    doc+='Wallet setup, member administration, basic navigation, searches and form details are referenced from the [Client catalogue](use-cases/client-use-cases.md), rather than specified twice. Account/Admin and AI remain separate contributions. Budget creation and savings-goal management are outside this narrowed assignment and still need explicit team coverage; Client budget viewing alone does not satisfy those requirements. On-demand reporting (former UC-DL-11) is merged into UC-DL-13.\n\n'
    doc+='**Business baseline:** Preserve PA2 bookkeeping-only payments, exact rounding, recipient/custodian confirmation, historical currency snapshots, own-group-share recognition and durable offline personal changes. The narrower database v1 differs in these areas; [the appendix](D_backend_notes.md) records the gaps. This document does not silently approve scope reductions or claim those backend functions are implemented.\n\n'
    doc+='**Prototypes:** [Local HTML wireframes](prototypes/data-logic/index.html) and labeled PNG exports are newly authored specification designs, not captures proving implemented features. Each case reuses six frames for its form/review/result and domain variants; common dialogs are shown once below. Alternative flows specify the content/action variant in that reused frame. Amounts and rates are illustrative.\n'
    doc+=table(['Use case','Goal / related client scope','Local requirement'],[(c['id'],c['title'],f"FR-DL-{c['n']:02d}") for c in cases])
    doc+='FR-DL IDs are local traceability labels to merge into Part B; they are not asserted to be already approved requirement IDs.\n\n'
    doc+='```mermaid\nflowchart LR\n    user["Regular User / Group Member"]\n    owner["Group Owner / custodian"]\n    scheduler["Deployment Scheduler"]\n    operator["Authorized Operator"]\n    owner -->|"generalization"| user\n    subgraph FinSync["FinSync — Data & Logic"]\n'
    for c in cases: doc+=f'        u{c["n"]}(["{c["id"]}: {c["title"]}"])\n'
    doc+='    end\n'
    for c in cases:
        n=c['n']
        if n!=14: doc+=f'    user --- u{n}\n'
        if n in [5,6,8]: doc+=f'    owner --- u{n}\n'
        if n in [13,14]: doc+=f'    scheduler --- u{n}\n    operator --- u{n}\n'
    doc+='```\n\nActors are outside FinSync; database/worker internals are not actors. Generalization denotes inherited participation. No include/extend relationship is forced onto validation steps; cross-referenced workflows have separate triggers.\n'
    doc+=heading(3,'D.1.1. Common Alternative and Exception Flows')
    doc+='C1–C5 apply wherever their condition is relevant; each case references this table instead of repeating it. Scheduled runs have no user form to discard; their explicit stop/recovery branch applies instead.\n\n'
    doc+=table(['ID / condition','Actions and outcome','Return / shared screen'],[
        ('C1 — Cancel before confirmation','Keep editing or discard the uncommitted draft; no new effect.','Step 2 or end; DL-C01'),
        ('C2 — Session/permission changed','Stop the action; reauthenticate if permitted, otherwise deny without exposing data.','Step 1 or end; DL-C02'),
        ('C3 — Stale review/concurrent edit','Show current values, retain a safe draft and request fresh confirmation; no silent overwrite.','Steps 2–3; DL-C04'),
        ('C4 — Network/service failure','Do not claim success; reconcile the original request outcome before retry. Confirmed items stay committed; unresolved items remain pending/failed.','Step 4 or end; DL-C05'),
        ('C5 — Duplicate submission/replay','Return the existing acknowledged result rather than apply the action again.','Step 5; DL-C06')])
    doc+='![Shared cancellation, validation and recovery dialogs](screenshots/prototypes/data-logic/DL-COMMON-compact.png)\n\n*Shared prototype: DL-C01–06. These same dialogs are referenced by the specifications below; a new screen is not required for each backend branch.*\n'
    for section,c in enumerate(cases,2):
        n=c['n']; cid=c['id']; prefix=f'D.{section}'
        doc+=heading(2,prefix+'. '+cid+' — '+c['title'])
        doc+=heading(3,prefix+'.1. Overview and Preconditions')
        doc+=table(['Field','Specification'],[
            ('Use-case ID and name',cid+' — '+c['title']),('Primary actor',c['actor']),('Supporting actor(s)',c['support']),('Description / goal',c['goal']),('Trigger',c['trigger']),('Related requirements / rules',f"FR-DL-{n:02d}; bookkeeping only, authorized data, exact amounts and no duplicate consumption."),('Related NFRs',c['nfr'].replace('; DL-NFR-02 and DL-NFR-03 below.','; recoverable, duplicate-safe background processing.')),('Relationships','Actor associations in D.1; shared C1–C5 apply as relevant. No mandatory included sub-use-case.'),('Preconditions',c['pre'])])
        doc+=heading(3,prefix+'.2. Basic Flow — Main Success Scenario')
        actor4='Intended recipient confirms receipt.' if n==7 else 'Group Owner verifies and confirms receipt.' if n==5 else 'Wait for the acknowledged result.'
        if n==14: actor4='Scheduler starts processing; operator monitors progress.'
        doc+=table(['Step','Actor action','System response / state change','Screen'],[
            (1,c['trigger'],'Check access; show eligible scope and current state.','S01'),
            (2,c['input'],'Validate the selected inputs and prepare a review; no financial effect yet.','S01'),
            (3,'Review and confirm the action.' if n!=14 else 'Scheduler supplies the due scope.',c['review'],'S02'),
            (4,actor4,c['commit'],'S03'),
            (5,'Open the authorized result or status.',c['result'],'S03')])
        doc+=heading(3,prefix+'.3. Alternative and Exception Flows')
        doc+=table(['Flow / branch point','Ordered actions','Resume / resulting state','Screen variant'],[(f"AF-{i:02d} — {a[0]}<br>{a[1]}",a[2],f"{a[3]} — {a[4]}",'S04' if i<=3 else 'S05 / S06') for i,a in enumerate(BRANCHES[n],1)])
        doc+='**Shared branches:** C1–C5 from D.1.1 where applicable. Use DL-C03 for field validation; use S06 for the case-specific pending/recovery result. Domain branches above take precedence where a conflict, offline state or scheduled stop needs different behavior.\n'
        doc+=heading(3,prefix+'.4. Postconditions and Special Requirements')
        doc+='**Success:** '+c['result']+'  \n**Failure/cancellation:** No unauthorized or partially committed financial change. Pending claims do not change confirmed balances; acknowledged sync/batch items remain completed while other items can fail.  \n**Special requirements:** '+c['nfr'].replace('; DL-NFR-02 and DL-NFR-03 below.','; recoverable, duplicate-safe background processing.')+' Monetary values and historical rates must remain exact. These are requirements, not measured test results.\n'
        doc+=heading(3,prefix+'.5. UI Prototype Screenshots')
        doc+=f'**Prototype:** [Compact wireframes v2](prototypes/data-logic/index.html#{cid}). Screen IDs below use the `{cid}-` prefix. Shared dialogs DL-C01–06 are embedded once in D.1.1.\n\n'
        doc+=table(['Frames','Flow coverage','Purpose'],[
            ('S01–S03','Basic steps 1–5','Input/scope, review and acknowledged result.'),
            ('S04',', '.join(f'AF-{i:02d}' for i in range(1,min(3,len(BRANCHES[n]))+1)),'Alternative actions use the same entry/review layout with the selected variant.'),
            ('S05–S06',', '.join(f'AF-{i:02d}' for i in range(4,len(BRANCHES[n])+1))+'; pending/recovery states','Changed values, unavailable data or incomplete outcome; no extra screen per internal check.')])
        doc+=f'![{cid} — six reusable prototype frames](screenshots/prototypes/data-logic/{cid}-compact.png)\n\n*Figure {prefix} — {cid}-S01–S06. Labeled design variants illustrate the specified flows; they are not proof of executed backend behavior.*\n'
    doc+=heading(2,'D.10. Coverage and Team Handoff')
    doc+=table(['Leader requirement','Covered by'],[
        ('Core financial processing','UC-DL-02, 05–08: validation, exact allocation, confirmation, corrections and consistent results.'),
        ('Automatic report aggregation','UC-DL-13: user-requested or monthly reports, same snapshot for view/export, explicit failures and revisions.'),
        ('Data synchronization','UC-DL-12: pending changes, conflict resolution, replay, interruption and receipt retry.'),
        ('Batch jobs','UC-DL-14 and scheduled UC-DL-13: eligible work, progress, duplicate suppression, restart, partial failure and stop.')])
    doc+='**Scope transfer:** UC-DL-01 wallet setup → UC-CLI-02; UC-DL-03 transfers → UC-CLI-03; UC-DL-04 membership → UC-CLI-10. UC-DL-09 budget viewing → UC-CLI-08, while budget creation and UC-DL-10 savings management still require team assignment/specification. UC-DL-11 report viewing/export is consolidated into UC-DL-13. Keep the PA2 currency/confirmation requirements when reconciling those contributions. This is not the full team Part D.\n\n'
    doc+='**Before submission:** Merge these eight IDs into the actual Part C catalogue; reconcile baseline differences using [Backend Notes](D_backend_notes.md); obtain peer review and export the final Markdown/figures to PDF. AI-assisted drafting and wireframe creation should be declared in the team AI Usage Report.\n'
    doc = re.sub(r'\]\(([^)]+)\)', lambda m: '](' + (m[1][10:] if m[1].startswith('use-cases/') else m[1] if m[1] == 'D_backend_notes.md' or m[1].startswith(('http:', 'https:', '#')) else '../' + m[1]) + ')', doc)
    (ROOT/'use-cases'/'D_usecase.md').write_text(doc,encoding='utf-8')
    html_doc='<!doctype html><html lang="en"><meta charset="utf-8"><title>FinSync compact use-case prototypes</title><style>body{font:16px Segoe UI,Arial;margin:32px;background:#f3f1fa;color:#172036}section{margin:30px 0}.grid{display:grid;grid-template-columns:repeat(3,1fr);gap:16px}.card{background:white;border:1px solid #ddd;padding:22px;border-radius:14px;min-height:350px}.field{background:#f5f2fb;padding:10px;margin:6px 0}.tag{color:#6845ba}.action{background:#7850cd;color:white;padding:12px;margin-top:18px}h2{font-size:24px}</style><body><h1>FinSync — Compact specification wireframes</h1><p>Design variants only; no backend operations are executed.</p>'
    for c in manifest['cases']:
        html_doc+=f'<section id="{c["id"]}"><h2>{html.escape(c["id"]+" — "+c["title"])}</h2><div class="grid">'
        for s in c['screens']:
            html_doc+=f'<article class="card"><p class="tag">{s["id"]} · {html.escape(s["tag"])}</p><h3>{html.escape(s["title"])}</h3>'+''.join('<div class="field">'+html.escape(f)+'</div>' for f in s['fields'])+'<p>'+html.escape(s['message'])+'</p><div class="action">'+html.escape(s['action'])+'</div></article>'
        html_doc+='</div></section>'
    (HERE/'index.html').write_text(html_doc+'</body></html>',encoding='utf-8')
    print(f'Compact document: {len(cases)} use cases, {len(doc.splitlines())} lines; 9 boards / 54 reusable frames.')


if __name__=='__main__':
    main()
