# Item Addition & Apply Workflow

This repository uses structured JSON payloads to build a master plan and then apply it to the configured state. The steps below show how to add or update any item (side, transaction, posting rule, GL profile, ABOR, etc.) and then run the plan + apply cycle so the persisted state reflects the change.

## 1. Pick the directory & payload model

1. Locate the directory under `changedfiles/<scope>/` that matches the kind of item you want to touch. See `docs/file-processing.md` for the current mapping between directories, parsing strategies, and `FileCategory` values.
2. Duplicate one of the sample JSON files in that directory; update the `scope`, `key`, `description`, `entries`, or any other field required by that model. Files are ingested by `ChangedFilesProvider` and routed via the matching `FileParsingStrategy` when `PlanService` builds the plan (`docs/plan-lifecycle.md` and `docs/architecture.md` describe this flow).
3. Commit the file (or keep as a working change) and record the relative path for the next step.

## 2. Assemble `CHANGED_FILES`

1. Set the environment variable to a space-separated list of the paths you want included. For example:

   ```powershell
   $env:CHANGED_FILES = 'changedfiles/ATG/sides/side2-ATG.json changedfiles/ATG/transactions/Buy-default-ATG.json'
   ```

2. You can also use the helper script `integrated-test-plan/run-integration-tests.ps1` to build the batch automatically; it selects one file per major category, mutates descriptions to ensure updates, and reruns the plan/apply stages (see `docs/integration test plan` for details).

## 3. Generate the master plan

1. From the repository root, run the plan stage with Maven:

   ```powershell
   mvn -q -DskipTests spring-boot:run -- --plan
   ```

2. Inspect `plan/masterplan.json` (and the `plan.log` file if present) to ensure each `PlanItem` maps to the intended category/key and carries the right `Action` (`NEW`, `UPDATE`, `DELETE`). The ordering obeys `plan.ordering.rules[*]` in `src/main/resources/application.properties`; adjust the property file if you need a different sequence before rerunning the plan.

3. Use the `PlanWriter` output to confirm that the payload references the JSON model you edited (e.g., `GeneralLedgerProfileRequest`, `PostingModuleRequest`).

## 4. Apply the plan

1. Once the master plan looks correct, run:

   ```powershell
   mvn -q -DskipTests spring-boot:run -- --apply
   ```

2. `PlanApplyService` reads `plan/masterplan.json`, resolves the right `PlanItemApplier`, and executes the mocked SDK actions found in each `PlanItemActionService`. Logs describe the action and category; any exceptions are logged per item so the run continues for the remaining entries.
3. After each action, `StateFileService` updates the Cosmos-style documents stored under `transaction_types_config` (managed via the emulator). Verify the persisted JSON in the emulator Data Explorer to ensure the applied payload matches the plan.

## 5. Confirm & iterate

- Re-run `--plan` to ensure no unexpected deletes appear once the new state is persisted.
- Use `docs/plan-lifecycle.md` to trace how `PlanService`, `PlanOrderingRuleEngine`, `PlanWriter`, and `PlanApplyService` interact when you rerun the cycle.
- If you break or add a new directory/category, update `FileCategory`, `FileLocationProperties`, the parsing strategy, ordering rules, and applier/test coverage following the guidance in `docs/contribution-guide.md`.

## Bonus: Batch scenarios

- The integration script in `integrated-test-plan/run-integration-tests.ps1` covers create → update → delete stages for each category, ensuring the workflow is exercised end to end. Consult `docs/integration test plan` for prerequisites (Cosmos emulator, certificates) and the result checklist.
- Capture logs from the script under `integrated-test-plan/logs/` for troubleshooting or knowledge sharing.
