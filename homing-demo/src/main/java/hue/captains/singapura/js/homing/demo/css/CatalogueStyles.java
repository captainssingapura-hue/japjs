package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

public record CatalogueStyles() implements CssGroup<CatalogueStyles> {
    public static final CatalogueStyles INSTANCE = new CatalogueStyles();

    public record cat_root() implements CssClass<CatalogueStyles> {}
    public record cat_header() implements CssClass<CatalogueStyles> {}
    public record cat_kicker() implements CssClass<CatalogueStyles> {}
    public record cat_title() implements CssClass<CatalogueStyles> {}
    public record cat_subtitle() implements CssClass<CatalogueStyles> {}
    public record cat_section() implements CssClass<CatalogueStyles> {}
    public record cat_section_title() implements CssClass<CatalogueStyles> {}
    public record cat_grid() implements CssClass<CatalogueStyles> {}
    public record cat_card() implements CssClass<CatalogueStyles> {}
    public record cat_card_featured() implements CssClass<CatalogueStyles> {}
    public record cat_card_head() implements CssClass<CatalogueStyles> {}
    public record cat_card_title() implements CssClass<CatalogueStyles> {}
    public record cat_card_desc() implements CssClass<CatalogueStyles> {}
    public record cat_card_meta() implements CssClass<CatalogueStyles> {}
    public record cat_card_link() implements CssClass<CatalogueStyles> {}
    public record cat_badge() implements CssClass<CatalogueStyles> {}
    public record cat_badge_pitch() implements CssClass<CatalogueStyles> {}
    public record cat_badge_3d() implements CssClass<CatalogueStyles> {}
    public record cat_badge_anim() implements CssClass<CatalogueStyles> {}
    public record cat_badge_basic() implements CssClass<CatalogueStyles> {}
    public record cat_footer() implements CssClass<CatalogueStyles> {}
    public record cat_mono() implements CssClass<CatalogueStyles> {}

    @Override
    public CssImportsFor<CatalogueStyles> cssImports() {
        return CssImportsFor.none(this);
    }

    @Override
    public List<CssClass<CatalogueStyles>> cssClasses() {
        return List.of(
                new cat_root(), new cat_header(), new cat_kicker(),
                new cat_title(), new cat_subtitle(),
                new cat_section(), new cat_section_title(),
                new cat_grid(), new cat_card(), new cat_card_featured(),
                new cat_card_head(), new cat_card_title(), new cat_card_desc(),
                new cat_card_meta(), new cat_card_link(),
                new cat_badge(), new cat_badge_pitch(), new cat_badge_3d(),
                new cat_badge_anim(), new cat_badge_basic(),
                new cat_footer(), new cat_mono()
        );
    }
}
