# RFC 0002-ext1 — Utility-First Composition + Two-Layer Semantic Tokens

| Field | Value |
|---|---|
| **Status** | **Implemented** — Phases 01–12 landed 2026-05-07. Studio + demo fully migrated to the marker model; `CssGroupImpl<CG, TH>` retained as a no-op compatibility shim until a future cleanup. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-07 |
| **Extends** | RFC 0002 — Typed Themes for CssGroups |
| **Target scope** | Studio first (`StudioStyles` + a new `Util` group), demo follows under a separate effort |

---

## 0. Status notice

RFC 0002 made themes typed and the registry the canonical CSS resolution path. In doing so it surfaced two new problems that 0002 itself did not solve:

1. **Per-theme duplication.** A `CssGroupImpl` for theme A is ~80% identical to the impl for theme B — same layout, different colors. The framework has no mechanism to factor that out.
2. **Hover/focus rules live in `globalRules()`.** Every theme's `globalRules()` re-translates the same `:hover`/`:focus` rules with theme-specific colors. They cannot be expressed as per-class `CssBlock`s under the current renderer.

This extension addresses both with one coordinated shift: **utility-first composition with state-modifier first-class support, layered on a two-tier semantic-token system.**

---

## 1. Motivation

### 1.1 The duplication audit

After Phase 05 of RFC 0002 the demo theme directory has 10 impl files totalling ~2,260 lines. Diff-by-diff:

- `PlaygroundStyles{DemoDefault,Alpine,Beach,Dracula}.java` — 4 × ~256 lines, ~150 lines byte-identical between any pair, ~100 lines differing only in literal color values.
- `SubwayStyles{DemoDefault,Beach}.java` — 2 × ~113 lines, same shape.
- `SpinningStyles{DemoDefault,Beach}.java` — 2 × ~126 lines, same shape.
- `StudioStylesHomingDefault.globalRules()` — ~150 lines, of which ~50 are `:hover`/`:focus` rules that would need re-translation per future theme.

**The pattern: layout/structure repeats; colors/values diverge per theme.** Today the framework forces both into the same per-impl method body.

### 1.2 The architectural insight

The fix is two independent moves that compose:

**Move A — Utility composition at the JS layer.** The `cn(record1, record2, ...)` mechanism already composes class names client-side. A "semantic" component class like `st_card` is a *named bundle of declarations*; nothing in the framework requires bundles to be named at the CSS layer. Apps that want named bundles can keep them; apps that want utility composition (`cn(p_4, bg_surface, rounded_lg)`) get the same type-safe path. Both shapes survive existing conformance checks unchanged.

**Move B — Semantic tokens as the value layer.** `cn(hover_color_amber)` leaks color identity into the API: a Beach theme would need to redefine `--amber` to a non-amber color (lying), or invent `hover_color_orange_for_beach` (combinatoric). `cn(hover_color_link)` describes the *role*; each theme maps the role to its own primitive. This is the standard two-layer design-token model (Spectrum, Carbon, Polaris, Material 3, Tailwind v3+).

Together: **state behavior is a utility, value is a semantic token.** Hover rules collapse to a small set of utility records that reference role-named CSS variables. Theme-specific work shrinks to "redefine the primitives, optionally remap a few semantic roles."

---

## 2. Goals & non-goals

### Goals

- **Pseudo-state modifiers as a first-class concept** in `CssClass`. A `:hover`, `:focus`, `:active` (etc.) variant is just a CssClass record with a `pseudoState()` override.
- **Semantic tokens** as the contract between component CSS and the theme. Bodies reference `var(--color-text-link)`, never `#1E2761`.
- **Primitive tokens** as the per-theme implementation of the semantic contract. Each theme picks the actual hex.
- **A small, hand-curated `Util` CssGroup** in `homing-studio-base` containing hover/focus utilities and core layout helpers. Apps import what they use.
- **Drop the implicit "every cn() resolves to a per-component-group record" doctrine.** Utility records are first-class peers. `cn(st_card)` and `cn(p_4, bg_surface, rounded_lg)` are both legal and idiomatic depending on context.
- **Studio first.** This RFC's acceptance criteria scope to studio + studio-base. Demo migration is a follow-up effort under the same playbook.

### Non-goals

- **Codegen for a Tailwind-scale utility set.** This RFC defines a small, hand-curated `Util` group (~30–80 records). Codegen / token-driven utility generation is a future effort.
- **Replacing existing semantic classes.** `StudioStyles` semantic classes (`st_card`, `st_section`, etc.) survive as legal, useful primitives for repeated bundles. The shift is "no longer mandatory," not "deprecated."
- **Variant prefixes (`md:`, `dark:`).** Media-query and color-scheme variants don't map to the single-suffix `pseudoState()` shape. They stay in `globalRules()` (or `@media` blocks inside the default theme — already supported). A future RFC can add `mediaQuery()` if the pain materializes.
- **Demo migration in this RFC.** Out of scope; tracked separately.

---

## 3. Design

### 3.1 `pseudoState()` on `CssClass`

