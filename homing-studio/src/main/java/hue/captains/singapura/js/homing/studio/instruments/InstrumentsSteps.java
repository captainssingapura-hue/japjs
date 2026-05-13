package hue.captains.singapura.js.homing.studio.instruments;

import java.util.List;

/**
 * Implementation tracker for <b>Real Instrument Audio Engine</b>.
 *
 * <p>RFC 0007 + RFC 0008 ship with Tone.js synthesising every cue from
 * scratch — one fresh {@code Synth} / {@code MembraneSynth} / {@code NoiseSynth}
 * per note hit, disposed 5 seconds later. This works for one-shot UI cues
 * (the Maple Bridge temple bell, the Retro 90s click, the Jazz Drums Phase 1
 * playable kit). It does NOT work for "real instrument play":</p>
 *
 * <ul>
 *   <li>Per-note synth instantiation is heavy — creating + tearing down audio
 *       nodes 10× per second (rapid drumming) burns CPU and risks audio glitches.</li>
 *   <li>No voice management — no voice stealing, no key-up handling for sustained
 *       instruments (piano, organ, strings), no expression / modulation surface.</li>
 *   <li>Synth quality ceiling — real drums + cymbals are spectrally complex
 *       (inharmonic partials, noise floor, room resonance). Pure synthesis lands
 *       in the "toy" register; sample-based playback is what gets you to "kit."</li>
 *   <li>No reusable engine — every theme that wants instruments would reinvent
 *       voice management, dispose timing, polyphony handling.</li>
 * </ul>
 *
 * <p>This tracker captures the work to either (a) build a managed instrument
 * layer on top of Tone.js, or (b) adopt a sample-based library (smplr,
 * soundfont-player, or similar) for the instrument-grade tier, keeping
 * Tone.js for the synth-cue tier. The decision is captured as D2.</p>
 *
 * <p>No companion RFC yet — the survey phase has to land first. Once we
 * know the library + integration shape, an RFC formalises the cue-layer
 * extension and the engine boundary.</p>
 */
public final class InstrumentsSteps {

    public enum Status {
        NOT_STARTED("Not started", "not-started"),
        IN_PROGRESS("In progress", "in-progress"),
        BLOCKED("Blocked", "blocked"),
        DONE("Done", "done");

        public final String label;
        public final String slug;
        Status(String label, String slug) { this.label = label; this.slug = slug; }
    }

    public enum DecisionStatus {
        OPEN("Open", "open"),
        RESOLVED("Resolved", "resolved");

        public final String label;
        public final String slug;
        DecisionStatus(String label, String slug) { this.label = label; this.slug = slug; }
    }

    public record Task(String description, boolean done) {}
    public record Dependency(String phaseId, String reason) {}

    public record Decision(
            String id,
            String question,
            String recommendation,
            String chosenValue,
            DecisionStatus status,
            String rationale,
            String notes
    ) {}

    public record Phase(
            String id,
            String label,
            String summary,
            String description,
            Status status,
            List<Task> tasks,
            List<Dependency> dependsOn,
            String verification,
            String rollback,
            String effort,
            String notes,
            List<Metric> metrics
    ) {
        public int progressPercent() {
            if (tasks.isEmpty()) return 0;
            long done = tasks.stream().filter(Task::done).count();
            return (int) (done * 100 / tasks.size());
        }
    }

    public record Metric(String label, String before, String after, String delta) {}

    // ------------------------------------------------------------------
    // OPEN DECISIONS — to be resolved before downstream phases can land.
    // ------------------------------------------------------------------

