package hue.captains.singapura.js.homing.core;

/**
 * A typed audio cue — synth-parameter description that the framework's
 * Tone.js-backed runtime turns into a one-shot sound when a bound
 * {@link ClickTarget} is clicked.
 *
 * <p>Sealed over three permitted shapes (the synth voices Tone.js ships
 * with). JS generation dispatches over the sealed permits via switch
 * expression — exhaustive, typed, zero string parsing on the runtime
 * path.</p>
 *
 * <p>Per RFC 0007, themes pick cues from the {@code Cues} stdlib or
 * construct fresh record literals. Either way the cue object is a
 * stateless value with typed parameters (enums for synth/note/duration,
 * primitives for envelope/volume).</p>
 */
public sealed interface Cue permits OscCue, MembraneCue, NoiseCue {

    /** Output volume in dB. Negative is quieter (Tone.js convention). */
    double volumeDb();

    /**
     * Pitch-palette mode for the bake step. See {@link PaletteMode} for
     * the tri-state semantics:
     * <ul>
     *   <li>{@link PaletteMode#NONE} — single buffer at the cue's declared
     *       pitches. Default for one-shot click cues.</li>
     *   <li>{@link PaletteMode#VOCAL} — one buffer per pitch in
     *       {@link VocalPalette}. 11 variants. Hover triggers select by
     *       hash on element identity; click triggers select random for
     *       humanization.</li>
     *   <li>{@link PaletteMode#CHORD} — one buffer per chord in
     *       {@link ChordPalette}. 6 variants. Same selection semantics as
     *       VOCAL but each variant plays a full chord, not a single note.</li>
     * </ul>
     *
     * <p>NoiseCue ignores this mode at bake time — pitch shift on
     * un-pitched noise is imperceptible. Always renders a single buffer.</p>
     */
    default PaletteMode paletteMode() { return PaletteMode.NONE; }

    /**
     * Distortion amount in [0.0, 1.0]. Default 0 (clean). When > 0, the
     * framework's bake step routes the synth through a
     * {@code Tone.Distortion(amount)} node before destination — gives
     * sustained waveforms (sawtooth, square) a gritty, electric-guitar feel.
     *
     * <p>Records that don't expose this field (MembraneCue, NoiseCue) use
     * the interface default 0. Records that opt in (OscCue with the field)
     * override the default with their own value.</p>
     */
    default double distortion() { return 0.0; }
}
