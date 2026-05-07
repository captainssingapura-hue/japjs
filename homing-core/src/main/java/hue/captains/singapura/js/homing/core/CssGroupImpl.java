package hue.captains.singapura.js.homing.core;

import java.util.Map;

/**
 * Stateless typed pairing of a {@link CssGroup} (the declarations / "header")
 * with a {@link Theme} (the visual identity). Implementing records carry zero
 * instance state — the type parameters {@code CG} and {@code TH} encode all
 * identity, the methods provide runtime access to the singletons.
 *
 * <p>The framework's CSS-serving action consults a registry of
 * {@code CssGroupImpl} instances to resolve which impl to render for a given
 * {@code (CssGroup, Theme.slug)} pair. Each {@code CssGroup} typically
 * declares its own per-theme contract via a nested {@code Impl<TH extends Theme>}
 * interface that adds one abstract CSS-body method per declared
 * {@link CssClass}, giving compile-time completeness across themes.
 *
 * <p>Example:
 * <pre>{@code
 * public record StudioStylesHomingDefault() implements StudioStyles.Impl<HomingDefault> {
 *     public static final StudioStylesHomingDefault INSTANCE = new StudioStylesHomingDefault();
 *
 *     @Override public HomingDefault theme() { return HomingDefault.INSTANCE; }
 *
 *     @Override public Map<String,String> cssVariables() {
 *         return Map.of("--st-navy", "#1A2330", "--st-amber", "#C8921E");
 *     }
 *
 *     @Override public CssBlock<st_root>   st_root()   { return CssBlock.of("display: flex; min-height: 100vh;"); }
 *     @Override public CssBlock<st_header> st_header() { return CssBlock.of("background: var(--st-navy); padding: 16px;"); }
 *     // … one method per CssClass — compile error if any missing or mistyped …
 * }
 * }</pre>
 *
 * <p>The {@code TH} type parameter is kept (rather than collapsed to plain
 * {@code Theme}) so impls expose the <em>specific</em> theme they realize:
 * {@code StudioStylesHomingDefault.theme()} returns {@code HomingDefault},
 * not just {@code Theme}, letting calling code recover the concrete theme
 * without a cast.
 *
 * @param <CG> the CssGroup this impl realizes
 * @param <TH> the Theme it applies; bound to {@link Theme} (not F-bounded)
 *
 * @see Theme
 * @see CssGroup
 * @see <a href="../../../../../../../../docs/rfcs/0002-typed-themes-for-cssgroups.md">RFC 0002 — Typed Themes for CssGroups</a>
 */
public interface CssGroupImpl<CG extends CssGroup<CG>, TH extends Theme> {

    /** Identity: which {@link CssGroup} this impl applies to. */
    CG group();

    /** Identity: which {@link Theme} this impl realizes. */
    TH theme();

    /**
     * Optional CSS custom properties (cascading variables) emitted as a
     * single {@code :root { … }} block before the per-class rules. Default
     * is empty: most themes will override.
     *
     * <p>Map keys are property names with the leading {@code --} included
     * (e.g., {@code "--st-navy"}); values are the CSS literal value
     * (e.g., {@code "#1A2330"}, {@code "16px"}, {@code "var(--st-foo, white)"}).
     */
    default Map<String, String> cssVariables() { return Map.of(); }

    /**
     * Global / non-class-keyed rules emitted between the {@code :root} block
     * and the per-class rules. Use for rules that don't fit the per-class
     * shape: {@code html}/{@code body} resets, pseudo-class variants
     * ({@code :hover}, {@code :focus}), descendant selectors, {@code @media}
     * queries, and rules targeting pseudo-elements within a class.
     *
     * <p>Default empty. Override per-CssGroup-Impl when needed.</p>
     */
    default String globalRules() { return ""; }
}
