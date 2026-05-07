package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;

import java.util.Map;

/**
 * The default theme for the homing-demo apps. Slug {@code "homing-default"}
 * to match the framework convention so default {@code /css-content} requests
 * (omitting {@code ?theme=}) resolve here.
 *
 * <p>RFC 0002-ext1 Phase 11 — restructured into identity record + nested
 * {@link Vars} and {@link Globals} singletons mirroring {@code HomingDefault}.</p>
 */
public record DemoDefault() implements Theme {

    public static final DemoDefault INSTANCE = new DemoDefault();

    @Override public String slug()  { return "homing-default"; }
    @Override public String label() { return "Demo default"; }

    public record Vars() implements ThemeVariables<DemoDefault> {
        public static final Vars INSTANCE = new Vars();
        @Override public DemoDefault theme() { return DemoDefault.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }

        private static final Map<CssVar, String> VALUES = Map.ofEntries(
                Map.entry(DemoVars.SHADOW_COLOR, "light-dark(rgba(0, 0, 0, 0.15), rgba(0, 0, 0, 0.5))"),
                Map.entry(DemoVars.CELL_SHEEN,   "light-dark(rgba(0, 0, 0, 0.02), rgba(255, 255, 255, 0.03))"),

                // PlaygroundStyles
                Map.entry(DemoVars.PG_TITLE_COLOR,       "light-dark(#0090b0, #00b4d8)"),
                Map.entry(DemoVars.PG_TITLE_TEXT_SHADOW, "2px 2px 0 light-dark(rgba(0, 0, 0, 0.1), #000)"),
                Map.entry(DemoVars.PG_TITLE_LETTER_SPACING, "4px"),
                Map.entry(DemoVars.PG_HINT_COLOR,        "light-dark(#666, #888)"),
                Map.entry(DemoVars.PG_SIZE_COLOR,        "light-dark(#0090b0, #00b4d8)"),
                Map.entry(DemoVars.PG_THEME_LABEL_COLOR, "light-dark(#555, #aaa)"),
                Map.entry(DemoVars.PG_THEME_BTN_BORDER,  "1px solid light-dark(#ccc, #444)"),
                Map.entry(DemoVars.PG_THEME_BTN_BG,      "light-dark(#fff, #2a2a3e)"),
                Map.entry(DemoVars.PG_THEME_BTN_COLOR,   "light-dark(#444, #bbb)"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BG,     "light-dark(#0090b0, #00b4d8)"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_BORDER, "light-dark(#0090b0, #00b4d8)"),
                Map.entry(DemoVars.PG_THEME_BTN_ACTIVE_COLOR,  "#fff"),
                Map.entry(DemoVars.PG_PLAYGROUND_BG,     "light-dark(#eef1f5, #16213e)"),
                Map.entry(DemoVars.PG_PLAYGROUND_BORDER, "2px solid light-dark(#ccc, #333)"),
                Map.entry(DemoVars.PG_PLAYGROUND_SHADOW, "0 0 30px light-dark(rgba(0, 180, 216, 0.08), rgba(0, 180, 216, 0.15))"),
                Map.entry(DemoVars.PG_SKY_BG,
                        "radial-gradient(ellipse 80px 50px at 12% 60%, light-dark(rgba(255,255,255,0.9), rgba(80,90,120,0.4)) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 60px 36px at 16% 55%, light-dark(rgba(255,255,255,0.9), rgba(80,90,120,0.4)) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 100px 55px at 38% 45%, light-dark(rgba(255,255,255,0.85), rgba(70,80,110,0.35)) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 70px 40px at 42% 40%, light-dark(rgba(255,255,255,0.85), rgba(70,80,110,0.35)) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 90px 48px at 65% 55%, light-dark(rgba(255,255,255,0.8), rgba(60,70,100,0.3)) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 65px 38px at 70% 50%, light-dark(rgba(255,255,255,0.8), rgba(60,70,100,0.3)) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 75px 42px at 88% 48%, light-dark(rgba(255,255,255,0.85), rgba(75,85,115,0.35)) 0%, transparent 100%),"
                      + "radial-gradient(ellipse 55px 34px at 91% 43%, light-dark(rgba(255,255,255,0.85), rgba(75,85,115,0.35)) 0%, transparent 100%)"),
                Map.entry(DemoVars.PG_SKY_BG_REPEAT,   "no-repeat"),
                Map.entry(DemoVars.PG_SKY_BG_POSITION, "left top"),
                Map.entry(DemoVars.PG_SKY_BG_SIZE,     "auto"),
                Map.entry(DemoVars.PG_PLATFORM_BG,           "light-dark(#6b8f6b, #3a5f3a)"),
                Map.entry(DemoVars.PG_PLATFORM_RADIUS,       "3px"),
                Map.entry(DemoVars.PG_PLATFORM_BORDER_TOP,   "2px solid light-dark(#8ab88a, #5a8f5a)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BG,     "light-dark(#88bb88, #509050)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_BORDER, "light-dark(#a0d8a0, #70b870)"),
                Map.entry(DemoVars.PG_PLATFORM_ACTIVE_SHADOW, "0 0 10px light-dark(rgba(100, 200, 100, 0.4), rgba(80, 180, 80, 0.5))"),
                Map.entry(DemoVars.PG_LAVA_BG,
                        "linear-gradient(0deg, light-dark(#ff4500, #cc3700), light-dark(#ff6a00, #e05500), light-dark(rgba(255, 106, 0, 0.3), rgba(204, 55, 0, 0.3)))"),
                Map.entry(DemoVars.PG_SCORE_COLOR,        "light-dark(#0090b0, #00b4d8)"),
                Map.entry(DemoVars.PG_GAMEOVER_BG,        "rgba(0, 0, 0, 0.7)"),
                Map.entry(DemoVars.PG_FINAL_SCORE_COLOR,  "#fff"),

                // SpinningStyles
                Map.entry(DemoVars.SPIN_TITLE_COLOR,       "light-dark(#c020d8, #e040fb)"),
                Map.entry(DemoVars.SPIN_TITLE_TEXT_SHADOW, "2px 2px 0 light-dark(rgba(0, 0, 0, 0.1), #000)"),
                Map.entry(DemoVars.SPIN_HINT_COLOR,        "light-dark(#666, #888)"),
                Map.entry(DemoVars.SPIN_CELL_BG,           "light-dark(#eef1f5, #16213e)"),
                Map.entry(DemoVars.SPIN_CELL_BORDER,       "2px solid light-dark(#ccc, #333)"),

                // SubwayStyles
                Map.entry(DemoVars.SUBWAY_TITLE_COLOR,       "light-dark(#e55a25, #ff6b35)"),
                Map.entry(DemoVars.SUBWAY_TITLE_TEXT_SHADOW, "2px 2px 0 light-dark(rgba(0, 0, 0, 0.1), #000)"),
                Map.entry(DemoVars.SUBWAY_HINT_COLOR,        "light-dark(#666, #888)"),
                Map.entry(DemoVars.SUBWAY_GRID_BG,           "light-dark(#eef1f5, #16213e)"),
                Map.entry(DemoVars.SUBWAY_GRID_BORDER,       "2px solid light-dark(#ccc, #333)"),
                Map.entry(DemoVars.SUBWAY_GRID_SHADOW,       "0 0 30px light-dark(rgba(0, 180, 216, 0.08), rgba(0, 180, 216, 0.15))")
        );
    }

    public record Globals() implements ThemeGlobals<DemoDefault> {
        public static final Globals INSTANCE = new Globals();
        @Override public DemoDefault theme() { return DemoDefault.INSTANCE; }
        @Override public String css() { return CSS; }

        private static final String CSS = """
                :root {
                    color-scheme: light dark;
                    --cat-navy:      #1E2761;
                    --cat-navy-deep: #111936;
                    --cat-ice:       #CADCFC;
                    --cat-amber:     #F4B942;
                    --cat-amber-dk:  #C8921E;
                    --cat-white:     #FFFFFF;
                    --cat-offwhite:  #FAFBFD;
                    --cat-gray-dk:   #3B4A6B;
                    --cat-gray-mid:  #64748B;
                    --cat-gray-lt:   #E2E8F0;
                    --pd-navy:      #1E2761;
                    --pd-navy-deep: #111936;
                    --pd-ice:       #CADCFC;
                    --pd-amber:     #F4B942;
                    --pd-amber-dk:  #C8921E;
                    --pd-white:     #FFFFFF;
                    --pd-offwhite:  #FAFBFD;
                    --pd-gray-dk:   #3B4A6B;
                    --pd-gray-mid:  #64748B;
                    --pd-gray-lt:   #E2E8F0;
                    --pd-green:     #059669;
                    --pd-red:       #DC2626;
                }

                /* ---- Catalogue + PitchDeck globals (DemoDefault-only groups) ---- */
                html, body {
                    margin: 0;
                    padding: 0;
                    background: linear-gradient(180deg, #FAFBFD 0%, #EEF1F8 100%);
                    color: var(--cat-gray-dk);
                    font-family: "Calibri", "Segoe UI", system-ui, sans-serif;
                    min-height: 100vh;
                }
                .cat-card:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 8px 18px rgba(30, 39, 97, 0.12);
                    border-color: var(--cat-amber-dk);
                    border-left-color: var(--cat-amber-dk);
                }
                .cat-card-featured:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 12px 28px rgba(30, 39, 97, 0.25);
                    border-color: var(--cat-amber);
                    border-left-color: var(--cat-amber);
                }
                .cat-card-featured .cat-card-head { flex: 1; margin-bottom: 0; }
                .cat-card-featured .cat-card-title { color: var(--cat-white); font-size: 28px; }
                .cat-card-featured .cat-card-desc { color: var(--cat-ice); font-size: 15px; flex: 0; margin-top: 6px; }
                .cat-card-featured .cat-card-meta {
                    border-top: none; padding-top: 0; margin-top: 0;
                    flex-direction: column; align-items: flex-end; gap: 12px;
                }
                .cat-card-featured .cat-card-link { color: var(--cat-amber); font-size: 14px; }
                .cat-card-featured .cat-mono {
                    background: rgba(202, 220, 252, 0.12);
                    color: var(--cat-ice);
                }
                .cat-footer code {
                    font-family: "Consolas", "Courier New", monospace;
                    background: rgba(30, 39, 97, 0.05);
                    padding: 1px 6px;
                    border-radius: 3px;
                    color: var(--cat-navy);
                }
                .pd-slide::before {
                    content: ""; position: absolute;
                    top: 0; left: 0; bottom: 0; width: 6px;
                    background: var(--pd-amber);
                }
                .pd-slide-dark::before { background: var(--pd-amber); }
                .pd-slide-dark .pd-kicker { color: var(--pd-amber); }
                .pd-slide-dark .pd-title { color: var(--pd-white); }
                .pd-slide-dark .pd-subtitle { color: var(--pd-ice); font-style: normal; }
                .pd-slide-dark .pd-body { color: var(--pd-ice); }
                @keyframes pd-pulse { 0%, 100% { opacity: 0.65; } 50% { opacity: 1; } }
                .pd-btn:hover {
                    background: rgba(202, 220, 252, 0.1);
                    border-color: var(--pd-amber);
                    color: var(--pd-amber);
                }
                .pd-btn:disabled { opacity: 0.3; cursor: default; pointer-events: none; }
                .pd-btn-primary:hover {
                    background: var(--pd-white);
                    color: var(--pd-navy);
                    border-color: var(--pd-white);
                }
                .pd-btn-bgm::before { content: "♪"; font-size: 16px; color: var(--pd-gray-mid); }
                .pd-btn-bgm-on::before { color: var(--pd-amber); }
                .pd-dot:hover { background: rgba(244, 185, 66, 0.6); }
                .pd-card-dark .pd-card-head { color: var(--pd-white); }
                .pd-card-dark .pd-card-body { color: var(--pd-ice); }
                .pd-stat::before {
                    content: ""; position: absolute;
                    top: 0; left: 0; bottom: 0; width: 3px;
                    background: var(--pd-amber);
                }
                .pd-table-cell:first-child { font-weight: 700; color: var(--pd-navy); }
                .pd-table-row-featured.pd-table-cell:first-child { color: var(--pd-navy-deep); }
                .pd-slide-dark .pd-hint { color: var(--pd-amber); }
                .pd-slide-dark .pd-accent { color: var(--pd-amber); }
                .pd-arch-layer {
                    background: var(--pd-white);
                    border: 1px solid var(--pd-ice);
                    border-left: 4px solid var(--pd-amber);
                    padding: 14px 18px;
                    margin-bottom: 10px;
                    border-radius: 4px;
                    opacity: 0;
                    transform: translateX(-20px);
                    transition: opacity 400ms ease, transform 400ms ease;
                    box-shadow: 0 2px 6px rgba(30, 39, 97, 0.05);
                }
                .pd-arch-layer.pd-arch-shown { opacity: 1; transform: translateX(0); }
                .pd-arch-layer-base { background: var(--pd-navy); color: var(--pd-ice); }
                .pd-arch-layer-num {
                    font-family: "Georgia", serif; font-size: 11px; font-weight: 700;
                    letter-spacing: 3px; color: var(--pd-gray-mid); text-transform: uppercase;
                }
                .pd-arch-layer-base .pd-arch-layer-num { color: var(--pd-amber); }
                .pd-arch-layer-title {
                    font-family: "Georgia", serif; font-size: 18px; font-weight: 700;
                    color: var(--pd-navy); margin-top: 4px;
                }
                .pd-arch-layer-base .pd-arch-layer-title { color: var(--pd-white); }
                .pd-arch-layer-desc { font-size: 12px; color: var(--pd-gray-dk); margin-top: 4px; }
                .pd-arch-layer-base .pd-arch-layer-desc { color: var(--pd-ice); }

                /* ---- Playground / Spinning / Subway shared body+chrome ---- */
                body {
                    margin: 0;
                    font-family: "Helvetica Neue", Arial, sans-serif;
                    background: light-dark(#f5f5f7, #1a1a2e);
                    color: light-dark(#222, #eee);
                }
                #app { padding: 24px 32px; }
                .pg-controls label {
                    display: flex; align-items: center; gap: 6px;
                    font-size: 0.85rem; color: light-dark(#555, #aaa);
                }
                .pg-controls input[type="range"] { width: 120px; accent-color: #00b4d8; }
                .pg-theme-btn:hover {
                    border-color: light-dark(#0090b0, #00b4d8);
                    color: light-dark(#0090b0, #00b4d8);
                }
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
                    border: 2px solid #ff6a00; border-radius: 6px;
                    background: rgba(255, 69, 0, 0.2); color: #fff;
                    cursor: pointer; transition: background 0.2s;
                }
                .pg-gameover button:hover { background: rgba(255, 69, 0, 0.4); }
                .spin-controls label {
                    display: flex; align-items: center; gap: 6px;
                    font-size: 0.85rem; color: light-dark(#555, #aaa);
                }
                .spin-controls input[type="range"] { width: 120px; accent-color: #e040fb; }
                .spin-controls button {
                    padding: 6px 14px;
                    border: 1px solid light-dark(#c020d8, #e040fb);
                    border-radius: 4px;
                    background: transparent;
                    color: light-dark(#c020d8, #e040fb);
                    font-weight: 600; cursor: pointer;
                    transition: background 0.2s;
                }
                .spin-controls button:hover {
                    background: light-dark(rgba(192, 32, 216, 0.1), rgba(224, 64, 251, 0.15));
                }
                .spin-cell:hover {
                    border-color: light-dark(#c020d8, #e040fb);
                    box-shadow: 0 0 16px light-dark(rgba(192, 32, 216, 0.15), rgba(224, 64, 251, 0.3));
                }
                .spin-cell.paused { opacity: 0.4; }
                .spin-cell svg {
                    width: 70%; height: auto;
                    filter: drop-shadow(1px 1px 2px var(--shadow-color));
                }
                .subway-title::after {
                    content: ""; display: block; width: 100%; height: 4px; margin-top: 8px;
                    background: repeating-linear-gradient(
                        90deg,
                        #ff6b35 0, #ff6b35 20px,
                        transparent 20px, transparent 28px,
                        #00b4d8 28px, #00b4d8 48px,
                        transparent 48px, transparent 56px,
                        #3ea63f 56px, #3ea63f 76px,
                        transparent 76px, transparent 84px
                    );
                }
                .subway-cell:hover {
                    background: light-dark(rgba(62, 166, 63, 0.08), rgba(62, 166, 63, 0.15));
                    box-shadow: 0 0 12px light-dark(rgba(62, 166, 63, 0.15), rgba(62, 166, 63, 0.3));
                }
                .subway-cell svg {
                    width: 100%; height: auto;
                    filter: drop-shadow(1px 1px 2px var(--shadow-color));
                }
                """;
    }
}
