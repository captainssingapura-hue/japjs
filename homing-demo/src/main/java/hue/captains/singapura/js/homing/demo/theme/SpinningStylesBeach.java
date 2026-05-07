package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.demo.css.SpinningStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Beach visual identity for {@link SpinningStyles}.
 * Translated 1-to-1 from the legacy {@code SpinningStyles.beach.css} file.
 */
public record SpinningStylesBeach() implements SpinningStyles.Impl<Beach> {

    public static final SpinningStylesBeach INSTANCE = new SpinningStylesBeach();

    @Override public Beach theme() { return Beach.INSTANCE; }

    @Override public Map<String, String> cssVariables() {
        var m = new LinkedHashMap<String, String>();
        m.put("color-scheme",   "light");
        m.put("--shadow-color", "rgba(0, 0, 0, 0.2)");
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
        .spin-controls label {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 0.85rem;
            color: #5a4a32;
        }
        .spin-controls input[type="range"] {
            width: 120px;
            accent-color: #d14545;
        }
        .spin-controls button {
            padding: 6px 14px;
            border: 1px solid #d14545;
            border-radius: 4px;
            background: rgba(255, 255, 255, 0.4);
            color: #d14545;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s;
        }
        .spin-controls button:hover {
            background: rgba(209, 69, 69, 0.15);
        }
        .spin-cell:hover {
            border-color: #d14545;
            box-shadow: 0 0 16px rgba(209, 69, 69, 0.25);
        }
        .spin-cell.paused {
            opacity: 0.4;
        }
        .spin-cell svg {
            width: 70%;
            height: auto;
            filter: drop-shadow(1px 1px 2px var(--shadow-color));
        }
        """;
    }

    @Override public CssBlock<SpinningStyles.spin_title> spin_title() { return CssBlock.of("""
        font-size: 2rem;
        font-weight: 800;
        text-transform: uppercase;
        letter-spacing: 4px;
        color: #d14545;
        text-shadow: 2px 2px 0 rgba(255, 255, 255, 0.5);
        margin: 0 0 4px 0;
        """);
    }

    @Override public CssBlock<SpinningStyles.spin_hint> spin_hint() { return CssBlock.of("""
        color: #6b5a42;
        font-style: italic;
        font-size: 0.9rem;
        margin: 12px 0 16px 0;
        """);
    }

    @Override public CssBlock<SpinningStyles.spin_controls> spin_controls() { return CssBlock.of("""
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 16px;
        flex-wrap: wrap;
        """);
    }

    @Override public CssBlock<SpinningStyles.spin_grid> spin_grid() { return CssBlock.of("""
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 12px;
        max-width: 560px;
        """);
    }

    @Override public CssBlock<SpinningStyles.spin_cell> spin_cell() { return CssBlock.of("""
        display: flex;
        align-items: center;
        justify-content: center;
        background: rgba(255, 255, 255, 0.35);
        border: 2px solid #c2a366;
        border-radius: 50%;
        aspect-ratio: 1;
        cursor: pointer;
        transition: border-color 0.3s, box-shadow 0.3s;
        """);
    }

    @Override public CssBlock<SpinningStyles.paused> paused() { return CssBlock.of("opacity: 0.4;"); }
}
