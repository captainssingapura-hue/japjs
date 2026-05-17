package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ExplicitOverImplicitDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0016 — Content Trees (Generalisation of Catalogues). Introduce
 * {@code ContentTree}, a data-authored hierarchical structure that shares
 * the catalogue rendering surface but is constructed from immutable tree
 * data rather than typed records.
 *
 * <p>Catalogue stays untouched — it remains the typed, code-authored
 * spine of the framework. {@code ContentTree} opens the door to generated,
 * imported, dynamic, and lightweight-authored hierarchies (search results,
 * tag pages, manifest-driven indexes, downstream-supplied trees) without
 * forcing a Java class per node. The two hierarchies render through the
 * same UI; their construction models are deliberately different.</p>
 *
 * <p>Depends on {@link Rfc0015Doc} (Doc Unification) for {@code Doc},
 * {@code ProxyDoc}, and {@code ContentViewer}. Together the two RFCs form
 * the protocol pair that opens the authoring surface from typed code to
 * imported data without compromising either discipline.</p>
 */
public record Rfc0016Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("b3c4d5e6-7f80-4912-a031-4b5c6d7e8f90");
    public static final Rfc0016Doc INSTANCE = new Rfc0016Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0016 — Content Trees"; }
    @Override public String summary() { return "Introduce ContentTree, a data-authored hierarchical structure that shares the catalogue rendering surface but is constructed from immutable tree data rather than typed records. Catalogue stays untouched as the typed code-authored spine. ContentTree opens the door to generated, imported, dynamic, and lightweight-authored hierarchies — search results, tag pages, manifest-driven indexes, downstream-supplied trees — without forcing a Java class per node. Both hierarchies render through the same UI; their construction models are deliberately different. Companion to RFC 0015 — Doc Unification."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-5",       Rfc0005Doc.INSTANCE),
                new DocReference("rfc-5-ext2",  Rfc0005Ext2Doc.INSTANCE),
                new DocReference("rfc-11",      Rfc0011Doc.INSTANCE),
                new DocReference("rfc-13",      Rfc0013Doc.INSTANCE),
                new DocReference("rfc-14",      Rfc0014Doc.INSTANCE),
                new DocReference("rfc-15",      Rfc0015Doc.INSTANCE),
                new DocReference("doc-cc",      CatalogueContainerDoc.INSTANCE),
                new DocReference("doc-eoi",     ExplicitOverImplicitDoc.INSTANCE),
                new DocReference("doc-fo",      FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-wc",      WeighedComplexityDoc.INSTANCE)
        );
    }
}
