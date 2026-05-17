package hue.captains.singapura.js.homing.studio.docs.casestudies;

import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.studio.base.SvgDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.composed.SvgSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TableSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TextSegment;
import hue.captains.singapura.js.homing.studio.base.table.TableData;
import hue.captains.singapura.js.homing.studio.base.table.TableData.Badge;
import hue.captains.singapura.js.homing.studio.base.table.TableData.Cell;
import hue.captains.singapura.js.homing.studio.base.table.TableDoc;

import java.util.List;
import java.util.Optional;

/**
 * RFC 0019 Phase 5 self-proof — the case study "Why We Ditched HTML",
 * authored end-to-end as a {@link ComposedDoc} using only typed
 * segments: strict-typed text prose, an SVG diagram, and a comparison
 * table. After Phase 4 the prose is {@link TextSegment} (parsed
 * server-side against the audit-driven {@code .mdad+} grammar) — no
 * markdown escape hatch anywhere on the page.
 *
 * <p>Three Docs ship with this study and are reachable via
 * {@code /doc?id=&lt;uuid&gt;}:</p>
 * <ul>
 *   <li>{@link #INSTANCE} — the ComposedDoc itself (kind {@code "composed"}).</li>
 *   <li>{@link #DIAGRAM_DOC} — the segment-ADT SVG diagram (kind {@code "svg"}).</li>
 *   <li>{@link #COMPARISON_DOC} — the HTML-vs-typed-kinds table (kind {@code "table"}).</li>
 * </ul>
 *
 * <p>{@code ArchitectureCaseStudiesCatalogue} registers all three so the
 * segment proxies resolve at runtime via the shared {@code DocRegistry}.</p>
 */
public final class WhyWeDitchedHtmlCaseStudy {

    private WhyWeDitchedHtmlCaseStudy() {}

    // -----------------------------------------------------------------------
    // Supporting visual Docs — referenced by the ComposedDoc's segments.
    // -----------------------------------------------------------------------

    public static final SvgDoc<WhyWeDitchedHtmlSvgs> DIAGRAM_DOC = new SvgDoc<>(
            new SvgRef<>(WhyWeDitchedHtmlSvgs.INSTANCE, new WhyWeDitchedHtmlSvgs.segmentAdt()),
            "ComposedDoc Segment ADT",
            "The four typed segment variants stacked inside a ComposedDoc envelope.");

    public static final TableDoc COMPARISON_DOC = buildComparisonTable();

    private static TableDoc buildComparisonTable() {
        var headers = List.of(
                Cell.of("HTML feature we used"),
                Cell.of("What it actually provided"),
                Cell.of("Typed replacement"),
                Cell.of("Status")
        );
        var rows = List.<List<Cell>>of(
                List.of(
                        Cell.of("<div>, <span>, custom CSS"),
                        Cell.of("Layout + ad-hoc visual styling"),
                        Cell.of("Themed framework primitives + SvgDoc"),
                        Cell.badged("REPLACED", Badge.SUCCESS)
                ),
                List.of(
                        Cell.of("<table> with classes"),
                        Cell.of("Structured tabular data with cell styling"),
                        Cell.of("TableDoc (slim: colspan, badges, alignment)"),
                        Cell.badged("REPLACED", Badge.SUCCESS)
                ),
                List.of(
                        Cell.of("<img>"),
                        Cell.of("Raster artwork display"),
                        Cell.of("ImageDoc (Raw tier, alt required)"),
                        Cell.badged("REPLACED", Badge.SUCCESS)
                ),
                List.of(
                        Cell.of("<svg>"),
                        Cell.of("Vector diagram display"),
                        Cell.of("SvgDoc (already first-class since RFC 0015)"),
                        Cell.badged("REPLACED", Badge.SUCCESS)
                ),
                List.of(
                        Cell.of("<script>"),
                        Cell.of("Inline interactivity"),
                        Cell.badged("REFUSED", Badge.ERROR),
                        Cell.of("No content kind for scripted prose. Apps own JS.")
                ),
                List.of(
                        Cell.of("<style>"),
                        Cell.of("Inline styling"),
                        Cell.badged("REFUSED", Badge.ERROR),
                        Cell.of("Themed tokens (RFC 0017) cover all sanctioned styling.")
                ),
                List.of(
                        Cell.of("<iframe>, <embed>"),
                        Cell.of("Foreign content embedding"),
                        Cell.badged("REFUSED", Badge.ERROR),
                        Cell.of("Trust boundary explicit; not paved.")
                ),
                List.of(
                        Cell.of("ARIA annotations"),
                        Cell.of("Accessibility metadata layered onto markup"),
                        Cell.badged("REFUSED", Badge.ERROR),
                        Cell.of("The typed schema IS the accessibility surface — extended, never annotated. The renderer emits semantic HTML by construction; new access needs grow the schema, not a parallel annotation layer.")
                )
        );
        return new TableDoc(
                TableDoc.deterministicUuid("casestudy:why-we-ditched-html:comparison"),
                "HTML features → typed-kind replacements",
                "The audit. Every HTML feature the prose pages used, what it actually provided, and what replaced it (or why nothing did).",
                new TableData(headers, rows));
    }

