# Plan Kit

A typed multi-phase tracker. Per **RFC 0005-ext1** and the **Plans as Living Containers** doctrine ([#ref:plan-doc](#ref:plan-doc)), a plan is a *living document* with three compiler-enforced structural pillars (questions, phased actions, acceptance) plus an optional fourth (objectives). The shared `PlanAppHost` serves every registered plan — no per-tracker AppModule, no JS, no CSS.

**Where**: `homing-studio-base/.../base/tracker/`

- `Plan.java` — the interface. Three compiler-enforced pillars (`decisions()`, `phases()`, `acceptance()`) + optional `objectives()` + identity (`name()`).
- `Decision.java` / `Phase.java` / `Task.java` / `Dependency.java` / `Metric.java` — phase-internal data records.
- `Acceptance.java` — pillar-3 record `(label, description, met)`.
- `Objective.java` — optional pillar-4 record `(label, description)`.
- `PlanAppHost.java` — shared AppModule; `/app?app=plan&id=<class-fqn>`.
- `PlanGetAction.java` — `/plan?id=<class-fqn>` returning fully-resolved JSON.
- `PlanRegistry.java` — boot-time registry; performs the §3 validations (id uniqueness, status non-null, dependency targets resolve, doc reachability).
- `PlanHostRenderer.java` + `.js` — shared renderer; dispatches index vs step view.

---

## A plan tracker is two files

The recipe — for a plan tracking, say, a service-mesh rollout:

```
my-studio/migrations/mesh/
├── MeshSteps.java       ← source-of-truth data (phases + decisions + acceptance + objectives)
└── MeshPlanData.java    ← thin adapter implementing Plan (~80 LoC, mostly boilerplate)
```

### `MeshSteps.java` — source of truth

Defines `Phase` / `Decision` records, the `PHASES` and `DECISIONS` static lists, and helper methods (`totalProgressPercent`, `openDecisionsCount`). Edit this file to update progress, resolve decisions, or revise phases. Mirrors the shape used by every studio tracker (e.g. `Rfc0005Steps.java`).

```java
public final class MeshSteps {
    public enum Status { NOT_STARTED, IN_PROGRESS, BLOCKED, DONE }
    public enum DecisionStatus { OPEN, RESOLVED }

    public record Task(String description, boolean done) {}
    public record Phase(String id, String label, /* … */ Status status,
                        List<Task> tasks, /* … */) { /* … */ }
    public record Decision(String id, String question, /* … */) { /* … */ }

    public static final List<Decision> DECISIONS = List.of(/* … */);
    public static final List<Phase>    PHASES    = List.of(/* … */);
}
```

### `MeshPlanData.java` — adapter

Implements `Plan` by adapting `MeshSteps`'s data into the framework's typed `Phase` / `Decision` / `Acceptance` / `Objective` records. The adapter is mechanical — copy from any existing tracker (e.g. `Rfc0005Ext1PlanData`). The only genuinely interesting code lives in `MeshSteps`.

```java
public final class MeshPlanData implements Plan {
    public static final MeshPlanData INSTANCE = new MeshPlanData();

    @Override public String kicker() { return "MIGRATION"; }
    @Override public String name()   { return "Service Mesh Rollout"; }

    @Override public List<Phase>      phases()     { /* adapt MeshSteps.PHASES */ }
    @Override public List<Decision>   decisions()  { /* adapt MeshSteps.DECISIONS */ }
    @Override public List<Acceptance> acceptance() { return List.of(/* ship-gates */); }
    @Override public List<Objective>  objectives() { return List.of(/* optional */); }
}
```

---

## The three pillars (compiler-enforced)

`Plan` declares `decisions()`, `phases()`, `acceptance()` as **abstract** — every concrete plan MUST implement all three or fail to compile. Empty lists are valid (a plan with no open questions returns `List.of()`); skipping the method is a compile error. This is stronger than runtime validation — the framework cannot be misused by accident.

| Pillar | Method | Shape | Meaning |
|---|---|---|---|
| **Questions** | `decisions()` | `List<Decision>` | Open or resolved design decisions. Keep DECISIONS in source order — chronology helps readers follow the design's evolution. |
| **Phased actions** | `phases()` | `List<Phase>` | Ordered execution units. Each `Phase` has tasks, dependencies on other phases (by id), status, optional metrics. |
| **Acceptance** | `acceptance()` | `List<Acceptance>` | Plan-level success criteria — pass/fail ship-gates. Each `Acceptance` has a `met` boolean that flips as evidence accumulates from phases. |

## The optional fourth pillar — Objectives

`Plan.objectives()` defaults to `List.of()`. Override to surface a high-level "Objectives" section at the top of the index view. **Distinct from acceptance**: objectives describe goals (un-checkboxed, prose), acceptance describes ship-gates (pass/fail). Use objectives to orient a reader before they scroll into questions, phases, or acceptance.

```java
@Override public List<Objective> objectives() {
    return List.of(
            new Objective(
                    "Reduce p99 latency to under 100ms",
                    "Service-to-service mesh adds observability without paying the synchronous-RPC tax."),
            new Objective(
                    "Zero-downtime rollout",
                    "Every phase is independently revertable; no blue-green window required.")
    );
}
```

---

## Identity & URL contract

- **Identity** = the implementing Java class (`MeshPlanData.class`). No UUID, no slug, no separate identity field. The class FQN is the wire-stable handle; renames are caught at compile time.
- **URL** = `/app?app=plan&id=<MeshPlanData-FQN>` for the index view; add `&phase=<id>` for the per-phase detail view. Both URLs are pre-resolved server-side via `PlanAppHost.urlFor(MeshPlanData.class)` / `urlFor(MeshPlanData.class, "01")` — the renderer never constructs URLs of its own.
- **JSON payload** lives at `/plan?id=<FQN>`. Brand, breadcrumbs, all four pillars + per-phase task / dependency / metric lists are server-pre-resolved; the JS renderer fetches once and dispatches index vs step view based on the `phase` param.

---

## Registration

Three places to add a new plan:

```java
// 1. studio-main/MyStudioServer.java — the registry (PlanRegistry source)
List<Plan> plans = List.of(
        /* existing trackers, */
        MeshPlanData.INSTANCE   // ← new
);

// 2. (Optional) JourneysCatalogue.java — so it appears in the Journeys page
@Override public List<Entry> entries() {
    return List.of(
            /* existing entries, */
            Entry.of(MeshPlanData.INSTANCE)   // ← new
    );
}

// 3. (Optional) MyStudioPlanConstructsTest.java — pin in CI
List<Plan> plans = List.of(/* …, */ MeshPlanData.INSTANCE);
```

The Journey/test wiring is optional. The PlanRegistry registration is what makes the URL serve.

---

## Boot-time validations (`PlanRegistry`)

Performed once at `StudioBootstrap.start(...)`. Each one fails fast with a clear message naming the offending plan:

1. **Plan class registered at most once** — duplicate detection.
2. **`name()` non-blank** — every plan needs a label.
3. **Phase ids unique within the plan**.
4. **Decision ids unique within the plan**.
5. **Phase `status` non-null** — `NOT_STARTED` is the explicit zero, not `null`.
6. **Every `dependsOn` target resolves** to a phase id in the same plan.
7. **`executionDoc()` UUID exists in the DocRegistry** when set (per RFC 0004 typed-doc reachability).
8. **`dossierDoc()` UUID exists in the DocRegistry** when set.

---

## Conformance — single registry-construction test

Per RFC 0005-ext1's §3 conformance posture: the framework's compiler enforcement plus `PlanRegistry`'s boot-time validations cover the structural invariants. CI just needs **one test** that constructs the registry from the studio's explicit plan list.

```java
class MyPlanConstructsTest {
    @Test void planRegistryConstructsCleanly() {
        var docRegistry = /* … */;
        List<Plan> plans = List.of(
                MeshPlanData.INSTANCE,
                /* every other plan */
        );
        assertDoesNotThrow(() -> new PlanRegistry(plans, docRegistry));
    }
}
```

If any validation above fails, the test fails with the registry's clear message. ~25 LoC pinned in CI is sufficient.

---

## Render contract

The shared `PlanHostRenderer` renders a plan in two modes, dispatched on the URL `phase` param:

- **Index view** (no `phase` param): brand header → kicker → name → subtitle → **Objectives** (if non-empty) → overall progress → **Decisions** (if non-empty) → **Acceptance** (if non-empty) → **Phases** (cards) → footer with `executionDoc` / `dossierDoc` links.
- **Step view** (`phase=<id>` param): per-phase detail — tasks (todo list), dependencies, metrics table, prev/next phase navigation.

The renderer is **render-agnostic** about plan content — it consumes the JSON shape of `Plan`, dispatches by structure, no per-plan customization.

For background on why this shape: see **RFC 0005-ext1** ([#ref:rfc-5e1](#ref:rfc-5e1)) and the **Plans as Living Containers** doctrine ([#ref:plan-doc](#ref:plan-doc)).

For the catalogue-side of the same typed-container story: see **`CatalogueKitDoc`** ([#ref:kit-cat](#ref:kit-cat)).
