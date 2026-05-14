package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Level 2 of the catalogue tree. RFC 0005-ext2 + RFC 0011 CRTP.
 *
 * @param <P>    the concrete L1 parent type
 * @param <Self> CRTP self-bound — the concrete L2's own type
 */
public non-sealed interface L2_Catalogue<P extends L1_Catalogue<?, P>,
                                          Self extends L2_Catalogue<P, Self>>
        extends Catalogue<Self> {
    P parent();
    @Override default List<? extends L3_Catalogue<Self, ?>> subCatalogues() { return List.of(); }
}
