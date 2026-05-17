package hue.captains.singapura.js.homing.studio.docs.doctrines;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.ontology.ViewerOntologyDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0017Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0018Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0019Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0020Doc;

import java.util.List;
import java.util.UUID;

/**
 * Doctrine — Typed Content Vocabulary. <em>"You don't really need HTML,
 * just SVGs."</em>
 *
 * <p>Framework-shipped content is composed exclusively of typed
 * primitives. No construct in framework-shipped content is untyped.
 * HTML escape hatches are forbidden; if a use case requires a construct
 * not in the existing vocabulary, file an extension proposal — don't
 * escape.</p>
 *
 * <p>The content-side mirror of the framework's code discipline
 * (Functional Objects, Explicit over Implicit, Weighed Complexity).
 * Same principle, content layer: typed-by-default; sealed-permits
 * extension; no untyped escape hatches.</p>
 */
public record TypedContentVocabularyDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("d1e2f3a4-5b6c-4d7e-8901-234567890abc");
    public static final TypedContentVocabularyDoc INSTANCE = new TypedContentVocabularyDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Doctrine — Typed Content Vocabulary"; }
    @Override public String summary() { return "\"You don't really need HTML, just SVGs.\" Framework-shipped content is composed exclusively of typed primitives — ProseDoc / MarkdownSegment for text, SvgDoc / SvgSegment for visuals, TableDoc / TableSegment for structured data, ImageDoc / ImageSegment for rasters, ComposedDoc for mixed flow. Inline HTML, raw markdown extensions, and author-rolled DOM constructs are forbidden. The content-side mirror of the framework's code discipline. Extensions to the vocabulary require typed-permits cooperation; no silent additions; no untyped escape hatches."; }
    @Override public String category(){ return "DOCTRINE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-17",          Rfc0017Doc.INSTANCE),
                new DocReference("rfc-18",          Rfc0018Doc.INSTANCE),
                new DocReference("rfc-19",          Rfc0019Doc.INSTANCE),
                new DocReference("rfc-20",          Rfc0020Doc.INSTANCE),
                new DocReference("doc-fo",          FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-eoi",         ExplicitOverImplicitDoc.INSTANCE),
                new DocReference("doc-wc",          WeighedComplexityDoc.INSTANCE),
                new DocReference("viewer-ontology", ViewerOntologyDoc.INSTANCE)
        );
    }
}
