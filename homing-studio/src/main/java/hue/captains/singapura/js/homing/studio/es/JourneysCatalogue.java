package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.rename.RenamePlanData;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001PlanData;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002PlanData;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1PlanData;
import hue.captains.singapura.js.homing.studio.rfc0004.Rfc0004PlanData;
import hue.captains.singapura.js.homing.studio.rfc0004ext1.Rfc0004Ext1PlanData;
import hue.captains.singapura.js.homing.studio.rfc0005.Rfc0005PlanData;
import hue.captains.singapura.js.homing.studio.rfc0005ext1.Rfc0005Ext1PlanData;
import hue.captains.singapura.js.homing.studio.release.V1PlanData;

import java.util.List;

/**
 * Sub-catalogue listing every plan tracker. Per RFC 0005-ext1, each entry is a
 * typed {@link hue.captains.singapura.js.homing.studio.base.tracker.Plan}
 * served by the shared {@code PlanAppHost} — no per-tracker AppModule needed.
 */
public record JourneysCatalogue() implements Catalogue {

    public static final JourneysCatalogue INSTANCE = new JourneysCatalogue();

    @Override public String name()    { return "Journeys"; }
    @Override public String summary() { return "Live trackers for every multi-phase plan in this project."; }

    @Override public List<Entry> entries() {
        return List.of(
                Entry.of(Rfc0001PlanData.INSTANCE),
                Entry.of(Rfc0002PlanData.INSTANCE),
                Entry.of(Rfc0002Ext1PlanData.INSTANCE),
                Entry.of(RenamePlanData.INSTANCE),
                Entry.of(Rfc0004PlanData.INSTANCE),
                Entry.of(Rfc0004Ext1PlanData.INSTANCE),
                Entry.of(Rfc0005PlanData.INSTANCE),
                Entry.of(Rfc0005Ext1PlanData.INSTANCE),
                Entry.of(V1PlanData.INSTANCE)
        );
    }
}
