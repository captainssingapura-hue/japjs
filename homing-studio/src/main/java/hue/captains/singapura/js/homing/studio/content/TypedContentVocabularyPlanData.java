package hue.captains.singapura.js.homing.studio.content;

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
 * Single multi-phase plan covering the Typed Content Vocabulary trio
 * (RFC 0018 — Slim Markdown, RFC 0019 — ComposedDoc, RFC 0020 — Visual
 * Asset Docs) and the closing self-proof case study.
 *
 * <p>Slogan: <em>"You don't really need HTML, just SVGs."</em></p>
 *
 * <p>The arc: ComposedDoc (PoC with md + SVG) → visual asset Docs
 * (TableDoc + ImageDoc) → segment ADT extended to wrap them → .mdad
 * format + conformance scanner → case study authored in the new
 * vocabulary (self-proof) → optional retrofits of high-value existing
 * docs. Each phase is independently shippable.</p>
 */
public final class TypedContentVocabularyPlanData implements Plan {

    public static final TypedContentVocabularyPlanData INSTANCE = new TypedContentVocabularyPlanData();

    private TypedContentVocabularyPlanData() {}

    @Override public String kicker()   { return "TYPED CONTENT"; }
    @Override public String name()     { return "Typed Content Vocabulary"; }
    @Override public String subtitle() {
        return "\"You don't really need HTML, just SVGs.\" Replace the markdown-with-inline-HTML "
             + "escape pattern with typed primitives: ComposedDoc (segments) + visual asset Docs "
             + "(SvgDoc / TableDoc / ImageDoc) + slim disciplined markdown (.mdad). Tracks RFC "
             + "0018 / 0019 / 0020 as one execution arc; closes with a self-proof case study "
             + "authored entirely in the new vocabulary.";
    }
    @Override public String summary() {
        return "Single arc realising the Typed Content Vocabulary doctrine across three RFCs. "
             + "PoC-first (ComposedDoc with markdown + SVG) then extends to visual assets, then "
             + "tightens markdown discipline, then publishes the self-proof case study.";
    }

    @Override public List<Objective> objectives() {
        return List.of(
                new Objective("Close the HTML escape hatch in framework-shipped content",
                        "Today authors reach for inline HTML when markdown can't express a construct (complex tables, themable diagrams, captioned images). Each escape fragments the framework's discipline. After this plan, every construct has a typed alternative; HTML is structurally unnecessary."),
                new Objective("Make ComposedDoc the new default Doc kind",
                        "Most tech docs benefit from at least one diagram. ProseDoc becomes a degenerate case (composed-with-only-markdown). New docs default to ComposedDoc with a sequence of typed segments."),
                new Objective("Promote visuals to first-class Docs",
                        "SvgDoc, TableDoc, ImageDoc are registered, citable, addressable Docs even when their primary use is inline in a ComposedDoc segment. Segments are proxies (ProxyDoc pattern from RFC 0015) — fresh identity per appearance, canonical content registered once."),
                new Objective("Tighten the markdown surface to .mdad",
                        "A disciplined markdown subset (conformance-enforced) closes the inline-HTML escape at the language level. Authors can't sneak HTML in via MarkdownSegment bodies; the discipline is structural, not aspirational."),
                new Objective("Self-prove with a case study",
                        "The closing milestone is a case study \"Why We Ditched HTML\" authored as a ComposedDoc with markdown + SVG segments. The case study can only exist if the vocabulary works; if you can read it, the vocabulary worked.")
        );
    }

