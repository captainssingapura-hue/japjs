package hue.captains.singapura.js.homing.demo;

import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.demo.theme.DemoCssGroupImplRegistry;
import hue.captains.singapura.js.homing.demo.theme.DemoDefault;
import hue.captains.singapura.js.homing.server.CssContentGetAction;
import hue.captains.singapura.js.homing.server.HomingActionRegistry;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps {@link HomingActionRegistry} and overrides the {@code /css-content}
 * route with a typed {@link CssContentGetAction} backed by
 * {@link DemoCssGroupImplRegistry} and {@link DemoDefault} as the default theme.
 *
 * <p>Mirrors the {@code StudioActionRegistry} composition pattern.</p>
 */
public class DemoActionRegistry implements ActionRegistry<RoutingContext> {

    private final HomingActionRegistry inner;
    private final CssContentGetAction cssContentAction;

    public DemoActionRegistry(ModuleNameResolver nameResolver, SimpleAppResolver appResolver) {
        this.inner = new HomingActionRegistry(nameResolver, appResolver);
        this.cssContentAction = new CssContentGetAction(
                DemoCssGroupImplRegistry.ALL,
                DemoDefault.INSTANCE);
    }

    @Override
    public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
        Map<String, GetAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.getActions());
        // Override the inner registry's typed-but-empty /css-content with the demo's typed-impl one.
        all.put("/css-content", cssContentAction);
        return Map.copyOf(all);
    }

    @Override
    public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
        return inner.postActions();
    }
}
