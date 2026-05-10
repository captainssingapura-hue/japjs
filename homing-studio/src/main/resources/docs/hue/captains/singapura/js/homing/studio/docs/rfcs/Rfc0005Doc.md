# RFC 0005 — Typed Catalogue Containers

| Field | Value |
|---|---|
| **Status** | **Draft** — open for iteration. Implementation pending. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-10 |
| **Last revised** | 2026-05-10 |
| **Honours** | [Catalogues as Containers](#ref:cc) — the doctrine this RFC operationalises. |
| **Relates to** | [RFC 0001](#ref:rfc-1) (typed-Linkable model identity rests on); [RFC 0004](#ref:rfc-4) + [0004-ext1](#ref:rfc-4-ext1) (typed Doc identity + managed references). |
| **Addresses** | The catalogue kit's existing type-safety + extensibility issues — flat 7-field `CatalogueTile`, hand-built URL strings at every call site, redundant `TileStyle` enum, hardcoded brand label, no path to downstream tile kinds. |
| **Target phase** | Phase 1 — bounded scope, mechanical migration. |

---

## 0. Status notice

This RFC turns the **Catalogues as Containers** doctrine into a concrete data model. It supersedes the existing `CatalogueAppModule` / `CatalogueData` / `CatalogueSection` / `CatalogueTile` shape with a strictly typed, structurally minimal one. Implementation deferred to the next session; this draft is the contract.

---

## 1. Motivation

The doctrine establishes:

1. A catalogue is merely a typed container of docs or sub-catalogues.
2. Every catalogue has an intrinsic identity.
3. All linking flows through identity.
4. The set of catalogues is open; the shape is closed.
5. The catalogue holds no opinion about its presentation.

The current kit violates each of these in concrete ways:

| Doctrine commitment | Current kit's violation |
|---|---|
| Container of typed entries | `CatalogueTile` is a 7-field flat record holding raw URL strings; no typed reference to the entry it points at |
| Intrinsic identity | Catalogue identity is conflated with `simpleName()` (class-derived) and replicated as URL-string substitutions across every parent |
| Identity-only linking | Every parent constructs `"/app?app=" + Child.INSTANCE.simpleName()` by hand; URL contract is duplicated, not enforced |
| Open set, closed shape | `CatalogueSection.TileStyle` is a closed enum; downstream cannot add tile kinds without patching the kit |
| No presentation directives | Tiles carry `tileStyle`, `badgeClass`, `featured`, `icon` — all rendering directives baked into catalogue data |

The root cause is the same in every row: the catalogue carries presentation data and untyped wire fragments instead of a typed structural skeleton. RFC 0004 and 0004-ext1 fixed the analogue for Docs (typed UUID identity + typed `Reference`); this RFC applies the same fix to Catalogues.

---

## 2. Proposed model

### 2.1 The `Catalogue` interface

```java
public interface Catalogue {
    /** Human-readable label. Identifies the catalogue to readers; not presentation. */
    String name();

    /** Optional one-line summary used in parent listings. Default empty. */
    default String summary() { return ""; }

    /** Ordered children — Docs and / or sub-Catalogues, intermixed. */
    List<Entry> entries();
}
```

That's the entire contract. Three methods, two required (`name`, `entries`), one optional (`summary`).

What's deliberately absent:

- **No identity method.** The Java class itself *is* the identity. `MyCatalogue.class` is canonical; the class FQN is the wire-stable handle. No UUID, no slug, no name-as-identity.
- **No URL field.** Wire URLs are derived by the framework at render time from the class identity.
- **No presentation fields.** No icon, no badge, no tile shape, no CSS class hint, no featured flag. The catalogue describes structure; the renderer decides looks.
- **No sections.** Entries are a flat ordered list.

### 2.2 The `Entry` sealed type

Doctrine sentence 1 says *"docs or sub-catalogues"*. The conceptual model: **a "doc" spans a spectrum from static (markdown shipped on classpath) to living (a richer app whose page is its own thing — Plan trackers, DocBrowser).** Both are content the user navigates *to*; the difference is richness. Static and living docs are two implementation kinds of the same conceptual entry.

The implementation has three sealed subtypes — `OfDoc` (static), `OfCatalogue` (sub-tree), `OfApp` (living). The third closes the gap between the doctrine's conceptual model and the practical need to list non-static-doc apps in a catalogue.

```java
public sealed interface Entry {

    record OfDoc(Doc doc)                 implements Entry {}   // static — RFC 0004 Doc
    record OfCatalogue(Catalogue cat)     implements Entry {}   // sub-tree
    record OfApp(NavigableApp app)        implements Entry {}   // living — opt-in AppModule

    /** Convenience factories for clean call sites. */
    static Entry of(Doc doc)              { return new OfDoc(doc); }
    static Entry of(Catalogue cat)        { return new OfCatalogue(cat); }
    static Entry of(NavigableApp app)     { return new OfApp(app); }
}
```

Three sealed subtypes. The renderer pattern-matches exhaustively:

```java
switch (entry) {
    case Entry.OfDoc(Doc d)             -> renderDocTile(d);
    case Entry.OfCatalogue(Catalogue c) -> renderCatalogueTile(c);
    case Entry.OfApp(NavigableApp a)    -> renderAppTile(a);
}
```

The compile-time `switch` exhaustiveness check guarantees the renderer covers every entry kind.

### 2.2.1 The `NavigableApp` marker

`NavigableApp` is the opt-in marker an `AppModule` implements to declare itself catalogue-listable. It's not pure-marker — it has a tiny display contract (mirroring `Catalogue`'s `name()` + `summary()`) so the catalogue tile has authoritative intrinsic display data without falling back to brittle class-name humanisation:

```java
public interface NavigableApp extends AppModule<? extends NavigableApp> {
    /** Human-readable label shown in catalogue tiles. */
    String name();

    /** Optional one-line summary shown beneath the name. Default empty. */
    default String summary() { return ""; }
}
```

`extends AppModule<…>` constrains opted-in things to be actual AppModules (not arbitrary types) and reuses `simpleName()` for URL derivation:

```
/app?app=<navigableApp.simpleName()>
```

Plan trackers (Rfc0001Plan, Rfc0002Plan, …), DocBrowser, future custom-shape apps all opt in by implementing `NavigableApp` and providing `name()` + optionally `summary()`. The opt-in is the entire integration cost; nothing else changes about the AppModule.

### 2.2.2 Future direction — unifying static + living

The static-or-living framing opens a future RFC's worth of work: collapsing static `Doc` and living `NavigableApp` under a single `Doc` interface family, with `MarkdownDoc` and `LivingDoc` as the two implementation kinds. Out of scope for RFC 0005 — Entry's three sealed subtypes are sufficient today and naturally extend that direction tomorrow.

### 2.3 Class-as-identity

Catalogues are stateless singletons (records with no fields), instantiated as a single `INSTANCE`:

```java
public record DoctrineCatalogue() implements Catalogue {
    public static final DoctrineCatalogue INSTANCE = new DoctrineCatalogue();

    @Override public String name()    { return "Doctrines"; }
    @Override public String summary() { return "The rules that hold the design together."; }

    @Override public List<Entry> entries() {
        return List.of(
                Entry.of(PureComponentViewsDoc.INSTANCE),
                Entry.of(MethodsOverPropsDoc.INSTANCE),
                Entry.of(ManagedDomOpsDoc.INSTANCE),
                Entry.of(OwnedReferencesDoc.INSTANCE),
                Entry.of(CatalogueContainerDoc.INSTANCE)
        );
    }
}
```

The class `DoctrineCatalogue` is the identity. Anywhere another artifact wants to reference this catalogue, it imports the class and uses `DoctrineCatalogue.INSTANCE`. Renaming the class (refactor) renames every reference in lock-step (IDE handles both); moving the class moves it cleanly. Identity *is* the typed Java reference — there is no separate identity field that could drift.

The wire URL is derived by the framework at render time:

```
/app?app=<simple-name-derived-from-class>
```

Per RFC 0001, every catalogue is also a `Linkable` (it's served as an `AppModule`); `Linkable.simpleName()` provides the URL token; the framework owns the URL contract. **No catalogue ever constructs a URL.**

### 2.4 What this replaces

| Old type | Status |
|---|---|
| `CatalogueAppModule<M>` | Replaced — `Catalogue` plus a thin `CatalogueAppModule` adapter that wraps any `Catalogue` as an `AppModule` |
| `CatalogueData` | Deleted — fields collapsed into `Catalogue.name()` / `summary()` / `entries()` |
| `CatalogueSection` | Deleted — every existing catalogue has exactly one section; the abstraction earned nothing |
| `CatalogueSection.TileStyle` | Deleted — entry shape determines render style; no per-section config needed |
| `CatalogueTile` (record + 4 factories) | Deleted — entries *are* typed Doc/Catalogue references; no wrapper record needed |
| `CatalogueCrumb` | **Survives** — breadcrumb path is a separate display concern, not part of catalogue structure |
| `CatalogueJson` | Replaced by a thinner serializer over the new shape |
| `CatalogueRenderer.java` + `.js` | Rewritten to consume the new shape; presentation logic unchanged in spirit |

### 2.5 Where presentation lives

Two render concerns, both belong to the renderer (not to `Catalogue`):

1. **The catalogue page itself** — header (with brand + crumbs), title (from `name()`), subtitle (from `summary()`), the entry list rendered as tiles.
2. **A catalogue's tile when listed by a parent** — small card derived from `name()` + `summary()` plus a link to the catalogue's page.

Both use the same `name()` + `summary()` data; the tile vs. page distinction is the renderer's call. Tile shape, colour, icon, badge — all renderer-side, theme-driven (RFC 0003 territory when the time comes). The catalogue data is identical in every render context.

### 2.6 Optional minimal `name()` default

We could provide a default `name()` derived from the class name (`JourneysCatalogue` → `"Journeys"`). **Not in this RFC** — explicit `name()` is required, because:

- Class-name humanisation is brittle (`BuildingBlocksCatalogue` → `"BuildingBlocks"` → needs camel-case split + special handling).
- Explicit `name()` is six characters of overhead per catalogue and removes a category of subtle bugs.
- Localisation (future) needs an explicit hook anyway.

---

## 3. Conformance

**Minimal**: no separate base. The four boot-time validations from §6.1 are enforced by `CatalogueRegistry`'s constructor — a single test that constructs the registry from the studio's explicit catalogue list catches every violation.

```java
@Test
void catalogueRegistryConstructsCleanly() {
    var brand      = new StudioBrand("Homing · studio", StudioCatalogue.class);
    var docs       = DocRegistry.from(appResolver);
    var catalogues = List.of(StudioCatalogue.INSTANCE, /* … */);

    assertDoesNotThrow(() -> new CatalogueRegistry(brand, docs, catalogues));
}
```

If the constructor throws (cycle, multi-parent, missing sub-catalogue, missing doc, blank name, null entries), the test fails with the registry's clear message. No per-catalogue dynamic test factory is needed — there's no manual JS to scan, no per-request failure mode (everything is pinned at boot).

Folds into the existing `StudioDocConformanceTest` (or sits alongside as a one-method test class). Either is fine; ~15 LoC.

---

## 4. Trade-offs and rejected alternatives

### 4.1 Class-as-identity vs. UUID

Considered: give each Catalogue a `UUID uuid()` like Docs.

**Rejected.** Catalogues are AppModules — they're already typed Java classes registered with `SimpleAppResolver`. The class FQN is already a stable, refactor-safe handle. Adding a UUID would create a parallel identity surface with no extra benefit (catalogue URLs would still need either UUID or simpleName). Doc has a UUID because Doc isn't an AppModule and lacks an inherent class-identity URL contract; Catalogue is.

### 4.2 No `title()` separate from `name()`

Considered: `title()` (rendered as `<h1>`) + `name()` (used in tile listings).

**Rejected.** The doctrine commits to minimalism. Two strings doing nearly-identical jobs invites inconsistency ("Doctrines" vs. "All Doctrines"). One `name()`; the renderer uses it everywhere.

### 4.3 Sealed Entry vs. heterogeneous list

Considered: `List<Object>` with `instanceof` dispatch in the renderer; or `List<Linkable>` (since Doc-via-DocReader and Catalogue are both Linkable-equivalent).

**Rejected.** Sealed `Entry` gives compile-time exhaustiveness and self-documents the two permitted entry kinds. Doc isn't a Linkable, so `List<Linkable>` is wrong; `List<Object>` is type erasure.

### 4.4 Sections / named groupings

Considered: `List<Section>` with each section having a name + a list of entries.

**Rejected.** All four current catalogues have exactly one section. The abstraction has earned nothing. If a future catalogue genuinely needs grouped display, the natural answer is to make each group a sub-catalogue (entries become `OfCatalogue(GroupX), OfCatalogue(GroupY)`); the recursion does the job, no separate `Section` type needed.

### 4.5 Per-entry presentation fields

Considered: `Entry` carries an icon, badge, override-label, "featured" flag.

**Rejected.** The doctrine bans presentation directives in catalogue data. Display per entry derives from the entry's own intrinsic data (Doc has `title()` + `summary()` + `category()`; Catalogue has `name()` + `summary()`) and from the renderer / theme. Per-entry decoration would resurrect the same problem the new `Reference` model deliberately avoided.

### 4.6 Default `name()` from class

Considered: `default String name() { return humanise(getClass().getSimpleName()); }`.

**Rejected** for v1 (per §2.6 above). Brittle for compound names; explicit is six characters; can be added later non-breakingly.

### 4.7 Wrap `CatalogueAppModule` over the existing kit, keep both

Considered: ship `Catalogue` alongside the existing kit, leave the existing kit running, migrate consumers one at a time.

**Rejected.** Two parallel models for the same job is exactly the duplication this RFC is solving. Migration is bounded (4 catalogues), the kit + concrete consumers can land in one atomic change.

---

## 5. Doctrine implications

This RFC is the operationalisation of the **Catalogues as Containers** doctrine. The doctrine's five commitments map one-to-one:

| Doctrine commitment | RFC mechanism |
|---|---|
| Container, not page | `Catalogue` interface = `name() + summary() + entries()`; nothing else |
| Identity, not name | The Java class is identity; no derived UUID, no name-as-key |
| Identity-only linking | Parent catalogue references children by typed Java class; framework derives URLs |
| Open set, closed shape | `interface Catalogue` (open) + sealed `Entry` (closed protocol) + conformance test (mechanical enforcement) |
| Render-agnostic | Catalogue carries no presentation fields; renderer + theme own all display |

A new conformance check (`CatalogueContainerConformanceTest`) makes commitment #4 mechanical. The other four commitments are structurally enforced by the type system.

---

## 6. Resolved decisions

- **D1 — Adapter shape: single `CatalogueAppHost`, explicit downstream registration.** A single `CatalogueAppHost` AppModule serves every catalogue (`/app?app=catalogue&id=<class-fqn>`). Catalogues are NOT auto-discovered — downstream studios pass the explicit list of every catalogue they want served, parallel to how `AppModule`s are registered today. Explicit registration prevents runtime surprises (no "I forgot to register but it almost worked"; no classpath-walk magic).
- **D2 — Brand label: injectable, provided alongside the registry.** The studio brand is a per-installation configuration object passed at boot, not a per-catalogue field, not a hardcoded default. Provided alongside (or as part of) the catalogue registry construction:
  ```java
  var catalogues = new CatalogueRegistry(
      new StudioBrand("Homing · studio", "studio-catalogue"),  // label + home-app simpleName
      List.of(StudioCatalogue.INSTANCE, DoctrineCatalogue.INSTANCE, …)
  );
  StudioBootstrap.start(port, apps, catalogues);
  ```
  Brand carries (at minimum) display label + home-app reference. Renderer reads from the registry at render time — no per-catalogue indirection.
- **D3 — Breadcrumb derivation: tree-parent index built at boot.** `CatalogueRegistry` walks every registered catalogue's `entries()` once during construction; builds a `Map<Class<? extends Catalogue>, Catalogue>` from each child class to its parent. Breadcrumbs at render time are an O(depth) walk up that map.

  **Implication — strict-tree v1**: the index assumes each catalogue has at most one parent in the registered set. Constructor throws if the same `Catalogue` appears as an entry in two different parents. The doctrine permits multi-parent in principle; v1 implementation enforces strict-tree for breadcrumb determinism. A future RFC could extend the URL with a `&from=<parent>` hint to support multi-parent.

---

## 6.1 Boot-time validations the registry performs

Folding D1 + D3 into a single boot-time pass over the explicit catalogue list:

1. **Strict tree** — each catalogue appears as an entry in at most one parent. Multi-parent throws `IllegalStateException` at registry construction.
2. **No cycles** — walking `entries()` from each catalogue terminates. Cycles throw.
3. **Closure completeness** — every `Entry.OfCatalogue(c)` references a `c` that's in the registered list. An entry pointing at an unregistered catalogue throws.
4. **Doc reachability** — every `Entry.OfDoc(d)` references a `d` that's in the studio's `DocRegistry` (the existing RFC 0004 registry). Catalogues can't link to off-classpath / private docs.

All four checks are **boot-time fail-fast**. No runtime surprises — by the time the server accepts requests, the catalogue tree is verified.

---

## 7. Migration order (proposed)

1. **Framework primitives** — `Catalogue` interface, `Entry` sealed type. New types in `homing-studio-base/.../base/app/`.
2. **Adapter** — `CatalogueAppHost` AppModule (or one-AppModule-per-catalogue, per §6 OPEN). Wires the new shape into the framework's app resolution.
3. **Catalogue registry** — boot-time walk like `DocRegistry`, used for breadcrumb derivation + conformance.
4. **Bootstrap wiring** — `StudioBootstrap` builds the catalogue registry; brand-label moves to bootstrap config.
5. **Renderer rewrite** — `CatalogueRenderer.java` + `.js` consume the new shape; pattern-match on `Entry` subtypes; render entries via a thin per-type display layer (renderer derives display from `Doc.title/summary` and `Catalogue.name/summary`).
6. **Migrate four concrete catalogues** — `StudioCatalogue`, `JourneysCatalogue`, `DoctrineCatalogue`, `BuildingBlocksCatalogue`. Each becomes a record implementing `Catalogue` with `name()` + `summary()` + `entries()`.
7. **Delete deprecated types** — `CatalogueAppModule`, `CatalogueData`, `CatalogueSection` (+ `TileStyle`), `CatalogueTile`, the old `CatalogueJson`. Old factory methods on tiles go too.
8. **Conformance** — `CatalogueContainerConformanceTest` (or fold into `DocConformanceTest`).
9. **Tracker** — `Rfc0005Steps` / `PlanData` / `Plan` / `Step`.
10. **Doc updates** — `CatalogueKitDoc.md` rewritten for the new shape; `BootstrapAndConformanceDoc.md` lists the new conformance base.

Each step compiles + tests pass before moving on. The 4-catalogue migration (step 6) is bounded — touching one at a time keeps PR diffs small.

---

## 8. Effort estimate

- Framework primitives + adapter + registry: **~2 hours**.
- Renderer rewrite (Java + JS): **~1.5 hours**.
- Per-catalogue migration (4 × ~15 min): **~1 hour**.
- Deletions of old types: **~30 minutes**.
- Conformance: **~30 minutes**.
- Tracker + doc updates: **~45 minutes**.

**Total: ~6 hours.** Roughly one working session.

**Net code change: substantial reduction.** Removes `CatalogueData` + `CatalogueSection` + `CatalogueTile` (3 records, ~80 LoC), `TileStyle` enum, `CatalogueAppModule` (~60 LoC), the per-catalogue tile-construction boilerplate (~10 LoC × 4 = 40 LoC). Adds `Catalogue` (~10 LoC), `Entry` (~15 LoC), `CatalogueAppHost` (~30 LoC), conformance (~30 LoC). Net: **~−120 LoC**.

---

## 9. Out of scope

- **Themed renderers (RFC 0003 territory).** The default renderer's CSS is theme-driven (colour); structural variation is RFC 0003.
- **Search / filter on catalogues.** The doctrine's boundary clause: a catalogue with built-in search isn't a catalogue, it's a different app (DocBrowser).
- **Live data catalogues.** Catalogues are static structural Java data, evaluated at request time. Streaming / live updates aren't in the contract.
- **Multi-language `name()` / `summary()`.** Localisation hook is a future addition; v1 is single-language.
- **Default `name()` from class name.** Per §4.6, can land later non-breakingly.

---

## 10. Revision log

- **2026-05-10** — Initial draft. Captures the data model fall-out from the **Catalogues as Containers** doctrine: minimal `Catalogue` interface (`name + summary + entries`), sealed `Entry` (`OfDoc | OfCatalogue`), class-as-identity, no sections, no per-tile fields, deletion of `CatalogueData / Section / Tile / TileStyle`. Three open questions on adapter shape, brand label, and breadcrumb derivation.
- **2026-05-10** — Open questions resolved. Single `CatalogueAppHost` with explicit downstream registration (D1); brand as injectable component provided alongside registry (D2); tree-parent index at boot with strict-tree enforcement (D3). Added §6.1 covering the four boot-time validations the registry performs (strict tree, no cycles, closure completeness, doc reachability) — fail-fast at boot, no runtime surprises. RFC ready for execution.
- **2026-05-10** — Resolved second-pass questions. Entry expanded to three sealed subtypes (`OfDoc | OfCatalogue | OfApp`) plus `NavigableApp` marker (β-shape: `name()` + default `summary()`); §2.2 reframed with the static-or-living conceptual model. Doctrine sentence 1 unchanged — "doc" spans static (markdown) to living (Plan trackers, DocBrowser). `CatalogueCrumb` survives as renderer-side display data only; never on `Catalogue`. Conformance reduced to a single registry-construction test (no per-Catalogue dynamic factory) — boot-time validations are mechanically enforced by `CatalogueRegistry`; the test pins success in CI. RFC ready for execution; Phase 1 starting.
