package hue.captains.singapura.js.homing.studio.base.graph;

import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.studio.base.Bootstrap;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * RFC 0014 — {@code GET /graph-md} — serves the live {@link StudioGraph}
 * as a markdown document for the {@code StudioGraphInspector} AppModule
 * to render on the front-end via the bundled marked.js.
 *
 * <p>Conditionally registered by {@code Bootstrap.compose()} only when
 * {@code RuntimeParams.diagnosticsEnabled()} is true. Default off — the
 * endpoint is absent in standard deployments.</p>
 *
 * <p>Optional {@code ?root=<class-FQN>} narrows the dump to a specific
 * subtree (any reachable vertex's class FQN). Empty / missing → defaults
 * to the {@link Bootstrap} root.</p>
 *
 * <p>Per Functional Objects doctrine: dependency-holding object — holds
 * the {@code Bootstrap} reference passed in at construction. Per
 * Stateless Server: per-request server-side computation, no per-user state,
 * graph reused across requests.</p>
 */
public final class StudioGraphMarkdownAction
        implements GetAction<RoutingContext,
                             StudioGraphMarkdownAction.Query,
                             EmptyParam.NoHeaders,
                             MarkdownContent> {

    /**
     * @param root optional vertex class FQN — narrows the tree dump to a
     *             subtree. Empty / missing → defaults to the Bootstrap root.
     *             {@code String} (not a typed wrapper) because FQNs are
     *             free-text and any class on the classpath is admissible.
     * @param view typed render mode — {@link StudioGraphView#TYPES} renders
     *             the type-only table; any other value (or missing) renders
     *             the default instance tree.
     */
    public record Query(String root, StudioGraphView view) implements Param._QueryString {}

    private final Bootstrap<?, ?> bootstrap;
    private final StudioGraph graph;

    public StudioGraphMarkdownAction(Bootstrap<?, ?> bootstrap) {
        this.bootstrap = Objects.requireNonNull(bootstrap, "bootstrap");
        this.graph = bootstrap.graph();
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(
                ctx.request().getParam("root"),
                StudioGraphView.parseOrDefault(ctx.request().getParam("view")));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<MarkdownContent> execute(Query query, EmptyParam.NoHeaders headers) {
        String md = switch (query.view() == null ? StudioGraphView.TREE : query.view()) {
            case TYPES -> graph.dumpTypesMarkdown();
            case TREE  -> graph.dumpMarkdown(resolveRoot(query.root()));
        };
        return CompletableFuture.completedFuture(new MarkdownContent(md));
    }

    /**
     * Resolve dump root. Empty/missing → use the {@link Bootstrap} (canonical
     * full-graph view). Otherwise look up by class FQN; fall back to Bootstrap
     * if the class isn't found among the graph's vertices (keeps the endpoint
     * useful rather than 404ing on a typo).
     */
    private Object resolveRoot(String fqn) {
        if (fqn == null || fqn.isBlank()) return bootstrap;
        for (Object v : graph.vertices()) {
            if (v.getClass().getName().equals(fqn)) return v;
        }
        return bootstrap;
    }
}
