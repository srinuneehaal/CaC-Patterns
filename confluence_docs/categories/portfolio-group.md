# Category: PORTFOLIO_GROUP

## Directory & model
- `changedfiles/<scope>/portfoliogroups/*.json` parse via `PortfolioGroupFileParsingStrategy`, producing lists of `CreatePortfolioGroupRequest` entries.

## Planning
- `PlanService` compares each group code to the stored Cosmos snapshot; deleted groups emit `DELETE` plan items, while new or changed groups emit `NEW`/`UPDATE`.

## Application
- The apply stage rewrites the snapshot via `StateFileService`, persisting the full `PortfolioGroupFile` list per scope so the next plan run sees the latest set.

## Strategy classes
- Planner: `PortfolioGroupFileParsingStrategy`.  
- Apply: `PortfolioGroupPlanItemApplier` + `StateFileService.persistListDocument`.

## Flow diagram
- Refer to `spec/plan-apply/all-categories.md#derived-portfolios-&-portfolio-groups` for the combined list-flow diagram: parser -> diff -> plan -> aggregate persistence.

## Sequence diagram
- Pair this page with `diagrams/sequence_1.mmd` to show how portfolio groups are part of the final apply phase that rewrites list snapshots.
