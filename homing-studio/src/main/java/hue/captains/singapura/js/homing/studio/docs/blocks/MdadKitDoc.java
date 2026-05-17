package hue.captains.singapura.js.homing.studio.docs.blocks;

import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.studio.base.SvgDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.composed.SvgSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TableSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TextSegment;
import hue.captains.singapura.js.homing.studio.base.table.TableData;
import hue.captains.singapura.js.homing.studio.base.table.TableData.Cell;
import hue.captains.singapura.js.homing.studio.base.table.TableDoc;

import java.util.List;
import java.util.Optional;

/**
 * The {@code .mdad+} kit — Building Block doc explaining the typed
 * text vocabulary. Authored end-to-end as a {@link ComposedDoc} using
 * {@link TextSegment}, {@link SvgSegment}, and {@link TableSegment} —
 * the doc explaining the kit is itself built on the kit.
 *
 * <p>Four supporting Docs ship with this kit and resolve via
 * {@code /doc?id=&lt;uuid&gt;}:</p>
 * <ul>
 *   <li>{@link #INSTANCE} — the ComposedDoc itself (kind {@code "composed"}).</li>
 *   <li>{@link #AST_DOC}            — the AST tree diagram (kind {@code "svg"}).</li>
 *   <li>{@link #PARSE_PIPELINE_DOC} — the parse-pipeline diagram (kind {@code "svg"}).</li>
 *   <li>{@link #RENDER_FLOW_DOC}    — the server-to-client render-flow diagram (kind {@code "svg"}).</li>
 *   <li>{@link #GRAMMAR_DOC}        — the T0..T4 grammar ladder (kind {@code "table"}).</li>
 * </ul>
 *
 * <p>{@code BuildingBlocksCatalogue} registers all of them so the segment
 * proxies resolve at runtime via the shared {@code DocRegistry}.</p>
 */
public final class MdadKitDoc {

    private MdadKitDoc() {}

    // -----------------------------------------------------------------------
    // Supporting visual Docs — referenced by the ComposedDoc's segments.
    // -----------------------------------------------------------------------

    public static final SvgDoc<MdadKitSvgs> AST_DOC = new SvgDoc<>(
            new SvgRef<>(MdadKitSvgs.INSTANCE, new MdadKitSvgs.astShape()),
            "The .mdad+ AST",
            "Block and Inline as sealed sums; every variant is a Java record.");

    public static final SvgDoc<MdadKitSvgs> PARSE_PIPELINE_DOC = new SvgDoc<>(
            new SvgRef<>(MdadKitSvgs.INSTANCE, new MdadKitSvgs.parsePipeline()),
            "TextParser pipeline",
            "Body text → normalise → block split → block classify → inline tokenise → typed AST.");

    public static final SvgDoc<MdadKitSvgs> RENDER_FLOW_DOC = new SvgDoc<>(
            new SvgRef<>(MdadKitSvgs.INSTANCE, new MdadKitSvgs.renderFlow()),
            "Server-to-client render flow",
            "Parse once on the server, walk once on the client — no second parser, no markdown library.");

    public static final TableDoc GRAMMAR_DOC = buildGrammarTable();

