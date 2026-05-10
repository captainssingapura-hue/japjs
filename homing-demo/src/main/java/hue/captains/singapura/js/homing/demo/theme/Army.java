package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;

import java.util.Map;

/**
 * Army theme — the moving-animal demo's playground becomes a forward
 * operating base with tank/armoured-truck/Humvee "platforms".
 */
public record Army() implements Theme {

    public static final Army INSTANCE = new Army();

    @Override public String slug()  { return "army"; }
    @Override public String label() { return "Army"; }

    public record Vars() implements ThemeVariables<Army> {
        public static final Vars INSTANCE = new Vars();
        @Override public Army theme() { return Army.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }

        private static final Map<CssVar, String> VALUES = Map.ofEntries(
                Map.entry(DemoVars.SHADOW_COLOR, "rgba(20, 30, 10, 0.40)"),
                Map.entry(DemoVars.CELL_SHEEN,   "rgba(170, 180, 130, 0.08)"),

                // PlaygroundStyles — desert / forward operating base
                Map.entry(DemoVars.PG_TITLE_COLOR,           "#3a4a18"),
                Map.entry(DemoVars.PG_TITLE_TEXT_SHADOW,     "2px 2px 0 rgba(255, 245, 200, 0.45)"),
                Map.entry(DemoVars.PG_TITLE_LETTER_SPACING,  "4px"),
                Map.entry(DemoVars.PG_HINT_COLOR,            "#5a6a30"),
                Map.entry(DemoVars.PG_SIZE_COLOR,            "#3a4a18"),
                Map.entry(DemoVars.PG_THEME_LABEL_COLOR,     "#3a4a18"),
                Map.entry(DemoVars.PG_THEME_BTN_BORDER,      "1px solid #8a9a5a"),
                Map.entry(DemoVars.PG_THEME_BTN_BG,          "rgba(255, 250, 220, 0.6)"),
                Map.entry(DemoVars.PG_THEME_BTN_COLOR,       "#3a4a18"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BG,     "#3a4a18"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BORDER, "#3a4a18"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_COLOR,  "#fff"),
                Map.entry(DemoVars.PG_PLAYGROUND_BG,
                        "linear-gradient(180deg, #c4b888 0%, #b0a070 30%, #8a7a48 65%, #4a3e1c 100%)"),
                Map.entry(DemoVars.PG_PLAYGROUND_BORDER,     "2px solid #6a5a28"),
                Map.entry(DemoVars.PG_PLAYGROUND_SHADOW,     "0 4px 20px rgba(74, 62, 28, 0.4)"),
                Map.entry(DemoVars.PG_SKY_BG,
                        "radial-gradient(ellipse 110px 35px at 14% 60%, rgba(255,245,210,0.7) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 80px 28px at 18% 55%, rgba(255,240,200,0.6) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 120px 40px at 48% 65%, rgba(255,245,210,0.65) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 95px 32px at 75% 58%, rgba(255,240,200,0.6) 0%, transparent 100%)"),
                Map.entry(DemoVars.PG_SKY_BG_REPEAT,         "no-repeat"),
                Map.entry(DemoVars.PG_SKY_BG_POSITION,       "left top"),
                Map.entry(DemoVars.PG_SKY_BG_SIZE,           "auto"),
                // Platform = packed dirt / track
                Map.entry(DemoVars.PG_PLATFORM_BG,           "linear-gradient(180deg, #6a5a30 0%, #4a3a18 100%)"),
                Map.entry(DemoVars.PG_PLATFORM_RADIUS,       "2px"),
                Map.entry(DemoVars.PG_PLATFORM_BORDER_TOP,   "2px solid #8a7a48"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BG,    "linear-gradient(180deg, #8a7a48 0%, #6a5a30 100%)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BORDER, "#c8b070"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_SHADOW, "0 0 12px rgba(200, 176, 112, 0.55)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_GLOW,   "rgba(255, 220, 130, 0.85)"),
                Map.entry(DemoVars.PG_VEHICLE_1_BG,          MilitarySvgs.ARMY_TANK),
                Map.entry(DemoVars.PG_VEHICLE_2_BG,          MilitarySvgs.ARMY_TRUCK),
                Map.entry(DemoVars.PG_VEHICLE_3_BG,          MilitarySvgs.ARMY_HUMVEE),
                Map.entry(DemoVars.PG_LAVA_BG,
                        "linear-gradient(0deg, #2a1808, #4a3018, rgba(74, 48, 24, 0.4))"),
                Map.entry(DemoVars.PG_SCORE_COLOR,           "#3a4a18"),
                Map.entry(DemoVars.PG_GAMEOVER_BG,           "rgba(0, 0, 0, 0.7)"),
                Map.entry(DemoVars.PG_FINAL_SCORE_COLOR,     "#fff"),

                Map.entry(DemoVars.SPIN_TITLE_COLOR,         "#3a4a18"),
                Map.entry(DemoVars.SPIN_TITLE_TEXT_SHADOW,   "2px 2px 0 rgba(255, 245, 200, 0.45)"),
                Map.entry(DemoVars.SPIN_HINT_COLOR,          "#5a6a30"),
                Map.entry(DemoVars.SPIN_CELL_BG,             "rgba(255, 250, 220, 0.35)"),
                Map.entry(DemoVars.SPIN_CELL_BORDER,         "2px solid #8a9a5a"),

                Map.entry(DemoVars.SUBWAY_TITLE_COLOR,       "#3a4a18"),
                Map.entry(DemoVars.SUBWAY_TITLE_TEXT_SHADOW, "2px 2px 0 rgba(255, 245, 200, 0.45)"),
                Map.entry(DemoVars.SUBWAY_HINT_COLOR,        "#5a6a30"),
                Map.entry(DemoVars.SUBWAY_GRID_BG,           "rgba(255, 250, 220, 0.30)"),
                Map.entry(DemoVars.SUBWAY_GRID_BORDER,       "2px solid #8a9a5a"),
                Map.entry(DemoVars.SUBWAY_GRID_SHADOW,       "0 4px 20px rgba(74, 62, 28, 0.3)")
        );
    }

