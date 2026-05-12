package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L2_Catalogue;

import java.util.List;

/**
 * L1 sub-catalogue grouping every multi-phase plan tracker in the studio.
 *
 * <p>RFC 0005-ext2 §11: now an intermediate node — plans are organised one
 * level deeper, under {@link RfcJourneysCatalogue} (RFC-driven plans) and
 * {@link OperationsJourneysCatalogue} (cross-cutting refactors / releases /
 * tooling). This catalogue carries no leaves of its own.</p>
 *
 * <p>This is the studio's first L2-deep catalogue and exercises the typed-levels
 * stack at depth 2 — breadcrumbs over any plan now render
 * {@code Homing · studio › Journeys › RFCs › <plan name>} (or
 * {@code … › Operations › <plan name>}).</p>
 */
public record JourneysCatalogue() implements L1_Catalogue<StudioCatalogue> {

    public static final JourneysCatalogue INSTANCE = new JourneysCatalogue();

    @Override public StudioCatalogue parent() { return StudioCatalogue.INSTANCE; }
    @Override public String name()    { return "Journeys"; }
    @Override public String summary() { return "Live trackers for every multi-phase plan in this project — grouped by RFC vs. cross-cutting operations."; }

    @Override public List<L2_Catalogue<JourneysCatalogue>> subCatalogues() {
        return List.of(
                RfcJourneysCatalogue.INSTANCE,
                OperationsJourneysCatalogue.INSTANCE
        );
    }
}
