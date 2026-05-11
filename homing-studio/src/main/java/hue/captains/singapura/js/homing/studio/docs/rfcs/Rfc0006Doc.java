package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0002Doc;
import hue.captains.singapura.js.homing.studio.docs.defects.Defect0003Doc;
import hue.captains.singapura.js.homing.studio.docs.doctrines.EncapsulatedComponentsDoc;

import java.util.List;
import java.util.UUID;

public record Rfc0006Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("e1336f67-9530-4616-bc46-d87d4faf0dd3");
    public static final Rfc0006Doc INSTANCE = new Rfc0006Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0006 — Writing-Media Textures + Wallpaper Backdrops"; }
    @Override public String summary() { return "A new dimension in theme design: separate the texture of WRITING SURFACES (vellum, silk, bamboo, papyrus — applied wherever text sits: cards, doc panes, prose) from the WALLPAPER BACKDROP (atmospheric page-level imagery: Win95 desktop, maple-bridge nocturne). Adopt the historical-textures pack (10 SVGs across 5 light/dark pairs) as the writing-surface library; keep Theme.backdrop() reserved for wallpaper. No code yet — this is the design RFC."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("def-2",            Defect0002Doc.INSTANCE),
                new DocReference("def-3",            Defect0003Doc.INSTANCE),
                new DocReference("doc-encapsulated", EncapsulatedComponentsDoc.INSTANCE),
                new DocReference("rfc-3",            Rfc0003Doc.INSTANCE)
        );
    }
}
