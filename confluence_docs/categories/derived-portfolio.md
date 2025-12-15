# Category: DERIVED_PORTFOLIO

## Directory & model
- Files under `changedfiles/<scope>/derivedportfolios/*.json` feed `DerivedPortfolioFileParsingStrategy`, producing lists of `CreateDerivedTransactionPortfolioRequest` entries.

## Planning
- The plan step iterates the list, comparing each code to persisted Cosmos entries. Missing portfolios translate to delete actions, while new or changed requests emit `NEW`/`UPDATE` plan items.

## Application
- Apply rewrites the aggregate `DerivedPortfolioFile` snapshot via `StateFileService.persistListDocument`, keeping the list of requests per scope in sync for the next plan run.

## Strategy classes
- Planner: `DerivedPortfolioFileParsingStrategy`.  
- Apply: `DerivedPortfolioPlanItemApplier` via `StateFileService.persistListDocument`.

## Flow diagram
- `spec/plan-apply/all-categories.md#derived-portfolios-&-portfolio-groups` shows the shared flow that splits list entries and emits deletes for missing codes.

## Sequence diagram
- Use `diagrams/sequence_1.mmd` while explaining how derived portfolios’ list diffs map into apply-phase snapshots.
