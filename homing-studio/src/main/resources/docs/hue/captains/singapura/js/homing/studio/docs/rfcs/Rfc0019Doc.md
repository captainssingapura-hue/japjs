# RFC 0019 — ComposedDoc

| Field | Value |
|---|---|
| **Status** | Proposed |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-17 |
| **Target release** | 0.0.103 |
| **Sibling** | [RFC 0018 — Slim Markdown](#ref:rfc-18), [RFC 0020 — Visual Asset Docs](#ref:rfc-20) |
| **Realises doctrine** | [Typed Content Vocabulary](#ref:doc-tcv) |
| **Scope** | Introduce `ComposedDoc` — a Doc kind whose body is an ordered sequence of typed segments (`MarkdownSegment`, `SvgSegment`, `TableSegment`, `ImageSegment`, future extensions via sealed permits). The new default Doc shape; replaces the "markdown with inline HTML escapes" pattern. Visual segments are typed proxies to canonical Docs (SvgDoc / TableDoc / ImageDoc) — same content can appear in multiple ComposedDocs without duplication. Server-rendered TOC; pure ADT segment family; no cross-segment references. |

---

## 1. Motivation

Most technical docs benefit from at least one diagram. The prose-only ProseDoc is a special case; the realistic shape is *prose + a diagram + a table + more prose*. Today's framework forces authors into the markdown-with-inline-HTML pattern for that — which immediately collides with the Typed Content Vocabulary doctrine.

`ComposedDoc` makes the mixed-content case typed and first-class. The doc IS a sequence of typed segments; each segment is rendered by the framework primitive that matches its kind. The HTML escape hatch becomes unnecessary because the typed alternatives are equally easy (or easier) to author.

The doctrine flips:

> **Old default**: "Write a ProseDoc; add diagrams only when needed."
>
> **New default**: "Write a ComposedDoc; the doc usually benefits from at least one diagram. If it really is pure prose, ProseDoc is the simpler degenerate path."

## 2. Design

### 2.1 The Segment ADT

```java
public sealed interface Segment permits
    MarkdownSegment,
    SvgSegment,
    TableSegment,
    ImageSegment
{}

public record MarkdownSegment(String body) implements Segment {}
public record SvgSegment(SvgDoc<?> doc, Optional<String> caption) implements Segment {}
public record TableSegment(TableDoc doc, Optional<String> caption) implements Segment {}
public record ImageSegment(ImageDoc doc, Optional<String> caption) implements Segment {}
```

Pure ADT discipline: each segment is its own record with its own typed fields. No common base, no shared interface beyond the sealed marker. Exhaustive switch dispatches the renderer.

Future kinds (CodeSegment, MathSegment, VideoSegment) extend the permits list — explicit framework cooperation required; no silent additions.

### 2.2 Visual segments are proxies

`SvgSegment`, `TableSegment`, `ImageSegment` each wrap a registered Doc (SvgDoc / TableDoc / ImageDoc per RFC 0020) plus an optional per-appearance caption override.

This is the [`ProxyDoc`](#ref:rfc-15) pattern applied to inline appearances:
- The canonical artifact (SvgDoc, TableDoc, ImageDoc) lives once with its own UUID, viewer URL, breadcrumb home
- Each appearance in a ComposedDoc is a thin reference + local framing
- Multi-home for free — the same SvgDoc can appear in 5 ComposedDocs, each with a different caption
- Citation grammar uniform — prose can `[link](#ref:my-diagram)` to either the standalone viewer or any embedded appearance

`MarkdownSegment` is the only segment that's intrinsic, not a proxy — its body is `.mdad` text that's specific to the composed doc; markdown bodies aren't independent artifacts.

### 2.3 ComposedDoc shape

```java
public record ComposedDoc(
        UUID            uuid,
        String          title,
        String          summary,
        String          category,
        List<Segment>   segments,
        List<Reference> references
) implements Doc {
    @Override public String kind() { return "composed"; }
    @Override public String url()  { return "/app?app=composed-viewer&id=" + uuid; }
    @Override public String contents() { return /* JSON-serialise segments + TOC */; }
    @Override public String contentType() { return "application/json; charset=utf-8"; }
}
```

### 2.4 Server-rendered TOC

The composed doc's serialized payload includes a typed Table of Contents derived server-side:

```java
public record TocEntry(int level, String text, String anchor) {}

// Built at /doc serialization time:
//  - SvgSegment / TableSegment / ImageSegment → caption (or doc.title()) at level 2
//  - MarkdownSegment → extract headings (h1–h4) from the .mdad body
```

For `.mdad` bodies, server-side heading extraction is reliable because the format is constrained (`#` ATX headings only, no setext, no inline-HTML to confuse the parser). Legacy `.md` keeps client-side scroll-spy TOC; `.mdad` gets the cleaner server path.

### 2.5 Viewer

`ComposedViewer extends DocViewer<Params, ComposedViewer>` — chrome free via [V11](#ref:viewer-ontology); `bodyJs()` fetches the JSON payload, iterates segments, dispatches per kind. Each segment kind has a small renderer (~20–50 JS lines).

### 2.6 No cross-segment references

Segments are independent. A TableSegment can't refer to a value introduced in an earlier MarkdownSegment; an SvgSegment doesn't know about the surrounding prose. If shared context is needed, the author restructures or uses the standard Reference machinery (citations work across segment boundaries because they target Docs, not segments).

Discipline holds: the segment is its own self-contained unit.

## 3. Invariants

Locked behavioural invariants:

1. **Segment family is sealed.** Adding a new segment kind requires updating the permits list and the renderer dispatch — explicit framework cooperation.
2. **Visual segments are proxy references**, not inline payloads. Every SvgSegment, TableSegment, ImageSegment wraps a registered Doc.
3. **MarkdownSegment bodies are `.mdad`** (RFC 0018). Conformance enforced.
4. **No cross-segment references.** Segments are self-contained.
5. **Server-rendered TOC.** The payload includes the TOC; client renders it.
6. **No common segment base.** Pure ADT; each segment's typed fields are its own.
7. **ComposedDoc is the new default** for docs that combine prose with any non-prose content. Pure-prose docs may stay as ProseDoc as a degenerate convenience.

## 4. Cost — Weighed Complexity

| Dimension | Cost |
|---|---|
| **Cognitive density** | Low. One new Doc kind; the segment ADT is bounded. |
| **Blast radius** | Net new — no migration required for existing ProseDocs. |
| **Reversibility** | High. ComposedDoc is purely additive; rollback removes the new types. |
| **Authoring tax** | Higher per-doc than markdown alone — author constructs a Java record with N segments. Mitigation: most segments wrap existing typed Docs; the construction is mechanical. |
| **Failure mode** | Segment renderer error → DocViewer's defensive try/catch (V11 base) surfaces the error inline. No silent failures. |

## 5. Decisions (locked)

1. **Sealed Segment family**; future kinds extend permits.
2. **Visual segments are proxies** to canonical Docs (RFC 0020).
3. **`.mdad` for MarkdownSegment bodies** (RFC 0018); legacy `.md` not used here.
4. **Server-side TOC** for ComposedDocs; client renders it.
5. **No cross-segment references**; segments are independent units.
6. **Pure ADT**; no common Segment metadata base.
7. **ComposedDoc is the new default**; ProseDoc remains as a pure-prose convenience.
8. **One ComposedDoc per Java file** — the segments list IS the doc; no doc spans files.

## 6. Implementation order

1. **Segment ADT**: define `Segment` sealed interface + `MarkdownSegment` / `SvgSegment` / `TableSegment` / `ImageSegment` records. Depends on RFC 0020 (visual asset Docs).
2. **ComposedDoc record**: implements Doc; `contents()` serialises segments + TOC as JSON; harvested into DocRegistry.
3. **TOC builder**: walk segments, extract headings from `.mdad` bodies, attach to payload.
4. **ComposedViewer**: extends `DocViewer<Params, ComposedViewer>`; bodyJs iterates segments and dispatches per kind.
5. **Per-segment renderers** (JS, ~20–50 lines each): MarkdownSegment (reuse marked.js path), SvgSegment (mirror SvgViewer pattern), TableSegment (use TableDoc's CSS), ImageSegment (`<img>` + caption).
6. **Demo**: one ComposedDoc in homing-studio (e.g., an RFC retrofit with a diagram + a table).

## 7. What this displaces

| Item | Status |
|---|---|
| HtmlDoc | **Permanently removed** from the roadmap. ComposedDoc covers ~90% of HTML's role; the remaining 10% is out of scope (interactive widgets → AppDoc; complex layouts → not a tech-doc concern). |
| Inline HTML in markdown bodies | **Banned in `.mdad`** (RFC 0018). |
| Markdown tables | **Replaced by TableSegment**. |
| Markdown inline images | **Replaced by ImageSegment**. |
| Mermaid diagram support (RFC 0014 P1d) | **Replaced by computed SVG via SvgDoc**; Mermaid's quality + theme integration weren't worth the bundle weight. |
| Cross-segment references | **Out of scope forever** — discipline holds. |

## See also

- [Doctrine — Typed Content Vocabulary](#ref:doc-tcv) — the principle this RFC realises
- [RFC 0018 — Slim Markdown](#ref:rfc-18) — the format used by MarkdownSegment bodies
- [RFC 0020 — Visual Asset Docs](#ref:rfc-20) — the typed Docs that visual segments wrap
- [RFC 0017 — Themable Content](#ref:rfc-17) — theme participation is automatic via DocViewer V12
- [RFC 0015 — Doc Unification](#ref:rfc-15) — the polymorphic doc viewer pattern; ComposedDoc joins as the new default Doc kind
- [Ontology — Viewer](#ref:viewer-ontology) — V11 (chrome composition) + V12 (theme scope) make ComposedViewer one line of plumbing per segment kind
