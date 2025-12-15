# Knowledge Share Sessions

Use this page to document the recurring agenda, talking points, and follow-up items for live knowledge-share meetings.

## Suggested agenda
1. **Architecture recap (5 min)** – Start with `docs/architecture.md` or the ASCII flow in `docs/plan-lifecycle.md` so the audience sees how `PlanRunner`, `PlanService`, `PlanOrderingRuleEngine`, and `ApplyRunner` interact with `StateFileService`.  
2. **Plan/apply walkthrough (10 min)** – Open `confluence_docs/diagrams-workflow.md` and review the sequence diagrams (`diagrams/sequence_1.mmd/png`) to demonstrate how each `PlanItem` is created and then applied.  
3. **Category spotlight (10 min)** – Pick a handful of `confluence_docs/categories/*` pages and talk through the directory, payload model, and expected state change.  
4. **Testing & verification (5 min)** – Highlight `confluence_docs/testing-steps.md`, mention the integration script logs, and explain how the plan/apply HTML report surfaces KPIs for reviewers.

## Follow-up artifacts
- Upload the `plan/masterplan.html` report or relevant `plan/masterplan.json` excerpt to Confluence as a clickable artifact after each knowledge share so people can revisit the plan ordering discussion.  
- Share the integration logs from `integrated-test-plan/logs/` to prove that each stage (create → update → delete) ran end to end.  
- Circulate the diagrams (system design, sequence, class) as attachments or embedded Mermaid blocks so folks can reference them later.

## Continuous improvement
- After each session, update this page with new questions from attendees, any diagrams that need refreshes, or additional context that should be added to `confluence_docs/knowledge-share/resources.md`.  
- Encourage teammates to open pull requests against the `confluence_docs` directory when they produce new notes, diagrams, or fail/redo stories; that keeps the Confluence pages directly in sync with repository updates.