```java
public interface CssClass<G extends CssGroup<G>> {
    /** Optional pseudo-class suffix appended to the rendered selector.
     *  e.g. ":hover", ":focus", ":active". null/empty → bare class selector.
     *  Default: no suffix (preserves all existing CssClass records unchanged). */
    default String pseudoState() { return null; }
}
```

Renderer change in `CssContentGetAction.renderCss()` (≤ 5 lines):

```java
String selector = "." + CssClassName.toCssName(cssClass.getClass());
String state = cssClass.pseudoState();
if (state != null && !state.isEmpty()) selector += state;
```

Backwards compatible: every existing record returns `null` and renders identically to today.

#### 3.1.1 Variants — auto-generated from the base

The base utility alone carries enough information to synthesize all of its state-restricted variants. Rather than declare separate `VariantOf<B>` records for each `(base, state)` pair, a base CssClass advertises which states it wants via `variants()`:

```java
public interface CssClass<C extends CssGroup<C>> extends Exportable._Constant<C> {
    default String pseudoState() { return null; }

    /** Pseudo-state variants the framework should auto-generate for this base.
     *  Each state s in the returned set produces an additional rule
     *  ".s-<kebab>:s { <body> }" reusing the base body. */
    default Set<String> variants() { return Set.of(); }
}
```

Common-case convenience for utilities that want all three default states:

```java
public interface UtilityCssClass<G extends CssGroup<G>> extends CssClass<G> {
    @Override default Set<String> variants() { return Set.of("hover", "focus", "active"); }
}
```

A utility base then needs **one record**, not four:

```java
// Before — VariantOf records (retired):
//   public record bg_accent()       implements CssClass<Util> {}
//   public record hover_bg_accent() implements HoverVariantOf<bg_accent> { … }
//   public record focus_bg_accent() implements FocusVariantOf<bg_accent> { … }
//   public record active_bg_accent() implements ActiveVariantOf<bg_accent> { … }

// After:
public record bg_accent() implements UtilityCssClass<Util> {}                   // all three states
public record bg_subtle() implements CssClass<Util> {                            // hover only
    @Override public Set<String> variants() { return Set.of("hover"); }
}
```

Renderer pseudo-code in `CssContentGetAction.renderCss()`:

```java
for (CssClass<?> cls : group.cssClasses()) {
    String baseKebab = CssClassName.toCssName(cls.getClass());
    String state = cls.pseudoState();
    String selector = "." + baseKebab + (state == null ? "" : state);
    String body = ((CssBlock<?>) impl.getClass().getMethod(cls.getClass().getSimpleName()).invoke(impl)).body();

    emit(selector + " { " + body + " }");

    // Auto-generate variants — same body, state-prefixed kebab + pseudo-class suffix.
    for (String s : cls.variants()) {
        emit("." + s + "-" + baseKebab + ":" + s + " { " + body + " }");
    }
}
```

The Util impl provides bodies for *base* utilities only. Variants are computed; no per-variant impl methods exist. The framework's `CssGroupImplConsistencyTest` from RFC 0002 still applies — it just gets even simpler because the surface to verify is smaller.

**Trade-off vs. VariantOf records:** the variant kebab name is now framework-owned (`<state>-<base>`), not user-controlled, and a custom variant body (different from the base) is no longer expressible. Both are deliberate losses — Tailwind's variant model has the same property, and the two-layer-token system handles theme-specific differences via re-mapped semantic tokens, not through divergent rule bodies.

### 3.2 Two-layer tokens

A new method on `CssGroupImpl`:

```java
public interface CssGroupImpl<CG extends CssGroup<CG>, TH extends Theme> {
    CG group();
    TH theme();
    default Map<String, String> cssVariables()    { return Map.of(); }   // primitives
    default Map<String, String> semanticTokens()  { return Map.of(); }   // semantic ⟶ var(primitive)
    default String globalRules()                  { return ""; }
}
```

Renderer emits both maps under `:root`, primitives first, then semantic tokens (so the latter can reference the former). Implementations may skip either map; the only behavioral change is that semantic tokens are now a recognized layer.

#### 3.2.1 Studio's starter semantic vocabulary

~25 names across three dimensions:

```
Surface:
  --color-surface           (page bg)
  --color-surface-raised    (cards)
  --color-surface-recessed  (subtle highlights)
  --color-surface-inverted  (sidebar / header)

Text:
  --color-text-primary
  --color-text-muted
  --color-text-on-inverted
  --color-text-link
  --color-text-link-hover

Border:
  --color-border
  --color-border-emphasis

Accent:
  --color-accent
  --color-accent-emphasis
  --color-accent-on        (text on accent surfaces)
```

Plus a small spacing scale (`--space-1` through `--space-8`) and radius scale (`--radius-sm`, `--radius-md`, `--radius-lg`).

Per-theme work shrinks to: redefine the primitive map (existing `cssVariables()`), optionally adjust the semantic map (usually unchanged).

### 3.3 The `Util` CssGroup

Lives in `homing-studio-base` (same module as `StudioStyles`). Initial contents (~30 records):

**Hover/focus utilities (reference semantic tokens only):**

