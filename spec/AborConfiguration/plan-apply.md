# Plan & Apply: ABOR Configuration

## Plan responsibilities
- Directory: `changedfiles/<scope>/aborconfigs`.
- Strategy: `AborConfigurationFileParsingStrategy` parses lists of `AborConfigurationRequest`s keyed by file name (e.g., `aborconfig-ATG`).
- `PlanService` compares the list snapshot with Cosmos state and emits `PlanItem`s for new/updated entries plus deletes for removed configurations.

## Apply responsibilities
- `AborConfigurationPlanItemApplier` relies on `StateFileService` list persistence helpers to write the new aggregate list per scope.

## Diagrams
- Refer to `spec/plan-apply/all-categories.md#abor-configuration` for the sequence diagram showing parser ƒ+' diff ƒ+' state upsert.
- Use `diagrams/sequence_1.mmd` when showing list-type flows like ABOR configurations during knowledge share.
- Draw the detailed stages from `spec/AborConfiguration/aborconfiguration-plan.mmd` and `spec/AborConfiguration/aborconfiguration-apply.mmd`.