    private static TableDoc buildGrammarTable() {
        var headers = List.of(
                Cell.of("Tier"),
                Cell.of("Adds"),
                Cell.of("Block / Inline kind"),
                Cell.of("Justified by")
        );
        var rows = List.<List<Cell>>of(
                List.of(
                        Cell.of("T0 — Text"),
                        Cell.of("Paragraphs (blank-line-separated). Zero syntax."),
                        Cell.of("Block.Para + Inline.Text"),
                        Cell.of("Every prose segment in existence")
                ),
                List.of(
                        Cell.of("T1 — Inline emphasis"),
                        Cell.of("**bold**, *italic*, `code`"),
                        Cell.of("Inline.Bold / .Italic / .Code"),
                        Cell.of("19 uses across the 11-segment audit")
                ),
                List.of(
                        Cell.of("T2 — Lists"),
                        Cell.of("`- ` bullet, `N. ` numbered (one line per item)"),
                        Cell.of("Block.Bullets / Block.Numbered"),
                        Cell.of("5 uses across the audit")
                ),
                List.of(
                        Cell.of("T3 — Blockquote"),
                        Cell.of("`> ` line prefix"),
                        Cell.of("Block.Quote"),
                        Cell.of("1 use; rhetorical pivot in the case study")
                ),
                List.of(
                        Cell.of("T4 — Typed references"),
                        Cell.of("[label](#ref:name) — anchor must start with #ref:"),
                        Cell.of("Inline.Ref"),
                        Cell.of("RFC 0004-ext1 cross-doc reference protocol")
                ),
                List.of(
                        Cell.of("— excluded —"),
                        Cell.of("Headings inside body, code blocks, raw HTML"),
                        Cell.of("not in the grammar"),
                        Cell.of("Segment title carries section heading; code → future CodeSegment; HTML → typed segments retired it")
                )
        );
        return new TableDoc(
                TableDoc.deterministicUuid("blocks:mdad-kit:grammar-ladder"),
                ".mdad+ grammar — the additive ladder",
                "Five accept tiers plus the explicit exclusions. Every tier earned its keep from the audit; the excluded features are either redundant under the typed schema or promoted to a separate Doc kind.",
                new TableData(headers, rows));
    }

    // -----------------------------------------------------------------------
    // The kit doc — ComposedDoc with text + SVG + table segments.
    // -----------------------------------------------------------------------

    public static final ComposedDoc INSTANCE = build();