```java
public record hover_color_link()        implements CssClass<Util> { @Override public String pseudoState() { return ":hover"; } }
public record hover_color_accent()      implements CssClass<Util> { @Override public String pseudoState() { return ":hover"; } }
public record hover_bg_surface_raised() implements CssClass<Util> { @Override public String pseudoState() { return ":hover"; } }
public record hover_translate_y_neg_2() implements CssClass<Util> { @Override public String pseudoState() { return ":hover"; } }
public record focus_ring_accent()       implements CssClass<Util> { @Override public String pseudoState() { return ":focus"; } }
// ... ~10 more
```

**Layout utilities (no pseudo-state, reference spacing scale):**

```java
public record p_2() implements CssClass<Util> {}  // padding: var(--space-2)
public record p_4() implements CssClass<Util> {}
public record gap_4() implements CssClass<Util> {}
public record flex() implements CssClass<Util> {}
public record grid() implements CssClass<Util> {}
// ... ~15 more
```

The `UtilHomingDefault` impl provides the bodies. Theme-independence comes for free because every body resolves through tokens.

### 3.4 JS-side variant exposition

The variant–base relationship encoded in Java carries through to the JS API. The module emitter (`EsModuleGetAction` or wherever module text is generated) walks each AppModule's imports, looks up registered `VariantOf<B>` records per base, and emits a richer JS object that reads like Tailwind at the call site.

**Project dialect: ES6+** throughout. ES6 modules are the loading mechanism (`import` statement is required for the framework to work at all), so ES6 classes / `const` / arrow functions / template literals are universally available. Earlier ES5 stylistic conventions in the codebase are retired.

**Framework runtime JS** — defined once, in the bootstrap shipped to every page. One uniform value type (`CssClass`), with dedicated subclasses per pseudo-state mirroring the Java side (`HoverVariantOf` / `FocusVariantOf` / `ActiveVariantOf`):

```js
class CssClass {
    constructor(name) { this.name = name; }
    toString() { return this.name; }
}

// Dedicated subclasses per pseudo-state — mirror Java's HoverVariantOf /
// FocusVariantOf / ActiveVariantOf. Distinct types so consumers can
// introspect via `instanceof`. The static `pseudoState` is informational.
class HoverVariant  extends CssClass { static pseudoState = ":hover";  }
class FocusVariant  extends CssClass { static pseudoState = ":focus";  }
class ActiveVariant extends CssClass { static pseudoState = ":active"; }

const VARIANT_CLASSES = {
    hover:  HoverVariant,
    focus:  FocusVariant,
    active: ActiveVariant,
};

class CssUtility extends CssClass {
    constructor(name, variants) {
        super(name);
        for (const state of Object.keys(variants || {})) {
            const VariantClass = VARIANT_CLASSES[state] || CssClass;
            this[state] = new VariantClass(variants[state]);   // precomputed instance
        }
    }
}
```

`toString()` lives on the prototype — one allocation across all instances, plain string-coercion just works for `cn()`, template literals, etc. Every class handle in the system is `instanceof CssClass`; variant handles are also `instanceof HoverVariant` / `FocusVariant` / etc., so type-aware tooling can distinguish them when needed.

**Generated per imported record:**

```js
// Plain CssClass record (no variants registered):
const st_root = new CssClass("st-root");

// CssClass record with registered variants — variants are precomputed CssClass
// instances exposed as PROPERTIES (no parens, no method-call ceremony).
const bg_accent_emphasis = new CssUtility("bg-accent-emphasis", {
    hover: "hover-bg-accent-emphasis",
    focus: "focus-bg-accent-emphasis",
});
// bg_accent_emphasis.hover  → CssClass instance with .name = "hover-bg-accent-emphasis"
// bg_accent_emphasis.focus  → CssClass instance with .name = "focus-bg-accent-emphasis"
```

**Single value type, no string smell.** Every class handle in the system — the base utility, the plain `CssClass` records, AND every variant — is a `CssClass` instance. `resolve()` has no string branch; the type is uniform.

**Call sites** (Tailwind-like co-location of base + state):

```js
// equivalent of <button class="bg-accent-emphasis hover:bg-accent-emphasis hover-bg-accent-emphasis-strong">
cn(bg_accent_emphasis, bg_accent_emphasis.hover)

// or directly on a DOM element:
cssSet(el, bg_accent_emphasis, bg_accent_emphasis.hover)
```

**AppModule imports stay parsimonious.** Only the base utility needs to appear in `imports()`; variants are reachable through it on the JS side because the emitter discovers them in the group's `cssClasses()`:

```java
.add(new ModuleImports<>(List.of(
    new Util.bg_accent_emphasis()              // .hover, .focus come along automatically
), Util.INSTANCE))
```

**Why this is strictly better than Tailwind's string-prefix approach:**

1. **Editor autocomplete** shows the *registered* variants only. `bg_accent_emphasis.` reveals exactly the properties that have a corresponding `VariantOf` record. Misspellings (`bg_accent_emphasis.hovr`) read as `undefined`, and the conformance test catches them at build time. Tailwind's `hovr:bg-sky-700` is silently invalid until visual inspection.
2. **One import per visual concept**, not one per (visual concept × variant).
3. **No string-scanning build step.** The framework already knows the type relationships; the JS API is generated from them directly.

### 3.5 What `StudioStyles` looks like after migration

