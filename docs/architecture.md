# Architecture Overview

This project converts a set of JSON configuration files into a ordered master plan and then applies that plan through mock LUSID SDK appliers. The critical pieces of the flow are below.

## High-level flow

1. **Discovery**  
   The `ChangedFilesProvider` reads the `CHANGED_FILES` environment variable and normalizes the supplied paths (`changedfiles/...`).  
2. **Plan generation**  
   A `--plan` run launches `PlanRunner`, which feeds those paths into `PlanService`.  
   - `PlanService` identifies each file’s category via `FileParsingStrategyFactory` (one implementation per file type, e.g., `SideFileParsingStrategy`, `GeneralLedgerProfileFileParsingStrategy`).  
   - The `JsonModelMapper` (backed by `JacksonConfiguration`) deserializes each file into a domain model (`SideFile`, `AccountFile`, `GeneralLedgerProfileRequest`, etc.).  
   - Special categories (ABOR, derived portfolios, ABOR configs, portfolio groups, accounts) are reconciled with state files via `StateFileService`, producing `PlanItem`s for creates/updates/deletes.  
   - `PlanOrderingRuleEngine` sorts the collected `PlanItem`s using configured rules (defaulting to the order listed in `application.properties`, including the new `GENERAL_LEDGER_PROFILE` rule).  
   - `PlanWriter` writes the resulting `MasterPlan` to `plan/masterplan.json`.
3. **Plan application**  
   Running `--apply` invokes `PlanApplyService`, which reads the master plan via `PlanReader` and converts each item’s payload (`GeneralLedgerProfileRequest`, `PostingModuleRequest`, etc.).  
   - It resolves the appropriate `PlanItemApplier` (e.g., `GeneralLedgerProfilePlanItemApplier`, `PostingModulePlanItemApplier`) via the `supportedCategory()` map.  
   - Each applier delegates to a `PlanItemActionService` (`GeneralLedgerProfileApiService`, `ChartOfAccountsApiService`, etc.) which currently log the intended LUSID SDK call but form the integration surface.
   - After each successful action, `StateFileService` updates the persisted Cosmos DB document in `transaction_types_config`, ensuring the next plan run can detect deletions/updates.

## Component relationships

```text
ChangedFilesProvider
      |
      v
   PlanRunner --(paths)--> PlanService <---> StateFileService (for comparison, delete scans)
         |                         |
         |                         v
         |                  FileParsingStrategyFactory --> JsonModelMapper
         |                                                   |
         |                                                   v
         |                                            File-specific models
         |                                                   |
         v                                                   v
     PlanWriter <-- MasterPlan <-- PlanOrderingRuleEngine

-- Apply Execution --
PlanApplyService <-- PlanReader <-- MasterPlan
        |
        v
 PlanItemApplier (per category) --> PlanItemActionService
        |
        v
 StateFileService
```

## Key directories & configuration

- `src/main/java/com/example/cacex/service/plan/stratagy`: one strategy per ordered directory (sides, transactions, accounts, postingrules, glprofile, etc.).  
- `src/main/java/com/example/cacex/model`: wrappers for each file type (`SideFile`, `AccountFile`, `GeneralLedgerProfileFile`), the `PlanItem`, `MasterPlan`, etc.  
- `src/main/java/com/example/cacex/service/apply`: appliers + API services that perform the `create / update / delete` actions.  
- `src/main/resources/application.properties`, `FileLocationProperties`, and the Azure Cosmos properties: define root directories (`changedfiles`), the Cosmos connection (`azure.cosmos.*`), and plan ordering rules (`plan.ordering.rules[...]`).

## Extending the architecture

To add a new file category:

1. Add a `FileCategory` enum entry and directory name in `FileLocationProperties`.  
2. Create a `FileParsingStrategy` that loads the JSON into a new model or existing LUSID `Request`.  
3. Update `PlanService` if the category needs special delete scanning (e.g., list-based files).  
4. Provide a `PlanOrderingRule` to ensure the new category runs in the desired sequence.  
5. Implement a `PlanItemApplier`/`PlanItemActionService` pair to handle SDK calls.  
6. Add model tests and ordering/property coverage to the test suite (`Model*Test`, `PlanOrderingRuleEngineTest`, etc.).
