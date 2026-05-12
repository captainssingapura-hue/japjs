# RFC 0005-ext2 — Typed Catalogue Levels

| Field | Value |
|---|---|
| **Status** | **Draft** — design locked; big-bang implementation begins immediately after RFC merges. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-12 |
| **Targets** | `homing-studio-base/app/` — `Catalogue`, `CatalogueRegistry`, and all 5 existing catalogue records in the studio. |
| **Builds on** | [RFC 0005](#ref:rfc-5) (typed catalogue containers) + [Catalogue-as-Container doctrine](#ref:cc). |
| **Replaces** | The runtime-discovered parent-child relationship in `CatalogueRegistry.parentByChild` with compile-time-encoded type parameters. |

---

## 1. The motivating insight

`CatalogueRegistry` currently discovers the tree shape at boot:

```
Walk each Catalogue's entries() — for every OfCatalogue entry,
record (child → parent) in parentByChild map. Detect cycles via DFS;
reject multi-parent at boot. Breadcrumbs walk the map upward.
```

This is **runtime inference of static structure**. The tree's shape is fixed at compile time (every catalogue declares exactly one set of entries; no entries() lookup changes between boots), but the framework re-derives it on every startup. Three concrete consequences:

1. **Wrong-level entry caught at boot, not at compile time.** A typo putting `StudioCatalogue.INSTANCE` as a child of `DoctrineCatalogue.INSTANCE` (cycle) — or putting a `Doc` where a sub-catalogue is expected — only surfaces when the registry validates. Type system silent.

2. **Multi-parent forbidden by policy, not by types.** RFC 0005 D2 chose strict-tree, but `Entry.of(catalogue)` accepts any catalogue regardless of who else already claimed it. The registry rejects at boot; the language permits the expression.

3. **Doc breadcrumbs go flat.** Because a doc doesn't *know* which catalogue contains it without registry consultation, `DocReader` hardcodes a single `[Home]` ancestor in its rendered breadcrumb. The catalogue tree's depth doesn't propagate to leaves.

The same pattern as the Layer ladder (Defect 0003) and `KeyCombo` enum (RFC 0008): take information the runtime discovers, lift it to the type system, let the compiler enforce it.

---

## 2. The design — sealed Catalogue base over 9 typed levels

```java
public sealed interface Catalogue
        permits L0_Catalogue,
                L1_Catalogue, L2_Catalogue, L3_Catalogue, L4_Catalogue,
                L5_Catalogue, L6_Catalogue, L7_Catalogue, L8_Catalogue {
    String name();
    String summary();
    List<Entry> entries();
}

public interface L0_Catalogue extends Catalogue {
    // Root. No parent.
}

public interface L1_Catalogue<P extends L0_Catalogue> extends Catalogue {
    P parent();
}

public interface L2_Catalogue<P extends L1_Catalogue<?>> extends Catalogue {
    P parent();
}

public interface L3_Catalogue<P extends L2_Catalogue<?>> extends Catalogue {
    P parent();
}

public interface L4_Catalogue<P extends L3_Catalogue<?>> extends Catalogue {
    P parent();
}

public interface L5_Catalogue<P extends L4_Catalogue<?>> extends Catalogue {
    P parent();
}

public interface L6_Catalogue<P extends L5_Catalogue<?>> extends Catalogue {
    P parent();
}

public interface L7_Catalogue<P extends L6_Catalogue<?>> extends Catalogue {
    P parent();
}

public interface L8_Catalogue<P extends L7_Catalogue<?>> extends Catalogue {
    P parent();
}
```

Each non-root level is generic over its parent's type. The wildcard `<?>` for grandparent and above keeps signatures from exploding — every level only needs to know its immediate parent's type.

**Depth ceiling: 9 levels (L0..L8).** Any realistic documentation hierarchy fits well within this — Wikipedia's category tree caps at ~5, software documentation rarely exceeds 4. L0..L8 is comfortable headroom; if we ever need L9+ the framework can add it as a sealed permit without breaking anything below.

### Concrete catalogues plug into their level

```java
public record StudioCatalogue() implements L0_Catalogue {
    public static final StudioCatalogue INSTANCE = new StudioCatalogue();
    @Override public String name() { return "Studio"; }
    @Override public String summary() { return /* … */; }
    @Override public List<Entry> entries() { return /* … */; }
}

public record DoctrineCatalogue() implements L1_Catalogue<StudioCatalogue> {
    public static final DoctrineCatalogue INSTANCE = new DoctrineCatalogue();
    @Override public StudioCatalogue parent() { return StudioCatalogue.INSTANCE; }
    @Override public String name() { return "Doctrines"; }
    @Override public String summary() { return /* … */; }
    @Override public List<Entry> entries() { return /* … */; }
}

// Hypothetical L2 — adding a sub-section to Doctrines later:
public record DoctrineMethodologyDoc() implements L2_Catalogue<DoctrineCatalogue> {
    @Override public DoctrineCatalogue parent() { return DoctrineCatalogue.INSTANCE; }
    // …
}
```

### Entry stays unchanged (Option A — locked in)

The existing `Entry` sum type (`OfDoc`, `OfPlan`, `OfApp`, `OfCatalogue`) stays as-is. **Order-preserving mixed entries** — a catalogue's `entries()` returns one `List<Entry>` interleaving sub-catalogues and leaves freely, exactly as today.

The depth constraint on the `OfCatalogue` case (each entry catalogue must be exactly one level deeper than its parent) is **enforced at boot** by `CatalogueRegistry`, not at compile time. Trade-off accepted:

- Compile-time would require `Entry<P>` generic over the parent level + an `OfCatalogue<P>` that constrains the carried catalogue to `LP+1`. Possible but verbose; type parameters propagate everywhere.
- Boot-time validation catches the same mistakes one second later with a clearer error message ("DoctrineCatalogue entry expected L2_Catalogue child, got L1_Catalogue") and keeps `Entry` simple.

Catalogues themselves are type-checked (you can't accidentally implement the wrong level interface); their entries are runtime-checked. Best-of-both-worlds compromise.

### Any-level abstraction via the sealed base

Code that doesn't care about depth uses `Catalogue` (the sealed marker). Code that walks parents uses the typed `LN_Catalogue<P>` interfaces and pattern-matches:

```java
public List<Catalogue> breadcrumbs(Catalogue c) {
    return switch (c) {
        case L0_Catalogue l0 -> List.of(l0);
        case L1_Catalogue<?> l1 -> append(breadcrumbs(l1.parent()), l1);
        case L2_Catalogue<?> l2 -> append(breadcrumbs(l2.parent()), l2);
        case L3_Catalogue<?> l3 -> append(breadcrumbs(l3.parent()), l3);
        case L4_Catalogue<?> l4 -> append(breadcrumbs(l4.parent()), l4);
        case L5_Catalogue<?> l5 -> append(breadcrumbs(l5.parent()), l5);
        case L6_Catalogue<?> l6 -> append(breadcrumbs(l6.parent()), l6);
        case L7_Catalogue<?> l7 -> append(breadcrumbs(l7.parent()), l7);
        case L8_Catalogue<?> l8 -> append(breadcrumbs(l8.parent()), l8);
    };
}
```

Exhaustive over the sealed permits — adding L9+ later forces every dispatch site to handle it. Same shape as the `Cue` and `Layer` switches that already exist in the framework.

---

## 3. What the type system enforces (and what runtime still validates)

| Constraint | Today | After RFC 0005-ext2 |
|---|---|---|
| Catalogue extends single base | Yes | Yes (sealed) |
| Catalogue's level is type-visible | No | **Yes (LN_Catalogue interface)** |
| Catalogue's parent type | Inferred from boot walk | **Type parameter** |
| Multi-parent (a catalogue claimed by two parents) | Boot-time error | **Impossible** — a class implements one set of interfaces, can have one `parent()` return type |
| Cycle | Boot-time DFS error | **Impossible** — `parent()` returns LN-1, which `parent()` returns LN-2, etc. — chain strictly descends; no cycle possible without violating the type system |
| Wrong-level entry (catalogue child not one level deeper than parent) | Boot-time validation | Boot-time validation (Option A trade-off) |
| Doc breadcrumb chain | Reverse-index map walk | **`parent()` recursion** via sealed switch |
| Plan breadcrumb chain | Same as doc | **`parent()` recursion** via sealed switch |

The "Impossible" rows are the wins. They're invariants the framework wants but currently merely *checks*; types make them invariants the *compiler* enforces.

---

## 4. The doc-breadcrumb-flat defect, structurally fixed

The original problem prompting this RFC: `DocReader` renders breadcrumbs as `[Home]` regardless of how deep the doc sits.

After this RFC:

1. `CatalogueRegistry` (during construction) walks every catalogue's `entries()` once and builds a reverse index `Map<UUID, Class<? extends Catalogue>>` — doc UUID → first containing catalogue. The same walk validates Entry depth (Option A trade-off).
2. `DocGetAction` includes the breadcrumb chain in its JSON response payload — derived by `breadcrumbs(homeCatalogue)` (the sealed-switch recursion), prepended to the doc's title.
3. `DocReader.selfContent` reads `crumbsAbove` from the doc response instead of hardcoding `[Home]`.
4. Same treatment for `PlanGetAction` + `PlanHostRenderer`.

A doc reached via `Studio → Doctrines → EncapsulatedComponentsDoc` now renders **Studio › Doctrines › Encapsulated Components**, not `Home › Encapsulated Components`. The framework knows the path because the catalogue tree's types tell it.

For docs not in any catalogue (only reachable via `DocBrowser` app), the breadcrumb falls back to the path to the DocBrowser Navigable's catalogue — typically `Studio › Documents`.

---

## 5. Migration — big-bang in one commit

The current studio's catalogue tree is shallow (2 levels deep). The migration touches:

| File | Change |
|---|---|
| `homing-studio-base/app/Catalogue.java` | Convert to sealed interface, permit L0..L8 |
| New `L0_Catalogue.java` through `L8_Catalogue.java` | 9 new interfaces in `homing-studio-base/app/` |
| `StudioCatalogue` | `implements L0_Catalogue` |
| `DoctrineCatalogue` | `implements L1_Catalogue<StudioCatalogue>` + `parent()` |
| `JourneysCatalogue` | Same |
| `BuildingBlocksCatalogue` | Same |
| `ReleasesCatalogue` | Same |
| `CatalogueRegistry` | Drop `parentByChild` map; `breadcrumbs(c)` becomes sealed-switch recursion. Build `Map<UUID, Class<? extends Catalogue>>` reverse index during construction. Validate Entry depth at boot. |
| `DocGetAction` | Include breadcrumb chain in response payload. |
| `PlanGetAction` | Include breadcrumb chain in response payload. |
| `DocReader.selfContent` | Read `crumbsAbove` from response instead of hardcoding. |
| `PlanHostRenderer` | Same. |
| `StudioCatalogueConstructsTest` | Stays valid — typed levels and existing registry validation both pass. |
| New `Defect0004Doc` | Captures the flat-breadcrumb diagnosis, marked Resolved-by-this-RFC. |

Big-bang is safe because:

- The type system catches every mis-level. If `DoctrineCatalogue` is mis-declared, it doesn't compile.
- The boot-time Entry-depth validation catches every mis-placed child.
- Existing tests (StudioCatalogueConstructsTest, StudioPlanConstructsTest, StudioDocConformanceTest) exercise the full tree at registry construction.
- The change is internal — no API surface visible to downstream studios changes (well, downstream studios that define their own `Catalogue` records DO need to migrate to a specific level — that's the breaking-but-typed change).

For downstream studios — adding `implements L1_Catalogue<StudioCatalogue>` instead of `implements Catalogue` is a 1-line change per catalogue. Catches the level mistake at compile time on their side too.

---

## 6. Doctrine refinements

**[Catalogue-as-Container doctrine](#ref:cc)** gains one paragraph:

> Catalogues carry their level in the type system. A `L1_Catalogue<StudioCatalogue>` is a class of catalogue whose parent is provably the studio root — at compile time, by the type signature. The tree's shape is established by the types, validated by the framework only at the seam between typed parents and untyped entry children. Authors can't accidentally produce a cycle or a multi-parent tree; the compiler doesn't let them write it.

**Catalogue-Container Doctrine §"Open extensions"** notes RFC 0005-ext2 as the resolution of "the tree's depth wasn't compile-time visible," same way RFC 0003 resolved Defect 0003 (cascade-layer ladder).

No new doctrine is needed; this is a refinement of an existing one.

---

## 7. Open questions — none

Choices locked in §2:

- **Option A** for Entry (mixed, runtime-validated). ✓
- **Depth ceiling L0..L8.** ✓
- **Any-level abstraction via sealed `Catalogue`.** ✓
- **Big-bang single commit.** ✓
- **Defect 0004 captures the breadcrumb problem; this RFC is its resolution.** ✓

No open questions remain. Implementation can start immediately after this RFC merges.

---

## 8. Decision

**Accepted.** Big-bang migration in a single commit after RFC 0005-ext2 lands. Estimated effort: ~3 hours of focused work (9 new interfaces + 5 catalogue migrations + registry refactor + doc/plan breadcrumb plumbing + Defect 0004 doc).

---

## 9. Implementation order

1. **`Catalogue.java`** — convert to sealed, declare permits for L0..L8.
2. **9 new level interfaces** — `L0_Catalogue.java` through `L8_Catalogue.java`, with parent() generic constraints.
3. **5 existing catalogues migrate** — `StudioCatalogue` → L0; four siblings → L1<StudioCatalogue>.
4. **`CatalogueRegistry`** — drop `parentByChild`, add sealed-switch `breadcrumbs(c)`, build doc/plan reverse indices.
5. **`DocGetAction`** + **`PlanGetAction`** — include breadcrumb chain in payloads.
6. **`DocReader.selfContent`** + **`PlanHostRenderer`** — render from payload.
7. **`Defect0004Doc`** — diagnose the flat-breadcrumb problem; mark Resolved via this RFC.
8. **Doctrine update** — one paragraph in Catalogue-as-Container.
9. **Build green** — type system + existing tests catch everything.

---

## 10. Why this is the right time

Three forces converging:

- **The Defect**: doc breadcrumbs are flat (multi-level catalogues don't show their path). Real user-visible issue.
- **The pattern**: typed-everything is the framework's spine. Layer, KeyCombo, Note, Cue, paletteMode — all started as runtime-discovered, all moved to compile-time. Catalogue depth is the same shape of fix.
- **The shallowness window**: the tree is currently only 2 levels deep with 5 catalogues. A future studio with 8 levels and 40 catalogues would face a much harder migration. We migrate now while the cost is bounded.

The framework gets simpler, the type system carries more, the defect closes structurally. No reason to defer.
