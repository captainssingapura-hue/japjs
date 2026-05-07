package hue.captains.singapura.js.homing.core;

/**
 * A typed theme — server-side identity token used as a lookup key when
 * resolving the {@link CssGroupImpl} for a {@link CssGroup}.
 *
 * <p>Each implementing record is a stateless singleton. Themes don't extend
 * {@code Exportable} (they're never JS-module exports) and don't use the
 * F-bound pattern (they have no self-returning methods — see RFC 0002 §3.1).
 *
 * <p>Example:
 * <pre>{@code
 * public record HomingDefault() implements Theme {
 *     public static final HomingDefault INSTANCE = new HomingDefault();
 *     @Override public String slug()  { return "homing-default"; }
 *     @Override public String label() { return "Homing default"; }
 * }
 * }</pre>
 *
 * @see CssGroupImpl
 * @see <a href="../../../../../../../../docs/rfcs/0002-typed-themes-for-cssgroups.md">RFC 0002 — Typed Themes for CssGroups</a>
 */
public interface Theme {

    /**
     * URL/filename slug. Stable, kebab-case. Used as the {@code theme=…}
     * query-string parameter value when the browser fetches a themed CSS
     * stylesheet.
     */
    String slug();

    /**
     * Optional human-readable name. Defaults to {@link #slug()} when not
     * overridden.
     */
    default String label() { return slug(); }
}
