# Defect 0006 — ConformanceTest Family Lacks a Taxonomy

| Field | Value |
|---|---|
| **Status** | **Open** (documentation debt; no functional impact). |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-17 |
| **Severity** | Documentation — without a top-level map, the conformance family looks like an unstructured pile of test classes. New contributors can't tell which family a proposed test joins, what gaps remain, or whether a test duplicates an existing one. Surfaces every time someone adds a new typed kind. |
| **Affected modules** | `homing-conformance` (the abstract bases), every downstream studio's `*ConformanceTest` subclasses. |
| **Surfaces in** | The trigger that filed this defect was filing [Defect 0005](#ref:def-5). The fix for D0005 added one more conformance test to a list that already had eight; the question "do we have one for *this* kind of invariant?" had no canonical answer. |

---

## 1. Symptom

Nine conformance tests live in `homing-conformance/src/main/java/hue/captains/singapura/js/homing/conformance/`:

```
CdnFreeConformanceTest
ContentViewerConformanceTest
CssConformanceTest
CssGroupImplConsistencyTest
DocConformanceTest
DoctrineConformanceTest
HrefConformanceTest
ManagerInjectionConformanceTest
PlanRegistrationConformanceTest
```

Each is an abstract base; every downstream studio subclasses the ones it needs. The framework's test pattern is good — but there's no doc that:

1. Lists every conformance test that exists.
2. States the invariant each one defends, in one sentence.
3. Groups them into a meaningful taxonomy.
4. Points at the bug, RFC, or pattern that motivated each.
5. Says which kinds of invariant are *not* yet defended and so might want a new test family.

The closest thing to a list is `ls` on the directory. The closest thing to a rationale is each test's javadoc — which is good per-test but invisible at the family level.

---

## 2. Root cause

The conformance family grew organically. Each test was added when a specific bug or RFC surfaced a need (RFC 0004 → `DocConformanceTest`; RFC 0007 → `HrefConformanceTest`; a CDN-import slip → `CdnFreeConformanceTest`; RFC 0015 Phase 5 → `ContentViewerConformanceTest`; Defect 0005 → `PlanRegistrationConformanceTest`). No-one ever stepped back and asked "what's the shape of this family?"

The framework has parallel families that *are* documented top-down (Doc kinds, Catalogue levels, Theme tiers, Segment kinds). Conformance tests are the oldest family without that treatment.

---

## 3. Proposed taxonomy

A first-cut categorisation, drawn from the existing nine:

### 3.1 Registration consistency

Invariant: the same entity is registered in two places, and the framework needs both halves to function. Drift between the two is a runtime 404 / NPE.

- **`ContentViewerConformanceTest`** — every `ContentViewer.app()` instance is present in `Fixtures.harnessApps()` (or some `Studio.apps()`).
- **`PlanRegistrationConformanceTest`** — every Plan wrapped as a catalogue-leaf `PlanDoc` is also in some `Studio.plans()`.

Root issue: [Defect 0005](#ref:def-5) — Two-Source Registration Drift.

### 3.2 Wire-surface integrity

Invariant: every entity reachable on the wire has the structural fields the protocol requires.

- **`DocConformanceTest`** — every contributed `Doc` has a non-null UUID, UUIDs are unique within the closure, `contents()` resolves to non-empty bytes, every markdown reference link resolves to a declared `Reference` (RFC 0004-ext1).

### 3.3 Discipline enforcement (anti-pattern detection)

Invariant: certain easy-to-write code patterns are forbidden because they break a framework property. The test scans source / bytecode for the anti-pattern.

- **`CdnFreeConformanceTest`** — no `EsModule` imports from a CDN URL; all third-party JS must be a `BundledExternalModule`.
- **`HrefConformanceTest`** — no raw `element.href = …` assignments; the typed `HrefManager` is the only sanctioned setter (RFC 0007 history-integrity discipline).
- **`CssConformanceTest`** — no raw `element.style.…` or `element.className = …` assignments; CSS application goes through the typed `css.addClass(...)` / `css.removeClass(...)` calls so the `CssRegistry` can track ownership.

### 3.4 Shape conformance (doc-shape patterns)

Invariant: certain doc kinds are required to follow a specific structural template — sections present in a fixed order, required fields filled, etc.

- **`DoctrineConformanceTest`** — every doctrine doc has the canonical eight-section shape (Stance, Why, How it pays, Refusal cost, etc.).

### 3.5 Pattern adherence (framework-level typed conventions)

Invariant: certain typed APIs require parallel registration in a typed registry; the test asserts the parallel registration is consistent.

- **`CssGroupImplConsistencyTest`** — every `CssClass` record declared inside a `CssGroup` is also in the group's `classes()` list (otherwise the class compiles but is never served).
- **`ManagerInjectionConformanceTest`** — managers like `HrefManagerInstance` are imported by the modules that use them; importing a manager API without importing its instance is a typed-imports gap.

---

## 4. Why a patch alone isn't enough

We could add a `README.md` next to the conformance directory and call it done. That improves discoverability but loses the framework's own dogfood opportunity: a Building Block doc, authored as a `ComposedDoc` with `TextSegment` + `TableSegment` segments, is the right shape — addressable, citable, themed, and *the same kind of doc the framework asks downstream authors to use*.

It also misses the second-order goal: **make it routine for new conformance tests to land with their taxonomy slot already chosen**. When the next dual-registered kind appears (Defect 0005's recurring shape), the contributor should see "Registration consistency" as a labelled family and slot the new test in without needing to invent the category.

---

## 5. Proposed resolution

Build a Building Block doc — call it the *Conformance Kit* — authored as a `ComposedDoc`. Structure:

1. **TextSegment — what conformance tests do.** Two-paragraph framing: tests as build-time gates for invariants the type system can't express.
2. **TextSegment — the families.** Brief intro to the taxonomy.
3. **TableSegment — the inventory.** Columns: Test class · Family · Invariant · Origin (RFC / Defect).
4. **TextSegment — adding a new test.** Convention: pick a family, write the abstract base, subclass in `homing-demo`, document the invariant + origin.
5. **TextSegment — known gaps.** Families that exist conceptually but don't yet have tests (e.g., theme-token coverage, viewer-chrome composition).

The kit doc lives in `BuildingBlocksCatalogue` next to the `.mdad+ Kit`; the categorisation lives in the typed table where every row's "Family" column is one of the five categories above. Adding a new test = adding a row to the typed table + an entry to the catalogue, both of which surface in code review.

---

## 6. Lesson

The framework's other typed families (Doc kinds, Catalogue levels, Theme tiers, Segment kinds) all earned a doctrine + an RFC + a Building Block kit doc as they matured. Conformance tests reached parity in count without reaching parity in documentation. When a family's vocabulary gets used inside CI pipelines and bug reports — *"go look at `PlanRegistrationConformanceTest`"* — it's a sign the family has graduated into the framework's everyday surface and deserves the same first-class treatment as Docs or Themes.
