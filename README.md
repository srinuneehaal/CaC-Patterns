# CaC-Ex Plan Generator

Spring Boot utility that reads JSON files listed in `CHANGED_FILES`, 
maps them to LUSID SDK models, compares them with statefiles, 
and writes a master plan describing new, updated, or deleted objects.

## Requirements
- Java 21
- Maven 3.9+

## Running
From the project root:

```powershell
# Build a plan from CHANGED_FILES
$env:CHANGED_FILES="changedfiles\\ATG\\coa\\USG-ATG.json changedfiles\\ATG\\sides\\side1-ATG.json changedfiles\\ATG\\transactions\\Buy-default-ATG.json changedfiles\\ATG\\aborconfigs\\aborconfig-ATG.json changedfiles\\ATG\\abor\\abor-ATG.json"
mvn -q -DskipTests spring-boot:run -- --plan

# Apply an existing plan from plan/masterplan.json
mvn -q -DskipTests spring-boot:run -- --apply
```

Supported inputs live under `changedfiles/<scope>/` and include transactions, sides, derived portfolios, portfolio groups, chart of accounts (in `coa/`), general ledger accounts (in `gla/`), posting rules, ABOR configurations (in `aborconfigs/`), and ABORs (in `abor/`). State files are read from the matching structure under `statefiles/`.

Find the generated plan at `plan/masterplan.json`. Apply mode will read that file and dispatch each item to the relevant service (sides, transaction types, derived portfolios, portfolio groups, chart of accounts, accounts, posting rules, ABOR configurations, or ABORs).


CHANGED_FILES=changedfiles/ATG/sides/side1-ATG.json changedfiles/ATG/transactions/Buy1-default-ATG.json changedfiles/DLD/sides/side1-DLD.json changedfiles/ATG/transactions/Buy1-FMS-ATG.json changedfiles/ATG/postingrules/USG-USG-ATG.json changedfiles/ATG/coa/USG-ATG-1.json
CHANGED_FILES=changedfiles/ATG/gla/USG-ATG.json

## Plan ordering
The master plan is sorted by a configurable rule engine. Edit `src/main/resources/application.properties` to change the category/action order (for example the default sequence is sides create/update, transactions delete/update/create sorted by `transactionSequence`, side deletes, chart of accounts, accounts, posting rules, ABOR configuration create/update/delete, ABOR create/update/delete, portfolio create/update, portfolio groups, and portfolio deletes). Update the `plan.ordering.rules[*]` entries to suit your rollout constraints without touching code.

## Build
```sh
mvn -q -DskipTests package
```
