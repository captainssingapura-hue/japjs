package hue.captains.singapura.js.homing.studio.docs.comparison;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;

import java.util.UUID;

public record HomingVsReactVueDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("84de3132-fe1d-43fa-a99a-78fa213c1dd3");
    public static final HomingVsReactVueDoc INSTANCE = new HomingVsReactVueDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Homing vs React / Vue"; }
    @Override public String summary() { return "Honest comparison, fair assessment of strengths and gaps."; }
    @Override public String category(){ return "REFERENCE"; }
}
