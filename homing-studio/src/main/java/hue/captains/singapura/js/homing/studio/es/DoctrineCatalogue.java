package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;

import java.util.List;

/**
 * Sub-catalogue grouping every foundational doctrine into typed L2
 * categories — Authoring Audience, View Architecture, Container Patterns,
 * Code Discipline, Privacy &amp; Trust. The doctrines themselves live as
 * leaves of their respective L2 sub-catalogues; this L1 holds only the
 * categorisation.
 */
public record DoctrineCatalogue() implements L1_Catalogue<StudioCatalogue, DoctrineCatalogue> {

    public static final DoctrineCatalogue INSTANCE = new DoctrineCatalogue();

    @Override public StudioCatalogue parent() { return StudioCatalogue.INSTANCE; }
    @Override public String name()    { return "Doctrines"; }
    @Override public String summary() { return "The rules that hold the design together. Required reading."; }
    @Override public String badge()   { return "DOCTRINE"; }
    @Override public String icon()    { return "📚"; }

    @Override public List<? extends L2_Catalogue<DoctrineCatalogue, ?>> subCatalogues() {
        return List.of(
                AudienceDoctrinesCatalogue.INSTANCE,
                ViewDoctrinesCatalogue.INSTANCE,
                ContainerDoctrinesCatalogue.INSTANCE,
                CodeDoctrinesCatalogue.INSTANCE,
                TrustDoctrinesCatalogue.INSTANCE
        );
    }
}
