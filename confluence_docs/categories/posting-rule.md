# Category: POSTING_RULE

## Directory & model
- `changedfiles/<scope>/postingrules/*.json` parses via `PostingRulesFileParsingStrategy` into `PostingModuleRequest` payloads. Keys follow the pattern `module-coa-scope`, ensuring the module and chart are paired.

## Planning
- The plan stage extracts the module code and chart, then compares the payload to the existing Cosmos document of type `POSTING_RULE`. Differences produce the matching `PlanItem`.

## Application
- `PostingModulePlanItemApplier` delegates to `StateFileService` to persist the module request along with the referenced chart code, keeping the aggregate document in sync for future runs.

## Strategy classes
- Planner: `PostingRulesFileParsingStrategy`.  
- Apply: `PostingModulePlanItemApplier` and `PostingModuleApiService`.

## Flow diagram
- The `graph TB` in `spec/plan-apply/all-categories.md#posting-rules` visualizes parser -> Cosmos type POSTING_RULE lookup -> `PlanItem` -> `StateFileService` upsert.

## Sequence diagram
- Reference `diagrams/system_design LLD.mmd` when explaining how posting rules move from JSON files through plan ordering to apply service.
