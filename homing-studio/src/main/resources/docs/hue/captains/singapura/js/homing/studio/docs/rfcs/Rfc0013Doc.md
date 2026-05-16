# RFC 0013 — jOntology Integration

| Field | Value |
|---|---|
| **Status** | Adopted (initial implementation landed; see §10 for follow-ups) |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-15 |
| **Adopted** | 2026-05-16 |
| **Target release** | 0.0.102 |
| **Scope** | Framework — adopt the [`jOntology`](#ref:jontology) marker-interface library across Homing's spine. Promotes the Functional Objects doctrine from convention to compile-and-runtime mechanism. Adds a build-time conformance test that runs the jOntology enforcer over the framework's classes. |

---

## 1. Motivation

The [Functional Objects doctrine](#ref:doc-fo) bans public statics anywhere in the framework, prescribing that behaviour belongs on methods of immutable typed objects. The doctrine names two shapes:

- **Stateless functional object** — a record with no fields (or only `final` config fields), exposing methods that depend on nothing but their arguments. Reached via a `public static final INSTANCE` field.
- **Dependency-holding object** — an immutable type with `final` references to its collaborators, set once at construction.

These classifications are currently enforced by **author discipline** plus **opportunistic doctrine review at code-review time**. There is no compile-time mechanism that says *"this class claims to be stateless"* and no runtime check that catches a regression. The doctrine binds future code, but the binding is convention, not mechanism.

[`jOntology`](#ref:jontology) — a sibling library from the same author group — provides exactly the missing mechanism: a small set of marker interfaces with a runtime enforcer. The classification overlap with Homing's doctrine is essentially exact:

| jOntology marker | Homing doctrine equivalent |
|---|---|
| `Mutable` | The exception case the doctrine names but generally refuses |
| `Immutable` | The umbrella for everything the doctrine prescribes |
| `Stateless` | "no fields at all" — pure marker types |
| `ValueObject` | Records carrying typed data with value-equality semantics |
| `FunctionalObject` | "dependency-holding object" — immutable with collaborator fields |
| `StatelessFunctionalObject` | "stateless functional object reached via INSTANCE" |

Adopting jOntology lifts the doctrine from convention to a typed contract:

- Every framework class **declares** its classification through `implements`.
- The jOntology **enforcer** verifies the contract holds — `Immutable` types have all-final fields, `ValueObject` types override `equals`/`hashCode`, `Stateless` types have no fields.
- A new **conformance test** runs the enforcer over the framework's classes at build time.

The doctrine's principles don't change. It gains a tool.

## 2. Design

### 2.1 Dependency

Add `io.github.captainssingapura-hue.lang.ontology:core` to `homing-core`'s `pom.xml`. Every other Homing module already depends on `homing-core` transitively.

The `enforcer` module depends on `core`; we add it as a `<scope>test</scope>` dependency in modules that run conformance tests.

### 2.2 Classification rule — mark high-level interfaces; implementations inherit

The framework's spine is a small set of typed interfaces (`Doc`, `Catalogue<Self>`, `Plan`, `Studio<L0>`) with many record implementations downstream. Rather than mark each record individually, **the marker goes on the interface and propagates by Java's interface-inheritance rules to every implementation**.

This is both ergonomic (one edit per interface, not per record) and stronger as a contract: a future record that tries to implement `Catalogue` with a non-final or stateful field fails the enforcer at build time, by virtue of inheriting the interface's marker.

**The framework's commitment is absolute**: every `Doc`, every `Catalogue`, every `Plan`, every `Studio` is — by design — a stateless functional object. There is no scenario in which any of them should hold mutable state or grow instance fields beyond what their contract requires. The marker enforces this discipline mechanically.

| Framework interface | jOntology marker | Propagates to |
|---|---|---|
| `Doc` | `StatelessFunctionalObject` | Every doctrine doc, RFC doc, case study doc, release doc, skill doc, building-block doc — about ~60 records |
| `Catalogue<Self>` (sealed: L0 through L8) | `StatelessFunctionalObject` | All ~17 catalogue records across the framework + downstream studios |
| `Plan` | `StatelessFunctionalObject` | All Plan-implementing records (`Rfc0001PlanData`, `RenamePlanData`, `DocDslPlanData`, etc.) |
| `Studio<L0>` | `StatelessFunctionalObject` | `HomingStudio`, `SkillsStudio`, `DemoBaseStudio`, `MultiStudio`, plus future downstream studios |
| `AppModule<P, M>` | `StatelessFunctionalObject` | All AppModule records (`CatalogueAppHost`, `PlanAppHost`, `DocReader`, `DocBrowser`, etc.) |

That's five interface-level markers covering the bulk of the framework's classes. Implementations need *zero* changes — they inherit the marker.

Types with actual fields (the small minority) get individual markers:

| Field-bearing type | jOntology marker | Rationale |
|---|---|---|
| `Bootstrap<S, F>` | `ValueObject` | `(F fixtures, RuntimeParams params)` — value equality matters for caching/comparison |
| `DefaultFixtures<S>` | `ValueObject` | `(Umbrella<S> umbrella)` — record with one structural field |
| `Umbrella.Solo<S>` / `Umbrella.Group<S>` | `ValueObject` | Record types with composition fields |
| `Entry.OfDoc / OfApp / OfPlan / OfStudio` | `ValueObject` | Sealed family of typed entry records |
| `StudioBrand` | `ValueObject` | `(label, homeApp class, logo)` triple |
| `RuntimeParams` (interface) | `ValueObject` (on `DefaultRuntimeParams` record) | Single-field record |
| `CatalogueClosure` | `StatelessFunctionalObject` | INSTANCE-pattern singleton with `walk(root)` method |
| `HomingActionRegistry`, `SimpleAppResolver`, `VertxActionHost` | `Mutable` (or refactored if possible) | Internal action-map / Vert.x state |

That's roughly ten field-bearing types to mark individually. Combined with the five interface markers, the entire framework spine is classified.

### 2.3 Why the user-doctrine assertion matters

The user's commitment — *"no Catalogue or Doc would be mutable or have any additional state for any reason"* — is the precondition that justifies interface-level marking. If `Doc` could legitimately have mutable subtypes, the marker couldn't go on the interface. Because `Doc` is asserted to be inherently stateless, marking the interface is correct.

This is the framework's contract speaking through types: anyone implementing `Doc` is opting into the doctrine. The marker makes that contract enforceable.

### 2.3 Enforcer integration

A new conformance test runs the enforcer:

```java
// homing-studio/src/test/.../OntologyConformanceTest.java
class OntologyConformanceTest {
    @Test void allMarkedFrameworkTypesHonourTheirContracts() {
        var report = OntologyEnforcer.scan(
                "hue.captains.singapura.js.homing.core",
                "hue.captains.singapura.js.homing.server",
                "hue.captains.singapura.js.homing.studio.base",
                "hue.captains.singapura.js.homing.studio");
        assertThat(report.violations()).isEmpty();
    }
}
```

The test fails the build if any type marked `Immutable` has a non-final field, any `ValueObject` skips `equals`/`hashCode`, any `Stateless` has fields, etc. Joins the existing conformance family (`StudioDocConformanceTest`, `StudioCatalogueConstructsTest`, `StudioPlanConstructsTest`).

### 2.4 What stays the same

- The Functional Objects doctrine's prose unchanged in spirit; gains a paragraph naming jOntology as the mechanism
- The framework's record-everywhere posture unchanged
- All existing API surfaces unchanged — markers are additive
- Downstream consumers see no breaking changes; they pick up jOntology as a transitive dependency they may use or ignore

## 3. Conformance — what the enforcer catches

The enforcer's value lies in catching things the type system alone cannot:

| Violation | Caught by jOntology |
|---|---|
| `Immutable` type with a non-final field (e.g. someone added a setter for "convenience") | ✅ |
| `ValueObject` that doesn't override `equals` (broken dedup, broken composition) | ✅ |
| `Stateless` type that gained a field (no longer interchangeable across instances) | ✅ |
| `StatelessFunctionalObject` whose method depends on instance state (cache, mutable lookup) | ✅ |
| `FunctionalObject` whose collaborator field is mutable (silently shared mutable state) | ✅ |
| `Mutable` type masquerading without a marker (no claim, no contract) | (caller-side issue; flag as missing classification in the conformance test) |

Every catch corresponds to a class of bug the framework's record-and-final discipline currently relies on author vigilance to prevent.

## 4. Usage cases

### 4.1 Existing framework types gaining markers

```java
// Was:
public record FunctionalObjectsDoc() implements ClasspathMarkdownDoc { ... }

// Becomes:
public record FunctionalObjectsDoc() implements ClasspathMarkdownDoc, StatelessFunctionalObject { ... }
```

```java
// Was:
public record StudioCatalogue() implements L0_Catalogue<StudioCatalogue> { ... }

// Becomes:
public record StudioCatalogue() implements L0_Catalogue<StudioCatalogue>, StatelessFunctionalObject { ... }
```

```java
// Was:
public record Bootstrap<S extends Studio<?>, F extends Fixtures<S>>(F fixtures, RuntimeParams params) { ... }

// Becomes:
public record Bootstrap<S extends Studio<?>, F extends Fixtures<S>>(F fixtures, RuntimeParams params) implements ValueObject { ... }
```

The marker is one extra clause in `implements`. No body changes.

### 4.2 Downstream studios opting in

A downstream studio may classify its own types using the same markers:

```java
public record MyHomeCatalogue() implements L0_Catalogue<MyHomeCatalogue>, StatelessFunctionalObject { ... }

public record MyConfig(String env, int port) implements ValueObject { ... }
```

Downstream may also run the enforcer over their own packages by extending the conformance test base.

### 4.3 New framework types — markers required from the start

The `create-homing-studio` skill's templates and the framework's authoring discipline require new types to declare their classification:

```java
// Templated for new doctrine docs:
public record MyDoctrineDoc() implements ClasspathMarkdownDoc, StatelessFunctionalObject { ... }
```

A doctrine doc that omits the marker would fail the conformance test at build time (with a *"unmarked type in framework package"* violation).

## 5. Doctrine refinement

The [Functional Objects doctrine](#ref:doc-fo) gains one section:

> *The framework adopts [jOntology](#ref:jontology) marker interfaces as the mechanical realisation of this doctrine. Every framework type carries a classification — `StatelessFunctionalObject`, `ValueObject`, `FunctionalObject`, `Mutable`, etc. — declared in its `implements` clause. The build runs the jOntology enforcer over framework packages; types whose declared classification is contradicted by their implementation fail the build. The doctrine's principles are unchanged; what changes is the enforcement mechanism — from author discipline to compile-and-runtime contract.*

No new doctrine. The existing one gets sharper.

## 6. Migration

The interface-level marking design collapses the migration to roughly **15 edits across the framework** — five interface markers plus ten field-bearing types — instead of ~100 per-record edits. Implementations inherit the marker; no per-record change required.

### 6.1 The five interface edits

| Edit | File | Change |
|---|---|---|
| 1 | `homing-core/.../AppModule.java` | `extends DomModule<M>, Linkable, StatelessFunctionalObject` |
| 2 | `homing-studio-base/.../Doc.java` | `interface Doc extends StatelessFunctionalObject` |
| 3 | `homing-studio-base/.../Catalogue.java` | sealed interface gains `extends StatelessFunctionalObject`; the L0–L8 sealed permits propagate by interface inheritance |
| 4 | `homing-studio-base/.../Plan.java` | `interface Plan extends StatelessFunctionalObject` |
| 5 | `homing-studio-base/.../Studio.java` | `interface Studio<L0 extends L0_Catalogue<L0>> extends StatelessFunctionalObject` |

After these five edits, ~100 implementing records become marked transitively. The enforcer test runs over the whole framework; any record that violates the inherited contract (gains a non-final field, gains a setter, gains mutable instance state) fails the build.

### 6.2 The ten field-bearing edits

| Edit | Type | Marker |
|---|---|---|
| 6 | `Bootstrap<S, F>` | `ValueObject` |
| 7 | `DefaultFixtures<S>` | `ValueObject` |
| 8 | `Umbrella.Solo<S>` / `Umbrella.Group<S>` | `ValueObject` (one edit on the sealed interface plus permits, or two records) |
| 9 | `Entry.OfDoc / OfApp / OfPlan / OfStudio` | `ValueObject` (similar — interface or each record) |
| 10 | `StudioBrand` | `ValueObject` |
| 11 | `DefaultRuntimeParams` | `ValueObject` |
| 12 | `CatalogueClosure` | `StatelessFunctionalObject` (INSTANCE-pattern with `walk()`) |
| 13 | `HomingActionRegistry` | `Mutable` (with comment explaining why — internal action map) |
| 14 | `SimpleAppResolver` | `Mutable` (with comment) or refactor to immutable if practical |
| 15 | `VertxActionHost` | `Mutable` (with comment — wraps Vert.x's mutable server state) |

### 6.3 What downstream sees

Downstream studios that already implement `Doc`, `Catalogue`, `Plan`, `Studio` get the marker for free — no change to their code, no rebuild required against new APIs. Their existing records simply inherit the framework's contract.

Downstream that *adds* a stateful field to a `Catalogue` or `Doc` will see the build fail with a clear message from the enforcer. This is the desired outcome: the doctrine becomes mechanically true for every consumer.

### 6.4 Net change

- ~15 framework edits (mostly one-liners adding `, MarkerName` to existing `extends` / `implements` clauses)
- One new conformance test (`OntologyConformanceTest` in `homing-studio`)
- ~100 records inheriting the marker automatically — zero per-record edits
- Doctrine prose updated in `FunctionalObjectsDoc.md` (one paragraph)
- One dependency added to `homing-core/pom.xml`

The work is small, the impact is full-framework. The interface-level marking is the central design move that makes the migration sane.

## 7. Decisions (locked)

1. **Adopt all six jOntology markers** — `Mutable`, `Immutable`, `Stateless`, `ValueObject`, `FunctionalObject`, `StatelessFunctionalObject`. Complete classification, no half-adoption.
2. **Dependency lives in `homing-core`** — every module already depends on it; no new dependency-graph edges introduced.
3. **Enforcer runs in CI as a conformance test** — joins the existing test family; failures break the build.
4. **Mark at the interface level, not the implementation level** — `Doc`, `Catalogue<Self>`, `Plan`, `Studio<L0>`, `AppModule<P, M>` each get the marker on the interface. Implementations inherit by Java's interface-inheritance rules. Reduces ~100 per-record edits to 5 interface edits.
5. **`Doc`, `Catalogue`, `Plan`, `Studio`, `AppModule` are inherently `StatelessFunctionalObject`** — the framework asserts these types CANNOT legitimately be mutable or grow per-instance state. Anyone implementing them is opting into the discipline; the marker enforces it. No exception path; no "but my use case needs state on a Doc."
6. **Field-bearing types get individual markers** — Bootstrap, DefaultFixtures, Umbrella, Entry, StudioBrand, RuntimeParams, etc. Roughly ten types; each gets its own classification call.
7. **Downstream classification is automatic for inherited markers** — a downstream studio's `MyDocSubtype` automatically inherits `StatelessFunctionalObject` from `Doc`. No additional opt-in step. Downstream's own NEW interfaces / records should be classified by the downstream's discipline.
8. **`jOntology`'s naming is canonical** — Homing's doctrine documents adopt jOntology's terminology (`StatelessFunctionalObject` etc.) where they overlap. Existing doctrine prose updated to use the canonical names.
9. **No replacement of the Functional Objects doctrine** — the doctrine stays; it gains a paragraph naming jOntology as its mechanical realisation.
10. **Pin the jOntology version** — start with `0.0.1` (current release); update via deliberate dependency-version PRs only.
11. **`Mutable` is for internal infrastructure only** — `HomingActionRegistry`, `VertxActionHost`, etc. Application-level types (Doc / Catalogue / Plan / Studio / AppModule subtypes) are *never* `Mutable`. The interface-level marker enforces this categorically.

## 8. Cost — Weighed Complexity

Honest per-dimension breakdown — note the dramatic reduction from interface-level marking:

| Dimension | Cost |
|---|---|
| **Cognitive density** | Low. Marker interfaces are a standard concept; jOntology's classification reads as the framework's existing prose made formal. New readers see the marker and understand the contract immediately. |
| **Blast radius** | **Small.** Five interface edits + ~10 field-bearing record edits = ~15 framework code changes total. Implementations inherit markers automatically. New conformance test in `homing-studio`. |
| **Reversibility** | High. Markers can be removed; the enforcer test can be deleted. Nothing in framework behaviour depends on the markers' presence — they're documentation-shaped. |
| **Authoring tax** | **Zero per new doc/catalogue/plan record** — they inherit `StatelessFunctionalObject` from the framework's interfaces. New record types with fields need one marker selection (~10 seconds, mechanical given the framework's patterns). |
| **Failure mode** | Build-time. Enforcer test fails when a marker contradicts implementation. **Zero new silent runtime-failure modes.** |

Per the [Weighed Complexity doctrine](#ref:doc-wc): the cost shrank by an order of magnitude when interface-level marking replaced per-record marking. ~15 edits, ~100 records propagated, full framework discipline mechanically enforced. The cost is sharply favourable; the benefit is full-coverage classification with downstream getting it for free.

## 9. Decision

**Adopt.** RFC 0013 promotes the Functional Objects doctrine from convention to mechanism using a sibling library that already exists, is already published, and shares the framework's authoring philosophy.

The integration is small, mechanical, additive, and fully-reversible. The benefit is that the framework's discipline becomes mechanically true — not just authored true.

## 10. Implementation order

The interface-level marking strategy collapses the work to a single coherent change set:

1. ✅ **Added `jOntology` dependency** to `homing-core`'s `pom.xml`.
2. ✅ **Marked the five framework spine interfaces** — `AppModule`, `Doc`, `Catalogue`, `Plan`, `Studio` each extend `StatelessFunctionalObject`. ~100 records inherit the marker automatically.
3. ✅ **Marked the field-bearing types** — `Bootstrap`, `DefaultFixtures`, `DefaultRuntimeParams`, `StudioBrand` (`ValueObject`); `Fixtures`, `RuntimeParams`, `Umbrella`, `Entry` (`Immutable`); `CatalogueClosure` (`StatelessFunctionalObject`). Sealed-interface markers (`Immutable` rather than `ValueObject`) chosen for `Umbrella` / `Entry` because interfaces can't satisfy `ValueObject`'s "instance field" requirement.
4. 🟡 **Mark internal infrastructure as `Mutable`** — `HomingActionRegistry`, `SimpleAppResolver`, `VertxActionHost`. **Deferred** — these aren't yet in the conformance test list, so the marker isn't urgent. Will land alongside expanding the conformance scope.
5. ✅ **Added `OntologyConformanceTest`** in `homing-studio`'s test sources. Curated list of 14 verified-clean types; documents follow-ups in the test's class Javadoc.
6. ✅ **Reactor builds + tests green** including the new conformance test.
7. ✅ **Updated [Functional Objects doctrine](#ref:doc-fo)** — added the *Mechanical realisation* section naming jOntology as the enforcement mechanism + cross-references to RFC 0013 and the jOntology repo.
8. 🟡 **Update `create-homing-studio` and `create-homing-component` skills** — **deferred**. Mention that `Doc` / `Catalogue` / `Plan` / `Studio` implementations inherit the marker for free; downstream's own new interfaces should be classified.
9. ⏳ **Release as 0.0.102** — pending broader 0.0.102 work.

### Tensions discovered during implementation (§10.5)

Two specific places where jOntology's contract turned out **stricter** than the Functional Objects doctrine — these emerged during enforcement, not anticipated in the original RFC:

1. **All static methods forbidden by jOntology's `Immutable`** — including `private static` helpers the Functional Objects doctrine explicitly permits as "internal organisation of a single class." Affects: `Bootstrap` (private union/dedup helpers), `DocBrowser.entry(...)`, `Rfc0001PlanData` adapter helpers. Mechanical refactor (drop `static`, methods become instance methods).
2. **`java.util.List/Map/Set` aren't in jOntology's known-immutable set** — any record with a `List<...>` field fails the transitive check, even when the constructor does `List.copyOf`. Affects most field-bearing records. Resolution paths: a custom `Immutable`-marked list wrapper, contribution to jOntology's known-immutable set, or accept these as classification debt.
3. **`StudioBrand.logo`'s `SvgRef<?>` field type isn't yet marked `Immutable`** — one-line fix in `homing-core`; deferred to keep the initial PR scoped.

### What landed in 0.0.102 (initial)

~15 framework edits across:
- `homing-core/pom.xml` (1 line)
- `AppModule.java` (extends clause)
- `homing-studio-base`: `Doc`, `Catalogue`, `Plan`, `Studio`, `Fixtures`, `RuntimeParams`, `Umbrella`, `Entry`, `Bootstrap`, `DefaultFixtures`, `DefaultRuntimeParams`, `StudioBrand`, `CatalogueClosure` (12 marker additions)
- `homing-studio/.../OntologyConformanceTest.java` (new test, ~80 lines)
- `FunctionalObjectsDoc.java` + `.md` (doctrine refresh)
- This RFC's status table

### What remains (incrementally)

- Refactor `Bootstrap` private statics to instance methods → expand conformance list
- Refactor `DocBrowser.entry(...)` and `Rfc0001PlanData` adapter helpers → expand conformance list
- Mark `SvgRef` as `Immutable` → enables `StudioBrand` to enter conformance list
- Decide List immutability story (custom wrapper vs jOntology contribution vs debt)
- Mark internal infrastructure as `Mutable` for completeness
- Update `create-homing-studio` skill

## 11. Why this is the right time

The Functional Objects doctrine landed in 0.0.101 (a few weeks ago). It said *"no public statics anywhere; behaviour belongs on methods of immutable typed objects."* The classification it described — stateless functional object vs dependency-holding object — was exactly what a future tool would mechanically enforce, but no such tool was named.

[`jOntology`](#ref:jontology) shipped the tool. Adopting it now, while the doctrine is still fresh and before more downstream code gets written without classification, is the cheap moment. Every additional class added to the framework without a marker is one more class that needs to be visited later when the enforcer becomes the rule.

The integration path is incremental — marker by marker, module by module — but the destination is named: every framework class declares its classification; the build verifies the declaration; the doctrine is mechanically enforced.

## See also

- [Functional Objects doctrine](#ref:doc-fo) — the principle this RFC mechanises.
- [Weighed Complexity doctrine](#ref:doc-wc) — justifies the per-class authoring tax against the long-term enforcement value.
- [Catalogue-as-Container doctrine](#ref:doc-cc) — the open-set / closed-shape pattern markers extend; jOntology's marker family is itself open-set / closed-shape.
- [jOntology repository](#ref:jontology) — the library being integrated.
