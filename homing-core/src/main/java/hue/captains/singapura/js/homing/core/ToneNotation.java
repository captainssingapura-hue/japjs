package hue.captains.singapura.js.homing.core;

/**
 * Conversion between Homing's typed audio enums and Tone.js's native
 * string notation. Lives in core because the cue-module emitter
 * (homing-studio-base) uses it to serialise enum values into Tone.js-
 * compatible strings at JS-gen time.
 *
 * <p>This is the ONE place strings derived from enums appear — and they
 * appear at code-generation time, not on the runtime data path.
 * Theme authors never invoke these helpers.</p>
 */
public final class ToneNotation {

    private ToneNotation() {}

    /**
     * Tone.js note string for a typed {@link Note}. Maps
     * {@code Note.C4_SHARP} → {@code "C#4"}, {@code Note.A0} → {@code "A0"}.
     */
    public static String forNote(Note n) {
        String name = n.name();                    // e.g. "C4_SHARP"
        int underscoreIdx = name.indexOf('_');
        if (underscoreIdx < 0) {
            return name;                            // e.g. "A0" → "A0"
        }
        // "C4_SHARP" → letter='C', octave='4', "#" + octave → "C#4"
        char letter = name.charAt(0);
        char octave = name.charAt(1);
        return "" + letter + "#" + octave;
    }

    /**
     * Tone.js duration string for a typed {@link NoteDuration}. Maps
     * {@code NoteDuration.HALF} → {@code "2n"}.
     */
    public static String forDuration(NoteDuration d) {
        return switch (d) {
            case WHOLE         -> "1n";
            case HALF          -> "2n";
            case QUARTER       -> "4n";
            case EIGHTH        -> "8n";
            case SIXTEENTH     -> "16n";
            case THIRTY_SECOND -> "32n";
            case SIXTY_FOURTH  -> "64n";
        };
    }

    /**
     * Tone.js oscillator-type string for a typed {@link OscType}. Maps
     * {@code OscType.SAWTOOTH} → {@code "sawtooth"}.
     */
    public static String forOsc(OscType t) {
        return t.name().toLowerCase(java.util.Locale.ROOT);
    }
}
