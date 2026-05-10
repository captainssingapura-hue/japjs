package hue.captains.singapura.js.homing.studio.docs.guides;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.rfcs.Rfc0001Doc;

import java.util.List;
import java.util.UUID;

public record UserGuideDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("e730562d-8b18-4bba-abb4-11a03423716d");
    public static final UserGuideDoc INSTANCE = new UserGuideDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "User Guide"; }
    @Override public String summary() { return "Getting-started reference for framework users."; }
    @Override public String category(){ return "REFERENCE"; }

    @Override public List<Reference> references() {
        return List.of(new DocReference("rfc-1", Rfc0001Doc.INSTANCE));
    }
}
