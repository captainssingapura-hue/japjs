# RFC 0014 — Typed Studio Graph

| Field | Value |
|---|---|
| **Status** | Proposed |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-16 |
| **Target release** | 0.0.102 |
| **Scope** | Framework — expose the in-memory object graph composed by `Bootstrap` as a typed, queryable `StudioGraph<S, F>` record. Reuses the registries already built at boot (`CatalogueRegistry`, `DocRegistry`, `PlanRegistry`, `StudioProxyManager`) as the graph's underlying state; adds typed query primitives over them. Becomes the foundation for auto-generated diagrams, cross-doc programmability, live conformance / observability, and the typed doc DSL's computed-content phase. |

---

## 1. Motivation

The framework's `Bootstrap.compose()` already walks the typed studio set at boot to build four registries:

- `CatalogueRegistry` — `Class → Catalogue<?>` lookup, breadcrumb derivation, doc/plan reverse-references.
- `DocRegistry` — `UUID → Doc` lookup; backs the `/doc` action and the typed reference resolution.
- `PlanRegistry` — `Class<Plan> → Plan` lookup; phase + decision ID uniqueness validation at boot.
- `StudioProxyManager` — cross-tree reverse-references for breadcrumb spanning under multi-studio composition (RFC 0011).

The data is there. It's queryable. But it's **buried inside the composed `ActionRegistry`** — the framework's actions (`/catalogue`, `/doc`, `/plan`, `/doc-refs`) read from it, and nothing else does.

This RFC names the missing affordance: **a typed `StudioGraph<S, F>` record that exposes the same data as a queryable, programmable surface**. Multiple high-value features sit downstream of having it:

