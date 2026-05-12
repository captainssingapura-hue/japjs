package hue.captains.singapura.js.homing.studio.instruments;

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

/** Adapter — exposes {@link InstrumentsSteps} as a {@link Plan}. */
public final class InstrumentsPlanData implements Plan {

    public static final InstrumentsPlanData INSTANCE = new InstrumentsPlanData();

    private InstrumentsPlanData() {}

    @Override public String kicker()        { return "AUDIO ENGINE"; }
    @Override public String name()          { return "Real Instrument Audio Engine"; }
    @Override public String subtitle() {
        return "Source of truth: InstrumentsSteps.java. Originally framed as a sample-library adoption (Howler / smplr / soundfont). Resolved differently: the render-once-play-many pivot via Tone.Offline + AudioBufferSourceNode addressed every concern (polyphony, allocation cost, latency) without leaving Tone.js or adding sample assets. Most phases shipped via that pivot. See RFC 0007 §11.";
    }
    @Override public int    totalProgress() { return InstrumentsSteps.totalProgressPercent(); }
    @Override public int    openDecisions() { return InstrumentsSteps.openDecisionsCount(); }
    @Override public String executionDoc()  { return null; }
    @Override public String dossierDoc()    { return null; }

    @Override
    public List<Phase> phases() {
        return InstrumentsSteps.PHASES.stream().map(InstrumentsPlanData::adaptPhase).toList();
    }

    @Override
    public List<Decision> decisions() {
        return InstrumentsSteps.DECISIONS.stream().map(InstrumentsPlanData::adaptDecision).toList();
    }

    @Override
    public List<Objective> objectives() {
        return List.of(
                new Objective(
                        "Two engines under one typed contract",
                        "The sealed Cue hierarchy grows by one permit (SampleCue). One-shot UI cues stay on synth; instrument-grade play rides the sample engine. Theme authors pick by cue type; the runtime dispatches by sealed switch. No engine choice escapes the type system."),
                new Objective(
                        "Make the Jazz Drum Kit sound like a kit",
                        "The Phase 1 drum kit synth cues are recognisably toys. A successful migration is the moment someone says 'huh, that actually sounds like drums' without prompting. Sample-based playback is the means; A/B audibility is the criterion."),
                new Objective(
                        "Keep the framework's offline invariant",
                        "Samples ship classpath-bundled with the framework jar. No CDN dependency, no runtime fetch, no licensing surprise. Total drum-kit audio asset weight stays under 500 KB compressed."),
                new Objective(
                        "Earn the RFC 0008 Phase 2 keyboard play",
                        "Keyboard-playable drums on toy synths feel like a demo. Keyboard-playable drums on real samples feel like an instrument. This tracker's migration unlocks Phase 2 of RFC 0008 — they ship together or in succession.")
        );
    }

    @Override
    public List<Acceptance> acceptance() {
        return List.of(
                new Acceptance(
                        "Survey lands; engine + library decisions resolved",
                        "Phase 02 survey doc reports latency, weight, and quality numbers for 3 candidate engines. D1 (synth vs sample) and D2 (library) both move to RESOLVED.",
                        false),
                new Acceptance(
                        "Sealed Cue hierarchy grows by one permit",
                        "SampleCue lands in homing-core, the sealed switch in AppHtmlGetAction.emitCueJs gets a new exhaustive arm, no existing themes break.",
                        false),
                new Acceptance(
                        "Jazz Drum Kit migrates to sample cues",
                        "Cues.KICK..CRASH are SampleCue constants. Listening A/B test prefers the new sounds. Click-to-sound latency stays ≤ 30 ms (D6). Asset weight stays ≤ 500 KB (D3).",
                        false),
                new Acceptance(
                        "RFC formalises the engine boundary",
                        "RFC 0009 (or whichever number lands) covers the two-engine architecture, the SampleCue contract, asset hosting, and the doctrine extension. Perceivable Surface doctrine updated to mention sample engine.",
                        false)
        );
    }

    private static Phase adaptPhase(InstrumentsSteps.Phase p) {
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

    private static Decision adaptDecision(InstrumentsSteps.Decision d) {
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

    private static PhaseStatus adaptStatus(InstrumentsSteps.Status s) {
        return switch (s) {
            case NOT_STARTED -> PhaseStatus.NOT_STARTED;
            case IN_PROGRESS -> PhaseStatus.IN_PROGRESS;
            case BLOCKED     -> PhaseStatus.BLOCKED;
            case DONE        -> PhaseStatus.DONE;
        };
    }

    private static DecisionStatus adaptDecisionStatus(InstrumentsSteps.DecisionStatus s) {
        return switch (s) {
            case OPEN     -> DecisionStatus.OPEN;
            case RESOLVED -> DecisionStatus.RESOLVED;
        };
    }
}
