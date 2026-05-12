# Defect 0004 — Flat Breadcrumbs in Multi-Level Catalogues

| Field | Value |
|---|---|
| **Status** | **Resolved** (2026-05-12) — RFC 0005-ext2 introduced typed catalogue levels (L0..L8). The catalogue's parent is now a compile-time fact; `CatalogueRegistry.breadcrumbsForDoc(UUID)` and `breadcrumbsForPlan(Class)` return the typed chain, and the doc/plan endpoints carry it to the renderers. The `crumbsAbove: [{ text: "Home", href: brand.homeUrl }]` hard-code in `DocReader.selfContent` is gone; `PlanHostRenderer._brandHeader` reads `data.breadcrumbs` instead of stamping a single brand-label crumb. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-12 |
| **Severity** | UX + architectural — broke the back-button mental model the moment any catalogue gained an L2 child. Every reader navigating into a nested doc lost the path back through the intermediate catalogue. |
| **Affected modules** | `homing-studio-base` (`DocReader`, `DocReaderRenderer.js`, `PlanHostRenderer.js`), every downstream studio with multi-level catalogues. |
| **Surfaces in** | `homing-studio`: Doctrines, Building Blocks, Journeys, Releases catalogues. Opening any leaf doc from those sub-catalogues showed `Home › <doc title>` instead of the full chain. The catalogue-level navigation that the cards screen made obvious vanished as soon as the user clicked through. |

---

## 1. Symptom

The studio catalogue tree is:

```
Homing · studio (L0)
├── Doctrines (L1)
│   ├── First User (Doc)
│   ├── Dual-Audience Skills (Doc)
│   ├── Pure Component Views (Doc)
│   └── …
├── Building Blocks (L1)
│   ├── Atoms (Doc)
│   ├── Catalogue Kit (Doc)
│   └── …
├── Journeys (L1)
│   ├── Rfc0001Plan (Plan)
│   └── …
└── Releases (L1)
    └── 0.0.11 (Doc)
```

Clicking *Pure Component Views* from the Doctrines screen lands on a page whose header reads:

> **Home** › Pure Component Views

The user has no link back to *Doctrines* — only to the brand root. Same for every plan tracker (`Home › RFC 0001` instead of `Studio › Journeys › RFC 0001`) and every release (`Home › 0.0.11`).

The cards screen *did* render the right path (Catalogue-as-Container has its own crumb logic), so the inconsistency was per-screen: catalogues OK, leaves broken.

---

## 2. Root cause

Two independent omissions:

**A. The framework couldn't *answer* "what's the chain above this doc?"**

`CatalogueRegistry` only stored a `parentByChild` map keyed by class — derived at boot by scanning every `Entry.OfCatalogue` and recording the containing catalogue. That gave it `parentOf(Class)` but nothing keyed by `Doc.uuid()` or `Plan.class`. The renderer would have had to traverse the entry tree itself to find the catalogue that contains a given UUID — and there was no API for that.

**B. The renderers didn't ask.**

`DocReader.selfContent` hard-coded:

```java
crumbsAbove: [{ text: "Home", href: brand.homeUrl }]
```

`PlanHostRenderer._brandHeader` did the equivalent in JS:

```js
var crumbs = [
    { text: brand.label, href: brand.homeUrl },
    { text: data.name }
];
```

Neither renderer fetched a chain. Neither had a place to *put* a chain even if the server had offered one — both assumed depth ≤ 1.

---

## 3. Why a patch wasn't enough

A patch would have added a `breadcrumbs(UUID)` method to `CatalogueRegistry` that walks the entry tree at request time, then plumbed the result through `/doc-refs` and `/plan`. That works for today's two-level studio — but it leaves three sharper edges in place:

1. **Cycle / multi-parent detection stays runtime.** `CatalogueRegistry` already runs both checks at boot because nothing stops you from authoring them. Every new level we add means another opportunity to silently misnest a tree.
2. **Depth is unverifiable.** An `Entry.OfCatalogue` in an L2 catalogue could reference an L1 catalogue (or an L4) — there's no compile-time signal that the entry "level" matches its container. The flat-breadcrumb bug is one face of that same hole: when the registry inferred parent at runtime, it had no way to reject a stale or wrong-level reference.
3. **The breadcrumb logic gets weaker as the tree grows.** A runtime walk through `Entry.OfCatalogue` lists works at depth 2; at depth 4 it costs a real traversal per request and offers no way for the type system to flag a leaf doc that's been moved between catalogues. The defect would recur, just deeper.

The recurring framework pattern (Layer ladder, KeyCombo, paletteMode) is: when runtime-discovered shape becomes a UX bug, lift the shape into the type system. RFC 0005-ext2 does that for the catalogue tree.

---

## 4. Resolution — RFC 0005-ext2

Summary of the fix as it relates to this defect (full design in RFC 0005-ext2):

* `Catalogue` is sealed to permit nine subtypes: `L0_Catalogue`, `L1_Catalogue<P extends L0_Catalogue>`, … `L8_Catalogue<P extends L7_Catalogue<?>>`. Each non-root level declares a typed `P parent()`.
* `CatalogueRegistry` walks `parent()` instead of inferring from entries. Cycles and multi-parent are now compile errors (a class can't extend two `L1_Catalogue<X>` types with different `X`, and `L2<L1<L2<…>>>` doesn't reduce to a finite type), so those runtime checks are gone.
* Two new reverse indices are built at construction: `Map<UUID, Catalogue> docHome` and `Map<Class<? extends Plan>, Catalogue> planHome`. They power `breadcrumbsForDoc(UUID)` and `breadcrumbsForPlan(Class)`.
* `/doc-refs` (handled by `DocRefsGetAction`) now returns `breadcrumbs: [{text, href}, …]` alongside the existing `title`, `summary`, `category`, `references`. `/plan` (handled by `PlanGetAction`) does the same.
* `DocReader.selfContent` no longer hard-codes the Home stub; the renderer reads `info.breadcrumbs` and replaces the crumb chain when present. `PlanHostRenderer._brandHeader` prefers `data.breadcrumbs` over the legacy single brand crumb.

After the fix, opening *Pure Component Views* renders:

> **Homing · studio** › **Doctrines** › Pure Component Views

`/plan?id=…RFC0001PlanData` renders:

> **Homing · studio** › **Journeys** › RFC 0001

Every intermediate crumb is a working link to its catalogue. The crumb chain is correct at any depth the framework supports (currently L0..L8); adding L9 means extending the sealed `permits` clause and exhaustive-switching the new case in `CatalogueRegistry` — both compile-enforced.

---

## 5. Verification

* `CatalogueRegistryTest` exercises the new typed-parent dispatch and the parent-match invariant.
* The original cycle / multi-parent tests were removed: those scenarios are no longer expressible in the source.
* All 153 studio tests + 23 base-module tests pass post-migration.

---

## 6. Lesson

When a chrome element (breadcrumbs, navigation, header) gets its shape from runtime traversal, and the underlying tree can grow, expect the chrome to lag the tree. The fix is rarely "make the renderer smarter" — it's almost always "give the shape a type the framework can check, and let the renderer ask for it by name."
