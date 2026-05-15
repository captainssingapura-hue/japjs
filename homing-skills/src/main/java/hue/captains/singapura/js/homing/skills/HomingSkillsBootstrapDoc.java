package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Typed Doc for the {@code homing-skills-bootstrap} SKILL.md — the first skill
 * an agent reads when encountering a project that ships this bundle. Points
 * at the index skill (TOC), names the CLI for re-dumping, and gives heuristics
 * for routing prompts to specific skills.
 */
public record HomingSkillsBootstrapDoc() implements ClasspathMarkdownDoc {

    private static final UUID ID = UUID.fromString("4f1c8a3b-7d6e-4f9c-92a8-3b6d8e1f5c70");
    public static final HomingSkillsBootstrapDoc INSTANCE = new HomingSkillsBootstrapDoc();

    @Override public UUID   uuid()         { return ID; }
    @Override public String title()        { return "Skill — Homing Skills Bootstrap"; }
    @Override public String summary()      { return "First skill an agent reads in a homing-skills-using project. Orients the agent, points at the index TOC, names the dump CLI, gives prompt-routing heuristics. Load before any other Homing skill."; }
    @Override public String category()     { return "SKILL"; }
    @Override public String resourcePath() { return "claude-skills/homing-skills-bootstrap/SKILL.md"; }

    @Override public List<Reference> references() { return List.of(); }
}
