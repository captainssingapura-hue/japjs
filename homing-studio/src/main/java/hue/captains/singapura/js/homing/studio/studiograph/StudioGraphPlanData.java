package hue.captains.singapura.js.homing.studio.studiograph;

import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.DecisionStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Dependency;
import hue.captains.singapura.js.homing.studio.base.tracker.Objective;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.PhaseStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.Task;

import java.util.List;

/**
 * Multi-phase plan tracking the implementation of RFC 0014 — Typed Studio
 * Graph. Builds an in-memory typed object graph from the composed Bootstrap,
 * exposing the four registries ({@code CatalogueRegistry}, {@code DocRegistry},
 * {@code PlanRegistry}, {@code StudioProxyManager}) through a unified
 * collection-based query API (refactored from Stream per the
 * Explicit over Implicit doctrine — see D3).
 *
 * <p>The graph is the foundation for multiple downstream features: auto-
 * generated diagrams (RFC 0013 §5 deferred), cross-doc programmability,
 * reverse-citation queries, live conformance / observability, search index
 * generation, and the typed doc DSL's computed-content phase.</p>
 *
 * <p>Phased incrementally: Phase 1 ships the foundation; subsequent phases
 * add reverse navigation, auto-diagrams, cross-doc queries, live conformance,
 * search/sitemap, and typed-doc-DSL integration. Each phase is independently
 * shippable.</p>
 */
public final class StudioGraphPlanData implements Plan {

    public static final StudioGraphPlanData INSTANCE = new StudioGraphPlanData();

    private StudioGraphPlanData() {}

    @Override public String kicker()   { return "STUDIO GRAPH"; }
    @Override public String name()     { return "Typed Studio Graph"; }
    @Override public String subtitle() {
        return "Build an in-memory typed object graph from the composed Bootstrap, "
             + "exposing the existing registries (Catalogue / Doc / Plan / StudioProxy) "
             + "as a unified collection-based query API. Foundation for auto-diagrams, "
             + "cross-doc programmability, live observability, and the typed doc DSL's "
             + "computed-content phase. Tracks RFC 0014.";
    }
    @Override public String summary() {
        return "Foundation primitive that unlocks RFC 0013's deferred auto-diagrams, "
             + "the Doc DSL plan's computed-content phase, and broader cross-doc programmability. "
             + "Small additive layer over registries already built at boot.";
    }

    @Override public List<Objective> objectives() {
        return List.of(
                new Objective("Expose the live object graph as a typed query API",
                        "The framework's four registries (Catalogue, Doc, Plan, StudioProxy) are already built at boot, but buried inside ActionRegistry. Surface them as a typed StudioGraph<S, F> record reachable from Bootstrap.graph()."),
                new Objective("Unlock RFC 0013's deferred auto-diagrams",
                        "CatalogueTree, PlanGraph, ReferenceGraph diagram types become trivial typed projections of the graph. Each is a one-line embed in a typed doc."),
                new Objective("Foundation for cross-doc programmability",
                        "Once any doc kind can be queried (allDocsOfKind, allTensions, allCompromises), the framework's analytical surface compounds — case studies can write \"every doctrine's Tensions\" as one expression."),
                new Objective("Single substrate for all future graph-walking features",
                        "Search index generation, live conformance checks, sitemap generation, skill bundle dumps — each becomes a small typed projection rather than its own walk implementation.")
        );
    }

