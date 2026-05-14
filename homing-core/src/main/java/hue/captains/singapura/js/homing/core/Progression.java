package hue.captains.singapura.js.homing.core;

import java.util.Objects;

/**
 * A typed chord progression for the auto-play feature (RFC 0008 extension).
 * References indices into {@link ChordPalette#CHORDS}; each chord plays for
 * {@code secondsPerChord} before the runtime advances to the next.
 *
 * <p>Themes opt into auto-play by:</p>
 * <ol>
 *   <li>Returning a non-empty list from {@link ThemeAudio#progressions()}.</li>
 *   <li>Returning a non-null {@link Cue} from {@link ThemeAudio#progressionVoice()}
 *       — the voice used to render each chord. Should declare
 *       {@code paletteMode = CHORD} so the framework bakes one buffer per
 *       chord in the palette.</li>
 * </ol>
 *
 * <p>The runtime picks progressions at random each cycle, plays them on
 * loop, and supports a root-pitch offset (via {@code playbackRate}) so the
 * user can transpose without the framework re-baking.</p>
 *
 * @param name           human-readable label (shown in the control panel /
 *                       debug logs — not strictly required for playback)
 * @param chordIndices   sequence of indices into {@link ChordPalette#CHORDS};
 *                       each index in {@code [0, 6)} for the diatonic-C palette
 * @param secondsPerChord interval before the next chord; typical 1.0–2.5
 * @param moodColor      hex CSS color (e.g. {@code "#3a5f8f"}) — the
 *                       background mood overlay tints to this while the
 *                       progression plays. Optional; pass {@code null} for
 *                       "no mood tint."
 *
 * @since RFC 0008 (extension)
 */
public record Progression(
        String name,
        int[] chordIndices,
        double secondsPerChord,
        String moodColor) {

    public Progression {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(chordIndices, "chordIndices");
        if (chordIndices.length == 0) {
            throw new IllegalArgumentException("Progression must have at least one chord");
        }
        if (secondsPerChord <= 0) {
            throw new IllegalArgumentException("secondsPerChord must be positive");
        }
        // moodColor nullable
    }
}
