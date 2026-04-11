package hue.captains.singapura.japjs.server;

import hue.captains.singapura.japjs.core.CssGroup;
import hue.captains.singapura.japjs.core.util.ResourceReader;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

/**
 * GET /css-content?class=&lt;CssGroup.canonical.class.name&gt;
 * <p>Serves the actual CSS file content for a {@link CssGroup}.
 * Reads from {@code japjs/css/<canonical-path>.css}.</p>
 */
public class CssContentGetAction
        implements GetAction<RoutingContext, ModuleQuery, EmptyParam.NoHeaders, CssContent> {

    private final ResourceReader resourceReader;

    public CssContentGetAction(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }

    public CssContentGetAction() {
        this(ResourceReader.INSTANCE);
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

            if (!(instance instanceof CssGroup<?>)) {
                return CompletableFuture.failedFuture(
                        new IllegalArgumentException(query.className() + " is not a CssGroup"));
            }

            String basePath = "japjs/css/" + query.className().replace(".", "/");
            String path = query.theme() != null
                    ? basePath + "." + query.theme() + ".css"
                    : basePath + ".css";
            String css;
            try {
                css = String.join("\n", resourceReader.getStringsFromResource(path));
            } catch (Exception e) {
                // Fall back to default if themed variant not found
                css = String.join("\n", resourceReader.getStringsFromResource(basePath + ".css"));
            }
            return CompletableFuture.completedFuture(new CssContent(css));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
