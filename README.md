# CaC-Ex Plan Generator

Spring Boot utility that reads JSON files listed in `CHANGEDFILES`, 
maps them to LUSID SDK models, compares them with statefiles, 
and writes a master plan describing new, updated, or deleted objects.

## Requirements
- Java 21
- Maven 3.9+

## Running
From the project root:

```powershell
# Build a plan from CHANGED_FILES
$env:CHANGED_FILES="changedfiles\\ATG\\sides\\side1-ATG.json changedfiles\\ATG\\transactions\\Buy-default-ATG.json"
mvn -q -DskipTests spring-boot:run -- --plan

# Apply an existing plan from plan/masterplan.json
mvn -q -DskipTests spring-boot:run -- --apply
```

Find the generated plan at `plan/masterplan.json`. Apply mode will read that file and dispatch each item to the relevant service (sides, transaction types, derived portfolios, or portfolio groups).

## Build
```sh
mvn -q -DskipTests package
```
