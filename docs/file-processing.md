# File Processing Catalogue

This document describes how the project interprets each directory under `changedfiles`, which parsing strategy handles it, and what model ultimately appears in a `PlanItem`. The provided catalog has been updated alongside the new sides plan/apply documentation and integration script so you can trace each directory through plan + apply without jumping between files.

| Directory | Strategy class | Category | Model / payload | Notes |
| --- | --- | --- | --- | --- |
| `sides` | `SideFileParsingStrategy` | `FileCategory.SIDE` | `SideFile` (maps `sideDefinitionRequest` to `SideDefinitionRequest`) | Key derived from `side` + scope; ensures definition is present. |
| `transactions` | `TransactionFileParsingStrategy` | `FileCategory.TRANSACTION` | `TransactionFile` (includes `transactionTypeRequest`) | Orders by `transactionSequence` when rules request it. |
| `coa` | `ChartOfAccountsFileParsingStrategy` | `FileCategory.CHART_OF_ACCOUNTS` | `ChartOfAccountsRequest` | Wrapped in `ChartOfAccountsFile` with scope/chart code. |
| `gla` | `AccountFileParsingStrategy` | `FileCategory.ACCOUNT` | LUSID `Account` entries (list stored as `glAccounts`) | File name format `COACODE-key.json`; creates deletes when accounts vanish. |
| `postingrules` | `PostingRulesFileParsingStrategy` | `FileCategory.POSTING_RULE` | `PostingModuleRequest` | Keys include `module-coa-scope`; persisted state lives in Cosmos documents keyed in the same way. |
| `glprofile` | `GeneralLedgerProfileFileParsingStrategy` | `FileCategory.GENERAL_LEDGER_PROFILE` | `GeneralLedgerProfileRequest` | Keys follow `profile-coa-scope` (falls back to filename); processing and state handling added recently. |
| `derivedportfolios` | `DerivedPortfolioFileParsingStrategy` | `FileCategory.DERIVED_PORTFOLIO` | `CreateDerivedTransactionPortfolioRequest` list | `PlanService` splits entries and emits deletes for missing codes. |
| `portfoliogroups` | `PortfolioGroupFileParsingStrategy` | `FileCategory.PORTFOLIO_GROUP` | `CreatePortfolioGroupRequest` list | Similar to derived portfolios. |
| `aborconfigs` | `AborConfigurationFileParsingStrategy` | `FileCategory.ABOR_CONFIGURATION` | `AborConfigurationRequest` list | `StateFileService` writes aggregated list files per scope. |
| `abor` | `AborFileParsingStrategy` | `FileCategory.ABOR` | `AborRequest` list | Handles insert/delete of individual ABOR records while scanning existing state. |

## How processing ties into the plan

1. `PlanService` takes each normalized path and finds the first `FileParsingStrategy` whose `supports(Path)` returns true.  
2. The strategy creates a `LoadedFile` containing `FileCategory`, key, path, and payload as listed above.  
3. For simple files, `PlanService` compares payloads to the persisted Cosmos state documents to produce `NEW / UPDATE / DELETE` `PlanItem`s.  
4. For list-type files (`AccountFile`, `GeneralLedgerProfileFile` is treated as single payload but state maintained similarly), `PlanService` may split entries into multiple items and scans the Cosmos container for missing keys to emit deletes.  

## Reusing models outside parsing

- `PlanReader` rehydrates `payload` JSON from the master plan into the same LUSID model types (`Account`, `GeneralLedgerProfileRequest`, `PostingModuleRequest`, etc.).  
- `StateFileService` writes Cosmos documents representing `AborConfigurationFile`, `DerivedPortfolioFile`, etc., so the next plan run can compare against the persisted payload snapshots.  
- `PlanItemApplier` implementations (e.g., `GeneralLedgerProfilePlanItemApplier`) expect these models as payloads to call the mock API services.
