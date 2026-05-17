package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.TypedContentVocabularyDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.WeighedComplexityDoc;

import java.util.List;
import java.util.UUID;

/**
 * RFC 0018 — Slim Markdown ({@code .mdad}). Defines a disciplined
 * markdown subset (conformance-enforced) that closes the inline-HTML
 * escape hatch by removing the constructs an author would otherwise
 * reach for HTML to express.
 *
 * <p>"Markdown and down" — slim markdown. Used inside
 * {@code ComposedDoc.MarkdownSegment} and (eventually) for new
 * {@code ProseDoc} bodies. Legacy {@code .md} files keep working with
 * the current permissive renderer; {@code .mdad} is the new typed path.</p>
 *
 * <p>One of the trio (RFC 0018 + RFC 0019 + RFC 0020) realising the
 * Typed Content Vocabulary doctrine.</p>
 */
public record Rfc0018Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("d8e9f0a1-2345-4b67-8901-234567890abc");
    public static final Rfc0018Doc INSTANCE = new Rfc0018Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0018 — Slim Markdown (.mdad)"; }
    @Override public String summary() { return "Define .mdad — a disciplined markdown subset (slim, conformance-enforced) that closes the inline-HTML escape hatch by removing constructs with typed alternatives. No inline HTML, no markdown tables (use TableSegment), no inline images (use ImageSegment), no bare external URLs (use typed citations). Used inside ComposedDoc MarkdownSegments. Legacy .md continues to work with the current permissive renderer; .mdad is the new typed path. Conformance scanner enforces at build time. Part of the Typed Content Vocabulary trio (RFC 0018/0019/0020)."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("doc-tcv",  TypedContentVocabularyDoc.INSTANCE),
                new DocReference("rfc-17",   Rfc0017Doc.INSTANCE),
                new DocReference("rfc-19",   Rfc0019Doc.INSTANCE),
                new DocReference("rfc-20",   Rfc0020Doc.INSTANCE),
                new DocReference("doc-wc",   WeighedComplexityDoc.INSTANCE)
        );
    }
}
