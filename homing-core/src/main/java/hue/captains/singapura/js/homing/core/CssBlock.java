package hue.captains.singapura.js.homing.core;

/**
 * Typed wrapper for the body of a single CSS rule — i.e., everything a theme
 * places between the curly braces of a rule keyed by one {@link CssClass}.
 *
 * <p>The {@code CC} type parameter binds the block to the specific
 * {@link CssClass} it styles. A theme method that returns
 * {@code CssBlock<st_root>} cannot be confused with one that returns
 * {@code CssBlock<st_header>} — the compiler catches "I pasted the wrong body
 * into the wrong method" before runtime.</p>
 *
 * <p>Returned by per-class methods on a {@link CssGroup}'s nested
 * {@code Impl<TH>} interface. The framework wraps this value with a kebab-cased
 * selector derived from the record's simple name when emitting the served CSS.</p>
 *
 * <p>The class binding also enables future same-class composition helpers
 * (merge, override) to be type-safe by construction: you can only merge two
 * {@code CssBlock<st_root>} values, never a mix.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * @Override public CssBlock<st_root> st_root() { return CssBlock.of("""
 *     display: flex;
 *     min-height: 100vh;
 *     """);
 * }
 * }</pre>
 *
 * @param <CC> the {@link CssClass} record this block styles
 * @see CssGroupImpl
 */
public record CssBlock<CC extends CssClass<?>>(String body) {

    /**
     * Convenience factory; reads better at call sites than
     * {@code new CssBlock<>(...)}. The target {@code CC} is inferred from the
     * declared return type at the call site.
     */
    public static <CC extends CssClass<?>> CssBlock<CC> of(String body) {
        return new CssBlock<>(body == null ? "" : body);
    }

    /**
     * Empty body — emitted as no declarations in the served CSS. Useful as a
     * placeholder during incremental implementation.
     */
    public static <CC extends CssClass<?>> CssBlock<CC> empty() {
        return new CssBlock<>("");
    }
}
