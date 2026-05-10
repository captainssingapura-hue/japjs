# RFC 0002 — Typed Themes for CssGroups (Header / Impl Split)

| Field | Value |
|---|---|
| **Status** | **Implemented** — Phases 01–07 landed 2026-05-07. File-based fallback removed; typed impls are the sole CSS source for studio + demo. |
| **Author** | Howard, with Homing |
| **Created** | 2026-05-06 |
| **Supersedes** | None (refines the existing string-based theme parameter) |
| **Target phase** | Before further studio-base extraction (StudioStyles is the dependency that's blocking DocBrowser/DocReader from moving) |

---

## 0. Status notice

This RFC proposes typing the theme dimension of CSS resolution. It is the prerequisite for moving `StudioStyles` to `homing-studio-base` cleanly: today the CSS file is bound to one canonical name with one default theme, leaving downstream projects no clean way to plug in their own visual identity without colliding on the resource path.

The deliberation that produced this RFC is captured in the conversation history; the gist is reflected in §4 (alternatives considered).

---

## 1. Motivation

### 1.1 What's broken today

The theme dimension is a leaky string abstraction at every layer:

```java
// homing-server/CssContentGetAction:
String basePath = "homing/css/" + query.className().replace(".", "/");
String path = query.theme() != null
        ? basePath + "." + query.theme() + ".css"   // homing/css/.../StudioStyles.dark.css
        : basePath + ".css";                         // homing/css/.../StudioStyles.css
```

```js
// browser:
import { st_root } from "/module?class=...StudioStyles&theme=dark";
```

Concrete failure modes:

- A typo in `theme=dakr` is **silently swallowed** — `CssContentGetAction` falls back to the default `.css`. The deployment ships in the wrong colors and nobody notices until a visual review.
- Adding a new theme = creating a `.css` file with a magic suffix. There's no Java declaration anywhere that says "this theme exists." Every consumer has to memorize the slug.
- Downstream projects can't replace `StudioStyles` styling without colliding on the canonical resource path; the only path is theme variants with magic-string slugs.
- `StudioStyles.java` mixes the *contract* (which classes exist) with the *default implementation* (the `.css` file at one specific resource path). Moving the contract requires moving the implementation.
- There is no compile-time check that every theme has rendered every declared class. A theme can omit `st_panel` entirely; consumers see unstyled panels at runtime.

### 1.2 The architectural insight

A `CssGroup` should be like a C++ header file: a stable typed declaration of which classes exist. The `.css` rules are an implementation, swappable per-theme. The current design conflates the two.

A clean header/impl split makes:

- The **contract** (CssGroup + records) part of the public API
- Each **impl** (theme application of the contract) a typed, registerable, compile-time-verified Java construct
- **Theme** itself a typed singleton — a lookup key, not a free-form string

This RFC formalizes that split.

---

## 2. Goals & non-goals

### Goals

- Themes are typed: each theme is a Java record implementing a `Theme` interface, with a `slug()` for URL/persistence
- Per-`(CssGroup, Theme)` implementations are typed: a stateless `CssGroupImpl<CG, TH>` record carries the binding
- Compile-time completeness: adding a `CssClass` to a `CssGroup` forces every `CssGroupImpl` to update or fail compilation
- Resolution is registry-based, explicit, audit-able (mirrors `HomingLibsRegistry` discipline)
- No silent fallbacks: a missing theme returns a 404 with a clear error
- Multi-theme support: a deployment can register many themes simultaneously; per-request `theme` slug picks one
- The CSS bytes themselves are produced by Java methods on the impl, not from `.css` files — single source of truth, refactor-safe

### Non-goals

- **JS-side runtime theme switching** is out of scope. The browser still gets one stylesheet per page load. Hot-swap theming would require a separate mechanism (subscription model, CSS variable injection at runtime). Future RFC if needed.
- **Generic CSS-in-JS** is not the intent. The CSS is in *Java*, on the server. The browser still receives plain `.css`.
- **Cascade / specificity tooling.** This RFC doesn't add a CSS linter or selector validator. CSS bodies are hand-written strings; standard CSS rules apply.
- **Themes for SvgGroup or other future asset types.** Scope is `CssGroup` only. The pattern is generalizable but not generalized in this RFC.

---

## 3. Design

### 3.1 Three new types in `homing-core`

#### `Theme`

```java
public interface Theme {
    /** URL/filename slug. Stable, kebab-case. e.g. "homing-default", "acme-dark". */
    String slug();

    /** Optional human-readable name. Defaults to slug(). */
    default String label() { return slug(); }
}
```

Theme records are stateless singletons:

```java
public record HomingDefault() implements Theme {
    public static final HomingDefault INSTANCE = new HomingDefault();
    @Override public String slug() { return "homing-default"; }
    @Override public String label() { return "Homing default"; }
}
```

> **Note on the asymmetry with `CssGroup<C extends CssGroup<C>>` etc.** Most typed primitives in the codebase use F-bounded polymorphism (`Foo<T extends Foo<T>>`) because they have *self-returning methods* — e.g., `CssGroup.exports()` returns `ExportsOf<C>`, where `C` must be the implementor's own type to preserve type evidence. `Theme` has no such methods (`slug()` and `label()` both return `String`), so the F-bound earns nothing. We deliberately keep `Theme` plain.

#### `CssGroupImpl<CG, TH>`

```java
public interface CssGroupImpl<CG extends CssGroup<CG>, TH extends Theme> {
    /** Identity: which CssGroup this impl applies to. */
    CG group();
    /** Identity: which Theme this impl realizes. */
    TH theme();

    /** Optional CSS custom properties (cascading vars) emitted as :root { … }. */
    default Map<String, String> cssVariables() { return Map.of(); }
}
```

Stateless: implementing records have zero instance fields. The `TH` type parameter is kept (not collapsed to plain `Theme`) so impls expose the *specific* theme they realize — `StudioStylesHomingDefault.theme()` returns `HomingDefault`, not just `Theme`. That precision lets calling code recover the concrete theme without a cast.

#### Per-CssGroup `Impl<TH>` nested interface — the compile-time gate

Each `CssGroup` declares its own per-theme impl interface, exposing one abstract method per declared `CssClass`:

```java
public record StudioStyles() implements CssGroup<StudioStyles> {
    public static final StudioStyles INSTANCE = new StudioStyles();

    public record st_root()   implements CssClass<StudioStyles> {}
    public record st_header() implements CssClass<StudioStyles> {}
    // … all ~90 CSS class records …

    /**
     * Per-theme implementation contract. A concrete impl that doesn't override
     * every method here is a compile error. Adding a record above forces every
     * downstream theme to add its method here.
     */
    public interface Impl<TH extends Theme> extends CssGroupImpl<StudioStyles, TH> {
        @Override default StudioStyles group() { return INSTANCE; }
        CssBlock<st_root>   st_root();
        CssBlock<st_header> st_header();
        // … one method per CssClass record, each parameterized by its matching record …
    }
}
```

> **Note on the return type — `CssBlock<CC extends CssClass<?>>`.** A typed wrapper documents intent at the call site (the value is a CSS rule body, not arbitrary text), reserves room for future safety checks (balanced-brace validation, escape policy) and composition helpers (theme inheritance via merge / override), and matches the project's pattern of typed values over strings.
>
> The class-witness parameter (`CssBlock<st_root>` for the body of `.st-root`) catches "I pasted the wrong body into the wrong method" at compile time and lets future composition helpers be type-safe by construction — you can only merge two `CssBlock<st_root>` values, never a `<st_root>` with a `<st_header>`.
>
> The wrapper is a one-line record (`record CssBlock<CC extends CssClass<?>>(String body) {}`) with static `of(String)` and `empty()` factories — minimal cost, real future-proofing.

### 3.2 Concrete impls

```java
public record StudioStylesHomingDefault() implements StudioStyles.Impl<HomingDefault> {

    public static final StudioStylesHomingDefault INSTANCE = new StudioStylesHomingDefault();

    @Override public HomingDefault theme() { return HomingDefault.INSTANCE; }

    @Override public Map<String, String> cssVariables() {
        return Map.of(
            "--st-navy",      "#1A2330",
            "--st-amber",     "#C8921E",
            "--st-amber-dk",  "#A87815",
            "--st-ice",       "#F5FBFF"
            // … all defined custom props …
        );
    }

    @Override public CssBlock<st_root> st_root() { return CssBlock.of("""
        display: flex;
        flex-direction: column;
        min-height: 100vh;
        background: var(--st-ice);
        """);
    }

    @Override public CssBlock<st_header> st_header() { return CssBlock.of("""
        background: var(--st-navy);
        color: white;
        padding: 16px 24px;
        """);
    }

    // … one method per CssClass record …
}
```

### 3.3 Server-side resolution

`CssContentGetAction` is refactored to look up an impl from the registry instead of constructing a `.css` path:

```
GET /css?class=hue.captains.singapura.js.homing.studio.css.StudioStyles&theme=homing-default
                                                                          │
   ┌──────────────────────────────────────────────────────────────────────┘
   │
   ▼
1. Resolve CssGroup: load StudioStyles.class via reflection (existing behavior).
2. Look up registered CssGroupImpl where impl.group().equals(group)
                                  AND impl.theme().slug().equals("homing-default").
3. If found:
     - Render: ":root { --x: y; ... }\n.st-root { <body> }\n.st-header { <body> }\n…"
     - Return as text/css.
   If not found:
     - Return 404 with error: "No impl registered for StudioStyles styled by homing-default."
```

The CSS body output for each class is `.<kebab-case-of-record-name> { <impl-method-output> }`. The class-name → kebab-case conversion uses the existing `CssClassName.toCssName(...)` utility.

### 3.4 Registry pattern (mirrors `HomingLibsRegistry`)

```java
public final class CssGroupImplRegistry {
    public static final List<CssGroupImpl<?, ?>> ALL = List.of(
        StudioStylesHomingDefault.INSTANCE
        // future: StudioStylesHomingDark.INSTANCE, AcmeStudioStyles.INSTANCE, …
    );
}
```

The same eternal-good rationale captured in `HomingLibsRegistry` applies (audit-able, build-deterministic, fail-loud, downstream-friendly).

`StudioActionRegistry` (or its homing-server-level base) takes the registry as a constructor argument. Downstream apps construct their own registry list when they have their own themes.

### 3.5 URL contract (unchanged)

The user-facing URL still looks like `/css?class=…&theme=homing-default`. The `theme` query parameter is the slug. What changes is purely server-side: the resolver consults the typed registry instead of building a file path.

This preserves the JS side (`CssClassManager.js`'s `loadCss(name, theme)`) without modification.

### 3.6 No backward-compat fallback

A request with no theme parameter should fail unless the deployment has registered a "default theme" at the `StudioActionRegistry` level. Two equivalent forms:

- `/css?class=X` — uses the registry's configured default
- `/css?class=X&theme=homing-default` — explicit

Implementation choice: `StudioActionRegistry` constructor takes a `defaultTheme: Theme<?>` parameter; missing-theme requests use it. If the deployment doesn't configure a default, `/css?class=X` 404s.

---

## 4. Alternatives considered

### 4.1 CSS variables only (rejected: insufficient)

Refactor `StudioStyles.css` to use CSS custom properties; downstream overrides via `:root { --color: …; }`. Simple, no Java changes.

**Why rejected:** addresses only color/spacing tweaks. Doesn't allow layout/structural changes, doesn't address the silent-fallback problem, doesn't give compile-time guarantees. Useful as a *complement* (kept in the proposal as the `cssVariables()` method on CssGroupImpl), not a replacement.

### 4.2 Theme-variant `.css` files (rejected: silent fallback)

Use the existing `?theme=slug` mechanism by convention; document it in the guide.

**Why rejected:** the silent-fallback failure mode is the core problem. A typo or missing file lands you on the wrong CSS without a hint.

### 4.3 SPI / pluggable theme provider with `.css` files (rejected: complexity without payoff)

Introduce a `StudioThemeProvider` interface; downstream registers a provider that returns `.css` paths. Server consults a chain.

**Why rejected:** adds a new SPI mechanism while still having the `.css` files as the source of truth. Doesn't solve the compile-time-completeness problem. More indirection, same untyped substrate.

### 4.4 Subclassable / abstract `StudioStyles` (rejected: breaks type safety)

Make `StudioStyles` abstract; downstream extends with their own concrete class plus their own `.css`.

**Why rejected:** records can't extend records; using interfaces here breaks the typed-record-as-CSS-class pattern. Workarounds (separate naming hierarchies, generic bounds) become contagious through every consumer.

### 4.5 Generic `body(CssClass<CG>)` method on `CssGroupImpl` (rejected: no compile-time completeness)

```java
public interface CssGroupImpl<CG extends CssGroup<CG>, TH extends Theme> {
    String body(CssClass<CG> cssClass);   // dispatches by runtime type
}
```

**Why rejected:** the impl can return empty/null for any class without the compiler catching it. Build-time conformance test could check it, but that's a runtime gate, not compile-time. The per-CssGroup nested `Impl<TH>` interface gives true compile-time completeness via abstract methods.

---

## 5. Trade-offs accepted

### 5.1 CSS-as-Java-strings

The chosen design has CSS bodies returned by Java methods as text-block strings.

**Loses:**

- IDE syntax highlighting / autocomplete inside the strings
- Standard CSS tooling (linters, prettifiers, unused-selector detection)
- Easy diffs (a stray `;` change pollutes the line)

**Wins:**

- Refactor-safe: rename a `CssClass` record → all impl methods rename
- Single source of truth (no Java/CSS drift)
- Compile-time completeness across themes
- Themes can compose Java values cleanly (shared color constants, calculated paddings, theme inheritance via delegation)
- Single typed unit of distribution: shipping a theme is shipping a `.class` file, not a directory of resources

For our scale (one studio CssGroup with ~90 classes, modest theme count expected) the wins outweigh the losses. For a 5,000-rule design system, the trade-off would lean the other way.

### 5.2 Two places to edit

Adding a new `CssClass` to a `CssGroup` requires:

1. Adding the record inside the group
2. Adding the abstract method to the nested `Impl<TH>` interface
3. Implementing that method in every concrete impl (compile error otherwise)

This is a feature, not a bug: it forces an explicit "do all themes need to support this?" decision and prevents adding orphan classes that no theme styles.

### 5.3 No silent fallback

Old behavior: missing theme silently used the default. New behavior: 404. Possible operational pain during initial migration when consumers' deployment configs need updating. Acceptable trade — silent fallbacks were the central failure mode this RFC fixes.

### 5.4 Memory / rendering cost

CSS rendering is now per-request: string-concat ~90 method calls + a few `:root` vars. Negligible for typical loads, but could become noticeable for high-traffic deployments.

**Mitigation:** the registry is static and the impls are stateless, so the rendered CSS is fully cacheable per `(group, theme)` tuple. Add a server-side cache (`HashMap<Pair<Class, String>, String>`) populated lazily; invalidate on JVM restart only. Out of scope for the initial implementation; trivial to add when needed.

---

## 6. Compile-time and build-time guarantees

| Guarantee | Mechanism |
|---|---|
| Adding a `CssClass` forces every `Impl<TH>` to update | Per-CssGroup nested interface with abstract methods — javac error |
| A registered impl's `group()` matches the type-parameter `CG` | Generic bound `CssGroupImpl<CG, TH>` — javac error if mis-paired |
| A registered impl's `theme()` matches the type-parameter `TH` | Same |
| The deployment has a default theme | StudioActionRegistry constructor signature requires a `Theme<?>` argument |
| Conformance: every CssGroup imported in any active DomModule has at least one registered impl | Build-time scan: walks `CssGroupImplRegistry.ALL`, asserts coverage |
| Conformance: registered impls have non-null `group()` and `theme()` | Build-time scan |
| URL theme slug → typed `Theme` lookup is total at startup | Server boot loads registry once, fails to start if any duplicate slug or missing default |

---

## 7. Migration plan

| Phase | Work | Verification |
|---|---|---|
| **01 — Generic types** | Add `Theme.java` and `CssGroupImpl.java` to `homing-core` | New types compile; no behavior change yet |
| **02 — `StudioStyles.Impl<TH>` nested interface** | Add the nested interface inside `StudioStyles` with one abstract method per existing `CssClass` (~90 methods) | `StudioStyles` compiles; no impl exists yet so no consumer of the interface |
| **03 — `HomingDefault` theme + impl record** | Create `HomingDefault.java` + `StudioStylesHomingDefault.java`. Translate every rule from `StudioStyles.css` into the corresponding method body. Translate the `:root` vars into `cssVariables()` map. | Concrete impl compiles without errors; spot-check a few methods against the original CSS |
| **04 — Server refactor** | Change `CssContentGetAction` to consult the registry; refactor `CssGroupContentProvider` so the JS-served CssGroup module triggers the right loadCss path. Add `CssGroupImplRegistry`. Wire `StudioActionRegistry` to take a default theme + the impl registry. | `mvn install` green; visual smoke shows no styling regression on studio routes |
| **05 — Hard cut: delete `StudioStyles.css`** | Remove the old resource file; verify no fallback is wired in | Studio still styled correctly via the new path |
| **06 — Conformance test** | Add a test that walks `CssGroupImplRegistry.ALL`, asserts internal consistency + asserts every active CssGroup has at least one impl + every impl has unique `(group, theme.slug)` keys | New test passes |
| **07 — Documentation** | Update the live-tracker-pattern guide's prerequisite section to mention how to register themes; add a short "How to add a theme" section | Guide serves correctly via DocReader |

Total effort: ~4 hours, with the bulk in phase 03 (mechanical translation of ~90 CSS rules into Java method bodies).

The phases align with the project's existing pause-and-verify discipline. Each phase ends with a green build + visual smoke; we pause between phases for the user to commit.

---

## 8. Open questions

### 8.1 Where do `HomingDefault` and `StudioStylesHomingDefault` live?

**Default answer:** `homing-studio`. They're Homing-specific concretizations of a studio-base contract.

**Tension:** today `StudioStyles` lives in `homing-studio`. After this RFC, it could move to `homing-studio-base` (the contract) while the impl stays in `homing-studio` (the homing-branded look). That extraction is consistent with the broader studio-base goals.

**Recommendation:** do the extraction in a follow-up RFC alongside DocBrowser/DocReader. This RFC keeps the existing locations to minimize blast radius.

### 8.2 Should the existing string-based theme parameter mechanism be retained as a fallback?

**Default answer:** no. Hard cut.

**Tension:** existing deployments may have ad-hoc themed `.css` files outside the typed registry.

**Recommendation:** hard cut. The existing themed `.css` file mechanism is undocumented and unused in any current deployment. Cleaner to start typed.

### 8.3 Should `Theme` extend `Importable` so themes can be referenced via the framework's `nav.*` typed nav?

**Default answer:** not in this RFC.

**Reasoning:** themes are a server-config concern. JS-side theme switching is out of scope. If a future RFC wants users to navigate to "the same page in dark mode" via a typed link, that RFC can extend `Theme` then.

### 8.4 Per-element themes (different parts of one app rendered with different themes)?

**Default answer:** out of scope.

**Reasoning:** all consumers using this so far have one theme per page load. Per-element theming requires per-element CSS scoping (Shadow DOM, scoped attributes) that's a much larger undertaking.

---

## 9. Acceptance criteria

The RFC is "done" when:

- [ ] `Theme` and `CssGroupImpl<CG, TH>` interfaces exist in `homing-core` with full Javadoc
- [ ] `StudioStyles.Impl<TH>` nested interface exists with one abstract method per declared `CssClass`
- [ ] `HomingDefault` theme record exists in `homing-studio`
- [ ] `StudioStylesHomingDefault` impl exists in `homing-studio` with all methods implemented (compile-checked)
- [ ] `cssVariables()` returns the existing `:root` custom properties from `StudioStyles.css`
- [ ] `CssGroupImplRegistry` exists with the homing default registered
- [ ] `CssContentGetAction` resolves via registry, not file path
- [ ] `StudioStyles.css` is deleted from resources
- [ ] Conformance test `CssGroupImplConsistencyTest` passes
- [ ] Visual smoke: `/app?app=studio-catalogue`, `/app?app=rename-plan`, `/app?app=rfc0001-plan`, `/app?app=doc-browser` all render with correct styling
- [ ] `mvn install` is green across all 8 modules with no allow-list exceptions
- [ ] No `https://`-style imports remain in JS resources (existing CdnFree conformance still passes)
- [ ] Live-tracker-pattern guide updated with a "Themes" subsection

---

## 10. Future work

- Extracting `StudioStyles` to `homing-studio-base` (separate RFC)
- A `SvgGroupImpl<SG, TH>` parallel for SVG asset theming, if needed
- Server-side caching of rendered CSS strings (trivial; deferred until perf justifies)
- A CLI helper that scaffolds a new theme by reading the existing CssGroup and producing a stub impl with all methods returning empty bodies for the user to fill in
