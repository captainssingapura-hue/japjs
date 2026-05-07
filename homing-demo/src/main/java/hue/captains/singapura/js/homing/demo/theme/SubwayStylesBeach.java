package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.demo.css.SubwayStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Beach visual identity for {@link SubwayStyles}.
 * Translated 1-to-1 from the legacy {@code SubwayStyles.beach.css} file.
 */
public record SubwayStylesBeach() implements SubwayStyles.Impl<Beach> {

    public static final SubwayStylesBeach INSTANCE = new SubwayStylesBeach();

    @Override public Beach theme() { return Beach.INSTANCE; }

    @Override public Map<String, String> cssVariables() {
        var m = new LinkedHashMap<String, String>();
        m.put("color-scheme",   "light");
        m.put("--shadow-color", "rgba(0, 0, 0, 0.2)");
        m.put("--cell-sheen",   "rgba(255, 255, 255, 0.15)");
        return m;
    }

    @Override public String globalRules() { return """
        body {
            margin: 0;
            font-family: "Helvetica Neue", Arial, sans-serif;
            background: linear-gradient(180deg, #87CEEB 0%, #f5deb3 60%, #c2a366 100%);
            background-attachment: fixed;
            color: #3b2e1a;
            min-height: 100vh;
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
                #e07020 0, #e07020 20px,
                transparent 20px, transparent 28px,
                #3a9bdc 28px, #3a9bdc 48px,
                transparent 48px, transparent 56px,
                #5ab85a 56px, #5ab85a 76px,
                transparent 76px, transparent 84px
            );
        }
        .subway-cell:hover {
            background: rgba(90, 184, 90, 0.15);
            box-shadow: 0 0 12px rgba(90, 184, 90, 0.25);
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
        color: #e07020;
        text-shadow: 2px 2px 0 rgba(255, 255, 255, 0.5);
        margin: 0 0 4px 0;
        position: relative;
        """);
    }

    @Override public CssBlock<SubwayStyles.subway_hint> subway_hint() { return CssBlock.of("""
        color: #6b5a42;
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
        background: rgba(255, 255, 255, 0.3);
        border: 2px solid #c2a366;
        border-radius: 8px;
        padding: 12px;
        box-shadow: 0 4px 20px rgba(194, 163, 102, 0.3);
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
