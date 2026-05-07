package hue.captains.singapura.js.homing.studio.base.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The default Homing visual identity rendered against {@link StudioStyles}.
 *
 * <p>Translated 1-to-1 from the legacy {@code StudioStyles.css} file. Every
 * method body holds only the declarations for the bare class; pseudo-classes,
 * descendant selectors, media queries, and html/body resets live in
 * {@link #globalRules()}.</p>
 *
 * <p>This impl ships with {@code homing-studio-base} so any consumer of that
 * module gets a complete working theme with zero design effort. Override one
 * or more methods to tweak; replace the whole class to redesign.</p>
 */
public record StudioStylesHomingDefault() implements StudioStyles.Impl<HomingDefault> {

    public static final StudioStylesHomingDefault INSTANCE = new StudioStylesHomingDefault();

    @Override public HomingDefault theme() { return HomingDefault.INSTANCE; }

    // -------------------------------------------------------------------
    // CSS custom properties (cascading vars). LinkedHashMap preserves
    // declaration order in the emitted :root { ... } block.
    // -------------------------------------------------------------------

    @Override public Map<String, String> cssVariables() {
        var m = new LinkedHashMap<String, String>();
        m.put("--st-navy",      "#1E2761");
        m.put("--st-navy-deep", "#111936");
        m.put("--st-ice",       "#CADCFC");
        m.put("--st-amber",     "#F4B942");
        m.put("--st-amber-dk",  "#C8921E");
        m.put("--st-white",     "#FFFFFF");
        m.put("--st-offwhite",  "#FAFBFD");
        m.put("--st-gray-dk",   "#3B4A6B");
        m.put("--st-gray-mid",  "#64748B");
        m.put("--st-gray-lt",   "#E2E8F0");
        m.put("--st-gray-vlt",  "#F1F4F9");
        return Map.copyOf(m);
    }

    // -------------------------------------------------------------------
    // Global / non-class-keyed rules: html/body, pseudo-classes,
    // descendant selectors, media queries.
    // -------------------------------------------------------------------

    @Override public String globalRules() { return """
        :root { color-scheme: light dark; }
        @media (prefers-color-scheme: dark) {
            :root {
                --st-offwhite:  #0F1320;
                --st-white:     #1A1F36;
                --st-gray-vlt:  #232943;
                --st-gray-lt:   #2D3454;
                --st-gray-mid:  #94A3B8;
                --st-gray-dk:   #E2E8F0;
                --st-ice:       #B8C9F2;
                /* navy/navy-deep/amber/amber-dk kept — brand accents read on both modes. */
            }
        }
        html, body {
            margin: 0;
            padding: 0;
            background: var(--st-offwhite);
            color: var(--st-gray-dk);
            font-family: "Calibri", "Segoe UI", system-ui, sans-serif;
            min-height: 100vh;
        }
        .st-crumb:hover { color: var(--st-amber); }
        .st-search:focus {
            outline: none;
            border-color: var(--st-amber);
            box-shadow: 0 0 0 3px rgba(244, 185, 66, 0.18);
        }
        .st-filter-btn:hover {
            border-color: var(--st-amber);
            color: var(--st-amber-dk);
        }
        .st-filter-btn-active:hover {
            background: var(--st-navy-deep);
            color: var(--st-amber);
            border-color: var(--st-navy-deep);
        }
        .st-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(30, 39, 97, 0.12);
            border-left-color: var(--st-amber-dk);
        }
        .st-card-featured .st-card-title {
            color: var(--st-white);
            font-size: 22px;
        }
        .st-card-featured .st-card-summary {
            color: var(--st-ice);
            font-size: 14px;
            margin-top: 4px;
        }
        .st-card-featured .st-card-meta {
            border: none;
            padding: 0;
            margin: 0;
            flex-direction: column;
            align-items: flex-end;
            gap: 8px;
        }
        .st-card-featured .st-card-link {
            color: var(--st-amber);
        }
        @media (max-width: 920px) {
            .st-layout { grid-template-columns: 1fr; }
            .st-sidebar { display: none; }
        }
        .st-toc-item:hover {
            color: var(--st-navy);
            border-left-color: var(--st-amber);
        }
        .st-doc h1, .st-doc h2, .st-doc h3, .st-doc h4 {
            font-family: "Georgia", serif;
            color: var(--st-navy);
            margin: 1.6em 0 0.6em 0;
            line-height: 1.25;
            scroll-margin-top: 24px;
        }
        .st-doc h1 { font-size: 32px; border-bottom: 2px solid var(--st-amber); padding-bottom: 8px; margin-top: 0; }
        .st-doc h2 { font-size: 24px; }
        .st-doc h3 { font-size: 19px; }
        .st-doc h4 { font-size: 16px; color: var(--st-amber-dk); letter-spacing: 1px; text-transform: uppercase; }
        .st-doc p  { margin: 0 0 1em 0; }
        .st-doc ul, .st-doc ol { margin: 0 0 1em 0; padding-left: 1.5em; }
        .st-doc li { margin: 0.3em 0; }
        .st-doc a { color: var(--st-amber-dk); text-decoration: underline; text-underline-offset: 2px; }
        .st-doc a:hover { color: var(--st-navy); }
        .st-doc blockquote {
            margin: 1em 0;
            padding: 4px 0 4px 18px;
            border-left: 3px solid var(--st-amber);
            color: var(--st-gray-mid);
            font-style: italic;
        }
        .st-doc code {
            font-family: "Consolas", "Courier New", monospace;
            font-size: 0.92em;
            background: var(--st-gray-vlt);
            color: var(--st-navy);
            padding: 1px 6px;
            border-radius: 3px;
        }
        .st-doc pre {
            background: var(--st-navy-deep);
            color: var(--st-ice);
            padding: 14px 18px;
            border-radius: 4px;
            overflow-x: auto;
            margin: 1em 0;
            font-size: 13px;
            line-height: 1.5;
        }
        .st-doc pre code {
            background: transparent;
            color: inherit;
            padding: 0;
            font-size: inherit;
        }
        .st-doc table {
            width: 100%;
            border-collapse: collapse;
            margin: 1em 0;
            font-size: 14px;
        }
        .st-doc th, .st-doc td {
            text-align: left;
            padding: 8px 12px;
            border-bottom: 1px solid var(--st-gray-lt);
            vertical-align: top;
        }
        .st-doc th {
            background: var(--st-navy);
            color: var(--st-white);
            font-weight: 700;
            border: none;
        }
        .st-doc tr:nth-child(even) td { background: var(--st-gray-vlt); }
        .st-doc hr {
            border: none;
            border-top: 1px solid var(--st-gray-lt);
            margin: 2em 0;
        }
        .st-doc img { max-width: 100%; }
        .st-footer code {
            font-family: "Consolas", "Courier New", monospace;
            background: var(--st-gray-vlt);
            color: var(--st-navy);
            padding: 1px 6px;
            border-radius: 3px;
        }
        .st-app-pill:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 18px rgba(30, 39, 97, 0.12);
            border-left-color: var(--st-amber-dk);
        }
        .st-app-pill-dark:hover {
            background: var(--st-navy-deep);
        }
        .st-app-pill-dark .st-app-pill-icon {
            background: var(--st-amber);
            color: var(--st-navy-deep);
        }
        .st-app-pill-dark .st-app-pill-label {
            color: var(--st-white);
        }
        .st-app-pill-dark .st-app-pill-desc {
            color: var(--st-ice);
        }
        .st-step-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(30, 39, 97, 0.10);
            border-left-color: var(--st-amber-dk);
        }
        .st-task-done .st-task-box {
            background: var(--st-amber);
            border-color: var(--st-amber-dk);
            color: var(--st-navy-deep);
            font-weight: 700;
        }
        .st-dep:hover { border-color: var(--st-amber); }
        """;
    }

    // -------------------------------------------------------------------
    // Per-class CSS bodies. Order mirrors the records in StudioStyles.
    // -------------------------------------------------------------------

    // -- shell --

    @Override public CssBlock<StudioStyles.st_root> st_root() { return CssBlock.of("""
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        """);
    }
    @Override public CssBlock<StudioStyles.st_header> st_header() { return CssBlock.of("""
        background: var(--st-navy);
        border-bottom: 2px solid var(--st-amber);
        padding: 14px 32px;
        display: flex;
        align-items: center;
        gap: 24px;
        flex: 0 0 auto;
        """);
    }
    @Override public CssBlock<StudioStyles.st_nav> st_nav() { return CssBlock.of("""
        margin-left: auto;
        display: flex;
        gap: 4px;
        align-items: center;
        """);
    }
    @Override public CssBlock<StudioStyles.st_brand> st_brand() { return CssBlock.of("""
        display: flex;
        align-items: center;
        gap: 10px;
        text-decoration: none;
        color: var(--st-white);
        """);
    }
    @Override public CssBlock<StudioStyles.st_brand_dot> st_brand_dot() { return CssBlock.of("""
        width: 12px;
        height: 12px;
        background: var(--st-amber);
        """);
    }
    @Override public CssBlock<StudioStyles.st_brand_word> st_brand_word() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-style: italic;
        font-size: 22px;
        color: var(--st-white);
        line-height: 1;
        """);
    }
    @Override public CssBlock<StudioStyles.st_breadcrumbs> st_breadcrumbs() { return CssBlock.of("""
        color: var(--st-ice);
        font-size: 13px;
        display: flex;
        align-items: center;
        gap: 8px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_crumb> st_crumb() { return CssBlock.of("""
        color: var(--st-ice);
        text-decoration: none;
        """);
    }
    @Override public CssBlock<StudioStyles.st_crumb_sep> st_crumb_sep() { return CssBlock.of("""
        color: var(--st-gray-mid);
        """);
    }

    // -- main / titles --

    @Override public CssBlock<StudioStyles.st_main> st_main() { return CssBlock.of("""
        flex: 1;
        max-width: 1280px;
        width: 100%;
        margin: 0 auto;
        padding: 36px 32px 64px;
        box-sizing: border-box;
        """);
    }
    @Override public CssBlock<StudioStyles.st_kicker> st_kicker() { return CssBlock.of("""
        font-size: 12px;
        letter-spacing: 4px;
        color: var(--st-amber-dk);
        font-weight: 700;
        text-transform: uppercase;
        margin: 0 0 12px 0;
        """);
    }
    @Override public CssBlock<StudioStyles.st_title> st_title() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 44px;
        font-weight: 700;
        color: var(--st-navy);
        margin: 0 0 12px 0;
        line-height: 1.1;
        letter-spacing: -0.5px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_subtitle> st_subtitle() { return CssBlock.of("""
        font-size: 17px;
        color: var(--st-gray-mid);
        margin: 0 0 32px 0;
        max-width: 760px;
        line-height: 1.55;
        font-style: italic;
        """);
    }

    // -- sections --

    @Override public CssBlock<StudioStyles.st_section> st_section() { return CssBlock.of("""
        margin-top: 40px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_section_title> st_section_title() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 12px;
        font-weight: 700;
        color: var(--st-navy);
        letter-spacing: 5px;
        text-transform: uppercase;
        margin: 0 0 16px 0;
        padding-bottom: 8px;
        border-bottom: 2px solid var(--st-amber);
        display: inline-block;
        """);
    }

    // -- grid + cards --

    @Override public CssBlock<StudioStyles.st_grid> st_grid() { return CssBlock.of("""
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
        gap: 16px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_card> st_card() { return CssBlock.of("""
        background: var(--st-white);
        border: 1px solid var(--st-gray-lt);
        border-left: 4px solid var(--st-amber);
        border-radius: 4px;
        padding: 18px 20px;
        cursor: pointer;
        text-decoration: none;
        color: inherit;
        display: flex;
        flex-direction: column;
        box-shadow: 0 1px 3px rgba(30, 39, 97, 0.04);
        transition: transform 160ms ease, box-shadow 160ms ease, border-color 160ms ease;
        min-height: 150px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_card_featured> st_card_featured() { return CssBlock.of("""
        grid-column: 1 / -1;
        background: var(--st-navy);
        color: var(--st-ice);
        border-left-color: var(--st-amber);
        flex-direction: row;
        align-items: center;
        gap: 24px;
        padding: 20px 28px;
        min-height: auto;
        """);
    }
    @Override public CssBlock<StudioStyles.st_card_title> st_card_title() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 18px;
        font-weight: 700;
        color: var(--st-navy);
        margin: 0 0 6px 0;
        line-height: 1.25;
        """);
    }
    @Override public CssBlock<StudioStyles.st_card_summary> st_card_summary() { return CssBlock.of("""
        font-size: 13px;
        color: var(--st-gray-mid);
        line-height: 1.5;
        margin: 0;
        flex: 1;
        """);
    }
    @Override public CssBlock<StudioStyles.st_card_meta> st_card_meta() { return CssBlock.of("""
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 14px;
        padding-top: 12px;
        border-top: 1px solid var(--st-gray-lt);
        font-size: 11px;
        color: var(--st-gray-mid);
        """);
    }
    @Override public CssBlock<StudioStyles.st_card_link> st_card_link() { return CssBlock.of("""
        color: var(--st-amber-dk);
        font-weight: 700;
        font-size: 11px;
        letter-spacing: 1.5px;
        text-transform: uppercase;
        """);
    }

    // -- badges --

    @Override public CssBlock<StudioStyles.st_badge> st_badge() { return CssBlock.of("""
        display: inline-block;
        font-size: 10px;
        font-weight: 700;
        letter-spacing: 1.5px;
        padding: 3px 8px;
        border-radius: 2px;
        text-transform: uppercase;
        """);
    }
    @Override public CssBlock<StudioStyles.st_badge_whitepaper> st_badge_whitepaper() { return CssBlock.of("background: var(--st-navy); color: var(--st-amber);"); }
    @Override public CssBlock<StudioStyles.st_badge_brochure>   st_badge_brochure()   { return CssBlock.of("background: var(--st-amber); color: var(--st-navy-deep);"); }
    @Override public CssBlock<StudioStyles.st_badge_rfc>        st_badge_rfc()        { return CssBlock.of("background: var(--st-navy-deep); color: var(--st-ice);"); }
    @Override public CssBlock<StudioStyles.st_badge_brand>      st_badge_brand()      { return CssBlock.of("background: var(--st-ice); color: var(--st-navy);"); }
    @Override public CssBlock<StudioStyles.st_badge_session>    st_badge_session()    { return CssBlock.of("background: var(--st-gray-vlt); color: var(--st-gray-dk); border: 1px solid var(--st-gray-lt);"); }
    @Override public CssBlock<StudioStyles.st_badge_reference>  st_badge_reference()  { return CssBlock.of("background: var(--st-gray-lt); color: var(--st-navy);"); }
    @Override public CssBlock<StudioStyles.st_badge_rename>     st_badge_rename()     { return CssBlock.of("background: var(--st-amber-dk); color: var(--st-ice);"); }

    // -- search & filter --

    @Override public CssBlock<StudioStyles.st_search_wrap> st_search_wrap() { return CssBlock.of("""
        margin: 8px 0 24px 0;
        display: flex;
        gap: 12px;
        align-items: center;
        flex-wrap: wrap;
        """);
    }
    @Override public CssBlock<StudioStyles.st_search> st_search() { return CssBlock.of("""
        flex: 1;
        min-width: 280px;
        padding: 10px 16px;
        border: 1px solid var(--st-gray-lt);
        border-radius: 4px;
        font-family: inherit;
        font-size: 14px;
        color: var(--st-gray-dk);
        background: var(--st-white);
        transition: border-color 160ms ease, box-shadow 160ms ease;
        """);
    }
    @Override public CssBlock<StudioStyles.st_filter> st_filter() { return CssBlock.of("""
        display: flex;
        gap: 6px;
        flex-wrap: wrap;
        """);
    }
    @Override public CssBlock<StudioStyles.st_filter_btn> st_filter_btn() { return CssBlock.of("""
        font-family: inherit;
        font-size: 11px;
        font-weight: 700;
        letter-spacing: 1.5px;
        padding: 6px 12px;
        border: 1px solid var(--st-gray-lt);
        background: var(--st-white);
        color: var(--st-gray-dk);
        cursor: pointer;
        border-radius: 3px;
        text-transform: uppercase;
        transition: all 140ms ease;
        """);
    }
    @Override public CssBlock<StudioStyles.st_filter_btn_active> st_filter_btn_active() { return CssBlock.of("""
        background: var(--st-navy);
        color: var(--st-white);
        border-color: var(--st-navy);
        """);
    }

    // -- doc reader layout --

    @Override public CssBlock<StudioStyles.st_layout> st_layout() { return CssBlock.of("""
        display: grid;
        grid-template-columns: 260px 1fr;
        gap: 32px;
        margin-top: 8px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_sidebar> st_sidebar() { return CssBlock.of("""
        position: sticky;
        top: 24px;
        align-self: start;
        max-height: calc(100vh - 48px);
        overflow-y: auto;
        padding: 4px 8px 4px 4px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_sidebar_title> st_sidebar_title() { return CssBlock.of("""
        font-size: 11px;
        letter-spacing: 4px;
        color: var(--st-amber-dk);
        font-weight: 700;
        text-transform: uppercase;
        margin: 0 0 12px 0;
        """);
    }
    @Override public CssBlock<StudioStyles.st_toc> st_toc() { return CssBlock.of("""
        display: flex;
        flex-direction: column;
        gap: 2px;
        border-left: 1px solid var(--st-gray-lt);
        """);
    }
    @Override public CssBlock<StudioStyles.st_toc_item> st_toc_item() { return CssBlock.of("""
        display: block;
        padding: 4px 12px;
        font-size: 13px;
        color: var(--st-gray-mid);
        text-decoration: none;
        line-height: 1.4;
        border-left: 2px solid transparent;
        margin-left: -1px;
        transition: color 140ms ease, border-color 140ms ease;
        """);
    }
    @Override public CssBlock<StudioStyles.st_toc_h1> st_toc_h1() { return CssBlock.of("font-weight: 700; color: var(--st-navy); padding-left: 12px;"); }
    @Override public CssBlock<StudioStyles.st_toc_h2> st_toc_h2() { return CssBlock.of("padding-left: 24px;"); }
    @Override public CssBlock<StudioStyles.st_toc_h3> st_toc_h3() { return CssBlock.of("padding-left: 36px; font-size: 12px;"); }
    @Override public CssBlock<StudioStyles.st_toc_active> st_toc_active() { return CssBlock.of("""
        color: var(--st-navy);
        border-left-color: var(--st-amber);
        background: var(--st-gray-vlt);
        """);
    }

    // -- doc body --

    @Override public CssBlock<StudioStyles.st_doc> st_doc() { return CssBlock.of("""
        font-size: 16px;
        line-height: 1.7;
        color: var(--st-gray-dk);
        max-width: 820px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_doc_meta> st_doc_meta() { return CssBlock.of("""
        margin-bottom: 24px;
        padding-bottom: 20px;
        border-bottom: 1px solid var(--st-gray-lt);
        display: flex;
        gap: 12px;
        align-items: center;
        flex-wrap: wrap;
        """);
    }

    // -- loading / error --

    @Override public CssBlock<StudioStyles.st_loading> st_loading() { return CssBlock.of("""
        text-align: center;
        padding: 48px 16px;
        color: var(--st-gray-mid);
        font-style: italic;
        font-size: 14px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_error> st_error() { return CssBlock.of("""
        background: rgba(220, 38, 38, 0.06);
        border: 1px solid rgba(220, 38, 38, 0.3);
        border-left: 4px solid #DC2626;
        padding: 16px 20px;
        border-radius: 4px;
        color: #7F1D1D;
        margin: 16px 0;
        """);
    }

    // -- footer --

    @Override public CssBlock<StudioStyles.st_footer> st_footer() { return CssBlock.of("""
        margin-top: 64px;
        padding-top: 24px;
        border-top: 1px solid var(--st-gray-lt);
        color: var(--st-gray-mid);
        font-size: 12px;
        """);
    }

    // -- launcher pills (catalogue) --

    @Override public CssBlock<StudioStyles.st_app_pill> st_app_pill() { return CssBlock.of("""
        background: var(--st-white);
        border: 1px solid var(--st-gray-lt);
        border-left: 4px solid var(--st-amber);
        padding: 22px 24px;
        border-radius: 4px;
        text-decoration: none;
        color: inherit;
        display: flex;
        align-items: center;
        gap: 18px;
        box-shadow: 0 1px 3px rgba(30, 39, 97, 0.05);
        transition: all 160ms ease;
        """);
    }
    @Override public CssBlock<StudioStyles.st_app_pill_dark> st_app_pill_dark() { return CssBlock.of("""
        background: var(--st-navy);
        color: var(--st-ice);
        border-left-color: var(--st-amber);
        """);
    }
    @Override public CssBlock<StudioStyles.st_app_pill_icon> st_app_pill_icon() { return CssBlock.of("""
        flex: 0 0 56px;
        height: 56px;
        background: var(--st-amber);
        color: var(--st-navy);
        border-radius: 4px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-family: "Georgia", serif;
        font-style: italic;
        font-size: 28px;
        font-weight: 700;
        """);
    }
    @Override public CssBlock<StudioStyles.st_app_pill_label> st_app_pill_label() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 19px;
        font-weight: 700;
        color: var(--st-navy);
        margin: 0 0 4px 0;
        """);
    }
    @Override public CssBlock<StudioStyles.st_app_pill_desc> st_app_pill_desc() { return CssBlock.of("""
        font-size: 13px;
        color: var(--st-gray-mid);
        margin: 0;
        line-height: 1.5;
        """);
    }

    // -- RFC implementation tracker --

    @Override public CssBlock<StudioStyles.st_overall_progress> st_overall_progress() { return CssBlock.of("""
        background: var(--st-navy);
        color: var(--st-ice);
        border-left: 4px solid var(--st-amber);
        padding: 18px 24px;
        border-radius: 4px;
        margin: 0 0 24px 0;
        display: flex;
        align-items: center;
        gap: 24px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_overall_bar> st_overall_bar() { return CssBlock.of("""
        flex: 1;
        height: 12px;
        background: rgba(202, 220, 252, 0.18);
        border-radius: 6px;
        overflow: hidden;
        """);
    }
    @Override public CssBlock<StudioStyles.st_overall_fill> st_overall_fill() { return CssBlock.of("""
        height: 100%;
        background: linear-gradient(90deg, var(--st-amber), var(--st-amber-dk));
        transition: width 280ms ease;
        """);
    }
    @Override public CssBlock<StudioStyles.st_overall_pct> st_overall_pct() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 28px;
        font-weight: 700;
        color: var(--st-amber);
        flex: 0 0 auto;
        """);
    }
    @Override public CssBlock<StudioStyles.st_step_card> st_step_card() { return CssBlock.of("""
        background: var(--st-white);
        border: 1px solid var(--st-gray-lt);
        border-left: 4px solid var(--st-amber);
        border-radius: 4px;
        padding: 18px 22px;
        margin-bottom: 12px;
        text-decoration: none;
        color: inherit;
        display: block;
        box-shadow: 0 1px 3px rgba(30, 39, 97, 0.04);
        transition: transform 160ms ease, box-shadow 160ms ease, border-color 160ms ease;
        """);
    }
    @Override public CssBlock<StudioStyles.st_step_head> st_step_head() { return CssBlock.of("""
        display: flex;
        align-items: baseline;
        gap: 14px;
        margin-bottom: 6px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_step_id> st_step_id() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-style: italic;
        font-size: 13px;
        color: var(--st-amber-dk);
        font-weight: 700;
        flex: 0 0 auto;
        letter-spacing: 1px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_step_label> st_step_label() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 18px;
        font-weight: 700;
        color: var(--st-navy);
        margin: 0;
        flex: 1;
        """);
    }
    @Override public CssBlock<StudioStyles.st_step_summary> st_step_summary() { return CssBlock.of("""
        font-size: 13px;
        color: var(--st-gray-mid);
        margin: 0 0 10px 0;
        line-height: 1.5;
        """);
    }
    @Override public CssBlock<StudioStyles.st_step_progress> st_step_progress() { return CssBlock.of("""
        display: flex;
        align-items: center;
        gap: 12px;
        margin-top: 10px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_step_progress_bar> st_step_progress_bar() { return CssBlock.of("""
        flex: 1;
        height: 6px;
        background: var(--st-gray-vlt);
        border-radius: 3px;
        overflow: hidden;
        """);
    }
    @Override public CssBlock<StudioStyles.st_step_progress_fill> st_step_progress_fill() { return CssBlock.of("""
        height: 100%;
        background: var(--st-amber);
        transition: width 280ms ease;
        """);
    }
    @Override public CssBlock<StudioStyles.st_step_meta> st_step_meta() { return CssBlock.of("""
        display: flex;
        gap: 10px;
        align-items: center;
        flex: 0 0 auto;
        font-size: 11px;
        color: var(--st-gray-mid);
        """);
    }

    // -- status badges --

    @Override public CssBlock<StudioStyles.st_status_badge> st_status_badge() { return CssBlock.of("""
        display: inline-block;
        font-size: 10px;
        font-weight: 700;
        letter-spacing: 1.5px;
        padding: 3px 8px;
        border-radius: 2px;
        text-transform: uppercase;
        """);
    }
    @Override public CssBlock<StudioStyles.st_status_not_started> st_status_not_started() { return CssBlock.of("background: var(--st-gray-lt); color: var(--st-gray-dk);"); }
    @Override public CssBlock<StudioStyles.st_status_in_progress> st_status_in_progress() { return CssBlock.of("background: var(--st-amber); color: var(--st-navy-deep);"); }
    @Override public CssBlock<StudioStyles.st_status_blocked>     st_status_blocked()     { return CssBlock.of("background: #FECACA; color: #7F1D1D;"); }
    @Override public CssBlock<StudioStyles.st_status_done>        st_status_done()        { return CssBlock.of("background: #BBF7D0; color: #14532D;"); }

    // -- step detail panels --

    @Override public CssBlock<StudioStyles.st_panel> st_panel() { return CssBlock.of("""
        background: var(--st-white);
        border: 1px solid var(--st-gray-lt);
        border-radius: 4px;
        padding: 18px 22px;
        margin-bottom: 16px;
        box-shadow: 0 1px 3px rgba(30, 39, 97, 0.04);
        """);
    }
    @Override public CssBlock<StudioStyles.st_panel_title> st_panel_title() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 12px;
        font-weight: 700;
        color: var(--st-amber-dk);
        letter-spacing: 4px;
        text-transform: uppercase;
        margin: 0 0 12px 0;
        """);
    }

    // -- task list --

    @Override public CssBlock<StudioStyles.st_task_list> st_task_list() { return CssBlock.of("""
        list-style: none;
        margin: 0;
        padding: 0;
        """);
    }
    @Override public CssBlock<StudioStyles.st_task_item> st_task_item() { return CssBlock.of("""
        display: flex;
        align-items: flex-start;
        gap: 10px;
        padding: 6px 0;
        color: var(--st-gray-dk);
        font-size: 14px;
        line-height: 1.5;
        """);
    }
    @Override public CssBlock<StudioStyles.st_task_done> st_task_done() { return CssBlock.of("""
        color: var(--st-gray-mid);
        text-decoration: line-through;
        """);
    }
    @Override public CssBlock<StudioStyles.st_task_box> st_task_box() { return CssBlock.of("""
        flex: 0 0 16px;
        width: 16px;
        height: 16px;
        border: 1.5px solid var(--st-gray-mid);
        border-radius: 3px;
        margin-top: 2px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        color: var(--st-white);
        background: var(--st-white);
        """);
    }

    // -- dependencies --

    @Override public CssBlock<StudioStyles.st_dep> st_dep() { return CssBlock.of("""
        display: inline-block;
        margin: 4px 6px 4px 0;
        padding: 4px 10px;
        background: var(--st-gray-vlt);
        border: 1px solid var(--st-gray-lt);
        border-radius: 3px;
        font-size: 12px;
        color: var(--st-navy);
        text-decoration: none;
        """);
    }

    // -- acceptance / effort --

    @Override public CssBlock<StudioStyles.st_acceptance> st_acceptance() { return CssBlock.of("""
        color: var(--st-gray-dk);
        line-height: 1.6;
        font-size: 14px;
        """);
    }
    @Override public CssBlock<StudioStyles.st_effort> st_effort() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-style: italic;
        color: var(--st-amber-dk);
        font-size: 14px;
        """);
    }
}
