# Defect 0003 — Two-Bundle CSS Cascade

| Field | Value |
|---|---|
| **Status** | **Resolved** (2026-05-11) — typed `Layer` ladder + CSS `@layer` wrappers make the cascade deterministic regardless of bundle load order. `!important` workarounds removed. See §8 below. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-11 |
| **Severity** | Architectural — bites every stylesheet author the moment they need to override a `StudioStyles` rule from a `Theme.Globals` block (or vice versa). |
| **Affected modules** | `homing-studio-base` (the chrome's CSS authoring), every downstream theme that wants to alter screen rules. |
| **Surfaces in** | The print stylesheet that wouldn't take. Anywhere a `@media`-gated rule, a hover behaviour, or a per-theme structural tweak needs to win against a base rule from `StudioStyles`. |

---

## 1. Symptom

Adding this rule to `HomingDefault.STRUCTURAL_CSS`:

```css
@media print {
    .st-doc { max-width: none; }
}
```

…**did not** widen the article body on print preview. The article stayed at its screen-mode `max-width: 820px`, leaving a third of the page empty.

Same selector, more specific intent (media-gated), but the *competing* rule from `StudioStyles` still won. Without `!important`, no amount of careful authoring inside `STRUCTURAL_CSS` could override it.

---

## 2. Root cause

The framework serves CSS for the studio chrome from **two independent stylesheet bundles**, both reaching the page via separate endpoints:

| Bundle | Source | Served at | Contains |
|---|---|---|---|
| Component styles | `StudioStyles.java` records (each `st_*` class is a `CssClass` body) | `/css-content?class=…` | `.st-card`, `.st-doc`, `.st-header`, `.st-list-item`, … — the **bulk** of the structural component CSS |
| Theme globals | `Theme.Globals.css()` returning `STRUCTURAL_CSS + DARK_OVERRIDE + per-theme-overrides` | `/theme-globals?theme=…` | Hover rules, prose descendant rules (`.st-doc h1`, `.st-doc a`), media queries, theme texture, font overrides |

Both stylesheets target the **same `.st-*` class names**. When two rules collide at equal specificity, CSS spec says **last-loaded wins** — but **load order isn't deterministic from the authoring side**. The framework's CSS-bundle assembly serves the two endpoints independently and the browser fetches them in whatever order it likes.

This means a rule authored in `STRUCTURAL_CSS` *might* win against a rule in `StudioStyles`, or might lose — depending on which fetch completes first. In practice, the studio-styles bundle tends to win because it's referenced from more places and gets cached more aggressively.

Inside a `@media print` block this is doubly invisible because you can't see the conflict on screen — only when the print preview pops up does the screen rule's lingering effect become apparent.

---

## 3. Workaround (in place)

For any rule in `STRUCTURAL_CSS` (or any other `Theme.Globals` block) that needs to override a `StudioStyles` rule, use `!important`:

```css
@media print {
    .st-layout    { grid-template-columns: 1fr !important; }
    .st-sidebar   { display: none !important; }
    .st-doc       { max-width: none !important; }
    .st-main      { max-width: none !important; padding: 12px 0 !important; }
    /* … */
}
```

**`!important` is legitimate here** because the rules are media-gated — they only fire during printing, can't pollute screen styling, and the override intent is unambiguous. This is exactly the use-case CSS designers had in mind for `!important`: forcing a rule to win when stylesheet load order isn't under your control.

The same workaround applies to `@media (prefers-color-scheme: dark)`, hover rules, or any other gated context where a cross-bundle override is needed.

---

## 4. Long-term fix (deferred)

The proper resolution is to **unify the authoring location** so there's no cross-bundle competition:

- **(a)** Move all `.st-*` rules — including hover, media queries, theme texture — into `StudioStyles.java` as `CssClass` bodies. `Theme.Globals` becomes thin: just `@media (prefers-color-scheme: dark) { :root { … } }` variable rebinds + per-theme texture/font overrides.
- **(b)** Or the reverse — move all component CSS into `STRUCTURAL_CSS` and reduce `StudioStyles.java` to a typed handle registry without bodies. Less appealing because the typed CssClass bodies have nice authoring affordance.

Either resolution is a meaningful refactor (~30 LoC moved per direction, plus reasoning through each existing rule's correct owning bundle). Not blocking, captured here for v1.x or a future refactor pass.

---

## 5. Lessons banked

For any framework author or downstream consumer authoring studio CSS:

1. **Know which bundle owns which rules.** If you're writing a CSS rule that overlaps an existing rule's selector, find out where the existing rule lives. Same bundle → simple cascade. Different bundle → cross-bundle override territory.
2. **Cross-bundle overrides need `!important`** until the bundles are unified. There's no way around this with current architecture.
3. **`!important` is acceptable for media-gated rules** — `@media print`, `@media (prefers-color-scheme: dark)`, `@media (hover: hover)`, etc. The media gate constrains the rule's blast radius; the `!important` constrains who wins the cascade. Both are explicit; neither leaks.
4. **`!important` is suspicious for unconditional rules.** A rule with `!important` outside any media query, in a global stylesheet, against a same-bundle competitor — that's a sign of architectural drift, not a workaround. Refactor instead.
5. **Inheritance ≠ pointer-events ≠ stacking.** Several CSS properties (`pointer-events`, `box-sizing` for shorthand resets, `direction`) have non-obvious inheritance/propagation rules that interact with bundle ordering. When a rule "doesn't seem to apply," check both inheritance semantics AND bundle ordering before reaching for `!important`.

---

## 6. Where this surfaces (concrete in-tree examples)

| Site | What didn't work | What worked |
|---|---|---|
| Print stylesheet (this defect's origin) | Plain `.st-doc { max-width: none }` in `@media print` | Same rule with `!important` |
| Maple Bridge theme — body background override | (Would have failed if the theme tried to override `.st-card` via Globals) | Theme keeps to non-overlapping selectors (`.theme-backdrop`, etc.) |
| Hover rules inside `STRUCTURAL_CSS` (`.st-card:hover`, etc.) | Work because they target a state (`:hover`) the base rule doesn't specify — no collision | n/a |

The pattern: **collisions only happen when two bundles target the same selector at the same specificity**. Hover/focus/media variations don't collide with base rules because the bundle authors typically scope their additions to states the base rule doesn't cover.

---

## 7. Action items for future stylesheet authors

- ☑ ~~When adding a `@media`-gated rule that needs to override a base rule, add `!important` from the start. Document why in a comment.~~ — no longer required; the `MediaGated` layer wins against `Component`/`Layout` by ladder position.
- ☑ ~~When adding a non-gated `.st-*` rule, find the existing definition first. If it lives in a different bundle, prefer moving the existing rule into your bundle (or vice versa) rather than fighting the cascade.~~ — bundles can collide freely; the typed layer decides.
- ☑ ~~If you find yourself reaching for `!important` outside a media query, stop.~~ — the layer ladder removes the underlying ambiguity.
- ☐ **New rule:** when authoring a `CssClass`, declare its tier via `InLayer<L>` (e.g. `implements CssClass<StudioStyles>, InLayer<Layout>`). Component is the implicit default — only the 5 layout primitives (`st_root`, `st_main`, `st_layout`, `st_grid`, `st_list`) opt down to `Layout`, and prose/state/media rules belong in `ThemeGlobals.chunks()` keyed by their respective layer class.

---

## 8. Resolution — typed cascade ladder

**Ship date:** 2026-05-11.

**Architecture.** A sealed `Layer` interface in `homing-core` defines the framework's canonical 7-tier ladder:

```
@layer reset, layout, component, prose, state, media, theme;
```

Each tier is a `non-sealed` sub-interface (`Reset`, `Layout`, `Component`, `Prose`, `State`, `MediaGated`, `ThemeOverlay`). Authors opt content into a tier two ways:

1. **Per-class** via a generic guard marker: `interface InLayer<L extends Layer> {}`. A `CssClass` record declares `implements CssClass<…>, InLayer<Layout>` to ride on the layout tier. Java's "no duplicate parameterised supertype" rule makes single-layer membership a *compile-time* guarantee — you can't accidentally claim two tiers.

2. **Per-chunk** via `ThemeGlobals.chunks()` returning `Map<Class<? extends Layer>, String>`. Themes split their global CSS by tier — reset rules, prose descendant rules, hover states, media-gated rules, and theme overrides all land in their own buckets.

**Serving.** `CssContentGetAction` and `ThemeGlobalsGetAction` emit `@layer reset, layout, component, prose, state, media, theme;` at the top of every bundle, then wrap each tier's content in `@layer X { … }`. Browsers honour `@layer` declarations independently of which stylesheet arrives first — so the cascade is deterministic regardless of bundle load order. Belts (Java ordering) and braces (CSS `@layer`) both.

**Why it works.** CSS `@layer` rules at any specificity in a later-declared layer always win against any specificity in an earlier-declared layer. The print stylesheet's `.st-doc { max-width: none }` in `@layer media` now wins against `.st-doc { max-width: 820px }` in `@layer component` *without* `!important`, because cascade layers outrank specificity.

**Migration cost paid.** Tagged the 5 layout primitives in `StudioStyles` with `InLayer<Layout>`. Split `HomingDefault.STRUCTURAL_CSS` into 5 tier-tagged chunks (`RESET_CSS`, `COMPONENT_CSS`, `PROSE_CSS`, `STATE_CSS`, `MEDIA_GATED_CSS`); the `DARK_OVERRIDE` block joins them on the `ThemeOverlay` tier. Print block lost its `!important`s — the cascade ladder makes them redundant.

**What was *not* done.** Other built-in themes (Forest, Sunset, Bauhaus, Forbidden City, Letterpress, Maple Bridge) still ride on the back-compat `css()` path which the server wraps in `@layer theme` as a single chunk. That's correct cascade-wise (they win against `Component`/`Prose` as expected) but coarse; future migration moves them to `chunks()` so reset/layout/state/media authoring becomes tier-aware per-theme.
