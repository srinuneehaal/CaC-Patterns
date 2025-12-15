# Plan & Apply: Transactions

## Plan responsibilities
- Directory: `changedfiles/<scope>/transactions`.
- Strategy: `TransactionFileParsingStrategy` converts JSON files into `TransactionFile` objects (including `TransactionTypeRequest` and `transactionSequence`).
- Plan flow: compare parsed payload against Cosmos snapshot (`cosmos://TRANSACTION/<scope>/<key>`), emit `NEW`/`UPDATE` when payload differs, emit `DELETE` when the source file disappears.

## Apply responsibilities
- `TransactionPlanItemApplier` resolves via `PlanItemApplierRegistry`, delegates to `StateFileService` through `TransactionTypeApiService`, and writes the entire `TransactionFile` snapshot so the next plan run sees the latest data.

## Diagrams
- `spec/plan-apply/all-categories.md#transactions` contains the sequence diagram that shows parser ƒ+' Cosmos lookup ƒ+' plan item ƒ+' state persistence.
- Embed `diagrams/sequence.mmd` in Confluence pages to show how the plan runner orders transactions (respecting `transactionSequence`) before the apply runner writes the state.
- Walk through `spec/Transactions/transactions-plan.mmd` and `spec/Transactions/transactions-apply.mmd` when covering the transaction-specific plan/apply flow.

## Key notes
- Ordering rules (`plan.ordering.rules` entries referencing transactions) ensure the `transactionSequence` reported in the JSON controls the execution order even when multiple files are processed in a single plan run.
