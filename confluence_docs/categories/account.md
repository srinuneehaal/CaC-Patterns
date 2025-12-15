# Category: ACCOUNT (GL accounts)

## Directory & model
- GL account definitions reside under `changedfiles/<scope>/gla/*.json`. `AccountFileParsingStrategy` produces `AccountFile` objects containing lists of GL `Account` entries, with keys normalized as `COACODE-AccountCode`.

## Planning
- `PlanService` compares each account list to the persisted snapshot (e.g., `cosmos://ACCOUNT/<scope>/<COACODE-AccountCode>`) and emits per-account `PlanItem`s for creations, updates, or deletes.

## Application
- The apply path rewrites the entire `AccountFile` snapshot via `StateFileService`, ensuring the Cosmos document contains the full `chartOfAccountsCode`, the `scope`, and the `glAccounts` collection.

## References
- `spec/plan-apply/all-categories.md#account-files-(gl-accounts)` provides the diagram that maps parsing, comparison, and persistence.  
- Mention this doc in knowledge-share sessions with the `docs/file-processing.md` table so teammates see where GL accounts live relative to other directories.
