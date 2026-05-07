package hue.captains.singapura.js.homing.studio.rfc0002ext1;

import java.util.List;

/**
 * Implementation tracker for RFC 0002-ext1 — Utility-First Composition + Two-Layer Semantic Tokens.
 *
 * <p>Source-of-truth for {@link Rfc0002Ext1Plan} and {@link Rfc0002Ext1Step} views.
 * Edit this file to update progress, resolve decisions, or revise phases.
 * Recompile the studio module and refresh — the new state appears live.</p>
 *
 * <p>Companion document: {@code docs/rfcs/0002-ext1-utility-first-and-semantic-tokens.md}.</p>
 */
public final class Rfc0002Ext1Steps {

    public enum Status {
        NOT_STARTED("Not started", "not-started"),
        IN_PROGRESS("In progress", "in-progress"),
        BLOCKED("Blocked", "blocked"),
        DONE("Done", "done");

        public final String label;
        public final String slug;
        Status(String label, String slug) { this.label = label; this.slug = slug; }
    }

    public enum DecisionStatus {
        OPEN("Open", "open"),
        RESOLVED("Resolved", "resolved");

        public final String label;
        public final String slug;
        DecisionStatus(String label, String slug) { this.label = label; this.slug = slug; }
    }

    public record Task(String description, boolean done) {}

    public record Dependency(String phaseId, String reason) {}

    public record Decision(
            String id,
            String question,
            String recommendation,
            String chosenValue,
            DecisionStatus status,
            String rationale,
            String notes
    ) {}

    public record Phase(
            String id,
            String label,
            String summary,
            String description,
            Status status,
            List<Task> tasks,
            List<Dependency> dependsOn,
            String verification,
            String rollback,
            String effort,
            String notes,
            List<Metric> metrics
    ) {
        public int progressPercent() {
            if (tasks.isEmpty()) return 0;
            long done = tasks.stream().filter(Task::done).count();
            return (int) (done * 100 / tasks.size());
        }
    }

    /**
     * Code-reduction metric captured at phase completion. Surfaced in the
     * studio's Plan and Step views so the cumulative impact of the RFC is
     * visible.
     *
     * @param label    short description of what's being measured
     * @param before   value before the phase landed (free-form, e.g. "938 lines")
     * @param after    value after the phase landed (e.g. "939 lines")
     * @param delta    optional summary of the change (e.g. "−52 primitive refs")
     */
    public record Metric(String label, String before, String after, String delta) {}

    public static final String RFC_DOC = "rfcs/0002-ext1-utility-first-and-semantic-tokens.md";

