package hue.captains.singapura.japjs.studio.rfc0001;

import hue.captains.singapura.japjs.core.*;
import hue.captains.singapura.japjs.studio.css.StudioStyles;
import hue.captains.singapura.japjs.studio.es.MarkedJs;

import java.util.List;

/**
 * Detail view for a single RFC 0001 step. Takes the step id from the URL
 * query string {@code ?id=01} and fetches its data via {@code /step-data}.
 */
public record Rfc0001Step() implements AppModule<Rfc0001Step> {

    record appMain() implements AppModule._AppMain<Rfc0001Step> {}

    public static final Rfc0001Step INSTANCE = new Rfc0001Step();

    @Override
    public String title() {
        return "japjs · studio · RFC 0001 step";
    }

    @Override
    public ImportsFor<Rfc0001Step> imports() {
        return ImportsFor.<Rfc0001Step>builder()
                .add(new ModuleImports<>(List.of(new MarkedJs.marked()), MarkedJs.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(), new StudioStyles.st_header(),
                        new StudioStyles.st_brand(), new StudioStyles.st_brand_dot(), new StudioStyles.st_brand_word(),
                        new StudioStyles.st_breadcrumbs(), new StudioStyles.st_crumb(), new StudioStyles.st_crumb_sep(),
                        new StudioStyles.st_main(), new StudioStyles.st_kicker(), new StudioStyles.st_title(), new StudioStyles.st_subtitle(),
                        new StudioStyles.st_section(), new StudioStyles.st_section_title(),
                        new StudioStyles.st_step_id(),
                        new StudioStyles.st_step_progress(), new StudioStyles.st_step_progress_bar(), new StudioStyles.st_step_progress_fill(),
                        new StudioStyles.st_status_badge(),
                        new StudioStyles.st_status_not_started(), new StudioStyles.st_status_in_progress(),
                        new StudioStyles.st_status_blocked(), new StudioStyles.st_status_done(),
                        new StudioStyles.st_panel(), new StudioStyles.st_panel_title(),
                        new StudioStyles.st_task_list(), new StudioStyles.st_task_item(),
                        new StudioStyles.st_task_done(), new StudioStyles.st_task_box(),
                        new StudioStyles.st_dep(), new StudioStyles.st_acceptance(), new StudioStyles.st_effort(),
                        new StudioStyles.st_doc(),
                        new StudioStyles.st_loading(), new StudioStyles.st_error()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<Rfc0001Step> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
