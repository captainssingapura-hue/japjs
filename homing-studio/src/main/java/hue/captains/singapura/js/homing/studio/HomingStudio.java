package hue.captains.singapura.js.homing.studio;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.SvgRef;
import hue.captains.singapura.js.homing.studio.base.Studio;
import hue.captains.singapura.js.homing.studio.base.app.StudioBrand;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.es.DocBrowser;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.es.StudioLogo;
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
 * RFC 0012 — the Homing framework's reference studio. Captures the studio's
 * intrinsic bundle: home catalogue, intrinsic apps ({@link DocBrowser}),
 * plans, and standalone brand. The catalogue closure is derived
 * automatically from {@link StudioCatalogue#subCatalogues()}.
 */
public record HomingStudio() implements Studio<StudioCatalogue> {

    public static final HomingStudio INSTANCE = new HomingStudio();

    @Override
    public StudioCatalogue home() { return StudioCatalogue.INSTANCE; }

    @Override
    public List<AppModule<?, ?>> apps() {
        // DocBrowser is intrinsic to the Homing studio — it's how the studio
        // surfaces its own browseable doc index. Harness apps (CatalogueAppHost,
        // PlanAppHost, DocReader, ThemesIntro) are layered on by DefaultFixtures.
        return List.of(DocBrowser.INSTANCE);
    }

    @Override
    public List<Plan> plans() {
        return List.of(
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
    }

    @Override
    public StudioBrand standaloneBrand() {
        return new StudioBrand(
                "Homing · studio",
                StudioCatalogue.class,
                new SvgRef<>(StudioLogo.INSTANCE, new StudioLogo.logo()));
    }
}
