package hue.captains.singapura.js.homing.studio.docs.blocks;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0005Doc;

import java.util.List;
import java.util.UUID;

/** Block 02 — Catalogue Kit (RFC 0005 typed container). */
public record CatalogueKitDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("19e5f1ab-0a7e-4705-9268-3b85af8ff251");
    public static final CatalogueKitDoc INSTANCE = new CatalogueKitDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Block 02 — Catalogue Kit"; }
    @Override public String summary() { return "Typed structural container for organizing the studio's docs, sub-catalogues, and other navigable apps. RFC 0005."; }
    @Override public String category(){ return "BLOCK"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("cc",       CatalogueContainerDoc.INSTANCE),
                new DocReference("rfc-5",    Rfc0005Doc.INSTANCE),
                new DocReference("atoms",    AtomsDoc.INSTANCE),
                new DocReference("bac",      BootstrapAndConformanceDoc.INSTANCE)
        );
    }
}
