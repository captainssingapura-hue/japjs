package hue.captains.singapura.js.homing.conformance;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.demo.es.AnimalCell;
import hue.captains.singapura.js.homing.demo.es.BobModule;
import hue.captains.singapura.js.homing.demo.es.DancingAnimals;
import hue.captains.singapura.js.homing.demo.es.DecomposedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.DemoCatalogue;
import hue.captains.singapura.js.homing.demo.es.ExtrudedSvgDemo;
import hue.captains.singapura.js.homing.demo.es.ExtrudedTurtleDemo;
import hue.captains.singapura.js.homing.demo.es.JumpPhysics;
import hue.captains.singapura.js.homing.demo.es.MovingAnimal;
import hue.captains.singapura.js.homing.demo.es.PlatformEngine;
import hue.captains.singapura.js.homing.demo.es.PlatformerBgm;
import hue.captains.singapura.js.homing.demo.es.SpinningAnimals;
import hue.captains.singapura.js.homing.demo.es.SvgDecomposer;
import hue.captains.singapura.js.homing.demo.es.SvgExtruder;
import hue.captains.singapura.js.homing.demo.es.TurtleDemo;
import hue.captains.singapura.js.homing.demo.es.WonderlandDemo;

import java.util.List;

/**
 * Enforces the universal view doctrines (Pure-Component Views + Owned
 * References) across every JS-bearing module in {@code homing-demo}.
 *
 * <p>The list below is the complete inventory of demo {@link EsModule}s.
 * Adding a new demo requires registering its module here; the test fails
 * loudly on any HTML literal, {@code innerHTML} write, or DOM lookup it
 * adds.</p>
 *
 * <p>Allowlist is empty — no demo module is exempt. If a future addition
 * requires an exemption, override {@code allowList()} with the offending
 * class and link a tracking ticket in commentary.</p>
 */
class DemoDoctrineConformanceTest extends DoctrineConformanceTest {

    @Override
    protected List<EsModule<?>> esModules() {
        return List.<EsModule<?>>of(
                AnimalCell.INSTANCE,
                BobModule.INSTANCE,
                DancingAnimals.INSTANCE,
                DecomposedSvgDemo.INSTANCE,
                DemoCatalogue.INSTANCE,
                ExtrudedSvgDemo.INSTANCE,
                ExtrudedTurtleDemo.INSTANCE,
                JumpPhysics.INSTANCE,
                MovingAnimal.INSTANCE,
                PlatformEngine.INSTANCE,
                PlatformerBgm.INSTANCE,
                SpinningAnimals.INSTANCE,
                SvgDecomposer.INSTANCE,
                SvgExtruder.INSTANCE,
                TurtleDemo.INSTANCE,
                WonderlandDemo.INSTANCE
        );
    }
}
