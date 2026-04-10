package hue.captains.singapura.japjs.server;

import hue.captains.singapura.japjs.core.ModuleNameResolver;
import hue.captains.singapura.japjs.core.util.ResourceReader;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

public class JapjsActionRegistry implements ActionRegistry<RoutingContext> {

    private final AppHtmlGetAction appAction;
    private final EsModuleGetAction moduleAction;
    private final CssGetAction cssAction;
    private final CssContentGetAction cssContentAction;

    public JapjsActionRegistry(ModuleNameResolver nameResolver) {
        this(nameResolver, ResourceReader.fromSystemProperty());
    }

    public JapjsActionRegistry(ModuleNameResolver nameResolver, ResourceReader resourceReader) {
        this.appAction = new AppHtmlGetAction(nameResolver);
        this.moduleAction = new EsModuleGetAction(nameResolver, resourceReader);
        this.cssAction = new CssGetAction();
        this.cssContentAction = new CssContentGetAction(resourceReader);
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
