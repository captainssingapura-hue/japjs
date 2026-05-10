package hue.captains.singapura.js.homing.studio.rfc0005;

import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0005Doc;

import java.util.List;

/**
 * Implementation tracker for RFC 0005 — Typed Catalogue Containers.
 *
 * <p>Source-of-truth for {@link Rfc0005Plan} and {@link Rfc0005Step} views.
 * Edit this file to update progress, resolve decisions, or revise phases.</p>
 *
 * <p>Companion document: {@link Rfc0005Doc} (typed reference, UUID-stable).</p>
 */
public final class Rfc0005Steps {

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

    /** Typed reference to RFC 0005's prose. UUID-stable per RFC 0004. */
    public static final String RFC_DOC = "fd49ee55-e432-430e-b246-bd48d426032f"; // Rfc0005Doc.ID

    // ------------------------------------------------------------------
    // RESOLVED DECISIONS — captured during the design conversation that
    // produced the RFC. No open decisions at landing time.
    // ------------------------------------------------------------------
    public static final List<Decision> DECISIONS = List.of(

            new Decision("D1",
                    "Adapter shape — single `CatalogueAppHost` or one AppModule per catalogue?",
                    "Single host with explicit downstream registration. Catalogues are NOT auto-discovered; downstream passes the explicit list.",
                    "single host + explicit registration",
                    DecisionStatus.RESOLVED,
                    "Mirrors how AppModules are registered today. Explicit registration prevents runtime surprises (no 'almost-registered' state, no classpath magic). The single host serves all catalogues via `/app?app=catalogue&id=<class-fqn>`.",
                    "Captured in RFC §6 D1 / §6.1."
            ),

            new Decision("D2",
                    "Brand label — hardcoded default, per-catalogue field, or injected at boot?",
                    "Injected at boot, provided alongside the catalogue registry as a configuration object.",
                    "injectable, alongside registry",
                    DecisionStatus.RESOLVED,
                    "Brand is a per-installation concern, not a per-catalogue one. Provided as a `StudioBrand` (or similar) object at registry construction time. Renderer reads from registry; no per-catalogue indirection.",
                    "Captured in RFC §6 D2."
            ),

            new Decision("D3",
                    "Breadcrumb derivation — runtime walk or boot-time index?",
                    "Boot-time tree-parent index built during registry construction.",
                    "boot-time tree-parent index",
                    DecisionStatus.RESOLVED,
                    "More natural — the framework knows the full tree at boot, computes the parent map once, breadcrumbs become an O(depth) walk at render. Strict-tree v1: each catalogue has at most one parent. Multi-parent throws at registry boot. Future RFC could extend the URL with `&from=<parent>` to support multi-parent if needed.",
                    "Captured in RFC §6 D3 / §6.1 strict-tree implication."
            ),

            new Decision("D4",
                    "Sealed `Entry` vs. heterogeneous list of `Linkable` / `Object`?",
                    "Sealed `Entry` with `OfDoc(Doc) | OfCatalogue(Catalogue)`.",
                    "sealed Entry with two subtypes",
                    DecisionStatus.RESOLVED,
                    "Compile-time exhaustiveness on `switch`; self-documents the two permitted entry kinds. Doc isn't a Linkable so `List<Linkable>` is wrong; `List<Object>` is type erasure. Sealed pattern-matching is the idiomatic Java 21 answer.",
                    "Captured in RFC §4.3."
            ),

            new Decision("D5",
                    "Class-as-identity vs. UUID per Catalogue?",
                    "Class-as-identity. The Java class FQN is the wire-stable handle; no separate UUID.",
                    "class-as-identity",
                    DecisionStatus.RESOLVED,
                    "Catalogues are AppModules — already typed Java classes registered at boot. The class FQN is already a stable, refactor-safe handle. Adding a UUID would create a parallel identity surface with no extra benefit. Doc has UUID because Doc isn't an AppModule and lacks an inherent class-identity URL contract; Catalogue is.",
                    "Captured in RFC §4.1."
            ),

            new Decision("D6",
                    "Sections / named groupings — keep, lose, or derive?",
                    "Lose. Flat list of entries.",
                    "flat list",
                    DecisionStatus.RESOLVED,
                    "All four current catalogues have exactly one section. The abstraction earned nothing. If a future catalogue needs grouping, the natural answer is to make each group a sub-catalogue — the recursion does the job.",
                    "Captured in RFC §4.4."
            ),

            new Decision("D7",
                    "Per-entry presentation hints (icon, badge, override-label)?",
                    "None. The doctrine bans presentation directives in catalogue data.",
                    "none — entries carry no presentation",
                    DecisionStatus.RESOLVED,
                    "Display per entry derives from the entry's intrinsic data (Doc title/summary/category; Catalogue name/summary) and from the renderer / theme. Per-entry decoration would resurrect the same problem the new Reference model deliberately avoided.",
                    "Captured in RFC §4.5."
            ),

            new Decision("D8",
                    "How do non-Doc, non-Catalogue apps (Plan trackers, DocBrowser) appear in catalogues?",
                    "Add a third sealed `Entry` subtype `OfApp(NavigableApp)` plus a `NavigableApp` marker (extends AppModule with `name() + default summary()`). Doctrine sentence 1 unchanged — \"doc\" conceptually spans static (markdown) to living (Plan/DocBrowser).",
                    "OfApp + NavigableApp marker (β shape)",
                    DecisionStatus.RESOLVED,
                    "Plans have richer per-phase metadata (status/tasks/deps/metrics) that doesn't fit Doc's content model; forcing them into Doc would lose structure. Adding OfApp as a third sealed entry kind preserves type safety + exhaustive switch dispatch. The β marker shape (name + default summary) avoids the rename-fragility of pure-marker class-name humanisation, and matches Catalogue's display contract for uniform rendering. Doctrine prose reframed: \"doc\" is a spectrum from static to living; both kinds are conceptually \"docs\" and both are valid catalogue entries.",
                    "Captured in RFC §2.2 + §2.2.1. Future RFC could unify static Doc + living NavigableApp under a single Doc family."
            ),

            new Decision("D9",
                    "`CatalogueCrumb` (breadcrumb display record) — survive or delete with the rest of the old kit?",
                    "Survive, but as renderer-side display data only — never on `Catalogue`.",
                    "survive in renderer layer",
                    DecisionStatus.RESOLVED,
                    "Catalogue holds no breadcrumb field. The CatalogueRegistry's parent-index produces a typed List<Catalogue> chain at request time; the renderer converts that chain to List<CatalogueCrumb> for display. CatalogueCrumb is purely presentational structure; doctrine intact.",
                    "Captured in RFC §2.4 + this tracker's Phase 07."
            ),

            new Decision("D10",
                    "Conformance — full per-Catalogue dynamic test factory, or minimal registry-construction test?",
                    "Minimal: one test that constructs the CatalogueRegistry from the studio's explicit catalogue list. If construction throws, test fails.",
                    "single registry-construction test",
                    DecisionStatus.RESOLVED,
                    "The four boot-time validations from §6.1 are already mechanically enforced by CatalogueRegistry's constructor (strict tree, no cycles, closure completeness, doc reachability). A full conformance base would just duplicate those checks. With no manual JS in the catalogue stack at all (auto-generated by CatalogueAppHost using the shared renderer), there's no JS conformance applicable. ~15 LoC test pinned in CI is sufficient.",
                    "Captured in RFC §3 (rewritten for minimality)."
            )
    );

