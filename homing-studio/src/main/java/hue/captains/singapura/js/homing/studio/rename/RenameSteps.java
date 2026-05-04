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
    public static final String OLD_PKG_ROOT       = "hue.captains.singapura.js.homing";
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
                    "Pure safety — establish a clean point of return. User opted for a working feature branch (`feature/name_change`) instead of an explicit `pre-rename-japjs` tag. Functionally equivalent: the rename commits live on the branch and can be reset/dropped if needed.",
                    Status.DONE,
                    List.of(
                            new Task("Working on feature/name_change branch (functional equivalent of pre-rename snapshot)", true),
                            new Task("Tag pre-rename-japjs — SKIPPED in favor of branch-based rollback", false),
                            new Task("Branch already created and checked out: feature/name_change", true)
                    ),
                    List.of(),
                    "Working on a non-main branch where the rename can be discarded by branch reset or branch deletion if needed.",
                    "git reset --hard <pre-rename-commit> on feature/name_change, or git branch -D feature/name_change after checking out main.",
                    "(skipped tag step)",
                    ""
            ),

            new Phase("02",
                    "Java side",
                    "Maven artifactIds, directory renames, package refactor, class renames, system properties.",
                    "The biggest mechanical edit. Affects ~140 Java files. Five Maven modules rename. Per D1: artifactIds become `homing-*`. Per D2: package root becomes `hue.captains.singapura.js.homing`. NB: 9 demo integration tests in `EsModuleGetActionTest` will fail at the Phase 2 boundary because they load JS resources whose paths are still on the old layout — Phase 3 fixes them.",
                    Status.DONE,
                    List.of(
                            new Task("Update root pom.xml: artifactId, modules, groupId (also flagged groupId change for review)", true),
                            new Task("Update each child pom.xml: artifactId, parent reference, inter-module dependency groupId+artifactId", true),
                            new Task("git mv 5 module dirs (japjs-X to homing-X)", true),
                            new Task("git mv Java package dirs (.../japjs to .../js/homing) across main + test sources", true),
                            new Task("Bulk sed across 140 .java files: package decls, imports, fully-qualified string literals", true),
                            new Task("Class rename: JapjsActionRegistry to HomingActionRegistry (file + all references)", true),
                            new Task("Find/replace system properties: homing.devRoot to homing.devRoot, homing.studio.docsRoot to homing.studio.docsRoot", true),
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
                    "Server resources live under `src/main/resources/japjs/{js,css,svg}/...`. The path is convention-bound — both filesystem and Java code must agree. Phase 3 changes both atomically. Filesystem convention root is `homing/` (not `homing.js/`).",
                    Status.DONE,
                    List.of(
                            new Task("git mv resources/japjs to resources/homing in 3 modules (demo, server, studio — core + conformance have no resource trees)", true),
                            new Task("git mv each nested package-mirror dir under homing/{js,css,svg} from .../japjs to .../js/homing (6 dirs total)", true),
                            new Task("Bulk sed in Java: \"homing/js/\" / \"homing/css/\" / \"homing/svg/\" to \"homing/...\" (12 path constants in core, server, conformance, plus 5 test classes)", true),
                            new Task("Fix HrefManagerTest hard-coded full path to match new package layout", true),
                            new Task("Verify homing.devRoot resolution: live-reload still works (path constants now read from resources/homing)", true),
                            new Task("mvn clean install green: full build + ALL tests pass (the 9 EsModuleGetActionTest failures from Phase 2 are now fixed)", true),
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
                    "Replace `japjs` strings inside JS, CSS, SVG, and remaining Java content (titles, comments, marker strings, brand wordmark).",
                    "Cosmetic and content edits — not paths or class references (those landed in Phases 2–3). Two-pass replacement: first code-y identifiers (artifact names, system properties, paths, internal markers like _japjsBuildAppUrl), then brand-prose `japjs` → `Homing` everywhere visible.",
                    Status.DONE,
                    List.of(
                            new Task("Code-y bulk pass: japjs-{core,server,…} to homing-X; japjs.devRoot to homing.devRoot; japjs/{js,css,svg} to homing/X; _japjsBuildAppUrl to _homingBuildAppUrl; 'japjs generated' markers to 'homing generated'; singapura.japjs to singapura.js.homing", true),
                            new Task("Brand-prose bulk pass: 'japjs' to 'Homing' across .java, .js, .css, .svg (excluding RenameSteps.java which intentionally documents the OLD name)", true),
                            new Task("Restored 3 markdown filename refs (japjs-whitepaper.md, japjs-shell-flexibility-whitepaper.md, japjs-vs-react-vue.md) — Phase 5 will rename the actual files + these path constants", true),
                            new Task("NavWriter / ParamsWriter generated-marker comments are now 'homing generated' (test assertions updated together)", true),
                            new Task("Brand SVG <title>, aria-label, and visible wordmark text in 8 docs/brand/ logos: 'japjs' to 'Homing'", true),
                            new Task("Brand mark redesign — DEFERRED per Decision D3 (separate task, post-rename)", false),
                            new Task("grep -r 'japjs' source tree returns only RenameSteps.java (OLD name documentation) + 3 .md filename refs awaiting Phase 5", true),
                            new Task("mvn clean install fully green; all tests pass", true),
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
                    "Sweep markdown content; rename 3 whitepaper/comparison files; update DocRegistry + DocBrowser path constants.",
                    "Substantial but mechanical text replacement across project markdown. Two-pass within content (code-y identifiers + brand-prose), preserving the rename-narrative docs (RENAME-TO-HOMING.md, EXECUTION-PLAN.md, session/action logs).",
                    Status.DONE,
                    List.of(
                            new Task("Two-pass content replace across project .md (excluding rename-narrative + historical session docs): code-y identifiers, then brand-prose japjs to Homing", true),
                            new Task("Renamed docs/whitepaper/japjs-whitepaper.md to homing-whitepaper.md (plain mv — docs/ is untracked)", true),
                            new Task("Renamed docs/whitepaper/japjs-shell-flexibility-whitepaper.md to homing-shell-flexibility-whitepaper.md", true),
                            new Task("Renamed docs/comparison/japjs-vs-react-vue.md to homing-vs-react-vue.md", true),
                            new Task("Updated cross-references in 4 markdown files (00-index, 06-architecture, 0001-rfc, homing-shell-flexibility) to point at new filenames", true),
                            new Task("Updated DocRegistry.java + DocBrowser.js + DocReader.js + StudioServer.java path constants to new filenames", true),
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
                    "The closing gate. Everything compiles, all tests pass, all servers boot, all browser flows work. After this phase passes, the user manually tags the rename complete and merges to main.",
                    Status.DONE,
                    List.of(
                            new Task("mvn clean install — all 5 modules SUCCESS, all tests pass (no exclusions)", true),
                            new Task("WonderlandDemoServer boots; 10/10 demo apps return 200 (catalogue + 9 demo modules)", true),
                            new Task("StudioServer boots; 9/9 nav points return 200 (catalogue, doc-browser, doc-reader, rfc0001-plan, rfc0001-step, rename-plan, rename-step phases)", true),
                            new Task("Brand wording verified: studio reads 'Homing · studio'; demo reads 'Homing · demos'", true),
                            new Task("4 conformance suites pass (DemoCss=5, DemoHref=13, StudioCss=7, StudioHref=7) — 32 conformance tests total, all green", true),
                            new Task("Updated docs/brand/RENAME-TO-HOMING.md status from 'Decision made, deferred' to 'Implemented (2026-05-04)'", true),
                            new Task("Updated this tracker — all 6 phases marked DONE", true),
                            new Task("PAUSE — hand off to user for manual git tag (rename-complete, homing-v0.1) and merge to main", false)
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
