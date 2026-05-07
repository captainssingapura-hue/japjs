package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssBlock;
import hue.captains.singapura.js.homing.demo.css.PlaygroundStyles;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dracula's Castle visual identity for {@link PlaygroundStyles}.
 * Translated 1-to-1 from the legacy {@code PlaygroundStyles.dracula.css} file.
 */
public record PlaygroundStylesDracula() implements PlaygroundStyles.Impl<Dracula> {

    public static final PlaygroundStylesDracula INSTANCE = new PlaygroundStylesDracula();

    @Override public Dracula theme() { return Dracula.INSTANCE; }

    @Override public Map<String, String> cssVariables() {
        var m = new LinkedHashMap<String, String>();
        m.put("color-scheme",   "dark");
        m.put("--shadow-color", "rgba(0, 0, 0, 0.7)");
        return m;
    }

    @Override public String globalRules() { return """
        body {
            margin: 0;
            font-family: "Palatino Linotype", "Book Antiqua", Palatino, serif;
            background: linear-gradient(180deg, #0a0012 0%, #1a0a2e 40%, #2d1045 100%);
            background-attachment: fixed;
            color: #d0c0d8;
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
            color: #8a7898;
        }
        .pg-controls input[type="range"] {
            width: 120px;
            accent-color: #cc2222;
        }
        .pg-theme-btn:hover {
            border-color: #cc2222;
            color: #cc2222;
        }
        .pg-animal svg {
            width: 50px;
            height: auto;
            filter: drop-shadow(1px 1px 3px var(--shadow-color));
        }
        .pg-gameover h2 {
            font-size: 2.5rem;
            color: #cc2222;
            text-shadow: 0 0 25px rgba(200, 0, 0, 0.7);
            margin: 0 0 8px 0;
        }
        .pg-gameover button {
            padding: 8px 24px;
            font-size: 1rem;
            font-weight: 600;
            border: 2px solid #cc2222;
            border-radius: 6px;
            background: rgba(180, 0, 0, 0.2);
            color: #d0c0d8;
            cursor: pointer;
            transition: background 0.2s;
        }
        .pg-gameover button:hover {
            background: rgba(180, 0, 0, 0.4);
        }
        """;
    }

