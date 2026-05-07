package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.demo.css.PitchDeckStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default visual identity rendered against {@link PitchDeckStyles}.
 * Translated 1-to-1 from the legacy {@code PitchDeckStyles.css} file.
 */
public record PitchDeckStylesDemoDefault() implements PitchDeckStyles.Impl<DemoDefault> {

    public static final PitchDeckStylesDemoDefault INSTANCE = new PitchDeckStylesDemoDefault();

    @Override public DemoDefault theme() { return DemoDefault.INSTANCE; }

    @Override public Map<String, String> cssVariables() {
        var m = new LinkedHashMap<String, String>();
        m.put("--pd-navy",      "#1E2761");
        m.put("--pd-navy-deep", "#111936");
        m.put("--pd-ice",       "#CADCFC");
        m.put("--pd-amber",     "#F4B942");
        m.put("--pd-amber-dk",  "#C8921E");
        m.put("--pd-white",     "#FFFFFF");
        m.put("--pd-offwhite",  "#FAFBFD");
        m.put("--pd-gray-dk",   "#3B4A6B");
        m.put("--pd-gray-mid",  "#64748B");
        m.put("--pd-gray-lt",   "#E2E8F0");
        m.put("--pd-green",     "#059669");
        m.put("--pd-red",       "#DC2626");
        return m;
    }

    @Override public String globalRules() { return """
        html, body {
            margin: 0;
            padding: 0;
            width: 100%;
            height: 100%;
            overflow: hidden;
            background: var(--pd-navy-deep);
            color: var(--pd-gray-dk);
            font-family: "Calibri", "Segoe UI", system-ui, sans-serif;
        }
        .pd-slide::before {
            content: "";
            position: absolute;
            top: 0; left: 0; bottom: 0;
            width: 6px;
            background: var(--pd-amber);
        }
        .pd-slide-dark::before { background: var(--pd-amber); }
        .pd-slide-dark .pd-kicker { color: var(--pd-amber); }
        .pd-slide-dark .pd-title { color: var(--pd-white); }
        .pd-slide-dark .pd-subtitle { color: var(--pd-ice); font-style: normal; }
        .pd-slide-dark .pd-body { color: var(--pd-ice); }
        @keyframes pd-pulse {
            0%, 100% { opacity: 0.65; }
            50%      { opacity: 1; }
        }
        .pd-btn:hover {
            background: rgba(202, 220, 252, 0.1);
            border-color: var(--pd-amber);
            color: var(--pd-amber);
        }
        .pd-btn:disabled {
            opacity: 0.3;
            cursor: default;
            pointer-events: none;
        }
        .pd-btn-primary:hover {
            background: var(--pd-white);
            color: var(--pd-navy);
            border-color: var(--pd-white);
        }
        .pd-btn-bgm::before {
            content: "♪";
            font-size: 16px;
            color: var(--pd-gray-mid);
        }
        .pd-btn-bgm-on::before { color: var(--pd-amber); }
        .pd-dot:hover { background: rgba(244, 185, 66, 0.6); }
        .pd-card-dark .pd-card-head { color: var(--pd-white); }
        .pd-card-dark .pd-card-body { color: var(--pd-ice); }
        .pd-stat::before {
            content: "";
            position: absolute;
            top: 0; left: 0; bottom: 0;
            width: 3px;
            background: var(--pd-amber);
        }
        .pd-table-cell:first-child {
            font-weight: 700;
            color: var(--pd-navy);
        }
        .pd-table-row-featured.pd-table-cell:first-child { color: var(--pd-navy-deep); }
        .pd-slide-dark .pd-hint { color: var(--pd-amber); }
        .pd-slide-dark .pd-accent { color: var(--pd-amber); }
        .pd-arch-layer {
            background: var(--pd-white);
            border: 1px solid var(--pd-ice);
            border-left: 4px solid var(--pd-amber);
            padding: 14px 18px;
            margin-bottom: 10px;
            border-radius: 4px;
            opacity: 0;
            transform: translateX(-20px);
            transition: opacity 400ms ease, transform 400ms ease;
            box-shadow: 0 2px 6px rgba(30, 39, 97, 0.05);
        }
        .pd-arch-layer.pd-arch-shown {
            opacity: 1;
            transform: translateX(0);
        }
        .pd-arch-layer-base {
            background: var(--pd-navy);
            color: var(--pd-ice);
        }
        .pd-arch-layer-num {
            font-family: "Georgia", serif;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 3px;
            color: var(--pd-gray-mid);
            text-transform: uppercase;
        }
        .pd-arch-layer-base .pd-arch-layer-num { color: var(--pd-amber); }
        .pd-arch-layer-title {
            font-family: "Georgia", serif;
            font-size: 18px;
            font-weight: 700;
            color: var(--pd-navy);
            margin-top: 4px;
        }
        .pd-arch-layer-base .pd-arch-layer-title { color: var(--pd-white); }
        .pd-arch-layer-desc {
            font-size: 12px;
            color: var(--pd-gray-dk);
            margin-top: 4px;
        }
        .pd-arch-layer-base .pd-arch-layer-desc { color: var(--pd-ice); }
        """;
    }

