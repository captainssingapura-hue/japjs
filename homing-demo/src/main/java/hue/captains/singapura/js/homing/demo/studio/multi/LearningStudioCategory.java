package hue.captains.singapura.js.homing.demo.studio.multi;

import hue.captains.singapura.js.homing.demo.studio.DemoStudio;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.Navigable;

import java.util.List;

/**
 * L1 category — illustrative / didactic studios. Holds the {@link DemoStudio}
 * Navigable tile. Categorisation layer in the multi-level multi-studio
 * launcher (RFC 0010 §3 worked example).
 */
public record LearningStudioCategory() implements L1_Catalogue<MultiStudioHome> {

    public static final LearningStudioCategory INSTANCE = new LearningStudioCategory();

    @Override public MultiStudioHome parent() { return MultiStudioHome.INSTANCE; }
    @Override public String name()    { return "Learning"; }
    @Override public String summary() { return "Illustrative studios — minimal examples, dogfood, reference implementations of framework patterns."; }
    @Override public String badge()   { return "LEARNING"; }
    @Override public String icon()    { return "🎓"; }

    @Override public List<Entry> leaves() {
        return List.of(
                Entry.of(new Navigable<>(
                        CatalogueAppHost.INSTANCE,
                        new CatalogueAppHost.Params(DemoStudio.class.getName()),
                        "🎨 Demo",
                        "Minimal example studio — turtle brand, intro doc, themes picker. The First-User Discipline reference implementation."))
        );
    }
}
