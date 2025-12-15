# Category: ABOR_CONFIGURATION

## Directory & model
- `changedfiles/<scope>/aborconfigs/*.json` parse into `AborConfigurationFile` models that wrap lists of `AborConfigurationRequest`s. Keys are derived from the containing file name (e.g., `aborconfig-ATG`).

## Planning
- `PlanService` deserializes the list, compares it with the existing Cosmos snapshot (type `ABOR_CONFIGURATION`), and emits plan items for added/updated configurations plus deletes for missing entries.

## Application
- The apply path rewrites the aggregated list document via `StateFileService`, so the stored payload represents the latest configuration per scope.

## References
- `spec/plan-apply/all-categories.md#abor-configuration` includes the Mermaid sequence that shows parser → diff → upsert.  
- Mention this doc whenever you talk about list-type categories alongside derived portfolios and portfolio groups.
