package hue.captains.singapura.js.homing.studio.docs.brand;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record RenameToHomingDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("40f89eeb-c34a-402e-b71e-786ab9e8c016");
    public static final RenameToHomingDoc INSTANCE = new RenameToHomingDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Rename Dossier — japjs → Homing"; }
    @Override public String summary() { return "Decision context, three-layer metaphor, migration logistics."; }
    @Override public String category(){ return "BRAND"; }

    @Override public List<Reference> references() {
        return List.of(new DocReference("brand-readme", BrandReadmeDoc.INSTANCE));
    }
}
