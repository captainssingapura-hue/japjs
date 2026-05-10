package hue.captains.singapura.js.homing.studio.rfc0004;

import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0004Doc;

import java.util.List;

/**
 * Implementation tracker for RFC 0004 — Typed Docs, UUIDs, and Public/Private Visibility.
 *
 * <p>The recursion is the proof — RFC 0004 introduced typed {@code Doc} references with stable
 * UUIDs, and this tracker uses one ({@link Rfc0004Doc#ID}) for its own
 * "Execution Plan" footer link. If the wire format works for the RFC's own tracker, it works
 * for everything else. See also: the same recursive proof pattern in
 * {@code BuildingBlocksCatalogue} (which lists kit docs that include the kit it itself uses
 * to render).</p>
 *
 * <p>Source-of-truth for {@link Rfc0004Plan} and {@link Rfc0004Step} views.
 * Edit this file to update progress, resolve decisions, or revise phases.
 * Recompile the studio module and refresh — the new state appears live.</p>
 *
 * <p>Companion document: {@link Rfc0004Doc} (typed) — public-tier docs/rfcs/Rfc0004Doc.md.</p>
 */
public final class Rfc0004Steps {

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

    public record Metric(String label, String before, String after, String delta) {}

    /**
     * Typed reference to RFC 0004's prose. Per RFC 0004 itself, the wire identity is the Doc's
     * UUID — when the renderer builds the "Execution Plan" footer link via
     * {@code nav.docReader(executionDoc)}, the URL becomes {@code ?app=doc-reader&doc=<uuid>}.
     * Renaming {@code Rfc0004Doc} or moving the markdown file leaves this reference intact.
     */
    public static final String RFC_DOC = "59ebba83-19fa-457f-8bd4-57bef5ec3d91"; // Rfc0004Doc.ID