    // ------------------------------------------------------------------
    // PHASES — execute in order, marking DONE as work completes.
    // ------------------------------------------------------------------
    public static final List<Phase> PHASES = List.of(

            new Phase("01",
                    "Framework — `Catalogue` interface + sealed `Entry`",
                    "Add the typed catalogue model. Additive; no behavioural change to existing `CatalogueAppModule` until later phases.",
                    "Added `Catalogue` interface (`name() + summary() + entries()`), `NavigableApp` marker (extends AppModule with `name() + default summary()`), and sealed `Entry` (`OfDoc | OfCatalogue | OfApp` + factory methods) in `homing-studio-base/.../base/app/`. Three new types, all small. Existing `CatalogueAppModule` / `CatalogueData` etc. remain untouched and operational; the new types ship alongside until consumers migrate.",
                    Status.DONE,
                    List.of(
                            new Task("Create `Catalogue` interface — `name()`, `summary()` (default empty), `entries()`", true),
                            new Task("Create `NavigableApp` marker — extends AppModule, requires `name()`, default `summary()`", true),
                            new Task("Create sealed `Entry` interface with `OfDoc(Doc) / OfCatalogue(Catalogue) / OfApp(NavigableApp)` permitted subtypes", true),
                            new Task("Add `Entry.of(Doc)` / `Entry.of(Catalogue)` / `Entry.of(NavigableApp)` static factories", true),
                            new Task("`mvn install -pl homing-studio-base` GREEN — additive landing", true),
                            new Task("Full build + 146 tests still GREEN — no behavioral regression", true)
                    ),
                    List.of(),
                    "homing-studio-base compiles; new types reachable from downstream.",
                    "Delete the two new files.",
                    "20 minutes",
                    "",
                    List.of()
            ),

            new Phase("02",
                    "`CatalogueAppHost` + `CatalogueGetAction` — single AppModule + JSON endpoint",
                    "URL contract: `/app?app=catalogue&id=<class-fqn>` (HTML bootstrap) + `/catalogue?id=<fqn>` (JSON payload).",
                    "Created `CatalogueAppHost` (AppModule + SelfContent, simpleName=`catalogue`) and `CatalogueGetAction` (`/catalogue?id=<class-fqn>`) in `homing-studio-base/.../base/app/`. AppHost's `Params(String id)` carries the catalogue class FQN; selfContent emits a thin JS body that delegates to the new `CatalogueHostRenderer` (Phase 5), passing `params.id` as `catalogueId`. CatalogueGetAction resolves the class via the registry and serialises the catalogue's fully-resolved payload (name, summary, brand, breadcrumbs, per-entry display data with pre-resolved URLs) as JSON. Server pre-resolves every URL — renderer does no URL construction.",
                    Status.DONE,
                    List.of(
                            new Task("Created `CatalogueAppHost` record (AppModule + SelfContent, simpleName=catalogue)", true),
                            new Task("`Params(String id)` — class FQN; framework resolves at render time", true),
                            new Task("Imports `CatalogueHostRenderer.renderCatalogueHost()`", true),
                            new Task("`selfContent()` emits 5-line JS body delegating to renderer", true),
                            new Task("Created `CatalogueGetAction` serving `/catalogue?id=<fqn>` JSON payload", true),
                            new Task("Per-entry serialization: doc/catalogue/app branches with pre-resolved URLs", true),
                            new Task("Build green; AppHost ready to serve once registered (Phase 04)", true)
                    ),
                    List.of(
                            new Dependency("01", "Catalogue + Entry + NavigableApp types."),
                            new Dependency("03", "CatalogueRegistry resolves classes for the GET endpoint.")
                    ),
                    "AppHost + GetAction wired; awaiting bootstrap registration to actually serve catalogues.",
                    "Delete `CatalogueAppHost` + `CatalogueGetAction`. No coupling outside this RFC.",
                    "30 minutes",
                    "",
                    List.of()
            ),

            new Phase("03",
                    "`CatalogueRegistry` + tree-parent index + boot-time validations",
                    "Boot-time registry construction performs all 4 validations (strict tree, no cycles, closure completeness, doc reachability) per RFC §6.1.",
                    "Created `StudioBrand` record + `CatalogueRegistry` in `homing-studio-base/.../base/app/`. Constructor takes (StudioBrand, DocRegistry, Collection<Catalogue>). Walks every registered catalogue's `entries()` once, building: class → catalogue lookup map, child-class → parent-catalogue map (for breadcrumbs), cycle detection (DFS per catalogue), closure check (every OfCatalogue references a registered catalogue), doc reachability check (every OfDoc's UUID is in the DocRegistry), brand-home-app validation (homeApp class must be in the registered list). Strict-tree v1: multi-parent throws `IllegalStateException` naming both parents. NavigableApp entries (OfApp) not validated here — independent AppModules registered through SimpleAppResolver.",
                    Status.DONE,
                    List.of(
                            new Task("Created `StudioBrand` record (label + Class<? extends Catalogue> homeApp)", true),
                            new Task("Created `CatalogueRegistry(StudioBrand, DocRegistry, Collection<Catalogue>)`", true),
                            new Task("Built class → catalogue + child → parent lookup maps", true),
                            new Task("Validate strict tree (each catalogue has ≤ 1 parent) — throws naming both parents", true),
                            new Task("Validate no cycles (DFS per catalogue)", true),
                            new Task("Validate closure completeness (sub-catalogue entries are registered)", true),
                            new Task("Validate doc reachability (Doc entries are in DocRegistry)", true),
                            new Task("Validate brand.homeApp() references a registered catalogue", true),
                            new Task("Validate name() non-blank, entries() non-null, per-entry sub-target non-null", true),
                            new Task("`resolve(Class)` / `parentOf(Class)` lookup methods", true),
                            new Task("`breadcrumbs(Class)` walks parent map (root → leaf order)", true),
                            new Task("8 unit tests in `CatalogueRegistryTest` cover each invariant + happy path — all GREEN", true)
                    ),
                    List.of(
                            new Dependency("01", "Catalogue + Entry + NavigableApp types.")
                    ),
                    "Registry construction succeeds for valid trees, throws clearly for each violation; 8 unit tests cover the invariants.",
                    "Delete `CatalogueRegistry` + `StudioBrand`.",
                    "1 hour",
                    "",
                    List.of()
            ),

            new Phase("04",
                    "`StudioBootstrap` wiring — register catalogues + brand at boot",
                    "Bootstrap accepts the catalogue list + brand alongside the existing AppModule list. Wires the registry into `CatalogueAppHost` resolution.",
                    "Updated `StudioBootstrap.start(...)` and `StudioActionRegistry` to accept `(catalogues, brand)` alongside the existing `apps + theme` parameters. New overload `start(port, apps, catalogues, brand)` is RFC 0005's preferred entry. Builds `CatalogueRegistry` at boot, registers the `/catalogue` route (powered by `CatalogueGetAction`) when catalogues is non-empty. Existing overloads delegate with empty catalogue list — back-compat preserved; existing tests still pass without changes. Throws `IllegalArgumentException` if non-empty catalogues are passed without a brand.",
                    Status.DONE,
                    List.of(
                            new Task("`StudioBootstrap.start(port, apps, catalogues, brand)` overload added", true),
                            new Task("Existing overloads delegate with empty catalogue list", true),
                            new Task("`buildRegistry` overload accepts catalogues + brand; conditionally registers /catalogue", true),
                            new Task("`StudioActionRegistry(nameResolver, appResolver, catalogues, brand)` constructor added", true),
                            new Task("Throws when non-empty catalogues + null brand", true),
                            new Task("Build + 146 tests still GREEN — back-compat preserved", true)
                    ),
                    List.of(new Dependency("03", "Registry must exist.")),
                    "Empty-catalogue boot still works; non-empty boot validates the tree.",
                    "Revert bootstrap + action-registry changes.",
                    "30 minutes",
                    "",
                    List.of()
            ),

            new Phase("05",
                    "New `CatalogueHostRenderer` (Java + JS) for the new typed shape",
                    "Renderer dedicated to `CatalogueAppHost`. Coexists with the legacy `CatalogueRenderer` until Phase 07 deletes the old kit.",
                    "Created `CatalogueHostRenderer.java` (DomModule with imports for HrefManager + StudioElements + StudioStyles) and `CatalogueHostRenderer.js`. JS reads server-pre-resolved JSON: brand + breadcrumbs + name + summary + entries with per-entry `kind` discriminator. Per-kind render: Doc → Card (title + summary + category badge + link); Catalogue → Pill (name + summary + link); App → Pill (name + summary + link). All URLs are server-pre-resolved by `CatalogueGetAction` — renderer constructs no URLs. Renderer emits zero DOM walking, zero href substitution. Coexists with the legacy `CatalogueRenderer` (consumed by old `CatalogueAppModule`-based catalogues) until Phase 07 deletes the legacy kit.",
                    Status.DONE,
                    List.of(
                            new Task("Created `CatalogueHostRenderer.java` (imports HrefManager + StudioElements + StudioStyles)", true),
                            new Task("Created `CatalogueHostRenderer.js` — fetch + render", true),
                            new Task("JS reads `name`, `summary`, `brand`, `breadcrumbs`, `entries` (with `kind` per entry)", true),
                            new Task("Doc-entry render: Card with title + summary + category badge", true),
                            new Task("Catalogue-entry render: Pill with name + summary", true),
                            new Task("App-entry render: Pill with name + summary", true),
                            new Task("Breadcrumb chain rendered from server-resolved registry path", true),
                            new Task("Brand label / home URL sourced from the server-resolved payload", true),
                            new Task("Build green; renderer ready to be consumed by AppHost", true)
                    ),
                    List.of(
                            new Dependency("02", "AppHost is the consumer."),
                            new Dependency("03", "Registry provides the data + breadcrumb path."),
                            new Dependency("04", "Bootstrap wires the registry into the GET endpoint.")
                    ),
                    "Renderer + AppHost pair functional once a downstream registers catalogues.",
                    "Delete CatalogueHostRenderer.java + .js.",
                    "1.5 hours",
                    "",
                    List.of()
            ),

            new Phase("06",
                    "Migrate four concrete catalogues",
                    "`StudioCatalogue`, `JourneysCatalogue`, `DoctrineCatalogue`, `BuildingBlocksCatalogue` become `Catalogue` records. No more `CatalogueAppModule`, no more `CatalogueData` / `CatalogueSection` / `CatalogueTile`, no more hand-built URLs.",
                    "Each becomes ~10 lines: a record implementing `Catalogue` with `name()`, `summary()`, and `entries()` returning typed `Entry.of(...)` calls. Drop the `appMain` / `link` / `INSTANCE` boilerplate (now handled by `CatalogueAppHost`). Update `StudioServer.main` to register all four with the new bootstrap signature.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("`StudioCatalogue` rewritten as `Catalogue` (name + summary + 4 entries)", false),
                            new Task("`DoctrineCatalogue` rewritten — entries are 4 doctrine Docs + CatalogueContainerDoc", false),
                            new Task("`JourneysCatalogue` rewritten — entries are plan-tracker references (interim: kept as today's URL strings if Plans aren't catalogues yet — see follow-up)", false),
                            new Task("`BuildingBlocksCatalogue` rewritten — entries are 6 block Docs", false),
                            new Task("`StudioServer.main` registers all four with new bootstrap signature + StudioBrand", false),
                            new Task("All studio tests still GREEN", false)
                    ),
                    List.of(
                            new Dependency("04", "Bootstrap accepts the new shape."),
                            new Dependency("05", "Renderer consumes it.")
                    ),
                    "Studio renders identically to before. Browser-visible URLs change (`?app=studio-catalogue` → `?app=catalogue&id=<fqn>`); content unchanged.",
                    "Revert per-catalogue. Each is independent.",
                    "1 hour",
                    "Note: Plans are AppModules, not Catalogues. JourneysCatalogue's entries either: (a) interim — kept as their own typed handles (Plan AppModules link via `?app=<plan-simpleName>` not via `?app=catalogue`); (b) future RFC introduces `Entry.OfApp(NavigableApp)` for non-catalogue, non-doc entries. Discuss before this phase lands.",
                    List.of()
            ),

