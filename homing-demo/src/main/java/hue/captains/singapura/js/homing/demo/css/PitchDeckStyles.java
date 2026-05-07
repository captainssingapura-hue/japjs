package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.CssImportsFor;
import hue.captains.singapura.js.homing.core.Theme;

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

    /** Per-theme implementation contract for {@link PitchDeckStyles}. */
    public interface Impl<TH extends Theme> extends CssGroupImpl<PitchDeckStyles, TH> {
        @Override default PitchDeckStyles group() { return INSTANCE; }

        CssBlock<pd_root> pd_root();
        CssBlock<pd_stage> pd_stage();
        CssBlock<pd_slide> pd_slide();
        CssBlock<pd_slide_active> pd_slide_active();
        CssBlock<pd_slide_dark> pd_slide_dark();
        CssBlock<pd_kicker> pd_kicker();
        CssBlock<pd_title> pd_title();
        CssBlock<pd_subtitle> pd_subtitle();
        CssBlock<pd_body> pd_body();

        CssBlock<pd_hero> pd_hero();
        CssBlock<pd_hero_title> pd_hero_title();
        CssBlock<pd_hero_tag> pd_hero_tag();
        CssBlock<pd_hero_sub> pd_hero_sub();
        CssBlock<pd_hero_press> pd_hero_press();

        CssBlock<pd_nav> pd_nav();
        CssBlock<pd_btn> pd_btn();
        CssBlock<pd_btn_primary> pd_btn_primary();
        CssBlock<pd_btn_ghost> pd_btn_ghost();
        CssBlock<pd_btn_bgm> pd_btn_bgm();
        CssBlock<pd_btn_bgm_on> pd_btn_bgm_on();

        CssBlock<pd_progress> pd_progress();
        CssBlock<pd_progress_fill> pd_progress_fill();
        CssBlock<pd_dots> pd_dots();
        CssBlock<pd_dot> pd_dot();
        CssBlock<pd_dot_active> pd_dot_active();

        CssBlock<pd_grid2> pd_grid2();
        CssBlock<pd_grid3> pd_grid3();
        CssBlock<pd_grid4> pd_grid4();
        CssBlock<pd_card> pd_card();
        CssBlock<pd_card_dark> pd_card_dark();
        CssBlock<pd_card_accent> pd_card_accent();
        CssBlock<pd_card_head> pd_card_head();
        CssBlock<pd_card_body> pd_card_body();

        CssBlock<pd_stat> pd_stat();
        CssBlock<pd_stat_num> pd_stat_num();
        CssBlock<pd_stat_label> pd_stat_label();

        CssBlock<pd_row> pd_row();
        CssBlock<pd_row_head> pd_row_head();
        CssBlock<pd_row_body> pd_row_body();

        CssBlock<pd_diagram> pd_diagram();
        CssBlock<pd_diagram_svg> pd_diagram_svg();

        CssBlock<pd_table> pd_table();
        CssBlock<pd_table_header> pd_table_header();
        CssBlock<pd_table_row> pd_table_row();
        CssBlock<pd_table_row_featured> pd_table_row_featured();
        CssBlock<pd_table_cell> pd_table_cell();

        CssBlock<pd_badge_built> pd_badge_built();
        CssBlock<pd_badge_designed> pd_badge_designed();

        CssBlock<pd_check> pd_check();
        CssBlock<pd_cross> pd_cross();

        CssBlock<pd_hint> pd_hint();
        CssBlock<pd_accent> pd_accent();
        CssBlock<pd_mono> pd_mono();

        CssBlock<pd_quote> pd_quote();
        CssBlock<pd_cta> pd_cta();

        CssBlock<pd_toast> pd_toast();
        CssBlock<pd_toast_show> pd_toast_show();
    }

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
