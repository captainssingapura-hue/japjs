package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.server.AppMeta;
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
import hue.captains.singapura.js.homing.studio.base.theme.ThemesGetAction;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanGetAction;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanRegistry;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * RFC 0012 — the typed studio bootstrap. Construct with a {@link Fixtures}
 * and a {@link RuntimeParams}; call {@link #start()}. No static methods, no
 * INSTANCE field, no parameter explosion — the record IS the functional
 * object, satisfying the Functional Objects doctrine by construction.
 *
 * <pre>{@code
 * // Standalone studio.
 * new Bootstrap<>(
 *         new DefaultFixtures<>(new Umbrella.Solo<>(HomingStudio.INSTANCE)),
 *         new DefaultRuntimeParams(8080)
 * ).start();
 *
 * // Multi-studio composition.
 * Umbrella<Studio<?>> tree = new Umbrella.Group<>("Homing Demo", "Three studios, one server.",
 *         List.of(new Umbrella.Solo<>(MultiStudio.INSTANCE),
 *                 new Umbrella.Solo<>(DemoBaseStudio.INSTANCE),
 *                 new Umbrella.Solo<>(SkillsStudio.INSTANCE),
 *                 new Umbrella.Solo<>(HomingStudio.INSTANCE)));
 * new Bootstrap<>(new DefaultFixtures<>(tree), new DefaultRuntimeParams(8082)).start();
 * }</pre>
 *
 * @param <S> the studio type at the umbrella's leaves
 * @param <F> the {@link Fixtures} subtype harnessing {@code S}
 */
public record Bootstrap<S extends Studio<?>, F extends Fixtures<S>>(
        F fixtures,
        RuntimeParams params) {

    public Bootstrap {
        Objects.requireNonNull(fixtures, "fixtures");
        Objects.requireNonNull(params,   "params");
    }

    /**
     * Compose the studio set into a typed action registry and boot the
     * Vert.x host. Blocks only briefly during registry construction; the
     * Vert.x server starts asynchronously.
     */
    public void start() {
        var registry = compose();
        var host = new VertxActionHost(registry, params.port());
        host.start().onSuccess(server -> {
            int actualPort = server.actualPort();
            System.out.println("Studio listening on port " + actualPort);
            for (var studio : fixtures.umbrella().studios()) {
                System.out.println("  · " + studio.getClass().getSimpleName()
                        + " (home: " + studio.home().getClass().getSimpleName() + ")");
            }
        }).onFailure(err -> {
            System.err.println("Failed to start: " + err.getMessage());
            System.exit(1);
        });
    }

    /**
     * Compose the studio set into an {@link ActionRegistry} without starting
     * Vert.x — useful for tests, or for downstream that wants its own host.
     */
    public ActionRegistry<RoutingContext> compose() {
        var studios = fixtures.umbrella().studios();
        if (studios.isEmpty()) {
            throw new IllegalArgumentException("Bootstrap.compose: umbrella has no studios");
        }

        // --- Union apps: each studio's intrinsic apps + harness apps. Dedup by class.
        var apps = unionAppsByClass(studios, fixtures.harnessApps());
        if (apps.isEmpty()) {
            throw new IllegalArgumentException("Bootstrap.compose: at least one AppModule required");
        }

        // --- Union catalogues: each studio's catalogues(). Dedup by class.
        var catalogues = unionCataloguesByClass(studios);

        // --- Union plans: each studio's plans(). Dedup by class.
        var plans = unionPlansByClass(studios);

        // --- Brand: from fixtures (default is first studio's standaloneBrand).
        StudioBrand brand = fixtures.brand();
        if (!catalogues.isEmpty() && brand == null) {
            throw new IllegalArgumentException(
                    "Bootstrap.compose: a non-empty catalogues list requires a non-null StudioBrand "
                            + "(supply via Studio.standaloneBrand() or Fixtures.brand())");
        }

        // --- Theme registry + default theme + resource reader come from fixtures + params.
        ThemeRegistry themeRegistry = fixtures.themeRegistry();
        Theme defaultTheme = fixtures.defaultTheme();

        // --- Resolvers + registries.
        var nameResolver = new QueryParamResolver();
        var appResolver  = new SimpleAppResolver(apps);
        var rootApp      = apps.get(0); // legacy "first app" fallback when no catalogues

        var appMeta = (brand != null && brand.label() != null && !brand.label().isBlank())
                ? new AppMeta(brand.label())
                : AppMeta.DEFAULT;
        var inner = new HomingActionRegistry(
                nameResolver, appResolver, params.resourceReader(),
                themeRegistry, appMeta);

        // --- Doc registry — walk DocProviders from apps AND catalogues (RFC 0004 + RFC 0005).
        var docProviders = new ArrayList<DocProvider>();
        for (AppModule<?, ?> app : appResolver.apps()) {
            if (app instanceof DocProvider p) docProviders.add(p);
        }
        for (Catalogue<?> c : catalogues) {
            if (c instanceof DocProvider p) docProviders.add(p);
        }
        var allDocs = new ArrayList<Doc>();
        for (var p : docProviders) allDocs.addAll(p.docs());
        var docRegistry = new DocRegistry(allDocs);

        // --- Standard studio actions.
        var cssContentAction = new CssContentGetAction(CssGroupImplRegistry.ALL, defaultTheme);
        var docAction        = new DocGetAction(docRegistry);
        var themesAction     = new ThemesGetAction(themeRegistry);
        var brandAction      = new BrandGetAction(brand, !catalogues.isEmpty());

        // --- Root redirect: brand home catalogue (catalogues present) or first app.
        final RootRedirectGetAction rootRedirect = (!catalogues.isEmpty() && brand != null)
                ? RootRedirectGetAction.toUrl(CatalogueAppHost.urlFor(brand.homeApp()))
                : new RootRedirectGetAction(rootApp.simpleName());

        // --- Catalogue registry + action (RFC 0005), only when catalogues registered.
        final CatalogueGetAction catalogueAction;
        final CatalogueRegistry catalogueRegistry;
        if (!catalogues.isEmpty()) {
            catalogueRegistry = new CatalogueRegistry(brand, docRegistry, catalogues);
            catalogueAction = new CatalogueGetAction(catalogueRegistry);
        } else {
            catalogueRegistry = null;
            catalogueAction   = null;
        }

        // --- Doc-refs action (RFC 0004-ext1 / RFC 0005-ext2 — carries breadcrumb chain).
        var docRefsAction = new DocRefsGetAction(docRegistry, catalogueRegistry);

        // --- Plan action (RFC 0005-ext1), only when plans registered.
        final PlanGetAction planAction;
        if (!plans.isEmpty()) {
            var planRegistry = new PlanRegistry(plans, docRegistry);
            planAction = new PlanGetAction(planRegistry, catalogueRegistry);
        } else {
            planAction = null;
        }

        // --- Compose final ActionRegistry.
        final var harnessGetActions  = fixtures.harnessGetActions();
        final var harnessPostActions = fixtures.harnessPostActions();
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
                if (catalogueAction != null) all.put("/catalogue", catalogueAction);
                if (planAction      != null) all.put("/plan",      planAction);
                all.putAll(harnessGetActions);
                return Map.copyOf(all);
            }

            @Override
            public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
                Map<String, PostAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.postActions());
                all.putAll(harnessPostActions);
                return Map.copyOf(all);
            }
        };
    }

    // ----- composition helpers -----

    private static List<AppModule<?, ?>> unionAppsByClass(
            List<? extends Studio<?>> studios,
            List<AppModule<?, ?>> harnessApps) {
        // Harness apps first — they're the framework's spine, studios layer on top.
        var byClass = new LinkedHashMap<Class<?>, AppModule<?, ?>>();
        for (var app : harnessApps) putAppDedup(byClass, app);
        for (var studio : studios) {
            for (var app : studio.apps()) putAppDedup(byClass, app);
        }
        return List.copyOf(byClass.values());
    }

    private static void putAppDedup(Map<Class<?>, AppModule<?, ?>> byClass, AppModule<?, ?> app) {
        var existing = byClass.putIfAbsent(app.getClass(), app);
        if (existing != null && existing != app) {
            throw new IllegalStateException(
                    "Bootstrap.compose: two instances of AppModule class "
                            + app.getClass().getName()
                            + " supplied — same class must mean same instance");
        }
    }

    private static List<Catalogue<?>> unionCataloguesByClass(List<? extends Studio<?>> studios) {
        var byClass = new LinkedHashMap<Class<?>, Catalogue<?>>();
        for (var studio : studios) {
            for (Catalogue<?> c : studio.catalogues()) {
                byClass.putIfAbsent(c.getClass(), c);
            }
        }
        return List.copyOf(byClass.values());
    }

    private static List<Plan> unionPlansByClass(List<? extends Studio<?>> studios) {
        var byClass = new LinkedHashMap<Class<?>, Plan>();
        for (var studio : studios) {
            for (Plan p : studio.plans()) {
                byClass.putIfAbsent(p.getClass(), p);
            }
        }
        return List.copyOf(byClass.values());
    }
}
