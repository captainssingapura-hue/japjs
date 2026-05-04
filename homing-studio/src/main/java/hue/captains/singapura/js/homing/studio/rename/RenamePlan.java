package hue.captains.singapura.js.homing.studio.rename;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.studio.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.es.DocReader;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;

import java.util.List;

public record RenamePlan() implements AppModule<RenamePlan> {

    record appMain() implements AppModule._AppMain<RenamePlan> {}

    public record link() implements AppLink<RenamePlan> {}

    public static final RenamePlan INSTANCE = new RenamePlan();

    @Override
    public String title() {
        return "Homing · studio · rename plan";
    }

    @Override
    public ImportsFor<RenamePlan> imports() {
        return ImportsFor.<RenamePlan>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new RenameStep.link()),       RenameStep.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),        DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(), new StudioStyles.st_header(),
                        new StudioStyles.st_brand(), new StudioStyles.st_brand_dot(), new StudioStyles.st_brand_word(),
                        new StudioStyles.st_breadcrumbs(), new StudioStyles.st_crumb(), new StudioStyles.st_crumb_sep(),
                        new StudioStyles.st_main(), new StudioStyles.st_kicker(), new StudioStyles.st_title(), new StudioStyles.st_subtitle(),
                        new StudioStyles.st_section(), new StudioStyles.st_section_title(),
                        new StudioStyles.st_overall_progress(), new StudioStyles.st_overall_bar(),
                        new StudioStyles.st_overall_fill(), new StudioStyles.st_overall_pct(),
                        new StudioStyles.st_step_card(), new StudioStyles.st_step_head(),
                        new StudioStyles.st_step_id(), new StudioStyles.st_step_label(), new StudioStyles.st_step_summary(),
                        new StudioStyles.st_step_progress(), new StudioStyles.st_step_progress_bar(), new StudioStyles.st_step_progress_fill(),
                        new StudioStyles.st_step_meta(),
                        new StudioStyles.st_status_badge(),
                        new StudioStyles.st_status_not_started(), new StudioStyles.st_status_in_progress(),
                        new StudioStyles.st_status_blocked(), new StudioStyles.st_status_done(),
                        new StudioStyles.st_panel(), new StudioStyles.st_panel_title(),
                        new StudioStyles.st_card(), new StudioStyles.st_card_title(), new StudioStyles.st_card_summary(),
                        new StudioStyles.st_loading(), new StudioStyles.st_error(), new StudioStyles.st_footer()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<RenamePlan> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
