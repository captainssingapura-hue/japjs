package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.util.ResourceReader;
import hue.captains.singapura.js.homing.server.CssContentGetAction;
import hue.captains.singapura.js.homing.server.HomingActionRegistry;
import hue.captains.singapura.js.homing.server.QueryParamResolver;
import hue.captains.singapura.js.homing.server.RootRedirectGetAction;
import hue.captains.singapura.js.homing.server.ThemeRegistry;
import hue.captains.singapura.js.homing.studio.base.theme.CssGroupImplRegistry;
import hue.captains.singapura.js.homing.studio.base.theme.HomingDefault;
import hue.captains.singapura.js.homing.studio.base.theme.StudioThemeRegistry;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One-call bootstrap for downstream studio servers.
 *
 * <p>Wires the standard studio stack — {@link HomingActionRegistry} +
 * {@link CssContentGetAction} (typed-impl backed) + {@link DocGetAction} +
 * {@link StudioThemeRegistry} + {@link HomingDefault} as the default theme +
 * {@link QueryParamResolver} + {@link SimpleAppResolver} + {@link VertxActionHost}
 * — from a list of {@link AppModule}s and a port number. Downstream main
 * methods become a single call:</p>
 *
 * <pre>{@code
 * public class MyStudioServer {
 *     public static void main(String[] args) {
 *         StudioBootstrap.start(8081, List.of(MyHomepage.INSTANCE));
 *     }
 * }
 * }</pre>
 *
 * <p>Overloads accept a custom {@link ThemeRegistry} (for downstream themes
 * stacked on top of the studio defaults) and extra GET/POST actions (for
 * downstream-specific endpoints).</p>
 */
public final class StudioBootstrap {

    private StudioBootstrap() {}

    /** Minimal entry — port + apps; uses {@link StudioThemeRegistry} and {@link HomingDefault}. */
    public static void start(int port, List<AppModule<?>> apps) {
        start(port, apps, StudioThemeRegistry.INSTANCE, HomingDefault.INSTANCE, Map.of(), Map.of());
    }

    /** With a custom theme registry + default theme. */
    public static void start(int port,
                             List<AppModule<?>> apps,
                             ThemeRegistry themeRegistry,
                             Theme defaultTheme) {
        start(port, apps, themeRegistry, defaultTheme, Map.of(), Map.of());
    }

    /** Full form — adds extra GET/POST actions on top of the studio defaults. */
    public static void start(int port,
                             List<AppModule<?>> apps,
                             ThemeRegistry themeRegistry,
                             Theme defaultTheme,
                             Map<String, GetAction<RoutingContext, ?, ?, ?>> extraGetActions,
                             Map<String, PostAction<RoutingContext, ?, ?, ?>> extraPostActions) {

        if (apps.isEmpty()) {
            throw new IllegalArgumentException("StudioBootstrap.start: at least one AppModule required");
        }
        var nameResolver = new QueryParamResolver();
        var appResolver  = new SimpleAppResolver(apps);
        // First app in the list is the studio's home — / redirects to it.
        AppModule<?> rootApp = apps.get(0);

        var registry = buildRegistry(
                nameResolver, appResolver,
                themeRegistry, defaultTheme, rootApp,
                extraGetActions, extraPostActions);

        var host = new VertxActionHost(registry, port);
        host.start().onSuccess(server -> {
            int actualPort = server.actualPort();
            System.out.println("Studio listening on port " + actualPort);
            System.out.println("Registered apps: " + appResolver.apps().size()
                    + " · proxies: " + appResolver.proxies().size());
            System.out.println();
            for (var app : appResolver.apps()) {
                System.out.println("  http://localhost:" + actualPort + "/app?app=" + app.simpleName()
                        + "    (" + app.title() + ")");
            }
        }).onFailure(err -> {
            System.err.println("Failed to start: " + err.getMessage());
            System.exit(1);
        });
    }

    /**
     * Compose the standard studio registry without starting Vert.x — useful
     * for tests, or for downstream that wants its own host.
     */
    public static ActionRegistry<RoutingContext> buildRegistry(
            QueryParamResolver nameResolver,
            SimpleAppResolver appResolver,
            ThemeRegistry themeRegistry,
            Theme defaultTheme,
            AppModule<?> rootApp,
            Map<String, GetAction<RoutingContext, ?, ?, ?>> extraGetActions,
            Map<String, PostAction<RoutingContext, ?, ?, ?>> extraPostActions) {

        var inner = new HomingActionRegistry(
                nameResolver, appResolver,
                ResourceReader.fromSystemProperty(),
                themeRegistry);

        // Studio defaults: typed-impl-backed /css-content + classpath /doc +
        // root redirect → home app.
        var cssContentAction = new CssContentGetAction(CssGroupImplRegistry.ALL, defaultTheme);
        var docAction        = new DocGetAction();
        var rootRedirect     = new RootRedirectGetAction(rootApp.simpleName());

        return new ActionRegistry<>() {
            @Override
            public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
                Map<String, GetAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.getActions());
                all.put("/",            rootRedirect);
                all.put("/css-content", cssContentAction);
                all.put("/doc",         docAction);
                all.putAll(extraGetActions);
                return Map.copyOf(all);
            }

            @Override
            public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
                Map<String, PostAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.postActions());
                all.putAll(extraPostActions);
                return Map.copyOf(all);
            }
        };
    }
}
