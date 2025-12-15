# Plan & Apply: Derived Portfolios

## Plan responsibilities
- Directory: `changedfiles/<scope>/derivedportfolios`.
- Strategy: `DerivedPortfolioFileParsingStrategy` produces `DerivedPortfolioFile` lists of `CreateDerivedTransactionPortfolioRequest`.
- Plan service compares each entry with Cosmos state and emits `NEW`/`UPDATE` for matches or `DELETE` if codes disappear.

## Apply responsibilities
- `DerivedPortfolioPlanItemApplier` uses `StateFileService.persistListDocument` to rewrite the aggregated list per scope so the next plan run can diff against the latest set.

## Diagrams
- See `spec/plan-apply/all-categories.md#derived-portfolios-&-portfolio-groups` for the shared list-based flow.
- Reference `diagrams/sequence_1.mmd` when covering how these flows tie into the wider plan/apply process.
- Refer to `spec/DerivedPortfolios/derivedportfolios-plan.mmd` and `spec/DerivedPortfolios/derivedportfolios-apply.mmd` for the specific plan/apply stages.
