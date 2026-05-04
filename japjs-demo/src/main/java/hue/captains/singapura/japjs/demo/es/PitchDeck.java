package hue.captains.singapura.japjs.demo.es;

import hue.captains.singapura.japjs.core.*;
import hue.captains.singapura.japjs.demo.css.PitchDeckStyles;

import java.util.List;

public record PitchDeck() implements AppModule<PitchDeck> {

    record appMain() implements AppModule._AppMain<PitchDeck> {}

    public record link() implements AppLink<PitchDeck> {}

    public static final PitchDeck INSTANCE = new PitchDeck();

    @Override
    public String title() {
        return "japjs — Executive Pitch";
    }

    @Override
    public ImportsFor<PitchDeck> imports() {
        return ImportsFor.<PitchDeck>builder()
                .add(new ModuleImports<>(List.of(
                        new ToneJs.Synth(),
                        new ToneJs.start()
                ), ToneJs.INSTANCE))
                .add(new ModuleImports<>(List.of(new PitchDeckBgm.getBgm()), PitchDeckBgm.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new PitchDeckDiagrams.arch(),
                        new PitchDeckDiagrams.ownership(),
                        new PitchDeckDiagrams.deployment(),
                        new PitchDeckDiagrams.messaging()
                ), PitchDeckDiagrams.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new PitchDeckStyles.pd_root(),
                        new PitchDeckStyles.pd_stage(),
                        new PitchDeckStyles.pd_slide(),
                        new PitchDeckStyles.pd_slide_active(),
                        new PitchDeckStyles.pd_slide_dark(),
                        new PitchDeckStyles.pd_kicker(),
                        new PitchDeckStyles.pd_title(),
                        new PitchDeckStyles.pd_subtitle(),
                        new PitchDeckStyles.pd_body(),
                        new PitchDeckStyles.pd_hero(),
                        new PitchDeckStyles.pd_hero_title(),
                        new PitchDeckStyles.pd_hero_tag(),
                        new PitchDeckStyles.pd_hero_sub(),
                        new PitchDeckStyles.pd_hero_press(),
                        new PitchDeckStyles.pd_nav(),
                        new PitchDeckStyles.pd_btn(),
                        new PitchDeckStyles.pd_btn_primary(),
                        new PitchDeckStyles.pd_btn_ghost(),
                        new PitchDeckStyles.pd_btn_bgm(),
                        new PitchDeckStyles.pd_btn_bgm_on(),
                        new PitchDeckStyles.pd_progress(),
                        new PitchDeckStyles.pd_progress_fill(),
                        new PitchDeckStyles.pd_dots(),
                        new PitchDeckStyles.pd_dot(),
                        new PitchDeckStyles.pd_dot_active(),
                        new PitchDeckStyles.pd_grid2(),
                        new PitchDeckStyles.pd_grid3(),
                        new PitchDeckStyles.pd_grid4(),
                        new PitchDeckStyles.pd_card(),
                        new PitchDeckStyles.pd_card_dark(),
                        new PitchDeckStyles.pd_card_accent(),
                        new PitchDeckStyles.pd_card_head(),
                        new PitchDeckStyles.pd_card_body(),
                        new PitchDeckStyles.pd_stat(),
                        new PitchDeckStyles.pd_stat_num(),
                        new PitchDeckStyles.pd_stat_label(),
                        new PitchDeckStyles.pd_row(),
                        new PitchDeckStyles.pd_row_head(),
                        new PitchDeckStyles.pd_row_body(),
                        new PitchDeckStyles.pd_diagram(),
                        new PitchDeckStyles.pd_diagram_svg(),
                        new PitchDeckStyles.pd_table(),
                        new PitchDeckStyles.pd_table_header(),
                        new PitchDeckStyles.pd_table_row(),
                        new PitchDeckStyles.pd_table_row_featured(),
                        new PitchDeckStyles.pd_table_cell(),
                        new PitchDeckStyles.pd_badge_built(),
                        new PitchDeckStyles.pd_badge_designed(),
                        new PitchDeckStyles.pd_check(),
                        new PitchDeckStyles.pd_cross(),
                        new PitchDeckStyles.pd_hint(),
                        new PitchDeckStyles.pd_accent(),
                        new PitchDeckStyles.pd_mono(),
                        new PitchDeckStyles.pd_quote(),
                        new PitchDeckStyles.pd_cta(),
                        new PitchDeckStyles.pd_toast(),
                        new PitchDeckStyles.pd_toast_show()
                ), PitchDeckStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PitchDeck> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
