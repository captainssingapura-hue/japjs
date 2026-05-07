package hue.captains.singapura.js.homing.demo.theme;

import hue.captains.singapura.js.homing.core.CssVar;

import java.util.Set;

/**
 * RFC 0002-ext1 Phase 11 — typed CSS variable vocabulary for the homing-demo apps.
 *
 * <p>Mirrors {@code StudioVars} in shape. The demo themes vary substantially in
 * colors and gradients across PlaygroundStyles / SpinningStyles / SubwayStyles
 * (DemoDefault, Beach, Alpine, Dracula). Each theme-varying value becomes a
 * {@link CssVar}; the class body references it via {@code var(--…)} and each
 * theme's {@code Vars.values()} maps the var to its theme-specific concrete value.
 * For composite values (e.g. a sky gradient stack), the entire CSS property value
 * lives behind one var.</p>
 *
 * <p>CatalogueStyles and PitchDeckStyles ship only with DemoDefault — their
 * bodies use literal values directly and need no tokens here.</p>
 */
public final class DemoVars {

    // -------------------------------------------------------------------
    // Cross-cutting (used in multiple groups)
    // -------------------------------------------------------------------
    public static final CssVar SHADOW_COLOR = new CssVar("--shadow-color");
    public static final CssVar CELL_SHEEN   = new CssVar("--cell-sheen");

    // -------------------------------------------------------------------
    // PlaygroundStyles tokens
    // -------------------------------------------------------------------
    public static final CssVar PG_TITLE_COLOR        = new CssVar("--pg-title-color");
    public static final CssVar PG_TITLE_TEXT_SHADOW  = new CssVar("--pg-title-text-shadow");
    public static final CssVar PG_TITLE_LETTER_SPACING = new CssVar("--pg-title-letter-spacing");
    public static final CssVar PG_HINT_COLOR         = new CssVar("--pg-hint-color");
    public static final CssVar PG_SIZE_COLOR         = new CssVar("--pg-size-color");
    public static final CssVar PG_THEME_LABEL_COLOR  = new CssVar("--pg-theme-label-color");
    public static final CssVar PG_THEME_BTN_BORDER   = new CssVar("--pg-theme-btn-border");
    public static final CssVar PG_THEME_BTN_BG       = new CssVar("--pg-theme-btn-bg");
    public static final CssVar PG_THEME_BTN_COLOR    = new CssVar("--pg-theme-btn-color");
    public static final CssVar PG_THEME_BTN_ACTIVE_BG     = new CssVar("--pg-theme-btn-active-bg");
    public static final CssVar PG_THEME_BTN_ACTIVE_BORDER = new CssVar("--pg-theme-btn-active-border");
    public static final CssVar PG_THEME_BTN_ACTIVE_COLOR  = new CssVar("--pg-theme-btn-active-color");
    public static final CssVar PG_PLAYGROUND_BG      = new CssVar("--pg-playground-bg");
    public static final CssVar PG_PLAYGROUND_BORDER  = new CssVar("--pg-playground-border");
    public static final CssVar PG_PLAYGROUND_SHADOW  = new CssVar("--pg-playground-shadow");
    public static final CssVar PG_SKY_BG             = new CssVar("--pg-sky-bg");
    public static final CssVar PG_SKY_BG_REPEAT      = new CssVar("--pg-sky-bg-repeat");
    public static final CssVar PG_SKY_BG_POSITION    = new CssVar("--pg-sky-bg-position");
    public static final CssVar PG_SKY_BG_SIZE        = new CssVar("--pg-sky-bg-size");
    public static final CssVar PG_PLATFORM_BG        = new CssVar("--pg-platform-bg");
    public static final CssVar PG_PLATFORM_RADIUS    = new CssVar("--pg-platform-radius");
    public static final CssVar PG_PLATFORM_BORDER_TOP = new CssVar("--pg-platform-border-top");
    public static final CssVar PG_PLATFORM_ACTIVE_BG     = new CssVar("--pg-platform-active-bg");
    public static final CssVar PG_PLATFORM_ACTIVE_BORDER = new CssVar("--pg-platform-active-border");
    public static final CssVar PG_PLATFORM_ACTIVE_SHADOW = new CssVar("--pg-platform-active-shadow");
    public static final CssVar PG_LAVA_BG            = new CssVar("--pg-lava-bg");
    public static final CssVar PG_SCORE_COLOR        = new CssVar("--pg-score-color");
    public static final CssVar PG_GAMEOVER_BG        = new CssVar("--pg-gameover-bg");
    public static final CssVar PG_FINAL_SCORE_COLOR  = new CssVar("--pg-final-score-color");

