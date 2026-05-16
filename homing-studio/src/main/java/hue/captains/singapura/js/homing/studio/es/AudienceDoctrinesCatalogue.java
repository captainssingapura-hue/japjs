package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.doctrines.DualAudienceSkillsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FirstUserDoc;

import java.util.List;

/**
 * L2 sub-catalogue of {@link DoctrineCatalogue} — doctrines about <i>who</i>
 * the framework serves and how content is shaped for them. The framework's
 * primary reader is Claude-class agents (First User); content has to work
 * for both the agent and the human (Dual-Audience Skills).
 */
public record AudienceDoctrinesCatalogue()
        implements L2_Catalogue<DoctrineCatalogue, AudienceDoctrinesCatalogue> {

    public static final AudienceDoctrinesCatalogue INSTANCE = new AudienceDoctrinesCatalogue();

    @Override public DoctrineCatalogue parent() { return DoctrineCatalogue.INSTANCE; }
    @Override public String name()    { return "Authoring Audience"; }
    @Override public String summary() { return "Who the framework serves and how content is shaped for them. The first user is an agent; every authored surface has to read for both the agent and the human."; }
    @Override public String badge()   { return "DOCTRINE"; }
    @Override public String icon()    { return "👥"; }

    @Override public List<Entry<AudienceDoctrinesCatalogue>> leaves() {
        return List.of(
                Entry.of(this, FirstUserDoc.INSTANCE),
                Entry.of(this, DualAudienceSkillsDoc.INSTANCE)
        );
    }
}