    private static ComposedDoc build() {
        var intro = new TextSegment(
                """
                The kit explaining the kit, built on the kit.

                This page is a `ComposedDoc` whose prose segments are `TextSegment`s parsed under the very grammar the page documents. The diagrams are `SvgDoc`s; the grammar ladder is a `TableDoc`. If the page renders, the toolchain is sound.
                """,
                Optional.of(".mdad+ — the typed text vocabulary"));

        var why = new TextSegment(
                """
                A documentation studio needs prose. The first instinct is markdown — but markdown carries an HTML escape hatch by design, and once any author can write `<div>` the typing discipline is gone. The doctrine *Typed Content Vocabulary* refuses that escape hatch on the same grounds as `<style>` and `<script>` — meaning routed around the type system.

                The audit-driven response: don't subtract from markdown, *add to plain text*. Start from `.txt`, watch what the existing prose actually uses, and add only those grammar tokens as evidence accumulates. The result is `.mdad+` — a tight five-tier grammar that fits on a single screen, with every tier traceable to a real use site.
                """,
                Optional.of("Why a new prose kind"));

        var ladder = new TextSegment(
                """
                The table below is the full grammar — every block kind, every inline token, and the explicit exclusions. Each row's *Justified by* column points at the audit evidence that earned it a place.
                """,
                Optional.of("The grammar ladder"));

        var grammarTable = new TableSegment(
                GRAMMAR_DOC,
                Optional.of("Figure 1 — the audit-driven .mdad+ grammar ladder."));

        var astWords = new TextSegment(
                """
                A `TextSegment` body is parsed eagerly at construction time. The result is a `List<Block>` where each block is one of four sealed variants; each block carries `List<Inline>` (or `List<List<Inline>>` for list items), where every inline is one of five sealed variants.

                That's the whole AST — two sealed sums, nine total variants. Exhaustive switch dispatch on both sides of the wire keeps the serialiser and the renderer aligned by the type system; adding a variant breaks both call sites at compile time.
                """,
                Optional.of("The AST"));

        var astDiagram = new SvgSegment(
                AST_DOC,
                Optional.of("Figure 2 — the typed AST. Two sealed sums, nine variants total."));

        var parseWords = new TextSegment(
                """
                The parser is ~180 lines of Java. Four passes:

                1. **Normalise** — fold CRLF to LF.
                2. **Block split** — walk lines; blank lines separate blocks; the leading marker (`- `, `N. `, `> `, or none) classifies each.
                3. **Block classify + collect** — gather contiguous lines belonging to the same block; soft-wrap paragraphs join with a single space.
                4. **Inline tokenise** — single-pass scanner per block; emits `Bold`, `Italic`, `Code`, `Ref`, or `Text` runs.

                Strictness over forgiveness. Unclosed delimiters, nested emphasis, empty inline code, non-`#ref:` link targets — all throw `ParseException(line, column, message)`. The constructor of `TextSegment` runs the parser, so an unparseable body **never lands in a Doc** — the discipline is enforced at object construction, not at render.
                """,
                Optional.of("The parser"));

        var parseDiagram = new SvgSegment(
                PARSE_PIPELINE_DOC,
                Optional.of("Figure 3 — the parsing pipeline, with a worked example."));

        var renderWords = new TextSegment(
                """
                The parsed AST is what travels on the wire. `ComposedDoc.contents()` walks the segments; for a `TextSegment` it emits `{ "kind": "text", "blocks": [...] }` where each block and inline mirrors its Java variant. The client never sees the original body text and never runs a markdown parser — the JS renderer is a pure data walk that maps each variant to a semantic HTML element.

                The payoff: one parser (Java), one renderer (JS), one wire shape (JSON). No second parser drifts out of sync; no surprise grammar quirk shows up only at render time.
                """,
                Optional.of("The render flow"));

        var renderDiagram = new SvgSegment(
                RENDER_FLOW_DOC,
                Optional.of("Figure 4 — server parses + serialises; client walks JSON to semantic HTML."));

        var properties = new TextSegment(
                """
                What the discipline buys you:

                - **Conformance for free.** The parser *is* the conformance gate. There's no second regex sweep because the grammar simply doesn't recognise raw HTML angle brackets, code fences, or ATX heading markers as tokens — they come through as literal text (which the author notices instantly) or fail parsing outright.
                - **Accessibility by construction.** The renderer emits semantic HTML (`<strong>`, `<ul>`, `<blockquote>`, `<code>`, `<a>`) by mapping from typed variants. The W3C's first rule of ARIA — *don't use ARIA if a native HTML element gives the same semantics* — is honoured automatically.
                - **Wire-format stability.** The JSON shape mirrors the AST shape. New grammar features need a new variant on both sides; exhaustive switches catch the addition at compile time. No mystery wire fields, no forgotten render branch.
                - **No external dependency.** The renderer doesn't import marked.js or any markdown library for `TextSegment`s — the AST walk is ~90 lines of JS.
                """,
                Optional.of("What the discipline earns you"));

        var future = new TextSegment(
                """
                The audit produced one deliberate hole: code blocks. Roughly 84% of existing prose Docs contain fenced code. The discipline says don't smuggle them into `TextSegment` — *promote them to a typed `CodeSegment` kind* (language tag, addressable, citable, syntax-highlighted under the same renderer-owns-presentation rule). That work is unblocked but unscheduled; pick it up when a code-heavy doc gets touched.

                Future inline gaps (pronunciation hints, abbreviation expansions, long-form image descriptions) follow the same loop — when a real need surfaces, enrich the typed schema rather than reach for an annotation layer. The case study *Why We Ditched HTML* makes the underlying stance.
                """,
                Optional.of("Open ends"));

        return ComposedDoc.of(
                ComposedDoc.deterministicUuid("blocks:mdad-kit"),
                ".mdad+ Kit — typed text vocabulary",
                "The Building Block doc for `TextSegment` and the `.mdad+` grammar. Authored in `TextSegment` + `SvgSegment` + `TableSegment` — the kit explaining the kit, built on the kit.",
                "BLOCK",
                List.of(intro, why, ladder, grammarTable,
                        astWords, astDiagram,
                        parseWords, parseDiagram,
                        renderWords, renderDiagram,
                        properties, future));
    }
}
