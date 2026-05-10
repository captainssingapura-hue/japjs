package hue.captains.singapura.js.homing.demo;

import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.demo.theme.DemoCssGroupImplRegistry;
import hue.captains.singapura.js.homing.demo.theme.DemoThemeRegistry;
import hue.captains.singapura.js.homing.demo.theme.Navy;
import hue.captains.singapura.js.homing.server.CssContentGetAction;
import hue.captains.singapura.js.homing.server.HomingActionRegistry;
import hue.captains.singapura.js.homing.server.ThemeGlobalsGetAction;
import hue.captains.singapura.js.homing.server.ThemeVarsGetAction;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps {@link HomingActionRegistry} and overrides the {@code /css-content},
 * {@code /theme-vars}, and {@code /theme-globals} routes with demo-specific
 * implementations backed by {@link DemoThemeRegistry} and {@link DemoDefault}
 * as the default theme.
 *
 * <p>Mirrors the {@code StudioActionRegistry} composition pattern.</p>
 */
public class DemoActionRegistry implements ActionRegistry<RoutingContext> {

    private final HomingActionRegistry inner;
    private final CssContentGetAction cssContentAction;
    private final ThemeVarsGetAction themeVarsAction;
    private final ThemeGlobalsGetAction themeGlobalsAction;

    public DemoActionRegistry(ModuleNameResolver nameResolver, SimpleAppResolver appResolver) {
        // RFC 0002-ext1 Phase 11/12: pass DemoThemeRegistry through so the
        // theme picker widget on every page is driven by demo's registered themes.
        this.inner = new HomingActionRegistry(
                nameResolver, appResolver,
                hue.captains.singapura.js.homing.core.util.ResourceReader.fromSystemProperty(),
                DemoThemeRegistry.INSTANCE);
        this.cssContentAction = new CssContentGetAction(
                DemoCssGroupImplRegistry.ALL,
                Navy.INSTANCE);
        this.themeVarsAction    = new ThemeVarsGetAction(DemoThemeRegistry.INSTANCE, Navy.INSTANCE);
        this.themeGlobalsAction = new ThemeGlobalsGetAction(DemoThemeRegistry.INSTANCE, Navy.INSTANCE);
    }

    @Override
    public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
        Map<String, GetAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.getActions());
        all.put("/css-content",   cssContentAction);
        all.put("/theme-vars",    themeVarsAction);
        all.put("/theme-globals", themeGlobalsAction);
        return Map.copyOf(all);
    }

    @Override
    public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
        return inner.postActions();
    }
}
