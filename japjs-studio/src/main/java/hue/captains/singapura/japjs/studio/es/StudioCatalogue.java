package hue.captains.singapura.japjs.studio.es;

import hue.captains.singapura.japjs.core.*;
import hue.captains.singapura.japjs.studio.css.StudioStyles;

import java.util.List;

public record StudioCatalogue() implements AppModule<StudioCatalogue> {

    record appMain() implements AppModule._AppMain<StudioCatalogue> {}

    public static final StudioCatalogue INSTANCE = new StudioCatalogue();

    @Override
    public String title() {
        return "japjs · studio";
    }

    @Override
    public ImportsFor<StudioCatalogue> imports() {
        return ImportsFor.<StudioCatalogue>builder()
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
