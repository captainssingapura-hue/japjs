package hue.captains.singapura.js.homing.skills;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.studio.base.StudioBootstrap;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost;

import java.util.List;

/**
 * The mini-studio. Boots a {@link StudioBootstrap} on the given port with
 * one home Catalogue listing every skill in the bundle.
 *
 * <p>No catalogue-specific Java code on this side — every page renders
 * through the framework's built-in {@link CatalogueAppHost} + {@link DocReader}
 * + {@link ThemesIntro}. Skills appear as typed {@code Doc}s; the markdown
 * is rendered with the same chrome any studio gets.</p>
 *
 * <p>Per the Dual-Audience Skills doctrine, this is the human-facing mode.
 * The CLI's {@code --dump} subcommand is the agent-facing mode.</p>
 */
public final class SkillsStudioServer {

    private SkillsStudioServer() {}

    public static void main(String[] args){
        start(8080);
    }

    public static void start(int port) {
        List<AppModule<?, ?>> apps = List.of(
                CatalogueAppHost.INSTANCE,
                PlanAppHost.INSTANCE,         // unused but registered for parity with any studio
                DocReader.INSTANCE,
                ThemesIntro.INSTANCE
        );

        List<Catalogue> catalogues = List.of(SkillsHome.INSTANCE);
        List<Plan> plans = List.of();

        StudioBrand brand = new StudioBrand("Homing · skills", SkillsHome.class);

        StudioBootstrap.start(port, apps, catalogues, plans, brand);
    }
}
