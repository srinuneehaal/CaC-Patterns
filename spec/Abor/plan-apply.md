# Plan & Apply: ABOR

## Plan responsibilities
- Directory: `changedfiles/<scope>/abor`.
- Strategy: `AborFileParsingStrategy` splits `AborRequest` lists into individual codes.
- The plan stage compares each code to its Cosmos document, emitting `NEW`/`UPDATE` when differences appear and `DELETE` when entries vanish.

## Apply responsibilities
- `AborPlanItemApplier` partners with `StateFileService` to rebuild the aggregated `AborFile` snapshot after each apply action.

## Diagrams
- `spec/plan-apply/all-categories.md#abors` describes the per-entry diff approach.  
- Use `diagrams/sequence_1.mmd` to illustrate how each ABOR entry flows through the plan/apply cycle.
- Render the specific stages via `spec/Abor/abor-plan.mmd` and `spec/Abor/abor-apply.mmd` when narrating this category.
