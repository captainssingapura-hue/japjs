package hue.captains.singapura.js.homing.studio.base.composed;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.Importable;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.studio.base.app.DocViewer;

import java.util.List;

/**
 * RFC 0019 — viewer AppModule for {@link ComposedDoc}. URL contract:
 *
 * <pre>/app?app=composed-viewer&id=&lt;composeddoc-uuid&gt;</pre>
 *
 * <p>Extends {@link DocViewer} so the framework's standard chrome
 * (Header + brand + breadcrumb + theme picker + audio runtime) is
 * composed automatically (V11 axiom). The body delegates to
 * {@link ComposedViewerRenderer}'s {@code renderComposed} JS function,
 * which fetches the composed-doc JSON payload via {@code /doc?id=<uuid>}
 * and dispatches per segment kind.</p>
 *
 * <p>Phase 1 segment kinds: {@code markdown} (marked.js) +
 * {@code svg} (proxy fetch to the referenced SvgDoc). Phase 2+
 * extends the dispatch (TableSegment, ImageSegment, etc.); no
 * change to this viewer needed — the renderer module switches on
 * the {@code kind} field.</p>
 *
 * @since RFC 0019 Phase 1
 */
public final class ComposedViewer extends DocViewer<ComposedViewer.Params, ComposedViewer> {

    public static final ComposedViewer INSTANCE = new ComposedViewer();

    private ComposedViewer() {}  // singleton via INSTANCE

    /** @param id UUID of the ComposedDoc to render. */
    public record Params(String id) implements AppModule._Param {}

    private record appMain() implements AppModule._AppMain<Params, ComposedViewer> {}
    public record link() implements AppLink<ComposedViewer> {}

    @Override public String simpleName() { return "composed-viewer"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public String title() { return "doc"; }

    @Override
    protected AppModule._AppMain<Params, ComposedViewer> appMain() {
        return new appMain();
    }

    @Override
    protected List<ModuleImports<? extends Importable>> bodyImports() {
        return List.of(
                new ModuleImports<>(
                        List.of(new ComposedViewerRenderer.renderComposed()),
                        ComposedViewerRenderer.INSTANCE)
        );
    }

    @Override
    protected List<String> bodyJs() {
        // Chrome already populated `main`; we hand it off to the renderer,
        // which manages the 2-column layout, TOC sidebar, and per-segment
        // dispatch.
        return List.of(
                "    if (!params.id) {",
                "        var errMsg = document.createElement('div');",
                "        css.addClass(errMsg, st_error);",
                "        errMsg.textContent = 'No composed-doc id supplied. Use ?id=<uuid>.';",
                "        main.replaceChildren(errMsg);",
                "        return;",
                "    }",
                "    renderComposed({ docId: params.id, main: main });"
        );
    }
}
