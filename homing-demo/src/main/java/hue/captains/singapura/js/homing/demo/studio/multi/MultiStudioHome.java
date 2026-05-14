package hue.captains.singapura.js.homing.demo.studio.multi;

import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;

import java.util.List;

/**
 * Multi-studio launcher — the L0 home of the demo server, composing three
 * studios (Demo, Skills, Homing) into three typed L1 categories
 * (Learning / Tooling / Core). Each category holds one {@code StudioProxy}
 * tile (RFC 0011) wrapping the source studio's L0; the breadcrumb chain
 * spans the boundary automatically.
 *
 * <p>RFC 0009 / 0010 / 0011 simultaneous worked example.</p>
 */
public record MultiStudioHome() implements L0_Catalogue<MultiStudioHome> {

    public static final MultiStudioHome INSTANCE = new MultiStudioHome();

    @Override public String name()    { return "Homing Studios"; }
    @Override public String summary() { return "Three categorised studios composed onto one server — Learning, Tooling, Core."; }
    @Override public String icon()    { return "🌐"; }

    @Override public List<? extends L1_Catalogue<MultiStudioHome, ?>> subCatalogues() {
        return List.of(
                LearningStudioCategory.INSTANCE,
                ToolingStudioCategory.INSTANCE,
                CoreStudioCategory.INSTANCE
        );
    }
}
