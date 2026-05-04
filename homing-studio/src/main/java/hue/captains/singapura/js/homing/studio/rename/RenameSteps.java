package hue.captains.singapura.js.homing.studio.rename;

import java.util.List;

/**
 * Implementation tracker for the japjs → Homing rename.
 *
 * <p>Source-of-truth for {@link RenamePlan} and {@link RenameStep} views.
 * Edit this file to update progress, resolve decisions, or revise phases.
 * Recompile the studio module and refresh — the new state appears live.</p>
 *
 * <p>Companion document: {@code docs/rename/EXECUTION-PLAN.md} (the prose
 * version of this same plan, with full rationale per phase).</p>
 */
public final class RenameSteps {

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

    public static final String OLD_NAME           = "japjs";
    public static final String NEW_NAME           = "Homing";
    public static final String NEW_PROJECT_NAME    = "homing.js";                      // D1 — root project name + groupId
    public static final String NEW_ARTIFACT_PREFIX = "homing";                         // child artifactIds: homing-core, homing-server, …
    public static final String OLD_PKG_ROOT       = "hue.captains.singapura.japjs";
    public static final String NEW_PKG_ROOT       = "hue.captains.singapura.js.homing"; // D2
    public static final String EXECUTION_DOC      = "rename/EXECUTION-PLAN.md";
    public static final String DOSSIER_DOC        = "brand/RENAME-TO-HOMING.md";

    // ------------------------------------------------------------------
    // RESOLVED DECISIONS — locked before executing Phase 1.
    // ------------------------------------------------------------------
    public static final List<Decision> DECISIONS = List.of(

            new Decision("D1",
                    "Project name suffix — `Homing` or `homing.js`?",
                    "Plain `Homing` (no .js suffix). Java is source of truth; the .js suffix tells the JavaScript-first story (React.js, Vue.js, Next.js).",
                    "homing.js",
                    DecisionStatus.RESOLVED,
                    "User chose `homing.js` to follow JS-framework naming convention. Project name + groupId carry the `.js` suffix; child artifactIds + module dirs use the cleaner `homing-X` form (the `.js` lives in the groupId already).",
                    "Brand wordmark in prose: `Homing` (capital H). Project name: `homing.js`. Module dirs / artifactIds: `homing-core`, `homing-server`, `homing-conformance`, `homing-demo`, `homing-studio`."
            ),

            new Decision("D2",
                    "Java package root — `hue.captains.singapura.homing` or `hue.captains.singapura.js.homing`?",
                    "Keep author-namespace prefix; only the trailing segment changes.",
                    "hue.captains.singapura.js.homing",
                    DecisionStatus.RESOLVED,
                    "User chose to insert a `.js` segment for consistency with D1. Final root: `hue.captains.singapura.js.homing.{core,server,conformance,demo,studio}`.",
                    "Adds one segment depth across every Java file. IDE Refactor → Rename Package handles imports automatically."
            ),

            new Decision("D3",
                    "Brand logo redesign — execute inside the rename, or defer?",
                    "Defer the lowercase-h-with-arch redesign as a separate task. Phase 4 only does the wordmark text replacement.",
                    "defer",
                    DecisionStatus.RESOLVED,
                    "User: code is more important at this stage. Logo redesign happens later as its own task with proper iteration. Keeps Phase 4 small.",
                    "Phase 4 still updates the wordmark text in 8 brand SVGs (`japjs` → `Homing`); the j-shaped mark stays with a known-issue note."
            ),

            new Decision("D4",
                    "Commit granularity — single commit or commit-per-phase?",
                    "Commit-per-phase. Granular commits give clean rollback boundaries.",
                    "manual-commits-with-pause",
                    DecisionStatus.RESOLVED,
                    "User will commit manually. Plan PAUSES at each phase verification gate so user can inspect, commit, and resume.",
                    "Each phase ends with: Claude reports verification status → waits for explicit go-ahead before starting next phase. No automated commits."
            )
    );

