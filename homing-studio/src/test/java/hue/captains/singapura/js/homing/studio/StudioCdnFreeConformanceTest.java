package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.CdnFreeConformanceTest;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.es.DocReader;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.rename.RenamePlan;
import hue.captains.singapura.js.homing.studio.rename.RenameStep;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001Plan;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001Step;

import java.util.List;

class StudioCdnFreeConformanceTest extends CdnFreeConformanceTest {

    @Override
    protected List<EsModule<?>> esModules() {
        return List.of(
                StudioCatalogue.INSTANCE,
                DocBrowser.INSTANCE,
                DocReader.INSTANCE,
                Rfc0001Plan.INSTANCE,
                Rfc0001Step.INSTANCE,
                RenamePlan.INSTANCE,
                RenameStep.INSTANCE,

                // Bundled — auto-skipped by the base class
                MarkedJs.INSTANCE
        );
    }
}
