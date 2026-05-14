package hue.captains.singapura.js.homing.demo.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.demo.es.CuteAnimal;
import hue.captains.singapura.js.homing.demo.studio.multi.CoreStudioCategory;
import hue.captains.singapura.js.homing.demo.studio.multi.LearningStudioCategory;
import hue.captains.singapura.js.homing.demo.studio.multi.MultiStudioHome;
import hue.captains.singapura.js.homing.demo.studio.multi.ToolingStudioCategory;
import hue.captains.singapura.js.homing.skills.SkillsHome;
import hue.captains.singapura.js.homing.studio.base.StudioBootstrap;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost;
import hue.captains.singapura.js.homing.studio.es.ArchitectureRfcsCatalogue;
import hue.captains.singapura.js.homing.studio.es.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.es.ContentRfcsCatalogue;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.es.DoctrineCatalogue;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.OperationsJourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.ReleasesCatalogue;
import hue.captains.singapura.js.homing.studio.es.RfcJourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.RfcsCatalogue;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.es.VisualSystemRfcsCatalogue;
import hue.captains.singapura.js.homing.studio.instruments.InstrumentsPlanData;
import hue.captains.singapura.js.homing.studio.release.V1PlanData;
import hue.captains.singapura.js.homing.studio.rename.RenamePlanData;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001PlanData;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002PlanData;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1PlanData;
import hue.captains.singapura.js.homing.studio.rfc0004.Rfc0004PlanData;
import hue.captains.singapura.js.homing.studio.rfc0004ext1.Rfc0004Ext1PlanData;
import hue.captains.singapura.js.homing.studio.rfc0005.Rfc0005PlanData;
import hue.captains.singapura.js.homing.studio.rfc0005ext1.Rfc0005Ext1PlanData;

import java.util.List;

/**
 * Multi-studio demo server — composes three studios (Demo, Skills, Homing)
 * onto one port via the {@link MultiStudioHome} launcher catalogue
 * (RFC 0009 worked example). Every source studio's catalogues, plans, and
 * Docs are registered in this server's registries so navigation lands
 * locally; the launcher's three Navigables point at each source L0.
 *
 * <p>Original brand mark ({@code CuteAnimal.turtle}) retained as the
 * umbrella's logo — the same SvgRef pattern, now over the multi-studio
 * umbrella rather than the single Demo studio.</p>
 *
 * <p>Runs on port {@code 8082} alongside the standalone single-studio
 * servers ({@code homing-studio} on {@code 8080}, {@code homing-skills}
 * on its own port) without conflict.</p>
 */
public class DemoStudioServer {

    public static void main(String[] args) {

        List<AppModule<?, ?>> apps = List.of(
                CatalogueAppHost.INSTANCE,
                PlanAppHost.INSTANCE,
                DocReader.INSTANCE,
                DocBrowser.INSTANCE,            // the Homing studio expects this AppModule
                ThemesIntro.INSTANCE
        );

        // All four L0s + every descendant catalogue. The umbrella is the
        // homeApp; the other three L0s are reachable only via the umbrella's
        // Navigable tiles (which point at /app?app=catalogue&id=<class>).
        // Each source studio's L0 keeps its own self-contained subtree.
        List<Catalogue<?>> catalogues = List.of(
                // Umbrella + three L1 categories (Learning / Tooling / Core).
                // The categories hold Navigable tiles pointing at each source L0.
                MultiStudioHome.INSTANCE,
                LearningStudioCategory.INSTANCE,
                ToolingStudioCategory.INSTANCE,
                CoreStudioCategory.INSTANCE,

                // Demo studio sub-tree (just the L0 — DemoStudio has no sub-catalogues)
                DemoStudio.INSTANCE,

                // Skills studio sub-tree (just the L0 — SkillsHome has no sub-catalogues)
                SkillsHome.INSTANCE,

                // Homing studio sub-tree — full set per StudioServer.main()
                StudioCatalogue.INSTANCE,
                DoctrineCatalogue.INSTANCE,
                RfcsCatalogue.INSTANCE,
                ArchitectureRfcsCatalogue.INSTANCE,
                ContentRfcsCatalogue.INSTANCE,
                VisualSystemRfcsCatalogue.INSTANCE,
                JourneysCatalogue.INSTANCE,
                RfcJourneysCatalogue.INSTANCE,
                OperationsJourneysCatalogue.INSTANCE,
                BuildingBlocksCatalogue.INSTANCE,
                ReleasesCatalogue.INSTANCE
        );

        // Homing studio's full plan list — needed so any catalogue entry
        // referencing a plan resolves at boot. Demo + Skills declare no plans.
        List<Plan> plans = List.of(
                Rfc0001PlanData.INSTANCE,
                Rfc0002PlanData.INSTANCE,
                Rfc0002Ext1PlanData.INSTANCE,
                RenamePlanData.INSTANCE,
                Rfc0004PlanData.INSTANCE,
                Rfc0004Ext1PlanData.INSTANCE,
                Rfc0005PlanData.INSTANCE,
                Rfc0005Ext1PlanData.INSTANCE,
                V1PlanData.INSTANCE,
                InstrumentsPlanData.INSTANCE
        );

        // Brand: turtle stays as the logo — same SvgRef pattern as the
        // previous single-studio demo, now branded as the multi-studio
        // umbrella. The label flows through AppMeta into <title>.
        StudioBrand brand = new StudioBrand(
                "Homing · multi-studio · demo",
                MultiStudioHome.class,
                new SvgRef<>(CuteAnimal.INSTANCE, new CuteAnimal.turtle()));

        StudioBootstrap.start(8082, apps, catalogues, plans, brand);
    }
}
