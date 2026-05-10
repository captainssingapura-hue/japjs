package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.js.homing.studio.base.Doc;

/**
 * One doc in a {@link DocBrowserData} list.
 *
 * <p>Per RFC 0004, the entry references a typed {@link Doc} — title / summary / category
 * / wire identity all sourced from the Doc itself. The {@code catLabel} (the longer
 * human-readable name shown on filter buttons and section headings) and {@code badgeClass}
 * stay on the entry as browser-display concerns: a Doc's category is intrinsic, but how
 * the browser labels and styles its filter chip is the browser's call.</p>
 *
 * @param doc        the typed Doc reference; identity via {@code doc.uuid()}
 * @param catLabel   filter button text and section title (e.g. {@code "RFCs"})
 * @param badgeClass kebab-name of a CssClass record for the badge variant
 */
public record DocBrowserEntry(
        Doc doc,
        String catLabel,
        String badgeClass
) {}
