package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.core.SvgRef;

import java.util.Objects;

/**
 * Per-installation brand configuration provided alongside the {@link CatalogueRegistry}
 * at boot. Replaces the previous hardcoded {@code "Homing · studio"} default in
 * {@code CatalogueAppModule.brandLabel()}.
 *
 * <p>Per <a href="../../../../../../../../../../docs/rfcs/Rfc0005Doc.md">RFC 0005</a>
 * D2, the studio brand is a per-installation configuration object passed at boot, not
 * a per-catalogue field, not a hardcoded default. Renderer reads from the registry at
 * render time; no per-catalogue indirection.</p>
 *
 * @param label    the human-readable brand label shown in the studio header
 *                 (e.g. {@code "Homing · studio"} or {@code "Acme Studio"})
 * @param homeApp  the catalogue class the brand link navigates to (typically the studio's
 *                 root catalogue, e.g. {@code StudioCatalogue.class})
 * @param logo     optional typed SVG logo rendered at the start of the brand link.
 *                 {@code null} → the framework's default coloured-square dot.
 *                 The same {@code SvgRef} can be reused anywhere else a typed
 *                 SVG glyph is wanted (custom markers, badges, …).
 *
 * @since RFC 0005
 */
public record StudioBrand(String label, Class<? extends Catalogue> homeApp, SvgRef<?> logo) {

    public StudioBrand {
        Objects.requireNonNull(label,   "label");
        Objects.requireNonNull(homeApp, "homeApp");
        if (label.isBlank()) {
            throw new IllegalArgumentException("StudioBrand label must not be blank");
        }
        // logo nullable — installations without a custom logo fall back to the dot.
    }

    /** Back-compat — installations that don't ship a logo yet. */
    public StudioBrand(String label, Class<? extends Catalogue> homeApp) {
        this(label, homeApp, null);
    }
}
