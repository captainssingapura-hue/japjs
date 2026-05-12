package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;

import java.util.List;

/**
 * L1 sub-catalogue gathering every shipped RFC, organised thematically rather
 * than by number. The DocBrowser still indexes them flat for search; this
 * catalogue is the structured reading entry point.
 *
 * <p>Sub-divided into three themes — {@link ArchitectureRfcsCatalogue},
 * {@link ContentRfcsCatalogue}, {@link VisualSystemRfcsCatalogue} — exercising
 * a second L1→L2 sub-tree parallel to {@link JourneysCatalogue}.</p>
 */
public record RfcsCatalogue() implements L1_Catalogue<StudioCatalogue> {

    public static final RfcsCatalogue INSTANCE = new RfcsCatalogue();

    @Override public StudioCatalogue parent() { return StudioCatalogue.INSTANCE; }
    @Override public String name()    { return "RFCs"; }
    @Override public String summary() { return "Every RFC the framework has shipped, grouped by theme. The architectural decisions behind Homing — what changed, why, and how."; }

    @Override public List<L2_Catalogue<RfcsCatalogue>> subCatalogues() {
        return List.of(
                ArchitectureRfcsCatalogue.INSTANCE,
                ContentRfcsCatalogue.INSTANCE,
                VisualSystemRfcsCatalogue.INSTANCE
        );
    }
}
