# Doctrine — Themes as Perceivable Surface

| Field | Value |
|---|---|
| **Status** | Active — codifies the cumulative thinking from [Defect 0002](#ref:def-2) (paint+shape) through [RFC 0007](#ref:rfc-7) (audio) and extends to the proposed interactive-theme realm. |
| **Audience** | Framework authors first; theme authors as consumers; downstream studios as users of the theme catalogue. |
| **Adopted** | 2026-05-12 |
| **Builds on** | [Defect 0002](#ref:def-2), [Defect 0003](#ref:def-3) cascade ladder, [RFC 0007](#ref:rfc-7) audio cues. |

---

## 1. The claim

A theme varies **what the user perceives**. A theme does not vary **what the page does**.

That single sentence is the doctrine. Everything below is the precise definition of each side of it and the worked consequences.

---

## 2. The five dimensions of perceivable surface

A theme owns five orthogonal dimensions:

| # | Dimension | Mechanism | Worked example |
|---|---|---|---|
| 1 | **Palette** | `ThemeVariables<TH>` returning `Map<CssVar, String>` | every theme has one |
| 2 | **Shape** | `CssClass` records + `ThemeGlobals.chunks(ThemeOverlay)` | Retro 90s reshapes `.st-card` as Win95 windows |
| 3 | **Atmosphere** | `Theme.backdrop()` returning `SvgRef<?>` | Maple Bridge nocturne; Retro 90s desktop |
| 4 | **Sound** | `Theme.audio()` returning `ThemeAudio<TH>`; typed `Cue`s synthesised via Tone.js | Maple Bridge temple bell; Retro 90s system click |
| 5 | **Ambient interactivity** | (proposed in [RFC 0007](#ref:rfc-7)'s successors) opt-in keyboard mode, persistent ambient state, theme tick loops | Jazz Drum Kit (Phase 1), Animal walk-across (Phase 3) |

The dimensions compose freely. A theme picks any subset. A "Plain" theme picks none and uses framework defaults. A "Jazz Drums" theme picks all five.

The dimensions are **independent**: a writing-medium texture (Vellum, per [RFC 0006](#ref:rfc-7)) composes with any palette, any backdrop, any audio. The Maple Bridge nocturne composes with the moon-bell click cue and (proposed) with a wind-chime keyboard mode. No cross-dimension coupling.

---

## 3. The one bright line — what a theme never crosses

Three things a theme **does not vary**:

| Forbidden | What it means | What it would break |
|---|---|---|
| **Page content semantics** | A theme cannot change what a doc says, what a catalogue lists, what a tracker tracks. | The reader's trust — they expect the *content* to be stable across theme changes. |
| **Navigation logic** | A theme cannot rewrite where a link goes or what a button does. | The site's contract — links are part of the site, not the theme. |
| **Data flow** | A theme cannot intercept fetches, write to app state, persist anything outside its own per-theme sandbox. | Application correctness — themes that touch app state become application logic in disguise. |

These are doctrinal absolutes. A theme that crosses any of them isn't a theme — it's an `AppModule` with delusions.

The boundary is **structural**, not policy: the theme's DOM tree (`.theme-backdrop` + the control panel) is rooted separately from the content tree (`.st-main` + `.st-doc` + everything inside). CSS layers + cascade ordering keep theme styles to theme territory. State sandboxing keeps theme state to the `homing-theme:<slug>:*` localStorage namespace. The framework enforces these; theme authors don't have to remember.

---

## 4. The doctrine's evolution

This doctrine wasn't born formed — it accreted through four moments:

### 4.1 Original framing (pre-Defect 0002)

> Themes vary colours and sizes. Anything more is a feature request.

The framework had `ThemeVariables` (palette) and `ThemeGlobals` (raw CSS), and that was the surface. [Defect 0002](#ref:def-2) (open) named the gap: themes couldn't reshape a card, couldn't swap an SVG path, couldn't change anything structural. The defect proposed a `ComponentImpl<C, TH>` primitive.

### 4.2 The paint+shape reframe (Defect 0002 resolution)

> Themes vary **paint + shape**, not behavior. Both paint and shape are CSS; the Defect 0003 cascade ladder makes the second deterministic.

[Defect 0003](#ref:def-3)'s typed `Layer` ladder + `@layer` serving made cross-bundle CSS overrides deterministic. The proposed `ComponentImpl<C, TH>` collapsed — themes can reshape any selector via `@layer theme` overrides without a per-theme component variant. The Retro 90s card-reshape is the worked refutation.

### 4.3 The audio extension (RFC 0007)

> Themes vary **perceivable surface** — sight AND sound. Themes do not vary control logic.

Audio cues felt like behavior (event-driven side effects) but turned out to fit the existing pattern: the theme contributes media + parameters; the framework owns the event handler and the playback machinery. Same shape as "the theme contributes a colour; the framework owns the rendering pipeline." RFC 0007 introduces `ThemeAudio<TH>`, typed `Cue` records, click-bound to typed `ClickTarget`s.

### 4.4 The interactive extension (proposed)

> Themes vary perceivable surface by default. Themes MAY offer **opt-in interactive modes** the user explicitly enables. Themes MAY carry **ambient state** that persists across navigation, within a fixed sandbox. Themes never alter page semantics.

This is the next frontier — captured in the doctrine but spec'd in successor RFCs. The key constraints:

- **Opt-in.** Keyboard play mode, drum-kit interactivity, ambient game state — all OFF by default. The user enables via a control panel toggle, persisted to per-theme localStorage.
- **Sandbox-bounded.** Themes get exactly five capabilities: localStorage, `requestAnimationFrame`, pointer/keyboard events (only in opt-in modes), audio synthesis, inline SVG manipulation. No network, no camera, no permissions, no app state.
- **Page-semantics-untouched.** The doc still says what it says; the catalogue still lists what it lists. Themes coexist alongside content; they never replace it.

---

## 5. The five capabilities in detail

A theme's sandbox is exactly:

1. **`localStorage` within the `homing-theme:<slug>:*` key namespace.** Per-theme preferences, ambient state, user overrides. Keys outside that namespace are off-limits.
2. **`requestAnimationFrame` callbacks** invoked when the theme is active and the tab is focused. The framework manages the lifecycle (start on theme activation, pause on tab blur, stop on theme deactivation).
3. **Pointer and keyboard events**, but only in opt-in modes. Click cues are passive — they only fire when a user clicks a theme-bound element. Keyboard cues require explicit "play mode" enabled by the user.
4. **Audio synthesis via the framework's bundled Tone.js.** No external audio files; cue parameters are typed records serialised at JS-gen time. The framework owns the audio context lifecycle and the destination graph.
5. **Inline SVG manipulation within `.theme-backdrop`.** The theme's backdrop SVG is the theme's DOM tree. Themes can animate elements within it, add transient classes, transform attributes — but cannot reach into `.st-main` or any content tree.

A theme that needs to:

- Make a network request → it's an `AppModule`, not a theme.
- Write to a global app state → it's an `AppModule`, not a theme.
- Render content based on app data → it's an `AppModule`, not a theme.

The boundary is bright. Themes that cross it are using the wrong abstraction.

---

## 6. What this means for theme authors

For someone writing a new theme:

1. **Start from the five dimensions.** Pick which you'll vary. A minimal theme is just palette. A maximalist theme touches all five.
2. **Compose from typed primitives.** `ThemeVariables<TH>` for palette, `InLayer<L>` for shape tier, `SvgRef<?>` for atmosphere, `ThemeAudio<TH>` for sound. Theme authors compose records and constants; the framework generates everything that ships.
3. **Respect the user's opt-in.** Interactive modes default OFF. The control panel toggle is the user's lever, not yours.
4. **Test your sandbox limits.** If you find yourself wanting `fetch()` or `window.localStorage.setItem("global-something")`, stop — you're writing an AppModule, not a theme.
5. **Themes are atmospheric, not load-bearing.** A user should be able to switch themes mid-task without losing work. State that matters belongs to the app, not the theme.

---

## 7. What this means for downstream studios

For a studio author picking themes:

1. **The theme catalogue is a curated set.** Each theme makes a coherent aesthetic + interactive promise. Switching themes is safe — content, navigation, and data are unchanged.
2. **Themes have a kind.** (Proposed) — `ATMOSPHERIC | INSTRUMENT | AMBIENT_GAME | MINIMAL`. The picker can group / filter so users know what they're getting.
3. **Performance has a ceiling.** Themes that animate respect `prefers-reduced-motion`. The control panel includes a "reduced" / "off" toggle for the ambient layer. Low-end hardware degrades gracefully.
4. **The framework defaults are opinionated.** Audio defaults to ON (with a prominent mute), interactivity defaults to OFF. A studio that doesn't like the defaults can override via per-installation configuration.

---

## 8. Why this works

The doctrine works because the framework's typed primitives already enforce structural separation:

- `Theme.backdrop()` returns an `SvgRef<?>` that's rendered inside `<div class="theme-backdrop">`, behind everything content-side. The theme's DOM is structurally outside the content's DOM.
- `ThemeAudio<TH>` bindings target `ClickTarget`s that carry classToken constants. The runtime walks up from `event.target` via `closest()` — events on content elements never match theme bindings.
- The Defect 0003 cascade ladder puts theme CSS in `@layer theme`, which wins over component-layer CSS at any specificity — themes can restyle anything without `!important`, but cannot escape their layer's semantic.
- (Proposed) Theme state lives in a namespaced localStorage key. The framework provides the read/write API; themes cannot accidentally see or write app state.

Each new dimension extends the doctrine because the structural enforcement is already in place. We're not adding policies; we're adding capabilities within a sandbox the framework already maintains.

---

## 9. The four worked themes (post-RFC 0007)

| Theme | Palette | Shape | Atmosphere | Sound | Interactive |
|---|---|---|---|---|---|
| **Homing Default** | ✓ | (framework defaults) | — | — | — |
| **Maple Bridge** | ✓ | (translucent slab override) | ✓ nocturne | ✓ temple bell, moon chime, lamp crackle | (moon hover-grow CSS only) |
| **Retro 90s** | ✓ | ✓ Win95 windows + Notepad reading panes | ✓ Win95 desktop with 4 icons | ✓ icon clicks + card thuds | (icon hover-grow CSS only) |
| **Letterpress, Bauhaus, Sunset, Forest, Forbidden City** | ✓ | (minimal overrides) | — | — | — |

Every active theme exercises a different subset of the dimensions. The doctrine isn't theoretical — it describes what's already shipping.

---

## 10. Open extensions (not yet codified)

These would live in successor doctrine doc revisions when committed:

- **Writing-medium textures** ([RFC 0006](#ref:rfc-7)) — a sub-dimension of "shape": content surfaces wear typed textures (vellum, silk, bamboo) independent of palette.
- **Keyboard play modes** — opt-in interactive mode for instrument-themed themes (drum kit, synth keyboard, typewriter). Specified by RFC 0008 (proposed).
- **Theme control panel** — UI surface for per-theme preferences, mute, volume, play-mode toggle. RFC 0008.
- **Persistent ambient state** — theme-scoped state that survives navigation. Animal-as-theme requires this. RFC 0009 (proposed).
- **Theme kind classification** — `ATMOSPHERIC | INSTRUMENT | AMBIENT_GAME | MINIMAL` for the theme picker. RFC 0008.

When any of these ship, this doctrine doc revises to reflect them. The doctrine is the canonical statement; RFCs are the specifications; release notes track what shipped.

---

## 11. The one-sentence test

If you're not sure whether a proposed theme feature is doctrinal:

> **Does it change what the user PERCEIVES, or what the page DOES?**

If perceives → theme territory. If does → app territory. If both → split it.

The drum kit changes what the user perceives (visual kit + sounds + opt-in keyboard play). The drum kit does NOT change what the doc reader fetches, renders, or saves. ✓ Theme.

A "save my drumming pattern" feature would change what the page does (writes user data). ✗ App, not theme.

The animal walking across pages changes what the user perceives (ambient sprite + animation). The animal does NOT change what the doc reader does. ✓ Theme.

A "high score leaderboard for animal jumps" would change what the page fetches. ✗ App, not theme.

That's the test. Apply it before building.
