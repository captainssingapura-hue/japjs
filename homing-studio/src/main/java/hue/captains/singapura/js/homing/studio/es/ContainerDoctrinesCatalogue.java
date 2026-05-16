package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PlanContainerDoc;

import java.util.List;

/**
 * L2 sub-catalogue of {@link DoctrineCatalogue} — doctrines that express
 * the framework's "open set, closed shape" container pattern. Catalogues
 * and Plans are the same idea at different scales: typed ordered containers
 * with intrinsic identity, structure-only data, renderer-owned presentation.
 */
public record ContainerDoctrinesCatalogue()
        implements L2_Catalogue<DoctrineCatalogue, ContainerDoctrinesCatalogue> {

    public static final ContainerDoctrinesCatalogue INSTANCE = new ContainerDoctrinesCatalogue();

    @Override public DoctrineCatalogue parent() { return DoctrineCatalogue.INSTANCE; }
    @Override public String name()    { return "Container Patterns"; }
    @Override public String summary() { return "Open set, closed shape. Catalogues and Plans are the same idea at different scales — typed ordered containers with intrinsic identity, structure-only data, and renderer-owned presentation."; }
    @Override public String badge()   { return "DOCTRINE"; }
    @Override public String icon()    { return "📦"; }

    @Override public List<Entry<ContainerDoctrinesCatalogue>> leaves() {
        return List.of(
                Entry.of(this, CatalogueContainerDoc.INSTANCE),
                Entry.of(this, PlanContainerDoc.INSTANCE)
        );
    }
}
