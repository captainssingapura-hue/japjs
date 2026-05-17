package hue.captains.singapura.js.homing.studio.docs.ontology;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.meta.OntologyFirstDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0011Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0012Doc;

import java.util.List;
import java.util.UUID;

/**
 * Ontology — Studio. A unit of typed composition. Declares exactly one
 * home L0 catalogue and contributes apps, plans, themes, and an optional
 * standalone brand to whatever Bootstrap composes it.
 *
 * <p>Contains only the definition and the eight axioms (S1–S8):
 * identity (S1–S2), home (S3), contribution surfaces (S4, S8),
 * statelessness / immutability (S5–S6), composability (S7).</p>
 *
 * <p>Operational concerns — how to scope a Studio, when to split it,
 * how to share content across Studios, how to design the L0 catalogue —
 * live in Doctrines, not here.</p>
 *
 * <p>Fourth Ontology entry. Sits at the composition layer: above Docs
 * and DocTrees (which define what content is), above Viewers (which
 * render it), the Studio is what bundles a logical product together
 * before Bootstrap composes the runtime.</p>
 */
public record StudioOntologyDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("a8901234-5678-4e67-8489-90112334455a");
    public static final StudioOntologyDoc INSTANCE = new StudioOntologyDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Ontology — Studio"; }
    @Override public String summary() { return "A Studio is a unit of typed composition. Class-identified singleton; declares exactly one home L0 catalogue; contributes apps, plans, themes, and an optional standalone brand. Eight axioms — class-identified, singleton instance, exactly one home, five contribution surfaces, stateless, immutable declaration, composable under Umbrella, closed contribution set. Realised by HomingStudio, DemoBaseStudio, MultiStudio, SkillsStudio. Definition and axioms only — operational guidance lives in Doctrines."; }
    @Override public String category(){ return "ONTOLOGY"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-ontfirst",     OntologyFirstDoc.INSTANCE),
                new DocReference("doctree-ontology", DocTreeOntologyDoc.INSTANCE),
                new DocReference("viewer-ontology",  ViewerOntologyDoc.INSTANCE),
                new DocReference("rfc-11",           Rfc0011Doc.INSTANCE),
                new DocReference("rfc-12",           Rfc0012Doc.INSTANCE)
        );
    }
}
