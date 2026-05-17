# Ontology — DocTree

A **DocTree** is a strict, immutable, rooted-tree structure of named nodes whose leaves bear Docs or reference other DocTrees. It is the framework's only structural container for content; everything a user navigates lives in one.

This entry states what a DocTree *is*. It does not prescribe how deep to nest, how to choose slug names, when to introduce a cross-tree leaf, when to choose a typed Catalogue vs a data-authored ContentTree, or how breadcrumbs render at a cross-tree boundary. Those are operational concerns and belong in Doctrines. See *Scope* below.

## Definition

A DocTree is a strict, immutable, rooted-tree structure of named nodes; only its leaves bear content; leaves are either Docs (content-bearing) or references to another DocTree's root (structural).

## Structural axioms

- **T1 — Strict rooted tree.** Exactly one root. Every non-root node has exactly one parent. No cycles. No diamonds (no two paths from the root to the same node). Breadcrumb derivation is therefore unique by construction.
- **T2 — Ordered children.** A node's children are ordered. Sibling order is part of the tree's identity; reordering produces a different tree value.
- **T3 — Leaf-or-branch closure.** Every node is either a *branch* (has children, bears no content) or a *leaf* (has no children, bears content). No node is both; no node is neither.
- **T4 — Leaf-kind closure.** Every leaf is exactly one of two kinds:
  - a *content leaf*, bearing a Doc;
  - a *tree-root reference leaf*, naming another DocTree's root.

  Any future navigational leaf kind requires an explicit extension of this ontology entry, not a quiet additional case in the renderer.

## Content axioms

- **T5 — Branches bear no content.** Branches carry only presentation-neutral metadata: name, summary, badge, icon. They never carry Doc bodies, viewer references, or any content payload. Their role is purely structural.
- **T6 — Per-Doc single-home (within and across DocTrees).** Within a single DocTree, each Doc id appears at most once. Across all registered DocTrees, each Doc id appears at most once. Multiple appearances of the same canonical content use `ProxyDoc` (each with its own id) per Doc A6. This is the DocTree side of Doc A5.

## Immutability axioms

- **T7 — Value-immutability.** Once constructed, a DocTree's structure and content references never change. The tree value is final. The content *of the Docs* it holds may evolve via deployment; the tree's pointers to them do not.

  For computed/dynamic DocTrees (a per-request realisation), each computed value is itself a fully-formed immutable DocTree; the dynamism is across requests, not within a request.

- **T8 — Construction-time well-formedness.** All structural and content invariants — T1 through T6 — are validated when the DocTree is built. After construction, consumers receive a DocTree already known well-formed; no consumer re-validates.

## Identity axioms

- **T9 — Tree identity.** Every DocTree has its own identity, distinct from any node inside it. The identity kind is intrinsic to the realisation: typed Catalogues are identified by class FQN; data-authored ContentTrees are identified by string id.
- **T10 — Node addressability.** Every node is addressable by `(tree-id, path)` where path is the sequence of child-identifiers from root to node. Branch nodes' canonical address is `(tree-id, path)`. Content leaves' canonical address is the content's own identity (Doc id → viewer URL); their `(tree-id, path)` is a structural alternate.

## Relationships

- **DocTree ↔ Doc.** A DocTree hosts Docs at its content leaves; each Doc has exactly one home (Doc A5 ⇔ T6).
- **DocTree ↔ DocTree.** DocTrees connect via tree-root reference leaves (T4). The graph of DocTrees-connected-by-references is itself acyclic — a DocTree cannot, transitively, reference back to itself.
- **DocTree ↔ Node.** A DocTree is composed of nodes (branches and leaves); every node belongs to exactly one DocTree (T1 + T9). A node never spans trees.
- **DocTree ↔ ContentViewer.** A DocTree is rendered uniformly regardless of realisation; the renderer reads the structural protocol, not the implementation. Content leaves route through their Doc's registered ContentViewer.

## Realisations

The Java types that embody DocTree in the current framework:

- `Catalogue` — typed authoring; CRTP level discipline (L0..L8); class-identified.
- `ContentTree` — data-authored; runtime depth-bounded; string-id-identified.

Both implement the same structural protocol. The renderer never branches on the realisation; downstream code that walks a DocTree treats both kinds uniformly.

## Scope

This entry defines what a DocTree *is*. It does not contain operational guidance.

- **When to choose a typed Catalogue vs a data-authored ContentTree** — authoring decision; lives in Doctrines.
- **How deep to nest, how broad to fan out, when to split** — authoring guidance; lives in Doctrines.
- **Slug naming conventions** — URL-safe characters, length limits, casing rules; operational. Lives in Doctrines.
- **When to introduce a tree-root reference leaf vs nest the content in-place** — composition guidance; lives in Doctrines.
- **How breadcrumbs render across a cross-tree boundary** — implementation detail of the renderer (today: prepend the host tree's chain per RFC 0011); operational.
- **Registration order, depth limits, dynamic-tree refresh policies, error-message wording** — all operational.

The split is intentional: changing how to use DocTrees is a frequent, evolving concern; changing what a DocTree *is* is a rare, foundational concern. They move at different speeds and belong in different layers.

## Conformance

A `DocTreeConformanceTest` is the intended build-time enforcer for these axioms over the registered DocTree set. Not yet implemented; queued as a follow-up alongside the realisation of the typed `DocTree` interface and the eventual landing of RFC 0016 (`ContentTree`).
