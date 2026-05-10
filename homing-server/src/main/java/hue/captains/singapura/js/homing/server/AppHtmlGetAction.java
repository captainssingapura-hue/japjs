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
    private final ThemeRegistry themeRegistry;     // RFC 0002-ext1 — for the theme picker widget

    public AppHtmlGetAction(ModuleNameResolver nameResolver) {
        this(nameResolver, null, ThemeRegistry.EMPTY);
    }

    public AppHtmlGetAction(ModuleNameResolver nameResolver, SimpleAppResolver appResolver) {
        this(nameResolver, appResolver, ThemeRegistry.EMPTY);
    }

    public AppHtmlGetAction(ModuleNameResolver nameResolver, SimpleAppResolver appResolver, ThemeRegistry themeRegistry) {
        this.nameResolver = nameResolver;
        this.appResolver = appResolver;
        this.themeRegistry = themeRegistry != null ? themeRegistry : ThemeRegistry.EMPTY;
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

        AppModule<?, ?> app;
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
                if (!(instance instanceof AppModule<?, ?> a)) {
                    return CompletableFuture.failedFuture(
                            ResourceNotFound.wrongType(query.className(), "AppModule"));
                }
                app = a;
            }
        } catch (Exception e) {
            String resource = query.hasSimpleName() ? query.simpleName() : query.className();
            return CompletableFuture.failedFuture(ResourceNotFound.forClass(resource, e));
        }

        // If the URL didn't carry ?theme=, fall back to the first theme in
        // the registry so downstream (theme-vars / theme-globals fetches,
        // module URLs, the picker's selected option) all see a concrete
        // slug instead of null. The URL itself is untouched — the default
        // is applied in-flight, transparently.
        String effectiveTheme = query.theme();
        if (effectiveTheme == null && !themeRegistry.themes().isEmpty()) {
            effectiveTheme = themeRegistry.themes().get(0).slug();
        }

        String baseModuleUrl = nameResolver.resolve(app).basePath();
        String themeJs  = effectiveTheme != null ? "\"" + effectiveTheme + "\"" : "null";
        String localeJs = query.locale() != null ? "\"" + query.locale() + "\"" : "null";
        String themePickerHtml = renderThemePicker(effectiveTheme);

        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                </head>
                <body>
                    <div id="app"></div>
                    %s
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
                """.formatted(app.title(), themePickerHtml, themeJs, localeJs, baseModuleUrl);

        return CompletableFuture.completedFuture(new HtmlPageContent(html));
    }

    /**
     * RFC 0002-ext1 — fixed-position theme switcher widget. Lists every theme
     * registered in the deployment's {@link ThemeRegistry}. On change, navigates
     * to the same URL with an updated {@code ?theme=<slug>} parameter. When the
     * registry has 0 or 1 themes, the widget renders nothing.
     */
    private String renderThemePicker(String currentTheme) {
        var themes = themeRegistry.themes();
        if (themes.size() < 2) return "";   // no point switching if there's only one

        StringBuilder options = new StringBuilder();
        for (var theme : themes) {
            String slug   = theme.slug();
            String label  = theme.label();
            String selected = slug.equals(currentTheme) ? " selected" : "";
            options.append("<option value=\"").append(htmlEscape(slug)).append("\"").append(selected)
                   .append(">").append(htmlEscape(label)).append("</option>");
        }

        // Renders into an invisible "slot" div the StudioElements Header picks
        // up at render time (see StudioElements.js — Header looks for
        // #__theme_picker_slot__ and reparents it into the sticky header bar).
        // Until the slot is reparented, `display:none` keeps it from flashing
        // as a stray element on first paint. Once reparented, an inline style
        // override resets `display` to flex.
        //
        // The picker uses the inverted-surface tokens so it visually merges
        // with the header's dark band — no hardcoded palette, theme-aware.
        return """
                <div id="__theme_picker_slot__" style="display:none; align-items:center; gap:6px; margin-left:auto; font:13px system-ui,sans-serif;">
                    <label style="color:var(--color-text-on-inverted-muted);">Theme:</label>
                    <select id="__theme_picker__" style="font:inherit; border:1px solid rgba(255,255,255,0.15); background:transparent; color:var(--color-text-on-inverted); cursor:pointer; padding:2px 6px; border-radius:4px;">
                        %s
                    </select>
                </div>
                <style>
                    /* OS-rendered popup list of <option>s falls back to the
                       theme's raised surface so it stays legible regardless
                       of the dark header band. */
                    #__theme_picker__ option {
                        background: var(--color-surface-raised);
                        color: var(--color-text-primary);
                    }
                </style>
                <script>
                    (function () {
                        var sel = document.getElementById('__theme_picker__');
                        if (!sel) return;
                        sel.addEventListener('change', function () {
                            var params = new URLSearchParams(window.location.search);
                            if (sel.value) params.set('theme', sel.value);
                            else params.delete('theme');
                            window.location.search = params.toString();
                        });
                    })();
                </script>
                """.formatted(options.toString());
    }

    private static String htmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static ResourceNotFound notFound(String resource, String reason) {
        return new ResourceNotFound(
                new ResourceNotFound._InternalError(null, reason + ": " + resource),
                new ResourceNotFound._ExternalError(resource, reason)
        );
    }
}
