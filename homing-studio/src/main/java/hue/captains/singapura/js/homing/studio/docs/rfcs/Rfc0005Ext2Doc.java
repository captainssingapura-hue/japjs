package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;

import java.util.List;
import java.util.UUID;

public record Rfc0005Ext2Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("de88cc57-0374-48ef-8b1e-8eeea5bcb3a5");
    public static final Rfc0005Ext2Doc INSTANCE = new Rfc0005Ext2Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0005-ext2 — Typed Catalogue Levels"; }
    @Override public String summary() { return "Encode catalogue tree depth in the type system. Sealed Catalogue base permits L0_Catalogue through L8_Catalogue; each non-root level is generic over its parent's type. Multi-parent and cycles become impossible to express; breadcrumb chains derive from typed parent() calls instead of registry reverse-index walks; the doc-breadcrumb-flat defect is fixed structurally rather than patched. Big-bang migration across the framework's 5 existing catalogues."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("cc",     CatalogueContainerDoc.INSTANCE),
                new DocReference("rfc-5",  Rfc0005Doc.INSTANCE)
        );
    }
}
