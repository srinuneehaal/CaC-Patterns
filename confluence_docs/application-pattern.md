# Application Patterns

This Spring Boot utility leans on a few deliberate patterns so each stage (discovering files, building plans, ordering rules, and applying changes) stays decoupled and testable.

## Strategy-driven parsing
- Each directory under `changedfiles/<scope>` maps to a `FileParsingStrategy` implementation. The `FileParsingStrategyFactory` picks the first strategy whose `supports(Path)` method returns `true`, allowing new directories to be registered without touching `PlanService`.  
- The parsed payload, along with its derived `FileCategory`, flows into `PlanService` as a `LoadedFile`, meaning the planner never needs to know how to deserialize JSON.

## Cataloged plan assembly
- `PlanService` centralizes category-specific reconciliation: list-type files (accounts, derived portfolios, ABOR lists, etc.) reconcile by comparing parsed entries with Cosmos-backed `StateFileService` documents, creating `NEW / UPDATE / DELETE` `PlanItem`s as needed.  
- A `PlanOrderingRuleEngine` applies configurable rules (defined in `application.properties`) before `PlanWriter` persists the sorted `MasterPlan`. The rule engine itself is an expression of the Strategy pattern, letting teams tweak the action order without recompiling.

## Apply-time dispatch tables
- `PlanApplyService` deserializes each `PlanItem.payload` using the same models generated during parsing. It looks up the matching `PlanItemApplier` via the `supportedCategory()` map so each category plugs into the apply path by registering a bean.  
- `PlanItemApplier` implementations delegate to `PlanItemActionService`s (e.g., `ChartOfAccountsApiService`, `AborConfigurationApiService`), which currently mock the LUSID SDK. This follows the Command pattern: each applier encapsulates “perform this new/update/delete operation” while `PlanApplyService` just iterates the list.

## Shared infrastructure
- `StateFileService` is the single point of truth for Cosmos interactions: loading payloads, writing documents, and comparing payloads with `json-diff` logic. All plan + apply flows call into it to keep persisted state aligned with plan combinations.  
- Environment-driven configuration (`FileLocationProperties`, `plan.ordering.rules`, Cosmos connection info) keeps the runners stateless, letting the same code conquer CI, local dev, or Docker-based conveyor belts.

Link these ideas back to `docs/architecture.md`, `docs/plan-lifecycle.md`, and `docs/file-processing.md` when translating into Confluence so the diagrams and catalogs reinforce this pattern story.
