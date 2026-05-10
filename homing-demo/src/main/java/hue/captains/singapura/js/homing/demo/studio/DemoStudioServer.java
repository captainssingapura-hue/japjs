package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.demo.es.CuteAnimal;
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
 * Dedicated demo studio — proves that {@code homing-studio-base} can be
 * consumed by an unrelated module (this one — the demo) with no plumbing
 * beyond the public API. Exists to satisfy the First-User Discipline:
 * the framework needs an in-tree consumer that isn't {@code homing-studio}.
 *
 * <p>The brand mark is {@code CuteAnimal.turtle} — a typed {@link SvgRef}
 * pointing at an SVG asset originally drawn for the SVG-extruder demo.
 * Reusing it as a logo validates that {@code SvgRef} works against assets
 * that weren't designed for branding.</p>
 *
 * <p>Runs on port {@code 8082} so it can run alongside the legacy
 * {@code WonderlandDemoServer} (8080) and the main studio (8080) without
 * conflict.</p>
 */
public class DemoStudioServer {

    public static void main(String[] args) {

        // The four shared AppModules every studio gets. No app-specific code in this server.
        List<AppModule<?, ?>> apps = List.of(
                CatalogueAppHost.INSTANCE,
                PlanAppHost.INSTANCE,
                DocReader.INSTANCE,
                ThemesIntro.INSTANCE
        );

        // The home catalogue. One entry — the intro doc — plus a link to the
        // shared themes-intro page. Demo-style: minimal, illustrative, dogfood.
        List<Catalogue> catalogues = List.of(
                DemoStudio.INSTANCE
        );

        // No plans tracked here — the demo studio is a consumer, not a project artefact.
        List<Plan> plans = List.of();

        // Brand: turtle as the logo, demonstrates SvgRef against a third-party-style asset.
        StudioBrand brand = new StudioBrand(
                "Homing · demo",
                DemoStudio.class,
                new SvgRef<>(CuteAnimal.INSTANCE, new CuteAnimal.turtle()));

        StudioBootstrap.start(8082, apps, catalogues, plans, brand);
    }
}