    @Override public List<Decision> decisions() {
        return List.of(
                new Decision("D1",
                        "ComposedDoc PoC scope — which segment kinds first?",
                        "MarkdownSegment + SvgSegment only. Defers TableSegment / ImageSegment until P2/P3. Validates the segment ADT, the viewer, and the proxy pattern (SvgSegment wraps SvgDoc) before adding more variants.",
                        "Markdown + SVG only",
                        DecisionStatus.RESOLVED,
                        "Smallest validating slice — exercises the segment family and the visual-proxy pattern with one new Doc kind (ComposedDoc) and one existing visual Doc (SvgDoc).",
                        ""),
                new Decision("D2",
                        "Stay with marked.js for MarkdownSegment, or write a slim custom renderer?",
                        "marked.js for v1; consider custom renderer in a later phase if .mdad's surface is narrow enough that the bundle weight becomes the dominant cost.",
                        "marked.js for v1",
                        DecisionStatus.RESOLVED,
                        "Reuses existing pipeline; zero new JS surface for v1. The .mdad conformance scanner enforces the subset on the Java side; the renderer can stay permissive.",
                        ""),
                new Decision("D3",
                        "Server-side TOC vs client-side scroll-spy?",
                        "Server-side for ComposedDoc — segment captions + .mdad heading extraction in Java. Legacy ProseDocs keep client-side scroll-spy.",
                        "Server-side for ComposedDoc",
                        DecisionStatus.RESOLVED,
                        "Constrained .mdad format makes server-side heading extraction reliable. Single source of truth; consistent across themes; no DOM-walking heuristics.",
                        ""),
                new Decision("D4",
                        "Drop Mermaid as the diagram path?",
                        "Yes — direct SVG (hand-authored or framework-computed) replaces Mermaid. Quality, themability, and bundle weight all favour direct SVG. Affects RFC 0014 P1d (now reframed as \"computed SVG StudioGraph view\").",
                        "Direct SVG only; no Mermaid",
                        DecisionStatus.RESOLVED,
                        "Mermaid's auto-layout produces uneven results, its theming doesn't compose with RFC 0017 tokens, and ~150KB bundle is significant. Direct SVG is more author work but for documentation diagrams (written rarely, read often), the quality trade is right.",
                        ""),
                new Decision("D5",
                        "Cross-segment references — supported?",
                        "No. Segments are self-contained. Citation works across segments because it targets Docs (not segments); shared context inside the composed doc is out of scope forever.",
                        "No cross-segment refs",
                        DecisionStatus.RESOLVED,
                        "Discipline holds. A TableSegment that needs context from an earlier MarkdownSegment indicates the doc should be restructured.",
                        ""),
                new Decision("D6",
                        "Slim Segment ADT or pluggable Segment with metadata?",
                        "Pure ADT — sealed interface; each segment record carries its own typed fields; no common base; dispatch via exhaustive switch.",
                        "Pure ADT, no common base",
                        DecisionStatus.RESOLVED,
                        "Each segment kind's metadata is genuinely different. Factoring out a common base would over-constrain future kinds. Sealed permits gives compile-checked dispatch; that's enough discipline.",
                        ""),
                new Decision("D7",
                        "Visual segments — inline content or proxy references?",
                        "Proxy references. Every SvgSegment / TableSegment / ImageSegment wraps a registered Doc (SvgDoc / TableDoc / ImageDoc) plus optional per-appearance caption override. Canonical content lives once; appearances are framings.",
                        "Proxy references to canonical Docs",
                        DecisionStatus.RESOLVED,
                        "Generalises the ProxyDoc pattern from RFC 0015 §2.5. Makes visuals citable independently, allows multi-home naturally, composes with the existing harvest machinery.",
                        ""),
                new Decision("D8",
                        "TableDoc — what's the slim line?",
                        "Tables, not spreadsheets. Headers / rows / cells / colspan-rowspan / alignment / token-derived cell badges. No formulas; no sort/filter interactivity; no editing; no multi-sheet. JSON for rich, CSV for lazy.",
                        "Slim: tables, not spreadsheets",
                        DecisionStatus.RESOLVED,
                        "Each excluded feature (formulas, interactivity) sounds reasonable individually; the cumulative effect is rebuilding Excel. The slim discipline holds the line.",
                        ""),
                new Decision("D9",
                        "ComposedDoc vs ProseDoc — which is the new default?",
                        "ComposedDoc is the new default for any doc that benefits from at least one non-prose segment. ProseDoc remains as a degenerate convenience for pure-prose docs.",
                        "ComposedDoc is the new default",
                        DecisionStatus.RESOLVED,
                        "Most tech docs benefit from a diagram. Making composed the default makes diagrams the norm, not a special case.",
                        ""),
                new Decision("D10",
                        "Legacy .md files — forced migration or gradual?",
                        "Gradual. Existing .md keeps working with the current permissive renderer. New ProseDocs and all MarkdownSegments use .mdad. Soft-mode scanner over .md files emits migration suggestions without failing the build.",
                        "Gradual; no forced migration",
                        DecisionStatus.RESOLVED,
                        "~50 existing .md files in the studio. Forced migration is high-risk for no immediate gain. Gradual lets the conformance scanner enforce on new content while old content migrates organically.",
                        "")
        );
    }

