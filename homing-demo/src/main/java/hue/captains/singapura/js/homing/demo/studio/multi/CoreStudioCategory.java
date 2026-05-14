package hue.captains.singapura.js.homing.demo.studio.multi;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.StudioProxy;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;

import java.util.List;

/**
 * L1 category — the Homing framework itself. Holds a {@link StudioProxy}
 * wrapping {@link StudioCatalogue} (the homing-studio's own L0 root).
 */
public record CoreStudioCategory()
        implements L1_Catalogue<MultiStudioHome, CoreStudioCategory> {

    public static final CoreStudioCategory INSTANCE = new CoreStudioCategory();

    @Override public MultiStudioHome parent() { return MultiStudioHome.INSTANCE; }
    @Override public String name()    { return "Core"; }
    @Override public String summary() { return "The Homing framework itself — doctrines, RFCs, journeys, building blocks, releases. The framework dogfooding."; }
    @Override public String badge()   { return "CORE"; }
    @Override public String icon()    { return "📦"; }

    @Override public List<Entry<CoreStudioCategory>> leaves() {
        return List.of(
                Entry.of(this, new StudioProxy<>(
                        StudioCatalogue.INSTANCE,
                        "Homing",
                        "Full Homing framework studio — doctrines, RFCs, journeys, building blocks, releases.",
                        "STUDIO",
                        "🏠"))
        );
    }
}
