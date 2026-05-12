package hue.captains.singapura.js.homing.core;

/**
 * Oscillator waveform for {@link OscCue}. Maps directly to Tone.js
 * {@code Synth({oscillator: {type: …}})} via lowercase enum name.
 */
public enum OscType { SINE, TRIANGLE, SQUARE, SAWTOOTH }
