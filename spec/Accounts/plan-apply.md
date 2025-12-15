# Plan & Apply: Accounts (GL Accounts)

## Plan responsibilities
- Directory: `changedfiles/<scope>/gla`.
- Strategy: `AccountFileParsingStrategy` maps JSON into `AccountFile` containing `Account` entries (keyed `COACODE-AccountCode`).
- Plan flow compares each account list with Cosmos documents (e.g., `cosmos://ACCOUNT/<scope>/<key>`), emitting `PlanItem`s for added/updated/deleted entries.

## Apply responsibilities
- `AccountPlanItemApplier` delegates to `StateFileService.applyStateDocument`, re-writing the `AccountFile` snapshot so future plan runs can diff against the latest accounts.

## Diagrams
- `spec/plan-apply/all-categories.md#account-files-(gl-accounts)` offers the flowchart covering parser → normalization → persistence.  
- Show `diagrams/sequence_1.mmd` to highlight how GL accounts participate in the plan/apply cycle when discussing this category.
- Visualize this plan/apply flow via `spec/Accounts/accounts-plan.mmd` and `spec/Accounts/accounts-apply.mmd`.
