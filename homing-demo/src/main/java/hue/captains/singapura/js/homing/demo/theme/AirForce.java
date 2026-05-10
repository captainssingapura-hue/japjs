package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;

import java.util.Map;

/**
 * Air Force theme — the moving-animal demo's playground becomes high-altitude
 * sky with fighter-jet/A-10/Apache-helicopter "platforms".
 */
public record AirForce() implements Theme {

    public static final AirForce INSTANCE = new AirForce();

    @Override public String slug()  { return "air-force"; }
    @Override public String label() { return "Air Force"; }

    public record Vars() implements ThemeVariables<AirForce> {
        public static final Vars INSTANCE = new Vars();
        @Override public AirForce theme() { return AirForce.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }

        private static final Map<CssVar, String> VALUES = Map.ofEntries(
                Map.entry(DemoVars.SHADOW_COLOR, "rgba(20, 30, 50, 0.30)"),
                Map.entry(DemoVars.CELL_SHEEN,   "rgba(255, 255, 255, 0.12)"),

                // PlaygroundStyles — high-altitude sky
                Map.entry(DemoVars.PG_TITLE_COLOR,           "#1a3060"),
                Map.entry(DemoVars.PG_TITLE_TEXT_SHADOW,     "2px 2px 0 rgba(255, 255, 255, 0.55)"),
                Map.entry(DemoVars.PG_TITLE_LETTER_SPACING,  "4px"),
                Map.entry(DemoVars.PG_HINT_COLOR,            "#3a5a85"),
                Map.entry(DemoVars.PG_SIZE_COLOR,            "#1a3060"),
                Map.entry(DemoVars.PG_THEME_LABEL_COLOR,     "#1a3060"),
                Map.entry(DemoVars.PG_THEME_BTN_BORDER,      "1px solid #7090c0"),
                Map.entry(DemoVars.PG_THEME_BTN_BG,          "rgba(255, 255, 255, 0.7)"),
                Map.entry(DemoVars.PG_THEME_BTN_COLOR,       "#1a3060"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BG,     "#1a3060"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BORDER, "#1a3060"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_COLOR,  "#fff"),
                Map.entry(DemoVars.PG_PLAYGROUND_BG,
                        "linear-gradient(180deg, #1a3060 0%, #4a78b8 25%, #b0d4f0 60%, #e8f2fc 100%)"),
                Map.entry(DemoVars.PG_PLAYGROUND_BORDER,     "2px solid #4a6a9a"),
                Map.entry(DemoVars.PG_PLAYGROUND_SHADOW,     "0 4px 20px rgba(26, 48, 96, 0.35)"),
                Map.entry(DemoVars.PG_SKY_BG,
                        "radial-gradient(ellipse 130px 40px at 18% 55%, rgba(255,255,255,0.95) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 100px 35px at 22% 50%, rgba(255,255,255,0.85) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 150px 50px at 50% 65%, rgba(255,255,255,0.92) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 110px 38px at 75% 55%, rgba(255,255,255,0.88) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 90px 32px at 92% 60%, rgba(255,255,255,0.85) 0%, transparent 100%)"),
                Map.entry(DemoVars.PG_SKY_BG_REPEAT,         "no-repeat"),
                Map.entry(DemoVars.PG_SKY_BG_POSITION,       "left top"),
                Map.entry(DemoVars.PG_SKY_BG_SIZE,           "auto"),
                // Platform = a stylised cloud bank the aircraft sits on
                Map.entry(DemoVars.PG_PLATFORM_BG,           "linear-gradient(180deg, rgba(255,255,255,0.92) 0%, rgba(190,210,235,0.85) 100%)"),
                Map.entry(DemoVars.PG_PLATFORM_RADIUS,       "8px"),
                Map.entry(DemoVars.PG_PLATFORM_BORDER_TOP,   "2px solid rgba(255, 255, 255, 0.95)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BG,    "linear-gradient(180deg, #ffffff 0%, #d8e8f8 100%)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BORDER, "#fff8b0"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_SHADOW, "0 0 14px rgba(255, 248, 176, 0.65)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_GLOW,   "rgba(255, 248, 176, 0.95)"),
                Map.entry(DemoVars.PG_VEHICLE_1_BG,          MilitarySvgs.AF_A10),
                Map.entry(DemoVars.PG_VEHICLE_2_BG,          MilitarySvgs.AF_FIGHTER),
                Map.entry(DemoVars.PG_VEHICLE_3_BG,          MilitarySvgs.AF_APACHE),
                Map.entry(DemoVars.PG_LAVA_BG,
                        "linear-gradient(0deg, #102040, #2a4a78, rgba(42, 74, 120, 0.3))"),
                Map.entry(DemoVars.PG_SCORE_COLOR,           "#1a3060"),
                Map.entry(DemoVars.PG_GAMEOVER_BG,           "rgba(0, 0, 0, 0.7)"),
                Map.entry(DemoVars.PG_FINAL_SCORE_COLOR,     "#fff"),

                Map.entry(DemoVars.SPIN_TITLE_COLOR,         "#1a3060"),
                Map.entry(DemoVars.SPIN_TITLE_TEXT_SHADOW,   "2px 2px 0 rgba(255, 255, 255, 0.55)"),
                Map.entry(DemoVars.SPIN_HINT_COLOR,          "#3a5a85"),
                Map.entry(DemoVars.SPIN_CELL_BG,             "rgba(255, 255, 255, 0.45)"),
                Map.entry(DemoVars.SPIN_CELL_BORDER,         "2px solid #7090c0"),

                Map.entry(DemoVars.SUBWAY_TITLE_COLOR,       "#1a3060"),
                Map.entry(DemoVars.SUBWAY_TITLE_TEXT_SHADOW, "2px 2px 0 rgba(255, 255, 255, 0.55)"),
                Map.entry(DemoVars.SUBWAY_HINT_COLOR,        "#3a5a85"),
                Map.entry(DemoVars.SUBWAY_GRID_BG,           "rgba(255, 255, 255, 0.40)"),
                Map.entry(DemoVars.SUBWAY_GRID_BORDER,       "2px solid #7090c0"),
                Map.entry(DemoVars.SUBWAY_GRID_SHADOW,       "0 4px 20px rgba(26, 48, 96, 0.3)")
        );
    }

    public record Globals() implements ThemeGlobals<AirForce> {
        public static final Globals INSTANCE = new Globals();
        @Override public AirForce theme() { return AirForce.INSTANCE; }
        @Override public String css() { return CSS; }

        private static final String CSS = """
                :root { color-scheme: light; }
                body {
                    margin: 0;
                    font-family: "Helvetica Neue", Arial, sans-serif;
                    background: linear-gradient(180deg, #1a3060 0%, #87b4d4 50%, #d8e8f8 100%);
                    background-attachment: fixed;
                    color: #0d1a2e;
                    min-height: 100vh;
                }
                #app { padding: 24px 32px; }
                .pg-controls label { display: flex; align-items: center; gap: 6px; font-size: 0.85rem; color: #1a3060; }
                .pg-controls input[type="range"] { width: 120px; accent-color: #1a3060; }
                .pg-theme-btn:hover { border-color: #1a3060; color: #1a3060; }
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
                    border: 2px solid #b0d4f0; border-radius: 6px;
                    background: rgba(176, 212, 240, 0.25); color: #fff;
                    cursor: pointer; transition: background 0.2s;
                }
                .pg-gameover button:hover { background: rgba(176, 212, 240, 0.45); }
                """;
    }
}
