# Defect 0005 — Two-Source Registration Drift

| Field | Value |
|---|---|
| **Status** | **Open** (backstopped by conformance tests; root-cause fix deferred). |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-17 |
| **Severity** | Architectural — the same shape recurs across at least two typed kinds (`Plan`, `AppModule`); every new typed kind that needs dual registration repeats the trap. Each instance is a 404 waiting to happen if the author forgets either side. |
| **Affected modules** | `homing-studio-base` (`Bootstrap`, `PlanRegistry`, `Fixtures`, `DefaultFixtures`), `homing-studio` (Plans + Catalogues), every downstream studio that registers Plans or custom AppModules. |
| **Surfaces in** | `/app?app=plan&id=<class-fqn>` returns 404 "Plan not registered" when the plan is wrapped as a catalogue leaf but missing from any `Studio.plans()`. `/app?app=<viewer-simple-name>...` returns 404 "No app registered with this simple name" when a `ContentViewer.app()` instance is missing from `Fixtures.harnessApps()`. |

---

## 1. Symptom

Two known instances of the same shape:

### Instance A — Plans

`TypedContentVocabularyPlanData` was filed under `RfcJourneysCatalogue` via `Entry.of(this, TypedContentVocabularyPlanData.INSTANCE)`. The tile appeared on the Journeys page (catalogue side knew about it). Clicking the tile navigated to:

```
/app?app=plan&id=hue.captains.singapura.js.homing.studio.content.TypedContentVocabularyPlanData
```

…and produced:

```
{"resource":"...","furtherExplanation":"Plan not registered"}
```

Root: `HomingStudio.plans()` didn't list the plan. `PlanRegistry` is populated only from `studio.plans()`; the catalogue leaf wraps the plan in a `PlanDoc` (which lands in `DocRegistry` via the synthetic-leaf harvest) but never reaches `PlanRegistry`.

### Instance B — AppModules (the original case, fixed earlier)

`ComposedViewer` (RFC 0019 Phase 1) was registered via `ComposedContentViewer.INSTANCE` in `Fixtures.contentViewers()` so the polymorphic doc router knew which app to dispatch the `"composed"` kind to. But `DefaultFixtures.harnessApps()` didn't include `ComposedViewer.INSTANCE` — so the framework's app resolver couldn't find an app called `"composed-viewer"`. Same response shape, different message:

```
{"resource":"composed-viewer","furtherExplanation":"No app registered with this simple name"}
```

The two failures are structurally identical: the framework needed the entity registered in **two places**, the author registered it in **one place**, and the type system permitted both halves to author independently.

---

## 2. Root cause

A typed entity (Plan, AppModule, …) is exposed to the framework via two **independent** registration paths that serve **different purposes**:

| Entity | Registration A (navigation) | Registration B (routing) |
|---|---|---|
| **Plan** | `Entry.of(catalogue, plan)` → wraps in `PlanDoc` → catalogue leaf | `Studio.plans()` → `PlanRegistry` → `/plan?id=…` |
| **AppModule (with ContentViewer)** | `ContentViewer.app()` → polymorphic doc-kind dispatch | `Fixtures.harnessApps()` or `Studio.apps()` → `SimpleAppResolver` → `/app?app=…` |
| **Catalogue** | `subCatalogues()` declared by parent | (not currently split — only one path) |

Neither registration knows the other exists. Authors author each side in isolation. The compiler is happy with either half on its own. The 404 only fires when a user clicks the navigation that points at the unrouted entity.

The framework's other typed-kind registries (`DocRegistry`, `CssRegistry`, `ThemeRegistry`) don't have this hazard — there's exactly one registration path per entity, and the leaf-harvest pattern derives downstream views from it.

---

## 3. Why a patch isn't enough

Two patches already landed:

- `ContentViewerConformanceTest` (RFC 0015 Phase 5) — asserts every `ContentViewer.app()` is in the studio's app closure.
- `PlanRegistrationConformanceTest` (this defect) — asserts every catalogue-leaf-wrapped Plan is in some `Studio.plans()`.

Both are backstops: they catch the drift at build time rather than at user click time, with precise error messages telling the author exactly what to add and where. That's the best the framework can do *without changing the registration model*.

But the conformance pattern doesn't scale cleanly:

