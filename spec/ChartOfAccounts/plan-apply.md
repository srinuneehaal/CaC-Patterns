# Plan & Apply: Chart of Accounts

## Plan responsibilities
- Directory: `changedfiles/<scope>/coa`.
- Strategy: `ChartOfAccountsFileParsingStrategy` produces `ChartOfAccountsFile` models that wrap `ChartOfAccountsRequest`.
- Plan flow compares the request to the Cosmos document keyed `cosmos://CHART_OF_ACCOUNTS/<scope>/<chartCode>` and emits `PlanItem`s for create/update/delete scenarios.

## Apply responsibilities
- `ChartOfAccountsPlanItemApplier`, through `ChartOfAccountsApiService`, writes the snapshot using `StateFileService` so that subsequent plan runs have an accurate baseline.

## Diagrams
- Refer to `spec/plan-apply/all-categories.md#chart-of-accounts` for the parser ƒ+' state diff ƒ+' persistence flowchart.  
- Include `diagrams/sequence.mmd` on Confluence to show how chart processing integrates into the overall plan/apply run.
- Visualize the parser and persistence flows via `spec/ChartOfAccounts/chartofaccounts-plan.mmd` and `spec/ChartOfAccounts/chartofaccounts-apply.mmd`.
