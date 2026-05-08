package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.util.ResourceReader;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * GET /doc?path=&lt;classpath-relative-path&gt;
 *
 * <p>Serves the bytes of a typed {@link Doc} from the classpath. The path
 * comes from {@code Doc.path()} on the consumer's typed declaration; the
 * action validates the extension against its allow-list, reads the resource,
 * and returns the body with the matching MIME type.</p>
 *
 * <h2>Supported doc kinds (RFC 0002-ext2)</h2>
 *
 * <p>By default the action accepts text-based extensions covering the
 * sub-interfaces shipped in homing-studio-base:</p>
 *
 * <ul>
 *   <li>{@code .md}    → {@code text/markdown}     (default — see {@link MarkdownDoc})</li>
 *   <li>{@code .html}  → {@code text/html}         ({@link HtmlDoc})</li>
 *   <li>{@code .txt}   → {@code text/plain}        ({@link PlainTextDoc})</li>
 *   <li>{@code .json}  → {@code application/json}  ({@link JsonDoc})</li>
 *   <li>{@code .svg}   → {@code image/svg+xml}     ({@link SvgDoc})</li>
 * </ul>
 *
 * <p>Downstream that needs additional kinds (e.g. {@code .yaml},
 * {@code .csv}) constructs the action with a custom content-type map. The
 * action itself is purely a utility — it doesn't enumerate registered Docs;
 * each request resolves the path against the classpath independently.</p>
 *
 * <p><b>Validation:</b> the path must end with a configured extension, must
 * not contain {@code ..} segments, and must not start with a slash. Anything
 * else is a 404.</p>
 */
public class DocGetAction
        implements GetAction<RoutingContext, DocGetAction.Query, EmptyParam.NoHeaders, DocContent> {

    public record Query(String path) implements Param._QueryString {}

    /** Default extension → content-type map covering the sub-interfaces shipped here. */
    public static final Map<String, String> DEFAULT_CONTENT_TYPES = Map.ofEntries(
            Map.entry(".md",   "text/markdown; charset=utf-8"),
            Map.entry(".html", "text/html; charset=utf-8"),
            Map.entry(".txt",  "text/plain; charset=utf-8"),
            Map.entry(".json", "application/json; charset=utf-8"),
            Map.entry(".svg",  "image/svg+xml; charset=utf-8")
    );

    private final ResourceReader resourceReader;
    private final Map<String, String> extensionToContentType;

    public DocGetAction() {
        this(ResourceReader.fromSystemProperty(), DEFAULT_CONTENT_TYPES);
    }

    public DocGetAction(ResourceReader resourceReader) {
        this(resourceReader, DEFAULT_CONTENT_TYPES);
    }

    /**
     * @param resourceReader          how to read classpath bytes (typically system-default)
     * @param extensionToContentType  allowed extensions (lower-case, including the leading dot)
     *                                and the MIME types they map to. Downstream may extend
     *                                this map to add their own kinds.
     */
    public DocGetAction(ResourceReader resourceReader, Map<String, String> extensionToContentType) {
        this.resourceReader = Objects.requireNonNull(resourceReader, "resourceReader");
        this.extensionToContentType = Map.copyOf(extensionToContentType);
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
        if (path.contains("..") || path.startsWith("/") || path.startsWith("\\")) {
            return CompletableFuture.failedFuture(
                    notFound(path, "Must not contain '..' segments or a leading slash"));
        }
        String contentType = lookupContentType(path);
        if (contentType == null) {
            return CompletableFuture.failedFuture(notFound(path,
                    "Unsupported file extension; allowed: " + extensionToContentType.keySet()));
        }
        try {
            String body = String.join("\n", resourceReader.getStringsFromResource(path));
            return CompletableFuture.completedFuture(new DocContent(body, contentType));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(notFound(path, "Doc not found on classpath"));
        }
    }

    /** Find the matching extension entry for a path, or null if none. */
    private String lookupContentType(String path) {
        String lower = path.toLowerCase();
        for (var entry : extensionToContentType.entrySet()) {
            if (lower.endsWith(entry.getKey())) return entry.getValue();
        }
        return null;
    }

    private static ResourceNotFound notFound(String resource, String reason) {
        return new ResourceNotFound(
                new ResourceNotFound._InternalError(null, reason + ": " + resource),
                new ResourceNotFound._ExternalError(resource, reason)
        );
    }
}
