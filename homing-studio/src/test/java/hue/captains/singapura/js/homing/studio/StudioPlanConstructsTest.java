package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.studio.base.DocRegistry;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanRegistry;
import hue.captains.singapura.js.homing.studio.docdsl.DocDslPlanData;
import hue.captains.singapura.js.homing.studio.es.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.instruments.InstrumentsPlanData;
import hue.captains.singapura.js.homing.studio.studiograph.StudioGraphPlanData;
import hue.captains.singapura.js.homing.studio.rename.RenamePlanData;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001PlanData;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002PlanData;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1PlanData;
import hue.captains.singapura.js.homing.studio.rfc0004.Rfc0004PlanData;
import hue.captains.singapura.js.homing.studio.rfc0004ext1.Rfc0004Ext1PlanData;
import hue.captains.singapura.js.homing.studio.rfc0005.Rfc0005PlanData;
import hue.captains.singapura.js.homing.studio.release.V1PlanData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * RFC 0005-ext1 §3 — single conformance test for the plan registry. Constructs the
 * {@link PlanRegistry} from the studio's explicit plan list. The boot-time validations
 * (phase ID uniqueness, decision ID uniqueness, status non-null, dep targets exist,
 * doc reachability) are mechanically enforced by the constructor; this test pins
 * registry construction success in CI.
 */
class StudioPlanConstructsTest {

    @Test
    void planRegistryConstructsCleanly() {
        // Build the same DocRegistry the bootstrap does — from the studio's
        // doc providers (the catalogue side + DocBrowser).
        var docRegistry = new DocRegistry(Stream.of(
                        DocBrowser.INSTANCE.docs(),
                        BuildingBlocksCatalogue.INSTANCE.docs())
                .flatMap(List::stream)
                .toList());

        List<Plan> plans = List.of(
                Rfc0001PlanData.INSTANCE,
                Rfc0002PlanData.INSTANCE,
                Rfc0002Ext1PlanData.INSTANCE,
                RenamePlanData.INSTANCE,
                Rfc0004PlanData.INSTANCE,
                Rfc0004Ext1PlanData.INSTANCE,
                Rfc0005PlanData.INSTANCE,
                V1PlanData.INSTANCE,
                InstrumentsPlanData.INSTANCE,
                DocDslPlanData.INSTANCE,
                StudioGraphPlanData.INSTANCE
        );

        assertDoesNotThrow(() -> new PlanRegistry(plans, docRegistry));
    }
}
