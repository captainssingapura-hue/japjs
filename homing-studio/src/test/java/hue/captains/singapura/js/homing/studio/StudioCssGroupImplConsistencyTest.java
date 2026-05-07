package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.CssGroupImplConsistencyTest;
import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.studio.base.theme.CssGroupImplRegistry;
import hue.captains.singapura.js.homing.studio.base.theme.HomingDefault;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.es.DocReader;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.rename.RenamePlan;
import hue.captains.singapura.js.homing.studio.rename.RenameStep;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001Plan;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001Step;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002Plan;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002Step;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1Plan;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1Step;

import java.util.List;

class StudioCssGroupImplConsistencyTest extends CssGroupImplConsistencyTest {

    @Override
    protected List<CssGroupImpl<?, ?>> impls() {
        return CssGroupImplRegistry.ALL;
    }

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(
                StudioCatalogue.INSTANCE,
                JourneysCatalogue.INSTANCE,
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
    protected String defaultThemeSlug() {
        return HomingDefault.INSTANCE.slug();
    }
}
