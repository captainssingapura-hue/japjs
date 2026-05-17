package hue.captains.singapura.js.homing.studio.docs.casestudies;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.OwnedReferencesDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0012Doc;

import java.util.List;
import java.util.UUID;

/**
 * Case study — why cross-studio Doc references resolve correctly under a
 * composed multi-studio Bootstrap without a single line of "cross-studio"
 * code in the framework.
 *
 * <p>Surfaced after RFC 0012 shipped and the user observed: <i>"I suspect
 * cross-studio reference should just work."</i> The validation confirmed it
 * — runtime resolution is studio-blind at every wire surface, and the
 * compile-time constraint is just a normal Maven dependency. This study
 * reverse-engineers why, naming the five design properties that converged
 * to make the property emerge for free.</p>
 *
 * <p>The general lesson the study extracts: cheap features in a framework
 * are the ones that fall out of saying <i>no</i> enough times early —
 * location-encoded URLs, symbolic references, per-studio sub-registries,
 * and studio-of-origin wire metadata were all refused for unrelated
 * reasons. Cross-studio reference is what's left.</p>
 */
public record CrossStudioRefsCaseStudy() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("c5a3d7e2-9b4f-4a6e-8c1d-5e7b3f2a9d40");
    public static final CrossStudioRefsCaseStudy INSTANCE = new CrossStudioRefsCaseStudy();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Case Study — Cross-Studio References Cost Nothing"; }
    @Override public String summary() { return "Why cross-studio Doc references resolve correctly under a composed multi-studio Bootstrap without a single line of \"cross-studio\" code in the framework. Five design properties converge — identity by UUID, references as typed objects, open-set / closed-shape composition, one registry per Bootstrap, location-free wire surfaces. The general lesson: the cheap features fall out of refusing the wrong shapes early."; }
    @Override public String category(){ return "CASE_STUDY"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-12",  Rfc0012Doc.INSTANCE),
                new DocReference("doc-cc",  CatalogueContainerDoc.INSTANCE),
                new DocReference("doc-fo",  FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-or",  OwnedReferencesDoc.INSTANCE)
        );
    }
}
