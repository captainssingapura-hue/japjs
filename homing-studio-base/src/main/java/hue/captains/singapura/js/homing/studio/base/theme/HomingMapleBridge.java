package hue.captains.singapura.js.homing.studio.base.theme;

import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.core.Theme;
import hue.captains.singapura.js.homing.core.ThemeGlobals;
import hue.captains.singapura.js.homing.core.ThemeVariables;

import java.util.Map;

/**
 * Maple Bridge — a tier-3 layered theme inspired by Zhang Ji's Tang-dynasty
 * poem 枫桥夜泊 ("Night Mooring at Maple Bridge"). A full-page SVG nocturne
 * sits as a fixed-position background; the studio chrome rides over it.
 *
 * <p>The SVG carries its own night / dawn palettes internally, switched by
 * {@code @media (prefers-color-scheme)}. The framework's semantic tokens
 * (this theme's {@code Vars} + dark-mode override) are <i>independently</i>
 * derived from the SVG's colours but not wired through to them — the SVG
 * flips with the OS preference, the chrome flips with the studio's theme
 * picker. Two themability systems running in parallel; both atmospheric.</p>
 *
 * <p>Activate via {@code ?theme=maple-bridge} on any studio URL.</p>
 *
 * <h3>Why this theme is structurally interesting</h3>
 * <ul>
 *   <li>First theme to declare a {@link Theme#backdrop()} — the framework
 *       renders the SvgRef-referenced illustration as <b>inline DOM</b>
 *       (not a {@code background-image} sandbox). Individual elements inside
 *       the SVG (the moon, the temple window, etc.) participate in the host
 *       document's CSS cascade — themes can attach {@code :hover}, transitions
 *       and animation triggers per-element.</li>
 *   <li>The moon grows on hover via a simple CSS rule targeting
 *       {@code .theme-backdrop .mb-moon}. Nothing in JS; the SVG primitive
 *       carries the rest.</li>
 *   <li>{@code position: fixed} on {@code .theme-backdrop} keeps the lake /
 *       temple / moon put while content scrolls over them.</li>
 * </ul>
 */
public record HomingMapleBridge() implements Theme {

    public static final HomingMapleBridge INSTANCE = new HomingMapleBridge();

    @Override public String slug()  { return "maple-bridge"; }
    @Override public String label() { return "Maple Bridge"; }

    /** The atmospheric nocturne — rendered as inline DOM, per-element-interactive. */
    @Override
    public SvgRef<?> backdrop() {
        return new SvgRef<>(HomingMapleBridgeBg.INSTANCE, new HomingMapleBridgeBg.nocturne());
    }

    public record Vars() implements ThemeVariables<HomingMapleBridge> {
        public static final Vars INSTANCE = new Vars();
        @Override public HomingMapleBridge theme() { return HomingMapleBridge.INSTANCE; }
        @Override public Map<CssVar, String> values() { return VALUES; }

        // Light mode (dawn) — distilled from the SVG's dawn palette but
        // independent of it. Page surfaces are translucent-ish warm tones
        // that read well against the dawn scene; cards/header are opaque
        // surfaces that sit cleanly over the illustration.
        private static final Map<CssVar, String> VALUES = Map.ofEntries(
                Map.entry(StudioVars.COLOR_SURFACE,                "#E8C9A0"),  // sky-bot dawn
                Map.entry(StudioVars.COLOR_SURFACE_RAISED,         "#F5E7C8"),  // raised paper
                Map.entry(StudioVars.COLOR_SURFACE_RECESSED,       "#D4B896"),  // sky-mid dawn
                Map.entry(StudioVars.COLOR_SURFACE_INVERTED,       "#3A4250"),  // temple slate

                Map.entry(StudioVars.COLOR_TEXT_PRIMARY,           "#2A2418"),  // deep ink
                Map.entry(StudioVars.COLOR_TEXT_MUTED,             "#5A5040"),
                Map.entry(StudioVars.COLOR_TEXT_ON_INVERTED,       "#FFF5DC"),  // dawn moon
                Map.entry(StudioVars.COLOR_TEXT_ON_INVERTED_MUTED, "#C0A878"),
                Map.entry(StudioVars.COLOR_TEXT_LINK,              "#8A6A3A"),  // amber window
                Map.entry(StudioVars.COLOR_TEXT_LINK_HOVER,        "#4A5466"),  // mountain-near

                Map.entry(StudioVars.COLOR_BORDER,                 "#C0A878"),
                Map.entry(StudioVars.COLOR_BORDER_EMPHASIS,        "#8A6A3A"),

                Map.entry(StudioVars.COLOR_ACCENT,                 "#8A6A3A"),
                Map.entry(StudioVars.COLOR_ACCENT_EMPHASIS,        "#4A5466"),
                Map.entry(StudioVars.COLOR_ACCENT_ON,              "#FFF5DC"),

                Map.entry(StudioVars.SPACE_1, "4px"),
                Map.entry(StudioVars.SPACE_2, "8px"),
                Map.entry(StudioVars.SPACE_3, "12px"),
                Map.entry(StudioVars.SPACE_4, "16px"),
                Map.entry(StudioVars.SPACE_5, "20px"),
                Map.entry(StudioVars.SPACE_6, "24px"),
                Map.entry(StudioVars.SPACE_7, "32px"),
                Map.entry(StudioVars.SPACE_8, "40px"),
                Map.entry(StudioVars.RADIUS_SM, "3px"),
                Map.entry(StudioVars.RADIUS_MD, "6px"),
                Map.entry(StudioVars.RADIUS_LG, "10px")
        );
    }

