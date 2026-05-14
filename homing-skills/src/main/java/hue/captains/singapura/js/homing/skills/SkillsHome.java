package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocProvider;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;

import java.util.ArrayList;
import java.util.List;

/**
 * Home Catalogue for the skills mini-studio. Lists the About doc + every
 * registered skill as typed Card entries. Also a {@link DocProvider}
 * contributing every shipped Doc to the studio's {@code DocRegistry}.
 *
 * <p>Per the Dual-Audience Skills doctrine, the entries iterate over
 * {@link SkillsManifest#ALL} — adding a new skill there automatically
 * surfaces it as a new tile here (and dumps it via the CLI). No per-skill
 * registration on this side.</p>
 */
public record SkillsHome() implements L0_Catalogue<SkillsHome>, DocProvider {

    public static final SkillsHome INSTANCE = new SkillsHome();

    @Override public String name()    { return "Homing Skills"; }
    @Override public String summary() { return "Skill recipes for working with the Homing framework — readable by humans here, dumpable as SKILL.md files for Claude Code agents via the CLI."; }

    @Override public List<Entry<SkillsHome>> leaves() {
        List<Entry<SkillsHome>> entries = new ArrayList<>();
        entries.add(Entry.of(this, SkillsAboutDoc.INSTANCE));
        for (SkillsManifest.Entry e : SkillsManifest.ALL) {
            entries.add(Entry.of(this, e.doc()));
        }
        return List.copyOf(entries);
    }

    @Override public List<Doc> docs() {
        List<Doc> docs = new ArrayList<>();
        docs.add(SkillsAboutDoc.INSTANCE);
        for (SkillsManifest.Entry e : SkillsManifest.ALL) {
            docs.add(e.doc());
        }
        return List.copyOf(docs);
    }
}
