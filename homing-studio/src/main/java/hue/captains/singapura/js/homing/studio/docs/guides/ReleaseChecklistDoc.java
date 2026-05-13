package hue.captains.singapura.js.homing.studio.docs.guides;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.docs.doctrines.DualAudienceSkillsDoc;
import hue.captains.singapura.js.homing.studio.docs.releases.Release0_0_11Doc;

import java.util.List;
import java.util.UUID;

/**
 * The canonical release checklist for Homing — extracted from the pattern
 * {@link Release0_0_11Doc} established and used for every release after it.
 *
 * <p>Versions in this project are written in <b>binary</b>: 0.0.11 = three,
 * 0.0.100 = four, 0.0.101 = five, etc. Each release authors a
 * {@code Release<X_Y_Z>Doc} record + classpath .md following the structure
 * here, then registers it in {@code ReleasesCatalogue} + {@code DocBrowser}.</p>
 */
public record ReleaseChecklistDoc() implements ClasspathMarkdownDoc {
    private static final UUID ID = UUID.fromString("a2c7e9f1-4b8d-4f6a-9e3c-8d7b6a5f4e3d");
    public static final ReleaseChecklistDoc INSTANCE = new ReleaseChecklistDoc();

    @Override public UUID   uuid()    { return ID; }
    @Override public String title()   { return "Release Checklist"; }
    @Override public String summary() { return "The seven-step recipe for shipping a Homing release: scope sweep, build green, release Doc + Markdown, three-place registration, re-test, migration skill (if breaking), git tag. Binary versioning convention. Extracted from the pattern established by 0.0.11."; }
    @Override public String category(){ return "GUIDE"; }

    @Override public List<Reference> references() {
        return List.of(
                new DocReference("rel-0-0-11",  Release0_0_11Doc.INSTANCE),
                new DocReference("dual-skills", DualAudienceSkillsDoc.INSTANCE)
        );
    }
}