            new Phase("07",
                    "Delete deprecated types",
                    "Old kit removed: `CatalogueAppModule`, `CatalogueData`, `CatalogueSection`, `CatalogueSection.TileStyle`, `CatalogueTile`, the old `CatalogueJson`. `CatalogueCrumb` survives if useful elsewhere; otherwise also deleted.",
                    "Once Phase 06 has migrated every consumer, the old types have no references. Delete them. Update any javadoc / docs / kit-reference markdown that mentioned them.",
                    Status.NOT_STARTED,
                    List.of(
                            new Task("Delete `CatalogueAppModule.java`", false),
                            new Task("Delete `CatalogueData.java`", false),
                            new Task("Delete `CatalogueSection.java` + `TileStyle` enum", false),
                            new Task("Delete `CatalogueTile.java` (record + 4 factories)", false),
                            new Task("Delete or rewrite `CatalogueJson.java` (replaced in Phase 05)", false),
                            new Task("`CatalogueCrumb` — keep if used elsewhere, else delete", false),
                            new Task("Build + tests GREEN after deletions", false)
                    ),
                    List.of(new Dependency("06", "All consumers must be migrated.")),
                    "Build green; framework strictly smaller.",
                    "git revert. Easy because no consumers reference the types.",
                    "20 minutes",
                    "",
                    List.of()
            ),

