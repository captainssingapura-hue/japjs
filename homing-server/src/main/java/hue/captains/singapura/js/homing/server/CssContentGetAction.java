package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.util.CssClassName;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * GET /css-content?class=&lt;CssGroup canonical name&gt;[&amp;theme=&lt;slug&gt;]
 *
 * <p>Renders CSS programmatically from a typed {@link CssGroupImpl} resolved
 * via the registry passed at construction. Body shape:</p>
 * <ol>
 *   <li>{@code :root { … }} from {@link CssGroupImpl#cssVariables()}</li>
 *   <li>{@link CssGroupImpl#globalRules()} verbatim — pseudo-classes, descendant
 *       selectors, media queries, html/body resets</li>
 *   <li>One {@code .kebab-name { body }} per declared {@link CssClass} in the
 *       group, body sourced by reflection from the impl's matching method
 *       (record {@code st_root} → method {@code st_root()} → selector
 *       {@code .st-root})</li>
 * </ol>
 *
 * <p>Hard cut: unknown {@code class} or {@code theme} returns 404. No
 * file-based fallback (RFC 0002 §3.6).</p>
 */
public class CssContentGetAction
        implements GetAction<RoutingContext, ModuleQuery, EmptyParam.NoHeaders, CssContent> {

    private final List<CssGroupImpl<?, ?>> impls;
    private final Theme defaultTheme;

    /**
     * @param impls every registered {@link CssGroupImpl}; must contain at least
     *              one entry per {@code (group, theme)} pair the deployment serves
     * @param defaultTheme the theme to use when a request omits {@code ?theme=}.
     *                     May be {@code null}, in which case unparameterized
     *                     requests return 404
     */
    public CssContentGetAction(List<CssGroupImpl<?, ?>> impls, Theme defaultTheme) {
        this.impls = Objects.requireNonNull(impls, "impls");
        this.defaultTheme = defaultTheme;
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, ModuleQuery> queryStrMarshaller() {
        return ctx -> new ModuleQuery(
                ctx.request().getParam("class"),
                ctx.request().getParam("theme"),
                ctx.request().getParam("locale")
        );
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<CssContent> execute(ModuleQuery query, EmptyParam.NoHeaders headers) {
        if (query.className() == null || query.className().isBlank()) {
            return CompletableFuture.failedFuture(ResourceNotFound.missingClass());
        }
        try {
            Class<?> clazz = Class.forName(query.className());
            Object instance = resolveInstance(clazz);
            if (!(instance instanceof CssGroup<?> group)) {
                return CompletableFuture.failedFuture(ResourceNotFound.wrongType(query.className(), "CssGroup"));
            }

            String themeSlug = query.theme() != null && !query.theme().isBlank()
                    ? query.theme()
                    : (defaultTheme != null ? defaultTheme.slug() : null);
            if (themeSlug == null) {
                return CompletableFuture.failedFuture(ResourceNotFound.forClass(
                        query.className(),
                        new IllegalStateException(
                                "No theme specified and no default theme configured")));
            }
            CssGroupImpl<?, ?> impl = findImpl(group, themeSlug);
            if (impl == null) {
                return CompletableFuture.failedFuture(ResourceNotFound.forClass(
                        query.className() + "/" + themeSlug,
                        new IllegalStateException(
                                "No CssGroupImpl registered for group=" + query.className()
                                        + " theme=" + themeSlug)));
            }
            return CompletableFuture.completedFuture(new CssContent(renderCss(impl, group)));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(ResourceNotFound.forClass(query.className(), e));
        }
    }

    // ---- helpers --------------------------------------------------------

    private static Object resolveInstance(Class<?> clazz) throws Exception {
        try {
            return clazz.getField("INSTANCE").get(null);
        } catch (NoSuchFieldException e) {
            return clazz.getDeclaredConstructor().newInstance();
        }
    }

    /** First impl whose group() class equals {@code group}'s class AND theme().slug() matches. */
    private CssGroupImpl<?, ?> findImpl(CssGroup<?> group, String themeSlug) {
        for (var impl : impls) {
            if (impl.group().getClass().equals(group.getClass())
                    && impl.theme().slug().equals(themeSlug)) {
                return impl;
            }
        }
        return null;
    }

    private static String renderCss(CssGroupImpl<?, ?> impl, CssGroup<?> group) {
        StringBuilder sb = new StringBuilder();

        // 1. CSS custom properties — primitives first, then semantic tokens,
        // both inside one :root {} block. Semantic tokens reference primitives
        // via var(--primitive), so order matters.
        Map<String, String> primitives = impl.cssVariables();
        Map<String, String> semantic   = impl.semanticTokens();
        if (!primitives.isEmpty() || !semantic.isEmpty()) {
            sb.append(":root {\n");
            primitives.forEach((k, v) -> sb.append("    ").append(k).append(": ").append(v).append(";\n"));
            semantic  .forEach((k, v) -> sb.append("    ").append(k).append(": ").append(v).append(";\n"));
            sb.append("}\n\n");
        }

        // 2. Global rules (pseudo-classes, descendants, media queries, etc.)
        String global = impl.globalRules();
        if (global != null && !global.isEmpty()) {
            sb.append(global);
            if (!global.endsWith("\n")) sb.append('\n');
            sb.append('\n');
        }

        // 3. Per-class rules — iterate the group's declared CssClasses, resolve
        // each one's body, then emit the base rule + auto-generated variant
        // rules per `cls.variants()`.
        //
        // RFC 0002-ext1 Phase 05: prefer `cls.body()` when non-null (inline
        // class-level body — theme-agnostic, no impl method needed). Fall back
        // to the legacy impl-method dispatch via reflection for classes that
        // still rely on the per-theme Impl<TH> contract.
        for (CssClass<?> cssClass : group.cssClasses()) {
            try {
                String baseKebab = CssClassName.toCssName(cssClass.getClass());
                String selector = "." + baseKebab;
                String state = cssClass.pseudoState();
                if (state != null && !state.isEmpty()) selector += state;

                String body;
                String inline = cssClass.body();
                if (inline != null) {
                    body = inline;
                } else {
                    Method m = impl.getClass().getMethod(cssClass.getClass().getSimpleName());
                    body = ((CssBlock<?>) m.invoke(impl)).body();
                }

                // Base rule (with optional pseudoState suffix).
                sb.append(selector).append(" {\n");
                if (!body.isEmpty()) sb.append(body.indent(4));
                sb.append("}\n");

                // Auto-generated variant rules — same body, state-prefixed kebab,
                // pseudo-state suffix on the selector.
                for (String variant : cssClass.variants()) {
                    sb.append(".").append(variant).append("-").append(baseKebab)
                      .append(":").append(variant).append(" {\n");
                    if (!body.isEmpty()) sb.append(body.indent(4));
                    sb.append("}\n");
                }
            } catch (NoSuchMethodException e) {
                sb.append("/* render error: no method ").append(cssClass.getClass().getSimpleName())
                  .append("() on ").append(impl.getClass().getSimpleName()).append(" */\n");
            } catch (Exception e) {
                sb.append("/* render error: ").append(cssClass.getClass().getSimpleName())
                  .append(" — ").append(e.getMessage()).append(" */\n");
            }
        }

        return sb.toString();
    }
}
