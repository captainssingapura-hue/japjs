package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L3_Catalogue;

import java.util.List;

/**
 * Sub-catalogue grouping every foundational doctrine into typed L3
 * categories — Authoring Audience, View Architecture, Container Patterns,
 * Code Discipline, Privacy &amp; Trust. The doctrines themselves live as
 * leaves of their respective L3 sub-catalogues; this L2 holds only the
 * categorisation.
 *
 * <p>Lives under {@link MetaCatalogue} alongside {@link OntologyCatalogue}
 * and the meta-doctrine — doctrines are operational principles for working
 * with the framework's named primitives, which makes them part of the same
 * foundational Meta layer that defines those primitives.</p>
 */
public record DoctrineCatalogue() implements L2_Catalogue<MetaCatalogue, DoctrineCatalogue> {

    public static final DoctrineCatalogue INSTANCE = new DoctrineCatalogue();

    @Override public MetaCatalogue parent() { return MetaCatalogue.INSTANCE; }
    @Override public String name()    { return "Doctrines"; }
    @Override public String summary() { return "The rules that hold the design together. Required reading."; }
    @Override public String badge()   { return "DOCTRINE"; }
    @Override public String icon()    { return "📚"; }

    @Override public List<? extends L3_Catalogue<DoctrineCatalogue, ?>> subCatalogues() {
        return List.of(
                AudienceDoctrinesCatalogue.INSTANCE,
                ViewDoctrinesCatalogue.INSTANCE,
                ContainerDoctrinesCatalogue.INSTANCE,
                CodeDoctrinesCatalogue.INSTANCE,
                ContentDoctrinesCatalogue.INSTANCE,
                TrustDoctrinesCatalogue.INSTANCE
        );
    }
}
