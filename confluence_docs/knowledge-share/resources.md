# Knowledge Share Resources

This page collects the narratives and artifacts you should highlight during knowledge-share sessions or onboarding moments.

## Reference documents
- `docs/architecture.md`: high-level architecture and component relationships for plan/apply flows.  
- `docs/plan-lifecycle.md`: describes discovery → plan → apply → state update cycle with the relevant beans and services.  
- `docs/file-processing.md`: catalog of directories, parsing strategies, and resulting models.  
- `docs/contribution-guide.md`: checklist for adding new categories, including ordering rules, parsing strategies, and tests.  
- `docs/testing-overview.md` and `docs/test-plan-suite.md`: summarise the unit/CI coverage plus the integration script.  
- `docs/knowledge-share.md`: curated list of diagrams and quick tips (link this from every shared session so others can effortlessly follow up).  
- `docs/kt.txt`: legacy bullet points that still contain helpful talking points; keep linking to it until you migrate the ideas elsewhere.

## Diagram anchors
- Link to `confluence_docs/diagrams-workflow.md` so readers can open each Mermaid file along with its rendered PNG (e.g., `sequence_1.png`).  
- When presenting, pair the ASCII component diagram from `docs/architecture.md` with `diagrams/system_design.png` for quick mental models.

## Sharing guidance
- Host a short walkthrough that starts with `docs/architecture.md`, then flips to `confluence_docs/diagrams-workflow.md` to show the plan & apply sequences.  
- Present `docs/file-processing.md` while describing each category’s directory; transition seamlessly into the category-specific pages you create under `confluence_docs/categories/`.  
- Keep `docs/knowledge-share.md` bookmarked so you can share the same bundle of materials across sessions and keep the diagrams synchronized.
