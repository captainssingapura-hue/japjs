package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Level 8 — terminal. RFC 0005-ext2 + RFC 0011 CRTP. There is no L9; the
 * sealed permits on {@link Catalogue} would need to extend before adding one.
 */
public non-sealed interface L8_Catalogue<P extends L7_Catalogue<?, P>,
                                          Self extends L8_Catalogue<P, Self>>
        extends Catalogue<Self> {
    P parent();
    @Override default List<? extends Catalogue<?>> subCatalogues() { return List.of(); }
}