    @Override public CssBlock<PitchDeckStyles.pd_root> pd_root() { return CssBlock.of("""
        position: fixed;
        inset: 0;
        display: flex;
        flex-direction: column;
        background: linear-gradient(180deg, #FAFBFD 0%, #EEF1F8 100%);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_stage> pd_stage() { return CssBlock.of("""
        flex: 1;
        position: relative;
        overflow: hidden;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_slide> pd_slide() { return CssBlock.of("""
        position: absolute;
        inset: 0;
        padding: 48px 64px 48px 80px;
        opacity: 0;
        pointer-events: none;
        transform: translateY(12px);
        transition: opacity 420ms ease, transform 420ms ease;
        box-sizing: border-box;
        overflow: auto;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_slide_active> pd_slide_active() { return CssBlock.of("""
        opacity: 1;
        pointer-events: auto;
        transform: translateY(0);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_slide_dark> pd_slide_dark() { return CssBlock.of("""
        background: var(--pd-navy);
        color: var(--pd-ice);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_kicker> pd_kicker() { return CssBlock.of("""
        font-size: 12px;
        letter-spacing: 4px;
        color: var(--pd-amber-dk);
        font-weight: 700;
        text-transform: uppercase;
        margin: 0 0 12px 0;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_title> pd_title() { return CssBlock.of("""
        font-family: "Georgia", "Times New Roman", serif;
        font-size: 42px;
        font-weight: 700;
        color: var(--pd-navy);
        margin: 0 0 18px 0;
        line-height: 1.15;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_subtitle> pd_subtitle() { return CssBlock.of("""
        font-size: 18px;
        color: var(--pd-gray-mid);
        font-style: italic;
        margin: 0 0 24px 0;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_body> pd_body() { return CssBlock.of("font-size: 16px; line-height: 1.6;"); }

    @Override public CssBlock<PitchDeckStyles.pd_hero> pd_hero() { return CssBlock.of("""
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 18px;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_hero_title> pd_hero_title() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 140px;
        font-weight: 700;
        color: var(--pd-white);
        line-height: 0.95;
        letter-spacing: -3px;
        margin: 0;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_hero_tag> pd_hero_tag() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 32px;
        font-style: italic;
        color: var(--pd-ice);
        margin: 0;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_hero_sub> pd_hero_sub() { return CssBlock.of("""
        font-size: 18px;
        color: var(--pd-ice);
        max-width: 880px;
        line-height: 1.5;
        margin: 0;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_hero_press> pd_hero_press() { return CssBlock.of("""
        margin-top: 18px;
        font-size: 13px;
        letter-spacing: 4px;
        color: var(--pd-amber);
        font-weight: 700;
        text-transform: uppercase;
        display: inline-flex;
        align-items: center;
        gap: 10px;
        animation: pd-pulse 2s ease-in-out infinite;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_nav> pd_nav() { return CssBlock.of("""
        flex: 0 0 auto;
        height: 64px;
        padding: 0 24px;
        display: flex;
        align-items: center;
        gap: 14px;
        background: var(--pd-navy);
        border-top: 2px solid var(--pd-amber);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_btn> pd_btn() { return CssBlock.of("""
        font-family: inherit;
        font-size: 13px;
        font-weight: 700;
        letter-spacing: 1px;
        padding: 10px 18px;
        border-radius: 4px;
        border: 1px solid var(--pd-ice);
        background: transparent;
        color: var(--pd-ice);
        cursor: pointer;
        transition: all 160ms ease;
        text-transform: uppercase;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_btn_primary> pd_btn_primary() { return CssBlock.of("""
        background: var(--pd-amber);
        color: var(--pd-navy-deep);
        border-color: var(--pd-amber);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_btn_ghost> pd_btn_ghost() { return CssBlock.of("""
        border: none;
        padding: 6px 10px;
        color: var(--pd-ice);
        font-size: 11px;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_btn_bgm> pd_btn_bgm() { return CssBlock.of("""
        margin-left: auto;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        background: transparent;
        border: 1px solid rgba(202, 220, 252, 0.3);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_btn_bgm_on> pd_btn_bgm_on() { return CssBlock.of("""
        border-color: var(--pd-amber);
        color: var(--pd-amber);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_progress> pd_progress() { return CssBlock.of("""
        position: absolute;
        top: 0; left: 0; right: 0;
        height: 3px;
        background: rgba(30, 39, 97, 0.08);
        z-index: 10;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_progress_fill> pd_progress_fill() { return CssBlock.of("""
        height: 100%;
        background: linear-gradient(90deg, var(--pd-amber), var(--pd-amber-dk));
        transition: width 360ms ease;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_dots> pd_dots() { return CssBlock.of("""
        display: flex;
        gap: 6px;
        margin-left: 16px;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_dot> pd_dot() { return CssBlock.of("""
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: rgba(202, 220, 252, 0.3);
        cursor: pointer;
        transition: all 160ms ease;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_dot_active> pd_dot_active() { return CssBlock.of("""
        background: var(--pd-amber);
        transform: scale(1.3);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_grid2> pd_grid2() { return CssBlock.of("display: grid; grid-template-columns: 1fr 1fr; gap: 22px;"); }
    @Override public CssBlock<PitchDeckStyles.pd_grid3> pd_grid3() { return CssBlock.of("display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 18px;"); }
    @Override public CssBlock<PitchDeckStyles.pd_grid4> pd_grid4() { return CssBlock.of("display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px;"); }

    @Override public CssBlock<PitchDeckStyles.pd_card> pd_card() { return CssBlock.of("""
        background: var(--pd-white);
        border: 1px solid var(--pd-ice);
        border-left: 4px solid var(--pd-amber);
        border-radius: 4px;
        padding: 20px 22px;
        box-shadow: 0 2px 6px rgba(30, 39, 97, 0.06);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_card_dark> pd_card_dark() { return CssBlock.of("""
        background: var(--pd-navy-deep);
        border: 1px solid var(--pd-amber);
        border-radius: 4px;
        padding: 22px;
        color: var(--pd-ice);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_card_accent> pd_card_accent() { return CssBlock.of("""
        background: rgba(244, 185, 66, 0.1);
        border: 1px solid var(--pd-amber);
        border-radius: 4px;
        padding: 18px 22px;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_card_head> pd_card_head() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 20px;
        font-weight: 700;
        color: var(--pd-navy);
        margin: 0 0 8px 0;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_card_body> pd_card_body() { return CssBlock.of("""
        font-size: 14px;
        color: var(--pd-gray-dk);
        line-height: 1.55;
        margin: 0;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_stat> pd_stat() { return CssBlock.of("""
        background: var(--pd-white);
        border: 1px solid var(--pd-ice);
        border-radius: 6px;
        padding: 20px 22px;
        box-shadow: 0 2px 6px rgba(30, 39, 97, 0.06);
        position: relative;
        overflow: hidden;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_stat_num> pd_stat_num() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 40px;
        font-weight: 700;
        color: var(--pd-navy);
        line-height: 1;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_stat_label> pd_stat_label() { return CssBlock.of("""
        font-size: 13px;
        color: var(--pd-gray-mid);
        margin-top: 8px;
        line-height: 1.4;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_row> pd_row() { return CssBlock.of("""
        display: grid;
        grid-template-columns: 240px 1fr;
        background: var(--pd-white);
        border: 1px solid var(--pd-gray-lt);
        border-left: 3px solid var(--pd-navy);
        padding: 12px 18px;
        align-items: center;
        margin-bottom: 8px;
        border-radius: 3px;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_row_head> pd_row_head() { return CssBlock.of("""
        font-weight: 700;
        color: var(--pd-navy);
        font-size: 14px;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_row_body> pd_row_body() { return CssBlock.of("""
        color: var(--pd-gray-dk);
        font-size: 14px;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_diagram> pd_diagram() { return CssBlock.of("""
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 8px;
        box-sizing: border-box;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_diagram_svg> pd_diagram_svg() { return CssBlock.of("""
        max-width: 100%;
        max-height: 100%;
        width: auto;
        height: auto;
        filter: drop-shadow(0 4px 12px rgba(30, 39, 97, 0.1));
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_table> pd_table() { return CssBlock.of("""
        display: grid;
        grid-template-columns: 1.6fr 1.2fr 1.4fr 1.6fr 1.4fr 1.4fr;
        gap: 1px;
        background: var(--pd-gray-lt);
        border-radius: 4px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(30, 39, 97, 0.08);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_table_header> pd_table_header() { return CssBlock.of("""
        background: var(--pd-navy);
        color: var(--pd-white);
        padding: 14px 12px;
        font-size: 12px;
        font-weight: 700;
        letter-spacing: 1px;
        text-transform: uppercase;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_table_row> pd_table_row() { return CssBlock.of("""
        background: var(--pd-white);
        padding: 12px 12px;
        font-size: 13px;
        color: var(--pd-gray-dk);
        display: flex;
        align-items: center;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_table_row_featured> pd_table_row_featured() { return CssBlock.of("""
        background: var(--pd-amber);
        color: var(--pd-navy-deep);
        font-weight: 700;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_table_cell> pd_table_cell() { return CssBlock.of(""); }

    @Override public CssBlock<PitchDeckStyles.pd_badge_built> pd_badge_built() { return CssBlock.of("""
        display: inline-block;
        font-size: 11px;
        font-weight: 700;
        letter-spacing: 2px;
        padding: 4px 10px;
        border-radius: 3px;
        text-transform: uppercase;
        margin-bottom: 12px;
        background: var(--pd-green);
        color: var(--pd-white);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_badge_designed> pd_badge_designed() { return CssBlock.of("""
        display: inline-block;
        font-size: 11px;
        font-weight: 700;
        letter-spacing: 2px;
        padding: 4px 10px;
        border-radius: 3px;
        text-transform: uppercase;
        margin-bottom: 12px;
        background: var(--pd-amber);
        color: var(--pd-navy-deep);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_check> pd_check() { return CssBlock.of("""
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 22px;
        height: 22px;
        border-radius: 50%;
        font-size: 13px;
        font-weight: 700;
        color: var(--pd-white);
        margin-right: 8px;
        flex: 0 0 auto;
        background: var(--pd-green);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_cross> pd_cross() { return CssBlock.of("""
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 22px;
        height: 22px;
        border-radius: 50%;
        font-size: 13px;
        font-weight: 700;
        color: var(--pd-white);
        margin-right: 8px;
        flex: 0 0 auto;
        background: var(--pd-red);
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_hint> pd_hint() { return CssBlock.of("""
        margin-top: 18px;
        font-size: 12px;
        color: var(--pd-amber-dk);
        font-style: italic;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_accent> pd_accent() { return CssBlock.of("color: var(--pd-amber-dk); font-weight: 700;"); }

    @Override public CssBlock<PitchDeckStyles.pd_mono> pd_mono() { return CssBlock.of("""
        font-family: "Consolas", "Courier New", monospace;
        font-size: 13px;
        background: rgba(30, 39, 97, 0.05);
        padding: 2px 6px;
        border-radius: 3px;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_quote> pd_quote() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 22px;
        font-style: italic;
        color: var(--pd-ice);
        border-left: 3px solid var(--pd-amber);
        padding-left: 20px;
        margin: 16px 0;
        line-height: 1.5;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_cta> pd_cta() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 60px;
        font-weight: 700;
        color: var(--pd-white);
        margin: 10px 0 30px 0;
        line-height: 1.05;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_toast> pd_toast() { return CssBlock.of("""
        position: fixed;
        bottom: 80px;
        left: 50%;
        transform: translateX(-50%) translateY(20px);
        background: var(--pd-navy);
        color: var(--pd-amber);
        padding: 10px 22px;
        border-radius: 24px;
        border: 1px solid var(--pd-amber);
        font-size: 13px;
        font-weight: 700;
        letter-spacing: 1px;
        opacity: 0;
        pointer-events: none;
        transition: opacity 260ms ease, transform 260ms ease;
        z-index: 100;
        text-transform: uppercase;
        """);
    }

    @Override public CssBlock<PitchDeckStyles.pd_toast_show> pd_toast_show() { return CssBlock.of("""
        opacity: 1;
        transform: translateX(-50%) translateY(0);
        """);
    }
}
