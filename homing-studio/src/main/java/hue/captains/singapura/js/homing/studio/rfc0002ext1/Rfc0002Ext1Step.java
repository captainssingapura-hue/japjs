package hue.captains.singapura.js.homing.studio.rfc0002ext1;

import hue.captains.singapura.js.homing.core.AppLink;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.studio.base.app.DocReader;
import hue.captains.singapura.js.homing.studio.es.JourneysCatalogue;
import hue.captains.singapura.js.homing.studio.es.StudioCatalogue;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanRenderer;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanStepAppModule;

import java.util.List;

/** Per-phase detail page for the RFC 0002-ext1 tracker. */
public record Rfc0002Ext1Step() implements PlanStepAppModule<Rfc0002Ext1Step> {

    record appMain() implements AppModule._AppMain<Rfc0002Ext1Step> {}

    public record link() implements AppLink<Rfc0002Ext1Step> {}

    public record Params(String phase) {}

    public static final Rfc0002Ext1Step INSTANCE = new Rfc0002Ext1Step();

    @Override public Class<?> paramsType() { return Params.class; }

    @Override public Plan   plan()              { return Rfc0002Ext1PlanData.INSTANCE; }
    @Override public String planAppSimpleName() { return Rfc0002Ext1Plan.INSTANCE.simpleName(); }
    @Override public String stepAppSimpleName() { return INSTANCE.simpleName(); }

    @Override
    public ImportsFor<Rfc0002Ext1Step> imports() {
        return ImportsFor.<Rfc0002Ext1Step>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()),    StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()),  JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Ext1Plan.link()),    Rfc0002Ext1Plan.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Ext1Step.link()),    Rfc0002Ext1Step.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),          DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanRenderer.renderStep()), PlanRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<Rfc0002Ext1Step> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
