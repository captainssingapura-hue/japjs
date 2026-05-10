package hue.captains.singapura.js.homing.studio.docs.blocks;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PlanContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0005Ext1Doc;

import java.util.List;
import java.util.UUID;

/** Block 03 — Plan Kit (RFC 0005-ext1 typed plan tracker). */
public record PlanKitDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("8b2a0c4f-3df1-4a55-9f8a-2e6c1d5a7b09");
    public static final PlanKitDoc INSTANCE = new PlanKitDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Block 03 — Plan Kit"; }
    @Override public String summary() { return "Typed multi-phase tracker — questions, phased actions, acceptance, optional objectives. RFC 0005-ext1."; }
    @Override public String category(){ return "BLOCK"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("plan-doc", PlanContainerDoc.INSTANCE),
                new DocReference("rfc-5e1",  Rfc0005Ext1Doc.INSTANCE),
                new DocReference("kit-cat",  CatalogueKitDoc.INSTANCE),
                new DocReference("atoms",    AtomsDoc.INSTANCE),
                new DocReference("bac",      BootstrapAndConformanceDoc.INSTANCE)
        );
    }
}
