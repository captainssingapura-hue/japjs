package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FirstUserDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ManagedDomOpsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.MethodsOverPropsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.OwnedReferencesDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PlanContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;

import java.util.List;

/** Sub-catalogue listing the foundational doctrines. */
public record DoctrineCatalogue() implements Catalogue {

    public static final DoctrineCatalogue INSTANCE = new DoctrineCatalogue();

    @Override public String name()    { return "Doctrines"; }
    @Override public String summary() { return "The rules that hold the design together. Required reading."; }

    @Override public List<Entry> entries() {
        return List.of(
                Entry.of(FirstUserDoc.INSTANCE),
                Entry.of(PureComponentViewsDoc.INSTANCE),
                Entry.of(MethodsOverPropsDoc.INSTANCE),
                Entry.of(ManagedDomOpsDoc.INSTANCE),
                Entry.of(OwnedReferencesDoc.INSTANCE),
                Entry.of(CatalogueContainerDoc.INSTANCE),
                Entry.of(PlanContainerDoc.INSTANCE)
        );
    }
}