    public record Globals() implements ThemeGlobals<HomingMapleBridge> {
        public static final Globals INSTANCE = new Globals();
        @Override public HomingMapleBridge theme() { return HomingMapleBridge.INSTANCE; }
        @Override public String css() {
            // Order: dark-mode overrides → shared structural cascade → our
            // SVG-background overlay. Putting the overlay last lets the
            // background-image longhand land after the structural
            // `background: var(--color-surface)` shorthand cleared it.
            return DARK_OVERRIDE + HomingDefault.STRUCTURAL_CSS + TEXTURE_OVERRIDE;
        }

        /** Dark-mode (night) palette — when the OS prefers dark. */
        private static final String DARK_OVERRIDE = """
                :root { color-scheme: light dark; }
                @media (prefers-color-scheme: dark) {
                    :root {
                        --color-surface:           #0A131E;   /* water-bot */
                        --color-surface-raised:    #1B2D44;   /* sky-mid night */
                        --color-surface-recessed:  #0D1620;   /* mountain-near */
                        --color-surface-inverted:  #0E1620;   /* temple */

                        --color-text-primary:            #CBD9E8;
                        --color-text-muted:              #7A89A0;
                        --color-text-on-inverted:        #BCC7D6;   /* mist */
                        --color-text-on-inverted-muted:  #7A89A0;
                        --color-text-link:               #F5E3B0;   /* moon */
                        --color-text-link-hover:         #F0DCA8;

                        --color-border:           #3A5070;          /* ripple */
                        --color-border-emphasis:  #F5E3B0;

                        --color-accent:           #F5E3B0;
                        --color-accent-emphasis:  #D9A35A;          /* temple-window */
                        --color-accent-on:        #0A131E;
                    }
                }
                """;

