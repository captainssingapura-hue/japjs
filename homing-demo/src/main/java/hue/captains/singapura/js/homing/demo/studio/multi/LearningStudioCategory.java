package hue.captains.singapura.js.homing.demo.studio.multi;

import hue.captains.singapura.js.homing.demo.studio.DemoStudio;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.StudioProxy;

import java.util.List;

/**
 * L1 category — illustrative / didactic studios. Holds a {@link StudioProxy}
 * wrapping {@link DemoStudio}, attaching it as a typed leaf of the umbrella
 * (RFC 0011). Breadcrumbs over any page inside Demo will read
 * {@code 🌐 Homing Studios › 🎓 Learning › <doc>}.
 */
public record LearningStudioCategory()
        implements L1_Catalogue<MultiStudioHome, LearningStudioCategory> {

    public static final LearningStudioCategory INSTANCE = new LearningStudioCategory();

    @Override public MultiStudioHome parent() { return MultiStudioHome.INSTANCE; }
    @Override public String name()    { return "Learning"; }
    @Override public String summary() { return "Illustrative studios — minimal examples, dogfood, reference implementations of framework patterns."; }
    @Override public String badge()   { return "LEARNING"; }
    @Override public String icon()    { return "🎓"; }

    @Override public List<Entry<LearningStudioCategory>> leaves() {
        return List.of(
                Entry.of(this, new StudioProxy<>(
                        DemoStudio.INSTANCE,
                        "Demo",
                        "Minimal example studio — turtle brand, intro doc, themes picker.",
                        "STUDIO",
                        "🎨"))
        );
    }
}
