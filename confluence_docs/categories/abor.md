# Category: ABOR

## Directory & model
- `changedfiles/<scope>/abor/*.json` contain `AborRequest` lists parsed via `AborFileParsingStrategy`. Each request carries a code that becomes the key.

## Planning
- The parser splits the list into individual entries and compares each code against the Cosmos state documents (type `ABOR`). Missing entries generate deletes; new entries produce `NEW` plan items.

## Application
- `StateFileService` rebuilds the `AborFile` aggregate document after each apply action, so the persisted list mirrors the latest config.

## Strategy classes
- Planner: `AborFileParsingStrategy`.  
- Apply: `AborPlanItemApplier` partnered with `StateFileService`.

## Flow diagram
- The diff explanation under `spec/plan-apply/all-categories.md#abors` highlights how each `AborRequest` is compared/per code and how deletes arise when entries vanish.

## Sequence diagram
- Show `diagrams/sequence_1.mmd` as the “per-item” view so reviewers see how ABOR diffs trigger `PlanItem` creation and the apply step rebuilds the aggregated list.
