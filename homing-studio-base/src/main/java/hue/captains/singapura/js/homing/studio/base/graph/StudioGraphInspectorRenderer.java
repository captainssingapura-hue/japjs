package hue.captains.singapura.js.homing.studio.base.graph;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;

import java.util.List;

/**
 * RFC 0014 — JS renderer for {@link StudioGraphInspector}. Fetches
 * {@code /graph-md} (or {@code /graph-md?root=<fqn>}), parses the markdown
 * with the bundled marked.js, inserts the resulting HTML into the page.
 *
 * <p>Pure front-end rendering — the only fetched data is the raw markdown
 * document; all formatting / parsing happens in the browser. The bundled
 * marked.js is the same renderer the framework's {@code DocReader} uses,
 * so style + behaviour stay consistent across the framework.</p>
 */
public record StudioGraphInspectorRenderer() implements DomModule<StudioGraphInspectorRenderer> {

    public record renderStudioGraphInspector()
            implements Exportable._Constant<StudioGraphInspectorRenderer> {}

    public static final StudioGraphInspectorRenderer INSTANCE = new StudioGraphInspectorRenderer();

    @Override
    public ImportsFor<StudioGraphInspectorRenderer> imports() {
        return ImportsFor.<StudioGraphInspectorRenderer>builder()
                .add(new ModuleImports<>(List.of(new MarkedJs.marked()), MarkedJs.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_doc(),
                        new StudioStyles.st_loading(),
                        new StudioStyles.st_error()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<StudioGraphInspectorRenderer> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new renderStudioGraphInspector()));
    }
}
