package hue.captains.singapura.js.homing.studio.docs.rename;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.brand.BrandReadmeDoc;
import hue.captains.singapura.js.homing.studio.docs.brand.RenameToHomingDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0001Doc;

import java.util.List;
import java.util.UUID;

public record RenameExecutionPlanDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("8a6849ae-ef48-4919-b1f4-1a44380657a7");
    public static final RenameExecutionPlanDoc INSTANCE = new RenameExecutionPlanDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Rename Execution Plan"; }
    @Override public String summary() { return "Six-phase migration plan with verification gates and rollback strategy."; }
    @Override public String category(){ return "RENAME"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-1",        Rfc0001Doc.INSTANCE),
                new DocReference("rename-doc",   RenameToHomingDoc.INSTANCE),
                new DocReference("brand-readme", BrandReadmeDoc.INSTANCE)
        );
    }
}
