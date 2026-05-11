# RFC 0006 — Writing-Media Textures + Wallpaper Backdrops

| Field | Value |
|---|---|
| **Status** | **Draft** — design proposed; implementation not started. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-11 |
| **Targets** | `homing-studio-base` (chrome) + downstream studios. |
| **Builds on** | [Defect 0003](#ref:def-3) cascade ladder, [Defect 0002](#ref:def-2) reframe (themes vary paint+shape, not behavior), [Encapsulated Components doctrine](#ref:doc-encapsulated). |
| **Adds** | A second theme dimension orthogonal to the palette: *which writing medium does text sit on*, separated from *what wallpaper hangs behind the page*. |

---

## 1. The motivating insight

A pack of 10 SVG textures landed in `R:\Temp\designs\files2\historical-textures\texture-pack` — vellum, silk, bamboo, papyrus, xuan paper, plus a dark counterpart for each (lacquered bamboo, indigo silk, slate, leather, vellum-candlelight). Every one of them is a **writing surface**: a physical material humans have inscribed text onto across cultures and eras.

The naive integration would treat them as page backdrops, replacing or augmenting the existing `Theme.backdrop()` SVGs (Maple Bridge's nocturne, Retro 90s's Win95 desktop). That's the wrong fit. Reading a markdown doc on top of a "Maple Bridge nocturne" backdrop is fine — the nocturne is *scenery*, the doc surface is its own thing. Reading the same doc on top of a *vellum* backdrop is structurally confused: the vellum *is* the doc surface, but it's behind the doc surface, so the doc is sitting on a meta-vellum-pretending-to-be-vellum.

The right framing — and what this RFC formalises:

| Texture role | What it is | Where it goes | Existing example |
|---|---|---|---|
| **Writing medium** | The substrate text sits on: parchment, silk, paper, bamboo. | Content-bearing surfaces: `.st-card`, `.st-doc`, `.st-list-item`, blockquotes, prose `<pre>` blocks. | None yet — Letterpress comes closest with body-level paper grain, but that conflates the two roles. |
| **Wallpaper** | The atmosphere/scene the page sits in front of: a Tang-dynasty lake at night, a Windows-95 desktop, a Bauhaus colour field. | `Theme.backdrop()` — the framework's existing inline-DOM SVG layer behind everything. | Maple Bridge `nocturne`, Retro 90s `desktop`. |

Themes get a **second independent dimension**: not just palette + optional backdrop, but **palette + writing medium + optional wallpaper**. A single theme can mix-and-match — *vellum-on-nocturne*, *bamboo-on-courtyard*, *xuan-on-mountains*, *plain-on-desktop*.

This is the kind of structural insight that's hard to see until the wrong fit slaps you in the face. The texture pack arrived, my instinct was "add 10 more themes," the user's correction was "no — writing media is for content surfaces; wallpaper is for the page; these are different things." That's the whole RFC in one sentence.

---

## 2. The two roles in detail

### 2.1 Writing-medium textures (content surfaces)

A *writing-medium texture* is applied to any DOM element whose primary purpose is to hold text the user reads. The texture **becomes the page in the reading metaphor**. The reader perceives the text as inked onto the medium, the way a real scribe would have inked onto vellum or brushed onto silk.

Concretely the surfaces that should receive a writing-medium texture:

- `.st-card` — every catalogue tile (the card *is* a piece of writing surface — title and summary inscribed on it)
- `.st-card-featured` — same role, larger
- `.st-doc` — the article-body container; the central reading pane
- `.st-list-item` — list-style enumeration entries
- `.st-doc blockquote` — quoted passages (in a real codex, often inked on a different ground)
- `.st-doc pre` — code blocks (a working surface, ink-on-paper)
- `.st-sidebar` — table-of-contents pane (a smaller, secondary writing surface)

These are exactly the surfaces the [Encapsulated Components doctrine](#ref:doc-encapsulated) identifies as content-bearing. The doctrine already says these surfaces are framework-owned; this RFC adds *what they should look like under the writing-medium dimension*.

The texture SVG goes inside each surface, not behind the page. CSS:

```css
.st-card {
    /* Writing-medium texture as an inline-SVG background. The data URI carries
       the SVG verbatim; CSS variables on .st-card propagate INTO the SVG via
       the `var(--tex-base-1, fallback)` pattern the texture pack uses. */
    background-image: var(--writing-medium-svg);
    background-size: cover;
    background-position: center;
}
```

Or — if the surface is structurally complex enough — the texture becomes an actual SVG child element rendered as the surface's first child via a `Component`. Both approaches are valid; the simpler `background-image` data-URI is the right starting point.

### 2.2 Wallpaper backdrops (page atmosphere)

A *wallpaper backdrop* is the page-level atmosphere the content sits in front of. It's **scenery**, not substrate. Reading the page should feel like "I'm in a room with a view" (nocturne, desktop, courtyard) rather than "the text I'm reading is etched onto this scenery."

Wallpapers are bigger, scenic, often unique to a theme. They are what `Theme.backdrop()` already serves:

- Maple Bridge — `nocturne.svg`, a Tang-dynasty lake scene with a hover-grow moon
- Retro 90s — `desktop.svg`, a Win95 desktop with iconic icons

Future wallpapers could include:

- Bauhaus — geometric colour fields
- Forest — woodcut treeline
- Sunset — gradient sky with silhouetted mountains
- Forbidden City — palace courtyard at dusk
- Maple Bridge dawn — same scene at sunrise (already in the SVG's media-query variant)

A theme **may or may not** ship a wallpaper. Themes that omit `backdrop()` get a solid `--color-surface` background (the current default for Letterpress, Bauhaus, Sunset, Forest, Forbidden City).

### 2.3 The orthogonality

The two dimensions don't constrain each other. A theme picks zero or one wallpaper *and* zero or one writing-medium independently:

| Wallpaper \\ Medium | (none) | Vellum | Silk | Bamboo | Xuan |
|---|---|---|---|---|---|
| **(none)** | Default | Letterpress-redone | Silk Scroll | Bamboo Strip | Rice Paper |
| **Maple Bridge** | (current) | "Letters from the Lake" | "Silk Scroll, Night Mooring" | etc. | etc. |
| **Retro 90s** | (current) | n/a — anachronism | n/a | n/a | n/a |
| **Bauhaus** | — | — | — | — | — |

The anachronism cell isn't a hard prohibition — it's a curation note. Nothing in the framework prevents `Retro 90s wallpaper + Vellum writing medium`; it just looks like a joke. The framework allows it, the theme author chooses what's tasteful.

---

## 3. Architecture

### 3.1 New primitive — `WritingMedium`

Sibling to `ThemeVariables`, `ThemeGlobals`, `ThemeOverlay`, etc.:

```java
public sealed interface WritingMedium<TH extends Theme> {
    /** Identity — which theme this medium is bound to. */
    TH theme();

    /** The inline SVG content (texture pack format), served as a CSS variable
     *  the chrome's content surfaces read as `background-image: var(--writing-medium-svg)`. */
    String svg();

    /** CSS variable overrides specific to this medium — colours, grain intensity,
     *  thread opacity, etc. Returned as a Map<String, String> so the framework
     *  can emit them as a :root { --tex-base-1: …; --tex-grain-a: …; } block. */
    Map<String, String> variables();
}
```

A `Theme` optionally returns one:

```java
public interface Theme {
    // … existing surface …
    default Optional<WritingMedium<?>> writingMedium() { return Optional.empty(); }
}
```

Themes that opt in:

```java
public record HomingVellum() implements Theme {
    @Override public Optional<WritingMedium<?>> writingMedium() {
        return Optional.of(HomingVellum.Medium.INSTANCE);
    }
    // … Vars, Globals, etc.

    public record Medium() implements WritingMedium<HomingVellum> {
        public static final Medium INSTANCE = new Medium();
        @Override public HomingVellum theme() { return HomingVellum.INSTANCE; }
        @Override public String svg() { return VELLUM_SVG; }  // texture-pack SVG, inlined
        @Override public Map<String, String> variables() { return VARIABLES; }
        // …
    }
}
```

### 3.2 A new served endpoint — `/writing-medium`

Parallel to `/theme-vars` and `/theme-globals`:

```
GET /writing-medium?theme=vellum
→ Content-Type: text/css
→
@layer theme {
    :root {
        --tex-base-1: #ede0c8;
        --tex-base-2: #e4d4b8;
        /* … all tex-* variables from WritingMedium.variables() … */
        --writing-medium-svg: url("data:image/svg+xml;utf8,<svg …>…</svg>");
    }
}
```

The SVG goes in a data URI assigned to `--writing-medium-svg`; the texture pack's per-texture CSS variables are emitted alongside. Themes that don't ship a medium → 200 with empty body (consistent with how `/theme-globals` handles empty cases).

### 3.3 New CssClass rules in `StudioStyles`

Cascade-tier `Component` (the implicit default), wired to use the writing-medium variable:

```java
public record st_card() implements CssClass<StudioStyles>, /* no InLayer → Component */ {
    @Override public String body() { return """
        background-color: var(--color-surface-raised);
        background-image: var(--writing-medium-svg);  /* writing-medium texture */
        background-size: cover;
        background-position: center;
        /* … rest of card chrome … */
        """;
    }
}
```

When `--writing-medium-svg` is unset (theme doesn't ship a medium), `background-image: <unset>` falls through to no image — the solid `background-color` shows. Themes that DO ship one paint the medium over the colour.

### 3.4 What stays unchanged

- `Theme.backdrop()` — wallpaper layer. No semantic change.
- `ThemeVariables` — palette. No change.
- `ThemeGlobals.chunks()` — per-tier CSS overrides. No change.
- `Theme.Globals.css()` — back-compat. No change.

This RFC **adds a fifth theme surface** (writing medium) alongside the existing four. It doesn't restructure the existing ones.

---

## 4. The texture pack as the seed library

The pack at `R:\Temp\designs\files2\historical-textures\texture-pack` is the obvious initial library of writing media. The 10 SVGs are each 30–40 lines, themeable via consistent `--tex-*` variables, and already use the `viewBox="0 0 680 380"` + `preserveAspectRatio="xMidYMid slice"` convention Maple Bridge established.

Proposed copy locations:

```
homing-studio-base/src/main/resources/homing/svg/
  hue/captains/singapura/js/homing/studio/base/theme/
    writingmedia/
      vellum.svg
      vellum-candlelight.svg
      silk.svg
      indigo-silk.svg
      bamboo.svg
      lacquered-bamboo.svg
      xuan-paper.svg
      slate.svg
      papyrus.svg
      leather.svg
```

A small Java registry — `WritingMediaLibrary` — exposes each one as a constant `WritingMedium<?>` instance. Themes pick from the library, or roll their own.

### 4.1 Light/dark pairing — not inversion

The texture pack's README makes a sharp point: *"dark-mode parchment isn't a thing — light cellulose surfaces don't exist in the dark unless lit."* The dark counterparts in the pack are different historical artifacts entirely. This aligns with the Defect 0002 reframe: theming is paint+shape, *not* an automatic colour inversion. Light/dark for writing media is **a theme-level decision**, not an `@media (prefers-color-scheme: dark)` automation.

The recommended pairings (per the pack's README):

| Light theme medium | Dark theme medium | Cultural pair |
|---|---|---|
| `vellum` | `vellum-candlelight` | Same recipe, two lightings — closest "matched pair" |
| `silk` | `indigo-silk` | Aged silk ↔ indigo-dyed silk used for gold-ink Buddhist sutras |
| `bamboo` | `lacquered-bamboo` | Natural bamboo strips ↔ black-lacquered strips inscribed in gold/red |
| `xuan-paper` | `slate` | Rice paper ↔ the ink stone you grind ink on (working surface) |
| `papyrus` | `leather` | Reed weave ↔ tooled European bookbinding leather |

A theme that wants automatic OS-preference adaptation can return *different* `WritingMedium`s under the `@media (prefers-color-scheme: dark)` branch of its `ThemeGlobals`. A theme that wants a single fixed look picks one.

---

## 5. Text-colour cohesion

The texture pack ships suggested ink colours per medium (from the README):

| Medium | Suggested text colour |
|---|---|
| Vellum, Xuan, Silk, Papyrus | `#3a2818` deep brown ink |
| Bamboo | `#f0e0c0` warm cream |
| Vellum candlelight | `#e8d4a8` amber |
| Leather | `#d8a868` gold leaf |
| Indigo silk | `#c8d0e0` silver ink |
| Slate | `#d8dde4` light gray |
| Lacquered bamboo | `#d8b888` gold leaf |

A theme that ships a writing medium **should also rebind** the relevant text-on-medium tokens (probably `--color-text-primary` and `--color-text-muted`) to match. This is the same pattern themes already follow for `--color-text-on-inverted` — pick text colour that reads against the surface beneath it.

Recommendation: `WritingMedium.variables()` may also rebind `--color-text-primary` etc., though the cleaner design keeps writing-medium variables in the `--tex-*` namespace and lets the theme's `Vars` rebind text tokens to match. The latter keeps the medium library generic and the theme owning the coordination.

---

## 6. Worked examples (proposed themes, not yet built)

### 6.1 Theme: Vellum

- **Palette:** warm cream surfaces, deep-brown text — distilled from the vellum SVG's palette.
- **Writing medium:** `vellum.svg`.
- **Wallpaper:** none — `backdrop()` returns the default. The page is `--color-surface` (parchment cream), every content surface wears the vellum texture.
- **Vibe:** monastery scriptorium, one continuous parchment field with cards as page-blocks.

### 6.2 Theme: Silk Scroll

- **Palette:** warm tan with deep-brown ink.
- **Writing medium:** `silk.svg`.
- **Wallpaper:** none, or — stretch goal — a thin painted-silk border around the page edges.
- **Vibe:** Song-dynasty handscroll, cards as silk panels.

### 6.3 Theme: Bamboo Slip

- **Palette:** tan with cream text — bamboo is dark enough that primary text inverts.
- **Writing medium:** `bamboo.svg`.
- **Wallpaper:** none — the bamboo strips on the cards do the visual work.
- **Vibe:** pre-paper Han-dynasty bamboo strips, each card a bound slip.

### 6.4 Theme: Maple Bridge Vellum

- **Palette:** existing Maple Bridge.
- **Writing medium:** `vellum-candlelight.svg`.
- **Wallpaper:** existing Maple Bridge `nocturne` (already shipping).
- **Vibe:** a candle-lit scriptorium on a lake at night. Maple Bridge gets richer without replacing what's there.

### 6.5 Theme: Letterpress, upgraded

- The existing Letterpress theme builds its parchment via inline `feTurbulence` in `ThemeGlobals.css()`. This RFC's writing-medium primitive subsumes that — Letterpress migrates to use `vellum.svg` as its writing medium and drops the hand-rolled SVG-data-URI body background.
- Net: ~30 lines of CSS removed from `HomingLetterpress`, behaviour functionally equivalent, theme becomes a downstream example of the new pattern rather than a parallel implementation of the same idea.

---

## 7. Open questions

1. **Scope of v1 — library only, or library + themes?** Two options:
   - **(a)** Ship just the `WritingMedium` primitive + the 10-texture library + the `/writing-medium` endpoint + the `st-card`/`st-doc` CSS hooks. **No new themes.** Existing themes pick the new path opt-in (Letterpress migrates as proof). Downstream studios get the toolkit; future themes (in this repo or downstream) build on it.
   - **(b)** Ship everything in (a) + 5 new themes (Vellum, Silk Scroll, Bamboo Slip, Maple Bridge Vellum, an indigo-silk variant). More immediately visible value, more authoring cost.

   **Recommendation:** (a). The Defect 0002 reframe argued that we should resist primitive proliferation and reach for the simpler mechanism. Same principle here — ship the primitive and one migration (Letterpress) to validate, leave theme authoring to wherever creative energy lands.

2. **`background-image` data URI vs inline DOM.** Data URIs are simpler but lose per-element interactivity (no hover on the silk threads, etc.). Inline DOM (the `Theme.backdrop()` pattern) allows interactivity but requires DOM injection at every content surface, which is harder for `.st-card` (one DOM tree per card) than for the page-level backdrop. **Recommendation:** data URI for v1, escape hatch documented if a future texture needs per-element interactivity.

3. **`--writing-medium-svg` global vs per-surface.** Should every content surface use the same medium, or can different surfaces use different media (cards on silk, doc on vellum)? **Recommendation:** one medium per theme for v1, simpler mental model. Multi-medium themes are a future RFC.

4. **Performance.** A vellum data URI is ~2.5KB. Ten content surfaces on a page = ten copies in CSS `background-image` evaluations. Browser caches the data URI evaluation per-URI-string, so this is one allocation, not ten — but worth measuring.

5. **Mobile / narrow viewports.** Textures designed for 680×380 may look stretched/repetitive on a 360-wide phone. The SVGs use `preserveAspectRatio="xMidYMid slice"` so they crop rather than distort, but small surfaces (a list-item 60px tall) may render almost no texture. **Recommendation:** apply textures only to surfaces above a minimum size — `.st-card` and `.st-doc` always; `.st-list-item` probably opt-in per theme.

6. **Print stylesheets.** `@media print` should strip writing-medium backgrounds — printers can't reproduce them and they waste toner. Add a rule under `MediaGated` to that effect.

7. **Accessibility.** Texture intensity (`--tex-grain-a`, `--tex-stain-a`) must remain low enough that text contrast against the textured surface meets WCAG AA. The texture pack's defaults are conservative on this front, but a "heavily-aged" theme variant could push too far. **Recommendation:** theme review process includes a contrast check pass.

---

## 8. Out of scope

- **Wallpaper redesign.** This RFC doesn't change how `Theme.backdrop()` works. New wallpapers (Bauhaus colour field, etc.) are future work.
- **Per-component writing medium override.** A studio that wants the catalogue cards on silk but the document panes on vellum cannot do that in v1. Possible v2.
- **Per-user writing medium picker.** No URL parameter for picking a medium independently of the theme. The theme owns the choice. A "medium picker" would be a future UX feature.
- **Animation.** Writing media are static. A "scrolling silk" or "rippling vellum" animation is explicitly out of scope.
- **Component primitive.** RFC 0003's `Component<C>` is still relevant for view-layer encapsulation but is orthogonal to this RFC. Writing media work whether or not RFC 0003 ships.

---

## 9. Decision

**Status:** Draft. No implementation yet. This RFC documents the design so the next agent / contributor / future-self picks up the right framing instead of re-inventing the wrong one.

When implementation starts, the recommended order:

1. Vendor the 10 SVGs into `homing-studio-base` under the proposed resource path.
2. Add `WritingMedium<TH>` interface in `homing-core`.
3. Add the `WritingMediaLibrary` registry of `WritingMedium` constants.
4. Add the `/writing-medium` `GetAction` in `homing-server`.
5. Wire `background-image: var(--writing-medium-svg)` into `st_card`, `st_doc`, etc., in `StudioStyles`.
6. Migrate `HomingLetterpress` from hand-rolled paper-grain to `vellum` as the worked example.
7. (Optional) Add 1–2 new themes that pair a fresh palette with a fresh writing medium, to make the new dimension visible in the theme picker.

This RFC closes when steps 1–6 ship. Step 7 is loose ambition, not a gate.
