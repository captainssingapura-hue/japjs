package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * GET /doc-content?path=&lt;relative-path&gt;
 *
 * <p>Serves a markdown document from the configured docs root. The path must:
 * end with {@code .md}, not contain {@code ..} segments, and resolve inside the
 * docs root after normalization. Anything else returns a not-found error.</p>
 *
 * <p>The docs root defaults to {@code ./docs} relative to the working directory,
 * configurable via the {@code homing.studio.docsRoot} system property.</p>
 */
public class DocContentGetAction
        implements GetAction<RoutingContext, DocContentGetAction.Query, EmptyParam.NoHeaders, DocContent> {

    public record Query(String path) implements Param._QueryString {}

    private final Path docsRoot;

    public DocContentGetAction() {
        this(Path.of(System.getProperty("homing.studio.docsRoot", "docs")).toAbsolutePath().normalize());
    }

    public DocContentGetAction(Path docsRoot) {
        this.docsRoot = docsRoot.toAbsolutePath().normalize();
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
        var rel = query.path();
        if (rel == null || rel.isBlank()) {
            return CompletableFuture.failedFuture(notFound("path", "Required query parameter 'path' was not provided", null));
        }
        if (!rel.endsWith(".md") || rel.contains("..") || rel.startsWith("/") || rel.startsWith("\\")) {
            return CompletableFuture.failedFuture(notFound(rel, "Must end with .md and contain no '..' segments", null));
        }
        Path target = docsRoot.resolve(rel).normalize();
        if (!target.startsWith(docsRoot)) {
            return CompletableFuture.failedFuture(notFound(rel, "Path escapes docs root", null));
        }
        if (!Files.isRegularFile(target)) {
            return CompletableFuture.failedFuture(notFound(rel, "Doc not found", null));
        }
        try {
            String body = Files.readString(target);
            return CompletableFuture.completedFuture(new DocContent(body));
        } catch (IOException e) {
            return CompletableFuture.failedFuture(notFound(rel, "Failed to read", e));
        }
    }

    private static ResourceNotFound notFound(String resource, String reason, Exception cause) {
        return new ResourceNotFound(
                new ResourceNotFound._InternalError(cause, reason + ": " + resource),
                new ResourceNotFound._ExternalError(resource, reason)
        );
    }
}
