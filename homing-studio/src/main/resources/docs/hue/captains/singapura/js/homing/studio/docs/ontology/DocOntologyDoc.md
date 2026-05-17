# Ontology — Doc

A **Doc** is the atomic unit of citable, viewable content in the framework. It is the smallest thing that can be identified, located, linked-to, opened, and read as a coherent whole.

This entry states what a Doc *is*. It does not prescribe how to author one, how to organise a collection of them, when to choose one content kind over another, or how to design good citation grammar. Those are operational concerns and belong in Doctrines. See *Scope* below.

## Definition

A Doc is a stateless, read-only, uniquely-identified, single-home artefact of viewable content. It carries universal metadata; its body is delivered by a registered viewer.

## Identity

- **A1 — Identifier kind.** A Doc is identified by a `DocId`. The framework supports three id variants: `ByUuid(UUID)`, `ByClass(Class<?>)`, `ByClassAndParams(Class<?>, Object)`. The id variant is intrinsic to the Doc subtype and does not vary across instances.
- **A2 — Universality of the identifier.** A Doc's id is the only authoritative handle on it. All references, all URLs, all registry lookups go through the id.
- **A3 — Durability.** A Doc's id is stable across content edits and process restarts. A new id appears only when the author decides "this is a different Doc."
- **A4 — Local uniqueness.** Within a given framework deployment, no two Docs share an id. Across deployments, id uniqueness is not claimed.

## Axioms

The eight rules every Doc satisfies.

- **A5 — Tree leaf.** A Doc is attached as a leaf to exactly one `DocTree` (a `Catalogue` or a `ContentTree`). A Doc registered without a leaf placement is rejected at boot; a Doc placed at two leaves is rejected at boot.

- **A6 — Single-home invariant.** Multi-home is not a property of Docs. The same canonical content appearing in two trees is realised by two Docs (one being a `ProxyDoc` whose target is the other), each with its own id and its own single home.

- **A7 — Reference by id.** A Doc may reference other Docs. Each reference is a `(localName, DocId)` pair. The localName is the cite token used inside the body; the DocId is what the registry resolves. References by path, name, or class outside the `DocId` family are not Doc references.

- **A8 — Cross-tree referenceability.** Reference resolution does not depend on the source and target Docs sharing a tree. Tree membership is irrelevant to whether a reference can be made.

- **A9 — Single-page surface.** A Doc surfaces as a single-page experience. A Doc may carry internal sub-states (sections, panels, expanded regions), but no sub-state is independently addressable by URL and no sub-state can be the target of a reference. If a candidate sub-state warrants its own URL or citation, it is a different Doc.

- **A10 — Read-only.** The Doc protocol exposes no write operations. The body is delivered by the viewer, not mutated by the Doc. Authoring happens out-of-band; runtime is read-only.

- **A11 — Statelessness across users.** A Doc yields the same content for every viewer at a given moment, modulo cosmetic deployment settings (theme, locale). No per-user state, no per-session variation, no cookies remembered by the Doc protocol.

- **A12 — One viewer per Doc, many Docs per viewer.** A Doc declares a content kind. The framework routes to the registered `ContentViewer` for that kind. The Doc carries no renderer; the viewer carries no Doc-specific logic. Each Doc has exactly one viewer; each viewer renders many Docs.

## Universal shape

Every Doc, regardless of kind, carries these properties:

- `id : DocId`
- `name : String`
- `summary : String`
- `badge : String`
- `references : List<Reference>`
- `kind : ContentKind` *(implicit in the Doc subtype)*

Body bytes are not part of the universal shape — they are delivered through the viewer's pipeline.

## Relationships

- **Doc ↔ DocTree.** Every Doc has exactly one home in exactly one tree (A5). A tree may host many Docs.
- **Doc ↔ ContentViewer.** Every Doc routes through exactly one viewer; a viewer serves a content kind across many Docs (A12).
- **Doc ↔ Doc.** A Doc may reference other Docs by id (A7). Self-reference is permitted.
- **Doc ↔ DocId.** Every Doc has an id; ids belong to the sealed `DocId` family (A1–A4).
- **Doc ↔ ProxyDoc.** `ProxyDoc` is a Doc subtype; it is the framework's only mechanism for surfacing the same canonical content in multiple trees while preserving A6.

## Realisations

The Java types that embody Doc in the current framework:

- `ProseDoc` — prose content (today: `ClasspathMarkdownDoc` and its subtypes).
- `PlanDoc` — structured trackers from the `Plan` family.
- `AppDoc` — interactive content; wraps an `AppModule` + typed `Params`.
- `ProxyDoc` — fresh-identity delegation to another Doc (A6 mechanism).

Downstream studios add Doc subtypes by implementing the `Doc` interface and satisfying the universal shape; the framework's machinery routes them through the registered viewer for their declared kind.

## Scope

This entry defines what a Doc *is*. It does not contain operational guidance.

- **How to choose a content kind**, when to introduce a new one, how to structure a markdown body, what makes a good summary, when to extract content into separate Docs — operational. Lives in Doctrines.
- **How to author cite tokens**, when to use a `ProxyDoc` vs let the canonical surface stand alone, how to manage cross-tree references in practice — operational. Lives in Doctrines.
- **How to migrate existing content into the Doc family**, what to do with legacy markdown files, how to handle deprecated references — operational. Lives in Doctrines (or in dedicated migration plans).
- **What viewers are available**, how to register a new one, what shapes well as a Card vs as a full-page reader — operational. Lives in Doctrines and in `ContentViewer`-related Building Blocks.

The split is intentional: changing how to use Docs is a frequent, evolving concern; changing what a Doc *is* is a rare, foundational concern. They move at different speeds and belong in different layers.

## Conformance

A `DocConformanceTest` is the intended build-time enforcer for these axioms over the registered Doc set. Not yet implemented; queued as a follow-up alongside the realisation of the typed `Doc` sealed family (per RFC 0015).
