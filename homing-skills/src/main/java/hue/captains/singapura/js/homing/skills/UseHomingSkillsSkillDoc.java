package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Typed Doc wrapping the {@code use-homing-skills} SKILL.md — the recipe a
 * downstream Maven project follows to consume this bundle. Overrides
 * {@link #resourcePath()} to read from the same classpath path the CLI dumps.
 */
public record UseHomingSkillsSkillDoc() implements ClasspathMarkdownDoc {

    private static final UUID ID = UUID.fromString("d3e7b8a5-9c4f-4a2d-86b1-5e7c3f8d2a40");
    public static final UseHomingSkillsSkillDoc INSTANCE = new UseHomingSkillsSkillDoc();

    @Override public UUID   uuid()         { return ID; }
    @Override public String title()        { return "Skill — Use homing-skills from your project"; }
    @Override public String summary()      { return "Add homing-skills as a Maven dependency, then invoke via `mvn exec:java@skills-dump` or `mvn exec:java@skills-serve`. Maven-native consumption — no fat-jar."; }
    @Override public String category()     { return "SKILL"; }
    @Override public String resourcePath() { return "claude-skills/use-homing-skills/SKILL.md"; }

    @Override public List<Reference> references() { return List.of(); }
}
