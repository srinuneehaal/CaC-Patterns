# Category: ABOR

## Directory & model
- `changedfiles/<scope>/abor/*.json` contain `AborRequest` lists parsed via `AborFileParsingStrategy`. Each request carries a code that becomes the key.

## Planning
- The parser splits the list into individual entries and compares each code against the Cosmos state documents (type `ABOR`). Missing entries generate deletes; new entries produce `NEW` plan items.

## Application
- `StateFileService` rebuilds the `AborFile` aggregate document after each apply action, so the persisted list mirrors the latest config.

## References
- `spec/plan-apply/all-categories.md#abors` explains the per-entry diff approach.  
- Include this doc in knowledge-share sessions when discussing how side-by-side diffing works for updates that touch translations or mathematical rules.
