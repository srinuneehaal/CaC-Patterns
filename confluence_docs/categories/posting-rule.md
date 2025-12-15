# Category: POSTING_RULE

## Directory & model
- `changedfiles/<scope>/postingrules/*.json` parses via `PostingRulesFileParsingStrategy` into `PostingModuleRequest` payloads. Keys follow the pattern `module-coa-scope`, ensuring the module and chart are paired.

## Planning
- The plan stage extracts the module code and chart, then compares the payload to the existing Cosmos document of type `POSTING_RULE`. Differences produce the matching `PlanItem`.

## Application
- `PostingModulePlanItemApplier` delegates to `StateFileService` to persist the module request along with the referenced chart code, keeping the aggregate document in sync for future runs.

## References
- Refer to `spec/plan-apply/all-categories.md#posting-rules` for the specific flow and diagram.  
- Knowledge-share decks should link to this doc when covering how posting rules tie into both budgeting and GL categorizations.
