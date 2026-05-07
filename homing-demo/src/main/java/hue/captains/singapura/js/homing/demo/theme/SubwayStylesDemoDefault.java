package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.demo.css.SubwayStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default visual identity for {@link SubwayStyles}.
 * Translated 1-to-1 from the legacy {@code SubwayStyles.css} file.
 */
public record SubwayStylesDemoDefault() implements SubwayStyles.Impl<DemoDefault> {

    public static final SubwayStylesDemoDefault INSTANCE = new SubwayStylesDemoDefault();

    @Override public DemoDefault theme() { return DemoDefault.INSTANCE; }

    @Override public Map<String, String> cssVariables() {
        var m = new LinkedHashMap<String, String>();
        m.put("color-scheme",   "light dark");
        m.put("--shadow-color", "light-dark(rgba(0, 0, 0, 0.15), rgba(0, 0, 0, 0.5))");
        m.put("--cell-sheen",   "light-dark(rgba(0, 0, 0, 0.02), rgba(255, 255, 255, 0.03))");
        return m;
    }

    @Override public String globalRules() { return """
        body {
            margin: 0;
            font-family: "Helvetica Neue", Arial, sans-serif;
            background: light-dark(#f5f5f7, #1a1a2e);
            color: light-dark(#222, #eee);
        }
        #app {
            padding: 24px 32px;
        }
        .subway-title::after {
            content: "";
            display: block;
            width: 100%;
            height: 4px;
            margin-top: 8px;
            background: repeating-linear-gradient(
                90deg,
                #ff6b35 0, #ff6b35 20px,
                transparent 20px, transparent 28px,
                #00b4d8 28px, #00b4d8 48px,
                transparent 48px, transparent 56px,
                #3ea63f 56px, #3ea63f 76px,
                transparent 76px, transparent 84px
            );
        }
        .subway-cell:hover {
            background: light-dark(rgba(62, 166, 63, 0.08), rgba(62, 166, 63, 0.15));
            box-shadow: 0 0 12px light-dark(rgba(62, 166, 63, 0.15), rgba(62, 166, 63, 0.3));
        }
        .subway-cell svg {
            width: 100%;
            height: auto;
            filter: drop-shadow(1px 1px 2px var(--shadow-color));
        }
        """;
    }

    @Override public CssBlock<SubwayStyles.subway_title> subway_title() { return CssBlock.of("""
        font-size: 2rem;
        font-weight: 800;
        text-transform: uppercase;
        letter-spacing: 4px;
        color: light-dark(#e55a25, #ff6b35);
        text-shadow: 2px 2px 0 light-dark(rgba(0, 0, 0, 0.1), #000);
        margin: 0 0 4px 0;
        position: relative;
        """);
    }

    @Override public CssBlock<SubwayStyles.subway_hint> subway_hint() { return CssBlock.of("""
        color: light-dark(#666, #888);
        font-style: italic;
        font-size: 0.9rem;
        margin: 12px 0 16px 0;
        """);
    }

    @Override public CssBlock<SubwayStyles.subway_grid> subway_grid() { return CssBlock.of("""
        display: grid;
        grid-template-columns: repeat(5, 1fr);
        gap: 6px;
        max-width: 620px;
        background: light-dark(#eef1f5, #16213e);
        border: 2px solid light-dark(#ccc, #333);
        border-radius: 8px;
        padding: 12px;
        box-shadow: 0 0 30px light-dark(rgba(0, 180, 216, 0.08), rgba(0, 180, 216, 0.15));
        """);
    }

    @Override public CssBlock<SubwayStyles.subway_cell> subway_cell() { return CssBlock.of("""
        display: flex;
        align-items: center;
        justify-content: center;
        transition: transform 0.3s ease;
        background: linear-gradient(
            145deg,
            var(--cell-sheen) 0%,
            transparent 100%
        );
        border-radius: 4px;
        padding: 4px;
        """);
    }
}
