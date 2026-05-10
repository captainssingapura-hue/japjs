package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.ExternalReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0002Doc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;

import java.util.List;
import java.util.UUID;

public record Rfc0004Ext1Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("ceaeafd3-b400-458f-9c3b-e3c7a161c3ff");
    public static final Rfc0004Ext1Doc INSTANCE = new Rfc0004Ext1Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0004-ext1 — Managed Markdown References"; }
    @Override public String summary() { return "Doctrine extension to RFC 0004: every out-of-document reference becomes a typed Reference declared in Java; markdown cites them via #ref:<name> anchors. External and image refs unified under the same mechanism."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("pcv",   PureComponentViewsDoc.INSTANCE),
                new DocReference("def-2", Defect0002Doc.INSTANCE),
                new ExternalReference("css-spec", "https://www.w3.org/TR/css/",
                        "CSS Snapshot 2024", "W3C reference cited as the canonical example of an external reference.")
        );
    }
}
