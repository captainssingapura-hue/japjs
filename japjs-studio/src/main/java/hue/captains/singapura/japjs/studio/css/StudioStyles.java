package hue.captains.singapura.japjs.studio.css;

import hue.captains.singapura.japjs.core.CssClass;
import hue.captains.singapura.japjs.core.CssGroup;
import hue.captains.singapura.japjs.core.CssImportsFor;

import java.util.List;

public record StudioStyles() implements CssGroup<StudioStyles> {
    public static final StudioStyles INSTANCE = new StudioStyles();

    public record st_root() implements CssClass<StudioStyles> {}
    public record st_header() implements CssClass<StudioStyles> {}
    public record st_nav() implements CssClass<StudioStyles> {}
    public record st_brand() implements CssClass<StudioStyles> {}
    public record st_brand_dot() implements CssClass<StudioStyles> {}
    public record st_brand_word() implements CssClass<StudioStyles> {}
    public record st_breadcrumbs() implements CssClass<StudioStyles> {}
    public record st_crumb() implements CssClass<StudioStyles> {}
    public record st_crumb_sep() implements CssClass<StudioStyles> {}
    public record st_main() implements CssClass<StudioStyles> {}
    public record st_kicker() implements CssClass<StudioStyles> {}
    public record st_title() implements CssClass<StudioStyles> {}
    public record st_subtitle() implements CssClass<StudioStyles> {}
    public record st_section() implements CssClass<StudioStyles> {}
    public record st_section_title() implements CssClass<StudioStyles> {}
    public record st_grid() implements CssClass<StudioStyles> {}
    public record st_card() implements CssClass<StudioStyles> {}
    public record st_card_featured() implements CssClass<StudioStyles> {}
    public record st_card_title() implements CssClass<StudioStyles> {}
    public record st_card_summary() implements CssClass<StudioStyles> {}
    public record st_card_meta() implements CssClass<StudioStyles> {}
    public record st_card_link() implements CssClass<StudioStyles> {}
    public record st_badge() implements CssClass<StudioStyles> {}
    public record st_badge_whitepaper() implements CssClass<StudioStyles> {}
    public record st_badge_brochure() implements CssClass<StudioStyles> {}
    public record st_badge_rfc() implements CssClass<StudioStyles> {}
    public record st_badge_brand() implements CssClass<StudioStyles> {}
    public record st_badge_session() implements CssClass<StudioStyles> {}
    public record st_badge_reference() implements CssClass<StudioStyles> {}
    public record st_badge_rename() implements CssClass<StudioStyles> {}
    public record st_search_wrap() implements CssClass<StudioStyles> {}
    public record st_search() implements CssClass<StudioStyles> {}
    public record st_filter() implements CssClass<StudioStyles> {}
    public record st_filter_btn() implements CssClass<StudioStyles> {}
    public record st_filter_btn_active() implements CssClass<StudioStyles> {}
    public record st_layout() implements CssClass<StudioStyles> {}
    public record st_sidebar() implements CssClass<StudioStyles> {}
    public record st_sidebar_title() implements CssClass<StudioStyles> {}
    public record st_toc() implements CssClass<StudioStyles> {}
    public record st_toc_item() implements CssClass<StudioStyles> {}
    public record st_toc_h1() implements CssClass<StudioStyles> {}
    public record st_toc_h2() implements CssClass<StudioStyles> {}
    public record st_toc_h3() implements CssClass<StudioStyles> {}
    public record st_toc_active() implements CssClass<StudioStyles> {}
    public record st_doc() implements CssClass<StudioStyles> {}
    public record st_doc_meta() implements CssClass<StudioStyles> {}
    public record st_loading() implements CssClass<StudioStyles> {}
    public record st_error() implements CssClass<StudioStyles> {}
    public record st_footer() implements CssClass<StudioStyles> {}
    public record st_app_pill() implements CssClass<StudioStyles> {}
    public record st_app_pill_dark() implements CssClass<StudioStyles> {}
    public record st_app_pill_icon() implements CssClass<StudioStyles> {}
    public record st_app_pill_label() implements CssClass<StudioStyles> {}
    public record st_app_pill_desc() implements CssClass<StudioStyles> {}

