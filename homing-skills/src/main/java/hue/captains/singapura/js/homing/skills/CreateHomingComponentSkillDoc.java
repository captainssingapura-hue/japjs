package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Typed Doc wrapping the {@code create-homing-component} SKILL.md — the
 * recipe for creating customised view-layer UI components in an MPA-shaped
 * Homing studio. Leads with composition + extension over the framework's
 * shipped builders (Card / ListItem / Section / Listing / Header / Footer);
 * the build-new path is strict and conformance-gated.
 *
 * <p>Scope: pure components (function of typed props → DOM Node). MPA only —
 * SPA-shaped components with reactive state / view diffing / unmount cleanup
 * are explicitly out of scope for this skill.</p>
 */
public record CreateHomingComponentSkillDoc() implements ClasspathMarkdownDoc {

    private static final UUID ID = UUID.fromString("7b2d9e4a-3f1c-4e6b-8a5d-2c7e9f4b1d80");
    public static final CreateHomingComponentSkillDoc INSTANCE = new CreateHomingComponentSkillDoc();

    @Override public UUID   uuid()         { return ID; }
    @Override public String title()        { return "Skill — Create a Homing Component"; }
    @Override public String summary()      { return "Create a customised view-layer UI component (MPA only). Decision tree: compose existing builders → extend with a typed CssClass → build new only when markup is structurally different. Strict pure-component contract enforced by the conformance suite."; }
    @Override public String category()     { return "SKILL"; }
    @Override public String resourcePath() { return "claude-skills/create-homing-component/SKILL.md"; }

    @Override public List<Reference> references() { return List.of(); }
}
