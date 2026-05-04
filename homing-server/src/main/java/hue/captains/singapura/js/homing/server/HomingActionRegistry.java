package hue.captains.singapura.js.homing.server;

import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.core.util.ResourceReader;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

public class HomingActionRegistry implements ActionRegistry<RoutingContext> {

    private final AppHtmlGetAction appAction;
    private final EsModuleGetAction moduleAction;
    private final CssGetAction cssAction;
    private final CssContentGetAction cssContentAction;

    /** Legacy constructor — only the {@code ?class=} contract is supported. */
    public HomingActionRegistry(ModuleNameResolver nameResolver) {
        this(nameResolver, null, ResourceReader.fromSystemProperty());
    }

    /**
     * Construct with a {@link SimpleAppResolver} (RFC 0001 Step 07) — supports both
     * the new {@code ?app=&lt;simpleName&gt;} and the legacy {@code ?class=&lt;canonical&gt;}
     * contracts on {@code /app}. Other endpoints retain {@code ?class=}.
     */
    public HomingActionRegistry(ModuleNameResolver nameResolver, SimpleAppResolver appResolver) {
        this(nameResolver, appResolver, ResourceReader.fromSystemProperty());
    }

    public HomingActionRegistry(ModuleNameResolver nameResolver, SimpleAppResolver appResolver, ResourceReader resourceReader) {
        this.appAction = new AppHtmlGetAction(nameResolver, appResolver);
        this.moduleAction = new EsModuleGetAction(nameResolver, resourceReader);
        this.cssAction = new CssGetAction();
        this.cssContentAction = new CssContentGetAction(resourceReader);
    }

    /** Backwards-compatible constructor for callers that don't yet use {@code SimpleAppResolver}. */
    public HomingActionRegistry(ModuleNameResolver nameResolver, ResourceReader resourceReader) {
        this(nameResolver, null, resourceReader);
    }

    @Override
    public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
        return Map.of(
                "/app", appAction,
                "/module", moduleAction,
                "/css", cssAction,
                "/css-content", cssContentAction
        );
    }

    @Override
    public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
        return Map.of();
    }
}
