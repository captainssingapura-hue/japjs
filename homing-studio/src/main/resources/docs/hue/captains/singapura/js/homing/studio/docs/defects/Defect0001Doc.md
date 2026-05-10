# Defect 0001 — No "App Kind" Abstraction; Patterns Re-Implemented Per-Instance

| Field | Value |
|---|---|
| **Status** | **Resolved** — 2026-05-09. The tracker kit (`Plan` interface + records, `PlanAppModule` + `PlanStepAppModule` interfaces, `PlanRenderer`, `PlanJson`, plus the new `StudioElements` builders `StatusBadge` / `OverallProgress` / `StepCard` / `DecisionCard` / `TodoList` / `Panel` / `MetricsTable`) collapsed the four existing trackers from ~900 LoC each to ~190 LoC each (data adapter + thin AppModule scaffolds). New trackers cost ~80 LoC. See §8 below for the resolution. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-08 |
| **Severity** | Design-level — affects every multi-instance pattern. |
| **Affected modules** | `homing-studio` (4 plans), prospectively any downstream that wants several similar trackers. |
| **Surfaces in** | Adding a new "Plan"/"Detour" requires ~900 LoC of mostly-mechanical boilerplate, dominated by content that is identical across instances. |

---

## 1. Symptom

Adding a 5th plan tracker to `homing-studio` (e.g. a "no-innerHTML migration" Detour) is estimated at ~700–900 LoC. Of that:

| File | LoC | What it actually is |
|---|---|---|
| `*Steps.java` | ~290 | The **only** real content — phases, tasks, decisions, dependencies. |
| `*DataGetAction.java` | ~150 | Reflection-walker turning Steps records → JSON. Nearly identical across plans. |
| `*Step.js` | ~170 | Detail view: tasks list, deps, acceptance, progress bar. ~95% chrome. |
| `*Plan.js` | ~150 | Index view: phase cards + overall progress. ~95% chrome. |
| `*Step.java` | ~75 | AppModule scaffolding; ~50 of those LoC is a hand-listed `StudioStyles.st_*` import block. |
| `*Plan.java` | ~55 | Same — most of it is a hand-listed CSS imports list. |

Roughly **~290 LoC of irreducible content** vs. **~610 LoC of pattern-replication boilerplate** per plan. Today's 4 plans together sit on ~2,400 LoC of duplicated mechanism.

---

## 2. Root cause

The framework abstracts **mechanism**, not **pattern**.

Every primitive needed to express a Plan tracker exists — `AppModule`, `AppLink`, `ImportsFor`, `CssClass`, typed query params, `EsModule`, the reflection utilities. What does **not** exist is a curated **kit** that says "here is how a Plan tracker is built; an instance is just its data."

Without that, every plan re-derives the same recipe from the same primitives. Type safety operates at the wrong granularity: we type-check that `st_step_card` is spelled correctly, but we don't type-check (or short-circuit) that "this app is a kind I've already built."

---

## 3. Where the boilerplate accumulates, and why

### 3.1 Hand-listed CSS imports (~100 LoC per plan)

```java
.add(new ModuleImports<>(List.of(
        new StudioStyles.st_root(), new StudioStyles.st_header(),
        new StudioStyles.st_brand(), new StudioStyles.st_brand_dot(),
        // …30–40 more lines
), StudioStyles.INSTANCE))
```

`ModuleImports` operates on individual `Exportable`s. There is no "bundle" primitive. The unit of cohesion in real life is "the studio chrome bundle" or "the progress-tracker chrome bundle" — sets that always travel together. Each consumer enumerates them by hand.

What's missing: a `Bundle<C>` type — a named group of `Exportable`s exposed as a single import. `StudioStyles.standardChrome()` returns a Bundle; `Plan.chrome()` returns a Bundle composing standardChrome + plan-specific additions. Downstream becomes one line per Bundle.

### 3.2 Per-plan `*DataGetAction.java` (~150 LoC each)

Each plan reflects on its Steps class and serializes its records. Across 4 plans, ~600 LoC of nearly-identical reflection code.

There is no shared "data shape interface" with an action attached. Records carry enough metadata that a single `<S extends PlanSteps> PlanDataGetAction<S>` could handle every existing plan and every future one. The framework doesn't ship that abstraction, so each plan rolls its own.

### 3.3 Per-plan view JS (~330 LoC each)

`*Plan.js` and `*Step.js` are 95% identical chrome (header, breadcrumbs, progress bars, status badges) and 5% domain-specific labels.

This is the layer where the studio framework **stops being a framework** and **starts being raw HTML output**. The Java side has typed everything; the JS side throws all that away and asks each AppModule to rebuild the same DOM tree. **The typing does not cross the import boundary into the view layer.**

There is no view composition system, no parametrised template, no "tracker view" component. Each plan's JS files are bespoke string concatenation.

### 3.4 AppModule scaffolding (~60 LoC each, even before imports)

Every record-AppModule restates `record appMain()`, `record link()`, `INSTANCE`, `title()`, `imports()` builder, `exports()`. Most of this is bookkeeping that every concrete app declares the same way.

`AppModule` is a primitive interface, not an opinionated abstract base. A `SimpleAppModule<S>` that defaults `appMain` / `link` / `exports` and asks for just `title()` + `imports()` would collapse half of this for every concrete app, not just plans.