    // -----------------------------------------------------------------------
    // The case study — a ComposedDoc with text + SVG + table segments.
    // After Phase 4: section headings ride the segment title, never the
    // body; list items each fit on one line; the .mdad+ parser is the
    // conformance gate.
    // -----------------------------------------------------------------------

    public static final ComposedDoc INSTANCE = build();

    private static ComposedDoc build() {
        var intro = new TextSegment(
                """
                This page itself is the proof.

                Every word and figure below is a typed segment of one **ComposedDoc** (RFC 0019). The prose is `TextSegment`, parsed server-side against the audit-driven `.mdad+` grammar. The diagram is a registered `SvgDoc`. The comparison grid is a registered `TableDoc`. There is no inline HTML, no hand-rolled `<table>`, no `<style>` block, no escape hatch. If the page renders, the doctrine holds.
                """,
                Optional.of("The proof"));

        var question = new TextSegment(
                """
                A few weeks before this study was filed, the studio's prose kit still had a quiet escape hatch: any Doc could mix raw HTML into its markdown. The question we put to ourselves was simple and slightly uncomfortable:

                > *Inside a technical-documentation studio, what does HTML actually buy us that markdown can't?*

                We expected a short list of unavoidable HTML features and a long list of "use markdown." It came out the other way around. The features we did use sorted neatly into two buckets — *typed kinds we already had or were one Doc subtype away from*, and *features whose absence is a feature*.
                """,
                Optional.of("The question"));

        var theAudit = new TextSegment(
                """
                Every HTML construct in the studio's prose got triaged. The table below is the full inventory. The pattern is visible at a glance — the only HTML features that survived as escape hatches were the ones the doctrine explicitly **refuses**.
                """,
                Optional.of("The audit"));

        var auditTable = new TableSegment(
                COMPARISON_DOC,
                Optional.of("Figure 1 — the HTML feature audit. Every escape-hatch use case, classified."));

        var theShape = new TextSegment(
                """
                Four typed segment kinds, sealed at the type level, dispatched exhaustively. Every variant is a Java record. Every visual segment is a thin proxy to a registered Doc — the canonical artifact lives in the registry, addressable and citable; the segment is just a per-appearance framing with an optional caption.
                """,
                Optional.of("The shape we landed on"));

        var diagram = new SvgSegment(
                DIAGRAM_DOC,
                Optional.of("Figure 2 — the typed Segment ADT under a ComposedDoc envelope."));

        var properties = new TextSegment(
                """
                The discipline is austere on purpose. The payoff is what falls out of refusing the wrong shapes:

                1. **Themability.** SVG fragments inherit the active theme via `currentColor` and `var(--color-*)` tokens (RFC 0017). Table cells inherit the theme through framework primitives. Raster images are Raw tier and exempt.
                2. **Scannability.** The `TextSegment` parser is the conformance gate. Anything outside the audit-driven grammar fails at construction time with a precise line and column — the discipline is mechanically enforceable, with no second regex sweep needed.
                3. **Reusability.** Every visual is its own registered Doc — `SvgDoc`, `TableDoc`, `ImageDoc`. The same diagram can appear in three case studies with three different captions and zero duplication; standalone, it's also linkable.
                4. **Citability.** Every typed Doc has a UUID and a canonical URL. A doctrine page can link straight at a table or a diagram, not at the page that happens to embed it.
                5. **Open-ended growth.** New segment kinds extend the sealed permits list. The renderer's exhaustive switch then fails to compile until the new kind is handled — the type system enforces the contract the doctrine describes.

                None of these are HTML's natural strengths; all of them are byproducts of saying "no" to HTML in this specific context.
                """,
                Optional.of("What we got for free"));

        var refusals = new TextSegment(
                """
                Three HTML features show up as **REFUSED** in the audit above. Each refusal is a deliberate stance, not an oversight.

                - **No `<script>` in prose.** Documentation must not execute code from a doc body. Interactivity belongs to AppModules — they own JS via the typed `imports()` / `exports()` graph (RFC 0001) where the framework can audit it. A prose page that needs a live demo links to an AppDoc instead.
                - **No `<style>` in prose.** Every sanctioned visual choice is a CSS token (RFC 0017). If a page wants a colour or a spacing the tokens don't cover, that's a conversation about the design system — not a license to ship hand-rolled CSS that escapes theming.
                - **No `<iframe>` / `<embed>`.** The trust boundary between this studio's content and a foreign origin must be **explicit** — an outbound `Reference` (RFC 0004-ext1), not a silently-loaded sub-document.

                The refusals aren't poverty. They're where the discipline pays for itself.
                """,
                Optional.of("What we refused, and why"));

        var deferred = new TextSegment(
                """
                Accessibility through annotation — `aria-label`, `role="…"`, `aria-describedby`, and the rest — is the parallel escape hatch the doctrine refuses on the same grounds as `<style>` and `<script>`. It routes meaning around the type system.

                The typed schema is *itself* the accessibility surface. Every `TextSegment` parses to a semantic AST (paragraphs, lists, blockquotes, emphasis); the renderer emits the matching semantic HTML elements (`<strong>`, `<ul>`, `<blockquote>`, `<table>` with proper `<th>` scope) by construction. `SvgSegment` and `ImageSegment` carry alt text and captions as typed fields. Segment titles map to heading elements, contributing to landmark navigation automatically. The W3C's first rule of ARIA — *don't use ARIA if a native HTML element gives the same semantics* — is honoured for free because the typed kinds emit native elements.

                When an access need surfaces that the typed schema doesn't cover, the discipline says: **enrich the schema, don't annotate the output.** A long-form image description becomes `ImageDoc.longDescription`. A decorative SVG becomes `SvgSegment.decorative`. A pronunciation hint becomes an `Inline.Lang` variant. Each request is schema feedback — the same evidence-driven loop that produced the `.mdad+` grammar.
                """,
                Optional.of("Accessibility, by construction"));

        var close = new TextSegment(
                """
                Read this page once more, this time looking at *how* it's built. Every block you read is one of four typed shapes. Every visual is a registered Doc with its own UUID. Open Figure 1 standalone — it's a `TableDoc` page; open Figure 2 standalone — it's an `SvgDoc` page. The composition is the proof.

                The doctrine claim — *"you don't really need HTML, just SVGs"* — is now demonstrable on a single URL.
                """,
                Optional.of("Closing — the self-proof"));

        return ComposedDoc.of(
                ComposedDoc.deterministicUuid("casestudy:why-we-ditched-html"),
                "Case Study — Why We Ditched HTML",
                "Self-proof: this page argues that the studio's typed content vocabulary covers every legitimate HTML use case, and demonstrates the claim by being written entirely in that vocabulary. No inline HTML; the only chrome is the framework's typed segments — text, SVG, table.",
                "CASE_STUDY",
                List.of(intro, question, theAudit, auditTable,
                        theShape, diagram, properties, refusals, deferred, close));
    }
}
