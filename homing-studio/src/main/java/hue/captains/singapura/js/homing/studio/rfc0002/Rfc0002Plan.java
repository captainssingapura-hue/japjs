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
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppModule;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanRenderer;

import java.util.List;

/** Index page for the RFC 0002 (Typed Themes) tracker. Auto-generated JS via {@link PlanAppModule}. */
public record Rfc0002Plan() implements PlanAppModule<Rfc0002Plan> {

    record appMain() implements AppModule._AppMain<Rfc0002Plan> {}

    public record link() implements AppLink<Rfc0002Plan> {}

    public static final Rfc0002Plan INSTANCE = new Rfc0002Plan();

    @Override public Plan   plan()              { return Rfc0002PlanData.INSTANCE; }
    @Override public String stepAppSimpleName() { return Rfc0002Step.INSTANCE.simpleName(); }

    @Override
    public ImportsFor<Rfc0002Plan> imports() {
        return ImportsFor.<Rfc0002Plan>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()),    StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()),  JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0002Step.link()),        Rfc0002Step.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),          DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanRenderer.renderPlan()), PlanRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<Rfc0002Plan> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
