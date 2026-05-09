package hue.captains.singapura.js.homing.studio.rfc0001;

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

/** Per-step detail page for the RFC 0001 tracker. */
public record Rfc0001Step() implements PlanStepAppModule<Rfc0001Step> {

    record appMain() implements AppModule._AppMain<Rfc0001Step> {}

    public record link() implements AppLink<Rfc0001Step> {}

    public record Params(String phase) {}

    public static final Rfc0001Step INSTANCE = new Rfc0001Step();

    @Override public Class<?> paramsType() { return Params.class; }

    @Override public Plan   plan()              { return Rfc0001PlanData.INSTANCE; }
    @Override public String planAppSimpleName() { return Rfc0001Plan.INSTANCE.simpleName(); }
    @Override public String stepAppSimpleName() { return INSTANCE.simpleName(); }

    @Override
    public ImportsFor<Rfc0001Step> imports() {
        return ImportsFor.<Rfc0001Step>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()),    StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()),  JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0001Plan.link()),        Rfc0001Plan.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0001Step.link()),        Rfc0001Step.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),          DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanRenderer.renderStep()), PlanRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<Rfc0001Step> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
