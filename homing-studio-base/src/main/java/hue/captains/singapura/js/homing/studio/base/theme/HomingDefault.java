package hue.captains.singapura.js.homing.studio.base.theme;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;

import java.util.Map;

/**
 * The default Homing theme — the visual identity that ships with
 * {@code homing-studio-base}. Any consumer depending on this module gets
 * working CSS out of the box without designing their own theme.
 *
 * <p>RFC 0002-ext1 Phase 10 — restructured into identity record + nested
 * {@link Vars} and {@link Globals} singletons. The framework serves these
 * at independently-cacheable routes ({@code /theme-vars?theme=homing-default},
 * {@code /theme-globals?theme=homing-default}); per-CssGroup CSS files served
 * by {@code /css-content} no longer carry the cascade.</p>
 */
public record HomingDefault() implements Theme {

    public static final HomingDefault INSTANCE = new HomingDefault();

    @Override public String slug()  { return "homing-default"; }
    @Override public String label() { return "Homing default"; }

    // -------------------------------------------------------------------
    // Vars — the variable values for this theme. Served at /theme-vars.
    // Single semantic layer (--color-*, --space-*, --radius-*) — each role
    // gets a concrete value directly, with no intermediate primitive layer.
    // -------------------------------------------------------------------

    public record Vars() implements ThemeVariables<HomingDefault> {
        public static final Vars INSTANCE = new Vars();
        @Override public HomingDefault theme() { return HomingDefault.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }

        // Semantic-only — no primitive layer. Each role has an independent
        // value per theme, eliminating the "primitive doing double duty"
        // class of bug. Light values here; dark overrides in Globals.@media.
        private static final Map<CssVar, String> VALUES = Map.ofEntries(
                // Surfaces
                Map.entry(StudioVars.COLOR_SURFACE,          "#FAFBFD"),
                Map.entry(StudioVars.COLOR_SURFACE_RAISED,   "#FFFFFF"),
                Map.entry(StudioVars.COLOR_SURFACE_RECESSED, "#F1F4F9"),
                Map.entry(StudioVars.COLOR_SURFACE_INVERTED, "#111936"),

                // Text
                Map.entry(StudioVars.COLOR_TEXT_PRIMARY,           "#3B4A6B"),
                Map.entry(StudioVars.COLOR_TEXT_MUTED,             "#64748B"),
                Map.entry(StudioVars.COLOR_TEXT_ON_INVERTED,       "#FFFFFF"),
                Map.entry(StudioVars.COLOR_TEXT_ON_INVERTED_MUTED, "#CADCFC"),
                Map.entry(StudioVars.COLOR_TEXT_LINK,              "#1E2761"),
                Map.entry(StudioVars.COLOR_TEXT_LINK_HOVER,        "#C8921E"),

                // Borders
                Map.entry(StudioVars.COLOR_BORDER,          "#E2E8F0"),
                Map.entry(StudioVars.COLOR_BORDER_EMPHASIS, "#F4B942"),

                // Accent
                Map.entry(StudioVars.COLOR_ACCENT,          "#F4B942"),
                Map.entry(StudioVars.COLOR_ACCENT_EMPHASIS, "#C8921E"),
                Map.entry(StudioVars.COLOR_ACCENT_ON,       "#111936"),

                // Spacing scale
                Map.entry(StudioVars.SPACE_1, "4px"),
                Map.entry(StudioVars.SPACE_2, "8px"),
                Map.entry(StudioVars.SPACE_3, "12px"),
                Map.entry(StudioVars.SPACE_4, "16px"),
                Map.entry(StudioVars.SPACE_5, "20px"),
                Map.entry(StudioVars.SPACE_6, "24px"),
                Map.entry(StudioVars.SPACE_7, "32px"),
                Map.entry(StudioVars.SPACE_8, "40px"),

                // Radius scale
                Map.entry(StudioVars.RADIUS_SM, "4px"),
                Map.entry(StudioVars.RADIUS_MD, "8px"),
                Map.entry(StudioVars.RADIUS_LG, "12px")
        );
    }

