package hue.captains.singapura.js.homing.studio.base.composed;

import hue.captains.singapura.js.homing.studio.base.table.TableDoc;

import java.util.Objects;
import java.util.Optional;

/**
 * RFC 0019 Phase 3 + RFC 0020 — proxy reference to a registered
 * {@link TableDoc} embedded inline within a {@link ComposedDoc}.
 *
 * <p>Mirrors the {@link SvgSegment} pattern: the canonical artifact is
 * the TableDoc — registered, addressable, citable, themable. This segment
 * wraps it with an optional per-appearance caption override; the table
 * data itself isn't duplicated, the segment is a thin reference.</p>
 *
 * <p>Caption rendering (same shape as SvgSegment):</p>
 * <ul>
 *   <li>{@code captionOverride.present} → use the override</li>
 *   <li>{@code captionOverride.empty} → fall back to the doc's title</li>
 * </ul>
 *
 * @param doc             the registered TableDoc this segment references
 * @param captionOverride optional caption specific to this appearance
 *
 * @since RFC 0019 Phase 3
 */
public record TableSegment(TableDoc doc, Optional<String> captionOverride) implements Segment {
    public TableSegment {
        Objects.requireNonNull(doc,             "TableSegment.doc");
        Objects.requireNonNull(captionOverride, "TableSegment.captionOverride (use Optional.empty)");
    }

    /** Convenience — no caption override; falls through to the TableDoc's title. */
    public TableSegment(TableDoc doc) {
        this(doc, Optional.empty());
    }

    /** The caption to render — explicit override, or doc.title() when blank. */
    public String resolvedCaption() {
        return captionOverride.orElse(doc.title());
    }
}
