package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0004Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0001Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0004Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0004Ext1Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0005Ext2Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0012Doc;

import java.util.List;
import java.util.UUID;

public record CatalogueContainerDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("9ad07961-72c5-4f0b-89ca-3e2089f40da9");
    public static final CatalogueContainerDoc INSTANCE = new CatalogueContainerDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Catalogues as Containers"; }
    @Override public String summary() { return "A catalogue is a typed ordered container of docs or sub-catalogues. Identity is intrinsic; linking flows through identity; rendering belongs to the renderer."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("pcv",        PureComponentViewsDoc.INSTANCE),
                new DocReference("or",         OwnedReferencesDoc.INSTANCE),
                new DocReference("rfc-1",      Rfc0001Doc.INSTANCE),
                new DocReference("rfc-4",      Rfc0004Doc.INSTANCE),
                new DocReference("rfc-4-ext1", Rfc0004Ext1Doc.INSTANCE),
                new DocReference("rfc-5-ext2", Rfc0005Ext2Doc.INSTANCE),
                new DocReference("rfc-12",     Rfc0012Doc.INSTANCE),
                new DocReference("def-4",      Defect0004Doc.INSTANCE)
        );
    }
}
