package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0004Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0004Ext1Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0018Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0019Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0020Doc;

import java.util.List;

/**
 * L2 sub-catalogue of {@link RfcsCatalogue} — RFCs about the framework's
 * document model: typed Doc records, UUID identity, cross-doc references.
 */
public record ContentRfcsCatalogue()
        implements L2_Catalogue<RfcsCatalogue, ContentRfcsCatalogue> {

    public static final ContentRfcsCatalogue INSTANCE = new ContentRfcsCatalogue();

    @Override public RfcsCatalogue parent() { return RfcsCatalogue.INSTANCE; }
    @Override public String name()    { return "Documents"; }
    @Override public String summary() { return "How content is typed, identified, and cross-linked — the doc model that survives renames and serves both human readers and Claude agents."; }

    @Override public List<Entry<ContentRfcsCatalogue>> leaves() {
        return List.of(
                Entry.of(this, Rfc0004Doc.INSTANCE),
                Entry.of(this, Rfc0004Ext1Doc.INSTANCE),
                Entry.of(this, Rfc0018Doc.INSTANCE),
                Entry.of(this, Rfc0019Doc.INSTANCE),
                Entry.of(this, Rfc0020Doc.INSTANCE)
        );
    }
}