    public record Globals() implements ThemeGlobals<Army> {
        public static final Globals INSTANCE = new Globals();
        @Override public Army theme() { return Army.INSTANCE; }
        @Override public String css() { return CSS; }

        private static final String CSS = """
                :root { color-scheme: light; }
                body {
                    margin: 0;
                    font-family: "Helvetica Neue", Arial, sans-serif;
                    background: linear-gradient(180deg, #c4b888 0%, #a89868 50%, #5a4a20 100%);
                    background-attachment: fixed;
                    color: #2a200a;
                    min-height: 100vh;
                }
                #app { padding: 24px 32px; }
                .pg-controls label { display: flex; align-items: center; gap: 6px; font-size: 0.85rem; color: #3a4a18; }
                .pg-controls input[type="range"] { width: 120px; accent-color: #3a4a18; }
                .pg-theme-btn:hover { border-color: #3a4a18; color: #3a4a18; }
                .pg-animal svg {
                    width: 50px; height: auto;
                    filter: drop-shadow(1px 1px 2px var(--shadow-color));
                }
                .pg-gameover h2 {
                    font-size: 2.5rem; color: #ffd700;
                    text-shadow: 0 0 20px rgba(255, 215, 0, 0.6);
                    margin: 0 0 8px 0;
                }
                .pg-gameover button {
                    padding: 8px 24px; font-size: 1rem; font-weight: 600;
                    border: 2px solid #c8b070; border-radius: 6px;
                    background: rgba(200, 176, 112, 0.25); color: #fff;
                    cursor: pointer; transition: background 0.2s;
                }
                .pg-gameover button:hover { background: rgba(200, 176, 112, 0.45); }
                """;
    }
}
