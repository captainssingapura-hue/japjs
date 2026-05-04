package hue.captains.singapura.japjs.demo.css;

import hue.captains.singapura.japjs.core.CssClass;
import hue.captains.singapura.japjs.core.CssGroup;
import hue.captains.singapura.japjs.core.CssImportsFor;

import java.util.List;

public record PitchDeckStyles() implements CssGroup<PitchDeckStyles> {
    public static final PitchDeckStyles INSTANCE = new PitchDeckStyles();

    public record pd_root() implements CssClass<PitchDeckStyles> {}
    public record pd_stage() implements CssClass<PitchDeckStyles> {}
    public record pd_slide() implements CssClass<PitchDeckStyles> {}
    public record pd_slide_active() implements CssClass<PitchDeckStyles> {}
    public record pd_slide_dark() implements CssClass<PitchDeckStyles> {}
    public record pd_kicker() implements CssClass<PitchDeckStyles> {}
    public record pd_title() implements CssClass<PitchDeckStyles> {}
    public record pd_subtitle() implements CssClass<PitchDeckStyles> {}
    public record pd_body() implements CssClass<PitchDeckStyles> {}

    public record pd_hero() implements CssClass<PitchDeckStyles> {}
    public record pd_hero_title() implements CssClass<PitchDeckStyles> {}
    public record pd_hero_tag() implements CssClass<PitchDeckStyles> {}
    public record pd_hero_sub() implements CssClass<PitchDeckStyles> {}
    public record pd_hero_press() implements CssClass<PitchDeckStyles> {}

    public record pd_nav() implements CssClass<PitchDeckStyles> {}
    public record pd_btn() implements CssClass<PitchDeckStyles> {}
    public record pd_btn_primary() implements CssClass<PitchDeckStyles> {}
    public record pd_btn_ghost() implements CssClass<PitchDeckStyles> {}
    public record pd_btn_bgm() implements CssClass<PitchDeckStyles> {}
    public record pd_btn_bgm_on() implements CssClass<PitchDeckStyles> {}

    public record pd_progress() implements CssClass<PitchDeckStyles> {}
    public record pd_progress_fill() implements CssClass<PitchDeckStyles> {}
    public record pd_dots() implements CssClass<PitchDeckStyles> {}
    public record pd_dot() implements CssClass<PitchDeckStyles> {}
    public record pd_dot_active() implements CssClass<PitchDeckStyles> {}

    public record pd_grid2() implements CssClass<PitchDeckStyles> {}
    public record pd_grid3() implements CssClass<PitchDeckStyles> {}
    public record pd_grid4() implements CssClass<PitchDeckStyles> {}
    public record pd_card() implements CssClass<PitchDeckStyles> {}
    public record pd_card_dark() implements CssClass<PitchDeckStyles> {}
    public record pd_card_accent() implements CssClass<PitchDeckStyles> {}
    public record pd_card_head() implements CssClass<PitchDeckStyles> {}
    public record pd_card_body() implements CssClass<PitchDeckStyles> {}

    public record pd_stat() implements CssClass<PitchDeckStyles> {}
    public record pd_stat_num() implements CssClass<PitchDeckStyles> {}
    public record pd_stat_label() implements CssClass<PitchDeckStyles> {}

    public record pd_row() implements CssClass<PitchDeckStyles> {}
    public record pd_row_head() implements CssClass<PitchDeckStyles> {}
    public record pd_row_body() implements CssClass<PitchDeckStyles> {}

    public record pd_diagram() implements CssClass<PitchDeckStyles> {}
    public record pd_diagram_svg() implements CssClass<PitchDeckStyles> {}

    public record pd_table() implements CssClass<PitchDeckStyles> {}
    public record pd_table_header() implements CssClass<PitchDeckStyles> {}
    public record pd_table_row() implements CssClass<PitchDeckStyles> {}
    public record pd_table_row_featured() implements CssClass<PitchDeckStyles> {}
    public record pd_table_cell() implements CssClass<PitchDeckStyles> {}

    public record pd_badge_built() implements CssClass<PitchDeckStyles> {}
    public record pd_badge_designed() implements CssClass<PitchDeckStyles> {}

    public record pd_check() implements CssClass<PitchDeckStyles> {}
    public record pd_cross() implements CssClass<PitchDeckStyles> {}

    public record pd_hint() implements CssClass<PitchDeckStyles> {}
    public record pd_accent() implements CssClass<PitchDeckStyles> {}
    public record pd_mono() implements CssClass<PitchDeckStyles> {}

    public record pd_quote() implements CssClass<PitchDeckStyles> {}
    public record pd_cta() implements CssClass<PitchDeckStyles> {}

    public record pd_toast() implements CssClass<PitchDeckStyles> {}
    public record pd_toast_show() implements CssClass<PitchDeckStyles> {}

    @Override
    public CssImportsFor<PitchDeckStyles> cssImports() {
        return CssImportsFor.none(this);
    }

    @Override
    public List<CssClass<PitchDeckStyles>> cssClasses() {
        return List.of(
                new pd_root(), new pd_stage(), new pd_slide(), new pd_slide_active(), new pd_slide_dark(),
                new pd_kicker(), new pd_title(), new pd_subtitle(), new pd_body(),
                new pd_hero(), new pd_hero_title(), new pd_hero_tag(), new pd_hero_sub(), new pd_hero_press(),
                new pd_nav(), new pd_btn(), new pd_btn_primary(), new pd_btn_ghost(),
                new pd_btn_bgm(), new pd_btn_bgm_on(),
                new pd_progress(), new pd_progress_fill(), new pd_dots(), new pd_dot(), new pd_dot_active(),
                new pd_grid2(), new pd_grid3(), new pd_grid4(),
                new pd_card(), new pd_card_dark(), new pd_card_accent(), new pd_card_head(), new pd_card_body(),
                new pd_stat(), new pd_stat_num(), new pd_stat_label(),
                new pd_row(), new pd_row_head(), new pd_row_body(),
                new pd_diagram(), new pd_diagram_svg(),
                new pd_table(), new pd_table_header(), new pd_table_row(), new pd_table_row_featured(), new pd_table_cell(),
                new pd_badge_built(), new pd_badge_designed(),
                new pd_check(), new pd_cross(),
                new pd_hint(), new pd_accent(), new pd_mono(),
                new pd_quote(), new pd_cta(),
                new pd_toast(), new pd_toast_show()
        );
    }
}
