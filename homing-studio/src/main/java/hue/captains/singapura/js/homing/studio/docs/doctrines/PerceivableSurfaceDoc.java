package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0002Doc;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0003Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0007Doc;

import java.util.List;
import java.util.UUID;

public record PerceivableSurfaceDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("8de6bdcb-17af-4429-81a9-b9cafe24dd94");
    public static final PerceivableSurfaceDoc INSTANCE = new PerceivableSurfaceDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Themes as Perceivable Surface"; }
    @Override public String summary() { return "A theme varies what the user PERCEIVES — sight, sound, opt-in interaction, ambient state — never what the page DOES. Five dimensions a theme owns (palette, shape, atmosphere, sound, ambient interactivity) and one bright line it never crosses (page content semantics, navigation logic, data flow). The doctrine evolves Defect 0002's paint+shape reframe through RFC 0007's audio extension into the design space of themes-as-experiences."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("def-2", Defect0002Doc.INSTANCE),
                new DocReference("def-3", Defect0003Doc.INSTANCE),
                new DocReference("rfc-7", Rfc0007Doc.INSTANCE)
        );
    }
}