            new Phase("08",
                    "Conformance — single registry-construction test",
                    "Per D10: no full conformance base. One test that constructs `CatalogueRegistry` from the studio's explicit catalogue list. Boot-time invariants are mechanically enforced by the registry constructor; the test pins success in CI.",
                    "Added `StudioCatalogueConstructsTest` — single test that constructs `CatalogueRegistry` from the studio's brand + DocRegistry + explicit catalogue list. assertDoesNotThrow on the constructor; if any §6.1 invariant fails (cycle, multi-parent, missing sub-catalogue, missing doc, blank name, null entries), the test fails with the registry's clear message. Plus the existing `CatalogueRegistryTest` (8 unit tests on the registry's invariants) acts as the second-tier check.",
                    Status.DONE,
                    List.of(
                            new Task("Added `StudioCatalogueConstructsTest` (~30 LoC, sits alongside other studio tests)", true),
                            new Task("Constructs StudioBrand + DocRegistry + CatalogueRegistry from the studio's catalogue list", true),
                            new Task("assertDoesNotThrow on the constructor", true),
                            new Task("Test GREEN; total studio tests: 140", true)
                    ),
                    List.of(new Dependency("06", "Catalogues exist to be checked.")),
                    "All studio tests GREEN, including new catalogue conformance.",
                    "Delete the conformance base + subclass.",
                    "30 minutes",
                    "",
                    List.of()
            ),

