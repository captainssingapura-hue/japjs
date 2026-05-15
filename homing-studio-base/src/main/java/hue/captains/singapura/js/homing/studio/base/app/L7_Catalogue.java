package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/** Level 7 of the catalogue tree. RFC 0005-ext2 + RFC 0011 CRTP. */
public non-sealed interface L7_Catalogue<P extends L6_Catalogue<?, P>,
                                          Self extends L7_Catalogue<P, Self>>
        extends Catalogue<Self> {
    P parent();
    @Override default List<? extends L8_Catalogue<Self, ?>> subCatalogues() { return List.of(); }
}
