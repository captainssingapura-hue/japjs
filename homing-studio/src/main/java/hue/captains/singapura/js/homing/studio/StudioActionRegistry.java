package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.server.CssContentGetAction;
import hue.captains.singapura.js.homing.server.HomingActionRegistry;
import hue.captains.singapura.js.homing.server.ThemeGlobalsGetAction;
import hue.captains.singapura.js.homing.server.ThemeVarsGetAction;
import hue.captains.singapura.js.homing.studio.base.DocGetAction;
import hue.captains.singapura.js.homing.studio.base.DocRefsGetAction;
import hue.captains.singapura.js.homing.studio.base.DocRegistry;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueGetAction;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueRegistry;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;
import hue.captains.singapura.js.homing.studio.base.theme.CssGroupImplRegistry;
import hue.captains.singapura.js.homing.studio.base.theme.HomingDefault;
import hue.captains.singapura.js.homing.studio.base.theme.StudioThemeRegistry;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Composes the standard {@link HomingActionRegistry} routes with the studio-specific
 * routes:
 * <ul>
 *   <li>{@code /doc} — bytes of a {@link hue.captains.singapura.js.homing.studio.base.Doc}
 *       resolved by UUID against the studio's
 *       {@link hue.captains.singapura.js.homing.studio.base.DocRegistry} (RFC 0004)</li>
 *   <li>{@code /css-content} — typed-impl-backed CSS body (RFC 0002)</li>
 *   <li>{@code /theme-vars}, {@code /theme-globals} — theme bundles (RFC 0002-ext1)</li>
 * </ul>
 *
 * <p>RFC 0004 deleted the legacy {@code /doc-content} filesystem endpoint and the
 * {@code homing.studio.docsRoot} system property; public docs now ship as classpath
 * resources alongside their {@link hue.captains.singapura.js.homing.studio.base.Doc}
 * records.</p>
 */
public class StudioActionRegistry implements ActionRegistry<RoutingContext> {

    private final HomingActionRegistry inner;
    private final DocGetAction docAction;
    private final DocRefsGetAction docRefsAction;
    private final CssContentGetAction cssContentAction;
    private final ThemeVarsGetAction themeVarsAction;
    private final ThemeGlobalsGetAction themeGlobalsAction;
    private final CatalogueGetAction catalogueAction;   // null when no catalogues registered

    public StudioActionRegistry(ModuleNameResolver nameResolver) {
        this(nameResolver, null, java.util.List.of(), null);
    }

    public StudioActionRegistry(ModuleNameResolver nameResolver, SimpleAppResolver appResolver) {
        this(nameResolver, appResolver, java.util.List.of(), null);
    }

    /**
     * RFC 0005: registers the catalogue tree + brand alongside the existing studio
     * actions. {@code catalogues} may be empty; {@code brand} is required iff
     * catalogues are non-empty.
     */
    public StudioActionRegistry(ModuleNameResolver nameResolver,
                                SimpleAppResolver appResolver,
                                java.util.List<Catalogue> catalogues,
                                StudioBrand brand) {
        // RFC 0002-ext1 Phase 10/12: pass StudioThemeRegistry through to the
        // inner HomingActionRegistry so AppHtmlGetAction renders the theme
        // picker widget driven by the studio's registered themes.
        this.inner = new HomingActionRegistry(
                nameResolver, appResolver,
                hue.captains.singapura.js.homing.core.util.ResourceReader.fromSystemProperty(),
                StudioThemeRegistry.INSTANCE);
        // RFC 0004 + RFC 0005: walk BOTH the app closure AND the registered catalogues
        // for DocProviders. Catalogues are no longer AppModules; AppResolver alone
        // misses Catalogue-side DocProviders like BuildingBlocksCatalogue.
        var docProviders = new java.util.ArrayList<hue.captains.singapura.js.homing.studio.base.DocProvider>();
        if (appResolver != null) {
            for (var app : appResolver.apps()) {
                if (app instanceof hue.captains.singapura.js.homing.studio.base.DocProvider p) {
                    docProviders.add(p);
                }
            }
        }
        for (var c : catalogues) {
            if (c instanceof hue.captains.singapura.js.homing.studio.base.DocProvider p) {
                docProviders.add(p);
            }
        }
        var allDocs = new java.util.ArrayList<hue.captains.singapura.js.homing.studio.base.Doc>();
        for (var p : docProviders) {
            allDocs.addAll(p.docs());
        }
        DocRegistry docRegistry = new DocRegistry(allDocs);
        this.docAction = new DocGetAction(docRegistry);
        // RFC 0004-ext1: typed References JSON for the DocReader's References section.
        this.docRefsAction = new DocRefsGetAction(docRegistry);
        // RFC 0002 Phase 04: CSS rendered from typed CssGroupImpls in CssGroupImplRegistry,
        // with HomingDefault as the default theme.
        this.cssContentAction = new CssContentGetAction(
                CssGroupImplRegistry.ALL,
                HomingDefault.INSTANCE);
        // RFC 0002-ext1 Phase 10: theme-bundle endpoints backed by the studio's populated
        // ThemeRegistry. /theme-vars and /theme-globals serve real content.
        this.themeVarsAction    = new ThemeVarsGetAction(StudioThemeRegistry.INSTANCE, HomingDefault.INSTANCE);
        this.themeGlobalsAction = new ThemeGlobalsGetAction(StudioThemeRegistry.INSTANCE, HomingDefault.INSTANCE);

        // RFC 0005: typed catalogue tree + /catalogue endpoint.
        if (!catalogues.isEmpty()) {
            if (brand == null) {
                throw new IllegalArgumentException(
                        "StudioActionRegistry: a non-empty catalogues list requires a non-null StudioBrand");
            }
            var catalogueRegistry = new CatalogueRegistry(brand, docRegistry, catalogues);
            this.catalogueAction = new CatalogueGetAction(catalogueRegistry);
        } else {
            this.catalogueAction = null;
        }
    }

    @Override
    public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
        Map<String, GetAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.getActions());
        all.put("/doc",           docAction);
        all.put("/doc-refs",      docRefsAction);
        all.put("/css-content",   cssContentAction);
        all.put("/theme-vars",    themeVarsAction);
        all.put("/theme-globals", themeGlobalsAction);
        if (catalogueAction != null) {
            all.put("/catalogue", catalogueAction);
        }
        return Map.copyOf(all);
    }

    @Override
    public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
        return inner.postActions();
    }
}
