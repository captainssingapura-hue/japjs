# 0.0.11 — Cascade Layers, Retro 90s, Reframes

| Field | Value |
|---|---|
| **Version** | 0.0.11 |
| **Released** | 2026-05-12 |
| **Predecessor** | 0.0.10 (no formal release notes — this is the first version that ships with them) |
| **Highlight** | Defect 0003 resolved with a typed cascade-layer ladder; Defect 0002 resolved by reframing rather than by adding a primitive. The framework's CSS contract is now type-checked end-to-end. |

---

## Summary

0.0.11 is a *foundations* release. No new app-layer features for downstream studios — what changed is the framework's CSS contract, the doctrine that explains it, and one new theme that demonstrates how far the contract reaches. Two open defects close (one fixed structurally, one reframed). One new RFC sets up the next compositional layer.

The headline is the **typed cascade-layer ladder** (`Layer` sealed interface + `InLayer<L>` generic guard marker + framework CSS serving wrapped in native `@layer` blocks). Combined with `ThemeGlobals.chunks()`, themes can now reshape any selector at any specificity *deterministically*, without `!important` and without per-bundle load-order rituals. That capability is what closes Defect 0003 mechanically *and* lets Defect 0002 close conceptually — themes are paint + shape (CSS-only is enough), behavior is view-layer territory (no `ComponentImpl<C, TH>` parameterised primitive needed).

Two new themes are the visible proof. **Maple Bridge** introduces the `Theme.backdrop()` mechanism with a full-page inline-DOM SVG nocturne and per-element CSS interactivity (the hover-grow moon, driven entirely by CSS `:has()` targeting elements inside the served SVG). **Retro 90s** picks up that same backdrop pattern and pushes it into a different aesthetic — a Windows-95 workstation with iconic VGA-blue card windows, a teal desktop carrying four hover-able icons, navy gradient title bars, CRT scanlines, Fixed3D bevels, and Notepad-style reading windows that wrap `.st-doc` and `.st-sidebar` in proper application-window chrome. Neither theme required new primitives — just the cascade ladder, per-tier CSS chunks, and `Theme.backdrop()`. Two themes built on the same machinery, demonstrating it scales.

---

## What shipped

### Framework primitives (homing-core)

- **`Layer`** sealed interface with 7 permitted non-sealed sub-interfaces: `Reset`, `Layout`, `Component`, `Prose`, `State`, `MediaGated`, `ThemeOverlay`. The canonical CSS cascade ladder, expressed at the type level.
- **`InLayer<L extends Layer>`** generic guard marker. A `CssClass` opts into a tier by declaring `implements CssClass<…>, InLayer<Layout>` (etc.). Java's "no duplicate parameterised supertype" rule makes single-tier membership a *compile-time guarantee* — you cannot accidentally claim two tiers.
- **`Layers`** helper: ordered ladder (`ASCENDING`), CSS layer name mapping (`CSS_NAME`), `@layer reset, layout, component, prose, state, media, theme;` declaration generator, reflection-based extractor (`ofImplementor`) for resolving which tier a `CssClass` opted into.
- **`ThemeGlobals.chunks()`** method — returns `Map<Class<? extends Layer>, String>` of tier-tagged CSS. The framework wraps each chunk in `@layer X { … }` when serving. Back-compat `css()` is preserved and wraps in `@layer theme` by default.

### Framework serving (homing-server)

- **`CssContentGetAction`** wraps emitted CSS in `@layer reset, layout, component, prose, state, media, theme;` declaration + per-tier `@layer X { … }` blocks. Per-class rules are grouped by `Layers.ofImplementor(cssClass)` — classes without `InLayer` default to `@layer component`.
- **`ThemeGlobalsGetAction`** emits the same layer declaration + per-chunk wrappers when `ThemeGlobals.chunks()` is non-empty, falls back to wrapping `css()` in `@layer theme` for unmigrated themes.

### Studio chrome (homing-studio-base)

- **5 layout primitives** in `StudioStyles` (`st_root`, `st_main`, `st_layout`, `st_grid`, `st_list`) tagged with `InLayer<Layout>`.
- **`HomingDefault.STRUCTURAL_CHUNKS`** — the framework's chrome CSS split into 5 tier-tagged chunks (`RESET_CSS`, `COMPONENT_CSS`, `PROSE_CSS`, `STATE_CSS`, `MEDIA_GATED_CSS`). New themes consume this and add their `ThemeOverlay` chunk. The `!important` workarounds in the print stylesheet are gone — the cascade ladder makes them redundant.
- **Doc-reader column slab** — the content column on doc-reading pages gets a "page on desk" treatment (`surface-raised` background, rounded corners, soft shadow) scoped via `:has(.st-doc-meta)` so catalogue/browser/themes/plan-host pages stay edge-to-edge.
- **`Theme.backdrop()` mechanism** — new framework hook for inline-DOM SVG atmospheric layers. Themes return a `SvgRef<?>` from `backdrop()`; the framework's `AppHtmlGetAction` injects `<div class="theme-backdrop"><svg>…</svg></div>` as `<body>`'s first child, behind everything else at `z-index: -1`. SVG interior elements participate in the host document's CSS cascade — themes attach `:hover`, transitions, and animations per-element. Used first by Maple Bridge (nocturne) and Retro 90s (Win95 desktop).

