package hue.captains.singapura.js.homing.studio.rename;

import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.DecisionStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Dependency;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.PhaseStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.Task;

import java.util.List;

/**
 * Adapter — exposes {@link RenameSteps} as a {@link Plan}, mapping the
 * tracker's local records (RenameSteps.Phase, .Decision, …) onto the shared
 * tracker shapes. The local file stays the editable source of truth; this
 * adapter is the bridge that lets {@link PlanRenderer} render it.
 */
public final class RenamePlanData implements Plan {

    public static final RenamePlanData INSTANCE = new RenamePlanData();

    private RenamePlanData() {}

    @Override public String kicker()        { return "project rename"; }
    @Override public String name()          { return RenameSteps.OLD_NAME + " → " + RenameSteps.NEW_NAME; }
    @Override public String subtitle() {
        return "Source of truth: RenameSteps.java. Edit, recompile, refresh — phases and decisions update live.";
    }
    @Override public int    totalProgress() { return RenameSteps.totalProgressPercent(); }
    @Override public int    openDecisions() { return RenameSteps.openDecisionsCount(); }
    @Override public String executionDoc()  { return RenameSteps.EXECUTION_DOC; }
    @Override public String dossierDoc()    { return RenameSteps.DOSSIER_DOC; }

    @Override
    public List<Phase> phases() {
        return RenameSteps.PHASES.stream().map(RenamePlanData::adaptPhase).toList();
    }

    @Override
    public List<Decision> decisions() {
        return RenameSteps.DECISIONS.stream().map(RenamePlanData::adaptDecision).toList();
    }

    @Override
    public List<Acceptance> acceptance() {
        return List.of();   // TODO: populate per-tracker; v1 falls back to per-phase outcomes only.
    }

    private static Phase adaptPhase(RenameSteps.Phase p) {
        return new Phase(
                p.id(),
                p.label(),
                p.summary(),
                p.description(),
                adaptStatus(p.status()),
                p.tasks().stream().map(t -> new Task(t.description(), t.done())).toList(),
                p.dependsOn().stream().map(d -> new Dependency(d.phaseId(), d.reason())).toList(),
                p.verification(),
                p.rollback(),
                p.effort(),
                p.notes()
        );
    }

    private static Decision adaptDecision(RenameSteps.Decision d) {
        return new Decision(
                d.id(),
                d.question(),
                d.recommendation(),
                d.chosenValue(),
                adaptDecisionStatus(d.status()),
                d.rationale(),
                d.notes()
        );
    }

    private static PhaseStatus adaptStatus(RenameSteps.Status s) {
        return switch (s) {
            case NOT_STARTED -> PhaseStatus.NOT_STARTED;
            case IN_PROGRESS -> PhaseStatus.IN_PROGRESS;
            case BLOCKED     -> PhaseStatus.BLOCKED;
            case DONE        -> PhaseStatus.DONE;
        };
    }

    private static DecisionStatus adaptDecisionStatus(RenameSteps.DecisionStatus s) {
        return switch (s) {
            case OPEN     -> DecisionStatus.OPEN;
            case RESOLVED -> DecisionStatus.RESOLVED;
        };
    }
}
