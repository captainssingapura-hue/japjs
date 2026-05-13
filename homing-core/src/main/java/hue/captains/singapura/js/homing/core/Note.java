package hue.captains.singapura.js.homing.core;

/**
 * Musical note — typed replacement for Tone.js's magic-string notation
 * ("C4", "G5", etc.). Enum constants use {@code SHARP} suffix for the
 * 5 black keys per octave; the runtime maps each constant to its
 * scientific-pitch string at JS-gen time.
 *
 * <p>Covers C0 through B8 — 9 octaves, 108 notes, MIDI 12 through 119.
 * That's the full Tone.js operating range without exotic extremes.</p>
 */
public enum Note {
    C0, C0_SHARP, D0, D0_SHARP, E0, F0, F0_SHARP, G0, G0_SHARP, A0, A0_SHARP, B0,
    C1, C1_SHARP, D1, D1_SHARP, E1, F1, F1_SHARP, G1, G1_SHARP, A1, A1_SHARP, B1,
    C2, C2_SHARP, D2, D2_SHARP, E2, F2, F2_SHARP, G2, G2_SHARP, A2, A2_SHARP, B2,
    C3, C3_SHARP, D3, D3_SHARP, E3, F3, F3_SHARP, G3, G3_SHARP, A3, A3_SHARP, B3,
    C4, C4_SHARP, D4, D4_SHARP, E4, F4, F4_SHARP, G4, G4_SHARP, A4, A4_SHARP, B4,
    C5, C5_SHARP, D5, D5_SHARP, E5, F5, F5_SHARP, G5, G5_SHARP, A5, A5_SHARP, B5,
    C6, C6_SHARP, D6, D6_SHARP, E6, F6, F6_SHARP, G6, G6_SHARP, A6, A6_SHARP, B6,
    C7, C7_SHARP, D7, D7_SHARP, E7, F7, F7_SHARP, G7, G7_SHARP, A7, A7_SHARP, B7,
    C8, C8_SHARP, D8, D8_SHARP, E8, F8, F8_SHARP, G8, G8_SHARP, A8, A8_SHARP, B8
}
