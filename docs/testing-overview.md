# Testing Overview

This repository includes focused unit tests for the plan generator, state handling, parsing strategies, and model objects. Below is a summarized matrix that can guide developers when adding new features or verifying behavior. Recent additions include `ModelCoverageTest` (full model validation), `CosmosStateRepositoryTest`, and the expanded `StateFileServiceTest` that now covers the account-specific document id logic, sides key derivation, and list-based deletions.

| Layer | Representative tests | Purpose |
| --- | --- | --- |
| **Services** | `PlanServiceCoreTest`, `PlanServiceSpecialCategoriesTest` | Verify plan construction logic: parsing categories, comparing to state files, handling special list-based categories, and ensuring the ordering engine is invoked. |
| **Plan ordering** | `PlanOrderingRuleEngineTest` | Confirms configured rules sort items as expected, including `transactionSequence` support and fallback behavior when rules miss. |
| **State persistence** | `StateFileServiceTest` | Covers per-category Cosmos document writes/deletes (sides, transactions, COA, accounts, posting rules, GL profiles, derived portfolios, etc.) plus edge cases such as missing metadata, scope backfill, and chart/account derivation. |
| **Model coverage** | `ModelFilesEqualityTest`, `ModelToStringTest`, `LoadedFileTest`, `PlanItemTest`, `FileCategoryTest` | Ensure data model equality/hashCode, toString, and plan item getters/setters behave correctly, including the newly added `GeneralLedgerProfileFile`. |
| **Parsing strategies** | `PostingRulesFileParsingStrategyTest`, `GeneralLedgerProfileFileParsingStrategyTest` | Validate that each strategy recognizes its folder, derives keys correctly, and populates payloads with necessary fields. |
| **Appliers** | `PlanItemAppliersTest` | Confirms every applier delegates `create/update/delete` to the underlying API service and advertises the right `FileCategory`. |
| **Runner & apply paths** | `PlanRunnerTest`, `ApplyRunnerTest` | Confirm CLI flags trigger plan generation/apply logic, handle extra arguments, and surface exceptions without crashing the application. |

### Running the suite

Execute `mvn -q test` to run the entire suite. Tests spawn Spring components where necessary (e.g., strategy beans) and rely on temporary directories (`@TempDir`) for filesystem interactions. The suite currently passes after the recent additions, so rerun before merging changes that touch plan ordering, file parsing, or state management.

### Adding new tests

1. **Model logic**: Add equality/hashCode/toString coverage in `ModelFilesEqualityTest` or a new focused class.  
2. **Parsing strategy**: Add `@TempDir`-based tests that write realistic JSON and assert key derivation/payload.  
3. **State updates**: Extend `StateFileServiceTest` to confirm new categories are persisted/deleted as expected.  
4. **Plan item flow**: If a category has special ordering needs, add assertions in `PlanOrderingRuleEngineTest` or create targeted `PlanService` tests.  
5. **Applier surface**: Add to `PlanItemAppliersTest` for new `PlanItemApplier`s to ensure `PlanApplyService` will route items correctly.
