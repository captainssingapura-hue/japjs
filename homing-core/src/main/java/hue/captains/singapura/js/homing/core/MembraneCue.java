package hue.captains.singapura.js.homing.core;

import java.util.List;

/**
 * Drum-like cue — Tone.js {@code MembraneSynth}. The body imitates a
 * struck membrane (drum, kick, low percussion). {@code pitchDecay} and
 * {@code octaves} shape the characteristic descending pitch sweep that
 * makes a membrane sound like a hit rather than a pure tone.
 *
 * @param pitchDecay seconds for the pitch to drop one octave
 * @param octaves    how many octaves the pitch falls during the strike
 * @param env        ADSR envelope (usually fast attack, short decay)
 * @param volumeDb   output volume in dB
 * @param notes      sequence of note hits
 */
public record MembraneCue(
        double pitchDecay,
        int octaves,
        Envelope env,
        double volumeDb,
        PaletteMode paletteMode,
        List<NoteHit> notes
) implements Cue {
    /** Back-compat — existing 5-arg callers stay single-buffer / no palette. */
    public MembraneCue(double pitchDecay, int octaves, Envelope env, double volumeDb, List<NoteHit> notes) {
        this(pitchDecay, octaves, env, volumeDb, PaletteMode.NONE, notes);
    }
}
