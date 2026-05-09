package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.base.ui.StudioElements;

import java.util.List;

/**
 * Shared JS renderer for {@link DocReader}. Fetches the markdown via
 * {@code /doc?path=…} (typed asset, served by {@code DocGetAction}), renders
 * with marked.js, and builds a TOC sidebar from the document's headings.
 */
public record DocReaderRenderer() implements DomModule<DocReaderRenderer> {

    public record renderDocReader() implements Exportable._Constant<DocReaderRenderer> {}

    public static final DocReaderRenderer INSTANCE = new DocReaderRenderer();

    @Override
    public ImportsFor<DocReaderRenderer> imports() {
        return ImportsFor.<DocReaderRenderer>builder()
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()),
                        HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new MarkedJs.marked()), MarkedJs.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioElements.Header()
                ), StudioElements.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_main(),
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
                        new StudioStyles.st_loading(),
                        new StudioStyles.st_error()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocReaderRenderer> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new renderDocReader()));
    }
}
