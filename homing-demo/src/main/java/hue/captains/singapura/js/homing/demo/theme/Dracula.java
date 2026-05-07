package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;

import java.util.Map;

public record Dracula() implements Theme {

    public static final Dracula INSTANCE = new Dracula();

    @Override public String slug()  { return "dracula"; }
    @Override public String label() { return "Dracula"; }

    public record Vars() implements ThemeVariables<Dracula> {
        public static final Vars INSTANCE = new Vars();
        @Override public Dracula theme() { return Dracula.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }

        private static final Map<CssVar, String> VALUES = Map.ofEntries(
                Map.entry(DemoVars.SHADOW_COLOR, "rgba(0, 0, 0, 0.7)"),

                Map.entry(DemoVars.PG_TITLE_COLOR,       "#cc2222"),
                Map.entry(DemoVars.PG_TITLE_TEXT_SHADOW, "0 0 12px rgba(200, 0, 0, 0.5), 2px 2px 0 #000"),
                Map.entry(DemoVars.PG_TITLE_LETTER_SPACING, "6px"),
                Map.entry(DemoVars.PG_HINT_COLOR,        "#7a6888"),
                Map.entry(DemoVars.PG_SIZE_COLOR,        "#cc2222"),
                Map.entry(DemoVars.PG_THEME_LABEL_COLOR, "#7a6888"),
                Map.entry(DemoVars.PG_THEME_BTN_BORDER,  "1px solid #3a1a50"),
                Map.entry(DemoVars.PG_THEME_BTN_BG,      "rgba(30, 10, 40, 0.6)"),
                Map.entry(DemoVars.PG_THEME_BTN_COLOR,   "#9a8aaa"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BG,     "#cc2222"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BORDER, "#cc2222"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_COLOR,  "#d0c0d8"),
                Map.entry(DemoVars.PG_PLAYGROUND_BG,
                        "linear-gradient(180deg, #0d0018 0%, #18082a 30%, #251040 60%, #1a0a28 100%)"),
                Map.entry(DemoVars.PG_PLAYGROUND_BORDER, "2px solid #3a1a50"),
                Map.entry(DemoVars.PG_PLAYGROUND_SHADOW, "0 0 40px rgba(150, 0, 0, 0.15), inset 0 0 60px rgba(0, 0, 0, 0.3)"),
                Map.entry(DemoVars.PG_SKY_BG,
                        "radial-gradient(circle 30px at 82% 25%, rgba(220, 210, 180, 0.9) 0%, rgba(220, 210, 180, 0.1) 60%, transparent 100%),"
                      + "radial-gradient(circle 50px at 82% 25%, rgba(200, 190, 160, 0.12) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 12px 5px at 15% 30%, rgba(20, 0, 30, 0.8) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 12px 5px at 18% 28%, rgba(20, 0, 30, 0.8) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 10px 4px at 72% 20%, rgba(30, 0, 40, 0.7) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 10px 4px at 74% 18%, rgba(30, 0, 40, 0.7) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 250px 25px at 35% 95%, rgba(80, 50, 100, 0.25) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 200px 20px at 55% 92%, rgba(60, 40, 80, 0.2) 0%, transparent 100%),"
                      + "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 120' preserveAspectRatio='xMidYMax slice'%3E%3Cdefs%3E%3ClinearGradient id='cg' x1='0' y1='0' x2='0' y2='1'%3E%3Cstop offset='0' stop-color='%23100020'/%3E%3Cstop offset='1' stop-color='%230a0012'/%3E%3C/linearGradient%3E%3C/defs%3E%3Cpath d='M0,120 L0,95 L15,95 L15,90 L20,90 L20,95 L30,95 L30,88 L35,88 L35,95 L50,95 L50,80 L55,80 L55,72 L60,72 L60,80 L70,80 L70,95 L90,95 L90,85 L95,85 L95,78 L100,78 L100,85 L108,85 L108,55 L112,55 L112,48 L115,40 L118,48 L118,55 L122,55 L122,85 L130,85 L130,65 L134,65 L134,58 L137,50 L140,42 L143,50 L146,58 L146,65 L150,65 L150,38 L153,38 L153,30 L155,22 L157,14 L159,22 L161,30 L161,38 L164,38 L164,65 L168,65 L168,58 L171,50 L174,42 L177,50 L180,58 L180,65 L184,65 L184,85 L192,85 L192,55 L196,55 L196,48 L199,40 L202,48 L202,55 L206,55 L206,85 L214,85 L214,78 L219,78 L219,85 L224,85 L224,95 L240,95 L240,82 L245,82 L245,75 L250,75 L250,82 L260,82 L260,95 L280,95 L280,90 L285,90 L285,95 L295,95 L295,88 L300,88 L300,95 L320,95 L320,92 L325,92 L325,95 L340,95 L340,90 L345,90 L345,95 L400,95 L400,120 Z' fill='url(%23cg)'/%3E%3Crect x='135' y='70' width='8' height='10' rx='4' ry='4' fill='%23352040' opacity='0.6'/%3E%3Crect x='165' y='70' width='8' height='10' rx='4' ry='4' fill='%23352040' opacity='0.6'/%3E%3Crect x='152' y='45' width='6' height='8' rx='3' ry='3' fill='%23453060' opacity='0.5'/%3E%3Crect x='113' y='60' width='5' height='7' rx='2.5' ry='2.5' fill='%23352040' opacity='0.5'/%3E%3Crect x='197' y='60' width='5' height='7' rx='2.5' ry='2.5' fill='%23352040' opacity='0.5'/%3E%3C/svg%3E\")"),
                Map.entry(DemoVars.PG_SKY_BG_REPEAT,   "no-repeat"),
                Map.entry(DemoVars.PG_SKY_BG_POSITION, "left top, left top, left top, left top, left top, left top, left top, left top, center bottom"),
                Map.entry(DemoVars.PG_SKY_BG_SIZE,     "auto, auto, auto, auto, auto, auto, auto, auto, 80% 90%"),
                Map.entry(DemoVars.PG_PLATFORM_BG,           "linear-gradient(180deg, #4a3a5a, #352845)"),
                Map.entry(DemoVars.PG_PLATFORM_RADIUS,       "2px"),
                Map.entry(DemoVars.PG_PLATFORM_BORDER_TOP,   "2px solid #6a5080"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BG,     "linear-gradient(180deg, #5a4070, #443058)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BORDER, "#8a60a0"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_SHADOW, "0 0 12px rgba(150, 80, 200, 0.4)"),
                Map.entry(DemoVars.PG_LAVA_BG,
                        "linear-gradient(0deg, #8b0000, #cc1100, rgba(180, 0, 0, 0.3))"),
                Map.entry(DemoVars.PG_SCORE_COLOR,        "#cc2222"),
                Map.entry(DemoVars.PG_GAMEOVER_BG,        "rgba(10, 0, 15, 0.85)"),
                Map.entry(DemoVars.PG_FINAL_SCORE_COLOR,  "#d0c0d8")
        );
    }

