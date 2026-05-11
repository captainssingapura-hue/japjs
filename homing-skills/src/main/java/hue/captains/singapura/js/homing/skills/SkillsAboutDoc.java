package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/** Landing page for the skills mini-studio — what this bundle is, how to use it. */
public record SkillsAboutDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("c2d5f7a9-3b1e-4c8d-95f2-6a4b7e8c2d10");
    public static final SkillsAboutDoc INSTANCE = new SkillsAboutDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "About this skills bundle"; }
    @Override public String summary() { return "What homing-skills is, the two modes it ships, and the doctrine that says both must exist."; }
    @Override public String category(){ return "INTRO"; }

    @Override public List<Reference> references() { return List.of(); }
}
