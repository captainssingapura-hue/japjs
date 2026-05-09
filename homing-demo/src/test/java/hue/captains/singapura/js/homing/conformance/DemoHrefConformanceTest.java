package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.demo.es.*;

import java.util.List;
import java.util.Set;

class DemoHrefConformanceTest extends HrefConformanceTest {

    @Override
    protected List<DomModule<?>> domModules() {
        return List.of(
                AnimalCell.INSTANCE,
                BobModule.INSTANCE,
                DancingAnimals.INSTANCE,
                SpinningAnimals.INSTANCE,
                MovingAnimal.INSTANCE,
                TurtleDemo.INSTANCE,
                ExtrudedTurtleDemo.INSTANCE,
                DecomposedSvgDemo.INSTANCE,
                ExtrudedSvgDemo.INSTANCE,
                WonderlandDemo.INSTANCE,
                PlatformerBgm.INSTANCE,
                DemoCatalogue.INSTANCE
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Set<Class<? extends DomModule<?>>> allowList() {
        return Set.of(
                // MovingAnimal reads `?theme=` from window.location and writes a new URL
                // for theme switching. This is the legitimate "I need to know the
                // current theme" case — the framework doesn't yet expose theme/locale
                // in the generated `params` const (theme is a reserved key). A clean
                // fix needs kernel support for env-style URL params; out of scope for
                // Step 11. See RFC 0001 Step 11 notes.
                (Class<? extends DomModule<?>>) (Class<?>) MovingAnimal.class
        );
    }
}
