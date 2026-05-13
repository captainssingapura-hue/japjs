package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/** Level 4 of the catalogue tree. RFC 0005-ext2. */
public non-sealed interface L4_Catalogue<P extends L3_Catalogue<?>> extends Catalogue {
    P parent();
    @Override default List<? extends L5_Catalogue<?>> subCatalogues() { return List.of(); }
}
