package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default ("light/dark adaptive") visual identity for {@link PlaygroundStyles}.
 * Translated 1-to-1 from the legacy {@code PlaygroundStyles.css} file.
 */
public record PlaygroundStylesDemoDefault() implements PlaygroundStyles.Impl<DemoDefault> {

    public static final PlaygroundStylesDemoDefault INSTANCE = new PlaygroundStylesDemoDefault();

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
        .pg-controls label {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 0.85rem;
            color: light-dark(#555, #aaa);
        }
        .pg-controls input[type="range"] {
            width: 120px;
            accent-color: #00b4d8;
        }
        .pg-theme-btn:hover {
            border-color: light-dark(#0090b0, #00b4d8);
            color: light-dark(#0090b0, #00b4d8);
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
            border: 2px solid #ff6a00;
            border-radius: 6px;
            background: rgba(255, 69, 0, 0.2);
            color: #fff;
            cursor: pointer;
            transition: background 0.2s;
        }
        .pg-gameover button:hover {
            background: rgba(255, 69, 0, 0.4);
        }
        """;
    }

    @Override public CssBlock<PlaygroundStyles.pg_title> pg_title() { return CssBlock.of("""
        font-size: 2rem;
        font-weight: 800;
        text-transform: uppercase;
        letter-spacing: 4px;
        color: light-dark(#0090b0, #00b4d8);
        text-shadow: 2px 2px 0 light-dark(rgba(0, 0, 0, 0.1), #000);
        margin: 0 0 4px 0;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_hint> pg_hint() { return CssBlock.of("""
        color: light-dark(#666, #888);
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
        color: light-dark(#0090b0, #00b4d8);
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
        color: light-dark(#555, #aaa);
        margin-right: 4px;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_theme_btn> pg_theme_btn() { return CssBlock.of("""
        padding: 4px 12px;
        font-size: 0.8rem;
        font-weight: 500;
        border: 1px solid light-dark(#ccc, #444);
        border-radius: 4px;
        background: light-dark(#fff, #2a2a3e);
        color: light-dark(#444, #bbb);
        cursor: pointer;
        transition: background 0.15s, border-color 0.15s, color 0.15s;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_theme_btn_active> pg_theme_btn_active() { return CssBlock.of("""
        background: light-dark(#0090b0, #00b4d8);
        border-color: light-dark(#0090b0, #00b4d8);
        color: #fff;
        cursor: default;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_playground> pg_playground() { return CssBlock.of("""
        position: relative;
        overflow: hidden;
        background: light-dark(#eef1f5, #16213e);
        border: 2px solid light-dark(#ccc, #333);
        border-radius: 8px;
        box-shadow: 0 0 30px light-dark(rgba(0, 180, 216, 0.08), rgba(0, 180, 216, 0.15));
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_sky> pg_sky() { return CssBlock.of("""
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1;
        background:
            radial-gradient(ellipse 80px 50px at 12% 60%, light-dark(rgba(255,255,255,0.9), rgba(80,90,120,0.4)) 0%, transparent 100%),
            radial-gradient(ellipse 60px 36px at 16% 55%, light-dark(rgba(255,255,255,0.9), rgba(80,90,120,0.4)) 0%, transparent 100%),
            radial-gradient(ellipse 100px 55px at 38% 45%, light-dark(rgba(255,255,255,0.85), rgba(70,80,110,0.35)) 0%, transparent 100%),
            radial-gradient(ellipse 70px 40px at 42% 40%, light-dark(rgba(255,255,255,0.85), rgba(70,80,110,0.35)) 0%, transparent 100%),
            radial-gradient(ellipse 90px 48px at 65% 55%, light-dark(rgba(255,255,255,0.8), rgba(60,70,100,0.3)) 0%, transparent 100%),
            radial-gradient(ellipse 65px 38px at 70% 50%, light-dark(rgba(255,255,255,0.8), rgba(60,70,100,0.3)) 0%, transparent 100%),
            radial-gradient(ellipse 75px 42px at 88% 48%, light-dark(rgba(255,255,255,0.85), rgba(75,85,115,0.35)) 0%, transparent 100%),
            radial-gradient(ellipse 55px 34px at 91% 43%, light-dark(rgba(255,255,255,0.85), rgba(75,85,115,0.35)) 0%, transparent 100%);
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
        background: light-dark(#6b8f6b, #3a5f3a);
        border-radius: 3px;
        border-top: 2px solid light-dark(#8ab88a, #5a8f5a);
        transition: background 0.2s, border-color 0.2s, box-shadow 0.2s;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_platform_active> pg_platform_active() { return CssBlock.of("""
        background: light-dark(#88bb88, #509050);
        border-top-color: light-dark(#a0d8a0, #70b870);
        box-shadow: 0 0 10px light-dark(rgba(100, 200, 100, 0.4), rgba(80, 180, 80, 0.5));
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_lava> pg_lava() { return CssBlock.of("""
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: linear-gradient(0deg,
            light-dark(#ff4500, #cc3700),
            light-dark(#ff6a00, #e05500),
            light-dark(rgba(255, 106, 0, 0.3), rgba(204, 55, 0, 0.3)));
        z-index: 2;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_score> pg_score() { return CssBlock.of("""
        position: absolute;
        top: 8px;
        right: 12px;
        font-size: 1rem;
        font-weight: 700;
        color: light-dark(#0090b0, #00b4d8);
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
