package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.util.ResourceReader;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

/**
 * GET /doc?path=&lt;classpath-relative.md&gt;
 *
 * <p>Serves the markdown bytes for a typed {@link hue.captains.singapura.js.homing.core.Doc}.
 * Reads classpath resources directly — no content-provider abstraction. The
 * path comes from {@code Doc.path()} on the consumer's typed declaration.</p>
 *
 * <p><b>Validation:</b> the path must end in {@code .md}, must not contain
 * {@code ..} segments, and must not start with a slash. Anything else is a 404.</p>
 *
 * <p><b>Why a separate action from {@code DocContentGetAction}:</b> that one
 * reads from a configured filesystem root for browseable project docs.
 * {@code DocGetAction} reads from the classpath for typed Docs that travel
 * inside the JAR — the model homing.js uses for self-contained, CDN-free
 * deployment.</p>
 */
public class DocGetAction
        implements GetAction<RoutingContext, DocGetAction.Query, EmptyParam.NoHeaders, DocContent> {

    public record Query(String path) implements Param._QueryString {}

    private final ResourceReader resourceReader;

    public DocGetAction() {
        this(ResourceReader.fromSystemProperty());
    }

    public DocGetAction(ResourceReader resourceReader) {
        this.resourceReader = resourceReader;
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(ctx.request().getParam("path"));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<DocContent> execute(Query query, EmptyParam.NoHeaders headers) {
        String path = query.path();
        if (path == null || path.isBlank()) {
            return CompletableFuture.failedFuture(
                    notFound("path", "Required query parameter 'path' was not provided"));
        }
        if (!path.endsWith(".md") || path.contains("..")
                || path.startsWith("/") || path.startsWith("\\")) {
            return CompletableFuture.failedFuture(
                    notFound(path, "Must end with .md and contain no '..' segments or leading slash"));
        }
        try {
            String body = String.join("\n", resourceReader.getStringsFromResource(path));
            return CompletableFuture.completedFuture(new DocContent(body));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(notFound(path, "Doc not found on classpath"));
        }
    }

    private static ResourceNotFound notFound(String resource, String reason) {
        return new ResourceNotFound(
                new ResourceNotFound._InternalError(null, reason + ": " + resource),
                new ResourceNotFound._ExternalError(resource, reason)
        );
    }
}
