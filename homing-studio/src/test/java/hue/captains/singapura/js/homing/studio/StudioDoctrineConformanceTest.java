package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.conformance.DoctrineConformanceTest;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.es.DoctrineCatalogue;
import hue.captains.singapura.js.homing.studio.es.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost;

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
                CatalogueAppHost.INSTANCE,
                PlanAppHost.INSTANCE,
                // Top-level catalogue / reader modules — compose StudioElements directly.
                DocBrowser.INSTANCE,
                DocReader.INSTANCE
                // RFC 0005-ext1: per-tracker AppModules deleted; PlanAppHost serves all.
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
