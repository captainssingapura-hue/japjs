package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Typed data shape for a {@link CatalogueAppModule}. The renderer turns this
 * into the standard catalogue page: header → kicker / title / subtitle →
 * one or more sections of tiles → optional footer.
 *
 * <p>Tiles come in two flavours, picked per-section by {@link CatalogueSection#tileStyle()}:
 * <ul>
 *   <li>{@link TileStyle#PILL} — icon + label + desc launcher tile (used by
 *       Studio's home page and the Journeys catalogue).</li>
 *   <li>{@link TileStyle#CARD} — title + summary + badge + open-link
 *       (used by the Doctrine catalogue and as the default for grouped lists).</li>
 * </ul></p>
 *
 * <p>Inline {@code `code`} spans (markdown-style backticks) in {@link #footer()}
 * become {@code <code>} elements at render time. No other formatting is
 * supported in the footer string — links and emphasis are deliberately out
 * of scope to keep the data shape simple. If a catalogue needs richer
 * footers, render a {@code DocReader} alongside it.</p>
 */
public record CatalogueData(
        String kicker,
        String title,
        String subtitle,
        List<CatalogueCrumb> crumbs,
        List<CatalogueSection> sections,
        String footer
) {
    /** Convenience: single "Home" crumb (current page), no footer. */
    public CatalogueData(String kicker, String title, String subtitle, List<CatalogueSection> sections) {
        this(kicker, title, subtitle, List.of(new CatalogueCrumb("Home", null)), sections, null);
    }
}
