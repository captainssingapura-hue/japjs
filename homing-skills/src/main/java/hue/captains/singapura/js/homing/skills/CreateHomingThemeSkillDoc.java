package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Typed Doc wrapping the {@code create-homing-theme} SKILL.md. Overrides
 * {@link #resourcePath()} to point at the same classpath path the CLI dumps
 * — single source of truth per the Dual-Audience Skills doctrine.
 */
public record CreateHomingThemeSkillDoc() implements ClasspathMarkdownDoc {

    private static final UUID ID = UUID.fromString("b8e3a2f4-5c9d-4e1b-87a6-2f5d8c3e9b40");
    public static final CreateHomingThemeSkillDoc INSTANCE = new CreateHomingThemeSkillDoc();

    @Override public UUID   uuid()         { return ID; }
    @Override public String title()        { return "Skill — Create a Homing Theme"; }
    @Override public String summary()      { return "Add a new theme — palette swap, optional textures/serif body/custom dividers. Three tiers (Basic / Identity-charged / Layered) with copy-from references."; }
    @Override public String category()     { return "SKILL"; }
    @Override public String resourcePath() { return "claude-skills/create-homing-theme/SKILL.md"; }

    @Override public List<Reference> references() { return List.of(); }
}
