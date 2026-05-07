package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.server.CssContentGetAction;
import hue.captains.singapura.js.homing.server.HomingActionRegistry;
import hue.captains.singapura.js.homing.server.ThemeGlobalsGetAction;
import hue.captains.singapura.js.homing.server.ThemeVarsGetAction;
import hue.captains.singapura.js.homing.studio.base.DocContentGetAction;
import hue.captains.singapura.js.homing.studio.base.DocGetAction;
import hue.captains.singapura.js.homing.studio.base.theme.CssGroupImplRegistry;
import hue.captains.singapura.js.homing.studio.base.theme.HomingDefault;
import hue.captains.singapura.js.homing.studio.base.theme.StudioThemeRegistry;
import hue.captains.singapura.js.homing.studio.rename.RenameDataGetAction;
import hue.captains.singapura.js.homing.studio.rfc0001.StepDataGetAction;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002DataGetAction;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1DataGetAction;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Composes the standard {@link HomingActionRegistry} routes with the studio-specific
 * routes:
 * <ul>
 *   <li>{@code /doc-content} — markdown from the configured filesystem docs root (legacy)</li>
 *   <li>{@code /doc} — markdown from the classpath, looked up by typed Doc path
 *       (registered by virtue of depending on {@code homing-studio-base})</li>
 * </ul>
 */
public class StudioActionRegistry implements ActionRegistry<RoutingContext> {

    private final HomingActionRegistry inner;
    private final DocContentGetAction docContentAction;
    private final DocGetAction docAction;
    private final StepDataGetAction stepDataAction;
    private final RenameDataGetAction renameDataAction;
    private final Rfc0002DataGetAction rfc0002DataAction;
    private final Rfc0002Ext1DataGetAction rfc0002Ext1DataAction;
    private final CssContentGetAction cssContentAction;
    private final ThemeVarsGetAction themeVarsAction;
    private final ThemeGlobalsGetAction themeGlobalsAction;

    public StudioActionRegistry(ModuleNameResolver nameResolver) {
        this(nameResolver, Path.of(System.getProperty("homing.studio.docsRoot", "docs")), null);
    }

    public StudioActionRegistry(ModuleNameResolver nameResolver, Path docsRoot) {
        this(nameResolver, docsRoot, null);
    }

    public StudioActionRegistry(ModuleNameResolver nameResolver, Path docsRoot, SimpleAppResolver appResolver) {
        // RFC 0002-ext1 Phase 10/12: pass StudioThemeRegistry through to the
        // inner HomingActionRegistry so AppHtmlGetAction renders the theme
        // picker widget driven by the studio's registered themes.
        this.inner = new HomingActionRegistry(
                nameResolver, appResolver,
                hue.captains.singapura.js.homing.core.util.ResourceReader.fromSystemProperty(),
                StudioThemeRegistry.INSTANCE);
        this.docContentAction = new DocContentGetAction(docsRoot);
        this.docAction = new DocGetAction();
        this.stepDataAction = new StepDataGetAction();
        this.renameDataAction = new RenameDataGetAction();
        this.rfc0002DataAction = new Rfc0002DataGetAction();
        this.rfc0002Ext1DataAction = new Rfc0002Ext1DataGetAction();
        // RFC 0002 Phase 04: CSS now rendered from typed CssGroupImpls in
        // CssGroupImplRegistry, with HomingDefault as the default theme.
        // Replaces the inner HomingActionRegistry's file-based CssContentGetAction
        // (we override the same /css-content route in getActions() below).
        this.cssContentAction = new CssContentGetAction(
                CssGroupImplRegistry.ALL,
                HomingDefault.INSTANCE);
        // RFC 0002-ext1 Phase 10: theme-bundle endpoints backed by the studio's
        // populated ThemeRegistry. /theme-vars and /theme-globals now serve real
        // content (HomingDefault.Vars + HomingDefault.Globals) instead of empty.
        this.themeVarsAction    = new ThemeVarsGetAction(StudioThemeRegistry.INSTANCE, HomingDefault.INSTANCE);
        this.themeGlobalsAction = new ThemeGlobalsGetAction(StudioThemeRegistry.INSTANCE, HomingDefault.INSTANCE);
    }

    @Override
    public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
        Map<String, GetAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.getActions());
        all.put("/doc-content",  docContentAction);
        all.put("/doc",          docAction);
        all.put("/step-data",    stepDataAction);
        all.put("/rename-data",  renameDataAction);
        all.put("/rfc0002-data", rfc0002DataAction);
        all.put("/rfc0002ext1-data", rfc0002Ext1DataAction);
        // Override the inner registry's file-based /css-content with the typed-impl one (RFC 0002).
        all.put("/css-content",  cssContentAction);
        // RFC 0002-ext1 Phase 10: override the inner registry's empty theme-bundle endpoints
        // with the studio's populated registry (HomingDefault.Vars / .Globals).
        all.put("/theme-vars",    themeVarsAction);
        all.put("/theme-globals", themeGlobalsAction);
        return Map.copyOf(all);
    }

    @Override
    public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
        return inner.postActions();
    }
}
