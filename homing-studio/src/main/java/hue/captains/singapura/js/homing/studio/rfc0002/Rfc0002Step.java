package hue.captains.singapura.js.homing.studio.rfc0002;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.base.css.Util;
import hue.captains.singapura.js.homing.studio.es.DocReader;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;

import java.util.List;

/**
 * Detail view for a single RFC 0002 phase. {@code ?phase=03} URL parameter
 * selects which phase to render.
 */
public record Rfc0002Step() implements AppModule<Rfc0002Step> {

    record appMain() implements AppModule._AppMain<Rfc0002Step> {}

    public record link() implements AppLink<Rfc0002Step> {}

    /** Typed query parameter — which phase id to render. */
    public record Params(String phase) {}

    public static final Rfc0002Step INSTANCE = new Rfc0002Step();

    @Override
    public String title() {
        return "Homing · studio · RFC 0002 phase";
    }

    @Override
    public Class<?> paramsType() {
        return Params.class;
    }

    @Override
    public ImportsFor<Rfc0002Step> imports() {
        return ImportsFor.<Rfc0002Step>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()), JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Plan.link()),     Rfc0002Plan.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Step.link()),     Rfc0002Step.INSTANCE))   // self-link for prev/next
                .add(new ModuleImports<>(List.of(new DocReader.link()),       DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new MarkedJs.marked()),      MarkedJs.INSTANCE))
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
                .add(new ModuleImports<>(List.of(new Util.border_emphasis()), Util.INSTANCE))   // RFC 0002-ext1 Phase 08
                .build();
    }

    @Override
    public ExportsOf<Rfc0002Step> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
