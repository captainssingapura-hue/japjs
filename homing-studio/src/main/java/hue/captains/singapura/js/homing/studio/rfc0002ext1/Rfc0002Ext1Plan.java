package hue.captains.singapura.js.homing.studio.rfc0002ext1;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.es.DocReader;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;

import java.util.List;

/**
 * Overview view for RFC 0002-ext1 (Utility-First Composition + Two-Layer Semantic Tokens) implementation.
 * Lists open decisions and the 7-phase plan.
 */
public record Rfc0002Ext1Plan() implements AppModule<Rfc0002Ext1Plan> {

    record appMain() implements AppModule._AppMain<Rfc0002Ext1Plan> {}

    public record link() implements AppLink<Rfc0002Ext1Plan> {}

    public static final Rfc0002Ext1Plan INSTANCE = new Rfc0002Ext1Plan();

    @Override
    public String title() {
        return "Homing · studio · RFC 0002-ext1 plan";
    }

    @Override
    public ImportsFor<Rfc0002Ext1Plan> imports() {
        return ImportsFor.<Rfc0002Ext1Plan>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()), JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Ext1Step.link()), Rfc0002Ext1Step.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),       DocReader.INSTANCE))
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
    public ExportsOf<Rfc0002Ext1Plan> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