    // -------------------------------------------------------------------
    // Globals — non-class-keyed CSS rules. Served at /theme-globals.
    // Two surviving use cases: descendant selectors over content the
    // framework didn't class (markdown-rendered HTML), and conditional
    // var definitions under media queries (system-preference dark mode).
    // -------------------------------------------------------------------

    /**
     * Theme-agnostic structural CSS shared across every studio theme — html/body
     * resets, multi-property hover rules, descendant selectors over
     * markdown-rendered content. References semantic tokens only, so resolves
     * correctly under any theme's primitive cascade.
     *
     * <p>Each theme's {@link ThemeGlobals#css()} composes its own
     * {@code @media (prefers-color-scheme: dark)} primitive override + this
     * shared structural string.</p>
     */
    public static final String STRUCTURAL_CSS = """
                html, body {
                    margin: 0;
                    padding: 0;
                    background: var(--color-surface);
                    color: var(--color-text-primary);
                    font-family: "Calibri", "Segoe UI", system-ui, sans-serif;
                    min-height: 100vh;
                }
                .st-crumb:hover { color: var(--color-accent); }
                .st-search:focus {
                    outline: none;
                    border-color: var(--color-border-emphasis);
                    box-shadow: 0 0 0 3px rgba(244, 185, 66, 0.18);
                }
                .st-filter-btn:hover {
                    border-color: var(--color-border-emphasis);
                    color: var(--color-text-link-hover);
                }
                .st-filter-btn-active:hover {
                    background: var(--color-surface-inverted);
                    color: var(--color-accent);
                    border-color: var(--color-surface-inverted);
                }
                .st-card:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(30, 39, 97, 0.12);
                    border-left-color: var(--color-accent-emphasis);
                }
                .st-card-featured .st-card-title {
                    color: var(--color-text-on-inverted);
                    font-size: 22px;
                }
                .st-card-featured .st-card-summary {
                    color: var(--color-text-on-inverted-muted);
                    font-size: 14px;
                    margin-top: 4px;
                }
                .st-card-featured .st-card-meta {
                    border: none;
                    padding: 0;
                    margin: 0;
                    flex-direction: column;
                    align-items: flex-end;
                    gap: 8px;
                }
                .st-card-featured .st-card-link {
                    color: var(--color-accent);
                }
                @media (max-width: 920px) {
                    .st-layout { grid-template-columns: 1fr; }
                    .st-sidebar { display: none; }
                }
                .st-toc-item:hover {
                    color: var(--color-text-link);
                    border-left-color: var(--color-border-emphasis);
                }
                .st-doc h1, .st-doc h2, .st-doc h3, .st-doc h4 {
                    font-family: "Georgia", serif;
                    color: var(--color-text-link);
                    margin: 1.6em 0 0.6em 0;
                    line-height: 1.25;
                    scroll-margin-top: 24px;
                }
                .st-doc h1 { font-size: 32px; border-bottom: 2px solid var(--color-border-emphasis); padding-bottom: 8px; margin-top: 0; }
                .st-doc h2 { font-size: 24px; }
                .st-doc h3 { font-size: 19px; }
                .st-doc h4 { font-size: 16px; color: var(--color-text-link-hover); letter-spacing: 1px; text-transform: uppercase; }
                .st-doc p  { margin: 0 0 1em 0; }
                .st-doc ul, .st-doc ol { margin: 0 0 1em 0; padding-left: 1.5em; }
                .st-doc li { margin: 0.3em 0; }
                .st-doc a { color: var(--color-text-link-hover); text-decoration: underline; text-underline-offset: 2px; }
                .st-doc a:hover { color: var(--color-text-link); }
                .st-doc blockquote {
                    margin: 1em 0;
                    padding: 4px 0 4px 18px;
                    border-left: 3px solid var(--color-border-emphasis);
                    color: var(--color-text-muted);
                    font-style: italic;
                }
                .st-doc code {
                    font-family: "Consolas", "Courier New", monospace;
                    font-size: 0.92em;
                    background: var(--color-surface-recessed);
                    color: var(--color-text-link);
                    padding: 1px 6px;
                    border-radius: 3px;
                }
                .st-doc pre {
                    background: var(--color-surface-inverted);
                    color: var(--color-text-on-inverted-muted);
                    padding: 14px 18px;
                    border-radius: 4px;
                    overflow-x: auto;
                    margin: 1em 0;
                    font-size: 13px;
                    line-height: 1.5;
                }
                .st-doc pre code {
                    background: transparent;
                    color: inherit;
                    padding: 0;
                    font-size: inherit;
                }
                .st-doc table {
                    width: 100%;
                    border-collapse: collapse;
                    margin: 1em 0;
                    font-size: 14px;
                }
                .st-doc th, .st-doc td {
                    text-align: left;
                    padding: 8px 12px;
                    border-bottom: 1px solid var(--color-border);
                    vertical-align: top;
                }
                .st-doc th {
                    background: var(--color-surface-inverted);
                    color: var(--color-text-on-inverted);
                    font-weight: 700;
                    border: none;
                }
                .st-doc tr:nth-child(even) td { background: var(--color-surface-recessed); }
                .st-doc hr {
                    border: none;
                    border-top: 1px solid var(--color-border);
                    margin: 2em 0;
                }
                .st-doc img { max-width: 100%; }
                .st-footer code {
                    font-family: "Consolas", "Courier New", monospace;
                    background: var(--color-surface-recessed);
                    color: var(--color-text-link);
                    padding: 1px 6px;
                    border-radius: 3px;
                }
                .st-app-pill:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 8px 18px rgba(30, 39, 97, 0.12);
                    border-left-color: var(--color-accent-emphasis);
                }
                .st-app-pill-dark:hover {
                    background: var(--color-surface-inverted);
                }
                .st-app-pill-dark .st-app-pill-icon {
                    background: var(--color-accent);
                    color: var(--color-accent-on);
                }
                .st-app-pill-dark .st-app-pill-label {
                    color: var(--color-text-on-inverted);
                }
                .st-app-pill-dark .st-app-pill-desc {
                    color: var(--color-text-on-inverted-muted);
                }
                .st-step-card:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 16px rgba(30, 39, 97, 0.10);
                    border-left-color: var(--color-accent-emphasis);
                }
                .st-task-done .st-task-box {
                    background: var(--color-accent);
                    border-color: var(--color-accent-emphasis);
                    color: var(--color-accent-on);
                    font-weight: 700;
                }
                """;

