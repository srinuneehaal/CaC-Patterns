# Category: PORTFOLIO_GROUP

## Directory & model
- `changedfiles/<scope>/portfoliogroups/*.json` parse via `PortfolioGroupFileParsingStrategy`, producing lists of `CreatePortfolioGroupRequest` entries.

## Planning
- `PlanService` compares each group code to the stored Cosmos snapshot; deleted groups emit `DELETE` plan items, while new or changed groups emit `NEW`/`UPDATE`.

## Application
- The apply stage rewrites the snapshot via `StateFileService`, persisting the full `PortfolioGroupFile` list per scope so the next plan run sees the latest set.

## References
- The shared list handling section in `spec/plan-apply/all-categories.md#derived-portfolios-&-portfolio-groups` also applies here.  
- Use this doc when explaining how derived portfolios and portfolio groups share state rehydration logic.
