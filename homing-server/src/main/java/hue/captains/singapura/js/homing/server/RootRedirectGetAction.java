package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

/**
 * GET {@code /} — emits a tiny HTML page that immediately redirects to
 * {@code /app?app=<rootSimpleName>}. Used as the home-page entry-point so
 * users typing the bare host (e.g. {@code http://localhost:8081/}) land on
 * the studio's main app instead of seeing a 404.
 *
 * <p>Implemented as an HTML response (meta-refresh + JS replace) rather than
 * a 302 because the host's {@code GetActionHandler} pipeline is built around
 * {@code TypedContent} bodies, not status-code redirects. The visible effect
 * is identical: the browser hops straight to the target URL without leaving
 * a flash of content.</p>
 */
public class RootRedirectGetAction
        implements GetAction<RoutingContext, EmptyParam.NoQuery, EmptyParam.NoHeaders, HtmlPageContent> {

    private final String rootSimpleName;

    public RootRedirectGetAction(String rootSimpleName) {
        this.rootSimpleName = rootSimpleName;
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, EmptyParam.NoQuery> queryStrMarshaller() {
        return ctx -> new EmptyParam.NoQuery();
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<HtmlPageContent> execute(EmptyParam.NoQuery query, EmptyParam.NoHeaders headers) {
        String target = "/app?app=" + rootSimpleName;
        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="refresh" content="0;url=%s">
                    <title>Redirecting…</title>
                </head>
                <body>
                    <script>window.location.replace(%s);</script>
                </body>
                </html>
                """.formatted(target, "\"" + target + "\"");
        return CompletableFuture.completedFuture(new HtmlPageContent(html));
    }
}
