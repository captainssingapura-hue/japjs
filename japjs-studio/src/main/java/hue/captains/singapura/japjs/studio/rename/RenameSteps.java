package hue.captains.singapura.japjs.studio.rename;

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

    public static final String OLD_NAME      = "japjs";
    public static final String NEW_NAME      = "Homing";
    public static final String EXECUTION_DOC = "rename/EXECUTION-PLAN.md";
    public static final String DOSSIER_DOC   = "brand/RENAME-TO-HOMING.md";

    // ------------------------------------------------------------------
    // OPEN DECISIONS — resolve before executing Phase 1.
    // ------------------------------------------------------------------
    public static final List<Decision> DECISIONS = List.of(

            new Decision("D1",
                    "Project name suffix — `Homing` or `homing.js`?",
                    "Plain `Homing` (no .js suffix). Java is source of truth; the .js suffix tells the JavaScript-first story (React.js, Vue.js, Next.js).",
                    null,
                    DecisionStatus.OPEN,
                    "If the user prefers `homing.js`, only the artifactIds change shape (`homing-core` → `homing.js-core`). Mechanically identical otherwise. Discouraged but possible.",
                    ""
            ),

            new Decision("D2",
                    "Java package root — `hue.captains.singapura.homing` or `io.homing`?",
                    "Keep `hue.captains.singapura.homing` for this rename. The author-namespace prefix is preserved; only the trailing `japjs` segment becomes `homing`.",
                    null,
                    DecisionStatus.OPEN,
                    "Migrating to `io.homing` is a separate strategic decision (project-level distribution namespace) that can happen later if/when the project goes public. Don't bundle.",
                    ""
            ),

            new Decision("D3",
                    "Brand logo redesign — execute inside the rename, or defer?",
                    "Defer the lowercase-h-with-arch redesign as a separate task. Do the wordmark text replacement (`japjs` → `Homing`) in Phase 4 only. Keep the j-shaped mark temporarily with a known-issue note.",
                    null,
                    DecisionStatus.OPEN,
                    "The mark redesign is real design work (1–2 hours by itself). Bundling it inflates the rename's calendar time and risks a half-baked mark if rushed. Cleaner to ship the rename, then take the mark redesign as its own task with proper iteration.",
                    ""
            ),

            new Decision("D4",
                    "Commit granularity — single commit or commit-per-phase?",
                    "Commit-per-phase, single PR at the end. Granular commits give clean rollback boundaries; the single PR keeps history navigable.",
                    null,
                    DecisionStatus.OPEN,
                    "Atomic single commit also works if you prefer a flat history. No strong opinion — pick the workflow that matches your habit.",
                    ""
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
                    "The biggest mechanical edit. Best done in IDE for the package refactor (handles all imports automatically). Affects ~136 Java files. Five Maven modules rename.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Update root pom.xml: artifactId `japjs` → `homing`; <modules> entries", false),
                            new Task("Update each child pom.xml: artifactId, parent reference, inter-module dependency tags", false),
                            new Task("`git mv japjs-{core,server,conformance,demo,studio} homing-...` directories", false),
                            new Task("IDE: Refactor → Rename Package `hue.captains.singapura.japjs` → `.homing` in every module", false),
                            new Task("Class rename: `JapjsActionRegistry` → `HomingActionRegistry` (IDE refactor)", false),
                            new Task("Find/replace system properties: `japjs.devRoot` → `homing.devRoot`, `japjs.studio.docsRoot` → `homing.studio.docsRoot`", false),
                            new Task("Verify no `*/japjs/*` directories remain (excluding target/)", false),
                            new Task("`mvn clean install` green; all tests pass", false)
                    ),
                    List.of(new Dependency("01", "Snapshot must exist for safe rollback")),
                    "`find . -type d -name 'japjs' | grep -v target` returns empty. `mvn clean install` green. All conformance tests still pass.",
                    "`git checkout pre-rename-japjs` (Phase 1 commit).",
                    "2 hours",
                    ""
            ),

            new Phase("03",
                    "Resource paths",
                    "Rename `japjs/...` resource subtrees to `homing/...`; update path constants in Java.",
                    "Server resources live under `src/main/resources/japjs/{js,css,svg}/...`. The path is convention-bound — both filesystem and Java code must agree. Phase 3 changes both atomically.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("`git mv` each `*/src/main/resources/japjs` to `homing` (5 modules)", false),
                            new Task("Update path constants in `ResourceReader`, `CssConformanceTest`, `HrefConformanceTest`", false),
                            new Task("Update path constants in `EsModuleGetAction`, `CssContentGetAction`, `SvgGroupContentProvider`, `CssGroupContentProvider`", false),
                            new Task("Verify `homing.devRoot` resolution works for live-reload", false),
                            new Task("`mvn clean install` green; demo + studio servers boot and serve a page", false)
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
                            new Task("Brand mark redesign (lowercase h with arch) — DEFERRED per Decision D3 unless overridden", false),
                            new Task("`grep -ri 'japjs' homing-*/src` returns only intentional matches", false)
                    ),
                    List.of(new Dependency("03", "Resource paths must already point to homing/")),
                    "Visual smoke: open every demo and studio app in browser; brand reads `Homing` everywhere; no stray `japjs` strings except in deliberate references (this plan, the dossier).",
                    "Revert the Phase 4 commit. Cosmetic-only — no functional impact if reverted.",
                    "1–3 hours (1 if logo deferred)",
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
                            new Task("Visual check: studio's DocBrowser lists every doc; each card opens correctly", false)
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
                            new Task("Tag rename commit: `git tag rename-complete` and `git tag homing-v0.1`", false),
                            new Task("Update `docs/brand/RENAME-TO-HOMING.md` status from Draft to Implemented", false),
                            new Task("Update this RenameSteps tracker — mark all phases DONE, decisions resolved", false)
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
