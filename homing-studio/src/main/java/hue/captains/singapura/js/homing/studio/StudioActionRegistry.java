package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.core.ModuleNameResolver;
import hue.captains.singapura.js.homing.core.SimpleAppResolver;
import hue.captains.singapura.js.homing.server.HomingActionRegistry;
import hue.captains.singapura.js.homing.studio.base.DocGetAction;
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

    public StudioActionRegistry(ModuleNameResolver nameResolver) {
        this(nameResolver, Path.of(System.getProperty("homing.studio.docsRoot", "docs")), null);
    }

    public StudioActionRegistry(ModuleNameResolver nameResolver, Path docsRoot) {
        this(nameResolver, docsRoot, null);
    }

    public StudioActionRegistry(ModuleNameResolver nameResolver, Path docsRoot, SimpleAppResolver appResolver) {
        this.inner = new HomingActionRegistry(nameResolver, appResolver);
        this.docContentAction = new DocContentGetAction(docsRoot);
        this.docAction = new DocGetAction();
        this.stepDataAction = new StepDataGetAction();
        this.renameDataAction = new RenameDataGetAction();
    }

    @Override
    public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
        Map<String, GetAction<RoutingContext, ?, ?, ?>> all = new HashMap<>(inner.getActions());
        all.put("/doc-content", docContentAction);
        all.put("/doc",         docAction);
        all.put("/step-data",   stepDataAction);
        all.put("/rename-data", renameDataAction);
        return Map.copyOf(all);
    }

    @Override
    public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() {
        return inner.postActions();
    }
}
