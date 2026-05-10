package hue.captains.singapura.js.homing.studio.rfc0002;

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
 * Adapter — exposes {@link Rfc0002Steps} as a {@link Plan}. {@link Rfc0002Steps}
 * stays the editable source of truth; this adapter maps its nested records
 * onto the shared tracker shapes.
 */
public final class Rfc0002PlanData implements Plan {

    public static final Rfc0002PlanData INSTANCE = new Rfc0002PlanData();

    private Rfc0002PlanData() {}

    @Override public String kicker()        { return "RFC 0002"; }
    @Override public String name()          { return "Typed Themes for CssGroups"; }
    @Override public String subtitle() {
        return "Source of truth: Rfc0002Steps.java. Edit, recompile, refresh — phases and decisions update live.";
    }
    @Override public int    totalProgress() { return Rfc0002Steps.totalProgressPercent(); }
    @Override public int    openDecisions() { return Rfc0002Steps.openDecisionsCount(); }
    @Override public String executionDoc()  { return Rfc0002Steps.RFC_DOC; }
    @Override public String dossierDoc()    { return null; }

    @Override
    public List<Phase> phases() {
        return Rfc0002Steps.PHASES.stream().map(Rfc0002PlanData::adaptPhase).toList();
    }

    @Override
    public List<Decision> decisions() {
        return Rfc0002Steps.DECISIONS.stream().map(Rfc0002PlanData::adaptDecision).toList();
    }

    @Override
    public List<Acceptance> acceptance() {
        return List.of();   // TODO: populate per-tracker; v1 falls back to per-phase outcomes only.
    }

    private static Phase adaptPhase(Rfc0002Steps.Phase p) {
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

    private static Decision adaptDecision(Rfc0002Steps.Decision d) {
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

    private static PhaseStatus adaptStatus(Rfc0002Steps.Status s) {
        return switch (s) {
            case NOT_STARTED -> PhaseStatus.NOT_STARTED;
            case IN_PROGRESS -> PhaseStatus.IN_PROGRESS;
            case BLOCKED     -> PhaseStatus.BLOCKED;
            case DONE        -> PhaseStatus.DONE;
        };
    }

    private static DecisionStatus adaptDecisionStatus(Rfc0002Steps.DecisionStatus s) {
        return switch (s) {
            case OPEN     -> DecisionStatus.OPEN;
            case RESOLVED -> DecisionStatus.RESOLVED;
        };
    }
}
