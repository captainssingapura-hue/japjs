package hue.captains.singapura.js.homing.core;

import java.util.Map;

/**
 * RFC 0002-ext1 Phase 09 — per-theme variable values, served as a separate
 * CSS file at {@code /theme-vars?theme=Y}.
 *
 * <p>{@link Theme} is identity-only ({@code slug() + label()}). The actual
 * variable values live in a sibling singleton implementing this interface,
 * bound to a specific theme via the {@code <TH>} type parameter:</p>
 *
 * <pre>
 *   public record HomingDefault() implements Theme {
 *       public static final HomingDefault INSTANCE = new HomingDefault();
 *       &#64;Override public String slug() { return "homing-default"; }
 *
 *       public record Vars() implements ThemeVariables&lt;HomingDefault&gt; {
 *           public static final Vars INSTANCE = new Vars();
 *           &#64;Override public HomingDefault theme() { return HomingDefault.INSTANCE; }
 *           &#64;Override public Map&lt;CssVar, String&gt; values() { return VALUES; }
 *           private static final Map&lt;CssVar, String&gt; VALUES = Map.ofEntries(...);
 *       }
 *   }
 * </pre>
 *
 * <p>Implementer is free to organize internally — split primitive/semantic
 * layers, load from external config, compute, etc. The framework consumes
 * the flat {@link #values()} map.</p>
 *
 * @param <TH> the {@link Theme} this variables singleton is bound to
 */
public interface ThemeVariables<TH extends Theme> {

    /** Identity: which theme these variables belong to. */
    TH theme();

    /** All variable values this theme defines, keyed by typed {@link CssVar}. */
    Map<CssVar, String> values();
}
