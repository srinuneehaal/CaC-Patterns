# Diagrams & Workflow

Use this page to host the visuals that illustrate how the plan and apply runners execute, including sequence flows and key domain models. Each diagram is already present as a Mermaid file under `diagrams/` and can be rendered directly in Confluence.

## Plan & apply workflow
- `diagrams/system_design.mmd` + `diagrams/system_design.png`: high-level component diagram showing runners (`PlanRunner`, `ApplyRunner`), services (`PlanService`, `PlanApplyService`, `StateFileService`), and the Cosmos-backed state sink. Good for orienting new colleagues.  
- `diagrams/system_design LLD.mmd` / `system_design LLD1.mmd`: lower-level diagrams that expand on how `PlanItemApplier` plus `PlanItemActionService` interact during apply. Include these under the workflow narrative to explain how each category’s payload flows through the mock SDK services.

## Sequence diagrams
- `diagrams/sequence.mmd` and `diagrams/sequence_1.mmd`: depict the runtime sequence of a plan run followed by an apply execution. Include them in the Confluence page as embedded Mermaid blocks or linked images; the repo already hosts `diagrams/sequence_1.png` for ready-made slides.  
- Mention in the Confluence page that `PlanService` reads `CHANGED_FILES`, consults `StateFileService`, and emits `PlanItem`s, then `PlanApplyService` reads the plan, selects `PlanItemApplier`, and calls the appropriate action service, each of which writes back to `StateFileService`.

## Class diagram
- `diagrams/class.mmd`: summarizes `PlanItem`, `LoadedFile`, `PlanItemApplier`, and the `PlanItemActionService` abstractions. Render this when explaining how to extend the application with a new category.  
- If you need a PNG version, export via the Mermaid preview and attach it to the Confluence page alongside the `.mmd` link.

## Future diagrams
- `diagrams/Untitled-1.mmd` is available as an editable canvas for ad-hoc sketches; rename it in Confluence once you give it a descriptive context.  
- Update this page whenever new Mermaid files land (e.g., new workflow variations for additional categories).
