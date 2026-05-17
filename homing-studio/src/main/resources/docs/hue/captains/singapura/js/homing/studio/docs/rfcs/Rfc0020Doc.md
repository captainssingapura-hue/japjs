# RFC 0020 — Visual Asset Docs

| Field | Value |
|---|---|
| **Status** | Proposed |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-17 |
| **Target release** | 0.0.103 |
| **Sibling** | [RFC 0018 — Slim Markdown](#ref:rfc-18), [RFC 0019 — ComposedDoc](#ref:rfc-19) |
| **Realises doctrine** | [Typed Content Vocabulary](#ref:doc-tcv) |
| **Scope** | Formalise the visual-asset Doc family — `SvgDoc` (already exists), `TableDoc` (new), `ImageDoc` (new) — as first-class registered Docs, each with their own viewer. These are the canonical artifacts that `ComposedDoc`'s visual segments wrap; even when primarily used inline, they exist as standalone, citable, addressable Docs. The proxy-pattern application of [`ProxyDoc`](#ref:rfc-15) to inline visual appearances. |

---

## 1. Motivation

ComposedDoc segments (RFC 0019) embed visual content. The question: is the segment the canonical artifact, or a reference to one?

Answer: **the segment is the reference; the visual is a canonical Doc.** Even an SVG that only appears in one place gets its own SvgDoc, its own UUID, its own viewer URL. This:

- Makes every visual citable from prose via the standard reference grammar
- Supports multi-home naturally — the same SvgDoc can appear in many ComposedDocs with different captions
- Composes with the existing Doc machinery (registry, ContentViewer, conformance) instead of inventing parallel infrastructure for "inline-only" assets
- Gives every visual a standalone-view URL for sharing/citing

The cost is tiny — registering one SvgDoc per visual is essentially free given the framework's existing harvest machinery. The benefit is uniformity: visual assets aren't second-class.

## 2. The three kinds

### 2.1 SvgDoc (already implemented)

Carries an `SvgRef<G>`; serves SVG markup from the classpath; kind = `"svg"`; viewer is `SvgViewer` (extends `DocViewer`).

Currently used directly in `ContentTree` leaves (AnimalsTree demo). RFC 0020 formalises its role and aligns it with the trio.

### 2.2 TableDoc (new)

Carries structured tabular data — either JSON (rich) or CSV (lazy). Kind = `"table"`; viewer is `TableViewer` (extends `DocViewer`).

**Data shape** — JSON for full features:

```json
{
  "headers": [
    {"text": "Phase"},
    {"text": "Status"},
    {"text": "Owner", "colspan": 2}
  ],
  "rows": [
    [
      {"text": "Phase 1"},
      {"text": "DONE", "badge": "success"},
      {"text": "alice", "colspan": 2}
    ]
  ]
}
```

CSV for simple grid (no merges, no cell styling):

```csv
Phase,Status,Owner
Phase 1,DONE,alice
Phase 2,IN_PROGRESS,bob
```

**Slim features only:**
- ✅ Headers, rows, cells, colspan/rowspan, cell alignment
- ⚠️ Optional: cell badges (success/warning/error tokens), column widths, footer/totals
- ❌ Formulas, sort/filter interactivity, computed cells, sheet management, editing

The "slim" discipline: tables, not spreadsheets. Formulas and interactivity are explicitly out of scope. Future enhancement = a new RFC, not feature creep here.

**CSS classes** — typed StudioStyles additions: `st_table`, `st_thead`, `st_th`, `st_td`, `st_td_align_left`, `st_td_align_center`, `st_td_align_right`, `st_td_badge_success`, `st_td_badge_warning`, `st_td_badge_error`.

### 2.3 ImageDoc (new)

Carries a raster image resource path (`.png`, `.jpg`, `.webp`). Kind = `"image"`; viewer is `ImageViewer` (extends `DocViewer`).

**Fields:**
- `resourcePath` — classpath location
- `alt` — accessibility text (required)
- `caption` — optional caption rendered below the image
- `width` / `height` — optional intrinsic dimensions (for responsive sizing without layout shift)

**Standalone use case** — photos, screenshots, brand artwork. **Inline use case** — ImageSegment inside a ComposedDoc.

No theming for raster content (Raw tier per RFC 0017). The image is shown as-is; chrome around it is themed.

## 3. The proxy pattern for inline use

When a visual appears inside a ComposedDoc via a segment:

```java
new SvgSegment(MyDiagram.INSTANCE, Optional.of("Caption specific to this appearance"))
new TableSegment(MyTable.INSTANCE,  Optional.empty())
new ImageSegment(MyScreenshot.INSTANCE, Optional.of("As shown in v0.0.103"))
```

The segment wraps the registered Doc (`MyDiagram` is an SvgDoc instance) plus an optional caption. The Doc itself is the canonical artifact; the segment is the per-appearance proxy.

This is exactly the [`ProxyDoc`](#ref:rfc-15) pattern from RFC 0015 §2.5, generalised:
- ProxyDoc carries a fresh UUID + delegates to a target Doc + optional metadata overrides
- Visual segments carry a Doc reference + optional caption override; the segment's "identity" is its position in the ComposedDoc

Both follow the same shape: canonical artifact + per-appearance framing.

## 4. Why all three earn their place

Each visual kind passes the "markdown can't do this cleanly" test (per the earlier audit):

| Kind | What markdown can't | Why typed Doc wins |
|---|---|---|
| **SvgDoc** | Address SVG independently, theme through tokens | Citable, themable, reusable |
| **TableDoc** | colspan/rowspan, cell styling, complex headers | Structured data, schema-validatable, future computed cells possible |
| **ImageDoc** | Standardised caption + alt + dimensions, addressable | Discoverable, accessible-by-default, reusable |

Each is a legitimate kind, not a wrapper over what markdown could already do.

## 5. Invariants

1. **Visual assets are always registered Docs.** Even when used inline only.
2. **Visual segments are proxies.** ComposedDoc's segments reference the canonical Doc; they don't carry the visual content inline.
3. **Slim discipline for TableDoc.** Formulas, interactivity, spreadsheet-ish features stay out. The slim line holds.
4. **Themable defaults.** SvgDoc participates in RFC 0017 theming; TableDoc uses framework cell CSS; ImageDoc is Raw tier (exempt from theming).
5. **Each visual has its own DocViewer.** SvgViewer, TableViewer, ImageViewer all extend the typed `DocViewer<P, M>` base — chrome and theme participation are mandated.
6. **One viewer per kind.** Even though TableDocs come in CSV and JSON ingestion formats, both route through one TableViewer.

## 6. Cost — Weighed Complexity

| Dimension | Cost |
|---|---|
| **New types** | 2 new Doc subtypes (TableDoc, ImageDoc); 2 new viewers; 2 new ContentViewer registrations; ~12 new typed CSS classes for tables |
| **Blast radius** | Net new; doesn't touch existing Docs |
| **Reversibility** | High; the new types are additive |
| **Authoring tax** | Negative — gives authors typed alternatives where they previously hand-rolled HTML / inline markdown tables |
| **Failure mode** | Boot-time validation rejects malformed table JSON / missing image resources / invalid SVG refs — visible, actionable |

Per [Weighed Complexity](#ref:doc-wc): adds three first-class kinds; opens future computed-cell / chart / multimedia kinds; closes the visual-content discipline gap. Sharply favourable.

## 7. Decisions (locked)

1. **Three kinds; SvgDoc already exists**; TableDoc and ImageDoc are new.
2. **SvgDoc / TableDoc / ImageDoc** are first-class Docs with UUIDs, viewers, URLs — even when their primary use is inline in a ComposedDoc segment.
3. **TableDoc is slim** — no formulas, no interactivity, no editing.
4. **TableDoc accepts JSON OR CSV** — author picks based on richness; both route through one TableViewer.
5. **Slim table CSS uses framework tokens** — every cell badge / alignment / border follows RFC 0017 themable tokens.
6. **ImageDoc is Raw tier per RFC 0017** — no theming attempted on raster content.
7. **Visual segments wrap Docs**, never inline content.

## 8. Implementation order

1. **TableDoc** + `TableData` JSON record + CSV parser + JSON validation.
2. **TableViewer** extending `DocViewer<Params, TableViewer>`; bodyJs builds `<table>` using framework CSS classes.
3. **`TableContentViewer`** registration + `Fixtures.contentViewers()` default.
4. **`StudioStyles.st_table_*`** CSS classes.
5. **ImageDoc** + `ImageViewer` + `ImageContentViewer` registration. Smaller — just `<img>` + caption + dimensions.
6. **Demo** — one TableDoc + one ImageDoc registered in homing-studio (e.g., a doctrine-by-category roll-up TableDoc + a brand artwork ImageDoc).
7. **Phase 7 — Wire as segments** (depends on RFC 0019 ComposedDoc): visual segments wrap these Docs.

## See also

- [Doctrine — Typed Content Vocabulary](#ref:doc-tcv)
- [RFC 0018 — Slim Markdown](#ref:rfc-18)
- [RFC 0019 — ComposedDoc](#ref:rfc-19) — primary consumer of these visual Docs as segment targets
- [RFC 0017 — Themable Content](#ref:rfc-17) — SvgDoc + TableDoc inherit themability; ImageDoc is exempt
- [RFC 0015 — Doc Unification](#ref:rfc-15) — the polymorphic doc viewer pattern; visual Docs are new realisations
- [RFC 0014 — Typed Studio Graph](#ref:rfc-14) — Phase 1d's deferred Mermaid 2D view is replaced by computed SvgDoc walking the graph
