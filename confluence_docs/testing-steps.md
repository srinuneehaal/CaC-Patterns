# Testing Steps

This page captures the key validation paths for the plan/apply automation so it can be shared on Confluence.

## Unit & integration checklist
1. **Unit tests**  
   - `mvn -q -DskipITs test` executes the fast unit suites that cover parsing strategies, plan ordering, state reconciliation, and the mocked `PlanItemActionService`s.  
   - Consult `docs/testing-overview.md` for the current suite breakdown and `docs/test-plan-suite.md` for CI-specific scripts.
2. **Integration script**  
   - Use `integrated-test-plan/run-integration-tests.ps1` to iterate creates/updates/deletes for each category. It auto-updates a representative file, runs `--plan`, follows with `--apply`, and captures logs under `integrated-test-plan/logs/`.  
   - Follow the pre-reqs in `docs/integration test plan` (Cosmos emulator, certificates, valid `CHANGED_FILES`) before hopping into the script.
3. **Smoke validation**  
   - Run `mvn -q -DskipTests spring-boot:run -- --plan` to generate `plan/masterplan.json` and inspect `plan/masterplan.html` for KPI tiles, chart distributions, and interactive diff rows.  
   - After a `--plan` pass, run `mvn -q -DskipTests spring-boot:run -- --apply` to verify the mocked appliers log each item and that `StateFileService` updates the Cosmos documents.

## Troubleshooting & evidence
- Store integration logs (`integrated-test-plan/logs/`) alongside the test step entry in Confluence so reviewers can replay the exact sequence and grep for any “booms.”  
- Reference `docs/knowledge-share.md` for diagrams/screenshots when explaining why the plan/apply cycle is resilient to exceptions (items fail independently, logs capture each failure).  
- When adding categories, update this page with the new happy-path validation (create/update/delete flows) plus any additional checks (e.g., `GeneralLedgerProfile` might require verifying the normalized request).