---

## 4. The deeper diagnosis

Mechanism-only frameworks are flexible but heavy: every consumer pays the full assembly cost. Pattern-bearing frameworks are opinionated but light: most consumers pay near-zero for the common case.

The studio is currently mechanism-heavy. Until 2026-05-08 it didn't even have a kit for the *server bootstrap* — that was ~900 LoC of duplication across `StudioServer.java` + `StudioActionRegistry.java` until `StudioBootstrap.start(...)` was extracted. The Plan layer is the same situation, one level up: a recipe re-cooked four times.

> **Whenever a pattern has been written more than twice, the framework owes a kit, not a primitive.**

This defect is the recognition that the Plan layer crossed that threshold three plans ago, and the cost is being paid every time a new tracker is added.

---

## 5. Suggested resolution (sketch — not committed)

Introduce **`PlanKit<S>`** in `homing-studio`:

```
homing-studio/
├── plan/
│   ├── PlanKit.java            ← parametric on S extends PlanSteps
│   ├── PlanSteps.java          ← interface: phases(), title(), summary()
│   ├── Phase.java, Task.java, Decision.java, Dependency.java   ← shared records
│   ├── PlanDataGetAction.java  ← generic; introspects any PlanSteps
│   └── resources/
│       ├── Plan.js             ← shared index view
│       └── PlanStep.js         ← shared detail view
```

A concrete tracker becomes:

```java
public record NoInnerHtmlDetour() implements PlanKit<NoInnerHtmlDetourSteps> { … }
public record NoInnerHtmlDetourSteps(...) implements PlanSteps { … }
```

Plus the actual phase/task/decision data, and a one-line registration. Estimated:
- Existing 4 plans collapse from ~900 LoC each → ~150 LoC each
- New Detour cost: ~80–150 LoC (just data)
- Net saving: ~3,000 LoC and immediate amortisation on every future tracker

---

## 6. Decision deferred

Two paths were on the table on 2026-05-08:

- **(A)** Defer both the kit and the no-innerHTML Detour; track the work in this defect doc and revisit when there is appetite for the kit refactor.
- **(B)** Build `PlanKit<S>` now as a prerequisite, migrate the 4 existing plans, then create the Detour as the first instance.

**Choice on 2026-05-08: (A).** No real dev capacity for the kit today; record the diagnosis to ensure we don't accidentally pay the per-plan cost a 5th time without first questioning whether to do (B).

---

## 7. Pending follow-ups blocked on this defect

- **No-innerHTML Detour** — superseded. The doctrines (Pure-Component Views et al.) and the tracker kit landed together; the no-innerHTML migration is enforced by `DoctrineConformanceTest`, not by a tracker.
- Any other tracker-shaped work (migrations, rollouts, audits) — now cheap to express. The new pattern (data file + adapter + ~50-line `*Plan.java` + ~55-line `*Step.java`) is the worked example for any future plan.

---

## 8. Resolution (2026-05-09)

Path **(B)** taken — the kit was extracted in `homing-studio/.../tracker/`:

**New types:**
- `Plan` interface — typed plan view with kicker/title/subtitle/totalProgress/openDecisions/executionDoc/dossierDoc/phases/decisions
- `Phase`, `Task`, `Decision`, `Dependency`, `Metric` records
- `PhaseStatus`, `DecisionStatus` enums
- `PlanJson` — Plan → JSON literal serialiser
- `PlanAppModule<M>` — generic AppModule interface; default `selfContent()` auto-generates the JS body
- `PlanStepAppModule<M>` — same for the per-phase detail page
- `PlanRenderer` (Java + JS DomModule) — pure rendering: `renderPlan(props)` + `renderStep(props)` returning `Node`s

**Shared builders added to `StudioElements`:**
- `StatusBadge`, `OverallProgress`, `StepCard`, `DecisionCard`, `TodoList`, `Panel`, `MetricsTable`

**Migrated trackers (all four):** Rename, RFC 0001, RFC 0002, RFC 0002-ext1. Each became a `*Steps.java` (unchanged data) + a thin `*PlanData.java` adapter + ~50-line `*Plan.java` + ~55-line `*Step.java`. **Eight `.js` files deleted, four `*DataGetAction.java` deleted, four routes removed from `StudioActionRegistry`.**

**Result:**
| Tracker | Before | After |
|---|---:|---:|
| Per-tracker LoC | ~900 (Java + JS combined, excluding Steps data) | **~190** |
| Hand-written `.js` view files per tracker | 2 | **0** |
| Studio-level boilerplate across 4 trackers | ~3,600 LoC | ~760 LoC |
| Net studio reduction | — | **~2,840 LoC** |
| Cost of a new tracker | ~900 LoC + ~3 days work | **~80 LoC + ~30 minutes** |

**Doctrine status:** `StudioDoctrineConformanceTest`'s allowlist is now `Set.of()` — the universal view doctrines (Pure-Component Views + Owned References) apply to every JS-bearing module in the studio, with no exemptions.

This defect closes. Future plan-tracker work should use the kit; if a structural mismatch surfaces, file a fresh defect against the kit rather than reopening this one.
