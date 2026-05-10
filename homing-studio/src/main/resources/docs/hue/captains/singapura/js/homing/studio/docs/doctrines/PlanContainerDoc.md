# Doctrine — Plans as Living Containers

> **A Plan is a typed living container of structured work: open questions to resolve, phased actions to execute, and acceptance criteria captured as work proceeds. The three-pillar structure is closed — every Plan has questions, phased actions, and acceptance; no other top-level kinds. The content within each pillar is open and lives over time: questions get added and resolved, phases get added and modified and marked done, acceptance items accumulate evidence. Plans don't add structural slots; they add content within the slots the doctrine fixes. Identity is intrinsic; rendering belongs to the renderer; the standard structure is what makes Plans comparable, traversable, and effective.**

This is the doctrine the framework commits to for Plans. Peer to [Catalogues as Containers](#ref:cc) — both share the "container with intrinsic identity, no presentation, open extensibility under closed shape" pattern, but a Plan adds **structured content** (three pillars instead of a flat entry list) and a **temporal aspect** (content evolves as work proceeds).

---

## What this doctrine commits to

Five sentences, five commitments:

1. **Three-pillar structure.** Every Plan has questions, phased actions, and acceptance. These are the only top-level kinds; nothing else lives at the Plan layer.
2. **Closed structure, open content.** The set of pillars is fixed by the doctrine. The content within each pillar is freely added, modified, and removed over the plan's lifetime.
3. **Living, not free-form.** "Living" means content evolves; it does not mean structure is negotiable. A plan that escapes the three-pillar shape stops being effective — it becomes prose.
4. **Intrinsic identity.** Every Plan has an identity (the implementing Java class itself). All references to a Plan flow through identity.
5. **Render-agnostic.** The Plan holds no presentational data. Display is the renderer's responsibility; alternative renderers are unrestricted.

---

## The three pillars

### Questions (`decisions()`)

Open questions whose resolution shapes the work. Each question carries a recommendation, the chosen value (when resolved), a status (`OPEN` / `RESOLVED`), and rationale.

- **Living**: questions are added as they surface, marked `RESOLVED` with a chosen value when settled.
- **Why "decisions" as the data field**: a decision is the *artifact* (an OPEN decision is a question; a RESOLVED decision is the answer). The doctrine prose says "questions"; the data field name is `decisions()` because it spans both states.

### Phased actions (`phases()`)

Ordered phases of work. Each phase has an id, label, status (`NOT_STARTED` / `IN_PROGRESS` / `BLOCKED` / `DONE`), an ordered list of tasks, dependencies on other phases, and per-phase outcomes (verification statement + metrics + rollback notes).

- **Living**: phases get added as scope clarifies; tasks toggled done; status flipped; metrics captured at completion.
- **Per-phase outcomes** stay with their phase — verification ("how do we know this phase is done?"), rollback ("how do we undo it?"), and metrics ("what changed quantitatively?").

### Acceptance (`acceptance()`)

Plan-level success criteria — *what does it mean for the whole plan to be done?* A list of acceptance items, each a statement plus a met/unmet flag. Cross-references per-phase metrics or decision IDs in the description as evidence.

- **Living**: acceptance items are declared up front; their `met` flag flips to `true` as evidence accumulates from phases.
- **Why a list**: a plan typically has multiple success criteria (one per intended outcome). A single sentence rarely captures the whole picture.
- **Cross-references**: an acceptance description can mention phase IDs and decision IDs by reference — the typed identity doctrine extends naturally.

---

## What this doctrine bans

- **No free-form sections.** Every piece of content lives in one of the three pillars. No "miscellaneous notes" pillar; no "appendices" pillar; no "related documents" pillar at Plan level. (Per-phase notes are fine — they live within phased-actions.)
- **No skipping pillars.** A Plan with `decisions() = []` is fine (no open questions). A Plan that doesn't override `decisions()` at all is **not a Plan** — required pillar methods are abstract; compile error if skipped.
- **No structural drift.** Downstream cannot add `Plan.appendices()` or `Plan.relatedDocs()`. If a need surfaces that doesn't fit the three pillars, the answer is either "use a Doc reference inside a phase note" or "the doctrine needs an extension RFC".
- **No untyped state.** Every phase has a status (even `NOT_STARTED` is a status). Every decision has a status (`OPEN` or `RESOLVED`). Null statuses are not allowed.
- **No presentation directives.** Plan data carries no icons, badge classes, tile shapes, or rendering hints — same render-agnostic commitment as Catalogues.

---

## What this doctrine permits

- **Empty pillars.** A plan can legitimately have zero open decisions, or no phases yet (just acceptance + decisions). The pillars exist; they may be empty.
- **Cross-reference between pillars.** An acceptance item's description may mention phase IDs ("driven by phase 03's metrics"). A phase's notes may mention decision IDs ("resolved per D7"). These are typed-identity references in prose.
- **Free editing of content.** Add tasks, mark them done, flip statuses, add metrics — all happen by editing the `Steps.java` source file and recompiling. The plan re-renders with the new state.
- **Multiple parents in the catalogue tree.** A Plan can appear as an entry in more than one parent catalogue — its identity is intrinsic.
- **Themed rendering.** The default renderer supplies an elegant default; alternative renderers replace the default without touching the Plan data.

---

## Where this doctrine doesn't apply

A surface that needs richer state than questions / phased actions / acceptance — full-text search, query-driven filtering, runtime mutability beyond edit-recompile-refresh, multi-user collaborative editing — is not a Plan. It's a different kind of app.

When in doubt, ask: *"can this be expressed as questions + phased actions + acceptance, edited at the source?"* If no, it isn't a Plan; it's a richer kind of app that the catalogue tree links to.

---

## Why the strictness is worth it

- **Comparability.** Every Plan looks the same at the structural level — the studio's tracker pattern, the rename plan, future migration plans, audit plans, all have questions+phases+acceptance. Reviewers know where to look.
- **Effectiveness.** Free-form planning documents drift toward prose-with-bullet-points and lose the operational handle. Structured plans force the writer to declare what they're deciding, what they're doing, and what success looks like — separately. The structure surfaces gaps.
- **Living without chaos.** Content evolves over time, but the structure stays put. A plan editor doesn't have to re-design the page every time something changes — they edit content in known slots.
- **Cross-referencing.** Stable phase IDs + decision IDs make typed cross-references possible without inventing a new linking mechanism per artifact.
- **Renderable + indexable.** A uniform structure means one renderer covers every plan; one indexer can extract questions / actions / outcomes across every plan in the studio.

---

## How to think about it

A Plan is a typed living tree node. It has identity (so it can be referenced), three structural pillars (questions / phases / acceptance) with typed content in each, and zero opinion about how it looks. The pillars are how the framework knows it's a Plan. The living content is how the work actually happens — added, modified, resolved over the plan's lifetime.

When designing a new "plan-shaped" thing, the test is: *do its top-level concerns map cleanly onto questions + phased actions + acceptance?* If yes, it's a Plan; the framework's PlanAppHost will render it. If no, it's something else.

---

## See also

- [Catalogues as Containers](#ref:cc) — the peer doctrine that introduced the typed-container pattern.
- [RFC 0005](#ref:rfc-5) — the catalogue redesign whose framework this doctrine extends.
- (RFC 0005-ext1 — the Plan implementation following this doctrine — to be added.)
