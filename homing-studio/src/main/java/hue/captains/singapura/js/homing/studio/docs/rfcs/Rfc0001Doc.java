package hue.captains.singapura.js.homing.studio.docs.rfcs;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.brand.RenameToHomingDoc;
import hue.captains.singapura.js.homing.studio.docs.whitepaper.HomingWhitepaperDoc;

import java.util.List;
import java.util.UUID;

public record Rfc0001Doc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("f0dd6846-2698-4ae7-80b1-7e15828c002f");
    public static final Rfc0001Doc INSTANCE = new Rfc0001Doc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "RFC 0001 — App Registry & Typed Nav"; }
    @Override public String summary() { return "Friendly-name URL contract, AppLink<L>, ProxyApp, conformance enforcement."; }
    @Override public String category(){ return "RFC"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rename-doc", RenameToHomingDoc.INSTANCE),
                new DocReference("whitepaper", HomingWhitepaperDoc.INSTANCE)
        );
    }
}
