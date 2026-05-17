package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.TypedContentVocabularyDoc;
import hue.captains.singapura.js.homing.studio.docs.ontology.ViewerOntologyDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0019 — ComposedDoc. Introduce a Doc kind whose body is an ordered
 * sequence of typed segments — MarkdownSegment (.mdad prose), SvgSegment
 * (proxy to SvgDoc), TableSegment (proxy to TableDoc), ImageSegment
 * (proxy to ImageDoc), future extensions via sealed permits.
 *
 * <p>The new default Doc shape, replacing the "markdown with inline HTML
 * escapes" pattern. Visual segments are typed proxies to canonical Docs;
 * same content can appear in multiple ComposedDocs without duplication.
 * Server-rendered TOC; pure ADT segment family; no cross-segment
 * references.</p>
 *
 * <p>One of the trio (RFC 0018 + RFC 0019 + RFC 0020) realising the
 * Typed Content Vocabulary doctrine.</p>
 */
public record Rfc0019Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("e9f0a1b2-3456-4c78-8012-3456789abcde");
    public static final Rfc0019Doc INSTANCE = new Rfc0019Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0019 — ComposedDoc"; }
    @Override public String summary() { return "Introduce ComposedDoc — a Doc kind whose body is an ordered sequence of typed segments (MarkdownSegment, SvgSegment, TableSegment, ImageSegment, future extensions). The new default Doc shape, replacing the markdown-with-inline-HTML pattern. Visual segments are typed proxies to canonical Docs (RFC 0020); same content can appear in multiple ComposedDocs without duplication. Server-rendered TOC derived from segments and .mdad headings; pure ADT segment family; no cross-segment references; no common segment base. Eliminates need for HtmlDoc. Part of the Typed Content Vocabulary trio (RFC 0018/0019/0020)."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-tcv",         TypedContentVocabularyDoc.INSTANCE),
                new DocReference("rfc-15",          Rfc0015Doc.INSTANCE),
                new DocReference("rfc-17",          Rfc0017Doc.INSTANCE),
                new DocReference("rfc-18",          Rfc0018Doc.INSTANCE),
                new DocReference("rfc-20",          Rfc0020Doc.INSTANCE),
                new DocReference("viewer-ontology", ViewerOntologyDoc.INSTANCE)
        );
    }
}
