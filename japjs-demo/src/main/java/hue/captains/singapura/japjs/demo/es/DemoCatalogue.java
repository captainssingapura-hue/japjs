package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;
import hue.captains.singapura.japjs.demo.css.CatalogueStyles;

import java.util.List;

public record DemoCatalogue() implements AppModule<DemoCatalogue> {

    record appMain() implements AppModule._AppMain<DemoCatalogue> {}

    public static final DemoCatalogue INSTANCE = new DemoCatalogue();

    @Override
    public String title() {
        return "japjs · demos";
    }

    @Override
    public ImportsFor<DemoCatalogue> imports() {
        return ImportsFor.<DemoCatalogue>builder()
                .add(new ModuleImports<>(List.of(
                        new CatalogueStyles.cat_root(),
                        new CatalogueStyles.cat_header(),
                        new CatalogueStyles.cat_kicker(),
                        new CatalogueStyles.cat_title(),
                        new CatalogueStyles.cat_subtitle(),
                        new CatalogueStyles.cat_section(),
                        new CatalogueStyles.cat_section_title(),
                        new CatalogueStyles.cat_grid(),
                        new CatalogueStyles.cat_card(),
                        new CatalogueStyles.cat_card_featured(),
                        new CatalogueStyles.cat_card_head(),
                        new CatalogueStyles.cat_card_title(),
                        new CatalogueStyles.cat_card_desc(),
                        new CatalogueStyles.cat_card_meta(),
                        new CatalogueStyles.cat_card_link(),
                        new CatalogueStyles.cat_badge(),
                        new CatalogueStyles.cat_badge_pitch(),
                        new CatalogueStyles.cat_badge_3d(),
                        new CatalogueStyles.cat_badge_anim(),
                        new CatalogueStyles.cat_badge_basic(),
                        new CatalogueStyles.cat_footer(),
                        new CatalogueStyles.cat_mono()
                ), CatalogueStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DemoCatalogue> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
