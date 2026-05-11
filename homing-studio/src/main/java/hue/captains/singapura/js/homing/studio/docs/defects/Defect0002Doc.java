package hue.captains.singapura.js.homing.studio.docs.defects;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0003Doc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.EncapsulatedComponentsDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0003Doc;

import java.util.List;
import java.util.UUID;

public record Defect0002Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("65f96013-c5a7-43d4-b5e3-56d139021928");
    public static final Defect0002Doc INSTANCE = new Defect0002Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Defect 0002 — Themes Vary Paint, Not Form"; }
    @Override public String summary() { return "Original framing claimed themes couldn't reshape cards or platforms — only repaint them. Resolved (2026-05-11) by separating concerns: themes vary paint + shape (CSS, now cascade-deterministic via Defect 0003 fix); themes do NOT vary behavior (DOM structure, event handlers, JS — that's view-layer territory, addressed separately by RFC 0003's Component primitive). The Retro 90s theme reshapes cards into Win95 windows via pure CSS, proving the shape gap is closed."; }
    @Override public String category(){ return "DEFECT"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("def-3",                Defect0003Doc.INSTANCE),
                new DocReference("doc-encapsulated",     EncapsulatedComponentsDoc.INSTANCE),
                new DocReference("rfc-3",                Rfc0003Doc.INSTANCE));
    }
}
