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

    /**
     * Optional atmospheric backdrop — a typed {@link SvgRef} pointing at an
     * SVG asset that the framework renders as <i>inline DOM</i> behind the
     * studio chrome, on every page, when this theme is active.
     *
     * <p>Returning a non-null {@code SvgRef} causes
     * {@code AppHtmlGetAction} to embed the resolved SVG markup as the first
     * child of {@code <body>}, wrapped in {@code <div class="theme-backdrop">}.
     * Because the SVG is real DOM (not a {@code background-image} sandbox),
     * its individual elements participate in the host document's CSS cascade
     * — themes can add per-element {@code :hover} animations, transitions,
     * pointer-event handlers, etc.</p>
     *
     * <p>Default {@code null} → no backdrop. Most themes return null and
     * tint the chrome via CSS variables alone. Atmospheric themes
     * ({@code HomingMapleBridge} is the framework's first) return an
     * {@code SvgRef} pointing at an inline SVG illustration.</p>
     */
    default SvgRef<?> backdrop() { return null; }
}
