package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.instruments.InstrumentsPlanData;
import hue.captains.singapura.js.homing.studio.release.V1PlanData;
import hue.captains.singapura.js.homing.studio.rename.RenamePlanData;

import java.util.List;

/**
 * L2 sub-catalogue of {@link JourneysCatalogue} — cross-cutting operational
 * plans (refactors, releases, feature initiatives) that aren't tied to a
 * specific numbered RFC.
 */
public record OperationsJourneysCatalogue()
        implements L2_Catalogue<JourneysCatalogue, OperationsJourneysCatalogue> {

    public static final OperationsJourneysCatalogue INSTANCE = new OperationsJourneysCatalogue();

    @Override public JourneysCatalogue parent() { return JourneysCatalogue.INSTANCE; }
    @Override public String name()    { return "Operations"; }
    @Override public String summary() { return "Cross-cutting initiatives: large refactors, release rollups, developer-tool builds. The plans that don't fit under a single RFC."; }

    @Override public List<Entry<OperationsJourneysCatalogue>> leaves() {
        return List.of(
                Entry.of(this, RenamePlanData.INSTANCE),
                Entry.of(this, V1PlanData.INSTANCE),
                Entry.of(this, InstrumentsPlanData.INSTANCE)
        );
    }
}
