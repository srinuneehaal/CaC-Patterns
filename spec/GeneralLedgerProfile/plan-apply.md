# Plan & Apply: General Ledger Profile

## Plan responsibilities
- Directory: `changedfiles/<scope>/glprofile`.
- Strategy: `GeneralLedgerProfileFileParsingStrategy` yields `GeneralLedgerProfileFile` payloads containing `GeneralLedgerProfileRequest`.
- `PlanService` normalizes keys (e.g., `profileCode-coa-scope`), compares requests with Cosmos documents, and emits `PlanItem`s as needed.

## Apply responsibilities
- `GeneralLedgerProfilePlanItemApplier` routes payloads to `GeneralLedgerProfileApiService` and uses `StateFileService` to keep the persisted profile document updated.

## Diagrams
- `spec/plan-apply/all-categories.md#general-ledger-profile` includes the flowchart for this category.
- `diagrams/sequence.mmd` is useful to explain where GL profiles sit relative to charts and posting rules in the plan/apply flow.
- Trace the detailed steps via `spec/GeneralLedgerProfile/generalledgerprofile-plan.mmd` and `spec/GeneralLedgerProfile/generalledgerprofile-apply.mmd`.
