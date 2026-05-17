package hue.captains.singapura.js.homing.studio.base.composed;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.base.image.ImageViewerRenderer;
import hue.captains.singapura.js.homing.studio.base.table.TableViewerRenderer;

import java.util.List;

/**
 * RFC 0019 — shared JS renderer for {@link ComposedViewer}. Fetches the
 * composed-doc JSON via {@code /doc?id=<uuid>} and dispatches per
 * segment kind:
 *
 * <ul>
 *   <li>{@code markdown} — parse {@code body} with marked.js, append to
 *       {@code main} under an anchor with id {@code seg-N}; extract H1-H4
 *       headings and assign per-heading anchors that match the server
 *       TOC's {@code seg-N-hM} pattern.</li>
 *   <li>{@code svg} — fetch {@code svgUrl}, inline the SVG inside a
 *       captioned figure; the SVG inherits theme via RFC 0017's
 *       currentColor / var(--color-*) discipline.</li>
 * </ul>
 *
 * <p>TOC sidebar is built directly from the server-supplied {@code toc[]}
 * array — no client-side heading-walking, no slug derivation, no
 * synchronisation drift. Each TOC entry is an anchor whose target id was
 * assigned during segment rendering.</p>
 *
 * @since RFC 0019 Phase 1
 */
public record ComposedViewerRenderer() implements DomModule<ComposedViewerRenderer> {

    public record renderComposed() implements Exportable._Constant<ComposedViewerRenderer> {}

    public static final ComposedViewerRenderer INSTANCE = new ComposedViewerRenderer();

    @Override
    public ImportsFor<ComposedViewerRenderer> imports() {
        return ImportsFor.<ComposedViewerRenderer>builder()
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()),
                        HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new MarkedJs.marked()), MarkedJs.INSTANCE))
                .add(new ModuleImports<>(List.of(new TableViewerRenderer.renderTable()),
                        TableViewerRenderer.INSTANCE))
                .add(new ModuleImports<>(List.of(new ImageViewerRenderer.renderImage()),
                        ImageViewerRenderer.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_layout(),
                        new StudioStyles.st_sidebar(),
                        new StudioStyles.st_sidebar_title(),
                        new StudioStyles.st_toc(),
                        new StudioStyles.st_toc_item(),
                        new StudioStyles.st_toc_h1(),
                        new StudioStyles.st_toc_h2(),
                        new StudioStyles.st_toc_h3(),
                        new StudioStyles.st_toc_active(),
                        new StudioStyles.st_doc(),
                        new StudioStyles.st_doc_meta(),
                        new StudioStyles.st_section(),
                        new StudioStyles.st_section_title(),
                        new StudioStyles.st_loading(),
                        new StudioStyles.st_error()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ComposedViewerRenderer> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new renderComposed()));
    }
}
