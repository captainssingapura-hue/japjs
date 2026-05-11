package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.Doc;

import java.util.List;

/**
 * The set of skills shipped by this artifact. Explicit, grep-able, single
 * place to register a new skill — same discipline the framework uses for
 * {@code HomingLibsRegistry}, {@code StudioCatalogue}, etc.
 *
 * <p>Per the <i>Dual-Audience Skills</i> doctrine, every entry here is read
 * by both modes:</p>
 *
 * <ul>
 *   <li><b>Dump mode</b> reads {@link Entry#classpathPath()} as bytes and
 *       writes them to {@code <target>/<slug>/SKILL.md}.</li>
 *   <li><b>Serve mode</b> renders {@link Entry#doc()} as a typed Doc through
 *       the studio's {@code DocReader} — and that Doc's {@code resourcePath()}
 *       returns the same {@link Entry#classpathPath()}.</li>
 * </ul>
 *
 * <p>Single source of truth: one {@code SKILL.md} per slug on the classpath.</p>
 */
public final class SkillsManifest {

    /** One row per skill — slug, the typed Doc, and the classpath path of the source .md. */
    public record Entry(String slug, Doc doc) {
        public String classpathPath() { return "claude-skills/" + slug + "/SKILL.md"; }
    }

    /** Every skill shipped by this artifact. Add new skills here.
     *  Order = display order in the home catalogue. The "use-homing-skills"
     *  meta-skill comes first because it's the first thing a downstream user
     *  needs to know — how to consume this bundle. */
    public static final List<Entry> ALL = List.of(
            new Entry("use-homing-skills",    UseHomingSkillsSkillDoc.INSTANCE),
            new Entry("create-homing-studio", CreateHomingStudioSkillDoc.INSTANCE),
            new Entry("create-homing-theme",  CreateHomingThemeSkillDoc.INSTANCE)
    );

    private SkillsManifest() {}
}
