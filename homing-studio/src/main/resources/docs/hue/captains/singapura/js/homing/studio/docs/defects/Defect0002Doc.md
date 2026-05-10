# Defect 0002 — Themes Vary Paint, Not Form; No Component Primitive

| Field | Value |
|---|---|
| **Status** | **Open** — diagnosis recorded; resolution drafted in [RFC 0003](#ref:rfc-3). |
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

[RFC 0003](#ref:rfc-3) drafts the resolution: a `Component<C>` + `ComponentImpl<C, TH>` primitive, the doctrine in §4 above as a foundational principle, a complete atom vocabulary so the doctrine has no escape hatches, and three scope flavours (atoms + chrome / + Mode B + assets / + conformance test) with v2 (the proposed v1 target) covering both this defect and the doctrine end-to-end.

When RFC 0003 ships, this defect closes; the RFC's docs replace it.
