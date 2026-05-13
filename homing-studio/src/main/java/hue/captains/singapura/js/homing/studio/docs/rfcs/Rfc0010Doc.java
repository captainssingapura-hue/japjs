package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0010 — Multi-Studio Launcher Pattern. Documents the single-process /
 * single-port composition of multiple studios onto one server via an
 * "umbrella" L0 catalogue whose {@code leaves()} are {@link
 * hue.captains.singapura.js.homing.studio.base.app.Navigable} tiles pointing
 * at each source studio's L0 page.
 *
 * <p>Zero framework changes — purely a use of existing primitives (typed
 * catalogue levels from RFC 0005-ext2, badge + icon from RFC 0009,
 * CatalogueAppHost's typed Params). The reference implementation lives in
 * {@code homing-demo}'s {@code MultiStudioHome} + the updated
 * {@code DemoStudioServer.main()}.</p>
 */
public record Rfc0010Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("9c4f6e2a-3d8b-4f5e-a1c7-6b2e8d4f3a9c");
    public static final Rfc0010Doc INSTANCE = new Rfc0010Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0010 — Multi-Studio Launcher Pattern"; }
    @Override public String summary() { return "Compose multiple studios on a single server / port via an umbrella L0 catalogue whose leaves are Navigable tiles pointing at each source studio's L0 page. Zero framework changes — uses existing primitives only. Trade-off accepted: each source studio's breadcrumb chain stops at its own L0 (no cross-boundary chain back to the umbrella). Brand-link bridges the gap. Reference impl in homing-demo's MultiStudioHome."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-5-ext2", Rfc0005Ext2Doc.INSTANCE),
                new DocReference("rfc-9",      Rfc0009Doc.INSTANCE),
                new DocReference("doc-cc",     CatalogueContainerDoc.INSTANCE)
        );
    }
}
