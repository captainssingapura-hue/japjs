package hue.captains.singapura.js.homing.studio.rfc0002;

import java.util.List;

/**
 * Implementation tracker for RFC 0002 — Typed Themes for CssGroups.
 *
 * <p>Source-of-truth for {@link Rfc0002Plan} and {@link Rfc0002Step} views.
 * Edit this file to update progress, resolve decisions, or revise phases.
 * Recompile the studio module and refresh — the new state appears live.</p>
 *
 * <p>Companion document: {@code docs/rfcs/0002-typed-themes-for-cssgroups.md}.</p>
 */
public final class Rfc0002Steps {

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
            String chosenValue,        // null when OPEN; the chosen option when RESOLVED
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

    public static final String RFC_DOC = "rfcs/0002-typed-themes-for-cssgroups.md";

    // ------------------------------------------------------------------
    // OPEN DECISIONS — resolve before/during execution.
    // ------------------------------------------------------------------
    public static final List<Decision> DECISIONS = List.of(

            new Decision("D1",
                    "Where do `HomingDefault` and `StudioStylesHomingDefault` live?",
                    "homing-studio (Homing-specific concretizations of a studio-base contract).",
                    null,
                    DecisionStatus.OPEN,
                    "Today StudioStyles is in homing-studio. Moving the contract (StudioStyles + Impl<TH> nested interface) to homing-studio-base is consistent with the broader extraction goals — but that's a separate RFC. For now the impl stays where StudioStyles is.",
                    ""
            ),

            new Decision("D2",
                    "Should the existing string-based theme parameter mechanism be retained as a fallback?",
                    "No. Hard cut.",
                    null,
                    DecisionStatus.OPEN,
                    "The existing themed `.css` file mechanism is undocumented and unused in any current deployment. Cleaner to start typed.",
                    ""
            ),

            new Decision("D3",
                    "Should `Theme` extend `Importable` so themes can be referenced via `nav.*` typed nav?",
                    "Not in this RFC.",
                    null,
                    DecisionStatus.OPEN,
                    "Themes are a server-config concern. JS-side theme switching is out of scope. If a future RFC wants users to navigate to 'the same page in dark mode' via a typed link, that RFC can extend Theme then.",
                    ""
            ),

            new Decision("D4",
                    "Per-element themes (different parts of one app rendered with different themes)?",
                    "Out of scope.",
                    null,
                    DecisionStatus.OPEN,
                    "All consumers using this so far have one theme per page load. Per-element theming requires per-element CSS scoping (Shadow DOM, scoped attributes) that's a much larger undertaking.",
                    ""
            )
    );

