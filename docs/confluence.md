Architecture overview (docs/architecture.md) describing the plan/apply flow, state service, and how the new sides integration fits in.
Configuration guide (docs/configuration.md) covering application properties, directory mappings, and Cosmos state setup with references to the latest sides automation.
Contribution checklist (docs/contribution-guide.md) explaining how to add a file category, parsing strategy, state handling, plan ordering, and applier plus the new doc/script updates.
File-processing catalogue (docs/file-processing.md) detailing every directory/category, parsing strategy, model payload, and interplay with plans/states.
Plan lifecycle reference (docs/plan-lifecycle.md) walking through discovery, plan construction, apply execution, and state maintenance, now linked to the detailed sides spec.
Testing overview (docs/testing-overview.md) that lists representative test suites and notes the new model/StateFileService/Cosmos coverage.
Knowledge-transfer notes (docs/kt.txt) capturing high-level writing tasks and the updated references to the sides and integration docs.
Spec-level diagrams & flows under spec/:
spec/Sides/sides-plan-apply.md for the complete sides plan/apply narrative + Mermaid flows.
spec/plan-apply/all-categories.md covering every category’s plan and apply responsibilities with diagrams.
spec/design-patterns.md describing the strategy/factory, template method registry, adapter, and other patterns in the plan/apply framework.