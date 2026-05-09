package hue.captains.singapura.js.homing.studio.rfc0002;

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

/** Per-phase detail page for the RFC 0002 tracker. Auto-generated JS via {@link PlanStepAppModule}. */
public record Rfc0002Step() implements PlanStepAppModule<Rfc0002Step> {

    record appMain() implements AppModule._AppMain<Rfc0002Step> {}

    public record link() implements AppLink<Rfc0002Step> {}

    public record Params(String phase) {}

    public static final Rfc0002Step INSTANCE = new Rfc0002Step();

    @Override public Class<?> paramsType() { return Params.class; }

    @Override public Plan   plan()              { return Rfc0002PlanData.INSTANCE; }
    @Override public String planAppSimpleName() { return Rfc0002Plan.INSTANCE.simpleName(); }
    @Override public String stepAppSimpleName() { return INSTANCE.simpleName(); }

    @Override
    public ImportsFor<Rfc0002Step> imports() {
        return ImportsFor.<Rfc0002Step>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()),    StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()),  JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Plan.link()),        Rfc0002Plan.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Step.link()),        Rfc0002Step.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),          DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanRenderer.renderStep()), PlanRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<Rfc0002Step> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
