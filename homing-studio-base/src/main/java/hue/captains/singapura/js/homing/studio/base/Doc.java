package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.Exportable;

/**
 * A document declared within a {@link DocGroup}.
 *
 * <p>Mirrors {@code CssClass} for {@code CssGroup}: each implementing record is an
 * exportable constant carrying typed metadata (path, title, summary, category)
 * about a single document. The framework's import-writer treats the record like
 * any other typed export — user code references docs by their record class, not
 * by string path, so renames are caught at compile time and missing docs surface
 * as missing classes.</p>
 *
 * <p>The {@link #path()} value is the location of the markdown bytes — typically
 * relative to a docs root the {@code DocGetAction} resolves at request time
 * (classpath, configured filesystem, etc.).</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * public record HomingWhitepaper() implements Doc<HomingDocs> {
 *     @Override public String path()     { return "whitepaper/homing-whitepaper.md"; }
 *     @Override public String title()    { return "Homing — Main White Paper"; }
 *     @Override public String summary()  { return "The full technical design."; }
 *     @Override public String category() { return "WHITEPAPER"; }
 * }
 * }</pre>
 *
 * @param <D> the {@link DocGroup} this doc belongs to
 */
public interface Doc<D extends DocGroup<D>> extends Exportable._Constant<D> {

    /** Path of the markdown bytes, relative to the docs resolver's root. */
    String path();

    /** Display title shown in browsers and reader headers. */
    String title();

    /** Optional one-line summary. Default empty. */
    default String summary() { return ""; }

    /** Optional category slug for grouping in browsers. Default empty. */
    default String category() { return ""; }
}
