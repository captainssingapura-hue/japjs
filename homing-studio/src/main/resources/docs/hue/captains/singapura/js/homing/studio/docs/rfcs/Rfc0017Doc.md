# RFC 0017 — Themable Content

| Field | Value |
|---|---|
| **Status** | Proposed — star feature for the next release |
| **Author** | Howard, with Homing |
| **Filed** | 2026-05-17 |
| **Target release** | 0.0.103 |
| **Implements ontology** | [Viewer](#ref:viewer-ontology) — adds axiom V12 (Theme Scope Promise) |
| **Scope** | Framework — extend the existing CSS-custom-properties theme system *through* inline visual content (SVG, HTML, and future visual kinds). Until now, theme support stopped at the chrome boundary: page header, tile borders, breadcrumbs all theme correctly, but a turtle SVG embedded in an SvgViewer page stays the same color whether the theme is Default, Forest, Sunset, Halloween, or Bauhaus. This RFC names the contract that lets content bodies opt into the theme via `currentColor` and `var(--color-*)` references, codifies a small stable token set, and adds the corresponding viewer-ontology axiom. **Almost no new framework code** — recognizes that the existing theme machinery already cascades into inline content via CSS, and makes the authoring discipline explicit. |

---

## 1. Motivation

Today's theme story is half-built. Themes (Default / Forest / Sunset / Halloween / Bauhaus) re-define a set of CSS custom properties; the framework's chrome — Header, tile borders, breadcrumbs, typography, link colors — picks them up and re-paints. Switch theme on any page, and the chrome flips instantly.

But the *content body* of a page is whatever the author put there:

- An SVG with `<path fill="#3a8bff">` shows that blue regardless of theme.
- An HTML asset with `<div style="background: #fff">` stays white in Halloween.
- A markdown doc renders to themed HTML via the framework's CSS (works today, because markdown emits structural tags like `h1`/`p`/`a` that inherit theme variables) — so prose is fine, but SVG and HTML lag behind.

The bug that motivates this RFC: in the recently-landed `SvgViewer`, the framework chrome around the SVG themes correctly under Halloween — yellow on teal backdrop — but the cute blue ghost in the middle of the page stays the same blue from the source file. Visually broken. Architecturally, the theme didn't reach the body.

The general shape of the gap:

| Layer | Theme support today | Theme support after this RFC |
|---|---|---|
| Page chrome (header, breadcrumbs) | ✅ Themed | ✅ Themed (unchanged) |
| Tile chrome (Card border, typography) | ✅ Themed | ✅ Themed (unchanged) |
| Prose body (markdown → HTML) | ✅ Themed (via framework CSS classes) | ✅ Themed (unchanged) |
| SVG body | ❌ Whatever the author hardcoded | ✅ Themed *when content uses semantic tokens* |
| HTML body | ❌ Whatever the author hardcoded | ✅ Themed *when content uses semantic tokens* |
| Future visual kinds (diagrams, charts, code highlighting) | ❌ Per-kind ad hoc | ✅ Themed by the same contract |

The framework's theme machinery already extends to inline content via CSS — it just needs the authoring contract that says "use these tokens" and the ontology axiom that says "every viewer guarantees the tokens are in scope." That's the work.

## 2. Design

### 2.1 Two levels of theme integration, both built on CSS

Every inline SVG or HTML element automatically inherits the theme's CSS custom properties via the DOM cascade — the chrome's outer container (`st_root`) carries the theme class, the variables defined on it propagate to every descendant including content placed by the viewer's body. Content opts into theming at two levels:

| Level | Mechanism | What gets themed |
|---|---|---|
| **Implicit** | SVG `currentColor` / HTML inherited `color` | Single-color shapes, text, basic borders — themes via the inherited text color |
| **Explicit** | `var(--color-name)` references inside the content's `fill` / `stroke` / `style` | Multi-color shapes, surfaces, accents, anything that needs a specific named token |

Both are pure browser CSS — no JavaScript runtime, no framework wrapper, no post-processing. The viewer's `main` slot is a DOM node inside `st_root`, which defines the theme's custom properties; any CSS reference inside content resolves against that scope.

### 2.2 The framework's stable token set

For the contract to be tractable, the framework publishes a stable, small list of theme tokens. Themes guarantee these are defined; content can reference any:

| Category | Tokens |
|---|---|
| **Text** | `--color-text-primary`, `--color-text-muted`, `--color-text-link`, `--color-text-link-hover` |
| **Surface** | `--color-background`, `--color-surface`, `--color-surface-elevated` |
| **Borders** | `--color-border`, `--color-divider` |
| **Accent** | `--color-accent`, `--color-accent-muted` |

Roughly a dozen tokens. Authors learn them once; themes define them all; content references any.

Content needing colors *outside* this set declares its own custom property locally with a fallback:

```svg
<path fill="var(--turtle-shell-color, #2a5a3f)" />
```

The local property is content-specific (themes may opt to define it; if not, the fallback applies). The framework's token set stays small and stable.

### 2.3 The viewer chrome promise — new ontology axiom V12

Adds to [Ontology — Viewer](#ref:viewer-ontology):

> **V12 — Theme context is guaranteed by the chrome.** Every viewer's main slot inherits the framework's theme custom properties via the chrome's `st_root` ancestry. Content rendered in the main slot may reference any framework theme token (`var(--color-*)`, `currentColor`) and have it resolve correctly under the active theme. The chrome guarantees the context; the content opts in.

This axiom is structurally already true (the `DocViewer` base composes `st_root`); V12 names the contract so authors can rely on it.

### 2.4 Authoring tiers — explicit doctrine

Not every piece of visual content should be themed. A photo of a sunset, a brand logo, a deliberate art piece — these have intentional fixed colors. The doctrine recognizes three tiers:

| Tier | What | Acceptable for |
|---|---|---|
| **Tokenized** | All colors via `currentColor` / `var(--color-*)` | Framework-shipped content; reusable icons; UI artwork; SVG that "belongs to the framework's voice" |
| **Semi-tokenized** | Structural colors tokenized; intentional brand colors hardcoded | Logos, brand artwork that mixes brand-fixed colors with theme-aware structure |
| **Raw** | All hardcoded values | Photos, photo-realistic SVG, imported third-party content, content where the colors *are* the meaning |

Framework-shipped content (the demo's `CuteAnimal` SVGs, future framework HTML samples, any visual content that ships with the studio) must be **Tokenized**. Downstream authors choose their tier. The doctrine, "Themable Tokens," is the operational rule.

### 2.5 What's in scope, what's out

**In scope:**
- SVG content (SvgDoc and future SVG-based kinds)
- HTML content (when HtmlDoc lands)
- Future visual kinds (diagrams from auto-generation per RFC 0014 Phase 1d, code surfaces, charts, anything that renders as DOM)
- Documentation of the stable token set
- One worked migration (the `CuteAnimal` SVGs as proof)
- The Themable Tokens doctrine + the V12 viewer axiom

**Out of scope:**
- **Sound effects in content** — RFC 0007 binds audio to interactive CSS classes (`st-card`, etc.) on the chrome/tile layer. Passive content (an SVG drawing) has no interaction semantic the framework can hook. Audio stays at the chrome layer.
- **Color post-processing** — no machinery to rewrite raw colors. Authors who want themed content opt in; authors who want fixed colors are respected.
- **A new SVG / HTML AST primitive** — content remains its native source format. No typed "themed rectangle" record.
- **iframe isolation** — defeats the entire purpose. Content participates in theme by being inside the DOM scope, not isolated from it.
- **Per-theme color-mapping config** — fragile and over-engineered. The CSS variable / `currentColor` mechanism is already the right level.

## 3. Invariants

Behavioural invariants after the RFC lands:

1. **Stable token set.** Every theme defines the dozen-or-so listed CSS custom properties. Themes may define additional properties; the listed set is the contract authors rely on.
2. **Viewer chrome guarantees scope.** Every Doc viewer composes its main slot inside the framework's themed CSS scope (V12; structurally guaranteed by the `DocViewer<P,M>` base).
3. **Authoring is opt-in.** Content using `currentColor` / `var(--color-*)` themes correctly; content using hardcoded values renders as-is. No coercion.
4. **Framework-shipped content is Tokenized.** Any visual content shipped with the framework uses semantic tokens. Enforced by a conformance scanner (queued — see implementation order).
5. **Sound stays at the chrome.** Audio binding follows RFC 0007 conventions at the tile/chrome level; content bodies don't gain interactive audio surfaces.
6. **No post-processing.** Inline SVG and HTML pass through the viewer unchanged. The theme cascade is the only mechanism affecting their appearance.
7. **Theme switching is instant** for tokenized content — same `var(...)` resolution as the chrome. No re-fetch, no re-render.

## 4. Cost — Weighed Complexity

| Dimension | Cost |
|---|---|
| **Cognitive density** | Low. Authors learn one rule: "use `currentColor` or `var(--color-*)`." Two doctrines, one axiom. |
| **Blast radius** | Narrow. Documentation + token-set publishing + ~6 SVG asset rewrites in the demo. No framework code paths change. |
| **Reversibility** | High. The mechanism is CSS; reverting is making content go back to hardcoded values. Nothing to roll back at runtime. |
| **Authoring tax** | Negative for new content (writers learn the contract once, content themes for free). Zero for existing content (raw values still work). |
| **Failure mode** | Author forgets a token → content renders with its hardcoded color (i.e. doesn't theme). Worst case is "the page looks slightly off-theme in that one place," not broken behavior. |
| **Backward compatibility** | Total. Existing themes work unchanged; existing content works unchanged; new behavior is purely additive. |

Per [Weighed Complexity](#ref:doc-wc): one of the highest-leverage cuts the framework has made in recent releases. Tiny implementation cost, large user-visible quality improvement (every shipped visual now responds to theme), opens the door for future visual content kinds to inherit theme support automatically.

## 5. Decisions (locked)

1. **No new framework code paths.** The mechanism is the existing CSS-custom-properties scope. The work is doctrine + documentation + asset migration.
2. **Stable token set is small (~12 tokens).** Authors learn them; themes define them all; anything else is content-specific custom properties with fallbacks.
3. **No post-processing.** Inline SVG / HTML passes through unchanged. Authoring discipline, not framework gymnastics.
4. **Tiered authoring.** Framework-shipped content is Tokenized. Downstream chooses Tokenized / Semi-tokenized / Raw per piece.
5. **Sound effects stay at chrome.** This RFC is explicitly visual; audio is RFC 0007 territory.
6. **V12 (viewer chrome theme-scope guarantee) is structurally already true** thanks to the typed `DocViewer<P,M>` base. The axiom names the contract; the type system enforces it via chrome composition (V11).
7. **Photo / brand / art SVG content is exempt** from the "tokenize everything" rule. The doctrine recognizes intentional fixed colors as valid.
8. **No iframe / shadow DOM isolation.** Content participates in theme by being inside the cascade. Isolation would defeat the goal.

## 6. Decision

**Adopt as the star feature of the next release.** The change is structurally small but visually significant — every framework-shipped SVG and future visual content kind responds to theme switching from the first moment authors learn the contract. It also unlocks the entire downstream content-kind landscape (RFC 0015's polymorphic doc viewer + RFC 0016's content trees) to participate in theme by default, not as a per-kind retrofit.

## 7. Implementation order

Each phase independently shippable.

1. **Phase 1 — Stable token-set documentation.** A new building-block doc enumerating the framework's CSS variable contract. Title: *"Theme Tokens — The Framework's Stable Color Contract"*. Lives under the existing CSS group / theme docs.

2. **Phase 2 — V12 axiom + Themable Tokens doctrine.** Add V12 to the Viewer ontology entry. File the Themable Tokens doctrine under the Doctrines/Visual System (or a new sub-category). One short doc each.

3. **Phase 3 — Migrate the demo `CuteAnimal` SVGs.** Rewrite the 6 SVG files using `currentColor` for the primary shape and `var(--color-accent)` / `var(--color-text-primary)` for accents. The animals visibly respond to theme switching. Proves the pattern end-to-end with no code change.

4. **Phase 4 — Conformance scanner.** A test that walks all framework-shipped SVG / HTML resources and flags raw hex color values. Initially a warning (allow existing content to migrate gradually); upgrade to error once framework content is fully migrated.

5. **Phase 5 — `HtmlDoc` as second realization.** Symmetric to `SvgDoc`: a Doc subtype for HTML content, served by a new `HtmlViewer extends DocViewer`. Demonstrates that the contract applies uniformly across visual content kinds. First consumer: probably a short "Welcome" HTML page replacing one of the existing markdown intros where richer layout matters.

6. **Phase 6 — Themed diagram authoring.** When the deferred RFC 0014 Phase 1d / 1e (Mermaid 2D / 3D StudioGraph views) lands, it inherits this contract: emitted SVG uses the framework token set; diagrams theme for free.

Phases 1-3 are essentially the entire star-feature payload: documentation + ontology + a visible demo. Phases 4-6 are follow-ups that amortize the contract.

## 8. What this enables downstream

| Future feature | How it benefits |
|---|---|
| **RFC 0014 P1d Mermaid 2D StudioGraph views** | Generated SVG inherits the framework's token set automatically; diagrams respond to theme |
| **RFC 0014 P1e 3D StudioGraph view** | three.js scene queries `var(--color-*)` for material colors; the 3D scene re-themes when the user switches |
| **Future CodeDoc** | Syntax highlighting palette derives from the framework tokens; code surfaces match the rest of the studio |
| **Future ChartDoc** | Bar / line chart colors come from the token set; matches the surrounding chrome |
| **Future architecture diagrams as first-class Docs** (per the earlier SVG-as-Doc realization) | Hand-authored architecture SVG themes alongside the surrounding doc; cross-doc visual cohesion comes for free |
| **Downstream brand themes** | A studio wanting its own dark / light / brand variant adds one CSS class defining the token set; all framework content + every visual asset re-themes instantly |

The compounding effect is the reason this is the star feature: every future visual content kind inherits theme support by *construction*, not by per-kind retrofit. Authors stop having to think "does this kind theme?"; the answer is structurally yes if the content uses tokens.

## 9. Why this is the right release

Three concurrent strands converge here:

- **RFC 0015 (Doc Unification) has landed** — Doc is now polymorphic; SvgDoc and the upcoming HtmlDoc fit into the same family. This RFC defines how their content bodies participate in theme.
- **RFC 0016 (Content Trees) has landed** — trees of visual content (categorised SVGs, image galleries, diagram collections) are now first-class. Without theme support, these trees would feel half-finished; with it, they feel native.
- **The typed `DocViewer<P,M>` base from V11** — already structurally guarantees the chrome's theme scope around every viewer's main slot. V12 names what V11 makes possible.

Together: the framework has spent recent releases building the *infrastructure* for polymorphic, hierarchically-organized, type-safe visual content. This RFC delivers the *visible quality* layer on top of that infrastructure. The infrastructure RFCs were architecture; this is the felt experience.

## See also

- [Ontology — Viewer](#ref:viewer-ontology) — gains axiom V12 (theme scope guarantee).
- [RFC 0007 — Theme Audio Cues](#ref:rfc-7) — explicitly-excluded counterpart; the sound layer stays at the chrome boundary.
- [RFC 0015 — Doc Unification](#ref:rfc-15) — the polymorphic doc viewer that lets SVG / HTML / future content kinds be first-class Docs.
- [RFC 0016 — Content Trees](#ref:rfc-16) — the hierarchical container that gets themed leaves for free.
- [RFC 0014 — Typed Studio Graph](#ref:rfc-14) — Phase 1d / 1e diagram views consume this contract directly.
- [Weighed Complexity doctrine](#ref:doc-wc) — informs the favourable cost analysis.
- [Stateless Server doctrine](#ref:doc-ss) — themes are stateless CSS; no per-user state introduced.
- [Functional Objects doctrine](#ref:doc-fo) — the design adds no parameter-explosion paths; the entire mechanism is CSS scope inheritance.
- (Future) **Doctrine — Themable Tokens** — the operational principle this RFC adopts; lives under Visual System doctrines once filed.
