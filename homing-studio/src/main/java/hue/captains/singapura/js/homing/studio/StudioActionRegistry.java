package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.server.HomingActionRegistry;
import hue.captains.singapura.js.homing.studio.rename.RenameDataGetAction;
import hue.captains.singapura.js.homing.studio.rfc0001.StepDataGetAction;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Composes the standard {@link HomingActionRegistry} routes with the studio-specific
 * {@code /doc-content} route for serving markdown documents from the configured docs root.
 */
public class StudioActionRegistry implements ActionRegistry<RoutingContext> {

    private final HomingActionRegistry inner;
    private final DocContentGetAction docContentAction;
    private final StepDataGetAction stepDataAction;
    private final RenameDataGetAction renameDataAction;

    public StudioActionRegistry(ModuleNameResolver nameResolver) {
        this(nameResolver, Path.of(System.getProperty("homing.studio.docsRoot", "docs")), null);
    }

    public StudioActionRegistry(ModuleNameResolver nameResolver, Path docsRoot) {
        this(nameResolver, docsRoot, null);
    }

    public StudioActionRegistry(ModuleNameResolver nameResolver, Path docsRoot, SimpleAppResolver appResolver) {
        this.inner = new HomingActionRegistry(nameResolver, appResolver);
        this.docContentAction = new DocContentGetAction(docsRoot);
        this.stepDataAction = new StepDataGetAction();
        this.renameDataAction = new RenameDataGetAction();
    }

    @Override
    public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
        Map<String, GetAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.getActions());
        all.put("/doc-content", docContentAction);
        all.put("/step-data", stepDataAction);
        all.put("/rename-data", renameDataAction);
        return Map.copyOf(all);
    }

    @Override
    public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
        return inner.postActions();
    }
}
