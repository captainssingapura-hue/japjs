package hue.captains.singapura.js.homing.studio.base.app;

/**
 * Serialises {@link CatalogueData} to a JSON literal embeddable in JavaScript.
 * Hand-rolled to keep {@code homing-studio-base} free of a JSON dependency.
 */
public final class CatalogueJson {

    private CatalogueJson() {}

    public static String of(CatalogueData c) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"kicker\":")  .append(jstr(c.kicker())).append(",");
        sb.append("\"title\":")   .append(jstr(c.title())).append(",");
        sb.append("\"subtitle\":").append(jstr(c.subtitle())).append(",");
        sb.append("\"footer\":")  .append(jstr(c.footer())).append(",");
        sb.append("\"crumbs\":[");
        boolean firstC = true;
        for (CatalogueCrumb cr : c.crumbs()) {
            if (!firstC) sb.append(",");
            firstC = false;
            sb.append("{\"text\":").append(jstr(cr.text()))
              .append(",\"href\":").append(jstr(cr.href())).append("}");
        }
        sb.append("],\"sections\":[");
        boolean firstS = true;
        for (CatalogueSection s : c.sections()) {
            if (!firstS) sb.append(",");
            firstS = false;
            sb.append("{\"title\":").append(jstr(s.title()))
              .append(",\"tileStyle\":").append(jstr(s.tileStyle().name()))
              .append(",\"tiles\":[");
            boolean firstT = true;
            for (CatalogueTile t : s.tiles()) {
                if (!firstT) sb.append(",");
                firstT = false;
                sb.append(tile(t));
            }
            sb.append("]}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private static String tile(CatalogueTile t) {
        // badgeClass is the kebab-name of a CssClass record (e.g.
        // "st-badge-rfc"); the renderer turns it back into a typed handle
        // via css.cls(...).
        return "{\"href\":"       + jstr(t.href())
             + ",\"label\":"      + jstr(t.label())
             + ",\"desc\":"       + jstr(t.desc())
             + ",\"icon\":"       + jstr(t.icon())
             + ",\"badge\":"      + jstr(t.badge())
             + ",\"badgeClass\":" + jstr(t.badgeClass())
             + ",\"featured\":"   + t.featured()
             + "}";
    }

    static String jstr(String v) {
        if (v == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"'  -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
