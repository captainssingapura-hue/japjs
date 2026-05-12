package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * Typed Doc wrapping the {@code migrate-from-0-0-11} SKILL.md. The breaking
 * changes in 0.0.100 all flow from RFC 0005-ext2 — typed catalogue levels +
 * the sub-catalogue / leaf split. This skill is the mechanical recipe for
 * downstream studios to follow.
 */
public record MigrateFrom0_0_11SkillDoc() implements ClasspathMarkdownDoc {

    private static final UUID ID = UUID.fromString("c8e4a2d3-7f1b-4a6e-9b8c-2d5f3e6a4b1c");
    public static final MigrateFrom0_0_11SkillDoc INSTANCE = new MigrateFrom0_0_11SkillDoc();

    @Override public UUID   uuid()         { return ID; }
    @Override public String title()        { return "Skill — Migrate from 0.0.11"; }
    @Override public String summary()      { return "Upgrade a Homing studio from 0.0.11 to 0.0.100. Six mechanical changes — Catalogue is sealed, entries() splits into subCatalogues() + leaves(), non-root catalogues declare typed parent(). All breaking changes flow from RFC 0005-ext2."; }
    @Override public String category()     { return "SKILL"; }
    @Override public String resourcePath() { return "claude-skills/migrate-from-0-0-11/SKILL.md"; }

    @Override public List<Reference> references() { return List.of(); }
}