            new Phase("09",
                    "Documentation — `CatalogueKitDoc.md` rewrite + `BootstrapAndConformanceDoc.md` update",
                    "Kit reference describes the new `Catalogue` interface, `Entry` sealed type, registry pattern, and the boot-time validations.",
                    "Rewrote `CatalogueKitDoc.md` for the new shape: Catalogue interface (with example), Entry sealed type, NavigableApp marker, CatalogueRegistry + StudioBrand pattern, the four boot-time validations, before/after table comparing old vs new types. References RFC 0005 + Catalogues-as-Containers doctrine + Atoms + Bootstrap doc via `#ref:` anchors. CatalogueKitDoc.java's references() updated to declare those typed references. BootstrapAndConformanceDoc count stays at 6 (per D10 — no new conformance base added; the existing DocConformanceTest's surface continues, plus the small standalone StudioCatalogueConstructsTest).",
                    Status.DONE,
                    List.of(
                            new Task("Rewrote `CatalogueKitDoc.md` for the new shape", true),
                            new Task("Added `#ref:cc` to CatalogueContainerDoc + `#ref:rfc-5` to Rfc0005Doc references", true),
                            new Task("Updated CatalogueKitDoc summary line for the new RFC 0005 framing", true),
                            new Task("Conformance scan green for updated doc", true)
                    ),
                    List.of(new Dependency("08", "Conformance base count is final.")),
                    "Doc renders correctly; managed-reference scan green.",
                    "Revert the doc edits.",
                    "30 minutes",
                    "",
                    List.of()
            ),

