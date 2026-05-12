package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/** Level 7 of the catalogue tree. RFC 0005-ext2. */
public non-sealed interface L7_Catalogue<P extends L6_Catalogue<?>> extends Catalogue {
    P parent();
    @Override default List<? extends L8_Catalogue<?>> subCatalogues() { return List.of(); }
}
