package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Level 3 of the catalogue tree. RFC 0005-ext2.
 *
 * @param <P> the concrete L2 parent type
 */
public non-sealed interface L3_Catalogue<P extends L2_Catalogue<?>> extends Catalogue {
    P parent();
    @Override default List<? extends L4_Catalogue<?>> subCatalogues() { return List.of(); }
}