    // ------------------------------------------------------------------
    // OPEN DECISIONS — resolve before/during execution.
    // ------------------------------------------------------------------
    public static final List<Decision> DECISIONS = List.of(

            new Decision("D1",
                    "`semanticTokens()` separate method vs folded into `cssVariables()`?",
                    "Separate method on `CssGroupImpl`.",
                    null,
                    DecisionStatus.OPEN,
                    "Caller intent is clearer when 'primitives' and 'semantic tokens' are different methods. Renderer change is two lines. Convention-based prefixing (`--token-*`) inside one map is fragile and hard to grep.",
                    ""
            ),

            new Decision("D2",
                    "Where does the `Util` CssGroup live?",
                    "homing-studio-base.",
                    null,
                    DecisionStatus.OPEN,
                    "Apps importing StudioStyles already depend on studio-base. Promoting Util to homing-server is a future move once the shape stabilizes and other consumers want it without StudioStyles.",
                    ""
            ),

            new Decision("D3",
                    "`pseudoState()` as default method on `CssClass` vs sibling `StatefulCssClass<G>` interface?",
                    "Default method on `CssClass`.",
                    null,
                    DecisionStatus.OPEN,
                    "Non-breaking — every existing record returns null. Type hierarchy stays flat. Sibling type is 'purer' but adds another type to reason about.",
                    ""
            ),

            new Decision("D4",
                    "Naming convention for utility records (especially scale-step utilities like spacing).",
                    "snake_case Java → kebab-case CSS, matching existing record convention. Numeric suffixes after underscore: `p_4`, `gap_2`, `xl_2`.",
                    null,
                    DecisionStatus.OPEN,
                    "Java disallows leading digits in identifiers (no `2xl`). Underscore-suffix gives unambiguous parsing. The framework's `CssClassName.toCssName` already handles snake↔kebab.",
                    ""
            ),

            new Decision("D5",
                    "Should `Util` register a separate `CssGroupImpl` per theme?",
                    "No — single `UtilHomingDefault` impl serves every theme.",
                    null,
                    DecisionStatus.OPEN,
                    "Utility bodies reference semantic tokens, not primitives. The semantic layer is theme-mapped (in `StudioStyles*` impls); utilities inherit theme-correctness through the cascade.",
                    ""
            ),

            new Decision("D6",
                    "How to handle multi-property hover effects (e.g. card-lift = transform + shadow + border)?",
                    "Compose multiple utilities at the call site for new code; existing multi-property rules stay in `globalRules()` until refactor pressure justifies splitting.",
                    null,
                    DecisionStatus.OPEN,
                    "Splitting a 3-property hover rule into 3 utilities is more verbose at call sites but theme-resilient. Pragmatic mix during migration.",
                    ""
            ),

            new Decision("D7",
                    "Should every base utility get every variant property on the JS side, or only the registered ones?",
                    "Only registered. Emitter walks the group's `cssClasses()` for `VariantOf<?, ?>` entries and emits `.hover` / `.focus` / etc. as CssClass-instance properties only for variants that actually exist for that base.",
                    null,
                    DecisionStatus.OPEN,
                    "Saves bytes; an unregistered access (`base.hovr`) reads as `undefined` at runtime and conformance-test failure at build. Consistent with the framework's fail-loud-at-build-time doctrine. Single value type — every variant is a CssClass instance, not a method returning a string (avoids the type-union smell).",
                    ""
            ),

            new Decision("D8",
                    "JS dialect for emitted module code: ES5 or ES6+?",
                    "ES6+ throughout the project.",
                    "ES6+",
                    DecisionStatus.RESOLVED,
                    "The framework's loading mechanism is ES6 modules (the `import` statement is required for it to work at all). Every browser that runs the code already supports ES6 classes, `const`, arrow functions, template literals, etc. The earlier ES5 stylistic convention is retired. Update the live-tracker-pattern guide to remove the ES5 pitfall note.",
                    "User decision 2026-05-07."
            ),

            new Decision("D9",
                    "Class-discovery mechanism for marker-shaped CssGroups: reflection on nested records, or explicit `static List<CssClass<G>> CLASSES`?",
                    "Explicit static field.",
                    null,
                    DecisionStatus.OPEN,
                    "Reflection on nested records is more magical, harder to reason about ordering, and couples discovery to file-organization choices. A static `CLASSES` field is one more place to remember to update when adding a record, but the duplication is local and a quick conformance test (`CLASSES contains every nested CssClass record found via reflection`) can guarantee consistency.",
                    ""
            ),

            new Decision("D10",
                    "Where do `primitives()` / `semantics()` / `globalRules()` live: on `Theme` (per-theme), on `CssGroupMarker` (per-group), or both?",
                    "On `Theme` only.",
                    null,
                    DecisionStatus.OPEN,
                    "Themes are the deployment-level concept; primitives and semantics are properties of the brand/visual identity, not of the CssGroup that consumes them. Per-group `globalRules()` can move into the appropriate theme's `globalRules()` (most studio rules are theme-agnostic CSS resets that apply to the whole page, not to one CssGroup). If a group genuinely needs group-scoped global rules in the future, we add it then.",
                    ""
            ),

            new Decision("D11",
                    "Should structural per-theme body variation be supported?",
                    "Yes, via the unified `Themed<G>` interface — same mechanism that carries `requiredVars()` for normal var-dependent classes also lets a class override `bodyFor(Theme)` for structurally different bodies per theme.",
                    null,
                    DecisionStatus.OPEN,
                    "One interface, two use sites: var-dependent classes leave `bodyFor()` defaulted (returns constant `body()` resolved by the cascade); structurally-themed classes override `bodyFor(Theme)` to dispatch per theme. Naturally extends to non-CSS theming (SVG, JS) via parameterized `Themed<T>` if needed later.",
                    ""
            ),

            new Decision("D12",
                    "`requiredVars()` source-of-truth: declared explicitly in Java OR inferred from body parsing?",
                    "Declared explicitly via typed `CssVar` constants. Body remains hand-written CSS; declaration is the canonical machine-readable contract. Conformance test catches drift.",
                    null,
                    DecisionStatus.OPEN,
                    "Body strings stay readable; `requiredVars()` is the typed contract. Build-time check parses bodies for `var(--…)` refs and verifies each matches a `CssVar` in `requiredVars()`. Two surfaces (declaration + body) but typed `CssVar` constants (used in both via `.ref()`) keep them in sync. Stronger gate than `Impl<TH>`-method-per-record because it catches the actual semantic dependency (variables), not a structural proxy (methods).",
                    ""
            )
    );

