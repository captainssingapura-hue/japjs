package hue.captains.singapura.js.homing.studio.base.app.tree;

import hue.captains.singapura.tao.ontology.ValueObject;

import java.util.Objects;

/**
 * RFC 0016 — pointer to viewable content carried by a {@link TreeLeaf}.
 * Two opaque fields:
 *
 * <ul>
 *   <li>{@code kind} — discriminator. Picks the registered ContentViewer
 *       on the client side (kind "doc" → DocReader, "plan" → PlanAppHost,
 *       "svg" → inline SVG render, downstream kinds via
 *       {@code Fixtures.contentViewers()}).</li>
 *   <li>{@code contentId} — kind-specific payload. For most kinds this is
 *       an identifier (UUID for prose, class FQN for plan, etc.) that the
 *       viewer resolves. For the Phase 1 demo's {@code "svg"} kind, the
 *       contentId carries the SVG markup inline so the tree renderer can
 *       embed it directly into the tile — a deliberate shortcut that
 *       avoids introducing a per-SVG addressable endpoint until a real
 *       need surfaces.</li>
 * </ul>
 *
 * <p>Tree leaves never render their content themselves — they delegate to
 * the viewer registered for the content kind. This decouples tree
 * structure from rendering: new content kinds are new ContentViewer
 * registrations, not new tree primitives.</p>
 *
 * @param kind      content-kind discriminator (e.g. "doc", "plan", "svg")
 * @param contentId opaque payload — viewer interprets per kind
 *
 * @since RFC 0016
 */
public record ContentRef(String kind, String contentId) implements ValueObject {
    public ContentRef {
        Objects.requireNonNull(kind,      "ContentRef.kind");
        Objects.requireNonNull(contentId, "ContentRef.contentId");
        if (kind.isBlank()) {
            throw new IllegalArgumentException("ContentRef.kind must not be blank");
        }
    }
}
