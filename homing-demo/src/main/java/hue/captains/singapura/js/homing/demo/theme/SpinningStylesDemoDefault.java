package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.demo.css.SpinningStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default visual identity for {@link SpinningStyles}.
 * Translated 1-to-1 from the legacy {@code SpinningStyles.css} file.
 */
public record SpinningStylesDemoDefault() implements SpinningStyles.Impl<DemoDefault> {

    public static final SpinningStylesDemoDefault INSTANCE = new SpinningStylesDemoDefault();

    @Override public DemoDefault theme() { return DemoDefault.INSTANCE; }

    @Override public Map<String, String> cssVariables() {
        var m = new LinkedHashMap<String, String>();
        m.put("color-scheme",   "light dark");
        m.put("--shadow-color", "light-dark(rgba(0, 0, 0, 0.15), rgba(0, 0, 0, 0.5))");
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
        .spin-controls label {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 0.85rem;
            color: light-dark(#555, #aaa);
        }
        .spin-controls input[type="range"] {
            width: 120px;
            accent-color: #e040fb;
        }
        .spin-controls button {
            padding: 6px 14px;
            border: 1px solid light-dark(#c020d8, #e040fb);
            border-radius: 4px;
            background: transparent;
            color: light-dark(#c020d8, #e040fb);
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s;
        }
        .spin-controls button:hover {
            background: light-dark(rgba(192, 32, 216, 0.1), rgba(224, 64, 251, 0.15));
        }
        .spin-cell:hover {
            border-color: light-dark(#c020d8, #e040fb);
            box-shadow: 0 0 16px light-dark(rgba(192, 32, 216, 0.15), rgba(224, 64, 251, 0.3));
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
        color: light-dark(#c020d8, #e040fb);
        text-shadow: 2px 2px 0 light-dark(rgba(0, 0, 0, 0.1), #000);
        margin: 0 0 4px 0;
        """);
    }

    @Override public CssBlock<SpinningStyles.spin_hint> spin_hint() { return CssBlock.of("""
        color: light-dark(#666, #888);
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
        background: light-dark(#eef1f5, #16213e);
        border: 2px solid light-dark(#ccc, #333);
        border-radius: 50%;
        aspect-ratio: 1;
        cursor: pointer;
        transition: border-color 0.3s, box-shadow 0.3s;
        """);
    }

    @Override public CssBlock<SpinningStyles.paused> paused() { return CssBlock.of("opacity: 0.4;"); }
}