    // ------------------------------------------------------------------
    // RESOLVED DECISIONS — captured during the design conversation that
    // produced the RFC. No open decisions at landing time.
    // ------------------------------------------------------------------
    public static final List<Decision> DECISIONS = List.of(

            new Decision("D1",
                    "Should `MarkdownDoc` and `ClasspathMarkdownDoc` be two layers, or collapsed?",
                    "Collapse to one — `ClasspathMarkdownDoc` carries both the loader and the markdown content-type defaults.",
                    "collapsed",
                    DecisionStatus.RESOLVED,
                    "RFC §6 left this open. Settled on collapse: there are no concrete consumers that want the markdown content-type without the classpath loader, and any future non-markdown classpath doc (e.g. SVG) gets its own `Classpath<Kind>Doc` interface. Keeping them split would have been speculative layering.",
                    "If a `ClasspathHtmlDoc` ever appears, the pattern is: copy `ClasspathMarkdownDoc`, override `contentType()` and `fileExtension()`. No abstract `MarkdownDoc` parent needed."
            ),

            new Decision("D2",
                    "How are UUIDs generated — tooling, or `UUID.randomUUID()` in a JShell session?",
                    "No tooling. Generate inline as needed; paste once, freeze.",
                    "no tooling",
                    DecisionStatus.RESOLVED,
                    "30 records is a one-time mechanical task. A maven goal or test scaffold would be more code than the records it exists to seed. Manually generated 30 UUIDs in a single PowerShell loop and pasted them into the records during migration.",
                    "If downstream studios need to seed a large doc set later, a 5-line shell snippet (`uuidgen` or `[guid]::NewGuid()`) is the answer."
            ),

            new Decision("D3",
                    "Where does `DocBrowserEntry.catLabel` live — on `Doc`, on `DocBrowserEntry`, or derived?",
                    "Stay on `DocBrowserEntry`. Browser-display concern, not intrinsic to the Doc.",
                    "DocBrowserEntry",
                    DecisionStatus.RESOLVED,
                    "`Doc.category()` is intrinsic (e.g. `RFC`, `DOCTRINE`). The longer label shown on filter buttons and section headings (`RFCs`, `Doctrines`) is how *this browser* labels its filter chips — a different browser could choose differently. Keeping `catLabel` on the entry preserves that flexibility.",
                    "Same reasoning applies to `badgeClass` (CSS variant for the badge) — also browser-side, also kept on the entry."
            ),

            new Decision("D4",
                    "Private folder name — `.doc/`, `.docs/`, or something else?",
                    "`.docs/` — leading dot for hidden, plural matches existing folder name.",
                    ".docs",
                    DecisionStatus.RESOLVED,
                    "Mirrors `.git`, `.idea`, `.claude`. Naturally ignored by `find` / `tree` / IDE search defaults. Renaming was a one-step `mv docs .docs` (the directory itself wasn't tracked separately in git, only its contents).",
                    "Captured in RFC §3.3."
            ),

            new Decision("D5",
                    "Brochure tier — public (polished pitch) or private (raw deliberation)?",
                    "Private for now. Promote later if reviewed and explicitly marked as ship-ready.",
                    "private",
                    DecisionStatus.RESOLVED,
                    "Brochure straddles the line. The RFC originally classified it as public per the 'polished pitch material' definition; user feedback during impl pulled it back to private until the content's reviewed. No `BrochureDoc` records were created in this RFC.",
                    "If brochure goes public, it's mechanical — one Doc record per brochure file under `studio.docs.brochure`, copy each .md into matching resource path, add to a DocProvider. No framework changes."
            ),

            new Decision("D6",
                    "Co-locate `.md` next to its Doc record under `docs/<package>/`, or flat `resources/docs/<area>/`?",
                    "Co-located mirror under `docs/<package>/`. Convention-driven `resourcePath()` derives the file location from the record's class name.",
                    "co-located mirror under docs/",
                    DecisionStatus.RESOLVED,
                    "Co-location lets the IDE refactor the Java record and the .md file in lock-step. The flat layout would have reintroduced a path string at the boundary between record and file — exactly what the RFC was trying to eliminate. `ResourceMarkdownDoc` is kept as an explicit-path escape hatch for the rare case where the convention doesn't fit.",
                    "Captured in RFC §3.2."
            ),

            new Decision("D7",
                    "Land the change directly, or write RFC 0004 first?",
                    "RFC first — the design surface (UUID identity, self-provide, public/private split, deletion of DocGroup) was big enough that capturing it before coding paid off in 30 minutes saved during migration.",
                    "RFC first",
                    DecisionStatus.RESOLVED,
                    "The design conversation that produced the RFC clarified the call-site ergonomics (DocBrowserEntry shape, CatalogueTile.docCard helper) and the deletions (DocContentGetAction, DocGroup, /doc-content, the homing.studio.docsRoot plumbing) before any code was touched.",
                    "The RFC is the artifact — landed at docs/rfcs/0004 (now Rfc0004Doc) before any framework primitive was written."
            )
    );

