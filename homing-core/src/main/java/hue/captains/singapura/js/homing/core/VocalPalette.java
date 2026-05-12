package hue.captains.singapura.js.homing.core;

import java.util.List;

/**
 * The framework's shared "vocal pitch palette" — 11 typed notes spanning
 * C3 to B5 with G4 at the perceptual centre. Cues that opt in via
 * {@link Cue#useVocalPalette()} are baked once per palette pitch; the
 * runtime selects per trigger (hash-stable for hover, random for click).
 *
 * <p>The selection rationale (RFC 0008 hover extension):</p>
 * <ul>
 *   <li><b>Vocal range</b> — C3 (low alto) to B5 (high soprano) covers
 *       the comfortable human singing range. Cards "speak" at pitches a
 *       vocalist could sustain — gives audio-bearing themes a quiet
 *       chorus-of-voices texture on cursor sweeps.</li>
 *   <li><b>G4 centred</b> — G4 sits at index 5 (median of 11). Pitches
 *       are distributed roughly symmetrically above and below.</li>
 *   <li><b>Consonant intervals</b> — every neighbouring pitch is at least
 *       a whole tone apart; close overlapping triggers (rare due to the
 *       400 ms hover throttle but possible across themes) ring as
 *       consonant intervals, never half-step dissonance.</li>
 * </ul>
 *
 * <p>Lives in {@code homing-core} (not {@code homing-studio-base}) so the
 * framework's audio runtime (in {@code homing-server}) can reference it
 * without inverting the module dependency direction.</p>
 */
public final class VocalPalette {

    /** 11 pitches from C3 to B5, with G4 at index 5 (median). */
    public static final List<Note> NOTES = List.of(
            Note.C3, Note.E3, Note.G3,            // low — alto register
            Note.C4, Note.E4, Note.G4, Note.B4,   // middle — G4 is index 5 (median of 11)
            Note.D5, Note.F5, Note.A5, Note.B5    // upper — soprano register
    );

    private VocalPalette() {}
}
