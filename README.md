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
# Example: list changed files separated by spaces
$env:CHANGEDFILES="changedfiles\sides\side1.json changedfiles\sides\side2.json changedfiles\transactions\transaction1.json changedfiles\transactions\transaction2.json"
mvn -q -DskipTests spring-boot:run
```

CHANGEDFILES=changedfiles/ATG/sides/side1-ATG.json changedfiles/DLD/derivedportfolios/derivedportfolios-DLD.json changedfiles/ATG/derivedportfolios/derivedportfolios-ATG.json changedfiles/ATG/portfoliogroups/portfoliogroups-ATG.json
$env:CHANGEDFILES=changedfiles/derivedportfolios/derivedportfolios-DLD.json changedfiles\sides\side1.json changedfiles/transactions/transaction3.json
Find the generated plan at `plan/masterplan.json`.

## Build
```sh
mvn -q -DskipTests package
```