    // ------------------------------------------------------------------
    // PHASES — execute in order. Each has a verification gate.
    // ------------------------------------------------------------------
    public static final List<Phase> PHASES = List.of(

            new Phase("01",
                    "Snapshot",
                    "Tag the current state and create a working branch.",
                    "Pure safety — establish a clean point of return. After this phase any subsequent failure can be rolled back with `git checkout pre-rename-japjs`.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Working tree is clean (`git status` shows no changes)", false),
                            new Task("Tag current HEAD as `pre-rename-japjs`", false),
                            new Task("Create and checkout branch `rename/japjs-to-homing`", false)
                    ),
                    List.of(),
                    "`git tag | grep pre-rename` returns the tag. `git status` shows clean tree on the new branch.",
                    "N/A — this phase IS the rollback point.",
                    "15 minutes",
                    ""
            ),

            new Phase("02",
                    "Java side",
                    "Maven artifactIds, directory renames, package refactor, class renames, system properties.",
                    "The biggest mechanical edit. Affects ~140 Java files. Five Maven modules rename. Per D1: artifactIds become `homing-*`. Per D2: package root becomes `hue.captains.singapura.js.homing`. NB: 9 demo integration tests in `EsModuleGetActionTest` will fail at the Phase 2 boundary because they load JS resources whose paths are still on the old layout — Phase 3 fixes them.",
                    Status.IN_PROGRESS,
                    List.of(
                            new Task("Update root pom.xml: artifactId, modules, groupId (also flagged groupId change for review)", true),
                            new Task("Update each child pom.xml: artifactId, parent reference, inter-module dependency groupId+artifactId", true),
                            new Task("git mv 5 module dirs (japjs-X to homing-X)", true),
                            new Task("git mv Java package dirs (.../japjs to .../js/homing) across main + test sources", true),
                            new Task("Bulk sed across 140 .java files: package decls, imports, fully-qualified string literals", true),
                            new Task("Class rename: JapjsActionRegistry to HomingActionRegistry (file + all references)", true),
                            new Task("Find/replace system properties: japjs.devRoot to homing.devRoot, japjs.studio.docsRoot to homing.studio.docsRoot", true),
                            new Task("Verify no Java-side */japjs/* dirs remain (excluding target + resources/japjs)", true),
                            new Task("Tighten 2 unit tests (.js substring) since package now contains '.js.homing.'", true),
                            new Task("mvn install green: all 5 modules build, 14 conformance tests pass; 9 EsModuleGetActionTest errors are expected (Phase 3 fix)", true),
                            new Task("PAUSE — report status, wait for user to commit and confirm before Phase 3", false)
                    ),
                    List.of(new Dependency("01", "Snapshot must exist for safe rollback")),
                    "Java-side `find . -type d -name 'japjs' | grep -v target | grep -v resources/japjs` returns empty. Five `homing-*` module dirs exist. `mvn install -Dtest='!EsModuleGetActionTest'` is fully green; the 9 expected resource-path failures will clear in Phase 3.",
                    "`git reset --hard HEAD` (no commit yet; Phase 2 is fully reversible).",
                    "2 hours",
                    ""
            ),

            new Phase("03",
                    "Resource paths",
                    "Rename `japjs/...` resource subtrees to `homing/...`; update path constants in Java.",
                    "Server resources live under `src/main/resources/japjs/{js,css,svg}/...`. The path is convention-bound — both filesystem and Java code must agree. Phase 3 changes both atomically. Note: filesystem dir is `homing/` (not `homing.js/`) — convention root only.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("`git mv` each `homing-*/src/main/resources/japjs` to `homing` (5 modules)", false),
                            new Task("Recursive rename of nested package-mirror dirs `.../japjs/...` → `.../js/homing/...` under each `resources/homing/{js,css,svg}` tree (matches new package root)", false),
                            new Task("Update path constants in `ResourceReader`, `CssConformanceTest`, `HrefConformanceTest`", false),
                            new Task("Update path constants in `EsModuleGetAction`, `CssContentGetAction`, `SvgGroupContentProvider`, `CssGroupContentProvider`", false),
                            new Task("Verify `homing.devRoot` resolution works for live-reload", false),
                            new Task("`mvn clean install` green; demo + studio servers boot and serve a page", false),
                            new Task("PAUSE — report status, wait for user to commit and confirm before Phase 4", false)
                    ),
                    List.of(new Dependency("02", "Java packages must already be renamed for resource path constants to make sense")),
                    "Both servers boot. `curl /app?app=demo-catalogue` and `curl /app?app=studio-catalogue` return HTTP 200.",
                    "Revert the Phase 3 commit (path renames are a single logical change).",
                    "1 hour",
                    ""
            ),

            new Phase("04",
                    "JS / CSS / SVG content sweep",
                    "Replace `japjs` / `Japjs` strings inside JS, CSS, SVG resource files; brand wordmark update.",
                    "Cosmetic and content edits — not paths or class references (those landed in Phases 2–3). Comments, header markers, brand strings, generated-comment text. Plus the brand mark wordmark.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("JS files: replace `japjs` (case-sensitive) in comments, string literals, header markers", false),
                            new Task("Update generated-marker strings in `NavWriter` and `ParamsWriter` Java", false),
                            new Task("CSS file headers: replace `japjs` in `/* … */` header blocks (cosmetic)", false),
                            new Task("Brand SVG asset titles: `japjs` → `Homing` in `<title>` and visible text", false),
                            new Task("Brand wordmark in 8 logo SVGs: `japjs` → `Homing`", false),
                            new Task("Brand mark redesign — DEFERRED per Decision D3 (separate task, post-rename)", false),
                            new Task("`grep -ri 'japjs' homing-*/src` returns only intentional matches", false),
                            new Task("PAUSE — report status, wait for user to commit and confirm before Phase 5", false)
                    ),
                    List.of(new Dependency("03", "Resource paths must already point to homing/")),
                    "Visual smoke: open every demo and studio app in browser; brand reads `Homing` everywhere; no stray `japjs` strings except in deliberate references (this plan, the dossier).",
                    "Revert the Phase 4 commit. Cosmetic-only — no functional impact if reverted.",
                    "1 hour (logo deferred per D3)",
                    ""
            ),

            new Phase("05",
                    "Documentation sweep",
                    "Replace `japjs` across all markdown docs; rename doc files; update DocRegistry.",
                    "Substantial but mechanical text replacement across ~18 markdown files. Tricky part: `japjs` (lowercase, code-y contexts) vs `Homing` (capitalized, prose contexts). Two-pass replacement strategy recommended.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Two-pass replace: prose `japjs` → `Homing`, code-y `japjs` → `homing`", false),
                            new Task("Rename `docs/whitepaper/japjs-whitepaper.md` → `homing-whitepaper.md`", false),
                            new Task("Rename `docs/whitepaper/japjs-shell-flexibility-whitepaper.md` → `homing-shell-flexibility-whitepaper.md`", false),
                            new Task("Rename `docs/comparison/japjs-vs-react-vue.md` → `homing-vs-react-vue.md`", false),
                            new Task("Update all cross-references in markdown to renamed files", false),
                            new Task("Update `homing-studio/.../DocRegistry.java` doc paths", false),
                            new Task("Visual check: studio's DocBrowser lists every doc; each card opens correctly", false),
                            new Task("PAUSE — report status, wait for user to commit and confirm before Phase 6", false)
                    ),
                    List.of(new Dependency("04", "Brand identity should already read Homing in JS/CSS/SVG")),
                    "Studio's DocBrowser opens correctly. Every doc card resolves. RFC 0001 plan still renders.",
                    "Revert the Phase 5 commit. No code impact.",
                    "1 hour",
                    ""
            ),

            new Phase("06",
                    "Verification + commit",
                    "Comprehensive smoke test, conformance scan, final tag.",
                    "The closing gate. Everything compiles, all tests pass, all servers boot, all browser flows work. After this phase passes, tag the rename complete and merge.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("`mvn clean install` — all 5 modules green, all tests pass", false),
                            new Task("`mvn -pl homing-demo exec:java …WonderlandDemoServer` boots; /app?app=demo-catalogue 200s", false),
                            new Task("`mvn -pl homing-studio exec:java …StudioServer` boots; /app?app=studio-catalogue 200s", false),
                            new Task("Browser smoke: every demo card → renders correctly", false),
                            new Task("Browser smoke: studio nav (Catalogue → DocBrowser → DocReader → TOC; Catalogue → Plan → Step)", false),
                            new Task("All 4 conformance tests still pass (Demo Css/Href, Studio Css/Href)", false),
                            new Task("PAUSE — report status; user manually tags `rename-complete` + `homing-v0.1` and merges", false),
                            new Task("Update `docs/brand/RENAME-TO-HOMING.md` status from Draft to Implemented", false),
                            new Task("Update this RenameSteps tracker — mark all phases DONE", false)
                    ),
                    List.of(new Dependency("05", "All previous phases must be complete and verified")),
                    "All listed verifications pass. Studio shows 6/6 phases DONE in the rename plan view.",
                    "Don't merge. Investigate any failure on the branch.",
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
            doneTasks  += p.tasks().stream().filter(Task::done).count();
            totalTasks += p.tasks().size();
        }
        return totalTasks == 0 ? 0 : (doneTasks * 100 / totalTasks);
    }

    public static int openDecisionsCount() {
        return (int) DECISIONS.stream().filter(d -> d.status() == DecisionStatus.OPEN).count();
    }

    private RenameSteps() {}
}
