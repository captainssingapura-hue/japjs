package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.ontology.DocOntologyDoc;
import hue.captains.singapura.js.homing.studio.docs.ontology.DocTreeOntologyDoc;
import hue.captains.singapura.js.homing.studio.docs.ontology.StudioOntologyDoc;
import hue.captains.singapura.js.homing.studio.docs.ontology.ViewerOntologyDoc;

import java.util.List;

/**
 * Ontology — the framework's named categories of existence. Each entry
 * (when added) defines one type of thing that exists in Homing: identity
 * rules, invariants, relationships to other types, and concrete
 * realisations.
 *
 * <p>Distinct from Doctrines. A doctrine prescribes <em>how to act</em>;
 * an ontology entry defines <em>what is there</em>. The narrower scope is
 * the value — ontology answers exactly one question (what exists?),
 * leaving doctrines free to be purely operational.</p>
 *
 * <p>Initially empty. First candidates for promotion: <em>Doc</em> (atomic
 * citable content, the eight-rule definition); <em>ManagedTree</em>
 * (structural container — Catalogue and ContentTree both realise it);
 * <em>Viewer</em> (content-kind renderer); <em>Studio</em> (composable
 * contributor). Each lands as its definition is refined and its
 * conformance test is in place.</p>
 *
 * <p>See {@link OntologyFirstDoc} for the operational principle that
 * makes this layer enforceable: ontological placement precedes
 * implementation.</p>
 */
public record OntologyCatalogue() implements L2_Catalogue<MetaCatalogue, OntologyCatalogue> {

    public static final OntologyCatalogue INSTANCE = new OntologyCatalogue();

    @Override public MetaCatalogue parent() { return MetaCatalogue.INSTANCE; }
    @Override public String name()    { return "Ontology"; }
    @Override public String summary() { return "Definitions of what kinds of things exist in the framework — identity, invariants, relationships, realisations. Each entry names one type of existence. Entries land as definitions sharpen and conformance tests follow."; }
    @Override public String badge()   { return "ONTOLOGY"; }
    @Override public String icon()    { return "🧱"; }

    @Override public List<Entry<OntologyCatalogue>> leaves() {
        return List.of(
                Entry.of(this, DocOntologyDoc.INSTANCE),
                Entry.of(this, DocTreeOntologyDoc.INSTANCE),
                Entry.of(this, ViewerOntologyDoc.INSTANCE),
                Entry.of(this, StudioOntologyDoc.INSTANCE)
        );
    }
}
