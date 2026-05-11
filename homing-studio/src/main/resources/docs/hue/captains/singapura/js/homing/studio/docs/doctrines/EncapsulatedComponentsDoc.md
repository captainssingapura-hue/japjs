# Doctrine — Well-Encapsulated Components (CSS Included)

| Field | Value |
|---|---|
| **Status** | Active |
| **Audience** | Framework authors first; downstream studio authors as consumers. |
| **Adopted** | 2026-05-11 (codified after Defect 0003 resolution). |
| **Related** | [Defect 0003 — Two-Bundle CSS Cascade](#) (the forcing function). |

---

## 1. The claim

A component is **well-encapsulated** when consumer code can invoke it without touching — or knowing about — its internals. In Homing, that boundary includes three things the rest of the JS ecosystem usually treats as separate concerns:

1. **Markup** — the DOM structure (already framework-owned via `Component` invocations; see [Pure-Component Views](#)).
2. **Behaviour** — event wiring, state, lifecycle (already framework-owned via `AppModule` / `Component`).
3. **CSS** — selectors, specificity, cascade tier, media adaptations, hover/focus states. **This doctrine is about #3.**

The front-end industry spent twenty years discovering that CSS leaks across component boundaries unless you fight it deliberately. BEM, CSS Modules, scoped styles, Tailwind, CSS-in-JS — each was an industry-wide reaction to the same realisation: **the cascade does not respect component boundaries by default**. Homing's stance: that fight belongs to the framework, not to every downstream consumer.

---

## 2. What "encapsulated CSS" means concretely

A component in Homing owns:

| Surface | Owned via | Encapsulation guarantee |
|---|---|---|
| Class names | `CssClass` records — typed Java handles, kebab-cased at the boundary | Consumers reference the *class type*, not a string. Renaming a class is a refactor, not a search-and-replace. |
| Rule bodies | `CssBlock.body()` returning the declaration body | The rule's source of truth is the component file, not a parallel `.css` file or theme override. |
| Cascade tier | `InLayer<L extends Layer>` marker on the `CssClass` record | The rule's role in the cascade (reset / layout / component / prose / state / media / theme) is declared at the type level. Single-tier membership is a compile-time guarantee. |
| Layered serving | `CssContentGetAction` wraps each tier in `@layer X { … }` | Rules from different components in the *same* layer compose by specificity; rules in *different* layers are ordered by the framework-owned ladder. |
| Theme adaptation | `var(--…)` references against semantic tokens | A component's CSS adapts to every registered theme without per-theme overrides. The component author never writes theme-specific rules. |

The combined effect: **a component's CSS travels with its Java declaration**. There is no parallel stylesheet to keep in sync, no theme file to update when a component is added, no specificity arithmetic to redo when a new component lands in the same studio.

---

## 3. Why this is the framework's job, not the consumer's

A naïve framework hands the consumer a set of CSS classes (`.card`, `.button`, `.modal`) and a stylesheet that defines them. The moment the consumer authors *anything custom* — a new widget, a one-off override, a theme tweak — they enter the cascade. They have to reason about:

- Which existing rule their selector competes with.
- What specificity their selector lands at.
- Whether their stylesheet loads before or after the framework's.
- Whether their rule will survive the next framework upgrade.

Every one of these is a real problem with real solutions in the wider ecosystem — and every solution is *discipline*, not a guarantee. BEM is discipline. CSS Modules is build tooling around discipline. Tailwind is *abandoning* the cascade entirely because the discipline was too expensive to maintain.

Homing's position: **the consumer should not have to be disciplined about CSS, because the consumer should rarely write CSS at all.** A downstream studio author building a documentation site, a catalogue, a tools dashboard — none of those tasks require new selectors. The framework's `CssClass` library covers the structural vocabulary (`st-root`, `st-main`, `st-layout`, `st-grid`, `st-card`, `st-doc`, `st-header`, `st-footer`, …) and the prose chrome (markdown rendering, badges, pills, lists). The author composes existing components, picks or customises a theme, and ships.

When custom CSS *is* needed, the typed ladder makes the consumer's path safe:

- New `CssClass` records default to `@layer component` — exactly the tier where downstream authoring belongs.
- The `InLayer<L>` marker is the *only* way to opt elsewhere, and the compiler enforces single-tier membership.
- The framework can refactor its own CSS freely (rules move between layers, get added, get deleted) without breaking downstream rules, because the tier contract is what's stable.

---

## 4. Contrast — what the rest of the ecosystem does

| Approach | Encapsulation strategy | Cost to consumer |
|---|---|---|
| **Plain CSS + naming convention (BEM)** | "If we all name things `block__element--modifier`, collisions are unlikely." | Every consumer is responsible for naming discipline. Collisions still happen across vendors. |
| **CSS Modules / scoped styles** | Build-tool hashes class names so they can't collide. | Consumers can't reference framework classes from outside the module without an explicit export. Theming becomes awkward. |
| **CSS-in-JS (styled-components, emotion)** | Runtime generates unique class names per component invocation. | Runtime cost, server-rendering complexity, debugging difficulty. The cascade still exists, just buried. |
| **Tailwind / utility-first** | Abandon component-scoped CSS entirely; compose atomic utilities at the call site. | Markup becomes verbose; theming requires re-tooling utilities; semantic intent disappears from the DOM. |
| **Homing — typed CssClass + cascade layers** | Component owns its class records + rule bodies + tier marker. Server emits native `@layer` blocks. | Consumer writes Java records; framework owns the CSS pipeline. |

The pattern across the first four rows: the consumer pays the encapsulation cost, in tooling, discipline, or runtime. Homing's row: the framework pays it once, in typed metadata that the compiler enforces.

This is only available to Homing because **every selector flows through a Java declaration** — there's no "raw CSS file" surface for downstream code to leak into. That constraint is what makes the encapsulation enforceable.

---

## 5. Rules of the doctrine

For framework authors (the people writing `CssClass` records inside `homing-core`, `homing-studio-base`, or any module that publishes components):

1. **Every selector that exists belongs to a `CssClass` record.** No exceptions for "just one quick rule" — that's how parallel stylesheets are born.
2. **Tag the cascade tier when it's not `Component`.** Layout primitives → `InLayer<Layout>`. Reset rules → `InLayer<Reset>`. State adaptations → `InLayer<State>`. Implicit default (no marker) is `Component`, which is correct for 80% of cases.
3. **Author rule bodies against `var(--…)` tokens, not hardcoded values.** A component that bakes in `#3478F6` is permanently broken for every theme that isn't Homing Default.
4. **Descendant selectors over unowned content go in `ThemeGlobals.chunks()`.** When markdown renders `<h1>` you can't put a `CssClass` on, the rule belongs on the theme's `Prose` chunk. The escape hatch is documented and tier-tagged — it's not "raw CSS."
5. **The component file is the single source of truth.** Class name, rule body, tier marker, variant rules — all in the same record, in the same file. A reviewer reading the file sees the complete CSS surface of the component.

For downstream consumers (the people building studios on top of the framework):

1. **Compose existing components first.** Most studios need zero new CSS. The framework's `CssClass` vocabulary is rich enough for documentation, catalogues, dashboards, and content sites.
2. **Customise the theme, not the components.** A new colour palette is a `Theme` with a different `cssVariables()` map. The framework's components adapt automatically — no per-component overrides needed.
3. **When you do need custom CSS, write a `CssClass` record.** Stay on the implicit `Component` default unless you genuinely need to win against framework structural rules. If you do, declare `InLayer<L>` honestly.
4. **Do not author parallel stylesheets.** If you find yourself opening a `.css` file or writing a `<style>` tag, you've exited the framework's contract. The cascade ladder cannot protect rules it doesn't see.

---

## 6. What this doctrine prevents

The concrete failures this doctrine rules out, with the historical precedent each represents:

| Failure mode | Without doctrine | With doctrine |
|---|---|---|
| New framework component breaks a downstream override | Common — load-order or specificity shift wins differently. | Impossible — component tier is fixed; downstream overrides sit on a higher tier by declaration. |
| Two themes need different print stylesheets | Each theme's print rules fight the base print rules; `!important` everywhere. | Each theme's `MediaGated` chunk is its own `@layer media` block; theme overlay layer wins by ladder. |
| Renaming a class breaks downstream | Find-and-replace across stylesheets and templates; easy to miss strings. | Java refactor; compiler catches every reference. |
| Adding a hover state to one component cascades to another | Hover rules collide because cascade doesn't respect component boundaries. | State rules live in `@layer state`; component-tier rules can't accidentally override them, and vice versa. |
| Downstream studio author writes their first custom rule and it breaks the chrome | Their rule lands at unknown specificity, may win or lose against framework rules. | Their rule defaults to `@layer component`, sits alongside framework component rules at the same tier, ordered by specificity (predictable) rather than load order (unpredictable). |

The Defect 0003 fix gave the framework the *mechanism*. This doctrine is the *practice* that keeps the mechanism honest as the codebase grows.

---

## 7. Tooling that backs this up

- **`Layer` sealed interface** + 7 non-sealed sub-interfaces — the canonical ladder.
- **`InLayer<L extends Layer>`** — generic guard marker, compile-time single-tier enforcement.
- **`Layers.ofImplementor()`** — reflection extractor used by the CSS-serving actions.
- **`CssContentGetAction` / `ThemeGlobalsGetAction`** — server-side rendering with `@layer` wrappers and the layer declaration emitted at the top of every bundle.
- **`CssConformanceTest`** (in `japjs-conformance`) — scans DOM-module JS for raw CSS operations; the doctrine's automated enforcement at the JS surface.

All present, all tested, all framework-owned. The doctrine isn't a hope — it's a constraint the type system and the test suite both check.
