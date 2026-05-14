package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.studio.base.StudioBootstrap;
import hue.captains.singapura.js.homing.studio.base.app.Catalogue;
import hue.captains.singapura.js.homing.studio.base.app.CatalogueAppHost;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;
import hue.captains.singapura.js.homing.studio.base.theme.ThemesIntro;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppHost;
import hue.captains.singapura.js.homing.studio.es.BuildingBlocksCatalogue;
import hue.captains.singapura.js.homing.studio.es.ReleasesCatalogue;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.es.DoctrineCatalogue;
import hue.captains.singapura.js.homing.studio.es.ArchitectureRfcsCatalogue;
import hue.captains.singapura.js.homing.studio.es.ContentRfcsCatalogue;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.OperationsJourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.RfcJourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.RfcsCatalogue;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.es.VisualSystemRfcsCatalogue;
import hue.captains.singapura.js.homing.studio.es.StudioLogo;
import hue.captains.singapura.js.homing.studio.rename.RenamePlanData;
import hue.captains.singapura.js.homing.studio.rfc0001.Rfc0001PlanData;
import hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002PlanData;
import hue.captains.singapura.js.homing.studio.rfc0002ext1.Rfc0002Ext1PlanData;
import hue.captains.singapura.js.homing.studio.rfc0004.Rfc0004PlanData;
import hue.captains.singapura.js.homing.studio.rfc0004ext1.Rfc0004Ext1PlanData;
import hue.captains.singapura.js.homing.studio.rfc0005.Rfc0005PlanData;
import hue.captains.singapura.js.homing.studio.rfc0005ext1.Rfc0005Ext1PlanData;
import hue.captains.singapura.js.homing.studio.instruments.InstrumentsPlanData;
import hue.captains.singapura.js.homing.studio.release.V1PlanData;

import java.util.List;

/**
 * Entry point for the Homing studio. Per RFC 0005 + RFC 0005-ext1, registers an
 * explicit list of AppModules (the catalogue + plan AppHosts + DocReader/DocBrowser),
 * an explicit list of catalogues, and an explicit list of plans, plus a {@link StudioBrand}.
 */
public class StudioServer {

    public static void main(String[] args) {
        // Just three AppModules now — the catalogue host, the plan host, and the
        // doc reader. Everything else is data registered via dedicated lists.
        List<AppModule<?, ?>> apps = List.of(
                CatalogueAppHost.INSTANCE,        // /app?app=catalogue&id=<fqn>
                PlanAppHost.INSTANCE,             // /app?app=plan&id=<fqn>
                DocReader.INSTANCE,               // /app?app=doc-reader&doc=<uuid>
                DocBrowser.INSTANCE,              // /app?app=doc-browser
                ThemesIntro.INSTANCE              // /app?app=themes
        );

        // RFC 0005: explicit catalogue list registered alongside.
        List<Catalogue<?>> catalogues = List.of(
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

        // RFC 0005-ext1: explicit plan list registered alongside.
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

        // RFC 0005: brand as injectable per-installation config.
        StudioBrand brand = new StudioBrand(
                "Homing · studio",
                StudioCatalogue.class,
                new SvgRef<>(StudioLogo.INSTANCE, new StudioLogo.logo()));

        StudioBootstrap.start(8080, apps, catalogues, plans, brand);
    }
}
