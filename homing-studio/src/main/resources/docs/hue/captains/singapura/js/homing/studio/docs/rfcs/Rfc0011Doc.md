# RFC 0011 — Typed Entry-Host Binding + Studio Proxy

| Field | Value |
|---|---|
| **Status** | Proposed |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-13 |
| **Target release** | 0.0.101 |
| **Scope** | Framework — `Catalogue` gets CRTP self-bound; `Entry` gets a host type parameter; typed `StudioProxy<S>` + `Entry.OfStudio<C, S>` variant + `StudioProxyManager` reverse-ref; breadcrumb augmentation in `CatalogueRegistry`. Every existing catalogue record gains one type argument (mechanical retype). |

---

## 1. The motivating problem

The multi-studio launcher [RFC 0010](#ref:rfc-10) composes multiple source studios onto a single server via an umbrella `L0` catalogue whose `Navigable` tiles point at each source studio's `L0` page. It works for navigation, but RFC 0010 §5 explicitly accepted one trade-off: **the breadcrumb chain doesn't span the umbrella → source-studio boundary**.

The cause is structural: a source studio (e.g. `SkillsHome`) is its own `L0` — has no `parent()`. The typed-level breadcrumb walk correctly stops there. When the user lands on a doc inside `SkillsHome`, the chain shows `📜 Skills › <doc>`, not `🌐 Homing Studios › 🤖 Tooling › 📜 Skills › <doc>`.

The shape of the gap, named: **one tree's root is another tree's leaf.** The framework currently has no typed primitive expressing that. This RFC closes the gap by extending the typed-levels machinery — same idiom the framework already commits to — to the entry-host relationship.

## 2. Design — the CRTP cascade

The keystone is `Entry<C extends Catalogue>` — the entry knows its host at the type level. That forces `Catalogue` to expose its own type as a self-bound (CRTP):

```java
public sealed interface Catalogue<Self extends Catalogue<Self>>
        permits L0_Catalogue, L1_Catalogue, …, L8_Catalogue {

    String name();
    default String summary() { return ""; }
    default String badge()   { return "CATALOGUE"; }
    default String icon()    { return ""; }
    default List<? extends Catalogue<?>> subCatalogues() { return List.of(); }
    default List<Entry<Self>>            leaves()        { return List.of(); }
}
```

Every level interface picks up two type parameters — `P` (parent's `Self`), `Self` (own):

```java
public non-sealed interface L0_Catalogue<Self extends L0_Catalogue<Self>>
        extends Catalogue<Self> {
    @Override default List<? extends L1_Catalogue<Self, ?>> subCatalogues() { return List.of(); }
}

public non-sealed interface L1_Catalogue<P extends L0_Catalogue<P>,
                                          Self extends L1_Catalogue<P, Self>>
        extends Catalogue<Self> {
    P parent();
    @Override default List<? extends L2_Catalogue<Self, ?>> subCatalogues() { return List.of(); }
}
// … through L8.
```

`subCatalogues()` returns wildcard-Self because children's `Self` types vary heterogeneously; what matters is that each child's *parent* type is bound to `Self`, which the type system enforces.

`Entry<C>` is parameterised by host:

```java
public sealed interface Entry<C extends Catalogue<C>>
        permits OfDoc, OfApp, OfPlan, OfStudio {

    record OfDoc<C extends Catalogue<C>, D extends Doc>
                 (D doc)                              implements Entry<C> {}
    record OfApp<C extends Catalogue<C>, A extends AppModule<?, ?>>
                 (Navigable<A, ?> nav)                implements Entry<C> {}
    record OfPlan<C extends Catalogue<C>, P extends Plan>
                  (P plan)                            implements Entry<C> {}
    record OfStudio<C extends Catalogue<C>, S extends L0_Catalogue<S>>
                    (StudioProxy<S> proxy)            implements Entry<C> {}

    // Generic factories — the `host` arg is a type witness for inference, not stored.
    static <C extends Catalogue<C>, D extends Doc>
           Entry<C> of(C host, D doc)                { return new OfDoc<>(doc); }
    static <C extends Catalogue<C>, A extends AppModule<?, ?>>
           Entry<C> of(C host, Navigable<A, ?> nav)  { return new OfApp<>(nav); }
    static <C extends Catalogue<C>, P extends Plan>
           Entry<C> of(C host, P plan)               { return new OfPlan<>(plan); }
    static <C extends Catalogue<C>, S extends L0_Catalogue<S>>
           Entry<C> of(C host, StudioProxy<S> proxy) { return new OfStudio<>(proxy); }
}
```

Authoring at a leaf-site:

```java
public record DoctrineCatalogue()
        implements L1_Catalogue<StudioCatalogue, DoctrineCatalogue> {

    @Override public List<Entry<DoctrineCatalogue>> leaves() {
        return List.of(
                Entry.of(this, FirstUserDoc.INSTANCE),
                Entry.of(this, DualAudienceSkillsDoc.INSTANCE),
                // ...
        );
    }
}
```

The compiler refuses `Entry.of(SomeOtherCatalogue.INSTANCE, …)` here — the entry's host type wouldn't match the declared `List<Entry<DoctrineCatalogue>>`. The misplaced-entry foot-gun becomes a compile error.

## 3. The studio proxy + manager

### 3.1 `StudioProxy<S>`

```java
public record StudioProxy<S extends L0_Catalogue<S>>(
        S source,            // typed instance — not Class<?>
        String name,
        String summary,
        String badge,
        String icon) {}
```

The proxy is **data, not a `Catalogue`** — typed-levels invariants on the parent chain stay untouched. The proxy lives inside an `Entry.OfStudio<C, S>` variant; the host `C` and source `S` are both compile-time facts.

### 3.2 `StudioProxyManager`

Built at boot by scanning every registered catalogue's `leaves()` for `OfStudio` entries:

```java
public final class StudioProxyManager {

    public record Hosting<S extends L0_Catalogue<S>>(
            StudioProxy<S> proxy, Catalogue<?> host) {}

    private final Map<Class<? extends L0_Catalogue<?>>, Hosting<?>> hostings;

    /** Walks all registered catalogues' leaves(); builds the reverse-ref. */
    public static StudioProxyManager scan(Collection<? extends Catalogue<?>> catalogues);

    @SuppressWarnings("unchecked")
    public <S extends L0_Catalogue<S>> Hosting<S> hostingFor(Class<S> sourceClass) {
        return (Hosting<S>) hostings.get(sourceClass);
    }

    public boolean isHosted(Class<? extends L0_Catalogue<?>> sourceClass) {
        return hostings.containsKey(sourceClass);
    }
}
```

The cast in `hostingFor` is **safe by registration invariant** — entries are stored under `Class<S>` keys derived from the proxy's own type witness, so retrieval under the same `Class<S>` recovers the same `Hosting<S>`. Standard Java idiom (matches `Class.cast()` / `Class.getAnnotation(Class<A>)`). The cast is localised inside the manager; callers never see it.

### 3.3 Boot validation

- Each source L0 has *exactly one* hosting. Duplicate registration → boot error.
- The source L0 named by a proxy must itself be registered. Closure check.
- Proxy's `source` field is compile-enforced as `L0_Catalogue<S>` by the generic bound.

## 4. The breadcrumb walk

`CatalogueRegistry` accepts a `StudioProxyManager` and augments `breadcrumbsForDoc(uuid)` / `breadcrumbsForPlan(class)` / `breadcrumbs(class)`:

```
breadcrumbsForDoc(uuid):
  1. typed-walk (existing): doc → containing catalogue → … → source L0
     chain = [source L0, …, containing]
  2. source = chain.first()
  3. manager.isHosted(source.getClass())?
     no  → return chain
     yes → host = manager.hostingFor(source.getClass()).host()
           umbrella = breadcrumbs(host.getClass())   // typed-walk again
           // The proxy occupies the source L0's slot in the displayed chain;
           // drop the duplicate label.
           return umbrella ++ chain.dropFirst()
```

For `MigrateFrom0_0_11SkillDoc` inside `SkillsHome` under `ToolingStudioCategory`:

| Step | Chain |
|---|---|
| Typed-walk in source studio | `[SkillsHome, MigrateFrom…Doc]` |
| `isHosted(SkillsHome.class)` → yes, host = `ToolingStudioCategory` | |
| Typed-walk for host | `[MultiStudioHome, ToolingStudioCategory]` |
| Suppress source L0 (proxy displaces it), concat | `[MultiStudioHome, ToolingStudioCategory, MigrateFrom…Doc]` |
| Render with icons | `🌐 Homing Studios › 🤖 Tooling › Migrate from 0.0.11` |

The proxy's `icon() + name()` replaces the source L0's own label in the rendered crumb.

## 5. What the type system enforces (and what stays runtime)

| Invariant | Before | After |
|---|---|---|
| Entry's host catalogue matches its container | Convention only (runtime, never checked) | **Compile error if mismatched** |
| Sub-catalogue child's parent type matches its container | Compile error (RFC 0005-ext2) | Compile error (preserved) |
| Sub-catalogue list is heterogeneous-but-bounded by Self | Runtime (depth check) | Compile-enforced via wildcard `?`-Self pattern |
| Studio proxy's source is an `L0_Catalogue` | n/a | **Compile error if not L0** |
| Studio proxy's source is *registered* in the catalogue list | n/a | Boot-time runtime check |
| One source L0 has at most one hosting | n/a | Boot-time runtime check |
| Breadcrumb chain spans umbrella → source studio | Doesn't (RFC 0010 §5 trade-off) | **Yes, automatically, for all sub-pages** |

## 6. Migration

Every catalogue record gains one type argument. Mechanical sweep — per the [Weighed Complexity](#ref:wc) doctrine's nuance on blast radius, *mechanical retypes in a properly engineered codebase are cheap*: each diff is one line, IDE inference handles inference, no semantic decisions per file.

```java
// Before
public record StudioCatalogue() implements L0_Catalogue { ... }
public record DoctrineCatalogue() implements L1_Catalogue<StudioCatalogue> { ... }
public record ArchitectureRfcsCatalogue() implements L2_Catalogue<RfcsCatalogue> { ... }

// After
public record StudioCatalogue() implements L0_Catalogue<StudioCatalogue> { ... }
public record DoctrineCatalogue()
        implements L1_Catalogue<StudioCatalogue, DoctrineCatalogue> { ... }
public record ArchitectureRfcsCatalogue()
        implements L2_Catalogue<RfcsCatalogue, ArchitectureRfcsCatalogue> { ... }
```

`leaves()` return types tighten:

```java
@Override public List<Entry<DoctrineCatalogue>> leaves() {
    return List.of(
            Entry.of(this, FirstUserDoc.INSTANCE),
            // ...
    );
}
```

`subCatalogues()` returns pick up wildcard for heterogeneous children:

```java
@Override public List<? extends L1_Catalogue<StudioCatalogue, ?>> subCatalogues() { ... }
```

### Inventory

| Module | Catalogue records |
|---|---|
| `homing-studio-base` | (interfaces only) |
| `homing-studio` | ~14 (Studio + Doctrine + RFCs family + Journeys family + Building Blocks + Releases) |
| `homing-skills` | 1 (`SkillsHome`) |
| `homing-demo` | 5 (`DemoStudio` + `MultiStudioHome` + 3 categories) |
| `homing-studio-base/test` | `CatalogueRegistryTest` fixtures (~7) |
| **Total** | **~27 records, ~1–2 lines each** |

Single commit, single review pass, single compile. The cascade is mechanical because the framework's typed idiom is already established — every catalogue author already knows the L<N> pattern; one more type argument is just *more of the same*.

## 7. Trade-offs and the multi-attach principle

### 7.1 What we accept

| Cost | Why acceptable |
|---|---|
| Every catalogue record's `implements` clause grows by one type argument | Matches the framework's typed-everything idiom. IDE auto-completes the cascade. Once one author has read one catalogue, the pattern is internalised. |
| `subCatalogues()` returns wildcard-Self for children | Required for heterogeneous-children lists. Preserves what we actually need (typed parent enforcement). Just the "no two children share a Self" weakening, which has no use case here. |
| One `@SuppressWarnings("unchecked")` cast in `StudioProxyManager.hostingFor()` | Localised single line. Safe by registration invariant. Callers see only typed `Hosting<S>`. Standard typed-heterogeneous-map idiom. |
| Host catalogue in `Hosting<S>` carried as `Catalogue<?>` (raw wildcard) | We use `host` only to walk its breadcrumbs, never to call typed methods on it. The host's own type witness lives in the catalogue tree it sits in. |

### 7.2 The general multi-attach principle (stated, scope deferred)

> A leaf attaches **directly** to exactly one catalogue. Any second or third attachment goes through a **proxy** that re-displays the leaf and explicitly states which catalogue acts as the *canonical home* for breadcrumb purposes.

Studios are the **only** case this RFC ships, because L0s have no direct-attach option (they can't be leaves of themselves). For Doc / Plan, the same principle applies but is *currently unused* — `DocProxy<D>` / `PlanProxy<P>` records and matching `Entry` variants can be added in a future RFC when an actual multi-attach case surfaces. Not in scope here.

## 8. Open questions — none

Design fully locked in §2–§4.

## 9. Decision

**Adopt.** The framework's posture is typed-everything; this RFC extends it consistently to the entry-host relationship. The mechanical migration cost is dwarfed by the structural correctness gained.

## 10. Implementation order

1. **`Catalogue<Self>` CRTP** — retype the sealed interface base. Every level interface adds `Self`. Compile breaks immediately across the repo — track to find every catalogue record needing migration.
2. **`Entry<C>`** — retype as generic-per-variant + new factories.
3. **`StudioProxy<S>` + `Entry.OfStudio<C, S>`** — new types.
4. **`StudioProxyManager`** — scan + typed reverse-ref + `Hosting<S>`.
5. **`CatalogueRegistry` accepts manager** — augment `breadcrumbs*()` methods. Constructor signature grows by one arg.
6. **`CatalogueGetAction.serialize()`** — `OfStudio` case emits a studio-kind card with URL pointing at the source L0.
7. **Migrate `homing-studio` catalogues** (14) — mechanical retypes.
8. **Migrate `homing-skills` and `homing-demo` catalogues** — same.
9. **Convert `homing-demo`'s three categories** from `Navigable` tiles to `StudioProxy` tiles.
10. **`CatalogueRegistryTest` fixtures** — retype.
11. **New test `StudioProxyManagerTest`** — verify the typed reverse-ref + breadcrumb augmentation.
12. **Doctrine update** — one-paragraph note in [Catalogue-as-Container](#ref:doc-cc) on the multi-attach principle.
13. **Build green across the reactor.**

Single commit; partial state wouldn't compile. Estimated effort: focused session.

## 11. Why this is the right time

RFC 0005-ext2 typed the catalogue tree's *vertical* structure (parent chain). RFC 0009 typed its *visual* structure (badge, icon). RFC 0010 used both to compose multiple trees onto one server, but documented the cross-boundary breadcrumb as a deliberate trade-off.

This RFC closes that trade-off by typing the *horizontal* structure — entries know their host. After it lands, the entire `(catalogue, entry, level, proxy, breadcrumb)` quintuple is compile-time consistent. No future RFC needs to fix this layer again.
