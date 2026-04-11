package hue.captains.singapura.japjs.server;

import hue.captains.singapura.japjs.core.AppModule;
import hue.captains.singapura.japjs.core.ModuleNameResolver;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CompletableFuture;

public class AppHtmlGetAction
        implements GetAction<RoutingContext, ModuleQuery, EmptyParam.NoHeaders, HtmlPageContent> {

    private final ModuleNameResolver nameResolver;

    public AppHtmlGetAction(ModuleNameResolver nameResolver) {
        this.nameResolver = nameResolver;
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
    public CompletableFuture<HtmlPageContent> execute(ModuleQuery query, EmptyParam.NoHeaders headers) {
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

            if (!(instance instanceof AppModule<?> app)) {
                return CompletableFuture.failedFuture(ResourceNotFound.wrongType(query.className(), "AppModule"));
            }

            String baseModuleUrl = nameResolver.resolve(app).basePath();
            String themeJs = query.theme() != null ? "\"" + query.theme() + "\"" : "null";
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
                            const theme = %s || (matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light");
                            const locale = %s || navigator.language;
                            document.documentElement.style.colorScheme = theme;
                            const moduleUrl = "%s" + "&theme=" + encodeURIComponent(theme) + "&locale=" + encodeURIComponent(locale);
                            const { appMain } = await import(moduleUrl);
                            appMain(document.getElementById("app"));
                        </script>
                    </body>
                    </html>
                    """.formatted(app.title(), themeJs, localeJs, baseModuleUrl);

            return CompletableFuture.completedFuture(new HtmlPageContent(html));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(ResourceNotFound.forClass(query.className(), e));
        }
    }
}
