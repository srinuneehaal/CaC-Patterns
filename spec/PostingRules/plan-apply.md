# Plan & Apply: Posting Rules

## Plan responsibilities
- Directory: `changedfiles/<scope>/postingrules`.
- Strategy: `PostingRulesFileParsingStrategy` constructs `PostingRulesFile` objects containing `PostingModuleRequest`s keyed as `module-coa-scope`.
- The plan stage compares each module request to the Cosmos `POSTING_RULE` document and emits `PlanItem`s according to the diff.

## Apply responsibilities
- `PostingModulePlanItemApplier` and `PostingModuleApiService` persist modules through `StateFileService`, keeping the stored payload in sync for deletes and updates.

## Diagrams
- See `spec/plan-apply/all-categories.md#posting-rules` for the graph showing parser ƒ+' Cosmos lookup ƒ+' plan item ƒ+' persistence.
- Use `diagrams/system_design LLD.mmd` when explaining how posting rules tie into the broader apply infrastructure.
- Sketch the detailed stages via `spec/PostingRules/postingrules-plan.mmd` and `spec/PostingRules/postingrules-apply.mmd`.
