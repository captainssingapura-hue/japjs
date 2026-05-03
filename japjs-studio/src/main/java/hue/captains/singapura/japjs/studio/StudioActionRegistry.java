package hue.captains.singapura.japjs.studio;

import hue.captains.singapura.japjs.core.ModuleNameResolver;
import hue.captains.singapura.japjs.server.JapjsActionRegistry;
import hue.captains.singapura.japjs.studio.rfc0001.StepDataGetAction;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Composes the standard {@link JapjsActionRegistry} routes with the studio-specific
 * {@code /doc-content} route for serving markdown documents from the configured docs root.
 */
public class StudioActionRegistry implements ActionRegistry<RoutingContext> {

    private final JapjsActionRegistry inner;
    private final DocContentGetAction docContentAction;
    private final StepDataGetAction stepDataAction;

    public StudioActionRegistry(ModuleNameResolver nameResolver) {
        this(nameResolver, Path.of(System.getProperty("japjs.studio.docsRoot", "docs")));
    }

    public StudioActionRegistry(ModuleNameResolver nameResolver, Path docsRoot) {
        this.inner = new JapjsActionRegistry(nameResolver);
        this.docContentAction = new DocContentGetAction(docsRoot);
        this.stepDataAction = new StepDataGetAction();
    }

    @Override
    public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
        Map<String, GetAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.getActions());
        all.put("/doc-content", docContentAction);
        all.put("/step-data", stepDataAction);
        return Map.copyOf(all);
    }

    @Override
    public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
        return inner.postActions();
    }
}