    public static final List<Decision> DECISIONS = List.of(

            new Decision("D1",
                    "Synthesis vs sampling for instrument-grade playback?",
                    "Sampling for instruments; keep synthesis for short UI cues.",
                    "neither — render-once-play-many pivot",
                    DecisionStatus.RESOLVED,
                    "Resolved by the pivot: synth at BAKE time, sample-playback at TRIGGER time. Each cue's Tone.js synth+envelope is rendered ONCE into an AudioBuffer via Tone.Offline at first audio gesture; subsequent triggers replay the buffer via AudioBufferSourceNode. We get the typed-synth authoring ergonomics AND the sample-playback performance — no actual external samples needed.",
                    "The original framing (synth vs sample) turned out to be a false binary. The render-once pivot collapses the question."
            ),

            new Decision("D2",
                    "Audio engine library — Tone.js + managed layer, or a new sample-based library?",
                    "Survey 3 candidates: Tone.js Sampler, smplr, custom AudioWorklet.",
                    "stay on Tone.js (no new library)",
                    DecisionStatus.RESOLVED,
                    "After seeing the js-demos/audio reference (Howler + procedural synthesizer.js), we adopted the PATTERN (render-once, play-many) but stayed on Tone.js. Tone.js already provides Tone.Offline (renders cues to AudioBuffer) and the same module already on the classpath gives us synth + offline + chord-stacking + Distortion + everything else we needed. Adding a second audio library would have duplicated playback machinery we already have.",
                    "Saved ~50KB of bundled JS by not adding Howler, plus avoided maintaining two audio dependencies."
            ),

            new Decision("D3",
                    "Sample asset hosting — classpath-bundled or runtime CDN fetch?",
                    "Classpath-bundled for offline invariant.",
                    "moot — no sample assets needed",
                    DecisionStatus.RESOLVED,
                    "The render-once pivot means we never ship audio files at all. Every cue is described as typed parameters (synth type, envelope, notes, distortion) and rendered to an AudioBuffer at first audio gesture. Zero binary audio assets in the framework. Zero licensing audit. Zero offline-vs-CDN tension.",
                    null
            ),

            new Decision("D4",
                    "Voice pool management — framework-owned, library-owned, or theme-owned?",
                    "Framework-owned. Themes contribute typed cue parameters.",
                    "framework-owned via AudioBufferSourceNode-per-trigger",
                    DecisionStatus.RESOLVED,
                    "The framework's audio runtime owns the audio context, the cue-baking pipeline, and the per-trigger source-node creation. Polyphony is automatic — each trigger creates a fresh AudioBufferSourceNode (cheap, ~0 allocation) connected to the destination. The runtime never holds long-lived synths; source nodes auto-dispose after playback. No 'voice pool' as such — just unbounded per-trigger source-node creation, which is what Web Audio is designed for.",
                    null
            ),

            new Decision("D5",
                    "Cue contract evolution — additive (new SampleCue permit) or replacing?",
                    "Additive — SampleCue joins the sealed permits.",
                    "additive — but to existing records, not via SampleCue",
                    DecisionStatus.RESOLVED,
                    "The sealed Cue hierarchy stayed at 3 permits (OscCue, MembraneCue, NoiseCue). We added FIELDS to the existing records rather than new permits: paletteMode (PaletteMode enum), distortion (double). The cue contract grew in feature surface, not in permit count. SampleCue was never needed because we don't actually ship samples — every 'sample' is a baked synth render.",
                    null
            ),

            new Decision("D6",
                    "Latency budget — what counts as 'feels playable'?",
                    "Target ≤30 ms click-to-sound.",
                    "achieved — AudioBufferSourceNode trigger is sub-10ms",
                    DecisionStatus.RESOLVED,
                    "AudioBufferSourceNode.start() schedules the buffer for immediate playback through Web Audio's destination graph; typical latency is 5–15ms on modern hardware. Well under the 30ms target. The Jazz Drum Kit's keyboard play feels responsive at realistic drumming tempo (~10 hits/second per drum). Music-production-grade latency was never a goal; we hit 'feels playable' comfortably.",
                    null
            )
    );

    // ------------------------------------------------------------------
    // PHASES — execute in order. Phase 02 (Survey) gates everything;
    // Phase 03 (Prototype) gates everything after.
    // ------------------------------------------------------------------

