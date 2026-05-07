package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.rename.RenamePlan;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001Plan;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002Plan;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1Plan;

import java.util.List;

/**
 * Sub-catalogue for plan trackers ("journeys"). Lists every multi-phase plan
 * that uses the live-tracker pattern. Sits one level below
 * {@link StudioCatalogue} (Home), and is the parent in every plan tracker's
 * breadcrumb chain.
 */
public record JourneysCatalogue() implements AppModule<JourneysCatalogue> {

    record appMain() implements AppModule._AppMain<JourneysCatalogue> {}

    public record link() implements AppLink<JourneysCatalogue> {}

    public static final JourneysCatalogue INSTANCE = new JourneysCatalogue();

    @Override
    public String title() {
        return "Homing · studio · journeys";
    }

    @Override
    public ImportsFor<JourneysCatalogue> imports() {
        return ImportsFor.<JourneysCatalogue>builder()
                // Navigation targets — every plan tracker.
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),       DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0001Plan.link()),     Rfc0001Plan.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Plan.link()),     Rfc0002Plan.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Ext1Plan.link()), Rfc0002Ext1Plan.INSTANCE))
                .add(new ModuleImports<>(List.of(new RenamePlan.link()),      RenamePlan.INSTANCE))
                // CSS imports (same set as StudioCatalogue — same shell).
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_header(),
                        new StudioStyles.st_brand(),
                        new StudioStyles.st_brand_dot(),
                        new StudioStyles.st_brand_word(),
                        new StudioStyles.st_breadcrumbs(),
                        new StudioStyles.st_crumb(),
                        new StudioStyles.st_crumb_sep(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_kicker(),
                        new StudioStyles.st_title(),
                        new StudioStyles.st_subtitle(),
                        new StudioStyles.st_section(),
                        new StudioStyles.st_section_title(),
                        new StudioStyles.st_grid(),
                        new StudioStyles.st_app_pill(),
                        new StudioStyles.st_app_pill_dark(),
                        new StudioStyles.st_app_pill_icon(),
                        new StudioStyles.st_app_pill_label(),
                        new StudioStyles.st_app_pill_desc(),
                        new StudioStyles.st_footer()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<JourneysCatalogue> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
