package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.DualAudienceSkillsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FirstUserDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ManagedDomOpsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.MethodsOverPropsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.OwnedReferencesDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PlanContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;

/** Sub-catalogue listing the foundational doctrines. */
public record DoctrineCatalogue() implements L1_Catalogue<StudioCatalogue> {

    public static final DoctrineCatalogue INSTANCE = new DoctrineCatalogue();

    @Override public StudioCatalogue parent() { return StudioCatalogue.INSTANCE; }
    @Override public String name()    { return "Doctrines"; }
    @Override public String summary() { return "The rules that hold the design together. Required reading."; }
    @Override public String badge()   { return "DOCTRINE"; }
    @Override public String icon()    { return "📚"; }

    @Override public List<Entry> leaves() {
        return List.of(
                Entry.of(FirstUserDoc.INSTANCE),
                Entry.of(DualAudienceSkillsDoc.INSTANCE),
                Entry.of(PureComponentViewsDoc.INSTANCE),
                Entry.of(MethodsOverPropsDoc.INSTANCE),
                Entry.of(ManagedDomOpsDoc.INSTANCE),
                Entry.of(OwnedReferencesDoc.INSTANCE),
                Entry.of(CatalogueContainerDoc.INSTANCE),
                Entry.of(PlanContainerDoc.INSTANCE),
                Entry.of(WeighedComplexityDoc.INSTANCE)
        );
    }
}
