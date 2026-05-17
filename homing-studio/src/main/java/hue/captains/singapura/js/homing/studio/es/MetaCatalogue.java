package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.meta.OntologyFirstDoc;

import java.util.List;

/**
 * Meta — the framework's foundational layer about its own categories of
 * existence and the operational principle that depends on them. Distinct
 * from {@link DoctrineCatalogue} (which is operational) and from
 * {@link RfcsCatalogue} (which records proposed changes): Meta names
 * <em>what kinds of things exist in the framework</em>, and the single
 * meta-doctrine that makes the layer enforceable.
 *
 * <p>Children:</p>
 * <ul>
 *   <li>{@link OntologyCatalogue} — definitional entries; each names one
 *       type of existence in the framework (Doc, ManagedTree, Viewer,
 *       Studio, …). Initially empty; entries land as their definitions
 *       are sharpened.</li>
 *   <li>{@link OntologyFirstDoc} — the meta-doctrine that anchors the
 *       layer. "Before adding a primitive, name its ontological category."
 *       Held as a leaf directly under Meta (not under Ontology) because
 *       it's a doctrine <em>about</em> Ontology, not an Ontology entry.</li>
 * </ul>
 *
 * <p>Existing {@link DoctrineCatalogue} is intentionally untouched. The
 * Meta layer is purely additive — existing doctrines that have ontological
 * residue can be refined and promoted later, one at a time.</p>
 */
public record MetaCatalogue() implements L1_Catalogue<StudioCatalogue, MetaCatalogue> {

    public static final MetaCatalogue INSTANCE = new MetaCatalogue();

    @Override public StudioCatalogue parent() { return StudioCatalogue.INSTANCE; }
    @Override public String name()    { return "Meta"; }
    @Override public String summary() { return "The framework's categories of existence and the operational principle that depends on them. Ontology names what kinds of things exist; the Ontology First meta-doctrine makes the layer enforceable. The foundational layer beneath Doctrines."; }
    @Override public String badge()   { return "META"; }
    @Override public String icon()    { return "🧭"; }

    @Override public List<? extends L2_Catalogue<MetaCatalogue, ?>> subCatalogues() {
        return List.of(
                OntologyCatalogue.INSTANCE,
                DoctrineCatalogue.INSTANCE
        );
    }

    @Override public List<Entry<MetaCatalogue>> leaves() {
        return List.of(
                Entry.of(this, OntologyFirstDoc.INSTANCE)
        );
    }
}
