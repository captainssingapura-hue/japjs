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
            String notes
    ) {
        public int progressPercent() {
            if (tasks.isEmpty()) return 0;
            long done = tasks.stream().filter(Task::done).count();
            return (int) (done * 100 / tasks.size());
        }
    }

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
                    "Should every base utility get every variant method on the JS side, or only the registered ones?",
                    "Only registered. Module emitter walks the impl registry and emits `.hover()` / `.focus()` / etc. only for variants that actually exist for that base.",
                    null,
                    DecisionStatus.OPEN,
                    "Saves bytes; an unregistered call (`base.hover()`) is `undefined()` at runtime and conformance-test failure at build. Consistent with the framework's fail-loud-at-build-time doctrine.",
                    ""
            ),

            new Decision("D8",
                    "JS dialect for emitted module code: ES5 or ES6+?",
                    "ES6+ throughout the project.",
                    "ES6+",
                    DecisionStatus.RESOLVED,
                    "The framework's loading mechanism is ES6 modules (the `import` statement is required for it to work at all). Every browser that runs the code already supports ES6 classes, `const`, arrow functions, template literals, etc. The earlier ES5 stylistic convention is retired. Update the live-tracker-pattern guide to remove the ES5 pitfall note.",
                    "User decision 2026-05-07."
            )
    );

    // ------------------------------------------------------------------
    // PHASES — execute in order. Each has a verification gate.
    // ------------------------------------------------------------------
    public static final List<Phase> PHASES = List.of(

            new Phase("01",
                    "Framework: `pseudoState()` + `VariantOf<B>` family",
                    "Add the optional pseudo-class mechanism + variant-of-base type system. Non-breaking.",
                    "Three additions to homing-core: (1) `default String pseudoState() { return null; }` on `CssClass`; (2) a `VariantOf<B extends CssClass<?>> extends CssClass<?>` interface declaring `Class<B> base()` and the inherited `pseudoState()`; (3) three convenience parents `HoverVariantOf<B>`, `FocusVariantOf<B>`, `ActiveVariantOf<B>` that pin the pseudo-state. Renderer dispatches: when a record implements `VariantOf<B>`, it reuses the base utility's `CssBlock` body under the variant's pseudo-state selector. Pure declarations — variant records carry no method on the impl. Existing records render byte-identically.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Add `pseudoState()` default method to `CssClass`", false),
                            new Task("Add `VariantOf<B>` interface to homing-core (`base()`, inherited `pseudoState()`)", false),
                            new Task("Add `HoverVariantOf<B>`, `FocusVariantOf<B>`, `ActiveVariantOf<B>` convenience parents", false),
                            new Task("Update `CssContentGetAction.renderCss()` with two-branch dispatch: bare/pseudo-state vs VariantOf-base-body-reuse", false),
                            new Task("Add `CssContentGetActionTest` case: bare CssClass with `pseudoState() = \":hover\"` renders `.foo:hover { … }`", false),
                            new Task("Add `CssContentGetActionTest` case: `HoverVariantOf<bg_accent>` record (no impl method) renders `.hover-bg-accent:hover { <bg_accent body> }`", false),
                            new Task("Verify all existing tests pass — every existing record still renders bare class with its impl method's body", false)
                    ),
                    List.of(),
                    "`mvn install` green. New unit tests pass. Existing rendered output unchanged.",
                    "Revert the framework changes. No downstream coupling.",
                    "1.5 hours",
                    ""
            ),

            new Phase("02",
                    "`semanticTokens()` on CssGroupImpl",
                    "Add the second token-layer method. Non-breaking.",
                    "Add a `default Map<String, String> semanticTokens() { return Map.of(); }` method to `CssGroupImpl`. Update `CssContentGetAction.renderCss()` to emit semantic tokens after primitive ones inside the same `:root { ... }` block. Order matters: semantic refs primitive, so primitives must come first.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Add `semanticTokens()` default method to `CssGroupImpl`", false),
                            new Task("Update `CssContentGetAction.renderCss()` to emit primitives then semantic tokens, both inside `:root {}`", false),
                            new Task("Add unit test asserting emission order (primitive → semantic)", false),
                            new Task("All existing impls compile unchanged (default returns empty map)", false)
                    ),
                    List.of(new Dependency("01", "Independent, but lands as the second half of the framework prep.")),
                    "`mvn install` green. Test asserts: when both maps are non-empty, output has primitives first, then semantic tokens, all under one `:root {}`.",
                    "Revert.",
                    "30 minutes",
                    ""
            ),

            new Phase("03",
                    "Framework: ES6 module emission with `CssClass` / `CssUtility`",
                    "Replace closure-based JS exposition with ES6 classes; auto-discover registered variants per imported base.",
                    "Update the module emitter (where AppModule `imports()` are turned into JS module text) to: (1) ship the framework runtime `CssClass` / `CssUtility` classes once per module bootstrap; (2) emit `new CssClass(\"kebab-name\")` for plain CssClass records, `new CssUtility(\"kebab\", { hover: \"hover-kebab\", focus: \"focus-kebab\" })` for bases that have registered `VariantOf<B>` records in the deployment's `CssGroupImplRegistry`; (3) drop ES5 IIFE/closure shapes — ES6 classes/`const`/arrow functions are the project dialect (D8 RESOLVED). Variant records themselves are NOT emitted as separate JS identifiers — they're reachable only through `<base>.<state>()`. Per D7 only registered variants get methods.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Add `CssClass` / `CssUtility` ES6 classes to the framework runtime bootstrap (homing-server)", false),
                            new Task("Update `EsModuleGetAction` (or wherever module text is generated) to emit `new CssClass(...)` / `new CssUtility(...)` per import", false),
                            new Task("Discover registered variants per base by walking the deployment's `CssGroupImplRegistry`; expose only registered ones as methods", false),
                            new Task("Update `cn()` / `css.*` JS helpers to handle objects via `toString()` coercion (existing string handling unchanged)", false),
                            new Task("Add unit test on emitted module text: contains `class CssClass`, no IIFE, variants present only when registered", false),
                            new Task("Verify all existing studio + demo modules still load and render — string coercion makes the change invisible to existing call sites", false)
                    ),
                    List.of(new Dependency("01", "Variants need `VariantOf<B>` to register; emitter discovers them by walking the registry.")),
                    "`mvn install` green. Emitted JS module text uses ES6 classes. Existing `cn(record1, record2)` call sites unchanged. New `cn(base, base.hover())` call sites work.",
                    "Revert the emitter changes; the framework runtime classes can stay (unused).",
                    "2 hours",
                    ""
            ),

            new Phase("04",
                    "Studio semantic vocabulary",
                    "Define the starter semantic-token names in `StudioStylesHomingDefault.semanticTokens()`.",
                    "Add ~15 colour roles (surface, text, border, accent) and a small spacing/radius scale. Each maps to an existing primitive `var(--st-*)`. No component bodies change yet — this is the new layer's introduction.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Add `semanticTokens()` to `StudioStylesHomingDefault` with surface/text/border/accent roles", false),
                            new Task("Add spacing scale: `--space-1` through `--space-8`", false),
                            new Task("Add radius scale: `--radius-sm`, `--radius-md`, `--radius-lg`", false),
                            new Task("Verify rendered `/css-content?class=...StudioStyles` includes both primitives AND semantic tokens", false),
                            new Task("Verify dark-mode @media block in `globalRules()` still resolves correctly through the semantic layer", false)
                    ),
                    List.of(new Dependency("02", "Needs `semanticTokens()` to exist on the impl interface.")),
                    "Network tab shows `/css-content?class=…StudioStyles` containing every `--color-*`, `--space-*`, `--radius-*` defined in §3.2.1 of the RFC.",
                    "Delete the new method. Component bodies still reference primitives, so no visual regression.",
                    "30 minutes",
                    ""
            ),

            new Phase("05",
                    "`Util` CssGroup + impl",
                    "Add a small hand-curated utility group in homing-studio-base.",
                    "Create `Util` CssGroup with ~30 records: a small set of base utilities (background, color, border, transform, shadow) + their hover/focus VariantOf declarations + core layout helpers (spacing, flex/grid, gap). Create `UtilHomingDefault` impl with bodies for the BASES only (variants delegate). Register in `CssGroupImplRegistry`. Conformance test must pass.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Create `Util` CssGroup record + nested `Impl<TH extends Theme>` interface in homing-studio-base", false),
                            new Task("Define ~8 base color/visual records: `bg_accent`, `bg_accent_emphasis`, `bg_surface_raised`, `color_link`, `color_link_emphasis`, `border_emphasis`, `translate_y_neg_2`, `shadow_lg`", false),
                            new Task("For each base that needs them: add `HoverVariantOf<...>` and/or `FocusVariantOf<...>` declarations (no body — base body reused)", false),
                            new Task("Define ~15–20 layout records (`p_*`, `m_*`, `gap_*`, `flex`, `grid`, `inline_flex`, `items_center`, `justify_between`, `text_center`)", false),
                            new Task("Create `UtilHomingDefault` impl with method bodies for BASE utilities only; variants resolve via the renderer", false),
                            new Task("Register `UtilHomingDefault.INSTANCE` in studio-base's `CssGroupImplRegistry.ALL`", false),
                            new Task("`StudioCssGroupImplConsistencyTest` still passes (Util reachable, no duplicates, every base has impl method)", false),
                            new Task("Smoke: `/css-content?class=…Util` returns 200 with rendered utility CSS, including `.hover-bg-accent:hover { … }` rules with body inherited from `.bg-accent`", false)
                    ),
                    List.of(
                            new Dependency("01", "Hover variants need `HoverVariantOf<B>`."),
                            new Dependency("04", "Utility bodies reference semantic tokens, which must exist in the cascade.")
                    ),
                    "All tests green. New `Util` group resolves and renders. Variant records emit `.<kebab>:hover { <base body> }` rules without per-variant impl methods.",
                    "Delete the new files. Rest of system unaffected.",
                    "1.5 hours",
                    ""
            ),

            new Phase("06",
                    "Refactor StudioStyles bodies to semantic tokens",
                    "Component bodies in `StudioStylesHomingDefault` switch from primitive `var(--st-*)` to semantic `var(--color-*)`. Drop hover rules from `globalRules()` that are now expressible as utilities.",
                    "Mechanical edit: replace primitive var refs with semantic ones in every `CssBlock<>` body. Delete single-property `:hover`/`:focus` rules from `globalRules()` that have a corresponding `Util` base + variant. Multi-property hover rules stay until call sites are migrated in Phase 07.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Replace `var(--st-amber)` etc. with `var(--color-accent)` etc. in every component body", false),
                            new Task("Identify single-property hover/focus rules in `globalRules()` that match a Util base + variant; delete them", false),
                            new Task("Verify visual no-regression on every studio page (catalogue, journeys, doc-browser, doc-reader, RFC plan/step views, rename plan/step views, RFC 0002-ext1 plan/step views)", false),
                            new Task("Dark-mode visual no-regression (DevTools → emulate `prefers-color-scheme: dark`)", false)
                    ),
                    List.of(new Dependency("05", "Util base utilities + variants must exist before deleting their globalRules counterparts.")),
                    "Visual smoke shows no regression on any studio route, in either light or dark mode. `mvn install` green.",
                    "Revert StudioStylesHomingDefault to the prior commit. Util records can stay registered (they just go unused).",
                    "2 hours",
                    ""
            ),

            new Phase("07",
                    "Migrate studio JS modules to use base + variant methods",
                    "Update studio AppModules to import Util base utilities and use the `base.hover()` shape at call sites.",
                    "For each studio AppModule that uses a class with hover behavior, add the relevant `Util` BASE records to `imports()` (variants come along automatically per Phase 03). Update JS views from `cn(st_crumb)` to `cn(st_crumb, color_link.hover())` shape — Tailwind-like co-location of base + state.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Identify studio AppModules with hover-bearing classes (StudioCatalogue, JourneysCatalogue, doc browser/reader, RFC plan/step views, rename views, RFC 0002-ext1 plan/step views)", false),
                            new Task("Add Util base records to each AppModule's `imports()` (NOT the variants — they come through the base on the JS side)", false),
                            new Task("Update `cn(...)` call sites: `cn(st_crumb)` → `cn(st_crumb, color_link.hover())` style", false),
                            new Task("`StudioCssConformanceTest` passes (every Util base used in JS is in the module's imports)", false),
                            new Task("Visual smoke per module: hover behavior preserved", false)
                    ),
                    List.of(
                            new Dependency("03", "Variant methods on JS-side base objects must be emitted."),
                            new Dependency("06", "Bodies must reference semantic tokens before utilities can take over.")
                    ),
                    "All studio routes pass conformance + visual smoke. Hover behavior visible in DevTools as `.hover-<base>:hover` rules taking effect.",
                    "Revert per-AppModule. Atomic commits.",
                    "2 hours",
                    ""
            ),

            new Phase("08",
                    "Documentation",
                    "Update guide; flip RFC status; retire ES5 pitfall note.",
                    "Add a 'Utility composition' subsection to docs/guides/live-tracker-pattern.md showing the `cn(base, base.hover())` shape. Remove the existing ES5 pitfall (#7) since the project now uses ES6+ throughout. Update RFC 0002-ext1 status to Implemented (with date).",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Add 'Utility composition' subsection to live-tracker-pattern.md (after the existing 'Themes' section)", false),
                            new Task("Show one base utility + one variant call (`cn(bg_accent, bg_accent.hover())`) plus one layout-utility example", false),
                            new Task("Remove pitfall #7 (\"The JS dialect is ES5\") — project is ES6+ throughout (D8 RESOLVED)", false),
                            new Task("Cross-link to RFC 0002-ext1 for the full design", false),
                            new Task("Update RFC 0002-ext1 status from Draft to Implemented (with date)", false)
                    ),
                    List.of(new Dependency("07", "Documentation lands once the implementation is fully shipped.")),
                    "Guide serves correctly via DocReader. ES5 pitfall is gone. RFC 0002-ext1 status updated.",
                    "git checkout the doc edits.",
                    "30 minutes",
                    ""
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
