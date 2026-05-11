package hue.captains.singapura.js.homing.studio.docs.releases;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0002Doc;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0003Doc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.EncapsulatedComponentsDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0006Doc;

import java.util.List;
import java.util.UUID;

/**
 * Release notes for 0.0.11 — the first version to ship with formal release
 * notes. Sets the precedent for every release after.
 */
public record Release0_0_11Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("f7e0cede-f0a9-43e7-9097-ea1732918f4d");
    public static final Release0_0_11Doc INSTANCE = new Release0_0_11Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "0.0.11 — Cascade Layers, Retro 90s, Reframes"; }
    @Override public String summary() { return "Cascade-layer ladder closes Defect 0003 — themes can now reshape any selector deterministically without !important. Defect 0002 resolves by reframing (themes vary paint+shape, not behavior). Two new themes land — Maple Bridge (Tang-dynasty nocturne, introduces Theme.backdrop()) and Retro 90s (Win95 desktop, Notepad-style reading windows). Encapsulated Components doctrine codifies what the cascade ladder makes enforceable. RFC 0006 drafts writing-media textures as a separate theme dimension."; }
    @Override public String category(){ return "RELEASE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("def-2",            Defect0002Doc.INSTANCE),
                new DocReference("def-3",            Defect0003Doc.INSTANCE),
                new DocReference("doc-encapsulated", EncapsulatedComponentsDoc.INSTANCE),
                new DocReference("rfc-6",            Rfc0006Doc.INSTANCE)
        );
    }
}
