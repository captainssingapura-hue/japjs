package hue.captains.singapura.js.homing.demo.studio.multi;

import hue.captains.singapura.js.homing.skills.SkillsHome;
import hue.captains.singapura.js.homing.studio.base.app.Entry;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.StudioProxy;

import java.util.List;

/**
 * L1 category — agent-side tooling and integrations. Holds a
 * {@link StudioProxy} wrapping {@link SkillsHome}.
 */
public record ToolingStudioCategory()
        implements L1_Catalogue<MultiStudioHome, ToolingStudioCategory> {

    public static final ToolingStudioCategory INSTANCE = new ToolingStudioCategory();

    @Override public MultiStudioHome parent() { return MultiStudioHome.INSTANCE; }
    @Override public String name()    { return "Tooling"; }
    @Override public String summary() { return "Bridges between Homing and the surrounding agent / developer ecosystem — skills, CLIs, integrations."; }
    @Override public String badge()   { return "TOOLING"; }
    @Override public String icon()    { return "🤖"; }

    @Override public List<Entry<ToolingStudioCategory>> leaves() {
        return List.of(
                Entry.of(this, new StudioProxy<>(
                        SkillsHome.INSTANCE,
                        "Skills",
                        "Claude-Code skill bundle — dump-as-SKILL.md from the CLI or browse them here.",
                        "STUDIO",
                        "📜"))
        );
    }
}
