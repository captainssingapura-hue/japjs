package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Alpine Mountain visual identity for {@link PlaygroundStyles}.
 * Translated 1-to-1 from the legacy {@code PlaygroundStyles.alpine.css} file.
 */
public record PlaygroundStylesAlpine() implements PlaygroundStyles.Impl<Alpine> {

    public static final PlaygroundStylesAlpine INSTANCE = new PlaygroundStylesAlpine();

    @Override public Alpine theme() { return Alpine.INSTANCE; }

    @Override public Map<String, String> cssVariables() {
        var m = new LinkedHashMap<String, String>();
        m.put("color-scheme",   "light");
        m.put("--shadow-color", "rgba(0, 0, 0, 0.25)");
        return m;
    }

    @Override public String globalRules() { return """
        body {
            margin: 0;
            font-family: "Helvetica Neue", Arial, sans-serif;
            background: linear-gradient(180deg, #a8cce0 0%, #d4e8d0 40%, #2d5a27 100%);
            background-attachment: fixed;
            color: #1a3a1a;
            min-height: 100vh;
        }
        #app {
            padding: 24px 32px;
        }
        .pg-controls label {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 0.85rem;
            color: #2d5a27;
        }
        .pg-controls input[type="range"] {
            width: 120px;
            accent-color: #3a7a3a;
        }
        .pg-theme-btn:hover {
            border-color: #3a7a3a;
            color: #3a7a3a;
        }
        .pg-animal svg {
            width: 50px;
            height: auto;
            filter: drop-shadow(1px 1px 2px var(--shadow-color));
        }
        .pg-gameover h2 {
            font-size: 2.5rem;
            color: #7aba5a;
            text-shadow: 0 0 20px rgba(90, 160, 60, 0.6);
            margin: 0 0 8px 0;
        }
        .pg-gameover button {
            padding: 8px 24px;
            font-size: 1rem;
            font-weight: 600;
            border: 2px solid #3a7a3a;
            border-radius: 6px;
            background: rgba(58, 122, 58, 0.2);
            color: #fff;
            cursor: pointer;
            transition: background 0.2s;
        }
        .pg-gameover button:hover {
            background: rgba(58, 122, 58, 0.4);
        }
        """;
    }

    @Override public CssBlock<PlaygroundStyles.pg_title> pg_title() { return CssBlock.of("""
        font-size: 2rem;
        font-weight: 800;
        text-transform: uppercase;
        letter-spacing: 4px;
        color: #2e5e4e;
        text-shadow: 2px 2px 0 rgba(255, 255, 255, 0.5);
        margin: 0 0 4px 0;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_hint> pg_hint() { return CssBlock.of("""
        color: #3a6a3a;
        font-style: italic;
        font-size: 0.9rem;
        margin: 12px 0 16px 0;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_controls> pg_controls() { return CssBlock.of("""
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 16px;
        flex-wrap: wrap;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_size_display> pg_size_display() { return CssBlock.of("""
        font-size: 0.85rem;
        color: #3a7a3a;
        font-weight: 600;
        min-width: 80px;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_theme_switcher> pg_theme_switcher() { return CssBlock.of("""
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 16px;
        flex-wrap: wrap;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_theme_label> pg_theme_label() { return CssBlock.of("""
        font-size: 0.85rem;
        font-weight: 600;
        color: #2d5a27;
        margin-right: 4px;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_theme_btn> pg_theme_btn() { return CssBlock.of("""
        padding: 4px 12px;
        font-size: 0.8rem;
        font-weight: 500;
        border: 1px solid #6a9a6a;
        border-radius: 4px;
        background: rgba(255, 255, 255, 0.6);
        color: #2d5a27;
        cursor: pointer;
        transition: background 0.15s, border-color 0.15s, color 0.15s;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_theme_btn_active> pg_theme_btn_active() { return CssBlock.of("""
        background: #3a7a3a;
        border-color: #3a7a3a;
        color: #fff;
        cursor: default;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_playground> pg_playground() { return CssBlock.of("""
        position: relative;
        overflow: hidden;
        background: linear-gradient(180deg, #a8cce0 0%, #c8dce8 15%, #e0ece0 35%, #5a9a4a 60%, #2d5a27 100%);
        border: 2px solid #4a7a4a;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(45, 90, 39, 0.3);
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_sky> pg_sky() { return CssBlock.of("""
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1;
        background:
            /* Snow-capped mountain peaks */
            linear-gradient(135deg, transparent 30%, #e8e8f0 32%, #f0f0f8 35%, #e0e4ea 40%, transparent 42%),
            linear-gradient(150deg, transparent 15%, #d8dce4 17%, #eaecf0 22%, #f5f5fa 25%, #dce0e8 30%, transparent 33%),
            linear-gradient(125deg, transparent 50%, #e0e4ea 52%, #f0f0f5 56%, #e8ecf0 60%, transparent 63%),
            linear-gradient(140deg, transparent 65%, #d0d8e0 67%, #e8ecf0 70%, #dce0e8 73%, transparent 75%),
            /* Rocky mountain body */
            linear-gradient(135deg, transparent 35%, #8a9aaa 37%, #7a8a9a 42%, transparent 45%),
            linear-gradient(150deg, transparent 25%, #7a8a98 27%, #6a7a88 33%, transparent 36%),
            linear-gradient(125deg, transparent 55%, #8090a0 57%, #708090 63%, transparent 66%),
            linear-gradient(140deg, transparent 68%, #7a8898 70%, #6a7888 75%, transparent 78%),
            /* Wispy clouds */
            radial-gradient(ellipse 90px 30px at 20% 30%, rgba(255,255,255,0.7) 0%, transparent 100%),
            radial-gradient(ellipse 70px 25px at 55% 20%, rgba(255,255,255,0.6) 0%, transparent 100%),
            radial-gradient(ellipse 80px 28px at 80% 35%, rgba(255,255,255,0.65) 0%, transparent 100%);
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_world> pg_world() { return CssBlock.of("""
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_animal> pg_animal() { return CssBlock.of("""
        position: absolute;
        transform-origin: center;
        will-change: transform, left, top;
        z-index: 5;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_platform> pg_platform() { return CssBlock.of("""
        position: absolute;
        background: linear-gradient(180deg, #5a8a3a 0%, #4a7a2a 100%);
        border-radius: 3px;
        border-top: 2px solid #6a9a4a;
        transition: background 0.2s, border-color 0.2s, box-shadow 0.2s;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_platform_active> pg_platform_active() { return CssBlock.of("""
        background: linear-gradient(180deg, #6aaa4a 0%, #5a9a3a 100%);
        border-top-color: #7aba5a;
        box-shadow: 0 0 10px rgba(90, 160, 60, 0.5);
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_lava> pg_lava() { return CssBlock.of("""
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: linear-gradient(0deg, #1a4a1a, #2d6a20, rgba(45, 106, 32, 0.3));
        z-index: 2;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_score> pg_score() { return CssBlock.of("""
        position: absolute;
        top: 8px;
        right: 12px;
        font-size: 1rem;
        font-weight: 700;
        color: #2e5e4e;
        z-index: 3;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_gameover> pg_gameover() { return CssBlock.of("""
        position: absolute;
        inset: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background: rgba(0, 0, 0, 0.7);
        z-index: 10;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_final_score> pg_final_score() { return CssBlock.of("""
        font-size: 1.2rem;
        color: #fff;
        margin: 0 0 16px 0;
        """);
    }
}
