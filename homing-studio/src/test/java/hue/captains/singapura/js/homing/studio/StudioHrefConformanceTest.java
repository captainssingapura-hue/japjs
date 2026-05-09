package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.HrefConformanceTest;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.es.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;
import hue.captains.singapura.js.homing.studio.rename.RenamePlan;
import hue.captains.singapura.js.homing.studio.rename.RenameStep;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001Plan;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001Step;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002Plan;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002Step;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1Plan;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1Step;

import java.util.List;
import java.util.Set;

class StudioHrefConformanceTest extends HrefConformanceTest {

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(
                StudioCatalogue.INSTANCE,
                JourneysCatalogue.INSTANCE,
                BuildingBlocksCatalogue.INSTANCE,
                DocBrowser.INSTANCE,
                DocReader.INSTANCE,
                Rfc0001Plan.INSTANCE,
                Rfc0001Step.INSTANCE,
                Rfc0002Plan.INSTANCE,
                Rfc0002Step.INSTANCE,
                Rfc0002Ext1Plan.INSTANCE,
                Rfc0002Ext1Step.INSTANCE,
                RenamePlan.INSTANCE,
                RenameStep.INSTANCE
        );
    }

    @Override
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of(); // No exceptions in studio — everything migrated to nav + href.
    }
}
