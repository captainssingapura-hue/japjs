package hue.captains.singapura.js.homing.studio.es;

import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L3_Catalogue;
import hue.captains.singapura.js.homing.studio.docs.doctrines.TypedContentVocabularyDoc;

import java.util.List;

/**
 * L3 sub-catalogue of {@link DoctrineCatalogue} — doctrines about how
 * the framework's <em>content</em> is authored. The content-side mirror
 * of Code Discipline: typed-by-default, sealed-permits extension, no
 * untyped escape hatches — applied to docs and visual assets instead of
 * Java APIs.
 *
 * <p>"You don't really need HTML, just SVGs." — the slogan that captures
 * the family's overall stance.</p>
 */
public record ContentDoctrinesCatalogue()
        implements L3_Catalogue<DoctrineCatalogue, ContentDoctrinesCatalogue> {

    public static final ContentDoctrinesCatalogue INSTANCE = new ContentDoctrinesCatalogue();

    @Override public DoctrineCatalogue parent() { return DoctrineCatalogue.INSTANCE; }
    @Override public String name()    { return "Content Discipline"; }
    @Override public String summary() { return "How the framework's content is authored. Typed primitives only; no HTML escape hatches; sealed-permits extension. The content-side mirror of Code Discipline. \"You don't really need HTML, just SVGs.\""; }
    @Override public String badge()   { return "DOCTRINE"; }
    @Override public String icon()    { return "📝"; }

    @Override public List<Entry<ContentDoctrinesCatalogue>> leaves() {
        return List.of(
                Entry.of(this, TypedContentVocabularyDoc.INSTANCE)
        );
    }
}
