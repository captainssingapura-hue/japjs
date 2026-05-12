package hue.captains.singapura.js.homing.studio.base.app;

import java.util.List;

/**
 * Level 1 of the catalogue tree — direct child of the studio's L0 root.
 * RFC 0005-ext2.
 *
 * <p>Generic over the parent type {@code P} — a concrete L1 catalogue
 * declares which L0 root it lives under. The {@code parent()} method
 * returns the typed parent instance, enabling breadcrumb chains to
 * walk strictly upward via type-level information.</p>
 *
 * <p>{@link #subCatalogues()} is narrowed to {@link L2_Catalogue} — concrete
 * L1 catalogues override it with a list typed by the concrete L1 class.</p>
 *
 * @param <P> the concrete L0 parent type
 */
public non-sealed interface L1_Catalogue<P extends L0_Catalogue> extends Catalogue {
    /** The typed parent — always the same L0 instance, returned by type. */
    P parent();

    @Override default List<? extends L2_Catalogue<?>> subCatalogues() { return List.of(); }
}