    // ------------------------------------------------------------------
    // PHASES — one per migration step from RFC §7. All landed in a
    // single session 2026-05-09 / 2026-05-10.
    // ------------------------------------------------------------------
    public static final List<Phase> PHASES = List.of(

            new Phase("01",
                    "Framework primitives — `Doc`, `DocProvider`, `DocRegistry`, three subinterfaces",
                    "New typed-Doc model alongside the legacy `DocGroup`/`Doc<D>` machinery; landed additively so the build stayed green during migration.",
                    "Created `Doc` (UUID + title + contents() + content-type defaults; no type parameter), `ClasspathMarkdownDoc` (default `resourcePath()` derives `docs/<package>/<SimpleName>.md`), `InlineDoc` (text-block content), `ResourceMarkdownDoc` (explicit path), `DocProvider` (marker for AppModules contributing docs), `DocRegistry` (UUID-indexed; `from(SimpleAppResolver)` walks the closure for DocProviders, throws on UUID collision at boot).",
                    Status.DONE,
                    List.of(
                            new Task("Create `Doc` interface — `uuid()`, `title()`, `contents()`, content-type defaults, no `<D>` parameter, no `Exportable._Constant`", true),
                            new Task("Create `ClasspathMarkdownDoc` with default `resourcePath()` derived from class name", true),
                            new Task("Create `InlineDoc` (forces override of `contents()`)", true),
                            new Task("Create `ResourceMarkdownDoc` (explicit `resourcePath()` escape hatch)", true),
                            new Task("Create `DocProvider` marker interface with `List<Doc> docs()`", true),
                            new Task("Create `DocRegistry` — UUID-indexed, collision detection at boot, `from(SimpleAppResolver)` static builder", true),
                            new Task("`mvn install -pl homing-studio-base` GREEN — additive, no behavioral change to existing types yet", true)
                    ),
                    List.of(),
                    "homing-studio-base compiles; six new types reachable from downstream.",
                    "Delete the six new files. No coupling yet to the legacy `Doc<D>`/`DocGroup` types.",
                    "30 minutes",
                    "",
                    List.of(
                            new Metric("New types in homing-studio-base", "0", "6", "+6 (Doc, ClasspathMarkdownDoc, InlineDoc, ResourceMarkdownDoc, DocProvider, DocRegistry)"),
                            new Metric("Build state",                      "green", "green", "additive landing")
                    )
            ),

            new Phase("02",
                    "Endpoint rewrite — `DocGetAction(DocRegistry)` + `DocReader.Params(UUID)` + JS fetch by id",
                    "Wire format flipped from `?path=` to `?id=`/`?doc=`. Server-side I/O leaves the action; only registered Docs reachable.",
                    "Rewrote `DocGetAction` to take a `DocRegistry`, parse the `id` query param as `UUID`, look up the registered Doc, return `doc.contents()` with `doc.contentType()`. Updated `DocReader.Params(UUID doc)` and `selfContent()` to thread the UUID into the JS body. Updated `DocReaderRenderer.js` to fetch `/doc?id=<uuid>` instead of `/doc?path=<path>`. Updated `DocBrowserRenderer.js` to build reader URLs as `?doc=<uuid>`. Updated `DocBrowserJson` to emit `id` instead of `path`. Path-traversal validation moved out of the request path entirely — the wire is no longer attack surface.",
                    Status.DONE,
                    List.of(
                            new Task("`DocGetAction(DocRegistry)` — parse UUID, registry lookup, return `doc.contents()`", true),
                            new Task("`DocReader.Params(UUID doc)` — typed query parameter", true),
                            new Task("`DocReader.selfContent()` — emits `params.doc` into the JS body (was `params.path`)", true),
                            new Task("`DocReaderRenderer.js` — fetch `/doc?id=<uuid>`; placeholder text updated", true),
                            new Task("`DocBrowserRenderer.js` — `readerUrl(id)` builds `?doc=<uuid>`; search dropped path field", true),
                            new Task("`DocBrowserJson` — emit `id` instead of `path`", true),
                            new Task("Rewrite `DocGetActionTest` for the UUID flow — register a sentinel Doc, fetch by UUID, assert content + content-type", true)
                    ),
                    List.of(new Dependency("01", "DocRegistry must exist before DocGetAction can take one.")),
                    "DocGetAction tests pass against the new UUID flow. Reader URL contract is `?doc=<uuid>`; bytes endpoint is `/doc?id=<uuid>`.",
                    "Revert DocGetAction + DocReader + the two .js files. Old `?path=` flow restored.",
                    "30 minutes",
                    "Security improvement: user-supplied input is parsed as a UUID before any lookup. Unknown UUIDs are 404s; path-traversal (..., leading /, extension whitelist) is no longer load-bearing.",
                    List.of(
                            new Metric("`/doc` URL parameter",  "?path=<string>",         "?id=<uuid>",    "typed identity"),
                            new Metric("Reader URL parameter",   "?path=<string>",         "?doc=<uuid>",   "typed identity"),
                            new Metric("Path-traversal surface", "validated per request", "no surface",    "input never reaches a path")
                    )
            ),

            new Phase("03",
                    "`StudioBootstrap` wiring + `StudioActionRegistry` simplification",
                    "Boot-time `DocRegistry` derived from the same `SimpleAppResolver` already built for nav.",
                    "`StudioBootstrap.start(...)` now calls `DocRegistry.from(appResolver)` and hands the registry to `DocGetAction`. `StudioActionRegistry` deleted its `DocContentGetAction` field, the `Path docsRoot` constructor parameter, and the `/doc-content` route registration. `StudioServer` deleted its `homing.studio.docsRoot` system-property read and the path passed to the registry constructor.",
                    Status.DONE,
                    List.of(
                            new Task("`StudioBootstrap` builds `DocRegistry.from(appResolver)` and passes to `DocGetAction(registry)`", true),
                            new Task("`StudioActionRegistry` constructor takes `(ModuleNameResolver, SimpleAppResolver)` — no `Path docsRoot`", true),
                            new Task("`/doc-content` route deleted from `StudioActionRegistry`", true),
                            new Task("`StudioServer.main` simplified — no `homing.studio.docsRoot` read", true),
                            new Task("`pom.xml` `<resource>` directive (the repo-root `../docs` mapping added in the previous turn) deleted", true)
                    ),
                    List.of(new Dependency("02", "DocGetAction's new UUID-taking constructor must exist.")),
                    "`mvn install -pl homing-studio` green; server boot prints the new endpoint summary.",
                    "Restore the four files; re-add the pom resource directive.",
                    "20 minutes",
                    "",
                    List.of(
                            new Metric("`StudioActionRegistry` constructor params",  "3 (resolver, docsRoot, appResolver)", "2 (resolver, appResolver)", "−1"),
                            new Metric("`StudioServer.main` lines",                   "~35",                                  "~28",                       "−7"),
                            new Metric("`pom.xml` <resource> directive",              "1 (../docs mapping)",                  "0",                         "−1 (workaround retired)")
                    )
            ),

            new Phase("04",
                    "First consumer — `BuildingBlocksCatalogue` migrated to typed Doc references",
                    "Five BLOCK Doc records co-located under `studio.docs.blocks`; catalogue tiles built from `Doc.uuid()`, no path strings.",
                    "Created six Doc records in `homing-studio.studio.docs.blocks` (AtomsDoc, CatalogueKitDoc, DocKitsDoc, TrackerKitDoc, BootstrapAndConformanceDoc, BlocksIndexDoc) — all `implements ClasspathMarkdownDoc` with frozen UUIDs. Moved the six matching `.md` files from `docs/blocks/` to `homing-studio/src/main/resources/docs/hue/captains/singapura/js/homing/studio/docs/blocks/<DocClass>.md`. Refactored `BuildingBlocksCatalogue` to `implements DocProvider`; its `docs()` returns the six instances. Tiles built via the new `CatalogueTile.docCard(doc, badge, badgeClass)` helper — the URL `/app?app=doc-reader&doc=<uuid>` is built once, in the kit, from the typed Doc.",
                    Status.DONE,
                    List.of(
                            new Task("Create 6 Doc records under `studio.docs.blocks` (5 blocks + index)", true),
                            new Task("Move 6 .md files into matching `resources/docs/<package>/<DocClass>.md` paths", true),
                            new Task("Add `CatalogueTile.docCard(Doc, String, String)` helper (URL built from `doc.uuid()`)", true),
                            new Task("`BuildingBlocksCatalogue implements DocProvider` — `docs()` returns the 6 instances", true),
                            new Task("`catalogueData()` rewritten to use `CatalogueTile.docCard` — no path strings, no manual URL construction", true),
                            new Task("Build green; manual smoke (read AtomsDoc.contents() in a unit test, asserts non-empty)", true)
                    ),
                    List.of(
                            new Dependency("01", "Doc + ClasspathMarkdownDoc + DocProvider must exist."),
                            new Dependency("03", "DocRegistry must be wired into bootstrap.")
                    ),
                    "Building Blocks page renders identically to before; tile URLs now carry UUIDs.",
                    "Revert BuildingBlocksCatalogue and the 6 Doc records. The 6 .md files can stay at the new path; nothing else references them yet.",
                    "30 minutes",
                    "First proof point — the ergonomics held up at small scale. `CatalogueTile.docCard` removes every path string from the call site; the iteration `for (i, doc) → docCard(doc, \"BLOCK \" + i, badgeClass)` reads cleaner than the previous `Block(\"01\", title, tagline, path)` records.",
                    List.of(
                            new Metric("Path strings in `BuildingBlocksCatalogue`",  "5 (one per block)", "0", "−5"),
                            new Metric("Doc records in `studio.docs.blocks`",         "0",                  "6", "+6"),
                            new Metric("Lines in `BuildingBlocksCatalogue.java`",     "98",                 "104", "+6 (gained DocProvider, lost Block)")
                    )
            ),

            new Phase("05",
                    "Bulk consumer — `DocBrowser` migrated; ~25 typed Doc records co-located",
                    "Every public doc the studio displays is now a typed record. Brochure pulled to private per D5.",
                    "Created 19 more Doc records under `studio.docs.{whitepaper,rfcs,defects,doctrines,guides,comparison,brand,rename}`, each with a frozen UUID and matching co-located `.md`. `DocBrowser implements DocProvider`; `DocBrowserEntry` shape simplified to `(Doc doc, String catLabel, String badgeClass)`. The `entry(Doc, String, Class)` helper replaces the previous 5-arg `doc(...)` factory. Brochure entries excluded — those .md files stay private under `.docs/brochure/` per D5.",
                    Status.DONE,
                    List.of(
                            new Task("19 Doc records created (2 whitepaper + 5 RFCs + 2 defects + 4 doctrines + 2 guides + 1 comparison + 2 brand + 1 rename)", true),
                            new Task("19 .md files moved to matching `resources/docs/<package>/<DocClass>.md` paths", true),
                            new Task("`DocBrowserEntry(Doc, String, String)` — Doc replaces 4 string fields (path, title, summary, category)", true),
                            new Task("`DocBrowser implements DocProvider`; `docs()` derived from `ENTRIES`", true),
                            new Task("Brochure entries removed from DocBrowser (D5 — private for now)", true),
                            new Task("Build green; tile click navigates to `?app=doc-reader&doc=<uuid>`", true)
                    ),
                    List.of(new Dependency("04", "Pattern validated on BuildingBlocksCatalogue first.")),
                    "DocBrowser renders 19 entries grouped by catLabel; each tile links to the typed reader URL.",
                    "Revert DocBrowser + the 19 Doc records. The .md files at the new paths can stay or be deleted.",
                    "1 hour",
                    "Mostly mechanical — Doc records average ~12 lines each. The reduction in DocBrowser.java is significant: the `doc(path, title, summary, category, catLabel, badgeClass)` factory's 6-arg call sites become `entry(DocClass.INSTANCE, catLabel, badgeClass)` 3-arg calls.",
                    List.of(
                            new Metric("Doc records in `studio.docs.*`",     "6 (after Phase 04)", "25",         "+19"),
                            new Metric("`DocBrowserEntry` field count",       "6",                  "3",          "−3 (Doc absorbs 4 fields)"),
                            new Metric("Entries in `DocBrowser.DOCS`/`ENTRIES`", "26 (with brochure)", "19",         "−7 (brochure pulled to private)"),
                            new Metric("Path strings in `DocBrowser.java`",   "26 (one per entry)", "0",          "−26")
                    )
            ),

            new Phase("06",
                    "Public-doc relocation",
                    "All 25 public .md files moved into `homing-studio/src/main/resources/docs/<package>/`; pom workaround retired.",
                    "Bulk `cp` from repo-root `docs/` into `homing-studio/src/main/resources/docs/hue/captains/singapura/js/homing/studio/docs/<area>/<DocClass>.md`. The `<resource>` directive added to `homing-studio/pom.xml` in the previous turn (mapping `../docs` onto the classpath as a workaround) was removed in Phase 03 along with `StudioActionRegistry`'s simplification — public docs now ship via standard `src/main/resources` layout.",
                    Status.DONE,
                    List.of(
                            new Task("Copy 6 blocks/*.md → blocks/*Doc.md", true),
                            new Task("Copy 2 whitepaper/*.md → whitepaper/*Doc.md", true),
                            new Task("Copy 5 rfcs/*.md → rfcs/Rfc*Doc.md", true),
                            new Task("Copy 2 defects/*.md → defects/Defect*Doc.md", true),
                            new Task("Copy 4 doctrines/*.md → doctrines/*Doc.md", true),
                            new Task("Copy 2 guides (live-tracker-pattern + user-guide) → guides/*Doc.md", true),
                            new Task("Copy 1 comparison + 2 brand + 1 rename .md files into matching paths", true),
                            new Task("Verify each file resolves via `getResourceAsStream` (covered by Phase 08 conformance test)", true)
                    ),
                    List.of(
                            new Dependency("04", "Block doc records exist."),
                            new Dependency("05", "DocBrowser doc records exist.")
                    ),
                    "Every Doc.contents() resolves; conformance test (Phase 08) gates the verification.",
                    "Delete the new resource paths.",
                    "20 minutes",
                    "",
                    List.of(
                            new Metric("Public .md files on classpath",       "0",  "25", "+25"),
                            new Metric("Public .md files at repo root `docs/`", "27", "0",  "to be removed in Phase 07"),
                            new Metric("Resource layout",                     "ad-hoc (pom <resource> mapping)", "standard (src/main/resources)", "convention restored")
                    )
            ),

            new Phase("07",
                    "Private-doc rename — `docs/` → `.docs/`",
                    "Repo-root folder renamed; only private subset retained (action plan, session summary, brochure).",
                    "`mv docs .docs` then `rm -rf` of the public subtrees inside .docs (their content already lives on the homing-studio classpath). Remaining: `ACTION-PLAN-2026-04-25.md`, `SESSION-SUMMARY-2026-04-25.md`, `brochure/` (raw pitch material per D5). The leading dot on `.docs/` mirrors `.git`, `.idea`, `.claude` — naturally hidden, naturally off the classpath.",
                    Status.DONE,
                    List.of(
                            new Task("`mv docs .docs` (Bash, since `git mv` couldn't move untracked parent)", true),
                            new Task("Remove public subtrees inside `.docs` (`blocks/`, `brand/`, `comparison/`, `defects/`, `doctrines/`, `guides/`, `rename/`, `rfcs/`, `whitepaper/`, `user-guide.md`)", true),
                            new Task("Verify private set: action plan, session summary, brochure", true),
                            new Task("Tests still pass — public docs reach only via classpath", true)
                    ),
                    List.of(new Dependency("06", "Public docs must be on classpath before pruning their copies from `docs/`.")),
                    "94/94 tests pass after rename. Repo-root `docs/` is gone.",
                    "`mv .docs docs` and restore subtrees from git history.",
                    "10 minutes",
                    "Captured in RFC §3.3.",
                    List.of()
            ),

            new Phase("08",
                    "Conformance test — `DocConformanceTest` base + `StudioDocConformanceTest`",
                    "Three checks pinned in CI: UUID non-null, UUID uniqueness, contents() resolution per Doc.",
                    "Created `DocConformanceTest` abstract base in `homing-conformance` — takes the studio's entry apps, derives the closure, walks for `DocProvider`s, generates one dynamic test per Doc + a UUID-uniqueness pin. Added `homing-studio-base` as a dependency of `homing-conformance` (the new base references `Doc`/`DocProvider`/`DocRegistry`); reorganised the parent pom's `<modules>` order accordingly so studio-base builds before conformance. `StudioDocConformanceTest` subclass in homing-studio's tests is ~10 lines (entry apps only).",
                    Status.DONE,
                    List.of(
                            new Task("`DocConformanceTest` abstract base — `entryApps()` abstract, `@TestFactory docConformance()`", true),
                            new Task("Per-Doc dynamic test: `uuid()` non-null", true),
                            new Task("Single dynamic test: UUIDs unique across closure (pinned via DocRegistry construction)", true),
                            new Task("Per-Doc dynamic test: `contents()` resolves to non-empty body", true),
                            new Task("Add `homing-studio-base` dep to `homing-conformance/pom.xml`", true),
                            new Task("Reorder parent pom modules so studio-base precedes conformance", true),
                            new Task("`StudioDocConformanceTest` subclass — entry list only", true),
                            new Task("Run: 51 dynamic tests pass (25 docs × 2 + uniqueness pin)", true)
                    ),
                    List.of(
                            new Dependency("04", "Doc records must exist."),
                            new Dependency("05", "Provider closure populated."),
                            new Dependency("06", "Resources reachable.")
                    ),
                    "`mvn -pl homing-studio test` — 94/94 pass, including 51 dynamic tests in StudioDocConformanceTest.",
                    "Delete the conformance base + subclass; revert the pom dep + module reorder.",
                    "30 minutes",
                    "The `DocRegistry` construction itself already throws on UUID collision — the uniqueness test is a CI-visible pin so a regression failure is reported as a single named test rather than a single test failing for an opaque reason. Per-Doc tests give one failure per broken record, not a single failure for whichever the closure walked first.",
                    List.of(
                            new Metric("Conformance bases in homing-conformance", "5", "6", "+1 (DocConformanceTest)"),
                            new Metric("Studio test count",                       "43", "94", "+51 (dynamic per-Doc + uniqueness)"),
                            new Metric("homing-conformance dependencies",         "homing-core", "homing-core + homing-studio-base", "+1 (Doc types live in studio-base)")
                    )
            ),

            new Phase("09",
                    "Cleanup — delete `DocGroup`, `DocManager`, the `*Doc<D>` sub-interfaces, `DocContentGetAction`, `homing.studio.docsRoot` plumbing",
                    "Every legacy type retired. Net code reduction realised.",
                    "Deleted `DocGroup.java`, `DocManager.java`, `DocManager.js`, `DocGroupServingTest.java`, `MarkdownDoc.java`, `HtmlDoc.java`, `JsonDoc.java`, `PlainTextDoc.java`, `SvgDoc.java`, `DocContentGetAction.java`. The `<D extends DocGroup<D>>` parameter was on `Doc<D>` and the five content-kind sub-interfaces — all gone with the parameter. `EsModuleGetAction` and `ManagerInjector` had javadoc comments referencing `DocGroup` / `DocManager`; those comments are now stale (notes follow-up) but the runtime logic is unaffected.",
                    Status.DONE,
                    List.of(
                            new Task("Delete `DocGroup.java`", true),
                            new Task("Delete `DocManager.java` + `DocManager.js`", true),
                            new Task("Delete `DocGroupServingTest.java`", true),
                            new Task("Delete `MarkdownDoc.java`, `HtmlDoc.java`, `JsonDoc.java`, `PlainTextDoc.java`, `SvgDoc.java`", true),
                            new Task("Delete `DocContentGetAction.java` + its registration in `StudioActionRegistry`", true),
                            new Task("Verify `mvn install` GREEN — no compile errors from now-missing types", true),
                            new Task("Verify all 94 tests still pass", true),
                            new Task("Stale javadoc references in `EsModuleGetAction` / `ManagerInjector` — flagged as follow-up, not blocking", false)
                    ),
                    List.of(new Dependency("05", "Last consumer migrated; no production code references the legacy types.")),
                    "Build + tests green; the framework is strictly smaller.",
                    "git revert. Easy — the deletions have no downstream coupling because there are no remaining consumers.",
                    "20 minutes",
                    "",
                    List.of(
                            new Metric("Files deleted",                       "0",  "10", "+10 deletions"),
                            new Metric("LoC of legacy doc machinery removed", "~700", "0", "−700"),
                            new Metric("`/doc-content` route",                  "registered (legacy)", "removed", "wire surface shrinks")
                    )
            ),

            new Phase("10",
                    "Documentation update + RFC tracker (this one)",
                    "`DocKitsDoc.md` rewritten for the typed-doc model; `BootstrapAndConformanceDoc.md` lists the sixth conformance base; this tracker captures the migration as history.",
                    "Rewrote `DocKitsDoc.md` to describe the typed `Doc` records, `ClasspathMarkdownDoc` convention, UUID URL contract, and the `DocBrowserEntry(Doc, String, String)` shape. Updated `BootstrapAndConformanceDoc.md` from 'Five conformance bases' to 'Six' and added the `DocConformanceTest` description. Added this tracker (`Rfc0004Steps` / `Rfc0004PlanData` / `Rfc0004Plan` / `Rfc0004Step`) under `studio.rfc0004.*` — recursive proof that RFC 0004's typed-Doc model works for the document the tracker itself links to (`Rfc0004Doc.ID`).",
                    Status.DONE,
                    List.of(
                            new Task("Rewrite `DocKitsDoc.md` for typed-Doc + UUID + DocProvider model", true),
                            new Task("Update `BootstrapAndConformanceDoc.md` — sixth conformance base added", true),
                            new Task("Create `Rfc0004Steps.java` (this file) with phases derived from RFC §7", true),
                            new Task("Create `Rfc0004PlanData.java` adapter, `Rfc0004Plan.java` index page, `Rfc0004Step.java` detail page", true),
                            new Task("Wire into `JourneysCatalogue` (one tile + two `link()` imports)", true),
                            new Task("Wire into all 5 conformance test subclasses (Cdn, Css, CssGroupImpl, Doctrine, Href)", true),
                            new Task("Use `Rfc0004Doc.ID` as `RFC_DOC` — the tracker's footer link is itself a typed Doc reference", true),
                            new Task("Build + tests still GREEN", true)
                    ),
                    List.of(
                            new Dependency("08", "DocConformanceTest must already gate the doc closure."),
                            new Dependency("09", "Cleanup must be done before documenting the final shape.")
                    ),
                    "`mvn install` GREEN; all studio tests pass; tracker resolves at /app?app=rfc0004-plan and links to RFC 0004 via UUID.",
                    "Delete the four tracker files + revert JourneysCatalogue + conformance subclass entries. Doc updates can stay or be reverted.",
                    "45 minutes",
                    "Recursion captured: RFC 0004's tracker references RFC 0004's typed Doc by UUID, exercising the very wire format the RFC introduced. If the tracker's footer link works, the model works.",
                    List.of(
                            new Metric("RFC trackers in the studio", "4 (RFC 0001, 0002, 0002-ext1, Rename)", "5 (+ 0004)", "+1"),
                            new Metric("RFC 0004 status",             "Draft (just shipped)",                  "Implemented",  "complete"),
                            new Metric("Net LoC delta of RFC 0004",   "—",                                     "≈ −500 (per RFC §8 estimate, met)", "framework smaller")
                    )
            )
    );

    // ------------------------------------------------------------------
    // Helpers — same shape as every other tracker.
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

    private Rfc0004Steps() {}
}
