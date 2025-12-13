# Knowledge Share & Diagrams

This document bundles the artifacts and references teammates can use to onboard quickly, run mental model sessions, or explain this project to new collaborators.

## Core knowledge share assets

- `docs/architecture.md`: High-level flow chart plus component relationships that show how `ChangedFilesProvider`, `PlanService`, `PlanOrderingRuleEngine`, and `PlanApplyService` interact with `StateFileService`.  
- `docs/plan-lifecycle.md`: Narrative of change detection, plan creation, apply execution, and Cosmos-backed state maintenance.  
- `docs/file-processing.md`: A catalog of directories, parsing strategies, models, and how they map into `PlanItem`s.  
- `docs/contribution-guide.md`: The onboarding checklist for adding new file categories, including which beans and tests to update.  
- `spec/plan-apply/all-categories.md` and `spec/Sides/sides-plan-apply.md`: Deep dives and sequence diagrams for specific categories.  
- `docs/testing-overview.md` & `docs/test-plan-suite.md`: Quick reference for the unit/CI suite and the integration plan/apply script.
- `docs/integration test plan`: Step-by-step instructions for the scripted scenario plus verification checklist.
- `docs/kt.txt`: Original knowledge share ideas, now referenced in this consolidated view so nothing is lost.

## Diagrams to share during walkthroughs

| File | Description |
| --- | --- |
| `diagrams/system_design.mmd` / `diagrams/system_design.png` | End-to-end component diagram showing runners, services, and persistence layers mentioned in `docs/architecture.md`. Use the PNG for quick slides or the `.mmd` to update diagrams with Mermaid. |
| `diagrams/system_design LLD.mmd` / `diagrams/system_design LLD1.mmd` | Lower-level flows for plan vs. apply interaction, especially when describing `PlanItemApplier` and `PlanItemActionService` behaviors. |
| `diagrams/sequence.mmd` / `diagrams/sequence_1.mmd` | Sequence diagrams illustrating how Plan generation and apply cycles execute. Provide these during knowledge-transfer sessions that describe `PlanRunner` and `ApplyRunner`. |
| `diagrams/class.mmd` | UML class diagram summarizing the domain models (`PlanItem`, `LoadedFile`, `PlanItemApplier`). Useful when explaining extensions or new categories. |
| `diagrams/Untitled-1.mmd` | Reserve for ad-hoc designs; feel free to rename to match new contexts during knowledge share sessions.

## How to use this bundle

1. **Pair documentation with diagrams** when walking someone through the workflow. Open the Mermaid files in VS Code or render them via the Mermaid preview extension.  
2. **Reference test coverage** via `docs/testing-overview.md` whenever discussing reliability, then show the integration logs under `integrated-test-plan/logs/`.  
3. **Update this document** whenever new knowledge artifacts arrive: add new sections for new diagrams, update the asset list for fresh documentation, and keep `docs/kt.txt` linked in case those ideas evolve.  
4. **Create presentation-friendly copies** by exporting relevant `.mmd` diagrams to PNG (the repo already hosts the rendered `sequence_1.png`).

## Sharing tips

- Host a short knowledge-share session with the architecture flow (using the ASCII diagram in `docs/architecture.md`) followed by a live run of `docs/item-workflow.md`.  
- Distribute `docs/test-plan-suite.md` with the script logs when demoing the integration automation.  
- Archive the diagrams in the shared drive or Confluence page mentioned in `docs/confluence.md` if you need to present to broader audiences.
