package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.core.CssGroupImpl;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.demo.es.AnimalCell;
import hue.captains.singapura.js.homing.demo.es.BobModule;
import hue.captains.singapura.js.homing.demo.es.DancingAnimals;
import hue.captains.singapura.js.homing.demo.es.DemoCatalogue;
import hue.captains.singapura.js.homing.demo.es.MovingAnimal;
import hue.captains.singapura.js.homing.demo.es.PitchDeck;
import hue.captains.singapura.js.homing.demo.es.PitchDeckBgm;
import hue.captains.singapura.js.homing.demo.es.PlatformerBgm;
import hue.captains.singapura.js.homing.demo.es.SpinningAnimals;
import hue.captains.singapura.js.homing.demo.es.TurtleDemo;
import hue.captains.singapura.js.homing.demo.es.WonderlandDemo;
import hue.captains.singapura.js.homing.demo.theme.DemoCssGroupImplRegistry;
import hue.captains.singapura.js.homing.demo.theme.DemoDefault;

import java.util.List;

class DemoCssGroupImplConsistencyTest extends CssGroupImplConsistencyTest {

    @Override
    protected List<CssGroupImpl<?, ?>> impls() {
        return DemoCssGroupImplRegistry.ALL;
    }

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

    @Override
    protected String defaultThemeSlug() {
        return DemoDefault.INSTANCE.slug();
    }
}
