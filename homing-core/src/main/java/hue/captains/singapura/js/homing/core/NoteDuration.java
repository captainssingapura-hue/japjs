package hue.captains.singapura.js.homing.core;

/**
 * Musical note duration — typed replacement for Tone.js's magic-string
 * notation ("2n", "16n", etc.). The runtime maps these enum values to
 * Tone.js notation via a single lookup table at JS-gen time.
 *
 * <p>Named {@code NoteDuration} (not just {@code Duration}) to avoid
 * collision with {@code java.time.Duration}.</p>
 */
public enum NoteDuration {
    WHOLE,          // "1n"
    HALF,           // "2n"
    QUARTER,        // "4n"
    EIGHTH,         // "8n"
    SIXTEENTH,      // "16n"
    THIRTY_SECOND,  // "32n"
    SIXTY_FOURTH    // "64n"
}