    // -------------------------------------------------------------------
    // SpinningStyles tokens
    // -------------------------------------------------------------------
    public static final CssVar SPIN_TITLE_COLOR       = new CssVar("--spin-title-color");
    public static final CssVar SPIN_TITLE_TEXT_SHADOW = new CssVar("--spin-title-text-shadow");
    public static final CssVar SPIN_HINT_COLOR        = new CssVar("--spin-hint-color");
    public static final CssVar SPIN_CELL_BG           = new CssVar("--spin-cell-bg");
    public static final CssVar SPIN_CELL_BORDER       = new CssVar("--spin-cell-border");

    // -------------------------------------------------------------------
    // SubwayStyles tokens
    // -------------------------------------------------------------------
    public static final CssVar SUBWAY_TITLE_COLOR       = new CssVar("--subway-title-color");
    public static final CssVar SUBWAY_TITLE_TEXT_SHADOW = new CssVar("--subway-title-text-shadow");
    public static final CssVar SUBWAY_HINT_COLOR        = new CssVar("--subway-hint-color");
    public static final CssVar SUBWAY_GRID_BG           = new CssVar("--subway-grid-bg");
    public static final CssVar SUBWAY_GRID_BORDER       = new CssVar("--subway-grid-border");
    public static final CssVar SUBWAY_GRID_SHADOW       = new CssVar("--subway-grid-shadow");

    public static final Set<CssVar> ALL = Set.of(
            SHADOW_COLOR, CELL_SHEEN,
            PG_TITLE_COLOR, PG_TITLE_TEXT_SHADOW, PG_TITLE_LETTER_SPACING,
            PG_HINT_COLOR, PG_SIZE_COLOR, PG_THEME_LABEL_COLOR,
            PG_THEME_BTN_BORDER, PG_THEME_BTN_BG, PG_THEME_BTN_COLOR,
            PG_THEME_BTN_ACTIVE_BG, PG_THEME_BTN_ACTIVE_BORDER, PG_THEME_BTN_ACTIVE_COLOR,
            PG_PLAYGROUND_BG, PG_PLAYGROUND_BORDER, PG_PLAYGROUND_SHADOW,
            PG_SKY_BG, PG_SKY_BG_REPEAT, PG_SKY_BG_POSITION, PG_SKY_BG_SIZE,
            PG_PLATFORM_BG, PG_PLATFORM_RADIUS, PG_PLATFORM_BORDER_TOP,
            PG_PLATFORM_ACTIVE_BG, PG_PLATFORM_ACTIVE_BORDER, PG_PLATFORM_ACTIVE_SHADOW,
            PG_LAVA_BG, PG_SCORE_COLOR, PG_GAMEOVER_BG, PG_FINAL_SCORE_COLOR,
            SPIN_TITLE_COLOR, SPIN_TITLE_TEXT_SHADOW, SPIN_HINT_COLOR,
            SPIN_CELL_BG, SPIN_CELL_BORDER,
            SUBWAY_TITLE_COLOR, SUBWAY_TITLE_TEXT_SHADOW, SUBWAY_HINT_COLOR,
            SUBWAY_GRID_BG, SUBWAY_GRID_BORDER, SUBWAY_GRID_SHADOW
    );

    private DemoVars() {}
}
