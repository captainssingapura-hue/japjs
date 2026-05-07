package hue.captains.singapura.js.homing.studio.base.css;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.CssImportsFor;
import hue.captains.singapura.js.homing.core.Theme;

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

    /**
     * Per-theme implementation contract for {@link StudioStyles}.
     *
     * <p>Adding a {@link CssClass} record above forces every concrete
     * implementation of this interface to override the corresponding new
     * method, or it won't compile — the compile-time gate for theme
     * completeness across all themes (RFC 0002 §3.1).</p>
     *
     * <p>Method names match record simple names exactly. Each method
     * returns a {@link CssBlock} parameterized by the matching record class
     * (e.g. {@code CssBlock<st_root>}) — the body that goes <em>inside</em>
     * the curly braces of the rule. The witness type catches "I pasted the
     * wrong body into the wrong method" at compile time. The framework wraps
     * the body with a kebab-cased selector derived from the record name
     * ({@code st_root} → {@code .st-root}).</p>
     *
     * <p>{@link #cssVariables()} is inherited from {@link CssGroupImpl} and
     * defaults to an empty map; override to emit a {@code :root \{ … \}}
     * block of CSS custom properties before the per-class rules.</p>
     *
     * @param <TH> the {@link Theme} this impl realizes
     */
    public interface Impl<TH extends Theme> extends CssGroupImpl<StudioStyles, TH> {

        @Override default StudioStyles group() { return INSTANCE; }

        // {@link CssGroupImpl#globalRules()} is the home for non-class-keyed rules
        // (html/body resets, pseudo-classes, descendants, media queries) — override there.

        // -- structure --
        CssBlock<st_root> st_root();
        CssBlock<st_header> st_header();
        CssBlock<st_nav> st_nav();
        CssBlock<st_brand> st_brand();
        CssBlock<st_brand_dot> st_brand_dot();
        CssBlock<st_brand_word> st_brand_word();
        CssBlock<st_breadcrumbs> st_breadcrumbs();
        CssBlock<st_crumb> st_crumb();
        CssBlock<st_crumb_sep> st_crumb_sep();
        CssBlock<st_main> st_main();
        CssBlock<st_kicker> st_kicker();
        CssBlock<st_title> st_title();
        CssBlock<st_subtitle> st_subtitle();
        CssBlock<st_section> st_section();
        CssBlock<st_section_title> st_section_title();
        CssBlock<st_grid> st_grid();
        CssBlock<st_card> st_card();
        CssBlock<st_card_featured> st_card_featured();
        CssBlock<st_card_title> st_card_title();
        CssBlock<st_card_summary> st_card_summary();
        CssBlock<st_card_meta> st_card_meta();
        CssBlock<st_card_link> st_card_link();

        // -- badges --
        CssBlock<st_badge> st_badge();
        CssBlock<st_badge_whitepaper> st_badge_whitepaper();
        CssBlock<st_badge_brochure> st_badge_brochure();
        CssBlock<st_badge_rfc> st_badge_rfc();
        CssBlock<st_badge_brand> st_badge_brand();
        CssBlock<st_badge_session> st_badge_session();
        CssBlock<st_badge_reference> st_badge_reference();
        CssBlock<st_badge_rename> st_badge_rename();

        // -- doc browser controls --
        CssBlock<st_search_wrap> st_search_wrap();
        CssBlock<st_search> st_search();
        CssBlock<st_filter> st_filter();
        CssBlock<st_filter_btn> st_filter_btn();
        CssBlock<st_filter_btn_active> st_filter_btn_active();

        // -- doc reader layout --
        CssBlock<st_layout> st_layout();
        CssBlock<st_sidebar> st_sidebar();
        CssBlock<st_sidebar_title> st_sidebar_title();
        CssBlock<st_toc> st_toc();
        CssBlock<st_toc_item> st_toc_item();
        CssBlock<st_toc_h1> st_toc_h1();
        CssBlock<st_toc_h2> st_toc_h2();
        CssBlock<st_toc_h3> st_toc_h3();
        CssBlock<st_toc_active> st_toc_active();
        CssBlock<st_doc> st_doc();
        CssBlock<st_doc_meta> st_doc_meta();

        // -- shared --
        CssBlock<st_loading> st_loading();
        CssBlock<st_error> st_error();
        CssBlock<st_footer> st_footer();

        // -- catalogue tiles --
        CssBlock<st_app_pill> st_app_pill();
        CssBlock<st_app_pill_dark> st_app_pill_dark();
        CssBlock<st_app_pill_icon> st_app_pill_icon();
        CssBlock<st_app_pill_label> st_app_pill_label();
        CssBlock<st_app_pill_desc> st_app_pill_desc();

        // -- RFC implementation tracker --
        CssBlock<st_overall_progress> st_overall_progress();
        CssBlock<st_overall_bar> st_overall_bar();
        CssBlock<st_overall_fill> st_overall_fill();
        CssBlock<st_overall_pct> st_overall_pct();
        CssBlock<st_step_card> st_step_card();
        CssBlock<st_step_head> st_step_head();
        CssBlock<st_step_id> st_step_id();
        CssBlock<st_step_label> st_step_label();
        CssBlock<st_step_summary> st_step_summary();
        CssBlock<st_step_progress> st_step_progress();
        CssBlock<st_step_progress_bar> st_step_progress_bar();
        CssBlock<st_step_progress_fill> st_step_progress_fill();
        CssBlock<st_step_meta> st_step_meta();
        CssBlock<st_status_badge> st_status_badge();
        CssBlock<st_status_not_started> st_status_not_started();
        CssBlock<st_status_in_progress> st_status_in_progress();
        CssBlock<st_status_blocked> st_status_blocked();
        CssBlock<st_status_done> st_status_done();
        CssBlock<st_panel> st_panel();
        CssBlock<st_panel_title> st_panel_title();
        CssBlock<st_task_list> st_task_list();
        CssBlock<st_task_item> st_task_item();
        CssBlock<st_task_done> st_task_done();
        CssBlock<st_task_box> st_task_box();
        CssBlock<st_dep> st_dep();
        CssBlock<st_acceptance> st_acceptance();
        CssBlock<st_effort> st_effort();
    }

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
