package hue.captains.singapura.js.homing.studio.base.app;

/** Serialises {@link DocBrowserData} to a JSON literal embeddable in JS. */
public final class DocBrowserJson {

    private DocBrowserJson() {}

    public static String of(DocBrowserData d) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"kicker\":")               .append(CatalogueJson.jstr(d.kicker())).append(",");
        sb.append("\"title\":")                .append(CatalogueJson.jstr(d.title())).append(",");
        sb.append("\"subtitle\":")             .append(CatalogueJson.jstr(d.subtitle())).append(",");
        sb.append("\"footer\":")               .append(CatalogueJson.jstr(d.footer())).append(",");
        sb.append("\"readerAppSimpleName\":")  .append(CatalogueJson.jstr(d.readerAppSimpleName())).append(",");
        sb.append("\"crumbs\":[");
        boolean firstC = true;
        for (CatalogueCrumb cr : d.crumbs()) {
            if (!firstC) sb.append(",");
            firstC = false;
            sb.append("{\"text\":").append(CatalogueJson.jstr(cr.text()))
              .append(",\"href\":").append(CatalogueJson.jstr(cr.href())).append("}");
        }
        sb.append("],\"docs\":[");
        boolean firstD = true;
        for (DocBrowserEntry e : d.docs()) {
            if (!firstD) sb.append(",");
            firstD = false;
            sb.append("{\"path\":")        .append(CatalogueJson.jstr(e.path()))
              .append(",\"title\":")       .append(CatalogueJson.jstr(e.title()))
              .append(",\"summary\":")     .append(CatalogueJson.jstr(e.summary()))
              .append(",\"category\":")    .append(CatalogueJson.jstr(e.category()))
              .append(",\"catLabel\":")    .append(CatalogueJson.jstr(e.catLabel()))
              .append(",\"badgeClass\":")  .append(CatalogueJson.jstr(e.badgeClass()))
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }
}
