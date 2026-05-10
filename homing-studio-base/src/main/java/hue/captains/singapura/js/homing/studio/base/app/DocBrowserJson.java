package hue.captains.singapura.js.homing.studio.base.app;

/** Serialises {@link DocBrowserData} to a JSON literal embeddable in JS. */
public final class DocBrowserJson {

    private DocBrowserJson() {}

    public static String of(DocBrowserData d) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"kicker\":")               .append(jstr(d.kicker())).append(",");
        sb.append("\"title\":")                .append(jstr(d.title())).append(",");
        sb.append("\"subtitle\":")             .append(jstr(d.subtitle())).append(",");
        sb.append("\"footer\":")               .append(jstr(d.footer())).append(",");
        sb.append("\"readerAppSimpleName\":")  .append(jstr(d.readerAppSimpleName())).append(",");
        sb.append("\"crumbs\":[");
        boolean firstC = true;
        for (CatalogueCrumb cr : d.crumbs()) {
            if (!firstC) sb.append(",");
            firstC = false;
            sb.append("{\"text\":").append(jstr(cr.text()))
              .append(",\"href\":").append(jstr(cr.href())).append("}");
        }
        sb.append("],\"docs\":[");
        boolean firstD = true;
        for (DocBrowserEntry e : d.docs()) {
            if (!firstD) sb.append(",");
            firstD = false;
            // RFC 0004: emit doc UUID + title/summary/category sourced from the typed Doc.
            sb.append("{\"id\":")          .append(jstr(e.doc().uuid().toString()))
              .append(",\"title\":")       .append(jstr(e.doc().title()))
              .append(",\"summary\":")     .append(jstr(e.doc().summary()))
              .append(",\"category\":")    .append(jstr(e.doc().category()))
              .append(",\"catLabel\":")    .append(jstr(e.catLabel()))
              .append(",\"badgeClass\":")  .append(jstr(e.badgeClass()))
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    /** RFC 8259 string escape — single-line; no newlines in metadata. */
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
