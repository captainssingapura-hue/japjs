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
import hue.captains.singapura.js.homing.studio.base.tracker.PlanRenderer;
import hue.captains.singapura.js.homing.studio.base.tracker.PlanStepAppModule;

import java.util.List;

/**
 * Per-phase detail page for the rename tracker. Reads {@code params.phase}
 * (typed query parameter) and inherits {@link PlanStepAppModule}'s
 * auto-generated JS body — {@link PlanRenderer#renderStep} draws.
 */
public record RenameStep() implements PlanStepAppModule<RenameStep> {

    record appMain() implements AppModule._AppMain<RenameStep> {}

    public record link() implements AppLink<RenameStep> {}

    /** Typed query parameter — which phase id to render. */
    public record Params(String phase) {}

    public static final RenameStep INSTANCE = new RenameStep();

    @Override public Class<?> paramsType() { return Params.class; }

    @Override public Plan   plan()              { return RenamePlanData.INSTANCE; }
    @Override public String planAppSimpleName() { return RenamePlan.INSTANCE.simpleName(); }
    @Override public String stepAppSimpleName() { return INSTANCE.simpleName(); }

    @Override
    public ImportsFor<RenameStep> imports() {
        return ImportsFor.<RenameStep>builder()
                .add(new ModuleImports<>(List.of(new StudioCatalogue.link()),    StudioCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new JourneysCatalogue.link()),  JourneysCatalogue.INSTANCE))
                .add(new ModuleImports<>(List.of(new RenamePlan.link()),         RenamePlan.INSTANCE))
                .add(new ModuleImports<>(List.of(new RenameStep.link()),         RenameStep.INSTANCE))  // self for prev/next nav
                .add(new ModuleImports<>(List.of(new DocReader.link()),          DocReader.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanRenderer.renderStep()), PlanRenderer.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<RenameStep> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new appMain()));
    }
}
