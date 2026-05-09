package hue.captains.singapura.js.homing.studio.base.app;

/**
 * One doc in a {@link DocBrowserData} list.
 *
 * @param path        classpath-relative markdown path, becomes {@code ?path=…} on the reader
 * @param title       card heading (h3)
 * @param summary     card body paragraph
 * @param category    filter key + badge text (e.g. {@code "RFC"}, {@code "DOCTRINE"})
 * @param catLabel    filter button text + section title (e.g. {@code "RFCs"}, {@code "Doctrines"})
 * @param badgeClass  kebab-name of a CssClass record for the badge variant; null = default
 */
public record DocBrowserEntry(
        String path,
        String title,
        String summary,
        String category,
        String catLabel,
        String badgeClass
) {}
