# State & Plan Lifecycle

This document walks through the lifecycle of a plan from changed files to applied state updates, highlighting the key services and files involved in `PlanService`, `PlanApplyService`, and `StateFileService`.

## 1. Change detection

- `PlanRunner` is triggered when the application receives the `--plan` flag.
- It delegates to `ChangedFilesProvider`, which reads the `CHANGED_FILES` environment variable and returns normalized `Path`s for each file.
- The runner passes these paths to `PlanService.buildPlan(...)`.

## 2. Building the master plan

1. **Category discovery** – `PlanService` uses `FileParsingStrategyFactory` to find the first `FileParsingStrategy` whose `supports(path)` returns true; each strategy corresponds to a directory like `sides`, `coa`, `glprofile`.
2. **Payload parsing** – The strategy uses `JsonModelMapper` to deserialize JSON into a domain payload (`SideFile`, `AccountFile`, `GeneralLedgerProfileRequest`, etc.).
3. **PlanItem creation** – The new payload is compared to the existing state (if any) via `StateFileService` to determine whether to emit `PlanItem`s with `Action.NEW`, `.UPDATE`, or `.DELETE`.
4. **Ordering** – `PlanOrderingRuleEngine` sorts the collected `PlanItem`s using configured rules (including action priority and `transactionSequence` sorting when enabled). It falls back to defaults when no rules are provided.
5. **Persistence** – `PlanWriter` stores the ordered `MasterPlan` in `plan/masterplan.json`. `PlanReader` reads this file again during apply.

## 3. Applying the master plan

1. `ApplyRunner` (triggered by `--apply`) begins by reading the master plan (`PlanReader.read()`), converting the JSON payload for each `PlanItem` into the expected LUSID model (located in `PlanReader.convertPayload()`).
2. `PlanApplyService` iterates through the `PlanItem`s:
   - Resolves a `PlanItemApplier` based on the `FileCategory`. Each applier extends `AbstractPlanItemApplier`, which throws if the payload has the wrong type.
   - Delegates `create/update/delete` calls to the corresponding `PlanItemActionService` (`ChartOfAccountsApiService`, `GeneralLedgerProfileApiService`, etc.), which currently log the operation but represent where actual SDK calls would go.
   - After each successful action, calls `StateFileService.applyStateChange(item)` to keep state files in sync.
3. Exceptions from each item are caught individually; the service logs and continues so one failing plan item does not stop the entire run.

## 4. State file maintenance

- `StateFileService` tracks JSON snapshots under `statefiles/<scope>/<dir>`. The directory names are configured in `FileLocationProperties`.
- For simple resources (`ChartOfAccounts`, `PostingRule`, `GeneralLedgerProfile`), the service writes a single JSON file containing scope, key, and the request payload.
- For list resources (`Abor`, `DerivedPortfolio`, `PortfolioGroup`, `Account`), it maintains aggregate files that contain the entire list; CRUD operations manipulate the list content (adding/removing entries) before writing back.
- The service also performs delete scanning for list-based files. When building a plan, `PlanService.addDeletesForMissingChanges` walks the state directory (guided by `DeleteScanSpec`) and emits `DELETE` items for entries no longer present in changed files.

## 5. Tips for debugging

- Inspect `plan/masterplan.json` after a `--plan` run to understand which actions will fire.
- Check `statefiles/...` to verify that `StateFileService` wrote the expected JSON (new files for new resources, deletes for missing entries).
- Logs from `PlanApplyService` and the API services detail each action taken; any errors include the plan item key.

By keeping these components aligned, the project ensures each changed JSON file flows through parsing, diff detection, ordering, application, and state persistence.
