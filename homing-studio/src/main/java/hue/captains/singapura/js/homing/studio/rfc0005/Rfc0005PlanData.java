package hue.captains.singapura.js.homing.studio.rfc0005;

import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.DecisionStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Dependency;
import hue.captains.singapura.js.homing.studio.base.tracker.Metric;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.PhaseStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.Task;

import java.util.List;

/** Adapter — exposes {@link Rfc0005Steps} as a {@link Plan}. */
public final class Rfc0005PlanData implements Plan {

    public static final Rfc0005PlanData INSTANCE = new Rfc0005PlanData();

    private Rfc0005PlanData() {}

    @Override public String kicker()        { return "RFC 0005"; }
    @Override public String name()          { return "Typed Catalogue Containers"; }
    @Override public String subtitle() {
        return "Source of truth: Rfc0005Steps.java. Edit, recompile, refresh — phases, decisions, and metrics update live.";
    }
    @Override public int    totalProgress() { return Rfc0005Steps.totalProgressPercent(); }
    @Override public int    openDecisions() { return Rfc0005Steps.openDecisionsCount(); }
    @Override public String executionDoc()  { return Rfc0005Steps.RFC_DOC; }
    @Override public String dossierDoc()    { return null; }

    @Override
    public List<Phase> phases() {
        return Rfc0005Steps.PHASES.stream().map(Rfc0005PlanData::adaptPhase).toList();
    }

    @Override
    public List<Decision> decisions() {
        return Rfc0005Steps.DECISIONS.stream().map(Rfc0005PlanData::adaptDecision).toList();
    }

    @Override
    public List<Acceptance> acceptance() {
        return List.of();   // TODO: populate per-tracker; v1 falls back to per-phase outcomes only.
    }

    private static Phase adaptPhase(Rfc0005Steps.Phase p) {
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
                p.notes(),
                p.metrics().stream().map(m -> new Metric(m.label(), m.before(), m.after(), m.delta())).toList()
        );
    }

    private static Decision adaptDecision(Rfc0005Steps.Decision d) {
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

    private static PhaseStatus adaptStatus(Rfc0005Steps.Status s) {
        return switch (s) {
            case NOT_STARTED -> PhaseStatus.NOT_STARTED;
            case IN_PROGRESS -> PhaseStatus.IN_PROGRESS;
            case BLOCKED     -> PhaseStatus.BLOCKED;
            case DONE        -> PhaseStatus.DONE;
        };
    }

    private static DecisionStatus adaptDecisionStatus(Rfc0005Steps.DecisionStatus s) {
        return switch (s) {
            case OPEN     -> DecisionStatus.OPEN;
            case RESOLVED -> DecisionStatus.RESOLVED;
        };
    }
}
