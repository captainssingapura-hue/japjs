package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.CatalogueContainerDoc;

import java.util.List;
import java.util.UUID;

public record Rfc0005Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("fd49ee55-e432-430e-b246-bd48d426032f");
    public static final Rfc0005Doc INSTANCE = new Rfc0005Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0005 — Typed Catalogue Containers"; }
    @Override public String summary() { return "Operationalises the Catalogues-as-Containers doctrine: a minimal Catalogue interface (name + summary + entries), sealed Entry (Doc | Catalogue), class-as-identity, no sections, no per-tile fields, no presentation directives in catalogue data."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("cc",         CatalogueContainerDoc.INSTANCE),
                new DocReference("rfc-1",      Rfc0001Doc.INSTANCE),
                new DocReference("rfc-4",      Rfc0004Doc.INSTANCE),
                new DocReference("rfc-4-ext1", Rfc0004Ext1Doc.INSTANCE)
        );
    }
}
