package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.CdnFreeConformanceTest;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost;
import hue.captains.singapura.js.homing.studio.es.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;

import java.util.List;

class StudioCdnFreeConformanceTest extends CdnFreeConformanceTest {

    @Override
    protected List<EsModule<?>> esModules() {
        return List.of(
                CatalogueAppHost.INSTANCE,
                PlanAppHost.INSTANCE,
                DocBrowser.INSTANCE,
                DocReader.INSTANCE,

                // Bundled — auto-skipped by the base class
                MarkedJs.INSTANCE
        );
    }
}
