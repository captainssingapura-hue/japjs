package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.ExplicitOverImplicitDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;
import hue.captains.singapura.js.homing.studio.docs.meta.OntologyFirstDoc;
import hue.captains.singapura.js.homing.studio.docs.ontology.DocOntologyDoc;
import hue.captains.singapura.js.homing.studio.docs.ontology.DocTreeOntologyDoc;
import hue.captains.singapura.js.homing.studio.docs.ontology.ViewerOntologyDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0015 — Doc Unification. Promote {@code Doc} from a single concrete
 * prose-document interface to a sealed family of content-bearing catalogue
 * leaves. Subsumes Plans and AppModule navigables under {@code Doc} as
 * typed subtypes; carves out {@code StudioProxy} as the explicit
 * <em>structural</em> leaf. Introduces {@code ProxyDoc} to resolve
 * multi-home situations via fresh identity per appearance. Introduces
 * {@code ContentViewer} as the extension point mapping content kinds to
 * viewer AppModules.
 *
 * <p>Net effect: one sealed sum collapses, four switch statements vanish,
 * the diagnostic and tree-leaf composition pathways stop reinventing
 * per-kind dispatch.</p>
 *
 * <p>Companion of {@link Rfc0016Doc} (Content Trees) — together they form
 * the protocol pair that opens the framework's authoring surface to
 * generated / imported / dynamic / lightweight content without
 * compromising the typed-by-default discipline of the spine.</p>
 */
public record Rfc0015Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("a2b3c4d5-6e7f-4801-8920-3a4b5c6d7e80");
    public static final Rfc0015Doc INSTANCE = new Rfc0015Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0015 — Doc Unification"; }
    @Override public String summary() { return "Promote Doc from a single prose-document interface to a sealed family of content-bearing catalogue leaves. Subsumes Plans and AppModule navigables under Doc; carves out StudioProxy as the explicit structural leaf. Introduces ProxyDoc to resolve multi-home via fresh identity per appearance. Introduces ContentViewer as the extension point mapping content kinds to viewer AppModules. Collapses the four-branch Entry sum into polymorphism; eliminates four switch statements; resolves silent multi-home into an explicit author choice."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-ontology",     DocOntologyDoc.INSTANCE),
                new DocReference("doctree-ontology", DocTreeOntologyDoc.INSTANCE),
                new DocReference("viewer-ontology",  ViewerOntologyDoc.INSTANCE),
                new DocReference("doc-ontfirst",     OntologyFirstDoc.INSTANCE),
                new DocReference("rfc-5",            Rfc0005Doc.INSTANCE),
                new DocReference("rfc-5-ext2",       Rfc0005Ext2Doc.INSTANCE),
                new DocReference("rfc-11",           Rfc0011Doc.INSTANCE),
                new DocReference("rfc-14",           Rfc0014Doc.INSTANCE),
                new DocReference("rfc-16",           Rfc0016Doc.INSTANCE),
                new DocReference("doc-fo",           FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-cc",           CatalogueContainerDoc.INSTANCE),
                new DocReference("doc-eoi",          ExplicitOverImplicitDoc.INSTANCE),
                new DocReference("doc-wc",           WeighedComplexityDoc.INSTANCE)
        );
    }
}
