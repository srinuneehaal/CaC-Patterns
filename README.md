# CaC-Ex Plan Generator

Spring Boot utility that reads JSON files listed in `CHANGED_FILES`, maps them to LUSID SDK models, compares them with persisted Cosmos state documents, and writes a master plan describing new, updated, or deleted objects.

## Requirements
- Java 21
- Maven 3.9+

## Running
From the project root:

```powershell
# Build a plan from CHANGED_FILES
$env:CHANGED_FILES="changedfiles\ATG\coa\USG-ATG.json changedfiles\ATG\sides\side1-ATG.json changedfiles\ATG\transactions\Buy-default-ATG.json changedfiles\ATG\aborconfigs\aborconfig-ATG.json changedfiles\ATG\abor\abor-ATG.json"
mvn -q -DskipTests spring-boot:run -- --plan

# Apply an existing plan from plan/masterplan.json
mvn -q -DskipTests spring-boot:run -- --apply
```

Supported inputs live under `changedfiles/<scope>/` and include transactions, sides, derived portfolios, portfolio groups, chart of accounts (in `coa/`), general ledger accounts (in `gla/`), posting rules, ABOR configurations (in `aborconfigs/`), and ABORs (in `abor/`). State comparisons now read from the Cosmos container `transaction_types_config` (partitioned by `typeOfItem`), whose document ids follow the same `<key>-<scope>` convention.

Find the generated plan at `plan/masterplan.json`. Apply mode will read that file and dispatch each item to the relevant service (sides, transaction types, derived portfolios, portfolio groups, chart of accounts, accounts, posting rules, ABOR configurations, or ABORs).

## Plan ordering
The master plan is sorted by a configurable rule engine. Edit `src/main/resources/application.properties` to change the category/action order (for example the default sequence is sides create/update, transactions delete/update/create sorted by `transactionSequence`, side deletes, chart of accounts, accounts, posting rules, ABOR configuration create/update/delete, ABOR create/update/delete, portfolio create/update, portfolio groups, and portfolio deletes). Update the `plan.ordering.rules[*]` entries to suit your rollout constraints without touching code.

## Template documentation
- `docs/item-workflow.md`: Step-by-step guide for adding any item (sides, transactions, posting rules, GL profiles, ABORs, etc.), setting `CHANGED_FILES`, and executing the plan/apply run.
- `docs/plan-lifecycle.md`, `docs/file-processing.md`, and `docs/architecture.md`: Ride-alongs that explain how change detection, parsing strategies, ordering rules, and apply services interact.
- `docs/knowledge-share.md`: Centralizes diagrams, reference docs, and knowledge-share tips so every teammate can explain the project with visuals.
- `docs/contribution-guide.md`: Onboarding checklist for extending categories, configuration, ordering rules, and tests.

## Testing & validation
- `docs/testing-overview.md` and `docs/test-plan-suite.md`: Coverage maps for the unit and CI suite plus the scripted integration workflow (`integrated-test-plan/run-integration-tests.ps1`).
- `integrated-test-plan/run-integration-tests.ps1`: Automates create/update/delete stages for major categories while capturing logs under `integrated-test-plan/logs/`.
- `docs/integration test plan`: Walks through the prerequisites (Cosmos emulator, certificates), the verification checklist, and how to interpret log output.

## Diagrams & knowledge sharing
- `diagrams/system_design*.mmd`, `diagrams/system_design LLD*.mmd`, and `diagrams/sequence*.mmd` illustrate the plan/apply flow you can use during walkthroughs.
- `diagrams/class.mmd` documents the key domain models (`PlanItem`, `LoadedFile`, appliers) and can be exported as a PNG for quick slides.
- `docs/knowledge-share.md` maps each diagram to the narrative docs so you can run a consistent onboarding session.

## Build
```sh
mvn -q -DskipTests package
```