    @Override public CssBlock<PlaygroundStyles.pg_title> pg_title() { return CssBlock.of("""
        font-size: 2rem;
        font-weight: 800;
        text-transform: uppercase;
        letter-spacing: 6px;
        color: #cc2222;
        text-shadow: 0 0 12px rgba(200, 0, 0, 0.5), 2px 2px 0 #000;
        margin: 0 0 4px 0;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_hint> pg_hint() { return CssBlock.of("""
        color: #7a6888;
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
        color: #cc2222;
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
        color: #7a6888;
        margin-right: 4px;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_theme_btn> pg_theme_btn() { return CssBlock.of("""
        padding: 4px 12px;
        font-size: 0.8rem;
        font-weight: 500;
        border: 1px solid #3a1a50;
        border-radius: 4px;
        background: rgba(30, 10, 40, 0.6);
        color: #9a8aaa;
        cursor: pointer;
        transition: background 0.15s, border-color 0.15s, color 0.15s;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_theme_btn_active> pg_theme_btn_active() { return CssBlock.of("""
        background: #cc2222;
        border-color: #cc2222;
        color: #d0c0d8;
        cursor: default;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_playground> pg_playground() { return CssBlock.of("""
        position: relative;
        overflow: hidden;
        background: linear-gradient(180deg, #0d0018 0%, #18082a 30%, #251040 60%, #1a0a28 100%);
        border: 2px solid #3a1a50;
        border-radius: 8px;
        box-shadow: 0 0 40px rgba(150, 0, 0, 0.15), inset 0 0 60px rgba(0, 0, 0, 0.3);
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_sky> pg_sky() { return CssBlock.of("""
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1;
        background:
            /* Moon */
            radial-gradient(circle 30px at 82% 25%, rgba(220, 210, 180, 0.9) 0%, rgba(220, 210, 180, 0.1) 60%, transparent 100%),
            radial-gradient(circle 50px at 82% 25%, rgba(200, 190, 160, 0.12) 0%, transparent 100%),
            /* Bats */
            radial-gradient(ellipse 12px 5px at 15% 30%, rgba(20, 0, 30, 0.8) 0%, transparent 100%),
            radial-gradient(ellipse 12px 5px at 18% 28%, rgba(20, 0, 30, 0.8) 0%, transparent 100%),
            radial-gradient(ellipse 10px 4px at 72% 20%, rgba(30, 0, 40, 0.7) 0%, transparent 100%),
            radial-gradient(ellipse 10px 4px at 74% 18%, rgba(30, 0, 40, 0.7) 0%, transparent 100%),
            /* Mist at castle base */
            radial-gradient(ellipse 250px 25px at 35% 95%, rgba(80, 50, 100, 0.25) 0%, transparent 100%),
            radial-gradient(ellipse 200px 20px at 55% 92%, rgba(60, 40, 80, 0.2) 0%, transparent 100%),
            /* Castle silhouette */
            url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 120' preserveAspectRatio='xMidYMax slice'%3E%3Cdefs%3E%3ClinearGradient id='cg' x1='0' y1='0' x2='0' y2='1'%3E%3Cstop offset='0' stop-color='%23100020'/%3E%3Cstop offset='1' stop-color='%230a0012'/%3E%3C/linearGradient%3E%3C/defs%3E%3Cpath d='M0,120 L0,95 L15,95 L15,90 L20,90 L20,95 L30,95 L30,88 L35,88 L35,95 L50,95 L50,80 L55,80 L55,72 L60,72 L60,80 L70,80 L70,95 L90,95 L90,85 L95,85 L95,78 L100,78 L100,85 L108,85 L108,55 L112,55 L112,48 L115,40 L118,48 L118,55 L122,55 L122,85 L130,85 L130,65 L134,65 L134,58 L137,50 L140,42 L143,50 L146,58 L146,65 L150,65 L150,38 L153,38 L153,30 L155,22 L157,14 L159,22 L161,30 L161,38 L164,38 L164,65 L168,65 L168,58 L171,50 L174,42 L177,50 L180,58 L180,65 L184,65 L184,85 L192,85 L192,55 L196,55 L196,48 L199,40 L202,48 L202,55 L206,55 L206,85 L214,85 L214,78 L219,78 L219,85 L224,85 L224,95 L240,95 L240,82 L245,82 L245,75 L250,75 L250,82 L260,82 L260,95 L280,95 L280,90 L285,90 L285,95 L295,95 L295,88 L300,88 L300,95 L320,95 L320,92 L325,92 L325,95 L340,95 L340,90 L345,90 L345,95 L400,95 L400,120 Z' fill='url(%23cg)'/%3E%3Crect x='135' y='70' width='8' height='10' rx='4' ry='4' fill='%23352040' opacity='0.6'/%3E%3Crect x='165' y='70' width='8' height='10' rx='4' ry='4' fill='%23352040' opacity='0.6'/%3E%3Crect x='152' y='45' width='6' height='8' rx='3' ry='3' fill='%23453060' opacity='0.5'/%3E%3Crect x='113' y='60' width='5' height='7' rx='2.5' ry='2.5' fill='%23352040' opacity='0.5'/%3E%3Crect x='197' y='60' width='5' height='7' rx='2.5' ry='2.5' fill='%23352040' opacity='0.5'/%3E%3C/svg%3E");
        background-repeat: no-repeat;
        background-position: left top, left top, left top, left top, left top, left top, left top, left top, center bottom;
        background-size: auto, auto, auto, auto, auto, auto, auto, auto, 80% 90%;
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
        background: linear-gradient(180deg, #4a3a5a, #352845);
        border-radius: 2px;
        border-top: 2px solid #6a5080;
        transition: background 0.2s, border-color 0.2s, box-shadow 0.2s;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_platform_active> pg_platform_active() { return CssBlock.of("""
        background: linear-gradient(180deg, #5a4070, #443058);
        border-top-color: #8a60a0;
        box-shadow: 0 0 12px rgba(150, 80, 200, 0.4);
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_lava> pg_lava() { return CssBlock.of("""
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: linear-gradient(0deg, #8b0000, #cc1100, rgba(180, 0, 0, 0.3));
        z-index: 2;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_score> pg_score() { return CssBlock.of("""
        position: absolute;
        top: 8px;
        right: 12px;
        font-size: 1rem;
        font-weight: 700;
        color: #cc2222;
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
        background: rgba(10, 0, 15, 0.85);
        z-index: 10;
        """);
    }

    @Override public CssBlock<PlaygroundStyles.pg_final_score> pg_final_score() { return CssBlock.of("""
        font-size: 1.2rem;
        color: #d0c0d8;
        margin: 0 0 16px 0;
        """);
    }
}