    // ------------------------------------------------------------------
    // PHASES — execute in order. Each has a verification gate.
    // ------------------------------------------------------------------
    public static final List<Phase> PHASES = List.of(

            new Phase("01",
                    "Framework: `pseudoState()` + `variants()` + `UtilityCssClass<G>` + auto-synthesis",
                    "Add the optional pseudo-class mechanism + auto-generated variants from base + state set. Non-breaking.",
                    "Two additions to homing-core: (1) `default String pseudoState() { return null; }` on `CssClass`; (2) `default Set<String> variants() { return Set.of(); }` on `CssClass` — base utilities advertise which states they want; (3) a `UtilityCssClass<G> extends CssClass<G>` convenience marker that defaults `variants()` to `{ \"hover\", \"focus\", \"active\" }` for the common case. Renderer in `CssContentGetAction` synthesizes variant rules by iterating `cls.variants()` per base, emitting `.<state>-<kebab>:<state> { <base body> }` per state. No separate variant records needed; framework owns the variant kebab convention. Existing records render byte-identically (default `variants()` returns empty set).",
                    Status.DONE,
                    List.of(
                            new Task("Add `pseudoState()` default method to `CssClass`", true),
                            new Task("Add `variants()` default method to `CssClass` returning `Set.of()`", true),
                            new Task("Add `UtilityCssClass<G>` convenience marker interface defaulting `variants()` to hover/focus/active", true),
                            new Task("Update `CssContentGetAction.renderCss()`: per base, emit bare rule + one rule per state in `cls.variants()` reusing base body", true),
                            new Task("Test: bare CssClass renders plain selector with body", true),
                            new Task("Test: bare CssClass with `pseudoState() = \":hover\"` renders `.foo:hover { … }`", true),
                            new Task("Test: base with explicit `variants() = Set.of(\"hover\", \"focus\")` emits two extra rules (hover + focus), NOT active", true),
                            new Task("Test: `UtilityCssClass` marker gives all three states automatically", true),
                            new Task("Verify all existing tests pass — every existing record renders identically (variants() default is empty)", true)
                    ),
                    List.of(),
                    "`mvn install` green across all 8 modules. 5 new unit tests in CssContentGetActionTest pass. Existing rendered output unchanged for non-utility CssClasses.",
                    "Revert two-method addition on CssClass + UtilityCssClass.java + the renderer's variant loop. Existing CssClass surface unchanged.",
                    "30 minutes (revised down from 1.5h)",
                    "INITIAL DESIGN (retired): had separate VariantOf<B,G>, HoverVariantOf<B,G>, FocusVariantOf<B,G>, ActiveVariantOf<B,G> interfaces and required per-variant records (hover_bg_accent etc.). User pushed back: 'as long as the base utility is there, everything could be generated on the fly.' Validated and refactored — variants() set on the base + framework convention `<state>-<kebab>` is strictly simpler and loses no realistic capability. Net: ~4 files deleted from homing-core, renderer simplified, tracker reflects the cleaner shape."
                    ,List.of()
            ),

            new Phase("02",
                    "`semanticTokens()` on CssGroupImpl",
                    "Add the second token-layer method. Non-breaking.",
                    "Added a `default Map<String, String> semanticTokens() { return Map.of(); }` method to `CssGroupImpl`. Updated `CssContentGetAction.renderCss()` to fold both maps into one `:root { ... }` block, primitives first, then semantic tokens (so semantic can reference primitive via `var(--…)`).",
                    Status.DONE,
                    List.of(
                            new Task("Add `semanticTokens()` default method to `CssGroupImpl`", true),
                            new Task("Update `CssContentGetAction.renderCss()` to emit primitives then semantic tokens, both inside one `:root {}`", true),
                            new Task("Add unit test asserting emission order (primitive → semantic) within one :root block", true),
                            new Task("All existing impls compile unchanged (default returns empty map)", true)
                    ),
                    List.of(new Dependency("01", "Independent, but lands as the second half of the framework prep.")),
                    "`mvn install` green across all 8 modules. New test passes: both maps fold into one `:root {}`, primitives precede semantic tokens.",
                    "Revert two-line edit on CssGroupImpl + the renderer's emission. No downstream coupling.",
                    "30 minutes",
                    ""
                    ,List.of()
            ),

            new Phase("03",
                    "Framework: ES6 module emission with `CssClass` / `CssUtility`",
                    "Replace closure-based JS exposition with ES6 classes; auto-discover registered variants per CssGroup.",
                    "Rewrote `CssClassManager.js` to expose top-level `class CssClass` (uniform value type) plus dedicated subclasses per pseudo-state — `HoverVariant`, `FocusVariant`, `ActiveVariant`. `class CssUtility extends CssClass` constructs each variant as a precomputed instance of the appropriate subclass via a `VARIANT_CLASSES` lookup table. `_css.cls(name[, variants])` returns either `CssClass` or `CssUtility`. Every class handle is `instanceof CssClass`; variant handles are additionally `instanceof HoverVariant` (etc.). `resolve()` only handles `CssClass` instances (no string branch). Updated `CssGroupContentProvider` to walk the group's cssClasses once and, per base, emit `_css.cls(\"kebab\", { state: \"<state>-kebab\" ... })` driven by `cls.variants()`. After the Phase 01 simplification (VariantOf records retired), the provider no longer needs a two-pass bucketing step — variants are computed structurally from the base.",
                    Status.DONE,
                    List.of(
                            new Task("Rewrite `CssClassManager.js`: top-level `class CssClass` / `class CssUtility extends CssClass`; manager singleton via IIFE for closure scope", true),
                            new Task("Replace `_n` field with `.name` and `toString()` on the class — `resolve()` accepts strings + CssClass instances", true),
                            new Task("Unify `cls(name, variants?)` factory: returns `CssUtility` if variants provided, else `CssClass`", true),
                            new Task("Update `CssGroupContentProvider.content()` to discover variants per base by walking `cssClasses()` for `VariantOf<?,?>` entries", true),
                            new Task("Per base: emit `_css.cls(\"kebab\")` if no variants, else `_css.cls(\"kebab\", { hover: \"hover-kebab\", focus: \"focus-kebab\" })`", true),
                            new Task("Skip variant records in the standalone-identifier emission loop — they're reachable only through their base (D7)", true),
                            new Task("Add `CssGroupContentProviderTest` with 4 cases: plain class, base+variants, variants-not-standalone, bootstrap preserved", true),
                            new Task("Verify all existing studio + demo modules still load and render — string coercion via toString() makes the change invisible to existing call sites", true)
                    ),
                    List.of(new Dependency("01", "Variants need `VariantOf<B,G>` declarations to discover; emitter buckets them by `base()`.")),
                    "`mvn install` green across all 8 modules. 4 new content-provider tests pass. Existing 13 EsModule tests still pass — call sites unchanged thanks to ES6 string-coercion via toString().",
                    "Revert CssClassManager.js + CssGroupContentProvider.java + new test. Emitted modules go back to `_css.cls(name)` returning `{ _n }` frozen objects.",
                    "1.5 hours actual",
                    "Surprisingly compact: walking cssClasses for VariantOf entries is enough — no need to wire CssGroupImplRegistry through to the emitter (the variant declarations live on the CssGroup itself, not on the impl). Discovery is purely structural."
                    ,List.of()
            ),

            new Phase("04",
                    "Studio semantic vocabulary",
                    "Define the starter semantic-token names in `StudioStylesHomingDefault.semanticTokens()`.",
                    "Added 14 colour roles (surface ×4, text ×5, border ×2, accent ×3) + spacing scale (`--space-1` through `--space-8`) + radius scale (`--radius-sm/md/lg`). Each maps to an existing primitive `var(--st-*)`. No component bodies changed — this is the new layer's introduction. Returned the LinkedHashMap directly (not Map.copyOf) so declaration order is preserved in the rendered output.",
                    Status.DONE,
                    List.of(
                            new Task("Add `semanticTokens()` to `StudioStylesHomingDefault` with surface/text/border/accent roles", true),
                            new Task("Add spacing scale: `--space-1` through `--space-8` (4px increments)", true),
                            new Task("Add radius scale: `--radius-sm`, `--radius-md`, `--radius-lg`", true),
                            new Task("Return LinkedHashMap directly (preserves order in rendered :root block)", true),
                            new Task("Build green — purely additive change, no existing rendering affected", true),
                            new Task("Live verification deferred to Phase 06 (when component bodies start referencing the tokens — any missing token will surface visually)", false)
                    ),
                    List.of(new Dependency("02", "Needs `semanticTokens()` to exist on the impl interface.")),
                    "`mvn install` green. Rendered `/css-content?class=…StudioStyles` includes the 14 `--color-*`, 8 `--space-*`, 3 `--radius-*` tokens in declaration order, after the primitive `--st-*` block.",
                    "Delete the new `semanticTokens()` method. Component bodies still reference primitives, so no visual regression.",
                    "30 minutes",
                    "Order of values inside Map is preserved by returning the LinkedHashMap directly instead of `Map.copyOf` (which docs say is unspecified order). Cosmetic concern only since CSS vars resolve regardless of declaration order — but readable rendered output matters for debugging."
                    ,List.of()
            ),

            new Phase("05",
                    "Framework: `body()` on `CssClass`",
                    "Inline class-level body support. Renderer prefers `cls.body()` over impl-method lookup.",
                    "Added `default String body() { return null; }` to `CssClass`. Updated `CssContentGetAction.renderCss()` with a body-resolution branch: prefers inline `cls.body()` when non-null, falls back to the impl-method dispatch via reflection otherwise. Variant auto-synthesis (Phase 01) reuses the resolved body regardless of which source provided it — works for both inline-bodied bases and impl-method-bodied bases.",
                    Status.DONE,
                    List.of(
                            new Task("Add `default String body() { return null; }` to `CssClass`", true),
                            new Task("Update `CssContentGetAction.renderCss()` to prefer `cls.body()` when non-null; fall back to impl-method dispatch otherwise", true),
                            new Task("Add `CssContentGetActionTest` case: a record with inline `body()` renders without needing an impl method", true),
                            new Task("Add `CssContentGetActionTest` case: variant auto-synthesis works for inline-bodied bases (hover variant of inline_bodied reuses limegreen body)", true),
                            new Task("All existing tests pass — every existing CssClass returns null body() and falls back to impl-method dispatch", true)
                    ),
                    List.of(),
                    "`mvn install` green across all 8 modules. 2 new unit tests pass (renders_inlineBodied_withoutImplMethod, renders_inlineBodied_variantsReuseInlineBody). Existing rendered output unchanged for bodyless CssClasses.",
                    "Revert one-method addition on CssClass + the renderer's body() preference branch. Existing CssClass surface unchanged.",
                    "30 minutes",
                    ""
                    ,List.of()
            ),

            new Phase("06",
                    "`Util` CssGroup with class-level bodies",
                    "Util group with inline bodies; trivial UtilImpl registered.",
                    "Created `Util` CssGroup in homing-studio-base with 38 utility records: 8 color/visual base utilities (UtilityCssClass — hover/focus/active variants auto-synthesized), plus 30 layout utilities (plain CssClass — no variants). Bodies inline via `body()`, referencing semantic tokens (--color-*, --space-*) provided by StudioStylesHomingDefault. Trivial `UtilImpl` (empty cssVariables / semanticTokens / globalRules) registered under HomingDefault to satisfy the registry's (group, theme.slug) resolution; will be retired entirely in Phase 09.",
                    Status.DONE,
                    List.of(
                            new Task("Create `Util` CssGroup record in homing-studio-base", true),
                            new Task("Define 8 color/visual base records as `UtilityCssClass<Util>` (auto hover/focus/active): bg_accent, bg_accent_emphasis, bg_surface_raised, color_link, color_link_emphasis, border_emphasis, translate_y_neg_2, shadow_lg", true),
                            new Task("Define 30 layout records as plain `CssClass<Util>` (no variants): p_1..p_8, m_1..m_8, gap_1..gap_6, flex, grid, inline_flex, items_{center,start,end}, justify_{center,between}, text_center", true),
                            new Task("Create trivial `UtilImpl` registered under `HomingDefault.INSTANCE`", true),
                            new Task("Register `UtilImpl.INSTANCE` in `CssGroupImplRegistry.ALL`", true),
                            new Task("Add `UtilRenderingTest` smoke test in homing-studio-base/src/test — 5 assertions covering inline body, variant auto-synthesis, no variants on layout, spacing token references, no fall-through to impl-method dispatch", true),
                            new Task("`mvn install` green across all 8 modules", true)
                    ),
                    List.of(
                            new Dependency("01", "Hover/focus variants are auto-generated from `UtilityCssClass<G>` / `cls.variants()`."),
                            new Dependency("04", "Utility bodies reference semantic tokens, which must exist in the cascade."),
                            new Dependency("05", "Inline bodies need `body()` on `CssClass`.")
                    ),
                    "All tests green. New `Util` group resolves at `/css-content?class=hue.captains.singapura.js.homing.studio.base.css.Util` and emits ~38 base rules + 24 auto-synthesized variant rules (8 utility bases × 3 states), all reading inline bodies. UtilImpl has zero per-class methods — the renderer never falls through to impl-method dispatch.",
                    "Delete `Util.java`, `UtilImpl.java`, `UtilRenderingTest.java`, and the registry entry. Nothing else depends on Util yet.",
                    "1 hour actual",
                    ""
                    ,List.of()
            ),

            new Phase("07",
                    "Refactor StudioStyles bodies to semantic tokens",
                    "Component bodies now reference semantic `var(--color-*)` tokens instead of primitive `var(--st-*)` tokens.",
                    "Mechanical substitution. Every primitive `var(--st-*)` reference in component method bodies + globalRules (post-@media-dark block) was replaced with the appropriate semantic role: surface roles for backgrounds, text roles for `color:` properties, border-emphasis for borders, accent-emphasis for hover/active states. Added `--color-text-on-inverted-muted` semantic token for the `--st-ice` role. Multi-property hover rules in globalRules() stay (will move to per-class with Phase 08+10).",
                    Status.DONE,
                    List.of(
                            new Task("Replace primitive var refs with semantic ones in every component body — 52 substitutions across the file", true),
                            new Task("Add `--color-text-on-inverted-muted` to semanticTokens() for the `--st-ice` role", true),
                            new Task("`mvn install` green; rendered CSS values are byte-identical (every semantic token resolves to the same primitive)", true),
                            new Task("Visual no-regression on every studio page — deferred until visual smoke", false),
                            new Task("Dark-mode visual no-regression (DevTools → emulate `prefers-color-scheme: dark`) — deferred", false)
                    ),
                    List.of(new Dependency("06", "Util base utilities + variants must exist before deleting their globalRules counterparts.")),
                    "Visual smoke shows no regression on any studio route, in either light or dark mode. `mvn install` green.",
                    "Revert StudioStylesHomingDefault to the prior commit.",
                    "1 hour actual",
                    "",
                    List.of(
                            new Metric("File line count",                                  "938 lines",   "939 lines",  "+1 (one new semantic token)"),
                            new Metric("Primitive var(--st-*) refs in component bodies",  "52",          "0",          "−52 (100% theme-portable)"),
                            new Metric("Primitive var(--st-*) refs total in file",        "67",          "15",         "−52 (the 15 remaining are ALL in semanticTokens(), where they belong — the primitive→semantic mapping)"),
                            new Metric("Semantic var(--color-*) refs in file",            "84",          "135",        "+51"),
                            new Metric("Semantic vocabulary size",                         "14 tokens",   "15 tokens",  "+1 (--color-text-on-inverted-muted)")
                    )
            ),

            new Phase("08",
                    "Migrate studio JS modules to use base + variant properties",
                    "First call sites adopt the Tailwind-style `cn(st_X, util.hover)` shape.",
                    "Migrated `cn(st_dep)` → `cn(st_dep, border_emphasis.hover)` in 4 Step views (Rfc0001Step, Rfc0002Step, Rfc0002Ext1Step, RenameStep). Each AppModule's `imports()` gained a single `new Util.border_emphasis()` line — variants are reachable as JS-side properties on the base (Phase 03). Retired the corresponding `.st-dep:hover { border-color: … }` rule from `StudioStylesHomingDefault.globalRules()`. Multi-property hover rules in globalRules (.st-card, .st-step-card, .st-toc-item, etc.) stay — they need multiple utilities composed at call site, deferred to Phase 10 when component bodies move on-class.",
                    Status.DONE,
                    List.of(
                            new Task("Add `Util.border_emphasis` import to all 4 Step AppModules' `imports()`", true),
                            new Task("Update 4 JS views: `cn(st_dep)` → `cn(st_dep, border_emphasis.hover)`", true),
                            new Task("Retire `.st-dep:hover { … }` rule from `StudioStylesHomingDefault.globalRules()`", true),
                            new Task("`StudioCssConformanceTest` passes (every Util base used in JS is in the module's imports)", true),
                            new Task("All 39 studio tests pass; build green across all 8 modules", true),
                            new Task("Visual smoke: hover on phase dependency badges still highlights border with accent — deferred to live verification", false),
                            new Task("Multi-property hover rules deferred to Phase 10 (will move on-class with the marker refactor)", false)
                    ),
                    List.of(
                            new Dependency("03", "Variant methods on JS-side base objects must be emitted."),
                            new Dependency("07", "Bodies must reference semantic tokens before utilities can take over.")
                    ),
                    "All studio routes pass conformance + visual smoke. Hover behavior on phase dependency badges visible in DevTools as `.hover-border-emphasis:hover` rules.",
                    "Revert per-AppModule. Atomic commits.",
                    "1 hour actual",
                    "",
                    List.of(
                            new Metric("Studio JS modules using `base.variant` shape", "0", "4", "+4 (all four Step views)"),
                            new Metric("Util base utility imports added", "0", "4", "+4 (one `Util.border_emphasis` per Step)"),
                            new Metric("Hover rules in `StudioStylesHomingDefault.globalRules()`", "11", "10", "−1 (`.st-dep:hover` retired)"),
                            new Metric("Auto-synthesized variant rules now active at real call sites", "0", "1", "first proof point — variants pull their weight")
                    )
            ),

            new Phase("09",
                    "Framework: `CssGroupMarker<G>` + identity-only `Theme` + sibling `ThemeVariables`/`ThemeGlobals` + typed `CssVar` + `Themed<G>` + new theme routes",
                    "The structural simplification — flatten the per-group per-theme impl matrix; vars/globals become independently-cacheable theme-scoped resources.",
                    "Introduce `CssGroupMarker<G>` as the empty-marker replacement for the heavy `CssGroup<C> extends EsModule<C>` parent (class enumeration via static `List<CssClass<G>> CLASSES` field, D9). Introduce typed `CssVar` record. Reduce `Theme` to identity-only (`slug() + label()`) — content lives in sibling singletons `ThemeVariables<TH>` (typed `Map<CssVar, String> values()`) and `ThemeGlobals<TH>` (raw `String css()`). Add two new routes: `/theme-vars?theme=Y` (renders `:root { … }` from the theme's Vars singleton) and `/theme-globals?theme=Y` (serves Globals.css() verbatim). Update `CssClassManager.js` to auto-load the theme bundle (vars + globals) idempotently per theme before any group CSS. Update `/css-content` to emit class rules only — no `:root`, no globals (those are now separate files). Introduce `Themed<G>` with `requiredVars()` + `bodyFor(Theme)`. Retire `CssGroupImpl<CG, TH>` and per-group `Impl<TH>` entirely. New `ThemeConsistencyTest` with three checks.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Add `CssGroupMarker<G>` interface to homing-core (empty marker; replaces heavy CssGroup parent for class-level-bodied groups)", false),
                            new Task("Add `CssVar` record to homing-core: `record CssVar(String name) { String ref() { return \"var(\" + name + \")\"; } }`", false),
                            new Task("Reduce `Theme` interface to identity only: `slug()` + `default label()`. Remove all content methods.", false),
                            new Task("Add `ThemeVariables<TH extends Theme>` interface: `theme()` + `Map<CssVar, String> values()`", false),
                            new Task("Add `ThemeGlobals<TH extends Theme>` interface: `theme()` + `String css()`", false),
                            new Task("Add `Themed<G extends CssGroup<G>> extends CssClass<G>` with `Set<CssVar> requiredVars()` + `default String bodyFor(Theme theme) { return body(); }`", false),
                            new Task("Retire `CssGroupImpl<CG, TH>` interface (delete file)", false),
                            new Task("Replace `CssGroupImplRegistry` with `ThemeRegistry` exposing `themes()`, `variables()`, `globals()` lists", false),
                            new Task("Add `ThemeVarsGetAction` action serving `GET /theme-vars?theme=Y` — renders `:root { value entries from Y.Vars.values() }`", false),
                            new Task("Add `ThemeGlobalsGetAction` action serving `GET /theme-globals?theme=Y` — emits Y.Globals.css() verbatim", false),
                            new Task("Update `CssContentGetAction` to emit per-class rules only (no `:root`, no globals); resolve theme via registry only for `cls instanceof Themed t ? t.bodyFor(theme) : cls.body()` dispatch + variant auto-synthesis", false),
                            new Task("Update `CssClassManager.js` `loadCss()`: ensure `/theme-vars?theme=Y` + `/theme-globals?theme=Y` are loaded (idempotent per theme) before per-group CSS", false),
                            new Task("Replace `CssGroupImplConsistencyTest` with `ThemeConsistencyTest`: (1) every class body refs only `requiredVars()` declared vars; (2) every theme's ThemeVariables covers every required CssVar in deployment; (3) every theme has ThemeVariables + ThemeGlobals (latter may be empty) registered", false),
                            new Task("Wire ThemeRegistry into StudioActionRegistry + DemoActionRegistry", false),
                            new Task("All framework tests pass with the new shape", false)
                    ),
                    List.of(
                            new Dependency("05", "Inline `body()` on CssClass is the prerequisite for class-level bodies."),
                            new Dependency("08", "Migration of studio call sites must complete before the renderer changes shape.")
                    ),
                    "`mvn install` green. New ThemeConsistencyTest passes. CssGroupImpl + per-group Impl<TH> are gone.",
                    "Restore the `CssGroupImpl` interface and per-group impls. Substantial — this is the largest single phase by surface area touched.",
                    "3 hours",
                    "Largest phase. Touches every CssGroupImpl in the project. Best landed as one commit so the type system stays consistent."
                    ,List.of()
            ),

            new Phase("10",
                    "Migrate StudioStyles to marker shape + StudioVars vocabulary + HomingDefault Vars/Globals singletons",
                    "Move per-class bodies onto StudioStyles records; introduce typed `StudioVars` vocabulary; restructure `HomingDefault` into identity record + nested `Vars` and `Globals` singletons; retire the impl class.",
                    "Create `StudioVars` constants class enumerating every `CssVar` the studio uses (~25 semantic tokens). StudioStyles becomes a `CssGroupMarker<StudioStyles>` with ~80 records — each implements `CssClass<StudioStyles>` (no vars) or `Themed<StudioStyles>` (with vars + inline `body()` using `StudioVars.X.ref()`). Static `CLASSES` field replaces `cssClasses()`. `HomingDefault` becomes the identity record + nested `HomingDefault.Vars implements ThemeVariables<HomingDefault>` (the variable map) + nested `HomingDefault.Globals implements ThemeGlobals<HomingDefault>` (raw CSS). Deletes `StudioStylesHomingDefault.java`.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Create `StudioVars` constants class with one `public static final CssVar` per semantic token (~25 constants) plus `Set<CssVar> ALL` for iteration", false),
                            new Task("Convert StudioStyles to marker shape; for each record that uses var refs, change `implements CssClass<StudioStyles>` → `implements Themed<StudioStyles>` and add `requiredVars()` declaration", false),
                            new Task("Move per-record body content from `StudioStylesHomingDefault` methods into inline `body()` on each record, using `StudioVars.X.ref()` for var references", false),
                            new Task("Add `static List<CssClass<StudioStyles>> CLASSES` field listing all records", false),
                            new Task("Restructure `HomingDefault` into identity record (slug + label) + nested `Vars` + nested `Globals` singletons", false),
                            new Task("Move `cssVariables()` + `semanticTokens()` content from `StudioStylesHomingDefault` into `HomingDefault.Vars.values()` as `Map<CssVar, String>` keyed by StudioVars constants", false),
                            new Task("Move `globalRules()` content from `StudioStylesHomingDefault` into `HomingDefault.Globals.css()` (most rules; some single-property hovers retire as utilities)", false),
                            new Task("Delete `StudioStylesHomingDefault.java`", false),
                            new Task("Register `HomingDefault.INSTANCE` + `HomingDefault.Vars.INSTANCE` + `HomingDefault.Globals.INSTANCE` in the new `ThemeRegistry`", false),
                            new Task("`StudioThemeConsistencyTest` passes (3 checks)", false),
                            new Task("Visual no-regression on every studio route — verify `/theme-vars?theme=homing-default` and `/theme-globals?theme=homing-default` are loaded by the page bootstrap", false)
                    ),
                    List.of(new Dependency("09", "Framework must support the marker shape + new Theme interface.")),
                    "Studio renders identically to before (visual smoke). One file lighter.",
                    "Revert the StudioStyles + HomingDefault changes; restore StudioStylesHomingDefault.",
                    "2 hours",
                    ""
                    ,List.of()
            ),

            new Phase("11",
                    "Migrate demo CssGroups + themes to marker shape + DemoVars vocabulary + nested Vars/Globals singletons",
                    "Same playbook for the demo. Inline bodies on demo CssGroups via `Themed<G>`; typed `DemoVars`; each demo Theme restructured into identity record + nested `Vars` + `Globals` singletons.",
                    "Demo's 7 CssGroups (AliceStyles, BaseStyles, CatalogueStyles, PitchDeckStyles, PlaygroundStyles, SpinningStyles, SubwayStyles) absorb their per-class bodies inline. Create `DemoVars` constants class for the demo's vocabulary. Demo themes (DemoDefault, Beach, Alpine, Dracula) each become identity record + nested `Vars` + `Globals` singletons. The 10 demo theme impl files retire.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Create `DemoVars` constants class enumerating every CssVar the demo uses across all 7 groups", false),
                            new Task("Convert each demo CssGroup to marker shape with `Themed<G>` records carrying inline `body()` + `requiredVars()`", false),
                            new Task("Restructure each demo Theme (DemoDefault, Beach, Alpine, Dracula) into identity record + nested `Vars` + `Globals` singletons; populate `Vars.values()` from existing primitive+semantic maps; populate `Globals.css()` from per-impl globalRules where applicable", false),
                            new Task("Delete the 10 demo theme impl files (CatalogueStylesDemoDefault, PitchDeckStylesDemoDefault, PlaygroundStyles{DemoDefault,Alpine,Beach,Dracula}, SpinningStyles{DemoDefault,Beach}, SubwayStyles{DemoDefault,Beach})", false),
                            new Task("Update DemoCssGroupImplRegistry → DemoThemeRegistry holding all 4 themes + their Vars + Globals", false),
                            new Task("DemoCssGroupImplConsistencyTest → DemoThemeConsistencyTest (3 checks)", false),
                            new Task("Demo CDN smoke: every demo route renders correctly under each theme; `/theme-vars` and `/theme-globals` routes return per-theme bundles", false)
                    ),
                    List.of(new Dependency("10", "Studio migration validates the playbook before applying to demo's 7 groups × 4 themes.")),
                    "Demo renders identically under each theme. ~10 files retired. Net code reduction substantial.",
                    "Revert the demo migration. Per-CssGroup atomic commits if doing piecemeal.",
                    "3 hours",
                    ""
                    ,List.of()
            ),

            new Phase("12",
                    "Documentation",
                    "Update guide; flip RFC status; retire ES5 pitfall note.",
                    "Rewrite the live-tracker-pattern guide's CSS section for the new model: marker groups, class-level bodies, top-level Themes, no per-group impls. Add 'Utility composition' subsection. Remove the existing ES5 pitfall (#7). Update RFC 0002-ext1 status to Implemented (with date).",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Rewrite the 'Themes' section in live-tracker-pattern.md for the marker + Theme model", false),
                            new Task("Add 'Utility composition' subsection showing `cn(base, base.hover)` shape", false),
                            new Task("Remove pitfall #7 (\"The JS dialect is ES5\") — project is ES6+ throughout (D8 RESOLVED)", false),
                            new Task("Cross-link to RFC 0002-ext1 for the full design", false),
                            new Task("Update RFC 0002-ext1 status from Draft to Implemented (with date)", false)
                    ),
                    List.of(new Dependency("11", "Documentation lands once both studio and demo migrations are complete.")),
                    "Guide serves correctly via DocReader. ES5 pitfall is gone. RFC 0002-ext1 status updated.",
                    "git checkout the doc edits.",
                    "1 hour",
                    ""
                    ,List.of()
            )
    );

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    public static Phase phaseById(String id) {
        for (var p : PHASES) if (p.id().equals(id)) return p;
        return null;
    }

    public static Decision decisionById(String id) {
        for (var d : DECISIONS) if (d.id().equals(id)) return d;
        return null;
    }

    public static int totalProgressPercent() {
        if (PHASES.isEmpty()) return 0;
        int doneTasks = 0;
        int totalTasks = 0;
        for (var p : PHASES) {
            doneTasks += (int) p.tasks().stream().filter(Task::done).count();
            totalTasks += p.tasks().size();
        }
        if (totalTasks == 0) return 0;
        return (int) ((long) doneTasks * 100 / totalTasks);
    }

    public static int openDecisionsCount() {
        return (int) DECISIONS.stream().filter(d -> d.status() == DecisionStatus.OPEN).count();
    }

    private Rfc0002Ext1Steps() {}
}
