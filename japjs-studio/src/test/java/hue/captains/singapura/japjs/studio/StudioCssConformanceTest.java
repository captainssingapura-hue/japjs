package hue.captains.singapura.japjs.studio;

import hue.captains.singapura.japjs.conformance.CssConformanceTest;
import hue.captains.singapura.japjs.core.DomModule;
import hue.captains.singapura.japjs.studio.es.DocBrowser;
import hue.captains.singapura.japjs.studio.es.DocReader;
import hue.captains.singapura.japjs.studio.es.StudioCatalogue;
import hue.captains.singapura.japjs.studio.rfc0001.Rfc0001Plan;
import hue.captains.singapura.japjs.studio.rfc0001.Rfc0001Step;

import java.util.List;
import java.util.Set;

class StudioCssConformanceTest extends CssConformanceTest {

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(
                StudioCatalogue.INSTANCE,
                DocBrowser.INSTANCE,
                DocReader.INSTANCE,
                Rfc0001Plan.INSTANCE,
                Rfc0001Step.INSTANCE
        );
    }

    @Override
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of();
    }
}