    @Override public List<Decision> decisions() {
        return List.of(
                new Decision("D1",
                        "Where does StudioGraph live in the module tree?",
                        "homing-studio-base — same module as Bootstrap; the query primitives don't need anything outside the base module.",
                        "homing-studio-base",
                        DecisionStatus.RESOLVED,
                        "Per RFC 0014 D8. Keeps the typed primitive co-located with the Bootstrap record it's reachable from.",
                        ""),
                new Decision("D2",
                        "Eager or lazy construction?",
                        "Eager — Bootstrap.graph() builds the registries and returns a complete StudioGraph immediately.",
                        "Eager",
                        DecisionStatus.RESOLVED,
                        "Per RFC 0014 §6.2. Predictable behaviour; framework size is bounded so memory isn't a concern. Avoids ambiguity about when registry construction errors surface.",
                        ""),
                new Decision("D3",
                        "Stream-based vs Collection-based query API?",
                        "Collection-based (Set<>) for bulk queries; Optional<X> for point lookups. "
                          + "Reversed from the original Stream-based decision during Phase 1 implementation "
                          + "after a code-review observation: a public Stream return is a lazy/single-use proxy "
                          + "that hides size, prevents re-iteration, and resists toString-debugging. A Set "
                          + "(or List/Map) is concrete, materialised, and tells the caller the truth about "
                          + "what it carries.",
                        "Set-based (Explicit over Implicit doctrine)",
                        DecisionStatus.RESOLVED,
                        "The reversal motivated filing the Explicit over Implicit doctrine "
                          + "(homing-studio/.../doctrines/ExplicitOverImplicitDoc.md). Internal pipelines "
                          + "still compose with streams freely; the boundary where data leaves a typed "
                          + "primitive is where the explicit form takes over.",
                        ""),
                new Decision("D4",
                        "How does Bootstrap expose the graph?",
                        "Bootstrap.graph() instance method, parallel to Bootstrap.compose().",
                        "Instance method graph()",
                        DecisionStatus.RESOLVED,
                        "Per RFC 0014 D1. Doctrine-aligned (no static factory on a separate utility class).",
                        ""),
                new Decision("D5",
                        "Should reverse-citation be a separate cached index or computed on-demand?",
                        "On-demand for Phase 1 — small framework, fast walks. Add caching only if profiling shows a need.",
                        null,
                        DecisionStatus.OPEN,
                        "",
                        "Resolved in Phase 2 based on actual query patterns."),
                new Decision("D6",
                        "Should auto-generated diagrams live with the graph or in a separate module?",
                        "With the graph in homing-studio-base for Phase 3. Diagrams as separate types but co-located.",
                        null,
                        DecisionStatus.OPEN,
                        "",
                        "Resolved in Phase 3 when implementing CatalogueTree."),
                new Decision("D7",
                        "What's the public surface — Bootstrap.compose(), Bootstrap.graph(), or both?",
                        "Both — compose() for HTTP serving, graph() for programmatic introspection. They share internal registry-construction state but produce different return types.",
                        "Both methods, complementary purposes",
                        DecisionStatus.RESOLVED,
                        "Per RFC 0014 §2.5. ActionRegistry serves HTTP; StudioGraph queries the typed graph. Separate concerns.",
                        ""),
                new Decision("D8",
                        "How do downstream studios extend the graph for their own typed doc kinds?",
                        "Automatic via interface inheritance. graph.docsOfKind(Class<D> kind) accepts any kind class extending Doc; downstream-added kinds work without framework changes.",
                        "Automatic via inheritance",
                        DecisionStatus.RESOLVED,
                        "Per RFC 0014 D12. Builds on the open-set-of-kinds property the typed doc DSL plan also relies on.",
                        ""),
                new Decision("D9",
                        "Does the graph include themes?",
                        "Not in Phase 1. ThemeRegistry is its own typed surface; integration is a follow-up.",
                        null,
                        DecisionStatus.OPEN,
                        "",
                        "Resolved in a future phase when a feature needs theme-set queries."),
                new Decision("D11",
                        "How are graph visualisations registered and discovered?",
                        "Typed extension point — non-sealed StudioGraphView interface (StatelessFunctionalObject) "
                          + "with slug() / label() / summary() / urlFor(rootFqn) plus default scopable() + global() "
                          + "predicates. Framework ships TreeView + TypesView records as built-ins. "
                          + "Registration via Fixtures.graphViews() default method — downstream overrides to add "
                          + "their own (Mermaid 2D, force-layout 2D, three.js 3D, etc.). DiagnosticsHub iterates "
                          + "the list to emit tiles; no switch on view kinds, no per-view logic in the hub. "
                          + "Adding a new view is one new record class — no framework code changes. "
                          + "Note: the existing StudioGraphView enum (markdown content mode) should be renamed "
                          + "to MarkdownGraphMode or similar to free the better name for the interface.",
                        "Non-sealed interface + Fixtures.graphViews() registry",
                        DecisionStatus.RESOLVED,
                        "Designed but not yet implemented (see Phase P1c). Matches the framework's existing "
                          + "extension-point conventions (Studio, Catalogue, Doc, Plan). Open/Closed by construction.",
                        ""),
                new Decision("D10",
                        "Should the graph be persistable / replayable across processes?",
                        "No — per Stateless Server doctrine, the graph is per-JVM and built at boot.",
                        "Per-JVM, no persistence",
                        DecisionStatus.RESOLVED,
                        "Per RFC 0014 D10. Persistence is out of scope; consumers that want cross-process state add their own.",
                        "")
        );
    }