        /**
         * Positions the framework-injected {@code .theme-backdrop} as a
         * fixed-cover layer behind the studio chrome, and attaches per-
         * element hover effects to the inline SVG's classed elements
         * (the moon, in this theme).
         *
         * <p>The framework's {@code AppHtmlGetAction} reads
         * {@link HomingMapleBridge#backdrop()}, resolves the SVG markup from
         * the classpath, and emits {@code <div class="theme-backdrop">SVG</div>}
         * as the first child of {@code <body>}. The rules below place that
         * div, and bind the moon's hover-grow to its classed {@code <circle>}.</p>
         *
         * <p>The SVG's internal {@code <style>} block carries the night/dawn
         * palette via {@code @media (prefers-color-scheme)} — it flips
         * with the OS while the framework's semantic tokens (above) flip
         * with the same media query, keeping the chrome and the
         * illustration in lockstep without explicit coordination.</p>
         */
        private static final String TEXTURE_OVERRIDE = """
                /* Body's `background: var(--color-surface)` from STRUCTURAL_CSS
                   would paint an opaque sandy layer over the z-index:-1 backdrop
                   (the symptom: SVG flashes once on first paint, then vanishes
                   as the body bg resolves). Make the body transparent for this
                   theme so the backdrop becomes the visible page surface. */
                html, body {
                    background: transparent;
                }
                /* Inline-DOM atmospheric layer. AppHtmlGetAction injects
                   <div class="theme-backdrop"><svg>…nocturne…</svg></div>
                   as the first child of <body>; we position it fixed-cover
                   behind everything else (z-index: -1). pointer-events:none
                   by default so the backdrop doesn't intercept clicks; the
                   moon re-enables them locally for its hover-grow effect. */
                .theme-backdrop {
                    position: fixed;
                    inset: 0;
                    z-index: -1;
                    pointer-events: none;
                    overflow: hidden;
                }
                .theme-backdrop svg {
                    width: 100%;
                    height: 100%;
                    display: block;
                }
                /* Doc-reader column slab is set framework-default (HomingDefault
                   COMPONENT_CSS targets `.st-main:has(.st-doc-meta)`). Maple
                   Bridge only adjusts the fill to be slightly translucent so
                   the nocturne bleeds a hint through the parchment — gives
                   the surface a "lit from behind" feel on the reading page.
                   Same scope: only the doc reader. */
                .st-main:has(.st-doc-meta) {
                    background-color: color-mix(in srgb, var(--color-surface-raised) 92%, transparent);
                }

                /* Inner panes inherit the parchment from .st-main; no per-pane
                   background needed. Padding kept for reading-pane feel. */
                .st-doc {
                    padding: 28px 32px;
                }
                .st-sidebar {
                    padding: 16px;
                }

                /* --- Moon hover plumbing ------------------------------------
                   The backdrop is at z-index:-1; for the moon to receive
                   hover, NOTHING above it in the stacking order can have
                   pointer-events: auto at the cursor position. Two subtleties
                   that bite scoped pointer-events fixes:

                     1. `pointer-events` does NOT inherit. Setting it on a
                        container doesn't propagate to descendants — each
                        `<p>` / `<h1>` retains its default `auto` and catches
                        events even when its parent is set to `none`.

                     2. The body element's bounding box IS the viewport.
                        Even in "visually empty" margins where no content
                        sits, body itself catches hovers before they fall
                        through to a z-index:-1 descendant.

                   Solution — pass through universally with `body, body *`
                   (the universal selector hits every descendant, regardless
                   of inheritance rules; the body rule covers the body box
                   itself). Restore `pointer-events: auto` selectively on
                   user-interactive elements + the moon.

                   Trade-off: in-article text selection is impaired; cards
                   and links still click; keyboard nav unaffected. */
                body, body * {
                    pointer-events: none;
                }
                a, button, input, select, textarea, label,
                .st-header, .st-card, .st-list-item, .st-toc-item,
                .theme-backdrop .mb-moon {
                    pointer-events: auto;
                }

                /* Moon hover-grow.

                   To keep the SAME crescent shape (just bigger), both the
                   moon DISK and the mask SHADOW have to scale together —
                   otherwise the disk grows while the cutout stays fixed and
                   the crescent shape changes. The shadow's offset from the
                   moon (originally +15,-5 in SVG coords) also scales, so the
                   lit sliver stays proportionally the same width.

                   Scale factor 1.5×: r 28→42 and 26→39, shadow centre
                   offsets 15→22.5 and 5→7.5, landing at (162.5, 167.5).

                   :has() lets us drive the shadow (inside the mask defs)
                   off the moon's :hover state — they're sibling-ish in the
                   document tree but :has() lets the backdrop ancestor see
                   the moon's hover and apply rules anywhere under itself. */
                .theme-backdrop .mb-moon {
                    pointer-events: auto;
                    transition: r 240ms ease, filter 240ms ease;
                    cursor: pointer;
                }
                .theme-backdrop .mb-moon-shadow {
                    transition: r 240ms ease, cx 240ms ease, cy 240ms ease;
                }
                .theme-backdrop:has(.mb-moon:hover) .mb-moon {
                    r: 42;
                    filter: drop-shadow(0 0 12px var(--mb-moon, #f5e3b0));
                }
                .theme-backdrop:has(.mb-moon:hover) .mb-moon-shadow {
                    r: 39;
                    cx: 162.5;
                    cy: 167.5;
                }
                """;
    }
}
