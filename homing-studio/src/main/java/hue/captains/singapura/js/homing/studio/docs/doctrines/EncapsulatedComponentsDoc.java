package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0003Doc;

import java.util.List;
import java.util.UUID;

public record EncapsulatedComponentsDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("e33e6e9e-e8f2-48b7-ab05-b3544b70f198");
    public static final EncapsulatedComponentsDoc INSTANCE = new EncapsulatedComponentsDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Well-Encapsulated Components (CSS Included)"; }
    @Override public String summary() { return "A component owns its markup, its behaviour, AND its CSS — authored as typed CssClass records on a layer the component declares. Downstream studios consume components, not raw selectors. Front-end's 20-year specificity fight is the framework's, not the consumer's."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("def-3", Defect0003Doc.INSTANCE)
        );
    }
}
