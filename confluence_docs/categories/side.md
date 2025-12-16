# Category: SIDE

## Directory & model
- Source files live under `changedfiles/<scope>/sides/{sideCode}-{scope}.json` and parse via `SideFileParsingStrategy` into `SideFile` payloads that encapsulate `SideDefinitionRequest`.

## Planning
- `PlanService` compares each parsed `SideFile` against the Cosmos `StateDocument` identified by `cosmos://SIDE/<scope>/<key>`. It emits `NEW`, `UPDATE`, or `DELETE` `PlanItem`s depending on whether a matching snapshot exists or the file disappears.

## Application
- `SidePlanItemApplier` hands off the action to `StateFileService.applySide`, which writes or removes the `StateDocument` so the next plan run sees an accurate baseline.

## Strategy classes
- Planner: `SideFileParsingStrategy` (registered via `FileParsingStrategyFactory`).  
- Apply: `SidePlanItemApplier` + `PlanItemActionService` beans that call `StateFileService.applySide`.

## Flow diagram
- See the `flowchart TD` in `spec/plan-apply/all-categories.md#side-files` for how the parser → state lookup → diff steps produce `PlanItem`s and how `StateFileService.applySide` persists the COSMOS document.
- Show the same steps in `spec/Sides/sides-plan.mmd` and `spec/Sides/sides-apply.mmd` to tie the narrative to the specific Mermaid files.

## Sequence diagram
- Reference `diagrams/sequence_1.mmd` and `diagrams/system_design LLD.mmd` for the ordered interactions from `PlanRunner` reading `CHANGED_FILES` through `PlanApplyService` applying each `PlanItem`.
