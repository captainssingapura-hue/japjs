package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.DocConformanceTest;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.es.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;

import java.util.List;

/**
 * RFC 0004 + 0004-ext1 — exercises every public Doc contributed by the studio's
 * doc providers. Per RFC 0005 the providers are listed explicitly (catalogues are
 * no longer AppModules and aren't reachable through SimpleAppResolver).
 */
class StudioDocConformanceTest extends DocConformanceTest {

    @Override
    protected List<DocProvider> docProviders() {
        return List.of(
                DocBrowser.INSTANCE,
                BuildingBlocksCatalogue.INSTANCE
        );
    }
}