    public record Globals() implements ThemeGlobals<HomingDefault> {
        public static final Globals INSTANCE = new Globals();
        @Override public HomingDefault theme() { return HomingDefault.INSTANCE; }
        @Override public String css() { return DARK_OVERRIDE + STRUCTURAL_CSS; }

        /** Light/dark adaptation for HomingDefault. Without a primitive layer,
         *  the @media block re-binds semantic tokens directly — each role gets
         *  an independent dark value, no overloading possible. */
        private static final String DARK_OVERRIDE = """
                :root { color-scheme: light dark; }
                @media (prefers-color-scheme: dark) {
                    :root {
                        /* Surfaces — inverted. */
                        --color-surface:           #0F1320;
                        --color-surface-raised:    #1A1F36;
                        --color-surface-recessed:  #232943;
                        --color-surface-inverted:  #111936;   /* kept dark — header bg */

                        /* Text — inverted. */
                        --color-text-primary:            #E2E8F0;
                        --color-text-muted:              #94A3B8;
                        --color-text-on-inverted:        #E2E8F0;
                        --color-text-on-inverted-muted:  #B8C9F2;
                        --color-text-link:               #8FA3D8;   /* lifted navy */
                        --color-text-link-hover:         #E0A833;   /* lifted amber-dk */

                        /* Borders. */
                        --color-border:           #2D3454;
                        --color-border-emphasis:  #F4B942;          /* kept gold */

                        /* Accent — gold reads well on both modes. */
                        --color-accent:           #F4B942;
                        --color-accent-emphasis:  #E0A833;          /* lifted amber-dk */
                        --color-accent-on:        #111936;          /* dark text on gold */
                    }
                }
                """;
    }
}
