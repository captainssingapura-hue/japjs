package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0009 — Catalogue Badge + Icon primitives. Lifts the hardcoded
 * {@code "CATALOGUE"} string in {@code CatalogueGetAction} into two optional
 * default methods on {@link hue.captains.singapura.js.homing.studio.base.app.Catalogue}:
 * {@code badge()} (text label driving the card badge CSS class) and
 * {@code icon()} (short emoji / glyph prefixed to the breadcrumb crumb).
 *
 * <p>Small primitive, framework-wide impact: every sub-catalogue card can
 * now declare its own typed badge instead of the uniform "CATALOGUE"; every
 * breadcrumb crumb can carry a glanceable icon without changing the JSON
 * shape. Sets the foundation for the multi-studio organization pattern
 * (Studios catalogue) without locking it in as the only use case.</p>
 */
public record Rfc0009Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("2b8a7e3f-4d6c-4f1e-9b5a-8e7f3c2d6a1b");
    public static final Rfc0009Doc INSTANCE = new Rfc0009Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0009 — Catalogue Badge + Icon Primitives"; }
    @Override public String summary() { return "Two optional default methods on Catalogue: badge() (text label for card differentiation) and icon() (short glyph prefixed into the breadcrumb crumb text). Small framework change, framework-wide payoff — every sub-catalogue gains typed visual identity. Sets the foundation for the Studios-catalogue pattern targeted at 0.0.101 without inventing it as a one-off."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-cc", CatalogueContainerDoc.INSTANCE)
        );
    }
}
