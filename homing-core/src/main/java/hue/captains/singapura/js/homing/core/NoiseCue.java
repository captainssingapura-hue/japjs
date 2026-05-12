package hue.captains.singapura.js.homing.core;

import java.util.List;

/**
 * Noise-based cue — Tone.js {@code NoiseSynth}. Useful for un-pitched
 * sounds: wind, rustle, hiss, crackle. The "notes" list still drives
 * timing/offset, but the note pitch is largely ignored by the noise
 * synth — what matters is the envelope and the trigger timing.
 *
 * @param env      ADSR envelope shaping the noise burst
 * @param volumeDb output volume in dB
 * @param notes    triggering offsets (pitch is decorative)
 */
public record NoiseCue(
        Envelope env,
        double volumeDb,
        PaletteMode paletteMode,
        List<NoteHit> notes
) implements Cue {
    /** Back-compat — existing 3-arg callers stay single-buffer.
     *  NoiseCue ignores paletteMode at bake time (pitch shift on
     *  un-pitched noise is imperceptible) but the field is present for
     *  shape consistency across the sealed Cue hierarchy. */
    public NoiseCue(Envelope env, double volumeDb, List<NoteHit> notes) {
        this(env, volumeDb, PaletteMode.NONE, notes);
    }
}
