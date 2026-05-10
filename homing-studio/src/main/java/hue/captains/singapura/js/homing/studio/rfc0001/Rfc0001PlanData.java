package hue.captains.singapura.js.homing.studio.rfc0001;

import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.Dependency;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.PhaseStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.Task;

import java.util.List;

/**
 * Adapter — exposes {@link Rfc0001Steps} as a {@link Plan}. Note Rfc0001's
 * tracker calls phases "Steps" (and dependencies' field is {@code stepId});
 * this adapter renames to the kit's vocabulary. Rfc0001 has no decisions —
 * adapter returns an empty list, the renderer hides the decisions section.
 *
 * <p>{@code Step.acceptance} and {@code Step.rfcSection} have no direct slot
 * in the kit's Phase shape: acceptance maps to {@code verification} (close
 * meaning), and rfcSection is folded into {@code notes} so it stays visible.</p>
 */
public final class Rfc0001PlanData implements Plan {

    public static final Rfc0001PlanData INSTANCE = new Rfc0001PlanData();

    private Rfc0001PlanData() {}

    @Override public String kicker()        { return "RFC 0001"; }
    @Override public String name()          { return "App Registry & Typed Navigation"; }
    @Override public String subtitle() {
        return "Source of truth: Rfc0001Steps.java. Edit, recompile, refresh — steps update live.";
    }
    @Override public int    totalProgress() { return Rfc0001Steps.totalProgressPercent(); }
    @Override public int    openDecisions() { return 0; }
    @Override public String executionDoc()  { return Rfc0001Steps.RFC_PATH; }
    @Override public String dossierDoc()    { return null; }

    @Override
    public List<Phase> phases() {
        return Rfc0001Steps.STEPS.stream().map(Rfc0001PlanData::adaptStep).toList();
    }

    @Override
    public List<Decision> decisions() {
        return List.of();   // RFC 0001 has no decisions concept.
    }

    @Override
    public List<Acceptance> acceptance() {
        return List.of();   // TODO: populate per-tracker; v1 falls back to per-phase outcomes only.
    }

    private static Phase adaptStep(Rfc0001Steps.Step s) {
        // Fold rfcSection into notes so the cross-reference stays visible.
        String notes = s.notes();
        if (s.rfcSection() != null && !s.rfcSection().isBlank()) {
            String prefix = "RFC reference: " + s.rfcSection();
            notes = notes == null || notes.isBlank() ? prefix : prefix + "\n\n" + notes;
        }
        return new Phase(
                s.id(),
                s.label(),
                s.summary(),
                s.description(),
                adaptStatus(s.status()),
                s.tasks().stream().map(t -> new Task(t.description(), t.done())).toList(),
                s.dependsOn().stream().map(d -> new Dependency(d.stepId(), d.reason())).toList(),
                s.acceptance(),  // → verification slot
                "",              // → rollback (Rfc0001 has no rollback per step)
                s.effort(),
                notes
        );
    }

    private static PhaseStatus adaptStatus(Rfc0001Steps.Status s) {
        return switch (s) {
            case NOT_STARTED -> PhaseStatus.NOT_STARTED;
            case IN_PROGRESS -> PhaseStatus.IN_PROGRESS;
            case BLOCKED     -> PhaseStatus.BLOCKED;
            case DONE        -> PhaseStatus.DONE;
        };
    }
}
