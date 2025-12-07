# Automated Integration Test Plan

The repository now ships a reusable script that runs the full `--plan` + `--apply` loop for every supported file category. Everything lives under `integrated-test-plan/` so you can rerun the integration suite as often as needed and save the artifacts for review.

## What it does

1. Builds the application jar via `mvn -DskipTests clean package`.
2. Detects one sample JSON file for each major category beneath `changedfiles/<scope>/`.
3. Sets the `CHANGED_FILES` environment variable so `PlanRunner` sees the right inputs.
4. Executes `java -jar … --plan` and `java -jar … --apply`, streaming output to `integrated-test-plan/logs/{plan,apply}.log`.
5. Leaves `plan/masterplan.json` in place for manual inspection.

## How to run

```powershell
.\integrated-test-plan\run-integration-tests.ps1
```

The script is idempotent and rewrites the log files each time it runs. If you need a different subset of categories, edit the `$categories` array near the top of the script.

## What to check after the run

- The logs in `integrated-test-plan/logs/` show the outcome of plan generation and plan application.
- `plan/masterplan.json` should list a plan item for every category that had a changed file.
- Cosmos documents in `adaptor.transaction_types_config` should reflect the applied state (use the emulator Data Explorer).
