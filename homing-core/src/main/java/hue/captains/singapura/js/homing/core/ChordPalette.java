package hue.captains.singapura.js.homing.core;

import java.util.List;

/**
 * The framework's shared chord palette — 6 diatonic triads in C major.
 * Cues that opt in via {@link PaletteMode#CHORD} are baked once per chord;
 * each chord is rendered with all its notes playing simultaneously, so
 * hovering an element fires a full chord rather than a single note.
 *
 * <p>The 6 chords:</p>
 * <ol>
 *   <li><b>C major</b> (I) — C4 – E4 – G4</li>
 *   <li><b>D minor</b> (ii) — D4 – F4 – A4</li>
 *   <li><b>E minor</b> (iii) — E4 – G4 – B4</li>
 *   <li><b>F major</b> (IV) — F4 – A4 – C5</li>
 *   <li><b>G major</b> (V) — G4 – B4 – D5</li>
 *   <li><b>A minor</b> (vi) — A4 – C5 – E5</li>
 * </ol>
 *
 * <p>Two design choices worth flagging:</p>
 *
 * <ul>
 *   <li><b>Voice leading.</b> Every two adjacent chords share two notes
 *       (C major ↔ D minor share nothing, but most other pairs share one).
 *       Sweeping across cards plays as a connected progression, not random
 *       jumps.</li>
 *   <li><b>No diminished.</b> The vii° (B diminished) is omitted — harsh,
 *       rarely fits pop progressions. 6 chords is enough variety; the
 *       7th would mostly add unpleasant overlaps.</li>
 * </ul>
 *
 * <p>Lives in {@code homing-core} (not theme code) so the framework's
 * audio runtime can reference the chord set without inverting the module
 * dependency direction.</p>
 */
public final class ChordPalette {

    /** 6 diatonic chords in C major, each a list of typed Note pitches. */
    public static final List<List<Note>> CHORDS = List.of(
            List.of(Note.C4, Note.E4, Note.G4),   // I  — C major
            List.of(Note.D4, Note.F4, Note.A4),   // ii — D minor
            List.of(Note.E4, Note.G4, Note.B4),   // iii — E minor
            List.of(Note.F4, Note.A4, Note.C5),   // IV — F major
            List.of(Note.G4, Note.B4, Note.D5),   // V  — G major
            List.of(Note.A4, Note.C5, Note.E5)    // vi — A minor
    );

    private ChordPalette() {}
}
