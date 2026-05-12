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
import hue.captains.singapura.js.homing.studio.base.app.BrandGetAction;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueGetAction;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueRegistry;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;
import hue.captains.singapura.js.homing.studio.base.theme.CssGroupImplRegistry;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanGetAction;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanRegistry;
import hue.captains.singapura.js.homing.studio.base.theme.HomingDefault;
import hue.captains.singapura.js.homing.studio.base.theme.StudioThemeRegistry;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesGetAction;
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

    /** Minimal entry — port + apps; uses {@link StudioThemeRegistry} and {@link HomingDefault}; no catalogues. */
    public static void start(int port, List<AppModule<?, ?>> apps) {
        start(port, apps, List.<Catalogue>of(), List.<Plan>of(), null,
                StudioThemeRegistry.INSTANCE, HomingDefault.INSTANCE, Map.of(), Map.of());
    }

    /** With a custom theme registry + default theme; no catalogues. */
    public static void start(int port,
                             List<AppModule<?, ?>> apps,
                             ThemeRegistry themeRegistry,
                             Theme defaultTheme) {
        start(port, apps, List.<Catalogue>of(), List.<Plan>of(), null, themeRegistry, defaultTheme, Map.of(), Map.of());
    }

    /** With catalogues + brand (RFC 0005). Catalogues registered explicitly per D1; brand provided alongside per D2. */
    public static void start(int port,
                             List<AppModule<?, ?>> apps,
                             List<Catalogue> catalogues,
                             StudioBrand brand) {
        start(port, apps, catalogues, List.of(), brand,
                StudioThemeRegistry.INSTANCE, HomingDefault.INSTANCE, Map.of(), Map.of());
    }

    /** With catalogues + plans + brand (RFC 0005-ext1). Plans registered explicitly. */
    public static void start(int port,
                             List<AppModule<?, ?>> apps,
                             List<Catalogue> catalogues,
                             List<Plan> plans,
                             StudioBrand brand) {
        start(port, apps, catalogues, plans, brand,
                StudioThemeRegistry.INSTANCE, HomingDefault.INSTANCE, Map.of(), Map.of());
    }

    /** Full form — adds extra GET/POST actions on top of the studio defaults; original signature kept for back-compat. */
    public static void start(int port,
                             List<AppModule<?, ?>> apps,
                             ThemeRegistry themeRegistry,
                             Theme defaultTheme,
                             Map<String, GetAction<RoutingContext, ?, ?, ?>> extraGetActions,
                             Map<String, PostAction<RoutingContext, ?, ?, ?>> extraPostActions) {
        start(port, apps, List.of(), List.of(), null, themeRegistry, defaultTheme, extraGetActions, extraPostActions);
    }

    /** Full form with catalogues + plans — RFC 0005-ext1's preferred entry point. */
    public static void start(int port,
                             List<AppModule<?, ?>> apps,
                             List<Catalogue> catalogues,
                             List<Plan> plans,
                             StudioBrand brand,
                             ThemeRegistry themeRegistry,
                             Theme defaultTheme,
                             Map<String, GetAction<RoutingContext, ?, ?, ?>> extraGetActions,
                             Map<String, PostAction<RoutingContext, ?, ?, ?>> extraPostActions) {

        if (apps.isEmpty()) {
            throw new IllegalArgumentException("StudioBootstrap.start: at least one AppModule required");
        }
        if (!catalogues.isEmpty() && brand == null) {
            throw new IllegalArgumentException(
                    "StudioBootstrap.start: a non-empty catalogues list requires a non-null StudioBrand");
        }
        var nameResolver = new QueryParamResolver();
        var appResolver  = new SimpleAppResolver(apps);
        // First app in the list is the studio's home — / redirects to it.
        AppModule<?, ?> rootApp = apps.get(0);

        var registry = buildRegistry(
                nameResolver, appResolver,
                catalogues, plans, brand,
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
     * for tests, or for downstream that wants its own host. Backward-compat
     * overload (no catalogues).
     */
    public static ActionRegistry<RoutingContext> buildRegistry(
            QueryParamResolver nameResolver,
            SimpleAppResolver appResolver,
            ThemeRegistry themeRegistry,
            Theme defaultTheme,
            AppModule<?, ?> rootApp,
            Map<String, GetAction<RoutingContext, ?, ?, ?>> extraGetActions,
            Map<String, PostAction<RoutingContext, ?, ?, ?>> extraPostActions) {
        return buildRegistry(nameResolver, appResolver, List.of(), List.of(), null,
                themeRegistry, defaultTheme, rootApp, extraGetActions, extraPostActions);
    }

    /**
     * Full form — composes the standard studio registry plus the RFC 0005 catalogue
     * stack and (RFC 0005-ext1) the plan stack when catalogues / plans are non-empty.
     */
    public static ActionRegistry<RoutingContext> buildRegistry(
            QueryParamResolver nameResolver,
            SimpleAppResolver appResolver,
            List<Catalogue> catalogues,
            List<Plan> plans,
            StudioBrand brand,
            ThemeRegistry themeRegistry,
            Theme defaultTheme,
            AppModule<?, ?> rootApp,
            Map<String, GetAction<RoutingContext, ?, ?, ?>> extraGetActions,
            Map<String, PostAction<RoutingContext, ?, ?, ?>> extraPostActions) {

        var inner = new HomingActionRegistry(
                nameResolver, appResolver,
                ResourceReader.fromSystemProperty(),
                themeRegistry);

        // Studio defaults: typed-impl-backed /css-content + classpath /doc +
        // root redirect → home app.
        // RFC 0004 + RFC 0005: walk BOTH the app closure AND the registered catalogues
        // for DocProviders, build the typed DocRegistry. Catalogues are no longer
        // AppModules (RFC 0005), so AppResolver alone misses Catalogue-side DocProviders
        // like BuildingBlocksCatalogue.
        var docProviders = new java.util.ArrayList<hue.captains.singapura.js.homing.studio.base.DocProvider>();
        for (AppModule<?, ?> app : appResolver.apps()) {
            if (app instanceof hue.captains.singapura.js.homing.studio.base.DocProvider p) {
                docProviders.add(p);
            }
        }
        for (Catalogue c : catalogues) {
            if (c instanceof hue.captains.singapura.js.homing.studio.base.DocProvider p) {
                docProviders.add(p);
            }
        }
        var allDocs = new java.util.ArrayList<hue.captains.singapura.js.homing.studio.base.Doc>();
        for (var p : docProviders) {
            allDocs.addAll(p.docs());
        }
        var docRegistry      = new DocRegistry(allDocs);
        var cssContentAction = new CssContentGetAction(CssGroupImplRegistry.ALL, defaultTheme);
        var docAction        = new DocGetAction(docRegistry);
        var themesAction     = new ThemesGetAction(themeRegistry);
        var brandAction      = new BrandGetAction(brand, !catalogues.isEmpty());

        // RFC 0005: when catalogues are registered, root redirects to the brand's home
        // catalogue (full URL with ?id=<class-fqn>). Otherwise legacy "first app" behaviour.
        final RootRedirectGetAction rootRedirect = (!catalogues.isEmpty() && brand != null)
                ? RootRedirectGetAction.toUrl(CatalogueAppHost.urlFor(brand.homeApp()))
                : new RootRedirectGetAction(rootApp.simpleName());

        // RFC 0005: typed Catalogue tree + CatalogueAppHost. Built only when catalogues
        // are explicitly registered. Empty list → no catalogue endpoint registered.
        final CatalogueGetAction catalogueAction;
        final CatalogueRegistry catalogueRegistry;
        if (!catalogues.isEmpty()) {
            catalogueRegistry = new CatalogueRegistry(brand, docRegistry, catalogues);
            catalogueAction = new CatalogueGetAction(catalogueRegistry);
        } else {
            catalogueRegistry = null;
            catalogueAction   = null;
        }

        // RFC 0004-ext1: typed References JSON for the DocReader's References section.
        // RFC 0005-ext2: also carries the breadcrumb chain so DocReader can render the
        // catalogue path above the doc title (replacing the old flat "Home" stub).
        var docRefsAction    = new DocRefsGetAction(docRegistry, catalogueRegistry);

        // RFC 0005-ext1: typed Plan registry + PlanAppHost. Built only when plans are
        // explicitly registered. Boot-time validations (phase ID uniqueness, decision ID
        // uniqueness, phase dep targets exist, doc reachability) all enforced.
        final PlanGetAction planAction;
        if (!plans.isEmpty()) {
            var planRegistry = new PlanRegistry(plans, docRegistry);
            planAction = new PlanGetAction(planRegistry, catalogueRegistry);
        } else {
            planAction = null;
        }

        return new ActionRegistry<>() {
            @Override
            public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
                Map<String, GetAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.getActions());
                all.put("/",            rootRedirect);
                all.put("/css-content", cssContentAction);
                all.put("/doc",         docAction);
                all.put("/doc-refs",    docRefsAction);
                all.put("/themes",      themesAction);
                all.put("/brand",       brandAction);
                if (catalogueAction != null) {
                    all.put("/catalogue", catalogueAction);
                }
                if (planAction != null) {
                    all.put("/plan", planAction);
                }
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