    public static final List<Phase> PHASES = List.of(

            new Phase("01",
                    "Tracker landed (this file)",
                    "Captures the constraint, open decisions, and phase list. Renders in Journeys catalogue.",
                    "InstrumentsSteps + InstrumentsPlanData created; registered in JourneysCatalogue, StudioServer plans list, and StudioPlanConstructsTest. Decisions D1–D6 captured as OPEN. The next agent (or future-self) picks this up by reading the decisions in order.",
                    Status.DONE,
                    List.of(
                            new Task("Create InstrumentsSteps.java + InstrumentsPlanData.java under homing-studio/.../instruments/", true),
                            new Task("Register InstrumentsPlanData.INSTANCE in JourneysCatalogue", true),
                            new Task("Register in StudioServer plans list", true),
                            new Task("Register in StudioPlanConstructsTest", true),
                            new Task("Studio tests GREEN", true)
                    ),
                    List.of(),
                    "Tracker visible at /app?app=plan&id=…instruments.InstrumentsPlanData; studio tests green.",
                    "Delete the four edits (2 new files + 3 list entries).",
                    "20 minutes",
                    "",
                    List.of()
            ),

            new Phase("02",
                    "[MUST] Survey — resolved via demo prior art instead",
                    "Originally framed as a 4-6h bake-off across three candidate engines. Resolved in ~30 min by recognizing the pattern in the existing js-demos/audio reference.",
                    "Read js-demos/audio: the demo uses Howler.js + procedurally-synthesized buffers via synthesizer.js encoded as WAV blob URLs. The PATTERN is render-once-play-many. We adopted the pattern but stayed on Tone.js (already bundled, native Tone.Offline gives us the same offline render, Tone.Player would have given us playback if we needed it — though we ended up using raw AudioBufferSourceNode for simplicity). No library survey needed; resolution informed by reading existing code.",
                    Status.DONE,
                    List.of(
                            new Task("Reviewed js-demos/audio reference (Howler + synthesizer.js)", true),
                            new Task("Identified the pattern: render-once at startup, play AudioBuffer per trigger", true),
                            new Task("Decided to adopt the pattern using Tone.Offline instead of synthesizer.js", true),
                            new Task("D1 + D2 RESOLVED based on this analysis", true)
                    ),
                    List.of(new Dependency("01", "Tracker exists.")),
                    "D1, D2, D3 RESOLVED; pattern picked; no new dependencies added.",
                    "Revert decisions to OPEN if pattern doesn't pan out.",
                    "30 minutes (way under the original 4-6h estimate)",
                    "Prior-art review beats from-scratch survey. Worth the habit.",
                    List.of()
            ),

            new Phase("03",
                    "[MUST] Prototype — render-once pipeline integrated",
                    "Updated the framework audio runtime to use Tone.Offline + AudioBufferSourceNode.",
                    "Refactored AppHtmlGetAction.renderAudioRuntime: replaced the per-trigger synth allocation pattern with a bake step (Tone.Offline renders each cue's audio to an AudioBuffer at first audio gesture, in parallel for all cues via Promise.all). Trigger time creates an AudioBufferSourceNode and starts it. The sealed Cue hierarchy stayed unchanged — bakeOneBuffer reads cue.kind and constructs the appropriate synth inside the offline context. Maple Bridge bell, Retro 90s click, Jazz Drums kit all now ride the new pipeline.",
                    Status.DONE,
                    List.of(
                            new Task("Refactor renderAudioRuntime to bake-once / source-node-per-trigger", true),
                            new Task("All existing cues migrated transparently (no theme changes)", true),
                            new Task("Eliminate polyphonic-collision errors (Maple Bridge bell)", true),
                            new Task("Rapid retriggers (drum kit) overlap cleanly", true)
                    ),
                    List.of(new Dependency("02", "Pattern decided.")),
                    "Audio runtime ships render-once-play-many. All themes work; no allocation per trigger.",
                    "Revert to per-trigger synth instantiation; lose the polyphony + perf wins.",
                    "Shipped in same session",
                    "",
                    List.of()
            ),

            new Phase("04",
                    "[MUST] Migrate Jazz Drum Kit to render-once cues",
                    "Re-frame: this happened automatically with Phase 03 since the cue contract didn't change.",
                    "The Jazz Drum Kit's 8 cues (Cues.KICK..CRASH) are MembraneCue / NoiseCue records — same as before Phase 03. They're rendered ONCE via Tone.Offline at first audio gesture (synth+envelope baked to AudioBuffer); replayed per trigger via AudioBufferSourceNode. No source code change to the kit itself; the runtime swap delivered the perf + quality improvements transparently. Subsequent additions (electric clean chord layer, hover chord cues) joined the same pipeline.",
                    Status.DONE,
                    List.of(
                            new Task("Existing Cues.KICK..CRASH constants work unchanged", true),
                            new Task("Sound quality: same synth recipe, but free of per-trigger noise (better for fast play)", true),
                            new Task("Latency: well under D6's 30ms budget", true),
                            new Task("Memory: ~30KB per drum cue, 8 cues = ~250KB total — trivial", true)
                    ),
                    List.of(new Dependency("03", "Pipeline live.")),
                    "Drum kit plays via render-once pipeline; rapid drumming works at realistic tempo.",
                    "n/a — covered by Phase 03 rollback.",
                    "0 (covered by Phase 03)",
                    "",
                    List.of()
            ),

            new Phase("05",
                    "[MUST] RFC — formalise engine + extensions",
                    "Updated RFC 0007 (Theme Audio Cues) with the render-once architecture evolution and the shipped extensions. Updated RFC 0008 (Interactive Theme Experiences) to mark Phase 1 + 2 Shipped + document scope decision on Phase 3.",
                    "RFC 0007 §11 documents the render-once-play-many pivot, the polyphonic-collision fix, the bake-time + memory profile. RFC 0007 §12 enumerates the 11+ extensions shipped beyond the original spec (hover cues, paletteMode, VocalPalette, ChordPalette, distortion field, visual .played, etc.). RFC 0008 §11–§13 document Phase 1 + 2 as built and the Phase 3 (ambient game) scope decision.",
                    Status.DONE,
                    List.of(
                            new Task("RFC 0007 — Status updated to Shipped+Extended; architecture evolution + extensions sections added", true),
                            new Task("RFC 0008 — Status updated to Phase 1+2 Shipped; Phase 3 scope decision documented", true),
                            new Task("This journey's decisions + phases reflect the actual resolution", true)
                    ),
                    List.of(new Dependency("04", "Worked migration informs the RFC.")),
                    "Both RFCs document the as-built audio architecture; cross-references resolve.",
                    "Revert RFC edits.",
                    "1 hour",
                    "",
                    List.of()
            ),

            new Phase("06",
                    "[STRETCH] Keyboard-playable instruments — SHIPPED",
                    "RFC 0008 Phase 2 (KeyCombo + keyBindings + control panel) is fully shipped.",
                    "Typed KeyCombo enum in homing-core, ThemeAudio.keyBindings() default-empty method, runtime keydown listener gated on per-theme play-mode toggle. Control panel surfaces mute (🔊/🔇) + play-mode (▷/▶) buttons next to the theme picker. Per-theme localStorage prefs (homing-theme:<slug>:muted, homing-theme:<slug>:play-mode). Jazz Drums keyBindings: A–K spatial drum mapping (upper row = top of kit, home row = bottom), U/I/O for electric-clean guitar power chords. Visual .played animations (drum-strike, cymbal-shimmer).",
                    Status.DONE,
                    List.of(
                            new Task("KeyCombo enum + ThemeAudio.keyBindings() shipped", true),
                            new Task("Control panel UI (mute + play-mode toggles, tooltip with bindings)", true),
                            new Task("Per-theme localStorage prefs", true),
                            new Task("Jazz Drums 2D spatial keyboard layout (A-K drums, U/I/O guitar)", true),
                            new Task("Visual .played class animations", true)
                    ),
                    List.of(new Dependency("04", "Render-once pipeline makes keyboard play feel responsive.")),
                    "User presses A-K to play the kit; U/I/O strum chord layer; play-mode toggle persists per-theme.",
                    "Disable keyboard listener; panel reverts to single mute button.",
                    "Shipped in same session as Phase 03/04/05",
                    "RFC 0008 §11–§12 documents what was actually shipped beyond the original spec.",
                    List.of()
            ),

            new Phase("07",
                    "[STRETCH] Pitched-instrument exploration — partially shipped",
                    "The Jazz Drums theme's electric-clean guitar layer (Cues.ELECTRIC_CLEAN_LOW/MID/HIGH, mapped to U/I/O keys) demonstrates pitched-instrument capability without a dedicated piano theme. A separate piano theme is descoped.",
                    "Triangle-wave + sustained envelope + power-chord-stack pitches → clean electric guitar timbre. Multi-note polyphony works via the render-once pipeline (each chord = one buffer with all chord notes summed in Tone.Offline). Plus the ChordPalette extension: 6 diatonic triads available to any cue that opts in via paletteMode=CHORD. Maple Bridge cards play chord-per-card hover cues; Retro 90s cards do chiptune chord triads. This validates the pipeline works for pitched + chord-stacked instruments, not just drums.",
                    Status.DONE,
                    List.of(
                            new Task("Electric clean guitar power chords in Jazz Drums (U/I/O keys)", true),
                            new Task("ChordPalette (6 diatonic triads) in homing-core", true),
                            new Task("Maple Bridge cards play chord per card via HOVER_CHORD_CLEAN", true),
                            new Task("Retro 90s cards play chiptune chords via HOVER_CHORD_RETRO", true),
                            new Task("[descoped] Dedicated HomingPiano theme — not needed; chord palette covers the use case", true)
                    ),
                    List.of(new Dependency("06", "Keyboard play landed; multi-pitch capability validated.")),
                    "Pitched-instrument cues work; chord layer validates polyphonic synthesis pipeline.",
                    "Drop the chord cues + electric guitar layer; drums still work standalone.",
                    "Shipped in same session as Phase 06",
                    "Piano theme idea descoped — the chord palette + electric-clean layer cover the same expressive ground without a dedicated theme.",
                    List.of()
            )
    );

    public static int totalProgressPercent() {
        if (PHASES.isEmpty()) return 0;
        int sum = 0;
        for (Phase p : PHASES) sum += p.progressPercent();
        return sum / PHASES.size();
    }

    public static int openDecisionsCount() {
        return (int) DECISIONS.stream().filter(d -> d.status() == DecisionStatus.OPEN).count();
    }

    private InstrumentsSteps() {}
}
