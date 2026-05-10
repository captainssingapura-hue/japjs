package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.ManagerInjectionConformanceTest;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;

import java.util.List;

/**
 * Pins the rule that no studio module redeclares a framework-auto-injected
 * identifier ({@code css} / {@code href} / any {@code ManagerInjector} bind name).
 *
 * <p>Listing the four host AppModules is sufficient — the conformance walks the
 * import graph transitively, so renderers ({@code PlanHostRenderer},
 * {@code CatalogueHostRenderer}, etc.) and shared element modules
 * ({@code StudioElements}) are scanned automatically.</p>
 */
class StudioManagerInjectionConformanceTest extends ManagerInjectionConformanceTest {

    @Override
    protected List<EsModule<?>> esModules() {
        return List.of(
                CatalogueAppHost.INSTANCE,
                PlanAppHost.INSTANCE,
                DocBrowser.INSTANCE,
                DocReader.INSTANCE
        );
    }
}