    // RFC implementation tracker
    public record st_overall_progress() implements CssClass<StudioStyles> {}
    public record st_overall_bar() implements CssClass<StudioStyles> {}
    public record st_overall_fill() implements CssClass<StudioStyles> {}
    public record st_overall_pct() implements CssClass<StudioStyles> {}
    public record st_step_card() implements CssClass<StudioStyles> {}
    public record st_step_head() implements CssClass<StudioStyles> {}
    public record st_step_id() implements CssClass<StudioStyles> {}
    public record st_step_label() implements CssClass<StudioStyles> {}
    public record st_step_summary() implements CssClass<StudioStyles> {}
    public record st_step_progress() implements CssClass<StudioStyles> {}
    public record st_step_progress_bar() implements CssClass<StudioStyles> {}
    public record st_step_progress_fill() implements CssClass<StudioStyles> {}
    public record st_step_meta() implements CssClass<StudioStyles> {}
    public record st_status_badge() implements CssClass<StudioStyles> {}
    public record st_status_not_started() implements CssClass<StudioStyles> {}
    public record st_status_in_progress() implements CssClass<StudioStyles> {}
    public record st_status_blocked() implements CssClass<StudioStyles> {}
    public record st_status_done() implements CssClass<StudioStyles> {}
    public record st_panel() implements CssClass<StudioStyles> {}
    public record st_panel_title() implements CssClass<StudioStyles> {}
    public record st_task_list() implements CssClass<StudioStyles> {}
    public record st_task_item() implements CssClass<StudioStyles> {}
    public record st_task_done() implements CssClass<StudioStyles> {}
    public record st_task_box() implements CssClass<StudioStyles> {}
    public record st_dep() implements CssClass<StudioStyles> {}
    public record st_acceptance() implements CssClass<StudioStyles> {}
    public record st_effort() implements CssClass<StudioStyles> {}

    @Override
    public CssImportsFor<StudioStyles> cssImports() {
        return CssImportsFor.none(this);
    }

    @Override
    public List<CssClass<StudioStyles>> cssClasses() {
        return List.of(
                new st_root(), new st_header(), new st_nav(),
                new st_brand(), new st_brand_dot(), new st_brand_word(),
                new st_breadcrumbs(), new st_crumb(), new st_crumb_sep(),
                new st_main(), new st_kicker(), new st_title(), new st_subtitle(),
                new st_section(), new st_section_title(),
                new st_grid(), new st_card(), new st_card_featured(),
                new st_card_title(), new st_card_summary(), new st_card_meta(), new st_card_link(),
                new st_badge(),
                new st_badge_whitepaper(), new st_badge_brochure(), new st_badge_rfc(),
                new st_badge_brand(), new st_badge_session(), new st_badge_reference(), new st_badge_rename(),
                new st_search_wrap(), new st_search(),
                new st_filter(), new st_filter_btn(), new st_filter_btn_active(),
                new st_layout(), new st_sidebar(), new st_sidebar_title(),
                new st_toc(), new st_toc_item(), new st_toc_h1(), new st_toc_h2(), new st_toc_h3(), new st_toc_active(),
                new st_doc(), new st_doc_meta(),
                new st_loading(), new st_error(), new st_footer(),
                new st_app_pill(), new st_app_pill_dark(),
                new st_app_pill_icon(), new st_app_pill_label(), new st_app_pill_desc(),
                // rfc tracker
                new st_overall_progress(), new st_overall_bar(), new st_overall_fill(), new st_overall_pct(),
                new st_step_card(), new st_step_head(), new st_step_id(), new st_step_label(),
                new st_step_summary(), new st_step_progress(), new st_step_progress_bar(), new st_step_progress_fill(),
                new st_step_meta(),
                new st_status_badge(),
                new st_status_not_started(), new st_status_in_progress(), new st_status_blocked(), new st_status_done(),
                new st_panel(), new st_panel_title(),
                new st_task_list(), new st_task_item(), new st_task_done(), new st_task_box(),
                new st_dep(), new st_acceptance(), new st_effort()
        );
    }
}
