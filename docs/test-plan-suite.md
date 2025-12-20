# Test Plan & Suite

This project follows a dual-pronged testing strategy: an automated integration plan/apply workflow that exercises Cosmos state updates end to end, and a fast unit-level suite covering parsing, ordering, state persistence, and applier wiring.

## Integration test plan

1. **Prerequisites**  
   - Run the Azure Cosmos emulator (`https://localhost:8081`) and trust its certificate for the JVM (`JAVA_HOME/lib/security/cacerts` or a custom trust store).  
   - Ensure `CHANGED_FILES` is empty before starting; the helper script overwrites it reliably.  
   - Confirm the emulator contains the `transaction_types_config` container used by `StateFileService`.

2. **Scripted workflow**  
   Use `integrated-test-plan/run-integration-tests.ps1`. It performs:
   - A Maven build (`mvn -DskipTests clean package`), capturing output under `integrated-test-plan/logs/mvn.log`.
   - A create → update → delete sequence across `sides`, `transactions`, `coa`, `gla`, `postingrules`, `glprofile`, `derivedportfolios`, `portfoliogroups`, `aborconfigs`, and `abor`. Each stage sets `CHANGED_FILES`, runs `--plan`, then `--apply` (the delete stage temporarily hides the files before applying).
   - Item mutation via inline PowerShell scripts to force updates for categories that already exist.
   - Log files for each stage (`plan-create.log`, `apply-create.log`, etc.).

3. **Verification checklist**  
- Review `plan/masterplan.json` after each stage to confirm actions/ordering.  
- Optionally confirm `plan/masterplan.html` when `MASTER_PLAN_REPORT_ENABLED` is left on (default) to visualize counts and diffs.  
- Inspect emulator documents in `transaction_types_config` to ensure the persisted JSON aligns with the latest run.  
   - Rerun the script to ensure idempotency (zero plan items for unchanged payloads).  
   - Add or adjust sample files in `changedfiles/<scope>/<category>/` when reinforcing the coverage for new features.

4. **When to update the plan**  
   - Extend the script when a new category (e.g., `glprofile`) is introduced. Update `$categorySamples` and `Modify-SampleFiles` to reflect the new payloads and desired update behavior.

## Unit & CI test suite

1. **Coverage areas** (see `docs/testing-overview.md` for details)  
   - Services: `PlanServiceCoreTest`, `PlanServiceSpecialCategoriesTest`  
   - Ordering: `PlanOrderingRuleEngineTest`  
   - State persistence: `StateFileServiceTest`, `CosmosStateRepositoryTest`  
   - Models & parsing: `ModelFilesEqualityTest`, `PostingRulesFileParsingStrategyTest`, `GeneralLedgerProfileFileParsingStrategyTest`, etc.  
   - Runners & appliers: `PlanRunnerTest`, `ApplyRunnerTest`, `PlanItemAppliersTest`

2. **Running the suite**  
   - Use Maven: `mvn -q test`.  
   - CI currently references `.github/workflows/build.yml`, which runs `mvn -q test` followed by Qodana analysis.  
   - Unit tests spawn Spring components as needed and rely on `@TempDir` for filesystem fixtures.

3. **Adding new tests**  
   - Mirror new categories in the parsing strategy tests and `StateFileServiceTest`.  
   - Add plan-ordering expectations if new rules are added to `application.properties`.  
   - Confirm `PlanItemAppliersTest` includes the new `PlanItemApplier`.

## Reporting & documentation

- Keep stage logs in `integrated-test-plan/logs/` for knowledge sharing and debugging.  
- Update `docs/test-plan-suite.md` when you introduce new automation, test tools, or coverage gaps.  
- Link the integration plan/test suite content in `README.md` so contributors know where to find the workflow.
