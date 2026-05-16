package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.doctrines.NoStealthDataDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.QualityWithoutSurveillanceDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.StatelessServerDoc;

import java.util.List;

/**
 * L2 sub-catalogue of {@link DoctrineCatalogue} — doctrines about what the
 * user is promised. Client refuses covert gathering (No Stealth Data);
 * server refuses covert retention (Stateless Server). Together: client data
 * is the client's; communication between them is auditable from the
 * browser's own tools.
 */
public record TrustDoctrinesCatalogue()
        implements L2_Catalogue<DoctrineCatalogue, TrustDoctrinesCatalogue> {

    public static final TrustDoctrinesCatalogue INSTANCE = new TrustDoctrinesCatalogue();

    @Override public DoctrineCatalogue parent() { return DoctrineCatalogue.INSTANCE; }
    @Override public String name()    { return "Privacy & Trust"; }
    @Override public String summary() { return "What the user is promised. Client refuses covert gathering, server refuses covert retention, engineering refuses to substitute surveillance for discipline. Together: client data is the client's; quality is the engineer's work; communication is auditable from the browser's own tools."; }
    @Override public String badge()   { return "DOCTRINE"; }
    @Override public String icon()    { return "🔒"; }

    @Override public List<Entry<TrustDoctrinesCatalogue>> leaves() {
        return List.of(
                Entry.of(this, NoStealthDataDoc.INSTANCE),
                Entry.of(this, StatelessServerDoc.INSTANCE),
                Entry.of(this, QualityWithoutSurveillanceDoc.INSTANCE)
        );
    }
}
