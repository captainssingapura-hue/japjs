package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.CssGroupImplConsistencyTest;
import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.studio.base.theme.CssGroupImplRegistry;
import hue.captains.singapura.js.homing.studio.base.theme.HomingDefault;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.es.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost;

import java.util.List;

class StudioCssGroupImplConsistencyTest extends CssGroupImplConsistencyTest {

    @Override
    protected List<CssGroupImpl<?, ?>> impls() {
        return CssGroupImplRegistry.ALL;
    }

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
    protected String defaultThemeSlug() {
        return HomingDefault.INSTANCE.slug();
    }
}
