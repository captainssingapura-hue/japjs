package hue.captains.singapura.js.homing.core;

import java.util.List;

/**
 * Oscillator-based cue — Tone.js {@code Synth} with a configurable
 * waveform and envelope. The bread-and-butter cue: bells, chimes,
 * clicks, ticks, simple tones are all oscillator-shaped.
 *
 * @param type     waveform (sine / triangle / square / sawtooth)
 * @param env      ADSR envelope
 * @param volumeDb output volume in dB (Tone.js convention; negative = quieter)
 * @param notes    sequence of note hits; zero-offset notes play simultaneously
 *                 (build harmonic stacks for bells, chords, etc.)
 */
public record OscCue(
        OscType type, Envelope env, double volumeDb,
        PaletteMode paletteMode,
        double distortion,
        List<NoteHit> notes
) implements Cue {
    /** Back-compat — original 4-arg callers stay single-buffer / no palette. */
    public OscCue(OscType type, Envelope env, double volumeDb, List<NoteHit> notes) {
        this(type, env, volumeDb, PaletteMode.NONE, 0.0, notes);
    }
    /** Back-compat — palette-only callers (no distortion). */
    public OscCue(OscType type, Envelope env, double volumeDb, PaletteMode paletteMode, List<NoteHit> notes) {
        this(type, env, volumeDb, paletteMode, 0.0, notes);
    }
}
