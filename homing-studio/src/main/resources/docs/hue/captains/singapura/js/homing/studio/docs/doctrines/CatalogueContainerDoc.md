# Doctrine — Catalogues as Containers

> **Catalogue is merely a standard container of multiple docs or sub-catalogues. Every catalogue has an identity — intrinsic, stable, and the sole handle other artifacts use to reference it. All links — between catalogues, between docs, between any artifact and either — flow through identity. The set of catalogues is open; the shape every catalogue satisfies is closed. It shall be displayed elegantly by default, and the catalogue holds no opinion about its presentation, so any other look is equally welcome.**

This is the doctrine the framework commits to. The catalogue kit is what makes the doctrine *expressible*; this doctrine is what makes it *required*.

---

## What this doctrine commits to

Five sentences, five commitments:

1. **Container, not page.** A catalogue is structure, not content. Its only job is to hold an ordered list of typed entries.
2. **Identity, not name.** Every catalogue has an intrinsic identity. Renaming, moving, or restyling a catalogue never changes what it *is*.
3. **Identity-only linking.** All references to a catalogue — from parent catalogues, from docs, from any artifact — flow through identity. No paths, no titles, no class names on the wire.
4. **Open set, closed shape.** Anyone may declare a new catalogue. Every catalogue satisfies the same shape — uniformly walked, uniformly rendered, uniformly indexed.
5. **Render-agnostic.** The catalogue holds no presentational data. Display is the renderer's responsibility; alternative renderers are unrestricted.

---

## What this doctrine bans

In any catalogue declaration:

- **No URL strings.** Links to entries are typed identity references; the framework derives URLs at render time.
- **No rendering directives.** No tile-shape selectors, no layout flags, no CSS class names embedded in catalogue data.
- **No identity surrogates.** Title, position in the tree, file path, and Java class name are *not* identity. Identity is its own thing.
- **No untyped entries.** Every entry is a typed reference to a Doc or another Catalogue. Strings, free-form payloads, and "items" of unspecified shape are not entries.

---

## What this doctrine permits

- **Recursion to bounded depth.** A catalogue may contain sub-catalogues to a depth set by the framework's typed levels (`L0..L8` today, [see below](#typed-levels-rfc-0005-ext2)). The same shape applies at every level — what differs is the typed parent each level declares.
- **Cross-doc references.** A doc may cite a catalogue via the typed reference mechanism, the same way it cites another doc.
- **Themed rendering.** The default renderer supplies an elegant default; alternative renderers (per theme, per installation, per accessibility mode) replace the default without touching the catalogue data.

---

<a id="typed-levels-rfc-0005-ext2"></a>
## Typed levels — the catalogue tree's shape ([RFC 0005-ext2](#ref:rfc-5-ext2))

Catalogue is not a single interface. It is a sealed family of nine: `L0_Catalogue` (root), then `L1_Catalogue<P extends L0_Catalogue>`, `L2_Catalogue<P extends L1_Catalogue<?>>`, … up to `L8_Catalogue<P extends L7_Catalogue<?>>`. Every non-root level declares its parent's type as a generic parameter and exposes a typed `P parent()` accessor.

Two consequences flow from this shape, and they're what the doctrine now commits to:

- **One parent, fixed at compile time.** A given catalogue class is `L<N>_Catalogue<SomeParent>` — it can only declare one parent type. The "multiple parents" allowance the doctrine previously permitted is gone. Multi-parent was always a runtime check; the type system now refuses it outright. If two parents need to share a catalogue, they share a *Doc* (which has no level), not a catalogue.
- **No cycles, no depth surprises.** `L<N>` parents must be `L<N-1>`. A cycle would require a finite type to extend itself; that doesn't reduce. Depth is bounded statically and breadcrumb chains are walked by following typed `parent()` — root to leaf in `N+1` calls, no traversal, no map lookup. The framework's recurring move — *lift runtime-discovered shape into the type system* — applied here as it was applied to the Layer ladder and KeyCombo.

