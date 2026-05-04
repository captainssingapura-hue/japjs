package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.core.*;
import hue.captains.singapura.js.homing.studio.css.StudioStyles;

import java.util.List;

public record DocBrowser() implements AppModule<DocBrowser> {

    record appMain() implements AppModule._AppMain<DocBrowser> {}

    public record link() implements AppLink<DocBrowser> {}

    public static final DocBrowser INSTANCE = new DocBrowser();

    @Override
    public String title() {
        return "Homing · studio · documents";
    }

    @Override
    public ImportsFor<DocBrowser> imports() {
        return ImportsFor.<DocBrowser>builder()
                // Navigation targets — RFC 0001 Step 11.
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()), StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),       DocReader.INSTANCE))
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
                        new StudioStyles.st_search_wrap(),
                        new StudioStyles.st_search(),
                        new StudioStyles.st_filter(),
                        new StudioStyles.st_filter_btn(),
                        new StudioStyles.st_filter_btn_active(),
                        new StudioStyles.st_grid(),
                        new StudioStyles.st_card(),
                        new StudioStyles.st_card_title(),
                        new StudioStyles.st_card_summary(),
                        new StudioStyles.st_card_meta(),
                        new StudioStyles.st_card_link(),
                        new StudioStyles.st_badge(),
                        new StudioStyles.st_badge_whitepaper(),
                        new StudioStyles.st_badge_brochure(),
                        new StudioStyles.st_badge_rfc(),
                        new StudioStyles.st_badge_brand(),
                        new StudioStyles.st_badge_rename(),
                        new StudioStyles.st_badge_session(),
                        new StudioStyles.st_badge_reference(),
                        new StudioStyles.st_loading(),
                        new StudioStyles.st_error(),
                        new StudioStyles.st_footer()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocBrowser> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
