package hue.captains.singapura.js.homing.studio.docs.whitepaper;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0001Doc;

import java.util.List;
import java.util.UUID;

public record HomingWhitepaperDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("f0ce8d0a-47c9-42fb-9fab-3758d397824a");
    public static final HomingWhitepaperDoc INSTANCE = new HomingWhitepaperDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Homing — Main White Paper"; }
    @Override public String summary() { return "The full technical design: four-layer architecture, diagrams, positioning."; }
    @Override public String category(){ return "WHITEPAPER"; }

    @Override public List<Reference> references() {
        return List.of(new DocReference("rfc-1", Rfc0001Doc.INSTANCE));
    }
}
