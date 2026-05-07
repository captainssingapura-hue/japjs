package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.demo.css.CatalogueStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default visual identity rendered against {@link CatalogueStyles}.
 * Translated 1-to-1 from the legacy {@code CatalogueStyles.css} file.
 */
public record CatalogueStylesDemoDefault() implements CatalogueStyles.Impl<DemoDefault> {

    public static final CatalogueStylesDemoDefault INSTANCE = new CatalogueStylesDemoDefault();

    @Override public DemoDefault theme() { return DemoDefault.INSTANCE; }

    @Override public Map<String, String> cssVariables() {
        var m = new LinkedHashMap<String, String>();
        m.put("--cat-navy",      "#1E2761");
        m.put("--cat-navy-deep", "#111936");
        m.put("--cat-ice",       "#CADCFC");
        m.put("--cat-amber",     "#F4B942");
        m.put("--cat-amber-dk",  "#C8921E");
        m.put("--cat-white",     "#FFFFFF");
        m.put("--cat-offwhite",  "#FAFBFD");
        m.put("--cat-gray-dk",   "#3B4A6B");
        m.put("--cat-gray-mid",  "#64748B");
        m.put("--cat-gray-lt",   "#E2E8F0");
        return m;
    }

    @Override public String globalRules() { return """
        html, body {
            margin: 0;
            padding: 0;
            background: linear-gradient(180deg, #FAFBFD 0%, #EEF1F8 100%);
            color: var(--cat-gray-dk);
            font-family: "Calibri", "Segoe UI", system-ui, sans-serif;
            min-height: 100vh;
        }
        .cat-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 18px rgba(30, 39, 97, 0.12);
            border-color: var(--cat-amber-dk);
            border-left-color: var(--cat-amber-dk);
        }
        .cat-card-featured:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 28px rgba(30, 39, 97, 0.25);
            border-color: var(--cat-amber);
            border-left-color: var(--cat-amber);
        }
        .cat-card-featured .cat-card-head {
            flex: 1;
            margin-bottom: 0;
        }
        .cat-card-featured .cat-card-title {
            color: var(--cat-white);
            font-size: 28px;
        }
        .cat-card-featured .cat-card-desc {
            color: var(--cat-ice);
            font-size: 15px;
            flex: 0;
            margin-top: 6px;
        }
        .cat-card-featured .cat-card-meta {
            border-top: none;
            padding-top: 0;
            margin-top: 0;
            flex-direction: column;
            align-items: flex-end;
            gap: 12px;
        }
        .cat-card-featured .cat-card-link {
            color: var(--cat-amber);
            font-size: 14px;
        }
        .cat-card-featured .cat-mono {
            background: rgba(202, 220, 252, 0.12);
            color: var(--cat-ice);
        }
        .cat-footer code {
            font-family: "Consolas", "Courier New", monospace;
            background: rgba(30, 39, 97, 0.05);
            padding: 1px 6px;
            border-radius: 3px;
            color: var(--cat-navy);
        }
        """;
    }

    @Override public CssBlock<CatalogueStyles.cat_root> cat_root() { return CssBlock.of("""
        max-width: 1180px;
        margin: 0 auto;
        padding: 64px 48px 96px;
        box-sizing: border-box;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_header> cat_header() { return CssBlock.of("""
        margin-bottom: 56px;
        padding-bottom: 32px;
        border-bottom: 1px solid var(--cat-gray-lt);
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_kicker> cat_kicker() { return CssBlock.of("""
        font-size: 12px;
        letter-spacing: 4px;
        color: var(--cat-amber-dk);
        font-weight: 700;
        text-transform: uppercase;
        margin: 0 0 14px 0;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_title> cat_title() { return CssBlock.of("""
        font-family: "Georgia", "Times New Roman", serif;
        font-size: 56px;
        font-weight: 700;
        color: var(--cat-navy);
        margin: 0 0 12px 0;
        line-height: 1.1;
        letter-spacing: -1px;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_subtitle> cat_subtitle() { return CssBlock.of("""
        font-size: 18px;
        color: var(--cat-gray-mid);
        margin: 0;
        max-width: 760px;
        line-height: 1.55;
        font-style: italic;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_section> cat_section() { return CssBlock.of("""
        margin-top: 48px;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_section_title> cat_section_title() { return CssBlock.of("""
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
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_grid> cat_grid() { return CssBlock.of("""
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
        gap: 18px;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_card> cat_card() { return CssBlock.of("""
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
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_card_featured> cat_card_featured() { return CssBlock.of("""
        background: var(--cat-navy);
        color: var(--cat-ice);
        border-left-color: var(--cat-amber);
        grid-column: 1 / -1;
        flex-direction: row;
        align-items: center;
        min-height: 140px;
        padding: 28px 32px;
        gap: 32px;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_card_head> cat_card_head() { return CssBlock.of("""
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 12px;
        margin-bottom: 10px;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_card_title> cat_card_title() { return CssBlock.of("""
        font-family: "Georgia", serif;
        font-size: 22px;
        font-weight: 700;
        color: var(--cat-navy);
        margin: 0;
        line-height: 1.2;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_card_desc> cat_card_desc() { return CssBlock.of("""
        font-size: 14px;
        color: var(--cat-gray-mid);
        line-height: 1.5;
        margin: 0;
        flex: 1;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_card_meta> cat_card_meta() { return CssBlock.of("""
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-top: 14px;
        padding-top: 14px;
        border-top: 1px solid var(--cat-gray-lt);
        font-size: 11px;
        color: var(--cat-gray-mid);
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_card_link> cat_card_link() { return CssBlock.of("""
        color: var(--cat-amber-dk);
        font-weight: 700;
        font-size: 12px;
        letter-spacing: 1px;
        text-transform: uppercase;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_badge> cat_badge() { return CssBlock.of("""
        display: inline-block;
        font-size: 10px;
        font-weight: 700;
        letter-spacing: 2px;
        padding: 3px 8px;
        border-radius: 2px;
        text-transform: uppercase;
        flex: 0 0 auto;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_badge_pitch> cat_badge_pitch() { return CssBlock.of("background: var(--cat-amber);  color: var(--cat-navy-deep);"); }
    @Override public CssBlock<CatalogueStyles.cat_badge_3d>    cat_badge_3d()    { return CssBlock.of("background: var(--cat-navy);   color: var(--cat-amber);"); }
    @Override public CssBlock<CatalogueStyles.cat_badge_anim>  cat_badge_anim()  { return CssBlock.of("background: var(--cat-ice);    color: var(--cat-navy);"); }
    @Override public CssBlock<CatalogueStyles.cat_badge_basic> cat_badge_basic() { return CssBlock.of("background: var(--cat-gray-lt); color: var(--cat-navy);"); }

    @Override public CssBlock<CatalogueStyles.cat_footer> cat_footer() { return CssBlock.of("""
        margin-top: 80px;
        padding-top: 32px;
        border-top: 1px solid var(--cat-gray-lt);
        color: var(--cat-gray-mid);
        font-size: 12px;
        line-height: 1.6;
        """);
    }

    @Override public CssBlock<CatalogueStyles.cat_mono> cat_mono() { return CssBlock.of("""
        font-family: "Consolas", "Courier New", monospace;
        font-size: 11px;
        color: var(--cat-gray-mid);
        background: rgba(30, 39, 97, 0.04);
        padding: 2px 8px;
        border-radius: 3px;
        word-break: break-all;
        """);
    }
}
