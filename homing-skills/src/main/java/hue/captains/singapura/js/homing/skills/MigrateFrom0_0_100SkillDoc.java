package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

public record MigrateFrom0_0_100SkillDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("c8e4a2d3-7f1b-4a6e-9b8c-2d5f3e6a4b2d");
    public static final MigrateFrom0_0_100SkillDoc INSTANCE = new MigrateFrom0_0_100SkillDoc();

    @Override public UUID   uuid()         { return ID; }
    @Override public String title()        { return "Skill — Migrate from 0.0.100"; }
    @Override public String summary()      { return "Upgrade a Homing studio from 0.0.100 to 0.0.101. One mechanical change — StudioBootstrap.start(...) is deleted; downstream studios introduce a typed Studio<L0> record and rewrite their *Server.main() to three lines using Umbrella + DefaultFixtures + Bootstrap. Effort: ~5 minutes per studio."; }
    @Override public String category()     { return "SKILL"; }
    @Override public String resourcePath() { return "claude-skills/migrate-from-0-0-100/SKILL.md"; }

    @Override public List<Reference> references() { return List.of(); }
}
