package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Level 3 of the catalogue tree. RFC 0005-ext2 + RFC 0011 CRTP.
 *
 * @param <P>    the concrete L2 parent type
 * @param <Self> CRTP self-bound — the concrete L3's own type
 */
public non-sealed interface L3_Catalogue<P extends L2_Catalogue<?, P>,
                                          Self extends L3_Catalogue<P, Self>>
        extends Catalogue<Self> {
    P parent();
    @Override default List<? extends L4_Catalogue<Self, ?>> subCatalogues() { return List.of(); }
}
