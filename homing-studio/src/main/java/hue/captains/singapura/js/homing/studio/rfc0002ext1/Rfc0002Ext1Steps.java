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
                    "Separate method initially (Phase 02); later folded into one flat `Vars.values()` map (Phase 09/10).",
                    "Folded — flat Map<CssVar, String>",
                    DecisionStatus.RESOLVED,
                    "Phase 02 added a separate `semanticTokens()` method on `CssGroupImpl` as recommended. Phase 09's structural rework retired `CssGroupImpl<CG, TH>` entirely; the new `ThemeVariables<TH>.values()` is a single flat `Map<CssVar, String>`. Implementers split primitive/semantic internally if they want (HomingDefault.Vars puts both in one Map.ofEntries). Cleaner — the framework doesn't impose the split.",
                    "Resolved through Phase 09 simplification."
            ),

            new Decision("D2",
                    "Where does the `Util` CssGroup live?",
                    "homing-studio-base — confirmed.",
                    "homing-studio-base",
                    DecisionStatus.RESOLVED,
                    "Apps importing StudioStyles already depend on studio-base. Promoting Util to homing-server is a future move once the shape stabilizes and other consumers want it without StudioStyles.",
                    "Phase 06: Util ships in homing-studio-base alongside StudioStyles."
            ),

            new Decision("D3",
                    "`pseudoState()` as default method on `CssClass` vs sibling `StatefulCssClass<G>` interface?",
                    "Default method on `CssClass`.",
                    "default method",
                    DecisionStatus.RESOLVED,
                    "Phase 01 added `default String pseudoState() { return null; }` to `CssClass`. Non-breaking — every existing record returns null. Type hierarchy stays flat.",
                    "Phase 01."
            ),

            new Decision("D4",
                    "Naming convention for utility records (especially scale-step utilities like spacing).",
                    "snake_case Java → kebab-case CSS, matching existing record convention. Numeric suffixes after underscore: `p_4`, `gap_2`.",
                    "snake_case → kebab-case",
                    DecisionStatus.RESOLVED,
                    "Phase 06's Util group records (`p_1..p_8`, `m_1..m_8`, `gap_1..gap_6`, `flex`, etc.) follow this pattern. The framework's `CssClassName.toCssName` handles the snake↔kebab transform.",
                    "Phase 06."
            ),

            new Decision("D5",
                    "Should `Util` register a separate `CssGroupImpl` per theme?",
                    "No — utilities are theme-agnostic via semantic tokens. Phase 11 retired the legacy CssGroupImpl<CG,TH> mechanism entirely; only a trivial `UtilImpl` placeholder remains.",
                    "No (theme-agnostic via cascade)",
                    DecisionStatus.RESOLVED,
                    "Utility bodies reference semantic tokens, not primitives. Theme cascade absorbs the variation. Phase 06 created a trivial UtilImpl as a registry placeholder; Phase 11 made the renderer tolerant of `impl == null`, so the placeholder is now optional.",
                    "Phase 06 + Phase 11."
            ),

            new Decision("D6",
                    "How to handle multi-property hover effects (e.g. card-lift = transform + shadow + border)?",
                    "Multi-property hover rules stay in the shared `STRUCTURAL_CSS` constant on `HomingDefault`; single-property hovers migrated to Util variants on a case-by-case basis.",
                    "Stay in STRUCTURAL_CSS",
                    DecisionStatus.RESOLVED,
                    "Phase 08 migrated the single-property `.st-dep:hover` rule to `cn(st_dep, border_emphasis.hover)`. Multi-property rules (.st-card:hover, .st-step-card:hover, .st-app-pill:hover, etc.) remain in STRUCTURAL_CSS — splitting them into 3-utility composites at every call site was judged not worth the verbosity.",
                    "Phase 08 + Phase 10."
            ),

            new Decision("D7",
                    "Should every base utility get every variant property on the JS side, or only the registered ones?",
                    "Only registered — driven by `cls.variants()` set on each base. The CssGroupContentProvider emits `.hover`/`.focus`/`.active` properties only for the states each base declares.",
                    "Only registered",
                    DecisionStatus.RESOLVED,
                    "Phase 01's simplification (variants() set on the base instead of separate VariantOf records) made the JS-side emitter directly driven by what each base advertises. Saves bytes; misspellings fail loud at runtime as `undefined`. Single value type maintained — every variant is a CssClass instance.",
                    "Phase 01 + Phase 03."
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
                    "Kept the existing `cssClasses()` instance method on `CssGroup<G>` — neither reflection nor a separate static CLASSES field was needed.",
                    "instance method (existing)",
                    DecisionStatus.RESOLVED,
                    "The marker-shape rework (Phase 09/10) didn't require changing the discovery mechanism. CssGroup<G> kept its `cssClasses()` instance method; records still implement `CssClass<G>` and are listed there. Saved a structural change that wasn't actually buying anything. The discovery question was driven by an earlier draft that proposed retiring CssGroup<G> entirely as a marker — Phase 09 took a more incremental path.",
                    "Phase 09/10 — kept the existing pattern."
            ),

            new Decision("D10",
                    "Where do variables and global rules live?",
                    "On sibling singletons under each Theme: `ThemeVariables<TH>` (the variable map) and `ThemeGlobals<TH>` (the raw CSS), each served at independently-cacheable routes (/theme-vars, /theme-globals).",
                    "sibling singletons (Vars + Globals)",
                    DecisionStatus.RESOLVED,
                    "Theme stays a pure identity object (slug + label). Vars and Globals are nested singleton records inside each Theme record (e.g., HomingDefault.Vars, HomingDefault.Globals), bound by type parameter. Independently-cacheable: per-page, /theme-vars and /theme-globals each load once per theme regardless of how many CssGroups consume them.",
                    "Phase 09 (interfaces) + Phase 10/11 (populated)."
            ),

            new Decision("D11",
                    "Should structural per-theme body variation be supported?",
                    "Yes — `Themed<G>` interface available with `bodyFor(Theme)`. In practice no production class uses it; demo themes' variation is fully expressed via tokens (token-driven, not body-driven).",
                    "available but unused — token-driven preferred",
                    DecisionStatus.RESOLVED,
                    "Phase 09 added `Themed<G> extends CssClass<G>` with `requiredVars()` + default `bodyFor(Theme)`. Phase 11's demo migration explored both options and chose token-driven for PlaygroundStyles' substantial per-theme variation (compound values like 8-stop gradients live behind single CssVars). The bodyFor(Theme) escape hatch remains available for the rare future case that genuinely needs structural variation.",
                    "Phase 09 (interface) + Phase 11 (token-driven validated)."
            ),

            new Decision("D12",
                    "`requiredVars()` source-of-truth: declared explicitly in Java OR inferred from body parsing?",
                    "Interface supports explicit declaration; not used in practice during this RFC. Bodies use raw `var(--…)` refs without typed CssVar.ref() resolution at the body level. Future RFC could add the conformance test.",
                    "deferred — declared interface available but unused",
                    DecisionStatus.RESOLVED,
                    "Themed.requiredVars() exists on the interface; no production class implements Themed yet. Bodies use string-literal `var(--color-foo)` directly. The conformance test that would parse bodies for var() refs and cross-check against requiredVars() was scoped out — current cascade-correctness is verified visually + by the existing tests. The mechanism is documented and ready when needed; doesn't justify the migration cost yet.",
                    "Phase 09 (interface) — conformance test deferred."
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
                    "Framework: `CssVar` + `Themed<G>` + `ThemeVariables` + `ThemeGlobals` + theme-bundle routes",
                    "Additive framework infrastructure for the marker model. Phase 10/11 actually migrate; legacy `CssGroupImpl<CG, TH>` retires after.",
                    "Landed as ADDITIVE framework changes — new types and routes alongside the existing system, no behavioral changes. Existing impls (StudioStylesHomingDefault, UtilImpl, all 10 demo theme impls) keep working unchanged. Phase 10 wires StudioStyles to the new path and populates a real ThemeRegistry; Phase 11 does demo. Once both migrate, the legacy `CssGroupImpl<CG, TH>` interface and per-group `Impl<TH>` patterns can retire (Phase 12 housekeeping).",
                    Status.DONE,
                    List.of(
                            new Task("Add `CssVar` record to homing-core (typed var with `ref()` → `var(--name)`)", true),
                            new Task("Add `Themed<G> extends CssClass<G>` interface with `requiredVars()` + default `bodyFor(Theme)`", true),
                            new Task("Add `ThemeVariables<TH extends Theme>` interface (typed `Map<CssVar, String> values()`)", true),
                            new Task("Add `ThemeGlobals<TH extends Theme>` interface (raw `String css()`)", true),
                            new Task("Add `ThemeRegistry` interface in homing-server with `themes()`, `variables()`, `globals()` + slug-based lookup helpers + `EMPTY` default", true),
                            new Task("Add `ThemeVarsGetAction` serving `GET /theme-vars?theme=Y` — renders `:root { values }` (200 with empty body when theme has no registered Vars)", true),
                            new Task("Add `ThemeGlobalsGetAction` serving `GET /theme-globals?theme=Y` — emits Globals.css() verbatim (200 with empty body when none)", true),
                            new Task("Wire both new actions into `HomingActionRegistry` (default `ThemeRegistry.EMPTY`; outer registries override)", true),
                            new Task("Update `CssClassManager.js` `loadCss()`: idempotently auto-load `/theme-vars?theme=Y` + `/theme-globals?theme=Y` before per-group CSS, gracefully tolerating empty/failed bundles", true),
                            new Task("All framework tests still pass with the new shape — additive change, no behavioral regression", true),
                            new Task("Phase 10/11 will migrate Studio + Demo respectively; legacy `CssGroupImpl<CG, TH>` retires after both ship", false),
                            new Task("`ThemeConsistencyTest` base class — deferred to Phase 10 (when first deployment populates a registry to test against)", false)
                    ),
                    List.of(
                            new Dependency("05", "Inline `body()` on CssClass is the prerequisite for class-level bodies."),
                            new Dependency("08", "Migration of studio call sites must complete before the renderer changes shape.")
                    ),
                    "`mvn install` green across all 8 modules. New routes return 200 with empty bodies (registry empty). Existing pages render identically; `:root` cascade still comes from `/css-content` legacy path. Theme-bundle endpoints ready for Phase 10 to populate.",
                    "Delete the 4 new homing-core files + 3 new homing-server files. Revert HomingActionRegistry + CssClassManager.js. No downstream coupling.",
                    "1.5 hours actual",
                    "Landed additively to keep the build green and decouple from Phase 10/11 migrations. Once Phase 10 populates StudioStyles' theme bundle, the new endpoints will return real content and the legacy `:root` emission in `/css-content` becomes redundant — Phase 10 removes it.",
                    List.of(
                            new Metric("New homing-core types", "0", "4", "+4 (CssVar, Themed, ThemeVariables, ThemeGlobals)"),
                            new Metric("New homing-server types", "0", "3", "+3 (ThemeRegistry, ThemeVarsGetAction, ThemeGlobalsGetAction)"),
                            new Metric("New framework routes",   "4 (/app, /module, /css, /css-content)", "6 (+/theme-vars, /theme-globals)", "+2"),
                            new Metric("Existing CssGroupImpl files unchanged", "12 (StudioStylesHomingDefault, UtilImpl, 10 demo impls)", "12", "0 (additive — Phase 10/11 retires them)")
                    )
            ),

            new Phase("10",
                    "Migrate StudioStyles to marker shape + StudioVars vocabulary + HomingDefault Vars/Globals singletons",
                    "All 80 StudioStyles bodies inline; HomingDefault restructured; StudioStylesHomingDefault.java retired.",
                    "Created `StudioVars` typed vocabulary (37 CssVar constants — primitives + semantic tokens + spacing/radius scales). Restructured `HomingDefault` from a 17-line identity record into a 287-line shape with nested `Vars implements ThemeVariables<HomingDefault>` (Map.ofEntries with 37 typed CssVar keys → primitive/semantic values) and `Globals implements ThemeGlobals<HomingDefault>` (the html/body resets, descendant-selector rules, and @media dark-mode block). Created `StudioThemeRegistry` exposing `HomingDefault` + Vars + Globals singletons. `StudioActionRegistry` now overrides `/theme-vars` and `/theme-globals` to serve the studio's populated registry instead of the framework's empty default. The CssClassManager's auto-load (Phase 09) now pulls real cascade content per page. **Then the bulk migration:** moved all 80 per-class CSS bodies from `StudioStylesHomingDefault.java` (939 lines) into inline `body()` overrides on `StudioStyles` records (262 → 818 lines). Removed the now-orphaned nested `Impl<TH extends Theme>` interface from StudioStyles.java (~120 lines). Deleted `StudioStylesHomingDefault.java` entirely. Removed its registration from `CssGroupImplRegistry` (now contains only `UtilImpl.INSTANCE`). Conformance test `CssGroupImplConsistencyTest` updated to accept groups whose classes all have non-null `body()` (matching the renderer's body-first dispatch from Phase 05).",
                    Status.DONE,
                    List.of(
                            new Task("Create `StudioVars` constants class — 37 typed `CssVar` constants (11 primitive + 26 semantic) + `Set<CssVar> ALL`", true),
                            new Task("Restructure `HomingDefault` into identity record + nested `Vars` + nested `Globals` singletons; values keyed by StudioVars constants", true),
                            new Task("Move `cssVariables()` + `semanticTokens()` content into `HomingDefault.Vars.values()` (Map.ofEntries with CssVar keys)", true),
                            new Task("Move `globalRules()` content into `HomingDefault.Globals.css()` verbatim (preserves @media dark-mode + descendant selectors over markdown)", true),
                            new Task("Create `StudioThemeRegistry` exposing themes + variables + globals lists", true),
                            new Task("Wire `StudioActionRegistry` to override `/theme-vars` + `/theme-globals` with `StudioThemeRegistry`-backed actions", true),
                            new Task("Move all 80 CSS bodies from `StudioStylesHomingDefault` methods into inline `body()` on `StudioStyles` records", true),
                            new Task("Remove the now-orphaned `Impl<TH extends Theme>` nested interface from `StudioStyles.java` (~120 lines)", true),
                            new Task("Delete `StudioStylesHomingDefault.java` (939 lines)", true),
                            new Task("Remove `StudioStylesHomingDefault.INSTANCE` from `CssGroupImplRegistry.ALL`", true),
                            new Task("Update `CssGroupImplConsistencyTest` to accept groups whose classes all have non-null `body()` — matches renderer's body-first dispatch", true),
                            new Task("`mvn install` GREEN across all 8 modules", true),
                            new Task("Visual no-regression — deferred to live verification (server restart needed)", false)
                    ),
                    List.of(new Dependency("09", "Framework must support the marker shape + new Theme interface.")),
                    "Studio renders identically to before. Theme bundle endpoints serve real content; per-class CSS files come from inline bodies.",
                    "git revert. Most invasive single phase by line count.",
                    "2 hours actual",
                    "",
                    List.of(
                            new Metric("StudioStylesHomingDefault.java", "939 lines", "0 (deleted)", "−939"),
                            new Metric("StudioStyles.java",                "262 lines", "818 lines",  "+556 (80 inline bodies absorbed)"),
                            new Metric("HomingDefault.java",               "17 lines",  "287 lines",  "+270 (Vars + Globals nested singletons)"),
                            new Metric("StudioVars.java (new)",            "—",         "100 lines",  "+100 (37 typed CssVar constants)"),
                            new Metric("StudioThemeRegistry.java (new)",   "—",         "38 lines",   "+38"),
                            new Metric("Net studio-base CSS-related lines", "1218",      "1243",       "+25 net (after retirement, structural improvements)"),
                            new Metric("CssGroupImplRegistry.ALL entries",  "2",         "1",          "−1 (only UtilImpl remains)"),
                            new Metric("Studio CssGroups using inline bodies", "0 of 1", "1 of 1",     "100% of studio's CssGroups migrated")
                    )
            ),

            new Phase("11",
                    "Migrate demo CssGroups + themes to marker shape + DemoVars vocabulary + nested Vars/Globals singletons",
                    "All 10 demo theme impl files retired; bodies inline; theme variation expressed via DemoVars tokens.",
                    "Created `DemoVars` (41 typed CssVar constants — 28 PlaygroundStyles tokens, 6 SubwayStyles, 5 SpinningStyles, 2 cross-cutting). Restructured all 4 demo themes (DemoDefault, Beach, Alpine, Dracula) into identity record + nested `Vars` + `Globals` singletons. Moved every per-class body inline onto its CssGroup record. PlaygroundStyles' per-theme variation expressed entirely via tokens (token-driven, not Themed<G>.bodyFor) — composite values like the 8-stop sky gradient live behind a single var. Created `DemoThemeRegistry` exposing all 4 themes' singletons. `DemoActionRegistry` now overrides `/theme-vars` + `/theme-globals` with the populated registry. Deleted all 10 impl files (~2,455 lines). `DemoCssGroupImplRegistry.ALL` is now an empty list (kept the file as the conformance test still references it; the post-Phase-10 conformance check accepts groups whose classes all have non-null body()).",
                    Status.DONE,
                    List.of(
                            new Task("Create `DemoVars` constants class — 41 typed CssVars across the 7 groups", true),
                            new Task("Restructure all 4 demo Themes into identity + nested Vars + Globals singletons", true),
                            new Task("Move all per-class bodies inline onto demo CssGroup records (5 groups: Catalogue, PitchDeck, Playground, Spinning, Subway; Alice/Base stay empty)", true),
                            new Task("Express PlaygroundStyles per-theme variation via tokens (composite values behind single vars; theme-specific Vars maps provide values)", true),
                            new Task("Create `DemoThemeRegistry` with all 4 themes + their Vars + Globals", true),
                            new Task("Wire `DemoActionRegistry` to override /theme-vars + /theme-globals with DemoThemeRegistry-backed actions", true),
                            new Task("Delete all 10 demo theme impl files", true),
                            new Task("Empty `DemoCssGroupImplRegistry.ALL`; conformance test still passes via the body-fallback path", true),
                            new Task("`mvn install` GREEN; all 76 demo tests pass", true),
                            new Task("Demo CDN smoke: every demo route renders correctly under each theme — deferred to live verification", false)
                    ),
                    List.of(new Dependency("10", "Studio migration validates the playbook before applying to demo's 7 groups × 4 themes.")),
                    "Demo renders identically under each theme. 10 impl files retired. Net code reduction ~864 lines.",
                    "Revert the demo migration. Per-CssGroup atomic commits if doing piecemeal.",
                    "3 hours actual",
                    "PlaygroundStyles' four themes vary substantially in compound values (gradient stacks, multi-shadow box-shadows). Expressing this via single composite vars per property (e.g. `--pg-sky-bg` holding the entire 8-stop gradient string) — each theme's Vars map provides the full string. More tokens than a strict primitive-only approach, but bodies stay theme-agnostic and the structural variation is fully captured at the var layer.",
                    List.of(
                            new Metric("10 demo theme impl files",       "2,455 lines",       "0 (all deleted)",  "−2,455"),
                            new Metric("Demo CssGroup files",             "474 lines (5 groups stub)", "≈1,200 lines (5 groups + 80+ inline bodies)", "+~726 (bodies absorbed)"),
                            new Metric("Demo Theme files",                "49 lines (4 stubs)", "≈580 lines (Vars + Globals)", "+~531"),
                            new Metric("New DemoVars + DemoThemeRegistry","—",                  "≈170 lines",        "+170"),
                            new Metric("Total demo CSS surface",          "3,057 lines",       "2,193 lines",       "−864 (≈28% reduction)"),
                            new Metric("Files retired",                   "10 impl files",     "—",                "10 files gone"),
                            new Metric("Demo CssGroups using inline bodies", "0 of 5 active",  "5 of 5 active",     "100% migrated"),
                            new Metric("Demo themes with proper Vars + Globals singletons", "0 of 4", "4 of 4", "all 4 themes structurally complete")
                    )
            ),

            new Phase("12",
                    "Documentation + RFC status flip + dark-mode removal",
                    "Guide updated for the marker model; ES5 pitfall retired; RFC status Implemented.",
                    "Rewrote the 'Themes' section of `docs/guides/live-tracker-pattern.md` for the marker model — identity-only Theme + nested Vars + Globals singletons. Added a 'Utility composition' subsection showing `cn(st_dep, border_emphasis.hover)` shape (no parens — variants are precomputed CssClass instances). Removed all three ES5 references from the guide (pitfall #7 + reviewer-checklist item + Recipe step 5 paragraph). Flipped RFC 0002-ext1 status from Draft to Implemented (2026-05-07). Also removed the `@media (prefers-color-scheme: dark)` block from `HomingDefault.Globals.css()` (per user request — dark-mode auto-adaptation no longer working under the split cascade; can be revisited as a future opt-in theme).",
                    Status.DONE,
                    List.of(
                            new Task("Rewrite the 'Themes' section in live-tracker-pattern.md for the marker + Theme model (identity Theme + nested Vars + Globals; no per-group impls)", true),
                            new Task("Add 'Utility composition' subsection showing `cn(base, base.hover)` shape with the actual border_emphasis.hover example from Phase 08", true),
                            new Task("Remove pitfall #7 (\"The JS dialect is ES5\") — project is ES6+ throughout (D8 RESOLVED)", true),
                            new Task("Remove ES5-checklist item from Reviewer checklist", true),
                            new Task("Update Recipe Step 5 ES5 paragraph to ES6+", true),
                            new Task("Cross-link to RFC 0002-ext1 for the full design", true),
                            new Task("Update RFC 0002-ext1 status from Draft to Implemented (2026-05-07)", true),
                            new Task("Remove dark-mode `@media (prefers-color-scheme: dark)` from HomingDefault.Globals.css() (no longer working under split cascade)", true)
                    ),
                    List.of(new Dependency("11", "Documentation lands once both studio and demo migrations are complete.")),
                    "Guide serves correctly via DocReader. ES5 references gone. RFC 0002-ext1 status updated. Dark mode auto-adaptation removed.",
                    "git checkout the doc + HomingDefault edits.",
                    "30 minutes actual",
                    "Dark-mode auto-adaptation worked under the legacy single-CSS-file cascade (where /css-content emitted everything in one stylesheet). After Phase 09 split the cascade across /theme-vars + /theme-globals + /css-content, the @media override against primitives still defined in :root no longer composed cleanly. Removing it for now; can be re-introduced as a separate `HomingDark` theme record in a future change (one new file mirroring HomingDefault, registered in StudioThemeRegistry).",
                    List.of(
                            new Metric("RFC 0002-ext1 status", "Draft", "Implemented", "Phases 01–12 shipped"),
                            new Metric("Phases completed", "0 of 12", "12 of 12", "100%"),
                            new Metric("ES5 references in live-tracker-pattern.md", "3", "0", "−3"),
                            new Metric("Dark-mode `@media` block in HomingDefault.Globals", "1", "0", "−1 (deferred to future HomingDark theme)")
                    )
            ),

            new Phase("13",
                    "Retire the primitive layer in studio CSS",
                    "Collapse two-layer (primitive + semantic) → single semantic layer with concrete values per role per theme.",
                    "After Phase 12's lesson surfaced (the `--st-white` primitive doing double duty caused the dark-mode legibility regression), retired the primitive layer entirely. Each semantic role now gets a concrete value directly per theme — light values in `Vars.values()`, dark overrides in `Globals.@media`. No more primitive → semantic indirection; the `--st-*` namespace is gone from studio. Restored the dark-mode `@media (prefers-color-scheme: dark)` block (was removed in Phase 12) — now overrides semantic tokens directly, so it composes cleanly with the split cascade.",
                    Status.DONE,
                    List.of(
                            new Task("Replace `var(--st-foo)` references in `HomingDefault.Vars.values()` with concrete colour hex", true),
                            new Task("Update `HomingDefault.Globals.DARK_OVERRIDE` to override semantic tokens directly (not primitives)", true),
                            new Task("Same migration for `HomingForest`", true),
                            new Task("Same migration for `HomingSunset`", true),
                            new Task("Trim `StudioVars` — remove the 11 `--st-*` primitive CssVar constants; ALL set updated", true),
                            new Task("Verify: 0 references to `var(--st-*)` or `--st-` anywhere in homing-studio-base", true),
                            new Task("`mvn install` GREEN; all 8 modules build; all tests pass", true)
                    ),
                    List.of(new Dependency("12", "Implementation lessons from Phase 12 informed the design.")),
                    "All three studio themes have semantic-only `Vars.values()`. Visual no-regression on every studio route in light AND dark mode. Dark-mode `@media` block restored.",
                    "git revert. Three theme files affected; StudioVars affected.",
                    "1 hour actual",
                    "The dark-mode regression that triggered this work was a symptom of a deeper modelling issue: a primitive doing double duty (one source value playing two semantic roles) breaks under inversion. Removing the primitive layer eliminates the bug class entirely, not just the one instance. Demo (homing-demo) still uses the primitive pattern — DemoVars has theme-specific brand primitives that distinguish DemoDefault/Beach/Alpine/Dracula. That model is appropriate for theme variation rather than light/dark adaptation; not retired here.",
                    List.of(
                            new Metric("`StudioVars` primitive constants",       "11 (--st-*)",        "0",                    "−11 (entire primitive layer retired)"),
                            new Metric("`StudioVars.java` line count",           "100",                "76",                   "−24"),
                            new Metric("`HomingDefault.Vars.values()` entries", "37 (11 prim + 26 sem)", "26 (semantic-only)",   "−11"),
                            new Metric("References to `var(--st-*)` in studio", "≈30",                "0",                    "−30 (every theme migrated)"),
                            new Metric("Layers in studio CSS stack",            "2 (primitive + semantic)", "1 (semantic only)", "Simpler model, no double-duty risk")
                    )
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
