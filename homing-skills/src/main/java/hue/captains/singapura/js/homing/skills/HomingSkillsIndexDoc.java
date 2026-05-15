package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * The meta-skill — table of contents listing every skill in
 * {@link SkillsManifest#ALL}. Body generated dynamically from the manifest
 * so it can never drift from the actual shipped set.
 *
 * <p>Implements {@link Doc} directly (not {@code ClasspathMarkdownDoc}) so
 * {@link #contents()} builds the markdown at call time instead of reading
 * from a static {@code .md}. {@link SkillsDumper} routes through
 * {@code doc.contents()} for every entry, so dynamic content lands at the
 * dumped path the same way static content does.</p>
 */
public record HomingSkillsIndexDoc() implements Doc {

    private static final UUID ID = UUID.fromString("8a3e6f2c-5d1b-4e7a-bf48-9c2e6a3d8f10");
    public static final HomingSkillsIndexDoc INSTANCE = new HomingSkillsIndexDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Skill — Homing Skills Index"; }
    @Override public String summary() { return "Table of contents for every shipped Homing skill — slug + summary + when-to-use. Generated from SkillsManifest.ALL at access time; cannot drift from the real shipped set."; }
    @Override public String category(){ return "SKILL"; }

    @Override public List<Reference> references() { return List.of(); }

    /**
     * Build the index markdown dynamically. Walks {@link SkillsManifest#ALL}
     * and emits a YAML frontmatter block + a table of every skill (including
     * this index itself — visible in the listing, never hidden).
     */
    @Override
    public String contents() {
        StringBuilder sb = new StringBuilder();

        sb.append("---\n");
        sb.append("name: homing-skills-index\n");
        sb.append("description: Use this skill to discover what skills are available in the Homing framework's skills bundle. ");
        sb.append("Returns a table of contents listing every skill with its slug, one-line summary, and a brief when-to-use. ");
        sb.append("Generated from SkillsManifest at dump time — cannot drift. Triggers — \"what skills are available\", \"list homing skills\", \"homing-skills index\", \"TOC of homing skills\", \"what can homing-skills do\". ");
        sb.append("Read this RIGHT AFTER homing-skills-bootstrap on first encounter with a homing-skills-using project.\n");
        sb.append("---\n\n");

        sb.append("# Homing Skills — Index\n\n");
        sb.append("Auto-generated from `SkillsManifest.ALL`. Every skill the bundle ships, in dump order:\n\n");

        sb.append("| Slug | Summary | When to use |\n");
        sb.append("|---|---|---|\n");
        for (SkillsManifest.Entry entry : SkillsManifest.ALL) {
            String slug    = entry.slug();
            String title   = safe(entry.doc().title());
            String summary = safe(entry.doc().summary());
            String whenTo  = whenToUse(slug);
            sb.append("| `").append(slug).append("` ");
            sb.append("| **").append(title).append("** — ").append(summary).append(" ");
            sb.append("| ").append(whenTo).append(" |\n");
        }

        sb.append("\n## How to load a specific skill\n\n");
        sb.append("Each skill lives at `.claude/skills/<slug>/SKILL.md` after the bundle is dumped. ");
        sb.append("Read the body of the matched skill in full; the framework's skills are dense — short bodies, every line earns its place.\n\n");

        sb.append("## How to refresh this index\n\n");
        sb.append("Re-run the dump CLI:\n\n");
        sb.append("```bash\n");
        sb.append("mvn -pl homing-skills exec:java \\\n");
        sb.append("    -Dexec.mainClass=hue.captains.singapura.js.homing.skills.SkillsCli \\\n");
        sb.append("    -Dexec.args=\"--dump .claude/skills\"\n");
        sb.append("```\n\n");
        sb.append("The CLI walks `SkillsManifest.ALL`, calls `doc.contents()` on each entry, and writes the bytes to disk. This index's `contents()` regenerates from the live manifest — so adding a new skill to `SkillsManifest.ALL` and re-dumping is the entire update procedure. No manual TOC maintenance.\n\n");

        sb.append("## Convention\n\n");
        sb.append("- Skill slugs are kebab-case, ASCII, no version numbers in active skills (version-pinned skills carry the version in their slug — e.g. `migrate-from-0-0-11`).\n");
        sb.append("- Triggers in YAML `description` are deliberately verbose: agents match against them lexically. Skip-conditions matter as much as include-conditions.\n");
        sb.append("- Skills cross-reference each other. The bootstrap skill is the entry point; this index is the catalog; the others are domain-specific.\n");

        return sb.toString();
    }

    /** Per-slug when-to-use hint, hand-curated for the bundle's known skills.
     *  Falls back to a generic prompt for unknown slugs so newly-added skills
     *  show up immediately even if the hint table hasn't been updated. */
    private static String whenToUse(String slug) {
        return switch (slug) {
            case "homing-skills-bootstrap" -> "First read in any homing-skills-using project.";
            case "homing-skills-index"     -> "Read immediately after bootstrap to see what's available.";
            case "use-homing-skills"       -> "User wants to consume the bundle from their own Maven project.";
            case "create-homing-studio"    -> "User wants to bootstrap a new studio on top of homing-studio-base.";
            case "create-homing-theme"     -> "User wants to add a new theme (palette / textures / typography).";
            case "create-homing-component" -> "User wants a customised view-layer UI component (MPA, compose-first).";
            case "migrate-from-0-0-11"     -> "User is upgrading a Homing studio from 0.0.11 to 0.0.100+.";
            default                        -> "See the skill's own YAML description for trigger phrases.";
        };
    }

    private static String safe(String s) {
        if (s == null) return "";
        // Markdown table cells can't contain raw pipes/newlines.
        return s.replace("|", "\\|").replace("\n", " ").trim();
    }
}
