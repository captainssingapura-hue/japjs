# RFC 0016 — Content Trees (Generalisation of Catalogues)

| Field | Value |
|---|---|
| **Status** | Proposed |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-16 |
| **Target release** | 0.0.103 |
| **Scope** | Framework — introduce `ContentTree`, a data-authored hierarchical structure that shares the catalogue rendering surface but is constructed from immutable tree data rather than typed records. Catalogue stays untouched — it remains the typed, code-authored spine of the framework. ContentTree opens the door to generated, imported, dynamic, and lightweight-authored hierarchies (search results, tag pages, manifest-driven indexes, downstream-supplied trees) without forcing a Java class per node. The two hierarchies render through the same UI; their construction models are deliberately different. |

---

## 1. Motivation

Catalogues today are exclusively **typed records authored in Java**. Each catalogue is its own class, declares typed `subCatalogues()` and `leaves()`, and gets compile-time guarantees about parent-child correctness via the CRTP level hierarchy (L0..L8). This discipline is genuinely valuable for the framework's spine — RFC 0005's typed levels prevent whole categories of authoring mistakes.

But typed code-authoring excludes a real and growing set of legitimate uses:

| Use case | Why typed-only blocks it |
|---|---|
| **Generated catalogues** — "every doc tagged X", "recent changes", "all docs from package Y" | Set is data-derived, not author-declared; can change without rebuild |
| **Search results** — list of docs matching a query | Set is per-request; cannot exist as a static class |
| **Imported trees** — bundles from external sources, exported skill manifests | Source is not Java code |
| **Dynamic / personalised views** — role-filtered listings | Set varies per user/context |
| **Lightweight downstream authoring** — a studio wanting three tiles | One Java class per node is disproportionate ceremony |

The framework already concedes the underlying shape — `Catalogue` has `name`, `summary`, `subCatalogues`, `leaves` accessors. Code-authoring is *one* way to satisfy the shape; immutable data should be another.

This RFC introduces **`ContentTree`** as a parallel hierarchy:

- **Same shape** as `Catalogue` — nested containers with content leaves
- **Same rendering surface** — reuses `CatalogueHostRenderer` via a uniform JSON contract
- **Different construction** — built from immutable tree data, validated at registration, no per-node Java class
- **Different identity model** — string ids (URL slugs) rather than class FQNs

The two hierarchies are **complementary, not competing**: Catalogue holds the framework's stable typed spine; ContentTree absorbs everything that's generated, imported, dynamic, or lightweight. The renderer doesn't know the difference.

