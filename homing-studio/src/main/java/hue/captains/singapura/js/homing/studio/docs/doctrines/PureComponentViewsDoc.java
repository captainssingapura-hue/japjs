package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0002Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0003Doc;

import java.util.List;
import java.util.UUID;

public record PureComponentViewsDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("bcb1c03b-79a5-4140-81d5-c5d2cc20da3e");
    public static final PureComponentViewsDoc INSTANCE = new PureComponentViewsDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Pure-Component Views"; }
    @Override public String summary() { return "No HTML in consumer code. Every UI element is a Component invocation. Required reading."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-3", Rfc0003Doc.INSTANCE),
                new DocReference("def-2", Defect0002Doc.INSTANCE)
        );
    }
}
