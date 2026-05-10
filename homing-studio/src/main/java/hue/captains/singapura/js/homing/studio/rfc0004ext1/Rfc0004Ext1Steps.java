package hue.captains.singapura.js.homing.studio.rfc0004ext1;

import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0004Ext1Doc;

import java.util.List;

/**
 * Implementation tracker for RFC 0004-ext1 — Managed Markdown References.
 *
 * <p>Source-of-truth for {@link Rfc0004Ext1Plan} and {@link Rfc0004Ext1Step} views.
 * Edit this file to update progress, resolve decisions, or revise phases.</p>
 *
 * <p>Companion document: {@link Rfc0004Ext1Doc} (typed reference, UUID-stable).</p>
 */
public final class Rfc0004Ext1Steps {

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

    /** Typed reference to RFC 0004-ext1's prose. UUID-stable per RFC 0004. */
    public static final String RFC_DOC = "ceaeafd3-b400-458f-9c3b-e3c7a161c3ff"; // Rfc0004Ext1Doc.ID

    // ------------------------------------------------------------------
    // RESOLVED DECISIONS — captured during the design conversation that
    // produced the RFC.
    // ------------------------------------------------------------------
    public static final List<Decision> DECISIONS = List.of(

            new Decision("D1",
                    "Anchor prefix shape — `#:foo`, `#ref-foo`, or `#ref:foo`?",
                    "`#ref:foo` — explicit prefix + collision-proof colon.",
                    "#ref:foo",
                    DecisionStatus.RESOLVED,
                    "User priority: correctness over brevity. Auto-slug generation only produces `[a-z0-9-]`, so a colon in the fragment guarantees no heading-slug can collide with a managed-reference anchor. The `ref` prefix word is also explicit-readable so a writer immediately knows the link is managed.",
                    "Captured in RFC §2.2 / §4.3."
            ),

            new Decision("D2",
                    "Conformance check direction — required only, or bidirectional?",
                    "Required only: every cited `#ref:KEY` must resolve. Don't require declared References to be cited.",
                    "required only",
                    DecisionStatus.RESOLVED,
                    "Generic 'further reading' references in the References section are valid even without inline citation. Forcing inline mention would constrain natural prose patterns.",
                    "Captured in RFC §2.4 / §4.4."
            ),

            new Decision("D3",
                    "Section title — 'See Also' or 'References'?",
                    "References.",
                    "References",
                    DecisionStatus.RESOLVED,
                    "'References' covers Doc cross-refs, external citations, and images uniformly. 'See Also' reads Doc-only.",
                    ""
            ),

            new Decision("D4",
                    "Unified `Reference` sealed interface vs. separate methods (`seeAlso()`, `externalRefs()`, `images()`)?",
                    "Unified sealed interface.",
                    "unified",
                    DecisionStatus.RESOLVED,
                    "One namespace for `#ref:KEY` resolution; one section render; one conformance test. Per-subtype rendering dispatches on instanceof in the renderer.",
                    "Captured in RFC §2.1 / §4.1."
            ),

            new Decision("D5",
                    "DOM walking / href substitution vs. native browser fragment navigation?",
                    "Native — emit a References section with stable `id=\"ref:KEY\"` elements; let the browser do the navigation.",
                    "native",
                    DecisionStatus.RESOLVED,
                    "The renderer's responsibility shrinks to 'concatenate two HTML sections.' No marked.js post-processing, no anchor walking, no URL substitution. The browser's native fragment navigation handles every cited reference for free.",
                    "Captured in RFC §2.3 / §4.2."
            ),

            new Decision("D6",
                    "ImageReference rendering in v1 — emit `<img>` immediately, or defer to a sibling RFC for the asset endpoint?",
                    "Defer image rendering. Declare ImageReference in v1 with a text-only placeholder (alt + caption + resourcePath). Sibling RFC adds `/asset` and `<img>` rendering.",
                    "defer rendering",
                    DecisionStatus.RESOLVED,
                    "Image serving needs an `/asset` endpoint that doesn't exist yet. No public doc currently has inline images, so deferring image rendering has zero migration impact while still validating the data shape.",
                    "Captured in RFC §2.3 / §4.6."
            ),

            new Decision("D7",
                    "Anchor key naming convention — free-form, prefix-by-kind (`doc-foo`/`ext-foo`), or class-derived (`pure-component-views-doc`)?",
                    "Free-form. Names are local to one Doc; the writer optimises for short and readable; conformance ensures resolution.",
                    "free-form",
                    DecisionStatus.RESOLVED,
                    "Per RFC §6 first bullet — leaning free-form. Confirmed during implementation: short names like `pcv` / `css-spec` read better than `doc-pcv` / `ext-css-spec`. The conformance check makes free-form safe.",
                    ""
            ),

            new Decision("D8",
                    "Render ordering of References section — declaration order or grouped by subtype?",
                    "Declaration order. The writer chooses the ordering by ordering `references()`.",
                    "declaration order",
                    DecisionStatus.RESOLVED,
                    "Per RFC §6 second bullet. Trivial decision — `List<Reference>` is naturally ordered. Grouping by subtype could be a presentational option later but isn't load-bearing.",
                    ""
            ),

            new Decision("D9",
                    "RFC frontmatter table — carve out exception, plain-text replacement, or promote to typed `RfcDoc` sub-interface?",
                    "Plain-text replacement. Restate the relation in a prose paragraph below the table that uses `#ref:<name>` syntax. No structural sub-interface needed.",
                    "plain-text + prose ref",
                    DecisionStatus.RESOLVED,
                    "Per RFC §6 third bullet. Smallest change; keeps the table as scannable metadata; the prose paragraph carries the citation through the typed `references()` mechanism.",
                    "Implementation-time policy: when migrating each RFC's Doc, strip URLs from the frontmatter table cells and add a 'Relations:' or similar paragraph below referencing each related doc via `#ref:<name>`."
            )
    );

