package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * {@code GET /doc?id=<uuid>} — serves the bytes of a typed {@link Doc} resolved by UUID.
 *
 * <p>Per <a href="../../../../../../../../../../docs/rfcs/0004-typed-docs-and-doc-visibility.md">
 * RFC 0004</a>, the wire identity of a Doc is its {@link UUID}; the action looks up the
 * registered {@link Doc} in the {@link DocRegistry} and returns {@link Doc#contents()} with
 * the Doc's declared {@link Doc#contentType()}. The action does no filesystem or classpath
 * I/O of its own — the Doc owns its sourcing.</p>
 *
 * <p><b>Security</b>: user-supplied input never reaches a path. Only Docs registered at
 * boot are reachable; an unknown UUID is a 404. The path-traversal surface from previous
 * versions (where {@code ?path=…} was a free-form string) is gone.</p>
 *
 * @since RFC 0004
 */
public class DocGetAction
        implements GetAction<RoutingContext, DocGetAction.Query, EmptyParam.NoHeaders, DocContent> {

    public record Query(String id) implements Param._QueryString {}

    private final DocRegistry registry;

    public DocGetAction(DocRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(ctx.request().getParam("id"));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<DocContent> execute(Query query, EmptyParam.NoHeaders headers) {
        String raw = query.id();
        if (raw == null || raw.isBlank()) {
            return CompletableFuture.failedFuture(
                    notFound("id", "Required query parameter 'id' was not provided"));
        }
        UUID id;
        try {
            id = UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return CompletableFuture.failedFuture(notFound(raw, "Malformed UUID"));
        }
        Doc doc = registry.resolve(id);
        if (doc == null) {
            return CompletableFuture.failedFuture(notFound(raw, "No Doc registered with this UUID"));
        }
        try {
            String body = doc.contents();
            return CompletableFuture.completedFuture(new DocContent(body, doc.contentType()));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(notFound(raw,
                    "Failed to load Doc contents: " + e.getMessage()));
        }
    }

    private static ResourceNotFound notFound(String resource, String reason) {
        return new ResourceNotFound(
                new ResourceNotFound._InternalError(null, reason + ": " + resource),
                new ResourceNotFound._ExternalError(resource, reason)
        );
    }
}
