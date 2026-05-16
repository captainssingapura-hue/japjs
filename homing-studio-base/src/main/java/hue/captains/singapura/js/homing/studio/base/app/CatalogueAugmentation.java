package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.tao.ontology.ValueObject;

import java.util.List;
import java.util.Objects;

/**
 * A framework-managed augmentation applied to a {@link Catalogue}'s
 * serialization by {@link CatalogueGetAction}. Two policies, distinguished
 * by {@link #replace}:
 *
 * <ul>
 *   <li><b>{@code replace = false}</b> — synthetic entries are <em>appended</em>
 *       after the catalogue's own typed {@code subCatalogues()} and
 *       {@code leaves()}. Used for additive injections such as a single
 *       framework-owned tile slotted onto a studio-owned home page.</li>
 *   <li><b>{@code replace = true}</b> — the catalogue's typed entries are
 *       suppressed entirely; only the synthetic entries render. Used when
 *       the framework needs to project a context-scoped view of a catalogue
 *       (e.g. a per-studio variant of the Diagnostics page).</li>
 * </ul>
 *
 * <p>Keyed in the {@link CatalogueGetAction}'s augmentation map by
 * {@link AugKey} — pairing the catalogue's class with an optional {@code
 * context} string. {@code context == null} addresses the catalogue's
 * unscoped page; a non-null context addresses the page reached via
 * {@code /app?app=catalogue&id=<fqn>&context=<value>}.</p>
 */
public record CatalogueAugmentation(
        boolean replace,
        List<SyntheticEntry> entries
) implements ValueObject {

    public CatalogueAugmentation {
        Objects.requireNonNull(entries, "entries");
        entries = List.copyOf(entries);
    }

    /** Composite key: catalogue class + optional context tag. */
    public record AugKey(
            Class<? extends Catalogue<?>> cls,
            String context  // null = unscoped page
    ) implements ValueObject {
        public AugKey {
            Objects.requireNonNull(cls, "cls");
            // context may be null
        }
        public static AugKey of(Class<? extends Catalogue<?>> cls) { return new AugKey(cls, null); }
        public static AugKey of(Class<? extends Catalogue<?>> cls, String context) {
            return new AugKey(cls, context);
        }
    }
}
