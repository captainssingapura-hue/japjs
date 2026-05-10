# Homing Brand Guide

The logo, colors, and typography that match Homing's design principles. One concept, executed quietly.

---

## The concept

> **A lowercase serif `j` whose tittle is bridged across to a second amber dot — the implied tittle of the `i` in `js`. The navy hairline between them is the typed channel.**

The mark reads three ways at once:

1. **A `j`** (Java).
2. **Two dots being wired together** (java↔js).
3. **A short module graph** — source node, typed edge, target node.

Every visual choice is a translation of a design principle:

| Choice | Reason |
|---|---|
| **Square** amber accents (not circles) | Java records — rectangular, structured, terminating. Round dots would suggest organic / fluid. |
| **Thin** navy hairline between tittles | A typed channel — present, deliberate, not decorative. |
| **Square stroke caps** on the `j` | Geometry over calligraphy. Type-safe over expressive. |
| **Georgia italic** wordmark | Classical, weighted, confident. Matches every heading in the docs. The slight italic gives motion without flash. |
| **Calibri** tagline | Modern, clean, secondary. Plays the supporting role. |
| **Amber accent** used sparingly | One tone of warmth in an otherwise cool palette. Marks the moments that matter (the tittles, the hint text, the CTAs) — never decoration. |
| **No gradient, no glow, no motion** | Homing's pitch is honesty over flash. The logo follows the pitch. |

---

## Asset inventory

| File | Use case | Format |
|---|---|---|
| [`logo-primary.svg`](#ref:logo-primary) | Default logo on **light** backgrounds. Header banners, READMEs, docs site. | Mark + wordmark |
| [`logo-light.svg`](#ref:logo-light) | Logo on **dark** backgrounds (navy, black, photography). Title slides, dark-mode docs. | Mark + wordmark, light treatment |
| [`logo-extended.svg`](#ref:logo-extended) | First-impression contexts where the full positioning matters — brochure covers, hero sections, conference signage. | Mark + wordmark + tagline |
| [`logo-wordmark.svg`](#ref:logo-wordmark) | Text-only contexts where the mark is redundant or too small. Citations, body-copy mentions. | Wordmark only |
| [`logo-mark.svg`](#ref:logo-mark) | Square / icon contexts. App icons, social avatars (≥ 128 px), header thumbnails. | Mark only, transparent background |
| [`logo-mono-dark.svg`](#ref:logo-mono-dark) | One-color print, embossing, single-color silkscreen. | Navy on transparent |
| [`logo-mono-light.svg`](#ref:logo-mono-light) | One-color reverse print, dark photography overlays. | White on navy |
| [`favicon.svg`](#ref:favicon) | Browser tab, bookmark, OS app icon (≤ 64 px). The bridge hairline disappears at this scale; this variant simplifies to `j` + tittle on a navy tile. | Tile mark |

---

## Color palette

The same **Midnight Executive** palette used throughout Homing's documentation. Six values; commit to them.

| Role | Hex | RGB | Use |
|---|---|---|---|
| **Primary navy** | `#1E2761` | 30, 39, 97 | Logo, headings, dark surfaces |
| **Deep navy** | `#111936` | 17, 25, 54 | Title-slide backgrounds, deepest tone |
| **Ice blue** | `#CADCFC` | 202, 220, 252 | Body text on dark, secondary surfaces |
| **Amber** | `#F4B942` | 244, 185, 66 | Accent dots, highlights, CTA color |
| **Amber dark** | `#C8921E` | 200, 146, 30 | Tagline color on light, hover states |
| **Off-white** | `#FAFBFD` | 250, 251, 253 | Light backgrounds (slightly warmer than pure white) |

---

## Typography

| Use | Font | Style |
|---|---|---|
| Wordmark | Georgia | Italic, regular weight |
| Headings (docs, slides, brochure) | Georgia | Regular or bold |
| Body text | Calibri / Segoe UI / system-ui | Regular |
| Code, technical strings | Consolas / monospace | Regular |
| Taglines, kickers | Calibri | Bold, all-caps, **letter-spacing 4–6** |

Both Georgia and Calibri are pre-installed on Windows, macOS, and most Linux distros. No web fonts required, no external load.

---

## Clear space and minimum sizes

- **Clear space:** keep at least the height of the mark's amber tittle (about **128** viewBox-units in the mark, or **8 %** of the longer dimension in any lockup) clear on all sides. No competing elements within that zone.
- **Minimum size — primary logo:** 200 px wide. Below this, switch to the mark.
- **Minimum size — mark:** 32 px. Below this, switch to the favicon variant.
- **Minimum size — favicon:** 16 px. The simplification ensures legibility at this size.

---

## Don't

The honest list — common misuses to avoid.

- **Don't add a drop shadow, glow, or bevel.** The mark is geometric on purpose. Effects fight that.
- **Don't recolor the amber to a different accent.** It's the one warmth in the palette; substituting changes the whole tone.
- **Don't render the wordmark in non-italic.** The italic is doing real work — it's the warmth in an otherwise structural design.
- **Don't compose the mark with extra decorations** (orbiting dots, framing brackets, halftone patterns). The mark is finished.
- **Don't use the wordmark without the mark when introducing the brand.** First contact gets both. Subsequent contact can use either.
- **Don't apply the tagline `JAVA · TO · ES · MODULE · BRIDGE` at sizes where it becomes illegible** (under ~9 px effective height). Drop it entirely instead.
- **Don't place the amber on a yellow, gold, or beige background.** Contrast collapses. Use navy or off-white surfaces.
- **Don't stretch or squash any variant.** All assets preserve aspect ratio.

---

## Why this works

The brand pitch is *type-safety, structure, honesty, Java craftsmanship*. The logo says exactly that:

- **Geometry says structure.** Rectangles, not blobs. Right angles, not soft fillets.
- **Two dots and a line says graph.** It's the smallest possible visualization of a typed module dependency.
- **Italic serif says gravitas.** Georgia italic carries weight without shouting. It's the tone of a white paper, not a startup pitch deck.
- **The amber is rationed.** It marks only the things that *terminate* — the dots, the call to action, the named records. Never decoration.
- **It scales.** Full lockup at 1600 viewBox-units. Mark at 1024. Favicon at 1024 simplified. One concept survives every reduction.

The logo is small enough to fit on a button and concrete enough to put on a slide. That's all it has to do.
