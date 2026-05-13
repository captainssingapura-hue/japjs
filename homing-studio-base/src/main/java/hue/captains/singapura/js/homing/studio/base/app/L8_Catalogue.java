package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Level 8 of the catalogue tree — the deepest level the framework permits.
 * RFC 0005-ext2. Adding L9 requires extending {@link Catalogue}'s sealed
 * permits + an L9_Catalogue interface; the sealed-switch dispatch sites
 * in CatalogueRegistry will force exhaustive update.
 *
 * <p>L8 is terminal: there is no L9 type to nest into. {@link #subCatalogues()}
 * is inherited from {@link Catalogue} with an empty default; the framework
 * additionally validates that L8 catalogues contribute no sub-catalogues at
 * boot (a runtime backstop since there's no narrower type to enforce against).</p>
 */
public non-sealed interface L8_Catalogue<P extends L7_Catalogue<?>> extends Catalogue {
    P parent();
    @Override default List<? extends Catalogue> subCatalogues() { return List.of(); }
}
