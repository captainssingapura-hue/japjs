package hue.captains.singapura.js.homing.studio.base;

import hue.captains.singapura.js.homing.core.*;

import java.util.List;

/**
 * Studio-level EsModule that provides the JS-side runtime for {@link DocGroup}s.
 *
 * <p>Mirrors {@code CssClassManager}: ships its own JS file ({@code DocManager.js})
 * which exports {@code DocManagerInstance} — the runtime helpers user code calls
 * (e.g. {@code docs.path(myDoc)}, {@code docs.url(myDoc)}, {@code docs.fetch(myDoc)}).</p>
 *
 * <p>The framework auto-imports {@code DocManagerInstance as docs} into any user
 * module whose imports chain reaches a {@code DocGroup}, via the generic
 * {@link ManagerInjector} mechanism in {@code homing-core}. {@code homing-server}
 * has no compile-time dependency on this class.</p>
 */
public record DocManager() implements EsModule<DocManager> {

    public static final DocManager INSTANCE = new DocManager();

    public record DocManagerInstance() implements Exportable._Constant<DocManager> {}

    @Override
    public ImportsFor<DocManager> imports() {
        return ImportsFor.noImports();
    }

    @Override
    public ExportsOf<DocManager> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new DocManagerInstance()));
    }
}
