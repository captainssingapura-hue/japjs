package hue.captains.singapura.js.homing.studio.docs.defects;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0003Doc;

import java.util.List;
import java.util.UUID;

public record Defect0002Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("65f96013-c5a7-43d4-b5e3-56d139021928");
    public static final Defect0002Doc INSTANCE = new Defect0002Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Defect 0002 — Themes Vary Paint, Not Form"; }
    @Override public String summary() { return "Theme primitives only vary colours/sizes. No way to reshape a card or platform per theme. Resolution drafted in RFC 0003."; }
    @Override public String category(){ return "DEFECT"; }

    @Override public List<Reference> references() {
        return List.of(new DocReference("rfc-3", Rfc0003Doc.INSTANCE));
    }
}
