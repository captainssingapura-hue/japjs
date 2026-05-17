package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001PlanData;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002PlanData;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1PlanData;
import hue.captains.singapura.js.homing.studio.rfc0004.Rfc0004PlanData;
import hue.captains.singapura.js.homing.studio.rfc0004ext1.Rfc0004Ext1PlanData;
import hue.captains.singapura.js.homing.studio.rfc0005.Rfc0005PlanData;
import hue.captains.singapura.js.homing.studio.rfc0005ext1.Rfc0005Ext1PlanData;
import hue.captains.singapura.js.homing.studio.content.TypedContentVocabularyPlanData;

import java.util.List;

/**
 * L2 sub-catalogue of {@link JourneysCatalogue} — RFC-driven plans. Each
 * entry is a multi-phase plan attached to a numbered RFC (or its extension).
 *
 * <p>First L2 catalogue in the studio — exercises the typed-levels stack down
 * to depth 2 (RFC 0005-ext2). The breadcrumb chain rendered above any RFC
 * plan now reads {@code Homing · studio › Journeys › RFCs › RFC 0001}.</p>
 */
public record RfcJourneysCatalogue()
        implements L2_Catalogue<JourneysCatalogue, RfcJourneysCatalogue> {

    public static final RfcJourneysCatalogue INSTANCE = new RfcJourneysCatalogue();

    @Override public JourneysCatalogue parent() { return JourneysCatalogue.INSTANCE; }
    @Override public String name()    { return "RFCs"; }
    @Override public String summary() { return "Plans attached to numbered RFCs and their extensions — the architectural decisions in flight, with phased execution and acceptance criteria."; }

    @Override public List<Entry<RfcJourneysCatalogue>> leaves() {
        return List.of(
                Entry.of(this, Rfc0001PlanData.INSTANCE),
                Entry.of(this, Rfc0002PlanData.INSTANCE),
                Entry.of(this, Rfc0002Ext1PlanData.INSTANCE),
                Entry.of(this, Rfc0004PlanData.INSTANCE),
                Entry.of(this, Rfc0004Ext1PlanData.INSTANCE),
                Entry.of(this, Rfc0005PlanData.INSTANCE),
                Entry.of(this, Rfc0005Ext1PlanData.INSTANCE),
                Entry.of(this, TypedContentVocabularyPlanData.INSTANCE)
        );
    }
}
