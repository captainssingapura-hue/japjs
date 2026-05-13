package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0011 — Studio Proxy + Typed Reverse-Ref. Closes the "one tree's root is
 * another tree's leaf" gap that the multi-studio launcher (RFC 0010) exposed:
 * a source L0 catalogue, attached as a leaf inside an umbrella tree, has no
 * parent() — so its breadcrumb walks stop at itself instead of spanning back
 * through the umbrella.
 *
 * <p>The fix is a small typed primitive — {@code StudioProxy<S>} data record
 * + {@code Entry.OfStudio} variant + {@code StudioProxyManager} reverse-ref
 * + breadcrumb augmentation in {@code CatalogueRegistry}. {@code Catalogue}
 * and {@code Entry} stay as they are; no CRTP cascade, no migration of
 * existing catalogue records.</p>
 *
 * <p>The CRTP-heavy alternative (entry-host as type parameter) was re-costed
 * against {@link WeighedComplexityDoc the Weighed Complexity doctrine} and
 * rejected — the perpetual authoring tax across every catalogue would buy
 * one rare bug class as compile-time error, a poor trade.</p>
 */
public record Rfc0011Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("5e8b2a1f-3c4d-4f9e-a7b6-1d8c2e5f9a3b");
    public static final Rfc0011Doc INSTANCE = new Rfc0011Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0011 — Studio Proxy + Typed Reverse-Ref"; }
    @Override public String summary() { return "A typed StudioProxy<S> record + Entry.OfStudio variant + StudioProxyManager reverse-ref let a source L0 be attached as a leaf in an umbrella tree, with breadcrumbs automatically prepending the umbrella chain. No CRTP cascade, no migration of existing catalogues — the heavier alternative was re-costed against the Weighed Complexity doctrine and rejected. Closes the cross-boundary breadcrumb gap left open by RFC 0010 §5."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-5-ext2", Rfc0005Ext2Doc.INSTANCE),
                new DocReference("rfc-9",      Rfc0009Doc.INSTANCE),
                new DocReference("rfc-10",     Rfc0010Doc.INSTANCE),
                new DocReference("doc-cc",     CatalogueContainerDoc.INSTANCE),
                new DocReference("wc",         WeighedComplexityDoc.INSTANCE)
        );
    }
}
