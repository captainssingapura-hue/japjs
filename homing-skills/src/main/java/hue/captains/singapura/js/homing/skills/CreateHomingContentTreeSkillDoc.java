package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Typed Doc wrapping the {@code create-homing-content-tree} SKILL.md.
 * Per the Dual-Audience Skills doctrine, the Doc and the dumped SKILL.md
 * share a single classpath source — the doc's {@link #resourcePath()}
 * returns the same path the CLI writes.
 */
public record CreateHomingContentTreeSkillDoc() implements ClasspathMarkdownDoc {

    private static final UUID ID = UUID.fromString("c8e2a5d9-7f31-4b6e-9c84-1a3e5b8d2f47");
    public static final CreateHomingContentTreeSkillDoc INSTANCE =
            new CreateHomingContentTreeSkillDoc();

    @Override public UUID   uuid()         { return ID; }
    @Override public String title()        { return "Skill — Create a Homing ContentTree"; }
    @Override public String summary()      { return "Add a data-authored tree of tagged / categorised content to a Homing studio (RFC 0016). Sibling of Catalogue — same UI surface, different authoring model. Two records + a Fixtures override."; }
    @Override public String category()     { return "SKILL"; }
    @Override public String resourcePath() { return "claude-skills/create-homing-content-tree/SKILL.md"; }

    @Override public List<Reference> references() { return List.of(); }
}
