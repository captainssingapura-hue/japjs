package hue.captains.singapura.japjs.server;

import hue.captains.singapura.japjs.core.CssGroup;
import hue.captains.singapura.japjs.core.CssGroupResolver;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * GET /css?class=&lt;CssGroup.canonical.class.name&gt;
 * <p>Accepts a {@link CssGroup} class name, transitively resolves its CSS
 * dependencies, and returns a JSON array of entries with {@code name} and
 * {@code href} for each resolved CssGroup in dependency order.</p>
 */
public class CssGetAction
        implements GetAction<RoutingContext, ModuleQuery, EmptyParam.NoHeaders, List<CssGetAction.CssEntry>> {

    public record CssEntry(String name, String href) {}

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
    public CompletableFuture<List<CssEntry>> execute(ModuleQuery query, EmptyParam.NoHeaders headers) {
        if (query.className() == null || query.className().isBlank()) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Missing 'class' query parameter"));
        }
        try {
            Class<?> clazz = Class.forName(query.className());
            Object instance;
            try {
                var instanceField = clazz.getField("INSTANCE");
                instance = instanceField.get(null);
            } catch (NoSuchFieldException e) {
                instance = clazz.getDeclaredConstructor().newInstance();
            }

            if (!(instance instanceof CssGroup<?> cssGroup)) {
                return CompletableFuture.failedFuture(
                        new IllegalArgumentException(query.className() + " is not a CssGroup"));
            }

            List<CssGroup<?>> resolved = CssGroupResolver.resolve(List.of(cssGroup));
            String themeSuffix = query.theme() != null ? "&theme=" + query.theme() : "";
            List<CssEntry> entries = resolved.stream()
                    .map(css -> {
                        String name = css.getClass().getCanonicalName();
                        String href = "/css-content?class=" + name + themeSuffix;
                        return new CssEntry(name, href);
                    })
                    .toList();

            return CompletableFuture.completedFuture(entries);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
