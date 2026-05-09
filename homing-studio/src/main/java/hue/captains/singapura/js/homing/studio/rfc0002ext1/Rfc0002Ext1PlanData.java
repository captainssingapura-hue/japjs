package hue.captains.singapura.js.homing.studio.rfc0002ext1;

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
 * Adapter — exposes {@link Rfc0002Ext1Steps} as a {@link Plan}. Same shape
 * as {@link hue.captains.singapura.js.homing.studio.rfc0002.Rfc0002PlanData}
 * with the addition of per-phase {@link Metric} mapping (the original
 * motivation for adding metrics to the kit).
 */
public final class Rfc0002Ext1PlanData implements Plan {

    public static final Rfc0002Ext1PlanData INSTANCE = new Rfc0002Ext1PlanData();

    private Rfc0002Ext1PlanData() {}

    @Override public String kicker()        { return "RFC 0002-ext1"; }
    @Override public String title()         { return "Utility-First Composition + Semantic Tokens"; }
    @Override public String subtitle() {
        return "Source of truth: Rfc0002Ext1Steps.java. Edit, recompile, refresh — phases, decisions, and metrics update live.";
    }
    @Override public int    totalProgress() { return Rfc0002Ext1Steps.totalProgressPercent(); }
    @Override public int    openDecisions() { return Rfc0002Ext1Steps.openDecisionsCount(); }
    @Override public String executionDoc()  { return Rfc0002Ext1Steps.RFC_DOC; }
    @Override public String dossierDoc()    { return null; }

    @Override
    public List<Phase> phases() {
        return Rfc0002Ext1Steps.PHASES.stream().map(Rfc0002Ext1PlanData::adaptPhase).toList();
    }

    @Override
    public List<Decision> decisions() {
        return Rfc0002Ext1Steps.DECISIONS.stream().map(Rfc0002Ext1PlanData::adaptDecision).toList();
    }

    private static Phase adaptPhase(Rfc0002Ext1Steps.Phase p) {
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

    private static Decision adaptDecision(Rfc0002Ext1Steps.Decision d) {
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

    private static PhaseStatus adaptStatus(Rfc0002Ext1Steps.Status s) {
        return switch (s) {
            case NOT_STARTED -> PhaseStatus.NOT_STARTED;
            case IN_PROGRESS -> PhaseStatus.IN_PROGRESS;
            case BLOCKED     -> PhaseStatus.BLOCKED;
            case DONE        -> PhaseStatus.DONE;
        };
    }

    private static DecisionStatus adaptDecisionStatus(Rfc0002Ext1Steps.DecisionStatus s) {
        return switch (s) {
            case OPEN     -> DecisionStatus.OPEN;
            case RESOLVED -> DecisionStatus.RESOLVED;
        };
    }
}
