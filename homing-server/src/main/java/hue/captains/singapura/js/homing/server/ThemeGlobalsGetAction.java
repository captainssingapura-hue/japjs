package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.Layer;
import hue.captains.singapura.js.homing.core.Layers;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeOverlay;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * RFC 0002-ext1 Phase 09 — serves per-theme global CSS rules as a separate,
 * cacheable CSS file at {@code GET /theme-globals?theme=Y}.
 *
 * <p>Body shape: the {@link ThemeGlobals#css()} string verbatim. Used for the
 * two cases the per-class model can't express: descendant selectors over
 * unowned content (markdown renders) and conditional variable definitions
 * under media queries (RFC 0002-ext1 §3.6.9).</p>
 *
 * <p>Tolerant of missing themes / globals: returns 200 with empty body when
 * the theme has no registered globals.</p>
 */
public class ThemeGlobalsGetAction
        implements GetAction<RoutingContext, ThemeGlobalsGetAction.Query, EmptyParam.NoHeaders, CssContent> {

    public record Query(String themeSlug) implements hue.captains.singapura.tao.http.action.Param._QueryString {}

    private final ThemeRegistry registry;
    private final Theme defaultTheme;

    public ThemeGlobalsGetAction(ThemeRegistry registry, Theme defaultTheme) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.defaultTheme = defaultTheme;
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(ctx.request().getParam("theme"));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<CssContent> execute(Query query, EmptyParam.NoHeaders headers) {
        String slug = query.themeSlug() != null && !query.themeSlug().isBlank()
                ? query.themeSlug()
                : (defaultTheme != null ? defaultTheme.slug() : null);

        if (slug == null) {
            return CompletableFuture.completedFuture(new CssContent(""));
        }

        ThemeGlobals<?> globals = registry.globalsForSlug(slug);
        if (globals == null) {
            return CompletableFuture.completedFuture(new CssContent(""));
        }
        return CompletableFuture.completedFuture(new CssContent(render(globals)));
    }

    /**
     * Wrap the theme's globals in cascade-layer declarations (Defect 0003).
     * Prefer {@link ThemeGlobals#chunks()} when present — each (layer →
     * content) pair gets its own {@code @layer X { … }} block, in ASCENDING
     * order. Fall back to {@link ThemeGlobals#css()} wrapped in
     * {@code @layer theme { … }} for themes that haven't migrated.
     */
    private static String render(ThemeGlobals<?> globals) {
        Map<Class<? extends Layer>, String> chunks = globals.chunks();
        String legacy = globals.css();
        boolean hasChunks = chunks != null && !chunks.isEmpty();
        boolean hasLegacy = legacy != null && !legacy.isEmpty();
        if (!hasChunks && !hasLegacy) return "";

        StringBuilder sb = new StringBuilder();
        sb.append(Layers.declaration()).append("\n\n");

        if (hasChunks) {
            for (Class<? extends Layer> layer : Layers.ASCENDING) {
                String content = chunks.get(layer);
                if (content == null || content.isEmpty()) continue;
                sb.append("@layer ").append(Layers.CSS_NAME.get(layer)).append(" {\n");
                sb.append(content.indent(4));
                if (!content.endsWith("\n")) sb.append('\n');
                sb.append("}\n\n");
            }
        } else {
            sb.append("@layer ").append(Layers.CSS_NAME.get(ThemeOverlay.class)).append(" {\n");
            sb.append(legacy.indent(4));
            if (!legacy.endsWith("\n")) sb.append('\n');
            sb.append("}\n");
        }
        return sb.toString();
    }
}