This RFC depends on [RFC 0015](#ref:rfc-15) — Doc Unification provides `Doc` + `ContentViewer` + `ProxyDoc`, all three of which ContentTree consumes directly.

## 2. Design

### 2.1 The `ContentTree` shape

```java
public record ContentTree(
        String       id,                // registry key; URL-safe slug
        String       name,
        String       summary,
        TreeBranch   root               // tree root, always a branch
) implements StatelessFunctionalObject { ... }

public sealed interface TreeNode permits TreeBranch, TreeLeaf {
    String segment();                   // url-safe slug, unique among siblings
    String name();
    String summary();
    String badge();
    String icon();
}

public record TreeBranch(
        String           segment,
        String           name,
        String           summary,
        String           badge,
        String           icon,
        List<TreeNode>   children       // mix of branches and leaves
) implements TreeNode { ... }

public record TreeLeaf(
        String           segment,
        String           name,
        String           summary,
        String           badge,
        String           icon,
        ContentRef       content        // typed pointer to viewable content
) implements TreeNode { ... }
```

The invariant — *only leaves bear content* — is enforced by the sealed split: `TreeBranch` has children, `TreeLeaf` has content. Type errors at construction prevent "branch with content" or "leaf with children."

### 2.2 `ContentRef` — pointing at viewable content

```java
public record ContentRef(
        String kind,           // viewer discriminator (e.g. "doc", "plan", "diagram")
        String contentId       // opaque payload for the viewer
) { ... }
```

A leaf does not render anything itself. It points at *what to render* via kind + content id. The framework's `ContentViewer` extension point ([RFC 0015](#ref:rfc-15) §2.7) registers per-kind viewer apps; the framework resolves `(kind, contentId)` to the right viewer URL.

This means a tree leaf can target:

- An existing prose doc — `ContentRef("doc", "<uuid>")` → `DocReader`
- A plan tracker — `ContentRef("plan", "<class-fqn>")` → `PlanAppHost`
- A diagram (future) — `ContentRef("diagram", "<id>")` → registered diagram viewer
- A 3D graph (future) — `ContentRef("graph3d", "<root-fqn>")` → registered 3D viewer
- Any downstream-registered content kind

Trees compose existing viewers. They never reinvent content rendering.

### 2.3 Shared rendering — one JSON contract

`CatalogueGetAction` today emits a JSON payload with `name`, `summary`, `brand`, `breadcrumbs`, `entries`. A new `TreeGetAction` (`/tree?id=<tree-id>&path=<branch-path>`) emits the **same shape**:

```json
{
  "name":    "...",
  "summary": "...",
  "brand":   { "label": "...", "homeUrl": "..." },
  "breadcrumbs": [ {"name": "...", "url": "..."}, ... ],
  "entries": [
    { "kind": "catalogue", ... },           // a TreeBranch child
    { "kind": "doc",       ... },           // a TreeLeaf with content kind "doc"
    { "kind": "app",       ... },           // a TreeLeaf with content kind "app"
    ...
  ]
}
```

`CatalogueHostRenderer.js` consumes this contract regardless of source. No client-side change. The renderer is genuinely generic — its name becomes a minor misnomer, but the behaviour is exactly right.

### 2.4 URL contract

| URL | Resolves to |
|---|---|
| `/app?app=catalogue&id=<class-fqn>` | Typed `Catalogue` (current — unchanged) |
| `/app?app=tree&id=<tree-id>&path=<branch-path>` | `ContentTree`'s branch at the given path |
| `/app?app=tree&id=<tree-id>` (no path) | `ContentTree`'s root branch |
| `/app?app=<viewer>&<viewer-params>` | Content leaf's canonical viewer URL |

A leaf's canonical URL is **always** the viewer URL — never `/app?app=tree&...&path=.../leaf`. The tree is for navigation among branches; content lives at the viewer URL. This is the unification from the address-model discussion: one canonical URL per piece of content; the tree adds discoverability but does not redefine content identity.

If a stale URL points `app=tree&id=X&path=Foo/Bar/Baz` and `Baz` resolves to a leaf, the tree action server-side `302`s to the leaf's canonical viewer URL. Bookmarks self-heal.

### 2.5 Breadcrumb derivation — registry-resolved, no URL hint

Tree leaves' breadcrumbs work the same as catalogue leaves' breadcrumbs: derived from the registry at request time. The unified `DocRegistry` ([RFC 0015](#ref:rfc-15) §2.4) knows each `DocId`'s home, regardless of whether that home is a `Catalogue` or a `ContentTree`. The viewer URL is canonical; the registry knows the breadcrumb chain; no URL parameter carries provenance.

This works because of **`ProxyDoc`** ([RFC 0015](#ref:rfc-15) §2.5): if a doc lives in catalogue X and is *also* surfaced through tree Y, tree Y's leaf carries `ContentRef("doc", "<proxy-uuid>")` where the proxy has its own UUID and its home is tree Y at path Foo/Bar/Baz. Two URLs, two homes, two breadcrumbs — no ambiguity.

Tree-generated proxies use **deterministic UUIDs** via UUID v5 (name-based hash) seeded by `(tree-id, leaf-path, target-uuid)`. Same inputs always produce the same UUID; URLs are stable across boots; bookmarks survive restarts.

### 2.6 Registration

```java
// On Fixtures (default empty):
default List<ContentTree> trees() { return List.of(); }
```

Downstream studios override `Fixtures.trees()` to register their trees. `Bootstrap.compose()` collects them into a `TreeRegistry` parallel to `CatalogueRegistry`. The registry validates at registration:

- `id` is unique across all trees
- `segment` is URL-safe and unique among siblings within each branch
- No cycles (records are acyclic by construction, but a sanity walk is cheap)
- Max depth bounded (16 levels — matches Catalogue's L0..L8 with generous headroom)
- Every `ContentRef.kind` has a registered `ContentViewer`
- Every `ContentRef.contentId` resolves (when the viewer can attest — optional per viewer)
- Tree-generated `ProxyDoc`s register in `DocRegistry` with home = (tree-id, leaf-path)

Validation parity with `CatalogueRegistry` is the bar — every check that protects catalogues today also protects trees.

### 2.7 Mixing trees and catalogues

The two hierarchies are **parallel, not nested**:

- A typed `Catalogue.subCatalogues()` cannot return a `ContentTree` (Catalogue stays untouched).
- A `ContentTree.TreeBranch.children` cannot directly contain a `Catalogue` (different shape; would force `ContentTree` to know about typed Catalogue's level discipline).

Cross-references between the two happen at the **leaf** level:

- A tree leaf with `ContentRef("catalogue", "<class-fqn>")` opens a typed catalogue (via a structural-kind viewer that's a parallel to `StudioProxy`). Future work, not Phase 1.
- A typed catalogue's `Entry.OfStudio` already opens cross-tree navigation; no symmetric construct for tree-side is needed yet.

The parallelism is intentional: each hierarchy maintains the discipline appropriate to its construction model. Mixing would force compromises on both sides.

### 2.8 Static vs dynamic trees

Trees come in two flavours:

| Flavour | Construction | Examples |
|---|---|---|
| **Static** | Registered at boot; immutable for the JVM lifetime | Imported manifests, hand-authored data trees, "all doctrines tagged Audience" computed at boot |
| **Dynamic** | Computed per request from a `TreeProvider` | Search results, role-filtered views, "recent changes" |

Both implement the same `ContentTree` interface; consumers don't differentiate. The framework's `TreeRegistry` stores either a concrete `ContentTree` or a `TreeProvider` that returns one per request:

```java
public interface TreeProvider extends StatelessFunctionalObject {
    String       id();           // stable tree id
    ContentTree  resolve(Request context);
}
```

**Dynamic trees do not own `ProxyDoc` registrations.** Owners must be static — a doc's home cannot vary per request. Dynamic trees can only reference existing content; their leaves are appearance-only.

This constraint also matters for: search indexing (only static trees are crawled), MHTML export (dynamic trees export a snapshot of the current request's resolution), and link previews (static trees produce stable preview metadata).

## 3. Worked example — diagnostic tree

The diagnostic surface from [RFC 0014](#ref:rfc-14)'s P1b currently uses the `CatalogueAugmentation` mechanism to inject tiles into the home L0 and into `DiagnosticsCatalogue`. A `ContentTree`-based design would express the same structure declaratively:

```java
public record DiagnosticsTree(List<Studio<?>> studios) implements TreeProvider {
    public String id() { return "diagnostics"; }
    public ContentTree resolve(Request ignored) {
        return new ContentTree(
            "diagnostics",
            "Diagnostics",
            "Self-introspection surfaces.",
            new TreeBranch("", "Diagnostics", "...", "DIAGNOSTICS", "🩺",
                studios.stream().map(s -> perStudioBranch(s)).toList()
            )
        );
    }

    private TreeBranch perStudioBranch(Studio<?> s) {
        String fqn = s.getClass().getName();
        return new TreeBranch(
            slug(s.home().name()), s.home().name(),
            "Diagnostics scoped to " + s.home().name(),
            "DIAGNOSTICS", "",
            List.of(
                new TreeLeaf("graph", "Object Graph", "...", "DIAGNOSTICS", "",
                    new ContentRef("app", "studio-graph?root=" + fqn + "&view=TREE")),
                new TreeLeaf("types", "Type View", "...", "DIAGNOSTICS", "",
                    new ContentRef("app", "studio-graph?root=" + fqn + "&view=TYPES"))
            )
        );
    }
}
```

The existing `CatalogueAugmentation` machinery for the home-L0 injection can stay (it's the right tool when the framework needs to *inject into a catalogue it doesn't own*). The `DiagnosticsCatalogue` content can migrate from augmentation-driven to tree-driven — cleaner because the framework *does* own that tree.

This is illustrative, not normative — the migration is a follow-up RFC if and when the augmentation mechanism's friction warrants it.

## 4. Invariants

Locked behavioural invariants of `ContentTree`:

1. **Catalogue is untouched.** Typed catalogues retain CRTP level discipline, parent-match validation, and all current properties. ContentTree is purely additive.
2. **Only leaves bear content.** The sealed `TreeNode = TreeBranch | TreeLeaf` split enforces this at the type level. A branch cannot have content; a leaf cannot have children.
3. **Identity is by string id.** Trees identified by `id` (URL-safe slug); leaves located by `(tree-id, path)` where path is `/segment/segment/...`. Class identity is not used.
4. **One canonical URL per piece of content.** The viewer URL is the address. Tree paths are for navigation; never the content's identity.
5. **Tree-leaf appearances of existing content use `ProxyDoc`.** No registration ever silently shares a UUID with another location. If the same canonical content appears in two places, two proxies exist, two UUIDs, two homes.
6. **Static trees own their leaves' homes; dynamic trees do not.** Only boot-registered (`Fixtures.trees()`) trees can be owners. `TreeProvider`-resolved trees can only reference content owned elsewhere.
7. **Tree depth bounded.** Max 16 levels; configurable but capped. Avoids pathological inputs from imported sources.
8. **Trees are immutable after registration.** A `ContentTree` instance is a value; `TreeProvider.resolve(...)` returns a fresh immutable instance per call. No in-place mutation.
9. **Registration validation is strict.** Slug uniqueness, viewer-kind resolution, content-id resolution (where viewers can attest), proxy uniqueness — all rejected at boot, not at first hit.
10. **Brand home cannot be a `ContentTree`.** `StudioBrand.homeApp` is typed (`Class<? extends Catalogue<?>>`); home must be a typed catalogue. Trees can live anywhere *but* home.

## 5. Cost — Weighed Complexity

| Dimension | Cost |
|---|---|
| **Cognitive density** | Moderate. Readers learn: trees are parallel to catalogues; same UI, different construction. `ContentRef` and `ContentViewer` from [RFC 0015](#ref:rfc-15). One sealed `TreeNode` split. No clever generics. |
| **Blast radius** | Narrow. Net-new files (`ContentTree`, `TreeRegistry`, `TreeGetAction`, `TreeAppHost`); zero edits to `Catalogue`, `CatalogueRegistry`, `CatalogueGetAction`. The shared renderer is consumed unchanged. |
| **Reversibility** | High. The whole feature can be removed by dropping the new classes; existing code is untouched. |
| **Authoring tax** | Negative for the use cases the RFC targets (generated, imported, dynamic, lightweight). Zero for existing typed catalogue authoring. |
| **Failure mode** | Boot-time errors on slug collisions, unknown content kinds, missing viewers, max-depth violations. Dynamic-tree resolution errors return `404` with the partial breadcrumb to the deepest valid ancestor. |

Per [Weighed Complexity](#ref:doc-wc): preserves the typed-spine discipline while opening a clean seam for everything that doesn't fit the typed mould. Each new tree is data, not a class. Each new content kind is a `ContentViewer` registration. Compound effects scale linearly with the surface they unlock; cost is sharply favourable.

## 6. Decisions (locked)

1. **`ContentTree` lives in `homing-studio-base`** alongside `Catalogue`. Parallel siblings, both at the spine.
2. **Catalogue is not modified.** Typed-CRTP level hierarchy stays. No "data-authored Catalogue" option; that responsibility belongs entirely to `ContentTree`.
3. **`TreeNode` is sealed: `TreeBranch | TreeLeaf`.** Branch-with-content and leaf-with-children are type errors, not runtime checks.
4. **Identity is string slugs (`id`, `segment`).** URL-safe `[a-z0-9-]`; collisions among siblings rejected at registration; tree-level collisions across trees rejected at registration.
5. **Leaf content is `ContentRef(kind, contentId)`.** Viewer routing via [RFC 0015](#ref:rfc-15) `ContentViewer`. Leaves do not render directly.
6. **One canonical URL per piece of content.** The viewer URL is the address. Tree URLs are for branches only; leaf-path URLs `302` to canonical.
7. **Tree-leaf appearances of existing content use `ProxyDoc` with deterministic UUIDs.** UUID v5 from `(tree-id, leaf-path, target-uuid)`. URL stability across boots.
8. **Static and dynamic trees both supported.** Static is the default (`Fixtures.trees()`); dynamic via `TreeProvider`. Only static can own proxy registrations.
9. **`TreeAppHost` and `TreeGetAction` parallel `CatalogueAppHost` and `CatalogueGetAction`.** Shared JSON contract; `CatalogueHostRenderer.js` consumed unchanged.
10. **Brand home stays typed.** `StudioBrand.homeApp` requires a typed `Catalogue` class; trees cannot be the home L0.
11. **Trees and catalogues do not nest directly.** Cross-references via leaf-level viewer routing only.
12. **`TreeRegistry` validation is strict, parity with `CatalogueRegistry`.** Slug uniqueness, kind resolvability, content-id resolution, proxy-uniqueness, depth bounds.

## 7. Decision

**Adopt.** ContentTree is the framework's data-authoring affordance — the necessary complement to typed Catalogue. The two together cover the full authoring surface from typed code to imported data without compromising either discipline.

## 8. Implementation order

Tracked in a dedicated *Content Trees* plan (to be filed under *Journeys → Operations*). Summary:

1. **Phase 1 — `ContentTree` + `TreeNode` family.** Define the record family; no registry yet, no rendering.
2. **Phase 2 — `TreeRegistry` + validation.** Slug uniqueness, depth bounds, viewer-kind resolution.
3. **Phase 3 — `TreeGetAction` + `TreeAppHost`.** Server-side JSON producer + AppModule reusing the existing renderer.
4. **Phase 4 — `Fixtures.trees()` + `Bootstrap.compose()` wiring.** Static-tree registration path.
5. **Phase 5 — `ProxyDoc` integration.** Tree-generated proxies with deterministic UUIDs; `DocRegistry` integration. Depends on [RFC 0015](#ref:rfc-15) Phase 4.
6. **Phase 6 — `TreeProvider` for dynamic trees.** Per-request resolution; appearance-only semantics for proxies.
7. **Phase 7 — First non-trivial consumer.** Pick one (search results, tag-based discovery, or diagnostic-tree migration) and validate the end-to-end story.

Phases 1-4 are the minimum viable shape. Phases 5-7 unlock specific consumers.

## 9. Why this is the right time

The framework has acquired multiple latent "I want a tree of content but don't want to write a Java class per node" use cases:

- The diagnostic UI's per-studio fan-out ([RFC 0014](#ref:rfc-14) P1b) is currently expressed via `CatalogueAugmentation` because there's no tree primitive. Augmentation works, but it's the wrong tool for "the framework owns the whole tree" — it's better suited for "inject into a catalogue I don't own."
- The eventual auto-diagrams from [RFC 0013](#ref:rfc-13) §5 produce trees that users navigate; those trees should be addressable.
- Future search and tag-based discovery surfaces (queued informally, no RFC yet) need trees per query — categorically not typed-class-per-node.
- Downstream studios composing multiple curated views of the same underlying content (e.g. "for new readers" vs "for framework contributors") need lightweight tree authoring.

Without this RFC, each of those re-invents a parallel structure. Naming `ContentTree` once, here, lets all of them compose onto a shared primitive — same renderer, same JSON contract, same `ContentViewer` registry, same `ProxyDoc` machinery for multi-home.

## See also

- [RFC 0005 — Catalogue + Doc Browser](#ref:rfc-5) — the typed catalogue model this RFC parallels.
- [RFC 0005-ext2 — Typed level hierarchy](#ref:rfc-5-ext2) — the L0..L8 discipline `ContentTree` deliberately opts out of.
- [RFC 0011 — Typed Cross-Tree Reverse References](#ref:rfc-11) — the structural-leaf carve-out that informs why trees don't nest directly under catalogues.
- [RFC 0014 — Typed Studio Graph](#ref:rfc-14) — the source of the diagnostic-tree worked example; `ContentTree` is a candidate target for migrating that surface.
- [RFC 0015 — Doc Unification](#ref:rfc-15) — provides `Doc`, `ProxyDoc`, and `ContentViewer`, all consumed directly by this RFC.
- [Catalogue-as-Container doctrine](#ref:doc-cc) — the principle this RFC respects in `ContentTree`'s shape.
- [Explicit over Implicit doctrine](#ref:doc-eoi) — `ContentTree.TreeNode` sealed split, registered viewer routing, and `DocRegistry`-resolved breadcrumbs are all worked examples.
- [Functional Objects doctrine](#ref:doc-fo) — `ContentTree`, `TreeProvider`, and every shape herein are records / interfaces with no parameter explosion.
