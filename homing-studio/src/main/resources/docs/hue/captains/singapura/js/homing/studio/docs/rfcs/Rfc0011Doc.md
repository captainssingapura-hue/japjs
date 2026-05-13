# RFC 0011 — Studio Proxy + Typed Reverse-Ref

| Field | Value |
|---|---|
| **Status** | Proposed |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-13 (revised same day after [Weighed Complexity](#ref:wc) re-cost) |
| **Target release** | 0.0.101 |
| **Scope** | Framework — a typed `StudioProxy<S>`, a new `Entry.OfStudio` variant, a `StudioProxyManager` reverse-ref, and breadcrumb augmentation in `CatalogueRegistry`. **No CRTP cascade, no migration of existing catalogues.** ~120 framework lines + 30 demo lines. |

---

## 1. The motivating problem

The multi-studio launcher [RFC 0010](#ref:rfc-10) composes multiple source studios onto a single server via an umbrella `L0` catalogue whose `Navigable` tiles point at each source studio's `L0` page. It works for navigation, but RFC 0010 §5 explicitly accepted one trade-off: **the breadcrumb chain doesn't span the umbrella → source-studio boundary**.

The cause is structural: a source studio (e.g. `SkillsHome`) is its own `L0` — has no `parent()`. The typed-level breadcrumb walk correctly stops there. When the user lands on a doc inside `SkillsHome`, the chain shows `📜 Skills › <doc>`, not `🌐 Homing Studios › 🤖 Tooling › 📜 Skills › <doc>`.

The shape of the gap, named: **one tree's root is another tree's leaf.** The framework has no typed primitive expressing that. This RFC introduces one.

## 2. Design

### 2.1 What this RFC does *not* do

An earlier draft proposed CRTP-self-bounding `Catalogue<Self extends Catalogue<Self>>` plus `Entry<C extends Catalogue<C>>` — the entry's host as a type parameter. That design was re-costed under the [Weighed Complexity](#ref:wc) doctrine: it taxes every catalogue author forever (perpetual authoring tax), requires migrating ~27 existing records (large blast radius, near-zero reversibility), and the only invariant it would buy — *an entry can't appear in the wrong catalogue's leaves() list* — is a vanishingly rare bug instance. Bad trade.

We instead keep `Catalogue` and `Entry` as-is, and add the typed primitive only where the actual problem lives: the proxy and its reverse-ref.

### 2.2 `StudioProxy<S>` — typed source-of-truth

```java
public record StudioProxy<S extends L0_Catalogue>(
        S source,           // the wrapped tree's root, as a typed instance
        String name,
        String summary,
        String badge,
        String icon) {}
```

The proxy is **data**, not a `Catalogue`. The typed-levels invariants are untouched. Authoring site is fully type-checked: `new StudioProxy<>(SkillsHome.INSTANCE, …)` resolves to `StudioProxy<SkillsHome>`. No `Class<?>` juggling at construction.

### 2.3 `Entry.OfStudio` — a new leaf variant

`Entry` gains one variant — non-generic at the sum-type level, generic only inside the variant record:

```java
public sealed interface Entry permits OfDoc, OfApp, OfPlan, OfStudio {
    record OfStudio(StudioProxy<?> proxy) implements Entry {}

    static <S extends L0_Catalogue> Entry of(StudioProxy<S> proxy) {
        return new OfStudio(proxy);
    }
}
```

`OfStudio` carries `StudioProxy<?>` — the type parameter is captured-but-erased at the variant level. The factory preserves `S` at the call site for inference. Renderers pattern-match `case OfStudio(StudioProxy<?> p) -> …` and read display fields from `p` directly; `p.source().getClass()` recovers the source class for reverse-ref lookup.

### 2.4 `StudioProxyManager` — typed reverse-ref

Built at boot by scanning every registered catalogue's `leaves()` for `OfStudio` entries:

```java
public final class StudioProxyManager {

    public record Hosting<S extends L0_Catalogue>(
            StudioProxy<S> proxy, Catalogue host) {}

    private final Map<Class<? extends L0_Catalogue>, Hosting<?>> hostings;

    /** Walks all registered catalogues' leaves(); builds the reverse-ref. */
    public static StudioProxyManager scan(Collection<? extends Catalogue> catalogues);

    @SuppressWarnings("unchecked")
    public <S extends L0_Catalogue> Hosting<S> hostingFor(Class<S> sourceClass) {
        return (Hosting<S>) hostings.get(sourceClass);
    }

    public boolean isHosted(Class<? extends L0_Catalogue> sourceClass) {
        return hostings.containsKey(sourceClass);
    }
}
```

The `@SuppressWarnings` on `hostingFor` is **safe by registration invariant** — entries are stored under `Class<S>` keys derived from the proxy's own type witness, so retrieval under the same `Class<S>` recovers the same `Hosting<S>`. Standard Java idiom (matches `Class.cast()` / `Class.getAnnotation(Class<A>)`). The cast is localised inside the manager; callers never see it.

### 2.5 Boot validation

- Each source L0 has *exactly one* hosting. Duplicate registration → boot error.
- The source L0 named by a proxy must itself be registered. Closure check.
- Proxy's `source` is compile-enforced as `L0_Catalogue` by the generic bound.

## 3. The breadcrumb walk

`CatalogueRegistry` accepts a `StudioProxyManager` (new constructor arg). Its `breadcrumbsForDoc(uuid)` / `breadcrumbsForPlan(class)` / `breadcrumbs(class)` methods augment chains for any page whose source L0 root is hosted:

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

The proxy's `icon() + name()` appears as the umbrella's last crumb (`🤖 Tooling`) — the source L0's own label is suppressed since the proxy semantically *is* it.

## 4. What the type system enforces (and what stays runtime)

| Invariant | Enforcement |
|---|---|
| Studio proxy's source is an `L0_Catalogue` | **Compile error** if not (generic bound) |
| Proxy's source field is a typed instance, not `Class<?>` | **Compile** (the record's component type is `S`) |
| `StudioProxyManager.hostingFor(Class<S>)` returns `Hosting<S>` | **Compile** at the API boundary (cast is internal) |
| Studio proxy's source is *registered* in the catalogue list | Boot-time runtime check |
| One source L0 has at most one hosting | Boot-time runtime check |
| Breadcrumb chain spans umbrella → source studio | Automatic at request time |

Three new things are compile-enforced; two boot-time checks cover the integration seam; one behaviour (the cross-boundary breadcrumb) is automatic without any author needing to opt in.

## 5. Migration

**None.** No existing catalogue records change. `Catalogue` interface unchanged. `Entry` sum gains one variant — additive, every existing switch becomes non-exhaustive at compile time and gets one new case (the only mechanical edit anywhere outside the new code itself).

Specifically:
- `CatalogueGetAction.serialize()`'s `Entry` switch — add `OfStudio` case (~12 lines)
- `CatalogueRegistry` constructor — accept manager (one extra arg)
- That's it.

The umbrella demo (`homing-demo`) gets a small rewrite: the three categories' `leaves()` swap their `Entry.of(new Navigable<>(...))` constructions for `Entry.of(new StudioProxy<>(...))`. ~30 lines of demo-side change.

## 6. Trade-offs and the multi-attach principle

### 6.1 What we accept

| Cost | Why acceptable |
|---|---|
| One `@SuppressWarnings("unchecked")` cast in `StudioProxyManager.hostingFor()` | Standard typed-heterogeneous-map idiom. Localised to a single line, safe by registration invariant, callers see only typed `Hosting<S>`. |
| The host catalogue in `Hosting<S>` is `Catalogue` (raw) | We use `host` only to walk its breadcrumb chain — never to call typed methods on it. The host's own type witness lives in the catalogue tree it sits in. |
| The source L0's label is suppressed in the rendered chain (in favour of the proxy's label) | The proxy represents the source in the umbrella context. Two consecutive crumbs with the same text reads as a bug. |

### 6.2 The general multi-attach principle (stated, scope deferred)

> A leaf attaches **directly** to exactly one catalogue. Any second or third attachment goes through a **proxy** that re-displays the leaf and explicitly states which catalogue acts as the *canonical home* for breadcrumb purposes.

Studios are the **only** case this RFC ships, because L0s have no direct-attach option (they can't be leaves of themselves). For Doc / Plan, the same principle applies but is *currently unused* — `DocProxy<D>` / `PlanProxy<P>` records and matching `Entry` variants can be added in a future RFC when an actual multi-attach case surfaces. Not in this scope.

## 7. Why this design over the heavier alternatives

Three runner-up designs were considered and discarded:

| Alternative | Why discarded |
|---|---|
| **Registry container map** (`Map<Class<? extends L0_Catalogue>, List<Catalogue>>` parameter to `CatalogueRegistry`) | Less typed than `StudioProxyManager` (Map values are raw `List<Catalogue>`, no `Hosting<S>` shape). Same per-deployment config burden. No advantage. |
| **Sticky URL parent param** (every action's Params + every renderer propagates `parent=…`) | Bookmarks must include the param or lose context; ~80 lines touching 3 actions + 3 renderers; URL noise everywhere. |
| **CRTP cascade — `Catalogue<Self>` + `Entry<C>`** | Perpetual authoring tax across every catalogue, ~27-record migration, ~355 lines, near-zero reversibility. Buys only one rare bug class as compile-time error. Re-costed against [Weighed Complexity](#ref:wc) — bad trade. |

The chosen design ships the same user-visible behaviour as the CRTP version (cross-boundary breadcrumb, type-safe proxy API) for ~120 framework lines, zero migration, and a single localised `@SuppressWarnings` cast.

## 8. Open questions — none

Design is fully locked in §2–§3.

## 9. Decision

**Adopt.**

## 10. Implementation order

1. **`StudioProxy<S>` record** — new file in `homing-studio-base/.../app/`.
2. **`Entry.OfStudio` variant + factory** — additive, no existing call sites broken.
3. **`StudioProxyManager`** — scan + typed reverse-ref + `Hosting<S>`.
4. **`CatalogueRegistry`** — constructor accepts manager; `breadcrumbs*()` methods augment chains for hosted L0s.
5. **`CatalogueGetAction.serialize()`** — `OfStudio` case emits a studio-kind card with URL pointing at the source L0.
6. **Demo migration** — convert `MultiStudioHome`'s 3 categories from `Navigable` tiles to `StudioProxy` tiles.
7. **Unit test** — `StudioProxyManagerTest`: typed lookup, isHosted, hostingFor returns correct typed Hosting; boot-time validations reject duplicates and unregistered sources.
8. **Unit test** — `CatalogueRegistryTest`: augmented breadcrumbs include the umbrella chain when source is hosted, are unchanged when source is unhosted.
9. **Build green across the reactor.**

Estimated effort: half a session for framework + manager + tests; another half for demo migration + live verification. Single commit.

## 11. Why this is the right time

RFC 0005-ext2 typed the catalogue tree's *vertical* structure (parent chain). RFC 0009 typed its *visual* structure (badge, icon). RFC 0010 used both to compose multiple trees onto one server, but documented the cross-boundary breadcrumb as a deliberate trade-off.

This RFC closes that trade-off with the lightest design that preserves type safety where it matters (the proxy's source is typed, the manager's API is typed) and pays its costs only where they buy something (the new variant, the manager, the augmentation). The CRTP-heavy alternative was a near miss — typed *more*, but at a perpetual authoring tax disproportionate to the bug it prevents.

After RFC 0011, the framework's catalogue subsystem is type-correct on three axes (level, kind, host-via-proxy) with no further structural work required.
