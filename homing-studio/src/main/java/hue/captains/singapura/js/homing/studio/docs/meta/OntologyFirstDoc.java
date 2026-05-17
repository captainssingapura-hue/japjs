package hue.captains.singapura.js.homing.studio.docs.meta;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Meta-Doctrine — Ontology First. Before adding a primitive to the
 * framework, name its ontological category. If an existing category fits,
 * realise the primitive as an instance of that category. If no category
 * fits, file the new ontological entry first; only then implement.
 *
 * <p>The single operational principle that anchors the Meta layer.
 * Distinct from ordinary doctrines (which prescribe how to work with
 * existing primitives): this one prescribes when a new primitive can
 * legitimately enter the framework at all. Catches the recurring failure
 * mode of building a <em>thing</em> without naming <em>what kind of thing
 * it is</em>.</p>
 *
 * <p>Held as a leaf directly under {@code MetaCatalogue} (not under
 * {@code OntologyCatalogue}) because it is a doctrine <em>about</em>
 * ontology, not an ontology entry. Two different roles.</p>
 */
public record OntologyFirstDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("c4d5e6f7-8091-4a23-b045-5c6d7e8f9001");
    public static final OntologyFirstDoc INSTANCE = new OntologyFirstDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Meta-Doctrine — Ontology First"; }
    @Override public String summary() { return "Before adding a primitive to the framework, name its ontological category. If an existing category fits, realise the primitive as an instance of that category. If no category fits, file the new ontological entry first — definition, identity, invariants, relationships, realisations — and only then implement. Catches the recurring failure mode of building a thing without naming what kind of thing it is. The operational principle that makes the Meta layer enforceable rather than merely descriptive."; }
    @Override public String category(){ return "META"; }

    @Override public List<Reference> references() {
        return List.of();
    }
}
