package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.DualAudienceSkillsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FirstUserDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ManagedDomOpsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.MethodsOverPropsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.OwnedReferencesDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PlanContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;

/** Sub-catalogue listing the foundational doctrines. */
public record DoctrineCatalogue() implements L1_Catalogue<StudioCatalogue, DoctrineCatalogue> {

    public static final DoctrineCatalogue INSTANCE = new DoctrineCatalogue();

    @Override public StudioCatalogue parent() { return StudioCatalogue.INSTANCE; }
    @Override public String name()    { return "Doctrines"; }
    @Override public String summary() { return "The rules that hold the design together. Required reading."; }
    @Override public String badge()   { return "DOCTRINE"; }
    @Override public String icon()    { return "📚"; }

    @Override public List<Entry<DoctrineCatalogue>> leaves() {
        return List.of(
                Entry.of(this, FirstUserDoc.INSTANCE),
                Entry.of(this, DualAudienceSkillsDoc.INSTANCE),
                Entry.of(this, PureComponentViewsDoc.INSTANCE),
                Entry.of(this, MethodsOverPropsDoc.INSTANCE),
                Entry.of(this, ManagedDomOpsDoc.INSTANCE),
                Entry.of(this, OwnedReferencesDoc.INSTANCE),
                Entry.of(this, CatalogueContainerDoc.INSTANCE),
                Entry.of(this, PlanContainerDoc.INSTANCE),
                Entry.of(this, WeighedComplexityDoc.INSTANCE),
                Entry.of(this, FunctionalObjectsDoc.INSTANCE)
        );
    }
}
