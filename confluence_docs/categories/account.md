# Category: ACCOUNT (GL accounts)

## Directory & model
- GL account definitions reside under `changedfiles/<scope>/gla/*.json`. `AccountFileParsingStrategy` produces `AccountFile` objects containing lists of GL `Account` entries, with keys normalized as `COACODE-AccountCode`.

## Planning
- `PlanService` compares each account list to the persisted snapshot (e.g., `cosmos://ACCOUNT/<scope>/<COACODE-AccountCode>`) and emits per-account `PlanItem`s for creations, updates, or deletes.

## Application
- The apply path rewrites the entire `AccountFile` snapshot via `StateFileService`, ensuring the Cosmos document contains the full `chartOfAccountsCode`, the `scope`, and the `glAccounts` collection.

## Strategy classes
- Planner: `AccountFileParsingStrategy`.  
- Apply: `AccountPlanItemApplier` with `StateFileService.applyStateDocument`.

## Flow diagram
- Refer to `spec/plan-apply/all-categories.md#account-files-(gl-accounts)` flowchart showing parser -> normalized key -> state comparison -> persistence.

## Sequence diagram
- Use `diagrams/sequence_1.mmd` in knowledge-share decks when describing how GL account plans are built, sorted, and applied through the state service.
