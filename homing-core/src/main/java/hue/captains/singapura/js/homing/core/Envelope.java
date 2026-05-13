package hue.captains.singapura.js.homing.core;

/**
 * ADSR envelope. All values in seconds; {@code sustain} is the
 * level (0–1) the note holds at after decay. Standard Tone.js mapping.
 *
 * @param attack  fade-in time
 * @param decay   fall to sustain time
 * @param sustain held level after decay (0.0 = silent at hold, 1.0 = full)
 * @param release fade-out time after note release
 */
public record Envelope(double attack, double decay, double sustain, double release) {}
