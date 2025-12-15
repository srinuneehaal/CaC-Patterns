# Category: CHART_OF_ACCOUNTS

## Directory & model
- Chart of accounts files live under `changedfiles/<scope>/coa/{chartCode}-{scope}.json`. `ChartOfAccountsFileParsingStrategy` reads them into `ChartOfAccountsFile` models, returning `ChartOfAccountsRequest` payloads with the chart code and scope.

## Planning
- `PlanService` compares the parsed request against the persisted Cosmos document for that chart (`cosmos://CHART_OF_ACCOUNTS/<scope>/<chartCode>`). Any difference triggers a `PlanItem` describing a create/update/delete.

## Application
- `ChartOfAccountsPlanItemApplier` (via `StateFileService.persistListDocument`/`applyStateDocument`) writes the snapshot to Cosmos so the next plan run can detect deletions or further edits.

## References
- The flowchart in `spec/plan-apply/all-categories.md#chart-of-accounts` shows how diff detection transitions into the state persistence step.  
- Link to `diagrams/system_design.png` when you need a high-level artifact showing this category among other feeds.
