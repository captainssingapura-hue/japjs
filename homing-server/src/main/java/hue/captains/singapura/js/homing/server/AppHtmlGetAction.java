package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

/**
 * GET {@code /app?app=<simpleName>}    — the RFC 0001 contract; resolves via {@link SimpleAppResolver}.
 * <br>
 * GET {@code /app?class=<canonical>}   — legacy fallback; uses reflection.
 *
 * <p>If a {@link SimpleAppResolver} is configured, {@code ?app=} is supported.
 * If not, only {@code ?class=} works (legacy mode for servers built before Step 07).</p>
 *
 * <p>Proxy-app names are explicitly rejected: a proxy is a URL builder, not a
 * navigable target. Requests for {@code ?app=&lt;proxy-name&gt;} return 404.</p>
 */
public class AppHtmlGetAction
        implements GetAction<RoutingContext, AppQuery, EmptyParam.NoHeaders, HtmlPageContent> {

    private final ModuleNameResolver nameResolver;
    private final SimpleAppResolver appResolver;   // may be null in legacy-only mode

    public AppHtmlGetAction(ModuleNameResolver nameResolver) {
        this(nameResolver, null);
    }

    public AppHtmlGetAction(ModuleNameResolver nameResolver, SimpleAppResolver appResolver) {
        this.nameResolver = nameResolver;
        this.appResolver = appResolver;
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, AppQuery> queryStrMarshaller() {
        return ctx -> new AppQuery(
                ctx.request().getParam("app"),
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
    public CompletableFuture<HtmlPageContent> execute(AppQuery query, EmptyParam.NoHeaders headers) {
        if (!query.hasSimpleName() && !query.hasClassName()) {
            return CompletableFuture.failedFuture(ResourceNotFound.missingClass());
        }

        AppModule<?> app;
        try {
            if (query.hasSimpleName()) {
                if (appResolver == null) {
                    return CompletableFuture.failedFuture(notFound(query.simpleName(),
                            "?app= dispatch requires a SimpleAppResolver — server was constructed without one"));
                }
                if (appResolver.resolveProxy(query.simpleName()) != null) {
                    return CompletableFuture.failedFuture(notFound(query.simpleName(),
                            "Proxy apps are not routable; they are URL builders for typed external links"));
                }
                app = appResolver.resolveApp(query.simpleName());
                if (app == null) {
                    return CompletableFuture.failedFuture(notFound(query.simpleName(),
                            "No app registered with this simple name"));
                }
            } else {
                // Legacy ?class= path — kept for backwards compatibility during Step 11 migration.
                Class<?> clazz = Class.forName(query.className());
                Object instance;
                try {
                    var instanceField = clazz.getField("INSTANCE");
                    instance = instanceField.get(null);
                } catch (NoSuchFieldException e) {
                    instance = clazz.getDeclaredConstructor().newInstance();
                }
                if (!(instance instanceof AppModule<?> a)) {
                    return CompletableFuture.failedFuture(
                            ResourceNotFound.wrongType(query.className(), "AppModule"));
                }
                app = a;
            }
        } catch (Exception e) {
            String resource = query.hasSimpleName() ? query.simpleName() : query.className();
            return CompletableFuture.failedFuture(ResourceNotFound.forClass(resource, e));
        }

        String baseModuleUrl = nameResolver.resolve(app).basePath();
        String themeJs  = query.theme()  != null ? "\"" + query.theme()  + "\"" : "null";
        String localeJs = query.locale() != null ? "\"" + query.locale() + "\"" : "null";

        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                </head>
                <body>
                    <div id="app"></div>
                    <script type="module">
                        // RFC 0002: theme is opt-in. If the URL didn't carry ?theme=, we
                        // forward nothing and the server resolves to its registered default.
                        // Browser prefers-color-scheme is intentionally ignored — themes are
                        // explicit, not auto-derived.
                        const theme = %s;
                        const locale = %s || navigator.language;
                        if (theme) document.documentElement.style.colorScheme = theme;
                        let moduleUrl = "%s" + "&locale=" + encodeURIComponent(locale);
                        if (theme) moduleUrl += "&theme=" + encodeURIComponent(theme);
                        const { appMain } = await import(moduleUrl);
                        appMain(document.getElementById("app"));
                    </script>
                </body>
                </html>
                """.formatted(app.title(), themeJs, localeJs, baseModuleUrl);

        return CompletableFuture.completedFuture(new HtmlPageContent(html));
    }

    private static ResourceNotFound notFound(String resource, String reason) {
        return new ResourceNotFound(
                new ResourceNotFound._InternalError(null, reason + ": " + resource),
                new ResourceNotFound._ExternalError(resource, reason)
        );
    }
}
