# Catalogue Kit

A typed structural container for organizing the studio's docs, sub-catalogues, and other navigable apps. Per **RFC 0005** and the **Catalogues as Containers** doctrine ([#ref:cc](#ref:cc)), a catalogue is *only* identity + ordered typed entries — no URLs, no presentation directives, no per-tile config.

**Where**: `homing-studio-base/.../base/app/`

- `Catalogue.java` — the interface (`name()` + `summary()` + `entries()`).
- `Entry.java` — sealed: `OfDoc(Doc) | OfCatalogue(Catalogue) | OfApp(NavigableApp)`.
- `NavigableApp.java` — opt-in marker for AppModules that are catalogue-listable (Plan trackers, DocBrowser, etc.).
- `CatalogueAppHost.java` — single shared AppModule that serves every registered catalogue at `/app?app=catalogue&id=<class-fqn>`.
- `CatalogueGetAction.java` — `/catalogue?id=<class-fqn>` returning fully-resolved JSON.
- `CatalogueRegistry.java` — boot-time registry; performs four §6.1 validations (strict tree, no cycles, closure completeness, doc reachability).
- `StudioBrand.java` — per-installation brand label + home-app reference.
- `CatalogueHostRenderer.java` + `.js` — shared renderer.

---

## A catalogue is a stateless record

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

- **Identity** = the implementing Java class (`DoctrineCatalogue.class`). No UUID, no slug, no separate identity field. The class FQN is the wire-stable handle; renames are caught at compile time.
- **Display data** = `name()` + optional `summary()`. That's it. No icon, no badge, no tile shape, no CSS class — those are renderer-side, not catalogue-side.
- **Children** = `List<Entry>`. Flat ordered list. No sections, no per-section render style.

---

## Entry — three sealed kinds

```java
public sealed interface Entry {
    record OfDoc(Doc doc)              implements Entry {}   // static markdown (RFC 0004)
    record OfCatalogue(Catalogue cat)  implements Entry {}   // sub-tree
    record OfApp(NavigableApp app)     implements Entry {}   // living "doc" (richer app)
}
```

The renderer pattern-matches exhaustively over all three subtypes; `switch` exhaustiveness is checked at compile time.

Conceptually (per RFC 0005 §2.2): "doc" spans a spectrum from **static** (markdown shipped on classpath, served as a `Doc` record per RFC 0004) to **living** (a richer app whose page is its own thing — a Plan tracker, a DocBrowser). Both are content the user navigates *to*; the difference is richness.

---

## NavigableApp — opt-in marker for living docs

An AppModule that wants to be catalogue-listable implements `NavigableApp`:

```java
public record Rfc0001Plan() implements PlanAppModule<Rfc0001Plan>, NavigableApp {

    @Override public String name()    { return "RFC 0001 Plan"; }
    @Override public String summary() { return "App Registry & Typed Navigation tracker."; }

    // … existing PlanAppModule implementation …
}
```

Two-method contract: `name()` (display label) + default `summary()`. The framework derives the URL via the AppModule's inherited `simpleName()`. Pure marker would have made the catalogue tile depend on class-name humanisation (brittle); the tiny display contract gives authoritative intrinsic display data.

---

## Registry + StudioBrand — explicit registration at boot

```java
StudioBootstrap.start(8080,
    List.of(                                  // explicit AppModule list
        CatalogueAppHost.INSTANCE,
        DocReader.INSTANCE,
        DocBrowser.INSTANCE,                  // also a NavigableApp
        Rfc0001Plan.INSTANCE, Rfc0001Step.INSTANCE
        // … every NavigableApp + their child apps
    ),
    List.of(                                  // explicit catalogue list
        StudioCatalogue.INSTANCE,
        DoctrineCatalogue.INSTANCE,
        JourneysCatalogue.INSTANCE,
        BuildingBlocksCatalogue.INSTANCE
    ),
    new StudioBrand("Homing · studio", StudioCatalogue.class)   // brand alongside
);
```

Per D1, catalogues are registered explicitly — no auto-discovery, no classpath magic, no "almost registered" surprise at runtime. The four registry validations run at construction:

1. **Strict tree** — each catalogue appears as an entry in at most one parent.
2. **No cycles** — DFS detects any cycle.
3. **Closure completeness** — every `Entry.OfCatalogue` references a registered catalogue.
4. **Doc reachability** — every `Entry.OfDoc` references a doc in the `DocRegistry`.

Failure throws at boot with a clear message naming the offending catalogue. No runtime surprises.

---

## What this kit replaces

The legacy `CatalogueAppModule` / `CatalogueData` / `CatalogueSection` / `CatalogueTile` / `CatalogueRenderer` shape (per-catalogue AppModule with hand-built URLs and a `tileStyle` enum) has been deleted. The new kit is strictly smaller, strictly typed, and strictly faithful to the doctrine.

| Old | New |
|---|---|
| `CatalogueAppModule<M>` (interface per catalogue) | `CatalogueAppHost` (one shared AppModule) |
| `CatalogueData(kicker,title,subtitle,crumbs,sections,footer)` | `Catalogue.name() + summary() + entries()` |
| `CatalogueSection.TileStyle` (PILL/CARD enum) | (deleted — entry kind drives render) |
| `CatalogueTile(href,label,desc,icon,badge,…)` (7 fields, half null per use) | `Entry` sealed (3 subtypes, each focused) |
| `"/app?app=" + Foo.INSTANCE.simpleName()` (hand-built URL string) | `CatalogueAppHost.urlFor(FooCatalogue.class)` (or never construct — server pre-resolves) |

---

## See also

- [Catalogues as Containers (doctrine)](#ref:cc) — the foundational rules this kit operationalises.
- [RFC 0005](#ref:rfc-5) — the design rationale + full migration history.
- [Bootstrap & Conformance](#ref:bac) — `StudioBootstrap.start(...)` registration; `StudioCatalogueConstructsTest` is the conformance gate.
- [Atoms — StudioElements](#ref:atoms) — the rendering primitives the catalogue host uses.
