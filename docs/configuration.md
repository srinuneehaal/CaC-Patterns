# Configuration Guide

This project relies on a small set of conventions defined in `src/main/resources/application.properties` and `FileLocationProperties`. The goal is to ensure the parsing/planning/apply layers agree on where files live and how they should be ordered.

## Directory layout

| Property | Description | Default value |
| --- | --- | --- |
| `cacex.paths.changed-files-dir` | Root folder scanned for changed JSON files during `--plan`. Relative to the repository root (default `changedfiles`). | `changedfiles` |
| `cacex.paths.state-files-dir` | Root where computed state files live. State files are updated while applying a plan to detect deletes later. | `statefiles` |
| `cacex.paths.plan-dir` | Output directory for the master plan JSON produced by the runner. | `plan` |
| `cacex.paths.master-plan-file` | Filename under `plan-dir` where the ordered `masterplan.json` is written. | `masterplan.json` |
| `cacex.paths.sides-dir-name` … `cacex.paths.abor-dir-name` | Directory names under each scope (e.g., `changedfiles/ATG/sides`). Matching names must be used by the parsing strategies because they detect files by folder name (`PostingRulesFileParsingStrategy` looks for `/postingrules/`). | See property list in `application.properties`. |
| `cacex.paths.general-ledger-profiles-dir-name` | New entry for the GL profile directory (`glprofile`) so the parser, state service, and plan service route those files correctly. |

The combination of `changedfiles/<scope>/<dir>` and `statefiles/<scope>/<dir>` is used to find changed and previous versions; filenames typically include the key plus `-<scope>.json`.

## Ordering rules

Plan ordering is driven by `PlanOrderingProperties`. When `application.properties` defines `plan.ordering.rules[i]`, each rule targets a `FileCategory` and a list of actions. Default entries look like:

```properties
plan.ordering.rules[0].category=SIDE
plan.ordering.rules[0].actions=NEW,UPDATE
plan.ordering.rules[1].category=TRANSACTION
plan.ordering.rules[1].actions=DELETE,UPDATE,NEW
plan.ordering.rules[1].sort-transaction-sequence=true
...
plan.ordering.rules[11].category=GENERAL_LEDGER_PROFILE
plan.ordering.rules[11].actions=NEW,UPDATE,DELETE
```

The engine falls back to these defaults when no configuration is supplied; custom values can reorder categories or introduce additional sequence sorting flags.

## Environment overrides

`FileLocationProperties` looks up two environment variables for overriding plan output:

| Env var | Purpose |
| --- | --- |
| `PLAN_DIR` | Overrides `cacex.paths.plan-dir` (useful when running inside containers or CI). |
| `MASTER_PLAN_FILE` | Overrides `cacex.paths.master-plan-file`. |

Use `FileLocationProperties#setEnvLookup` in tests to simulate the override behavior without touching the process environment.

## Adding new directories

1. Update `FileLocationProperties` with new getter/setter pairs, defaulting to the desired folder name.  
2. Expose the property under `cacex.paths` in `application.properties`.  
3. Create a parsing strategy that looks for the folder name, registers a `FileCategory`, and produces a payload.  
4. Hook it into the PlanService flow (special treatment if you need delete scanning).  
5. Add an `PlanOrderingProperties.Rule` entry so the ordering engine schedules the category appropriately.
