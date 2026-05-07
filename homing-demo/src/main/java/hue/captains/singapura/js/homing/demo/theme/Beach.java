package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;

import java.util.Map;

public record Beach() implements Theme {

    public static final Beach INSTANCE = new Beach();

    @Override public String slug()  { return "beach"; }
    @Override public String label() { return "Beach"; }

    public record Vars() implements ThemeVariables<Beach> {
        public static final Vars INSTANCE = new Vars();
        @Override public Beach theme() { return Beach.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }

        private static final Map<CssVar, String> VALUES = Map.ofEntries(
                Map.entry(DemoVars.SHADOW_COLOR, "rgba(0, 0, 0, 0.2)"),
                Map.entry(DemoVars.CELL_SHEEN,   "rgba(255, 255, 255, 0.15)"),

                // PlaygroundStyles - Beach
                Map.entry(DemoVars.PG_TITLE_COLOR,       "#e07020"),
                Map.entry(DemoVars.PG_TITLE_TEXT_SHADOW, "2px 2px 0 rgba(255, 255, 255, 0.5)"),
                Map.entry(DemoVars.PG_TITLE_LETTER_SPACING, "4px"),
                Map.entry(DemoVars.PG_HINT_COLOR,        "#6b5a42"),
                Map.entry(DemoVars.PG_SIZE_COLOR,        "#e07020"),
                Map.entry(DemoVars.PG_THEME_LABEL_COLOR, "#6b5a42"),
                Map.entry(DemoVars.PG_THEME_BTN_BORDER,  "1px solid #c2a366"),
                Map.entry(DemoVars.PG_THEME_BTN_BG,      "rgba(255, 255, 255, 0.6)"),
                Map.entry(DemoVars.PG_THEME_BTN_COLOR,   "#6b5a42"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BG,     "#e07020"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BORDER, "#e07020"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_COLOR,  "#fff"),
                Map.entry(DemoVars.PG_PLAYGROUND_BG,
                        "linear-gradient(180deg, #87CEEB 0%, #a8d8ea 25%, #f5deb3 55%, #c2b280 100%)"),
                Map.entry(DemoVars.PG_PLAYGROUND_BORDER, "2px solid #c2a366"),
                Map.entry(DemoVars.PG_PLAYGROUND_SHADOW, "0 4px 20px rgba(194, 163, 102, 0.3)"),
                Map.entry(DemoVars.PG_SKY_BG,
                        "radial-gradient(ellipse 90px 50px at 10% 55%, rgba(255,255,255,0.95) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 70px 40px at 14% 50%, rgba(255,255,255,0.95) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 110px 55px at 35% 40%, rgba(255,255,255,0.9) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 80px 42px at 39% 35%, rgba(255,255,255,0.9) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 70px 45px at 60% 60%, rgba(255,255,255,0.85) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 55px 35px at 63% 55%, rgba(255,255,255,0.85) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 85px 48px at 85% 45%, rgba(255,255,255,0.9) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 60px 35px at 88% 40%, rgba(255,255,255,0.9) 0%, transparent 100%)"),
                Map.entry(DemoVars.PG_SKY_BG_REPEAT,   "no-repeat"),
                Map.entry(DemoVars.PG_SKY_BG_POSITION, "left top"),
                Map.entry(DemoVars.PG_SKY_BG_SIZE,     "auto"),
                Map.entry(DemoVars.PG_PLATFORM_BG,           "#a08060"),
                Map.entry(DemoVars.PG_PLATFORM_RADIUS,       "3px"),
                Map.entry(DemoVars.PG_PLATFORM_BORDER_TOP,   "2px solid #c0a070"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BG,     "#c0a060"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BORDER, "#ddc080"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_SHADOW, "0 0 10px rgba(210, 170, 80, 0.5)"),
                Map.entry(DemoVars.PG_LAVA_BG,
                        "linear-gradient(0deg, #ff4500, #ff8c00, rgba(255, 140, 0, 0.3))"),
                Map.entry(DemoVars.PG_SCORE_COLOR,        "#e07020"),
                Map.entry(DemoVars.PG_GAMEOVER_BG,        "rgba(0, 0, 0, 0.7)"),
                Map.entry(DemoVars.PG_FINAL_SCORE_COLOR,  "#fff"),

                // SpinningStyles - Beach
                Map.entry(DemoVars.SPIN_TITLE_COLOR,       "#d14545"),
                Map.entry(DemoVars.SPIN_TITLE_TEXT_SHADOW, "2px 2px 0 rgba(255, 255, 255, 0.5)"),
                Map.entry(DemoVars.SPIN_HINT_COLOR,        "#6b5a42"),
                Map.entry(DemoVars.SPIN_CELL_BG,           "rgba(255, 255, 255, 0.35)"),
                Map.entry(DemoVars.SPIN_CELL_BORDER,       "2px solid #c2a366"),

                // SubwayStyles - Beach
                Map.entry(DemoVars.SUBWAY_TITLE_COLOR,       "#e07020"),
                Map.entry(DemoVars.SUBWAY_TITLE_TEXT_SHADOW, "2px 2px 0 rgba(255, 255, 255, 0.5)"),
                Map.entry(DemoVars.SUBWAY_HINT_COLOR,        "#6b5a42"),
                Map.entry(DemoVars.SUBWAY_GRID_BG,           "rgba(255, 255, 255, 0.3)"),
                Map.entry(DemoVars.SUBWAY_GRID_BORDER,       "2px solid #c2a366"),
                Map.entry(DemoVars.SUBWAY_GRID_SHADOW,       "0 4px 20px rgba(194, 163, 102, 0.3)")
        );
    }

