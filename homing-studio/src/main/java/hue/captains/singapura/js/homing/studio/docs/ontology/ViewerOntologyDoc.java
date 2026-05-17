package hue.captains.singapura.js.homing.studio.docs.ontology;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.meta.OntologyFirstDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0014Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0015Doc;

import java.util.List;
import java.util.UUID;

/**
 * Ontology — Viewer. The framework type that renders content of one
 * specific kind. The kind-bound, registered bridge between a Doc's
 * identity and its rendered surface.
 *
 * <p>Contains only the definition and the ten axioms (V1–V10): identity
 * (V1–V2), kind exclusivity (V3), Doc routing (V4–V5), URL composition
 * (V6), read-only / stateless contracts (V7–V8, V10), registration
 * (V9).</p>
 *
 * <p>Operational concerns — how to design good Viewer UX, when to
 * introduce a new kind, what library to use for rendering — live in
 * Doctrines, not here.</p>
 *
 * <p>Third Ontology entry, after {@link DocOntologyDoc} and
 * {@link DocTreeOntologyDoc}. Closes the Doc-DocTree-Viewer triad: Doc
 * names the atomic unit, DocTree the structural container, Viewer the
 * rendering bridge.</p>
 */
public record ViewerOntologyDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("f7a89012-3456-4d56-8378-8f9012233344");
    public static final ViewerOntologyDoc INSTANCE = new ViewerOntologyDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Ontology — Viewer"; }
    @Override public String summary() { return "A Viewer is the framework type that renders content of one specific kind. Stateless, read-only, registered binding between a content kind and an AppModule that renders Docs of that kind. Eleven axioms — identity by kind, kind exclusivity, Doc routing via kind, one Viewer many Docs, canonical URL composition, read-only rendering, stateless across users, registration as activation, stateless functional object, mandated chrome composition baked into the type system (the framework-owned DocViewer<P,M> base has final selfContent/imports/exports; concrete subclasses supply only body + appMain + simpleName, never chrome). Two-layer structure: bare-viewer layer (heterogeneous, subclass-supplied) + common-infra layer (physically one type, no extension). Realised by DocReader, PlanAppHost, SvgViewer (typed base), StudioGraphInspector. Definition and axioms only — operational guidance lives in Doctrines."; }
    @Override public String category(){ return "ONTOLOGY"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-ontfirst",   OntologyFirstDoc.INSTANCE),
                new DocReference("doc-ontology",   DocOntologyDoc.INSTANCE),
                new DocReference("doctree-ontology", DocTreeOntologyDoc.INSTANCE),
                new DocReference("rfc-14",         Rfc0014Doc.INSTANCE),
                new DocReference("rfc-15",         Rfc0015Doc.INSTANCE)
        );
    }
}
