package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.HrefConformanceTest;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.es.DocReader;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.rename.RenamePlan;
import hue.captains.singapura.js.homing.studio.rename.RenameStep;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001Plan;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001Step;

import java.util.List;
import java.util.Set;

class StudioHrefConformanceTest extends HrefConformanceTest {

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(
                StudioCatalogue.INSTANCE,
                DocBrowser.INSTANCE,
                DocReader.INSTANCE,
                Rfc0001Plan.INSTANCE,
                Rfc0001Step.INSTANCE,
                RenamePlan.INSTANCE,
                RenameStep.INSTANCE
        );
    }

    @Override
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of(); // No exceptions in studio — everything migrated to nav + href.
    }
}