- All component method bodies (`st_card`, `st_panel`, etc.) reference *semantic* CSS variables only. No literal hex, no `var(--st-amber)`-style primitives in component bodies.
- `globalRules()` shrinks: hover/focus rules that mapped to a single property + semantic value move into `Util`. Multi-property hover rules (e.g. `.st-card:hover { transform; box-shadow; border-color; }`) remain in `globalRules()` for now — composing a triple-utility at every call site is verbose.
- Component records remain available for apps that prefer named bundles.

### 3.6 Class-level bodies + theme-as-cascade — the structural simplification

Once §3.2 (two-layer tokens) and §3.4 (utility-first composition) are in place, an additional simplification falls out: **the per-CssGroup `Impl<TH extends Theme>` matrix becomes obsolete.**

The reasoning: with strict semantic-token discipline, every component body is a constant string referencing `var(--token-*)`. Theme variation enters the cascade through the token layer, not through per-class per-theme bodies. The compile-time gate that `Impl<TH>` was buying — "every theme implements every method" — is verifying a property that has no failure mode if all bodies reference tokens.

This RFC adopts the simplification:

#### 3.6.1 CssGroup becomes a marker

```java
// Before — heavy parent class with instance methods + EsModule binding:
public record StudioStyles() implements CssGroup<StudioStyles> {
    public static final StudioStyles INSTANCE = new StudioStyles();
    @Override public List<CssClass<StudioStyles>> cssClasses() { return List.of(...); }
    @Override public CssImportsFor<StudioStyles> cssImports()  { return ...; }
    public interface Impl<TH extends Theme> extends CssGroupImpl<StudioStyles, TH> {
        CssBlock<st_root> st_root();
        // 80+ abstract methods
    }
    // 80+ record declarations, no bodies
}

// After — empty marker for routing, classes carry their own bodies:
public final class StudioStyles implements CssGroupMarker<StudioStyles> {
    public static final StudioStyles INSTANCE = new StudioStyles();

    public record st_root() implements CssClass<StudioStyles> {
        @Override public String body() { return "min-height: 100vh; …"; }
    }
    public record st_card() implements CssClass<StudioStyles> {
        @Override public String body() {
            return """
                background: var(--color-surface-raised);
                color: var(--color-text-primary);
                padding: var(--space-4);
                border-radius: var(--radius-md);
                """;
        }
    }
    // … 80 records, each with body inline
}
```

The marker class exists for:
- **Routing** — the `CssClass<G>` type parameter still binds a class to its JS module; the framework knows that requesting `bg_accent` resolves through `Util.INSTANCE`'s module.
- **Organization** — file-level grouping for IDE / codebase navigation.

The marker no longer carries class enumeration or import chains as instance methods. These move to static fields on the marker (or are discovered via reflection on nested records — small open decision, lean explicit-static):

```java
public final class StudioStyles implements CssGroupMarker<StudioStyles> {
    public static final List<CssClass<StudioStyles>> CLASSES = List.of(
        new st_root(), new st_card(), …
    );
    public static final List<CssGroupMarker<?>> IMPORTS = List.of();
    // …
}
```

#### 3.6.2 Theme is identity only; vars and globals are sibling singletons

`CssGroupImpl<CG, TH>` and the per-CssGroup nested `Impl<TH>` interface go away. `Theme` itself becomes a pure identity object:

```java
public interface Theme {
    String slug();
    default String label() { return slug(); }
}
```

The actual content — variable values and global rules — lives in *separate* singleton objects bound to the theme via type parameter. Each is independently servable as its own CSS file, which the browser caches per-theme regardless of how many CssGroups consume it.

```java
public interface ThemeVariables<TH extends Theme> {
    TH theme();
    Map<CssVar, String> values();
}

public interface ThemeGlobals<TH extends Theme> {
    TH theme();
    String css();   // raw CSS, served verbatim
}
```

A theme composes its singletons:

```java
public record HomingDefault() implements Theme {
    public static final HomingDefault INSTANCE = new HomingDefault();
    @Override public String slug() { return "homing-default"; }

    public record Vars() implements ThemeVariables<HomingDefault> {
        public static final Vars INSTANCE = new Vars();
        @Override public HomingDefault theme() { return HomingDefault.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }
        private static final Map<CssVar, String> VALUES = Map.ofEntries(
            Map.entry(StudioVars.COLOR_SURFACE,        "#FAFBFD"),
            Map.entry(StudioVars.COLOR_SURFACE_RAISED, "#FFFFFF"),
            Map.entry(StudioVars.SPACE_4,              "16px")
            // … every required CssVar
        );
    }

    public record Globals() implements ThemeGlobals<HomingDefault> {
        public static final Globals INSTANCE = new Globals();
        @Override public HomingDefault theme() { return HomingDefault.INSTANCE; }
        @Override public String css() { return GLOBALS; }
        private static final String GLOBALS = """
            /* descendant selectors over markdown, @media overrides, etc. */
            """;
    }
}
```

The two-layer (primitive + semantic) pattern from §3.2 doesn't disappear — `Vars.values()` is free to reference primitives internally via `var()` chains. That organization is a property of the theme implementation, not of the framework's contract.

