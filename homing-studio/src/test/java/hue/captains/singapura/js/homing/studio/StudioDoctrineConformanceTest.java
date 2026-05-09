package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.DoctrineConformanceTest;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.es.DoctrineCatalogue;
import hue.captains.singapura.js.homing.studio.es.BuildingBlocksCatalogue;
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
import java.util.Set;

/**
 * Enforces the universal view doctrines (Pure-Component Views + Owned
 * References) across the studio's JS-bearing modules.
 *
 * <p>All studio modules are doctrine-compliant — top-level catalogues /
 * reader compose the shared {@code StudioElements} builders directly
 * (Header, Card, Pill, Section, Footer); the four plan/step trackers
 * (Rename, RFC 0001, RFC 0002, RFC 0002-ext1) inherit auto-generated JS
 * from {@code PlanAppModule} / {@code PlanStepAppModule} and never author
 * HTML strings.</p>
 *
 * <p>The allowlist is empty — view doctrines apply universally. If a future
 * exemption is needed, override {@code allowList()} with the offending
 * class and link a tracking ticket in commentary.</p>
 */
class StudioDoctrineConformanceTest extends DoctrineConformanceTest {

    @Override
    protected List<EsModule<?>> esModules() {
        return List.<EsModule<?>>of(
                // Top-level catalogue / reader modules — compose StudioElements directly.
                StudioCatalogue.INSTANCE,
                JourneysCatalogue.INSTANCE,
                BuildingBlocksCatalogue.INSTANCE,
                DoctrineCatalogue.INSTANCE,
                DocBrowser.INSTANCE,
                DocReader.INSTANCE,
                // Plan trackers — auto-generated JS via PlanAppModule + PlanRenderer.
                // The base class skips modules that have no .js resource (the
                // tracker pattern emits its body via SelfContent), so the test
                // is effectively a no-op for these. Listed anyway so any
                // future hand-written tracker JS gets caught.
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
    protected Set<Class<? extends EsModule<?>>> allowList() {
        // All four trackers (Rename / Rfc0001 / Rfc0002 / Rfc0002Ext1) are
        // migrated to the kit (PlanAppModule + PlanRenderer, auto-generated
        // JS). Allowlist is empty — view doctrines apply universally across
        // every JS-bearing module in the studio.
        return Set.of();
    }
}
