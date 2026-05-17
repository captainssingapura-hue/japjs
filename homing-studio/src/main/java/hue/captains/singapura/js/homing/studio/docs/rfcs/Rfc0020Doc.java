package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.TypedContentVocabularyDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0020 — Visual Asset Docs. Formalise the visual-asset Doc family
 * — SvgDoc (already implemented), TableDoc (new), ImageDoc (new) —
 * as first-class registered Docs, each with their own viewer.
 *
 * <p>These are the canonical artifacts that ComposedDoc's visual
 * segments wrap; even when primarily used inline, they exist as
 * standalone, citable, addressable Docs. The proxy-pattern application
 * of ProxyDoc (RFC 0015 §2.5) to inline visual appearances.</p>
 *
 * <p>TableDoc is intentionally slim — no formulas, no interactivity,
 * no editing. The line "tables, not spreadsheets" holds; future
 * enhancement requires a separate RFC.</p>
 *
 * <p>One of the trio (RFC 0018 + RFC 0019 + RFC 0020) realising the
 * Typed Content Vocabulary doctrine.</p>
 */
public record Rfc0020Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("f0a1b2c3-4567-4d89-8023-456789abcdef");
    public static final Rfc0020Doc INSTANCE = new Rfc0020Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0020 — Visual Asset Docs"; }
    @Override public String summary() { return "Formalise the visual-asset Doc family — SvgDoc (already implemented), TableDoc (new, slim JSON/CSV tables), ImageDoc (new, raster images) — as first-class registered Docs. Even when used inline via ComposedDoc segments, each is a standalone Doc with UUID, viewer URL, breadcrumb home. Visual segments are typed proxies (ProxyDoc pattern from RFC 0015 §2.5) wrapping the canonical Doc. TableDoc is intentionally slim: no formulas, no interactivity, no editing — tables, not spreadsheets. SvgDoc + TableDoc are themable per RFC 0017; ImageDoc is Raw tier. Part of the Typed Content Vocabulary trio (RFC 0018/0019/0020)."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-tcv",  TypedContentVocabularyDoc.INSTANCE),
                new DocReference("rfc-14",   Rfc0014Doc.INSTANCE),
                new DocReference("rfc-15",   Rfc0015Doc.INSTANCE),
                new DocReference("rfc-17",   Rfc0017Doc.INSTANCE),
                new DocReference("rfc-18",   Rfc0018Doc.INSTANCE),
                new DocReference("rfc-19",   Rfc0019Doc.INSTANCE),
                new DocReference("doc-wc",   WeighedComplexityDoc.INSTANCE)
        );
    }
}
