package hue.captains.singapura.js.homing.core;

/**
 * One note in a {@link Cue}'s sequence. {@code offsetMs} = 0 means
 * "play simultaneously with the cue's other zero-offset notes," which
 * is how harmonic stacks (e.g. bell partials) are built.
 *
 * @param note      typed pitch
 * @param duration  typed note length
 * @param offsetMs  milliseconds after cue trigger before this note fires
 */
public record NoteHit(Note note, NoteDuration duration, int offsetMs) {}
