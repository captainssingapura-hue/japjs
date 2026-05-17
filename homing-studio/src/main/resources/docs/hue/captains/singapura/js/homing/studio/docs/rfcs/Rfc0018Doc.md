# RFC 0018 — Slim Markdown (`.mdad`)

| Field | Value |
|---|---|
| **Status** | Proposed |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-17 |
| **Target release** | 0.0.103 |
| **Sibling** | [RFC 0019 — ComposedDoc](#ref:rfc-19), [RFC 0020 — Visual Asset Docs](#ref:rfc-20) |
| **Realises doctrine** | [Typed Content Vocabulary](#ref:doc-tcv) |
| **Scope** | Define `.mdad` — a disciplined markdown subset (slim, conformance-enforced) that closes the inline-HTML escape hatch by removing the constructs an author would otherwise reach for HTML to express. Used inside `ComposedDoc.MarkdownSegment` and (eventually) for new ProseDoc bodies. Legacy `.md` files keep working with the current permissive renderer; `.mdad` is the new typed path. |

---

## 1. Motivation

Standard markdown is generous. Inline HTML, reference-style links, footnotes, raw URLs, complex tables, HTML entities — all permitted. That generosity is the discipline gap that the Typed Content Vocabulary doctrine names: authors reach for HTML inside markdown when the typed alternative is harder to find, and the framework's discipline leaks.

`.mdad` ("markdown and down" — slim) is the disciplined subset. Constructs that have a typed alternative are forbidden; markdown is reduced to the constructs that genuinely belong in prose. The conformance scanner enforces it at build time; the renderer doesn't need to handle the kitchen sink.

## 2. The permitted set

| Construct | Status |
|---|---|
| Headings `# ## ### ####` (h1–h4) | ✅ |
| Paragraphs | ✅ |
| Ordered lists `1.` | ✅ |
| Unordered lists `-` / `*` | ✅ |
| Nested lists (depth ≤ 3) | ✅ |
| Bold `**` / italic `_` | ✅ |
| Inline code `` ` `` | ✅ |
| Fenced code blocks ` ``` ` with language hint | ✅ |
| Single-level block quotes `>` | ✅ |
| Horizontal rules `---` | ✅ |
| Hard line breaks (two trailing spaces) | ✅ |
| Typed citations `[label](#ref:name)` | ✅ — the only permitted link form |

## 3. The forbidden set

| Construct | Replacement |
|---|---|
| Inline HTML (any `<tag>...</tag>`) | ComposedDoc with the appropriate segment kind |
| Tables (pipe syntax) | `TableSegment` in a ComposedDoc |
| Inline images `![alt](url)` | `ImageSegment` in a ComposedDoc |
| Bare external URLs `[label](https://…)` | Wrap as `ExternalReference`; cite via `[label](#ref:name)` |
| Reference-style links `[label][id]` + `[id]: url` | Use typed citations |
| HTML entities `&amp;` `&lt;` etc. | Use Unicode literals directly |
| Footnotes `[^1]` | Use `references()` |
| Deeply nested lists (depth > 3) | Restructure; signals the prose is doing too much |
| Inline code longer than ~80 chars | Move to a fenced code block |
| Setext headings (`====` / `----` under text) | Use ATX (`#`) — one syntax per construct |

## 4. Conformance scanner

A `MdadConformanceTest` parses each `.mdad` resource and asserts the forbidden constructs are absent. Lives alongside the existing studio conformance suite.

Implementation: small custom parser (no need for full marked.js) — line-by-line scan with regex matching for forbidden patterns. Fast, deterministic, fails the build with file + line + violation kind.

## 5. Migration

| Path | Strategy |
|---|---|
| Existing `.md` files | Stay as-is; legacy ProseDoc renderer continues to permit the standard markdown surface. Conformance scanner exempts `.md`. |
| New content | Authored as `.mdad`. New ProseDocs use `.mdad`; new ComposedDoc MarkdownSegments use `.mdad`. |
| Gradual migration | Each `.md` file becomes `.mdad` when an author touches it; not forced. Eventually the studio converges; legacy renderer can be deprecated. |

The framework can scan `.md` files and emit *suggestions* ("this construct would be forbidden in `.mdad`; consider migration") without failing the build. Soft adoption path.

## 6. Renderer

`MarkdownSegment.body` is `.mdad` content. The renderer:

- Reuses the existing marked.js pipeline (with `gfm: false` to drop GFM extensions; only CommonMark-permitted constructs subset to the .mdad set survive)
- Or: a smaller custom renderer for the slim set, ~100 lines of JS, simpler to maintain
- Outputs themed HTML using the framework's standard CSS classes (already themed)

Choice between marked.js (reuse, slightly heavier) vs custom (smaller, more discipline) is an implementation decision; default to marked.js with reduced config for v1 (less code, less risk).

## 7. Cost — Weighed Complexity

| Dimension | Cost |
|---|---|
| **Cognitive density** | Low — authors learn a smaller markdown surface, not a new format |
| **Blast radius** | Narrow — new conformance test + small renderer config change |
| **Reversibility** | High — `.mdad` files are valid CommonMark subset; can always be re-renamed `.md` |
| **Authoring tax** | Slight — authors lose some markdown freedom but gain typed alternatives |
| **Failure mode** | Build failure on forbidden constructs in `.mdad` files — visible, actionable |

Per [Weighed Complexity](#ref:doc-wc): closes the major escape hatch in the framework's content surface for a small per-file scanner; downstream studios opt in by adopting the format.

## 8. Decisions (locked)

1. **`.mdad` is a constrained markdown, not a new format.** It's CommonMark-compatible-ish; the constraint is what's removed, not what's added.
2. **Conformance is build-time, not runtime.** A bad `.mdad` fails the build; doesn't crash at request time.
3. **Legacy `.md` coexists** indefinitely. No forced migration.
4. **No `.mdad` superset features.** The format only *removes* from standard markdown; never adds. No new syntax to learn.
5. **No tables in `.mdad`.** Even simple tables go through TableSegment. One way to do tables.
6. **No inline images in `.mdad`.** Even one-off images go through ImageSegment. One way to embed visual content.
7. **Only typed citations allowed for links.** No bare external URLs.

## 9. Implementation order

1. Specify the permitted/forbidden sets formally (done in §2–§3).
2. Write `MdadConformanceTest` — parse + violations.
3. Add `.mdad` file extension to `ClasspathMarkdownDoc` (or new `ClasspathMdadDoc` subtype).
4. Configure the renderer (marked.js options for the slim subset, or custom).
5. Demo: convert one existing `.md` to `.mdad`; verify it passes conformance + renders identically.
6. Optional: soft scanner over existing `.md` files emitting migration suggestions.

Phases 1–4 are the scaffolding; Phase 5 validates the path. Phase 6 is the gradual-migration aid.

## See also

- [Doctrine — Typed Content Vocabulary](#ref:doc-tcv) — the principle this RFC realizes for prose
- [RFC 0019 — ComposedDoc](#ref:rfc-19) — the consumer of MarkdownSegment, where `.mdad` is the body format
- [RFC 0020 — Visual Asset Docs](#ref:rfc-20) — the typed replacements for tables / images / SVG that `.mdad` directs authors to
- [RFC 0017 — Themable Content](#ref:rfc-17) — themable content for which `.mdad` is the prose contribution
