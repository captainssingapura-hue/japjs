package hue.captains.singapura.js.homing.studio.docs.ontology;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.meta.OntologyFirstDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0015Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0016Doc;

import java.util.List;
import java.util.UUID;

/**
 * Ontology — Doc. The atomic unit of citable, viewable content. Defines
 * what a Doc <em>is</em> via the universal id grammar, the eight axioms,
 * the universal shape, and the relationships to other ontological types
 * (ManagedTree, ContentViewer, DocId).
 *
 * <p>Contains only definition and axioms. Operational concerns — how to
 * author Docs, when to use ProxyDoc, how to design cite tokens, how to
 * migrate content — live in Doctrines, not here. The scope split is
 * intentional: <em>what a Doc is</em> changes rarely and slowly;
 * <em>how to use Docs</em> changes often and evolves with practice.</p>
 *
 * <p>The first Ontology entry filed under {@code OntologyCatalogue}.
 * Anchored by the Ontology First meta-doctrine — every primitive in the
 * framework that surfaces as content gets one of these entries before it
 * gets a runtime realisation.</p>
 */
public record DocOntologyDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("d5e6f7a8-9012-4b34-8156-6d7e8f901112");
    public static final DocOntologyDoc INSTANCE = new DocOntologyDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Ontology — Doc"; }
    @Override public String summary() { return "A Doc is the atomic unit of citable, viewable content in the framework. Identified by a durable DocId; attached as a leaf to exactly one ManagedTree; references other Docs by id only; surfaces as a single-page experience with no separately-addressable sub-states; read-only; stateless across users; routed through exactly one ContentViewer. Eight axioms; universal metadata shape; sealed identity grammar. Definition and axioms only — operational guidance lives in Doctrines."; }
    @Override public String category(){ return "ONTOLOGY"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-ontfirst", OntologyFirstDoc.INSTANCE),
                new DocReference("rfc-15",       Rfc0015Doc.INSTANCE),
                new DocReference("rfc-16",       Rfc0016Doc.INSTANCE)
        );
    }
}