    // ------------------------------------------------------------------
    // PHASES — execute in order, marking DONE as work completes.
    // ------------------------------------------------------------------
    public static final List<Phase> PHASES = List.of(

            new Phase("01",
                    "Framework — `Reference` sealed interface + 3 records + `Doc.references()`",
                    "Add the typed reference model; non-breaking additive change.",
                    "Added `Reference` sealed interface in `homing-studio-base`, with permits for `DocReference`, `ExternalReference`, `ImageReference`. Each subtype carries `name()` (the anchor key) plus subtype-specific data. Added `default List<Reference> references() { return List.of(); }` to `Doc`. No existing record changes — every Doc inherits the empty default.",
                    Status.DONE,
                    List.of(
                            new Task("Create `Reference` sealed interface — `name()` method, permits the three subtypes", true),
                            new Task("Create `DocReference(String name, Doc target)` record", true),
                            new Task("Create `ExternalReference(String name, String url, String label, String description)` record", true),
                            new Task("Create `ImageReference(String name, String resourcePath, String alt, String caption)` record", true),
                            new Task("Add `default List<Reference> references()` to `Doc`", true),
                            new Task("`mvn install -pl homing-studio-base` GREEN — additive, no behavioral change", true)
                    ),
                    List.of(),
                    "homing-studio-base compiles; new types reachable from downstream.",
                    "Delete the four new types + the default method. No coupling.",
                    "20 minutes",
                    "",
                    List.of()
            ),

            new Phase("02",
                    "Renderer — emit References section in `DocReader.selfContent()` + `DocReaderRenderer.js`",
                    "Page renders the markdown body unchanged plus a References section with stable in-page IDs.",
                    "References fetched client-side rather than embedded in the JS body — `selfContent()` doesn't know which Doc the user is viewing (that's `params.doc` at runtime), so server-side resolution isn't possible. Added `DocRefsGetAction` at `/doc-refs?id=<uuid>` returning the typed `references()` as JSON (with a `kind` discriminator per subtype). Wired into both `StudioBootstrap.buildRegistry` and `StudioActionRegistry`. `DocReaderRenderer.js` adds a `_renderReferences(refs, container)` function that fetches `/doc-refs?id=<uuid>` in parallel with the markdown fetch; emits one card per Reference inside a `<section>` (initially hidden, revealed when refs arrive), with `id=\"ref:<name>\"` per entry. Per-subtype dispatch: doc → linked title via `href.set(a, '/app?app=doc-reader&doc=<uuid>')`; external → linked label with `target=_blank rel=noopener`; image → text-only placeholder per D6.",
                    Status.DONE,
                    List.of(
                            new Task("Created `DocRefsGetAction` — UUID lookup → `Reference` JSON serialization (3 subtype branches)", true),
                            new Task("Wired `/doc-refs` route in `StudioBootstrap.buildRegistry` and `StudioActionRegistry`", true),
                            new Task("Added `_renderReferences(refs, container)` to `DocReaderRenderer.js` — per-subtype dispatch on JSON `kind`", true),
                            new Task("DocReference render: title (linked via `href.set` to `/app?app=doc-reader&doc=<uuid>`) + summary paragraph", true),
                            new Task("ExternalReference render: label (linked, `target=_blank rel=noopener`) + description paragraph", true),
                            new Task("ImageReference render: alt + caption + resourcePath as text (image rendering deferred per D6)", true),
                            new Task("Section heading: `<h2>References</h2>`", true),
                            new Task("Section initially `display:none`, revealed when refs arrive (omitted entirely when refs list is empty)", true),
                            new Task("References imports added to `DocReaderRenderer.imports()`: st_section, st_section_title, st_card, st_card_title, st_card_summary, st_card_link", true),
                            new Task("`mvn install` GREEN; all 108 tests still pass", true)
                    ),
                    List.of(new Dependency("01", "References model must exist.")),
                    "DocReader page renders the References section; in-page anchor navigation works for cited references.",
                    "Revert DocReader.selfContent + DocReaderRenderer.js. References section disappears; pages render markdown only.",
                    "45 minutes",
                    "",
                    List.of()
            ),

            new Phase("03",
                    "Conformance — extend `DocConformanceTest` with markdown reference scan",
                    "Per-Doc dynamic test: every `#ref:KEY` resolves to a declared Reference; no out-of-document URL escapes.",
                    "Extended `DocConformanceTest`'s factory with a per-`.md`-Doc reference scan. Parses `contents()` for markdown links via regex; classifies each URL by prefix (`#ref:KEY` → must resolve in `references()`; `#anchor` → allowed; anything else → fail). Strips fenced code blocks AND inline-code spans before scanning so the RFC's own example syntax doesn't trigger violations. Failure messages point at the typed-Reference migration shape (for out-of-document links) or the missing declaration (for unresolved `#ref:`). Initial run captured 21 docs with violations totalling ~92 links to migrate.",
                    Status.DONE,
                    List.of(
                            new Task("Extended `DocConformanceTest` factory with per-markdown-Doc reference scan", true),
                            new Task("Regex `\\[([^\\]]*)\\]\\(([^)]+)\\)` to extract every link", true),
                            new Task("Classify `#ref:KEY` → look up in declared References; `#anchor` → skip; other → fail", true),
                            new Task("Failure message for unmanaged URL points at the typed-Reference migration shape", true),
                            new Task("Failure message for unresolved `#ref:` points at the typo or missing Reference declaration", true),
                            new Task("Strip fenced code blocks before scanning (the RFC's own examples must not trigger violations)", true),
                            new Task("Strip inline-code spans (single backticks) — covers table cells with `[label](url)` examples", true),
                            new Task("`StudioDocConformanceTest` runs — captured initial violation list of ~21 failing docs / ~92 links", true)
                    ),
                    List.of(new Dependency("01", "References declarations are the lookup target.")),
                    "Conformance test produces a per-Doc violation list ready for the migration sweep.",
                    "Revert the conformance extension; tests go back to UUID + contents checks only.",
                    "30 minutes",
                    "Conformance is RED at the end of this phase by design — Phase 04 turns it green.",
                    List.of()
            ),

            new Phase("04",
                    "Migration sweep — declare `references()` + replace inline links across ~18 docs",
                    "Mechanical pass over every public Doc with category-A or B links.",
                    "Mechanical pass over every public Doc with category-A or B links. Bulk sed handled the predictable cross-doc patterns (e.g. `](../doctrines/foo.md)` → `](#ref:short)`). Per-Doc Edit calls added `references()` overrides declaring the typed targets. Brand SVG asset links became ImageReference declarations (image rendering deferred per RFC 0004-ext1 §4.6 — text-only placeholder for now). Private-doc links (ACTION-PLAN, SESSION-SUMMARY, brochure/) and folder-style references (../doctrines/, .) were demoted to plain text since they have no public Doc record on the classpath.",
                    Status.DONE,
                    List.of(
                            new Task("Block docs (6 files): cross-link AtomsDoc / CatalogueKitDoc / DocKitsDoc / TrackerKitDoc / BootstrapAndConformanceDoc / BlocksIndexDoc via DocReferences", true),
                            new Task("RFC docs (5 files): each RFC declares References to defects/doctrines/sibling RFCs it cites", true),
                            new Task("Doctrine docs (4 files): pairwise DocReferences + pointers to RFC 0003", true),
                            new Task("Defect docs (1 file with refs — Defect 0001 had none): DocReference to Rfc0003Doc", true),
                            new Task("Whitepaper docs (2 files): one ExternalReference (Picocli); private-doc links stripped to plain text", true),
                            new Task("Brand docs (2 files): cross-link BrandReadmeDoc ↔ RenameToHomingDoc; 8 SVG ImageReferences declared (rendering deferred)", true),
                            new Task("Guides + Rename (3 files): DocReferences to relevant RFCs / defects; deep cross-doc anchors degraded to top-of-doc references", true),
                            new Task("Run `StudioDocConformanceTest` — GREEN; 134/134 tests pass (was 108 before the RFC; +26 new dynamic ref-scan tests)", true)
                    ),
                    List.of(
                            new Dependency("02", "Renderer must already display the References section so manual smoke shows the result."),
                            new Dependency("03", "Conformance defines the precise violation list to fix.")
                    ),
                    "All 19 public Docs have declared References; conformance test green.",
                    "Per-Doc revert via git; each Doc edit is independent.",
                    "2 hours (mechanical, ~5–8 min per doc)",
                    "",
                    List.of()
            ),

            new Phase("05",
                    "Documentation — `DocKitsDoc.md` describes `references()` + `#ref:<name>` syntax",
                    "Kit reference doc updated to describe the managed-reference flow.",
                    "Updated `DocKitsDoc.md` with a 'Managed References (RFC 0004-ext1)' section describing `Doc.references()`, the three Reference subtypes (with example code), the `#ref:<name>` markdown syntax, and the conformance gate. Cross-references the typed-doc design back to RFC 0004 via `#ref:rfc-4`.",
                    Status.DONE,
                    List.of(
                            new Task("Added 'Managed References (RFC 0004-ext1)' section to `DocKitsDoc.md`", true),
                            new Task("Documented the three Reference subtypes with example code", true),
                            new Task("Documented the `#ref:<name>` markdown syntax", true),
                            new Task("Updated 'See also' to mention the conformance gate's extended scope", true)
                    ),
                    List.of(new Dependency("04", "References mechanism is fully migrated before documenting it.")),
                    "DocKitsDoc renders correctly; its References section shows the cross-link to RFC 0004-ext1.",
                    "Revert the doc edit.",
                    "20 minutes",
                    "",
                    List.of()
            ),

            new Phase("06",
                    "Tracker recursion — declare this tracker's own references via the new mechanism",
                    "Capture the recursive proof: the tracker for RFC 0004-ext1 itself uses managed References for any cross-links from its own RFC document.",
                    "Recursion confirmed. `Rfc0004Ext1Doc.references()` is populated with DocReferences to PureComponentViewsDoc + Defect0002Doc and one ExternalReference to the W3C CSS spec — every cited `#ref:` in its own .md resolves to a typed declaration. The tracker's footer link (RFC_DOC) points at Rfc0004Ext1Doc by UUID; the rendered page emits the References section beneath the body. All 134 studio tests pass; build green across all 8 modules.",
                    Status.DONE,
                    List.of(
                            new Task("Rfc0004Ext1Doc.references() declares all citations: pcv, def-2, css-spec", true),
                            new Task("Conformance scan green for Rfc0004Ext1Doc — no out-of-document URLs, every `#ref:` resolves", true),
                            new Task("All 134 studio tests GREEN after the full landing", true)
                    ),
                    List.of(new Dependency("05", "Documentation lands; the recursion confirms the model works for its own RFC.")),
                    "Tracker links to RFC 0004-ext1; RFC 0004-ext1 References section links to Rfc0004Doc and the cited Docs; conformance green; all tests pass.",
                    "Revert per-Doc.",
                    "10 minutes (mostly verification)",
                    "Same recursion-proof shape as RFC 0004's tracker. If the typed-Reference mechanism works for the document defining it, it works for everything.",
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

    private Rfc0004Ext1Steps() {}
}