    public record Globals() implements ThemeGlobals<Dracula> {
        public static final Globals INSTANCE = new Globals();
        @Override public Dracula theme() { return Dracula.INSTANCE; }
        @Override public String css() { return CSS; }

        private static final String CSS = """
                :root { color-scheme: dark; }
                body {
                    margin: 0;
                    font-family: "Palatino Linotype", "Book Antiqua", Palatino, serif;
                    background: linear-gradient(180deg, #0a0012 0%, #1a0a2e 40%, #2d1045 100%);
                    background-attachment: fixed;
                    color: #d0c0d8;
                    min-height: 100vh;
                }
                #app { padding: 24px 32px; }
                .pg-controls label {
                    display: flex; align-items: center; gap: 6px;
                    font-size: 0.85rem; color: #8a7898;
                }
                .pg-controls input[type="range"] { width: 120px; accent-color: #cc2222; }
                .pg-theme-btn:hover { border-color: #cc2222; color: #cc2222; }
                .pg-animal svg {
                    width: 50px; height: auto;
                    filter: drop-shadow(1px 1px 3px var(--shadow-color));
                }
                .pg-gameover h2 {
                    font-size: 2.5rem; color: #cc2222;
                    text-shadow: 0 0 25px rgba(200, 0, 0, 0.7);
                    margin: 0 0 8px 0;
                }
                .pg-gameover button {
                    padding: 8px 24px; font-size: 1rem; font-weight: 600;
                    border: 2px solid #cc2222; border-radius: 6px;
                    background: rgba(180, 0, 0, 0.2); color: #d0c0d8;
                    cursor: pointer; transition: background 0.2s;
                }
                .pg-gameover button:hover { background: rgba(180, 0, 0, 0.4); }
                """;
    }
}
