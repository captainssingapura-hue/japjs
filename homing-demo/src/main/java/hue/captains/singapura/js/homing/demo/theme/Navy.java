package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;

import java.util.Map;

/**
 * Navy theme — the moving-animal demo's playground becomes an open ocean
 * with destroyer/submarine/aircraft-carrier "platforms".
 */
public record Navy() implements Theme {

    public static final Navy INSTANCE = new Navy();

    @Override public String slug()  { return "navy"; }
    @Override public String label() { return "Navy"; }

    public record Vars() implements ThemeVariables<Navy> {
        public static final Vars INSTANCE = new Vars();
        @Override public Navy theme() { return Navy.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }

        private static final Map<CssVar, String> VALUES = Map.ofEntries(
                Map.entry(DemoVars.SHADOW_COLOR, "rgba(0, 20, 40, 0.35)"),
                Map.entry(DemoVars.CELL_SHEEN,   "rgba(180, 210, 240, 0.10)"),

                // PlaygroundStyles — open ocean
                Map.entry(DemoVars.PG_TITLE_COLOR,           "#0a3a72"),
                Map.entry(DemoVars.PG_TITLE_TEXT_SHADOW,     "2px 2px 0 rgba(255, 255, 255, 0.45)"),
                Map.entry(DemoVars.PG_TITLE_LETTER_SPACING,  "4px"),
                Map.entry(DemoVars.PG_HINT_COLOR,            "#3a5a7a"),
                Map.entry(DemoVars.PG_SIZE_COLOR,            "#0a3a72"),
                Map.entry(DemoVars.PG_THEME_LABEL_COLOR,     "#0a3a72"),
                Map.entry(DemoVars.PG_THEME_BTN_BORDER,      "1px solid #6a8aaa"),
                Map.entry(DemoVars.PG_THEME_BTN_BG,          "rgba(255, 255, 255, 0.6)"),
                Map.entry(DemoVars.PG_THEME_BTN_COLOR,       "#0a3a72"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BG,     "#0a3a72"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BORDER, "#0a3a72"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_COLOR,  "#fff"),
                Map.entry(DemoVars.PG_PLAYGROUND_BG,
                        "linear-gradient(180deg, #87b4d4 0%, #5a8ab0 25%, #2a6090 55%, #0d3a6a 100%)"),
                Map.entry(DemoVars.PG_PLAYGROUND_BORDER,     "2px solid #2a4a6a"),
                Map.entry(DemoVars.PG_PLAYGROUND_SHADOW,     "0 4px 20px rgba(10, 58, 114, 0.4)"),
                Map.entry(DemoVars.PG_SKY_BG,
                        "radial-gradient(ellipse 90px 50px at 12% 60%, rgba(255,255,255,0.85) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 70px 40px at 16% 55%, rgba(255,255,255,0.8) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 110px 55px at 38% 45%, rgba(255,255,255,0.85) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 85px 50px at 65% 50%, rgba(255,255,255,0.78) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 75px 42px at 88% 48%, rgba(255,255,255,0.82) 0%, transparent 100%)"),
                Map.entry(DemoVars.PG_SKY_BG_REPEAT,         "no-repeat"),
                Map.entry(DemoVars.PG_SKY_BG_POSITION,       "left top"),
                Map.entry(DemoVars.PG_SKY_BG_SIZE,           "auto"),
                // The platform itself is the waterline — flat, low-contrast
                Map.entry(DemoVars.PG_PLATFORM_BG,           "linear-gradient(180deg, #4a7aa0 0%, #2d5a82 100%)"),
                Map.entry(DemoVars.PG_PLATFORM_RADIUS,       "2px"),
                Map.entry(DemoVars.PG_PLATFORM_BORDER_TOP,   "2px solid #6a9ac0"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BG,    "linear-gradient(180deg, #6a9ac0 0%, #4a7aa0 100%)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BORDER, "#9ac0e0"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_SHADOW, "0 0 12px rgba(120, 180, 220, 0.55)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_GLOW,   "rgba(255, 230, 120, 0.85)"),
                Map.entry(DemoVars.PG_VEHICLE_1_BG,          MilitarySvgs.NAVY_CARRIER),
                Map.entry(DemoVars.PG_VEHICLE_2_BG,          MilitarySvgs.NAVY_DESTROYER),
                Map.entry(DemoVars.PG_VEHICLE_3_BG,          MilitarySvgs.NAVY_SUBMARINE),
                Map.entry(DemoVars.PG_LAVA_BG,
                        "linear-gradient(0deg, #051a30, #0d3a6a, rgba(13, 58, 106, 0.4))"),
                Map.entry(DemoVars.PG_SCORE_COLOR,           "#0a3a72"),
                Map.entry(DemoVars.PG_GAMEOVER_BG,           "rgba(0, 0, 0, 0.7)"),
                Map.entry(DemoVars.PG_FINAL_SCORE_COLOR,     "#fff"),

                // Minimal SPIN_* / SUBWAY_* — neutral nautical to keep other apps usable.
                Map.entry(DemoVars.SPIN_TITLE_COLOR,         "#0a3a72"),
                Map.entry(DemoVars.SPIN_TITLE_TEXT_SHADOW,   "2px 2px 0 rgba(255, 255, 255, 0.45)"),
                Map.entry(DemoVars.SPIN_HINT_COLOR,          "#3a5a7a"),
                Map.entry(DemoVars.SPIN_CELL_BG,             "rgba(255, 255, 255, 0.35)"),
                Map.entry(DemoVars.SPIN_CELL_BORDER,         "2px solid #6a8aaa"),

                Map.entry(DemoVars.SUBWAY_TITLE_COLOR,       "#0a3a72"),
                Map.entry(DemoVars.SUBWAY_TITLE_TEXT_SHADOW, "2px 2px 0 rgba(255, 255, 255, 0.45)"),
                Map.entry(DemoVars.SUBWAY_HINT_COLOR,        "#3a5a7a"),
                Map.entry(DemoVars.SUBWAY_GRID_BG,           "rgba(255, 255, 255, 0.30)"),
                Map.entry(DemoVars.SUBWAY_GRID_BORDER,       "2px solid #6a8aaa"),
                Map.entry(DemoVars.SUBWAY_GRID_SHADOW,       "0 4px 20px rgba(10, 58, 114, 0.3)")
        );
    }

    public record Globals() implements ThemeGlobals<Navy> {
        public static final Globals INSTANCE = new Globals();
        @Override public Navy theme() { return Navy.INSTANCE; }
        @Override public String css() { return CSS; }

        private static final String CSS = """
                :root { color-scheme: light; }
                body {
                    margin: 0;
                    font-family: "Helvetica Neue", Arial, sans-serif;
                    background: linear-gradient(180deg, #87b4d4 0%, #c8dfee 35%, #2a4a6a 100%);
                    background-attachment: fixed;
                    color: #0d2a48;
                    min-height: 100vh;
                }
                #app { padding: 24px 32px; }
                .pg-controls label { display: flex; align-items: center; gap: 6px; font-size: 0.85rem; color: #0a3a72; }
                .pg-controls input[type="range"] { width: 120px; accent-color: #0a3a72; }
                .pg-theme-btn:hover { border-color: #0a3a72; color: #0a3a72; }
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
                    border: 2px solid #6a9ac0; border-radius: 6px;
                    background: rgba(106, 154, 192, 0.25); color: #fff;
                    cursor: pointer; transition: background 0.2s;
                }
                .pg-gameover button:hover { background: rgba(106, 154, 192, 0.45); }
                """;
    }
}
