package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/** Welcome page for the Homing demo studio — what the studio is, how to navigate. */
public record DemoIntroDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("c0a4f1d8-7e3b-49a2-b5f6-1a8d2c4e7f90");
    public static final DemoIntroDoc INSTANCE = new DemoIntroDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Welcome to the Homing Demo Studio"; }
    @Override public String summary() { return "A tiny dogfood studio — homing-studio-base running on its own, branded with the demo's turtle SvgGroup. Built to validate that downstream installations can spin up a Catalogue/Plan-shaped studio with no plumbing of their own."; }
    @Override public String category(){ return "INTRO"; }

    @Override public List<Reference> references() {
        return List.of();   // standalone — no cross-doc references
    }
}
