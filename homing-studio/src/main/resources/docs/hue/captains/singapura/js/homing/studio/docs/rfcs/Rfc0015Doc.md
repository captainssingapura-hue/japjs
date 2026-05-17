# RFC 0015 — Doc Unification

| Field | Value |
|---|---|
| **Status** | Accepted — In Progress |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-16 |
| **Last updated** | 2026-05-17 — aligned with the now-landed Ontology layer; renamed `DocTree` → `DocTree` throughout |
| **Target release** | 0.0.103 |
| **Implements ontology** | [Doc](#ref:doc-ontology) (eight axioms A1–A12), [Viewer](#ref:viewer-ontology) (ten axioms V1–V10) |
| **Anchored by meta-doctrine** | [Ontology First](#ref:doc-ontfirst) — the ontology entries define the contracts; this RFC realises them in Java |
| **Scope** | Framework — realise the Doc ontology by promoting `Doc` from a single concrete prose-document interface to a sealed family of content-bearing catalogue leaves. Subsumes Plans and AppModule navigables under `Doc` as typed subtypes; carves out `StudioProxy` as the explicit *structural* leaf per Doc's relationship axioms. Introduces `ProxyDoc` to resolve multi-home situations via fresh identity per appearance (Doc A6 mechanism). Introduces `ContentViewer` as the extension point realising the Viewer ontology. Net effect: one sealed sum collapses, four switch statements vanish, the diagnostic and tree-leaf composition pathways stop reinventing per-kind dispatch. |

---

## 0. Relationship to the Ontology layer

This RFC was filed before the Ontology layer existed. The Ontology layer has since landed (Doc, DocTree, Viewer, Studio entries under `Meta → Ontology`), and the Ontology First meta-doctrine now requires every framework primitive to have its ontology entry filed first.

The relationship reverses: the **ontology defines the contract** (axioms); **this RFC implements it** (Java types, registry semantics, runtime behaviour). Specifically:

- [**Doc ontology**](#ref:doc-ontology) A1–A12 are the contract. This RFC realises them as:
  - `DocId` sealed family — realises A1 (identifier kind) and A4 (local uniqueness)
  - `Doc` sealed family (`ProseDoc | PlanDoc | AppDoc | ProxyDoc`) — realises A8 (universal metadata, per-kind body) and the universal shape
  - `ProxyDoc` subtype — realises A6 (single-home invariant via fresh identity)
  - Boot-time validation — realises A5 (leaf placement) and A6 (multi-home enforcement)
  - `DocRegistry` keyed by `DocId` — realises A2 (universality of the identifier)
- [**Viewer ontology**](#ref:viewer-ontology) V1–V10 are the contract. This RFC realises them as:
  - `ContentViewer` interface — realises V1–V2 (kind-identified, paired with AppModule), V6 (URL composition), V10 (StatelessFunctionalObject record shape)
  - `Fixtures.contentViewers()` registry — realises V9 (registration as activation)
  - Server-side dispatch in `DocGetAction` (and `/doc` equivalents per kind) — realises V4 (Doc routing through kind), V7–V8 (read-only, stateless)

The remaining content of this RFC — the design discussion, the migration steps, the implementation order — stays as written. Anywhere this RFC said "we define X to mean…", read instead "we realise the ontology's existing definition of X by introducing…".

## 1. Motivation

The current `Entry<C>` sealed sum has four variants:

```
Entry.OfDoc      (Doc)                     — a prose document
Entry.OfApp      (Navigable<P, M>)         — an AppModule + typed Params
Entry.OfPlan     (Plan)                    — a structured tracker
Entry.OfStudio   (StudioProxy<L0>)         — RFC 0011 cross-tree portal
```

Three of those four variants describe **content the user opens, reads, or uses**. The fourth describes **navigation between trees**. The framework has been treating these as four parallel kinds when really they are two roles played by different shapes:

- **Content leaves** — opens something the user views (Doc / App / Plan)
- **Structural leaves** — relocates the user (StudioProxy)

The asymmetry leaks into the rest of the framework as four-branch switch statements: `CatalogueGetAction.serialize`, `CatalogueRegistry`'s validation, breadcrumb derivation, and every consumer that walks leaves uniformly. Each new leaf kind today requires touching all of them.

Meanwhile, **multi-home** — the same content reachable from multiple places — has been handled implicitly by `docHome.putIfAbsent(...)`: first-registered wins, silently. This becomes incoherent the moment content can legitimately appear in multiple places (e.g. a generated tree that lists docs already living in catalogues).

This RFC fixes both:

1. Collapses the four-branch `Entry` sum into **polymorphism over a sealed `Doc` family**, with the structural carve-out named explicitly.
2. Resolves multi-home by introducing **`ProxyDoc`** — a typed Doc subtype that delegates content to a target while carrying its own identity, home, and breadcrumb context.
3. Registers content-kind-to-viewer-app routing as a typed **`ContentViewer`** extension point — the seam that the upcoming Content Trees ([RFC 0016](#ref:rfc-16)) and the diagnostic-view plug-ins ([RFC 0014](#ref:rfc-14) Phase 1c) both consume.

The work is small (one sealed interface, one carve-out, one Doc subtype family, one extension point) and the result is sharply more general.

## 2. Design

### 2.1 The `CatalogueLeaf` family — structural carve-out

```java
public sealed interface CatalogueLeaf
        extends StatelessFunctionalObject
        permits Doc, StudioProxy { ... }
```

Every leaf is either a content-bearing `Doc` or a structural `StudioProxy` (or any future typed structural leaf). The principle:

> **Catalogue leaves are Docs by default. Exceptions are structural — leaves whose role is navigation or composition rather than viewing.**

`StudioProxy` keeps its current shape (RFC 0011 cross-tree portal). Future structural carve-outs follow the same discipline: only when the leaf's role is genuinely *not viewable content* — external links, search shortcuts, mount points. The default presumption is **Doc**; structural is the explicit exception.

### 2.2 The `Doc` sub-hierarchy

`Doc` becomes a non-sealed-permits interface so downstream studios can add their own Doc kinds. Framework ships four built-in subtypes:

```java
public non-sealed interface Doc extends CatalogueLeaf {
    DocId   id();           // typed identity (see §2.4)
    String  name();         // tile heading
    String  summary();      // tile body
    String  badge();        // category label
    List<Reference> references();
    String  url();          // canonical URL — framework computes for typed kinds
}

// Built-in subtypes (records):
public interface ProseDoc extends Doc { /* uuid-identified prose content */ }
public interface PlanDoc  extends Doc { /* wraps a tracker.Plan */ }
public interface AppDoc   extends Doc { /* wraps an AppModule + typed Params */ }
public record   ProxyDoc(...) implements Doc { /* delegates to a target Doc */ }
```

The existing `ClasspathMarkdownDoc` implements `ProseDoc`. The existing `Plan` interface becomes (or is wrapped by) `PlanDoc`. The existing `Navigable<P, M>` becomes (or is wrapped by) `AppDoc`. Migration is mechanical — each variant already carries the metadata the unified protocol needs.

### 2.3 The five-method contract

What's *universal* across all Doc kinds (the doctrine of "Doc-ness"):

| Method | Purpose |
|---|---|
| `id()` | Typed identity (`DocId`; see §2.4) |
| `name()` | User-facing heading |
| `summary()` | User-facing body description |
| `badge()` | Category label rendered on the tile |
| `references()` | Typed cross-references (every Doc may cite others, regardless of kind) |
| `url()` | Canonical URL the framework opens — computed per subtype |

What's *per-kind* (and rightly so):

- **Content / rendering pipeline** — ProseDoc has classpath markdown; PlanDoc has structured JSON; AppDoc forwards to the bound AppModule. The current `/doc`, `/plan`, and `/app?app=...` endpoint plurality stays — each kind has its own optimised path. The unification is at the *entry-point* level (the catalogue tile), not the rendering machinery.
- **Indexing** — `DocRegistry` becomes a unified registry keyed by `DocId`; internal sub-indexes (`UUID → Doc`, `Class<Plan> → Doc`, etc.) stay.
- **Validation** — each kind has its own validity rules (UUID uniqueness for ProseDoc, params-vs-paramsType compatibility for AppDoc, phase-ID uniqueness for PlanDoc). Stays per-kind.

### 2.4 Typed identity — `DocId`

The four current leaf kinds have different identity models:

| Kind | Identity |
|---|---|
| ProseDoc | `UUID` |
| PlanDoc  | `Class<? extends Plan>` |
| AppDoc   | `(Class<AppModule>, Params)` |
| ProxyDoc | `UUID` (own; not the target's) |

A sealed `DocId` family captures the asymmetry without flattening it:

```java
public sealed interface DocId {
    record ByUuid             (UUID id)                   implements DocId {}
    record ByClass            (Class<?> cls)              implements DocId {}
    record ByClassAndParams   (Class<?> cls, Object params) implements DocId {}
}
```

`DocRegistry.resolve(DocId)` dispatches on the variant. `DocReference` lookups work uniformly for any kind. The existing prose `Doc.uuid()` method becomes the `ByUuid` accessor; the 50+ existing prose docs are not touched.

### 2.5 `ProxyDoc` — multi-home via fresh identity

The core insight: **each appearance of content gets its own identity, so no UUID has two homes**. The "multi-home" problem dissolves because no content has multiple homes — each home has its own UUID, each UUID has exactly one home, the existing single-home invariant is preserved.

```java
public record ProxyDoc(
        UUID            uuid,                    // own identity, not the target's
        Doc             target,                  // the actual content
        Optional<String> titleOverride,
        Optional<String> summaryOverride,
        Optional<String> categoryOverride
) implements ProseDoc {
    public DocId id()                       { return new DocId.ByUuid(uuid); }
    public String name()                    { return titleOverride.orElse(target.name()); }
    public String summary()                 { return summaryOverride.orElse(target.summary()); }
    public String badge()                   { return categoryOverride.orElse(target.badge()); }
    public List<Reference> references()     { return target.references(); }
    // content body delegates to target (see §2.6 resolution)
}
```

`ProxyDoc` is a `Doc` like any other — full citizen of the registry, own home, own breadcrumb. The target is invisible at the wire level: clients see what looks like a regular doc with the proxy's UUID and the proxy's framing.

**Override scope** — title, summary, category are author-overridable per appearance (the same content can be framed as "API Reference" in one tree and "How to use the API" in another). Content body is **never** overridable (defeats the point). References are always from the target (changing what a doc cites would mislead readers).

### 2.6 Server-side proxy resolution

`DocGetAction.execute(query)` resolves the proxy chain transparently:

```
GET /doc?id=<proxy-uuid>
  ↓
DocRegistry.resolve(ByUuid(proxy-uuid)) → ProxyDoc instance
  ↓
walk delegation chain (single-hop only — see invariants §3)
  ↓
return target's body content with proxy's id + name + summary + category
```

The viewer doesn't need to know it's a proxy. One request, no extra round trip, no client-side resolution logic. Breadcrumb derives from the registry's home for the **proxy's** UUID — which is its own catalogue/tree location — not the target's.

### 2.7 The `ContentViewer` extension point

Different Doc subtypes need different viewer AppModules:

| Subtype | Viewer | URL shape |
|---|---|---|
| ProseDoc | `DocReader` | `/app?app=doc-reader&doc=<uuid>` |
| PlanDoc | `PlanAppHost` | `/app?app=plan&id=<class-fqn>` |
| AppDoc | (forwards to the bound AppModule) | `/app?app=<simpleName>&<params>` |
| ProxyDoc | (delegates to target's viewer, with proxy UUID) | `/app?app=<target-viewer>&id=<proxy-uuid>` |

The `Doc.url()` method computes its own URL — each subtype encapsulates the viewer mapping. For external pluggability, the framework adds:

```java
public interface ContentViewer extends StatelessFunctionalObject {
    String          kind();                          // discriminator
    AppModule<?, ?> app();                           // the AppModule rendering this kind
    String          urlFor(String contentId);        // URL composition
    default String  summary() { return ""; }         // for introspection
}
```

Registered via `Fixtures.contentViewers()` default — same pattern as the `StudioGraphView` extension point ([RFC 0014](#ref:rfc-14) Phase 1c). Framework ships viewers for prose / plan / app; downstream adds viewers for diagrams, code, tables, 3D graphs, etc., each as one record.

### 2.8 What `Entry` becomes

`Entry<C>` keeps its CRTP host-binding role and becomes a thin wrapper:

```java
public record Entry<C extends Catalogue<C>>(CatalogueLeaf leaf) { ... }

// Factory preserved for ergonomics:
public static <C> Entry<C> of(C host, CatalogueLeaf leaf) { ... }
```

The four-branch switch statements in `CatalogueGetAction.serialize`, `CatalogueRegistry`'s leaf validation, etc. become polymorphic dispatch on `CatalogueLeaf` (or on the concrete Doc subtype, as needed). Adding a future leaf kind doesn't require editing existing switches.

## 3. Invariants

Locked behavioural invariants of the unified Doc family:

1. **One UUID, one home.** Each `DocId` registers to exactly one catalogue/tree location. Multi-home is achieved via `ProxyDoc` — never by registering the same id twice. Validation rejects duplicate registrations as an explicit boot error.
2. **ProxyDoc chain depth is exactly 1.** A `ProxyDoc.target` must be a non-proxy `Doc`. Proxy-of-proxy is rejected at registration. Loses nothing in practice; eliminates all cycle-detection complexity.
3. **Override scope is metadata-only.** `ProxyDoc` overrides `name` / `summary` / `category`. Content body and references come from the target unconditionally.
4. **Doc body content is read-only.** Doc protocol is for *reading*, not authoring; no Doc subtype gains write APIs.
5. **`CatalogueLeaf` carve-outs are explicit.** New non-Doc leaf kinds must be added to the `permits` clause of the sealed `CatalogueLeaf` interface — the framework refuses implicit non-Doc leaves.
6. **`StudioProxy` stays exactly as it is.** RFC 0011's cross-tree reverse-references are unchanged in shape and semantics; only the type hierarchy moves it under `CatalogueLeaf` alongside `Doc`.
7. **Identity collisions across kinds are rejected.** A `ByUuid` and a `ByClass` cannot collide (different variants), but two `ByUuid` with the same UUID, or two `ByClass` with the same class, are boot errors.

## 4. Migration

The change is structural but mechanical. Existing code paths:

| Today | After |
|---|---|
| `record SomeDoc() implements ClasspathMarkdownDoc` | unchanged — `ClasspathMarkdownDoc extends ProseDoc` |
| `Entry.of(this, SomeDoc.INSTANCE)` | unchanged — factory preserved |
| `Entry.of(this, new Navigable<>(App, params, ...))` | becomes `Entry.of(this, new AppDoc(App, params, name, summary))` |
| `Entry.of(this, somePlan)` | becomes `Entry.of(this, new PlanDoc(somePlan))` |
| `Entry.of(this, new StudioProxy(...))` | unchanged |
| `CatalogueGetAction.serialize` switch on `Entry.OfDoc` etc. | switch on `CatalogueLeaf` instead — `Doc` polymorphic, `StudioProxy` special |
| `DocReference` resolves by UUID | resolves by `DocId` — same UUID variant for prose |
| Breadcrumb derivation | unchanged — proxies have their own home registration |

The 50+ existing `ClasspathMarkdownDoc` declarations are untouched. The four catalogue-leaf sites change from four Entry variants to one polymorphic call. Roughly a day of mechanical refactoring, mostly compile-driven.

## 5. What this enables

| Downstream feature | How it uses this unification |
|---|---|
| **Content Trees** ([RFC 0016](#ref:rfc-16)) | Tree leaves carry `ContentRef(kind, id)` resolved through `ContentViewer`; tree-generated `ProxyDoc`s carry deterministic UUIDs for stable URLs |
| **2D / 3D graph view plug-ins** (RFC 0014 P1d / P1e) | New view AppModules ship as `ContentViewer` registrations; the diagnostic catalogue surfaces them automatically |
| **Featured / curated subsets** | A "featured docs" catalogue lists `ProxyDoc`s pointing at canonical docs, reframed for the curation context |
| **Cross-studio doc inclusion** | One studio's catalogue can include a `ProxyDoc` of another studio's doc, keeping the canonical home intact |
| **Multi-language / multi-audience framings** | The same content body rendered with different titles/summaries per audience tree |
| **Search results pages** | Generated `DataCatalogue` / `ContentTree` of `ProxyDoc`s of matching content; each result has its own URL for citation |

## 6. Cost — Weighed Complexity

| Dimension | Cost |
|---|---|
| **Cognitive density** | Moderate. Readers learn: `CatalogueLeaf = Doc \| StudioProxy`; `Doc` has four built-in subtypes; `ProxyDoc` delegates. Mostly nameable concepts; no clever generics beyond what's already in `Entry<C>`. |
| **Blast radius** | Wide but shallow. Touches `Entry`, `Doc`, `CatalogueGetAction`, `CatalogueRegistry`, `Bootstrap` — but each touch is mechanical. No new conceptual model for catalogue authors. |
| **Reversibility** | Medium. The `CatalogueLeaf` family and `ProxyDoc` are unobtrusive additions; reverting them is a roll-back of one PR. The migration of Plan/App entries into `PlanDoc`/`AppDoc` is the larger commitment. |
| **Authoring tax** | Negative. Authors gain access to `ProxyDoc` for the multi-home case (currently impossible cleanly). Existing authoring is unchanged. |
| **Failure mode** | Boot-time error on identity collisions, proxy cycles, missing viewers. **Reduces** silent-failure surface — `docHome.putIfAbsent` first-wins becomes an explicit error. |

Per [Weighed Complexity](#ref:doc-wc): the unification removes four parallel kinds without forcing them into one; preserves typed authoring discipline; eliminates a class of latent silent bugs (silent multi-home); unlocks two downstream RFCs ([RFC 0016](#ref:rfc-16) Content Trees, [RFC 0014](#ref:rfc-14) view plug-ins). Sharply favourable.

## 7. Decisions (locked)

1. **`CatalogueLeaf` is sealed; `Doc` and `StudioProxy` are the initial permits.** Structural carve-outs require explicit framework changes; no implicit non-Doc leaves.
2. **`Doc` is non-sealed (permits open).** Downstream studios can add Doc kinds without framework changes. Constrained only by the five-method contract.
3. **Multi-home via `ProxyDoc`, not via flags or URL params.** No `owner: true/false` flag; no `from=` URL param; no provenance grammar. Each appearance is its own typed Doc with its own identity.
4. **`ProxyDoc` chain depth = 1.** No proxy-of-proxy. Simplifies validation; loses nothing in practice.
5. **Server-side proxy resolution in `DocGetAction`.** Client / viewer never sees the proxy mechanism. One request per content.
6. **Override scope is metadata.** `ProxyDoc` overrides display fields only. Content body and references are target-defined.
7. **`ContentViewer` extension point lives on `Fixtures`.** Default returns the framework's built-in three (prose, plan, app); downstream overrides to add more. Same pattern as `StudioGraphView`.
8. **`DocRegistry` becomes typed-identity (`DocId`).** Single unified registry; internal sub-indexes per `DocId` variant.
9. **The existing `Doc.uuid()` accessor stays for `ProseDoc`** as the `ByUuid` accessor. No churn for the 50+ existing prose docs.
10. **`Entry<C>` keeps its CRTP host-binding role** — the typed compile-time guarantee that an entry belongs to a specific catalogue scope. Just wraps `CatalogueLeaf` instead of the four-branch sum.
11. **Boot-time validation is strict.** Identity collisions, proxy cycles, missing viewers, malformed References — all rejected at registration, not at first hit.
12. **No retroactive renaming.** "Doc" stays "Doc" even though its meaning broadens. The term is already in users' mouths; the broader meaning subsumes the narrow one cleanly enough.

## 8. Decision

**Adopt.** The unification names a structure the framework has been informally implementing in four parallel code paths. Naming it once collapses the duplication, opens the seam for typed extension, and resolves the silent multi-home behaviour into an explicit author choice.

## 9. Implementation order

Tracked in a dedicated *Doc Unification* plan (to be filed under *Journeys → Operations*). Summary:

1. **Phase 1 — `CatalogueLeaf` sum + `Doc` sub-hierarchy.** Define interfaces; mark existing `ClasspathMarkdownDoc` as `ProseDoc`. No behavioural change.
2. **Phase 2 — `DocId` + unified `DocRegistry`.** Lift identity from `UUID` to `DocId`. Strict-mode collision validation. Migrate `PlanRegistry`'s class-keyed home indexing into `DocRegistry` (or keep separate but unified at the query surface).
3. **Phase 3 — Migrate `Entry.OfApp` and `Entry.OfPlan` to `AppDoc` / `PlanDoc`.** Collapse the four-branch switch in `CatalogueGetAction.serialize`.
4. **Phase 4 — `ProxyDoc` + auto-resolution.** Add the proxy subtype; extend `DocGetAction` to walk the single-hop delegation.
5. **Phase 5 — `ContentViewer` extension point + `Fixtures.contentViewers()`.** Register framework defaults; document the seam.
6. **Phase 6 — Deprecate `Entry.OfDoc` / `Entry.OfPlan` / `Entry.OfApp`** in favour of `Entry.of(host, leaf)`. Keep the old factories for one release; remove in the following.

Phases are independently shippable. Phase 1 alone is a no-op refactor; phases 2-6 each unlock specific downstream features.

## 10. Why this is the right time

Three concurrent strands now want the same unification:

- **Content Trees** ([RFC 0016](#ref:rfc-16)) — data-authored hierarchies need a typed extension point for content kinds and a clean multi-home model. Both fall out of this RFC.
- **Diagnostic view plug-ins** ([RFC 0014](#ref:rfc-14) P1d / P1e) — the 2D / 3D / future visualisation views need to register as content kinds without framework changes. `ContentViewer` is the seam.
- **Curated subsets across studios** — multi-studio compositions want to feature each other's docs in their own catalogues without duplicating content. `ProxyDoc` is the answer.

Without this RFC, each of those strands re-implements its own variant of "different content kinds" and "different framings of the same content." Naming it once, here, lets all three compose cleanly.

## See also

- [Ontology — Doc](#ref:doc-ontology) — the contract this RFC implements (eight axioms).
- [Ontology — Viewer](#ref:viewer-ontology) — the contract for the `ContentViewer` extension point.
- [Ontology — DocTree](#ref:doctree-ontology) — the structural container Docs are placed into.
- [Meta-Doctrine — Ontology First](#ref:doc-ontfirst) — the anchoring principle that says ontology contracts come before RFC implementation.
- [RFC 0005 — Catalogue + Doc Browser](#ref:rfc-5) — defines the original Doc + Catalogue shapes this RFC generalises.
- [RFC 0005-ext2 — Typed level hierarchy](#ref:rfc-5-ext2) — the typed-leaf scoping that `Entry<C>` preserves.
- [RFC 0011 — Typed Cross-Tree Reverse References](#ref:rfc-11) — `StudioProxy`, the structural-leaf carve-out's prototype.
- [RFC 0014 — Typed Studio Graph](#ref:rfc-14) — the diagnostic-view extension point (`StudioGraphView`) that this RFC's `ContentViewer` parallels.
- [RFC 0016 — Content Trees](#ref:rfc-16) — the companion RFC; consumes `ContentViewer` and `ProxyDoc` directly.
- [Functional Objects doctrine](#ref:doc-fo) — informs the record-based shape of each Doc subtype.
- [Catalogue-as-Container doctrine](#ref:doc-cc) — the structural-vs-content separation this RFC clarifies.
- [Explicit over Implicit doctrine](#ref:doc-eoi) — the `DocId` sealed family is a worked example.