The breadcrumb chain (root → … → containing catalogue) is now a fact the framework can compute structurally and serve to renderers as data ([Defect 0004](#ref:def-4) tracks the absence of this prior to RFC 0005-ext2). Adding `L9` would require extending the sealed `permits` clause + an `L9_Catalogue` interface; the sealed-switch dispatch in `CatalogueRegistry` then forces the new case to be handled. This is the friction we want: depth grows only when the framework's contract is updated to know about it.

---

## Where this doctrine doesn't apply

A surface that needs more than ordered identity — search, filtering, custom-shaped data views, structured per-entry metadata, live state — stops being a catalogue and becomes its own AppModule. Catalogues are the universal *container*; they are not the universal *page*.

When in doubt, ask: *"can this be expressed as an ordered list of Doc-or-Catalogue identities?"* If no, it isn't a catalogue; it's a richer kind of app that the catalogue tree links to.

---

## Why the strictness is worth it

- **Uniform traversal.** Every catalogue is walked the same way; no per-kind dispatch in indexers, search, or downstream tools.
- **Composability.** Any catalogue can be a sub-catalogue of any other — the typed shape guarantees the parent doesn't need to know what kind of catalogue it's containing.
- **Survives refactor.** Identity-based linking means renaming a Java class, moving a file, or re-organising the tree never breaks references.
- **Extensibility without fragmentation.** The open-set / closed-shape pattern lets anyone declare a catalogue without forking the framework or growing a kit-side enum.
- **Themeability without coupling.** The catalogue data is theme-agnostic; the same catalogue tree renders identically across every theme without per-theme branches.

---

## Open extensions — the Umbrella one level up

The same "open set of types, closed structural shape" principle that governs a single catalogue tree also governs how multiple studios compose into one deployable server. Per [RFC 0012](#ref:rfc-12), studios compose into a typed tree via the `Umbrella<S>` ADT — leaves are studios, branches are pure organisational categories. The tree is constructed by the deployer at composition time; the framework reads it as input.

The Umbrella's permitted shape is fixed (`Group` / `Solo`), but the set of studios it can carry is open — any `Studio<L0>` implementation slots in. Visual chrome for each node is supplied by `Fixtures.chromeFor(node)`, mirroring the renderer-owns-presentation rule that catalogues themselves obey. The Catalogue-as-Container doctrine extends, in the same shape, to Studio-as-leaf-of-Umbrella: identity-by-class, structure-only data, no presentation directives carried, framework handles rendering.

---

## How to think about it

A catalogue is a typed tree node. It has identity (so it can be pointed at), it lists ordered children (Docs and other catalogues), and it knows nothing about how it looks. Everything else — the title shown in headers, the icons in tiles, the URLs in hrefs, the section grouping — is either intrinsic to its entries or computed by the renderer.

When designing a new "catalogue-shaped" thing, the test is: *does it have just identity and ordered typed entries, or does it carry more?* If more, it's not a catalogue.

---

## See also

- [Pure-Component Views](#ref:pcv) — the foundational doctrine that informs the renderer's responsibility separation.
- [Owned References](#ref:or) — identity-as-handle for DOM elements; this doctrine extends the same reasoning to navigation.
- [RFC 0001 — App Registry & Typed Nav](#ref:rfc-1) — the typed-Linkable model identity rests on.
- [RFC 0004 — Typed Docs, UUIDs, and Visibility](#ref:rfc-4) — the same identity-as-wire-handle pattern, applied to docs.
- [RFC 0004-ext1 — Managed Markdown References](#ref:rfc-4-ext1) — the Reference model identity-based linking flows through.
- [RFC 0005-ext2 — Typed Catalogue Levels](#ref:rfc-5-ext2) — the sealed `L0..L8` family that gives the tree its shape and turns multi-parent / cycles / depth surprises into compile errors.
- [RFC 0012 — Typed Studio Composition](#ref:rfc-12) — lifts this doctrine one level up. The Umbrella ADT places studios at the leaves of the same "open set, closed shape" container pattern, with `Fixtures.chromeFor` supplying the rendering separation catalogues already obey.
- [Defect 0004 — Flat Breadcrumbs](#ref:def-4) — the bug that motivated the typed-levels refactor.
