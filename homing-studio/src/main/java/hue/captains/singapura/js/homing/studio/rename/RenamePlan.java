package hue.captains.singapura.js.homing.studio.rename;

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

/**
 * Index page for the Homing rename tracker. Inherits {@link PlanAppModule}'s
 * auto-generated JS body — the renderer ({@link PlanRenderer#renderPlan}) does
 * the drawing, this module just declares identity + imports + which plan
 * data to render.
 */
public record RenamePlan() implements PlanAppModule<RenamePlan> {

    record appMain() implements AppModule._AppMain<RenamePlan> {}

    public record link() implements AppLink<RenamePlan> {}

    public static final RenamePlan INSTANCE = new RenamePlan();

    @Override public Plan   plan()              { return RenamePlanData.INSTANCE; }
    @Override public String stepAppSimpleName() { return RenameStep.INSTANCE.simpleName(); }

    @Override
    public ImportsFor<RenamePlan> imports() {
        return ImportsFor.<RenamePlan>builder()
                // Nav targets — the renderer needs to build URLs to these apps.
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()),    StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()),  JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new RenameStep.link()),         RenameStep.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReader.link()),          DocReader.INSTANCE))
                // Shared renderer — the auto-generated body calls renderPlan().
                .add(new ModuleImports<>(List.of(new PlanRenderer.renderPlan()), PlanRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<RenamePlan> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
