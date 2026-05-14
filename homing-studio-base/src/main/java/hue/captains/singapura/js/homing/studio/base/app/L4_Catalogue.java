package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/** Level 4 of the catalogue tree. RFC 0005-ext2 + RFC 0011 CRTP. */
public non-sealed interface L4_Catalogue<P extends L3_Catalogue<?, P>,
                                          Self extends L4_Catalogue<P, Self>>
        extends Catalogue<Self> {
    P parent();
    @Override default List<? extends L5_Catalogue<Self, ?>> subCatalogues() { return List.of(); }
}
