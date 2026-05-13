package hue.captains.singapura.js.homing.demo.studio.multi;

import hue.captains.singapura.js.homing.studio.base.app.L0_Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.L1_Catalogue;

import java.util.List;

/**
 * Multi-studio launcher — the L0 home of the demo server, composing three
 * studios (Demo, Skills, Homing) into three typed L1 categories
 * (Learning / Tooling / Core). Each category holds one Navigable tile
 * pointing at the source studio's L0 page; clicking takes the user into
 * that studio's full L0 page (registered in the same server's
 * {@code CatalogueRegistry} so it resolves locally).
 *
 * <p>RFC 0010 worked example, RFC 0009 worked example simultaneously —
 * each category exercises {@code badge()} + {@code icon()} for visible
 * differentiation, and the L0→L1→leaf shape exercises the typed-levels
 * stack at the umbrella side. The source studios remain self-contained
 * L0 trees; only the launcher is multi-level on its own side.</p>
 *
 * <p>Trade-off accepted: clicking a tile navigates into the source L0
 * page whose breadcrumb starts at itself (L0s have no parent). The brand
 * link in the header returns to this umbrella in one click.</p>
 */
public record MultiStudioHome() implements L0_Catalogue {

    public static final MultiStudioHome INSTANCE = new MultiStudioHome();

    @Override public String name()    { return "Homing Studios"; }
    @Override public String summary() { return "Three categorised studios composed onto one server — Learning, Tooling, Core."; }
    @Override public String icon()    { return "🌐"; }

    @Override public List<L1_Catalogue<MultiStudioHome>> subCatalogues() {
        return List.of(
                LearningStudioCategory.INSTANCE,
                ToolingStudioCategory.INSTANCE,
                CoreStudioCategory.INSTANCE
        );
    }
}
