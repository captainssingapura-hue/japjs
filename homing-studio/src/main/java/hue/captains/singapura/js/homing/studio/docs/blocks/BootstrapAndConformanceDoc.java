package hue.captains.singapura.js.homing.studio.docs.blocks;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0001Doc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ManagedDomOpsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.MethodsOverPropsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.OwnedReferencesDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;

import java.util.List;
import java.util.UUID;

/** Block 05 — Bootstrap & Conformance. */
public record BootstrapAndConformanceDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("577b886e-a45d-4d2e-a641-4d2163d5dabc");
    public static final BootstrapAndConformanceDoc INSTANCE = new BootstrapAndConformanceDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Block 05 — Bootstrap & Conformance"; }
    @Override public String summary() { return "StudioBootstrap.start(...) one-call server entrypoint + the six conformance test bases."; }
    @Override public String category(){ return "BLOCK"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("pcv",   PureComponentViewsDoc.INSTANCE),
                new DocReference("mop",   MethodsOverPropsDoc.INSTANCE),
                new DocReference("mdo",   ManagedDomOpsDoc.INSTANCE),
                new DocReference("or",    OwnedReferencesDoc.INSTANCE),
                new DocReference("def-1", Defect0001Doc.INSTANCE)
        );
    }
}