The framework's `ThemeRegistry` holds three lists: `themes()`, `variables()`, `globals()`. Each entry is a singleton instance.

#### 3.6.3 Three routes, three resources

The CSS pipeline splits into three independently-cacheable endpoints:

| Route | Content | Cache scope |
|---|---|---|
| `GET /theme-vars?theme=Y` | `:root { …Y.Vars.values() }` rendered to CSS | per theme — one shot per page |
| `GET /theme-globals?theme=Y` | `Y.Globals.css()` verbatim | per theme — one shot per page |
| `GET /css-content?class=G&theme=Y` | Per-class rules for G + auto-synthesized variants. **No `:root` block, no globals.** | per `(group, theme)` |

The CSS manager (`CssClassManager.js`) auto-loads the theme bundle (`/theme-vars` + `/theme-globals` for the active theme) before any group-scoped CSS, idempotently. The cascade is established by the theme bundle; per-group files contribute only the class rules that consume the cascade.

This means: a page that loads StudioStyles + Util under `homing-default` ships *one* `:root` block (cached), *one* globals block (cached), and *one* per-group rule set per group. Today's design redundantly emits the cascade in every `/css-content` response.

#### 3.6.4 What this buys

- **One file per CssGroup** holding declarations + class bodies.
- **One file per Theme** holding the identity record + nested `Vars` + nested `Globals` singletons (all bound by type parameter to that theme).
- **No `(group, theme)` matrix** — themes scale with the deployment, not with the number of groups.
- **Browser caching is naturally efficient.** `:root` and globals are loaded once per theme; per-group CSS files are smaller (just class rules, no cascade re-emission).
- **Adding a new CssClass** to a group is one record edit; no theme impl needs updating.
- **Adding a new theme** is one new file; no per-group impl needs creating.
- **Net code reduction** vs. RFC 0002's pattern: substantial. StudioStyles' ~580-line `StudioStylesHomingDefault` impl folds back into the StudioStyles class as inline bodies. Demo themes' ~2,200 lines collapse similarly.

#### 3.6.5 What this loses

1. **Compile-time theme-completeness gate is reshaped, not lost.** RFC 0002 Phase 01 designed `Impl<TH>` so that adding a CssClass forced every theme to compile-fail until updated. That gate verified a *structural* property (every theme has a method for every class), which was an indirect proxy for what we actually want: that every variable a class depends on is provided by every theme. The marker model gives us the *direct* gate via §3.6.6 — typed `CssVar` declarations + build-time conformance — which catches strictly more failures than the old method-per-record contract did.

2. **Genuinely structural per-theme bodies are still expressible via `Themed<G>`** (§3.6.7). The same interface that carries `requiredVars()` for normal var-dependent classes also lets a class override `bodyFor(Theme)` for the rare case of structurally-different bodies per theme. No separate escape hatch; one mechanism.

3. **Class-discovery mechanism is new.** Marker class no longer has `cssClasses()` instance method. Either reflection on nested records or explicit `static List<CssClass<G>> CLASSES`. Lean explicit (D9 below).

#### 3.6.6 Variable dependencies as typed values

CssVar is a typed record, not a free-form string. Each deployment defines its vocabulary as a set of `CssVar` constants:

```java
public record CssVar(String name) {
    public String ref() { return "var(" + name + ")"; }
}

public final class StudioVars {
    public static final CssVar COLOR_SURFACE        = new CssVar("--color-surface");
    public static final CssVar COLOR_SURFACE_RAISED = new CssVar("--color-surface-raised");
    public static final CssVar COLOR_TEXT_PRIMARY   = new CssVar("--color-text-primary");
    public static final CssVar SPACE_4              = new CssVar("--space-4");
    // …

    /** Full vocabulary — useful for conformance / iteration. */
    public static final Set<CssVar> ALL = Set.of(
        COLOR_SURFACE, COLOR_SURFACE_RAISED, COLOR_TEXT_PRIMARY, SPACE_4 /* … */
    );
}
```

CssClasses that depend on variables declare them via `Themed.requiredVars()` (§3.6.7). Bodies reference them via `varConstant.ref()`:

```java
public record st_card() implements Themed<StudioStyles> {
    @Override public Set<CssVar> requiredVars() {
        return Set.of(StudioVars.COLOR_SURFACE_RAISED, StudioVars.COLOR_TEXT_PRIMARY, StudioVars.SPACE_4);
    }
    @Override public String body() {
        return "background: " + StudioVars.COLOR_SURFACE_RAISED.ref() + ";"
             + "color: "      + StudioVars.COLOR_TEXT_PRIMARY.ref() + ";"
             + "padding: "    + StudioVars.SPACE_4.ref() + ";";
    }
}
```

The framework derives the deployment's required-var checklist by union over every active class's `requiredVars()`. Themes must provide a value for every var in that checklist.

**Two build-time conformance checks:**

1. **Body-references-only-declared-vars.** Parse each class's body for `var(--…)` references. Every parsed name must correspond to a `CssVar` in that class's `requiredVars()`. Catches drift where the body adds a new var without updating the declaration.

2. **Theme-provides-every-required-var.** For every registered theme, `theme.variables().keySet()` must be a superset of the deployment's required-var checklist. Catches theme-incomplete deployments at build time.

