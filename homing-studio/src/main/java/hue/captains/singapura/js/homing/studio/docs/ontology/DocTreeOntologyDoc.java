package hue.captains.singapura.js.homing.studio.docs.ontology;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.meta.OntologyFirstDoc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0011Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0015Doc;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0016Doc;

import java.util.List;
import java.util.UUID;

/**
 * Ontology — DocTree. A strict, immutable, rooted-tree structure of named
 * nodes whose leaves bear Docs or reference other DocTrees. The framework's
 * only structural container for content; everything a user navigates lives
 * in one.
 *
 * <p>Contains only the definition and the ten axioms (T1–T10): structural
 * (T1–T4), content (T5–T6), immutability (T7–T8), identity (T9–T10).
 * Operational concerns — when to choose Catalogue vs ContentTree, how deep
 * to nest, slug naming, when to introduce cross-tree leaves, breadcrumb
 * rendering across boundaries — live in Doctrines, not here.</p>
 *
 * <p>The second Ontology entry, sibling to {@link DocOntologyDoc}. Together
 * they define the two intertwined primitives of the framework's content
 * spine: Doc (the atomic unit) and DocTree (the structural container).
 * Doc A5 and DocTree T6 are the same invariant from two angles — single
 * home per Doc.</p>
 */
public record DocTreeOntologyDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("e6f7a890-1234-4c45-8267-7e8f90112233");
    public static final DocTreeOntologyDoc INSTANCE = new DocTreeOntologyDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Ontology — DocTree"; }
    @Override public String summary() { return "A DocTree is a strict, immutable, rooted-tree structure of named nodes; only its leaves bear content; leaves are either Docs (content-bearing) or references to another DocTree's root (structural). Ten axioms — structural (strict rooted tree, ordered children, leaf-or-branch closure, leaf-kind closure); content (branches bear no content, per-Doc single-home); immutability (value-immutable, construction-time well-formedness); identity (tree identity, node addressability). Realised by Catalogue (typed) and ContentTree (data-authored). Definition and axioms only — operational guidance lives in Doctrines."; }
    @Override public String category(){ return "ONTOLOGY"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-ontfirst", OntologyFirstDoc.INSTANCE),
                new DocReference("doc-ontology", DocOntologyDoc.INSTANCE),
                new DocReference("rfc-11",       Rfc0011Doc.INSTANCE),
                new DocReference("rfc-15",       Rfc0015Doc.INSTANCE),
                new DocReference("rfc-16",       Rfc0016Doc.INSTANCE)
        );
    }
}