    @Override public List<Phase> phases() {
        return List.of(
                new Phase("P1", "Foundation — StudioGraph record + Bootstrap.graph()",
                        "Define the typed StudioGraph record (vertices + typed edges) and expose it via Bootstrap.graph().",
                        "Shipped as a typed object graph: vertices are the framework objects themselves "
                        + "(Docs, Catalogues, Plans, etc.); edges are typed (CONTAINS | REFERENCES) with an "
                        + "optional label. Built eagerly by StudioGraphBuilder from the composed Bootstrap via "
                        + "sealed-switch dispatch over Umbrella + Entry types. Query primitives return Set<> "
                        + "(not Stream — see D3 reversal during implementation). Diagnostic dump() prints an "
                        + "indented text tree with ontology emojis derived from the jOntology marker chain.",
                        PhaseStatus.DONE,
                        List.of(
                                new Task("Define StudioGraph record in homing-studio-base (vertices: Set<Object>, edges: Set<Edge>, Kind enum)", true),
                                new Task("StudioGraphBuilder — typed walk over Umbrella + Catalogue + Entry families", true),
                                new Task("Bootstrap.graph() instance method", true),
                                new Task("Query primitives — children / parents / referencesFrom / referencedBy / verticesOfType (Set-returning per Explicit over Implicit doctrine)", true),
                                new Task("Diagnostic dump(root) — indented tree with ontology emoji per vertex", true),
                                new Task("Markdown projections — dumpMarkdown(root) and dumpTypesMarkdown() (type roll-up; ❓-unmarked sorted first as code-quality gauge)", true),
                                new Task("Marked all StudioGraph types via jOntology markers", true)
                        ),
                        List.<Dependency>of(),
                        "StudioGraph constructs without throwing from the multi-studio demo Bootstrap; all queries return non-empty collections; dump() output is human-readable; dumpMarkdown / dumpTypesMarkdown survive marked.js rendering on the front-end.",
                        "Phase 1 is purely additive — rollback is removing StudioGraph + the new Bootstrap.graph() method; the existing compose() pipeline keeps working.",
                        "M",
                        "Open decisions D5 and D6 still apply to later phases; D3 was reversed mid-implementation and motivated the Explicit over Implicit doctrine."),

                new Phase("P1b", "Diagnostic UI surfaces",
                        "Make the live graph visible in the running UI, gated by RuntimeParams.diagnosticsEnabled().",
                        "Front-end-rendered AppModule reading server-emitted markdown. Two views (Object Graph tree, "
                        + "Type View table). Surfaces as a top-level Diagnostics catalogue page that the framework "
                        + "injects into the brand's home L0 + (in multi-studio compositions) projects per-studio "
                        + "via a context-scoped variant of the same catalogue class. Generalises the injection as "
                        + "a reusable CatalogueAugmentation mechanism (see D11) — downstream studios get the "
                        + "Diagnostics tile for free when they launch with -Dhoming.diagnostics=true; no studio "
                        + "code is required.",
                        PhaseStatus.DONE,
                        List.of(
                                new Task("StudioGraphMarkdownAction + MarkdownContent — /graph-md(?root=&view=)", true),
                                new Task("StudioGraphInspector AppModule + StudioGraphInspectorRenderer (Java DomModule + JS) — fetches /graph-md, parses with bundled marked.js", true),
                                new Task("Typed StudioGraphView enum (TREE | TYPES) — replaced raw String view parameter", true),
                                new Task("DiagnosticsCatalogue L0 (empty leaves — content comes from augmentation)", true),
                                new Task("SyntheticEntry + CatalogueAugmentation(replace, entries) + AugKey(class, context) — framework-managed JSON-boundary tile injection", true),
                                new Task("CatalogueGetAction.Query and CatalogueAppHost.Params extended with optional context; CatalogueHostRenderer.js forwards it to the JSON endpoint", true),
                                new Task("DiagnosticsHub — 3-tier injection (home-L0 Diagnostics tile / per-studio parent tiles / per-studio Object Graph + Type View)", true),
                                new Task("Bootstrap.compose() conditional wiring; absent surface when flag is off", true)
                        ),
                        List.of(new Dependency("P1", "Needs the StudioGraph + dumpMarkdown / dumpTypesMarkdown projections")),
                        "With -Dhoming.diagnostics=true: home page shows the Diagnostics tile; Diagnostics page shows per-studio parent tiles (multi-studio) or view tiles directly (single-studio); per-studio context page shows Object Graph + Type View scoped to that studio. With the flag off: zero surface (no app, no endpoints, no tile).",
                        "Phase additive; rollback is removing DiagnosticsCatalogue / DiagnosticsHub / SyntheticEntry / CatalogueAugmentation + reverting Query / Params / serialize / renderer changes (the augmentation map type is empty by default so callers that don't pass one are unaffected).",
                        "M",
                        "Carry-over: scoped Type View currently ignores root (shows global vertex set); breadcrumb on context-scoped page doesn't yet indicate which studio. Both queued as small follow-ups."),

                new Phase("P1c", "Extension point — StudioGraphView interface + Fixtures.graphViews()",
                        "Install the typed extension point so future 2D / 3D / interactive visualisations "
                          + "plug in without touching framework code.",
                        "Refactor the current hardcoded TREE/TYPES pair in DiagnosticsHub into a non-sealed "
                          + "StudioGraphView interface (StatelessFunctionalObject — slug + label + summary + "
                          + "urlFor(rootFqn), with default scopable() and global() predicates). Ship two "
                          + "framework-default record implementations (TreeView, TypesView). Rename the existing "
                          + "StudioGraphView enum (the markdown content mode) to MarkdownGraphMode to free the "
                          + "name. Add Fixtures.graphViews() default returning the framework defaults; downstream "
                          + "overrides to add custom views. DiagnosticsHub becomes a stream over the list — no "
                          + "switch on view kinds. Bootstrap passes fixtures.graphViews() into the hub.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Rename existing StudioGraphView enum to MarkdownGraphMode (compile-driven, ~3 sites)", false),
                                new Task("Define StudioGraphView interface in homing-studio-base graph package", false),
                                new Task("TreeView + TypesView records implementing the interface", false),
                                new Task("Fixtures.graphViews() default method", false),
                                new Task("DiagnosticsHub iterates fixtures.graphViews() instead of hardcoded tile list", false),
                                new Task("Bootstrap wires fixtures.graphViews() through to DiagnosticsHub constructor", false),
                                new Task("Verify no behavioural change — same two tiles per studio + globally", false)
                        ),
                        List.of(new Dependency("P1b", "Builds on the diagnostic UI surface")),
                        "Behavioural parity: with -Dhoming.diagnostics=true, the page renders identically to today. Architectural parity: adding a stub no-op view to Fixtures.graphViews() surfaces a third tile with no other code changes.",
                        "Phase additive at the protocol level; rollback is reverting to the hardcoded pair.",
                        "S",
                        "Pure refactor preparing the seam. Land before any concrete 2D/3D view to avoid migrating call sites twice."),

