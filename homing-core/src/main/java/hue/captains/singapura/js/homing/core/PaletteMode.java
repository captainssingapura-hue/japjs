package hue.captains.singapura.js.homing.core;

/**
 * Pre-baked pitch palette mode for a {@link Cue}. Determines how the
 * runtime renders + selects buffer variants when triggered:
 *
 * <ul>
 *   <li>{@link #NONE} — single buffer rendered at the cue's declared pitches.
 *       The default for one-shot click cues (drums, system clicks, bells).</li>
 *   <li>{@link #VOCAL} — one buffer per pitch in {@link VocalPalette}. 11
 *       variants spanning C3–B5. Hovering different elements gets different
 *       pitches; same element gets a stable pitch within a session
 *       (RFC 0008 hover extension).</li>
 *   <li>{@link #CHORD} — one buffer per chord in {@link ChordPalette}. 6
 *       diatonic chords in C major (pop toolbox). Each card / list item
 *       plays a full chord instead of a single note — richer harmonic
 *       texture for the hover layer.</li>
 * </ul>
 *
 * <p>NoiseCue ignores this mode at bake time (pitch shift on un-pitched
 * noise is imperceptible) — always renders a single buffer.</p>
 */
public enum PaletteMode {
    NONE, VOCAL, CHORD
}
