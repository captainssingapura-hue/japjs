package hue.captains.singapura.js.homing.core;

/**
 * RFC 0002-ext1 Phase 09 — per-theme global CSS rules, served as a separate
 * CSS file at {@code /theme-globals?theme=Y}.
 *
 * <p>The escape hatch for cascade features the per-class model can't cover.
 * Two real use cases survive (RFC 0002-ext1 §3.6.9):</p>
 * <ol>
 *   <li>Descendant selector rules over content the framework didn't class
 *       (e.g. {@code .st-doc h1, .st-doc h2 { … }} — markdown-rendered HTML
 *       has no class hooks).</li>
 *   <li>Conditional variable definitions under media queries
 *       ({@code @media (prefers-color-scheme: dark) { :root { --color-foo: bar } }})
 *       when a single theme wants to internally adapt to system preference.</li>
 * </ol>
 *
 * <p>Like {@link ThemeVariables}, this is a sibling singleton bound to a
 * specific theme via the {@code <TH>} type parameter:</p>
 *
 * <pre>
 *   public record HomingDefault() implements Theme {
 *       // …
 *       public record Globals() implements ThemeGlobals&lt;HomingDefault&gt; {
 *           public static final Globals INSTANCE = new Globals();
 *           &#64;Override public HomingDefault theme() { return HomingDefault.INSTANCE; }
 *           &#64;Override public String css() { return GLOBALS; }
 *           private static final String GLOBALS = """
 *               .st-doc h1 { color: var(--color-text-link); }
 *               &#64;media (prefers-color-scheme: dark) { :root { --st-offwhite: #0F1320; } }
 *               """;
 *       }
 *   }
 * </pre>
 *
 * @param <TH> the {@link Theme} this globals singleton is bound to
 */
public interface ThemeGlobals<TH extends Theme> {

    /** Identity: which theme these globals belong to. */
    TH theme();

    /** Raw CSS body, served verbatim. */
    String css();
}
