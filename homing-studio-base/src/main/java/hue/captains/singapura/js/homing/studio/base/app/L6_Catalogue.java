package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/** Level 6 of the catalogue tree. RFC 0005-ext2. */
public non-sealed interface L6_Catalogue<P extends L5_Catalogue<?>> extends Catalogue {
    P parent();
    @Override default List<? extends L7_Catalogue<?>> subCatalogues() { return List.of(); }
}
