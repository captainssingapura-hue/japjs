package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0005Doc;

import java.util.List;
import java.util.UUID;

public record PlanContainerDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("e4e599df-2a8b-463e-87b1-53c233c4e447");
    public static final PlanContainerDoc INSTANCE = new PlanContainerDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Plans as Living Containers"; }
    @Override public String summary() { return "A plan is a typed living container of structured work: questions, phased actions, and acceptance. The structure is closed; the content within each pillar is open and lives over time."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("cc",    CatalogueContainerDoc.INSTANCE),
                new DocReference("rfc-5", Rfc0005Doc.INSTANCE)
        );
    }
}