#### 3.6.7 `Themed<G>` for var-dependent and structurally-themed classes

Three categories of CssClass cleanly:

| Category | Interface | `body()` | `requiredVars()` | `bodyFor(Theme)` |
|---|---|---|---|---|
| **Truly theme-agnostic** | `CssClass<G>` | constant string, no `var(--…)` | n/a | n/a |
| **Var-dependent** (most common themed) | `Themed<G>` | constant string, may use `var.ref()` | declared | default — returns `body()` |
| **Structurally per-theme** (rare) | `Themed<G>` | null | declared (vars referenced across all bodies) | overridden, dispatches per theme |

```java
public interface Themed<G extends CssGroup<G>> extends CssClass<G> {
    /** Variables this class depends on, resolved by the active theme. */
    Set<CssVar> requiredVars();

    /** Per-theme body. Default returns the constant body() (which may use
     *  var refs that the theme cascade resolves). Override only for the rare
     *  case of genuinely different body strings per theme. */
    default String bodyFor(Theme theme) { return body(); }
}
```

The renderer:

1. If `cls instanceof Themed t` → use `t.bodyFor(currentTheme)` (which is `t.body()` by default).
2. Else → use `cls.body()`.

Same code path produces a body string in either case; the difference is whether the theme is consulted to resolve structural variation.

The `Themed<G>` design extends naturally to non-CSS theming. A future SVG variant would parameterize differently — `Themed<G> extends SvgBeing<…>` returning `SvgContent` from `contentFor(Theme)` — same shape, different content type. Not implemented in this RFC; design hook only.

#### 3.6.8 Static themes only — explicit non-goal

Theme is fixed per page load. To switch, the user reloads with `?theme=<slug>`. The renderer is per-request, the cascade is fixed at page load, no client-side dynamic theme swap.

This is a deliberate trade-off:
- **Simpler.** No DOM thrash, no FOUC, no var-set-at-runtime mechanism, no `:root` mutation.
- **Safer.** Each theme is a known shape; rendering is deterministic per request.
- **Cheaper.** Browsers cache one CSS bundle per theme rather than recomputing on every swap.
- **Downside:** minor inconvenience (a reload, not an instant transition). Acceptable for the safety/simplicity trade.

#### 3.6.9 `ThemeGlobals` — the surviving escape hatch

`ThemeGlobals.css()` returns raw CSS, served verbatim at `/theme-globals?theme=Y`. After the marker model + `Themed<G>` + utility variants, almost every use becomes expressible per-class. Audit of today's `StudioStylesHomingDefault.globalRules()` (~150 lines) shows what's left:

| Content | Status |
|---|---|
| `html, body { … }` resets | Move to `Globals.css()` of the appropriate theme, OR skip entirely (browser defaults). |
| Single-property `.foo:hover { color: var(--bar); }` | Replaced by hover variants (Phase 01 auto-synthesis). |
| Multi-property `.foo:hover { transform; box-shadow; border-color; }` | Expressible as a CssClass with `pseudoState() = ":hover"` and multi-line body. |
| `@media (max-width: 920px) { … }` (responsive) | Future work — `mediaQuery()` mechanism on CssClass. For now, `Globals.css()` covers it. |
| `.st-doc h1, .st-doc h2 { … }` (descendant selectors over markdown content) | **Not replaceable.** Markdown renders raw `<h1>` tags with no class hooks. Use `Globals.css()`. |
| `@media (prefers-color-scheme: dark) { :root { --color-foo: bar } }` (conditional var values) | **Not replaceable** without it. Use `Globals.css()` if a single theme wants to internally adapt to system preference. (Default recommendation: define discrete themes instead — `HomingDefault`, `HomingDark` — and let users pick via `?theme=`. But the escape hatch remains.) |

