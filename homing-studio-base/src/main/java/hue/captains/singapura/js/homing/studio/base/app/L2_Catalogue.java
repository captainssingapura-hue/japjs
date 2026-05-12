package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Level 2 of the catalogue tree. RFC 0005-ext2.
 *
 * @param <P> the concrete L1 parent type
 */
public non-sealed interface L2_Catalogue<P extends L1_Catalogue<?>> extends Catalogue {
    P parent();
    @Override default List<? extends L3_Catalogue<?>> subCatalogues() { return List.of(); }
}
