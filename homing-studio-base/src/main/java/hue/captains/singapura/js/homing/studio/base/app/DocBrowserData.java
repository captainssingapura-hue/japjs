package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Typed data shape for a {@link DocBrowserAppModule}. The renderer turns this
 * into a searchable / category-filterable card grid.
 *
 * <p>Each {@link DocBrowserEntry} carries enough metadata to build the
 * reader URL and the card chrome: path (becomes {@code ?path=…} on the
 * reader), title (h3), summary (paragraph), category (filter key + badge
 * text), catLabel (filter button text + section title), and an optional
 * badgeClass for category-specific colouring.</p>
 */
public record DocBrowserData(
        String kicker,
        String title,
        String subtitle,
        List<CatalogueCrumb> crumbs,
        String readerAppSimpleName,
        List<DocBrowserEntry> docs,
        String footer
) {}
