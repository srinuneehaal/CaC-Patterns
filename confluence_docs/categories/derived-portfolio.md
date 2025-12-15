# Category: DERIVED_PORTFOLIO

## Directory & model
- Files under `changedfiles/<scope>/derivedportfolios/*.json` feed `DerivedPortfolioFileParsingStrategy`, producing lists of `CreateDerivedTransactionPortfolioRequest` entries.

## Planning
- The plan step iterates the list, comparing each code to persisted Cosmos entries. Missing portfolios translate to delete actions, while new or changed requests emit `NEW`/`UPDATE` plan items.

## Application
- Apply rewrites the aggregate `DerivedPortfolioFile` snapshot via `StateFileService.persistListDocument`, keeping the list of requests per scope in sync for the next plan run.

## References
- `spec/plan-apply/all-categories.md#derived-portfolios-&-portfolio-groups` highlights the shared list-based flow.  
- When presenting categories, include this page alongside `portfolio-group.md` to emphasize commonalities in list diffing.