    public record Globals() implements ThemeGlobals<Beach> {
        public static final Globals INSTANCE = new Globals();
        @Override public Beach theme() { return Beach.INSTANCE; }
        @Override public String css() { return CSS; }

        private static final String CSS = """
                :root { color-scheme: light; }
                body {
                    margin: 0;
                    font-family: "Helvetica Neue", Arial, sans-serif;
                    background: linear-gradient(180deg, #87CEEB 0%, #f5deb3 60%, #c2a366 100%);
                    background-attachment: fixed;
                    color: #3b2e1a;
                    min-height: 100vh;
                }
                #app { padding: 24px 32px; }
                .pg-controls label {
                    display: flex; align-items: center; gap: 6px;
                    font-size: 0.85rem; color: #5a4a32;
                }
                .pg-controls input[type="range"] { width: 120px; accent-color: #e07020; }
                .pg-theme-btn:hover { border-color: #e07020; color: #e07020; }
                .pg-animal svg {
                    width: 50px; height: auto;
                    filter: drop-shadow(1px 1px 2px var(--shadow-color));
                }
                .pg-gameover h2 {
                    font-size: 2.5rem; color: #ff4500;
                    text-shadow: 0 0 20px rgba(255, 69, 0, 0.6);
                    margin: 0 0 8px 0;
                }
                .pg-gameover button {
                    padding: 8px 24px; font-size: 1rem; font-weight: 600;
                    border: 2px solid #e07020; border-radius: 6px;
                    background: rgba(224, 112, 32, 0.2); color: #fff;
                    cursor: pointer; transition: background 0.2s;
                }
                .pg-gameover button:hover { background: rgba(224, 112, 32, 0.4); }
                .spin-controls label {
                    display: flex; align-items: center; gap: 6px;
                    font-size: 0.85rem; color: #5a4a32;
                }
                .spin-controls input[type="range"] { width: 120px; accent-color: #d14545; }
                .spin-controls button {
                    padding: 6px 14px; border: 1px solid #d14545; border-radius: 4px;
                    background: rgba(255, 255, 255, 0.4); color: #d14545;
                    font-weight: 600; cursor: pointer; transition: background 0.2s;
                }
                .spin-controls button:hover { background: rgba(209, 69, 69, 0.15); }
                .spin-cell:hover { border-color: #d14545; box-shadow: 0 0 16px rgba(209, 69, 69, 0.25); }
                .spin-cell.paused { opacity: 0.4; }
                .spin-cell svg {
                    width: 70%; height: auto;
                    filter: drop-shadow(1px 1px 2px var(--shadow-color));
                }
                .subway-title::after {
                    content: ""; display: block; width: 100%; height: 4px; margin-top: 8px;
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
                    width: 100%; height: auto;
                    filter: drop-shadow(1px 1px 2px var(--shadow-color));
                }
                """;
    }
}