                new Phase("P1d", "Concrete view — Mermaid 2D (instance graph + type graph)",
                        "First non-default StudioGraphView implementation — Mermaid flowchart for both the "
                          + "instance graph and the type-with-cross-dependencies graph.",
                        "Produce a Mermaid flowchart block from the StudioGraph: nodes labelled with ontology "
                          + "emoji + class name; edges styled by Kind (CONTAINS solid, REFERENCES dashed with "
                          + "label). Two view records: MermaidInstanceView (vertices + all edges from a root) "
                          + "and MermaidTypeView (vertices projected to classes, edges projected and deduped). "
                          + "Bundle mermaid.js in homing-libs (single ESM file, same shape as the existing "
                          + "marked.js bundle). Extend the renderer JS to post-process language-mermaid code "
                          + "blocks via mermaid.run(). Type view defaults to references-only (CONTAINS at type "
                          + "level is mostly noise — every Studio contains Catalogues, well-known structurally).",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("StudioGraph.toMermaidInstance(root) + toMermaidType(rootOrNull) projections", false),
                                new Task("Bundle mermaid.js in homing-libs (MarkedJs sibling pattern)", false),
                                new Task("MermaidInstanceView + MermaidTypeView records", false),
                                new Task("Renderer JS — mermaid.run() over language-mermaid blocks after marked.parse()", false),
                                new Task("Default type-view edge filter — references-only with toggle", false),
                                new Task("Watchpoints — hide-foreign-types affordance, value-record equals-collision tooltip", false)
                        ),
                        List.of(
                                new Dependency("P1c", "Needs the extension point"),
                                new Dependency("P1",  "Type-edge projection builds on the graph's Edge model")
                        ),
                        "Two new tiles appear on each studio's diagnostics page (Instance Graph, Type Graph) when downstream registers them. Mermaid renders readable diagrams for the demo bootstrap; references-only type view exposes a real architectural relationship (e.g. cross-tree hosting).",
                        "Rollback removes the two view records + the mermaid.js bundle; the extension point machinery is unaffected.",
                        "M",
                        "Mermaid is the right first concrete view — text-based, low bundle cost, readable up to ~80 nodes. Force-layout / 3D land after this proves the seam."),

                new Phase("P1e", "Concrete view — 3D object graph (the OOP showcase)",
                        "Render the live object graph as actual 3D objects in space — pan / zoom / orbit / "
                          + "click-to-inspect. Object-Oriented Programming made literally visible.",
                        "Each vertex becomes a 3D shape in space — shape and colour derived from jOntology "
                          + "classification (⚡ StatelessFunctionalObject → glowing sphere; 💎 ValueObject → "
                          + "crystal; 🔄 Mutable → red marker; ❓ unmarked → grey question; etc.). Edges become "
                          + "lines / curves coloured by Kind. Force-directed 3D layout (three.js already "
                          + "bundled in homing-libs) keeps related vertices clustered. Click a node → side "
                          + "panel showing class FQN, package, ontology markers, outgoing edges, incoming "
                          + "edges. Filter chips for ontology kind, edge kind, package. This is the artefact "
                          + "that makes 'your software is a graph of objects with relationships' literal "
                          + "rather than metaphorical — and a great teaching surface for what OOP actually is.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("JSON projection of StudioGraph — /graph-json with same root/view params shape as /graph-md", false),
                                new Task("Force3DView record implementing StudioGraphView", false),
                                new Task("New AppModule (StudioGraph3DInspector) — different renderer from the markdown inspector", false),
                                new Task("three.js scene — node geometry per ontology marker, edge lines per Kind, force-directed 3D layout", false),
                                new Task("Click-to-inspect side panel — vertex details, incoming / outgoing edges, neighbour navigation", false),
                                new Task("Filter chips — ontology kind, edge kind, package", false),
                                new Task("Performance budget — handle the multi-studio demo graph (~hundreds of vertices) smoothly", false)
                        ),
                        List.of(
                                new Dependency("P1c", "Needs the extension point so it slots in as a peer view"),
                                new Dependency("P1",  "Walks the existing StudioGraph data")
                        ),
                        "The 3D view ships as a new tile; the demo bootstrap renders smoothly at interactive frame rates; ontology classification is visually obvious; clicking a node reveals its relationships; the result reads as a credible teaching artefact for what OOP is.",
                        "Rollback removes Force3DView + StudioGraph3DInspector + the JSON endpoint; markdown views unaffected.",
                        "L",
                        "Headline-feature scope. Worth its own RFC when started — UX decisions (geometry vocabulary, interaction model, performance budget) deserve writing-down. Treat as the showcase phase."),

                new Phase("P2", "Reverse-citation + cross-tree breadcrumb queries",
                        "Add reverse-citation (docs citing a target) and cross-tree breadcrumb derivation.",
                        "Phase 2 adds the reverse-navigation primitives: docsCiting(Doc target) → List<Doc>, "
                        + "and full-chain breadcrumb queries through StudioProxyManager for cross-tree cases. "
                        + "These mostly delegate to existing reverse-index registries; what's new is the "
                        + "unified API surface. Decide D5 (cached vs on-demand) based on Phase-1's query "
                        + "shape — likely on-demand is fine for current framework size.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Implement docsCiting(Doc) walking all references in reverse", false),
                                new Task("Verify cross-tree breadcrumb derivation through StudioProxyManager", false),
                                new Task("Resolve D5 — caching strategy", false),
                                new Task("Tests covering reverse-citation and multi-studio breadcrumb cases", false)
                        ),
                        List.of(new Dependency("P1", "Phase 1's StudioGraph + basic queries must exist")),
                        "docsCiting returns the correct set for a sample doctrine/RFC; cross-tree breadcrumbs span umbrella → category → source-studio L0 → leaf.",
                        "Phase 2 is additive — rollback is removing the new query methods.",
                        "S",
                        ""),

                new Phase("P3", "First auto-diagram — CatalogueTree",
                        "Realise RFC 0013 §5's deferred auto-generated catalogue diagram.",
                        "Add the CatalogueTree diagram type (typed record taking an L0_Catalogue<?> root and "
                        + "an optional StudioGraph for context). Implementation walks subCatalogues() "
                        + "recursively via the existing CatalogueClosure functional object. Renders as SVG "
                        + "client-side. Establishes the pattern other diagram types (PlanGraph, "
                        + "ReferenceGraph) will follow. Demonstrate by embedding new CatalogueTree("
                        + "StudioCatalogue.INSTANCE) in a real doc (likely the case-study or RFC currently "
                        + "describing the typed-everything stance).",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Define sealed Diagram sub-family with first permit (CatalogueTree)", false),
                                new Task("Implement CatalogueTree taking root + optional graph", false),
                                new Task("Client-side SVG renderer for catalogue trees", false),
                                new Task("Embed CatalogueTree in a real doc; verify rendering", false),
                                new Task("Resolve D6 — where diagrams live in module structure", false)
                        ),
                        List.of(new Dependency("P1", "Needs StudioGraph for graph-aware diagrams")),
                        "A typed doc embeds new CatalogueTree(StudioCatalogue.INSTANCE) and renders the live tree client-side; the diagram regenerates as the catalogue tree changes.",
                        "Phase 3 is opt-in per doc — rollback is removing the Diagram permits.",
                        "M",
                        "Realises the most-anticipated downstream affordance — embeddable, live, typed diagrams."),

                new Phase("P4", "Cross-doc programmability — typed-kind queries",
                        "Enable querying docs by their typed-DSL kind interface (Doctrine, RFC, CaseStudy, etc.).",
                        "Phase 4 depends on the Typed Doc DSL plan having defined kind interfaces (Doctrine, "
                        + "RFC, CaseStudy, BuildingBlock). When those exist, the graph's docsOfKind(Class) "
                        + "method becomes meaningfully typed — e.g. graph.docsOfKind(Doctrine.class) returns "
                        + "Stream<Doctrine>. This unlocks roll-up queries: every doctrine's Tensions across "
                        + "all studios; every case study's Compromises; every RFC in Proposed status. "
                        + "Without the DSL kinds, docsOfKind degenerates to Class-equality filter, which is "
                        + "less powerful.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Verify docsOfKind works for typed-DSL kind interfaces once they exist", false),
                                new Task("Demonstrate with a case study or doc that queries cross-kind", false),
                                new Task("Document the typed-DSL prerequisite", false)
                        ),
                        List.of(
                                new Dependency("P1", "Foundation"),
                                new Dependency("P2", "Reverse-citation helpful for cross-kind navigation")
                        ),
                        "A doc successfully renders content derived from a cross-doc query (e.g., embedded list of every doctrine's Tensions).",
                        "Phase 4 entries are per-feature; rollback is per-feature.",
                        "M",
                        "Trigger-gated by Typed Doc DSL plan's Phase 2 (kind interfaces). Don't start until those land."),

                new Phase("P5", "Live conformance / observability",
                        "Runtime health-report queries against the composed graph.",
                        "Add graph.healthReport() returning structured findings: docs with empty summaries, "
                        + "references not cited inline, orphan catalogues (subCatalogues unreached from any "
                        + "L0), plans with 0% progress + 0 open decisions, etc. Distinct from build-time "
                        + "StudioDocConformanceTest because it runs against the live composed Bootstrap — "
                        + "catches issues that only emerge in specific multi-studio deployments. "
                        + "Optionally expose as a typed HTTP action (/graph/health).",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Define HealthFinding typed record family", false),
                                new Task("Implement graph.healthReport() with initial checks", false),
                                new Task("Optionally add /graph/health action", false),
                                new Task("Document the live-vs-buildtime conformance distinction", false)
                        ),
                        List.of(new Dependency("P1", "Needs StudioGraph foundation")),
                        "Health report runs in <100 ms for the demo bootstrap; surfaces at least one actionable finding (e.g., a doc with an empty summary, if any exist).",
                        "Phase 5 is additive — rollback removes the health-report API.",
                        "S",
                        ""),

                new Phase("P6", "Search index / sitemap generation",
                        "Surface for SEO + browseable-index features.",
                        "Add graph.sitemap() returning a typed sitemap; graph.searchIndex() returning a flat-"
                        + "text index keyed by Doc UUID. Optionally expose as /sitemap.xml and "
                        + "/graph/search-index actions. Enables downstream studios to ship search and "
                        + "discoverability without authoring search-specific code.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Define Sitemap typed record", false),
                                new Task("Define SearchIndex typed record", false),
                                new Task("Implement graph.sitemap() + graph.searchIndex()", false),
                                new Task("Optionally add /sitemap.xml + /graph/search-index actions", false)
                        ),
                        List.of(new Dependency("P1", "Foundation")),
                        "Sitemap and search-index both produced from the demo bootstrap; sample entries match expected Doc URLs / titles.",
                        "Phase 6 is additive.",
                        "S",
                        ""),

                new Phase("P7", "Typed doc DSL integration",
                        "Wire StudioGraph into the typed doc DSL's computed-content phase.",
                        "When the Typed Doc DSL plan reaches its Phase 5 (computed / auto-generated "
                        + "content), the renderer needs a graph reference to resolve content like "
                        + "new CatalogueTree(StudioCatalogue.INSTANCE) or "
                        + "new TensionsOf(MyDoctrineDoc.INSTANCE). This phase wires the StudioGraph "
                        + "into the typed-doc-DSL renderer pipeline. Two-way dependency — the DSL plan's "
                        + "Phase 5 also lists this phase as a prerequisite.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Pass StudioGraph into typed-doc-DSL renderer pipeline", false),
                                new Task("Demonstrate via a typed doc with computed content", false),
                                new Task("Coordinate completion with Doc DSL plan Phase 5", false)
                        ),
                        List.of(
                                new Dependency("P3", "Diagram types established"),
                                new Dependency("P4", "Cross-doc query primitives established")
                        ),
                        "A typed doc successfully embeds computed content via the graph; the typed-doc-DSL renderer correctly resolves and renders it.",
                        "Phase 7 is the convergence point between this plan and the Doc DSL plan. Rollback per-feature.",
                        "M",
                        "Trigger-gated by Typed Doc DSL plan reaching its Phase 5 (computed content).")
        );
    }

    @Override public List<Acceptance> acceptance() {
        return List.of(
                new Acceptance("Foundation graph builds from demo Bootstrap",
                        "Bootstrap.graph() returns a non-null StudioGraph for the multi-studio demo Bootstrap; queries return non-empty Set results; dumpMarkdown + dumpTypesMarkdown produce well-formed markdown.",
                        true),
                new Acceptance("Diagnostic UI surfaces visible behind the flag",
                        "Launching with -Dhoming.diagnostics=true surfaces a Diagnostics tile on the home page; the Diagnostics page shows per-studio parent tiles in multi-studio mode or view tiles directly in single-studio mode; per-studio context page shows Object Graph + Type View rooted at that studio. With the flag off, none of these surfaces are reachable.",
                        true),
                new Acceptance("Reverse-citation queries work",
                        "graph.docsCiting(FunctionalObjectsDoc.INSTANCE) returns the docs that reference Functional Objects (RFC 0013, the privacy doctrines, etc.); cross-tree breadcrumbs span umbrella → category → source-studio L0 → leaf correctly.",
                        false),
                new Acceptance("First auto-diagram renders end-to-end",
                        "A typed doc embeds new CatalogueTree(StudioCatalogue.INSTANCE); the diagram renders correctly in the browser; the existing catalogue / breadcrumb / reference systems work around it without regression.",
                        false),
                new Acceptance("Cross-doc query produces useful output",
                        "At least one published doc renders content computed from a cross-doc query (e.g., embedded list of every doctrine's Tensions, or every RFC currently in Proposed status, or all reverse-citations of a key doctrine).",
                        false),
                new Acceptance("Health report runs against live Bootstrap",
                        "graph.healthReport() executes in <100 ms; surfaces meaningful findings about the live composed framework; documented as the runtime complement to StudioDocConformanceTest.",
                        false),
                new Acceptance("Search index + sitemap shipped",
                        "graph.sitemap() and graph.searchIndex() both produce valid output for the demo bootstrap; optionally exposed via /sitemap.xml and /graph/search-index actions; at least one downstream feature consumes them.",
                        false),
                new Acceptance("Typed-doc-DSL computed content integration",
                        "The Typed Doc DSL renderer reads from StudioGraph for computed content; at least one typed doc with embedded computed content renders correctly. This acceptance closes when both this plan's Phase 7 and the Doc DSL plan's Phase 5 are met.",
                        false)
        );
    }
}
