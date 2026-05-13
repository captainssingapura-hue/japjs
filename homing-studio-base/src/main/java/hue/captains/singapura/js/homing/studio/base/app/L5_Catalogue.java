package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/** Level 5 of the catalogue tree. RFC 0005-ext2. */
public non-sealed interface L5_Catalogue<P extends L4_Catalogue<?>> extends Catalogue {
    P parent();
    @Override default List<? extends L6_Catalogue<?>> subCatalogues() { return List.of(); }
}
