package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.util.ResourceReader;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

/**
 * GET /css-content?class=&lt;CssGroup.canonical.class.name&gt;
 * <p>Serves the actual CSS file content for a {@link CssGroup}.
 * Reads from {@code homing/css/<canonical-path>.css}.</p>
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
            return CompletableFuture.failedFuture(ResourceNotFound.missingClass());
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
                return CompletableFuture.failedFuture(ResourceNotFound.wrongType(query.className(), "CssGroup"));
            }

            String basePath = "homing/css/" + query.className().replace(".", "/");
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
            return CompletableFuture.failedFuture(ResourceNotFound.forClass(query.className(), e));
        }
    }
}