    @Override public List<Phase> phases() {
        return List.of(
                new Phase("P1", "ComposedDoc PoC — markdown + SVG segments only",
                        "Smallest validating slice of RFC 0019. Segment ADT with two permits; ComposedViewer extending DocViewer; one demo ComposedDoc.",
                        "Create ComposedDoc record, the Segment sealed interface with MarkdownSegment + SvgSegment "
                        + "permits, ComposedViewer extending the typed DocViewer base, server-side TOC builder "
                        + "(segment captions + .md heading extraction), JSON body serialisation, ComposedContentViewer "
                        + "registration in Fixtures.contentViewers(). Demo: convert one existing doc to ComposedDoc "
                        + "or build a new one (likely a small smoke-test doc that proves the segment dispatch + chrome).",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Define Segment sealed interface + MarkdownSegment + SvgSegment records", false),
                                new Task("Define ComposedDoc record implementing Doc; kind=\"composed\"", false),
                                new Task("Build ComposedViewer extending DocViewer<Params, ComposedViewer>", false),
                                new Task("JSON serialisation for the segment list + TOC bundled into Doc.contents()", false),
                                new Task("Server-side TOC builder (segment captions + markdown heading extraction)", false),
                                new Task("Register ComposedContentViewer in Fixtures.contentViewers()", false),
                                new Task("Harvest ComposedDocs in DocRegistry.harvestSyntheticFromLeaves", false),
                                new Task("Demo: one ComposedDoc registered in homing-studio + verified visually", false)
                        ),
                        List.<Dependency>of(),
                        "ComposedViewer renders a ComposedDoc with markdown + SVG segments in a single flowing page; framework chrome (header, breadcrumb, theme, audio) inherited via DocViewer base; TOC appears alongside content; theme switching applies to both prose and SVG.",
                        "Purely additive; rollback removes the new types. No existing Doc kind affected.",
                        "M",
                        "PoC scope — TableSegment / ImageSegment / .mdad strictness all deferred."),

                new Phase("P2", "Visual asset Docs — TableDoc + ImageDoc",
                        "Realise RFC 0020. Two new first-class Doc kinds + viewers + ContentViewer registrations.",
                        "TableDoc record + JSON shape + CSV ingestion + slim discipline. TableViewer extending "
                        + "DocViewer; framework-themed table CSS (st_table, st_thead, st_th, st_td, st_td_align_*, "
                        + "st_td_badge_*). ImageDoc record (resource path, alt, caption, optional dimensions). "
                        + "ImageViewer extending DocViewer. Both ContentViewers registered in Fixtures.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("TableDoc record + TableData JSON record + CSV parser", false),
                                new Task("TableViewer extending DocViewer; bodyJs builds <table> from JSON", false),
                                new Task("Themable st_table_* CSS classes per RFC 0017 token discipline", false),
                                new Task("TableContentViewer registration + Fixtures wiring", false),
                                new Task("ImageDoc record + ImageViewer + ImageContentViewer", false),
                                new Task("Demo: one TableDoc + one ImageDoc in homing-studio", false)
                        ),
                        List.of(new Dependency("P1", "Validates the typed-viewer pattern that TableDoc / ImageDoc inherit")),
                        "TableDoc renders complex tables (colspan/rowspan, cell badges) themed correctly across all studio themes; ImageDoc shows raster images with caption + alt, framework chrome wrapping them; both addressable as standalone docs at their own URLs.",
                        "Purely additive; rollback removes the new types.",
                        "M",
                        "TableDoc deliberately slim per D8. ImageDoc is Raw tier per RFC 0017 (no theming on raster)."),