### New theme: HomingMapleBridge

Activate with `?theme=maple-bridge`. Tang-dynasty inspiration — Zhang Ji's 枫桥夜泊 ("Night Mooring at Maple Bridge"). The framework's first tier-3 layered theme:

- **Inline-DOM SVG backdrop** — new `HomingMapleBridgeBg` SvgGroup serving a 680×380 viewBox `nocturne.svg`. This is the introduction of the `Theme.backdrop()` mechanism that Retro 90s later builds on. The SVG carries its own night/dawn palettes internally via `@media (prefers-color-scheme)` — independent of the studio's semantic-token system, so the SVG flips with the OS preference while the chrome flips with the theme picker.
- **Per-element CSS interactivity from the host stylesheet** — every salient element inside the served SVG (the moon, the temple window, the mountain layers, the water reflections) is classed, so the theme's CSS can wire `:hover`, transitions, and animations per-element. Nothing in JS; the framework's inline-DOM injection puts these elements directly in the cascade.
- **Moon hover-grow** — the canonical first demonstration of the `:has()` pattern Homing leans into. Hovering the moon scales the lit disk *and* the crescent shadow mask in lockstep (so the crescent keeps its shape, just bigger), plus a drop-shadow phosphor glow. Three SVG attributes animate via CSS `transition` on `r`, `cx`, `cy`. The hover state is detected on the moon and applied to the shadow via `.theme-backdrop:has(.mb-moon:hover) .mb-moon-shadow { … }`.
- **Pointer-events plumbing** — first theme to solve the "backdrop at `z-index: -1` can't receive hover" problem with universal `body, body *` pass-through + selective restoration on interactive surfaces and the moon. Documented as a reusable pattern; Retro 90s reuses it verbatim.
- **Dual-palette `Vars`** — dawn (warm gold sky, brick links, ink temple slate) for light mode, distilled independently from but resonant with the SVG's dawn palette. Dark-mode override re-binds tokens for the night scene.
- **Translucent reading panes on doc-reader** — `.st-doc` uses `color-mix(in srgb, var(--color-surface-raised) 88%, transparent)` so the nocturne bleeds faintly through the parchment behind prose, giving the reading surface a "lit from behind" feel without losing legibility.

### New theme: HomingRetro90s

Activate with `?theme=retro-90s`. Windows-95 era trading-workstation aesthetic:

