package hue.captains.singapura.japjs.conformance;

import hue.captains.singapura.japjs.core.DomModule;
import hue.captains.singapura.japjs.demo.es.*;

import java.util.List;
import java.util.Set;

class DemoCssConformanceTest extends CssConformanceTest {

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(
                AnimalCell.INSTANCE,
                BobModule.INSTANCE,
                DancingAnimals.INSTANCE,
                SpinningAnimals.INSTANCE,
                MovingAnimal.INSTANCE,
                TurtleDemo.INSTANCE,
                WonderlandDemo.INSTANCE,
                PlatformerBgm.INSTANCE,
                PitchDeck.INSTANCE,
                PitchDeckBgm.INSTANCE,
                DemoCatalogue.INSTANCE
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of(
                (Class<? extends DomModule<?>>) (Class<?>) AnimalCell.class
        );
    }
}