**Conclusion:** `ThemeGlobals` is a free-form escape hatch with two real use cases (descendant-selector rules over content the framework didn't class; conditional var definitions under media queries). Any other use should be expressible per-class — if it isn't, that's a signal the framework is missing a mechanism, not that `ThemeGlobals` should grow.

---

## 4. Phases

| # | Title | Scope |
|---|---|---|
| 01 | Framework: `pseudoState()` + `variants()` on `CssClass` + `UtilityCssClass<G>` marker + renderer auto-synthesis | homing-core, homing-server |
| 02 | Framework: `semanticTokens()` on `CssGroupImpl` + renderer change | homing-core, homing-server |
| 03 | Framework: ES6 module emission with `CssClass` / `CssUtility` JS classes; auto-discovery of registered variants | homing-server (module emitter) |
| 04 | Studio's starter semantic vocabulary in `StudioStylesHomingDefault` | homing-studio-base (additive — existing primitives stay) |
| 05 | Framework: `body()` on `CssClass` + renderer prefers inline body over impl-method lookup | homing-core, homing-server |
| 06 | `Util` CssGroup with inline class-level bodies (no `Impl<TH>`); trivial `UtilImpl` registered | homing-studio-base |
| 07 | Refactor `StudioStylesHomingDefault` component bodies to semantic tokens; drop the now-redundant hover rules from `globalRules()` | homing-studio-base |
| 08 | Migrate studio JS modules to use base utilities + variant properties at call sites (`bg_accent.hover` shape — no parens) | homing-studio (every AppModule that uses hover-bearing classes) |
| 09 | Framework: `CssGroupMarker<G>` + identity-only `Theme` + `ThemeVariables<TH>` + `ThemeGlobals<TH>` sibling interfaces + typed `CssVar` record + `Themed<G>` with `requiredVars()` + `bodyFor(Theme)`; new routes `/theme-vars` and `/theme-globals`; CssClassManager auto-loads theme bundle; retire `CssGroupImpl<CG, TH>` and per-group `Impl<TH>`; new `ThemeConsistencyTest` (body-refs-only-declared-vars + theme-vars-provides-every-required-var + theme-vars-and-globals-presence) | homing-core, homing-server |
| 10 | Migrate `StudioStyles` to marker shape with inline bodies via `Themed<G>` + `static CLASSES`; create `StudioVars` typed vocabulary; create `HomingDefault` Theme record + nested `Vars` and `Globals` singletons; retire `StudioStylesHomingDefault.java` | homing-studio-base |
| 11 | Migrate demo CssGroups + themes to marker shape — `DemoVars` vocabulary, demo Themes with nested `Vars` / `Globals` singletons; retire 10 demo impl files | homing-demo |
| 12 | Documentation + RFC status flip + retire ES5 pitfall in live-tracker-pattern guide | docs |

Demo migration is **explicitly deferred** to a separate effort.

---

## 5. Decisions (all resolved 2026-05-07)

All twelve open decisions resolved through Phases 01-12. Outcomes:

1. **D1 — `semanticTokens()` separate vs folded into `cssVariables()`.** **Resolved: folded into a flat map.** Phase 02 added a separate `semanticTokens()` method as initially recommended. Phase 09's structural rework retired `CssGroupImpl<CG, TH>` entirely; the new `ThemeVariables<TH>.values()` is a single flat `Map<CssVar, String>`. Implementers split primitive/semantic internally if they want — the framework no longer imposes the split.

2. **D2 — Where does `Util` live?** **Resolved: `homing-studio-base`** (Phase 06). Apps importing StudioStyles already depend on studio-base.

3. **D3 — `pseudoState()` as default method on `CssClass`, or sibling `StatefulCssClass<G>`?** **Resolved: default method on `CssClass`** (Phase 01). Non-breaking; flat type hierarchy.

4. **D4 — Naming convention for utility records.** **Resolved: snake_case Java → kebab-case CSS** (Phase 06). Numeric suffixes after underscore: `p_4`, `gap_2`. Framework's `CssClassName.toCssName` handles the transform.

5. **D5 — Should `Util` register an impl per theme?** **Resolved: no.** Utilities are theme-agnostic via semantic tokens. Phase 06 created a trivial `UtilImpl` placeholder; Phase 11 made the renderer tolerant of missing impls, so the placeholder is now optional.

6. **D6 — Multi-property hover effects.** **Resolved: stay in `STRUCTURAL_CSS`.** Single-property hovers migrated to Util variants on a case-by-case basis (Phase 08 did `.st-dep:hover` → `cn(st_dep, border_emphasis.hover)`). Multi-property rules (.st-card:hover, .st-step-card:hover, .st-app-pill:hover) stay in the shared structural CSS — splitting into 3-utility composites at every call site wasn't worth the verbosity.

7. **D7 — Variant property emission.** **Resolved: only registered ones** (Phase 01 + Phase 03). Driven by `cls.variants()` set on each base. The CssGroupContentProvider emits `.hover` / `.focus` / `.active` properties only for the states each base advertises.

8. **D8 — JS dialect for emitted module code.** **Resolved: ES6+ throughout the project.** ES6 modules are the loading mechanism; every browser running the code supports the rest. ES5 conventions in hand-written views retired. Live-tracker-pattern guide updated.

9. **D9 — Class-discovery mechanism for marker-shaped CssGroups.** **Resolved: kept the existing `cssClasses()` instance method.** The marker-shape rework (Phase 09/10) didn't require changing the discovery mechanism. CssGroup<G> kept its `cssClasses()` instance method; records still implement `CssClass<G>` and are listed there. Saved a structural change that wasn't actually buying anything — the discovery question was driven by an earlier draft that proposed retiring `CssGroup<G>` entirely as a marker, but Phase 09 took a more incremental path.

10. **D10 — Where do variables and global rules live?** **Resolved: on sibling singletons under each Theme.** Theme stays identity-only (`slug() + label()`). Each theme gets nested singleton records `Vars implements ThemeVariables<TH>` and `Globals implements ThemeGlobals<TH>`, served at independent routes (`/theme-vars`, `/theme-globals`). Browser caches them once per theme.

11. **D11 — Structural per-theme body variation.** **Resolved: `Themed<G>` interface available with `bodyFor(Theme)`, but unused in production.** Phase 11's demo migration explored both options and chose token-driven for PlaygroundStyles' substantial per-theme variation (compound values like 8-stop gradients live behind single CssVars). The bodyFor(Theme) escape hatch remains available for the rare future case that genuinely needs structural variation.

12. **D12 — `requiredVars()` source-of-truth.** **Resolved: deferred — interface available, conformance test scoped out.** `Themed.requiredVars()` exists on the interface; no production class implements `Themed` yet. Bodies use string-literal `var(--color-foo)` directly. The conformance test that would parse bodies for `var()` refs and cross-check against `requiredVars()` was scoped out — current cascade-correctness is verified visually + by the existing tests. The mechanism is documented and ready when needed; doesn't justify the migration cost yet.

**Implementation lessons (post-execution notes worth recording):**

- The `--st-white` primitive doing double duty (card surface AND text-on-inverted) created a dark-mode legibility regression. Surface roles invert correctly via primitive override; text-on-inverted needs a separate disambiguation. Initial fix: per-theme `@media` block re-binding `--color-text-on-inverted` directly. **Final fix: retire the primitive layer entirely.** Each semantic role now gets a concrete value per theme (light) plus an independent override per theme (dark, in the `@media` block). One layer instead of two; no more "primitive doing double duty" class of bug, period. `StudioVars` now contains only semantic CssVar constants (15 colour roles + 8 spacing + 3 radius); the 11 `--st-*` primitive constants were removed.

- The `CssClassManager.js` `ensureThemeBundleLoaded(theme)` initially returned early when `theme` was null, breaking pages visited without an explicit `?theme=` URL parameter — the cascade was never set up. Fix: always call the routes; let the server pick its registered default.

- Ordering matters for the cascade: `/theme-vars` must land in the DOM before `/theme-globals` (sequential, not `Promise.all`). Globals' `@media (prefers-color-scheme: dark) { :root { … } }` overrides primitives; if vars loaded last, the unconditional `:root` would shadow the @media override (last-wins for same specificity in CSS).

---

## 6. Acceptance criteria

`mvn install` green across all 8 modules at every phase boundary.

**Phases 01–04 (already shipped):**
- `pseudoState()` + `variants()` + `UtilityCssClass<G>` available; framework auto-synthesizes variant rules from the base body.
- `semanticTokens()` available on `CssGroupImpl`; rendered output emits primitives + semantic tokens in one `:root` block.
- Emitted JS modules use ES6 classes (`CssClass`, `CssUtility`, `HoverVariant`/`FocusVariant`/`ActiveVariant`); manager exposes `_css.cls(name[, variants])` factory.
- Studio's starter semantic vocabulary (~25 tokens across surface/text/border/accent + spacing + radius scales) emitted by `StudioStylesHomingDefault.semanticTokens()`.

**Phases 05–08 (utility-first):**
- `body()` available on `CssClass`; renderer prefers inline body over impl-method lookup.
- `Util` group exists in homing-studio-base with class-level bodies; trivial `UtilImpl` registered.
- `StudioStyles` component bodies contain no hard-coded color hex; all resolve through `var(--color-*)`.
- At least one studio JS module uses a `base.hover` property-access shape at a real call site (visual smoke).

**Phases 09–12 (structural simplification):**
- `CssGroupMarker<G>` available; `CssGroupImpl<CG, TH>` retired.
- `Theme` is identity-only (`slug() + label()`); content lives in sibling singletons `ThemeVariables<TH>` and `ThemeGlobals<TH>`.
- New routes `/theme-vars?theme=Y` and `/theme-globals?theme=Y` serve theme-scoped CSS independently. `/css-content?class=G&theme=Y` returns class rules only (no `:root`, no globals).
- `CssClassManager.js` auto-loads the theme bundle (vars + globals) before any group-scoped CSS, idempotently per theme.
- Typed `CssVar` record exists; deployment vocabulary classes (`StudioVars`, `DemoVars`) collect named constants.
- `Themed<G>` interface exists with `requiredVars(): Set<CssVar>` and `bodyFor(Theme)`; default `bodyFor` returns constant `body()`.
- `StudioStyles` is a marker class — records implement `CssClass<G>` (no vars) or `Themed<G>` (with vars); `static List<CssClass<StudioStyles>> CLASSES` field for discovery.
- `HomingDefault` exists as a top-level `Theme` record with nested `Vars` + `Globals` singletons; `Vars.values()` provides every required `CssVar`.
- All 10 demo theme impl files retired; demo CssGroups carry inline bodies; demo Themes have nested `Vars` / `Globals` singletons.
- New `ThemeConsistencyTest` replaces `CssGroupImplConsistencyTest` — three checks: (1) every class's body references only its declared `requiredVars()`; (2) every theme has a registered `ThemeVariables` whose `values()` covers every required `CssVar`; (3) every theme has a registered `ThemeGlobals` (may be empty).
- Doc: live-tracker-pattern guide rewritten for the new model; ES5 pitfall note removed.

---

## 7. Future work (out of scope here)

- **Demo migration** under the same playbook. Expected ~70% line reduction in demo theme files.
- **Codegen for utility generation** from a token spec (Tailwind-style breadth without hand-writing every record).
- **Media-query variants** (`md_*`, `dark_*`) via a `mediaQuery()` method on `CssClass`.
- **Promote `Util` to `homing-server`** if it acquires consumers outside the studio-base lineage.
