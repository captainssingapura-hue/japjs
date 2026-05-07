package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Beach visual identity for {@link PlaygroundStyles}.
 * Translated 1-to-1 from the legacy {@code PlaygroundStyles.beach.css} file.
 */
public record PlaygroundStylesBeach() implements PlaygroundStyles.Impl<Beach> {

    public static final PlaygroundStylesBeach INSTANCE = new PlaygroundStylesBeach();

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
        .pg-controls label {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 0.85rem;
            color: #5a4a32;
        }
        .pg-controls input[type="range"] {
            width: 120px;
            accent-color: #e07020;
        }
        .pg-theme-btn:hover {
            border-color: #e07020;
            color: #e07020;
        }
        .pg-animal svg {
            width: 50px;
            height: auto;
            filter: drop-shadow(1px 1px 2px var(--shadow-color));
        }
        .pg-gameover h2 {
            font-size: 2.5rem;
            color: #ff4500;
            text-shadow: 0 0 20px rgba(255, 69, 0, 0.6);
            margin: 0 0 8px 0;
        }
        .pg-gameover button {
            padding: 8px 24px;
            font-size: 1rem;
            font-weight: 600;
            border: 2px solid #e07020;
            border-radius: 6px;
            background: rgba(224, 112, 32, 0.2);
            color: #fff;
            cursor: pointer;
            transition: background 0.2s;
        }
        .pg-gameover button:hover {
            background: rgba(224, 112, 32, 0.4);
        }
        """;
    }

    @Override public CssBlock<PlaygroundStyles.pg_title> pg_title() { return CssBlock.of("""
        font-size: 2rem;
        font-weight: 800;
        text-transform: uppercase;
        letter-spacing: 4px;
        color: #e07020;
        text-shadow: 2px 2px 0 rgba(255, 255, 255, 0.5);
        margin: 0 0 4px 0;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_hint> pg_hint() { return CssBlock.of("""
        color: #6b5a42;
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
        color: #e07020;
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
        color: #6b5a42;
        margin-right: 4px;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_theme_btn> pg_theme_btn() { return CssBlock.of("""
        padding: 4px 12px;
        font-size: 0.8rem;
        font-weight: 500;
        border: 1px solid #c2a366;
        border-radius: 4px;
        background: rgba(255, 255, 255, 0.6);
        color: #6b5a42;
        cursor: pointer;
        transition: background 0.15s, border-color 0.15s, color 0.15s;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_theme_btn_active> pg_theme_btn_active() { return CssBlock.of("""
        background: #e07020;
        border-color: #e07020;
        color: #fff;
        cursor: default;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_playground> pg_playground() { return CssBlock.of("""
        position: relative;
        overflow: hidden;
        background: linear-gradient(180deg, #87CEEB 0%, #a8d8ea 25%, #f5deb3 55%, #c2b280 100%);
        border: 2px solid #c2a366;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(194, 163, 102, 0.3);
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_sky> pg_sky() { return CssBlock.of("""
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1;
        background:
            radial-gradient(ellipse 90px 50px at 10% 55%, rgba(255,255,255,0.95) 0%, transparent 100%),
            radial-gradient(ellipse 70px 40px at 14% 50%, rgba(255,255,255,0.95) 0%, transparent 100%),
            radial-gradient(ellipse 110px 55px at 35% 40%, rgba(255,255,255,0.9) 0%, transparent 100%),
            radial-gradient(ellipse 80px 42px at 39% 35%, rgba(255,255,255,0.9) 0%, transparent 100%),
            radial-gradient(ellipse 70px 45px at 60% 60%, rgba(255,255,255,0.85) 0%, transparent 100%),
            radial-gradient(ellipse 55px 35px at 63% 55%, rgba(255,255,255,0.85) 0%, transparent 100%),
            radial-gradient(ellipse 85px 48px at 85% 45%, rgba(255,255,255,0.9) 0%, transparent 100%),
            radial-gradient(ellipse 60px 35px at 88% 40%, rgba(255,255,255,0.9) 0%, transparent 100%);
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
        background: #a08060;
        border-radius: 3px;
        border-top: 2px solid #c0a070;
        transition: background 0.2s, border-color 0.2s, box-shadow 0.2s;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_platform_active> pg_platform_active() { return CssBlock.of("""
        background: #c0a060;
        border-top-color: #ddc080;
        box-shadow: 0 0 10px rgba(210, 170, 80, 0.5);
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_lava> pg_lava() { return CssBlock.of("""
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: linear-gradient(0deg, #ff4500, #ff8c00, rgba(255, 140, 0, 0.3));
        z-index: 2;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_score> pg_score() { return CssBlock.of("""
        position: absolute;
        top: 8px;
        right: 12px;
        font-size: 1rem;
        font-weight: 700;
        color: #e07020;
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