| Feature | Depends on the graph because |
|---|---|
| Auto-generated catalogue / plan / reference diagrams ([RFC 0013](#ref:rfc-13) §5 deferred work) | A diagram is just a typed projection of the graph |
| Cross-doc programmability (rolled-up Tensions, Compromises, etc.) | Needs to iterate all docs of a kind, walk their typed roles |
| Reverse-citation queries (*"what docs cite this doctrine?"*) | Needs the doc→references reverse index |
| Live conformance / observability beyond build-time tests | Needs a runtime view of the composed framework |
| Search index / sitemap generation | Needs to enumerate every reachable Doc / Catalogue |
| Typed doc DSL's computed-content phase (per the Doc DSL plan, filed under Operations Journeys) | The graph is what computed content reads from |
| Downstream skill bundle generation | Needs all skill docs across all studios |

The framework has *already paid for this work* — every entry in the registries was built at boot. What's missing is the typed query API. This RFC adds it as a small, additive, no-breaking-change layer.

## 2. Design

### 2.1 The `StudioGraph<S, F>` record

```java
public record StudioGraph<S extends Studio<?>, F extends Fixtures<S>>(
        Bootstrap<S, F> bootstrap,
        CatalogueRegistry catalogues,
        DocRegistry docs,
        PlanRegistry plans,
        StudioProxyManager proxies
) implements StatelessFunctionalObject {

    /** Eagerly construct the graph from a Bootstrap's composition. */
    public static <S extends Studio<?>, F extends Fixtures<S>>
            StudioGraph<S, F> of(Bootstrap<S, F> b) {
        // Re-uses Bootstrap.compose()'s registry construction; returns the typed graph
    }

    // Stream-based query primitives (see §4)
    public Stream<Doc> allDocs() { ... }
    public Stream<Catalogue<?>> allCatalogues() { ... }
    public Stream<Plan> allPlans() { ... }
    public List<S> allStudios() { ... }

    // Reverse-navigation
    public List<Doc> docsCiting(Doc target) { ... }
    public List<Catalogue<?>> breadcrumbsFor(Doc doc) { ... }
    public List<Catalogue<?>> breadcrumbsFor(Plan plan) { ... }

    // Typed-kind queries (extensible)
    public <D extends Doc> Stream<D> docsOfKind(Class<D> kind) { ... }

    // Reference walks
    public Stream<DocReference> allDocReferences() { ... }
    public Stream<ExternalReference> allExternalReferences() { ... }
}
```

The graph is a typed record that bundles the four existing registries plus the originating `Bootstrap`. Marked `StatelessFunctionalObject` because:
- All its data was set at construction (the registries are themselves immutable after boot)
- All queries are pure functions of `(graph, query-parameters)`
- No mutation, no caching beyond what the registries already do internally

### 2.2 Construction lifecycle

`Bootstrap.compose()` already builds the registries. Two surfacing options:

| Option | API | Trade-off |
|---|---|---|
| **A** — `Bootstrap.graph()` returns a fresh `StudioGraph` | `bootstrap.graph()` | Each call rebuilds the graph; consumers must cache |
| **B** — `Bootstrap.compose()` returns both `ActionRegistry` + `StudioGraph` | (refactor) | Reuses internal state; tighter integration |
| **C** — `StudioGraph.of(bootstrap)` static factory | `StudioGraph.of(b)` | Functional Objects doctrine forbids static factories on the result type itself — would have to live elsewhere |

**Decision (D1, locked)**: Option A — `Bootstrap.graph()` as an instance method on the record. Per Functional Objects doctrine, factories belong on the producer object, not on a separate utility. The framework already has `Bootstrap.compose()` as the canonical "build the registries"; `Bootstrap.graph()` becomes the parallel "build the queryable view." Internally, both share the same registry-construction path.

### 2.3 Query model — typed streams

All bulk queries return `Stream<...>`. Three reasons:

1. **Composability** — downstream can chain `filter` / `map` / `flatMap` without intermediate collection construction
2. **Laziness** — large frameworks can have hundreds of docs; not all queries need eager materialization
3. **Idiomatic Java** — the Stream API is what every framework-level consumer expects

Specific point queries (single doc, single plan, etc.) return `Optional<X>` or `null` per the existing registry conventions.

### 2.4 What the graph carries vs. what it doesn't

**Carries** (the typed projection of the registries):
- Every `Doc` instance from every studio
- Every `Catalogue<?>` instance (across all studios + harness)
- Every `Plan` instance
- Every `Studio<L0>` instance (from `umbrella().studios()`)
- Every `Entry<?>` reachable from a catalogue's `leaves()`
- Every `Reference` declared by a doc
- Every `Phase` / `Decision` / `Acceptance` / `Objective` / `Task` / `Dependency` / `Metric` reachable via plans
- Cross-tree reverse-references via `StudioProxyManager`

**Does not carry** (intentionally):
- Themes — the `ThemeRegistry` is its own typed surface; integration is a follow-up
- AppModules' internal state — apps are sealed by their own typed contracts
- Action handlers — those live in `ActionRegistry`, separate concern
- Runtime mutable state — the graph is built once at boot and immutable thereafter
- Cross-studio breadcrumb chains beyond the existing `StudioProxyManager` scope

### 2.5 Relation to existing registries

The graph is a **typed façade**, not a parallel data structure. The registries remain the source of truth; the graph is the query layer over them.

```
Bootstrap (record)
├── fixtures: F
└── params: RuntimeParams

Bootstrap.compose() → ActionRegistry   (existing, returns the HTTP routing surface)
Bootstrap.graph()   → StudioGraph<S,F> (new, returns the typed graph surface)

Both methods share the same underlying registries.
```

The two return types are *complementary*: `ActionRegistry` is for serving HTTP traffic; `StudioGraph` is for programmatic introspection.

## 3. What the graph enables

This RFC ships the graph itself + a foundational set of queries. The follow-on work is what consumers build on top.

### 3.1 Auto-generated diagrams (links to [RFC 0013](#ref:rfc-13) §5)

The graph makes the deferred work from RFC 0013 trivial:

```java
public record CatalogueTree(L0_Catalogue<?> root, StudioGraph<?, ?> graph) implements Diagram {
    public List<TreeNode> nodes() {
        // graph.catalogues().subCataloguesOf(root) etc.
    }
}

public record PlanGraph(Plan plan, StudioGraph<?, ?> graph) implements Diagram {
    public List<GraphNode> nodes() { return plan.phases(); }
    public List<GraphEdge> edges() {
        return plan.phases().stream()
                .flatMap(p -> p.dependsOn().stream().map(d -> new GraphEdge(d.phaseId(), p.id())))
                .toList();
    }
}

public record ReferenceGraph(Doc seed, int depth, StudioGraph<?, ?> graph) implements Diagram {
    // Walks forward+reverse via graph.allDocReferences() + graph.docsCiting(...)
}
```

Each of these is a typed projection of the graph. The framework's authoring surface gains *"embed a live diagram of the catalogue tree"* as a one-line operation.

### 3.2 Cross-doc programmability

```java
// "List every doctrine's Tensions section" (when typed doc DSL lands)
graph.docsOfKind(Doctrine.class)
     .flatMap(d -> d.tensions().stream().flatMap(t -> t.items().stream()))
     .toList();

// "Which RFCs cite the Functional Objects doctrine?"
graph.docsCiting(FunctionalObjectsDoc.INSTANCE).stream()
     .filter(d -> d instanceof RFC)
     .toList();

// "All Compromises across case studies"
graph.docsOfKind(CaseStudy.class)
     .flatMap(d -> d.compromises().stream())
     .toList();
```

### 3.3 Live conformance / observability

```java
graph.healthReport()
// Returns: docs with empty summaries, references not cited inline,
// orphan catalogues (subCatalogues unreached from any L0),
// plans with 0% progress + 0 open decisions (suspicious),
// etc.
```

Distinct from build-time conformance tests because it runs **against the live composed bootstrap** — catches issues that only emerge in specific multi-studio deployments.

### 3.4 Search index / sitemap

```java
// Generate a sitemap of every reachable Doc
graph.allDocs()
     .map(d -> new SitemapEntry(CatalogueAppHost.urlFor(d), d.title(), d.summary()))
     .toList();

// Generate a flat-text search index
graph.allDocs()
     .map(d -> new SearchEntry(d.uuid(), d.title(), d.summary(), d.contents()))
     .collect(toSearchIndex());
```

### 3.5 Skill bundle dump

The existing `SkillsCli --target <dir>` dump path could read from the graph instead of `SkillsManifest.ALL` directly, making downstream skills automatically dumpable without manifest edits.

## 4. The query API surface (Phase 1)

Locked surface for the foundation phase:

```java
// Bulk enumeration
Stream<Doc>             allDocs()
Stream<Catalogue<?>>    allCatalogues()
Stream<Plan>            allPlans()
List<S>                 allStudios()

// Point lookups (delegated to registries)
Optional<Doc>           docByUuid(UUID id)
Optional<Catalogue<?>>  catalogueByClass(Class<? extends Catalogue<?>> cls)
Optional<Plan>          planByClass(Class<? extends Plan> cls)

// Reverse navigation (existing reverse-ref indices)
List<Catalogue<?>>      breadcrumbsFor(Doc doc)
List<Catalogue<?>>      breadcrumbsFor(Plan plan)
List<Doc>               docsCiting(Doc target)

// Reference walks
Stream<DocReference>      allDocReferences()
Stream<ExternalReference> allExternalReferences()

// Typed-kind queries (extensible by downstream)
<D extends Doc> Stream<D> docsOfKind(Class<D> kind)

// Catalogue tree walks
Stream<Catalogue<?>>    descendantsOf(Catalogue<?> root)
Stream<Entry<?>>        entriesOf(Catalogue<?> catalogue)
```

Phase 2+ adds more (auto-diagram support, live conformance, etc.) — those land as the consumers materialize.

## 5. Future integration points

| Future feature | How it uses the graph |
|---|---|
| Typed doc DSL (Doc DSL plan Phase 5) | Computed content (`new CatalogueTree(...)`) reads from the graph |
| RFC 0013 deferred diagrams | `Graph`, `Flowchart`, `CatalogueTree`, `PlanGraph` types take a `StudioGraph` |
| RFC 0007 audio cues (future cross-theme query) | *"All cues in all themes"* — graph extension for themes |
| Live observability dashboard (potential future) | Read-only HTTP endpoint exposing graph metrics |
| Skill bundle live re-dump | Skills enumerated from `graph.docsOfKind(Skill.class)` |

The graph is the foundation. Each feature gets a small, focused projection.

## 6. Decisions (locked)

1. **`Bootstrap.graph()` is the canonical entry point** — instance method on the existing Bootstrap record. No separate static factory; doctrine-aligned.
2. **Construction is eager** — `Bootstrap.graph()` builds the registries (or reuses ones already built by `Bootstrap.compose()`) and returns a complete `StudioGraph` immediately. No lazy registry construction.
3. **Construction is idempotent** — calling `Bootstrap.graph()` twice on the same Bootstrap returns equal (record-equality) graphs. Internal registries may be cached or rebuilt; this is an implementation detail.
4. **Query API is Stream-based** — bulk queries return `Stream<X>`; point queries return `Optional<X>`. No List or Set in the public API surface.
5. **Forward navigation via existing typed accessors** — `Doc.references()`, `Catalogue.subCatalogues()`, `Plan.phases()`, etc. No new reflection-based traversal.
6. **Reverse navigation via existing reverse-index registries** — `docHome`, `planHome` on `CatalogueRegistry`, `StudioProxyManager` for cross-tree. No new indices required for Phase 1.
7. **`StudioGraph` is a record** — typed primitive, marked `StatelessFunctionalObject`. Cannot be subclassed; downstream extension is via consuming the graph, not extending it.
8. **`StudioGraph` lives in `homing-studio-base`** — same module as `Bootstrap`. The query primitives don't need anything outside the base module.
9. **The graph is per-Bootstrap** — multi-studio composition is already handled at Bootstrap level; the graph is just the introspectable view of one composed deployment.
10. **No persistence** — the graph is in-memory only, built per JVM. Persistence (for live editing, hot reload, cross-process queries) is out of scope.
11. **Themes are not yet included** — `Theme` and `ThemeRegistry` integration is a follow-up; Phase 1 ships with docs / catalogues / plans / studios / references.
12. **Downstream-added Doc kinds inherit graph queries automatically** — `graph.allDocs()` returns every `Doc`-implementing record, including downstream's. Typed-kind queries (`graph.docsOfKind(Doctrine.class)`) work for any kind class extending `Doc`.

## 7. Cost — Weighed Complexity

| Dimension | Cost |
|---|---|
| **Cognitive density** | Low. The graph is a thin typed façade over four registries that already exist. New readers see one record with stream-method accessors; the underlying data model is unchanged. |
| **Blast radius** | Tiny. One new record (`StudioGraph<S, F>` in `homing-studio-base`), one new method (`Bootstrap.graph()`), no breaking changes to any existing API. |
| **Reversibility** | High. If the abstraction proves wrong, the graph record can be removed without touching the registries. |
| **Authoring tax** | Zero for existing code. New consumer code uses the typed Stream API which Java developers already know. |
| **Failure mode** | Compile-time on misuse (e.g. wrong type parameter to `docsOfKind`); runtime `Optional.empty()` for missing lookups. **Zero new silent-failure modes.** |

Per the [Weighed Complexity doctrine](#ref:doc-wc): the foundation is small (~200 lines), purely additive, and unlocks an entire class of features (auto-diagrams, cross-doc queries, live observability, search) without each having to re-implement the registry-walk plumbing. The cost is sharply favourable.

## 8. Decision

**Adopt.** The graph is a small, additive primitive that the framework has been informally implementing piece-by-piece across the registries; this RFC names the primitive and exposes it cleanly.

## 9. Implementation order

Tracked in the dedicated *Typed Studio Graph* plan (filed under *Journeys → Operations*). Summary:

1. **Phase 1 — Foundation.** Define `StudioGraph<S, F>` record + `Bootstrap.graph()`. Implement the locked Phase-1 query primitives (§4). Add unit tests.
2. **Phase 2 — Reverse navigation.** Reverse-citation walks, breadcrumb queries. Mostly delegated to existing registry methods.
3. **Phase 3 — First auto-diagram.** `CatalogueTree` diagram type taking a `StudioGraph` — realises [RFC 0013](#ref:rfc-13) §5's deferred work.
4. **Phase 4 — Cross-doc programmability.** Doc-of-kind queries (depends on typed doc DSL Phase 2's kind interfaces from the Doc DSL plan filed under *Journeys → Operations*).
5. **Phase 5 — Live conformance / observability.** Health-report queries; runtime health check endpoint (optional).
6. **Phase 6 — Search / sitemap generation.** Surface for SEO + browseable index features.
7. **Phase 7 — Typed doc DSL integration.** Computed content (`new CatalogueTree(StudioCatalogue.INSTANCE)`) reads from the graph; the typed doc DSL's renderer hooks in.

## 10. Why this is the right time

The framework has accumulated four registries piecemeal — Catalogue first, then Doc, then Plan, then StudioProxyManager. Each was added when the immediate consumer (a specific action) needed it. The result is a working but un-unified introspection surface.

Three concurrent strands now want the same data:

- [RFC 0013 §5](#ref:rfc-13) (deferred): auto-generated diagrams need to walk catalogue / plan / reference graphs
- The Doc DSL plan's computed-content phase: computed content needs to read framework data
- The case studies' analytical work increasingly needs *"every doc / catalogue / plan across the framework"* as a queryable surface

Rather than each of those building its own walk, this RFC names the shared primitive. Subsequent work composes onto it rather than reimplementing it.

## See also

- [RFC 0011 — Typed Cross-Tree Reverse References](#ref:rfc-11) — already builds the cross-tree breadcrumb walk; this RFC consumes its output.
- [RFC 0012 — Typed Studio Composition](#ref:rfc-12) — defines `Bootstrap` and the umbrella shape this RFC reads from.
- [RFC 0013 — jOntology Integration](#ref:rfc-13) — the type-classification work the graph is built on top of.
- [Functional Objects doctrine](#ref:doc-fo) — informs the record-based shape and method-on-Bootstrap surfacing.
- [Catalogue-as-Container doctrine](#ref:doc-cc) — the open-set/closed-shape pattern the graph respects.
- [Cross-Studio References Cost Nothing](#ref:csref) — companion case study; the typed object graph is *why* the property in that case study holds.