                new Phase("P3", "Extend Segment ADT to wrap TableDoc + ImageDoc",
                        "Tie P2's visual Docs back into ComposedDoc via the typed segment family.",
                        "Add TableSegment + ImageSegment records as sealed permits of Segment. Each wraps a Doc "
                        + "instance + optional caption override (proxy pattern per D7). ComposedViewer's per-segment "
                        + "dispatch gains two new cases (small JS additions reusing TableViewer's / ImageViewer's "
                        + "rendering primitives).",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Extend Segment sealed permits with TableSegment + ImageSegment", false),
                                new Task("Update ComposedViewer's segment dispatcher", false),
                                new Task("Demo: extend the demo ComposedDoc with a table segment + image segment", false)
                        ),
                        List.of(
                                new Dependency("P1", "Segment ADT exists"),
                                new Dependency("P2", "TableDoc / ImageDoc exist as wrappable canonical Docs")
                        ),
                        "ComposedDoc with mixed segment kinds (markdown + SVG + table + image) renders correctly in one flowing page; the same TableDoc reused across two ComposedDocs collapses to one registered Doc with two appearances.",
                        "Purely additive.",
                        "S",
                        "Closes the 90% claim — these are the segment kinds that cover most HTML in tech docs."),

                new Phase("P4", ".mdad — slim markdown + conformance scanner",
                        "Realise RFC 0018. Disciplined markdown subset; conformance test fails the build on violations.",
                        "Specify the .mdad permitted/forbidden sets formally (already done in RFC 0018 §2-§3). "
                        + "Write MdadConformanceTest — small custom parser, per-file scan, fails the build on "
                        + "forbidden constructs. Add ClasspathMdadDoc subtype (or extend ClasspathMarkdownDoc to "
                        + "recognise .mdad extension and route through the slim path). Convert one existing .md "
                        + "to .mdad as validation. Optional: soft scanner over .md files emitting migration "
                        + "suggestions in warning mode.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("MdadConformanceTest — parser + violation reporter", false),
                                new Task("ClasspathMdadDoc subtype recognising .mdad extension", false),
                                new Task("Migrate one .md to .mdad; verify it passes conformance + renders identically", false),
                                new Task("Optional: soft .md scanner emitting migration warnings", false)
                        ),
                        List.of(new Dependency("P1", "MarkdownSegment is the primary consumer of .mdad bodies")),
                        ".mdad files with forbidden constructs fail the build with file + line + violation kind; valid .mdad files render identically to their .md equivalents; one converted file demonstrates the migration is mechanical.",
                        "Existing .md files unchanged; conformance scanner exempts them. Rollback removes the scanner + Doc subtype.",
                        "S",
                        "Closes the escape hatch. Authors can't smuggle HTML into MarkdownSegment bodies once .mdad is the format."),

                new Phase("P5", "Case study — Why We Ditched HTML (self-proof)",
                        "The closing milestone. A case study authored entirely in the new vocabulary.",
                        "Write \"Why We Ditched HTML\" as a ComposedDoc with markdown + SVG segments. Six "
                        + "sections roughly: (1) the SvgViewer chrome bug that started it, (2) the HTML-vs-markdown "
                        + "audit that surfaced the 90% number, (3) the proposed shape (ComposedDoc + visuals + "
                        + ".mdad), (4) what was declined (Mermaid, HtmlDoc, escape hatches), (5) what the case "
                        + "study itself demonstrates by existing, (6) the slogan. Each section is a MarkdownSegment; "
                        + "diagrams between sections are SvgSegments. Register as a CaseStudy.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Draft the six sections in .mdad", false),
                                new Task("Author the diagrams (call-stack failure, HTML capability tiers, ComposedDoc structure, discipline lattice)", false),
                                new Task("Assemble the ComposedDoc + register in CaseStudies catalogue", false),
                                new Task("Verify the case study reads coherently across all themes", false)
                        ),
                        List.of(
                                new Dependency("P1", "ComposedDoc renders the case study"),
                                new Dependency("P3", "(Optional) TableSegment if the audit table fits better as TableDoc")
                        ),
                        "The case study renders correctly; its existence is proof that the vocabulary supports nontrivial authoring; the case study cites and links to the trio of RFCs + the doctrine.",
                        "The case study is a content artifact; rollback removes the doc file. No code impact.",
                        "M",
                        "Hard milestone. Without P5 the work is just plumbing; with P5 the work is its own published proof."),

                new Phase("P6", "Retrofit high-value existing docs (optional)",
                        "Convert selected ProseDocs to ComposedDocs with added diagrams.",
                        "Candidates: RFC 0015 with the leaf-hierarchy diagram, RFC 0016 with the tree-shape "
                        + "diagram, RFC 0017 with the theme-token cascade, ontology entries with relationship "
                        + "graphs. Each retrofit adds visual clarity; sets the bar for future authoring.",
                        PhaseStatus.NOT_STARTED,
                        List.of(
                                new Task("Select 3-5 high-value docs for retrofit", false),
                                new Task("Author the diagrams for each", false),
                                new Task("Convert each ProseDoc to ComposedDoc with prose + diagram segments", false)
                        ),
                        List.of(new Dependency("P1", "ComposedDoc is the target shape")),
                        "Each retrofitted doc reads more clearly with the added visual; the retrofit doesn't break existing citations (UUID stays the same on conversion).",
                        "Each retrofit is independent; rollback per-doc.",
                        "L",
                        "Optional. Phases 1-5 deliver the vocabulary; P6 spreads it to existing content where the gain is real.")
        );
    }

    @Override public List<Acceptance> acceptance() {
        return List.of(
                new Acceptance("ComposedDoc PoC renders correctly",
                        "A ComposedDoc with at least one MarkdownSegment + one SvgSegment renders as a single flowing page with framework chrome (header, breadcrumb, theme, audio) and a server-rendered TOC. Theme switching applies to both prose and SVG bodies.",
                        false),
                new Acceptance("Visual asset Docs are first-class",
                        "TableDoc (with both JSON and CSV ingestion paths) and ImageDoc are registered, addressable, citable Docs with their own viewer URLs. Each renders standalone correctly; each can be wrapped as a segment in a ComposedDoc with optional caption override.",
                        false),
                new Acceptance(".mdad conformance enforced at build",
                        "A .mdad file containing forbidden constructs (inline HTML, markdown tables, inline images, bare external URLs) fails the build with actionable error messages; valid .mdad files render identically to their .md equivalents.",
                        false),
                new Acceptance("Self-proof case study published",
                        "\"Why We Ditched HTML\" exists as a ComposedDoc with markdown + SVG segments, registered in the CaseStudies catalogue, reads coherently across all studio themes, and demonstrates by existing that the vocabulary supports nontrivial authoring with diagrams.",
                        false),
                new Acceptance("No new path to HTML escape",
                        "Conformance + framework discipline holds: no framework-shipped content contains inline HTML; no new Doc kind permits author-rolled DOM constructs; all visuals route through SvgDoc / TableDoc / ImageDoc with proper theme participation per RFC 0017.",
                        false)
        );
    }
}
