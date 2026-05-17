package hue.captains.singapura.js.homing.studio.base.table;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;

import java.util.List;

/**
 * RFC 0020 — shared JS renderer for {@link TableViewer}. Fetches the
 * table JSON via {@code /doc?id=<uuid>} and builds a {@code <table>}
 * using the framework's typed cell CSS — colspan/rowspan, optional
 * cell badges (success / warning / error tokens), optional alignment.
 *
 * <p>Slim by design — no client-side sort, no filter, no edit (RFC 0020
 * §2.2). The renderer's job is exactly: take the JSON, emit the table.</p>
 *
 * @since RFC 0020
 */
public record TableViewerRenderer() implements DomModule<TableViewerRenderer> {

    public record renderTable() implements Exportable._Constant<TableViewerRenderer> {}

    public static final TableViewerRenderer INSTANCE = new TableViewerRenderer();

    @Override
    public ImportsFor<TableViewerRenderer> imports() {
        return ImportsFor.<TableViewerRenderer>builder()
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_table(),
                        new StudioStyles.st_thead(),
                        new StudioStyles.st_th(),
                        new StudioStyles.st_td(),
                        new StudioStyles.st_td_align_left(),
                        new StudioStyles.st_td_align_center(),
                        new StudioStyles.st_td_align_right(),
                        new StudioStyles.st_td_badge_success(),
                        new StudioStyles.st_td_badge_warning(),
                        new StudioStyles.st_td_badge_error(),
                        new StudioStyles.st_loading(),
                        new StudioStyles.st_error()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TableViewerRenderer> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new renderTable()));
    }
}
