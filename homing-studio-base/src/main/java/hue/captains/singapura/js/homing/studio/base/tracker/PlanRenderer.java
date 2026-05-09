package hue.captains.singapura.js.homing.studio.base.tracker;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.studio.base.css.StudioStyles;
import hue.captains.singapura.js.homing.studio.base.ui.StudioElements;

import java.util.List;

/**
 * Shared JS renderer for any {@link Plan} tracker. Exports two functions —
 * {@code renderPlan} (index page, lists phases as step-cards) and
 * {@code renderStep} (detail page, shows tasks / deps / acceptance for one
 * phase). Both take a single props object and return a {@code Node}.
 *
 * <p>{@link PlanAppModule} and {@link PlanStepAppModule} auto-generate a tiny
 * {@code appMain} that calls into these functions; the consumer never writes
 * tracker JS by hand.</p>
 */
public record PlanRenderer() implements DomModule<PlanRenderer> {

    public record renderPlan() implements Exportable._Constant<PlanRenderer> {}
    public record renderStep() implements Exportable._Constant<PlanRenderer> {}

    public static final PlanRenderer INSTANCE = new PlanRenderer();

    @Override
    public ImportsFor<PlanRenderer> imports() {
        return ImportsFor.<PlanRenderer>builder()
                // HrefManager is normally auto-injected for AppLink-importing
                // modules; PlanRenderer is a generic helper without AppLinks,
                // so import explicitly. Aliased to `href` at the top of
                // PlanRenderer.js for the same usage as elsewhere.
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()),
                        HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioElements.Header(),
                        new StudioElements.Card(),
                        new StudioElements.Section(),
                        new StudioElements.Footer(),
                        new StudioElements.StatusBadge(),
                        new StudioElements.OverallProgress(),
                        new StudioElements.StepCard(),
                        new StudioElements.DecisionCard(),
                        new StudioElements.TodoList(),
                        new StudioElements.Panel(),
                        new StudioElements.MetricsTable()
                ), StudioElements.INSTANCE))
                .add(new ModuleImports<>(List.of(
                        new StudioStyles.st_root(),
                        new StudioStyles.st_main(),
                        new StudioStyles.st_kicker(),
                        new StudioStyles.st_title(),
                        new StudioStyles.st_subtitle()
                ), StudioStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlanRenderer> exports() {
        return new ExportsOf<>(INSTANCE, List.of(new renderPlan(), new renderStep()));
    }
}
