# Plan & Apply: Portfolio Groups

## Plan responsibilities
- Directory: `changedfiles/<scope>/portfoliogroups`.
- Strategy: `PortfolioGroupFileParsingStrategy` parses `CreatePortfolioGroupRequest` lists.
- Plan service compares the list to Cosmos state, emitting `PlanItem`s for entries created, updated, or deleted.

## Apply responsibilities
- `PortfolioGroupPlanItemApplier` writes the aggregated snapshot via `StateFileService.persistListDocument`, keeping the list of portfolio groups current.

## Diagrams
- `spec/plan-apply/all-categories.md#derived-portfolios-&-portfolio-groups` includes the shared flow diagram.  
- Use `diagrams/sequence_1.mmd` when explaining how both derived portfolios and portfolio groups get processed in the apply runner.
- Refer to `spec/PortfolioGroups/portfoliogroups-plan.mmd` and `spec/PortfolioGroups/portfoliogroups-apply.mmd` for the list-specific plan/apply steps.
