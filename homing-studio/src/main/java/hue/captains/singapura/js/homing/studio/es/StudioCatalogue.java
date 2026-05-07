package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
// Plan trackers are reached via JourneysCatalogue (the sub-catalogue), not directly from Home.

import java.util.List;

public record StudioCatalogue() implements AppModule<StudioCatalogue> {

    record appMain() implements AppModule._AppMain<StudioCatalogue> {}

    public record link() implements AppLink<StudioCatalogue> {}

    public static final StudioCatalogue INSTANCE = new StudioCatalogue();

    @Override
    public String title() {
        return "Homing · studio";
    }

    @Override
    public ImportsFor<StudioCatalogue> imports() {
        return ImportsFor.<StudioCatalogue>builder()
                // Navigation targets — RFC 0001 Step 11.
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()),    StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocBrowser.link()),         DocBrowser.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()),  JourneysCatalogue.INSTANCE))
                // CSS imports.
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
    public ExportsOf<StudioCatalogue> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
