# Doctrine — Typed Content Vocabulary

> *"You don't really need HTML, just SVGs."*

Framework-shipped content is composed exclusively of typed primitives. No construct in framework-shipped content is untyped. HTML escape hatches are forbidden; if a use case requires a construct not in the existing vocabulary, file an extension proposal — don't escape.

This is the **content-side mirror of the framework's code discipline**. The Java spine refuses public statics, raw collections in public APIs, untyped catalogues, custom chrome in viewers. The content spine refuses inline HTML, untyped tables, raw markdown extensions, author-rolled DOM constructs. Same principle; both layers.

## The vocabulary today

| Primitive | Carries | Authored via |
|---|---|---|
| **ProseDoc** (legacy `.md`) / **MarkdownSegment** (`.mdad`) | Text — paragraphs, lists, headings, code, links | Slim markdown subset |
| **SvgDoc** / **SvgSegment** | Visual diagrams, illustrations, icons | SVG with semantic tokens (per RFC 0017) |
| **TableDoc** / **TableSegment** | Structured tabular data | JSON (rich) or CSV (lazy) |
| **ImageDoc** / **ImageSegment** | Raster images (photos, screenshots) | `.png` / `.jpg` resource + alt + caption |
| **ComposedDoc** | Sequenced mix of the above | Java record + segment list |
| **PlanDoc** | Structured tracker state | Typed Plan record |
| **AppDoc** | Interactive content surface | AppModule + typed Params |
| **ProxyDoc** | Fresh-identity reference to canonical content | UUID + target Doc |

The vocabulary expands through framework cooperation only — new kinds require an RFC and a sealed-permits extension. No silent additions.

## What the discipline forbids

- **Inline HTML in markdown bodies.** Caught by `.mdad` conformance (RFC 0018). Legacy `.md` tolerated during migration; `.mdad` is strict.
- **Author-rolled DOM constructs that bypass framework primitives.** Card, Header, Section, Tile — these are the framework's; downstream uses them, doesn't replace them. (See *Chrome Is Framework-Owned* — V11 axiom and queued companion doctrine.)
- **Hardcoded colors in framework-shipped visuals.** Use `currentColor` and `var(--color-*)` per RFC 0017's Themable Tokens. Hardcoded values are reserved for content where the color *is* the meaning (photos, brand artwork, intentional fixed palette).
- **Custom CSS classes outside the typed `StudioStyles` family.** New visual primitives are typed records in `StudioStyles`, not loose class names.
- **Untyped escape hatches.** "I need to do X but the vocabulary doesn't have it" → extend the vocabulary by typed proposal, don't smuggle HTML.

## Why this works

Three reinforcing properties:

1. **Theme participation is total.** Every typed primitive composes the framework's themed CSS scope (Viewer ontology V12). Hardcoded HTML escapes that scope by default; typed primitives can't.
2. **Validation is mechanical.** Sealed sums + compile-checked dispatch + conformance scanners catch violations at boot or build, not at first hit. Typed content is auditable; HTML strings aren't.
3. **The vocabulary documents itself.** The typed family is the catalogue authors browse to find what's available. With HTML escape hatches, the actual surface fragments — every studio invents its own constructs. Typed-only means the framework's primitives are the discoverable answer.

## Why "no HTML" actually works

Most HTML in tech documentation is one of:
- Prose (markdown handles it)
- Diagrams (SvgDoc / SvgSegment handles it; better than raster + theming for free)
- Tables (TableDoc / TableSegment handles complex tables; markdown handles simple ones)
- Images (ImageDoc / ImageSegment handles them)
- Code (markdown fenced blocks; future CodeDoc when annotations matter)

The interactive cases (forms, widgets) are AppDoc territory by design — not content authoring.

The layout cases (multi-column, custom grids) are uncommon in tech documentation and would represent a deliberate non-use of the discipline anyway.

**That covers >90% of HTML's real role in tech docs**, replaced cleanly by typed primitives that compose, theme, and validate where HTML doesn't.

## What earned the doctrine

Three converging realizations from recent work:

- **The SvgViewer chrome bug.** A viewer rolled custom DOM instead of using `Card` / `Header` and lost visual + audio support. The fix promoted chrome composition to the type system (V11). The same lesson generalizes to content: author-rolled DOM is the same failure mode at the content layer.
- **The "what does HtmlDoc add over markdown?" audit.** Honest answer: little, for tech docs. What it would enable (complex tables, layout) is better served by typed alternatives (TableSegment, ComposedDoc).
- **The ComposedDoc proposal.** Once a doc can natively mix markdown + diagrams + tables + images as typed segments, the case for HTML evaporates.

## Relationship to other doctrines

- **Code Discipline doctrines** — Functional Objects, Weighed Complexity, Explicit over Implicit — apply at the Java layer. This doctrine is their content-layer mirror.
- **Themable Tokens** (RFC 0017) — every typed visual primitive uses semantic CSS tokens. This doctrine is what makes Themable Tokens enforceable at the *content* level: an SvgSegment can't smuggle hardcoded colors via the back door of arbitrary HTML.
- **Chrome Is Framework-Owned** (Viewer ontology V11) — viewers compose chrome; downstream provides content for slots. Same shape: framework owns presentation, content stays content.

## Scope

Applies to **framework-shipped content** (Homing's own docs — RFCs, doctrines, ontology entries, case studies, plans, building blocks, release notes) and **studios built on Homing that want the same discipline**.

Downstream studios may opt to be looser — that's a choice, not a violation. The doctrine governs what *the framework* commits to; downstream can adopt fully, partially, or not at all.

## See also

- [RFC 0017 — Themable Content](#ref:rfc-17)
- [RFC 0018 — Slim Markdown (.mdad)](#ref:rfc-18)
- [RFC 0019 — ComposedDoc](#ref:rfc-19)
- [RFC 0020 — Visual Asset Docs](#ref:rfc-20)
- [Functional Objects doctrine](#ref:doc-fo) — the Java-layer mirror
- [Explicit over Implicit doctrine](#ref:doc-eoi) — the API-surface mirror
- [Weighed Complexity doctrine](#ref:doc-wc) — informs the favourable cost analysis
- [Ontology — Viewer](#ref:viewer-ontology) — V11 (chrome) + V12 (theme scope) are the structural axioms this doctrine rests on
