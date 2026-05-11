# Defect 0002 — Themes Vary Paint, Not Form; No Component Primitive

| Field | Value |
|---|---|
| **Status** | **Resolved** (2026-05-11) — by reframing rather than by adding a new primitive. Themes vary **paint + shape** (both CSS, now cascade-deterministic per [Defect 0003](#ref:def-3) fix); themes do **not** vary **behavior** (DOM, JS, events — that's view-layer territory, separately addressed by [RFC 0003](#ref:rfc-3)'s Component primitive). See §7 below. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-08 |
| **Severity** | Design-level — caps the expressiveness of theming and forces UI duplication across every consumer. |
| **Affected modules** | `homing-studio-base` (chrome), `homing-demo` (moving-animal demo), every downstream studio. |
| **Surfaces in** | Themes can recolor a card but cannot reshape it; the moving-animal's platform stays a flat rectangle no matter the theme; every studio JS file rewrites the same card HTML by hand. |

---

## 1. Symptom

Two concrete examples surfacing the same gap:

### 1.1 Moving-animal demo

The demo's theme switch varies background colour, sky tone, and ambient music — but the platform the animal walks on stays a fixed shape. A "forest" theme should put the animal on a forest floor (grass tufts, fallen logs); a "sunset" theme on a desert dune; a "winter" theme on snow. The framework offers no way for a theme to swap the platform's *form* — only its colour.

### 1.2 Studio card duplication

Every studio AppModule that lists items (`DocBrowser`, `NotationDocBrowser`, `JourneysCatalogue`, plan trackers, …) re-emits the same card HTML inline:

```js
'<a class="' + cn(st_card) + '" ' + href.toAttr(url) + '>'
+   '<h3 class="' + cn(st_card_title) + '">' + escape(title) + '</h3>'
+   '<p class="' + cn(st_card_summary) + '">' + escape(summary) + '</p>'
+   '<div class="' + cn(st_card_meta) + '">…</div>'
+ '</a>'
```

A theme that wanted cards with parchment edges, geometric corner ornaments, or hand-drawn frames cannot deliver them — the HTML structure is fixed in every consumer's JS, and the theme system has no hook to override it.

---

## 2. Root cause

The framework's theming primitives are:

- `ThemeVariables<TH>` — `Map<CssVar, String>` (semantic tokens like `--color-surface`)
- `ThemeGlobals<TH>` — raw CSS strings for `@media`, descendant rules
- `CssGroupImpl<C, TH>` — per-CssGroup, per-theme **paint** rules

All three vary the *painting* of an element. None of them can:

- emit different DOM structures per theme
- ship different SVG path data per theme
- swap an `<img>` URL or `<audio>` source per theme
- re-arrange children

To change form, a consumer must write theme-aware logic in their own JS — which there is currently no mechanism for either. **Themes vary paint, not form**, because the framework has no primitive for "a piece of UI whose visual form is theme-keyed."

The deeper missing concept is a **component primitive**:

| Layer | Has typed identity? | Has per-theme implementation? |
|---|---|---|
| CSS class rules | `CssGroup<C>` | `CssGroupImpl<C, TH>` ✓ |
| Theme variables | (implicit per `Theme`) | `ThemeVariables<TH>` ✓ |
| Theme globals | (implicit per `Theme`) | `ThemeGlobals<TH>` ✓ |
| **UI form** | ✗ — no `Component<C>` | ✗ — no `ComponentImpl<C, TH>` |

The same parametric shape that lets `CssGroupImpl<C, TH>` give per-theme paint to a CssGroup should let a `ComponentImpl<C, TH>` give per-theme rendering to a Component.

---

## 3. Costs incurred today

- **Themes are weak.** A theme switch can repaint but cannot re-form. Visual differentiation between Default / Forest / Sunset is limited to colour palettes; deeper differentiation requires per-theme HTML in every consumer.
- **UI is duplicated.** Every studio app re-emits the same card / pill / progress-bar HTML inline. Defect 0001's per-plan boilerplate is the same problem one level up.
- **No-innerHTML doctrine has nowhere to land.** "Don't write HTML strings" is unsatisfying as a doctrine because there's no Component primitive to write *instead*. The two questions are the same question.
- **App-specific shapes are stuck.** The animal demo's platform is app-specific — even a hypothetical generic "Component" library wouldn't cover it. The framework needs a primitive that downstream apps can declare their own Components against.

---

## 4. The doctrine the gap implies

The duplication smell and the form-cannot-be-themed gap come from the same missing rule: **consumer code is allowed to write HTML at all**. As long as `appMain(rootElement)` can do `rootElement.innerHTML = '<a class="st-card">…'`, every consumer becomes a one-off, themes can't reach inside, and the framework's typed Java declarations get thrown away at the import boundary.

The strong doctrine the gap motivates:

> **No HTML in any consumer code. Every UI element in every AppModule is a Component invocation. The only HTML allowed is the outermost mount-point `<div id="app">` provided by the framework's bootstrap page.**

That's the rule. Components are what makes it expressible. RFC 0003 §2 commits to it as a foundational principle and §3.9 makes it enforceable via a conformance test.

---

## 5. Convergence with other open work

- **Defect 0001 (no app-kind kit)** — Plan trackers re-implement view-layer chrome per instance. Components solve this at the view layer the same way `PlanKit<S>` would solve it at the app layer. Components feed PlanKit: Plan/Step views become compositions of typed Card / ProgressBar / Badge / Header Components.
- **`StudioBootstrap`** — one-call server bootstrap (kit, not primitive). Components are the parallel one-call view kit. Same philosophical move.

---

## 6. Decision deferred → resolution

[RFC 0003](#ref:rfc-3) drafts a `Component<C>` + `ComponentImpl<C, TH>` primitive and the no-innerHTML doctrine in §4 above. That work is still valuable — but for view-layer encapsulation, not for theming. See §7 for what shipped.

---

## 7. Resolution — concerns separated, shape gap closed by CSS

**Ship date:** 2026-05-11.

The original framing conflated two concerns that turn out to belong to different layers:

| Concern | Layer | Who controls it |
|---|---|---|
| **Paint** — colours, sizes, typography | CSS variables + class rules | Theme |
| **Shape** — visual form: corners, borders, decorative chrome, backdrop, padding mass | CSS class rules + `::before`/`::after` + per-theme `backdrop()` SVG | Theme |
| **Behavior** — DOM structure, child arrangement, event handlers, JS state | View-layer code | **Not theme** — application or `Component` |

The defect's original symptom inventory (cards, animal platform, JS duplication) folded all three into "things themes can't change." But on reflection, only the first two *should* be theme territory. A theme that swapped event handlers or reordered DOM children would couple visual identity to behavioural surface — a recipe for themes that subtly break apps.

### 7.1 What changed

**Nothing new was added to the theme primitives.** What changed is the cascade plumbing underneath them, plus the realisation that CSS is more capable than the original defect credited.

[Defect 0003](#ref:def-3)'s typed `Layer` ladder + `@layer` serving made cross-bundle CSS overrides deterministic. Themes can now reshape any selector in any layer without `!important` and without specificity arithmetic. The cascade itself is the mechanism — the same mechanism that was always shipping, just made trustworthy.

### 7.2 What "shape" CSS can actually do

The 2022–2025 CSS surface is materially richer than the era this defect was written against. A theme working in pure CSS can:

- **Reshape any element** — `border-radius`, `border`, `box-shadow` (inset for 3D bevels), `padding`, `clip-path`, `mask-image`.
- **Synthesize decorative chrome** — `::before` / `::after` with `content` add visual structure (gradient title bars, ornamental corners, callouts) without DOM changes.
- **Swap background imagery** — `background-image: url("data:image/svg+xml,…")` lets each theme inject different SVG path data via a CSS variable. The forest theme's mossy platform and the sunset theme's dune are different SVG `d` attributes embedded in different data URIs assigned to the same `--platform-bg` variable.
- **Replace the backdrop entirely** — `Theme.backdrop()` (introduced for Maple Bridge, used again by Retro 90s) renders a per-theme inline SVG as the page atmosphere. The SVG's interior elements get classed so the *theme's CSS* can wire per-element hover/transitions — interactive backdrop without theme-driven JS.
- **Compose layered fills** — multiple `background-image` layers, `mix-blend-mode`, `filter`, `backdrop-filter` cover most "different visual texture per theme" cases.

### 7.3 Worked example — the Retro 90s card reshape

The `HomingRetro90s` theme reshapes `.st-card` from a rounded-corner left-accented tile into a Windows-95 window. **Zero DOM changes.** Pure CSS in `@layer theme`:

- `border-radius: 0`, full `1px` border on all sides (vs the default `border-left: 4px`),
- Inset asymmetric `box-shadow` for the Fixed3D bevel,
- `::before` with `content: "▸"` and a navy-gradient background paints the title bar,
- `padding: 0` on the card with re-padding on the children via `> *:first-child` / `> *:last-child`.

The card's HTML is identical to every other theme's. The visual form is unmistakably different. That's the entire defect's "cards can't be reshaped" claim, refuted in 30 lines of CSS that win the cascade by `@layer` ordering rather than by hacks.

### 7.4 What's still NOT theme territory — and shouldn't be

The defect's §1.2 listed "Studio card duplication" — every studio's JS re-emits the same card HTML inline. That's a real problem, but it's **not a theming problem**. It's a view-layer encapsulation problem: there should be a typed `Component` so consumers compose `Card(title, summary, meta)` instead of writing HTML strings.

[RFC 0003](#ref:rfc-3)'s `Component<C>` primitive still addresses that — and the [Encapsulated Components doctrine](#ref:doc-encapsulated) makes it normative. But notice the shape: `Component<C>`, not `ComponentImpl<C, TH>`. The per-theme parameter the original defect proposed is **not needed** — themes vary the *paint and shape* of a Component via CSS targeting its class names, not by emitting different DOM.

A Component's job: own its markup, its behavior, and its CSS (the doctrine). A theme's job: paint and reshape every Component on the page via CSS in `@layer theme`. The two contracts compose without ever meeting at a `ComponentImpl<C, TH>` seam.

### 7.5 Lessons banked

- **Conflating concerns inflates defects.** This one named "themes can't change DOM" as a problem when the right framing is "themes shouldn't change DOM."
- **CSS is the theming surface; it was already enough.** The Defect 0003 cascade fix unlocked what CSS could do all along.
- **Per-theme variant typeclasses (`X<C, TH>`) are a smell when the same selector + better CSS would do.** `CssGroupImpl<C, TH>` predates the chunked-globals approach and is now mostly load-bearing for back-compat; new content goes through `CssClass` bodies + theme-level CSS overrides, no per-theme impl needed.
- **The no-innerHTML doctrine survives intact** but is now framed as view-layer encapsulation (Pure-Component Views + Encapsulated Components doctrines), not as a theming concern.