1. **Every new typed kind that needs dual registration needs its own conformance test.** When (not if) someone adds a third dual-registered kind, they'll forget the new test until the next 404.
2. **Tests defend invariants; they don't make them inexpressible.** An author can read the conformance-test source and recognise the pattern — but the type system still permits writing only half a registration. The discipline lives in CI, not in the IDE.
3. **The conformance-test family grows without classification.** With ~10 conformance tests already in the framework (`DocConformanceTest`, `ContentViewerConformanceTest`, `CssConformanceTest`, `CdnFreeConformanceTest`, `DoctrineConformanceTest`, `HrefConformanceTest`, `CssGroupImplConsistencyTest`, `ManagerInjectionConformanceTest`, plus this one), it's getting hard to see at a glance which invariants are covered and which gaps remain. See [Defect 0006](#ref:def-6) for that meta-issue.

---

## 4. Resolution sketch (not started)

Several shapes for a root-cause fix; each has tradeoffs. **No option chosen yet** — pick when the next dual-registration kind shows up, or when the conformance backstop misses one.

### Option A — Single source: catalogue leaves

`Studio.plans()` and `Fixtures.harnessApps()` become *derived* values. `Bootstrap` harvests Plans from `Entry.OfDoc(PlanDoc(plan))` leaves and AppModules from `ContentViewer.app()` references; the author registers each entity exactly once, on the navigation side.

- **Pro:** Zero possibility of drift. The discipline is "if it's not addressable from the catalogue tree, it doesn't exist."
- **Con:** A plan or app that's intentionally URL-only (not in any catalogue) becomes hard to express. Today's `DefaultFixtures.harnessApps()` includes `DocReader`, `PlanAppHost`, `ThemesIntro`, `SvgViewer`, `ComposedViewer`, `TableViewer`, `ImageViewer` — none of which would naturally have a catalogue leaf. They'd need a different registration path, and we're back to two sources.

### Option B — Single source: studio side

`Entry.of(catalogue, plan)` becomes `Entry.of(catalogue, planClass)`; the catalogue references the plan by class, and `CatalogueRegistry` resolves the class against `PlanRegistry` at build time. Same shape for AppModules: catalogues reference AppModules by class; resolution against the app resolver.

- **Pro:** Catalogue authoring becomes lighter (no `.INSTANCE` import in catalogue files).
- **Con:** Compile-time class references in a catalogue can't carry typed Params for `AppDoc`-style nav. The `Entry.of(this, new Navigable<>(SomeApp.INSTANCE, new SomeApp.Params(...)))` pattern is exactly why we have instance-based catalogue leaves.

### Option C — Sealed registration witness

Introduce a typed `Registered<T>` wrapper that can only be constructed from a `Studio.plans()` callback. `Entry.of(catalogue, registered)` takes the wrapper. The type system makes it impossible to add a catalogue leaf for a plan that isn't in `Studio.plans()`.

- **Pro:** Pushes the discipline into the type system as far as Java allows.
- **Con:** Adds ceremony at every catalogue leaf. Doesn't help AppModule case (no analog because `ContentViewer.app()` returns a runtime instance, not a typed reference).

### Option D — Status quo + conformance tests + clear documentation

Accept the dual-registration model; rely on conformance tests as the gate; document the pattern explicitly in [Defect 0006](#ref:def-6)'s taxonomy work so future kinds know to add their own conformance test.

- **Pro:** No framework changes; no breaking refactors.
- **Con:** New dual-registered kinds will still forget the conformance test on first try. The discipline lives in tribal knowledge until it's documented.

---

## 5. Verification (of the backstop)

* `ContentViewerConformanceTest` — covers Instance B; demonstrated by reverting the `ComposedViewer.INSTANCE` addition to `DefaultFixtures.harnessApps()` and watching the test fail with a precise message.
* `PlanRegistrationConformanceTest` — covers Instance A; demonstrated by reverting the `TypedContentVocabularyPlanData.INSTANCE` addition to `HomingStudio.plans()` and watching the test fail with the catalogue path and the URL that would 404.

Both tests are wired into `homing-demo`'s test suite, exercising the same umbrella that the demo server boots.

---

## 6. Lesson

When the same conceptual entity needs to appear in two registries to function, *and* the two registries serve different parts of the same user journey (here: the tile, and the page the tile opens), drift between them is the dominant failure mode — not the exception.

Conformance tests are the right immediate backstop; they catch every instance at build time with actionable messages. But they're patches on a symptom — *the registration model itself permits the inconsistency*. A future RFC should either collapse to a single registration source per entity (Option A or B) or push the consistency requirement into the type system (Option C). The dual-registered kind that finally pushes us over is probably the one that gets it done.
