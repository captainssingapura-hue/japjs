package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.demo.es.*;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.libs.ThreeJs;
import hue.captains.singapura.js.homing.libs.ThreeJsSvgLoader;
import hue.captains.singapura.js.homing.libs.ToneJs;

import java.util.List;

class DemoCdnFreeConformanceTest extends CdnFreeConformanceTest {

    @Override
    protected List<EsModule<?>> esModules() {
        return List.of(
                // Demo apps — must not import from any CDN
                DemoCatalogue.INSTANCE,
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
                ExtrudedTurtleDemo.INSTANCE,
                DecomposedSvgDemo.INSTANCE,
                ExtrudedSvgDemo.INSTANCE,
                SvgDecomposer.INSTANCE,
                SvgExtruder.INSTANCE,
                JumpPhysics.INSTANCE,

                // Bundled 3rd-party libs — auto-skipped by CdnFreeConformanceTest
                // since they're BundledExternalModules. Listed here so any future
                // demoted-to-ExternalModule case would be caught.
                MarkedJs.INSTANCE,
                ThreeJs.INSTANCE,
                ThreeJsSvgLoader.INSTANCE,
                ToneJs.INSTANCE
        );
    }
}
