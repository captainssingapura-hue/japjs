package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;

import java.util.Map;

public record Alpine() implements Theme {

    public static final Alpine INSTANCE = new Alpine();

    @Override public String slug()  { return "alpine"; }
    @Override public String label() { return "Alpine"; }

    public record Vars() implements ThemeVariables<Alpine> {
        public static final Vars INSTANCE = new Vars();
        @Override public Alpine theme() { return Alpine.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }

        private static final Map<CssVar, String> VALUES = Map.ofEntries(
                Map.entry(DemoVars.SHADOW_COLOR, "rgba(0, 0, 0, 0.25)"),

                Map.entry(DemoVars.PG_TITLE_COLOR,       "#2e5e4e"),
                Map.entry(DemoVars.PG_TITLE_TEXT_SHADOW, "2px 2px 0 rgba(255, 255, 255, 0.5)"),
                Map.entry(DemoVars.PG_TITLE_LETTER_SPACING, "4px"),
                Map.entry(DemoVars.PG_HINT_COLOR,        "#3a6a3a"),
                Map.entry(DemoVars.PG_SIZE_COLOR,        "#3a7a3a"),
                Map.entry(DemoVars.PG_THEME_LABEL_COLOR, "#2d5a27"),
                Map.entry(DemoVars.PG_THEME_BTN_BORDER,  "1px solid #6a9a6a"),
                Map.entry(DemoVars.PG_THEME_BTN_BG,      "rgba(255, 255, 255, 0.6)"),
                Map.entry(DemoVars.PG_THEME_BTN_COLOR,   "#2d5a27"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BG,     "#3a7a3a"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BORDER, "#3a7a3a"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_COLOR,  "#fff"),
                Map.entry(DemoVars.PG_PLAYGROUND_BG,
                        "linear-gradient(180deg, #a8cce0 0%, #c8dce8 15%, #e0ece0 35%, #5a9a4a 60%, #2d5a27 100%)"),
                Map.entry(DemoVars.PG_PLAYGROUND_BORDER, "2px solid #4a7a4a"),
                Map.entry(DemoVars.PG_PLAYGROUND_SHADOW, "0 4px 20px rgba(45, 90, 39, 0.3)"),
                Map.entry(DemoVars.PG_SKY_BG,
                        "linear-gradient(135deg, transparent 30%, #e8e8f0 32%, #f0f0f8 35%, #e0e4ea 40%, transparent 42%),"
                      + "linear-gradient(150deg, transparent 15%, #d8dce4 17%, #eaecf0 22%, #f5f5fa 25%, #dce0e8 30%, transparent 33%),"
                      + "linear-gradient(125deg, transparent 50%, #e0e4ea 52%, #f0f0f5 56%, #e8ecf0 60%, transparent 63%),"
                      + "linear-gradient(140deg, transparent 65%, #d0d8e0 67%, #e8ecf0 70%, #dce0e8 73%, transparent 75%),"
                      + "linear-gradient(135deg, transparent 35%, #8a9aaa 37%, #7a8a9a 42%, transparent 45%),"
                      + "linear-gradient(150deg, transparent 25%, #7a8a98 27%, #6a7a88 33%, transparent 36%),"
                      + "linear-gradient(125deg, transparent 55%, #8090a0 57%, #708090 63%, transparent 66%),"
                      + "linear-gradient(140deg, transparent 68%, #7a8898 70%, #6a7888 75%, transparent 78%),"
                      + "radial-gradient(ellipse 90px 30px at 20% 30%, rgba(255,255,255,0.7) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 70px 25px at 55% 20%, rgba(255,255,255,0.6) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 80px 28px at 80% 35%, rgba(255,255,255,0.65) 0%, transparent 100%)"),
                Map.entry(DemoVars.PG_SKY_BG_REPEAT,   "no-repeat"),
                Map.entry(DemoVars.PG_SKY_BG_POSITION, "left top"),
                Map.entry(DemoVars.PG_SKY_BG_SIZE,     "auto"),
                Map.entry(DemoVars.PG_PLATFORM_BG,           "linear-gradient(180deg, #5a8a3a 0%, #4a7a2a 100%)"),
                Map.entry(DemoVars.PG_PLATFORM_RADIUS,       "3px"),
                Map.entry(DemoVars.PG_PLATFORM_BORDER_TOP,   "2px solid #6a9a4a"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BG,     "linear-gradient(180deg, #6aaa4a 0%, #5a9a3a 100%)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BORDER, "#7aba5a"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_SHADOW, "0 0 10px rgba(90, 160, 60, 0.5)"),
                Map.entry(DemoVars.PG_LAVA_BG,
                        "linear-gradient(0deg, #1a4a1a, #2d6a20, rgba(45, 106, 32, 0.3))"),
                Map.entry(DemoVars.PG_SCORE_COLOR,        "#2e5e4e"),
                Map.entry(DemoVars.PG_GAMEOVER_BG,        "rgba(0, 0, 0, 0.7)"),
                Map.entry(DemoVars.PG_FINAL_SCORE_COLOR,  "#fff")
        );
    }

    public record Globals() implements ThemeGlobals<Alpine> {
        public static final Globals INSTANCE = new Globals();
        @Override public Alpine theme() { return Alpine.INSTANCE; }
        @Override public String css() { return CSS; }

        private static final String CSS = """
                :root { color-scheme: light; }
                body {
                    margin: 0;
                    font-family: "Helvetica Neue", Arial, sans-serif;
                    background: linear-gradient(180deg, #a8cce0 0%, #d4e8d0 40%, #2d5a27 100%);
                    background-attachment: fixed;
                    color: #1a3a1a;
                    min-height: 100vh;
                }
                #app { padding: 24px 32px; }
                .pg-controls label {
                    display: flex; align-items: center; gap: 6px;
                    font-size: 0.85rem; color: #2d5a27;
                }
                .pg-controls input[type="range"] { width: 120px; accent-color: #3a7a3a; }
                .pg-theme-btn:hover { border-color: #3a7a3a; color: #3a7a3a; }
                .pg-animal svg {
                    width: 50px; height: auto;
                    filter: drop-shadow(1px 1px 2px var(--shadow-color));
                }
                .pg-gameover h2 {
                    font-size: 2.5rem; color: #7aba5a;
                    text-shadow: 0 0 20px rgba(90, 160, 60, 0.6);
                    margin: 0 0 8px 0;
                }
                .pg-gameover button {
                    padding: 8px 24px; font-size: 1rem; font-weight: 600;
                    border: 2px solid #3a7a3a; border-radius: 6px;
                    background: rgba(58, 122, 58, 0.2); color: #fff;
                    cursor: pointer; transition: background 0.2s;
                }
                .pg-gameover button:hover { background: rgba(58, 122, 58, 0.4); }
                """;
    }
}
