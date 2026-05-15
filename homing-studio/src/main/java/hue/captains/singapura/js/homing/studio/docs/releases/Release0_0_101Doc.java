package hue.captains.singapura.js.homing.studio.docs.releases;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.FunctionalObjectsDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;
import hue.captains.singapura.js.homing.studio.docs.guides.ReleaseChecklistDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0012Doc;

import java.util.List;
import java.util.UUID;

/**
 * Release notes for 0.0.101 (binary — fifth release, predecessor 0.0.100).
 *
 * <p>One headline RFC: <b>RFC 0012 — Typed Studio Composition</b>. The
 * framework's bootstrap reshapes from a ten-parameter static funnel into a
 * typed four-piece record stack (Studio / Umbrella / Fixtures /
 * RuntimeParams / Bootstrap), satisfying the new <b>Functional Objects
 * doctrine</b> by construction. The <b>Weighed Complexity doctrine</b>
 * lands in parallel to justify why the typed shape is worth the slight
 * authoring cost. One cross-tree breadcrumb bug closes as a side-effect.</p>
 */
public record Release0_0_101Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("5b2e8a4f-1c7d-4e3a-9b8c-3d6e9f5a8c30");
    public static final Release0_0_101Doc INSTANCE = new Release0_0_101Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "0.0.101 — Typed Studio Composition"; }
    @Override public String summary() { return "RFC 0012 lands — Studio, Umbrella, Fixtures, RuntimeParams, Bootstrap as a typed four-piece stack replacing StudioBootstrap's ten-parameter static surface. Two new doctrines (Functional Objects, Weighed Complexity) name the principles the reshape obeys. One cross-tree breadcrumb bug closes as a side-effect. One breaking change for downstream studios — covered by the migrate-from-0-0-100 skill."; }
    @Override public String category(){ return "RELEASE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-12",       Rfc0012Doc.INSTANCE),
                new DocReference("doc-fo",       FunctionalObjectsDoc.INSTANCE),
                new DocReference("doc-wc",       WeighedComplexityDoc.INSTANCE),
                new DocReference("doc-cc",       CatalogueContainerDoc.INSTANCE),
                new DocReference("checklist",    ReleaseChecklistDoc.INSTANCE),
                new DocReference("rel-0-0-100",  Release0_0_100Doc.INSTANCE)
        );
    }
}
