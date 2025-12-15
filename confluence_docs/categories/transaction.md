# Category: TRANSACTION

## Directory & model
- Files under `changedfiles/<scope>/transactions/*.json` are parsed by `TransactionFileParsingStrategy` into `TransactionFile` payloads containing `TransactionTypeRequest` details, including `transactionSequence`.

## Planning
- `PlanService` derives the key from the transaction code and compares the incoming payload to the Cosmos snapshot (`cosmos://TRANSACTION/<scope>/<key>`). The plan engine factors in `transactionSequence` when the ordering rules require it, so transactions respect the configured sequence.

## Application
- `TransactionPlanItemApplier` persists the entire `TransactionFile` snapshot via `StateFileService`, updating Cosmos `transaction` documents and emitting delete events when entries vanish.

## Diagrams & references
- `spec/plan-apply/all-categories.md#transactions` contains the sequence diagram showing parser → state lookup → apply chain.  
- Pair that with `diagrams/sequence.mmd` or `diagrams/system_design.mmd` whenever you explain how transactions flow through plan and apply in knowledge share sessions.