    // ------------------------------------------------------------------
    // PHASES — execute in order. Each has a verification gate.
    // ------------------------------------------------------------------
    public static final List<Phase> PHASES = List.of(

            new Phase("01",
                    "Generic types in homing-core",
                    "Add `Theme` and `CssGroupImpl<CG, TH>` interfaces.",
                    "Two small interfaces in homing-core. Theme is plain (no F-bound, no Exportable). CssGroupImpl keeps `<CG, TH>` generics with `TH extends Theme` (not recursive).",
                    Status.DONE,
                    List.of(
                            new Task("Add `Theme` interface to homing-core (slug, label methods)", true),
                            new Task("Add `CssGroupImpl<CG extends CssGroup<CG>, TH extends Theme>` interface with group(), theme(), default cssVariables()", true),
                            new Task("Compile homing-core green; full reactor green (no behaviour regressions; no consumers yet)", true)
                    ),
                    List.of(),
                    "Both new interfaces exist in homing-core; `mvn -pl homing-core install` green.",
                    "Delete the two interface files; trivial undo since nothing depends on them yet.",
                    "20 minutes",
                    ""
            ),

            new Phase("02",
                    "StudioStyles.Impl<TH> nested interface",
                    "Add the nested interface to StudioStyles with one abstract method per declared CssClass.",
                    "Mechanical: walk every CssClass record in StudioStyles, add a corresponding abstract method to a new nested interface `Impl<TH extends Theme> extends CssGroupImpl<StudioStyles, TH>`. 81 method signatures (revised from the RFC's ~90 estimate after counting). The Impl interface is the compile-time gate: any concrete impl that doesn't override every method is a compile error. Methods return a typed `CssBlock` (not raw String) to document intent and reserve room for future composition / validation helpers.",
                    Status.DONE,
                    List.of(
                            new Task("Audit StudioStyles cssClasses() to enumerate all class records — 81 found", true),
                            new Task("Write `Impl<TH extends Theme> extends CssGroupImpl<StudioStyles, TH>` nested interface", true),
                            new Task("Add a default `group()` returning StudioStyles.INSTANCE", true),
                            new Task("Add one abstract `CssBlock<recordName> recordName()` method per CssClass record (81 total, grouped by section comment) — class-parameterized witness type catches mismatched bodies at compile time", true),
                            new Task("Add `CssBlock<CC extends CssClass<?>>` typed wrapper to homing-core (record + of/empty factories) — refinement after Phase 02 first land, tightens the body type to its specific class", true),
                            new Task("Compile homing-studio green; full reactor green; no concrete impl exists yet (compile-time gate ready)", true)
                    ),
                    List.of(new Dependency("01", "Needs Theme + CssGroupImpl interfaces from homing-core")),
                    "StudioStyles.Impl<TH> nested interface exists with one abstract method per class. javadoc cross-references each.",
                    "Delete the nested interface; StudioStyles reverts to its current shape.",
                    "30 minutes",
                    ""
            ),

            new Phase("03",
                    "HomingDefault theme + StudioStylesHomingDefault impl in studio-base",
                    "Translate every rule from StudioStyles.css into the corresponding Java method body. Default theme moves to homing-studio-base so client apps get a working visual identity out of the box without designing one.",
                    "The bulk of the work. Read each existing CSS rule, copy the body into the matching method in StudioStylesHomingDefault. Translate :root custom properties into the cssVariables() map. Pseudo-classes, descendant selectors, media queries, and html/body resets go into a new globalRules() default method on the Impl interface. Side effect: StudioStyles itself moves from homing-studio to homing-studio-base (the contract belongs with the default impl).",
                    Status.DONE,
                    List.of(
                            new Task("Move StudioStyles.java from homing-studio.studio.css to homing-studio-base.studio.base.css (10 importers updated)", true),
                            new Task("Move StudioStyles.css resource to the matching new canonical path", true),
                            new Task("Add `globalRules()` default method to StudioStyles.Impl<TH> for non-class-keyed CSS", true),
                            new Task("Create HomingDefault theme record in studio-base (slug = 'homing-default')", true),
                            new Task("Create StudioStylesHomingDefault implementing StudioStyles.Impl<HomingDefault>", true),
                            new Task("Translate :root custom properties into cssVariables() (LinkedHashMap, 11 entries, order preserved)", true),
                            new Task("Translate pseudo-classes, descendants, media queries, html/body resets into globalRules()", true),
                            new Task("Translate every per-class CSS rule into its method body (81 total, Java text blocks)", true),
                            new Task("Compile homing-studio green — compile error if any method missing (gate held: 0 errors)", true)
                    ),
                    List.of(new Dependency("02", "Needs the Impl<TH> contract")),
                    "StudioStylesHomingDefault compiles with all ~90 methods implemented. Visual diff from a known styled page renders identically.",
                    "Delete HomingDefault + StudioStylesHomingDefault; StudioStyles.css is still around as a fallback (until phase 05). Visual UX rolls back to the file-based path.",
                    "90 minutes",
                    ""
            ),

            new Phase("04",
                    "Server refactor — registry-based resolution",
                    "Refactor CssContentGetAction to render from typed CssGroupImpls.",
                    "CssContentGetAction now consults a List<CssGroupImpl<?,?>> + default theme passed at construction. Looks up impl by (group canonical class, theme.slug). Renders ':root { vars }' + globalRules() + per-class '.kebab-name { body }' blocks via reflection on the impl's record-named methods. Hard-cut: unknown theme returns 404, no silent file fallback. Studio overrides the inner /css-content route with the typed-impl-aware action; downstream apps get the same path by depending on studio-base + including its registry. globalRules() moved up from StudioStyles.Impl to the CssGroupImpl base interface (more general, reflection-friendly).",
                    Status.DONE,
                    List.of(
                            new Task("Move globalRules() from StudioStyles.Impl<TH> up to CssGroupImpl base interface", true),
                            new Task("Create CssGroupImplRegistry in studio-base with HomingDefault impl registered", true),
                            new Task("Refactor CssContentGetAction.execute() to resolve via registry — typed path takes priority, file-based stays as legacy back-compat for tests", true),
                            new Task("Render CSS body: ':root { vars }' + globalRules() + per-class '.kebab-name { body }' blocks (kebab-case via existing CssClassName.toCssName)", true),
                            new Task("StudioActionRegistry constructs the typed CssContentGetAction(CssGroupImplRegistry.ALL, HomingDefault.INSTANCE) and overrides the inner /css-content route", true),
                            new Task("Hard-cut on missing theme: unknown ?theme= returns 404 (verified — no silent fallback)", true),
                            new Task("Visual smoke: studio routes (catalogue, journeys, rename, rfc0001, rfc0002, doc-browser) render identically; rendered CSS is 17 KB of valid styles", true)
                    ),
                    List.of(new Dependency("03", "Needs the impl record to be present in the registry")),
                    "`mvn install` green. Studio routes (catalogue, rename-plan, rfc0001-plan, doc-browser) render with correct styling. Browser DevTools network tab shows /css?class=...&theme=homing-default returning 200.",
                    "Revert the action refactor; restore file-based path resolution. Impl + registry can stay as dead code temporarily.",
                    "30 minutes",
                    ""
            ),

            new Phase("05",
                    "Hard cut — delete StudioStyles.css and remove file-based fallback",
                    "Eliminate the legacy resource file and the file-based code path entirely.",
                    "The typed impl is now the sole CSS source. CssContentGetAction no longer carries a ResourceReader or any file-based path. The base HomingActionRegistry serves a no-op CssContentGetAction (empty impls, no default theme) — every /css-content request 404s unless an outer registry (e.g. StudioActionRegistry) overrides it with a typed-impl-aware action. Demo CSS is consciously broken pending follow-up migration of its 7 CssGroups to typed impls.",
                    Status.DONE,
                    List.of(
                            new Task("Delete homing-studio-base/src/main/resources/homing/css/.../StudioStyles.css", true),
                            new Task("Strip ResourceReader field and all file-based code paths out of CssContentGetAction; constructor is now (List<CssGroupImpl<?,?>>, Theme) only", true),
                            new Task("Update HomingActionRegistry: serve a typed-only CssContentGetAction(List.of(), null) — base registry 404s, outer registries override", true),
                            new Task("StudioActionRegistry continues to override /css-content with the typed-impl-aware action (no change needed)", true),
                            new Task("`mvn install` green across all 8 modules", true),
                            new Task("Smoke: GET /css-content?class=hue.captains.singapura.js.homing.studio.base.css.StudioStyles → 200, ~17 KB rendered CSS", true),
                            new Task("FOLLOW-UP (out of RFC scope): demo's 7 CssGroups (AliceStyles, BaseStyles, CatalogueStyles, PitchDeckStyles, PlaygroundStyles, SpinningStyles, SubwayStyles) need typed impls — currently 404", false)
                    ),
                    List.of(new Dependency("04", "Needs the registry-backed resolution working")),
                    "StudioStyles.css gone. CssContentGetAction has no ResourceReader. Build green. Studio /css-content returns 200 with ~17KB of typed-rendered CSS. Demo CSS routes 404 as designed (hard cut).",
                    "git revert. Restore StudioStyles.css and the file-based code path in CssContentGetAction.",
                    "20 minutes",
                    ""
            ),

            new Phase("06",
                    "Conformance test",
                    "Add a build-time gate that walks the registry and checks consistency.",
                    "New conformance test in homing-conformance: walks the deployment's CssGroupImpl registry, asserts every impl has non-null group()/theme(), no duplicate (group, theme.slug) pairs, and every reachable non-empty CssGroup has at least one impl under the default theme slug. Concrete subclasses in homing-studio AND homing-demo wire it to their respective registries.",
                    Status.DONE,
                    List.of(
                            new Task("Add CssGroupImplConsistencyTest base class in homing-conformance", true),
                            new Task("Add concrete StudioCssGroupImplConsistencyTest in homing-studio", true),
                            new Task("Add concrete DemoCssGroupImplConsistencyTest in homing-demo", true),
                            new Task("Test: every impl's group() and theme() are non-null", true),
                            new Task("Test: no duplicate (group, theme.slug) pairs in registry", true),
                            new Task("Test: every reachable non-empty CssGroup has an impl under the default theme slug (transitive cssImports walk)", true),
                            new Task("Run mvn install — new tests pass (3 dynamic checks each, both modules green)", true)
                    ),
                    List.of(new Dependency("05", "Needs the registry to be the canonical resolution path")),
                    "New conformance tests run and pass. mvn install green across all 8 modules.",
                    "Delete the test classes. No production behavior changes.",
                    "30 minutes",
                    ""
            ),

            new Phase("07",
                    "Documentation",
                    "Update the live-tracker-pattern guide with a 'Themes' section.",
                    "Brief addition to the existing guide: how to declare a theme, how to provide a CssGroupImpl, how to register. Link the RFC for the full design.",
                    Status.DONE,
                    List.of(
                            new Task("Add 'Themes' subsection to docs/guides/live-tracker-pattern.md (placed before 'Workflow once it's wired')", true),
                            new Task("Show one-record Theme + one impl record example (HomingDark dark-mode walkthrough)", true),
                            new Task("Cross-link to RFC 0002 for the full design", true),
                            new Task("Update RFC 0002 status from Draft to Implemented (Phases 01–07 landed 2026-05-07)", true)
                    ),
                    List.of(new Dependency("06", "Needs the conformance gate for the implementation to be considered complete")),
                    "Guide serves correctly via DocReader. RFC 0002 status updated.",
                    "git checkout the doc edits.",
                    "20 minutes",
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
            doneTasks  += p.tasks().stream().filter(Task::done).count();
            totalTasks += p.tasks().size();
        }
        return totalTasks == 0 ? 0 : (doneTasks * 100 / totalTasks);
    }

    public static int openDecisionsCount() {
        return (int) DECISIONS.stream().filter(d -> d.status() == DecisionStatus.OPEN).count();
    }

    private Rfc0002Steps() {}
}
