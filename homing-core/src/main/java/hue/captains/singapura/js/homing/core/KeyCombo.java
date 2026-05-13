package hue.captains.singapura.js.homing.core;

/**
 * Typed keyboard key — the binding key for {@link ThemeAudio#keyBindings()}.
 * RFC 0008 Phase 2.
 *
 * <p>Maps to the browser's {@code KeyboardEvent.code} string at JS-gen time
 * via {@link #eventCode()}. The framework's runtime listens for keydown,
 * matches by event.code, dispatches the bound {@link ClickTarget}'s cue.</p>
 *
 * <p><b>Excluded by design:</b> Tab, Escape, arrow keys, function keys.
 * These are reserved for assistive technology and browser navigation —
 * themes may not intercept them, even in opt-in play mode. The enum's
 * bounded permits this restriction at the type level: there is no
 * {@code TAB} or {@code ESC} constant to map.</p>
 */
public enum KeyCombo {
    KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I, KEY_J, KEY_K, KEY_L, KEY_M,
    KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R, KEY_S, KEY_T, KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z,
    DIGIT_0, DIGIT_1, DIGIT_2, DIGIT_3, DIGIT_4, DIGIT_5, DIGIT_6, DIGIT_7, DIGIT_8, DIGIT_9,
    SPACE;

    /** Browser {@code KeyboardEvent.code} string. */
    public String eventCode() {
        String n = name();
        if (n.startsWith("KEY_"))   return "Key"   + n.substring(4);
        if (n.startsWith("DIGIT_")) return "Digit" + n.substring(6);
        return switch (this) {
            case SPACE -> "Space";
            default    -> n;
        };
    }

    /** Short visible label for the control panel ("A", "1", "␣"). */
    public String displayLabel() {
        String n = name();
        if (n.startsWith("KEY_"))   return n.substring(4);
        if (n.startsWith("DIGIT_")) return n.substring(6);
        return switch (this) {
            case SPACE -> "␣";
            default    -> n;
        };
    }
}
