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
import hue.captains.singapura.js.homing.studio.base.tracker.PlanAppModule;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanRenderer;

import java.util.List;

/** Index page for the RFC 0001 (App Registry & Typed Nav) tracker. */
public record Rfc0001Plan() implements PlanAppModule<Rfc0001Plan> {

    record appMain() implements AppModule._AppMain<Rfc0001Plan> {}

    public record link() implements AppLink<Rfc0001Plan> {}

    public static final Rfc0001Plan INSTANCE = new Rfc0001Plan();

    @Override public Plan   plan()              { return Rfc0001PlanData.INSTANCE; }
    @Override public String stepAppSimpleName() { return Rfc0001Step.INSTANCE.simpleName(); }

    @Override
    public ImportsFor<Rfc0001Plan> imports() {
        return ImportsFor.<Rfc0001Plan>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()),    StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()),  JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new Rfc0001Step.link()),        Rfc0001Step.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),          DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanRenderer.renderPlan()), PlanRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<Rfc0001Plan> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
