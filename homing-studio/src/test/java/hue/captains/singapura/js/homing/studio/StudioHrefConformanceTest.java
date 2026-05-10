package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.HrefConformanceTest;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost;
import hue.captains.singapura.js.homing.studio.es.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;

import java.util.List;
import java.util.Set;

class StudioHrefConformanceTest extends HrefConformanceTest {

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(
                CatalogueAppHost.INSTANCE,
                PlanAppHost.INSTANCE,
                DocBrowser.INSTANCE,
                DocReader.INSTANCE
        );
    }

    @Override
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of(); // No exceptions in studio — everything migrated to nav + href.
    }
}
