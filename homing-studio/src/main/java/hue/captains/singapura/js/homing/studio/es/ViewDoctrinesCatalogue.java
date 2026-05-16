package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ManagedDomOpsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.MethodsOverPropsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.OwnedReferencesDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;

import java.util.List;

/**
 * L2 sub-catalogue of {@link DoctrineCatalogue} — doctrines about how the
 * client-side view layer is authored. Pure-function rendering, method-shaped
 * component APIs, managed DOM mutation, and explicit element ownership form
 * one coherent stance: the view is its data, the DOM is owned, mutation is
 * disciplined.
 */
public record ViewDoctrinesCatalogue()
        implements L2_Catalogue<DoctrineCatalogue, ViewDoctrinesCatalogue> {

    public static final ViewDoctrinesCatalogue INSTANCE = new ViewDoctrinesCatalogue();

    @Override public DoctrineCatalogue parent() { return DoctrineCatalogue.INSTANCE; }
    @Override public String name()    { return "View Architecture"; }
    @Override public String summary() { return "How the client-side UI is authored. Pure-function rendering, method-shaped component APIs, managed DOM mutation, explicit element ownership — one coherent stance on the view layer."; }
    @Override public String badge()   { return "DOCTRINE"; }
    @Override public String icon()    { return "🎨"; }

    @Override public List<Entry<ViewDoctrinesCatalogue>> leaves() {
        return List.of(
                Entry.of(this, PureComponentViewsDoc.INSTANCE),
                Entry.of(this, MethodsOverPropsDoc.INSTANCE),
                Entry.of(this, ManagedDomOpsDoc.INSTANCE),
                Entry.of(this, OwnedReferencesDoc.INSTANCE)
        );
    }
}