- **Palette** — Win95 desktop teal `#008080` chassis, VGA blue `#0000A8` card windows, Win95 chrome grey `#C0C0C0` header/footer task bars, bright white text on dark surfaces, cyan `#55FFFF` muted labels, amber `#FFFF55` accent. Spacing scale tightened (terminals don't breathe). All radii zero (the 1990s had no rounded corners).
- **Card-window reshape** — `.st-card` becomes a Win95 window: zero border-radius, asymmetric inset-shadow Fixed3D bevel, navy-gradient title-bar strip via `::before` with `▸` glyph, full-width 1px white border replacing the framework's `border-left: 4px` accent.
- **Inline-DOM desktop backdrop** — new `HomingRetro90sBg` SvgGroup serving a 1200×800 viewBox SVG with four iconic icons: My Computer (CRT monitor + tower), My Documents (manila folder), Network Neighborhood (globe + two PCs + cable), Recycle Bin (with teal recycle band). Each icon has a per-class hook (`.w95-icon-mycomputer` etc.) for individual targeting. Pinned to the left edge via `xMinYMid slice`.
- **Icon hover-grow** — subtle 6% scale-up using the independent CSS `scale` property (composes with each icon's `transform="translate(…)"` attribute; would have collided with `transform: scale(…)`). No glow, no flare — Win95 icons didn't dramatically light up on hover, they just felt slightly more *there*.
- **CRT scanline overlay** — fixed-position `body::before` with a 3px-pitch repeating-linear-gradient at 12% opacity. Atmospheric without competing with text.
- **Notepad-style reading windows** — `.st-doc` and `.st-sidebar` become Win95 application windows on the doc-reader page: navy gradient title bars (`📄 Document Reader`, `📑 Outline`), cream `#FFFFE1` content surfaces, period-accurate Tahoma sans-serif body for long-form prose readability (with inline code and `<pre>` blocks keeping monospace). Hyperlinks rebind to canonical `#0000EE` blue + `#551A8B` visited purple.
- **Status-bar doc-meta strip** — `.st-doc-meta` styled as a sunken-bevel Win95 status bar (grey chassis, inverted bevel — top-left dark, bottom-right white).
- **Pointer-events plumbing** — universal `body, body *` pass-through (so the backdrop receives hover at `z-index: -1`) with selective restoration on user-interactive elements, the icons, and now the reading panes so text selection inside the windows works.

### Documentation

- **[Defect 0003](#ref:def-3) — Two-Bundle CSS Cascade** — created and immediately closed with the cascade-layer ladder fix. Full §8 resolution narrative explaining the typed ladder + `@layer` plumbing + why `!important` workarounds dropped.
- **[Defect 0002](#ref:def-2) — Themes Vary Paint, Not Form** — closed with §7 resolution narrative. Reframed: themes vary paint + shape (both CSS); themes do *not* vary behavior (DOM, JS, events — view-layer territory, addressed orthogonally by RFC 0003's `Component<C>` primitive). The proposed `ComponentImpl<C, TH>` is *dropped*; the Retro 90s card reshape is cited as the worked refutation of "cards can't be reshaped per theme."
- **[Encapsulated Components doctrine](#ref:doc-encapsulated)** — new doctrine codifying that a component owns its markup *and* its behavior *and* its CSS, all through typed Java declarations. Acknowledges the 20-year front-end fight against cascade leakage (BEM, CSS Modules, CSS-in-JS, Tailwind) and explains why Homing can win where others couldn't: every selector flows through a `CssClass` record, so the encapsulation contract is enforceable at the type level.
- **[RFC 0006](#ref:rfc-6) — Writing-Media Textures + Wallpaper Backdrops** — Draft. Proposes a second independent theme dimension: writing-medium (vellum / silk / bamboo / papyrus — applied to content surfaces) separate from wallpaper (atmosphere — `Theme.backdrop()`). Includes the seed library plan for the 10-SVG historical-textures pack. No implementation yet.

---

## Numbers

| Module | Tests passing | Net file count |
|---|---|---|
| `homing-core` | (in reactor) | +9 new types (Layer, InLayer, Layers, 7 layer sub-interfaces) |
| `homing-server` | 39 tests | 2 actions migrated to `@layer` emission |
| `homing-studio-base` | 83 tests | +2 themes (Maple Bridge, Retro 90s) + 2 SVG backdrops (`nocturne.svg`, `desktop.svg`) — first themes to use the new `Theme.backdrop()` mechanism |
| `homing-studio` | 138 tests | +4 docs (Defect 0003, Encapsulated Components, RFC 0006, this release) |

Reactor `mvn install` clean. No failing tests. No skipped tests. Conformance suite green across the board.

---

## Cascade ladder ↔ doctrine

The two foundational changes work in lockstep:

| Mechanism | Doctrine it enables |
|---|---|
| `Layer` sealed interface + `InLayer<L>` guard | Every CssClass declares its cascade tier at the type level → framework can enforce *where* a rule belongs. |
| `@layer` wrapping in served CSS | Browser honours the ladder regardless of load order → deterministic cross-bundle cascade. |
| `ThemeGlobals.chunks()` tier-tagged content | Themes can override at any tier without escalating specificity → no `!important`. |
| Encapsulated Components doctrine | Codifies the contract: component owns markup + behavior + CSS. The CSS half is now enforceable, not just aspirational. |

Together, this is what the front-end industry has been routing *around* for 20 years. Homing's answer — typed `CssClass` records flowing through a single CSS serving pipeline — only works because every selector exists in the type system before it exists on the wire.

---

## Compatibility

- **No breaking changes** to public APIs in `homing-core`, `homing-server`, or `homing-studio-base`.
- **`ThemeGlobals.css()`** still works exactly as before — the framework wraps unmigrated themes in `@layer theme` automatically. Themes that opt into `chunks()` get finer-grained tier control.
- **New `InLayer<L>` interface** is opt-in. CssClass records without it default to `@layer component` — the right tier for the bulk case.
- **Browser support floor** for `@layer`: Chrome 99 / Firefox 97 / Safari 15.4 — all shipped Q1 2022.
- **`:has()` selector** (used by the doc-reader slab scope): Chrome 105 / Firefox 121 / Safari 15.4. Universal in modern engines.

Downstream studios on 0.0.10 upgrade with a single dependency bump — no code changes needed unless they want to opt into the new primitives.

---

## What's next

The work paths visible from 0.0.11's vantage point:

- **RFC 0006 implementation** — the writing-media library, the `/writing-medium` endpoint, the `--writing-medium-svg` variable hookup in `.st-card`/`.st-doc`, the Letterpress migration as worked example. Estimated 4–6 hours of focused work.
- **RFC 0003 Component primitive (re-scoped)** — now that the per-theme `ComponentImpl<C, TH>` is dropped, the remaining `Component<C>` is a tighter, simpler primitive: typed view-layer encapsulation, no theme parameter, supports the no-innerHTML doctrine. Implementation cost much lower than the original RFC anticipated.
- **Theme catalogue expansion** — Bauhaus, Sunset, Forest, Forbidden City all still ride the back-compat `css()` path. Migration to `chunks()` is mechanical (~30 lines per theme) and unlocks per-tier theme overrides.

Nothing is blocking. The framework is shippable at 0.0.11.
