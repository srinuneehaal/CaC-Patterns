# Category: TRANSACTION

## Directory & model
- Files under `changedfiles/<scope>/transactions/*.json` are parsed by `TransactionFileParsingStrategy` into `TransactionFile` payloads containing `TransactionTypeRequest` details, including `transactionSequence`.

## Planning
- `PlanService` derives the key from the transaction code and compares the incoming payload to the Cosmos snapshot (`cosmos://TRANSACTION/<scope>/<key>`). The plan engine factors in `transactionSequence` when the ordering rules require it, so transactions respect the configured sequence.

## Application
- `TransactionPlanItemApplier` persists the entire `TransactionFile` snapshot via `StateFileService`, updating Cosmos `transaction` documents and emitting delete events when entries vanish.

## Strategy classes
- Planner: `TransactionFileParsingStrategy`.  
- Apply: `TransactionPlanItemApplier` + `TransactionTypeApiService` (logs payloads) via `PlanItemActionService`.

## Flow diagram
- The sequence diagram in `spec/plan-apply/all-categories.md#transactions` illustrates parser → Cosmos lookup → `PlanItem` emission → `StateFileService` persistence.

## Sequence diagram
- Use `diagrams/sequence.mmd`/`diagrams/system_design.mmd` to show the full plan/apply run, noting how `transactionSequence` ordering affects the plan ordering phase.
