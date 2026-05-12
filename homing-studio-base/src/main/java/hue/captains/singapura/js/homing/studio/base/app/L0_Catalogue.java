package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Root level of the catalogue tree — RFC 0005-ext2.
 *
 * <p>An L0 catalogue is the studio's root catalogue. It has no parent
 * (the brand's home-app is itself an L0). Studios register exactly one
 * L0 catalogue at boot via {@link StudioBrand#homeApp()}.</p>
 *
 * <p>{@link #subCatalogues()} is narrowed to {@link L1_Catalogue} — concrete
 * L0 catalogues override it with a list typed by the concrete L0 class:</p>
 *
 * <pre>{@code
 * @Override public List<L1_Catalogue<StudioCatalogue>> subCatalogues() {
 *     return List.of(DoctrineCatalogue.INSTANCE, JourneysCatalogue.INSTANCE);
 * }
 * }</pre>
 *
 * <p>Leaves (Docs, Plans, Apps) live in {@link Catalogue#leaves()}.</p>
 */
public non-sealed interface L0_Catalogue extends Catalogue {
    @Override default List<? extends L1_Catalogue<?>> subCatalogues() { return List.of(); }
}
