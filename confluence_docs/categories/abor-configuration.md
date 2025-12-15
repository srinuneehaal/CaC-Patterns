# Category: ABOR_CONFIGURATION

## Directory & model
- `changedfiles/<scope>/aborconfigs/*.json` parse into `AborConfigurationFile` models that wrap lists of `AborConfigurationRequest`s. Keys are derived from the containing file name (e.g., `aborconfig-ATG`).

## Planning
- `PlanService` deserializes the list, compares it with the existing Cosmos snapshot (type `ABOR_CONFIGURATION`), and emits plan items for added/updated configurations plus deletes for missing entries.

## Application
- The apply path rewrites the aggregated list document via `StateFileService`, so the stored payload represents the latest configuration per scope.

## Strategy classes
- Planner: `AborConfigurationFileParsingStrategy`.  
- Apply: `AborConfigurationPlanItemApplier` using `StateFileService` list persistence helpers.

## Flow diagram
- The `sequenceDiagram` in `spec/plan-apply/all-categories.md#abor-configuration` illustrates the parser -> diff -> upsert process for config lists.

## Sequence diagram
- Reference `diagrams/sequence_1.mmd` when narrating how ABOR configs move through the plan/apply cycle; the apply step writes the aggregate list back to Cosmos.
