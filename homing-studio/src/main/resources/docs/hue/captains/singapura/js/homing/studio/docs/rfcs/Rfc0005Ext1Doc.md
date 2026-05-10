# RFC 0005-ext1 — Typed Plan Containers

| Field | Value |
|---|---|
| **Status** | **Draft** — open for iteration. Implementation in flight. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-10 |
| **Last revised** | 2026-05-10 |
| **Honours** | [Plans as Living Containers](#ref:plan-doc) — the doctrine this RFC operationalises. |
| **Extends** | [RFC 0005](#ref:rfc-5) — the catalogue redesign whose AppHost + Registry + sealed-Entry framework Plans now reuse. |
| **Honours (peer)** | [Catalogues as Containers](#ref:cc) — the parent pattern. |
| **Addresses** | The 14 per-tracker AppModule files (7 `*Plan.java` + 7 `*Step.java`) plus their imports/exports/link-record boilerplate; the lack of a Plan-level acceptance pillar making the three-pillar doctrine implicit; the framework's inability to compile-enforce that Plans honour their structure. |
| **Target phase** | Phase 1 — bounded scope, mechanical migration. |

---

## 0. Status notice

This RFC turns the **Plans as Living Containers** doctrine into a concrete data model + execution shape. It applies RFC 0005's framework (single AppHost + Registry + sealed Entry) at a new layer (Plan), and adds the missing `acceptance()` pillar to make the doctrine's three pillars visible at the Java interface.

---

## 1. Motivation

The catalogue redesign (RFC 0005) eliminated 14 per-catalogue AppModule files, replaced them with one shared `CatalogueAppHost`, and made catalogues pure typed data. The same overhead exists at the Plan layer:

- 7 `RfcXXXXPlan.java` AppModules, ~30 LoC each
- 7 `RfcXXXXStep.java` AppModules, ~30 LoC each
- Each Plan and Step declares its own `appMain` / `link` / `INSTANCE` / `imports()` / `exports()` records
- Each Plan + Step pair must be registered in `StudioServer.main`'s app list (14 entries)
- Each Plan + Step pair was implementing `NavigableApp` (RFC 0005) for catalogue listing
- Each Plan + Step provides `homeCatalogueClass()` + `parentCatalogueClass()` (RFC 0005) for breadcrumb derivation

That's ~600 LoC + 14 app-registry entries that exist only because the framework chose "one AppModule per Plan" as the shape. RFC 0005 already proved that "one AppHost serving every registered instance" is a cleaner shape; applying it to Plans saves the duplication.

Separately, the [Plans as Living Containers](#ref:plan-doc) doctrine commits to **three pillars** — questions, phased actions, acceptance — but today's `Plan` interface only exposes `decisions()` (questions) and `phases()` (phased actions). Plan-level acceptance is implicit in per-phase verification + metrics; the doctrine's third pillar has no surface in the data interface. This RFC adds it.

---

## 2. Proposed model

### 2.1 The expanded `Plan` interface

```java
public interface Plan {
    /** Identity-display: human-readable label. */
    String name();

    /** Optional one-line summary used in catalogue listings. */
    default String summary() { return ""; }

    /** Optional tracker-page subtitle (longer prose). */
    default String subtitle() { return ""; }

    // --- The three pillars (compiler-enforced — abstract methods) ---

    /** Pillar 1: open questions. */
    List<Decision> decisions();

    /** Pillar 2: phased actions. */
    List<Phase> phases();

    /** Pillar 3: plan-level success criteria. */
    List<Acceptance> acceptance();

    // --- Optional doc references (already present from current Plan) ---
    default String executionDoc() { return null; }
    default String dossierDoc()   { return null; }
}
```

- **Compiler-enforced**: `name()`, `decisions()`, `phases()`, `acceptance()` have no defaults — implementing classes must override or fail to compile. The doctrine's "no skipping pillars" guard is enforced at the type system.
- **`subtitle()` keeps the existing tracker-page lead paragraph** as optional content; not a structural pillar (it's intro prose, not a pillar of the work).
- **`executionDoc()` / `dossierDoc()`** stay as optional cross-references to companion docs — backward-compatible with existing trackers.

### 2.2 The `Acceptance` record

```java
public record Acceptance(
        String label,         // short title — "All trackers migrated"
        String description,   // longer statement; may mention phase IDs / decision IDs by reference
        boolean met           // status flag — true once the criterion is satisfied
) {}
```

Three fields. `description` is free prose where cross-references to phase IDs (`"phase 06"`) and decision IDs (`"per D7"`) appear naturally — the same identity-by-string mechanism existing trackers already use within phase notes.

### 2.3 `Plan` as a sealed `Entry` subtype — `Entry.OfPlan`

```java
public sealed interface Entry {
    record OfDoc(Doc doc)              implements Entry {}
    record OfCatalogue(Catalogue cat)  implements Entry {}
    record OfApp(NavigableApp app)     implements Entry {}
    record OfPlan(Plan plan)           implements Entry {}    // NEW

    static Entry of(Doc doc)           { return new OfDoc(doc); }
    static Entry of(Catalogue cat)     { return new OfCatalogue(cat); }
    static Entry of(NavigableApp app)  { return new OfApp(app); }
    static Entry of(Plan plan)         { return new OfPlan(plan); }   // NEW
}
```

Four-way sealed dispatch in the renderer. `JourneysCatalogue.entries()` becomes `Entry.of(Rfc0001Plan.INSTANCE), …` — same shape as today's `NavigableApp` opt-in, but Plans are no longer AppModules.

### 2.4 `PlanAppHost` — single shared AppModule

URL contract:

```
/app?app=plan&id=<class-fqn>                          ← index page
/app?app=plan&id=<class-fqn>&phase=<phase-id>         ← phase detail page
```

One AppModule serves both views. `PlanAppHost.Params(String id, String phase)` — `phase` optional; presence determines view kind. Server pre-resolves the Plan via `PlanRegistry`; renderer dispatches index-vs-step based on the phase param.

### 2.5 `PlanRegistry` — boot-time validation

```java
public final class PlanRegistry {
    public PlanRegistry(Collection<Plan> plans, DocRegistry docs) { … }
}
```

Boot-time validations (parallel to `CatalogueRegistry`):

1. **Class-uniqueness** — each Plan class registered at most once.
2. **Phase ID uniqueness within a plan** — duplicate phase IDs throw at boot.
3. **Decision ID uniqueness within a plan** — duplicates throw.
4. **Phase status non-null** per phase.
5. **Decision status non-null** per decision.
6. **Phase dependency targets exist** — `Dependency.phaseId()` references a real phase ID in the same plan.
7. **executionDoc / dossierDoc resolve** in the supplied `DocRegistry`.

All fail-fast at boot. No runtime surprises.

### 2.6 `PlanGetAction` + `PlanStepGetAction` — JSON endpoints

`/plan?id=<fqn>` returns:

```json
{
  "kicker":   "RFC 0001",
  "name":     "App Registry & Typed Navigation",
  "summary":  "...",
  "subtitle": "...",
  "brand":    { "label": "...", "homeUrl": "..." },
  "breadcrumbs": [ { "name": "...", "url": "..." }, ... ],
  "totalProgress": 100,
  "openDecisions": 0,
  "decisions": [ { "id": "D1", "question": "...", "status": "RESOLVED", ... } ],
  "phases":    [ { "id": "01", "label": "...", "status": "DONE", "tasks": [...], "metrics": [...], ... } ],
  "acceptance":[ { "label": "...", "description": "...", "met": true }, ... ]
}
```

`/plan-step?id=<fqn>&phase=<id>` returns the same shape plus the resolved current-phase data for direct rendering.

(Alternatively: single `/plan` endpoint with the full payload, renderer chooses which phase to show. Lean: single endpoint; fewer round-trips.)

### 2.7 Per-tracker file count: 4 → 2

Each tracker today:

| File | Status after RFC 0005-ext1 |
|---|---|
| `Rfc0001Steps.java` | **Stays** (data) |
| `Rfc0001PlanData.java` | **Stays** (adapter implementing `Plan` — gets the new `acceptance()` method) |
| `Rfc0001Plan.java` | **Deleted** (`PlanAppHost` serves it) |
| `Rfc0001Step.java` | **Deleted** (`PlanAppHost` serves it) |

Net per tracker: −2 files, −60 LoC, −4 conformance-test list entries.

Across 7 trackers: **−14 files, −420 LoC, −28 conformance entries.**

### 2.8 Compiler-enforced structural integrity

The doctrine's "no skipping pillars" guard is type-system enforced:

```java
public class Rfc0001PlanData implements Plan {
    // Forced by the compiler:
    @Override public String                 name()       { ... }
    @Override public List<Decision>         decisions()  { ... }
    @Override public List<Phase>            phases()     { ... }
    @Override public List<Acceptance>       acceptance() { ... }
}
```

If any of `name`, `decisions`, `phases`, `acceptance` is missing, the class doesn't compile. Empty list is a valid implementation (`return List.of();`); skipping the method entirely is not.

What's mechanically beyond the compiler stays in conformance:
- ID uniqueness (per-plan)
- Status non-null
- Dependency-target validity
- Doc reachability

`PlanRegistry`'s constructor performs all of these at boot; a single `StudioPlanConstructsTest` (analogous to `StudioCatalogueConstructsTest`) pins it in CI.

---

## 3. Conformance

Same shape as RFC 0005's conformance: one tiny test that constructs `PlanRegistry` from the studio's explicit Plan list. If construction throws, the test fails with the registry's clear message.

```java
@Test
void planRegistryConstructsCleanly() {
    var docRegistry = ...;
    var plans = List.of(Rfc0001PlanData.INSTANCE, /* ... */);
    assertDoesNotThrow(() -> new PlanRegistry(plans, docRegistry));
}
```

Plus the existing `Plan` interface enforces (compile-time) that every concrete Plan implements all three pillars. No per-Plan dynamic-test factory needed.

---

## 4. Trade-offs and rejected alternatives

### 4.1 Single endpoint vs. separate `/plan` + `/plan-step`

Considered: separate endpoints for index and step views (mirroring `CatalogueGetAction`'s shape).

**Rejected.** Plan data is a single coherent payload — phases include enough per-phase data to render the step view directly. Returning the whole plan once + having the renderer pick the phase is simpler than two endpoints. The cost is a slightly larger initial payload; plans are small Java records, the JSON is well under 50KB even for the largest tracker.

### 4.2 Plan-level `acceptance` as a single string vs. `List<Acceptance>`

Considered: `String acceptance()` — one statement summarising success.

**Rejected.** Real plans have multiple success criteria — each surfacing a different intended outcome. A single string forces the writer to mash them into one paragraph (loses the "checklist" nature) or skip them. `List<Acceptance>` keeps each criterion individually trackable (`met` flag per item).

### 4.3 Cross-references in `Acceptance.description` — typed vs. free-form

Considered: `Acceptance` carries typed `List<String> phaseRefs` + `List<String> decisionRefs`.

**Rejected for v1.** Phase IDs and decision IDs already appear in free prose throughout existing tracker phase notes — they're effectively identity-by-string at the textual layer. Promoting them to typed fields adds API surface without buying mechanical guarantees (the description would still need prose around the references). If a future need for structured cross-refs surfaces (e.g., a "this acceptance is met by these specific metrics" rollup query), an extension RFC can add it.

### 4.4 Plans as `Doc` subtype vs. peer Entry kind

Considered: `Plan extends Doc` (the "static-or-living" framing) — Plans become a richer kind of Doc with their own data on top of `contents()`.

**Rejected.** Doc's contract centres on `contents() → String` (markdown bytes). A Plan has structured data (phases, decisions, acceptance) that doesn't fit a single string contents. Forcing Plan into Doc would either require Doc to expose richer data (breaks Doc's simple contract) or require Plans to serialise their structured data into markdown (breaks the typed-data benefit). A peer `Entry.OfPlan` keeps both interfaces clean.

### 4.5 Auto-discovery of Plans vs. explicit registration

Considered: walk `SimpleAppResolver`'s closure for things implementing `Plan`.

**Rejected.** Same reasoning as RFC 0005 D1 for catalogues — explicit registration prevents runtime surprises and makes the studio's plan inventory readable in one place (`StudioServer.main`).

### 4.6 Keep `Rfc0001Plan` as AppModule + add `Acceptance` field

Considered: minimal change — leave the per-tracker AppModule files in place and just add the `acceptance()` pillar.

**Rejected.** Doesn't address the doctrine's broader "the structure should be standard" pressure. Per-tracker AppModule boilerplate is precisely the symptom that the catalogue redesign solved at one level; leaving it at the Plan level would undermine the consistency story across the framework. Migration cost is bounded (7 trackers, mechanical) and the long-term simplicity wins.

---

## 5. Migration order (proposed)

1. **Framework primitives** — Add `Acceptance` record. Update `Plan` interface: add `name()` and `acceptance()` as abstract methods; demote some current optionals (`kicker`, `title`, `subtitle`) appropriately. Add `Entry.OfPlan` to the sealed Entry types.
2. **Per-tracker `Plan` implementation update** — Each tracker's `*PlanData.java` adapter gains `name()` + `acceptance()` overrides. Existing `decisions()` + `phases()` stay. ~10 minutes per tracker.
3. **`PlanAppHost`** — single shared AppModule. `Params(String id, String phase)`. JS body delegates to renderer; renderer reads `phase` to pick view.
4. **`PlanRegistry`** — boot-time registry with validations from §2.5.
5. **`PlanGetAction`** — `/plan?id=<fqn>` returning JSON payload.
6. **Renderer migration** — `PlanRenderer.java` + `.js` reshaped to consume the new payload. Existing renderer logic stays mostly intact (already pattern-matched on payload structure); the `appMain` glue moves into `PlanAppHost.selfContent`.
7. **Bootstrap wiring** — `StudioBootstrap.start(...)` accepts `List<Plan>` + handles `PlanRegistry` construction. Per-installation Plan registration in `StudioServer.main`.
8. **`JourneysCatalogue` migration** — entries use `Entry.of(Rfc0001PlanData.INSTANCE)` etc. Plans no longer need `NavigableApp`; the marker comes off them.
9. **Delete legacy types** — 7 × `RfcXXXXPlan.java`, 7 × `RfcXXXXStep.java`, `PlanAppModule.java`, `PlanStepAppModule.java`. Remove all 14 entries from `StudioServer.main`. Remove all conformance-test list entries.
10. **Conformance** — `StudioPlanConstructsTest` (registry construction).
11. **Tracker** — `Rfc0005Ext1Steps` / `PlanData` / `Plan` / `Step` (the last RFC tracker that still uses the legacy pattern; recursion-proof for the migration).
12. **Doc updates** — `TrackerKitDoc.md` rewritten for the new shape.
13. **Close-out** — verify, mark phases DONE, status → Implemented.

---

## 6. Effort estimate

- Framework primitives + Plan interface update: **~1 hour**.
- Per-tracker Plan adapter migration (7 × ~15 min): **~1.5 hours**.
- `PlanAppHost` + `PlanRegistry` + `PlanGetAction`: **~1.5 hours**.
- Renderer reshape (Java + JS): **~1 hour**.
- Bootstrap wiring + StudioServer + JourneysCatalogue: **~30 minutes**.
- Legacy deletion + conformance: **~30 minutes**.
- Tracker + doc updates: **~45 minutes**.

**Total: ~6.5 hours.** Slightly more than RFC 0005 because the renderer is richer (Plan has more structure than Catalogue) and there are 7 trackers vs 4 catalogues.

**Net code change: ~−500 LoC.** Mostly from deleting per-tracker AppModule boilerplate.

---

## 7. Doctrine implications

This RFC operationalises [Plans as Living Containers](#ref:plan-doc). The doctrine's five commitments map to RFC mechanisms:

| Doctrine commitment | RFC mechanism |
|---|---|
| Three-pillar structure | `Plan` interface — three abstract methods (`decisions`, `phases`, `acceptance`) |
| Closed structure, open content | Java type system enforces structure (compile error if pillar missing); content is editable Java records |
| Living, not free-form | Plans are stateless records edited at source; rendering reads current state on each request |
| Intrinsic identity | The Java class itself is identity (mirrors RFC 0005 catalogues) |
| Render-agnostic | `PlanAppHost` + `PlanGetAction` separate data from presentation |

The Catalogue Doctrine (RFC 0005) and the Plan Doctrine (this RFC) are now both anchored in the framework. Future "structured living container" types (e.g., a Roadmap, a Risk Register) follow the same pattern: define the doctrine's pillars, add an interface with abstract pillar methods, build an AppHost + Registry + sealed Entry kind.

---

## 8. Open questions

- **OPEN — `kicker` field on Plan**. Today's `Plan.kicker()` returns a small label like "RFC 0001" or "rename". Useful for the page header, but it's display-decorative not structural. Lean: keep as optional metadata (default empty) to preserve renderer behaviour; the doctrine treats it as part of `name()` flavour rather than its own pillar.
- **OPEN — `executionDoc` / `dossierDoc` reachability check**. RFC §2.5 invariant 7 requires these to resolve in DocRegistry. But existing trackers set `executionDoc` to a UUID string; the registry has no schema-aware way to distinguish "Doc UUID" from "any string the renderer treats as a hint". Lean: validate when non-null + parseable as UUID; allow null / unparseable for backward compat. Decide before §5 step 7.
- **OPEN — Legacy `Plan.kicker / title / subtitle / totalProgress / openDecisions`** vs. minimal `name() + summary() + subtitle()`. The current `Plan` interface has more methods than the doctrine's three pillars + display data. Some are derived (`totalProgress`, `openDecisions`); some are display (`kicker`). Lean: keep derived methods as defaults (computed from phases/decisions); make `kicker` optional default empty; replace `title()` with `name()` for consistency with Catalogue. Decide before §5 step 1.

---

## 9. Out of scope

- **Multi-user collaborative editing** — Plans are static Java records; mutability is at edit-recompile-refresh granularity.
- **Search across plans** — covered by the doc browser (it can index Plan summaries via DocReference if needed).
- **Plan templating / scaffolding** — generating new tracker skeletons. Future tooling concern.
- **Per-installation acceptance dashboards** — rendering "all acceptance items across all plans". Possible follow-up RFC; the data shape is now there to support it.

---

## 10. Revision log

- **2026-05-10** — Initial draft. Captures the data model from the **Plans as Living Containers** doctrine, with the three pillars made explicit at the interface level. Acceptance is a `List<Acceptance>` to support multiple criteria. Compiler-enforces pillar presence; conformance handles content rules. Single PlanAppHost serves all plans (URL: `/app?app=plan&id=<fqn>`); legacy 14 per-tracker AppModule files deleted. Tracker for this RFC is the last one to use the legacy pattern.
