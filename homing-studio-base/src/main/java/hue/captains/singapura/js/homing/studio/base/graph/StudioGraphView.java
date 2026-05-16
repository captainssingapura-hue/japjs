package hue.captains.singapura.js.homing.studio.base.graph;

import hue.captains.singapura.tao.ontology.ValueObject;

/**
 * RFC 0014 — closed set of rendering modes for the StudioGraph markdown
 * surface. Lifts the previous {@code String view} parameter into a typed
 * value so callers can't typo their way past the compiler, and so the
 * frontend's {@code ParamsWriter} can emit an {@code _enum(...)} guard for
 * the JS-side coercion.
 *
 * <ul>
 *   <li>{@link #TREE} — indented instance tree rooted at a vertex. Default.</li>
 *   <li>{@link #TYPES} — one row per distinct vertex class, ❓-unmarked
 *       surfaced first as a code-quality gauge.</li>
 * </ul>
 *
 * <p>Why an enum rather than a sealed record family: the URL marshalling
 * layer ({@code Navigable.url()} / {@code ParamsWriter}) supports enums
 * directly; nested records in an AppModule {@code Params} record are
 * forbidden in v1 of the marshaller spec. Enum is the right tool for a
 * closed set of value-only options anyway.</p>
 */
public enum StudioGraphView implements ValueObject {
    /** Indented instance tree dump rooted at a vertex (default). */
    TREE,
    /** Type-only table — one row per concrete vertex class. */
    TYPES;

    /**
     * Lenient parse from a query-string value. {@code null} / blank / unknown
     * inputs fall back to {@link #TREE} so a stale or hand-typed URL keeps
     * working rather than erroring. Case-insensitive.
     */
    public static StudioGraphView parseOrDefault(String s) {
        if (s == null || s.isBlank()) return TREE;
        try {
            return StudioGraphView.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return TREE;
        }
    }
}
