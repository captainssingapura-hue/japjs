package hue.captains.singapura.js.homing.studio.release;

import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.DecisionStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Dependency;
import hue.captains.singapura.js.homing.studio.base.tracker.Metric;
import hue.captains.singapura.js.homing.studio.base.tracker.Objective;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.PhaseStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.Task;

import java.util.List;

/** Adapter — exposes {@link V1Steps} as a {@link Plan}. */
public final class V1PlanData implements Plan {

    public static final V1PlanData INSTANCE = new V1PlanData();

    private V1PlanData() {}

    @Override public String kicker()        { return "RELEASE v1"; }
    @Override public String name()          { return "Ship Checkpoint"; }
    @Override public String subtitle() {
        return "Source of truth: V1Steps.java. Ship gates for v1; gaps audited from the codebase, not designed up front.";
    }
    @Override public int    totalProgress() { return V1Steps.totalProgressPercent(); }
    @Override public int    openDecisions() { return V1Steps.openDecisionsCount(); }
    @Override public String executionDoc()  { return null; }   // Tracker is self-contained — no companion RFC.
    @Override public String dossierDoc()    { return null; }

    @Override
    public List<Phase> phases() {
        return V1Steps.PHASES.stream().map(V1PlanData::adaptPhase).toList();
    }

    @Override
    public List<Decision> decisions() {
        return V1Steps.DECISIONS.stream().map(V1PlanData::adaptDecision).toList();
    }

    @Override
    public List<Objective> objectives() {
        return List.of(
                new Objective(
                        "Ship a downstream-ready studio base",
                        "homing-studio-base reaches the bar where a fresh agent can boot a Catalogue/Plan-shaped studio in one file from the README, with CI guardrails, in under an hour."),
                new Objective(
                        "Codify the typed-container worldview in docs",
                        "Catalogue (RFC 0005) and Plan (RFC 0005-ext1) have peer kit reference docs that read as the canonical recipe; doctrine + RFCs cross-link cleanly."),
                new Objective(
                        "Pin silent-failure conformance",
                        "The classes of bug that ship green from CI but break in the browser (auto-injected identifier collisions, missing references, raw href ops, raw CSS ops) all have a conformance test downstream can subclass in 4 lines."),
                new Objective(
                        "Recursion-proof the framework",
                        "The release tracker itself uses every primitive it ships — Catalogue + Plan + Objectives + Acceptance — so the framework's typed-everything story is provably consistent at v1.")
        );
    }

    @Override
    public List<Acceptance> acceptance() {
        return List.of(
                new Acceptance(
                        "README modernized for typed-container world",
                        "homing-studio-base/README.md teaches the new bootstrap signature, Catalogue-as-home, and the conformance baseline. Driven by phase 02.",
                        true),
                new Acceptance(
                        "PlanKitDoc lands as peer to CatalogueKitDoc",
                        "Plan recipe is documented at the kit level — not learnable only by reading 7 trackers. Driven by phase 03.",
                        true),
                new Acceptance(
                        "Conformance baseline section published",
                        "README enumerates the 9 conformance bases with one-line purposes and a single-file 'subclass everything' example. Driven by phase 05.",
                        true),
                new Acceptance(
                        "Objectives pillar added to Plan; this tracker populates it",
                        "Plan.objectives() defaulted to List.of(); V1PlanData populates it; renderer surfaces an Objectives section at top of index view. Driven by phase 06.",
                        true),
                new Acceptance(
                        "All 7 modules build green; studio boots cleanly",
                        "mvn install passes across homing-core, homing-server, homing-conformance, homing-libs, homing-studio-base, homing-studio, homing-demo. Studio Journeys page renders the V1 tracker with populated Objectives and Acceptance. Driven by phase 09.",
                        true)
        );
    }

    private static Phase adaptPhase(V1Steps.Phase p) {
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

    private static Decision adaptDecision(V1Steps.Decision d) {
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

    private static PhaseStatus adaptStatus(V1Steps.Status s) {
        return switch (s) {
            case NOT_STARTED -> PhaseStatus.NOT_STARTED;
            case IN_PROGRESS -> PhaseStatus.IN_PROGRESS;
            case BLOCKED     -> PhaseStatus.BLOCKED;
            case DONE        -> PhaseStatus.DONE;
        };
    }

    private static DecisionStatus adaptDecisionStatus(V1Steps.DecisionStatus s) {
        return switch (s) {
            case OPEN     -> DecisionStatus.OPEN;
            case RESOLVED -> DecisionStatus.RESOLVED;
        };
    }
}
