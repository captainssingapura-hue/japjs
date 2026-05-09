package hue.captains.singapura.js.homing.studio.base.app;

/**
 * One tile in a {@link CatalogueSection}.
 *
 * <p>The kit uses a single record for both pill and card flavours so a section
 * can switch render style without rewriting its data. Per-flavour fields:</p>
 *
 * <table>
 *   <tr><th>Field</th><th>Pill use</th><th>Card use</th></tr>
 *   <tr><td>{@code href}</td>     <td>link target</td>      <td>link target</td></tr>
 *   <tr><td>{@code label}</td>    <td>main label</td>       <td>title (h3)</td></tr>
 *   <tr><td>{@code desc}</td>     <td>description text</td> <td>summary paragraph</td></tr>
 *   <tr><td>{@code icon}</td>     <td>1-char icon</td>      <td>(unused)</td></tr>
 *   <tr><td>{@code badge}</td>    <td>(unused)</td>         <td>badge text (e.g. "RFC", "DOCTRINE 01")</td></tr>
 *   <tr><td>{@code badgeClass}</td><td>(unused)</td>        <td>CSS class for the badge variant</td></tr>
 *   <tr><td>{@code featured}</td> <td>dark pill variant</td><td>(unused — set by section)</td></tr>
 * </table>
 *
 * <p>Pass {@code null} for fields that don't apply to your tile style.
 * Convenience factories below cover the common cases.</p>
 */
public record CatalogueTile(
        String href,
        String label,
        String desc,
        String icon,
        String badge,
        String badgeClass,
        boolean featured
) {
    /** Pill tile — icon + label + desc, optionally featured (dark). */
    public static CatalogueTile pill(String href, String icon, String label, String desc, boolean featured) {
        return new CatalogueTile(href, label, desc, icon, null, null, featured);
    }

    /** Card tile — title + summary + badge. */
    public static CatalogueTile card(String href, String title, String summary, String badge, String badgeClass) {
        return new CatalogueTile(href, title, summary, null, badge, badgeClass, false);
    }
}
