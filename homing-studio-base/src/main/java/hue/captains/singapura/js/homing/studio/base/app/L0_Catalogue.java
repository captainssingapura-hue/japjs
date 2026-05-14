package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Root level of the catalogue tree — RFC 0005-ext2 + RFC 0011 CRTP.
 *
 * <p>An L0 catalogue is the studio's root catalogue. It has no parent.
 * Studios register exactly one L0 catalogue at boot via
 * {@link StudioBrand#homeApp()}.</p>
 *
 * @param <Self> CRTP self-bound — the concrete L0's own type. Enables
 *               {@link #leaves()} to return {@code List<Entry<Self>>},
 *               binding each entry to this catalogue at compile time.
 */
public non-sealed interface L0_Catalogue<Self extends L0_Catalogue<Self>>
        extends Catalogue<Self> {
    @Override default List<? extends L1_Catalogue<Self, ?>> subCatalogues() { return List.of(); }
}
