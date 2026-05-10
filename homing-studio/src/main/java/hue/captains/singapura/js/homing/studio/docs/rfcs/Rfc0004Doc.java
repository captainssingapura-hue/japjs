package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.OwnedReferencesDoc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.PureComponentViewsDoc;

import java.util.List;
import java.util.UUID;

public record Rfc0004Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("59ebba83-19fa-457f-8bd4-57bef5ec3d91");
    public static final Rfc0004Doc INSTANCE = new Rfc0004Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0004 — Typed Docs, UUIDs, and Visibility"; }
    @Override public String summary() { return "Doc as typed UUID-keyed value with self-provided contents; public/private split; legacy /doc-content removed."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rfc-1", Rfc0001Doc.INSTANCE),
                new DocReference("pcv",   PureComponentViewsDoc.INSTANCE),
                new DocReference("or",    OwnedReferencesDoc.INSTANCE)
        );
    }
}
