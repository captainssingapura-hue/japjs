package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.Exportable;

/**
 * A document declared within a {@link DocGroup}.
 *
 * <p>Mirrors {@code CssClass} for {@code CssGroup}: each implementing record is an
 * exportable constant carrying typed metadata about a single document. The
 * framework's import-writer treats the record like any other typed export — user
 * code references docs by their record class, not by string path, so renames are
 * caught at compile time and missing docs surface as missing classes.</p>
 *
 * <p>The {@link #path()} value is the location of the bytes — typically a
 * classpath resource path the {@link DocGetAction} resolves at request time.</p>
 *
 * <h2>Doc kinds (RFC 0002-ext2)</h2>
 *
 * <p>Doc is content-type-agnostic. The default {@link #contentType()} and
 * {@link #fileExtension()} target markdown; convenience sub-interfaces
 * ({@link MarkdownDoc}, {@link HtmlDoc}, {@link PlainTextDoc}, {@link JsonDoc},
 * {@link SvgDoc}) flip the defaults for other text formats. Downstream may
 * implement {@code Doc<D>} directly with custom overrides — the framework
 * doesn't constrain the open set of doc kinds.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * public record HomingWhitepaper() implements MarkdownDoc<HomingDocs> {
 *     @Override public String path()     { return "whitepaper/homing-whitepaper.md"; }
 *     @Override public String title()    { return "Homing — Main White Paper"; }
 *     @Override public String summary()  { return "The full technical design."; }
 *     @Override public String category() { return "WHITEPAPER"; }
 * }
 *
 * public record ArchitectureDiagram() implements SvgDoc<HomingDocs> {
 *     @Override public String path()  { return "diagrams/architecture.svg"; }
 *     @Override public String title() { return "Architecture diagram"; }
 * }
 * }</pre>
 *
 * @param <D> the {@link DocGroup} this doc belongs to
 */
public interface Doc<D extends DocGroup<D>> extends Exportable._Constant<D> {

    /** Path of the bytes, relative to the docs resolver's root (typically classpath). */
    String path();

    /** Display title shown in browsers and reader headers. */
    String title();

    /** Optional one-line summary. Default empty. */
    default String summary() { return ""; }

    /** Optional category slug for grouping in browsers. Default empty. */
    default String category() { return ""; }

    /**
     * MIME type the action serves this doc as. Default: {@code text/markdown}.
     * Sub-interfaces override per kind ({@link HtmlDoc}, {@link PlainTextDoc}, etc.).
     */
    default String contentType() { return "text/markdown; charset=utf-8"; }

    /**
     * Required suffix on {@link #path()} (validated by {@link DocGetAction}).
     * Default {@code .md}. Sub-interfaces override per kind.
     */
    default String fileExtension() { return ".md"; }
}
