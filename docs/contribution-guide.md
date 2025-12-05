# Contribution Guide

This guide outlines the steps to add a new file category, directory, or plan behavior so contributors can quickly plug into the existing architecture.

## 1. Update configuration

1. **Enum entry** – Add your new type to `com.example.cacex.model.FileCategory`.  
2. **Directory name** – Add getters/setters in `FileLocationProperties` and, if needed, expose the property in `src/main/resources/application.properties` under `cacex.paths`.  
3. **Plan ordering** – Add a `plan.ordering.rules[...]` entry in `application.properties` (or note it in `PlanOrderingProperties`) that specifies the desired action ordering (NEW/UPDATE/DELETE) and whether transaction sequencing sorting applies.

## 2. Implement parsing

1. Create a `FileParsingStrategy` that:
   - Checks for your directory name in `supports(Path)` (use `FileLocationProperties` for folder names).  
   - Reads JSON via `JsonModelMapper` and constructs your `LoadedFile` with category, key, path, and payload.
2. Register it as a Spring component so `FileParsingStrategyFactory` can discover it automatically.
3. If the file contains nested lists (like `AccountFile`, `AborFile`), ensure your strategy supplies the correct structure for `PlanService` to split into individual `PlanItem`s.

## 3. Handle state & deletes

1. Extend `PlanService` if the category needs special delete detection (see `processPlanEntries`/`addDeletesForMissingChanges` for list-based handling).  
2. Add logic to `StateFileService` to persist the resource during apply (`apply...` methods) and to delete the corresponding JSON when `Action.DELETE` occurs.  
3. If needed, introduce new helper files (e.g., `GeneralLedgerProfileFile`) to wrap payload + metadata.

## 4. Wire apply-side behavior

1. Create a `PlanItemActionService` (e.g., `FooApiService`) implementing create/update/delete operations (initially logging the action).  
2. Implement a `PlanItemApplier` (extend `AbstractPlanItemApplier`) that routes payloads to the API service and advertises `supportedCategory()`.  
3. Add a test to `PlanItemAppliersTest` to ensure the new applier delegates correctly.

## 5. Update tests & docs

1. Add model coverage (`ModelFilesEqualityTest`, `ModelToStringTest`, or new class) verifying equals/hashCode/toString for any new wrapper classes.  
2. Extend strategy tests to cover key derivation/payload parsing.  
3. Extend `StateFileServiceTest` if you changed persistence logic.  
4. Update documentation (see `docs/file-processing.md`, `docs/plan-lifecycle.md`, `docs/configuration.md`, etc.) to note the new directory and flow.  
5. Run `mvn -q test` to ensure the suite passes and note any required plan/order adjustments.
