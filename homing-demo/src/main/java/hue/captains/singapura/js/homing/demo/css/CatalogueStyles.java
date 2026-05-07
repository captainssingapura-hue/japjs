package hue.captains.singapura.js.homing.demo.css;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssImportsFor;

import java.util.List;

public record CatalogueStyles() implements CssGroup<CatalogueStyles> {
    public static final CatalogueStyles INSTANCE = new CatalogueStyles();

    public record cat_root() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                max-width: 1180px;
                margin: 0 auto;
                padding: 64px 48px 96px;
                box-sizing: border-box;
                """;
        }
    }
    public record cat_header() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                margin-bottom: 56px;
                padding-bottom: 32px;
                border-bottom: 1px solid var(--cat-gray-lt);
                """;
        }
    }
    public record cat_kicker() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                font-size: 12px;
                letter-spacing: 4px;
                color: var(--cat-amber-dk);
                font-weight: 700;
                text-transform: uppercase;
                margin: 0 0 14px 0;
                """;
        }
    }
    public record cat_title() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                font-family: "Georgia", "Times New Roman", serif;
                font-size: 56px;
                font-weight: 700;
                color: var(--cat-navy);
                margin: 0 0 12px 0;
                line-height: 1.1;
                letter-spacing: -1px;
                """;
        }
    }
    public record cat_subtitle() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                font-size: 18px;
                color: var(--cat-gray-mid);
                margin: 0;
                max-width: 760px;
                line-height: 1.55;
                font-style: italic;
                """;
        }
    }
    public record cat_section() implements CssClass<CatalogueStyles> {
        @Override public String body() { return "margin-top: 48px;"; }
    }
    public record cat_section_title() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                font-family: "Georgia", serif;
                font-size: 13px;
                font-weight: 700;
                color: var(--cat-navy);
                letter-spacing: 5px;
                text-transform: uppercase;
                margin: 0 0 20px 0;
                padding-bottom: 10px;
                border-bottom: 2px solid var(--cat-amber);
                display: inline-block;
                """;
        }
    }
    public record cat_grid() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
                gap: 18px;
                """;
        }
    }
    public record cat_card() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                background: var(--cat-white);
                border: 1px solid var(--cat-gray-lt);
                border-left: 4px solid var(--cat-amber);
                border-radius: 4px;
                padding: 22px 24px;
                box-shadow: 0 2px 6px rgba(30, 39, 97, 0.05);
                cursor: pointer;
                text-decoration: none;
                color: inherit;
                display: flex;
                flex-direction: column;
                transition: transform 180ms ease, box-shadow 180ms ease, border-color 180ms ease;
                min-height: 200px;
                """;
        }
    }
    public record cat_card_featured() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                background: var(--cat-navy);
                color: var(--cat-ice);
                border-left-color: var(--cat-amber);
                grid-column: 1 / -1;
                flex-direction: row;
                align-items: center;
                min-height: 140px;
                padding: 28px 32px;
                gap: 32px;
                """;
        }
    }
    public record cat_card_head() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                gap: 12px;
                margin-bottom: 10px;
                """;
        }
    }
    public record cat_card_title() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                font-family: "Georgia", serif;
                font-size: 22px;
                font-weight: 700;
                color: var(--cat-navy);
                margin: 0;
                line-height: 1.2;
                """;
        }
    }
    public record cat_card_desc() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                font-size: 14px;
                color: var(--cat-gray-mid);
                line-height: 1.5;
                margin: 0;
                flex: 1;
                """;
        }
    }
    public record cat_card_meta() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-top: 14px;
                padding-top: 14px;
                border-top: 1px solid var(--cat-gray-lt);
                font-size: 11px;
                color: var(--cat-gray-mid);
                """;
        }
    }
    public record cat_card_link() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                color: var(--cat-amber-dk);
                font-weight: 700;
                font-size: 12px;
                letter-spacing: 1px;
                text-transform: uppercase;
                """;
        }
    }
    public record cat_badge() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                display: inline-block;
                font-size: 10px;
                font-weight: 700;
                letter-spacing: 2px;
                padding: 3px 8px;
                border-radius: 2px;
                text-transform: uppercase;
                flex: 0 0 auto;
                """;
        }
    }
    public record cat_badge_pitch() implements CssClass<CatalogueStyles> {
        @Override public String body() { return "background: var(--cat-amber);  color: var(--cat-navy-deep);"; }
    }
    public record cat_badge_3d() implements CssClass<CatalogueStyles> {
        @Override public String body() { return "background: var(--cat-navy);   color: var(--cat-amber);"; }
    }
    public record cat_badge_anim() implements CssClass<CatalogueStyles> {
        @Override public String body() { return "background: var(--cat-ice);    color: var(--cat-navy);"; }
    }
    public record cat_badge_basic() implements CssClass<CatalogueStyles> {
        @Override public String body() { return "background: var(--cat-gray-lt); color: var(--cat-navy);"; }
    }
    public record cat_footer() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                margin-top: 80px;
                padding-top: 32px;
                border-top: 1px solid var(--cat-gray-lt);
                color: var(--cat-gray-mid);
                font-size: 12px;
                line-height: 1.6;
                """;
        }
    }
    public record cat_mono() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
                font-family: "Consolas", "Courier New", monospace;
                font-size: 11px;
                color: var(--cat-gray-mid);
                background: rgba(30, 39, 97, 0.04);
                padding: 2px 8px;
                border-radius: 3px;
                word-break: break-all;
                """;
        }
    }

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
