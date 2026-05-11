package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Typed Doc wrapping the {@code create-homing-studio} SKILL.md. Overrides
 * {@link #resourcePath()} to point at the same classpath path the CLI dumps
 * — single source of truth per the Dual-Audience Skills doctrine.
 */
public record CreateHomingStudioSkillDoc() implements ClasspathMarkdownDoc {

    private static final UUID ID = UUID.fromString("a4f1c6d8-2e7b-4a3d-9c5f-3b8e2c4d7f01");
    public static final CreateHomingStudioSkillDoc INSTANCE = new CreateHomingStudioSkillDoc();

    @Override public UUID   uuid()         { return ID; }
    @Override public String title()        { return "Skill — Create a Homing Studio"; }
    @Override public String summary()      { return "Bootstrap a new studio on top of homing-studio-base. Catalogue/Plan-shaped studio in 4–5 Java records, no manual JS, conformance baseline included."; }
    @Override public String category()     { return "SKILL"; }
    @Override public String resourcePath() { return "claude-skills/create-homing-studio/SKILL.md"; }

    @Override public List<Reference> references() { return List.of(); }
}
