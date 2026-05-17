package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0002Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0002Ext1Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0003Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0006Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0007Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0008Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0017Doc;

import java.util.List;

/**
 * L2 sub-catalogue of {@link RfcsCatalogue} — RFCs covering Homing's visual
 * system end-to-end: typed CSS, themes, wallpapers, audio cues, interactive
 * theme experiences.
 */
public record VisualSystemRfcsCatalogue()
        implements L2_Catalogue<RfcsCatalogue, VisualSystemRfcsCatalogue> {

    public static final VisualSystemRfcsCatalogue INSTANCE = new VisualSystemRfcsCatalogue();

    @Override public RfcsCatalogue parent() { return RfcsCatalogue.INSTANCE; }
    @Override public String name()    { return "Visual System"; }
    @Override public String summary() { return "Typed CSS groups, semantic tokens, themeable primitives, wallpaper backdrops, audio cues, and interactive theme experiences — the studio's sensory layer end-to-end."; }

    @Override public List<Entry<VisualSystemRfcsCatalogue>> leaves() {
        return List.of(
                Entry.of(this, Rfc0002Doc.INSTANCE),
                Entry.of(this, Rfc0002Ext1Doc.INSTANCE),
                Entry.of(this, Rfc0003Doc.INSTANCE),
                Entry.of(this, Rfc0006Doc.INSTANCE),
                Entry.of(this, Rfc0007Doc.INSTANCE),
                Entry.of(this, Rfc0008Doc.INSTANCE),
                Entry.of(this, Rfc0017Doc.INSTANCE)
        );
    }
}
