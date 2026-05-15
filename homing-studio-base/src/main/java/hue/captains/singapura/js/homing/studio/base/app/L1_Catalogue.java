package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Level 1 of the catalogue tree. RFC 0005-ext2 + RFC 0011 CRTP.
 *
 * @param <P>    the concrete L0 parent type
 * @param <Self> CRTP self-bound — the concrete L1's own type
 */
public non-sealed interface L1_Catalogue<P extends L0_Catalogue<P>,
                                          Self extends L1_Catalogue<P, Self>>
        extends Catalogue<Self> {
    P parent();
    @Override default List<? extends L2_Catalogue<Self, ?>> subCatalogues() { return List.of(); }
}