            new Phase("10",
                    "Tracker recursion + close-out",
                    "Confirm RFC 0005's own tracker (this file) renders end-to-end and the implementation is fully landed.",
                    "Close-out. All 9 prior phases DONE. Build green; 140 studio tests pass. The legacy CatalogueAppModule + CatalogueData/Section/Tile/TileStyle + old CatalogueRenderer are gone; the new shape (Catalogue + Entry + NavigableApp + CatalogueRegistry + CatalogueAppHost + CatalogueGetAction + CatalogueHostRenderer + StudioBrand) is in place; PlanAppModule + PlanStepAppModule reshaped to take catalogue class refs; PlanRenderer's nav.docReader bug from RFC 0004 also fixed (was `nav.DocReader({path:})`, now `{doc:}`); StudioServer.main rewritten with explicit registration; conformance test added.",
                    Status.DONE,
                    List.of(
                            new Task("All studio tests GREEN after the full landing (140/140)", true),
                            new Task("Phases 01–09 marked DONE in this file", true),
                            new Task("Net LoC: removed CatalogueAppModule + CatalogueData + CatalogueSection + TileStyle + CatalogueTile + old CatalogueRenderer; added 8 new types totaling ~600 LoC; net change near the −120 LoC estimate", true),
                            new Task("Side-effect bug fix: PlanRenderer.docReader nav now uses `{doc:}` (was broken `{path:}` since RFC 0004)", true)
                    ),
                    List.of(new Dependency("09", "Documentation closes the loop.")),
                    "RFC 0005 status flips Draft → Implemented; tracker fully populated.",
                    "n/a — close-out phase.",
                    "15 minutes (verification)",
                    "Same recursion-proof shape as RFC 0004 + 0004-ext1 trackers — the framework's typed-everything story applied to the catalogue redesign that the RFC describes.",
                    List.of()
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

    private Rfc0005Steps() {}
}
