package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

public record PitchDeckStyles() implements CssGroup<PitchDeckStyles> {
    public static final PitchDeckStyles INSTANCE = new PitchDeckStyles();

    public record pd_root() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                position: fixed;
                inset: 0;
                display: flex;
                flex-direction: column;
                background: linear-gradient(180deg, #FAFBFD 0%, #EEF1F8 100%);
                """;
        }
    }
    public record pd_stage() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                flex: 1;
                position: relative;
                overflow: hidden;
                """;
        }
    }
    public record pd_slide() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                position: absolute;
                inset: 0;
                padding: 48px 64px 48px 80px;
                opacity: 0;
                pointer-events: none;
                transform: translateY(12px);
                transition: opacity 420ms ease, transform 420ms ease;
                box-sizing: border-box;
                overflow: auto;
                """;
        }
    }
    public record pd_slide_active() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                opacity: 1;
                pointer-events: auto;
                transform: translateY(0);
                """;
        }
    }
    public record pd_slide_dark() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                background: var(--pd-navy);
                color: var(--pd-ice);
                """;
        }
    }
    public record pd_kicker() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-size: 12px;
                letter-spacing: 4px;
                color: var(--pd-amber-dk);
                font-weight: 700;
                text-transform: uppercase;
                margin: 0 0 12px 0;
                """;
        }
    }
    public record pd_title() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-family: "Georgia", "Times New Roman", serif;
                font-size: 42px;
                font-weight: 700;
                color: var(--pd-navy);
                margin: 0 0 18px 0;
                line-height: 1.15;
                """;
        }
    }
    public record pd_subtitle() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-size: 18px;
                color: var(--pd-gray-mid);
                font-style: italic;
                margin: 0 0 24px 0;
                """;
        }
    }
    public record pd_body() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return "font-size: 16px; line-height: 1.6;"; }
    }

    public record pd_hero() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                height: 100%;
                display: flex;
                flex-direction: column;
                justify-content: center;
                gap: 18px;
                """;
        }
    }
    public record pd_hero_title() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-family: "Georgia", serif;
                font-size: 140px;
                font-weight: 700;
                color: var(--pd-white);
                line-height: 0.95;
                letter-spacing: -3px;
                margin: 0;
                """;
        }
    }
    public record pd_hero_tag() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-family: "Georgia", serif;
                font-size: 32px;
                font-style: italic;
                color: var(--pd-ice);
                margin: 0;
                """;
        }
    }
    public record pd_hero_sub() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-size: 18px;
                color: var(--pd-ice);
                max-width: 880px;
                line-height: 1.5;
                margin: 0;
                """;
        }
    }
    public record pd_hero_press() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
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
                """;
        }
    }

    public record pd_nav() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                flex: 0 0 auto;
                height: 64px;
                padding: 0 24px;
                display: flex;
                align-items: center;
                gap: 14px;
                background: var(--pd-navy);
                border-top: 2px solid var(--pd-amber);
                """;
        }
    }
    public record pd_btn() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
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
                """;
        }
    }
    public record pd_btn_primary() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                background: var(--pd-amber);
                color: var(--pd-navy-deep);
                border-color: var(--pd-amber);
                """;
        }
    }
    public record pd_btn_ghost() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                border: none;
                padding: 6px 10px;
                color: var(--pd-ice);
                font-size: 11px;
                """;
        }
    }
    public record pd_btn_bgm() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                margin-left: auto;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                background: transparent;
                border: 1px solid rgba(202, 220, 252, 0.3);
                """;
        }
    }
    public record pd_btn_bgm_on() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                border-color: var(--pd-amber);
                color: var(--pd-amber);
                """;
        }
    }

    public record pd_progress() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                position: absolute;
                top: 0; left: 0; right: 0;
                height: 3px;
                background: rgba(30, 39, 97, 0.08);
                z-index: 10;
                """;
        }
    }
    public record pd_progress_fill() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                height: 100%;
                background: linear-gradient(90deg, var(--pd-amber), var(--pd-amber-dk));
                transition: width 360ms ease;
                """;
        }
    }
    public record pd_dots() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                display: flex;
                gap: 6px;
                margin-left: 16px;
                """;
        }
    }
    public record pd_dot() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                width: 8px;
                height: 8px;
                border-radius: 50%;
                background: rgba(202, 220, 252, 0.3);
                cursor: pointer;
                transition: all 160ms ease;
                """;
        }
    }
    public record pd_dot_active() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                background: var(--pd-amber);
                transform: scale(1.3);
                """;
        }
    }

    public record pd_grid2() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return "display: grid; grid-template-columns: 1fr 1fr; gap: 22px;"; }
    }
    public record pd_grid3() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return "display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 18px;"; }
    }
    public record pd_grid4() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return "display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px;"; }
    }
    public record pd_card() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                background: var(--pd-white);
                border: 1px solid var(--pd-ice);
                border-left: 4px solid var(--pd-amber);
                border-radius: 4px;
                padding: 20px 22px;
                box-shadow: 0 2px 6px rgba(30, 39, 97, 0.06);
                """;
        }
    }
    public record pd_card_dark() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                background: var(--pd-navy-deep);
                border: 1px solid var(--pd-amber);
                border-radius: 4px;
                padding: 22px;
                color: var(--pd-ice);
                """;
        }
    }
    public record pd_card_accent() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                background: rgba(244, 185, 66, 0.1);
                border: 1px solid var(--pd-amber);
                border-radius: 4px;
                padding: 18px 22px;
                """;
        }
    }
    public record pd_card_head() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-family: "Georgia", serif;
                font-size: 20px;
                font-weight: 700;
                color: var(--pd-navy);
                margin: 0 0 8px 0;
                """;
        }
    }
    public record pd_card_body() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-size: 14px;
                color: var(--pd-gray-dk);
                line-height: 1.55;
                margin: 0;
                """;
        }
    }

    public record pd_stat() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                background: var(--pd-white);
                border: 1px solid var(--pd-ice);
                border-radius: 6px;
                padding: 20px 22px;
                box-shadow: 0 2px 6px rgba(30, 39, 97, 0.06);
                position: relative;
                overflow: hidden;
                """;
        }
    }
    public record pd_stat_num() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-family: "Georgia", serif;
                font-size: 40px;
                font-weight: 700;
                color: var(--pd-navy);
                line-height: 1;
                """;
        }
    }
    public record pd_stat_label() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-size: 13px;
                color: var(--pd-gray-mid);
                margin-top: 8px;
                line-height: 1.4;
                """;
        }
    }

    public record pd_row() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                display: grid;
                grid-template-columns: 240px 1fr;
                background: var(--pd-white);
                border: 1px solid var(--pd-gray-lt);
                border-left: 3px solid var(--pd-navy);
                padding: 12px 18px;
                align-items: center;
                margin-bottom: 8px;
                border-radius: 3px;
                """;
        }
    }
    public record pd_row_head() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-weight: 700;
                color: var(--pd-navy);
                font-size: 14px;
                """;
        }
    }
    public record pd_row_body() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                color: var(--pd-gray-dk);
                font-size: 14px;
                """;
        }
    }

    public record pd_diagram() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                width: 100%;
                height: 100%;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 8px;
                box-sizing: border-box;
                """;
        }
    }
    public record pd_diagram_svg() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                max-width: 100%;
                max-height: 100%;
                width: auto;
                height: auto;
                filter: drop-shadow(0 4px 12px rgba(30, 39, 97, 0.1));
                """;
        }
    }

    public record pd_table() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                display: grid;
                grid-template-columns: 1.6fr 1.2fr 1.4fr 1.6fr 1.4fr 1.4fr;
                gap: 1px;
                background: var(--pd-gray-lt);
                border-radius: 4px;
                overflow: hidden;
                box-shadow: 0 2px 8px rgba(30, 39, 97, 0.08);
                """;
        }
    }
    public record pd_table_header() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                background: var(--pd-navy);
                color: var(--pd-white);
                padding: 14px 12px;
                font-size: 12px;
                font-weight: 700;
                letter-spacing: 1px;
                text-transform: uppercase;
                """;
        }
    }
    public record pd_table_row() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                background: var(--pd-white);
                padding: 12px 12px;
                font-size: 13px;
                color: var(--pd-gray-dk);
                display: flex;
                align-items: center;
                """;
        }
    }
    public record pd_table_row_featured() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                background: var(--pd-amber);
                color: var(--pd-navy-deep);
                font-weight: 700;
                """;
        }
    }
    public record pd_table_cell() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return ""; }
    }

    public record pd_badge_built() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
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
                """;
        }
    }
    public record pd_badge_designed() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
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
                """;
        }
    }

    public record pd_check() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
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
                """;
        }
    }
    public record pd_cross() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
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
                """;
        }
    }

    public record pd_hint() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                margin-top: 18px;
                font-size: 12px;
                color: var(--pd-amber-dk);
                font-style: italic;
                """;
        }
    }
    public record pd_accent() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return "color: var(--pd-amber-dk); font-weight: 700;"; }
    }
    public record pd_mono() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-family: "Consolas", "Courier New", monospace;
                font-size: 13px;
                background: rgba(30, 39, 97, 0.05);
                padding: 2px 6px;
                border-radius: 3px;
                """;
        }
    }

    public record pd_quote() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-family: "Georgia", serif;
                font-size: 22px;
                font-style: italic;
                color: var(--pd-ice);
                border-left: 3px solid var(--pd-amber);
                padding-left: 20px;
                margin: 16px 0;
                line-height: 1.5;
                """;
        }
    }
    public record pd_cta() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                font-family: "Georgia", serif;
                font-size: 60px;
                font-weight: 700;
                color: var(--pd-white);
                margin: 10px 0 30px 0;
                line-height: 1.05;
                """;
        }
    }

    public record pd_toast() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
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
                """;
        }
    }
    public record pd_toast_show() implements CssClass<PitchDeckStyles> {
        @Override public String body() { return """
                opacity: 1;
                transform: translateX(-50%) translateY(0);
                """;
        }
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
