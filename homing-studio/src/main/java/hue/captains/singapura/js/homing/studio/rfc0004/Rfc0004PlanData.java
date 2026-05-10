package hue.captains.singapura.js.homing.studio.rfc0004;

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

/**
 * Adapter — exposes {@link Rfc0004Steps} as a {@link Plan}. Same shape as the other RFC
 * tracker plan-data adapters; the only RFC-specific note is that {@link #executionDoc()}
 * returns a UUID string (the typed Doc reference) rather than a markdown path — see the
 * field definition in {@link Rfc0004Steps#RFC_DOC}.
 */
public final class Rfc0004PlanData implements Plan {

    public static final Rfc0004PlanData INSTANCE = new Rfc0004PlanData();

    private Rfc0004PlanData() {}

    @Override public String kicker()        { return "RFC 0004"; }
    @Override public String name()          { return "Typed Docs, UUIDs, and Public/Private Visibility"; }
    @Override public String subtitle() {
        return "Source of truth: Rfc0004Steps.java. The RFC introduced typed Doc references; this tracker uses one for its own footer link — recursive proof that the model works.";
    }
    @Override public int    totalProgress() { return Rfc0004Steps.totalProgressPercent(); }
    @Override public int    openDecisions() { return Rfc0004Steps.openDecisionsCount(); }
    @Override public String executionDoc()  { return Rfc0004Steps.RFC_DOC; }
    @Override public String dossierDoc()    { return null; }

    @Override
    public List<Phase> phases() {
        return Rfc0004Steps.PHASES.stream().map(Rfc0004PlanData::adaptPhase).toList();
    }

    @Override
    public List<Decision> decisions() {
        return Rfc0004Steps.DECISIONS.stream().map(Rfc0004PlanData::adaptDecision).toList();
    }

    @Override
    public List<Acceptance> acceptance() {
        return List.of();   // TODO: populate per-tracker; v1 falls back to per-phase outcomes only.
    }

    private static Phase adaptPhase(Rfc0004Steps.Phase p) {
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

    private static Decision adaptDecision(Rfc0004Steps.Decision d) {
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

    private static PhaseStatus adaptStatus(Rfc0004Steps.Status s) {
        return switch (s) {
            case NOT_STARTED -> PhaseStatus.NOT_STARTED;
            case IN_PROGRESS -> PhaseStatus.IN_PROGRESS;
            case BLOCKED     -> PhaseStatus.BLOCKED;
            case DONE        -> PhaseStatus.DONE;
        };
    }

    private static DecisionStatus adaptDecisionStatus(Rfc0004Steps.DecisionStatus s) {
        return switch (s) {
            case OPEN     -> DecisionStatus.OPEN;
            case RESOLVED -> DecisionStatus.RESOLVED;
        };
    }
}
