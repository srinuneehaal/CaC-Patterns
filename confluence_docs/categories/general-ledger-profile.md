# Category: GENERAL_LEDGER_PROFILE

## Directory & model
- Files under `changedfiles/<scope>/glprofile/*.json` are parsed by `GeneralLedgerProfileFileParsingStrategy` into `GeneralLedgerProfileFile` models containing `GeneralLedgerProfileRequest`.
- Keys follow the format `profileCode-coa-scope`, falling back to the filename when the chart is omitted.

## Planning
- `PlanService` normalizes the payload (extracting the profile request) and compares it with the Cosmos document under `cosmos://GENERAL_LEDGER_PROFILE/<scope>/<key>`. New or changed requests produce `PlanItem`s, while missing files result in deletes.

## Application
- `GeneralLedgerProfilePlanItemApplier` calls `GeneralLedgerProfileApiService` (a logging stub) and writes the snapshot through `StateFileService`, ensuring the persisted state remains aligned with configuration changes.

## Strategy classes
- Planner: `GeneralLedgerProfileFileParsingStrategy`.  
- Apply: `GeneralLedgerProfilePlanItemApplier` + `GeneralLedgerProfileApiService`.

## Flow diagram
- `spec/plan-apply/all-categories.md#general-ledger-profile` includes the flowchart tracing parser -> state document -> plan diff -> `GeneralLedgerProfileApiService` call.

## Sequence diagram
- Use `diagrams/sequence.mmd` to illustrate how the GL profile request steps in right after chart/account processing before the apply services persist the new profile.
