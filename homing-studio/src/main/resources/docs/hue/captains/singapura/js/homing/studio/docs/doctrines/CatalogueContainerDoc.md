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

- **Recursion to any depth.** A catalogue may contain catalogues to any depth; the same shape applies at every level.
- **Multiple parents.** A catalogue can appear as an entry in more than one parent catalogue — identity is intrinsic, not parent-derived.
- **Cross-doc references.** A doc may cite a catalogue via the typed reference mechanism, the same way it cites another doc.
- **Themed rendering.** The default renderer supplies an elegant default; alternative renderers (per theme, per installation, per accessibility mode) replace the default without touching the catalogue data.

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
