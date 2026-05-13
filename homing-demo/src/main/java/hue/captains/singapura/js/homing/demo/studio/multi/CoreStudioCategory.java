package hue.captains.singapura.js.homing.demo.studio.multi;

import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;

import java.util.List;

/**
 * L1 category — the Homing framework itself. Holds the {@link StudioCatalogue}
 * Navigable tile. The framework dogfoods itself, served alongside its own
 * sibling categories (Learning / Tooling) under the umbrella.
 */
public record CoreStudioCategory() implements L1_Catalogue<MultiStudioHome> {

    public static final CoreStudioCategory INSTANCE = new CoreStudioCategory();

    @Override public MultiStudioHome parent() { return MultiStudioHome.INSTANCE; }
    @Override public String name()    { return "Core"; }
    @Override public String summary() { return "The Homing framework itself — doctrines, RFCs, journeys, building blocks, releases. The framework dogfooding."; }
    @Override public String badge()   { return "CORE"; }
    @Override public String icon()    { return "📦"; }

    @Override public List<Entry> leaves() {
        return List.of(
                Entry.of(new Navigable<>(
                        CatalogueAppHost.INSTANCE,
                        new CatalogueAppHost.Params(StudioCatalogue.class.getName()),
                        "🏠 Homing",
                        "Full Homing framework studio — doctrines, RFCs, journeys, building blocks, releases."))
        );
    }
}
