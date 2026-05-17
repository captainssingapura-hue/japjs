package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L3_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ExplicitOverImplicitDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;

/**
 * L2 sub-catalogue of {@link DoctrineCatalogue} — doctrines about how the
 * codebase itself is structured. No public static methods anywhere
 * (Functional Objects); the cost of code is multi-dimensional, not
 * line-counted (Weighed Complexity). Together they govern what the
 * framework's source <i>looks like</i>.
 */
public record CodeDoctrinesCatalogue()
        implements L3_Catalogue<DoctrineCatalogue, CodeDoctrinesCatalogue> {

    public static final CodeDoctrinesCatalogue INSTANCE = new CodeDoctrinesCatalogue();

    @Override public DoctrineCatalogue parent() { return DoctrineCatalogue.INSTANCE; }
    @Override public String name()    { return "Code Discipline"; }
    @Override public String summary() { return "How the codebase itself is structured. No public statics anywhere; cost is multi-dimensional, not line-counted. Together they govern what the framework's source looks like."; }
    @Override public String badge()   { return "DOCTRINE"; }
    @Override public String icon()    { return "⚙️"; }

    @Override public List<Entry<CodeDoctrinesCatalogue>> leaves() {
        return List.of(
                Entry.of(this, FunctionalObjectsDoc.INSTANCE),
                Entry.of(this, WeighedComplexityDoc.INSTANCE),
                Entry.of(this, ExplicitOverImplicitDoc.INSTANCE)
        );
    }
}
