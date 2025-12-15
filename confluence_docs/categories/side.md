# Category: SIDE

## Directory & model
- Source files live under `changedfiles/<scope>/sides/{sideCode}-{scope}.json` and parse via `SideFileParsingStrategy` into `SideFile` payloads that encapsulate `SideDefinitionRequest`.

## Planning
- `PlanService` compares each parsed `SideFile` against the Cosmos `StateDocument` identified by `cosmos://SIDE/<scope>/<key>`. It emits `NEW`, `UPDATE`, or `DELETE` `PlanItem`s depending on whether a matching snapshot exists or the file disappears.

## Application
- `SidePlanItemApplier` hands off the action to `StateFileService.applySide`, which writes or removes the `StateDocument` so the next plan run sees an accurate baseline.

## Diagrams and references
- `diagrams/sequence_1.mmd` and `diagrams/system_design LLD.mmd` highlight how `SidePlanItemApplier` fits into the apply chain.  
- Reference `spec/plan-apply/all-categories.md#side-files` for the Mermaid flowchart specific to sides.
