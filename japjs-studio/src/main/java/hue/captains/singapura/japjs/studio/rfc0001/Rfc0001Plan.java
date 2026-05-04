package hue.captains.singapura.japjs.studio.rfc0001;

import hue.captains.singapura.japjs.core.*;
import hue.captains.singapura.japjs.studio.css.StudioStyles;
import hue.captains.singapura.japjs.studio.es.DocReader;
import hue.captains.singapura.japjs.studio.es.StudioCatalogue;

import java.util.List;

public record Rfc0001Plan() implements AppModule<Rfc0001Plan> {

    record appMain() implements AppModule._AppMain<Rfc0001Plan> {}

    public record link() implements AppLink<Rfc0001Plan> {}

    public static final Rfc0001Plan INSTANCE = new Rfc0001Plan();

    @Override
    public String title() {
        return "japjs · studio · RFC 0001 plan";
    }

    @Override
    public ImportsFor<Rfc0001Plan> imports() {
        return ImportsFor.<Rfc0001Plan>builder()
                // Navigation targets — RFC 0001 Step 11.
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0001Step.link()),     Rfc0001Step.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),       DocReader.INSTANCE))
                // CSS imports.
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
                        new StudioStyles.st_loading(), new StudioStyles.st_error(), new StudioStyles.st_footer()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<Rfc0001Plan> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
