package hue.captains.singapura.js.homing.studio.base.app;

import hue.captains.singapura.tao.ontology.ValueObject;

import java.util.Objects;

/**
 * A specialized catalogue leaf that exists only to illustrate the catalogue
 * node it is for. Carries a short markdown blurb rendered as a hero block
 * above the catalogue's tile grid; not addressable, not citable, not
 * registered as a Doc, no UUID.
 *
 * <p>The DocTree T4 ontology axiom names two leaf kinds — content (Doc)
 * and tree-root reference (StudioProxy). CatalogueIllustration is a third
 * structural carve-out: the "in-place decoration" kind. Its identity is
 * the catalogue position it occupies; outside that catalogue it has no
 * meaning. Multiple illustrations can appear in the same catalogue's
 * {@code leaves()} list; each renders at the position it occupies.</p>
 *
 * <p>Differences from a Doc:</p>
 * <ul>
 *   <li>No UUID — no wire identity, no cross-reference target.</li>
 *   <li>No DocRegistry registration — invisible to {@code /doc?id=…}.</li>
 *   <li>No URL — clicking does nothing; nothing to click.</li>
 *   <li>No references — illustrations don't cite other Docs.</li>
 *   <li>One field — the markdown body.</li>
 * </ul>
 *
 * <p>Usage in a catalogue's {@code leaves()}:</p>
 *
 * <pre>{@code
 * @Override public List<Entry<MyCatalogue>> leaves() {
 *     return List.of(
 *         Entry.of(this, new CatalogueIllustration(
 *             "**Welcome.** This catalogue collects the framework's foundational "
 *           + "principles. Read each entry once; they're short.")),
 *         Entry.of(this, SomeDoc.INSTANCE),
 *         Entry.of(this, AnotherDoc.INSTANCE)
 *     );
 * }
 * }</pre>
 *
 * @param body markdown content; rendered inline above (or interleaved with)
 *             the catalogue's tile grid depending on leaf order
 */
public record CatalogueIllustration(String body) implements ValueObject {
    public CatalogueIllustration {
        Objects.requireNonNull(body, "CatalogueIllustration.body");
    }
}
