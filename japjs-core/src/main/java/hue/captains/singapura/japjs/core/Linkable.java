package hue.captains.singapura.japjs.core;

/**
 * Common supertype for navigation targets — anything that can be the destination
 * of a typed link.
 *
 * <p>Implemented by {@link AppModule} (an internal app served by the kernel) and,
 * once Step 03 lands, by {@code ProxyApp} (a typed declaration for an external
 * URL like a GitHub repo or docs site). Both are addressable by their
 * {@link #simpleName()} and may declare a typed {@link #paramsType()} record.</p>
 *
 * <p>Introduced in RFC 0001.</p>
 *
 * <p><b>Sealed:</b> the {@code permits} clause lists {@link AppModule} (the
 * internal-app case) and {@link ProxyApp} (the typed external-URL case).
 * Both kinds participate in the same registry and the same {@link AppLink}
 * import mechanism; the resolver and writer differentiate at the type
 * level when they need to.</p>
 */
public sealed interface Linkable extends Importable permits AppModule, ProxyApp {

    /**
     * The public URL identifier for this linkable. Used as the value of the
     * {@code ?app=} query parameter. Defaults — when not overridden — to the
     * kebab-case derivation of the simple class name (see {@link #defaultSimpleName(Class)}).
     *
     * <p>Override to lock the URL contract independently of the Java class
     * name (e.g., to keep a stable URL through future class renames).</p>
     */
    String simpleName();

    /**
     * The Java record describing this linkable's typed query parameters,
     * or {@code Void.class} for parameter-less linkables (the default).
     *
     * <p>Each component of the record becomes one URL query key on
     * outgoing links, and is parsed back into a typed value on the
     * receiving side.</p>
     */
    Class<?> paramsType();

    /**
     * Default simple-name derivation. Splits the class's simple name on
     * uppercase boundaries, lowercases each piece, joins with {@code -}.
     *
     * <p>Examples:</p>
     * <ul>
     *   <li>{@code PitchDeck.class}     → {@code "pitch-deck"}</li>
     *   <li>{@code WonderlandDemo.class} → {@code "wonderland-demo"}</li>
     *   <li>{@code HTTPHandler.class}   → {@code "http-handler"}</li>
     *   <li>{@code A.class}             → {@code "a"}</li>
     * </ul>
     *
     * <p>For an acronym followed by a normal word ({@code HTTPHandler}), the
     * acronym is treated as a single segment.</p>
     */
    static String defaultSimpleName(Class<?> cls) {
        String simple = cls.getSimpleName();
        if (simple.isEmpty()) {
            throw new IllegalArgumentException("Cannot derive simple name from empty class name");
        }

        StringBuilder out = new StringBuilder(simple.length() + 4);
        int i = 0;
        int n = simple.length();
        while (i < n) {
            char c = simple.charAt(i);
            if (Character.isUpperCase(c)) {
                // Walk a run of uppercase chars (handles acronyms like HTTP).
                int start = i;
                while (i < n && Character.isUpperCase(simple.charAt(i))) {
                    i++;
                }
                int runEnd = i; // exclusive

                // If a non-upper follows, the LAST upper of the run starts the next word.
                // E.g., "HTTPHandler" → run [HTTP], then "Handler" — the H of Handler
                // belongs to the next word, not the acronym.
                int acronymEnd = (i < n && Character.isLowerCase(simple.charAt(i)) && (runEnd - start) > 1)
                        ? runEnd - 1
                        : runEnd;

                if (acronymEnd > start) {
                    if (out.length() > 0) out.append('-');
                    for (int k = start; k < acronymEnd; k++) {
                        out.append(Character.toLowerCase(simple.charAt(k)));
                    }
                }
                if (acronymEnd < runEnd) {
                    // The leftover upper char starts the next word.
                    if (out.length() > 0) out.append('-');
                    out.append(Character.toLowerCase(simple.charAt(acronymEnd)));
                }
            } else {
                // Continue the current word.
                out.append(c);
                i++;
            }
        }
        return out.toString();
    }
}
