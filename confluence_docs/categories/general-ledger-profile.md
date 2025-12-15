# Category: GENERAL_LEDGER_PROFILE

## Directory & model
- Files under `changedfiles/<scope>/glprofile/*.json` are parsed by `GeneralLedgerProfileFileParsingStrategy` into `GeneralLedgerProfileFile` models containing `GeneralLedgerProfileRequest`.
- Keys follow the format `profileCode-coa-scope`, falling back to the filename when the chart is omitted.

## Planning
- `PlanService` normalizes the payload (extracting the profile request) and compares it with the Cosmos document under `cosmos://GENERAL_LEDGER_PROFILE/<scope>/<key>`. New or changed requests produce `PlanItem`s, while missing files result in deletes.

## Application
- `GeneralLedgerProfilePlanItemApplier` calls `GeneralLedgerProfileApiService` (a logging stub) and writes the snapshot through `StateFileService`, ensuring the persisted state remains aligned with configuration changes.

## References
- `spec/plan-apply/all-categories.md#general-ledger-profile` features the Mermaid diagram for this category.  
- Pair this page with `docs/plan-lifecycle.md` when explaining the overlapping responsibilities of parsing strategies and apply services.
